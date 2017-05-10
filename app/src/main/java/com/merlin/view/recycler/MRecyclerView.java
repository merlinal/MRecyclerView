package com.merlin.view.recycler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

import com.merlin.view.R;

/**
 * Created by ncm on 16/11/13.
 */

public class MRecyclerView extends RecyclerView {

    public final static int MODE_LIST = 1;
    public final static int MODE_GRID = 2;
    public final static int MODE_STAGGER = 3;

    public MRecyclerView(Context context) {
        this(context, null, 0);
    }

    public MRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs != null) {
            TypedArray typed = context.obtainStyledAttributes(attrs, R.styleable.MRecyclerView);
            if (typed != null) {
                mode = typed.getInt(R.styleable.MRecyclerView_mode, MODE_LIST);
                orientation = typed.getInt(R.styleable.MRecyclerView_orientation, LinearLayoutManager.VERTICAL);
                dividerHeight = (int) typed.getDimension(R.styleable.MRecyclerView_dividerHeight, 0);
                dividerOffsetStart = typed.getInteger(R.styleable.MRecyclerView_dividerOffsetStart, 0);
                dividerOffsetEnd = typed.getInteger(R.styleable.MRecyclerView_dividerOffsetEnd, 0);
                dividerColor = typed.getColor(R.styleable.MRecyclerView_dividerColor, Color.TRANSPARENT);
                numColumns = typed.getInteger(R.styleable.MRecyclerView_numColumns, 1);
                typed.recycle();
            }
            setRecycler();
        }
    }

    private int mode = MODE_LIST;
    private int orientation;
    private int dividerHeight;
    private int dividerOffsetStart;
    private int dividerOffsetEnd;
    private int dividerColor;
    private int numColumns;

    private boolean loading = false;

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

        if (onScrollListener != null) {
            LayoutManager layoutManager = getLayoutManager();
            if (layoutManager == null) { //it maybe unnecessary
                throw new RuntimeException("LayoutManagers is null,Please check it!");
            }
            Adapter adapter = getAdapter();
            if (adapter == null) { //it maybe unnecessary
                throw new RuntimeException("Adapter is null,Please check it!");
            }
            //GridLayoutManager
            if (layoutManager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                int rowCount = (int) Math.ceil(adapter.getItemCount() * 1.0 / gridLayoutManager.getSpanCount());
                int lastVisibleRowPosition = (int) Math.ceil(gridLayoutManager.findLastVisibleItemPosition() * 1.0 / gridLayoutManager.getSpanCount());
                if (!loading) {
                    onScrollListener.onScroll(this, getScrollState(), lastVisibleRowPosition, rowCount);
                }
            }
            //LinearLayoutManager
            else if (layoutManager instanceof LinearLayoutManager) {
                int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                int rowCount = adapter.getItemCount();
                if (!loading) {
                    onScrollListener.onScroll(this, getScrollState(), lastVisibleItemPosition, rowCount);
                }
            }
            //StaggeredGridLayoutManager
            else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                int spanCount = staggeredGridLayoutManager.getSpanCount();
                int[] into = new int[spanCount];
                int[] eachSpanListVisibleItemPosition = staggeredGridLayoutManager.findLastVisibleItemPositions(into);
                int lastPosition = 0;
                for (int i = 0; i < spanCount; i++) {
                    lastPosition = Math.max(lastPosition, eachSpanListVisibleItemPosition[i]);
                }
                if (!loading) {
                    onScrollListener.onScroll(this, getScrollState(),
                            (int) Math.ceil(lastPosition / spanCount),
                            (int) Math.ceil(adapter.getItemCount() * 1.0 / spanCount));
                }
            }
        }
    }

    private void setRecycler() {
        switch (mode) {
            case MODE_LIST:
                list(orientation, dividerHeight, dividerOffsetStart, dividerOffsetEnd, dividerColor);
                break;
            case MODE_GRID:
                grid(numColumns, orientation, dividerHeight, dividerOffsetStart, dividerOffsetEnd, dividerColor);
                break;
            case MODE_STAGGER:
                staggered(numColumns, orientation, dividerHeight, dividerOffsetStart, dividerOffsetEnd, dividerColor);
                break;
        }
    }

    private void list(int orientation, int dividerHeight, int dividerOffsetStart, int dividerOffsetEnd, int dividerColor) {
        setLayoutManager(new LinearLayoutManager(getContext(), orientation, false));
        addItemDecoration(new RecyclerListDecoration(getContext(), orientation, dividerHeight, dividerOffsetStart, dividerOffsetEnd, dividerColor));
        setItemAnimator(new DefaultItemAnimator());
    }

    private void grid(int numColumns, int orientation, int dividerHeight, int dividerOffsetStart, int dividerOffsetEnd, int dividerColor) {
        setLayoutManager(new GridLayoutManager(getContext(), numColumns, orientation, false));
        if (dividerHeight > 0) {
            addItemDecoration(new RecyclerGridDecoration(getContext(), orientation, dividerHeight, dividerOffsetStart, dividerOffsetEnd, dividerColor));
        }
        setItemAnimator(new DefaultItemAnimator());
    }

    private void staggered(int numColumns, int orientation, int dividerHeight, int dividerOffsetStart, int dividerOffsetEnd, int dividerColor) {
        setLayoutManager(new StaggeredGridLayoutManager(numColumns, orientation));
        if (dividerHeight > 0) {
            addItemDecoration(new RecyclerGridDecoration(getContext(), orientation, dividerHeight, dividerOffsetStart, dividerOffsetEnd, dividerColor));
        }
        setItemAnimator(new DefaultItemAnimator());
    }

    public void set(RecyclerView.LayoutManager layoutManager, ItemDecoration decoration, ItemAnimator animator) {
        setLayoutManager(layoutManager);

        if (decoration == null) {
            decoration = new RecyclerListDecoration(getContext(), orientation, 0, 0, 0, 0);
        }
        addItemDecoration(decoration);

        if (animator == null) {
            animator = new DefaultItemAnimator();
        }
        setItemAnimator(animator);
    }

    public void set(int mode, int orientation, int numColumns, int dividerHeight, int dividerOffsetStart, int dividerOffsetEnd, int dividerColor) {
        this.mode = mode;
        this.orientation = orientation;
        this.dividerHeight = dividerHeight;
        this.dividerOffsetStart = dividerOffsetStart;
        this.dividerOffsetEnd = dividerOffsetEnd;
        this.dividerColor = dividerColor;
        this.numColumns = numColumns;
        setRecycler();
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public interface OnScrollListener {
        void onScroll(MRecyclerView recyclerView, int state, int lastVisibleRow, int rowCount);
    }

    private OnScrollListener onScrollListener;

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

}
