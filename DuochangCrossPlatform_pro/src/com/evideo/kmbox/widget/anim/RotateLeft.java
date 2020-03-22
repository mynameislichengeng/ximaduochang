package com.evideo.kmbox.widget.anim;

import android.animation.ObjectAnimator;
import android.view.View;

/**
 * [功能说明]左边翻转
 */
public class RotateLeft extends BaseAnim{

    @Override
    protected void setupAnimation(View view) {
        getAnimatorSet().playTogether(
                ObjectAnimator.ofFloat(view, "rotationY", 90, 0).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "translationX", -300, 0).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "alpha", 0, 1).setDuration(mDuration * 3 / 2)

        );
    }
}
