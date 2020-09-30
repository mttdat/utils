package mttdat.centergridlayout;

public class CenterGridLayoutManager {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int orientation;
    private int[] gaps; // Each item_flow_list of gaps is the number of item_flow_list in a row.
    private int itemWidth;
    private int itemHeight;

    public CenterGridLayoutManager(int orientation) {
        this.orientation = orientation;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public CenterGridLayoutManager(int[] gaps, int itemWidth) {
        this(0, gaps, itemWidth, itemWidth);
    }

    public CenterGridLayoutManager(int[] gaps, int itemWidth, int itemHeight) {
        this(0, gaps, itemWidth, itemHeight);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public CenterGridLayoutManager(int orientation, int[] gaps, int itemWidth) {
        this(orientation, gaps, itemWidth, itemWidth);
    }

    public CenterGridLayoutManager(int orientation, int[] gaps, int itemWidth, int itemHeight) {
        this.orientation = orientation;
        this.gaps = gaps;
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
    }

    public int getTotalItemsByGaps(){
        if(gaps == null){
            return 0;
        }

        int totalItem = 0;
        for(int num : gaps){
            totalItem += num;
        }

        return totalItem;
    }

    public int convert2Pos(int row, int column){
        int pos = 0;

        for(int r = 0; r <= row; r++){
            if(r == row){
                pos += column;
            }else {
                pos += gaps[row];
            }
        }

        return pos;
    }

    public int getOrientation() {
        return orientation;
    }

    public int[] getGaps() {
        return gaps;
    }

    public int getItemWidth() {
        return itemWidth;
    }

    public int getItemHeight() {
        return itemHeight;
    }
}
