package com.merlin.view.recycler;

import android.databinding.ObservableBoolean;
import android.support.v7.widget.RecyclerView;

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
        super(false);
        if (isInit) {
            initData();
        }
    }

    public ObservableBoolean isRefreshing = new ObservableBoolean(false);
    public ObservableBoolean isLoading = new ObservableBoolean(false);

    protected ArrayList<T> mDataList = new ArrayList<>();

    public RecyclerView.Adapter mAdapter = initAdapter();

    public abstract void loadMore();

    public abstract RecyclerView.Adapter initAdapter();

    public boolean canRefresh() {
        return !isRefreshing.get() && !isLoading.get();
    }

    public boolean canLoad() {
        return !isRefreshing.get() && !isLoading.get() && !ValiUtil.isEmpty(mDataList);
    }

}
