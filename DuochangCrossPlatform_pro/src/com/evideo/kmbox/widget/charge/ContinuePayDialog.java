/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年4月19日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.charge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.KmApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.charge.ChargeProductInfo;
import com.evideo.kmbox.model.chargeproxy.ChargePrice;
import com.evideo.kmbox.model.chargeproxy.ChargeProxy;
import com.evideo.kmbox.model.datacenter.UrlList;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.recode.exit.ExitUtil;
import com.evideo.kmbox.util.BitmapUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.TimeUtil;
import com.evideo.kmbox.widget.ContinuePayBtn;
import com.evideo.kmbox.widget.common.AnimLoadingView;
import com.evideo.kmbox.widget.common.BaseDialog;
import com.evideo.kmbox.widget.common.ToastUtil;
import com.evideo.kmbox.widget.mainview.MainViewManager;

import java.util.List;

/**
 * [功能说明]
 */
public class ContinuePayDialog extends BaseDialog implements ChargePrice.IGetPriceListener, View.OnClickListener {

    private final String TAG = ContinuePayDialog.class.getSimpleName();
    private LinearLayout mPayButtonFirstLineShowRect = null;
    private List<ChargeProductInfo> mPriceList = null;
    private TextView mRemainTimeTx = null;
    private TextView mPaySerialNoTx = null;
    private AnimLoadingView mGetPriceLoadingView = null;
    private static final int MAX_PRICE_NUM_SHOW_ONE_LINE = 3;
    private static final int POINTS_BY_ONE_YUAN = 100;//1元等于100分
    private static final int MILLSSECODE_BY_ONE_MINUTE = 60 * 1000;//1分钟等于60*1000毫秒
    private UpdateValidTimeRunnable mRunnable = null;
    private Context mContext = null;
    private int[] img = new int[]{R.drawable.pay_include_selector
            , R.drawable.pay_include_selector
            , R.drawable.pay_include_selector
            , R.drawable.pay_include_selector
            , R.drawable.pay_include_selector};
    private LinearLayout mEditViewLayout = null;


    private EditText mEditView = null;


    /**
     * @param context
     */
    public ContinuePayDialog(Context context) {
        super(context, R.style.ActivityDialogStyle);
        setContentView(R.layout.dialog_continue_pay);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        init();
        mContext = context;
        ChargePrice.getInstance().setListener(this);
    }

