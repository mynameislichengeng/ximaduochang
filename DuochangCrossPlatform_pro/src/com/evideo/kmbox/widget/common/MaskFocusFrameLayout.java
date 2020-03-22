/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date			Author		Version		Description
 *  -----------------------------------------------
 *  2015-7-2		"zhaoyunlong"		1.0		[修订说明]
 *
 */

package com.evideo.kmbox.widget.common;

import com.evideo.kmbox.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * [功能说明]
 */
public class MaskFocusFrameLayout extends FrameLayout {
    
    private boolean focusFlag = false;
    
    private boolean forceFocusFlag = false;
    
    private int mFocusPaddingLeft;
    private int mFocusPaddingRight;
    private int mFocusPaddingTop;
    private int mFocusPaddingBottom;
    private int mFocusFrameResId = R.drawable.focus_frame_new;

    public MaskFocusFrameLayout(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, 
                R.styleable.MaskFocusView, defStyle, 0);
        mFocusPaddingLeft = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingLeft, 0);
        mFocusPaddingRight = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingRight, 0);
        mFocusPaddingTop = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingTop, 0);
        mFocusPaddingBottom = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingBottom, 0);
        mFocusFrameResId = a.getResourceId(R.styleable.MaskFocusView_focusFrame, R.drawable.focus_frame_new);
        a.recycle();
    }

    public MaskFocusFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MaskFocusFrameLayout(Context context) {
        this(context,null);
    }
    
    public void setFocusFrame(int resId ) {
        mFocusFrameResId = resId;
    }
    
    public void setForceFocusFlag(boolean forceFocus) {
        forceFocusFlag = forceFocus;
        invalidate();
    }
        
    @Override
    protected void onFocusChanged(boolean focused, int direction,
            Rect previouslyFocusedRect) {
        //EvLog.d("something", "MaskFocusButton: gainFocus: " + focused + " direction: " + direction);
        focusFlag = focused;
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawFocus(canvas);
    }
    
    private void drawFocus(Canvas canvas) {
        if(forceFocusFlag || (focusFlag && !isPressed())) {
            final Drawable drawable = getResources().getDrawable(mFocusFrameResId);
            drawable.setBounds(-mFocusPaddingLeft, -mFocusPaddingTop, 
                    getWidth() + mFocusPaddingRight, getHeight() + mFocusPaddingBottom);
            //drawable.setBounds(0, 0, getWidth(), getHeight());
            drawable.draw(canvas);
        }
    }
    
}
