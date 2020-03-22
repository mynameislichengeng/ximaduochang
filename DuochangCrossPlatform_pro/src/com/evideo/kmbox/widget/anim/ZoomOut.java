/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-9-30     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.anim;

import android.animation.ObjectAnimator;
import android.view.View;

/**
 * [功能说明]从大到小离开视野
 */
public class ZoomOut extends BaseAnim {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setupAnimation(View view) {
        getAnimatorSet().playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.5f, 0.1f).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.5f, 0.1f).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "alpha", 1, 0).setDuration(mDuration * 3 / 2));
    }

}
