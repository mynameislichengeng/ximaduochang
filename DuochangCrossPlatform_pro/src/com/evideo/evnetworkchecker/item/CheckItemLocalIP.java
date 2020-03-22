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
import com.evideo.kmbox.util.NetUtils;
import com.evideo.kmbox.widget.mainview.about.NetWorkCheckManager;


/**
 * [功能说明] 本地IP地址检测
 */
public class CheckItemLocalIP implements ICheckItem {
    private boolean mResult = false;
    private String mIP = null;


    @Override
    public void run() {
        // 网络设备连接异常的情况
        if (!NetUtils.isNetworkConnected(BaseApplication.getInstance().getBaseContext())) {
            mResult = false;
            return;
        }

        mIP = NetWorkCheckManager.getInstance().getIP();
        
        // 获取IP信息后检验其有效性
        if (TextUtils.isEmpty(mIP)) {
            mResult = false;
        } else {
            mResult = NetUtils.isValidIpAddress(mIP);
        }
    }

    @Override
    public boolean getResult() {
        return mResult;
    }

    @Override
    public String getResultMessage() {
        return mIP.toString();
    }

    @Override
    public int getErrorType() {
        return 0;
    }

    @Override
    public String getResolution() {
        if (mResult) {
            return null;
        }
        return BaseApplication.getInstance().getString(R.string.network_check_error_hint_step2);
    }
}
