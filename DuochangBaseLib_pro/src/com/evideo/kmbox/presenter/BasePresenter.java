package com.evideo.kmbox.presenter;

import com.evideo.kmbox.BaseApplication;

import android.os.Handler;
import android.os.Looper;

/**
 * @brief : [异步任务的基类]
 */
public class BasePresenter {
    
    /**
     * @brief : [在UI线程中执行]
     * @param runnable
     */
    public static void runInUI(Handler handler,Runnable runnable) {
        if(runnable == null || handler == null){
            return;
        }
        if(Thread.currentThread() == Looper.getMainLooper().getThread()){
            runnable.run();
        }else{
            handler.post(runnable);
        }
    }
    
    public static void runInUI(Runnable runnable) {
        if(runnable == null){
            return;
        }
        if(Thread.currentThread() == Looper.getMainLooper().getThread()){
            runnable.run();
        }else{
            BaseApplication.getHandler().post(runnable);
        }
    }
    
    public static void runInUIDelay(Handler handler, Runnable runnable,long delayMillis) {
        if(runnable == null || handler == null){
            return;
        }
        if(Thread.currentThread() == Looper.getMainLooper().getThread()){
            runnable.run();
        }else{
            handler.postDelayed(runnable, delayMillis);
        }
    }
    
    
    /**
     * @brief : [检查当前线程是否是UI线程，不是则抛出异常]
     */
    protected static void check(){
        if(Thread.currentThread() != Looper.getMainLooper().getThread()){
            new RuntimeException("Must be running on the UI thread");
        }
    }
    
}
