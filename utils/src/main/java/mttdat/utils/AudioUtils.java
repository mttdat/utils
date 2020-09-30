package mttdat.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.LinkedList;

import ss.wohui.Wohui;
import ss.wohui.services.audio.PrepareAudioService;

public class AudioUtils {

    private static final long TICK_INTERVAL = 100;

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
        public void onDataSourceFailed(){};
        public void onPlayEnd(){};
        public void onPlayStart(){};
        public void onPlayTick(int currentPosition){};
    }

    private static boolean isPlaying = false;
    private static MediaPlayerPlus mpSound;
    private static LinkedList<AudioQueueObj> links;

    public static void playSound(Context context, int resRaw){
        playSound(context, resRaw, null);
    }

    public static void playSound(Context context, int resRaw, Listener listener){
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
            mpSound.setDataSource(context, Uri.parse("android.resource://" + Wohui.PACKAGE_NAME + "/" + resRaw));

            mpSound.prepare();

            mpSound.setOnCompletionListener(mediaPlayer -> {
                mediaPlayer.stop();
                mediaPlayer.reset();

                if(listener != null) {
                    listener.onPlayEnd();
                }

                isPlaying = false;
            });

            mpSound.start();
        } catch (IOException e) {
            Log.e("AudioUtils", e.getMessage());
        }
    }

    public static void playSound(String link){
        playSound(null, link, null);
    }

    public static void playSound(String link, Listener listener){
        playSound(null, link, listener);
    }

    // Pass param Context to save sound at local to play next time.

    public static void playSound(Context context, String link){
        playSound(context, link, null);
    }

    public static void playSound(Context context, String link, Listener listener){

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
        if(mpSound == null){
            mpSound = new MediaPlayerPlus();
        }
        try {
            mpSound.setDataSource(source);
            mpSound.prepareAsync();
            mpSound.setOnPreparedListener(mp -> {
                mpSound.start();

                if(listener != null) {
                    listener.onPlayStart();
                }
            });
            mpSound.setOnCompletionListener(mediaPlayer -> {
                mediaPlayer.stop();
                mediaPlayer.reset();
                isPlaying = false;

                if(listener != null) {
                    listener.onPlayEnd();
                }
            });
            mpSound.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    PrepareAudioService.removeLocalUrlAudio(link);
                    isPlaying  = false;

                    if(listener != null) {
                        listener.onDataSourceFailed();
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

            Logger.t("AudioUtils").d("Audio is playing, add %s to queue.", source);
        }else {

            links = new LinkedList<>();

            links.add(new AudioQueueObj(source, listener));

            Logger.t("AudioUtils").d("First start playing sound: ", source);

            isPlaying = true;

            Logger.t("AudioUtils").d("Set isPlaying = true");

            if(mpSound == null){
                mpSound = new MediaPlayerPlus();
            }

            try {
                mpSound.setDataSource(source);
                mpSound.prepareAsync();
                mpSound.setOnPreparedListener(mp -> {

//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
//                        mpSound.setPlaybackParams(mpSound.getPlaybackParams().setSpeed(1.5f));
//                    }

                    mpSound.start();

                    if(links.getFirst().listener != null) {
                        links.getFirst().listener.onPlayStart();
                    }

                    Logger.t("AudioUtils").d("Start the sound: %s", source);

                    links.getFirst().timer = new CountDownTimer(mpSound.getDuration(), TICK_INTERVAL) {
                        @Override
                        public void onTick(long l) {
                            if(links.getFirst().listener != null){
                                links.getFirst().listener.onPlayTick(mpSound.getCurrentPosition());
                            }
                        }

                        @Override
                        public void onFinish() {

                        }
                    };

                    links.getFirst().timer.start();
                });
                mpSound.setOnCompletionListener(mediaPlayer -> {

                    Logger.t("AudioUtils").d("Finish the previous sound: %s", links.peekFirst());

                    // Reset to get ready for next sound.
                    mpSound.reset();

                    if(links.getFirst().listener != null) {
                        links.getFirst().listener.onPlayEnd();
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

                        Logger.t("AudioUtils").d("The final sound in queue --> stop");

                        return;
                    }

                    // Get the next sound.
                    String nextSound = links.getFirst().link;

                    Logger.t("AudioUtils").d("Get ready to play next sound: %s", nextSound);

                    Logger.t("AudioUtils").d("Num sounds still in queue after polling the previous sound: %d", links.size());

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

                        Logger.t("AudioUtils").d("Error occur while playing next sound %s", nextSound);
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

                Logger.t("AudioUtils").d("Error occur while playing the first sound %s", source);
            }
        }
    }

    public static void stopQueue(){

        if(!isPlaying){

            // Media Player is idle, not need to stop anything.
            return;
        }

        if(mpSound != null) {
            mpSound.stop();
        }

        release();

        isPlaying = false;

        // Cancel timers.
        for(AudioQueueObj audioQueueObj : links){
            if(audioQueueObj != null && audioQueueObj.timer != null){
                audioQueueObj.timer.cancel();
            }
        }

        links.clear();
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

    public static void stopAndRelease(){
        try {
            if (mpSound != null) {
                mpSound.stop();
                mpSound.reset();
                mpSound.release();

                mpSound = null;

                isPlaying = false;
            }
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

    public static boolean isIsPlaying() {
        return isPlaying;
    }
}
