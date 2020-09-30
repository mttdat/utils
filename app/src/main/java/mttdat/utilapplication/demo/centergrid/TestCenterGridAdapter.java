package mttdat.utilapplication.demo.centergrid;

import android.view.View;

import java.util.List;

import mttdat.centergridlayout.CenterGridAdapter;
import mttdat.centergridlayout.CenterGridViewHolder;

public class TestCenterGridAdapter extends CenterGridAdapter<String> {

    public TestCenterGridAdapter(int resource, List<String> listItems) {
        super(resource, listItems);
    }

    @Override
    public CenterGridViewHolder<String> onCreateViewHolder(View v, int posInAdapter) {
        return new TestCenterGridHolder(v, posInAdapter);
    }
}
