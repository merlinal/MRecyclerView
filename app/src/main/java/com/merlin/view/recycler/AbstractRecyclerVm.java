package com.merlin.view.recycler;

import android.databinding.ObservableBoolean;

import com.merlin.core.base.AbstractVM;
import com.merlin.core.util.ValiUtil;

import java.util.ArrayList;

/**
 * Created by ncm on 16/11/14.
 */

public abstract class AbstractRecyclerVm<T> extends AbstractVM {

    public ObservableBoolean isRefreshing = new ObservableBoolean(false);
    public ObservableBoolean isLoading = new ObservableBoolean(false);

    protected ArrayList<T> mDataList = new ArrayList<>();

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
