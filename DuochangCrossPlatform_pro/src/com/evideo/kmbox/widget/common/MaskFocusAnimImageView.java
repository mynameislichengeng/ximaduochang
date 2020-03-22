/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date             Author             Version Description
 *  -----------------------------------------------
 *  2015年4月3日	     "wurongquan"      1.0		[修订说明]
 *
 */

package com.evideo.kmbox.widget.common;


import com.evideo.kmbox.SystemConfigManager;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * [带动画边框图片imageView]
 */
public class MaskFocusAnimImageView extends MaskFocusImageView implements AnimatorUpdateListener {

    private static final int ONE_STEP_DURATION = 200;
   
    private float mAnimationProgress = 0.0f;
    private AnimatorSet mExpandSet = null;
    private AnimatorSet mContractSet = null;
    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public MaskFocusAnimImageView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public MaskFocusAnimImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public MaskFocusAnimImageView(Context context) {
        super(context);
    }
 
    public interface IFocusChangeListener {
        public void focusChange(boolean focus);
    }
    private IFocusChangeListener mListener = null;
    public void setFocusChangeListener(IFocusChangeListener listener) {
        mListener = listener;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFocusChanged(boolean focused, int direction,
            Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (mListener!=null) {
            mListener.focusChange(focused);
        }
        
        if (focused) {
            startExpandAnim(this);
        } else {
            startContractAnim(this);
        }    
    }
 
    private  void startExpandAnim(View view) {

        if (mContractSet != null && mContractSet.isRunning()) {
            mContractSet.cancel();
        }
        
        if (mAnimationProgress <= 1.0f) {
            mAnimationProgress = 1.0f;
        }
        
        ValueAnimator zoomInAnimX = ObjectAnimator.ofFloat(view, "scaleX", mAnimationProgress, SystemConfigManager.MAX_EXPAND_SCALE);
        zoomInAnimX.setDuration(ONE_STEP_DURATION);
        zoomInAnimX.addUpdateListener(this);
        ValueAnimator zoomInAnimY = ObjectAnimator.ofFloat(view, "scaleY", mAnimationProgress, SystemConfigManager.MAX_EXPAND_SCALE);
        zoomInAnimY.setDuration(ONE_STEP_DURATION);  
        if (mExpandSet == null) {
            mExpandSet = new AnimatorSet();
        }
        
        bringToFront();
        requestLayout();
        invalidate();
        
        mExpandSet.play(zoomInAnimX).with(zoomInAnimY);      
        mExpandSet.start();        
    }
    
    private void startContractAnim(View view) {
        
        if (mAnimationProgress < 1.0f) {
            return;
        }
        
        if (mExpandSet != null && mExpandSet.isRunning()) {
            mExpandSet.cancel();
        }
       
        ValueAnimator zoomInAnimX = ObjectAnimator.ofFloat(view, "scaleX", mAnimationProgress, 1.0f);
        zoomInAnimX.setDuration(ONE_STEP_DURATION);
        zoomInAnimX.addUpdateListener(this);
        ValueAnimator zoomInAnimY = ObjectAnimator.ofFloat(view, "scaleY", mAnimationProgress, 1.0f);
        zoomInAnimY.setDuration(ONE_STEP_DURATION);      
        mContractSet = new AnimatorSet();
        mContractSet.play(zoomInAnimX).with(zoomInAnimY);      
        mContractSet.start();       
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mAnimationProgress = (Float) animation.getAnimatedValue();
    }
}
