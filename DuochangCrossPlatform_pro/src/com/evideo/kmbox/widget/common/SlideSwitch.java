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

import com.evideo.kmbox.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

/**
 * [滑动开关]
 */
public class SlideSwitch extends View {
    private Bitmap mSwitchOff = null;
    private Bitmap mSwitchOn = null;
    private Bitmap mSwitchThumb = null;
    private int mBmpWidth = 0;  
    private int mBmpHeight = 0;  
    private int mThumbWidth = 0;  
    
    //用于显示的文本  
    private String mOnText = null;  
    private String mOffText = null;
    
    private int mDstX = 0;  
    private int mSwitchStatus = SWITCH_OFF;  
    
    /** [关闭状态  ] */
    public static final int SWITCH_OFF = 0; 
    /** [打开状态 ] */
    public static final int SWITCH_ON = 1; 
    /** [滚动状态 ] */
    public static final int SWITCH_SCROLING = 2; 
    
    private static final int STEP_TO_RIGHT = 3;
    private static final int STEP_TO_LEFT = -3;
    private static final int ANIMATION_X_LEFT = 12;
    private static final int ANIMATION_X_RIGHT = 72;
    private static final int ANIMATION_SLEEP_INTERVAL = 10;
    
    private static final float TEXT_DEFAULT_SIZE = 14;
    
    private float mTextSize = TEXT_DEFAULT_SIZE;
    private float mTextMarginTop = 0;    
    private float mOnTextMarginLeft = 0;
    private float mOffTextMarginLeft = 0;
    
    private static final int OFF_TEXT_COLOR = Color.rgb(105, 105, 105);
    
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
    
    public SlideSwitch(Context context) {
        this(context, null);
    }
    
    public SlideSwitch(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init();  
    }  
  
    public SlideSwitch(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        init();  
    }  
    
  //初始化三幅图片  
    private void init() {  
        Resources res = getResources();  
        mSwitchOff = BitmapFactory.decodeResource(res, R.drawable.switch_off);  
        mSwitchOn = BitmapFactory.decodeResource(res, R.drawable.switch_on);  
        mSwitchThumb = BitmapFactory.decodeResource(res, R.drawable.switch_thumb);  
        mBmpWidth = mSwitchOn.getWidth();  
        mBmpHeight = mSwitchOn.getHeight();  
        mThumbWidth = mSwitchThumb.getWidth(); 
        mTextMarginTop = res.getDimensionPixelSize(R.dimen.px72);
        mOnTextMarginLeft = res.getDimensionPixelSize(R.dimen.px45);
        mOffTextMarginLeft = res.getDimensionPixelSize(R.dimen.px9);
        mOnText = res.getString(R.string.settings_debugopt_slide_btn_on);
        mOffText = res.getString(R.string.settings_debugopt_slide_btn_off);
    }  
    
    @Override  
    public void setLayoutParams(LayoutParams params) {  
        params.width = mBmpWidth;  
        params.height = mBmpHeight;  
        super.setLayoutParams(params);  
    }  
    /** 
     * 设置开关上面的文本 
     * @param onText  控件打开时要显示的文本 
     * @param offText  控件关闭时要显示的文本 
     */  
    public void setText(final String onText, final String offText) {  
        mOnText = onText;  
        mOffText = offText;  
        invalidate();  
    }  
    
    
    /**
     * [设置滑动开关状态]
     * @param on 是否开启
     */
    public void setStatus(boolean on) {
        mSwitchStatus = on ? SWITCH_ON : SWITCH_OFF;
        invalidate();
    }  
    

    /** 
     * 设置开关的状态 
     * @param on 是否打开开关 打开为true 关闭为false 
     */  
    public void changeStatus(boolean on) {  
        mSwitchStatus = on ? SWITCH_ON : SWITCH_OFF;  
        int xFrom = ANIMATION_X_LEFT;
        int xTo = ANIMATION_X_RIGHT;
        
        if (mSwitchStatus == SWITCH_OFF) {  
            xFrom = ANIMATION_X_RIGHT;  
            xTo = ANIMATION_X_LEFT;  
        }  
        AnimationTransRunnable runnable = new AnimationTransRunnable(xFrom, xTo, 1);  
        new Thread(runnable).start();  
    }  
    
    /**
     * [获取开关状态]
     * @return 开关状态
     */
    public int getStatus() {
        return mSwitchStatus;
    }
    
    /**
     * [设置开关提示文字大小]
     * @param textSize 字体大小
     */
    public void setTextSize(float textSize) {
        mTextSize = textSize;
    }
    
