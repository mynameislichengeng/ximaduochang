package com.evideo.kmbox.widget.anim;

import android.animation.ObjectAnimator;
import android.view.View;


/**
 * [功能说明]摇晃
 */
public class Shake extends BaseAnim {

    @Override
    protected void setupAnimation(View view) {
        getAnimatorSet().playTogether(
                ObjectAnimator.ofFloat(view, "translationX", 
                        0, .10f, -25, .26f, 25,.42f, 
                        -25, .58f, 25, .74f, -25, .90f, 1,0).setDuration(mDuration));
    }
}
