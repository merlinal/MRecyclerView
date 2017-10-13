package com.merlin.view.recycler;

import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ncm on 16/11/11.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    //使用dataBinding

    public RecyclerViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    private ViewDataBinding binding;

    public ViewDataBinding getBinding() {
        return binding;
    }


    //不使用dataBinding

    private SparseArray<View> viewHolder;

    public RecyclerViewHolder(ViewGroup parent, @LayoutRes int resId) {
        super(LayoutInflater.from(parent.getContext()).inflate(resId, parent, false));
        viewHolder = new SparseArray<>();
    }

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        viewHolder = new SparseArray<>();
    }

    /**
     * @param id
     * @param <T>
     * @return
     */
    public <T extends View> T view(int id) {
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = itemView.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }

}
