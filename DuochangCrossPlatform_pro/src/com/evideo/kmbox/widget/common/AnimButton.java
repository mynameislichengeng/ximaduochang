/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年5月7日     "wurongquan"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.common;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * [带缩放动画按钮]
 */
public class AnimButton extends Button implements AnimatorUpdateListener {
  
    private static final int ONE_STEP_DURATION = 200;
    private static final float MIN_CONTRACT_SCALE = 0.8f;
    private float mAnimationProgress = 1.0f;
    private AnimatorSet mExpandSet = null;
    private AnimatorSet mContractSet = null;
    /**
     * @param context Context
     * @param attrs AttributeSet
     * @param defStyle defaultStyle
     */
    public AnimButton(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public AnimButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
  
    public AnimButton(Context context) {
        super(context);
        init();
    }  
    /**
     * [初始化]
     */
    private void init() {
        startContractAnim(this);
    }
    
    /**
    * {@inheritDoc}
    */
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if  (focused) {
            startExpandAnim(this);
        } else {
            startContractAnim(this);
        }
    }
    
    private  void startExpandAnim(View view) {

        if (mContractSet != null && mContractSet.isRunning()) {
            mContractSet.cancel();
        }

        if (mAnimationProgress <= MIN_CONTRACT_SCALE) {
            mAnimationProgress = MIN_CONTRACT_SCALE;
        }
        
        ValueAnimator zoomInAnimX = ObjectAnimator.ofFloat(view, "scaleX", mAnimationProgress, 1.0f);
        zoomInAnimX.setDuration(ONE_STEP_DURATION);
        zoomInAnimX.addUpdateListener(this);
        ValueAnimator zoomInAnimY = ObjectAnimator.ofFloat(view, "scaleY", mAnimationProgress, 1.0f);
        zoomInAnimY.setDuration(ONE_STEP_DURATION);
        if (mExpandSet == null) {
            mExpandSet = new AnimatorSet();
        }
        mExpandSet.play(zoomInAnimX).with(zoomInAnimY);
        mExpandSet.start();
    }

    private void startContractAnim(View view) {

        if (mAnimationProgress > 1.0f || mAnimationProgress < MIN_CONTRACT_SCALE) {
            return;
        }

        if (mExpandSet != null && mExpandSet.isRunning()) {
            mExpandSet.cancel();
        }

        ValueAnimator zoomInAnimX = ObjectAnimator.ofFloat(view, "scaleX", mAnimationProgress, MIN_CONTRACT_SCALE);
        zoomInAnimX.setDuration(ONE_STEP_DURATION);
        zoomInAnimX.addUpdateListener(this);
        ValueAnimator zoomInAnimY = ObjectAnimator.ofFloat(view, "scaleY", mAnimationProgress, MIN_CONTRACT_SCALE);
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
  
//    private Paint mPaint = new Paint();
//    private Matrix mMatrix = new Matrix();;
//    private Shader mShader = new LinearGradient(0, 0, 0, 1, 0xFF000000, 0, Shader.TileMode.CLAMP);
//
//    /**
//    * {@inheritDoc}
//    *
//    */
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        //手动实现fadingEdge效果
//        mPaint.setShader(mShader);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
//        mMatrix.setScale(1, 100);
//        mMatrix.postRotate(-90);
//        mMatrix.postTranslate(0, 0);
//        mShader.setLocalMatrix(mMatrix);
//        canvas.drawRect(0, 0, 100, canvas.getHeight(), mPaint);
//    }
}
