package com.evideo.kmbox.util;

import android.os.Handler;
import android.os.Looper;

/**
 * @brief : [按指定周期循环执行任务]
 * 执行任务的代码写在回调中,IActionCallback的repeat方法里
 */
public class RepeatTimerTask implements Runnable {
    private Handler mHandler;
    private IActionCallback mActionCallback;
    private long cycle = 4000;    //循环周期
    private int times = -1;
    private boolean isStop;
    private boolean mUseThread = false;
    
    public RepeatTimerTask() {
        init(Looper.getMainLooper());
    }
    
    public RepeatTimerTask(IActionCallback callbackAction) {
        super();
        mActionCallback = callbackAction;
        init(Looper.getMainLooper());
    }
    
    public RepeatTimerTask(IActionCallback callbackAction,Looper looper) {
        super();
        mActionCallback = callbackAction;
        if (looper != Looper.getMainLooper()) {
            mUseThread = true;
        }
        init(looper);
    }
    public boolean isStop() {
        return isStop;
    }
    private void init(Looper looper) {
        mHandler = new Handler(looper);
    }
    
    public void setActionCallback(IActionCallback actionCallback) {
        mActionCallback = actionCallback;
    }
    
    /**
     * [功能说明] 定次循环 
     *         >=1
     *         ==0 立即结束
     *         ==-1 无限循环
     * @param times
     */
    public void setTimes(int times) {
        if (times <= -1) {
            times = -1;
        }
        this.times = times;
    }
    
    public int getTimes() {
        return this.times;
    }
    
    /**
     * 设置循环周期
     * @param cycle
     */
    public void setCycle(long cycle) {
        this.cycle = cycle;
    }
    
    public void scheduleAtFixedRate(long cycleTime){
        mHandler.removeCallbacks(this);
        isStop = false;
        this.cycle = cycleTime;
        if(mActionCallback != null){
            mActionCallback.start();
        }
        mHandler.postDelayed(this, cycleTime);
    }
    
    /**
     * 重复任务开始执行，周期请先在setCycle(long cycle)方法设置
     */
    public void scheduleAtFixedRate(long delayTime,long cycleTime){
        mHandler.removeCallbacks(this);
        isStop = false;
        this.cycle = cycleTime;
        if(mActionCallback != null){
            mActionCallback.start();
        }
        mHandler.postDelayed(this, delayTime);
    }
    
    /**
     * 停止重复任务的执行，并释放资源
     */
    public void stop(){
        isStop = true;
        times = -1;
        mHandler.removeCallbacks(this);
        if (mUseThread) {
            mHandler.getLooper().quit();
        }
        if(mActionCallback != null){
            mActionCallback.stop();
        }
    }
    
    /**
     * 重复任务的回调接口
     * @author zhouxinghua
     *
     */
    public interface IActionCallback{        
        public void start();
        /**
         * 重复执行的代码写在这里
         */
        public void repeat();
        public void stop();
    }

    @Override
    public void run() {
        if(isStop){
            return;
        }
        
        if(mActionCallback != null){
            mActionCallback.repeat();
        }
        
        if (times > -1) {
            times--;
            if (times <= 0) {
                isStop = true;
                times = -1;
            }
        }
        
        if(!isStop){
            mHandler.postDelayed(this, cycle);
        }
    }
}
