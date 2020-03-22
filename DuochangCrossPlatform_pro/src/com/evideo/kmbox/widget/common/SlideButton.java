/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-4-15     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evideo.kmbox.R;

/**
 * [带提示文字的滑动开关控件]
 */
public class SlideButton extends LinearLayout {
    
    private View mContentView;
    private TextView mDescTv = null;
    private SlideSwitch mSlideSwitch = null;
    private OnSwitchChangeListener mSwitchChangeListener = null;
    private int mSlideBtnTextSize = 0;

    public SlideButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public SlideButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SlideButton(Context context) {
        super(context);
        initView(context);
    }
    
    private void initView(Context context) {
        mContentView = View.inflate(context, R.layout.slide_button_lay, this);
        mSlideBtnTextSize = getResources().getDimensionPixelSize(R.dimen.px45);
        
        this.setFocusable(true);
        this.setClickable(true);
        this.setBackgroundResource(R.drawable.common_focused_selector);
        
        mSlideSwitch =  (SlideSwitch)mContentView.findViewById(R.id.slide_btn);
        mSlideSwitch.setTextSize(mSlideBtnTextSize);
        
        mDescTv = (TextView) mContentView.findViewById(R.id.desc_slide);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mSwitchChangeListener != null ) {
                    mSwitchChangeListener.onChange();
                }
            }
        });
    }
    
    public void setSwitchStatus(boolean open,boolean anim) {
        if ( mSlideSwitch != null ) {
            if ( !anim ) {
                mSlideSwitch.setStatus(open);
            } else {
                mSlideSwitch.changeStatus(open);
            }
        }
    }
    
    public void setDescription(String desc) {
        if (mDescTv != null) {
            mDescTv.setText(desc);
        }
    }
    
    public void setDescriptionSize(float size) {
        if (mDescTv != null) {
            mDescTv.setTextSize(size);
        }
    }
    public void setDescription(int resid) {
        if(resid > 0) {
            mDescTv.setText(resid);
        }
    }
    
    public void setDescColor(int color) {
        if (mDescTv != null) {
            mDescTv.setTextColor(color);
        }
    }
    
    public void setOnSwitchChangeListener(OnSwitchChangeListener onChangeListener) {
        mSwitchChangeListener = onChangeListener;
    }
    
    public interface OnSwitchChangeListener {
        public void onChange();
    }
}
