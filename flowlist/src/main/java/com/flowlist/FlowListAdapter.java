package com.flowlist;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.os.Build;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.Barrier;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public abstract class FlowListAdapter<T>{

    private final String TAG = "FlowListAdapter";

    private FlowList mFlowList;

    protected ArrayList<T> mItems;

    // Save as 2 list for quick access.
    private ArrayList<FlowListViewHolder<T>> mViewHolders = new ArrayList<>();
    private ArrayList<ArrayList<FlowListViewHolder<T>>> mViewHoldersByRows = new ArrayList<>();

    private ArrayList<SpaceViewHolder> mSpaceViewHolders = new ArrayList<>();

    private ArrayList<Integer> mBarrierIds = new ArrayList<>();
    private ArrayList<LinearLayout> rows = new ArrayList<>();

    protected HashMap<Integer, Boolean> listChosen;
    private int mResourceItem;

    protected LayoutInflater inflater;

    FlowList.OnAttachedAdapterListener mOnAttachedAdapterListener = new FlowList.OnAttachedAdapterListener() {
        @Override
        public void onAttached(FlowList layout) {
            mFlowList = layout;

            inflater = LayoutInflater.from(mFlowList.getContext());
        }
    };

    public FlowListAdapter() {
        this(-1, null);
    }

    public FlowListAdapter(int resourceItem) {
        this(resourceItem, null);
    }

    public FlowListAdapter(ArrayList<T> items) {
        this(-1, items);
    }

    public FlowListAdapter(int resourceItem, ArrayList<T> items) {
        mResourceItem = resourceItem;
        mItems = items;
    }

    private void createFlowList(){

        if(!mFlowList.mLayoutManager.isLayoutManagerValid()) {
            Log.e(TAG, "Layout manager of the Flow list is not valid.");
            return;
        }

        int width = 0;  // Current width at current row.
        int row = 0;    // How many rows right now.
        int col = 0;    // How many cols right now.

        generateItemsInFlowList(0, -1, width, row, col);
    }

    private void createFlowList(int fromPos, int moveItemPos){

        if(!mFlowList.mLayoutManager.isLayoutManagerValid()) {
            Log.e(TAG, "Layout manager of the Flow list is not valid.");
            return;
        }

        // Find where the previous @fromPos is.
        int row = fromPos == 0 ? 0 : (mViewHolders.get(fromPos - 1).row);    // How many rows right now.
        int col = fromPos == 0 ? 0 : (mViewHolders.get(fromPos - 1).col + 1);    // How many cols right now.
        int width = 0;                              // Current width at current row.

        for(int i = 0; i < col; i++){
            width += mFlowList.mLayoutManager.space + mViewHoldersByRows.get(row).get(i).getExpectedWidth();
        }

        generateItemsInFlowList(fromPos, moveItemPos, width, row, col);
    }

    private void generateItemsInFlowList(int fromPos, int moveItemPos, int width, int row, int col){
        for (int i = fromPos; i < mItems.size(); i++) {

            T item = mItems.get(i);

            int takenWidth;
            do{
                takenWidth = addItem(width, item, i, row, col,
                        i < mViewHolders.size() && mViewHolders.get(i).getAdapterPosition() == moveItemPos);

                // Add item_flow_list into a current row successfully.
                if(takenWidth != -1){

                    // Increase span in a current row.
                    width += takenWidth;

                    col ++;
                }else {

                    // Reset span to create a new row.
                    width = 0;
                    col = 0;

                    // New row.
                    row ++;
                }
            }while (takenWidth == -1);   // If add item_flow_list failed, re-add it.
        }

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mFlowList.clContainer);

        // Constrain base lines.
        for(int i = 0; i < mViewHoldersByRows.size(); i++) {

            int[] refIds = new int[mViewHoldersByRows.get(i).size()];
            for(int j = 0; j < mViewHoldersByRows.get(i).size(); j++){
                refIds[j] = mViewHoldersByRows.get(i).get(j).view.getId();
            }

            constraintSet.createBarrier(mBarrierIds.get(i), Barrier.BOTTOM, refIds);
        }

        constraintSet.applyTo(mFlowList.clContainer);
    }

    /** After add an item_flow_list.
     *  @return + -1 means there is not enough space in a current row for this item_flow_list; or
     *          + a needed width for item_flow_list included a space before it.
     */
    private int addItem(int width, T item, int pos, int row, int col){
        return addItem(width, item, pos, row, col, false);
    }

    private int addItem(int width, T item, int pos, int row, int col, boolean forceRecreateItem){

        /* Every item_flow_list will have a space before it.
         * But, the last item_flow_list in row would have one more space after it.
         * */

        // At first in a row.
        if(col == 0){

            // Create a base line for a row.
            createBaseLine();

            // Instantiate a list to store all items in a row.
            mViewHoldersByRows.add(new ArrayList<>());
        }

        // Create a space before.
        SpaceViewHolder spaceViewHolder = createSpace(row, col, pos);

        // Create an item_flow_list or get the one exists.
        FlowListViewHolder<T> itemViewHolder = createItem(item, row, col, pos, width, spaceViewHolder.view, forceRecreateItem);

        // Not enough space for this item_flow_list in the current row.
        if(itemViewHolder == null){

            // Add the space to fill up the rest of the row before starting a new row.
            addSpaceAfter(row, col, pos, false);

            return -1;
        }

        // Add drag drop listener.
        addOnDragListener(spaceViewHolder.view);
        addTouchToDragListener(itemViewHolder.view);

        // Add view into container.
        mFlowList.clContainer.addView(spaceViewHolder.view);
        if(mFlowList.clContainer.findViewById(itemViewHolder.view.getId()) == null) {
            mFlowList.clContainer.addView(itemViewHolder.view);
        }

        // Save view holder.
        mSpaceViewHolders.add(spaceViewHolder);
        if(forceRecreateItem){
            mViewHolders.set(pos, itemViewHolder);
        }else {
            if(!mViewHolders.contains(itemViewHolder)) {
                mViewHolders.add(itemViewHolder);
            }
        }

        mViewHoldersByRows.get(row).add(itemViewHolder);

        // The last item_flow_list is also the last item_flow_list in the current row.
        if(pos == mItems.size() - 1){

            // Add the space to fill up the rest of the row before finishing the list.
            // But this will be added right after adding the last item_flow_list.
            addSpaceAfter(row, col, pos, true);
        }

        return mFlowList.mLayoutManager.space + itemViewHolder.getExpectedWidth();
    }

    private void addSpaceAfter(int row, int col, int pos, boolean isLastSpaceInList){

        // Create a space after.
        SpaceViewHolder spaceViewHolderAfter = createSpace(row, col, pos, true, isLastSpaceInList);

        // Add drag drop listener.
        addOnDragListener(spaceViewHolderAfter.view);

        // Add space into container.
        mFlowList.clContainer.addView(spaceViewHolderAfter.view);

        // Save space holder.
        mSpaceViewHolders.add(spaceViewHolderAfter);
    }

    /** Create a barrier for each row since not all items in the row have the same height.
     * @Note: this only create a barrier without no constraint yet. We have to constraint item_flow_list to
     * this barrier later on.
     * */
    private void createBaseLine(){
        Barrier barrier = new Barrier(mFlowList.getContext());
        barrier.setId(View.generateViewId());
        barrier.setType(Barrier.BOTTOM);

        if(mBarrierIds == null){
            mBarrierIds = new ArrayList<>();
        }

        mBarrierIds.add(barrier.getId());
    }

    private ConstraintLayout.LayoutParams getSpaceParam(int row, int col, int pos, boolean isLastSpaceInRow, boolean isLastSpaceInList){

        ConstraintLayout.LayoutParams spaceParams = new ConstraintLayout.LayoutParams(
                isLastSpaceInRow ?
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT :
                        (col == 0 ? mFlowList.mLayoutManager.spacePaddingHorizontal : mFlowList.mLayoutManager.space),
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
        );

        // Other rows.
        if(row != 0){
            spaceParams.topToTop = mBarrierIds.get(row - 1);
        }else { // First row.
            spaceParams.topToTop = mFlowList.clContainer.getId();
        }

        // The last space in list which is unique, so check it first.
        if(isLastSpaceInList){
            spaceParams.startToEnd = mViewHolders.get(pos).view.getId();
        }else {

            // Other items in row.
            if(col != 0) {
                spaceParams.startToEnd = mViewHolders.get(pos - 1).view.getId();
            }else { // First item_flow_list in row.
                spaceParams.startToStart = mFlowList.clContainer.getId();
            }
        }

        if(isLastSpaceInRow){
            spaceParams.endToEnd = mFlowList.clContainer.getId();
        }

        spaceParams.bottomToBottom = mBarrierIds.get(row);

        return spaceParams;
    }

    /** Create a space before the item_flow_list.
     * To create a space after the item_flow_list use the method {@link com.flowlist.FlowListAdapter#createSpace(int, int, int, boolean, boolean)}
     * */
    private SpaceViewHolder createSpace(int row, int col, int pos){
        return createSpace(row, col, pos, false, false);
    }

    /** Create space after the item_flow_list has 2 cases:
     *  1) The space after the item_flow_list but not the final space in the list.
     *  2) The space after the item_flow_list and also the final space in the list.
     * */
    private SpaceViewHolder createSpace(int row, int col, int pos, boolean isLastSpaceInRow, boolean isLastSpaceInList){

        // If a space is at the "beginning" / "end" of the list, no need a "left" / "right" link.
        int left, right;
        if(pos == 0){
            left = -1;
            right = pos;
        }else if(isLastSpaceInList){
            left = pos;
            right = -1;
        }else {
            left = pos - 1;
            right = pos;
        }

        // Create a space view.
//        View spaceView = new View(mFlowList.getContext());
        TextView spaceView = new TextView(mFlowList.getContext());
        spaceView.setId(View.generateViewId());
        spaceView.setLayoutParams(getSpaceParam(row, col, pos, isLastSpaceInRow, isLastSpaceInList));

        if(mFlowList.isDebug) {
            spaceView.setBackgroundResource(R.color.blue);
            spaceView.setText(left + "," + right);
        }

        return new SpaceViewHolder(spaceView, left, right)
                .setRow(row);
    }

    private ConstraintLayout.LayoutParams getItemParam(int width, int row, View spaceView){

        ConstraintLayout.LayoutParams itemParams = new ConstraintLayout.LayoutParams(
                width,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        itemParams.startToEnd = spaceView.getId();

        // Other rows.
        if(row != 0){
            itemParams.topMargin = mFlowList.mLayoutManager.spacePaddingVertical;
            itemParams.topToTop = mBarrierIds.get(row - 1);
        }else { // First row.
            itemParams.topToTop = mFlowList.clContainer.getId();
        }

        return itemParams;
    }

    /** Create Item in a flow list. This method just instantiate a view, but not add in a hierarchy yet.
     * @param curWidth the current sum-up with so far in a row.
     * @param viewSpace the space before this item_flow_list.
     * @param forceRecreateItem */
    private FlowListViewHolder<T> createItem(T item, int row, int col, int pos, int curWidth, View viewSpace, boolean forceRecreateItem){

        FlowListViewHolder<T> flowListViewHolder;

        if(forceRecreateItem || pos >= mViewHolders.size()){

            int type = getItemViewType(pos);
            View view = getItemView(mFlowList, type);
            view.setId(View.generateViewId());

            flowListViewHolder = createItemFlowListViewHolder(view, item, pos);
            flowListViewHolder.setType(type);
        }else {
            flowListViewHolder = mViewHolders.get(pos);
        }

        // Check if there is enough space for this item_flow_list in a current row.
        // Note: - curWidth is the current occupied width in a row
        //       - We need at least a space before and after a view: space end and space normal have different size.
        int neededWidthForItem = flowListViewHolder.getExpectedWidth();
        if(curWidth + neededWidthForItem + mFlowList.mLayoutManager.space +
                mFlowList.mLayoutManager.spacePaddingHorizontal > mFlowList.mLayoutManager.width){
            return null;  // Not enough, return null to notify that we need a new row.
        }

        flowListViewHolder.setRowCol(row, col);
        flowListViewHolder.setPos(pos);

        flowListViewHolder.onBind(item);

        flowListViewHolder.view.setLayoutParams(getItemParam(neededWidthForItem, row, viewSpace));

        return flowListViewHolder;
    }

    protected int getItemViewType(int pos){
        return -1;
    }

    protected View getItemView(ViewGroup viewGroup, int viewType){
        return inflater.inflate(mResourceItem, viewGroup, false);
    }

    /** A sub method to create and inflate an item_flow_list view from provided layout.
     * We cannot create this view on our own in this abstract adapter.
     * */
    protected abstract FlowListViewHolder<T> createItemFlowListViewHolder(View view, T item, int pos);

    private void addTouchToDragListener(View view){
        view.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:

                        // Data this view is sending.
                        ClipData data = ClipData.newPlainText("", "");

                        // A shadow which is a conveyor of the data above.
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                        // Start dragging.
                        // Note: the flag here is 0 means we don't care about complicated situations.
                        boolean rs;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            rs = view.startDragAndDrop(data, shadowBuilder, view, 0);
                        } else {
                            rs = view.startDrag(data, shadowBuilder, view, 0);
                        }

                        // If dragging was successful.
                        if(rs) {
                            view.setVisibility(View.INVISIBLE);

                            if(mFlowList.mOnDragDropListener != null){
                                mFlowList.mOnDragDropListener.onDragStart();
                            }

                            return true;
                        }

                        return false;
                }

                return false;
            }
        });
    }

    private void addOnDragListener(View view){
        view.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent event) {

                View draggedView;
                FlowListViewHolder draggedHolder;
                SpaceViewHolder space;

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:

                        space = findSpaceViewHolderByView(view);

                        if(space == null){
                            return false;
                        }

                        if(mFlowList.isDebug) {
                            Log.d("zzz", "ACTION_DRAG_STARTED, space " + space.left + " , " + space.right);
                        }

                        draggedView = (View) event.getLocalState();
                        draggedHolder = findViewHolderByView(draggedView);

                        if(!space.canMoveItemHere(draggedHolder.getAdapterPosition())){

                            // Check if can move item_flow_list into this space.
                            return false;
                        }

                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:

                        space = findSpaceViewHolderByView(view);

                        if(space == null){
                            return false;
                        }

                        if(mFlowList.isDebug) {
                            Log.d("zzz", "ACTION_DRAG_ENTERED, space " + space.left + " , " + space.right);
                        }

                        if(mFlowList.mOnDragDropListener != null){
                            mFlowList.mOnDragDropListener.onDragEnter(view, space.row, space.left, space.right);
                        }

                        break;

                    case DragEvent.ACTION_DRAG_EXITED:

                        space = findSpaceViewHolderByView(view);

                        if(space == null){
                            return false;
                        }

                        if(mFlowList.isDebug) {
                            Log.d("zzz", "ACTION_DRAG_EXITED, space " + space.left + " , " + space.right);
                        }

                        if(mFlowList.mOnDragDropListener != null){
                            mFlowList.mOnDragDropListener.onDragExit(view, space.row, space.left, space.right);
                        }

                        break;

                    case DragEvent.ACTION_DROP:

                        space = findSpaceViewHolderByView(view);

                        if(space == null){
                            return false;
                        }

                        if(mFlowList.isDebug) {
                            Log.d("zzz", "ACTION_DROP, space " + space.left + " , " + space.right);
                        }

                        draggedView = (View) event.getLocalState();

                        moveItemToSpace(findViewHolderByView(draggedView), space);

                        if(mFlowList.mOnDragDropListener != null){
                            mFlowList.mOnDragDropListener.onSwap();
                        }

                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:

                        space = findSpaceViewHolderByView(view);

                        if(space == null){
                            return false;
                        }

                        if(mFlowList.isDebug) {
                            Log.d("zzz", "ACTION_DRAG_ENDED, space " + space.left + " , " + space.right);
                        }

                        if(!event.getResult()) {
                            draggedView = (View) event.getLocalState();
                            draggedView.setVisibility(View.VISIBLE);

//                            animateDragToStart(draggedView, event.getX(), event.getY());
                        }

                        if(mFlowList.mOnDragDropListener != null){
                            mFlowList.mOnDragDropListener.onDragEnd(view, space.row, space.left, space.right);
                        }

                        break;

                    default:
                        break;
                }

                return true;
            }

            private void animateDragToStart(View initialView, float fromX, float fromY) {
                float topMargin = fromY - initialView.getTop();
                float leftMargin = fromX - initialView.getLeft();

                Animation translateAnimation = new TranslateAnimation(
                        leftMargin - (initialView.getWidth() / 2.0f), 0,
                        topMargin - (initialView.getHeight() / 2.0f), 0);
                translateAnimation.setDuration(500);
                translateAnimation.setInterpolator(new AccelerateInterpolator());
                initialView.startAnimation(translateAnimation);
                initialView.setVisibility(View.VISIBLE);
            }
        });
    }

    protected void notifyItemChange(int pos){
        mViewHolders.get(pos).onBind(mItems.get(pos));
    }

    public void notifyDataSetHasChange(){

        // Clear everything if yes.
        mViewHolders.clear();
        rows.clear();

        if(listChosen != null) {
            listChosen.clear();
        }

        mFlowList.clContainer.removeAllViews();

        // Create list.
        createFlowList();
//        if(mFlowList.isItemWidthSame) {
//            createListTags();
//        }else {
//            createListTagsNoSpan();
//        }
    }

    public FlowListViewHolder findViewHolderByView(View v){
        for(FlowListViewHolder holder : mViewHolders){
            if(holder.view == v){
                return holder;
            }
        }

        return null;
    }

    public SpaceViewHolder findSpaceViewHolderByView(View v){
        for(SpaceViewHolder holder : mSpaceViewHolders){
            if(holder.view == v){
                return holder;
            }
        }

        return null;
    }

    private void moveItemToSpace(FlowListViewHolder item, SpaceViewHolder space){

        if(!space.canMoveItemHere(item.getAdapterPosition())){

            // Check if can move item_flow_list into this space.
            return;
        }

        boolean isMoveBack = space.isMoveItemBack(item.getAdapterPosition());

        int moveItemPos = item.getAdapterPosition();
        int targetItemPos = isMoveBack ? space.right : space.left;

        Log.d("zzz", "move item_flow_list " + moveItemPos + " to space " + targetItemPos);

        int startChangePos = Math.min(moveItemPos, targetItemPos);

        /* Update list data. */

        // Move the dragged item_flow_list.
        moveItemFromTo(moveItemPos, targetItemPos);

        /* Update UI. */

        //TODO: có thể check coi swap cùng hàng ko sẽ đỡ update UI hơn.

        removeSpaceFrom(startChangePos);
        removeViewInRowListFrom(startChangePos, moveItemPos);
        createFlowList(startChangePos, moveItemPos);
    }

    /**
     * This method will only move data. The position, row, or column should be updated
     * manually.
     * */
    private void moveItemFromTo(int posFrom, int posTo){
        T temp = mItems.get(posFrom);
        mItems.remove(posFrom);
        mItems.add(posTo, temp);

        FlowListViewHolder<T> tempVHolder = mViewHolders.get(posFrom);
        mViewHolders.remove(posFrom);
        mViewHolders.add(posTo, tempVHolder);
    }

    private void removeSpaceAt(int pos){

        Iterator<SpaceViewHolder> iterator = mSpaceViewHolders.iterator();

        while (iterator.hasNext()){
            SpaceViewHolder spaceViewHolder = iterator.next();

            if(spaceViewHolder.right == pos || spaceViewHolder.left == pos){
                mFlowList.clContainer.removeView(spaceViewHolder.view);
                iterator.remove();
            }
        }
    }

    private void removeSpaceFrom(int pos){
        for(int i = pos; i < mItems.size(); i++){
            removeSpaceAt(i);
        }
    }

    /** This will remove items in the mViewHoldersByRows.
     * Note: browsing from the end of the list until we meet the desired position.
     *
     * @param pos is the position we want to remove forward to the end.
     * @param moveItemPos the item_flow_list moved should be removed from container. The position is still
     *                    the old position because {@link com.flowlist.FlowListAdapter#moveItemFromTo(int, int)}
     *                    actually only moves the data and physical order (order of the list data).
     */
    private void removeViewInRowListFrom(int pos, int moveItemPos){

        outerLoop:
        for(int i = mViewHoldersByRows.size() - 1; i >= 0; i --){
            for (int j = mViewHoldersByRows.get(i).size() - 1; j >= 0; j --){

                // If this current position.
                FlowListViewHolder<T> flowListViewHolder = mViewHoldersByRows.get(i).get(j);;
                int curPos = flowListViewHolder.getAdapterPosition();

                mViewHoldersByRows.get(i).remove(j);

                if(j == 0){
                    mViewHoldersByRows.remove(i);
                    mBarrierIds.remove(i);
                }

                if(curPos == moveItemPos){
                    mFlowList.clContainer.removeView(flowListViewHolder.view);
                }

                if(curPos == pos){
                    break outerLoop;
                }
            }
        }
    }

    /* ******************** Sub-class ******************** */

    private static class SpaceViewHolder{
        public int left = -1;
        public int right = -1;
        public int row;
        public View view;

        public SpaceViewHolder(View view, int left, int right) {
            this.left = left;
            this.right = right;
            this.view = view;
        }

        public boolean canMoveItemHere(int posDraggedView){

            // Left and right must be defined at least one of both.
            if(left == -1 && right == -1){
                return false;
            }

            //  The targets can't be next to dragged view.
            return posDraggedView != left && posDraggedView != right;
        }

        public boolean isMoveItemBack(int posDraggedView){
            return posDraggedView > left && posDraggedView > right;
        }

        public SpaceViewHolder setRow(int row) {
            this.row = row;
            return this;
        }
    }

    /* *************************************************** */

    public HashMap<Integer, Boolean> getListChosen() {
        return listChosen;
    }

    public void initListChosen(int firstChosenPos) {

        // Do nothing if there is no item_flow_list in list.
        if (mItems == null) {
            return;
        }

        this.listChosen = new HashMap<>();

        for (int i = 0; i < mItems.size(); i++) {

            if (i == firstChosenPos) {
                listChosen.put(i, true);
            } else {
                listChosen.put(i, false);
            }
        }
    }

    public ArrayList<T> getItems() {
        return mItems;
    }

    public void setItems(ArrayList<T> items) {
        this.mItems = items;
    }

    public ArrayList<LinearLayout> getRows() {
        return rows;
    }
}
