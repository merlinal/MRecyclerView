package com.merlin.view.mrecycler;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * @author zal
 * @date 2017/12/16.
 */

public abstract class AbstractHeaderView extends FrameLayout implements ILoader {

    public AbstractHeaderView(@NonNull Context context) {
        this(context, null);
    }

    public AbstractHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected View mLoadView;
    private int mViewHeight;
    private boolean isLoading = false;

    private void init() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        setLayoutParams(params);
        setPadding(0, 0, 0, 0);

        mLoadView = initLoadView();
        mLoadView.measure(0, 0);
        mViewHeight = mLoadView.getMeasuredHeight();
        addView(mLoadView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 0));
    }

    public void startLoad() {
        if (!isLoading) {
            onLoadStart();
        }
    }

    public void onPulling(float deltaY) {
        if (deltaY == 0) {
            return;
        }
        deltaY = deltaY / 2;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mLoadView.getLayoutParams();
        params.height += deltaY;
        if (params.height < 0) {
            params.height = isLoading ? 1 : 0;
        }
        mLoadView.setLayoutParams(params);

        onLoadPulling(params.height, deltaY, params.height * 1.0f / mViewHeight);

        if (!isLoading) {
            if (params.height == 0) {
                onLoadBefore();
            } else if (params.height < mViewHeight * 1.2) {
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
            smoothScrollTo(mViewHeight, false);
        }
    }

    public void loaded() {
        isLoading = false;
        onLoaded();
        smoothScrollTo(0, true);
    }

    public void loadCancel() {
        if (!isLoading) {
            onLoadCancel();
            smoothScrollTo(0, true);
        } else {
            smoothScrollTo(mViewHeight, false);
        }
    }

    private int getViewHeight() {
        return mLoadView.getLayoutParams().height;
    }

    private void setViewHeight(int height) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mLoadView.getLayoutParams();
        params.height = height;
        mLoadView.setLayoutParams(params);
    }

    private void smoothScrollTo(int destHeight, final boolean isFinished) {
        ValueAnimator animator = ValueAnimator.ofInt(getViewHeight(), destHeight);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                onPulling(value);
                setViewHeight(value);
                if (isFinished && value == 0) {
                    onLoadFinished();
                }
            }
        });
        animator.start();
    }

    public boolean isLoadReady() {
        return getHeight() >= mViewHeight * 1.2;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isHigherThanOrigin() {
        return getHeight() > 0;
    }

}
