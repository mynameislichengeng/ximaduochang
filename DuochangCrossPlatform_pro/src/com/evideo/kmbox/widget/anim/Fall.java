/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-5-8     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.widget.anim;


import android.animation.ObjectAnimator;
import android.view.View;

/**
 * [功能说明]落入，从大到小
 */
public class Fall extends BaseAnim {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setupAnimation(View view) {
        getAnimatorSet().playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 2, 1.5f, 1).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "scaleY", 2, 1.5f, 1).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "alpha", 0, 1).setDuration(mDuration * 3 / 2));
    }

}
