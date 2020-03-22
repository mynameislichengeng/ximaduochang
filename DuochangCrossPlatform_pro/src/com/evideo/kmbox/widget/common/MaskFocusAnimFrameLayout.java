/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date             Author             Version  Description
 *  -----------------------------------------------
 *  2015-7-2         "zhaoyunlong"        1.0     [修订说明]
 *
 */

package com.evideo.kmbox.widget.common;

import com.evideo.kmbox.util.EvLog;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * [功能说明]
 */
public class MaskFocusAnimFrameLayout extends MaskFocusFrameLayout implements AnimatorUpdateListener{

    private static final int ONE_STEP_DURATION = 200;
    private static final float MAX_EXPAND_SCALE = 1.1f;
    private float mAnimationProgress = 0.0f;
    private AnimatorSet mExpandSet = null;
    private AnimatorSet mContractSet = null;
    
    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public MaskFocusAnimFrameLayout(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    public MaskFocusAnimFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaskFocusAnimFrameLayout(Context context) {
        super(context);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFocusChanged(boolean focused, int direction,
            Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            startExpandAnim(this);            
        } else {
            startContractAnim(this);
        }      
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dispatchSetSelected(boolean selected) {
        super.dispatchSetSelected(selected);
        EvLog.d("MaskFocusAnimFrameLayout dispatchSetSelected " + selected);
        if (selected) {
            startExpandAnim(this); 
        } else {
            startContractAnim(this);
        }
    }
        
    public  void startExpandAnim(View view) {

        if (mContractSet != null && mContractSet.isRunning()) {
            mContractSet.cancel();
        }
        
        if (mAnimationProgress <= 1.0f) {
            mAnimationProgress = 1.0f;
        }
        
        ValueAnimator zoomInAnimX = ObjectAnimator.ofFloat(view, "scaleX", mAnimationProgress, MAX_EXPAND_SCALE);
        zoomInAnimX.setDuration(ONE_STEP_DURATION);
        zoomInAnimX.addUpdateListener(this);
        ValueAnimator zoomInAnimY = ObjectAnimator.ofFloat(view, "scaleY", mAnimationProgress, MAX_EXPAND_SCALE);
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
    
    public void startContractAnim(View view) {
        
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
        if (mContractSet == null) {
            mContractSet = new AnimatorSet();
        }
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
