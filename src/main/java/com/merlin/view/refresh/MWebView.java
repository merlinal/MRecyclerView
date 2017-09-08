package com.merlin.view.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by 2144 on 2017/4/25.
 */

public class MWebView extends WebView {

    public MWebView(Context context) {
        super(context);
    }

    public MWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isBottom() {
        return computeVerticalScrollRange() == getHeight() + getScrollY();
    }

    @Override
    public int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }
}