    private void init() {

        mPaySerialNoTx = (TextView) findViewById(R.id.pay_serialno_tx);
        mRemainTimeTx = (TextView) findViewById(R.id.remain_time_tx);
        mPayButtonFirstLineShowRect = (LinearLayout) findViewById(R.id.price_rect);
        mGetPriceLoadingView = (AnimLoadingView) findViewById(R.id.get_price_hint);
        mEditViewLayout = (LinearLayout) findViewById(R.id.redem_edit_layout);
        mEditViewLayout.setVisibility(View.INVISIBLE);
        mEditView = (EditText) findViewById(R.id.redem_edit);
        mEditView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus) {
                    mEditView.setFocusable(true);
                    mEditView.setFocusableInTouchMode(true);
                    mEditView.requestFocus();
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEditView, 0);
                }
            }
        });


    }

    @Override
    public void show() {
        super.show();
        if (mPriceList == null || mPriceList.size() == 0) {
            ChargePrice.getInstance().startGetPrice();
        } else {
            if (!TextUtils.isEmpty(UrlList.pay_huodong_url)) {
                ChargePrice.getInstance().startGetPrice();
            } else {
                showPriceInfo();
                return;
            }
        }
        if (mPayButtonFirstLineShowRect != null) {
            mPayButtonFirstLineShowRect.removeAllViews();
        }
        if (mGetPriceLoadingView != null) {
            if (mGetPriceLoadingView.getVisibility() != View.VISIBLE) {
                mGetPriceLoadingView.setVisibility(View.VISIBLE);
            }
            mGetPriceLoadingView.setLoadingTxt(R.string.getting_pay_price);
            mGetPriceLoadingView.startAnim();
            return;
        }
    }

    private void showPayPriceRect() {
        if (mGetPriceLoadingView != null && mGetPriceLoadingView.getVisibility() != View.GONE) {
            mGetPriceLoadingView.setVisibility(View.GONE);
        }

        if (mPriceList == null || mPriceList.size() == 0) {
            mPriceList = ChargePrice.getInstance().getPriceList();
        }

        int size = mPriceList.size();
        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeResource(mContext.getResources(), R.drawable.paybtn_selected, options);
        int height = options.outHeight;
        int width = options.outWidth;
        int marginBetweenBtn = BaseApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.px_12);
        LayoutParams param = new LayoutParams(width, height);
        mPayButtonFirstLineShowRect.removeAllViews();
        if (size > 0) {
            for (int i = 0; i < mPriceList.size(); i++) {
                final ContinuePayBtn continuePayBtn = new ContinuePayBtn(mContext);
                continuePayBtn.setBtnImageResource(img[i]);
                if (i == 0) {
                    continuePayBtn.setBtnImageResource(R.drawable.pay_include_selected);
                    continuePayBtn.requestFocus();
                    continuePayBtn.setFocusable(true);
                    continuePayBtn.setFocusableInTouchMode(true);
                } else {
                    continuePayBtn.setBtnImageResource(img[i]);
                }
                continuePayBtn.setBtnBackground(R.drawable.btn_pay_selector);
                continuePayBtn.setBtnClickListener(this);
                continuePayBtn.setFlag(mPriceList.get(i).productId);
                continuePayBtn.setNextFocusUpId(mEditView.getId());
                continuePayBtn.setProductNameText(mPriceList.get(i).productName);
                continuePayBtn.setPriceNowText(String.valueOf((float) mPriceList.get(i).productNowPrice / POINTS_BY_ONE_YUAN));
                continuePayBtn.setPriceText(String.valueOf(mPriceList.get(i).productPrice / POINTS_BY_ONE_YUAN));
                mPayButtonFirstLineShowRect.addView(continuePayBtn, param);
                param.leftMargin = marginBetweenBtn;
                final int temp = i;
                continuePayBtn.setBtnOnFocusChangeListener(new OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        continuePayBtn.setBtnImageResource(hasFocus ? R.drawable.pay_include_selected : R.drawable.pay_include_unselected);
                    }
                });
            }
        }
        if (mPriceList.size() < 5) {
            int temp = 5 - mPriceList.size();
            for (int i = 0; i < temp; i++) {
                ContinuePayBtn continuePayBtn = new ContinuePayBtn(mContext);
                mPayButtonFirstLineShowRect.addView(continuePayBtn, param);
                continuePayBtn.setVisibility(View.INVISIBLE);
            }
        }

        if (ChargeProxy.getInstance().isAuthed()) {
            updateValidRemainTime();
            mRemainTimeTx.setVisibility(View.VISIBLE);
            mPaySerialNoTx.setVisibility(View.VISIBLE);
            mEditViewLayout.setVisibility(View.GONE);
        } else {
            mRemainTimeTx.setVisibility(View.INVISIBLE);
            mPaySerialNoTx.setVisibility(View.INVISIBLE);
        }
        mGetPriceLoadingView.setVisibility(View.GONE);
        mEditViewLayout.setVisibility(View.VISIBLE);
        if (mRunnable == null) {
            mRunnable = new UpdateValidTimeRunnable();
        }
        BaseApplication.getHandler().postDelayed(mRunnable, MILLSSECODE_BY_ONE_MINUTE);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mRunnable != null) {
            BaseApplication.getHandler().removeCallbacks(mRunnable);
        }
        if (!TextUtils.isEmpty(mEditView.getText())) {
            mEditView.setText("");
        }

    }

    public class UpdateValidTimeRunnable implements Runnable {
        @Override
        public void run() {
            updateValidRemainTime();
            BaseApplication.getHandler().removeCallbacks(mRunnable);
            BaseApplication.getHandler().postDelayed(mRunnable, MILLSSECODE_BY_ONE_MINUTE);
        }
    }

    private void updateValidRemainTime() {
        long time = ChargeProxy.getInstance().getValidTime();
        long eclipseTime = (System.currentTimeMillis() - KmApplication.getInstance().getPayValidTimeSetTimestamp()) / (60 * 1000);
        EvLog.i("getValidTime:" + time + ",eclipseTime:" + eclipseTime);
        String serialNum = ChargeProxy.getInstance().getTradeNo();
        if (!TextUtils.isEmpty(serialNum)) {
            mPaySerialNoTx.setText(BaseApplication.getInstance().getResources().getString(R.string.charge_serialnum_hint, serialNum));
        }
        String formatTime = TimeUtil.formatTimeByMinute(time - eclipseTime);
        String text = BaseApplication.getInstance().getResources().getString(R.string.charge_valid_time_hint, formatTime);
        if (!TextUtils.isEmpty(formatTime)) {
            mRemainTimeTx.setText(Html.fromHtml(text));
        } else {
            mPaySerialNoTx.setText("");
            mRemainTimeTx.setText("");
        }
    }


    private void showPriceInfo() {
        if (mGetPriceLoadingView != null && mGetPriceLoadingView.getVisibility() == View.VISIBLE) {
            mGetPriceLoadingView.setVisibility(View.GONE);
        }

        int size = ChargePrice.getInstance().getPriceList().size();
        if (size > 0) {
            showPayPriceRect();
        }
    }


    @Override
    public void onGetPriceSuccess() {
        mEditViewLayout.setVisibility(View.INVISIBLE);
        showPriceInfo();
    }

    @Override
    public void onGetPriceFailed() {
        if (mGetPriceLoadingView != null) {
            if (mGetPriceLoadingView.getVisibility() != View.VISIBLE) {
                mGetPriceLoadingView.setVisibility(View.VISIBLE);
            }
            mGetPriceLoadingView.showLoadFail(R.string.get_pay_price_failed);
        }
    }

    public interface ICommonPayClickListener {
        public void onClickPayBtn(ChargeProductInfo info);
    }

    private ICommonPayClickListener mClickListener = null;

    public void setPayBtnClickListener(ICommonPayClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public void onClick(View arg0) {
        log("---onClick()--");
        if (arg0 == null) {
            return;
        }
        int id = (Integer) arg0.getTag();
        EvLog.i("onClick id:" + id);
        for (int i = 0; i < mPriceList.size(); i++) {
            if (id == mPriceList.get(i).productId) {
                if (mClickListener != null) {
                    mClickListener.onClickPayBtn(mPriceList.get(i));
                }
                break;
            }
        }
    }

    private float x_start = 0;

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        log("--onTouchEvent()--");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x_start = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                float move_x = event.getX() - x_start;
                if (x_start < ExitUtil.getExitXMax(mContext)) {
                    if (move_x > ExitUtil.getExitMoveX()) {
                        this.dismiss();
                        return true;
                    }
                }
                break;


        }
        return super.onTouchEvent(event);
    }

    private void log(String msg) {
        Log.d("gsp", TAG + ">>>" + msg);
    }

}
