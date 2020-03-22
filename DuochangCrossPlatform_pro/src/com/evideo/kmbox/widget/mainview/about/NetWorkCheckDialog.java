/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年8月3日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.about;

import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evideo.evnetworkchecker.item.CheckItemCDN;
import com.evideo.evnetworkchecker.item.CheckItemCable;
import com.evideo.evnetworkchecker.item.CheckItemDC;
import com.evideo.evnetworkchecker.item.CheckItemDNS;
//import com.evideo.evnetworkchecker.item.CheckItemGateway;
import com.evideo.evnetworkchecker.item.CheckItemInternet;
import com.evideo.evnetworkchecker.item.CheckItemLocalIP;
import com.evideo.evnetworkchecker.item.ICheckItem;
import com.evideo.evnetworkchecker.item.ICheckStepObserver;
import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.exception.DataCenterCommuException;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.presenter.BasePresenter;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.NetUtils;
import com.evideo.kmbox.widget.common.BaseDialog;
import com.evideo.kmbox.widget.common.MaskFocusButton;
import com.evideo.kmbox.widget.mainview.about.NetWorkCheckManager.TestConfig;

/**
 * [功能说明]
 */
public class NetWorkCheckDialog extends BaseDialog implements ICheckStepObserver {

    private TextView mTitle = null;
    private TextView mTitleTip = null;
    private ArrayList<NetCheckItemWidget> mCheckWidget = null;
    private ProgressBar mBar = null;
    private MaskFocusButton mButton = null;
    private Handler mHandler = null;
    private TotalCheckPresenter mTotalCheckPresenter = new TotalCheckPresenter();

    private boolean mIsAborted = false;
    private boolean mIsCanceled = false;
    private boolean mIsCDNOver = false;
    private int mCurStep = 0;
    private int mCurStepError = 0;
    private int mCurCheckState = 0;
    private int mRemaindSecs = 0;
    private long mCDNResponseTime = 0;
    private long mInterResponseTime = 0;
    private String mCurStepErrorMessage = null;

    private static final int CDN_SPEED_COUNT_DOWN = 10;
//    private static final String LANGUAGETYPE_PRE = "languagetype";

    private String mCloudSongUrl = "";
    private ArrayList<ICheckItem> mStepsArray = null;
    private NetPing mTask = null;

    public class PingStep {
        /**[网络设备检测]*/
        public static final int PING_STEP_LOCAL_CABLE = 0;
        /**[本地IP地址检测]*/
        public static final int PING_STEP_IP = 1;
     /*   *//**[网关地址检测]*//*
        public static final int PING_STEP_NET_GATE = 2;*/
        /**[DNS解析检测]*/
        public static final int PING_STEP_DNS = 2;
        /**[Internet检测]*/
        public static final int PING_STEP_INTERNET = 3;
        /**[多唱数据中心检测]*/
        public static final int PING_STEP_DATA_CENTER = 4;
        /**[云曲库检测]*/
        public static final int PING_STEP_CLOUD_SONG_URL = 5;
        
        public static final int MAX_NUM = 6;
    }
    
    public NetWorkCheckDialog(Context context) {
        super(context, R.style.ActivityDialogStyle);
        setContentView(R.layout.network_check_dialog_lay);
        init();
    }
    
    public void init() {
        NetWorkCheckManager.getInstance().registerListener(this);
        initRes();
        reset();
        initItems();
    }
    