    @Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
        mPaint.setTextSize(mTextSize);  
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);  
          
        if (mSwitchStatus == SWITCH_OFF) {  
            drawBitmap(canvas, null, null, mSwitchOff);  
            drawBitmap(canvas, null, null, mSwitchThumb);  
            mPaint.setColor(OFF_TEXT_COLOR);  
            canvas.translate(mSwitchThumb.getWidth(), 0);  
            canvas.drawText(mOffText, mOffTextMarginLeft, mTextMarginTop, mPaint);  
        } else if (mSwitchStatus == SWITCH_ON) {  
            drawBitmap(canvas, null, null, mSwitchOn);  
            int count = canvas.save();  
            canvas.translate(mSwitchOn.getWidth() - mSwitchThumb.getWidth(), 0);  
            drawBitmap(canvas, null, null, mSwitchThumb);  
            mPaint.setColor(Color.WHITE);  
            canvas.restoreToCount(count);  
            canvas.drawText(mOnText, mOnTextMarginLeft, mTextMarginTop, mPaint);  
        //SWITCH_SCROLING  
        } else {
//            mSwitchStatus = mDstX > 35 ? SWITCH_ON : SWITCH_OFF;  
            mSwitchStatus = mDstX > ((mBmpWidth - mThumbWidth) / 2) ? SWITCH_ON : SWITCH_OFF;
            drawBitmap(canvas, new Rect(0, 0, mDstX, mBmpHeight), new Rect(0, 0, (int) mDstX, mBmpHeight), mSwitchOn);  
            mPaint.setColor(Color.WHITE);  
            canvas.drawText(mOnText, mOnTextMarginLeft, mTextMarginTop, mPaint);  
  
            int count = canvas.save();  
            canvas.translate(mDstX, 0);  
            drawBitmap(canvas, new Rect(mDstX, 0, mBmpWidth, mBmpHeight),  
                          new Rect(0, 0, mBmpWidth - mDstX, mBmpHeight), mSwitchOff);  
            canvas.restoreToCount(count);  
  
            count = canvas.save();  
            canvas.clipRect(mDstX, 0, mBmpWidth, mBmpHeight);  
            canvas.translate(mThumbWidth, 0);  
            mPaint.setColor(OFF_TEXT_COLOR);  
            canvas.drawText(mOffText, mOffTextMarginLeft, mTextMarginTop, mPaint);  
            canvas.restoreToCount(count);  
  
            count = canvas.save();  
            canvas.translate(mDstX - mThumbWidth / 2, 0);  
            drawBitmap(canvas, null, null, mSwitchThumb);  
            canvas.restoreToCount(count);  
        }  
    }  
  
    private void drawBitmap(Canvas canvas, Rect src, Rect dst, Bitmap bitmap) {
        if (dst == null) {
            dst = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        }
        Paint paint = new Paint();  
        canvas.drawBitmap(bitmap, src, dst, paint);  
    }  
    
    /** 
     * AnimationTransRunnable 做滑动动画所使用的线程 
     */  
    private class AnimationTransRunnable implements Runnable {  
        private int mFromX;
        private int mToX;  
        private int mUseAnimation;  
        private int mStep;
  
        /** 
         * 滑动动画 
         * @param srcX 滑动起始点 
         * @param dstX 滑动终止点 
         * @param duration 是否采用动画，1采用，0不采用 
         */  
        public AnimationTransRunnable(float srcX, float dstX, final int duration) {  
            this.mFromX = (int) srcX;  
            this.mToX = (int) dstX;  
            if (this.mFromX < this.mToX) {
                this.mStep = STEP_TO_RIGHT;
            } else {
                this.mStep = STEP_TO_LEFT;
            }
            this.mUseAnimation = duration;  
        }  
  
        @Override  
        public void run() {  
//            final int patch = (dstX > srcX ? 5 : -5);  
            if (mUseAnimation == 0) {  
                SlideSwitch.this.mSwitchStatus = SWITCH_SCROLING;  
                SlideSwitch.this.postInvalidate();  
            } else {  
//                EvLog.d( "start Animation: [ " + srcX + " , " + dstX + " ]");  
                int x = mFromX +  this.mStep;  
                while (Math.abs(x - mToX) > Math.abs(this.mStep)) {  
                    mDstX = x;  
                    SlideSwitch.this.mSwitchStatus = SWITCH_SCROLING;  
                    SlideSwitch.this.postInvalidate();  
                    x += this.mStep;  
                    try {  
                        Thread.sleep(ANIMATION_SLEEP_INTERVAL);  
                    } catch (InterruptedException e) {  
//                        e.printStackTrace();  
                    }  
                }  
                
                if (mDstX != mToX) {
                    mDstX = mToX;  
                    SlideSwitch.this.mSwitchStatus = mDstX > ((mBmpWidth - mThumbWidth)/2) ? SWITCH_ON : SWITCH_OFF;  
                    SlideSwitch.this.postInvalidate();  
                }
            }  
        }  
    }
}
