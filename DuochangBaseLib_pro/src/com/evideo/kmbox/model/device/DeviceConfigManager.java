/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年7月26日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.device;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.util.ApkInfoUtil;
import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明] 设备
 */
public class DeviceConfigManager {
    private static DeviceConfigManager instance = null;

    public static DeviceConfigManager getInstance() {
        if (instance == null) {
            synchronized (DeviceConfigManager.class) {
                DeviceConfigManager temp = instance;
                if (temp == null) {
                    temp = new DeviceConfigManager();
                    instance = temp;
                }
            }
        }
        return instance;
    }

    public IDeviceConfig getDevice() {
        return mDeviceConfig;
    }

    public String getChannelName() {
        return (mDeviceConfig != null) ? (mDeviceConfig.getChannelName()) : ("");
    }

    private IDeviceConfig mDeviceConfig = null;
    private String mChipId = "";
    private String mCode = "";  // 校验播链，防止相同sn设备同时请求播链
    private long mRemainVipTime = 0;
    private String mPicurhead = ""; //获取头像地址前缀
    private boolean isFree = false; //是否是免费歌曲

    public void init(Context context, IDeviceConfig config, String mac) {
        mDeviceConfig = config;
        mChipId = mac;
        EvLog.i("init device:" + mDeviceConfig.getDeviceName() + ",chipid:" + mChipId);
    }

    public boolean forbitPaySongPlay() {
        return (mDeviceConfig != null) ? (mDeviceConfig.mForbidPaySongPlay) : (false);
    }

    public int getAssignPlayerId() {
        return (mDeviceConfig != null) ? (mDeviceConfig.getAssignPlayerId()) : (-1);
    }

    public boolean isNeedDelFile() {
        return (mDeviceConfig != null) ? (mDeviceConfig.mIsSongFileDelWhenCutSong) : (true);
    }

    public boolean isUseChannelConfigByDB() {
        return (mDeviceConfig != null) ? (mDeviceConfig.mSetChannelByDB) : (false);
    }

    public int getPlayMode() {
        return (mDeviceConfig != null) ? (mDeviceConfig.mPlayMode) : (IDeviceConfig.PLAY_MODE_BUFFER_MEM_PLAY);
    }

    public void setPlayMode(int mode) {
        if (mDeviceConfig != null) {
            mDeviceConfig.mPlayMode = mode;
        }
    }

    public boolean isSupportPlayMic() {
        return (mDeviceConfig != null) ? (mDeviceConfig.isSupportPlayMic()) : (false);
    }

    public boolean isSupportCharge() {
        return (mDeviceConfig != null) ? (mDeviceConfig.mSupportCharge) : (false);
    }

    public String getTestDataCenterUrl() {
        return (mDeviceConfig != null) ? (mDeviceConfig.mTestDataCenterURI) : ("");
    }

    public String getNormalDataCenterUrl() {
        return (mDeviceConfig != null) ? (mDeviceConfig.mNormalDataCenterURI) : ("");
    }

    public boolean isSupportUserLogin() {
        return (mDeviceConfig != null) ? (mDeviceConfig.mSupportUserLogin) : (false);
    }

    public boolean isSupportSaveLog() {
        return (mDeviceConfig != null) ? (mDeviceConfig.isSupportSaveLog()) : (false);
    }

    public boolean isThirdApp() {
        return (mDeviceConfig != null) ? (mDeviceConfig.isThirdApp()) : (false);
    }

    public int getBroadcastSongType() {
        return (mDeviceConfig != null) ? (mDeviceConfig.getBroadcastSongType()) : (IDeviceConfig.BROADCAST_SONG_TYPE_NONE);
    }

    public String getDeviceName() {
        return (mDeviceConfig != null) ? (mDeviceConfig.getDeviceName()) : ("");
    }

    public boolean isOrderNeedAnimation() {
        return (mDeviceConfig != null) ? (mDeviceConfig.isOrderNeedAnimation()) : (false);
    }

    public String getChipId() {
        return mChipId;
    }

    private String mUserAgent = "";

    public String getUserAgent() {
        if (TextUtils.isEmpty(mUserAgent)) {
            mUserAgent = getDeviceName()
                    + "/"
                    + ApkInfoUtil.getVerName(BaseApplication.getInstance());
            EvLog.d("userAgent:" + mUserAgent);
        }
        return mUserAgent;
    }

    public long getRemainVipTime() {
        return mRemainVipTime;
    }

    public void setRemainVipTime(long mRemainVipTime) {
        this.mRemainVipTime = mRemainVipTime;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String mCode) {
        this.mCode = mCode;
    }
    public String getPicurhead() {
        return mPicurhead == null ? "" : mPicurhead;
    }

    public void setPicurhead(String picurhead) {
        mPicurhead = picurhead;
    }
    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

}
