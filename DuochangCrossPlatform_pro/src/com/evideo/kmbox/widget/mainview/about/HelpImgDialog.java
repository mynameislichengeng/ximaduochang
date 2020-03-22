/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年8月7日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.about;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ViewSwitcher.ViewFactory;

import com.evideo.kmbox.R;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.BaseDialog;
import com.evideo.kmbox.widget.mainview.MainViewManager;

/**
 * [功能说明]
 */
public class HelpImgDialog  extends BaseDialog implements ViewFactory {
    private ImageSwitcher mImageSwitch = null;
    private Context mContext = null;
    private int[] imgIds = {R.drawable.remote_control_guide,R.drawable.micphone_link_guide};  
    private int mCurrentIndex = -1;
   /* private ImageView mLeftBtn = null;
    private ImageView mRightBtn = null;*/
    private Animation mNextOutAnimation = null;
    private boolean mNextOutAnimationEnd = true;
    private Animation mPrevOutAnimation = null;
    private boolean mPrevOutAnimationEnd = true;
    //装载点点的容器    
    private LinearLayout linearLayout = null;  
    //点点数组  
    private ImageView[] tips = null;   
    
    public HelpImgDialog(Context context) {
        super(context, R.style.ActivityDialogStyle);
        setContentView(R.layout.dialog_help_image);
        mContext = context;
        
      /*  mLeftBtn = (ImageView)findViewById(R.id.activity_left_btn);
        mLeftBtn.setImageResource(R.drawable.activity_left_arrow);
        
        mRightBtn = (ImageView)findViewById(R.id.activity_right_btn);
        mRightBtn.setImageResource(R.drawable.activity_right_arrow);*/
        
        linearLayout = (LinearLayout)findViewById(R.id.viewGroup);
        tips = new ImageView[imgIds.length]; 
        for(int i=0; i<imgIds.length; i++){  
            ImageView mImageView = new ImageView(context);  
            tips[i] = mImageView;  
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,      
                    LayoutParams.WRAP_CONTENT));    
            layoutParams.rightMargin = context.getResources().getDimensionPixelOffset(R.dimen.px15);  
            layoutParams.leftMargin = context.getResources().getDimensionPixelOffset(R.dimen.px15);
              
            mImageView.setBackgroundResource(R.drawable.ic_tip_normal);  
            linearLayout.addView(mImageView, layoutParams);  
        }  
        mImageSwitch = (ImageSwitcher)findViewById(R.id.activity_dialog_imageswitcher);
        mImageSwitch.setFactory(this);
        mImageSwitch.setBackgroundResource(R.drawable.toast_bg);
        
        this.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
                if (arg1 == KeyEvent.KEYCODE_DPAD_RIGHT && arg2.getAction() == KeyEvent.ACTION_DOWN) {
                    EvLog.d("recv right ");
                    showNext();
                    return true;
                } else if (arg1 == KeyEvent.KEYCODE_DPAD_LEFT && arg2.getAction() == KeyEvent.ACTION_DOWN) {
                    EvLog.d("recv left ");
                    showPrev();
                    return true;
                } else if (arg1 == KeyEvent.KEYCODE_HOME && arg2.getAction() == KeyEvent.ACTION_DOWN) {
                    HelpImgDialog.this.dismiss();
//                    MainViewManager.getInstance().backToHome();
                }
                return false;
            }
        });
    }
    /**   
     * 设置选中的tip的背景   
     * @param selectItems   
     */    
    private void setTipImageBackground(int selectItems){    
        for(int i=0; i<tips.length; i++){    
            if(i == selectItems){    
                tips[i].setBackgroundResource(R.drawable.ic_tip_hl);    
            }else{    
                tips[i].setBackgroundResource(R.drawable.ic_tip_normal);    
            }    
        }    
    }   
    
    public void setCurrentImageIndex(int index) {
        mCurrentIndex = index;
    }
    
    @Override
    public void show() {
        super.show();
        mCurrentIndex = 0;
        mImageSwitch.setImageResource(imgIds[mCurrentIndex]);
        setTipImageBackground(mCurrentIndex);
      /*  mLeftBtn.setVisibility(View.VISIBLE);
        mRightBtn.setVisibility(View.VISIBLE);*/
    }
    
    @Override
    public void dismiss() {
        super.dismiss();
        mImageSwitch.clearAnimation();
        
    }
    
    public int getCurrentIndex() {
        return mCurrentIndex;
    }
    
    private void showNext() {
        if (!mNextOutAnimationEnd) {
            return;
        }
        
        EvLog.d("showNext mCurrentIndex" + mCurrentIndex);
        mCurrentIndex++;
        if (mCurrentIndex >= imgIds.length) {
            mCurrentIndex = 0;
        }
    
        
        mImageSwitch.setInAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.push_right_in));
        
        if (mNextOutAnimation == null) {
            mNextOutAnimation = AnimationUtils.loadAnimation(mContext,R.anim.push_left_out);
            mNextOutAnimation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mNextOutAnimationEnd = false;
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    mNextOutAnimationEnd = true;
                    setTipImageBackground(mCurrentIndex);
                }
            });
        }
        mImageSwitch.setOutAnimation(mNextOutAnimation);
        mImageSwitch.setImageResource(imgIds[mCurrentIndex]);
        mImageSwitch.setInAnimation(null);
        mImageSwitch.setOutAnimation(null);
    }
    
    private void showPrev() {
        if (!mPrevOutAnimationEnd) {
            return;
        }
        
        mCurrentIndex--;
        if (mCurrentIndex < 0) {
            mCurrentIndex = imgIds.length-1;
        }
        mImageSwitch.setInAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.push_left_in));
        if (mPrevOutAnimation == null) {
            mPrevOutAnimation = AnimationUtils.loadAnimation(mContext,R.anim.push_right_out);
            mPrevOutAnimation.setAnimationListener(new AnimationListener() {
                
                @Override
                public void onAnimationStart(Animation animation) {
                    mPrevOutAnimationEnd = false;
                }
                
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    mPrevOutAnimationEnd = true;
                    setTipImageBackground(mCurrentIndex);
                }
            });
        }
        mImageSwitch.setOutAnimation(mPrevOutAnimation);
        mImageSwitch.setImageResource(imgIds[mCurrentIndex]);
        mImageSwitch.setInAnimation(null);
        mImageSwitch.setOutAnimation(null);
    }

    @Override
    public View makeView() {
        final ImageView i = new ImageView(mContext);  
//        i.setBackgroundColor(0xE0282f41);  
//        i.setBackgroundResource(R.drawable.toast_bg);
        i.setScaleType(ImageView.ScaleType.CENTER_CROP);  
        final ImageSwitcher.LayoutParams param = new ImageSwitcher.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        param.gravity = Gravity.CENTER;
        i.setLayoutParams(param);  
        return i ;  
    }
}
