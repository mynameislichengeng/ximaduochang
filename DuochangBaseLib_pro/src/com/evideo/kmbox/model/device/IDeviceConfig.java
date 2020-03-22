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

import android.view.KeyEvent;

/**
 * [功能说明]
 */
public abstract class IDeviceConfig {
    public static String APP_NAME = "好乐多唱K歌";

    //测试服务器地址
    public String mTestDataCenterURI = "";
    //正式服务器地址
    public String mNormalDataCenterURI = "";

    public boolean mSupportCharge = true;

    public static final int PLAY_MODE_BUFFER_MEM_PLAY = 1;
    public static final int PLAY_MODE_BUFFER_FILE_PLAY = 2;
    public static final int PLAY_MODE_DOWN_COMPLETE_PLAY = 3;

    public int mPlayMode = PLAY_MODE_BUFFER_FILE_PLAY;

    protected boolean mSuportPlayMic = false;

    /**
     * [是否支持日志保存]
     */
    protected boolean mSupportSaveLog = true;

    /**
     * [是否是第三方应用]
     */
    protected boolean mIsThirdApp = false;
    /**
     * [是否支持点歌时显示动画]
     */
    protected boolean mOrderNeedAnimation = true;
    /**
     * [切歌时在线歌曲是否从本地删除]
     */
    protected boolean mIsSongFileDelWhenCutSong = false;

    protected boolean mSupportUserLogin = false;

    protected boolean mSetChannelByDB = true;

    public boolean mForbidPaySongPlay = true;

    protected int mBroadcastSongType = BROADCAST_SONG_TYPE_NONE;

    public static final int BROADCAST_SONG_TYPE_NONE = 0;
    public static final int BROADCAST_SONG_TYPE_LOCAL = 1;
    public static final int BROADCAST_SONG_TYPE_ONLINE = 2;

    public static final int KEYEVENT_SWITCH_MV = KeyEvent.KEYCODE_MENU;

    protected String mDeviceName = "unknow";


    public boolean isSupportPlayMic() {
        return mSuportPlayMic;
    }

    public boolean isSupportSaveLog() {
        return mSupportSaveLog;
    }

    public boolean isOrderNeedAnimation() {
        return this.mOrderNeedAnimation;
    }

    public int getBroadcastSongType() {
        return mBroadcastSongType;
    }

    public boolean isThirdApp() {
        return mIsThirdApp;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public abstract String getChannelName();

    public abstract int getAssignPlayerId();

    public abstract String getQQ();
}

