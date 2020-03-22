/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-9-26     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.homepage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.SystemConfigManager;
import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.util.BitmapUtil;
import com.evideo.kmbox.util.CommonUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.QrGenerator;
import com.evideo.kmbox.widget.common.BaseDialog;
import com.evideo.kmbox.widget.common.CommonDialog;
import com.evideo.kmbox.widget.common.SearchKeyboard;
import com.evideo.kmbox.widget.common.ToastUtil;
import com.evideo.kmbox.widget.mainview.MainViewId;

import java.util.ArrayList;

/**
 * [功能说明]手机点歌弹框
 */
public class OrderByPhoneDialog extends BaseDialog {
    private int mQrWidth = 0;
    private int mQrHeight = 0;
    private ImageView mQRImageView = null;
    private ImageView mLogoImageView = null;
    private Bitmap mQrBmp = null;
    /**
     * @param context
     */
    public OrderByPhoneDialog(Context context) {
        super(context, R.style.QrDialogStyle);
        setContentView(R.layout.dialog_order_by_phone);
        getWindow().setBackgroundDrawableResource(R.drawable.home_phone_ordersong_bg);
        init();
        mQrWidth = context.getResources().getDimensionPixelSize(R.dimen.px388);
        mQrHeight = context.getResources().getDimensionPixelSize(R.dimen.px388);
    }
    
    private void init() {
        mQRImageView = (ImageView) findViewById(R.id.qr_code_iv);
      /*  Bitmap bmp = BitmapUtil.getBitmapByResId(BaseApplication.getInstance(), R.drawable.km_qr_code_default);
        mQRImageView.setImageBitmap(bmp);*/
        
        mLogoImageView = (ImageView) findViewById(R.id.qr_code_logo);
        Bitmap bmpLogo = BitmapUtil.getBitmapByResId(BaseApplication.getInstance(), R.drawable.qrcode_logo_k20);
        mLogoImageView.setImageBitmap(bmpLogo);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (checkKeyCode(keyCode)) {
                    switchDC();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
    
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private static final int THRESHOLD_TIME = 500;
    private static final int MIN_KEYDOWN_NUM_TO_SHOW_ALL_OPTIONS = 5;
    private ArrayList<Integer> mKeyCodeList = new ArrayList<Integer>();
    private long mCurrTime = 0;
    /**
     * [检测记录的key数组]
     *
     * @param keyCode
     */
    public boolean checkKeyCode(int keyCode) {
        long tempTime = System.currentTimeMillis();
        long gapTime = tempTime - mCurrTime;
        if (gapTime >= THRESHOLD_TIME) {
            mKeyCodeList.clear();
            mKeyCodeList.add(keyCode);
        } else if (gapTime > 0 && gapTime < THRESHOLD_TIME) {
            mKeyCodeList.add(keyCode);
            EvLog.d(" add down key " + mKeyCodeList.size());
        }
        mCurrTime = tempTime;
        if (mKeyCodeList.size() == MIN_KEYDOWN_NUM_TO_SHOW_ALL_OPTIONS /*&& mAppInstallTv.isFocused()*/) {
            mKeyCodeList.clear();
            return true;
        }
        return false;
    }
    private void switchDC() {
        final String dcType = KmSharedPreferences.getInstance().getString(KeyName.KEY_DATA_CENTER_URI_TYPE, SystemConfigManager.DC_TYPE_NORMAL);
        String content = dcType.equals(SystemConfigManager.DC_TYPE_NORMAL) ?
                BaseApplication.getInstance().getResources().getString(R.string.switch_test_dc_dialog_tx) :
                BaseApplication.getInstance().getResources().getString(R.string.switch_formal_dc_dialog_tx);
        final CommonDialog dialog = new CommonDialog(getContext());
        dialog.setTitle(-1);
        dialog.setContent(content);
        dialog.setButton(R.string.confirm, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String showTxt = "";
                if (dcType.equals(SystemConfigManager.DC_TYPE_NORMAL)) {
                    showTxt = getContext().getResources().getString(R.string.switch_test_dc_tx);
                    KmSharedPreferences.getInstance().putString(KeyName.KEY_DATA_CENTER_URI_TYPE, SystemConfigManager.DC_TYPE_TEST);
                } else {
                    showTxt = getContext().getResources().getString(R.string.switch_formal_dc_tx);
                    KmSharedPreferences.getInstance().putString(KeyName.KEY_DATA_CENTER_URI_TYPE, SystemConfigManager.DC_TYPE_NORMAL);
                }
                ToastUtil.showLongToast(BaseApplication.getInstance().getBaseContext(), showTxt);
            }
        }, R.string.cancel, null);
        dialog.setOkBtnBg(R.drawable.btn_red_bg);
        dialog.show();
    }

}
