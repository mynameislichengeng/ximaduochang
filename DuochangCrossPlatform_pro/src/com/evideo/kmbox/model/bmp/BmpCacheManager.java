/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年8月31日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.bmp;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.datacenter.UrlList;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.NetUtils;
import com.evideo.kmbox.util.QrGenerator;


/**
 * [功能说明]
 */
public class BmpCacheManager {
    private static BmpCacheManager instance = null;
    public static BmpCacheManager getInstance() {
        if(instance == null) {
            synchronized (BmpCacheManager.class) {
                BmpCacheManager temp = instance;
                if(temp == null) {
                  temp = new BmpCacheManager();
                  instance = temp;
                }
            }
         }
         return instance;
    }
    
    private Bitmap mLackSongQrBmp = null;
    
    public void clear() {
        if (mLackSongQrBmp != null && !mLackSongQrBmp.isRecycled()) {
            mLackSongQrBmp.recycle();
            mLackSongQrBmp = null;
        }
    }
    
    public Bitmap getLackSongQrBmp(int width) {
        if (mLackSongQrBmp != null) {
            return mLackSongQrBmp;
        }
        StringBuffer url = new StringBuffer(UrlList.sn_song_feedback_lack);
        if (TextUtils.isEmpty(url)) {
            EvLog.e("get no lack song bmp");
            return null;
        }
        url.append("?&version-info=").append(DeviceConfigManager.getInstance().getUserAgent());
        url.append("?&MAC=").append(NetUtils.getMacAddr());
        url.append("?&sn=").append(DeviceConfigManager.getInstance().getChipId());
        
        EvLog.i("lack song url:" + url);
        try {
            mLackSongQrBmp = QrGenerator.createQRImage(url.toString(), width, width,true);
        } catch (Exception e) {
            mLackSongQrBmp = null;
        }
        return mLackSongQrBmp;
    }
}
