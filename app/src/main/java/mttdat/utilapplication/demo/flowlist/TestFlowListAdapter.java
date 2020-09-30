package mttdat.utilapplication.demo.flowlist;

import android.view.View;

import com.flowlist.FlowListAdapter;
import com.flowlist.FlowListViewHolder;

import java.util.ArrayList;

public class TestFlowListAdapter extends FlowListAdapter<String> {

    public TestFlowListAdapter(int resourceItem, ArrayList<String> strings) {
        super(resourceItem, strings);
    }

    @Override
    protected FlowListViewHolder<String> createItemFlowListViewHolder(View view, String item, int pos) {
        return new TestFlowListVHolder(view, item, pos);
    }
}
