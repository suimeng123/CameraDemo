package com.lx.camerademo.util;

import android.hardware.Camera;

/**
 * com.lx.camerademo.util
 * CameraDemo
 * Created by lixiao2
 * 2018/9/12.
 */

public class BaseTakeCamera implements ITakeCamera {
    /**相机类别 前置还是后置摄像头*/
    public int cameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;

    /**是否在录制中*/
    public boolean isMediaRecorder = false;

    @Override
    public void openCarmera() {

    }

    @Override
    public void closeCarmera() {

    }

    @Override
    public void openPreview() {

    }

    @Override
    public void closePreview() {

    }

    @Override
    public void takePhoto(String outputPath) {

    }

    @Override
    public void startVideo(String outputPath) {

    }

    @Override
    public void closeVideo() {

    }
}
