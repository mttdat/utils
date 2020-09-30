package mttdat.utils;

import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayDeque;

import ss.wohui.views.ScrollableLinearLayoutManager;

public class LoadMoreOnScrollListener extends RecyclerView.OnScrollListener {

    private final String TAG = "LoadMoreSListen";
    
    private ScrollableLinearLayoutManager layoutManager;
    private boolean isLoading;      // Is loading new items, this flag prevents loading continuous when reaching bottom (threshold).
    private boolean isShowNoMore;   // Is show no more, not turn off yet.

    private int lastScrollState = -1;
    private boolean atBottom;
    private int totalItemCount;

    private boolean isOverScrollAtBottom;

    public interface OnDataListener{
        void onReachThreshold();
        void onJustReachBottomOfList();
        void onTryLoadMoreFromBottomOfList();
    }

    private OnDataListener onDataListener;
    private int bottomThreshold = 2;    // How many items below.
    private ArrayDeque<Integer> pageTracker = new ArrayDeque<>();

    public LoadMoreOnScrollListener(ScrollableLinearLayoutManager layoutManager){
        this.layoutManager = layoutManager;

        this.layoutManager.setOnOverScrollListener(new ScrollableLinearLayoutManager.OnOverScrollListener() {
            @Override
            public void onOverScrollTop(int overScrollRange) {
                Log.d(TAG, "onOverScrollTop");

                isOverScrollAtBottom = false;
            }

            @Override
            public void onOverScrollBottom(int overScrollRange) {
                Log.d(TAG, "onOverScrollBottom");

                isOverScrollAtBottom = true;
            }
        });
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        atBottom = false;
        totalItemCount = layoutManager.getItemCount();
        int lastVisibleItemPosition = 0, lastVisibleCompletelyItemPosition = 0;

        if(layoutManager instanceof LinearLayoutManager){
            lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();
            lastVisibleCompletelyItemPosition = ((LinearLayoutManager)layoutManager).findLastCompletelyVisibleItemPosition();
        }

        // Notice: dy > 0 --> make sure the list scroll in the right direction. (Down)
        // if dy = 0, means it scrolled automatically no by finger interaction.

        if(((lastVisibleItemPosition + bottomThreshold) == (totalItemCount - 1)) && dy > 0){
            onDataListener.onReachThreshold();

            Log.d(TAG, "OnReachThreshold");
        }

        Log.d(TAG, "dy: " + dy);

        if((lastVisibleCompletelyItemPosition == (totalItemCount - 1)) && dy > 0){
            atBottom = true;
//            onDataListener.onTryLoadMoreFromBottomOfList();

            Log.d(TAG, "Reach bottom");
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        Log.d(TAG, "scroll state: " + newState + ", at bot: " + atBottom);

        if( newState == RecyclerView.SCROLL_STATE_IDLE &&
            (lastScrollState == RecyclerView.SCROLL_STATE_DRAGGING || lastScrollState == RecyclerView.SCROLL_STATE_SETTLING) &&
            atBottom) {

            Log.d(TAG, "Just reach bottom");

            onDataListener.onJustReachBottomOfList();
        }

        Log.d(TAG, "last item: " + ((LinearLayoutManager)layoutManager).findLastCompletelyVisibleItemPosition());
        Log.d(TAG, "total item: " + totalItemCount);
        Log.d(TAG, "isOverScrollAtBottom: " + isOverScrollAtBottom);

        if( newState == RecyclerView.SCROLL_STATE_IDLE &&
                (lastScrollState == RecyclerView.SCROLL_STATE_DRAGGING || lastScrollState == RecyclerView.SCROLL_STATE_SETTLING) &&
                ((totalItemCount - 1) == ((LinearLayoutManager)layoutManager).findLastCompletelyVisibleItemPosition())){

            if(isOverScrollAtBottom) {
                Log.d(TAG, "Try load more from bottom");

                onDataListener.onTryLoadMoreFromBottomOfList();
            }
        }

        if(newState == RecyclerView.SCROLL_STATE_IDLE){
            isOverScrollAtBottom = false;
            Log.d(TAG, "isOverScrollAtBottom = false, recyclerview idle");
        }

        lastScrollState = newState;
    }

    // Only load more if list is idle and not load this (page + 1) yet.
    // Return -1 --> should not; otherwise, return a page should be loaded.
    public int shouldLoadNextPage(){

        return (!isLoading && !isLoadedPage(getLatestLoaded() + 1)) ? (getLatestLoaded() + 1) : -1;
    }

    // If not load any times, return -1.
    public int getLatestLoaded(){
        return pageTracker.isEmpty() ? -1 : pageTracker.getLast();
    }

    public void addLoadedPage(int page){
        pageTracker.add(page);
    }

    public void clearPageTracker(){
        pageTracker.clear();
    }

    public boolean isLoadedPage(int page){
        return pageTracker.contains(page);
    }

    public void setOnDataListener(OnDataListener onDataListener) {
        this.onDataListener = onDataListener;
    }

    public boolean shouldShowNoMore(){
        return !isShowNoMore;
    }

    public int getBottomThreshold() {
        return bottomThreshold;
    }

    public void setBottomThreshold(int bottomThreshold) {
        this.bottomThreshold = bottomThreshold;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isShowNoMore() {
        return isShowNoMore;
    }

    public void setShowNoMore(boolean showNoMore) {
        isShowNoMore = showNoMore;
    }
}
