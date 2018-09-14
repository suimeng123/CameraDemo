package com.lx.camerademo.activity;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.lx.camerademo.R;
import com.lx.camerademo.util.BaseTakeCamera;
import com.lx.camerademo.util.CameraHelper;
import com.lx.camerademo.util.CameraManager;
import com.lx.camerademo.util.CommUtils;
import com.lx.camerademo.util.ITakeCamera;

import java.io.File;

import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

/**
 * com.lx.camerademo
 * CameraDemo
 * Created by lixiao2
 * 2018/9/11.
 */

public class CameraActivity extends AppCompatActivity {
    Button takePhotoBtn,takeVideoBtn;
    BaseTakeCamera takeCamera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 隐藏标题栏
        getSupportActionBar().hide();
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 隐藏导航栏
        CommUtils.hideNavigationBar(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        FrameLayout frameLayout = findViewById(R.id.fl);
        takePhotoBtn = findViewById(R.id.take_photo);
        takeVideoBtn = findViewById(R.id.take_video);
        SurfaceView surfaceView = new SurfaceView(this);
        surfaceView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.addView(surfaceView);

        takeCamera = new CameraManager(this,surfaceView);

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = CommUtils.getSDCardUrl() + File.separator + System.currentTimeMillis() + ".jpg";
                takeCamera.takePhoto(path);
            }
        });

        takeVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (takeCamera.isMediaRecorder) {
                    takeVideoBtn.setText("开始录像");
                    takeCamera.closeVideo();
                } else {
                    takeVideoBtn.setText("结束录像");
                    String path = CommUtils.getSDCardUrl() + File.separator + System.currentTimeMillis() + ".mp4";
                    takeCamera.startVideo(path);
                }
            }
        });

        findViewById(R.id.change_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (takeCamera.cameraType == Camera.CameraInfo.CAMERA_FACING_FRONT){
                    takeCamera.cameraType = Camera.CameraInfo.CAMERA_FACING_BACK;
                }else {
                    takeCamera.cameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }
                takeCamera.closeCarmera();
                takeCamera.openCarmera();
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        takeCamera.closeVideo();
        takeVideoBtn.setText("开始录像");
        takeCamera.closeCarmera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        takeCamera.openCarmera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        takeCamera.closeVideo();
        takeCamera.closeCarmera();
    }
}
