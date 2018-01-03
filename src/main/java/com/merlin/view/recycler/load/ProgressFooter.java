package com.merlin.view.recycler.load;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merlin.view.progress.ProgressBallRoll;
import com.merlin.view.progress.ProgressView;
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

    private ProgressView mProgressView;
    private TextView mHintText;
    private View mCenterView;

    @Override
    public View initLoadView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.m_progress_footer, this, false);
        mCenterView = view.findViewById(R.id.m_progress_footer_center);
        mProgressView = (ProgressView) view.findViewById(R.id.m_progress_footer_progressView);
        mHintText = (TextView) view.findViewById(R.id.m_progress_footer_text);
        mProgressView.setProgress(new ProgressBallRoll(3, 12, 0xff00cd00, 0xffcd0000, 0xff0000cd));
        return view;
    }


    @Override
    public void onLoading() {
        mHintText.setText(R.string.m_load_hint_refreshing);
        mProgressView.onLoading();
    }

    @Override
    public void onLoadCancel() {
        mHintText.setText(R.string.m_load_hint_normal);
        mProgressView.onLoaded();
    }

    @Override
    public void onLoaded() {
        mHintText.setText(R.string.m_load_hint_refreshed);
        mProgressView.onLoaded();
    }

    @Override
    public void onLoadBefore() {
        mHintText.setText(R.string.m_load_hint_normal);
    }

    @Override
    public void onLoadStart() {
        mHintText.setText(R.string.m_load_hint_normal);
        mProgressView.onLoaded();
    }

    @Override
    public void onLoadPulling(float offsetY, float deltaY, float rate) {
        mProgressView.onPulling(rate);
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
    public void onHasMoreChanged(boolean isHasMore) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mHintText.getLayoutParams();
        if (params == null) {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        if (isHasMore) {
            mCenterView.setVisibility(View.VISIBLE);
            mProgressView.setVisibility(View.VISIBLE);
            mHintText.setText(R.string.m_load_hint_normal);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
        } else {
            mCenterView.setVisibility(View.GONE);
            mProgressView.setVisibility(View.GONE);
            mHintText.setText(R.string.m_load_hint_over);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
        }
        mHintText.setLayoutParams(params);
    }

}
