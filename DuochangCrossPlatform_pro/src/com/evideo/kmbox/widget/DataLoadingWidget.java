/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年7月1日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evideo.kmbox.R;

/**
 * [功能说明] 数据加载控件
 */
public class DataLoadingWidget extends FrameLayout{
    
    private LinearLayout mLoadingLayout = null;
    private TextView mLoadFailTv = null;
    private TextView mLoadingTv = null;
    
    
    public DataLoadingWidget(Context context) {
        super(context);
        initView(context);
    }
    
    public DataLoadingWidget(Context context, AttributeSet attrs,int defStyle) {
        super(context,attrs,defStyle);
        initView(context);
    }
    
    public DataLoadingWidget(Context context, AttributeSet attrs) {
        super(context,attrs);
        initView(context);
    }
    
    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.widget_data_loading_layout, this, true);
        mLoadingLayout = (LinearLayout)findViewById(R.id.data_loading_layout);
        mLoadingTv = (TextView)findViewById(R.id.data_loading_tv);
        mLoadFailTv = (TextView)findViewById(R.id.data_load_error_tv);
    }
    
    public void showLoading() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadFailTv.setVisibility(View.GONE);
    }
    
    public void showLoadingWithoutText() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingTv.setVisibility(View.GONE);
        mLoadFailTv.setVisibility(View.GONE);
    }
    
    public void showLoadedFailed() {
        mLoadingLayout.setVisibility(View.GONE);
        mLoadFailTv.setVisibility(View.VISIBLE);
    }
    
    public void showLoadedFailed(String text) {
        mLoadingLayout.setVisibility(View.GONE);
        mLoadFailTv.setText(text);
        mLoadFailTv.setVisibility(View.VISIBLE);
    }

    public void hide() {
        this.setVisibility(View.GONE);
    }
}
