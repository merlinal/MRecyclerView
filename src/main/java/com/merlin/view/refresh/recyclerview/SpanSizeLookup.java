package com.merlin.view.refresh.recyclerview;

import android.support.v7.widget.GridLayoutManager;

/**
 * use this class to let the footerview have full width
 */
public class SpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    private BaseRecyclerAdapter adapter;
    private int mSpanSize = 1;

    public SpanSizeLookup(BaseRecyclerAdapter adapter, int spanSize) {
        this.adapter = adapter;
        this.mSpanSize = spanSize;
    }

    @Override
    public int getSpanSize(int position) {
        boolean isHeaderOrFooter = adapter.isFooter(position) || adapter.isHeader(position);
        return isHeaderOrFooter ? mSpanSize : 1;
    }
}