/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年8月2日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.about;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;
import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.player.KmVideoPlayerType;
import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.BaseDialog;
import com.evideo.kmbox.widget.common.MaskFocusButton;
import com.evideo.kmbox.widget.common.ToastUtil;

/**
 * [功能说明]
 */
public class DecodeSetDialog  extends BaseDialog implements View.OnClickListener{
    private RadioButton mUseVLCDecodeBtn = null;
    private RadioButton mUseMediaPlayerDecodeBtn = null;
    private MaskFocusButton mSureBtn = null;
    
    public DecodeSetDialog(Context context) {
        super(context, R.style.ActivityDialogStyle);
        setContentView(R.layout.dialog_help_decode_setting);
        init();
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void show() {
        int mediaId = KmSharedPreferences.getInstance().getInt(KeyName.KEY_USE_VLC_DECODE, KmVideoPlayerType.VLC);
        EvLog.i("mediaId:" + mediaId);
        if (mediaId == KmVideoPlayerType.VLC) {
            mUseVLCDecodeBtn.setChecked(true);
            mUseVLCDecodeBtn.requestFocus();
            mUseMediaPlayerDecodeBtn.setChecked(false);
        } else {
            mUseVLCDecodeBtn.setChecked(false);
            mUseMediaPlayerDecodeBtn.setChecked(true);
            mUseMediaPlayerDecodeBtn.requestFocus();
        }
        super.show();
    }
    
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.use_vlc_decode_btn) {
            EvLog.i("mOpenSoftDecodeBtn click: use_vlc_decode_btn");
            mUseVLCDecodeBtn.setChecked(true);
            mUseMediaPlayerDecodeBtn.setChecked(false);
        } else if (v.getId() == R.id.use_mediaplayer_decode_btn) {
            EvLog.i("mCloseSoftDecodeBtn click: use_mediaplayer_decode_btn");
            mUseMediaPlayerDecodeBtn.setChecked(true);
            mUseVLCDecodeBtn.setChecked(false);
        } else if (v.getId() == R.id.soft_decode_commit_btn) {
            if (mUseVLCDecodeBtn.isChecked()) {
                KmSharedPreferences.getInstance().putInt(KeyName.KEY_USE_VLC_DECODE, KmVideoPlayerType.VLC);
            } else {
                KmSharedPreferences.getInstance().putInt(KeyName.KEY_USE_VLC_DECODE, KmVideoPlayerType.MEDIAPLAYER);
            }
            ToastUtil.showLongToast(BaseApplication.getInstance(), BaseApplication.getInstance().getResources().getString(R.string.soft_decode_commit));
        }
    }
    
    private void init() {
        mUseVLCDecodeBtn = (RadioButton)findViewById(R.id.use_vlc_decode_btn);
        mUseVLCDecodeBtn.setChecked(false);
        mUseVLCDecodeBtn.setOnClickListener(this);
        
        mUseMediaPlayerDecodeBtn = (RadioButton)findViewById(R.id.use_mediaplayer_decode_btn);
        mUseMediaPlayerDecodeBtn.setChecked(false);
        mUseMediaPlayerDecodeBtn.setOnClickListener(this);
        
        mSureBtn = (MaskFocusButton)findViewById(R.id.soft_decode_commit_btn);
        mSureBtn.setOnClickListener(this);
    }
}
