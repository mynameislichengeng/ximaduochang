package com.evideo.kmbox.widget.anim;

import android.animation.ObjectAnimator;
import android.view.View;


/**
 * [功能说明]底部翻转
 */
public class RotateBottom extends BaseAnim{

    @Override
    protected void setupAnimation(View view) {
        getAnimatorSet().playTogether(
                ObjectAnimator.ofFloat(view, "rotationX", 90, 0).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "translationY", 300, 0).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "alpha", 0, 1).setDuration(mDuration * 3 / 2));
    }
}
