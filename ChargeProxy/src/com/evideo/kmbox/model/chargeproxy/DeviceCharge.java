/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年4月6日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.chargeproxy;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.evideo.kmbox.model.charge.ChargeError;
import com.evideo.kmbox.model.charge.ChargeProductInfo;
import com.evideo.kmbox.model.charge.IBaseCharge;
import com.evideo.kmbox.model.chargeproxy.DeviceCommu.AuthResultInfo;
import com.evideo.kmbox.model.datacenter.DataCenterCommu;
import com.evideo.kmbox.model.datacenter.proxy.data.DataCenterMessage;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.device.IDeviceConfig;
import com.evideo.kmbox.model.pay.PayActivity;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.presenter.CommuPresenter;
import com.evideo.kmbox.presenter.CommuPresenter.CommuCallback;
import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明]
 */
public class DeviceCharge extends IBaseCharge {

    private static final String KEY_TRADE_NO = "key_charge_trade_num";
    private Context mActivityContext = null;
//    private IChargeInitListener mInitListener = null;
    
    private IChargePayResultListener mPayResultListener = null;
    
    public DeviceCharge() {
    }
    
    /*private String getSerialNo() {
        String no = DeviceConfigManager.getInstance().getChipId();
        no += String.valueOf(System.currentTimeMillis());
        no += new Random().nextInt(9999);
        return no;
    }*/

    @Override
    public void init(Context context, IChargeInitListener listener) {
        mInitSuccess = true;
        EvLog.i("init success,mTradeNo:" + mTradeNo);
    }

    @Override
    public void uninit() {
        if (mAuthPresenter != null) {
            mAuthPresenter.cancel();
            mAuthPresenter = null;
        }
    }
    private CommuPresenter mAuthPresenter = null;
    private AuthCommu mAuthCommu = null;
    private IChargeAuthResultListener mAuthListener = null;
    
    @Override
    public void sendAuthRequest(Context context,IChargeAuthResultListener listener) {
        mAuthListener = listener;
        if (mAuthPresenter != null) {
            mAuthPresenter.cancel();
            mAuthPresenter = null;
        }
        if (mAuthCommu == null) {
            mAuthCommu = new AuthCommu();
        }
        mAuthPresenter = new CommuPresenter(mAuthCommu);
        mAuthPresenter.start(mTradeNo);
    }
  
    
    
    private void beginPay(ChargeProductInfo info,String tradeNo){
        Intent intent = new Intent();
        intent.setClass(mActivityContext, PayActivity.class);
        intent.putExtra("PID",String.valueOf(info.productId)); //商品id，最大长度为40个字符，必填
        intent.putExtra("Pname",IDeviceConfig.APP_NAME + info.productName); //商品名称，最大长度为60个字符，必填
        //当贝市场是以元为单位
        intent.putExtra("Pprice",String.valueOf(((float)info.productNowPrice/100))); //商品价格，必填
        intent.putExtra("Pdesc",IDeviceConfig.APP_NAME + info.productName); //商品描述，最大长度为60个字符，必填
        Log.i("gsp", "beginPay: 订单数据是DeviceCharge"+tradeNo);
        intent.putExtra("order",tradeNo); //order为订单号（数字），可选
        intent.putExtra("extra",tradeNo); //extra为备用字段，可选
        intent.putExtra("productId",info.productId); //extra为备用字段，可选

        mActivityContext.startActivity(intent);
    }
    
