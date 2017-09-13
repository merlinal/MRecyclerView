package com.merlin.view.recycler.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.merlin.view.recycler.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zal on 2017/9/8.
 */

public class AdapterHelper {

    private RecyclerView.Adapter adapter;

    public AdapterHelper(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    private class Data<T> {

        private int viewType;
        private T t;
        private OnBindDataListener<T> onBindDataListener;

        public Data(int viewType, T t, OnBindDataListener<T> onBindDataListener) {
            this.viewType = viewType;
            this.t = t;
            this.onBindDataListener = onBindDataListener;
        }
    }

    private SparseIntArray layouts = new SparseIntArray();
    private List<Data> dataList = new ArrayList<>();
    private int footerCount;
    private int headerCount;

    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isDataBinding) {
        return isDataBinding ?
                new RecyclerViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layouts.get(viewType), parent, false))
                : new RecyclerViewHolder(parent, layouts.get(viewType));
    }

    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Data data = dataList.get(position);
        if (data.onBindDataListener != null) {
            data.onBindDataListener.onBindData(holder, position, data.t);
        }
    }

    public int getItemCount() {
        return dataList.size();
    }

    public int getItemViewType(int position) {
        return dataList.get(position).viewType;
    }

    public int getHeaderCount() {
        return headerCount;
    }

    public int getFooterCount() {
        return footerCount;
    }

    public int getContentCount() {
        return dataList.size() - headerCount - footerCount;
    }

    public int getCount(int viewType) {
        int count = 0;
        for (Data data : dataList) {
            if (data.viewType == viewType) {
                count++;
            }
        }
        return count;
    }

    public boolean isHeader(int position) {
        return position < headerCount;
    }

    public boolean isFooter(int position) {
        return position >= dataList.size() - footerCount;
    }

    //添加数据
    public void add(int layoutId, int viewType, OnBindDataListener onBindDataListener) {
        layouts.put(viewType, layoutId);
        int startPosition = dataList.size() - footerCount;
        dataList.add(startPosition, new Data<>(viewType, null, onBindDataListener));
        adapter.notifyItemInserted(startPosition);
    }

    public <T> void add(int layoutId, int viewType, OnBindDataListener<T> onBindDataListener, List<T> list) {
        if (list != null && list.size() > 0) {
            layouts.put(viewType, layoutId);
            int startPosition = dataList.size() - footerCount;
            for (T t : list) {
                dataList.add(startPosition, new Data<>(viewType, t, onBindDataListener));
                startPosition++;
            }
            adapter.notifyDataSetChanged();
        }
    }

    public <T> void add(int layoutId, int viewType, OnBindDataListener<T> onBindDataListener, T... ts) {
        if (ts != null && ts.length > 0) {
            layouts.put(viewType, layoutId);
            int startPosition = dataList.size() - footerCount;
            for (T t : ts) {
                dataList.add(startPosition, new Data<>(viewType, t, onBindDataListener));
                startPosition++;
            }
            adapter.notifyDataSetChanged();
        }
    }

    //插入数据
    public void insert(int position, int layoutId, int viewType, OnBindDataListener onBindDataListener) {
        layouts.put(viewType, layoutId);
        position = getInsertPosition(position);
        dataList.add(position, new Data<>(viewType, null, onBindDataListener));
        adapter.notifyItemInserted(position);
    }

    public <T> void insert(int position, int layoutId, int viewType, OnBindDataListener<T> onBindDataListener, List<T> list) {
        if (list != null && list.size() > 0) {
            layouts.put(viewType, layoutId);
            position = getInsertPosition(position);
            for (T t : list) {
                dataList.add(position, new Data<>(viewType, t, onBindDataListener));
                position++;
            }
            adapter.notifyDataSetChanged();
        }
    }

    public <T> void insert(int position, int layoutId, int viewType, OnBindDataListener<T> onBindDataListener, T... ts) {
        if (ts != null && ts.length > 0) {
            layouts.put(viewType, layoutId);
            position = getInsertPosition(position);
            for (T t : ts) {
                dataList.add(position, new Data<>(viewType, t, onBindDataListener));
                position++;
            }
            adapter.notifyDataSetChanged();
        }
    }

    //边界校正
    private int getInsertPosition(int position) {
        if (position < 0) {
            position = headerCount;
        }
        if (position > dataList.size()) {
            position = dataList.size() - footerCount;
        }
        return position;
    }

    //页脚
    public <T> void addFooter(int layoutId, int viewType, OnBindDataListener<T> onBindDataListener, T t) {
        layouts.put(viewType, layoutId);
        dataList.add(new Data<>(viewType, t, onBindDataListener));
        footerCount++;
        adapter.notifyDataSetChanged();
    }

    //页眉
    public <T> void addHeader(int layoutId, int viewType, OnBindDataListener<T> onBindDataListener, T t) {
        layouts.put(viewType, layoutId);
        dataList.add(0, new Data<>(viewType, t, onBindDataListener));
        headerCount++;
        adapter.notifyDataSetChanged();
    }

    //清空数据
    public void remove() {
        dataList.clear();
        adapter.notifyDataSetChanged();
    }

    public void remove(int position) {
        dataList.remove(position);
        adapter.notifyDataSetChanged();
    }

    /**
     * 删掉除header和footer以外的其他数据
     */
    public void removeContents() {
        while (headerCount + footerCount < dataList.size()) {
            dataList.remove(headerCount);
        }
        adapter.notifyDataSetChanged();
    }

    public void removeHeaders() {
        while (headerCount > 0) {
            dataList.remove(0);
            headerCount--;
        }
        adapter.notifyDataSetChanged();
    }

    public void removeFooters() {
        while (footerCount > 0) {
            dataList.remove(dataList.size() - 1);
            footerCount--;
        }
        adapter.notifyDataSetChanged();
    }

    public void removeItems(int viewType) {
        int position = 0;
        while (position < dataList.size()) {
            if (dataList.get(position).viewType == viewType) {
                dataList.remove(position);
            } else {
                position++;
            }
        }
        adapter.notifyDataSetChanged();
    }

}
