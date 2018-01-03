package com.merlin.view.recycler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * 每个Item左右下 有分隔线
 * Created by ncm on 2017/5/9.
 */

public class RecyclerStaggerDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int mHeight;
    private int mOrientation;
    private int mOffsetStart;
    private int mOffsetEnd;
    private int mColor;
    private Paint dividerPaint;

    public RecyclerStaggerDecoration(Context context, int orientation, int height, int offsetStart, int offsetEnd, int color) {
        this.mOrientation = orientation;
        this.mHeight = height;
        this.mOffsetStart = offsetStart;
        this.mOffsetEnd = offsetEnd;
        this.mColor = color;

        if (mColor != 0) {
            dividerPaint = new Paint();
            dividerPaint.setColor(mColor);
        } else {
            final TypedArray typed = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
            mDivider = typed.getDrawable(0);
            typed.recycle();
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getAdapter().getItemCount() == mOffsetStart + mOffsetEnd) {
            //初次刷新，若outRect.set(0, 0, 0, 0)会导致刷新头部不显示
            if (mOrientation == GridLayoutManager.HORIZONTAL) {
                outRect.set(0, 0, 1, 0);
            } else {
                outRect.set(0, 0, 0, 1);
            }
            return;
        }
        super.getItemOffsets(outRect, view, parent, state);
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        int position = parent.getChildLayoutPosition(view);
        if (!isHideDivider(parent, position, childCount)) {
            outRect.set(mHeight, 0, mHeight, mHeight);
//            position = position - mOffsetStart;
//            int itemCount = childCount - mOffsetStart - mOffsetEnd;
//            if (mOrientation == GridLayoutManager.HORIZONTAL) {
//                if (isLastRaw(parent, position, spanCount, itemCount)) {
//                    // 如果是最后一行，则不需要绘制底部
//                    if (isLastColumn(parent, position, spanCount, itemCount)) {
//                        outRect.set(0, 0, 0, 0);
//                    } else {
//                        outRect.set(0, mHeight, 0, 0);
//                    }
//                } else if (isLastColumn(parent, position, spanCount, itemCount)) {
//                    // 如果是最后一列，则不需要绘制右边
//                    outRect.set(0, 0, mHeight, 0);
//                } else {
//                    outRect.set(0, mHeight, mHeight, 0);
//                }
//            } else {
//                if (isLastRaw(parent, position, spanCount, itemCount)) {
//                    // 如果是最后一行，则不需要绘制底部
//                    if (isLastColumn(parent, position, spanCount, itemCount)) {
//                        outRect.set(0, 0, 0, 0);
//                    } else {
//                        outRect.set(0, 0, mHeight, 0);
//                    }
//                } else if (isLastColumn(parent, position, spanCount, itemCount)) {
//                    // 如果是最后一列，则不需要绘制右边
//                    outRect.set(0, 0, 0, mHeight);
//                } else {
//                    outRect.set(0, 0, mHeight, mHeight);
//                }
//            }
        } else {
            outRect.set(0, 0, 0, 0);
        }
    }

    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            if (isHideDivider(parent, parent.getChildLayoutPosition(child), parent.getAdapter().getItemCount())) {
                continue;
            }
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin + mHeight;
            final int top = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
            final int bottom = top + mHeight;

            if (mDivider == null) {
                c.drawRect(left, top, right, bottom, dividerPaint);
            } else {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            if (isHideDivider(parent, parent.getChildLayoutPosition(child), parent.getAdapter().getItemCount())) {
                continue;
            }
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            //Item右边
            int top = child.getTop() - params.topMargin;
            int bottom = child.getBottom() + params.bottomMargin;
            int left = child.getRight() + params.rightMargin + Math.round(ViewCompat.getTranslationX(child));
            int right = left + mHeight;
            if (mDivider == null) {
                c.drawRect(left, top, right, bottom, dividerPaint);
            } else {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }

            //Item左边
            top = top - mHeight;
            bottom = bottom + mHeight;
            left = child.getLeft() - params.leftMargin - mHeight;
            right = left + mHeight;
            if (mDivider == null) {
                c.drawRect(left, top, right, bottom, dividerPaint);
            } else {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) {// 如果是最后一列，则不需要绘制右边
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0) {// 如果是最后一列，则不需要绘制右边
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount) {// 如果是最后一列，则不需要绘制右边
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            childCount = childCount - (childCount % spanCount == 0 ? spanCount : childCount % spanCount);
            if (pos >= childCount) {// 如果是最后一行，则不需要绘制底部
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            }
            // StaggeredGridLayoutManager 且横向滚动
            else {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isHideDivider(RecyclerView parent, int pos, int childCount) {
        if (pos >= mOffsetStart && pos < childCount - mOffsetEnd) {
            return false;
        }
        return true;
    }
}
