/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年10月27日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.update.resource;

import android.text.TextUtils;

import com.evideo.kmbox.model.chargeproxy.ChargeProxy;
import com.evideo.kmbox.model.datacenter.UrlList;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.model.update.BackgroundUpdateManager;
import com.evideo.kmbox.model.update.UpdateTimer.IUpdateTimeOutListener;
import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明]
 */
public class UpdateDCCommu {

    public static void init() {
        if (TextUtils.isEmpty(UrlList.pay_notify_url)&&DeviceConfigManager.getInstance().isSupportCharge()) {
            getChargeNotifyUrl();
        } else {
            EvLog.i(DeviceConfigManager.getInstance().getDeviceName() + " do not need getChargeNotifyUrl");
        }
    }
    
    private static void getChargeNotifyUrl() {
        final IUpdateTimeOutListener getUrllistener = new IUpdateTimeOutListener() {
            @Override
            public void timeOut() {
                EvLog.e("getChargeNotifyUrl timeOut");
                try {
                    String url = ChargeProxy.getInstance().getPayNotifyUrl();
                    if (TextUtils.isEmpty(url)) {
                        url = UrlList.pay_notify_url;
                    }
                    
                    if (!TextUtils.isEmpty(url)) {
                        EvLog.i("remove getChargeNotifyUrl listener");
                        this.needRemove = true;
                        return;
                    }
                } catch (Exception e) {
                    EvLog.e(e.getMessage());
                    UmengAgentUtil.reportError("[DC-ERROR]requestResourceHeadUrl failed:" + e.getMessage());
                }
                return;
            }
        };
        BackgroundUpdateManager.getInstance().addUpdateTask(true,getUrllistener,1);
    } 
}
