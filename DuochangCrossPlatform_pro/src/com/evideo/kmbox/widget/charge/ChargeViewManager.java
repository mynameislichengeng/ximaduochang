/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年3月9日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.charge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.KmApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.charge.ChargeError;
import com.evideo.kmbox.model.charge.ChargeProductInfo;
import com.evideo.kmbox.model.charge.IBaseCharge;
import com.evideo.kmbox.model.charge.IBaseCharge.IChargeAuthResultListener;
import com.evideo.kmbox.model.charge.IBaseCharge.IChargePayResultListener;
import com.evideo.kmbox.model.chargeproxy.ChargeProxy;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.device.DeviceName;
import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;
import com.evideo.kmbox.model.umeng.EventConst;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.BasePresenter;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.TimeUtil;
import com.evideo.kmbox.widget.charge.ContinuePayDialog.ICommonPayClickListener;
import com.evideo.kmbox.widget.common.CommonDialog;
import com.evideo.kmbox.widget.common.ToastUtil;
import com.evideo.kmbox.widget.mainview.MainViewManager;

import java.util.ArrayList;
import java.util.List;

/**
 * [功能说明]
 */
public class ChargeViewManager implements IChargePayResultListener,
        IChargeAuthResultListener, ICommonPayClickListener, OnDismissListener {
    private static ChargeViewManager instance = null;
    private ContinuePayDialog mPayDialog = null;
    private boolean mNeedShowPayDialog = false;

    private boolean isStopGetResult = false;
    private String mTradeNo = "";

    public static ChargeViewManager getInstance() {
        if (instance == null) {
            synchronized (ChargeViewManager.class) {
                ChargeViewManager temp = instance;
                if (temp == null) {
                    temp = new ChargeViewManager();
                    instance = temp;
                }
            }
        }
        return instance;
    }

    public boolean isStopGetResult() {
        return isStopGetResult;
    }

    public void setStopGetResult(boolean stopGetResult) {
        isStopGetResult = stopGetResult;
    }

    public void setmTradeNo(String mTradeNo) {
        this.mTradeNo = mTradeNo;
    }

    public String getmTradeNo() {
        return mTradeNo;
    }

    public boolean getmNeedShowPayDialog() {
        return mNeedShowPayDialog;
    }

    public void setmNeedShowPayDialog(boolean mNeedShowPayDialog) {
        this.mNeedShowPayDialog = mNeedShowPayDialog;
    }

    private ChargeViewManager() {
        mListeners = new ArrayList<ChargeViewManager.IChargeFinishListener>();
        ChargeProxy.getInstance().setAuthResultListener(this);
    }

    public void clickChargeView(final Context context) {
        if (mPayDialog == null) {
            mPayDialog = new ContinuePayDialog(context);
            mPayDialog.setOnDismissListener(this);
            mPayDialog.setPayBtnClickListener(this);
        }

        mPayDialog.show();
        LogAnalyzeManager.onEvent(context, EventConst.ID_CLICK_CHARGE_ORDER);
        return;
    }

    public interface IChargeFinishListener {
        public void onChargeSuccess();

        public void onChargeFailed();

        public void onChargeCancel();
    }

    private List<IChargeFinishListener> mListeners = null;

    public void addListener(IChargeFinishListener listener) {
        if (mListeners != null) {
            mListeners.add(listener);
        }
    }

    public void removeListener(IChargeFinishListener listener) {
        if (mListeners != null && mListeners.contains(listener)) {
            mListeners.remove(listener);
        }
    }

    public void showContinuePayDialog(long time) {
        // K米盒子不显示应用退出对话框
        if (!DeviceConfigManager.getInstance().isThirdApp()) {
            return;
        }

        final CommonDialog mValidTimeDialog = new CommonDialog(MainViewManager
                .getInstance().getActivity());
        mValidTimeDialog.setTitle(-1);
        String text = BaseApplication
                .getInstance()
                .getResources()
                .getString(R.string.continue_pay_content_text,
                        TimeUtil.formatTimeByMinute(time));
        mValidTimeDialog.setContent(text);
        mValidTimeDialog.setButton(R.string.continue_pay,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickChargeView(BaseApplication.getInstance());
                        //修改点击去续订，没有订购的情况下，仍可以点歌问题
                        if (DeviceConfigManager.getInstance().isSupportCharge()) {
                            auth();
                        }
                    }
                }, -1, null);
        mValidTimeDialog.setOkBtnBg(R.drawable.btn_red_bg);
        mValidTimeDialog.show();
        return;
    }

    public void auth() {
        ChargeProxy.getInstance().checkAuth(BaseApplication.getInstance());
    }

    public interface IAuthListener {
        public void onAuthSuccess();

        public void onAuthFailed();
    }

    private IAuthListener mAuthListener = null;

    public void auth(IAuthListener listener) {
        ChargeProxy.getInstance().checkAuth(BaseApplication.getInstance());
        mAuthListener = listener;
    }

    @Override
    public void onChargePaySuccess() {
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                Log.i("gsp", "onChargePaySuccess:dialog支付成功 ");
                if (mPayDialog != null && mPayDialog.isShowing()) {
                    mPayDialog.hide();
                }
                MainViewManager.getInstance().setChargePayBtnResId(R.drawable.ic_unicom_payed);
                ToastUtil.showLongToast(
                        BaseApplication.getInstance(),
                        BaseApplication.getInstance().getResources()
                                .getString(R.string.unicom_pay_success));
                if (DeviceConfigManager.getInstance().isSupportUserLogin()) {
                    long time = ChargeProxy.getInstance().getValidTime();
                    DeviceConfigManager.getInstance().setRemainVipTime(time);
                }
                for (IChargeFinishListener listener : mListeners) {
                    listener.onChargeSuccess();
                }
                KmApplication.getInstance().setPayValidTimeSetTimestamp(
                        System.currentTimeMillis());
            }
        });

    }

    @Override
    public void onChargePayFailed(final int errorCode, final String errMsg) {
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                Log.i("gsp", "onChargePayFailed:产品支付失败 ");
                if (mPayDialog != null && mPayDialog.isShowing()) {
                    mPayDialog.hide();
                }
                String errorMessage = "";
                if (!TextUtils.isEmpty(errMsg)) {
                    errorMessage = errMsg;
                } else {
                    if (errorCode == IBaseCharge.ERROR_GET_PAY_NOTIFY_URL_FAILED) {
                        errorMessage = BaseApplication.getInstance()
                                .getResources()
                                .getString(R.string.get_pay_notify_url_failed);
                    } else {
                        errorMessage = String.format(
                                BaseApplication.getInstance().getResources()
                                        .getString(R.string.unicom_pay_failed),
                                errorCode);
                    }
                }

                ToastUtil.showLongToast(BaseApplication.getInstance(),
                        errorMessage);
                for (IChargeFinishListener listener : mListeners) {
                    listener.onChargeFailed();
                }
            }
        });

    }

    @Override
    public void onChargeCancel() {
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                Log.i("gsp", "onChargePayFailed:产品返回取消 ");
                ToastUtil.showLongToast(
                        BaseApplication.getInstance(),
                        BaseApplication.getInstance().getResources()
                                .getString(R.string.pay_cancel));
            }
        });

    }

    @Override
    public void onChargeAuthSuccess() {
        EvLog.i("onAuthSuccess");
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                if (mAuthListener != null) {
                    mAuthListener.onAuthSuccess();
                }
                if (DeviceConfigManager.getInstance().getRemainVipTime() > 0) {
                    MainViewManager.getInstance().getStatusBar().setChargePayBtnResId(R.drawable.ic_unicom_payed);
                }else {
                    MainViewManager.getInstance().getStatusBar().setChargePayBtnResId(R.drawable.ic_unicom_pay);
                }
                int productId = ChargeProxy.getInstance().getProductId();
                long validTime = ChargeProxy.getInstance().getValidTime();

                EvLog.i("get ProductId:" + productId + ",validTime:" + validTime);
                if (validTime <= 0) {
                    String appSerialNum = ChargeProxy.getInstance()
                            .getTradeNo();
                    UmengAgentUtil.reportError(appSerialNum
                            + " auth,get validTime:" + validTime);
                    return;
                }
                KmApplication.getInstance().setPayValidTimeSetTimestamp(
                        System.currentTimeMillis());
                DeviceConfigManager.getInstance().setRemainVipTime(validTime);

                //剩余有效使用时长大于3个小时，不提示到期
                if (validTime >= IBaseCharge.VALID_TIME_WARN_MIN_MINUTE) {
                    return;
                }

                EvLog.i("checkAuth delay:" + validTime * 60 * 1000);
                BaseApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainViewManager
                                .getInstance()
                                .getStatusBar()
                                .setChargePayBtnResId(
                                        R.drawable.ic_unicom_pay);
                        showContinuePayDialog(0);
                    }
                }, validTime * 60 * 1000 + 1000);
            }
        });
    }

    @Override
    public void onChargeAuthFailed(final int errorCode) {
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                EvLog.i("onChargeAuthFailed:" + errorCode);
                if (mAuthListener != null) {
                    mAuthListener.onAuthSuccess();
                }

                MainViewManager.getInstance().getStatusBar()
                        .setChargePayBtnResId(R.drawable.ic_unicom_pay);
                boolean isAuthedLocal = !TextUtils.isEmpty(ChargeProxy.getInstance()
                        .getTradeNo());
                if (isAuthedLocal && errorCode == ChargeError.ERROR_CODE_NET_WHEN_AUTH) {
                    ToastUtil.showLongToast(
                            BaseApplication.getInstance(),
                            BaseApplication.getInstance().getResources()
                                    .getString(R.string.auth_failed_check_net));
                }
            }
        });
    }

    @Override
    public void onClickPayBtn(final ChargeProductInfo info) {

        ChargeViewManager.getInstance().setStopGetResult(false);
        if (!ChargeProxy.getInstance().isInitSuccess()) {
            ToastUtil.showLongToast(BaseApplication.getInstance(), BaseApplication.getInstance().getResources()
                    .getString(R.string.unicom_pay_error_init_failed));
            return;
        }
        ChargeProxy.getInstance().setPayResultListener(this);
        Log.i("gsp", "onClickPayBtn: 开始调用支付代理"+MainViewManager.getInstance().getActivity()+" info"+info);
        ChargeProxy.getInstance().pay(MainViewManager.getInstance().getActivity(), "", info);

    }

    public void notifyChargeFinish(boolean success) {
        if (success) {
            for (IChargeFinishListener listener : mListeners) {
                listener.onChargeSuccess();
            }
        } else {
            for (IChargeFinishListener listener : mListeners) {
                listener.onChargeFailed();
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (!ChargeProxy.getInstance().isAuthed()) {
            for (IChargeFinishListener listener : mListeners) {
                listener.onChargeCancel();
            }
            int size = PlayListManager.getInstance().getCount();
            if (size > 0) {
                PlayListManager.getInstance().clearList();
            }
        } else {
            for (IChargeFinishListener listener : mListeners) {
                listener.onChargeSuccess();
            }
        }
    }

    public interface PayDialogCancelCallBack {
        void cancelPay(int time);
    }
}
