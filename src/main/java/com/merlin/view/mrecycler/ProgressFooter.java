package com.merlin.view.mrecycler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.merlin.view.recycler.R;

/**
 * Created by Administrator on 2017/12/18.
 */

public class ProgressFooter extends AbstractFooterView {


    public ProgressFooter(@NonNull Context context) {
        super(context);
    }

    public ProgressFooter(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private ProgressBar mProgressBar;
    private TextView mHintText;

    @Override
    public View initLoadView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.m_progress_footer, this, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.m_progress_header_bar);
        mHintText = (TextView) view.findViewById(R.id.m_progress_header_text);
        return view;
    }


    @Override
    public void onLoading() {
        mHintText.setText(R.string.m_load_hint_refreshing);
    }

    @Override
    public void onLoadCancel() {
        mHintText.setText(R.string.m_load_hint_normal);
    }

    @Override
    public void onLoaded() {
        mHintText.setText(R.string.m_load_hint_refreshed);
    }

    @Override
    public void onLoadBefore() {
        mHintText.setText(R.string.m_load_hint_normal);
    }

    @Override
    public void onLoadStart() {
        mHintText.setText(R.string.m_load_hint_normal);
    }

    @Override
    public void onLoadPulling(float offsetY, float deltaY, float rate) {
    }

    @Override
    public void onLoadReady() {
        mHintText.setText(R.string.m_load_hint_ready);
    }

    @Override
    public void onLoadFinished() {
        mHintText.setText(R.string.m_load_hint_normal);
    }

    @Override
    public void onLoadOver() {
        mHintText.setText("没有更多了");
    }

}
