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

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.util.NetUtils;


/**
 * [功能说明] 网络设备检测
 */
public class CheckItemCable implements ICheckItem {

    private boolean mResult = false;

    @Override
    public void run() {
        mResult = NetUtils.isNetworkConnected(BaseApplication.getInstance().getBaseContext());
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
        return BaseApplication.getInstance().getResources().getString(R.string.network_check_error_hint_step1);
    }
}
