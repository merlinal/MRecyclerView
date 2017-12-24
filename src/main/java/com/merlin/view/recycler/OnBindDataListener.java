package com.merlin.view.recycler;

import com.merlin.view.recycler.RecyclerViewHolder;

/**
 * Created by zal on 2017/9/8.
 */

public interface OnBindDataListener<T> {
    void onBindData(RecyclerViewHolder holder, int position, T t);
}
