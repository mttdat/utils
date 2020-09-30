package mttdat.utilapplication.demo.flowlist;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.flowlist.FlowListViewHolder;

import mttdat.utils.ViewUtils;

public class TestFlowListVHolder extends FlowListViewHolder<String> {

    TextView mTextView;

    private Context mContext;

    public TestFlowListVHolder(View view, String item, int pos) {
        super(view, item, pos);

        mContext = view.getContext();

        mTextView = (TextView) view;// view.findViewById(R.id.tv);
    }

    @Override
    protected void onBind(String string) {
//        mTextView.setText(String.valueOf(getAdapterPosition()));
        mTextView.setText(string);
    }

    @Override
    public int getExpectedWidth() {
        return ViewUtils.getWidthFromText(mContext, data, 70, null) + ViewUtils.dpToPx(7) * 2 + 10;
    }
}
