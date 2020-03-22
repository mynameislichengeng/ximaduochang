package com.evideo.kmbox.widget.mainmenu.setting;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.evideo.kmbox.R;

/**
 * [存储进度条控件]
 */
public class StorageProgressBar extends View {
    
    private static int LOCAL_USE_COLOR = 0xff266ee7;
    private static int EX_USE_COLOR = 0xff00c21e;
    private static int AVAILABLE_COLOR = 0xff424956;
    
    private float mTotalProgress;
    private float mLocalUseProgress;
    private float mExUseProgress;
    
    private float mSaveProgress;//节省空间
    
    public StorageProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public StorageProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StorageProgressBar(Context context) {
        super(context);
        init(context);
    }
    
    private void init(Context context) {
        LOCAL_USE_COLOR = getResources().getColor(R.color.blue_storage);
        EX_USE_COLOR = getResources().getColor(R.color.green_storage);
        AVAILABLE_COLOR = getResources().getColor(R.color.gray_storage);
    }
    
    /**
     * @brief : [更新存储盘使用进度]
     * @param total 总大小
     * @param localUse 本地使用存储
     * @param exUse 外部使用存储
     */
    public void updateProgress(float total, float localUse, float exUse) {
        mTotalProgress = total;
        mLocalUseProgress = localUse;
        mExUseProgress = exUse;
        invalidate();
    }
    
    /**
     * @brief : [更新存储盘使用进度]
     * @param total 总大小
     * @param used 已使用大小
     */
    public void updateProgress(float total, float used) {
        mTotalProgress = total;
        mLocalUseProgress = 0;
        mExUseProgress = used;
        invalidate();
    }
    
    /**
     * @brief : [更新存储盘使用进度]
     * @param total 总大小
     * @param used 已使用大小
     */
    public void updateProgressWithSave(float total, float used,float save) {
        mTotalProgress = total;
        mLocalUseProgress = 0;
        mExUseProgress = used;
        mSaveProgress = save;
        invalidate();
    }
    
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPb(canvas);
    }
    
    private void drawPb(Canvas canvas) {
        
        //计算各区间长度
        if (mTotalProgress <= 0 || mLocalUseProgress > mTotalProgress || mExUseProgress > mTotalProgress) {
            return;
        }
        if (mLocalUseProgress < 0 || mExUseProgress < 0 || mSaveProgress < 0) {
            return;
        }
        
        int width = getWidth();
        int height = getHeight();
        float localWidth = mLocalUseProgress / mTotalProgress * width;
        float exWidth = (mExUseProgress - mSaveProgress) / mTotalProgress * width;
        if ((mExUseProgress / mTotalProgress) < 0.05f) {
            exWidth = 0.05f * width;
        }
        float availableWidth = width - localWidth - exWidth;
        
        Log.d("something", "width: " + width
                + " height: " + height + " localWidth: " + localWidth
                + " exWidth: " + exWidth + " availableWidth: " + availableWidth);
        
        if (availableWidth < 0) {
            return;
        }
        
        Paint paint = new Paint();
        paint.setStyle(Style.FILL);
        paint.setAntiAlias(true);
        
        paint.setColor(LOCAL_USE_COLOR);
        RectF localR = new RectF(0, 0, localWidth, height);
        canvas.drawRect(localR, paint);
        
        paint.setColor(EX_USE_COLOR);
        RectF exR = new RectF(localWidth, 0, localWidth + exWidth, height);
        canvas.drawRect(exR, paint);
        
        float saveWidth = mSaveProgress/mTotalProgress * width;
        if (saveWidth > 0) {
            paint.setColor(Color.YELLOW);
            RectF saveR = new RectF(localWidth + exWidth, 0, localWidth + exWidth+saveWidth, height);
            canvas.drawRect(saveR, paint);
        } 
        
        paint.setColor(AVAILABLE_COLOR);
        RectF availR = new RectF(localWidth + exWidth+saveWidth, 0, width, height);
        canvas.drawRect(availR, paint);
    }
}
