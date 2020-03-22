/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年4月7日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.device;

import android.os.Build;
import android.text.TextUtils;

import com.evideo.kmbox.model.player.KmVideoPlayerType;
import com.evideo.kmbox.util.EvLog;


/**
 * [功能说明] 项目配置
 */
public class DeviceConfig extends IDeviceConfig{
    public DeviceConfig() {
        super.mDeviceName = DeviceName.DC_KEIGE_DEVICE_NAME;
//        super.mTestDataCenterURI = "http://sy-api-test.duochang.cc/";
//        super.mNormalDataCenterURI = "http://sy-api-test.duochang.cc/";

        super.mTestDataCenterURI = "http://sxyd.duochang.shengyintic.com/";
        super.mNormalDataCenterURI = "http://sxyd.duochang.shengyintic.com/";
        super.mSupportCharge = true;
        super.mPlayMode = IDeviceConfig.PLAY_MODE_BUFFER_FILE_PLAY;
        super.mIsThirdApp = true;
        super.mIsSongFileDelWhenCutSong = true;
        super.mBroadcastSongType = IDeviceConfig.BROADCAST_SONG_TYPE_ONLINE;
        super.mSupportUserLogin = true;
        super.mOrderNeedAnimation = true;
        super.mSetChannelByDB = true;
        super.mSuportPlayMic = false;
        super.mForbidPaySongPlay = true;
    }

    @Override
    public int getAssignPlayerId() {
        String device = Build.DEVICE;
        int mediaId = KmVideoPlayerType.VLC;
        
        if (TextUtils.isEmpty(device)) {
            return mediaId;
        }
        
        if (Build.VERSION.SDK_INT < 19) {
            mediaId = KmVideoPlayerType.MEDIAPLAYER;
        }
        EvLog.i("getAssignPlayerId device:" +device + ",mediaId:" + mediaId);
        return mediaId;
    }

    @Override
    public String getChannelName() {
        return "盛音天承";
    }

    @Override
    public String getQQ() {
        return "";
    }
}
