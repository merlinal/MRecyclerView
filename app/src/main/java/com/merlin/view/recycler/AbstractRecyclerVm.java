package com.merlin.view.recycler;

import android.databinding.ObservableBoolean;

import com.merlin.core.base.AbstractVM;
import com.merlin.core.util.ValiUtil;

import java.util.ArrayList;

/**
 * Created by ncm on 16/11/14.
 */

public abstract class AbstractRecyclerVm<T> extends AbstractVM {

    public AbstractRecyclerVm() {
        this(true);
    }

    public AbstractRecyclerVm(boolean isInit) {
        isRefreshing = new ObservableBoolean(false);
        isLoading = new ObservableBoolean(false);
        mDataList = new ArrayList<>();
        if (isInit) {
            initData();
        }
    }

    public ObservableBoolean isRefreshing;
    public ObservableBoolean isLoading;

    protected ArrayList<T> mDataList;

    public AbstractRecyclerAdapter<T> mAdapter = initAdapter();

    public abstract void loadMore();

    public abstract AbstractRecyclerAdapter<T> initAdapter();

    public boolean canRefresh() {
        return !isRefreshing.get() && !isLoading.get();
    }

    public boolean canLoad() {
        return !isRefreshing.get() && !isLoading.get() && !ValiUtil.isEmpty(mDataList);
    }

}
