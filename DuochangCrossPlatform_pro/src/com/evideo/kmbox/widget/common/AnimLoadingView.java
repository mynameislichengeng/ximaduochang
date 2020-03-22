/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年9月1日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.common;

import com.evideo.kmbox.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * [功能说明]
 */
public class AnimLoadingView extends LinearLayout {
    private Context mContext;
//    private ImageView loadingIv;
    private ProgressBar mProgressBar;
    private TextView loadingTv;
 
    public AnimLoadingView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }
 
    public AnimLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }
 
    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.loading_view_layout, null);
//        loadingIv = (ImageView) view.findViewById(R.id.loadingIv);
        mProgressBar = (ProgressBar)view.findViewById(R.id.loading_pb);
        loadingTv = (TextView) view.findViewById(R.id.loadingTv);
        addView(view);
    }
 
    public void setLoadingTxt(int resid) {
        loadingTv.setText(resid);
    }
    
    public void startAnim() {
        /*loadingIv.setBackgroundResource(R.drawable.loading_anim);
        AnimationDrawable animationDrawable = (AnimationDrawable) loadingIv.getBackground();
        if (animationDrawable != null) {
            animationDrawable.start();
        }*/
        if (mProgressBar.getVisibility() != View.VISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }
    
    public void stopAnim() {
       /* AnimationDrawable animationDrawable = (AnimationDrawable) loadingIv.getBackground();
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
        loadingIv.setBackgroundResource(0);*/
        if (mProgressBar.getVisibility() != View.GONE) {
            mProgressBar.setVisibility(View.GONE);
        }
    }
    
   /* public ImageView getLoadingIv() {
        return loadingIv;
    }*/
 
    public TextView getLoadingTv() {
        return loadingTv;
    }
    
    public void showLoadFail(String txt) {
        stopAnim();
//        loadingIv.setBackgroundResource(0);
        loadingTv.setText(txt);
    }
    public void showLoadFail(int txtId) {
        stopAnim();
//        loadingIv.setBackgroundResource(0);
        loadingTv.setText(txtId);
    }
}
