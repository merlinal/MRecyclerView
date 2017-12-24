package com.merlin.view.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2017/12/21.
 */

public class ProgressView extends View {

    public ProgressView(Context context) {
        super(context);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private IProgress iProgress;
    private float mCenterX, mCenterY;

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mCenterX = w / 2;
        mCenterY = h / 2;
        onLayoutView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mCenterX = MeasureSpec.getSize(widthMeasureSpec) / 2;
        mCenterY = MeasureSpec.getSize(heightMeasureSpec) / 2;
        onLayoutView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (iProgress != null) {
            iProgress.onDraw(canvas);
        }
    }

    public void onPulling(float rate) {
        if (iProgress != null) {
            iProgress.onPulling(rate);
        }
    }

    public void onLoading() {
        if (iProgress != null) {
            iProgress.onLoading();
        }
    }

    public void onLoaded() {
        if (iProgress != null) {
            iProgress.onLoaded();
        }
    }

    public void setProgress(IProgress iProgress) {
        this.iProgress = iProgress;
        onLayoutView();
    }

    private void onLayoutView() {
        if (this.iProgress != null) {
            if (mCenterX != 0 || mCenterY != 0) {
                iProgress.onLayout(this, mCenterX, mCenterY);
            }
        }
    }

}
