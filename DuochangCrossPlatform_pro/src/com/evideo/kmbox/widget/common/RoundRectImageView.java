/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年9月24日     "dinghaiqiang"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * 自定义的圆角矩形ImageView，可以直接当组件在布局中使用。
 */
public class RoundRectImageView extends ImageView{
    private Paint mMaskPaint = null;
    private Path mMaskPath = null;
    private float mCornerRadius = 26.0f;
    
    
    public RoundRectImageView(Context context) {  
        this(context,null);  
    }  
  
    public RoundRectImageView(Context context, AttributeSet attrs) {  
        this(context, attrs,0);  
    }  
  
    public RoundRectImageView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle); 
        init();
    }  
    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mMaskPaint = new Paint();
        mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mMaskPaint.setAntiAlias(true);
    }
    
    private void generateMaskPath(int width, int height) {
        mMaskPath = new Path();
        mMaskPath.addRoundRect(new RectF(0.0F, 0.0F, width, height), mCornerRadius,mCornerRadius, Path.Direction.CW);
        mMaskPath.setFillType(Path.FillType.INVERSE_WINDING);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if ((w != oldw) || (h != oldh))
            generateMaskPath(w, h);
    }
    
    @Override  
    protected void onDraw(Canvas canvas) {
        // 保存当前layer的透明橡树到离屏缓冲区。并新创建一个透明度爲255的新layer
     int saveCount = canvas.saveLayerAlpha(0.0F, 0.0F, canvas.getWidth(), canvas.getHeight(),  
                255, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);  
        super.onDraw(canvas);  
        if (this.mMaskPath != null) {  
            canvas.drawPath(this.mMaskPath, this.mMaskPaint);  
        }  
        canvas.restoreToCount(saveCount);   
    }
    
    /**
     * [设置圆角矩形的圆角半径，单位为px]
     * @param radius
     */
    public void setRadius(float radius) {
        mCornerRadius = radius;
    }
}