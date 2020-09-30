package mttdat.utilapplication.demo.centergrid;

import android.view.View;
import android.widget.TextView;

import mttdat.centergridlayout.CenterGridViewHolder;
import mttdat.utilapplication.R;

public class TestCenterGridHolder extends CenterGridViewHolder<String> {

    private TextView tv;

    public TestCenterGridHolder(View v, int i) {
        super(v, i);

        tv = (TextView) v.findViewById(R.id.tv);
    }

    @Override
    protected void onBind(String item) {
            tv.setText(item);
    }
}
