package com.evideo.kmbox.widget.common;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.evideo.kmbox.R;
import com.evideo.kmbox.util.BitmapUtil;
import com.evideo.kmbox.util.DimensionsUtil;

/**
 * [点歌动画]
 */
public class OrderSongAnimView extends View implements AnimatorUpdateListener {
    
    /** [点歌动作来自点歌界面] */
    public static final int FROM_VIEW_MAIN_MENU = 0;
    /** [点歌动作来自已唱列表] */
    public static final int FROM_VIEW_SUNG_LIST = 1;
    /** [点歌界面动画持续时间] */
    public static final int MAIN_MENU_VIEW_DURATION = 500;
    /** [已唱界面动画持续时间] */
    public static final int SUNG_LIST_VIEW_DURATION = 550;
    private int mAnimDuration;
    private int mWidth = -1;
    private int mTargetTop;
    private int mTargetRight;
    private int mListWidth;
    private Bitmap mDrawBitmap;
    
    private final ArrayList<AnimViewHolder> mHolders = new ArrayList<OrderSongAnimView.AnimViewHolder>();
    
    public OrderSongAnimView(Context context) {
        super(context);
        mTargetTop = DimensionsUtil.getDimensionPixelSize(context, R.dimen.order_song_anim_target_top);
        mTargetRight = DimensionsUtil.getDimensionPixelSize(context, R.dimen.order_song_anim_target_right);
        mListWidth = DimensionsUtil.getDimensionPixelSize(context, R.dimen.ordered_song_list_anim_distance);
    }
    
    /**
     * [开始点歌动画]
     * @param bitmap 动画来源位图
     * @param x 开始x坐标
     * @param y 开始y坐标
     * @param fromView 点歌动画起始位置
     */
    public void startOrderSongAnim(Bitmap bitmap, float x, float y, int fromView) {
//        EvLog.d("startOrderSongAnim x: " + x + " y: " + y);
        if (bitmap == null) {
            return;
        }
        if (mWidth <= 0) {
            mWidth = getWidth();
        }
        
        int mTargetX = 0;

        
        if (fromView == FROM_VIEW_MAIN_MENU) {
            mTargetX = mWidth - mTargetRight;
            mAnimDuration = MAIN_MENU_VIEW_DURATION;
        } else if (fromView == FROM_VIEW_SUNG_LIST) {
            mTargetX = mWidth - mListWidth;
            mAnimDuration = SUNG_LIST_VIEW_DURATION;
        } 
        
        AnimViewHolder holder = new AnimViewHolder(bitmap, x, y);
        mHolders.add(holder);
        
        ValueAnimator yAnim = ObjectAnimator.ofFloat(holder, "y", holder.getY(), mTargetTop);
        yAnim.setDuration(mAnimDuration);
        yAnim.addUpdateListener(this);
        yAnim.setInterpolator(new AccelerateInterpolator());
        
        ValueAnimator xAnim = ObjectAnimator.ofFloat(holder, "x", holder.getX(), mTargetX);
        xAnim.setDuration(mAnimDuration);
        xAnim.setInterpolator(new AccelerateInterpolator());
        
        ValueAnimator alphaAnim = ObjectAnimator.ofFloat(holder, "alpha", holder.getAlpha(), 0);
        alphaAnim.setDuration(mAnimDuration);
        alphaAnim.setInterpolator(new AccelerateInterpolator());
        
        ValueAnimator scaleAnim = ObjectAnimator.ofFloat(holder, "scale", holder.getScale(), 0.2f);
        scaleAnim.setDuration(mAnimDuration);
        
        final AnimatorSet set = new AnimatorSet();
        set.play(yAnim).with(xAnim).with(scaleAnim);
        xAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
//                EvLog.d("OrderSongAnim onAnimationEnd eclipse " + (System.currentTimeMillis()-timeStart));
                AnimViewHolder holder = (AnimViewHolder) ((ObjectAnimator) animation).getTarget();
                if (holder != null) {
                    if (holder.bitmap != null) {
                        holder.bitmap.recycle();
                        holder.bitmap = null;
                    }
                    mHolders.remove(holder);
                }
            }
        });
        set.start();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mHolders.size(); i++) {
            AnimViewHolder holder = mHolders.get(i);
            if (holder.getBitmap().isRecycled()) {
                continue;
            }
            mDrawBitmap = BitmapUtil.getScaledBitmap(holder.getBitmap(), holder.getScale(), holder.getScale());
            canvas.drawBitmap(mDrawBitmap, holder.getX(), holder.getY(), holder.getPaint());
        }
    }
    
    @Override
    protected void onDetachedFromWindow() {
        mHolders.clear();
        if (mDrawBitmap != null) {
            mDrawBitmap.recycle();
            mDrawBitmap = null;
        }
        super.onDetachedFromWindow();
    }

    public class AnimViewHolder {
        
        public AnimViewHolder(Bitmap bitmap, float x, float y) {
            this.bitmap = bitmap;
            this.x = x;
            this.y = y;
            paint = new Paint();
        }
        
        private float x = 0, y = 0;
        private Paint paint;
        private Bitmap bitmap;
        private float scale = 1f;
        private float alpha = 1f;
        
        public Paint getPaint() {
            return paint;
        }
        public void setPaint(Paint paint) {
            this.paint = paint;
        }
        public Bitmap getBitmap() {
            return bitmap;
        }
        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
        public float getX() {
            return x;
        }
        public void setX(float x) {
            this.x = x;
        }
        public float getY() {
            return y;
        }
        public void setY(float y) {
            this.y = y;
        }
        public float getAlpha() {
            return alpha;
        }
        public void setAlpha(float alpha) {
            this.alpha = alpha;
            if (paint != null) {
                paint.setAlpha((int) ((alpha * 255f) + .5f));
            }
        }
        public float getScale() {
            return scale;
        }
        public void setScale(float scale) {
            this.scale = scale;
        }
        
    }
 
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        invalidate();
    }

}
