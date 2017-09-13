package com.merlin.view.recycler;

import android.databinding.ObservableBoolean;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by ncm on 16/11/14.
 */

public abstract class AbstractRecyclerVm<T> {

    public AbstractRecyclerVm() {
        this(true);
    }

    public AbstractRecyclerVm(boolean isInit) {
        if (isInit) {
            initData();
        }
    }

    public ObservableBoolean isRefreshing = new ObservableBoolean(false);
    public ObservableBoolean isLoading = new ObservableBoolean(false);

    protected ArrayList<T> mDataList = new ArrayList<>();

    public RecyclerView.Adapter mAdapter = initAdapter();

    public abstract void initData();

    public abstract void loadMore();

    public abstract RecyclerView.Adapter initAdapter();

    public boolean canRefresh() {
        return !isRefreshing.get() && !isLoading.get();
    }

    public boolean canLoad() {
        return !isRefreshing.get() && !isLoading.get() && mDataList != null;
    }

}
