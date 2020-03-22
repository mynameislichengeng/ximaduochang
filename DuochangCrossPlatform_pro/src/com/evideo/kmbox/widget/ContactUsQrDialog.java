/*package com.evideo.kmbox.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.evideo.kmbox.R;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.widget.common.BaseDialog;

public class ContactUsQrDialog extends BaseDialog {
    
    private SlideBottomAnimatorSet mSlideBottomAnimatorSet = new SlideBottomAnimatorSet();
    private FrostedGlassPresenter mFrostedGlassPresenter;
    private FrameLayout mLayout;
    private LinearLayout mSlideBottomLayout;
    private Activity mActivity;
    
	public ContactUsQrDialog(Activity context) {
        super(context, R.style.TvAlertDialogStyle);
        setContentView(R.layout.dialog_contact_us_qr);
        mActivity = context;
        getWindow().setBackgroundDrawableResource(R.drawable.qr_dialog_bg);
        mLayout = (FrameLayout) findViewById(R.id.contact_us_qr_dialog_layout);
        mSlideBottomLayout = (LinearLayout) findViewById(R.id.slide_bottom_lay);
        mSlideBottomAnimatorSet.start(mSlideBottomLayout);
        mFrostedGlassPresenter = new FrostedGlassPresenter();
        mFrostedGlassPresenter.start();
    }
	
	*//**
	 * {@inheritDoc}
	 *//*
	@Override
	public void onDetachedFromWindow() {
	    super.onDetachedFromWindow();
	    if (mFrostedGlassPresenter != null) {
            mFrostedGlassPresenter.cancel();
            mFrostedGlassPresenter = null;
        }
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		this.dismiss();
		return super.onKeyDown(keyCode, event);
	}
	
	private class FrostedGlassPresenter extends AsyncPresenter<Bitmap> {

        *//**
         * {@inheritDoc}
         *//*
        @Override
        protected Bitmap doInBackground(Object... params) throws Exception {
            if (mActivity.isFinishing()) {
                return null;
            }
            FrostedGlass glass = new FrostedGlass();
            final Bitmap bitmap = glass.getFrostedGlassBitmap(mActivity);
            return bitmap;
        }

        *//**
         * {@inheritDoc}
         *//*
        @Override
        protected void onCompleted(Bitmap result, Object... params) {
            if (result != null) {
                if (mLayout != null) {
                    mLayout.setBackground(new BitmapDrawable(null, result));
                }
            }
        }

        *//**
         * {@inheritDoc}
         *//*
        @Override
        protected void onFailed(Exception exception, Object... params) {
        }
	    
	}
	
	private class SlideBottomAnimatorSet {
	    
	    private static final int DURATION = 1 * 700;

	    protected long mDuration =DURATION ;

	    private AnimatorSet mAnimatorSet = new AnimatorSet();

	    public void start(View view) {
	        reset(view);
	        setupAnimation(view);
	        mAnimatorSet.start();
	    }
	    public void reset(View view) {
	        view.setPivotX(view.getMeasuredWidth() / 2.0f);
	        view.setPivotY(view.getMeasuredHeight() / 2.0f);
	    }


	    public AnimatorSet getAnimatorSet() {
	        return mAnimatorSet;
	    }
	    
	    public void setDuration(long duration) {
	        this.mDuration = duration;
	    }

	    protected void setupAnimation(View view) {
	        getAnimatorSet().playTogether(
	                ObjectAnimator.ofFloat(view, "translationY", 300, 0).setDuration(mDuration),
	                ObjectAnimator.ofFloat(view, "alpha", 0, 1).setDuration(mDuration*3/2)

	        );
	    }
	}
}
*/