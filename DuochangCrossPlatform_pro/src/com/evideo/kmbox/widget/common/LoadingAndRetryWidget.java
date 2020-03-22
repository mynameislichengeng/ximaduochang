/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年9月4日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.common;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.evideo.kmbox.R;

/**
 * [功能说明]
 */
public class LoadingAndRetryWidget extends FrameLayout implements View.OnClickListener{
    
    private LinearLayout mLoadingLayout = null;
    private LinearLayout mRetryLayout = null;
    private MaskFocusButton mRetryBtn = null;
//    private ImageView mLoadingIv = null;
    
    public LoadingAndRetryWidget(Context context) {
        super(context);
        initView(context);
    }
    
    public LoadingAndRetryWidget(Context context, AttributeSet attrs,int defStyle) {
        super(context,attrs,defStyle);
        initView(context);
    }
    
    public LoadingAndRetryWidget(Context context, AttributeSet attrs) {
        super(context,attrs);
        initView(context);
    }
    
    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.widget_loading_and_retry_layout, this, true);
        mLoadingLayout = (LinearLayout)findViewById(R.id.data_loading_layout);
        mRetryLayout = (LinearLayout)findViewById(R.id.data_retry_layout);
        mRetryBtn = (MaskFocusButton)findViewById(R.id.retry_btn);
        mRetryBtn.setOnClickListener(this);
//        mLoadingIv = (ImageView)findViewById(R.id.loadingIv);
    }
    
    public void showLoading() {
        if (mLoadingLayout.getVisibility() != View.VISIBLE) {
            mLoadingLayout.setVisibility(View.VISIBLE);
        }
       /* AnimationDrawable animationDrawable = (AnimationDrawable) mLoadingIv.getBackground();
        if (animationDrawable != null)
            animationDrawable.start();*/
        
        if (mRetryLayout.getVisibility() != View.GONE) {
            mRetryLayout.setVisibility(View.GONE);
        }
    }
    
    public void showRetry() {
        if (mLoadingLayout.getVisibility() != View.GONE) {
           /* AnimationDrawable animationDrawable = (AnimationDrawable) mLoadingIv.getBackground();
            if (animationDrawable != null)
                animationDrawable.stop();*/
            mLoadingLayout.setVisibility(View.GONE);
        }
        if (mRetryLayout.getVisibility() != View.VISIBLE) {
            mRetryLayout.setVisibility(View.VISIBLE);
        }
    }
    
    public int getRetryBtnId() {
        return mRetryBtn.getId();
    }

    public interface IClickRetryBtnListener {
        public void onClickRetryBtn();
    }
    private IClickRetryBtnListener mListener = null;
    public void setRetryCallback(IClickRetryBtnListener listener) {
        mListener = listener;
    }
    
    public void hide() {
     /*   AnimationDrawable animationDrawable = (AnimationDrawable) mLoadingIv.getBackground();
        if (animationDrawable != null)
            animationDrawable.stop();*/
        this.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onClickRetryBtn();
        }
    }
}