package mttdat.centergridlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

public class CenterGridLayout extends ConstraintLayout {

    private CenterGridLayoutManager centerGridLayoutManager;
    private CenterGridAdapter centerGridAdapter;
    private boolean isInit; // Does center grid initialize its layout with attached adapter?

    interface OnAttachedAdapterListener {
        void onAttached(CenterGridLayout layout);
    }

    public CenterGridLayout(Context context) {
        super(context);
    }

    public CenterGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public CenterGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CenterGridLayout);

        centerGridLayoutManager = new CenterGridLayoutManager(
                a.getInt(R.styleable.CenterGridLayout_orientation, CenterGridLayoutManager.HORIZONTAL)
        );

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(isInEditMode()){

            switch (centerGridLayoutManager.getOrientation()){
                case CenterGridLayoutManager.HORIZONTAL:

                    setMeasuredDimension(
                            200, 400
                    );

                    break;

                case CenterGridLayoutManager.VERTICAL:

                    setMeasuredDimension(
                            400, 200
                    );

                    break;
            }

            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setAdapter(CenterGridAdapter centerGridAdapter) {
        this.centerGridAdapter = centerGridAdapter;
        centerGridAdapter.getOnAttachedAdapterListener().onAttached(this);

        if(centerGridLayoutManager != null) {

            if(centerGridLayoutManager.getTotalItemsByGaps() != centerGridAdapter.getCount()){
                throw new RuntimeException("Total item_flow_list in CenterGridAdapter and CenterGridLayoutManager must be matched.");
            }

            initLayout();
            centerGridAdapter.notifyDataSetChanged();
        }
    }

    private void initLayout() {

        /* Init views. */
        LayoutInflater inflater = LayoutInflater.from(getContext());

        // Browsing rows.
        for(int i = 0; i < centerGridLayoutManager.getTotalItemsByGaps(); i ++){
            centerGridAdapter.inflateViewItem(inflater, i);
        }

        /* Layout views. */

        setId(View.generateViewId());

        switch (centerGridLayoutManager.getOrientation()){
            case CenterGridLayoutManager.HORIZONTAL:
                // Chưa làm.
                break;

            case CenterGridLayoutManager.VERTICAL:

                int pos = 0;
                for(int r = 0; r < centerGridLayoutManager.getGaps().length; r ++){

                    // First row.
                    if(r == 0){

                        // Browsing column.
                        for(int j = 0; j < centerGridLayoutManager.getGaps()[r]; j ++){

                            // Size for item_flow_list.
                            LayoutParams layoutParams = new LayoutParams(
                                    centerGridLayoutManager.getItemWidth(), centerGridLayoutManager.getItemHeight());

                            layoutParams.startToStart = getId();

                            // First column.
                            if(j == 0){
                                layoutParams.topToTop = getId();
                                layoutParams.bottomToTop = centerGridAdapter.getItemView(pos + 1).getId();
                                layoutParams.verticalChainStyle = LayoutParams.CHAIN_PACKED;
                            } else if(j == centerGridLayoutManager.getGaps()[r] - 1){   // Last column.
                                layoutParams.topToBottom = centerGridAdapter.getItemView(pos - 1).getId();
                                layoutParams.bottomToBottom = getId();
                            } else {
                                layoutParams.topToBottom = centerGridAdapter.getItemView(pos - 1).getId();
                                layoutParams.bottomToTop = centerGridAdapter.getItemView(pos + 1).getId();
                            }

                            this.addView(centerGridAdapter.getItemView(pos), layoutParams);

                            // Go to next pos.
                            pos ++;
                        }
                    } else {    // The other rows.

                        if(r == centerGridLayoutManager.getGaps().length - 1){
                            Log.d("","");
                        }

                        // Browsing column.
                        for(int c = 0; c < centerGridLayoutManager.getGaps()[r]; c ++){

                            // Size for item_flow_list.
                            LayoutParams layoutParams = new LayoutParams(
                                    centerGridLayoutManager.getItemWidth(), centerGridLayoutManager.getItemHeight());

                            layoutParams.startToEnd = centerGridAdapter.getItemView(centerGridLayoutManager.convert2Pos(r - 1, 0/*c*/)).getId();

                            // First column.
                            if(c == 0){
                                layoutParams.topToTop = getId();
                                layoutParams.bottomToTop = centerGridAdapter.getItemView(pos + 1).getId();
                                layoutParams.verticalChainStyle = LayoutParams.CHAIN_PACKED;
                            } else if(c == centerGridLayoutManager.getGaps()[r] - 1){   // Last column.
                                layoutParams.topToBottom = centerGridAdapter.getItemView(pos - 1).getId();
                                layoutParams.bottomToBottom = getId();
                            } else {
                                layoutParams.topToBottom = centerGridAdapter.getItemView(pos - 1).getId();
                                layoutParams.bottomToTop = centerGridAdapter.getItemView(pos + 1).getId();
                            }

                            this.addView(centerGridAdapter.getItemView(pos), layoutParams);

                            // Go to next pos.
                            pos ++;
                        }
                    }
                }

                break;
        }

        isInit = true;
    }

    public void setLayoutManager(CenterGridLayoutManager centerGridLayoutManager) {
        this.centerGridLayoutManager = centerGridLayoutManager;
    }
}
