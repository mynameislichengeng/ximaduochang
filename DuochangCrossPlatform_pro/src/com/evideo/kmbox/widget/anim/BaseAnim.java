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

import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.view.View;


/**
 * [功能说明]动画基类
 */
public abstract class BaseAnim {

    private static final int DURATION = 1 * 700;

    protected long mDuration =DURATION ;

    private AnimatorSet mAnimatorSet;

    {
        mAnimatorSet = new AnimatorSet();
    }

    protected abstract void setupAnimation(View view);

    /**
     * [功能说明]开始动画
     * @param view 做动画的view
     */
    public void start(View view) {
        if (view == null) {
            return;
        }
        reset(view);
        setupAnimation(view);
        mAnimatorSet.start();
    }
    
    /**
     * [功能说明]重置，设置动画的相对点为view的中心处
     * @param view  做动画的view
     */
    public void reset(View view) {
        if (view == null) {
            return;
        }
        view.setPivotX(view.getMeasuredWidth() / 2.0f);
        view.setPivotY(view.getMeasuredHeight() / 2.0f);
    }


    /**
     * [功能说明]获取AnimatorSet实例
     * @return AnimatorSet实例
     */
    public AnimatorSet getAnimatorSet() {
        return mAnimatorSet;
    }
    
    /**
     * [功能说明]设置动画持续时间
     * @param duration 持续时间
     */
    public void setDuration(long duration) {
        this.mDuration = duration;
    }
    
    /**
     * Returns true if any of the child animations of this AnimatorSet have been started and have
     * not yet ended.
     * @return Whether this AnimatorSet has been started and has not yet ended.
     */
    public boolean isStarted() {
        if (mAnimatorSet == null) {
            return false;
        }
        return mAnimatorSet.isStarted();
    }
    
    /**
     * Returns true if any of the child animations of this AnimatorSet have been started and have
     * not yet ended.
     * @return Whether this AnimatorSet has been started and has not yet ended.
     */
    public boolean isRunning() {
        if (mAnimatorSet == null) {
            return false;
        }
        return mAnimatorSet.isRunning();
    }
    
    /**
     * [功能说明]设置动画监听
     * @param animatorListenerAdapter 动画监听适配器
     */
    public void setAnimListener(AnimatorListenerAdapter animatorListenerAdapter) {
        if (mAnimatorSet != null && animatorListenerAdapter != null) {
            mAnimatorSet.addListener(animatorListenerAdapter);
        }
    }

}
