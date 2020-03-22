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
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.util.BitmapUtil;

/**
 * [功能说明]
 */
public class SingleHelpBtn extends FrameLayout {

    private Button mHelpBtn = null;
    private ImageView mHelpImg = null;
    private TextView mHelpTx = null;
    public SingleHelpBtn(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public SingleHelpBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SingleHelpBtn(Context context) {
        super(context);
        initView(context);
    }
    
    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.widget_single_help_btn, this, true);
        mHelpBtn = (Button)findViewById(R.id.help_btn);
        mHelpImg = (ImageView)findViewById(R.id.help_icon);
        mHelpTx = (TextView)findViewById(R.id.help_tx);
    }
    
    public Button getBtn() {
        return mHelpBtn;
    }
    
    public void setHelpBtnBg(int resid) {
        mHelpBtn.setBackgroundResource(resid);
    }
    
    public void setHelpIcon(int resid) {
        Bitmap bmp = BitmapUtil.getBitmapByResId(BaseApplication.getInstance(), resid);
        if (bmp != null) {
            mHelpImg.setImageBitmap(bmp);
        }
    }
    
    public void setHelpTxt(String txt) {
        mHelpTx.setText(txt);
    }
}
