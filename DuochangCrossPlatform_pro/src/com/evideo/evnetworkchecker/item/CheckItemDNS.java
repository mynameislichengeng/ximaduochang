/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *    Date           Author       Version       Description
 *  ------------------------------------------------------
 *  unknown        "unknown"        1.0         [修订说明]
 */

package com.evideo.evnetworkchecker.item;

import android.text.TextUtils;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.widget.mainview.about.NetWorkCheckManager;


/**
 * [功能说明] 网关地址检测
 */
public class CheckItemDNS implements ICheckItem {

    private boolean mResult = false;
    private String mDNS = null;

    @Override
    public void run() {
        mDNS = NetWorkCheckManager.getInstance().getDNS();
        if (TextUtils.isEmpty(mDNS)) {
            mResult = false;
        }
        mResult = NetWorkCheckManager.getInstance().checkIPAvailable(mDNS)
                && NetWorkCheckManager.getInstance().checkIPAvailable("duochang.cc");
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
        return 0;
    }

    @Override
    public String getResolution() {
        if (mResult) {
            return null;
        }
        return BaseApplication.getInstance().getString(R.string.network_check_error_hint_step4);
    }
}