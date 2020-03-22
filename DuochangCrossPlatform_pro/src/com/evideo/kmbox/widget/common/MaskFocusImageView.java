package com.evideo.kmbox.widget.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.evideo.kmbox.R;

public class MaskFocusImageView extends ImageView {
    
    protected boolean focusFlag = false;
    
    protected boolean forceFocus = false;
    
    protected int mFocusPaddingLeft;
    protected int mFocusPaddingRight;
    protected int mFocusPaddingTop;
    protected int mFocusPaddingBottom;
    protected boolean mFocusOnTop = true;
    protected int mDefaultFocusFrame = R.drawable.focus_frame_new;
    
    public boolean isForceFocus() {
        return forceFocus;
    }

    public void setFocusFrame(int resId ) {
        mDefaultFocusFrame = resId;
    }
    public void setForceFocus(boolean forceFocus) {
        this.forceFocus = forceFocus;
        invalidate();
    }

    public MaskFocusImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, 
                R.styleable.MaskFocusView, defStyle, 0);
        mFocusPaddingLeft = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingLeft, 0);
        mFocusPaddingRight = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingRight, 0);
        mFocusPaddingTop = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingTop, 0);
        mFocusPaddingBottom = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingBottom, 0);
        mDefaultFocusFrame = a.getResourceId(R.styleable.MaskFocusView_focusFrame, R.drawable.focus_frame_new);
        mFocusOnTop = a.getBoolean(R.styleable.MaskFocusView_focusOnTop, true);
        a.recycle();
    }

    public MaskFocusImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MaskFocusImageView(Context context) {
        this(context,null);
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
        if (mFocusOnTop) {
            drawFocus(canvas);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (!mFocusOnTop) {
            drawFocus(canvas);
        }
        super.onDraw(canvas);
    }
    
    private void drawFocus(Canvas canvas) {
        if((focusFlag && !isPressed()) || forceFocus) {
            Drawable drawable = getResources().getDrawable(mDefaultFocusFrame);
//            drawable.setBounds(0, 0, getWidth(), getHeight());
            drawable.setBounds(-mFocusPaddingLeft, -mFocusPaddingTop, 
                    getWidth() + mFocusPaddingRight, getHeight() + mFocusPaddingBottom);
            drawable.draw(canvas);
        }
    }

}
