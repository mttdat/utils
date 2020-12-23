package mttdat.utilapplication.demo.audioprogress;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import mttdat.utilapplication.R;
import mttdat.utilapplication.demo.basis.BasisDemoActivity;
import mttdat.utils.AudioUtils;
import mttdat.viewplus.ImageAutoScale;

public class AudioProgressActivity extends BasisDemoActivity {

    ImageAutoScale ivBtnPlay;
    SeekBar sbDuration;

    AsyncTaskGetDuration mAsyncTaskGetDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_progress);

        ivBtnPlay = findViewById(R.id.iv_btn_play);
        sbDuration = findViewById(R.id.sb_duration);

        // Todo: please note on this issue when connecting to source online.
        // https://stackoverflow.com/questions/45940861/android-8-cleartext-http-traffic-not-permitted
        String link = "http://www.schillmania.com/projects/soundmanager2/demo/_mp3/rain.mp3";

        ivBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Todo: this demo not save the current playing position, just reset everything.
                //  And also not check all situation, just show how to track the duration while playing audio.
                if(AudioUtils.isPlaying()){
                    ivBtnPlay.setImageResource(R.drawable.bkg_btn_play);
                    AudioUtils.stop();
                    mAsyncTaskGetDuration.cancel(true);
                }else {
                    mAsyncTaskGetDuration = new AsyncTaskGetDuration();
                    mAsyncTaskGetDuration.execute(link);
                }
            }
        });
    }

    private class AsyncTaskGetDuration extends AsyncTask<String, Void, Void>{

        private String link;
        private long duration;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Todo: show loading here.
        }

        @Override
        protected Void doInBackground(String... strings) {

            link = strings[0];

            duration = AudioUtils.getDuration(link);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Todo: stop loading here.

            AudioUtils.playSound(link, new AudioUtils.Listener(){
                @Override
                public void onPlayStart() {
                    super.onPlayStart();

                    ivBtnPlay.setImageResource(R.drawable.bkg_btn_play_pause);
                }

                @Override
                public void onPlayTick(int currentPosition) {
                    super.onPlayTick(currentPosition);

                    // Notice: conver duration to float to make the division not be rounded.
                    sbDuration.setProgress((int) (currentPosition / (float) duration * 100));
                }

                @Override
                public void onPlayEnd() {
                    super.onPlayEnd();

                    ivBtnPlay.setImageResource(R.drawable.bkg_btn_play);
                }
            });
        }
    }
}