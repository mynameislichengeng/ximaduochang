package com.evideo.kmbox.widget.anim;

import android.animation.ObjectAnimator;
import android.view.View;


/**
 * [功能说明]旋转（类似报纸特效）
 */
public class NewsPaper extends BaseAnim{

    @Override
    protected void setupAnimation(View view) {
        getAnimatorSet().playTogether(
                ObjectAnimator.ofFloat(view, "rotation", 1080, 720, 360, 0).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "alpha", 0, 1).setDuration(mDuration * 3 / 2),
                ObjectAnimator.ofFloat(view, "scaleX", 0.1f, 0.5f, 1).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "scaleY", 0.1f, 0.5f, 1).setDuration(mDuration));
    }
}
