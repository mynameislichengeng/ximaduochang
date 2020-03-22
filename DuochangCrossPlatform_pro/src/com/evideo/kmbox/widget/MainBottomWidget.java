/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年11月24日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.SystemConfigManager;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.observer.net.INetworkInfoObserver;
import com.evideo.kmbox.model.observer.net.IWifiInfoObserver;
import com.evideo.kmbox.model.observer.net.NetworkInfoSubject;
import com.evideo.kmbox.model.observer.net.WifiInfoSubject;
import com.evideo.kmbox.util.BitmapUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.NetUtils;

/**
 * [功能说明]
 */
public class MainBottomWidget extends RelativeLayout implements INetworkInfoObserver, IWifiInfoObserver {

    /** [消息滚动间隔时间] */
    private static final int TIMEOUT_MILLISECONDS = 5 * 1000;
    private ImageView  mSmallMvFrame;
    // 当前正在播放
    private TextView mHomePageCurrentSongTv;
    // 底部提示
    private ButtomMsgView mHomePageMsgView;
    /** [用来postDelay的handler] */
    private Handler mHandler;
    /** [执行滚动消息的runnable] */
    private Runnable mRunnable;
    
    private View mFocusedView;
    
    private ImageView mBottomRightIv = null;
    
    /**
     * @param context
     */
    public MainBottomWidget(Context context) {
        super(context);
    }

    public MainBottomWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public void hideSmallMv() {
        if (mSmallMvFrame != null && mSmallMvFrame.getVisibility() != View.GONE) {
            mSmallMvFrame.setVisibility(View.GONE);
        }
        if (mHomePageCurrentSongTv != null && mHomePageCurrentSongTv.getVisibility() != View.GONE) {
            mHomePageCurrentSongTv.setVisibility(View.GONE);
        }
    }
    
