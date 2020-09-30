package com.flowlist;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class FlowList extends ScrollView {

    public interface OnDragDropListener{
        void onDragStart();
        void onDragEnter(View view, int row, int left, int right);
        void onDragExit(View view, int row, int left, int right);
        void onSwap();
        void onDragEnd(View view, int row, int left, int right);
    }

    interface OnAttachedAdapterListener {
        void onAttached(FlowList layout);
    }

    /* Listener */

    OnDragDropListener mOnDragDropListener;

    ConstraintLayout clContainer;

    private FlowListAdapter mAdapter;
    FlowListLayoutManager mLayoutManager;

    // Attributes.
    boolean isItemWidthSame;        // If the item_flow_list width will the same.

    boolean isDebug;

    public FlowList(Context context) {
        super(context);
    }

    public FlowList(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public FlowList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttributes(context, attrs);
    }

    public FlowList(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowList);
        isItemWidthSame = a.getBoolean(R.styleable.FlowList_isItemWidthSame, false);
        a.recycle();
    }

    private void initView(){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflate layout for main layout (this).
        if (inflater != null) {
            inflater.inflate(R.layout.view_flow_list, this);
        }else {
            throw new RuntimeException("Can not get inflater");
        }

        clContainer = findViewById(R.id.container);

        mAdapter.notifyDataSetHasChange();
    }

    public void setAdapter(FlowListAdapter adapter){
        if(mLayoutManager != null){
            mAdapter = adapter;

            // Notify that layout was attached to this adapter.
            adapter.mOnAttachedAdapterListener.onAttached(this);

            initView();
        }
    }

    public void setLayoutManager(FlowListLayoutManager layoutManager) {
        mLayoutManager = layoutManager;
    }

    public void setItemWidthSame(boolean itemWidthSame) {
        isItemWidthSame = itemWidthSame;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    public void setOnDragDropListener(OnDragDropListener onDragDropListener) {
        mOnDragDropListener = onDragDropListener;
    }
}
