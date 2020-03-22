package com.evideo.kmbox.widget.anim;

import android.animation.ObjectAnimator;
import android.view.View;


/**
 * [功能说明]右边滑进
 */
public class SlideRightIn extends BaseAnim {
    
    private int mTranslationX = 300;
    
    /**
     * [功能说明]设置滑动距离
     * @param x 滑动距离
     */
    public void setTranslationX(int x) {
        mTranslationX = x;
    }

    @Override
    protected void setupAnimation(View view) {
        getAnimatorSet().playTogether(
                ObjectAnimator.ofFloat(view, "translationX", mTranslationX,0).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "alpha", 0, 1).setDuration(mDuration * 3 / 2));
    }
}
