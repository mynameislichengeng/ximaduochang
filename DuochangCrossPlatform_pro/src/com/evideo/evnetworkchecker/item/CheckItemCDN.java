/*
 *  Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 *  All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *    Date           Author      Version     Description
 *  ----------------------------------------------------
 *  unknown        "unknown"       1.0       [修订说明]
 *  2016-8-1     "qiuyunliang"     1.1       [修订说明]
 */

package com.evideo.evnetworkchecker.item;

import java.net.SocketTimeoutException;

import android.text.TextUtils;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.widget.mainview.about.NetWorkCheckManager;
import com.evideo.kmbox.widget.mainview.about.NetWorkCheckManager.TestConfig;

/**
 * [功能说明] 云曲库检测
 */
public class CheckItemCDN implements ICheckItem {
    private int mRet = 0;
    private boolean mResult = false;
    private int mErrorType = 0;

    @Override
    public void run() {
        try {
            String url = DCDomain.getInstance().requestCDNTestUrl();
            if (TextUtils.isEmpty(url)) {
                mRet = TestConfig.ERROR_DATACENTER_ERROR_PROTOCOL;
            } else {
                mRet =  NetWorkCheckManager.getInstance().startCheckCloudSongUrl(
                        /*TestConfig.QINIU_SONGBOOK_URL*/url, ResourceSaverPathManager.getInstance().getCloudListSavePath());
            }
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                mRet = TestConfig.ERROR_TIMEOUT;
            }
        }
        mErrorType = mRet;
        mResult = (mRet == 0);
    }

    @Override
    public boolean getResult() {
        return mResult;
    }

    @Override
    public String getResultMessage() {
        return null;
    }

    @Override
    public String getResolution() {
        if (mResult) {
            return null;
        }
        if (mErrorType == TestConfig.ERROR_TIMEOUT) {
            return BaseApplication.getInstance().getString(R.string.network_check_error_hint_step7_timeout);
        } else if (mErrorType == TestConfig.ERROR_RAINBOW_ERROR_ADDR) {
            return BaseApplication.getInstance().getString(R.string.network_check_error_hint_step7_error_url);
        } else if (mErrorType == TestConfig.ERROR_HTTPCODE_ILLEAGL) {
            return BaseApplication.getInstance().getString(R.string.network_check_error_hint_step7_errorresponse);
        } else if(mErrorType == TestConfig.ERROR_DOWNLOAD ){
            return BaseApplication.getInstance().getString(R.string.network_check_error_hint_step7_download_error);
        }
        return null;
    }

    @Override
    public int getErrorType() {
        return mErrorType;
    }
}
