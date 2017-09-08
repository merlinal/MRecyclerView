package com.merlin.view.recycler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * This class is from the v7 samples of the Android SDK. It's not by me!
 * <p/>
 * See the license above for details.
 */
public class RecyclerListDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int mHeight;
    private final int mOrientation;
    private int mOffsetStart;
    private int mOffsetEnd;
    private int mColor;
    private Paint dividerPaint;

//    private static final int[] ATTRS = new int[]{
//            android.R.attr.listDivider
//    };

//    public RecyclerListDecoration(Context context) {
//        this(context, LinearLayoutManager.VERTICAL, Util.dp2px(1), 0, 0, 0);
//    }
//
//    public RecyclerListDecoration(Context context, int orientation) {
//        this(context, orientation, Util.dp2px(1), 0, 0, 0);
//    }
//
//    public RecyclerListDecoration(Context context, int orientation, int dividerWidth) {
//        this(context, orientation, dividerWidth, 0, 0, 0);
//    }

    public RecyclerListDecoration(Context context, int orientation, int height, int offsetStart, int offsetEnd, int color) {
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
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, 0);
        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            if (isHideDivider(parent, parent.getChildLayoutPosition(view), parent.getAdapter().getItemCount())) {
                outRect.set(0, 0, 0, 0);
            } else {
                outRect.set(0, 0, mHeight, 0);
            }
        } else if (mOrientation == LinearLayoutManager.VERTICAL) {
            if (isHideDivider(parent, parent.getChildLayoutPosition(view), parent.getAdapter().getItemCount())) {
                outRect.set(0, 0, 0, 0);
            } else {
                outRect.set(0, 0, 0, mHeight);
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
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
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin + Math.round(ViewCompat.getTranslationX(child));
            final int right = left + mHeight;

            if (mDivider == null) {
                c.drawRect(left, top, right, bottom, dividerPaint);
            } else {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    private boolean isHideDivider(RecyclerView parent, int pos, int childCount) {
        if (pos >= mOffsetStart && pos < childCount - mOffsetEnd - 1) {
            return false;
        }
        return true;
    }

}
