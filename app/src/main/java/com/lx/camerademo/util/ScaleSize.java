package com.lx.camerademo.util;

/**
 * com.lx.camerademo.util
 * CameraDemo
 * Created by lixiao2
 * 2018/9/12.
 */

public class ScaleSize {
    private int width;
    private int height;

    // 宽高比
    private int scaleWidth;
    private int scaleHeight;

    public ScaleSize(int width, int height) {
        this.width = width;
        this.height = height;
        int scale = CommUtils.getGCD(width, height);
        this.scaleWidth = width / scale;
        this.scaleHeight = height / scale;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getScaleWidth() {
        return scaleWidth;
    }

    public void setScaleWidth(int scaleWidth) {
        this.scaleWidth = scaleWidth;
    }

    public int getScaleHeight() {
        return scaleHeight;
    }

    public void setScaleHeight(int scaleHeight) {
        this.scaleHeight = scaleHeight;
    }

    @Override
    public String toString() {
        return "ScaleSize{" +
                "width=" + width +
                ", height=" + height +
                ", scaleWidth=" + scaleWidth +
                ", scaleHeight=" + scaleHeight +
                '}';
    }
}
