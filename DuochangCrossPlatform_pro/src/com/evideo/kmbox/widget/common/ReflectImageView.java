/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-9-29     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * [功能说明]倒影图片
 */
public class ReflectImageView extends ImageView {
    
    private int mWidth;
    private int mHeight;
    private Bitmap mSrcBitmap;
    private Bitmap mReflectBitmap;
    private int mStartReflectColor = 0x40ffffff;
    private int mEndReflectColor = 0x00ffffff;

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public ReflectImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @param context
     * @param attrs
     */
    public ReflectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context
     */
    public ReflectImageView(Context context) {
        super(context);
    }
    
    @Override
    public void setImageDrawable(Drawable drawable) {
        if(drawable != null) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            mSrcBitmap = bd.getBitmap();
            mReflectBitmap = getReflectBitmap();
        }
        super.setImageDrawable(drawable);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setImageBitmap(Bitmap bmp) {
        if (bmp != null) {
            if (mSrcBitmap != null) {
                // mSrcBitmap.recycle();
                mSrcBitmap = null;
            }
            mSrcBitmap = bmp;
            mReflectBitmap = null;
        } else {
            mSrcBitmap = bmp;
            mReflectBitmap = getReflectBitmap();
        }
        super.setImageBitmap(mSrcBitmap);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
      
      // 获取宽度
      int specMode = MeasureSpec.getMode(widthMeasureSpec);
      int specSize = MeasureSpec.getSize(widthMeasureSpec);
      // match parent || 没有设置初始图片
      if(specMode == MeasureSpec.EXACTLY) {
          mWidth = specSize;
      } else {
          if (mSrcBitmap != null) {
              // 由图片决定的宽
              mWidth = getPaddingLeft() + getPaddingRight() + mSrcBitmap.getWidth();
          } else {
              mWidth = specSize;
          }
      }
      // 获取高度
      specMode = MeasureSpec.getMode(heightMeasureSpec);
      specSize = MeasureSpec.getSize(heightMeasureSpec);
      if(specMode == MeasureSpec.EXACTLY || mSrcBitmap == null) {
          mHeight = specSize;
      } else {
          if (mSrcBitmap != null) {
              mHeight = getPaddingTop() + getPaddingBottom() + mSrcBitmap.getHeight();
          } else {
              mHeight = specSize;
          }
      }
      
      setMeasuredDimension(mWidth, mHeight);
  }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Canvas canvas) {
//        Log.d("zxh", "mSrcBitmap width " + mSrcBitmap.getWidth() + " height " + mSrcBitmap.getHeight());
//        Log.d("zxh", "mWidth: " + mWidth + " mHeight: " + mHeight);
        if (mReflectBitmap == null) {
            mReflectBitmap = getReflectBitmap();
        }
        if (mReflectBitmap != null) {
            canvas.drawBitmap(mReflectBitmap, 0, 0, null);
        }
    }
    
    /**
     * [功能说明]设置倒影的开始和结束颜色值
     * @param startColor 开始色值
     * @param endColor  结束色值
     */
    public void setReflectColor(int startColor, int endColor) {
        mStartReflectColor = startColor;
        mEndReflectColor = endColor;
    }
    
    private Bitmap getReflectBitmap() {
        if (mSrcBitmap == null) {
            return null;
        }
        
        if (mHeight <= 0) {
            return null;
        }
        
        Matrix matrix = new Matrix();
        // y轴翻转180°
        matrix.setScale(1, -1);
        
        if (mSrcBitmap.getWidth() != mWidth && mWidth > 0) {
            float sx = (float)mWidth/mSrcBitmap.getWidth();
            matrix.postScale(sx, 1.0f);
        } 
        Bitmap clipBitmap = Bitmap.createBitmap(mSrcBitmap, 0, mSrcBitmap.getHeight() - mHeight, 
                mSrcBitmap.getWidth(), mHeight, matrix, false);
        
        
        
        Bitmap reflectBitmap = Bitmap.createBitmap(mSrcBitmap.getWidth(), mHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(reflectBitmap);
        canvas.drawBitmap(clipBitmap, 0, 0, null);
        
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        
        // 倒影渐变遮罩
        LinearGradient gradient = new LinearGradient(0, 0, 
                0, reflectBitmap.getHeight(), mStartReflectColor, mEndReflectColor, TileMode.CLAMP);
        
        paint.setShader(gradient);
        canvas.drawRect(0, 0, reflectBitmap.getWidth(), reflectBitmap.getHeight(), paint);
        
        clipBitmap.recycle();
        
        return reflectBitmap;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
    
}
