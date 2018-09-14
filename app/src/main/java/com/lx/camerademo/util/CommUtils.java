package com.lx.camerademo.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Environment;
import android.view.View;

import com.lx.camerademo.MainActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

/**
 * Created by lixiao2 on 2018/4/17.
 */

public class CommUtils {
    /**
     * 获取sd卡路径
     * @return
     */
    public static File getSDCardUrl(){
        //插入sd卡
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File sdCardDir = Environment.getExternalStorageDirectory();
            return sdCardDir;
        }
        throw new NullPointerException("sd卡路径不存在");
    }

    /**
     * 判断文件或者文件夹是否存在 不存在就创建(在sd卡根目录下面创建)
     * @param fileStr
     */
    public static File createFile(String fileStr) {
        File file = new File(getSDCardUrl(),fileStr);
        if(fileStr.indexOf(".") == -1) {
            if (!file.exists()) {
                file.mkdirs();
            }
        }else {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * 得到两个数的最大公约数
     * @return
     */
    public static int getGCD(int x, int y){
        if (x < 0 || y < 0) {
            return 0;
        }
        int a = Math.max(x,y);
        int b = Math.min(x,y);
        while(b != 0) {
            int c = b;
            b = a % b;
            a = c;
        }
        return a;
    }

    /**
     * 判断手机是否是横屏
     * @return
     */
    public static boolean isLand(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        // 获取屏幕方向
        int ori = configuration.orientation;
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 隐导航栏
     * @param activity
     */
    public static void hideNavigationBar(Activity activity){
        View decorView = activity.getWindow().getDecorView();
        int option = SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(option);
    }
}