    @Override
    public void pay(Context context, String appSerialNum, ChargeProductInfo info,
            IChargePayResultListener listener) {
        mPayResultListener = listener;
        mActivityContext = context;
        /*if (TextUtils.isEmpty(appSerialNum)) {
            queryTradeNo(info);
        } else */{
//            mQueryTradeNo = appSerialNum;
            Log.i("gsp", "pay:调用支付DeviceChaege "+appSerialNum);
            beginPay(info,appSerialNum);
        }
    }
 
   
    public void onUpdateAuthResult(long time,int productid,String appSerialNo) {
        if (time > 0) {
            mIsAuthed = true;
        }
        EvLog.i("charge onUpdateValidTime:" + time + ",appSerialNo:" + appSerialNo);
        super.mValidTime = time;
        super.mProductId = productid;
        if (!super.mTradeNo.equals(appSerialNo)) {
            EvLog.i("onUpdateAuthResult set mTradeNo:" + appSerialNo);
            super.mTradeNo = appSerialNo;
            KmSharedPreferences.getInstance().putString(KEY_TRADE_NO, mTradeNo);
        }
//        if (!super.mIsAuthed) {
//            super.mIsAuthed = true;
//        }
    }

    @Override
    public String getPayNotifyUrl() {
        return "";
    }

    @Override
    public void sdkPaySuccess(long time, int productId, String serialNo) {
        Log.i("gsp", "onAuthSuccess:DeviceCharge3 "+mAuthListener);
        onUpdateAuthResult(time, productId, serialNo);
        if (mPayResultListener != null) {
            mPayResultListener.onChargePaySuccess();
        }
    }

    @Override
    public void sdkPayFailed(int errorCode, String errorMessag) {
        Log.i("gsp", "onAuthSuccess:DeviceCharge2 "+mAuthListener);
//        mQueryTradeNo = "";
        if (mPayResultListener != null) {
            mPayResultListener.onChargePayFailed(errorCode, errorMessag);
        }
    }
    
    public void onAuthSuccess(long time, int productId, String appSerialNo) {
        Log.i("gsp", "onAuthSuccess:DeviceCharge "+mAuthListener);
        onUpdateAuthResult(time,productId,appSerialNo);
        if (mAuthListener != null) {
            mAuthListener.onChargeAuthSuccess();
        }
    }
    
    public void onAuthFailed(int errorCode, String errorMessage) {
        mIsAuthed = false;
        if (mAuthListener != null) {
            mAuthListener.onChargeAuthFailed(errorCode);
        }
        
        if (errorCode != ChargeError.ERROR_CODE_NET_WHEN_AUTH && !TextUtils.isEmpty(mTradeNo)) {
            mTradeNo = "";
            KmSharedPreferences.getInstance().putString(KEY_TRADE_NO, mTradeNo);
            EvLog.e("onChargeAuthFailed update mTradeNo:" + mTradeNo);
        }
    }
    
    public class AuthCommu implements CommuCallback{

        private AuthResultInfo mInfo = null;
        @Override
        public Boolean doCommu(Object... params) throws Exception {
            String serialNo = (String) params[0];
            EvLog.i("AuthPresenter auth: " + serialNo);
          
          int maxTryNum = 3;
          for (int i = 0; i < maxTryNum; i++) {
              try {
                  mInfo = DeviceCommu.queryAuth();
                  if (mInfo != null) {
                      break;
                  }
              } catch (Exception e) {
                  e.printStackTrace();
                  continue;
              }
          }
          
          if (mInfo != null && mInfo.errorCode == 0) {
              return true;
          }
          return false;
        }

        @Override
        public void commuSuccess() {
            EvLog.d(">>>> AuthCommu commuSuccess");
            mAuthPresenter = null;
            onAuthSuccess(mInfo.validTime, mInfo.productId, mInfo.appSerialNo);
        }

        @Override
        public void commuFailed(Exception exception) {
            EvLog.d(">>>> AuthCommu commuFailed");
            mAuthPresenter = null;
            String errMessage = "服务器通信异常";
            int errorCode = ChargeError.ERROR_CODE_NET_WHEN_AUTH;
            if (mInfo != null) {
                errMessage = mInfo.errorMessage;
                errorCode = mInfo.errorCode;
            } 
            onAuthFailed(errorCode, errMessage);
        }
    }

    @Override
    public void cancelCheckPayResult() {
        
    }

}
