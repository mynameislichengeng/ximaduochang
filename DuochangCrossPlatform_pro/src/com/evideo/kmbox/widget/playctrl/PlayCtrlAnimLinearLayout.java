/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-9-28     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.playctrl;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.evideo.kmbox.widget.common.MaskFocusLinearLayout;
import com.evideo.kmbox.R;

/**
 * [功能说明]播控按钮布局
 */
public class PlayCtrlAnimLinearLayout extends MaskFocusLinearLayout implements AnimatorUpdateListener {
  
    private static final int ONE_STEP_DURATION = 200;
    private static final float MIN_CONTRACT_SCALE = 0.8f;
    private float mAnimationProgress = 1.0f;
    private AnimatorSet mExpandSet = null;
    private AnimatorSet mContractSet = null;
    
    private ImageView mImageView;
    private TextView mTextView;
    
    /**
     * @param context Context
     * @param attrs AttributeSet
     * @param defStyle defaultStyle
     */
    public PlayCtrlAnimLinearLayout(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PlayCtrlAnimLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
  
    public PlayCtrlAnimLinearLayout(Context context) {
        super(context);
        init();
    }  
    /**
     * [初始化]
     */
    private void init() {
        
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.play_ctrl_item, this, true);
        
        mImageView = (ImageView) findViewById(R.id.play_ctrl_item_iv);
        mTextView = (TextView) findViewById(R.id.play_ctrl_item_tv);
        
        startContractAnim(this);
    }
    
    public void setText(String text) {
        if (mTextView != null && !TextUtils.isEmpty(text)) {
            mTextView.setText(text);
        }
    }
    
    public void setText(int resId) {
        if (mTextView != null && resId > 0) {
            mTextView.setText(resId);
        }
    }
    
    public void setImageResource(int resId) {
        if (resId <= 0) {
            return;
        }
        if (mImageView != null) {
            mImageView.setImageResource(resId);
        }
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