    private void initRes() {
        // 获取资源
//        mCloseImv = (ImageView) findViewById(R.id.dormant_tips_close_imv);
        mButton = (MaskFocusButton) findViewById(R.id.network_check_commit);
        mBar = (ProgressBar) findViewById(R.id.network_check_progress);
        mTitle = (TextView) findViewById(R.id.setting_net_title);
        mTitle.setText(R.string.network_check_title);
        mTitleTip = (TextView) findViewById(R.id.network_check_title_tip);
        mTitleTip.setText(R.string.network_check_initial_tip);
        mTitleTip.setTextColor(BaseApplication.getInstance().getResources().getColor(R.color.gray));


        /** 网络检测开始诊断按钮监听 */
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mCurCheckState != TestConfig.CHECK_STATE_STARTED) {
                    mCurCheckState = TestConfig.CHECK_STATE_STARTED;
                    reset();
                    mTotalCheckPresenter.start();
                } else {
                    // 若当前处于正在检测状态，点击取消按钮
                    mButton.setEnabled(false);
                    cancelCurrentTask();
                    mCurCheckState = TestConfig.CHECK_STATE_CANCEL;
                }
                // 更新按钮显示状态
                updateButtonState(mCurCheckState);
            }
        });

        /** 接收来自NetWorkTestManager的下载信息 */
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == NetWorkCheckManager.MSG_UPDATE_SPEED) {
                    Bundle bundle = msg.getData();
                    float speed = bundle.getFloat(NetWorkCheckManager.MSG_PER_SPEED);
                    mRemaindSecs = bundle.getInt(NetWorkCheckManager.MSG_TIME_REMAIN);

                    if (speed >= 0) {
                        onCheckCDNSpeed(speed);
                    } else if (!mIsCDNOver) {
                        mIsCDNOver = true;
                        onCheckEnd(PingStep.PING_STEP_CLOUD_SONG_URL, 0);
                    }
                }
                super.handleMessage(msg);
            }
        };

        NetWorkCheckManager.getInstance().setHandler(mHandler);

        Resources res = BaseApplication.getInstance().getResources();
        mCheckWidget = new ArrayList<NetCheckItemWidget>();
        NetCheckItemWidget widget = (NetCheckItemWidget)findViewById(R.id.network_check_dialog_step1);
        widget.setTitle(res.getString(R.string.network_check_step1));
        mCheckWidget.add(widget);
        NetCheckItemWidget widget2 = (NetCheckItemWidget)findViewById(R.id.network_check_dialog_step2);
        widget2.setTitle(res.getString(R.string.network_check_step2));
        mCheckWidget.add(widget2);
     /*   NetCheckItemWidget widget3 = (NetCheckItemWidget)findViewById(R.id.network_check_dialog_step3);
        widget3.setTitle(res.getString(R.string.network_check_step3));
        mCheckWidget.add(widget3);*/
        NetCheckItemWidget widget4 = (NetCheckItemWidget)findViewById(R.id.network_check_dialog_step4);
        widget4.setTitle(res.getString(R.string.network_check_step4));
        mCheckWidget.add(widget4);
        NetCheckItemWidget widget5 = (NetCheckItemWidget)findViewById(R.id.network_check_dialog_step5);
        widget5.setTitle(res.getString(R.string.network_check_step5));
        mCheckWidget.add(widget5);
        NetCheckItemWidget widget6 = (NetCheckItemWidget)findViewById(R.id.network_check_dialog_step6);
        widget6.setTitle(res.getString(R.string.network_check_step6));
        mCheckWidget.add(widget6);
        NetCheckItemWidget widget7 = (NetCheckItemWidget)findViewById(R.id.network_check_dialog_step7);
        widget7.setTitle(res.getString(R.string.network_check_step7));
        mCheckWidget.add(widget7);
    }

    /**
     * [功能说明] CDN下载速度更新
     *
     * @param speed CDN下载速度
     */
    @Override
    public void onCheckCDNSpeed(float speed) {
        if (mIsAborted) {
            EvLog.d("CDNisAborted=" + mIsAborted);
            return;
        }

        String speedTv = speed + " KB/s";
        if (speed > 1024) {
            DecimalFormat df = new DecimalFormat("0.00");
            speedTv = df.format(speed / 1024) + " MB/s";
        }

        final String speedTvfinal = speedTv;
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                Resources res = BaseApplication.getInstance().getResources();
                String content = res.getString(
                      R.string.network_check_step7) + "(" + res.getString(R.string.about_text) + mRemaindSecs + "s)";
                mCheckWidget.get(PingStep.PING_STEP_CLOUD_SONG_URL).setTitle(content);
                mCheckWidget.get(PingStep.PING_STEP_CLOUD_SONG_URL).setResult(speedTvfinal);
                mCheckWidget.get(PingStep.PING_STEP_CLOUD_SONG_URL).setResultColor(res.getColor(R.color.common_confirm_dialog_normal_text_color));
            }
        });
    }

    /**
     * [功能说明] 初始化各项检测
     */
    private void initItems() {
        mStepsArray = new ArrayList<ICheckItem>();
        mStepsArray.add(new CheckItemCable());
        mStepsArray.add(new CheckItemLocalIP());
//        mStepsArray.add(new CheckItemGateway());
        mStepsArray.add(new CheckItemDNS());
        mStepsArray.add(new CheckItemInternet());
        mStepsArray.add(new CheckItemDC());
        mStepsArray.add(new CheckItemCDN());
    }

    /**
     * [功能说明] 异步执行网络连通性检查
     */
    private class NetPing extends AsyncPresenter<Boolean> {
        @Override
        protected Boolean doInBackground(Object... params) throws Exception {
            String ipAddr = (String) params[0];
            switch (mCurStep) {
                case PingStep.PING_STEP_IP:
                    return NetUtils.isValidIpAddress(ipAddr);
              /*  case PingStep.PING_STEP_NET_GATE:
                    return NetWorkCheckManager.getInstance().checkIPAvailable(ipAddr);*/
                case PingStep.PING_STEP_DNS:
                    return NetWorkCheckManager.getInstance().checkIPAvailable(ipAddr)
                            && NetWorkCheckManager.getInstance().checkIPAvailable("duochang.cc");
                case PingStep.PING_STEP_DATA_CENTER:
                    mCloudSongUrl = NetWorkCheckManager.getInstance().startCheckDataCenter();
                    return TextUtils.isEmpty(mCloudSongUrl);
                case PingStep.PING_STEP_CLOUD_SONG_URL:
                    EvLog.i("mCloudSongUrl:" + mCloudSongUrl);
                    int ret = NetWorkCheckManager.getInstance().startCheckCloudSongUrl(mCloudSongUrl,ResourceSaverPathManager.getInstance().getCloudListSavePath());
                    return mCurStepError == 0 && isHttpCodeLegal();
                case PingStep.PING_STEP_INTERNET:
                    mInterResponseTime = NetWorkCheckManager.getInstance().checkInternetAvaliable();
                    return mInterResponseTime > 0;
                default:
                    break;
            }
            return false;
        }

        @Override
        protected void onCompleted(Boolean result, Object... params) {
            if (mIsAborted) {
                return;
            }

            if (result) {
                NetWorkCheckManager.getInstance().notifyCheckFinish(mCurStep, 0);
                if (mCurStep == PingStep.PING_STEP_CLOUD_SONG_URL && mCDNResponseTime > 0) {
                    return;
                } else {
                    startCheckTask(mCurStep + 1);
                }
            } else {
                NetWorkCheckManager.getInstance().notifyCheckError(mCurStep, mCurStepError);
            }
        }

        @Override
        protected void onFailed(Exception exception, Object... params) {
            if (exception != null) {
                if (exception instanceof DataCenterCommuException) {
                    mCurStepErrorMessage = exception.getMessage();
                    NetWorkCheckManager.getInstance().notifyCheckError(mCurStep,
                            TestConfig.ERROR_DATACENTER_ERROR_PROTOCOL);
                } else if (exception instanceof SocketTimeoutException) {
                    NetWorkCheckManager.getInstance().notifyCheckError(mCurStep,
                            TestConfig.ERROR_TIMEOUT);
                }
            }
        }
    }

    /**
     * [功能说明] 判断HTTP响应码是否正常
     */
    private boolean isHttpCodeLegal() {
        return NetWorkCheckManager.getInstance().getHttpCode() >= 200
                && NetWorkCheckManager.getInstance().getHttpCode() < 300;
    }

    /**
     * [功能说明] 开始连通性检查任务
     */
    private void startCheckTask(int pingStep) {
        if (mTask != null) {
            mTask = null;
        }
        String ipAddr = null;
        switch (pingStep) {
          /*  case PingStep.PING_STEP_NET_GATE:
                ipAddr = NetWorkCheckManager.getInstance().getGateWay();
                break;*/
            case PingStep.PING_STEP_IP:
                ipAddr = NetWorkCheckManager.getInstance().getIP();
                break;
            case PingStep.PING_STEP_DNS:
                if (TextUtils.isEmpty(NetWorkCheckManager.getInstance().getDNS())) {
                    ipAddr = "8.8.8.8";
                } else {
                    ipAddr = NetWorkCheckManager.getInstance().getDNS();
                }
                break;
            case PingStep.PING_STEP_INTERNET:
                ipAddr = TestConfig.INTERNET_TEST_URL;
                break;
            default:
                break;
        }
        mCurStep = pingStep;
        mTask = new NetPing();
        mTask.start(ipAddr);
        NetWorkCheckManager.getInstance().notifyCheckStart(pingStep);
    }

    /**
     * [功能说明] 更新按钮状态
     *
     * @param state 步骤
     */
    public void updateButtonState(int state) {
        Resources res = BaseApplication.getInstance().getResources();
        
        switch (state) {
            case TestConfig.CHECK_STATE_IDLE:
                mButton.setText(res.getString(R.string.network_check_start_check));
//                mButton.setBackground(res.getDrawable(R.drawable.common_dialog_ok_big_selector));
                break;
            case TestConfig.CHECK_STATE_STARTED:
                mButton.setText(res.getString(R.string.network_check_abort));
//                mButton.setBackground(res.getDrawable(R.drawable.common_dialog_cancel_big_selector));
                break;
            case TestConfig.CHECK_STATE_END:
                mButton.setText(res.getString(R.string.network_check_recheck));
//                mButton.setBackground(res.getDrawable(R.drawable.common_dialog_ok_big_selector));
                break;
            case TestConfig.CHECK_STATE_CANCEL:
                mButton.setText(res.getString(R.string.network_check_cancel));
                break;
            default:
                break;
        }
    }

    /**
     * [功能说明] 重置UI
     */
    public void reset() {
        mIsAborted = false;
        mIsCanceled = false;
        mIsCDNOver = false;

        // 进度条进度值重置
        if (mBar != null) {
            mBar.setProgress(0);
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }

        NetWorkCheckManager nt = NetWorkCheckManager.getInstance();
        nt.stopDownloadTask();
        nt.mIsCanceled = false;
        mCurCheckState = TestConfig.CHECK_STATE_IDLE;
        /** 更新UI */
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                updateButtonState(mCurCheckState);
                Resources res = BaseApplication.getInstance().getResources();
                // 恢复各项检测标题和状态的字体颜色
                for (int i = 0; i < PingStep.MAX_NUM; i++) {
                    mCheckWidget.get(i).setGifResId(R.drawable.network_check_wait);
                    mCheckWidget.get(i).setResult(res.getString(R.string.network_check_unchecked));
                    mCheckWidget.get(i).setResultColor(res.getColor(R.color.common_confirm_dialog_normal_text_color));
                    mCheckWidget.get(i).setTitleColor(res.getColor(R.color.common_confirm_dialog_normal_text_color));
                }
                // 恢复进度条颜色为绿色，出现异常时设置为红色
                mBar.setProgressDrawable(res.getDrawable(R.drawable.process_dialog_network_check));
                // 恢复检测提示
                mTitleTip.setText(R.string.network_check_initial_tip);
                mTitleTip.setTextColor(res.getColor(R.color.gray));
                // 重置倒数计时
                mRemaindSecs = CDN_SPEED_COUNT_DOWN;
                mCheckWidget.get(PingStep.PING_STEP_CLOUD_SONG_URL).setTitle(res.getString(R.string.network_check_step7));
            }
        });
    }

    /**
     * [功能说明] 取消当前任务
     */
    private void cancelCurrentTask() {
        NetWorkCheckManager.getInstance().stopDownloadTask();
        mIsCanceled = true;
    }

    @Override
    public void onDetachedFromWindow() {
        cancelCurrentTask();
        super.onDetachedFromWindow();
    }

    @Override
    public void show() {
        super.show();
        reset();
//        mCheckWidget.get(PingStep.PING_STEP_CLOUD_SONG_URL).setGifResId(R.drawable.network_check_wait);
//        mCheckWidget.get(PingStep.PING_STEP_CLOUD_SONG_URL).setResult(BaseApplication.getInstance().getResources().getString(R.string.network_check_unchecked));
    }
    
    @Override
    public void dismiss() {
        cancelCurrentTask();
        super.dismiss();
    }

    @Override
    protected void onStop(){
        super.onStop();
        NetWorkCheckManager.getInstance().unregisterListener(this);
    }

    /**
     * [功能说明] 重构之后的异步任务类，执行各项检测
     */
    private class TotalCheckPresenter extends AsyncPresenter<Boolean> {
        @Override
        protected Boolean doInBackground(Object... params) throws Exception {
            // 执行各项检测
            for (int i = 0; i < PingStep.MAX_NUM; i++) {
                // 检测用户是否点击取消检测按钮
                if (mIsCanceled) {
                    /** 更新UI */
                    BasePresenter.runInUI(new Runnable() {
                        @Override
                        public void run() {
                            reset();
                            mButton.setEnabled(true);
//                            mButton.setBackground(BaseApplication.getInstance().getResources().getDrawable(R.drawable.common_dialog_ok_big_selector));
                        }
                    });
                    break;
                }
                onCheckStart(i);

                // 执行各项检测
                EvLog.i("step " + i + " running");
                mStepsArray.get(i).run();
                EvLog.i("step " + i + " done");

                // 对于时间较长的检测项，在其执行后判断用户是否取消
                if (mIsCanceled) {
                    /** 更新UI */
                    BasePresenter.runInUI(new Runnable() {
                        @Override
                        public void run() {
                            reset();
                            mButton.setEnabled(true);
//                            mButton.setBackground(BaseApplication.getInstance().getResources().getDrawable(R.drawable.common_dialog_ok_big_selector));
                        }
                    });
                    break;
                }

                if (i == 1) {
                    String msg = mStepsArray.get(i).getResultMessage();
                    /*// 以接收到到msg重构IpParams
                    mIps = IpParams.getInstanceByString(msg);*/
                    NetWorkCheckManager.getInstance().initIPInfo();
                }

                if (!mStepsArray.get(i).getResult()) {
                    if (!mIsCanceled) {
                        onCheckError(i, mStepsArray.get(i).getErrorType());
                        break;
                    }
                } else {
                    if (i <= PingStep.MAX_NUM) {
                        onCheckEnd(i, 0);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onCompleted(Boolean result, Object... params) {
            if (!mIsCanceled) {
                EvLog.i("check success");
            } else {
                EvLog.i("check caneled");
            }
        }

        @Override
        protected void onFailed(Exception exception, Object... params) {
            EvLog.i("check fail");
        }
    }

    /**
     * [功能说明] 检测开始时，根据检测进度更新对应UI
     *
     * @param state 步骤
     */
    @Override
    public void onCheckStart(final int state) {
        if (state < 0 || state > PingStep.PING_STEP_CLOUD_SONG_URL) {
            return;
        }
        EvLog.i("step:" + state + " start");

        /** 开始执行第一步检测时，更新按钮/检测提示文本 */
        if (state == PingStep.PING_STEP_LOCAL_CABLE) {
            mCurCheckState = TestConfig.CHECK_STATE_STARTED;
            BasePresenter.runInUI(new Runnable() {
                @Override
                public void run() {
                    updateButtonState(TestConfig.CHECK_STATE_STARTED);
                    mTitleTip.setText(R.string.network_check_hint_ischecking);
                    mTitleTip.setTextColor(BaseApplication.getInstance().getResources().getColor(R.color.common_confirm_dialog_normal_text_color));
                    mTitleTip.setVisibility(View.VISIBLE);
                }
            });
        }

        /** 更新进度条/各项检测的UI */
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                mCheckWidget.get(state).setGifResId(R.drawable.network_check_loading);
//                mTipIcon.get(state).setImageResource(R.drawable.network_check_loading);
                int progress = (100*state)/(PingStep.MAX_NUM);
                EvLog.i(">>>>>>>>>>state:"+state + ",progress:"+progress);
                mBar.setProgress(progress);
                if (state < PingStep.PING_STEP_CLOUD_SONG_URL) {
                    mCheckWidget.get(state).setResult(BaseApplication.getInstance().getResources().getString(R.string.network_check_ischecking));
                    mCheckWidget.get(state).setResultColor(BaseApplication.getInstance().getResources().getColor(R.color.common_confirm_dialog_normal_text_color));
                }
            }
        });
    }

    /**
     * [功能说明] 各项检测执行完成后，根据进度更新UI
     *
     * @param state    步骤
     * @param trycount 未知参数
     */
    @Override
    public void onCheckEnd(final int state, int trycount) {
        if (state < 0 || state > PingStep.PING_STEP_CLOUD_SONG_URL) {
            return;
        }

        if (mCurCheckState == TestConfig.CHECK_STATE_END) {
            return;
        }

        Runnable freshUI = new Runnable() {
            @Override
            public void run() {
                if (state == PingStep.PING_STEP_CLOUD_SONG_URL) {
                    long speed = NetWorkCheckManager.getInstance().getAverageSpeed();
                    String speedTv = speed + " KB/s";
                    if (speed > 1024) {
                        DecimalFormat df = new DecimalFormat("0.00");
                        speedTv = df.format(1.0F * speed / 1024) + " MB/s";
                    }
                    final String speedTvFinal = speedTv;
                    mBar.setProgress(100);
                    updateButtonState(mCurCheckState);
                    Resources res = BaseApplication.getInstance().getResources();
                    mCheckWidget.get(PingStep.PING_STEP_CLOUD_SONG_URL).setTitle(res.getString(R.string.network_check_step7));
//                    mItemTitle[PingStep.PING_STEP_CLOUD_SONG_URL].setText(res.getString(R.string.network_check_step7));
                    mTitleTip.setText(R.string.network_check_everything_ok);
                    mCheckWidget.get(state).setGifResId(R.drawable.network_check_success);
                    mCheckWidget.get(state).setResult(speedTvFinal);
                    mCheckWidget.get(state).setResultColor(res.getColor(R.color.common_confirm_dialog_grey_text_color));
                    mCheckWidget.get(state).setTitleColor(res.getColor(R.color.common_confirm_dialog_grey_text_color));
                } else {
                    Resources res = BaseApplication.getInstance().getResources();
//                    mBar.setProgress(100 / (PingStep.PING_STEP_CLOUD_SONG_URL + 1) * (state + 1));
                    mBar.setProgress((state/PingStep.MAX_NUM)*100);
                    mCheckWidget.get(state).setGifResId(R.drawable.network_check_success);
                    mCheckWidget.get(state).setTitleColor(res.getColor(R.color.common_confirm_dialog_grey_text_color));
                    mCheckWidget.get(state).setResult(res.getString(R.string.network_check_checked_correct));
                    mCheckWidget.get(state).setResultColor(res.getColor(R.color.common_confirm_dialog_grey_text_color));
                }
            }
        };

        EvLog.i("ping step:" + state + " end");
        if (state == PingStep.PING_STEP_CLOUD_SONG_URL && mIsCDNOver) {
            mCurCheckState = TestConfig.CHECK_STATE_END;
            BasePresenter.runInUI(freshUI);
        } else {
            BasePresenter.runInUI(freshUI);
        }
    }

    /**
     * [功能说明] 出现异常时，更新UI提示用户
     * @param state 步骤
     * @param type  类型
     */
    @Override
    public void onCheckError(final int state, final int type) {
        if (mIsCanceled) {
            return;
        }
        mIsAborted = true;
        EvLog.e("ping step:" + state + " error");
        mCurCheckState = TestConfig.CHECK_STATE_END;
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                Resources res = BaseApplication.getInstance().getResources();
                updateButtonState(mCurCheckState);
                mTitleTip.setText(mStepsArray.get(state).getResolution());
                mTitleTip.setTextColor(res.getColor(R.color.red_error));
                mTitleTip.setVisibility(View.VISIBLE);
                // 网络检测发现异常时，进度条设置为红色
                mBar.setProgressDrawable(res.getDrawable(
                        R.drawable.process_dialog_network_error));
                // 网络检测发现异常时，相应检测项提示异常
                mCheckWidget.get(state).setResult(res.getString(R.string.network_check_checked_error));
                mCheckWidget.get(state).setResultColor(res.getColor(R.color.red_error));
                mCheckWidget.get(state).setTitleColor(res.getColor(R.color.red_error));
                mCheckWidget.get(state).setGifResId(R.drawable.network_check_fail);
                // 云曲库检测选项文本重置
                mCheckWidget.get(PingStep.PING_STEP_CLOUD_SONG_URL).setTitle(res.getString(R.string.network_check_step7));
            }
        });

        switch (state) {
            case PingStep.PING_STEP_DATA_CENTER:
                BasePresenter.runInUI(new Runnable() {
                    @Override
                    public void run() {
                        if (type == TestConfig.ERROR_DATACENTER_ERROR_PROTOCOL) {
                            mTitleTip.setText(BaseApplication.getInstance().getResources().getString(
                                    R.string.network_check_error_hint_step6_wrong_errorcode, mCurStepErrorMessage));
                        }
                    }
                });
                break;
            case PingStep.PING_STEP_CLOUD_SONG_URL:
                /** 区别错误类型：点播链错误 */
                BasePresenter.runInUI(new Runnable() {
                    @Override
                    public void run() {
                        // 云曲库链接无效
                        if (type == TestConfig.ERROR_RAINBOW_ERROR_ADDR) {
                            mTitleTip.setText(BaseApplication.getInstance().getResources().getString(
                                    R.string.network_check_error_hint_step7_error_url));
                            // socket连接超时
                        } else if (type == TestConfig.ERROR_TIMEOUT) {
                            mTitleTip.setText(BaseApplication.getInstance().getResources().getString(R.string.network_check_error_hint_step7_timeout));
                            // httpCode错误
                        } else if (type == TestConfig.ERROR_DOWNLOAD) {
                            mTitleTip.setText(BaseApplication.getInstance().getResources().getString(R.string.network_check_error_hint_step7_download_error,
                                    String.valueOf(NetWorkCheckManager.getInstance().getErrorCode())));
                        } else if (type == TestConfig.ERROR_HTTPCODE_ILLEAGL) {
                            mTitleTip.setText(BaseApplication.getInstance().getResources().getString(R.string.network_check_error_hint_step7_errorresponse));
                        }
                    }
                });
                break;
            default:
                break;
        }
    }
}

