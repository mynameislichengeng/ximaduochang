/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年8月24日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.huodong;

import com.evideo.kmbox.util.EvLog;

import android.view.View;

/**
 * [功能说明]
 */
public class DepthPageTransformer extends BaseTransformer {
    private static final float MIN_SCALE = 0.75f;

    @Override
    protected void onTransform(View view, float position) {
        EvLog.d("----------DepthPageTransformer");
        if (position <= 0f) {
            view.setTranslationX(0f);
            view.setScaleX(1f);
            view.setScaleY(1f);
        } else if (position <= 1f) {
            final float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            view.setAlpha(1 - position);
            view.setPivotY(0.5f * view.getHeight());
            view.setTranslationX(view.getWidth() * -position);
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }
    }

    @Override
    protected boolean isPagingEnabled() {
        return true;
    }
}
