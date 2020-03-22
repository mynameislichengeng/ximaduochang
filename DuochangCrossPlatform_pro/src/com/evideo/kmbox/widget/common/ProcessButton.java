package com.evideo.kmbox.widget.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.Button;

import com.evideo.kmbox.R;

public abstract class ProcessButton extends Button {

    private int mProgress;
    private int mMaxProgress;
    private int mMinProgress;
    private GradientDrawable mProgressDrawable;
    private GradientDrawable mProgressDrawableBg;
	public ProcessButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public ProcessButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ProcessButton(Context context) {
		super(context);
		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		mMinProgress = 0;
		mMaxProgress = 100;
	}

	public void setProgress(int progress) {
		mProgress = progress;

		onProgress();
		invalidate();
	}

	public void onProgress() {
		setText(getResources().getString(R.string.settings_about_updating_progress_tip, mProgress));
		Drawable drawable = getResources().getDrawable(R.drawable.button_blue_abount_progress_bg);
		setBackgroundCompat(drawable);
	}

	public void onNormalState(String mcomtext) {
		if (mcomtext != null) {
			setText(mcomtext);
		}

		mProgress = 0;

		setBackgroundCompat(getResources().getDrawable(R.drawable.button_green_about_check_update));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// progress
		if (mProgress >= mMinProgress && mProgress <= mMaxProgress) {
			drawProgress(canvas);
		}

		super.onDraw(canvas);
	}

	public abstract void drawProgress(Canvas canvas);

	public int getProgress() {
		return mProgress;
	}

	public int getMaxProgress() {
		return mMaxProgress;
	}

	public int getMinProgress() {
		return mMinProgress;
	}

	public GradientDrawable getProgressDrawable() {
		return mProgressDrawable;
	}

	public void setProgressDrawable(GradientDrawable progressDrawable) {
		mProgressDrawable = progressDrawable;
	}

	public GradientDrawable getmProgressDrawableBg() {
		return mProgressDrawableBg;
	}

	public void setmProgressDrawableBg(GradientDrawable mProgressDrawableBg) {
		this.mProgressDrawableBg = mProgressDrawableBg;
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.mProgress = mProgress;

		return savedState;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (state instanceof SavedState) {
			SavedState savedState = (SavedState) state;
			mProgress = savedState.mProgress;
			super.onRestoreInstanceState(savedState.getSuperState());
			setProgress(mProgress);
		} else {
			super.onRestoreInstanceState(state);
		}
	}

	/**
	 * A {@link android.os.Parcelable} representing the
	 * {@link com.dd.processbutton.ProcessButton}'s state.
	 */
	public static class SavedState extends BaseSavedState {

		private int mProgress;

		public SavedState(Parcelable parcel) {
			super(parcel);
		}

		private SavedState(Parcel in) {
			super(in);
			mProgress = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(mProgress);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
	
	@SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public void setBackgroundCompat(Drawable drawable) {
        int pL = getPaddingLeft();
        int pT = getPaddingTop();
        int pR = getPaddingRight();
        int pB = getPaddingBottom();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
        setPadding(pL, pT, pR, pB);
    }
}
