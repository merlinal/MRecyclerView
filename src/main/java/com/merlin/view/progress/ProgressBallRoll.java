package com.merlin.view.progress;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/22.
 */

public class ProgressBallRoll implements IProgress {

    private View mView;
    private int[] mColor;
    private float mCenterX, mCenterY;
    private ArrayList<Ball> mBallList;
    private ArrayList<Paint> mPaintList;
    private float mRadius;
    private AnimatorSet mLoadingAnim;
    private long mDuration = 1000;

    private float[][] mLoadingRadius;

    public ProgressBallRoll(int num, float radius, int... colors) {
        mColor = colors;
        mRadius = radius;
        mBallList = new ArrayList<>();
        mPaintList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            mBallList.add(new Ball(i, mColor[i % mColor.length], 1));
            mPaintList.add(new Paint());
        }
        mLoadingRadius = new float[][]{
                {mRadius * 0.5f, mRadius, mRadius * 1.2f, mRadius, mRadius * 0.5f},
                {mRadius, mRadius * 1.2f, mRadius, mRadius * 0.5f, mRadius},
                {mRadius * 1.2f, mRadius, mRadius * 0.5f, mRadius, mRadius * 1.2f},
                {mRadius, mRadius * 1.2f, mRadius, mRadius * 0.5f, mRadius},
                {mRadius * 0.5f, mRadius, mRadius * 1.2f, mRadius, mRadius * 0.5f}
        };
    }

    @Override
    public void onLayout(View view, float centerX, float centerY) {
        mView = view;
        mCenterX = centerX;
        mCenterY = centerY;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mView != null && mBallList != null) {
            for (int i = 0; i < mPaintList.size(); i++) {
                Ball ball = mBallList.get(i);
                mPaintList.get(i).setColor(ball.getColor());
                canvas.drawCircle(ball.getCenterX(), mCenterY, ball.getRadius(), mPaintList.get(i));
            }
        }
    }

    @Override
    public void onPulling(float rate) {
        if (mView == null || mView.getVisibility() != View.VISIBLE) {
            return;
        }
        float radius = mRadius * rate;
        if (radius < 1) {
            radius = 1;
        }
        if (radius > mRadius) {
            radius = mRadius;
        }
        setXY(radius, radius);
        mView.invalidate();
    }

    @Override
    public void onLoading() {
        if (mLoadingAnim == null) {
            setLoadingAnimTwo();
        }
        if (!mLoadingAnim.isRunning()) {
            mLoadingAnim.start();
        }
    }

    @Override
    public void onLoaded() {
        if (mView == null) {
            return;
        }
        if (mLoadingAnim != null && mLoadingAnim.isRunning()) {
            mLoadingAnim.end();
        }
        if (mView.getVisibility() != View.VISIBLE) {
            return;
        }
        setXY(mRadius, mRadius);
        mView.invalidate();
    }

    private void setXY(float radius, float divider) {
        if (mCenterX > 0) {
            int middleIndex = mBallList.size() / 2;
            if (mBallList.size() % 2 == 0) {
                for (int i = 0; i < mBallList.size(); i++) {
                    Ball ball = mBallList.get(i);
                    ball.setCenterX(mCenterX - ((middleIndex - i) * 2 - 1) * (divider / 2 + radius));
                    ball.setRadius(radius);
                }
            } else {
                for (int i = 0; i < mBallList.size(); i++) {
                    Ball ball = mBallList.get(i);
                    ball.setCenterX(mCenterX - (middleIndex - i) * (2 * radius + divider));
                    ball.setRadius(radius);
                }
            }
        }
    }

    private void setLoadingAnimTwo() {
        ArrayList<Animator> animList = new ArrayList<>(mBallList.size());
        for (int i = 0; i < mBallList.size(); i++) {
            ObjectAnimator animRadius = ObjectAnimator.ofFloat(mBallList.get(i), "radius",
                    mLoadingRadius[i % mLoadingRadius.length]);
            animRadius.setRepeatCount(ValueAnimator.INFINITE);
            if (i == 0) {
                animRadius.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mView.invalidate();
                    }
                });
            }
            animList.add(animRadius);
        }

        //属性动画集合
        mLoadingAnim = new AnimatorSet();
        //四个属性动画一块执行
        mLoadingAnim.playTogether(animList);
        //动画一次运行时间
        mLoadingAnim.setDuration(mDuration);
        //时间插值器，这里表示动画开始最快，结尾最慢
        mLoadingAnim.setInterpolator(new LinearInterpolator());
    }

}
