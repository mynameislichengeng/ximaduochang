/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年3月14日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.charge;


import android.content.Context;
import android.util.Log;

/**
 * [功能说明]
 */
public abstract class IBaseCharge {
    public static final int ERROR_GET_PAY_NOTIFY_URL_FAILED = -10000;
    public static final long VALID_TIME_WARN_MIN_MINUTE = 3 * 60/*3*24*60*/;

    public interface IChargeInitListener {
        void onChargeInitSuccess();

        void onChargeInitFailed(int errorCode);
    }

    public interface IChargeAuthResultListener {
        void onChargeAuthSuccess();

        void onChargeAuthFailed(int errorCode);
    }

    public interface IChargePayResultListener {
        void onChargePaySuccess();

        void onChargePayFailed(int errorCode, String errorMessage);

        void onChargeCancel();
    }

    protected String mTradeNo = "";
    protected boolean mInitSuccess = false;

    /**
     * [有效期]
     */
    public long mValidTime = 0;
    /**
     * [订购产品id]
     */
    public int mProductId = 0;

    public long getValidTime() {
        Log.i("gsp", "有限期为getValidTime: "+mValidTime);
        return mValidTime;
    }

    public int getProductId() {
        return mProductId;
    }

    public boolean isInitSuccess() {
        Log.i("gsp", "isInitSuccess: 是否返回成功"+mInitSuccess);
        return mInitSuccess;
    }

    public boolean mIsAuthed = false;

    public boolean isAuthed() {
        return mIsAuthed;
    }

    public void setTradeNo(String no) {
        mTradeNo = no;
    }

    public String getTradeNo() {
        return mTradeNo;
    }

    public abstract void sdkPaySuccess(long time, int productId, String serialNo);

    public abstract void sdkPayFailed(int errorCode, String errorMessag);

    public abstract void init(final Context context, final IChargeInitListener listener);

    public abstract void pay(final Context context, String appSerialNum, final ChargeProductInfo info, final IChargePayResultListener listener);

    public abstract void uninit();

    public abstract void sendAuthRequest(final Context context, final IChargeAuthResultListener listener);

    //    public abstract boolean requestUrlInfo();
    public abstract String getPayNotifyUrl();

    public abstract void cancelCheckPayResult();
}
