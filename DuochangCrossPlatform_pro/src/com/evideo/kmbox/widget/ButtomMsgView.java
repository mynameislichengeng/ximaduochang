package com.evideo.kmbox.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evideo.kmbox.R;

public class ButtomMsgView extends LinearLayout {
  
    private static final int ANIM_DURATION_ONE_STEP = 500;
    
    private TextView mTVOffline;
    private TextView mTVOnline;
    private boolean mOnLineFlag = false;

    private ObjectAnimator mVisToInvis;
    private ObjectAnimator mInvisToVis;
    private AnimatorSet mAnimatorSet;
    
    private Interpolator accelerator = new AccelerateInterpolator();
    private Interpolator decelerator = new DecelerateInterpolator();

	public ButtomMsgView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public ButtomMsgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ButtomMsgView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
    	LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.home_page_bottom_msg_lay, this, true);
        
        mTVOffline = (TextView) findViewById(R.id.bottom_msg_tv_offline);
        mTVOnline = (TextView) findViewById(R.id.bottom_msg_tv_online);
        
        mVisToInvis = new ObjectAnimator();
        mVisToInvis.setFloatValues(0f, 90f);
        mVisToInvis.setPropertyName("rotationX");
        mVisToInvis.setDuration(ANIM_DURATION_ONE_STEP);
        mVisToInvis.setInterpolator(accelerator);
        
        mInvisToVis = new ObjectAnimator();
        mInvisToVis.setFloatValues(90f, 0f);
        mInvisToVis.setPropertyName("rotationX");
        mInvisToVis.setDuration(ANIM_DURATION_ONE_STEP);
        mInvisToVis.setInterpolator(decelerator);
        
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(mVisToInvis).before(mInvisToVis);
    }
    
    public TextView getOfflineTipView() {
    	return mTVOffline;
    }

    public TextView getNormalTipView() {
    	return mTVOnline;
    }
    
    public void setOnLineFlag(boolean flag) {
        mOnLineFlag = flag;
    }
    
    /**
     * [��ת]
     */
    public boolean flip() {
        final TextView visibleTV;
        final TextView invisibleTV;
        
        if (mTVOffline.getVisibility() == View.GONE) {
            if (mOnLineFlag) {
                return true;
            }
            visibleTV = mTVOnline;
            invisibleTV = mTVOffline;
        } else {
            visibleTV = mTVOffline;
            invisibleTV = mTVOnline;
        }
        
        mVisToInvis.setTarget(visibleTV);
        mInvisToVis.setTarget(invisibleTV);
        
        mVisToInvis.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                visibleTV.setVisibility(View.GONE);
                invisibleTV.setVisibility(View.VISIBLE);
            }
        });

        mAnimatorSet.start();
        return false;
    }
}
