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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.os.HandlerThread;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.RepeatTimerTask;
import com.evideo.kmbox.util.RepeatTimerTask.IActionCallback;

/**
 * [功能说明]
 */
public class UpdateTimer implements IActionCallback{
    //20 second
    private static final int TIMER_CYCLE_INTERVAL = 30;
    private List<UpdateTaskInfo> mList = null;
    private RepeatTimerTask mRepeatTimer = null;
    private HandlerThread mHandlerThread = null;
    public static abstract class IUpdateTimeOutListener {
        /** [是否移除监听器] */
        public boolean needRemove = false;
        public abstract void timeOut();
    }
    
    private class UpdateTaskInfo{
        public boolean execFristTime;
        public int countDown;
        public int updateInterval;
        public IUpdateTimeOutListener updateListener;
        public UpdateTaskInfo(boolean execFristTime,IUpdateTimeOutListener listener,int updateInterval) {
            this.updateInterval = updateInterval;
            this.updateListener = listener;
            //timer是20循环一次
            this.countDown = (updateInterval*60)/TIMER_CYCLE_INTERVAL;
            this.execFristTime = execFristTime;
//            EvLog.i("countDown = " + this.countDown);
        }
    }
    
    public UpdateTimer() {
        mList = new ArrayList<UpdateTaskInfo>();
    }
    
    public void startTimer(boolean immediately) {
        if (mRepeatTimer == null) {
            mHandlerThread = new HandlerThread("updateThread");
            mHandlerThread.setPriority(Thread.MIN_PRIORITY);//lowest
            mHandlerThread.start();
            mRepeatTimer = new RepeatTimerTask(this,mHandlerThread.getLooper());
        }
        if (immediately) {
            mRepeatTimer.scheduleAtFixedRate(0,TIMER_CYCLE_INTERVAL*1000);
        } else {
            mRepeatTimer.scheduleAtFixedRate(TIMER_CYCLE_INTERVAL*1000);
        }
        EvLog.e( "UpdateTimer start");
    }
    
    //FIXME
    public void traverse() {
        if (mRepeatTimer != null) {
            mRepeatTimer.scheduleAtFixedRate(0,TIMER_CYCLE_INTERVAL*1000);
        }
    }
    
    public void stopTimer() {
        if (mRepeatTimer != null) {
            mRepeatTimer.stop();
            mRepeatTimer = null;
        }
        if (mHandlerThread != null) {
            EvLog.i("updateTimer stop timer");
            if (mHandlerThread.getLooper() != null) {
                mHandlerThread.getLooper().quit();
            }
            mHandlerThread = null;
        }
        EvLog.e( "UpdateTimer stop");
    }

    
    public void registerTask(boolean execImmediately,IUpdateTimeOutListener listener,int updateInterval) {
        synchronized (mList) {
            UpdateTaskInfo info = new UpdateTaskInfo(execImmediately,listener,updateInterval);
            mList.add(info);
        }
    }
    
    public void registerTask(UpdateTaskInfo info) {
        synchronized (mList) {
            mList.add(info);
        }
    }

    public void unregisterTask(IUpdateTimeOutListener listener) {
        synchronized (mList) {
            Iterator<UpdateTaskInfo> it = mList.iterator(); 
            while (it.hasNext()) {
                UpdateTaskInfo info = it.next();
                if (info.updateListener == listener) {
                    it.remove();
                    break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void repeat() {
        synchronized (mList) {
            Iterator<UpdateTaskInfo> it = mList.iterator(); 
            while (it.hasNext()) {
                UpdateTaskInfo info = it.next();
                if (info == null || info.updateListener == null) {
                    continue;
                }
                if (info.execFristTime) {
                    info.updateListener.timeOut();
                    info.execFristTime = false;
                    continue;
                }
                info.countDown--;
                if (info.countDown == 0) {
                    info.countDown = info.updateInterval;
                    info.updateListener.timeOut();
                    if (info.updateListener.needRemove) {
                        it.remove();
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        EvLog.d("updateTimer real stop");
    }
}

