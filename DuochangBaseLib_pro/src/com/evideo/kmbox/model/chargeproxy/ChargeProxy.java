package com.evideo.kmbox.model.chargeproxy;
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


import android.content.Context;
import android.util.Log;

import com.evideo.kmbox.model.charge.ChargeProductInfo;
import com.evideo.kmbox.model.charge.IBaseCharge;
import com.evideo.kmbox.model.charge.IBaseCharge.IChargeAuthResultListener;
import com.evideo.kmbox.model.charge.IBaseCharge.IChargeInitListener;
import com.evideo.kmbox.model.charge.IBaseCharge.IChargePayResultListener;
import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明]
 */
public class ChargeProxy implements IChargeInitListener, IChargeAuthResultListener, IChargePayResultListener {
    private static ChargeProxy instance = null;

    public static ChargeProxy getInstance() {
        if (instance == null) {
            synchronized (ChargeProxy.class) {
                ChargeProxy temp = instance;
                if (temp == null) {
                    temp = new ChargeProxy();
                    instance = temp;
                }
            }
        }
        return instance;
    }


    private IBaseCharge mCharge = null;

    private ChargeProxy() {
    }

    public void setChargeDevice(IBaseCharge charge) {
        mCharge = charge;
    }

    public String getPayNotifyUrl() {
        if (mCharge == null) {
            return "";
        }
        return mCharge.getPayNotifyUrl();
    }

    public boolean isAuthed() {
        if (mCharge != null) {
            return mCharge.isAuthed();
        }
        return false;
    }

    public void setIsAuthed(boolean isAuth) {
        if (mCharge != null) {
            mCharge.mIsAuthed = isAuth;
        }
    }

    public boolean isInitSuccess() {
        if (mCharge != null) {
            return mCharge.isInitSuccess();

        }
        return false;
    }

    //    private Context mContext = null;
    private IChargeInitListener mInitListener = null;

    public void setInitListener(IChargeInitListener listener) {
        mInitListener = listener;
    }

    public void init(Context context) {
//        mContext = context;
        mCharge.init(context, this);
    }

    public void uninit() {
        if (mCharge != null) {
            mCharge.uninit();
        }
    }


    private IChargeAuthResultListener mAuthResultListener = null;

    public void setAuthResultListener(IChargeAuthResultListener listener) {
        mAuthResultListener = listener;
    }

    public void checkAuth(Context context) {
        if (mCharge != null) {
            mCharge.sendAuthRequest(context, this);
        }
    }

    public long getValidTime() {
        if (mCharge != null) {
            return mCharge.getValidTime();
        }
        return 0;
    }

    public void setValidTime(long time) {
        if (mCharge != null) {
            EvLog.d("setValidTime mCharge != null");
            mCharge.mValidTime = time;
        } else {
            EvLog.d("setValidTime mCharge == null");
        }
    }

    public int getProductId() {
        if (mCharge != null) {
            return mCharge.getProductId();
        }
        return 0;
    }

    public String getTradeNo() {
        if (mCharge != null) {
            return mCharge.getTradeNo();
        }
        return "";
    }

    private IChargePayResultListener mPayResultListener = null;

    public void setPayResultListener(IChargePayResultListener listener) {
        mPayResultListener = listener;
    }

    public void pay(Context context, String appSerialNum, ChargeProductInfo info) {
        if (mCharge != null) {
            Log.i("gsp", "pay: 支付获取的参数为appSerialNum  "+appSerialNum+"  info  "+info.productName+"   "+info.productId+"  "+info.productNowPrice+"   "+info.productPrice);

            mCharge.pay(context, appSerialNum, info, this);
        }
    }

    @Override
    public void onChargeInitSuccess() {
        if (mInitListener != null) {
            mInitListener.onChargeInitSuccess();
        }
    }

    @Override
    public void onChargeInitFailed(int errorCode) {
        if (mInitListener != null) {
            mInitListener.onChargeInitFailed(errorCode);
        }
    }

    //慎用  兑换码输入并验证通过后 要走订购成功的流程
    public void redemListener() {
        EvLog.i("onChargePaySuccess mTradeNo:" + mCharge.getTradeNo());
        if (mPayResultListener != null) {
            mPayResultListener.onChargePaySuccess();
        }
    }

    @Override
    public void onChargeAuthSuccess() {
        if (mAuthResultListener != null) {
            mAuthResultListener.onChargeAuthSuccess();
        }
    }

    @Override
    public void onChargeAuthFailed(int errorCode) {

        if (mAuthResultListener != null) {
            mAuthResultListener.onChargeAuthFailed(errorCode);
        }
    }

    @Override
    public void onChargePaySuccess() {
        EvLog.i("onChargePaySuccess mTradeNo:" + mCharge.getTradeNo());
        /*if (!mCharge.isAuthed()) {
            mCharge.mIsAuthed = true;
        }*/
        if (mPayResultListener != null) {
            mPayResultListener.onChargePaySuccess();
        }
    }

    @Override
    public void onChargePayFailed(int errorCode, String errorMessage) {
        EvLog.i("ChargeProxy: onChargePayFailed");
        if (mPayResultListener != null) {
            mPayResultListener.onChargePayFailed(errorCode, errorMessage);
        }
    }

    public IBaseCharge getCharge() {
        return mCharge;
    }

    @Override
    public void onChargeCancel() {
        if (mPayResultListener != null) {
            mPayResultListener.onChargeCancel();
        }
    }
}
