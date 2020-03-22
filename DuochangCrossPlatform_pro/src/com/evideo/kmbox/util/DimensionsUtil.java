package com.evideo.kmbox.util;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.View.MeasureSpec;


/**
 * 尺寸计算工具类
 * @brief : [功能说明]
 */
public class DimensionsUtil {
    
    private static Resources getResources(Context context){
        return context.getResources();
    }
    
    public static int DIPToPX(Context context, float dipValue){ 
        final float scale = getResources(context).getDisplayMetrics().density; 
        return (int)(dipValue * scale + 0.5f);
    }
    
    public static int PXToDIP(Context context, float pxValue){ 
        final float scale = getResources(context).getDisplayMetrics().density; 
        return (int)( ( pxValue - 0.5f) / scale); 
    }
    
    /**
     * @brief : [根据指定尺寸的资源id返回尺寸大小，单位px，返回类型为float类型]
     * @param context
     * @param resid
     * @return
     */
    public static float getDimension(Context context, int resid) {
        return getResources(context).getDimension(resid);
    }
    
    /**
     * @brief : [根据指定尺寸的资源id返回尺寸大小，单位px，返回类型为int类型]
     * @param context
     * @param resid
     * @return
     */
    public static int getDimensionPixelSize(Context context, int resid) {
        return getResources(context).getDimensionPixelSize(resid);
    }
    
    public static void measureView(View view){
        final int widthMeasureSpec =
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec =
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
    }
    
}
