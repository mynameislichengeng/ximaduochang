package com.evideo.kmbox.util;

import com.evideo.kmbox.BaseApplication;

import android.os.Looper;

public class ThreadUtil {

    /**
     * 检测当前线程是否是主线程
     */
    public static void check(){
        if(Thread.currentThread() != Looper.getMainLooper().getThread()){
            new RuntimeException("Must be running on the UI thread!!!");
        }
    }
    
    /**
     * 运行在主线程
     */
    public static void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            BaseApplication.getHandler().post(action);
        } else {
            action.run();
        }
    }
    
    
}
