package com.evideo.kmbox.widget.common;


import com.evideo.kmbox.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;


/**
 * [用于列表的带有走马灯效果的TextView]
 */
public class ListMarqueeTextView extends TextView implements AnimatorUpdateListener {
    
    /** [滚动速率] */
    private static final int SCROLL_VELOCITY = 50;
    /** [开始走马灯的延时时间] */
    private static final int START_SCROLL_DELAY_TIME = 2000;
    /** [alpha动画时间] */
    private static final int ANIM_DURATION_ALPHA = 1000;
    
    private boolean mClip = false;
    private int mHeight;
    private Rect mClipRect;
    private ScrollHandler mHandler;
    private boolean mStopped = false;
    /** [执行走马灯时的宽度] */
    private int mSpecWidth;
    private int mScrollDistance;
    private int mLastX;
    private ValueAnimator mMarqueeAnimator;
    private ObjectAnimator mAlphaAnimator;
    private AnimatorSet mAnimatorSet;
    
    private View mParentFocusedView;
    
    /**
     * [功能说明]设置可获取焦点的父view
     * @param parentFocusedView
     */
    public void setParentFocusedView(View parentFocusedView) {
        mParentFocusedView = parentFocusedView;
    }

    public ListMarqueeTextView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ListMarqueeTextView, defStyle, 0);
        mSpecWidth = a.getDimensionPixelSize(R.styleable.ListMarqueeTextView_specWidth, 0);
        a.recycle();
        init(context);
    }

    public ListMarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListMarqueeTextView(Context context) {
        this(context, null);
    }
    
    private void init(Context context) {
        setSingleLine(true);
        mHandler = new ScrollHandler();
        mClipRect = new Rect();
        mMarqueeAnimator = new ValueAnimator();
        mMarqueeAnimator.addUpdateListener(this);
        mMarqueeAnimator.setInterpolator(new LinearInterpolator());
        
        Keyframe kf0 = Keyframe.ofFloat(1.0f, 1.0f);
        Keyframe kf1 = Keyframe.ofFloat(1.0f, 0);
        PropertyValuesHolder pvh = PropertyValuesHolder.ofKeyframe("alpha", kf0, kf1);
        mAlphaAnimator = ObjectAnimator.ofPropertyValuesHolder(this, pvh);
        mAlphaAnimator.setDuration(ANIM_DURATION_ALPHA);
        
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(mMarqueeAnimator).before(mAlphaAnimator);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mStopped && mHandler != null) {
                    mHandler.sendEmptyMessage(ScrollHandler.MSG_WHAT_RESTART_SCROLL);
                }
            }
        });
    }
    
    /**
     * @brief : [设置执行走马灯时的宽度]
     * @param specWidth
     */
    public void setSpecWidth(int specWidth) {
        if (specWidth > 0) {
            mSpecWidth = specWidth;
        }
    }
    
    /**
     * @brief : [获取执行走马灯时的宽度]
     * @return
     */
    public int getSpecWidth() {
        return mSpecWidth;
    }
    
    /**
     * @brief : [设置是否截断]
     * @param clip
     */
    public void setClip(boolean clip) {
        mClip = clip;
        invalidate();
    }
    
    private void startScroll() {
        if (mAnimatorSet == null || mMarqueeAnimator == null) {
            return;
        }
        stopScroll();
        if (!mStopped) {
            int duration = mScrollDistance*1000/SCROLL_VELOCITY;
//            Log.v("something", "duration " + duration);
            mMarqueeAnimator.setIntValues(0, mScrollDistance);
            mMarqueeAnimator.setDuration(duration);
            mAnimatorSet.start();
        }
    }
    
    private void stopScroll() {
        if (mAnimatorSet == null) {
            return;
        }
        if (mAnimatorSet.isStarted()) {
            mAnimatorSet.cancel();
        }
        setAlpha(1.0f);
        scrollTo(0, 0);
    }
    
    /**
     * @brief : [重置显示效果]
     */
    public void reset() {
        setAlpha(1.0f);
        stopScroll();
        mHandler.removeMessages(ScrollHandler.MSG_WHAT_START_SCROLL);
        mHandler.removeMessages(ScrollHandler.MSG_WHAT_RESTART_SCROLL);
        mHandler.removeMessages(ScrollHandler.MSG_WHAT_STOP_SCROLL);
        setClip(isSelected());
        if (isSelected()) {
            setEllipsize(null);
            startScrollAction();
        } else {
            setEllipsize(TruncateAt.END);
            stopScrollAction();
        }
    }
    
    /**
     * @brief : [开始走马灯]
     */
    public void startScrollAction() {
        if (mHandler == null) {
            return;
        }
        if (!needScroll()) {
            return;
        }
        mStopped = false;
        mHandler.removeMessages(ScrollHandler.MSG_WHAT_START_SCROLL);
        mHandler.sendEmptyMessageDelayed(ScrollHandler.MSG_WHAT_START_SCROLL, START_SCROLL_DELAY_TIME);
    }
    
    /**
     * @brief : [结束走马灯]
     */
    public void stopScrollAction() {
        if (mHandler == null) {
            return;
        }
        mStopped = true;
        mHandler.removeMessages(ScrollHandler.MSG_WHAT_START_SCROLL);
        mHandler.removeMessages(ScrollHandler.MSG_WHAT_RESTART_SCROLL);
        mHandler.removeMessages(ScrollHandler.MSG_WHAT_STOP_SCROLL);
        mHandler.sendEmptyMessage(ScrollHandler.MSG_WHAT_STOP_SCROLL);
    }
    
    private class ScrollHandler extends Handler {
        public static final int MSG_WHAT_START_SCROLL = 2015;
        public static final int MSG_WHAT_STOP_SCROLL = 2016;
        public static final int MSG_WHAT_RESTART_SCROLL = 2017;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_WHAT_START_SCROLL:
                if (!mStopped) {
                    startScroll();
                }
                break;
            case MSG_WHAT_STOP_SCROLL:
                stopScroll();
                break;
            case MSG_WHAT_RESTART_SCROLL:
                stopScroll();
                removeMessages(MSG_WHAT_START_SCROLL);
                if (!mStopped) {
                    sendEmptyMessageDelayed(MSG_WHAT_START_SCROLL, START_SCROLL_DELAY_TIME);
                }
                break;
            default:
                break;
            }
        }
    }
    
    public void setParentViewFocused(boolean isParentViewFocused) {
        setClip(isParentViewFocused);
        if (isParentViewFocused) {
            setEllipsize(null);
            startScrollAction();
        } else {
            setEllipsize(TruncateAt.END);
            stopScrollAction();
        }
    }
    
    @Override
    protected void dispatchSetSelected(boolean selected) {
        if (mParentFocusedView != null && mParentFocusedView.isFocused() && selected) {
            setClip(true);
            setEllipsize(null);
            startScrollAction();
        } else {
            setClip(false);
            setEllipsize(TruncateAt.END);
            stopScrollAction();
        }
//        setClip(selected);
//        if (selected) {
//            setEllipsize(null);
//            startScrollAction();
//        } else {
//            setEllipsize(TruncateAt.END);
//            stopScrollAction();
//        }
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        
        if (mClip && mSpecWidth >= 0) {
            canvas.save();
            
            mHeight = canvas.getHeight();
            
            if (mSpecWidth > 0) {
                mClipRect.set(0, 0, mSpecWidth + getScrollX(), mHeight);
            } else if (mSpecWidth == 0) {
                mClipRect.set(0, 0, canvas.getWidth() + getScrollX(), mHeight);
            }
            
            canvas.clipRect(mClipRect, Region.Op.REPLACE);
            
            super.onDraw(canvas);
            
            canvas.restore();
        } else {
            super.onDraw(canvas);
        }
        
    }
    
    private boolean needScroll() {
        String text = getText().toString();
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        float w = getPaint().measureText(text) + getPaddingLeft() + getPaddingRight();
        if (mSpecWidth > 0) {
            if (w > mSpecWidth) {
                mScrollDistance = (int) (w - mSpecWidth);
//            Log.v("something", "text " + text);
//            Log.v("something", "w " + w + " mScrollDistance " + mScrollDistance);
                if (mScrollDistance > 2) {
                    return true;
                }
            }
        } else if (mSpecWidth == 0) {
            int width = getWidth() + getPaddingLeft() + getPaddingRight();
            if (w > width) {
                mScrollDistance = (int) (w - width);
                if (mScrollDistance > 2) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
    
    @Override
    protected void onDetachedFromWindow() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
        if (mHandler != null) {
            mHandler.removeMessages(ScrollHandler.MSG_WHAT_START_SCROLL);
            mHandler.removeMessages(ScrollHandler.MSG_WHAT_STOP_SCROLL);
            mHandler.removeMessages(ScrollHandler.MSG_WHAT_RESTART_SCROLL);
        }
        super.onDetachedFromWindow();
    }
    
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (animation == null) {
            return;
        }
        int x = (Integer) animation.getAnimatedValue();
        if (mLastX != x) {
            mLastX = x;
            scrollTo(x, 0);
        }
    }
    
}
