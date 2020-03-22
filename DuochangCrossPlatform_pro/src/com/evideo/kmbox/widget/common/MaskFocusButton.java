package com.evideo.kmbox.widget.common;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;


import com.evideo.kmbox.R;


public class MaskFocusButton extends Button {
    
    private boolean focusFlag = false;
    
    private int mFocusPadding;
    private int mFocusPaddingLeft;
    private int mFocusPaddingTop;
    private int mFocusPaddingRight;
    private int mFocusPaddingBottom;
    
    private int mFocusFrame = R.drawable.focus_frame_new;

    public MaskFocusButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    public MaskFocusButton(Context context, AttributeSet attrs) {
        this(context, attrs, com.android.internal.R.attr.buttonStyle);
    }

    public MaskFocusButton(Context context) {
        this(context, null);
    }
    
    private void init(Context context, AttributeSet attrs, int defStyle) {
        mFocusPadding = context.getResources().getDimensionPixelSize(R.dimen.px20);
        
        TypedArray a = context.obtainStyledAttributes(attrs, 
                R.styleable.MaskFocusView, defStyle, 0);
        mFocusPaddingLeft = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingLeft, mFocusPadding);
        mFocusPaddingRight = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingRight, mFocusPadding);
        mFocusPaddingTop = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingTop, mFocusPadding);
        mFocusPaddingBottom = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingBottom, mFocusPadding);
        a.recycle();
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
        drawFocus(canvas);
    }
    
    private void drawFocus(Canvas canvas) {
        if (focusFlag) {
            Drawable drawable = getResources().getDrawable(mFocusFrame);
            drawable.setBounds(-mFocusPaddingLeft, -mFocusPaddingTop, 
                    getWidth() + mFocusPaddingRight, getHeight() + mFocusPaddingBottom);
            drawable.draw(canvas);
        }
    }

    /**
     * @param paddingLeft 左padding
     * @param paddingTop 上padding
     * @param paddingRight 右padding
     * @param paddingBottom 底padding
     */
    public void setFocusPadding(int paddingLeft, int paddingTop,
            int paddingRight, int paddingBottom) {
        mFocusPaddingLeft = paddingLeft;
        mFocusPaddingTop = paddingTop;
        mFocusPaddingRight = paddingRight;
        mFocusPaddingBottom = paddingBottom;
    }
    
    /**[设置边框]
     * @param focusResId 边框drawable id
     */
    public void setFocusFrame(int focusResId) {
        mFocusFrame = focusResId;
    }

}
