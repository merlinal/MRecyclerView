package com.merlin.view.load;

import android.graphics.Canvas;
import android.view.View;

/**
 * Created by Administrator on 2017/12/21.
 */

public interface IProgress {

    void onLayout(View view, float centerX, float centerY);

    void onDraw(Canvas canvas);

    void onPulling(float rate);

    void onLoading();

    void onLoaded();

}
