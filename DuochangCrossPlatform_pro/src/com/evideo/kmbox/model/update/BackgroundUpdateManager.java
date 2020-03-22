/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年11月17日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.update;

import com.evideo.kmbox.model.update.UpdateTimer.IUpdateTimeOutListener;
import com.evideo.kmbox.util.EvLog;



/**
 * [功能说明]
 */
public class BackgroundUpdateManager{
    private UpdateTimer mTimer = null;
    private static boolean instanceFlag = false; // true if 1 instance
    private static BackgroundUpdateManager instance = null;
    
    
    public static BackgroundUpdateManager getInstance() {
        if (!instanceFlag) {
            instanceFlag = true;
            instance = new BackgroundUpdateManager();
            
            return instance;
        }
        return instance;
    }
    
    @Override
    public void finalize() {
        instanceFlag = false;
        instance = null;
    }
    
    private BackgroundUpdateManager() {
        mTimer = new UpdateTimer();
    }
    

    /**
     * [功能说明]
     * @param listener
     * @param updateMinuteInterval 调用listener间隔，以分钟为单位
     */
    public boolean addUpdateTask(IUpdateTimeOutListener listener,int updateMinuteInterval) {
        if (updateMinuteInterval <= 0) {
            EvLog.d("updateMinuteInterval invalid");
            return false;
        }
        mTimer.registerTask(false,listener,updateMinuteInterval);
        return true;
    }
    
    /**
     * [功能说明]
     * @param updateImmediately 是否马上执行
     * @param listener 监听器
     * @param updateMinuteInterval 更新间隔，以分钟为单位
     */
    public void addUpdateTask(boolean execFristTime,IUpdateTimeOutListener listener,int updateMinuteInterval) {
        if (listener != null && updateMinuteInterval > 0) {
            mTimer.registerTask(execFristTime,listener, updateMinuteInterval);
            /*if (execFristTime) {
                mTimer.traverse();
            }*/
        } else {
            EvLog.e("addUpdateTask failed");
        }
    }
    
    public void removeUpdateTask(IUpdateTimeOutListener listener) {
        mTimer.unregisterTask(listener);
    }
    
    public void start(boolean immediately) {
        mTimer.startTimer(immediately);
    }
    
    public void stop() {
        mTimer.stopTimer();
    }
  
//    public void exedcuteOneTime(IBackgroundUpdateTimeOutListener listener) {
//        mTimer.exedcuteOneTime(listener);
//    }
}
 