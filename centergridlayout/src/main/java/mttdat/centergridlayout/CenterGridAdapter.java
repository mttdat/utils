package mttdat.centergridlayout;

import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

abstract public class CenterGridAdapter<T> {

    private List<T> listItems;
    private List<CenterGridViewHolder<T>> centerGridViewHolders;

    private CenterGridLayout centerGridLayout;  // Attached centerGridLayout.
    private int resource;

    private CenterGridLayout.OnAttachedAdapterListener onAttachedAdapterListener = new CenterGridLayout.OnAttachedAdapterListener() {
        @Override
        public void onAttached(CenterGridLayout layout) {
            centerGridLayout = layout;
        }
    };


    public CenterGridAdapter(int resource) {
        this.resource = resource;
        this.centerGridViewHolders = new ArrayList<>();
    }

    public CenterGridAdapter(int resource, List<T> listItems) {
        this(resource);
        this.listItems = listItems;
    }

    public void inflateViewItem(LayoutInflater inflater, int posInAdapter) {

        View v = inflater.inflate(resource, centerGridLayout, false);
        v.setId(View.generateViewId());

        CenterGridViewHolder<T> centerGridViewHolder = onCreateViewHolder(v, posInAdapter);

        centerGridViewHolders.add(centerGridViewHolder);
    }

    protected abstract CenterGridViewHolder<T> onCreateViewHolder(View v, int posInAdapter);

    public void notifyDataSetChanged() {
        for (int i = 0; i < centerGridViewHolders.size(); i++) {
            notifyItemChanged(i);
        }
    }

    public void notifyItemChanged(int pos) {
        centerGridViewHolders.get(pos).onBind(listItems.get(pos));
        centerGridViewHolders.get(pos).getItemView().requestLayout();
    }

    public int getCount() {
        return listItems != null ? listItems.size() : 0;
    }

    public View getItemView(int pos) {
        return centerGridViewHolders == null ? null : centerGridViewHolders.get(pos).getItemView();
    }

    public CenterGridLayout.OnAttachedAdapterListener getOnAttachedAdapterListener() {
        return onAttachedAdapterListener;
    }

    public CenterGridLayout getAttachedCenterGridLayout() {
        return centerGridLayout;
    }
}
