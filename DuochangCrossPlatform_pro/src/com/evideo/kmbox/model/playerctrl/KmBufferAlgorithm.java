/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-5-6     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.playerctrl;

import com.evideo.kmbox.SystemConfigManager;
import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明]
 */
public class KmBufferAlgorithm {

    private static float mAverageSpeed = 0.0f;
    private static int mDuration = 0;
    private static long mTotalSize = 0;
    private static long mDownSize = 0;
    /** [已播放数据大小] */
    private static long mPlayedSize = 0;
    /** [已播放数据长度] */
    private static long mPlayedTime = 0;
//    private static final int KB_SIZE = 1024;
    
    private static long mTick = 0;

    private static float mMinSpeed = 0.0f;
    private static float mMaxSpeed = 0.0f;
    private static float mCalibrateSpeed = 0.0f;
    private static float mMaxFluctuationSpeed = 0.0f;
    private static int mPercent = 0;
    
    public static void reset() {
        mDuration = 0;
        mTotalSize = 0;
        mDownSize = 0;
        mPlayedSize = 0;
        mPlayedTime = 0;
        mPercent = 0;
        
        resetSpeed();
    }
    
    public static void resetSpeed() {
        EvLog.i(" --- KmBufferAlgorithm resetSpeed");
        mAverageSpeed = 0.0f;
        mTick = 0;
        mMinSpeed = 0.0f;
        mMaxSpeed = 0.0f;
        mMaxFluctuationSpeed = 0.0f;
        mCalibrateSpeed = 0.0f;
    }
    
    public static float getMaxSpeed() {
    	return mMaxSpeed;
    }
    
    public static float getMinSpeed() {
    	return mMinSpeed;
    }
    
    public static float getMaxFluctuationSpeed() {
    	return mMaxFluctuationSpeed;
    }
    
    public static float getAverageSpeed() {
    	return mAverageSpeed;
    }
    
    public static void setDuration(int duration) {
        mDuration = duration;
    }
    
    public static void setTotalSize(long totalSize) {
        mTotalSize = totalSize;
    }
    
    public static void updatePlayedSize(long playedSize) {
        mPlayedSize = playedSize;
    }
    
    public static void updatePlayedTime(long playedTime) {
        mPlayedTime = playedTime;
    }
    
    public static void updateDownSize(long downed) {
        mDownSize = downed;
    }
    
    public static long getDownSize() {
        return mDownSize;
    }
    


    public static void updateSpeed(float speed) {
        if (mMinSpeed > speed) {
        	mMinSpeed = speed;
        }

        if (mMaxSpeed < speed) {
        	mMaxSpeed = speed;
        }

        mAverageSpeed = (mTick * mAverageSpeed + speed) / (mTick + 1);
        float fluctuation = speed - mAverageSpeed;

        if (fluctuation > mMaxFluctuationSpeed) {
        	mMaxFluctuationSpeed = fluctuation;
        }

        float coefficient = 1.0f;
        //f 越小，波动越少
        float f = Math.abs(fluctuation) / mAverageSpeed;
        /*if ( f > 0.0f && f <= 0.1f) {
            coefficient = 0.9f;
        } else if (f > 0.1f && f <= 0.5f) {
            coefficient = 0.8f;
        } else if (f > 0.5f && f <= 0.9f) {
            coefficient = 0.7f;
        } else {
            coefficient = 0.6f;
        }*/

        mCalibrateSpeed = mAverageSpeed * coefficient * SystemConfigManager.NET_SPEED_FADER;
        
        /*EvLog.d(mTick + " = speed:" + speed + ",averagespeed:" + mAverageSpeed + ",calibrateSpeed:" + mCalibrateSpeed
        		+ ",fluatuation:" + fluctuation + "maxFluctuationSpeed:" + mMaxFluctuationSpeed
        		+ ",minSpeed:" + mMinSpeed + ",maxSpeed:" + mMaxSpeed);*/
        
        mTick++;
    }
    
    public static boolean isCanPlay() {
    	if (mTotalSize == mDownSize) {
    		return true;
    	}

    	float speed = mCalibrateSpeed;

        long needSize = (mTotalSize - mPlayedSize) - (long) (speed * (mDuration - mPlayedTime));
//        EvLog.d("need:" + needSize + ",downed:" + (mDownSize - mPlayedSize) + ",speed:" + speed + ",mDuration:" + mDuration + ",mTotalSize=" + mTotalSize);
        if (needSize <= (mDownSize - mPlayedSize)) {
            return true;
        } else {
            mPercent = (int) ((mDownSize - mPlayedSize)*100/needSize);
        }
        
        return false;
    }
    
    
    public static int getPercent() {
        return mPercent;
    }
}
