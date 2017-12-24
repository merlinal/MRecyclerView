package com.merlin.view.recycler;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ncm on 16/11/13.
 */

public abstract class AbstractRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {

    private List<T> mDataList;

    public AbstractRecyclerAdapter() {
        mDataList = new ArrayList<>();
    }

    public AbstractRecyclerAdapter(List<T> mDataList) {
        if (mDataList != null) {
            this.mDataList = mDataList;
        } else {
            this.mDataList = new ArrayList<>();
        }
    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolder viewHolder = null;
        ViewDataBinding binding = getItemBinding(parent, viewType);
        if (binding != null) {
            viewHolder = new RecyclerViewHolder(binding);
        }
        if (viewHolder == null) {
            int layoutResId = getItemResId(parent, viewType);
            if (layoutResId != 0) {
                viewHolder = new RecyclerViewHolder(parent, layoutResId);
            }
        }
        if (viewHolder == null) {
            View itemView = getItemView(parent, viewType);
            if (itemView != null) {
                viewHolder = new RecyclerViewHolder(itemView);
            }
        }
        return viewHolder;
    }

    /**
     * @param parent
     * @param viewType
     * @return
     */
    public ViewDataBinding getItemBinding(ViewGroup parent, int viewType) {
        return null;
    }

    /**
     * @param parent
     * @param viewType
     * @return
     */
    public int getItemResId(ViewGroup parent, int viewType) {
        return 0;
    }

    /**
     * @param parent
     * @param viewType
     * @return
     */
    public View getItemView(ViewGroup parent, int viewType) {
        return null;
    }

    /**
     * @param position
     * @return
     */
    protected T getData(int position) {
        return mDataList.get(position);
    }

    /**
     * 重置data
     *
     * @param dataList
     */
    public void reset(List<T> dataList) {
        this.mDataList = dataList;
        notifyDataSetChanged();
    }

    /**
     * @param dataList
     */
    public void addAll(List<T> dataList) {
        if (this.mDataList == null) {
            this.mDataList = new ArrayList<>();
        }
        this.mDataList.addAll(dataList);
        notifyDataSetChanged();
    }

}
