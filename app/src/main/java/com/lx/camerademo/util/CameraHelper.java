package com.lx.camerademo.util;

import android.content.Context;
import android.view.SurfaceView;

/**
 * com.lx.camerademo
 * CameraDemo
 * Created by lixiao2
 * 2018/9/11.
 */

public class CameraHelper {

    ITakeCamera takeCamera;

    public CameraHelper(Context context, ITakeCamera takeCamera) {
        this.takeCamera = takeCamera;
    }
}
