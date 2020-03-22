/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-11-13     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview;

import com.evideo.kmbox.R;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * [功能说明]
 */
public class BreadCrumbsWidget  extends LinearLayout {

    private TextView mFirstTv = null;
    private TextView mSecondTv = null;
    private ImageView mLinkIv = null;
    
    public BreadCrumbsWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public BreadCrumbsWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BreadCrumbsWidget(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.breadcrumbs_widget_layout, this);
        mFirstTv = (TextView) findViewById(R.id.crumbs_first_tv);
        mSecondTv = (TextView) findViewById(R.id.crumbs_second_tv);
        mLinkIv  = (ImageView) findViewById(R.id.crumbs_link_iv);
    }
    
    public void setFirstTitle(String text) {
        mFirstTv.setText(text);
    }
    
    public void setSecondTitle(String text) {
        mSecondTv.setText(text);
        if (!TextUtils.isEmpty(text)) {
            mLinkIv.setVisibility(View.VISIBLE);
        }
    }
}
