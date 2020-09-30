package mttdat.utils;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by swagsoft on 12/7/16.
 */

/**
 * This is abstract holder. It can be bound by T item.
 * */
public abstract class RecyclerViewHolder<T> extends RecyclerView.ViewHolder{

    public RecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBind(T sample);

    public void special_clear(){}
}
