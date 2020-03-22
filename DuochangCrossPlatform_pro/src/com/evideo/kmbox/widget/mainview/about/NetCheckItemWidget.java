/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年8月4日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.about;

import pl.droidsonroids.gif.GifImageView;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.util.BitmapUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * [功能说明]
 */
public class NetCheckItemWidget extends LinearLayout {

    private GifImageView mGifIv = null;
    private TextView mTitleTv = null;
    private TextView mResultTv = null;
    
    public NetCheckItemWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public NetCheckItemWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public NetCheckItemWidget(Context context) {
        super(context);
        initView(context);
    }
    
    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_net_check_widget, this, true);
        mGifIv = (GifImageView)findViewById(R.id.tip_icon);
        Bitmap bmp = BitmapUtil.getBitmapByResId(BaseApplication.getInstance(), R.drawable.network_check_wait);
        mGifIv.setImageBitmap(bmp);
        mTitleTv = (TextView)findViewById(R.id.network_check_title);
        mResultTv = (TextView)findViewById(R.id.network_check_result);
    }
    
    public void setTitle(String txt) {
        if (mTitleTv != null) {
            mTitleTv.setText(txt);
        }
    }
    
    public void setTitleColor(int color) {
        if (mTitleTv != null) {
            mTitleTv.setTextColor(color);
        }
    }

    public void setResult(String txt) {
        if (mResultTv != null) {
            mResultTv.setText(txt);
        }
    }
    
    public void setResultColor(int colorId) {
        if (mResultTv != null) {
            mResultTv.setTextColor(colorId);
        }
    }
    public void setGifResId(int resId) {
        if (mGifIv != null) {
            mGifIv.setImageResource(resId);
        }
    }
    
}
