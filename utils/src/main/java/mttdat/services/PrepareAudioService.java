package mttdat.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.HashMap;

import mttdat.utils.DownloadFileFromURL;
import mttdat.utils.FileUtils;

public class PrepareAudioService extends IntentService {

    public static final String ACTION_START_PREPARE_ONE = "prepare_one";
    public static final String ACTION_START_PREPARE_LIST = "prepare_list";
    public static final String ACTION_RECEIVER_FINISH = "downloaded";
    public static final String ACTION_RECEIVER_ERROR = "error";
    public static final String ARG_DATA = "data";
    public static final String ARG_AUDIO_URL = "audio_url";

    public static HashMap<String, String> audioManager;
    private boolean saveInCache = true;    // TODO: saveInCache should be true

    public PrepareAudioService(String name) {
        super(name);
    }

    public PrepareAudioService() {
        super("PrepareAudioService");
    }

    @Override
    protected synchronized void onHandleIntent(@Nullable Intent intent) {

        switch (intent.getAction()){
            case ACTION_START_PREPARE_ONE:

                // If don't have url to download.
                if(!intent.hasExtra(ARG_AUDIO_URL)){
                    sendInfoBack(ACTION_RECEIVER_ERROR);
                    return;
                }

                String url = intent.getStringExtra(ARG_AUDIO_URL);

                if(audioManager == null){
                    audioManager = new HashMap<>();
                }

                // If download this url already.
                if(audioManager.containsKey(url)){
                    sendInfoBack(ACTION_RECEIVER_FINISH);
                    return;
                }

                String fileName = "audio_" + System.nanoTime() + ".mp3";

                DownloadAudioAsyncTask atDownAudio =
                        new DownloadAudioAsyncTask(new DownloadFileFromURL.Listener() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onUpdateProgress(float progress) {
                            }

                            @Override
                            public void onFinish(String directorySavedFile) {

                                if(directorySavedFile != null) {
                                    audioManager.put(url, directorySavedFile);
                                }

                                sendInfoBack(ACTION_RECEIVER_FINISH);
                            }

                            @Override
                            public void onError(String message) {
                                sendInfoBack(ACTION_RECEIVER_ERROR);
                            }
                        });

                if(!saveInCache) {

                    // Get the directory for the user's public mainDir directory.
                    String directory = FileUtils.getExternalPublicDirectory(Environment.DIRECTORY_MUSIC, "Wohui-prepare-audio");
                    File folder = new File(directory);

                    // If directory doesn't exist, create it.
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }

                    File file = new File(folder, fileName);

                    atDownAudio.execute(
                            url,                                                      // Url to download file.
                            file.getAbsolutePath()                                    // Directory to save file.
                    );
                }else {
                    atDownAudio.execute(
                            url,                                                      // Url to download file.
                            FileUtils.getCacheDirectory(this, fileName)       // Directory to save file.
                    );
                }

                break;

            case ACTION_START_PREPARE_LIST:
                // Not done yet.
                break;
        }
    }

    private void sendInfoBack(String actionReceiver, String... data){
        Intent intent = new Intent();
        intent.setAction(actionReceiver);
        intent.putExtra(ARG_DATA, data);

        sendBroadcast(intent);
    }

    public static boolean containLocalUrlAudio(String url){
        return audioManager != null && audioManager.containsKey(url) && (new File(audioManager.get(url))).exists();
    }

    public static String getLocalUrlAudio(String url){
        return audioManager == null ? null : audioManager.get(url);
    }

    public static void removeLocalUrlAudio(String url){

        if(audioManager == null){
            return;
        }

        audioManager.remove(url);
    }

    static class DownloadAudioAsyncTask extends DownloadFileFromURL {

        DownloadAudioAsyncTask(Listener listener) {
            super();

            this.setListener(listener);
        }
    }
}
