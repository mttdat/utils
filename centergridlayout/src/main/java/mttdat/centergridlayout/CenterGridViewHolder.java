package mttdat.centergridlayout;

import android.view.View;

public abstract class CenterGridViewHolder<T> {

    private View itemView;
    private int posInAdapter;

    public CenterGridViewHolder(View v, int i) {
        this.itemView = v;
        this.posInAdapter = i;
    }

    abstract protected void onBind(T item);

    public int getAdapterPosition() {
        return posInAdapter;
    }

    public View getItemView() {
        return itemView;
    }
}
