package com.merlin.view.recycler;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ncm on 16/11/14.
 */

public class RecyclerViewListener {

    public AbstractRecyclerVm recyclerVm;
    public RecyclerView recyclerView;

    public RecyclerViewListener(AbstractRecyclerVm recyclerVm, RecyclerView recyclerView) {
        this.recyclerVm = recyclerVm;
        this.recyclerView = recyclerView;
    }

    public View.OnClickListener clickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        };
    }

    public View.OnLongClickListener longClickListener() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        };
    }

    public SwipeRefreshLayout.OnRefreshListener onRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (recyclerVm.canRefresh()) {
                    recyclerVm.initData();
                }
            }
        };
    }

    public MRecyclerView.OnScrollListener onScrollListener() {
        return new MRecyclerView.OnScrollListener() {
            @Override
            public void onScroll(MRecyclerView recyclerView, int state, int lastVisibleRow, int rowCount) {
                if (recyclerVm.canLoad() && lastVisibleRow + 3 > rowCount) {
                    recyclerVm.loadMore();
                }
            }
        };
    }

}
