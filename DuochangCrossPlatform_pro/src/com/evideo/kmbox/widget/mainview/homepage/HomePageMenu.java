/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-9-24     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.homepage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.evideo.kmbox.widget.common.MaskFocusAnimImageView;
import com.evideo.kmbox.R;

/**
 * [功能说明]首页歌星
 */
public class HomePageMenu extends MaskFocusAnimImageView {
    
    private int mTopGap;
    private Drawable mDrawable = null;
    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public HomePageMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * @param context
     * @param attrs
     */
    public HomePageMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * @param context
     */
    public HomePageMenu(Context context) {
        super(context);
        init();
    }
    
    private void init() {
        mTopGap = getResources().getDimensionPixelSize(R.dimen.px72);
        mDrawable = getResources().getDrawable(R.drawable.home_page_menu);
    }
    
    public void resetSingerDrawable() {
        mDrawable = getResources().getDrawable(R.drawable.home_page_menu);
        invalidate();
    }
    
    public void setSingerDrawable(Drawable draw) {
        mDrawable = draw;
        invalidate();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        final Drawable singer = getResources().getDrawable(R.drawable.home_page_singer);
        mDrawable.setBounds(0, -mTopGap, getWidth(), getHeight());
        mDrawable.draw(canvas);
    }
}
