package com.merlin.view.recycler.load;

import android.view.View;

/**
 * Created by Administrator on 2017/12/16.
 */

public interface ILoader {

    View initLoadView();

    void onLoadBefore();

    void onLoadStart();

    void onLoadPulling(float offsetY, float deltaY, float rate);

    void onLoadReady();

    void onLoading();

    void onLoaded();

    void onLoadCancel();

    void onLoadFinished();

}
