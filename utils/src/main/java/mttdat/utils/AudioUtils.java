package mttdat.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import mttdat.services.PrepareAudioService;

public class AudioUtils {

    private static final long TICK_INTERVAL = 10;

    public static class AudioQueueObj {
        public String link;
        public CountDownTimer timer;
        public Listener listener;

        public AudioQueueObj(String link, Listener listener) {
            this.link = link;
            this.listener = listener;
        }
    }

    public static class MediaPlayerPlus extends MediaPlayer{

        private String dataSource;

        @Override
        public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException
        {
            super.setDataSource(path);
            dataSource = path;
        }

        public String getDataSource()
        {
            return dataSource;
        }
    }

    public abstract static class Listener{
        public void onPlayStart(){};
        public void onPlayEnd(){};  // Stop by completing the audio.
        public void onPlayStop(){}; // Stop by any reason.
        public void onPlayTick(int currentPosition){};
        public void onPlayInterrupt(){};
        public void onDataSourceFailed(){};
    }

    private static boolean isPlaying = false;
    private static MediaPlayerPlus mpSound;
    private static LinkedList<AudioQueueObj> links;

    public static void playSound(Context context, String packageName, int resRaw){
        playSound(context, packageName, resRaw, null);
    }

    public static void playSound(Context context, String packageName, int resRaw, Listener listener){
        if (resRaw == -1) {
            return;
        }

        if(isPlaying){
            try {
                if (mpSound != null) {
                    mpSound.stop();
                    mpSound.reset();
                }
            }catch (Exception e) {
                Log.e("AudioUtils", e.getMessage());
            }
        }

        isPlaying = true;

        /*
         *   * Using Resource Name
         *
         *  - Syntax : android.resource://[package]/[res type]/[res name]
         *  - Example : Uri.parse("android.resource://com.my.package/drawable/icon");
         *
         * * Using Resource Id
         *
         *  - Syntax : android.resource://[package]/[resource_id]
         *  - Example : Uri.parse("android.resource://com.my.package/" + R.drawable.icon);
         */

        mpSound = new MediaPlayerPlus();
        try {
            mpSound.setDataSource(context, Uri.parse("android.resource://" + packageName + "/" + resRaw));

            mpSound.prepare();

            mpSound.setOnCompletionListener(mediaPlayer -> {
                mediaPlayer.stop();
                mediaPlayer.reset();

                if(listener != null) {
                    listener.onPlayEnd();
                    listener.onPlayStop();
                }

                isPlaying = false;
            });

            mpSound.start();
        } catch (IOException e) {
            Log.e("AudioUtils", e.getMessage());
        }
    }

    public static void playSound(String link){
        playSound(null, link, null, false);
    }

    public static void playSound(String link, Listener listener){
        playSound(null, link, listener, false);
    }

    public static void playSound(String link, Listener listener, boolean shouldStopThePrevious){
        playSound(null, link, listener, shouldStopThePrevious);
    }

    // Pass param Context to save sound at local to play next time.

    public static void playSound(Context context, String link){
        playSound(context, link, null, false);
    }

    public static void playSound(Context context, String link, Listener listener){
        playSound(context, link, listener, false);
    }

