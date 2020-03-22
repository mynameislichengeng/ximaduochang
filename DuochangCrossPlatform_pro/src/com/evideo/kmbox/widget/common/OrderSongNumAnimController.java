package com.evideo.kmbox.widget.common;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * [已点数量图标动画控制器]
 */
public class OrderSongNumAnimController {
    
    private static final int ONE_STEP_DURATION = 200;
    private static boolean sIsOnAnim = false;
    private static AnimationEndListener sListener = null;
    
    public static void startAnim(View view) {
        if (sIsOnAnim || view == null) {
            return;
        }
        
        ValueAnimator zoomInAnimX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.3f);
        zoomInAnimX.setDuration(ONE_STEP_DURATION);
        zoomInAnimX.setInterpolator(new DecelerateInterpolator());
        ValueAnimator zoomInAnimY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.3f);
        zoomInAnimY.setDuration(ONE_STEP_DURATION);
        zoomInAnimY.setInterpolator(new DecelerateInterpolator());
        
        AnimatorSet zoomInAnim = new AnimatorSet();
        zoomInAnim.play(zoomInAnimX).with(zoomInAnimY);
        
        ValueAnimator zoomOutAnimX = ObjectAnimator.ofFloat(view, "scaleX", 1.3f, 1.0f);
        zoomOutAnimX.setDuration(ONE_STEP_DURATION);
        zoomOutAnimX.setInterpolator(new AccelerateInterpolator());
        zoomOutAnimX.addListener(new AnimatorListenerAdapter() {
          /**
           * {@inheritDoc}
           */
            @Override
            public void onAnimationEnd(Animator animation) {             
                    super.onAnimationEnd(animation);
                    if (sListener != null) {
                        sListener.onAnimationEnd();
                }
            }
        });
        ValueAnimator zoomOutAnimY = ObjectAnimator.ofFloat(view, "scaleY", 1.3f, 1.0f);
        zoomOutAnimY.setDuration(ONE_STEP_DURATION);
        zoomOutAnimY.setInterpolator(new AccelerateInterpolator());
        
        AnimatorSet zoomOutAnim = new AnimatorSet();
        zoomOutAnim.play(zoomOutAnimX).with(zoomOutAnimY);
        
        AnimatorSet set = new AnimatorSet();
        set.play(zoomInAnim).before(zoomOutAnim);
        
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
                
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                sIsOnAnim = false;
            }
        });
        
        set.start();
        
    }
    
    public interface AnimationEndListener {
        public void onAnimationEnd();
    }
    
    public static void setAnimEndListener(AnimationEndListener listener){
        sListener = listener;
    }
}
