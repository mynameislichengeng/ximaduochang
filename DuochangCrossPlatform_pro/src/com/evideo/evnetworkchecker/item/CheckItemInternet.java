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

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.widget.mainview.about.NetWorkCheckManager;
import com.evideo.kmbox.widget.mainview.about.NetWorkCheckManager.TestConfig;

/**
 * [功能说明] Internet检测
 */
public class CheckItemInternet implements ICheckItem {

    private boolean mResult;
    private long mResponse = 0L;
    private int mErrorType = 0;

    @Override
    public void run() {
        try {
            // 检测测试连接是否可以Ping通
            mResponse = NetWorkCheckManager.getInstance().checkInternetAvaliable();
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                mErrorType = TestConfig.ERROR_TIMEOUT;
            }
        }
        mResult = mResponse > 0;
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
    public int getErrorType() {
        return mErrorType;
    }

    @Override
    public String getResolution() {
        if (mResult) {
            return null;
        }
        return BaseApplication.getInstance().getString(R.string.network_check_error_hint_step5);
    }
}
