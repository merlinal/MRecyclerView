package com.merlin.view.recycler.adapter;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.merlin.core.util.ValiUtil;
import com.merlin.view.recycler.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ncm on 16/11/13.
 */

public abstract class AbstractRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {

    private ArrayList<T> mDataList;

    public AbstractRecyclerAdapter() {
        this.mDataList = new ArrayList<>();
    }

    public AbstractRecyclerAdapter(ArrayList<T> mDataList) {
        if (mDataList != null) {
            this.mDataList = mDataList;
        } else {
            this.mDataList = new ArrayList<>();
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getNormalCount() + getFooterCount();
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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isHeader(position) || isFooter(position)) ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            if ((isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
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
     * header数量
     *
     * @return
     */
    protected int getHeaderCount() {
        return 0;
    }

    /**
     * footer数量
     *
     * @return
     */
    protected int getFooterCount() {
        return 0;
    }

    /**
     * 数据集item数量
     *
     * @return
     */
    private int getNormalCount() {
        return ValiUtil.isEmpty(mDataList) ? 0 : mDataList.size();
    }

    /**
     * 是否headerView
     *
     * @param position
     * @return
     */
    protected boolean isHeader(int position) {
        return position < getHeaderCount();
    }

    /**
     * 是否footerView
     *
     * @param position
     * @return
     */
    protected boolean isFooter(int position) {
        return position >= getHeaderCount() + getNormalCount();
    }

    /**
     * 获取实际位置
     *
     * @param position
     * @return
     */
    protected int getRealPosition(int position) {
        return position - getHeaderCount();
    }

    /**
     * @param position
     * @return
     */
    protected T getData(int position) {
        return mDataList.get(getRealPosition(position));
    }

    /**
     * 重置data
     *
     * @param dataList
     */
    public void setDataList(List<T> dataList) {
        if (this.mDataList == null) {
            this.mDataList = new ArrayList<>();
        } else {
            this.mDataList.clear();
        }
        this.mDataList.addAll(dataList);
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

//    protected OnItemClickListener onItemClickListener;
//
//    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }

}