    /** The main play sound method.
     *
     * @param shouldStopThePrevious : if there was an audio which is playing current, just stop when this flag = TRUE.
     * @param context : if context = NULL, just play the sound link directly from network; not need to save cache at local.
     * */
    public static void playSound(Context context, String link, Listener listener, boolean shouldStopThePrevious){

        if (link == null) {
            return;
        }

        // Get the local audio if there is.
        String source;
        if(context == null){
            source = link;
        }else {
            if (PrepareAudioService.containLocalUrlAudio(link)) {

                // Will play sound at local.
                source = PrepareAudioService.getLocalUrlAudio(link);
            } else {

                // Will play sound from the internet.
                source = link;

                // Call service to download audio file.
                Intent intent = new Intent(context, PrepareAudioService.class);
                intent.setAction(PrepareAudioService.ACTION_START_PREPARE_ONE);
                intent.putExtra(PrepareAudioService.ARG_AUDIO_URL, link);
                context.startService(intent);
            }
        }

        if(isPlaying){

            if(links.getFirst().listener != null){
                links.getFirst().listener.onPlayInterrupt();
                links.getFirst().listener.onPlayStop();
            }

            stop();

            if(shouldStopThePrevious) {
                return;
            }
        }

        isPlaying = true;
        if(mpSound == null){
            mpSound = new MediaPlayerPlus();
        }
        try {
            links = new LinkedList<>();

            links.add(new AudioQueueObj(source, listener));

            mpSound.setDataSource(source);
            mpSound.prepareAsync();
            mpSound.setOnPreparedListener(mp -> {
                mpSound.start();

                if(links.getFirst().listener != null) {
                    links.getFirst().listener.onPlayStart();
                }

                links.getFirst().timer = new CountDownTimer(mpSound.getDuration(), TICK_INTERVAL) {

                    @Override
                    public void onTick(long l) {
                        if(links.getFirst().listener != null){
                            links.getFirst().listener.onPlayTick((int) (mpSound.getDuration() - l));
                        }
                    }

                    @Override
                    public void onFinish() {
//                        if(links.getFirst().listener != null){
//                            links.getFirst().listener.onPlayTick(mpSound.getCurrentPosition());
//                        }
                    }
                };

                links.getFirst().timer.start();
            });
            mpSound.setOnCompletionListener(mediaPlayer -> {
                if(links.getFirst().listener != null) {
                    links.getFirst().listener.onPlayEnd();
                    links.getFirst().listener.onPlayStop();
                }

                stop();
            });
            mpSound.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    PrepareAudioService.removeLocalUrlAudio(link);
                    isPlaying  = false;

                    if(links.getFirst().listener != null) {
                        links.getFirst().listener.onDataSourceFailed();
                    }

                    return false;
                }
            });
        } catch (IOException e) {
            PrepareAudioService.removeLocalUrlAudio(link);
            isPlaying  = false;
        }
    }

    public static void playSoundInQueue(Context context, String link){

        playSoundInQueue(context, link, null);

//        if (link == null) {
//            return;
//        }
//
//        // Get the local audio if there is.
//        String source;
//        if(PrepareAudioService.containLocalUrlAudio(link)){
//
//            // Will play sound at local.
//            source = PrepareAudioService.getLocalUrlAudio(link);
//        }else {
//
//            // Will play sound from the internet.
//            source = link;
//
//            // Call service to download audio file.
//            Intent intent = new Intent(context, PrepareAudioService.class);
//            intent.setAction(PrepareAudioService.ACTION_START_PREPARE_ONE);
//            intent.putExtra(PrepareAudioService.ARG_AUDIO_URL, link);
//            context.startService(intent);
//        }
//
//        if(isPlaying){
//            links.add(new AudioObject(source));
//
//            Logger.t("AudioUtils").d("Audio is playing, add %s to queue.", source);
//        }else {
//
//            links = new LinkedList<>();
//
//            links.add(new AudioObject(source));
//
//            Logger.t("AudioUtils").d("First start playing sound: ", source);
//
//            isPlaying = true;
//
//            Logger.t("AudioUtils").d("Set isPlaying = true");
//
//            if(mpSound == null){
//                mpSound = new MediaPlayerPlus();
//            }
//
//            try {
//                mpSound.setDataSource(source);
//                mpSound.prepareAsync();
//                mpSound.setOnPreparedListener(mp -> {
//
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
//                        mpSound.setPlaybackParams(mpSound.getPlaybackParams().setSpeed(1.5f));
//                    }
//
//                    mpSound.start();
//
//                    Logger.t("AudioUtils").d("Start the first sound: %s", source);
//
//                    links.getFirst().timer = new CountDownTimer(mpSound.getDuration(), TICK_INTERVAL) {
//                        @Override
//                        public void onTick(long l) {
//
//                        }
//
//                        @Override
//                        public void onFinish() {
//
//                        }
//                    };
//                });
//                mpSound.setOnCompletionListener(mediaPlayer -> {
//
//                    /* The last sound in queue, just stop! */
//                    if(links.size() == 0) {
//
//                        if(mpSound != null) {
//                            mpSound.stop();
//                        }
//
//                        release();
//
//                        isPlaying = false;
//
//                        Logger.t("AudioUtils").d("The final sound in queue --> stop");
//
//                        return;
//                    }
//
//                    Logger.t("AudioUtils").d("Finish the previous sound: %s", links.peekFirst());
//
//                    // Reset to get ready for next sound.
//                    mpSound.reset();
//
//                    AudioObject audioObject = links.pollFirst();
//
//                    if(audioObject == null){
//                        throw new RuntimeException("The current sound is null");
//                    }
//
//                    String nextSound = audioObject.link;
//
//                    Logger.t("AudioUtils").d("Get ready to play next sound: %s", nextSound);
//
//                    Logger.t("AudioUtils").d("Num sounds still in queue after polling the previous sound: %d", links.size());
//
//                    try {
//
//                        if(!TextUtils.isEmpty(nextSound)) {
//                            mpSound.setDataSource(nextSound);
//
//                            mpSound.prepareAsync();
//                            mpSound.setOnPreparedListener(mp -> {
//
//                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
//                                    mpSound.setPlaybackParams(mpSound.getPlaybackParams().setSpeed(1.5f));
//                                }
//
//                                mpSound.start();
//
//                                Logger.t("AudioUtils").d("Start next sound: %s", nextSound);
//                            });
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                        /* Error occur, just stop! */
//
//                        if(mpSound != null) {
//                            mpSound.stop();
//                        }
//
//                        release();
//
//                        isPlaying = false;
//
//                        links = new LinkedList<>();
//
//                        Logger.t("AudioUtils").d("Error occur while playing next sound %s", nextSound);
//                    }
//                });
//                mpSound.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                    @Override
//                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                        PrepareAudioService.removeLocalUrlAudio(link);
//                        isPlaying  = false;
//                        return false;
//                    }
//                });
//            } catch (IOException e) {
//
//                /* Error occur, just stop! */
//
//                if(mpSound != null) {
//                    mpSound.stop();
//                }
//
//                release();
//
//                isPlaying = false;
//
//                links = new LinkedList<>();
//
//                Logger.t("AudioUtils").d("Error occur while playing the first sound %s", source);
//            }
//        }
    }

    public static void playSoundInQueue(Context context, String link, Listener listener){

        if (link == null) {
            return;
        }

        // Get the local audio if there is.
        String source;
        if(PrepareAudioService.containLocalUrlAudio(link)){

            // Will play sound at local.
            source = PrepareAudioService.getLocalUrlAudio(link);
        }else {

            // Will play sound from the internet.
            source = link;

            // Call service to download audio file.
            Intent intent = new Intent(context, PrepareAudioService.class);
            intent.setAction(PrepareAudioService.ACTION_START_PREPARE_ONE);
            intent.putExtra(PrepareAudioService.ARG_AUDIO_URL, link);
            context.startService(intent);
        }

        if(isPlaying){
            links.add(new AudioQueueObj(source, listener));

            Log.d("AudioUtils", "Audio is playing, add " + source + " to queue.");
        }else {

            links = new LinkedList<>();

            links.add(new AudioQueueObj(source, listener));

            Log.d("AudioUtils", "First start playing sound: " + source);

            isPlaying = true;

            Log.d("AudioUtils", "Set isPlaying = true");

            if(mpSound == null){
                mpSound = new MediaPlayerPlus();
            }

            try {
                mpSound.setDataSource(source);
                mpSound.prepareAsync();
                mpSound.setOnPreparedListener(mp -> {

                    mpSound.start();

                    if(links.getFirst().listener != null) {
                        links.getFirst().listener.onPlayStart();
                    }

                    Log.d("AudioUtils", "Start the sound: " + source);

                    links.getFirst().timer = new CountDownTimer(mpSound.getDuration(), TICK_INTERVAL) {
                        @Override
                        public void onTick(long l) {
                            if(links.getFirst().listener != null){
                                links.getFirst().listener.onPlayTick((int) (mpSound.getDuration() - l));
                            }
                        }

                        @Override
                        public void onFinish() {

                        }
                    };

                    links.getFirst().timer.start();
                });
                mpSound.setOnCompletionListener(mediaPlayer -> {

                    Log.d("AudioUtils", "Finish the previous sound: " + links.peekFirst());

                    // Reset to get ready for next sound.
                    mpSound.reset();

                    if(links.getFirst().listener != null) {
                        links.getFirst().listener.onPlayEnd();
                        links.getFirst().listener.onPlayStop();
                    }

                    // Remove this sound from the stack.
                    links.pollFirst();

                    // The last sound in queue, just stop!
                    if(links.isEmpty()){
                        if(mpSound != null) {
                            mpSound.stop();
                        }

                        release();

                        isPlaying = false;

                        Log.d("AudioUtils", "The final sound in queue --> stop");

                        return;
                    }

                    // Get the next sound.
                    String nextSound = links.getFirst().link;

                    Log.d("AudioUtils", "Get ready to play next sound: " + nextSound);

                    Log.d("AudioUtils", "Num sounds still in queue after polling the previous sound: " + links.size());

                    try {

                        if(!TextUtils.isEmpty(nextSound)) {
                            mpSound.setDataSource(nextSound);
                            mpSound.prepareAsync();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                        /* Error occur, just stop! */

                        if(mpSound != null) {
                            mpSound.stop();
                        }

                        release();

                        isPlaying = false;

                        links.clear();

                        Log.d("AudioUtils", "Error occur while playing next sound: " + nextSound);
                    }
                });
                mpSound.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                        PrepareAudioService.removeLocalUrlAudio(link);
                        isPlaying  = false;

                        if(links.getFirst().listener != null) {
                            links.getFirst().listener.onDataSourceFailed();
                        }

                        return false;
                    }
                });
            } catch (IOException e) {

                /* Error occur, just stop! */

                if(mpSound != null) {
                    mpSound.stop();
                }

                release();

                isPlaying = false;

                links.clear();

                Log.d("AudioUtils", "Error occur while playing the first sound: " + source);
            }
        }
    }

    // Save sound at local to play next time.
    public static synchronized void saveSound(Context context, String link){

        if (link == null) {
            return;
        }

        // Get the local audio if there is.
        if(!PrepareAudioService.containLocalUrlAudio(link)){

            // Call service to download audio file.
            Intent intent = new Intent(context, PrepareAudioService.class);
            intent.setAction(PrepareAudioService.ACTION_START_PREPARE_ONE);
            intent.putExtra(PrepareAudioService.ARG_AUDIO_URL, link);
            context.startService(intent);
        }
    }

    public static void stop(){

        if(!isPlaying){

            // Media Player is idle, not need to stop anything.
            return;
        }

        try {
            if (mpSound != null) {
                mpSound.stop();
                mpSound.reset();
            }

            release();

            isPlaying = false;

            // Cancel timers if there are.
            for(AudioQueueObj audioQueueObj : links){
                if(audioQueueObj != null && audioQueueObj.timer != null){
                    audioQueueObj.timer.cancel();
                }
            }

            links.clear();

        }catch (Exception e) {
            Log.e("AudioUtils", e.getMessage());
        }
    }

    public static void release(){
        if(mpSound != null) {
            mpSound.release();
            mpSound = null;
        }
    }

    public static boolean isPlaying() {
        return isPlaying;
    }

    public static long getDuration(String audioUrl){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        retriever.setDataSource(audioUrl, new HashMap<String, String>());
        // The old/ legacy method. Build.VERSION.SDK_INT < 14
        // retriever.setDataSource(soundLink);

        return Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
    }
}
