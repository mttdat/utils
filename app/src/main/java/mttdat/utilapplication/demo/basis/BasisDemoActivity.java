package mttdat.utilapplication.demo.basis;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import mttdat.utilapplication.R;

@SuppressLint("Registered")
public class BasisDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_right_in, R.anim.fade_right_out);
    }
}
