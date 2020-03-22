package com.evideo.kmbox.widget.common;

import com.evideo.kmbox.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

public class MaskFocusTextView extends TextView {
    
    private boolean focusFlag = false;
    private int mFocusPaddingLeft;
    private int mFocusPaddingRight;
    private int mFocusPaddingTop;
    private int mFocusPaddingBottom;
    
    private boolean forceFocusFlag = false;
    private int mFocusFrameResId = R.drawable.focus_frame_new;
    private int mSelectedFrameResId = R.drawable.selected_frame;
    
    private boolean mCustomSelectedFlag = false;
    private int mSelectPaddingLeft;
    private int mSelectPaddingRight;
    private int mSelectPaddingTop;
    private int mSelectPaddingBottom;

    public MaskFocusTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        TypedArray a = context.obtainStyledAttributes(attrs, 
                R.styleable.MaskFocusView, defStyle, 0);
        mFocusPaddingLeft = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingLeft, 0);
        mFocusPaddingRight = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingRight, 0);
        mFocusPaddingTop = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingTop, 0);
        mFocusPaddingBottom = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingBottom, 0);
        mSelectPaddingLeft = a.getDimensionPixelSize(R.styleable.MaskFocusView_selectPaddingLeft, 0);
        mSelectPaddingRight = a.getDimensionPixelSize(R.styleable.MaskFocusView_selectPaddingRight, 0);
        mSelectPaddingTop = a.getDimensionPixelSize(R.styleable.MaskFocusView_selectPaddingTop, 0);
        mSelectPaddingBottom = a.getDimensionPixelSize(R.styleable.MaskFocusView_selectPaddingBottom, 0);
        a.recycle();
        init(context);
    }

    public MaskFocusTextView(Context context, AttributeSet attrs) {
        this(context, attrs, com.android.internal.R.attr.textViewStyle);
    }

    public MaskFocusTextView(Context context) {
        super(context);
        init(context);
    }
    
    private void init(Context context) {
    }
    
    public void setCustomSelectedFlag(boolean customSelectedFlag) {
        mCustomSelectedFlag = customSelectedFlag;
    }
    
    public void setFocusFrame(int resId ) {
        mFocusFrameResId = resId;
    }
    
    public void setForceFocusFlag(boolean forceFocus) {
        forceFocusFlag = forceFocus;
        invalidate();
    }
    
    public void setFocusPadding(int focusPaddingLeft, int focusPaddingTop, 
            int focusPaddingRight, int focusPaddingBottom) {
        mFocusPaddingLeft = focusPaddingLeft;
        mFocusPaddingTop = focusPaddingTop;
        mFocusPaddingRight = focusPaddingRight;
        mFocusPaddingBottom = focusPaddingBottom;
    }
    
    @Override
    protected void onFocusChanged(boolean focused, int direction,
            Rect previouslyFocusedRect) {
//        EvLog.d("something", "MaskFocusButton: gainFocus: " + focused + " direction: " + direction);
        focusFlag = focused;
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawSelectedState(canvas);
        drawFocus(canvas);
    }
    
    private void drawFocus(Canvas canvas) {
        if(forceFocusFlag || (focusFlag && !isPressed())) {
            final Drawable drawable = getResources().getDrawable(mFocusFrameResId);
//            if(mFocusGap) {
//                drawable.setBounds(0, 0, getWidth(), getHeight());
//            } else {
//                drawable.setBounds(-mGap, -mGap, getWidth() + mGap, getHeight() + mGap);
//            }
            drawable.setBounds(-mFocusPaddingLeft, -mFocusPaddingTop, 
                    getWidth() + mFocusPaddingRight, getHeight() + mFocusPaddingBottom);
            drawable.draw(canvas);
        }
    }
    
    private void drawSelectedState(Canvas canvas) {
        if (isSelected() && mCustomSelectedFlag) {
            final Drawable drawable = getResources().getDrawable(mSelectedFrameResId);
            drawable.setBounds(-mSelectPaddingLeft, -mSelectPaddingTop, 
                    getWidth() + mSelectPaddingRight, getHeight() + mSelectPaddingBottom);
            drawable.draw(canvas);
        }
    }
    

}
