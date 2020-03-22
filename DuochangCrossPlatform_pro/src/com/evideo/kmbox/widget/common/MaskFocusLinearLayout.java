package com.evideo.kmbox.widget.common;

import com.evideo.kmbox.R;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MaskFocusLinearLayout extends LinearLayout {
    
    private boolean focusFlag = false;
    
    private boolean forceFocusFlag = false;
    
    private int mFocusPaddingLeft;
    private int mFocusPaddingRight;
    private int mFocusPaddingTop;
    private int mFocusPaddingBottom;
    protected boolean mFocusOnTop = true;
    private int mFocusFrameResId = R.drawable.focus_frame_new;

    public MaskFocusLinearLayout(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, 
                R.styleable.MaskFocusView, defStyle, 0);
        mFocusPaddingLeft = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingLeft, 0);
        mFocusPaddingRight = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingRight, 0);
        mFocusPaddingTop = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingTop, 0);
        mFocusPaddingBottom = a.getDimensionPixelSize(R.styleable.MaskFocusView_focusPaddingBottom, 0);
        mFocusFrameResId = a.getResourceId(R.styleable.MaskFocusView_focusFrame, R.drawable.focus_frame_new);
        mFocusOnTop = a.getBoolean(R.styleable.MaskFocusView_focusOnTop, true);
        a.recycle();
    }

    public MaskFocusLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MaskFocusLinearLayout(Context context) {
        this(context, null);
    }
    
    public void setFocusFrame(int resId ) {
        mFocusFrameResId = resId;
    }
    
    public void setForceFocusFlag(boolean forceFocus) {
        forceFocusFlag = forceFocus;
        invalidate();
    }
    
    public void setFocusPadding(int left, int top, int right , int buttom) {
        mFocusPaddingLeft = left;
        mFocusPaddingRight = right;
        mFocusPaddingTop = top;
        mFocusPaddingBottom = buttom;
    }
        
    @Override
    protected void onFocusChanged(boolean focused, int direction,
            Rect previouslyFocusedRect) {
//        EvLog.e("something", this.getId() + " MaskFocusButton: gainFocus: " + focused + " direction: " + direction);
        focusFlag = focused;
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (canvas == null) {
            UmengAgentUtil.reportError("MaskFocusLinearLayout dispatchDraw canvas is null");
            return;
        }
        
        if (mFocusOnTop) {
            try {
                super.dispatchDraw(canvas);
                drawFocus(canvas);
            } catch (NullPointerException e) {
                e.printStackTrace();
                UmengAgentUtil.reportError("MaskFocusLinearLayout dispatchDraw catch NullPointerException," + e.getMessage());
                return;
            }
            
        } else {
            if (focusFlag) {
                drawFocus(canvas);
            }
            try {
                super.dispatchDraw(canvas);
            } catch (NullPointerException e) {
                e.printStackTrace();
                UmengAgentUtil.reportError("MaskFocusLinearLayout dispatchDraw catch NullPointerException," + e.getMessage());
                return;
            }
        } 
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Canvas canvas) {
//        EvLog.d(this.getId() + "onDraw," );
        if (!mFocusOnTop) {
            drawFocus(canvas);
        }
        super.onDraw(canvas);
    }
    
    private void drawFocus(Canvas canvas) {
//        EvLog.d(this.getId()  + "drawFocus,forceFocusFlag:" + forceFocusFlag + ",focusFlag:" + focusFlag + ",isPressed:" + isPressed());
        if(forceFocusFlag || (focusFlag && !isPressed())) {
//            EvLog.d("drawFocus 95-----------------");
            final Drawable drawable = getResources().getDrawable(mFocusFrameResId);
            drawable.setBounds(-mFocusPaddingLeft, -mFocusPaddingTop, 
                    getWidth() + mFocusPaddingRight, getHeight() + mFocusPaddingBottom);
            //drawable.setBounds(0, 0, getWidth(), getHeight());
            drawable.draw(canvas);
        }
    }
}
