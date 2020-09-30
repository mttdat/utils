package com.flowlist;

import android.widget.LinearLayout;

public class FlowListLayoutManager {
    public static int ORIENTATION_VERTICAL = LinearLayout.VERTICAL;
    public static int ORIENTATION_HORIZONTAL = LinearLayout.HORIZONTAL;
    private static int DEFAULT_SPACE = 60;
    private static int DEFAULT_SPACE_HORIZONTAL = 80;
    private static int DEFAULT_SPACE_VERTICAL = 60;
    private static int DEFAULT_NUM_ITEM_IN_ROW = 1;

    int itemWidth = -1;             // Width of item_flow_list.
    int space;                      // Space between items.
    int spacePaddingHorizontal = DEFAULT_SPACE_HORIZONTAL;      // Space padding start and end.
    int spacePaddingVertical = DEFAULT_SPACE_VERTICAL;          // Space padding between rows.
    int numItemInRow;               // How many items in a row.
    int width;                      // Width of flow list.
    int orientation;

    public FlowListLayoutManager(int width) {
        this(DEFAULT_SPACE, width, ORIENTATION_VERTICAL, DEFAULT_NUM_ITEM_IN_ROW, false);
    }

    public FlowListLayoutManager(int space, int width) {
        this(space, width, ORIENTATION_VERTICAL, DEFAULT_NUM_ITEM_IN_ROW,false);
    }

    public FlowListLayoutManager(int space, int width, int orientation) {
        this(space, width, orientation, DEFAULT_NUM_ITEM_IN_ROW,false);
    }

    public FlowListLayoutManager(int space, int width, int orientation, int numItemInRow) {
        this(space, width, orientation, numItemInRow,true);
    }

    public FlowListLayoutManager(int space, int width, int orientation, int numItemInRow, boolean isItemWidthSame) {
        this.space = space;
        this.numItemInRow = numItemInRow;
        this.width = width;
        this.orientation = orientation;

        // Calculate expected width of item_flow_list in a row.
        if(isItemWidthSame) {
            itemWidth = (width - (space * (numItemInRow - 1))) / numItemInRow;
        }
    }

    public boolean isLayoutManagerValid(){
        return  width > 0 &&
                (orientation == ORIENTATION_VERTICAL || orientation == ORIENTATION_HORIZONTAL) &&
                space > 0 &&
                numItemInRow >= 1;
    }

    public void setNumItemInRow(int numItemInRow) {
        this.numItemInRow = numItemInRow;
    }

    public void setSpacePaddingHorizontal(int spacePaddingHorizontal) {
        this.spacePaddingHorizontal = spacePaddingHorizontal;
    }

    public void setSpacePaddingVertical(int spacePaddingVertical) {
        this.spacePaddingVertical = spacePaddingVertical;
    }
}
