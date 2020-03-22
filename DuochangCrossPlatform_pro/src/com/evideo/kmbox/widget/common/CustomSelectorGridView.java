/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年9月24日     "dinghaiqiang"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.common;

import java.lang.reflect.Field;

import com.evideo.kmbox.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.GridView;

/**
 * [可定制绘制selector的GridView]
 */
public class CustomSelectorGridView extends GridView {
    private int mSelectorPaddingLeft = 0;
    private int mSelectorPaddingTop = 0;
    private int mSelectorPaddingRight = 0;
    private int mSelectorPaddingBottom = 0;
    private Drawable mFocusFrame;
    private boolean mForceSquared = false;

    public CustomSelectorGridView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public CustomSelectorGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CustomSelectorGridView(Context context) {
        super(context);
        initView();
    }
    
    private void initView() {
        reflectSelectorRect();
        setSelector(R.drawable.song_list_focus_frame);
        mFocusFrame = getResources().getDrawable(R.drawable.focus_frame_new);
    }
    
    /**
     * [功能说明] 设置焦点框
     * @param frame 焦点框
     */
    public void setCustomSelectorDrawable(Drawable frame) {
        mFocusFrame = frame;
    }
    
    /**
     * [功能说明] 正方形selector，用于产生规则的圆形selector
     * @param value true or false
     */
    public void setEnableSquareSelector(boolean value) {
        mForceSquared = value;
    }
    
    /**
     * [功能说明] 等长padding
     * @param paddingAll padding
     */
    public void setSelectorPadding(int paddingAll) {
        mSelectorPaddingLeft = paddingAll;
        mSelectorPaddingTop = paddingAll;
        mSelectorPaddingRight = paddingAll;
        mSelectorPaddingBottom = paddingAll;
    }
    
    /**
     * [设置list selector的padding值]
     * @param paddingLeft 左padding值
     * @param paddingTop 上padding值
     * @param paddingRight 右padding值
     * @param paddingBottom 下padding值
     */
    public void setSelectorPadding(int paddingLeft, int paddingTop, 
            int paddingRight, int paddingBottom) {
        mSelectorPaddingLeft = paddingLeft;
        mSelectorPaddingTop = paddingTop;
        mSelectorPaddingRight = paddingRight;
        mSelectorPaddingBottom = paddingBottom;
    }
    
    private Rect mListSelectorRect;
    
    /**
     * [功能说明]反射获取mSelectorRect的引用
     */
    private void reflectSelectorRect() {
        try {
            Class<?> c = Class.forName("android.widget.AbsListView");
            Field f = c.getDeclaredField("mSelectorRect");
            f.setAccessible(true);
            mListSelectorRect = (Rect) f.get(this);
        } catch (Exception e) {
        }
    }
    
    private void drawListSelector(Canvas canvas) {

        if (!mListSelectorRect.isEmpty()) {
            if (mForceSquared) {
                //产生正方形边长
                int length = mListSelectorRect.right - mListSelectorRect.left;
                //修正rect区域
                mListSelectorRect.bottom = mListSelectorRect.top + length;
            }
            final Drawable selector = mFocusFrame;
            selector.setBounds(
                    mListSelectorRect.left - mSelectorPaddingLeft, 
                    mListSelectorRect.top - mSelectorPaddingTop,
                    mListSelectorRect.right + mSelectorPaddingRight, 
                    mListSelectorRect.bottom + mSelectorPaddingBottom);
            selector.draw(canvas);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isFocused()) {
            drawListSelector(canvas);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
            Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }
    
    public interface IEdgeListener {
        public boolean onDownEdge();
        public boolean onLeftEdge();
        public boolean onRightEdge();
        public boolean onUpEdge();
    }
    
    private IEdgeListener mListener = null;
    public void setEdgeListener(IEdgeListener listener) {
        mListener = listener;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getAdapter().getCount() <= 0) {
            return super.onKeyDown(keyCode, event);
        }
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_UP: {
            int lineNum = getAdapter().getCount() / getNumColumns();
            int firstLineItemEndIndex = getAdapter().getCount() -1;
            if (lineNum > 0) {
                firstLineItemEndIndex = getNumColumns() - 1;
            }
            if ((getSelectedItemPosition() >= 0)
                    && (getSelectedItemPosition() <= firstLineItemEndIndex)) {
                if (mListener != null) {
                    return mListener.onUpEdge();
                }
            }
            break;
        }
        case KeyEvent.KEYCODE_DPAD_DOWN: {
            int lineNum = getAdapter().getCount() / getNumColumns();
            int lastLineItemNum = getAdapter().getCount() % getNumColumns();
            if (lastLineItemNum > 0) {
                lineNum++;
            }
            int lastLineItemStartIndex = (lineNum-1)*getNumColumns();
//            EvLog.i("lastLineItemStartIndex:" + lastLineItemStartIndex + ",lineNum:" + lineNum);
            if ((getSelectedItemPosition() >= lastLineItemStartIndex)
                    && (getSelectedItemPosition() <= getAdapter().getCount() - 1)) {
                if (mListener != null) {
                    return mListener.onDownEdge();
                }
            }
            break;
        }
        case KeyEvent.KEYCODE_DPAD_LEFT: {
                if (getSelectedItemPosition() % getNumColumns() == 0) {
                    if (mListener != null) {
                        return mListener.onLeftEdge();
                    }
                }
                break;
            } 
        case KeyEvent.KEYCODE_DPAD_RIGHT: {
//                EvLog.i("recv KEYCODE_DPAD_RIGHT" + getNumColumns());
                if ((getSelectedItemPosition() == (getAdapter().getCount() - 1)) ||
                    (((getSelectedItemPosition()+1) % getNumColumns()) == 0)/*(getSelectedItemPosition() - getNumColumns() -1) % getNumColumns() == 0*/) {
                    if (mListener != null) {
                        return mListener.onRightEdge();
                    }
                }
                break;
            }
        default:
            break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
