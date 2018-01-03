package com.merlin.view.progress;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/12/21.
 */

public class ProgressBallRotate implements IProgress {

    private View mView;
    private int[] mColor;
    private float mCenterX, mCenterY;
    private ArrayList<Ball> mBallList;
    private ArrayList<Paint> mPaintList;
    private float mRadius;
    private AnimatorSet mLoadingAnim;
    private float mDistance;
    private long mDuration = 1000;
    private HashMap<String, float[][]> mLoadingAnimMap;

    public ProgressBallRotate(int num, float radius, float distance, long duration, int... colors) {
        mColor = colors;
        mRadius = radius;
        mDistance = distance;
        mDuration = duration;
        mBallList = new ArrayList<>();
        mPaintList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            mBallList.add(new Ball(i, mColor[i % mColor.length], 1));
            mPaintList.add(new Paint());
        }
        mLoadingAnimMap = new HashMap<>();
        /* 2个小球的情况
        小球1半径：中间大小->最大->中间大小->最小->中间大小
        小球1中心：左中右中左
        小球2中间大小->最小->中间大小->最大->中间大小
        小球2中心：右中左中右
        */
        mLoadingAnimMap.put("2radius", new float[][]{
                {mRadius, mRadius * 1.3f, mRadius, mRadius * 0.5f, mRadius},
                {mRadius, mRadius * 0.5f, mRadius, mRadius * 1.3f, mRadius}
        });
        mLoadingAnimMap.put("2distance", new float[][]{
                {-1, 0, 1, 0, -1},
                {1, 0, -1, 0, 1}
        });
        /* 3个小球的情况 TODO
        */
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
        setXY(radius, radius * 1.5f);
        mView.invalidate();
    }

    @Override
    public void onLoading() {
        if (mLoadingAnim == null) {
            setLoadingAnim();
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
        setXY(mRadius, mRadius * 1.5f);
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

    private void setLoadingAnim() {
        float[][] loadingRadius = getLoadingRadius();
        float[][] loadingDistance = getLoadingDistance();
        if (loadingRadius == null || loadingDistance == null) {
            return;
        }
        ArrayList<Animator> animList = new ArrayList<>();
        for (int i = 0; i < mBallList.size(); i++) {
            final Ball ball = mBallList.get(i);

            //半径变化
            ObjectAnimator animRadius = ObjectAnimator.ofFloat(ball, "radius",
                    loadingRadius[i % loadingRadius.length]);
            animRadius.setRepeatCount(ValueAnimator.INFINITE);
            if (i == 0) {
                animRadius.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        //不停的刷新view，让view不停的重绘
                        if (mView != null) {
                            mView.invalidate();
                        }
                    }
                });
            }
            animList.add(animRadius);
            //中心点变化
            ValueAnimator animDistance = ValueAnimator.ofFloat(loadingDistance[i % loadingDistance.length]);
            animDistance.setRepeatCount(ValueAnimator.INFINITE);
            animDistance.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (Float) animation.getAnimatedValue();
                    ball.setCenterX(mCenterX + (mDistance) * value);
                }
            });

            animList.add(animDistance);
        }
        //属性动画集合
        mLoadingAnim = new AnimatorSet();
        //四个属性动画一块执行
        mLoadingAnim.playTogether(animList);
        //动画一次运行时间
        mLoadingAnim.setDuration(mDuration);
        //时间插值器，这里表示动画开始最快，结尾最慢
        mLoadingAnim.setInterpolator(new DecelerateInterpolator());
    }

    private float[][] getLoadingRadius() {
        return mLoadingAnimMap.get(mBallList.size() + "radius");
    }

    private float[][] getLoadingDistance() {
        return mLoadingAnimMap.get(mBallList.size() + "distance");
    }

}
