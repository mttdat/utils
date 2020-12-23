package mttdat.utilapplication.demo.textwrapwidth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import mttdat.utilapplication.R;
import mttdat.utils.ViewUtils;
import mttdat.viewplus.TextViewWrapWidth;

public class TextWrapWidthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_wrap_width);

        TextViewWrapWidth tv = findViewById(R.id.tv);
        tv.setMaxWidth((int) (ViewUtils.getScreenWidth(this) * 0.8f));
    }
}