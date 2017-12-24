package com.merlin.view.recycler.load;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.merlin.view.progress.ProgressBallRotate;
import com.merlin.view.progress.ProgressView;
import com.merlin.view.recycler.R;

/**
 * Created by Administrator on 2017/12/18.
 */

public class ProgressHeader extends AbstractHeaderView {

    public ProgressHeader(@NonNull Context context) {
        super(context);
    }

    public ProgressHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private TextView mHintText;
    private ProgressView mProgressView;

    @Override
    public View initLoadView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.m_progress_header, this, false);
        mHintText = (TextView) view.findViewById(R.id.m_progress_header_text);
        mProgressView = (ProgressView) view.findViewById(R.id.m_progress_header_progressView);
        mProgressView.setProgress(new ProgressBallRotate(2, 12, 24, 1000, 0xff00cd00, 0xffcd0000));
//        mProgressView.setProgress(new ProgressBallRoll(5, 12, 0xff00cd00, 0xffcd0000, 0xff0000cd));
        return view;
    }

    @Override
    public void onLoadBefore() {

    }

    @Override
    public void onLoadStart() {
        mHintText.setText(R.string.m_refresh_hint_normal);
    }

    @Override
    public void onLoadPulling(float offsetY, float deltaY, float rate) {
        mProgressView.onPulling(rate);
    }


    @Override
    public void onLoadReady() {
        mHintText.setText(R.string.m_refresh_hint_ready);
    }

    @Override
    public void onLoading() {
        mHintText.setText(R.string.m_refresh_hint_refreshing);
        mProgressView.onLoading();
    }

    @Override
    public void onLoaded() {
        mHintText.setText(R.string.m_refresh_hint_refreshed);
        mProgressView.onLoaded();
    }

    @Override
    public void onLoadCancel() {
        mHintText.setText(R.string.m_refresh_hint_normal);
    }

    @Override
    public void onLoadFinished() {
        mHintText.setText(R.string.m_refresh_hint_normal);
    }
}
