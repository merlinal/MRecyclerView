package com.merlin.view.recycler;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by zal on 2017/8/19.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private AdapterHelper helper = new AdapterHelper(this);
    private boolean isDataBinding = false;

    public RecyclerAdapter() {
    }

    public RecyclerAdapter(boolean isDataBinding) {
        this.isDataBinding = isDataBinding;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return helper.onCreateViewHolder(parent, viewType, isDataBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        helper.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return helper.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return helper.getItemViewType(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (helper.isHeader(position) || helper.isFooter(position)) ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            if ((helper.isHeader(holder.getLayoutPosition()) || helper.isFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    public int getHeaderCount() {
        return helper.getHeaderCount();
    }

    public int getFooterCount() {
        return helper.getFooterCount();
    }

    public int getContentCount() {
        return helper.getContentCount();
    }

    public int getCount(int viewType) {
        return helper.getCount(viewType);
    }

    //添加数据

    public void add(int layoutId) {
        add(layoutId, layoutId, null);
    }

    public void add(int layoutId, int viewType) {
        add(layoutId, viewType, null);
    }

    public <T> void add(int layoutId, OnBindDataListener<T> onBindDataListener) {
        add(layoutId, layoutId, onBindDataListener);
    }

    public void add(int layoutId, int viewType, OnBindDataListener onBindDataListener) {
        helper.add(layoutId, viewType, onBindDataListener);
    }

    public <T> void add(int layoutId, OnBindDataListener<T> onBindDataListener, List<T> list) {
        add(layoutId, layoutId, onBindDataListener, list);
    }

    public <T> void add(int layoutId, int viewType, OnBindDataListener<T> onBindDataListener, List<T> list) {
        helper.add(layoutId, viewType, onBindDataListener, list);
    }

    public <T> void add(int layoutId, int viewType, OnBindDataListener<T> onBindDataListener, T... ts) {
        helper.add(layoutId, viewType, onBindDataListener, ts);
    }

    //插入数据

    public void insert(int position, int layoutId) {
        insert(position, layoutId, layoutId, null);
    }

    public void insert(int position, int layoutId, int viewType) {
        insert(position, layoutId, viewType, null);
    }

    public void insert(int position, int layoutId, int viewType, OnBindDataListener onBindDataListener) {
        helper.insert(position, layoutId, viewType, onBindDataListener);
    }

    public <T> void insert(int position, int layoutId, OnBindDataListener<T> onBindDataListener, List<T> list) {
        insert(position, layoutId, layoutId, onBindDataListener, list);
    }

    public <T> void insert(int position, int layoutId, OnBindDataListener<T> onBindDataListener) {
        insert(position, layoutId, layoutId, onBindDataListener);
    }

    public <T> void insert(int position, int layoutId, int viewType, OnBindDataListener<T> onBindDataListener, List<T> list) {
        helper.insert(position, layoutId, viewType, onBindDataListener, list);
    }

    public <T> void insert(int position, int layoutId, int viewType, OnBindDataListener<T> onBindDataListener, T... ts) {
        helper.insert(position, layoutId, viewType, onBindDataListener, ts);
    }

    //页脚
    public void addFooter(int layoutId) {
        addFooter(layoutId, layoutId, null);
    }

    public void addFooter(int layoutId, int viewType) {
        addFooter(layoutId, viewType, null);
    }

    public void addFooter(int layoutId, int viewType, OnBindDataListener onBindDataListener) {
        addFooter(layoutId, viewType, onBindDataListener, null);
    }

    public <T> void addFooter(int layoutId, int viewType, OnBindDataListener<T> onBindDataListener, T t) {
        helper.addFooter(layoutId, viewType, onBindDataListener, t);
    }

    //页眉
    public void addHeader(int layoutId) {
        addFooter(layoutId, layoutId, null);
    }

    public void addHeader(int layoutId, int viewType) {
        addHeader(layoutId, viewType, null);
    }

    public void addHeader(int layoutId, int viewType, OnBindDataListener onBindDataListener) {
        addHeader(layoutId, viewType, onBindDataListener, null);
    }

    public <T> void addHeader(int layoutId, int viewType, OnBindDataListener<T> onBindDataListener, T t) {
        helper.addHeader(layoutId, viewType, onBindDataListener, t);
    }

    //清空数据
    public void remove() {
        helper.remove();
    }

    public void remove(int position) {
        helper.remove(position);
    }

    /**
     * 删掉除header和footer以外的其他数据
     */
    public void removeContents() {
        helper.removeContents();
    }

    public void removeHeaders() {
        helper.removeHeaders();
    }

    public void removeFooters() {
        helper.removeFooters();
    }

    public void removeItems(int viewType) {
        helper.removeItems(viewType);
    }

    //重置数据
    public <T> void reset(List<T> list){
        helper.reset(list);
    }

}
