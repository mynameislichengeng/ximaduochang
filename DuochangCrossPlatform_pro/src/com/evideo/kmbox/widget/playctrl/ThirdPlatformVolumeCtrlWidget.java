/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-12-28     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.playctrl;

import java.util.HashMap;

import com.evideo.kmbox.R;
import com.evideo.kmbox.activity.MainActivity.OnDialogKeyListener;
import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
import com.evideo.kmbox.model.umeng.EventConst;
import com.evideo.kmbox.model.umeng.PageName;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.playctrl.TimeOutHandler.TimeOutListener;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnFocusChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * [功能说明]第三方平台音量调节对话框
 */
public class ThirdPlatformVolumeCtrlWidget extends BaseVolumeCtrlWidget {
    
    private SeekBar mMusicSeekBar = null;
    private TextView mMusicCurrentValue = null;
    
    private int mMusicMax = -1;
    
    private int mFocusedColor;
    private int mNormalColor;
    
    private final int VOLUME_SHOW_TIMEOUT = 5000;

    private TimeOutHandler mtimeHandler;
    private final int VOLUME_HIDE_MESSAGE = 10;
    
    private OnDialogKeyListener mKeyListener = null;

    /**
     * @param context
     */
    public ThirdPlatformVolumeCtrlWidget(Context context) {
        super(context, R.style.VolumeCtrlWidgetDialogStyleOnThirdPlatform);
        setContentView(R.layout.dialog_volume_ctrl_third_platform);
        
        mFocusedColor = context.getResources().getColor(R.color.text_yellow);
        mNormalColor = context.getResources().getColor(R.color.text_blue);
        
        init(context);
        
        this.setOnKeyListener(new OnKeyListener() {
            
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                mtimeHandler.resend();
                switch(keyCode) {
                
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if ( event.getAction() == KeyEvent.ACTION_DOWN ) {
                         if (ThirdPlatformVolumeCtrlWidget.this.isShowing()) {
                             handleVolumeChangeEvent(true);
                             onUmengEventVolume(true);
                         }
                         return true;
                    }
                    break;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if ( event.getAction() == KeyEvent.ACTION_DOWN ) {
                        if (ThirdPlatformVolumeCtrlWidget.this.isShowing()) {
                            handleVolumeChangeEvent(false);
                            onUmengEventVolume(false);
                        }
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                     if ( mKeyListener != null ) {
                          mKeyListener.onDialogKeyListener(keyCode, event);
                          return true;
                      }
                     break;
                case KeyEvent.KEYCODE_F1:
                    ThirdPlatformVolumeCtrlWidget.this.dismiss();
                    if (mKeyListener != null) {
                        mKeyListener.onDialogKeyListener(keyCode, event);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if ( event.getAction() == KeyEvent.ACTION_DOWN ) {
                        handleLeftEvent();
                        onUmengEventVolume(false);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if ( event.getAction() == KeyEvent.ACTION_DOWN ) {
                        handleRightEvent();
                        onUmengEventVolume(true);
                        return true;
                    }
                    break;
                }
                return false;
            }
        });
        
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.0f;
        lp.y = lp.y - 50;
        getWindow().setAttributes(lp);
        
        mtimeHandler = new TimeOutHandler(VOLUME_SHOW_TIMEOUT, VOLUME_HIDE_MESSAGE);
        mtimeHandler.setListener(new TimeOutListener() {
            @Override
            public boolean onTimeOut() {
                ThirdPlatformVolumeCtrlWidget.this.dismiss();
                return false;
            }
        });
    }
    
    private void init(Context context) {
        mMusicSeekBar = (SeekBar) findViewById(R.id.dialog_volumebar_music_seekbar);
        mMusicCurrentValue = (TextView) findViewById(R.id.dialog_volumebar_music_current_value);
        mMusicSeekBar.setFocusable(true);
        mMusicSeekBar.setFocusableInTouchMode(true);
        
        mMusicSeekBar.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mMusicCurrentValue.setTextColor(mFocusedColor);
                }
            }
        });
    }
    
    private void onUmengEventVolume(boolean up) {
        if(mMusicSeekBar.isFocused()) {
            UmengAgentUtil.onEventVolumeAction(getContext(), EventConst.ID_CLICK_VOLUME_MUSIC, up);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void show() {
        
        AudioManager audioManager = (AudioManager) getContext()
                .getSystemService(Context.AUDIO_SERVICE);
        int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        setMusicCurrentVolume(vol);
        
        super.show();
        mtimeHandler.resend();
        LogAnalyzeManager.getInstance().onPageStart(PageName.VOLUME_SETTING_DIALOG);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dismiss() {
        super.dismiss();
        mtimeHandler.clear();
        
        HashMap<String, String> m = new HashMap<String, String>();
        m.put(EventConst.K_VOLUME_MUSIC, getCurrentMusicVolume() + "");
//        m.put(EventConst.K_VOLUME_MIC, getCurrentMicVolume() + "");
//        m.put(EventConst.K_VOLUME_EFFECT, getCurrentEffectVolume() + "");
        //UmengAgent.onEvent(getContext(), EventConst.ID_VOLUME_PARAMS, m);
        LogAnalyzeManager.onEvent(getContext(), EventConst.ID_VOLUME_PARAMS, m);
        //UmengAgent.getInstance().onPageStart(PageName.MAIN_ACTIVITY);
        LogAnalyzeManager.getInstance().onPageStart(PageName.MAIN_ACTIVITY);
    }
    
    private int getCurrentMusicVolume() {
        AudioManager audioManager = (AudioManager) getContext()
                .getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDialogKeyListener(OnDialogKeyListener listener) {
        mKeyListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleVolumeChangeEvent(boolean up) {
        mtimeHandler.resend();
        
        if ( up )
            handleRightEvent();
        else
            handleLeftEvent();
    }
    
    private void handleLeftEvent() {
        if (mMusicSeekBar.isFocused()) {
             AudioManager audioManager = (AudioManager) getContext()
                     .getSystemService(Context.AUDIO_SERVICE);
             int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

             if (vol > 0) {
                 vol--;
                 setMusicCurrentVolume(vol);
             }
        }
    }
    
    private void handleRightEvent() {
        if (mMusicSeekBar.isFocused()) {
             AudioManager audioManager = (AudioManager) getContext()
                     .getSystemService(Context.AUDIO_SERVICE);
             int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
             int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
             
             if (vol < maxVol) {
                 vol++;
                 this.setMusicCurrentVolume(vol);
             }
                 
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMusicCurrentVolume(int value) {
        mMusicSeekBar.setProgress(value);
        mMusicCurrentValue.setText(String.valueOf(value));
        
        AudioManager audioManager = (AudioManager) getContext()
                .getSystemService(Context.AUDIO_SERVICE);
        int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        
        if ( vol != value ) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMusicMaxVolume(int max) {
        if (mMusicMax == -1) {
            mMusicSeekBar.setMax(max);
//            initMusicListener();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMicrophoneCurrentVolume(int value) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMicrophoneMaxVolume(int max) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEffectCurrentVol(int value) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEffectVolRange(int min, int max) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus(int index) {
        if ( index == 0 ) {
            if ( !mMusicSeekBar.isFocused() ) {
                 EvLog.d(" mMusicSeekBar requestFocus");
                 mMusicSeekBar.requestFocus();
                 if ( !mMusicSeekBar.isFocused() ) {
                     EvLog.e(" mMusicSeekBar requestFocus failed");
                 }
            } else {
                EvLog.d(" mMusicSeekBar already focused");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFocusIndex() {
        if ( mMusicSeekBar.isFocused() )
            return 0;
        else 
            return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVolumeCtrlListener(OnVolumeCtrlChangedListener listener) {
    }

}