    public void showSmallMvFrame() {
        if (mSmallMvFrame != null && mSmallMvFrame.getVisibility() != View.VISIBLE) {
            mSmallMvFrame.setVisibility(View.VISIBLE);
        }
        if (mHomePageCurrentSongTv != null && mHomePageCurrentSongTv.getVisibility() != View.VISIBLE) {
            mHomePageCurrentSongTv.setVisibility(View.VISIBLE);
        }
    }
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_main_bottom_layout, this);
        mSmallMvFrame = (ImageView) findViewById(R.id.small_mv_frame);
        mSmallMvFrame.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    mSmallMvFrame.setBackgroundResource(R.drawable.small_mv_bg_focus);
                } else {
                    mSmallMvFrame.setBackground(null);
                }
            }
        });
        mSmallMvFrame.setNextFocusRightId(R.id.small_mv_frame);
        mSmallMvFrame.setNextFocusDownId(R.id.small_mv_frame);
        mSmallMvFrame.setNextFocusLeftId(R.id.small_mv_frame);
        
        mHomePageCurrentSongTv = (TextView) findViewById(R.id.home_page_current_song_tv);
        
        if (!SystemConfigManager.SHOW_SMALL_MV) {
            hideSmallMv();
        }
        mHomePageMsgView = (ButtomMsgView) findViewById(R.id.home_page_bottom_msg_view);
        
        mBottomRightIv = (ImageView) findViewById(R.id.home_page_liantong_qr);
        
        if (DeviceConfigManager.getInstance().isThirdApp()) {
            mHomePageMsgView.setVisibility(View.GONE);
//            mBottomRightIv.setVisibility(View.VISIBLE);
        } else {
            mHomePageMsgView.getNormalTipView().setText(/*DeviceInfoUtils.isBesTvLittleRed() 
                  ? R.string.main_view_bottom_tip_bestv_little_red :*/ R.string.main_view_bottom_tip);
            mHandler = new Handler();
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mHomePageMsgView != null) {
                        if (mHomePageMsgView.flip()) {
                            mHandler.removeCallbacks(mRunnable);
                            return;
                        };
                    }
                    if (mHandler != null) {
                        mHandler.postDelayed(this, TIMEOUT_MILLISECONDS);
                    }
                }
            };
            updateBottomMsgView();
        }
        
    }
    
    private Bitmap mPrevRightBottomBmp = null;
    public void updateBottomRightIcon(String imgPath) {
        if (TextUtils.isEmpty(imgPath)) {
            return;
        }
        if (mBottomRightIv == null) {
            return;
        }
        Bitmap bmp = BitmapUtil.getBmpByPath(imgPath);
        if (bmp == null) {
            return;
        }
        
        if (mBottomRightIv.getVisibility() != View.VISIBLE) {
            mBottomRightIv.setVisibility(View.VISIBLE);
        }
        
        mBottomRightIv.setImageBitmap(bmp);
        if (mPrevRightBottomBmp != null && !mPrevRightBottomBmp.isRecycled()) {
            mPrevRightBottomBmp.recycle();
            mPrevRightBottomBmp = null;
        }
        mPrevRightBottomBmp = bmp;
    }
    
    public ImageView getMvFrame() {
        return mSmallMvFrame;
    }
    
    public void updateSongTv(String text) {
        mHomePageCurrentSongTv.setText(text);
    }
    
    public String getSongTvText() {
        return mHomePageCurrentSongTv.getText().toString();
    }
   /* public void showNoSong() {
        mSmallMvFrame.setBackgroundResource(R.drawable.small_no_mv);
    }*/
    
   /* public void hideNoSong() {
        mSmallMvFrame.setBackground(null);
    }*/
    
   /* public boolean isNoSongShow() {
        return mSmallMvFrame.getBackground() != null;
    }*/
    
    public void saveFocusedView() {
        if (hasFocus()) {
            mFocusedView = findFocus();
        } else {
            mFocusedView = null;
        }
    }
    
    public boolean restoreFocusedView() {
        if (mFocusedView != null) {
            mFocusedView.requestFocus();
            return true;
        } else {
            return false;
        }
    }
    
    
    public int getTVFrameMarginTop() {
        FrameLayout.LayoutParams param = (FrameLayout.LayoutParams)this.getLayoutParams();
        int top = param.topMargin;
        //边框顶部透明部分
        top += getResources().getDimensionPixelSize(R.dimen.px18);
        return top;
    }
    
    public int getTVFrameMarginLeft() {
        FrameLayout.LayoutParams param = (FrameLayout.LayoutParams)this.getLayoutParams();
        int left = param.leftMargin;
        //边框左侧透明部分
        left += getResources().getDimensionPixelSize(R.dimen.px24);
        return left;
    }
    
    public int getTVFrameWidth() {
        return getResources().getDimensionPixelSize(R.dimen.px255);
    }
    
    public int getTVFrameHeight() {
        return getResources().getDimensionPixelSize(R.dimen.px150);
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NetworkInfoSubject.getInstance().registNetworkInfoObserver(this);
        WifiInfoSubject.getInstance().registWifiInfoObserver(this);
        updateBottomMsgView();
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NetworkInfoSubject.getInstance().unregistNetworkInfoObserver(this);
        WifiInfoSubject.getInstance().unregistWifiInfoObserver(this);
    }

    @Override
    public void onNetworkChanged(boolean isConnected) {
        updateBottomMsgView();
    }

    @Override
    public void onWifiStateChange(boolean isConnected) {
        updateBottomMsgView();
    }

    @Override
    public void onWifiRssiChange() {
        // TODO Auto-generated method stub
    }
    
    private void updateBottomMsgView() {
        if (DeviceConfigManager.getInstance().isThirdApp()) {
            return;
        }
        
        boolean isConnected = NetUtils.isNetworkConnected(BaseApplication.getInstance());
        if (isConnected) {
            if (mRunnable != null && mHandler != null) {
                mHomePageMsgView.setOnLineFlag(true);
            }
        } else {
            EvLog.i("main bottom widget", "observed network disconnected");
            if (mRunnable != null && mHandler != null) {
                mHomePageMsgView.setOnLineFlag(false);
                mHandler.removeCallbacks(mRunnable);
                mHandler.post(mRunnable);
            }
        }
    }
}
