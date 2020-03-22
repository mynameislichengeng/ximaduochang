/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-3-18     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.widget.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * [可定制绘制selector的ListView]
 */
public class CustomSelectorListView extends ListView {
    
    private int mSelectorPaddingLeft = 0;
    private int mSelectorPaddingTop = 0;
    private int mSelectorPaddingRight = 0;
    private int mSelectorPaddingBottom = 0;

    public CustomSelectorListView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomSelectorListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSelectorListView(Context context) {
        super(context);
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
            Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

}
