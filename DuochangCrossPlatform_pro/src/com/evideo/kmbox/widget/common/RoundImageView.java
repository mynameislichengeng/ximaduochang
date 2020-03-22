package com.evideo.kmbox.widget.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundImageView extends ImageView {
    
    private int mWidth;
    private int mHeight;
    private Bitmap mSrcBitmap;

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundImageView(Context context) {
        super(context);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        // 获取宽度
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        // match parent || 没有设置初始图片
        if(specMode == MeasureSpec.EXACTLY || mSrcBitmap == null) {
            mWidth = specSize;
        } else {
            // 由图片决定的宽
            int desireByImg = getPaddingLeft() + getPaddingRight() + mSrcBitmap.getWidth();
            if (specMode == MeasureSpec.AT_MOST)// wrap_content
            {
                mWidth = Math.min(desireByImg, specSize);
            }
        }
        
        // 获取高度
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if(specMode == MeasureSpec.EXACTLY || mSrcBitmap == null) {
            mHeight = specSize;
        } else {
            int desire = getPaddingTop() + getPaddingBottom() + mSrcBitmap.getHeight();
            if (specMode == MeasureSpec.AT_MOST)// wrap_content
            {
                mHeight = Math.min(desire, specSize);
            }
        }
        
        setMeasuredDimension(mWidth, mHeight);
    }
    
    @Override
    public void setImageDrawable(Drawable drawable) {
        if(drawable != null) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            mSrcBitmap = bd.getBitmap();
        }
        super.setImageDrawable(drawable);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        drawCircleImage(canvas);
    }
    
    private void drawCircleImage(Canvas canvas) {
        if(mSrcBitmap == null) {
            return;
        }
        int min = Math.min(mWidth, mHeight);
        mSrcBitmap = Bitmap.createScaledBitmap(mSrcBitmap, min, min, false);
        canvas.drawBitmap(createCircleImage(mSrcBitmap, min), 0, 0, null);
    }
    
    /**
     * 根据原图和变长绘制圆形图片
     * 
     * @param source
     * @param min
     * @return
     */
    private Bitmap createCircleImage(Bitmap source, int min)
    {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
        /**
         * 产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(target);
        /**
         * 首先绘制圆形
         */
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        /**
         * 使用SRC_IN，参考上面的说明
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /**
         * 绘制图片
         */
        canvas.drawBitmap(source, 0, 0, paint);
        
        // 绘制圆形边框
//        paint.setXfermode(null);
//        paint.setColor(Color.parseColor("#266ee7"));
//        paint.setStyle(Style.STROKE);
//        paint.setStrokeWidth(2);
//        canvas.drawCircle(min / 2, min / 2, min / 2 - 1, paint);
        
        return target;
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mSrcBitmap != null) {
            mSrcBitmap.recycle();
            mSrcBitmap = null;
        }
    }

}
