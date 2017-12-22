package com.merlin.view.mrecycler;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2017/12/18.
 */

public abstract class AbstractFooterView extends FrameLayout implements ILoader {

    public AbstractFooterView(@NonNull Context context) {
        this(context, null);
    }

    public AbstractFooterView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractFooterView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected View mView;
    private int mViewHeight;
    private boolean isLoading = false;
    private boolean isHasMore = true;

    private void init() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        setLayoutParams(params);
        setPadding(0, 0, 0, 0);

        mView = initLoadView();
        mView.measure(0, 0);
        mViewHeight = mView.getMeasuredHeight();
        addView(mView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 0));
    }

    public void startLoad() {
        if (!isLoading && isHasMore) {
            onLoadStart();
        }
    }

    public void onPulling(float deltaY) {
        if (deltaY == 0) {
            return;
        }
        deltaY = deltaY / 2;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mView.getLayoutParams();
        params.height -= deltaY;
        if (params.height < mViewHeight) {
            params.height = isLoading ? mViewHeight + 1 : mViewHeight;
        }
        mView.setLayoutParams(params);

        onLoadPulling(params.height, deltaY, params.height * 1.0f / mViewHeight);

        if (!isLoading && isHasMore) {
            if (params.height == mViewHeight) {
                onLoadBefore();
            } else if (params.height < mViewHeight * 2) {
                onLoadStart();
            } else {
                onLoadReady();
            }
        }
    }

    public void loading() {
        if (!isLoading) {
            isLoading = true;
            onLoading();
            if (getHeight() != mViewHeight) {
                smoothScrollTo(mViewHeight, false);
            }
        }
    }

    public void loaded() {
        isLoading = false;
        onLoaded();
        smoothScrollTo(mViewHeight, true);
    }

    public void loadCancel() {
        if (!isLoading && isHasMore) {
            onLoadCancel();
            smoothScrollTo(mViewHeight, true);
        } else {
            smoothScrollTo(mViewHeight, false);
        }
    }

    private int getViewHeight() {
        return mView.getLayoutParams().height;
    }

    private void setViewHeight(int height) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mView.getLayoutParams();
        params.height = height;
        mView.setLayoutParams(params);
    }

    private void smoothScrollTo(int destHeight, final boolean isFinished) {
        if (getViewHeight() == destHeight) {
            if (isFinished) {
                onLoadFinished();
            }
            return;
        }
        ValueAnimator animator = ValueAnimator.ofInt(getViewHeight(), destHeight);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                setViewHeight(value);
                if (isFinished && value == mViewHeight) {
                    onLoadFinished();
                }
            }
        });
        animator.start();
    }

    public boolean isReadyLoad() {
        return getHeight() >= mViewHeight * 2;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isHigherThanOrigin() {
        return getHeight() > mViewHeight;
    }

    public void setHasMore(boolean isHasMore) {
        this.isHasMore = isHasMore;
        if (!isHasMore) {
            onLoadOver();
        }
    }

    public boolean isHasMore() {
        return isHasMore;
    }

    public abstract void onLoadOver();

}
