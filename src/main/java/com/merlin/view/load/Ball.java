package com.merlin.view.load;

import android.support.annotation.Keep;

/**
 * Created by Administrator on 2017/12/21.
 */

public class Ball {

    public Ball() {
    }

    public Ball(int id, int color, int radius) {
        this.id = id;
        this.color = color;
        this.radius = radius;
    }

    private int id;
    @Keep
    private float radius;
    private float centerX;
    private int color;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Keep
    public float getRadius() {
        return radius;
    }

    @Keep
    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
