package com.lx.camerademo.util;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * com.lx.camerademo
 * CameraDemo
 * Created by lixiao2
 * 2018/9/11.
 */

public class CameraManager extends BaseTakeCamera {
    private static final String TAG = "CameraManager";
    private Context mContext;
    private SurfaceView mSurfaceView;

    /**
     * 摄像头Id
     */
    private int cameraId;
    /**
     * 相机参数
     */
    private Camera.Parameters mParameters;

    Camera mCamera;

    OrientationEventListener mOrientationListener;

    /**视频录制器*/
    private MediaRecorder mediaRecorder;

    public CameraManager(Context context, SurfaceView surfaceView) {
        mContext = context;
        mSurfaceView = surfaceView;
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                openCarmera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                closePreview();
                releaseMediaRecorder();
                closeCarmera();
            }
        });
    }

    /**
     * 获取相应摄像头
     */
    public boolean getCameraId() {
        // 获取摄像头个数
        int len = Camera.getNumberOfCameras();
        for (int i = 0; i < len; i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            // 获取每个摄像头信息
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == cameraType) {
                // 前置摄像头
                cameraId = i;
                return true;
            }
        }
        return false;
    }

    @Override
    public void openCarmera() {
        getCameraId();
        mCamera = Camera.open(cameraId);
        openPreview();
    }

    @Override
    public void closeCarmera() {
        if (mOrientationListener != null && !mOrientationListener.canDetectOrientation()) {
            mOrientationListener.disable();
        }
        mOrientationListener = null;
        if (mediaRecorder != null && isMediaRecorder) {
            releaseMediaRecorder();
        }
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            mParameters = null;
        }
    }

    @Override
    public void openPreview() {
        if (!getCameraId()) {
            return;
        }
        mSurfaceView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mParameters = mCamera.getParameters();
                    /**设置闪光灯为自动闪光模式*/
                    if (Camera.Parameters.FLASH_MODE_OFF.equals(mParameters.getFlashMode())) {
//                        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    }
                    if (cameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        /** 如果是前置摄像头 关闭闪光灯*/
                        mParameters.setFlashMode(Camera.Parameters.ANTIBANDING_OFF);
                    } else {
                        /**设置对焦模式为连续对焦模式 前置摄像头在三星和华为等很多机型上开启对焦模式就会报错*/
                        mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    }
                    mCamera.setDisplayOrientation(getDisplayRotation(getCameraInfo().orientation));
                    onOrientationChanged();

                    /** 设置预览图片尺寸*/
                    ScaleSize previewSize = getSize(mParameters.getSupportedPreviewSizes(), mSurfaceView);
                    mParameters.setPreviewSize(previewSize.getWidth(),previewSize.getHeight());

                    mCamera.setParameters(mParameters);
                    mCamera.setPreviewDisplay(mSurfaceView.getHolder());
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /** 获取尺寸*/
    private ScaleSize getSize(List<Camera.Size> sizeList,SurfaceView surfaceView) {
        // 得到和显示控件尺寸比例相同的SIZE数组
        List<ScaleSize> mListSize = getSizeFromView(sizeList, surfaceView.getWidth(),surfaceView.getHeight());
        if (mListSize.isEmpty()) {
            Camera.Size ms = sizeList.get(sizeList.size() - 1);
            return new ScaleSize(ms.width,ms.height);
        }
        int viewWidth = Math.max(surfaceView.getWidth(),surfaceView.getHeight());
        int viewHeight = Math.min(surfaceView.getWidth(),surfaceView.getHeight());
        for (ScaleSize size : mListSize) {
            if (size.getWidth() >= viewWidth && size.getHeight() >= viewHeight) {
                return size;
            }
        }
        return mListSize.get(mListSize.size() - 1);
    }

    /**得到和显示控件尺寸比例一样的Size数组*/
    private List<ScaleSize> getSizeFromView(List<Camera.Size> sizeList,int viewWidth,int viewHeight) {
        List<ScaleSize> mSizeList = new ArrayList<>();
        ScaleSize viewScaleSize = new ScaleSize(viewWidth, viewHeight);
        ScaleSize scaleSize;
        for (Camera.Size size : sizeList) {
            scaleSize = new ScaleSize(size.width,size.height);
            // 相机默认是横向的 所以所有相机支持的尺寸都是宽大于高的（width>height），所以只需匹配是这个比例就行 宽和高互换也可以
            if ((scaleSize.getScaleWidth() == viewScaleSize.getScaleWidth() && scaleSize.getScaleHeight() == viewScaleSize.getScaleHeight()) ||
                    (scaleSize.getScaleWidth() == viewScaleSize.getScaleHeight() && scaleSize.getScaleHeight() == viewScaleSize.getScaleWidth())) {
                mSizeList.add(scaleSize);
            }
        }
        return mSizeList;
    }



    /**得到预览需要旋转的角度*/
    private int getDisplayRotation(int orientationp) {
        final int rotaion = getDisplayRotation();
        int degrees = 0;
        int displayRotation = 0;
        switch (rotaion) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                break;
        }
        if (getCameraInfo().facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayRotation = (orientationp + degrees) % 360;
            displayRotation = (360 - displayRotation) % 360;
        } else {
            displayRotation = (orientationp - degrees + 360) % 360;
        }
        return displayRotation;
    }

    private Camera.CameraInfo getCameraInfo() {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        return info;
    }

    // 获取生成的图片或者视频需旋转角度
    private int getPicRotation(int orientation) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        orientation = (orientation + 45) / 90 * 90;
        int rotation = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation - orientation + 360) % 360;
        } else {  // back-facing camera
            rotation = (info.orientation + orientation) % 360;
        }
        return rotation;
    }

    // 获取设备旋转的角度
    private int getDisplayRotation() {
        return ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
    }

    int mo;
    /** 监听手机旋转*/
    private void onOrientationChanged() {
        mo = -1;
        mOrientationListener = new OrientationEventListener(mContext, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return;
                }
                if (orientation != mo) {
                    int rotation = getPicRotation(orientation);
                    if (mParameters != null) {
                        mParameters.setRotation(rotation);
                    }
                    mo = orientation;
                }
            }
        };
        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        } else {
            mOrientationListener.disable();
        }
    }

    @Override
    public void closePreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    @Override
    public void takePhoto(final String outputPath) {
        if (mCamera == null) {
            return;
        }
        /** 设置图片尺寸*/
        ScaleSize pictureSize = getSize(mParameters.getSupportedPictureSizes(), mSurfaceView);
        mParameters.setPictureSize(pictureSize.getWidth(),pictureSize.getHeight());
        mParameters.setRotation(getPicRotation(getDisplayRotation()));
        mCamera.setParameters(mParameters);
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    File outputFile = new File(outputPath);
                    if (!outputFile.exists()) {
                        outputFile.createNewFile();
                    }
                    FileOutputStream outputStream = new FileOutputStream(outputFile);
                    outputStream.write(data);
                    outputStream.close();
                    openPreview();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    /** 准备视频录制器*/
    private boolean prepareMediaRecorder(String outputPath) {
        if (mCamera == null) {
            return false;
        }

        mediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            // 获取CAMERA视频资源
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            //输出格式
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            // 这两项需要放在setOutputFormat之后
            //音频编码器
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //音频编码率
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            //视频帧率
            mediaRecorder.setVideoFrameRate(30);
            //视频比特率越大图片越清晰
            mediaRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);
            //音频编码率
            mediaRecorder.setAudioEncodingBitRate(MediaRecorder.AudioEncoder.AAC);
            //音频声道
//            mediaRecorder.setAudioChannels(MediaRecorder.AudioEncoder.camcorderProfile.audioChannels);
            //音频采样率
//            mediaRecorder.setAudioSamplingRate(camcorderProfile.audioSampleRate);

            // 获取相机支持的最适合尺寸
            ScaleSize scaleSize = getSize(mParameters.getSupportedPreviewSizes(), mSurfaceView);
            //设置要捕获的视频的宽度和高度
            mediaRecorder.setVideoSize(scaleSize.getWidth(),scaleSize.getHeight());
            //设置记录会话的最大持续时间（毫秒）
            mediaRecorder.setMaxDuration(60 * 1000);

            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                }
            });
            mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    // 监听录制时长
                }
            });
//            mediaRecorder.setPreviewDisplay( mSurfaceView.getHolder().getSurface());
            mediaRecorder.setOrientationHint(getPicRotation(getDisplayRotation()));

            File file = new File(outputPath);
            if(!file.exists()) {
                file.createNewFile();
            }

            //输出路径
            mediaRecorder.setOutputFile(outputPath);
            //准备
            mediaRecorder.prepare();

            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        releaseMediaRecorder();
        return false;
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null && isMediaRecorder) {
            mediaRecorder.setOnErrorListener(null);
            mediaRecorder.setOnInfoListener(null);
            mediaRecorder.setPreviewDisplay(null);
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            isMediaRecorder = false;
            mediaRecorder = null;
        }
    }

    @Override
    public void startVideo(final String ouputPath) {
        mSurfaceView.post(new Runnable() {
            @Override
            public void run() {
                if (prepareMediaRecorder(ouputPath)){
                    mediaRecorder.start();
                    isMediaRecorder = true;
                }
            }
        });
    }

    @Override
    public void closeVideo() {
        releaseMediaRecorder();
    }
}
