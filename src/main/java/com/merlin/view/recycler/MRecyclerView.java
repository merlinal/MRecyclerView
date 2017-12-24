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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.merlin.view.recycler.load.AbstractFooterView;
import com.merlin.view.recycler.load.AbstractHeaderView;
import com.merlin.view.recycler.load.ProgressFooter;
import com.merlin.view.recycler.load.ProgressHeader;

/**
 * @author zal
 * @date 2017/12/16.
 */

public class MRecyclerView extends RecyclerView {

    public final static int MODE_LIST = 1;
    public final static int MODE_GRID = 2;
    public final static int MODE_STAGGER = 3;

    public MRecyclerView(Context context) {
        this(context, null);
    }

    public MRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private WrapAdapter mWrapAdapter;
    private DataObserver mDataObserver = new DataObserver();

    private float mLastY = -1f;
    private boolean isPullRefresh = false;
    private AppBarStateChangeListener.State mAppbarState = AppBarStateChangeListener.State.EXPANDED;

    private AbstractHeaderView iHeader;
    private AbstractFooterView iFooter;

    private OnRefreshListener onRefreshListener;
    private OnLoadListener onLoadListener;

    private boolean isAutoLoad = false;
    private boolean isPullLoad = false;

    private int mAutoLoadOffset = 0;

    private int mode = MODE_LIST;
    private int orientation;
    private int dividerHeight;
    private int dividerOffsetStart;
    private int dividerOffsetEnd;
    private int dividerColor;
    private int numColumns;
    private int emptyId;

    private View mEmptyView;

