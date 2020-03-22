/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date             Author             Version  Description
 *  -----------------------------------------------
 *  2015年4月1日	     "wurongquan"        1.0      [修订说明]
 *
 */

package com.evideo.kmbox.widget.playerlist;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * [收藏歌曲数量变化动画控制器]
 */
public class FavoriteSongAnimController {
    private static final int ONE_STEP_DURATION = 700;
    private static boolean sIsOnAnim = false;
    
    public static void startAnim(final View view) {
        if (sIsOnAnim || view == null) {
            return;
        }
        view.setVisibility(View.VISIBLE);
        
        ValueAnimator animTransY = ObjectAnimator.ofFloat(view, "translationY", 0, -60);
        animTransY.setDuration(ONE_STEP_DURATION);
        animTransY.setInterpolator(new AccelerateInterpolator());
        ValueAnimator animAlpha = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.2f); 
        animAlpha.setDuration(ONE_STEP_DURATION);
        animAlpha.setInterpolator(new AccelerateInterpolator());
        ValueAnimator zoomOutAnimX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.7f);
        zoomOutAnimX.setDuration(ONE_STEP_DURATION);
        zoomOutAnimX.setInterpolator(new AccelerateInterpolator());
        ValueAnimator zoomOutAnimY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.7f);
        zoomOutAnimY.setDuration(ONE_STEP_DURATION);
        zoomOutAnimY.setInterpolator(new AccelerateInterpolator());
       
        AnimatorSet set = new AnimatorSet();
        set.play(animTransY).with(animAlpha).with(zoomOutAnimX).with(zoomOutAnimY);
     
        set.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                sIsOnAnim = true;
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
                sIsOnAnim = true;
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                sIsOnAnim = false;
                view.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                sIsOnAnim = false;
            }
        });
        
        set.start();
        
    }
}
