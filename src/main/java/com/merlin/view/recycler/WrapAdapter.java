package com.merlin.view.recycler;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/16.
 */

public class WrapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int typeHeaderStart = 10000;
    private int typeFooterStart = 20000;

    private ArrayList<View> mHeaderViewList = new ArrayList<>();
    private ArrayList<View> mFooterViewList = new ArrayList<>();

    private RecyclerView.Adapter adapter;

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    public RecyclerView.Adapter getOriginalAdapter() {
        return this.adapter;
    }

    public boolean isHeader(int position) {
        return position < getHeaderCount();
    }

    public boolean isFooter(int position) {
        return position >= getItemCount() - getFooterCount();
    }

    public int getHeaderCount() {
        return mHeaderViewList.size();
    }

    public int getFooterCount() {
        return mFooterViewList.size();
    }

    public void addHeader(View view) {
        mHeaderViewList.add(view);
        notifyItemInserted(mHeaderViewList.size() - 1);
    }

    public void addFooter(View view) {
        mFooterViewList.add(mFooterViewList.size() - 1, view);
    }

    public View getHeader(int index) {
        return mHeaderViewList.get(index + 1);
    }

    public View getFooter(int index) {
        return mFooterViewList.get(index);
    }

    public void setRefreshHeader(View view) {
        if (mHeaderViewList.size() == 0) {
            mHeaderViewList.add(view);
        } else {
            mHeaderViewList.set(0, view);
        }
    }

    public void setLoadHeader(View view) {
        if (mFooterViewList.size() == 0) {
            mFooterViewList.add(view);
        } else {
            mFooterViewList.set(mFooterViewList.size() - 1, view);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType >= typeFooterStart) {
            return new SimpleViewHolder(mFooterViewList.get(viewType - typeFooterStart));
        }
        if (viewType >= typeHeaderStart) {
            return new SimpleViewHolder(mHeaderViewList.get(viewType - typeHeaderStart));
        }
        return adapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeader(position) || isFooter(position)) {
            return;
        }
        if (adapter != null) {
            adapter.onBindViewHolder(holder, position - getHeaderCount());
        }
    }

    /**
     * some times we need to override this
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (isHeader(position) || isFooter(position)) {
            return;
        }
        if (adapter != null) {
            if (payloads.isEmpty()) {
                adapter.onBindViewHolder(holder, position - getHeaderCount());
            } else {
                adapter.onBindViewHolder(holder, position - getHeaderCount(), payloads);
            }
        }
    }

    @Override
    public int getItemCount() {
        return adapter.getItemCount() + getHeaderCount() + getFooterCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return typeHeaderStart + position;
        }
        if (isFooter(position)) {
            return typeFooterStart + position - getHeaderCount() - (adapter != null ? adapter.getItemCount() : 0);
        }
        if (adapter != null) {
            int type = adapter.getItemViewType(position - getHeaderCount());
            if (type >= typeHeaderStart) {
                throw new IllegalStateException("XRecyclerView require itemViewType in adapter should be less than 10000 ");
            }
            return type;
        }
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        if (adapter != null && position >= getHeaderCount()) {
            return adapter.getItemId(position - getHeaderCount());
        }
        return -1;
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
        adapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        adapter.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
        adapter.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        adapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        adapter.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        return adapter.onFailedToRecycleView(holder);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        adapter.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        adapter.registerAdapterDataObserver(observer);
    }

    private static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public SimpleViewHolder(View itemView) {
            super(itemView);
        }
    }

}
