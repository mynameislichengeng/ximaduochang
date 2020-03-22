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

import android.view.View;

/**
 * [功能说明]
 */
public class FadeInTransformer extends BaseTransformer {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onTransform(View view, float position) {
        if (position < -1 || position > 1) {
            view.setAlpha(0);
        }
        else if (position <= 0 || position <= 1) {
            // Calculate alpha. Position is decimal in [-1,0] or [0,1]
            float alpha = (position <= 0) ? position + 1 : 1 - position;
            view.setAlpha(alpha);
        }
        else if (position == 0) {
            view.setAlpha(1);
        }
    }
}