    private void init(Context context, @Nullable AttributeSet attrs) {
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
                emptyId = typed.getResourceId(R.styleable.MRecyclerView_emptyId, 0);
                typed.recycle();
            }
        }
        setRecycler();

        setPullRefresh(true);
        setPullLoad(true);
        mWrapAdapter = new WrapAdapter();
        setRefreshHeader(new ProgressHeader(getContext()));
        setLoadFooter(new ProgressFooter(getContext()));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (emptyId != 0 && mEmptyView == null) {
            mEmptyView = ((View) getParent()).findViewById(emptyId);
        }
    }

    /*****************************  MRecyclerView的刷新&加载更多  *****************************/

    public void setPullRefresh(boolean enabled) {
        isPullRefresh = enabled;
        setOverScrollMode(isPullRefresh ? OVER_SCROLL_NEVER : OVER_SCROLL_IF_CONTENT_SCROLLS);
    }

    public void setPullLoad(boolean enabled) {
        isPullLoad = enabled;
        setOverScrollMode(isPullLoad ? OVER_SCROLL_NEVER : OVER_SCROLL_IF_CONTENT_SCROLLS);
    }

    public void setRefreshHeader(AbstractHeaderView headerView) {
        iHeader = headerView;
        mWrapAdapter.setRefreshHeader(iHeader);
    }

    public void setLoadFooter(AbstractFooterView footerView) {
        iFooter = footerView;
        mWrapAdapter.setLoadHeader(footerView);
        iFooter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });
    }

    public void addHeader(View view) {
        mWrapAdapter.addHeader(view);
    }

    public void addFooter(View view) {
        mWrapAdapter.addFooter(view);
    }

    public void refresh() {
        smoothScrollToPosition(0);
        if (onRefreshListener != null && !iHeader.isLoading()) {
            onRefreshListener.onRefresh();
            iHeader.loading();
        }
    }

    public void load() {
        smoothScrollToPosition(getAdapter().getItemCount() - 1);
        if (onLoadListener != null && !iFooter.isLoading() && iFooter.isHasMore()) {
            onLoadListener.onLoad();
            iFooter.loading();
        }
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        mWrapAdapter.setAdapter(adapter);
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE
                && !iHeader.isLoading()
                && onLoadListener != null && !iFooter.isLoading() && iFooter.isHasMore() && isAutoLoad) {
            if (getLayoutManager().getChildCount() > 0
                    && getLastVisiblePosition() >= getAdapter().getItemCount() - 1 - mAutoLoadOffset
                    && getAdapter().getItemCount() >= getLayoutManager().getChildCount()) {
                onLoadListener.onLoad();
                iFooter.loading();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (getVisibility() == GONE) {
            return super.onTouchEvent(ev);
        }
        boolean isOnTop = isOnTop();
        boolean isOnBottom = isOnBottom();
        boolean isSkip = !(isOnTop && isPullRefresh) && !(isOnBottom && isPullLoad);
        if (isSkip) {
            mLastY = -1;
            return super.onTouchEvent(ev);
        }
        if (mLastY == -1) {
            mLastY = ev.getRawY();
            if (isOnTop) {
                iHeader.startLoad();
            } else if (isOnBottom) {
                iFooter.startLoad();
            }
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (isOnTop && isPullRefresh && mAppbarState == AppBarStateChangeListener.State.EXPANDED) {
                    iHeader.onPulling(deltaY);
                } else if (isOnBottom && isPullLoad) {
                    iFooter.onPulling(deltaY);
                }
                if (iHeader.isHigherThanOrigin()) {
                    return false;
                }
                break;
            default:
                // reset
                mLastY = -1;
                if (isOnTop && isPullRefresh && mAppbarState == AppBarStateChangeListener.State.EXPANDED) {
                    if (iHeader.isLoadReady() && onRefreshListener != null && !iHeader.isLoading()) {
                        onRefreshListener.onRefresh();
                        iHeader.loading();
                    } else {
                        iHeader.loadCancel();
                    }
                } else if (isOnBottom && isPullLoad) {
                    if (iFooter.isReadyLoad() && onLoadListener != null && !iFooter.isLoading() && iFooter.isHasMore()) {
                        onLoadListener.onLoad();
                        iFooter.loading();
                    } else {
                        iFooter.loadCancel();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private boolean isOnTop() {
        return iHeader.getParent() != null;
//        return ((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0;
    }

    private boolean isOnBottom() {
        if (iFooter.isHigherThanOrigin()) {
            return true;
        }
        return getLastVisiblePosition() == getAdapter().getItemCount() - 1;
    }

    private int getLastVisiblePosition() {
        LayoutManager layoutManager = getLayoutManager();
        int lastVisibleItemPosition;
        if (layoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            ((StaggeredGridLayoutManager) layoutManager).findLastCompletelyVisibleItemPositions(into);
            lastVisibleItemPosition = into[0];
            for (int value : into) {
                if (value > lastVisibleItemPosition) {
                    lastVisibleItemPosition = value;
                }
            }
        } else {
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
        }
        return lastVisibleItemPosition;
    }

    private class DataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
            if (mWrapAdapter != null && mEmptyView != null) {
                if (mWrapAdapter.getItemCount() == 2) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    MRecyclerView.this.setVisibility(View.GONE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                    MRecyclerView.this.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

    public void refreshed() {
        iHeader.loaded();
    }

    public void loaded() {
        iFooter.loaded();
    }

    public void setHasMore(boolean isHasMore) {
        iFooter.setHasMore(isHasMore);
    }

    public boolean isRefreshing() {
        return iHeader.isLoading();
    }

    public boolean isLoading() {
        return iFooter.isLoading();
    }

    public boolean isHasMore() {
        return iFooter.isHasMore();
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadListener {
        void onLoad();
    }

    /*****************************  MRecyclerView的空页面  *****************************/

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        ViewGroup parentView = (ViewGroup) getParent();
        if (parentView != null) {
            mEmptyView.setVisibility(View.GONE);
            parentView.addView(mEmptyView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    public void setEmptyView(int layoutId) {
        ViewGroup parentView = (ViewGroup) getParent();
        if (parentView != null) {
            mEmptyView = LayoutInflater.from(getContext()).inflate(layoutId, parentView, false);
            parentView.addView(mEmptyView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    /*****************************  MRecyclerView的便捷配置  *****************************/


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

    public void set(LayoutManager layoutManager, ItemDecoration decoration, ItemAnimator animator) {
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

}
