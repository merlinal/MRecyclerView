package com.merlin.view.recycler;

/**
 * @author merlin
 */

public interface OnBindDataListener<T> {
    /**
     * 绑定数据
     *
     * @param holder
     * @param position
     * @param t
     */
    void onBindData(RecyclerViewHolder holder, int position, T t);
}
