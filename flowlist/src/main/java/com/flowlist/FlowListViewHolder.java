package com.flowlist;

import android.view.View;

public abstract class FlowListViewHolder<T> {

    protected View view;
    protected T data;
    private int pos;
    protected int row;
    protected int col;
    protected int type;

    public FlowListViewHolder(View view, T item, int pos) {
        this.view = view;
        data = item;
        this.pos = pos;
    }

    protected abstract void onBind(T item);

    public int getAdapterPosition() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public abstract int getExpectedWidth();

    public View getView() {
        return view;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setRowCol(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setType(int type) {
        this.type = type;
    }
}
