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

import com.evideo.kmbox.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

/**
 * [功能说明]
 */
public class AutoEnlargeTextView extends TextView {
    private int mNormalTextSize;
    private int mEnlargedTextSize;
    
    private View mParentFocusedView;

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public AutoEnlargeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mEnlargedTextSize = context.getResources().getDimensionPixelSize( R.dimen.px57);
        mNormalTextSize = context.getResources().getDimensionPixelSize( R.dimen.px39);
       /* if (DeviceInfoUtils.isBesTvLittleRed()) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mEnlargedTextSize);
        }*/
    }
    
    public AutoEnlargeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEnlargedTextSize = context.getResources().getDimensionPixelSize( R.dimen.px57);
        mNormalTextSize = context.getResources().getDimensionPixelSize( R.dimen.px39);
        /*if (DeviceInfoUtils.isBesTvLittleRed()) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mEnlargedTextSize);
        }*/
    }

    public AutoEnlargeTextView(Context context) {
        super(context);
        mEnlargedTextSize = context.getResources().getDimensionPixelSize( R.dimen.px57);
        mNormalTextSize = context.getResources().getDimensionPixelSize( R.dimen.px39);
        /*if (DeviceInfoUtils.isBesTvLittleRed()) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mEnlargedTextSize);
        }*/
    }
    
    /**
     * [功能说明]设置可获取焦点的父view
     * @param parentFocusedView
     */
    public void setParentFocusedView(View parentFocusedView) {
        mParentFocusedView = parentFocusedView;
    }
    
    public void setParentViewFocused(boolean isParentViewFocused) {
        /*if (DeviceInfoUtils.isBesTvLittleRed()) {
            return;
        }*/
        if (isParentViewFocused) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mEnlargedTextSize);
        } else {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalTextSize);
        }
    }
    
    /**
     * [设置正常与放大字体]
     * @param normal 正常字体
     * @param enlarge 放大字体
     */
    public void setTextSizePairs(int normal, int enlarge) {
        if (normal > 0) {
            mNormalTextSize = normal;
        }
        if (enlarge > 0) {
            mEnlargedTextSize = enlarge;
        }
    }

    /**
     * {@inheritDoc}
     * @brief : [被选中时字体变大，否则回到正常的字体大小]
     */
    @Override
    protected void dispatchSetSelected(boolean selected) {
        super.dispatchSetSelected(selected);
        /*if (DeviceInfoUtils.isBesTvLittleRed()) {
            return;
        }*/
        if (mParentFocusedView != null && mParentFocusedView.isFocused() && selected) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mEnlargedTextSize);
        } else {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalTextSize);
        }
//        if (selected) {
//            setTextSize(TypedValue.COMPLEX_UNIT_PX, mEnlargedTextSize);
//        } else {
//            setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalTextSize);
//        }
    }


}
