package com.lx.camerademo.util;

/**
 * com.lx.camerademo
 * CameraDemo
 * Created by lixiao2
 * 2018/9/11.
 */

public interface ITakeCamera {

    /**
     * 开启相机
     */
    void openCarmera();

    /**
     * 关闭相机
     */
    void closeCarmera();

    /**
     * 开启预览
     */
    void openPreview();

    /**
     * 关闭预览
     */
    void closePreview();

    /**
     * 拍照
     */
    void takePhoto(String outputPath);

    /**
     * 开启视频录制
     */
    void startVideo(String outputPath);

    /**
     * 结束视频录制
     */
    void closeVideo();
}
