/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-10-5     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.anim;

import android.animation.ObjectAnimator;
import android.view.View;

/**
 * [功能说明]从右边滑出
 */
public class SlideRightOut extends BaseAnim {
    
    private int mTranslationX = 300;
    
    /**
     * [功能说明]设置滑动距离
     * @param x 滑动距离
     */
    public void setTranslationX(int x) {
        mTranslationX = x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setupAnimation(View view) {
        getAnimatorSet().playTogether(
                ObjectAnimator.ofFloat(view, "translationX", 0, mTranslationX).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "alpha", 1, 0).setDuration(mDuration * 3 / 2));
    }

}
