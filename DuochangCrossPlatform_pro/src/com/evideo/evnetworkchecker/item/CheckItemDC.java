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

import android.text.TextUtils;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.exception.DataCenterCommuException;
import com.evideo.kmbox.widget.mainview.about.NetWorkCheckManager;
import com.evideo.kmbox.widget.mainview.about.NetWorkCheckManager.TestConfig;

import java.net.SocketTimeoutException;

/**
 * [功能说明] 多唱数据中心检测
 */
public class CheckItemDC implements ICheckItem {

//    private long mResponseTime = 0;
    private boolean mResult = false;
    private int mErrorType = 0;
    private String mResultMessage = null;

    @Override
    public void run() {
        try {
            String url = NetWorkCheckManager.getInstance().startCheckDataCenter();
            mResult = !TextUtils.isEmpty(url);
        } catch (Exception e) {
            if (e instanceof DataCenterCommuException) {
                mResultMessage = e.getMessage();
                mErrorType = TestConfig.ERROR_DATACENTER_ERROR_PROTOCOL;
            } else if (e instanceof SocketTimeoutException) {
                mErrorType = TestConfig.ERROR_TIMEOUT;
            }
        }
    }

    @Override
    public boolean getResult() {
        return mResult;
    }

    @Override
    public String getResultMessage() {
        return mResultMessage;
    }

    @Override
    public int getErrorType() {
        return mErrorType;
    }

    @Override
    public String getResolution() {
        if (mResult) {
            return null;
        }
        if (mErrorType == TestConfig.ERROR_TIMEOUT) {
            return BaseApplication.getInstance().getString(R.string.network_check_error_hint_step6_noresponse);
        } else if (mErrorType == TestConfig.ERROR_DATACENTER_ERROR_PROTOCOL) {
            return BaseApplication.getInstance().getString(R.string.network_check_error_hint_step6_wrong_errorcode, mResultMessage);
        }
        return null;
    }
}
