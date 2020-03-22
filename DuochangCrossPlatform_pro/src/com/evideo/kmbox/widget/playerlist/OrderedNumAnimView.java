package com.evideo.kmbox.widget.playerlist;

import com.evideo.kmbox.R;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * [已点歌曲数量显示控件]
 */
public class OrderedNumAnimView extends View implements AnimatorUpdateListener {
    
    private static final int ANIM_DURATION = 100;
    
    private boolean mOnAnim = false;
    
    private boolean mDrawSmallBg = false;
    
    private Paint mPaint;
    private String mText;
    private String mOrderedText;
    
    private int mWidth;
    private int mHeight;
    private int mOrginalWidth;
    private int mTextClipGap;
    private int mFirstTextPos;
    
    private int mNumColor;
    private float mFirstTextSize;
    private float mNumTextSize;
    private float mSmallNumTextSize;
    
    private Rect mClipRect;
    private Drawable mBgDrawable;
    private Drawable mSmallBgDrawable;
    
    private ValueAnimator mContractAnim;
    private ValueAnimator mExpandAnim;
    private int mAnimValue;

    public OrderedNumAnimView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public OrderedNumAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OrderedNumAnimView(Context context) {
        super(context);
        init(context);
    }
    
    private void init(Context context) {
        
        mOrderedText = getResources().getString(R.string.tip_ordered_num);
        mFirstTextSize = getResources().getDimension(R.dimen.ordered_num_first_text_size);
        mNumTextSize = getResources().getDimension(R.dimen.ordered_num_text_size);
        mSmallNumTextSize = getResources().getDimension(R.dimen.ordered_num_small_text_size);
        
        mNumColor = getResources().getColor(R.color.red_orderlist_count);
        
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Align.CENTER);
        
        mBgDrawable = getResources().getDrawable(R.drawable.status_ordered_num_bg);
        mSmallBgDrawable = getResources().getDrawable(R.drawable.status_ordered_num_bg_small);
        
        mWidth = getResources().getDimensionPixelSize(R.dimen.ordered_num_bg_width);
        mOrginalWidth = mBgDrawable.getIntrinsicWidth();
        mHeight = mBgDrawable.getIntrinsicHeight();
        mTextClipGap = getResources().getDimensionPixelSize(R.dimen.ordered_num_text_clip_gap);
        mFirstTextPos = getResources().getDimensionPixelSize(R.dimen.ordered_num_first_text_pos);
//        EvLog.d("something", "mBgBitmap width " + mOrginalWidth + " height: " + mHeight);
        initAnimtor();
    }
    
    private void initAnimtor() {
        mContractAnim = ValueAnimator.ofInt(0, (mWidth - mOrginalWidth) / 2);
        mContractAnim.setDuration(ANIM_DURATION);
        mContractAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mOnAnim = true;
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                mOnAnim = false;
                mDrawSmallBg = true;
            }
        });
        mContractAnim.addUpdateListener(this);
        
        mExpandAnim = ValueAnimator.ofInt((mWidth - mOrginalWidth) / 2, 0);
        mExpandAnim.setDuration(ANIM_DURATION);
        mExpandAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mOnAnim = true;
                mDrawSmallBg = false;
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                mOnAnim = false;
            }
        });
        mExpandAnim.addUpdateListener(this);
    }
    
    /**
     * [收缩动画]
     * @return 返回收缩动画
     */
    public ValueAnimator getContractAnim() {
        return mContractAnim;
    }
    
    /**
     * [膨胀动画]
     * @return 返回膨胀动画
     */
    public ValueAnimator getExpandAnim() {
        return mExpandAnim;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        drawView(canvas);
    }
    
    private void drawView(Canvas canvas) {
        
        canvas.save();
        // 裁剪显示区域
        mClipRect = new Rect(0 + mAnimValue, 0, mWidth - mAnimValue, mHeight);
        canvas.clipRect(mClipRect,  Region.Op.REPLACE);
        
        // 画背景
        if (!mDrawSmallBg) {
            mBgDrawable.setBounds(0 + mAnimValue, 0, mWidth - mAnimValue, mHeight);
            mBgDrawable.draw(canvas);
        } else {
            mSmallBgDrawable.setBounds(0 + mAnimValue, 0, mWidth - mAnimValue, mHeight);
            mSmallBgDrawable.draw(canvas);
        }
        
        // 裁剪文字显示区域
        mClipRect = new Rect(0 + mAnimValue + mTextClipGap, 0, mWidth - mAnimValue - mTextClipGap, mHeight);
        canvas.clipRect(mClipRect,  Region.Op.REPLACE);
        
        // 画文字
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(mFirstTextSize);
        canvas.drawText(mOrderedText, mFirstTextPos, (mHeight + mPaint.getTextSize()) / 2 - 2, mPaint);
        
        mPaint.setColor(mNumColor);
        if (!mDrawSmallBg) {
            mPaint.setTextSize(mNumTextSize);
        } else {
            mPaint.setTextSize(mSmallNumTextSize);
        }
        if (!TextUtils.isEmpty(mText)) {
            canvas.drawText(mText, mWidth / 2, (mHeight + mPaint.getTextSize()) / 2 - 2, mPaint);
        } else {
            canvas.drawText("0", mWidth / 2, (mHeight + mPaint.getTextSize()) / 2 - 2, mPaint);
        }
        
        canvas.restore();
        
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }
    
    /**
     * @brief : [开始收缩动画]
     */
    public void startContractAnim() {
        if (mOnAnim) {
            return;
        }
        mContractAnim.start();
    }
    
    /**
     * @brief : [开始扩张动画]
     */
    public void startExpandAnim() {
        if (mOnAnim) {
            return;
        }
        mExpandAnim.start();
    }
    
    /**
     * [是否在动画中]
     * @return true:是;false：否
     */
    public boolean isOnAnim() {
        return mOnAnim;
    }
    
    /**
     * @brief : [设置文字内容]
     * @param text 文字内容
     */
    public void setText(String text) {
        mText = text;
        invalidate();
    }
    
    @Override
    protected void onDetachedFromWindow() {
        if (mBgDrawable != null) {
            mBgDrawable = null;
        }
        if (mPaint != null) {
            mPaint = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mAnimValue = (Integer) animation.getAnimatedValue();
        invalidate();
    }
    
    public void switchToFavoriteCount(int favoriteCount) {
        mOrderedText = getResources().getString(R.string.tip_favorite_num);
        String numHint = String.valueOf(favoriteCount);
        this.setText(numHint);
        invalidate();
    }
    
    public void switchToOrderedCount() {
        mOrderedText = getResources().getString(R.string.tip_ordered_num);
        String numHint = String.valueOf(PlayListManager.getInstance().getCount());
        this.setText(numHint);
        invalidate();
    }
}
