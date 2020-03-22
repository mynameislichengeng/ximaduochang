/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年9月28日     "dinghaiqiang"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.common;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * [功能说明]
 */
public class AutoHideTextView extends TextView {
    
    private View mParentFocusedView;
    
    /**
     * [功能说明]设置可获取焦点的父view
     * @param parentFocusedView
     */
    public void setParentFocusedView(View parentFocusedView) {
        mParentFocusedView = parentFocusedView;
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public AutoHideTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
       /* if (DeviceInfoUtils.isBesTvLittleRed()) {
            setVisibility(View.GONE);
        }*/
    }

    public AutoHideTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
       /* if (DeviceInfoUtils.isBesTvLittleRed()) {
            setVisibility(View.GONE);
        }*/
    }

    public AutoHideTextView(Context context) {
        super(context);
        /*if (DeviceInfoUtils.isBesTvLittleRed()) {
            setVisibility(View.GONE);
        }*/
    }
    
    public void setParentViewFocused(boolean isParentViewFocused) {
       /* if (DeviceInfoUtils.isBesTvLittleRed()) {
            return;
        }*/
        if (isParentViewFocused) {
            setVisibility(View.INVISIBLE);

        } else {
            setVisibility(View.VISIBLE);
        }
    }

    /**
     * {@inheritDoc}
     * @brief : [被选中时自动隐藏，否则显示]
     */
    @Override
    protected void dispatchSetSelected(boolean selected) {
        super.dispatchSetSelected(selected);
        Log.i("gsp", "setParentViewFocused: 设置歌手的名字为隐藏 ");
        /*if (DeviceInfoUtils.isBesTvLittleRed()) {
            return;
        }*/
        if (mParentFocusedView != null && mParentFocusedView.isFocused() && selected) {
            setVisibility(View.INVISIBLE);
        } else {
            setVisibility(View.INVISIBLE);
        }
    }
    
}
