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
import com.merlin.view.recycler.load.BlankFooter;
import com.merlin.view.recycler.load.BlankHeader;
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

    public final static int MODE_PROGRESS = 1;
    public final static int MODE_BLANK = 2;

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
    /**
     * 底部还有mAutoLoadOffset项开始自动加载下一页数据
     */
    private int mAutoLoadOffset;

    private int mode = MODE_LIST;
    private int orientation;
    private int dividerHeight;
    /**
     * 前dividerOffsetStart项之间无分隔线
     */
    private int dividerOffsetStart;
    /**
     * 后dividerOffsetStart项之间无分隔线
     */
    private int dividerOffsetEnd;
    private int dividerColor;
    private int numColumns;
    private int emptyId;
    /**
     * MRecyclerView的最大宽度
     */
    private int maxWidth;
    /**
     * MRecyclerView的最大高度
     */
    private int maxHeight;

    private View mEmptyView;

    private OnDividerChangedListener mDividerChangedListener;

    private void init(Context context, @Nullable AttributeSet attrs) {
        int headerMode = MODE_PROGRESS, footerMode = MODE_PROGRESS;
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
                isPullLoad = typed.getBoolean(R.styleable.MRecyclerView_pullLoad, true);
                isPullRefresh = typed.getBoolean(R.styleable.MRecyclerView_pullRefresh, true);
                mAutoLoadOffset = typed.getInt(R.styleable.MRecyclerView_autoLoadOffset, 0);
                maxWidth = (int) typed.getDimension(R.styleable.MRecyclerView_maxWidth, 0);
                maxHeight = (int) typed.getDimension(R.styleable.MRecyclerView_maxHeight, 0);
                isAutoLoad = mAutoLoadOffset > 0;
                mAutoLoadOffset = Math.min(mAutoLoadOffset, 5);

                headerMode = typed.getInt(R.styleable.MRecyclerView_refreshHeader, MODE_PROGRESS);
                footerMode = typed.getInt(R.styleable.MRecyclerView_loadFooter, MODE_PROGRESS);

                typed.recycle();
            }
        }

        setRecycler();

        setPullRefresh(isPullRefresh);
        setPullLoad(isPullLoad);
        mWrapAdapter = new WrapAdapter();

        switch (headerMode) {
            case MODE_PROGRESS:
                setRefreshHeader(new ProgressHeader(getContext()));
                break;
            default:
                setRefreshHeader(new BlankHeader(getContext()));
                break;
        }
        switch (footerMode) {
            case MODE_PROGRESS:
                setLoadFooter(new ProgressFooter(getContext()));
                break;
            default:
                setLoadFooter(new BlankFooter(getContext()));
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置控件最大宽高
        if (maxWidth > 0) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST);
        }
        if (maxHeight > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        }
        //重新计算控件高、宽
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
//        setOverScrollMode(isPullRefresh ? OVER_SCROLL_NEVER : OVER_SCROLL_IF_CONTENT_SCROLLS);
    }

    public void setPullLoad(boolean enabled) {
        isPullLoad = enabled;
//        setOverScrollMode(isPullLoad ? OVER_SCROLL_NEVER : OVER_SCROLL_IF_CONTENT_SCROLLS);
    }

    /**
     * 下拉刷新头部
     *
     * @param headerView 头部View
     */
    public void setRefreshHeader(AbstractHeaderView headerView) {
        if (iHeader == null) {
            dividerOffsetStart++;
            dividerOffsetChanged();
        }
        iHeader = headerView;
        mWrapAdapter.setRefreshHeader(iHeader);
    }

    /**
     * 加载更多底部
     *
     * @param footerView 底部View
     */
    public void setLoadFooter(AbstractFooterView footerView) {
        if (iFooter == null) {
            dividerOffsetEnd++;
            dividerOffsetChanged();
        }
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
        //scrollState有三种状态，分别是开始滚动（SCROLL_STATE_FLING），正在滚动(SCROLL_STATE_TOUCH_SCROLL), 已经停止（SCROLL_STATE_IDLE
        //当 滚动停止&没有正在刷新&没有正在加载更多&有更多数据&自动加载开启 时
        if (state == RecyclerView.SCROLL_STATE_IDLE
                && !iHeader.isLoading()
                && onLoadListener != null && !iFooter.isLoading() && iFooter.isHasMore() && isAutoLoad) {
            //当 已有item&最后可见的item到达自动加载条件&adapter中item数不小于子view数 时
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
        //不在顶部|不可下拉 且 不在底部|不可上拉 时，跳过onTouchEvent
        boolean isSkip = !(isOnTop && isPullRefresh) && !(isOnBottom && isPullLoad);
        if (isSkip) {
            mLastY = -1;
            return super.onTouchEvent(ev);
        }
        if (mLastY == -1) {
            mLastY = ev.getRawY();
            if (isOnTop) {
                iHeader.startLoad();
            } else {
                iFooter.startLoad();
            }
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //记录y
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                //y偏移量
                float deltaY = ev.getRawY() - mLastY;
                //更新y
                mLastY = ev.getRawY();
                //在顶部，下拉；在底部，上拉
                if (isOnTop && isPullRefresh && mAppbarState == AppBarStateChangeListener.State.EXPANDED) {
                    iHeader.onPulling(deltaY);
                } else if (isOnBottom && isPullLoad) {
                    iFooter.onPulling(deltaY);
                }
                //header被拉伸
                if (iHeader.isHigherThanOrigin()) {
                    return false;
                }
                break;
            default:
                //重置y
                mLastY = -1;
                if (isOnTop && isPullRefresh && mAppbarState == AppBarStateChangeListener.State.EXPANDED) {
                    //达到下拉刷新条件&没有正在刷新，执行刷新；否则，取消刷新
                    if (iHeader.isLoadReady() && onRefreshListener != null && !iHeader.isLoading()) {
                        onRefreshListener.onRefresh();
                        iHeader.loading();
                    } else {
                        iHeader.loadCancel();
                    }
                } else if (isOnBottom && isPullLoad) {
                    //达到上拉加载条件&没有正在加载&有更多数据，执行加载；否则，取消加载
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
        //最后一个完整显示的item位置
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
                //=2 是因为下拉刷新头部+上拉加载底部一直都在
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
        //不满一屏，不显示加载更多
        if (!isOnBottom()) {
            iFooter.show();
        } else {
            hideLoadFooter();
        }
    }

    public void loaded() {
        iFooter.loaded();
    }

    public void hideLoadFooter() {
        isPullLoad = false;
        iFooter.setVisibility(View.GONE);
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
            default:
                break;
        }
    }

    private void list(int orientation, int dividerHeight, int dividerOffsetStart, int dividerOffsetEnd, int dividerColor) {
        setLayoutManager(new LinearLayoutManager(getContext(), orientation, false));
        setItemAnimator(new DefaultItemAnimator());
        if (dividerHeight > 0) {
            RecyclerListDecoration decoration = new RecyclerListDecoration(getContext(), orientation, dividerHeight, dividerOffsetStart, dividerOffsetEnd, dividerColor);
            addItemDecoration(decoration);
            setOnDividerChangedListener(decoration);
        }
    }

    private void grid(int numColumns, int orientation, int dividerHeight, int dividerOffsetStart, int dividerOffsetEnd, int dividerColor) {
        setLayoutManager(new GridLayoutManager(getContext(), numColumns, orientation, false));
        setItemAnimator(new DefaultItemAnimator());
        if (dividerHeight > 0) {
            addItemDecoration(new RecyclerGridDecoration(getContext(), orientation, dividerHeight, dividerOffsetStart, dividerOffsetEnd, dividerColor));
        }
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

    /*****************************  ItemDecoration的配置  *****************************/

    private void dividerOffsetChanged() {
        if (mDividerChangedListener != null) {
            mDividerChangedListener.onOffsetChanged(dividerOffsetStart, dividerOffsetEnd);
        }
    }

    public void setOnDividerChangedListener(OnDividerChangedListener onDividerChangedListener) {
        this.mDividerChangedListener = onDividerChangedListener;
    }

    public interface OnDividerChangedListener {
        /**
         * 参数变化
         *
         * @param offsetStart 前偏移
         * @param offsetEnd   后偏移
         */
        void onOffsetChanged(int offsetStart, int offsetEnd);
    }

}
