package com.evideo.kmbox.widget.anim;

import android.animation.ObjectAnimator;
import android.view.View;


/**
 * [功能说明]旋转落入
 */
public class SideFall extends BaseAnim{

    @Override
    protected void setupAnimation(View view) {
        getAnimatorSet().playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 2, 1.5f, 1).setDuration(mDuration),
                ObjectAnimator.ofFloat(view,"scaleY", 2, 1.5f, 1).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "rotation", 25, 0).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "translationX", 80, 0).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "alpha", 0, 1).setDuration(mDuration * 3 / 2));
    }
}
