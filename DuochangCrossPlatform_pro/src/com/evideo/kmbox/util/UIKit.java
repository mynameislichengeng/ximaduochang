package com.evideo.kmbox.util;


import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.view.View;

/**
 * @brief      : [文件功能说明]
 */
public class UIKit {
    public static int getTextHeight(String str, Paint paint) {
        Rect rect = new Rect();   // 使用上面的画笔最终绘制出字符串所占的矩形
        paint.getTextBounds(str, 0, str.length(), rect); // 四个参数分别为字符串，起始位置，结束位置，矩形
        // 这时就可以方便的获得宽度和高度了，分别为：
        FontMetrics fm = paint.getFontMetrics();  
        
        return (int)Math.ceil(fm.descent - fm.ascent);  
    }
    
    public static int getTextWidth(String str, Paint paint) {
        return (int) paint.measureText(str);
    }
    
    public static void setBackgroundResource(View view, int resid) {
        int top = view.getPaddingTop();
        int bottom = view.getPaddingBottom();
        int left = view.getPaddingLeft();
        int right = view.getPaddingRight();
        view.setBackgroundResource(resid);
        view.setPadding(left, top, right, bottom);
    }
    
    public static void setBackgroundColor(View view, int color) {
        int top = view.getPaddingTop();
        int bottom = view.getPaddingBottom();
        int left = view.getPaddingLeft();
        int right = view.getPaddingRight();
        view.setBackgroundColor(color);
        view.setPadding(left, top, right, bottom);
    }
    
    public static boolean forceRequestFocus(View view){
        if(view == null){
            return false;
        }
        boolean bFocusableInTouchModeBak = view.isFocusableInTouchMode();
        if(view.requestFocus()){
            return true;
        }
        if(view.getVisibility() != View.VISIBLE){
            return false;
        }
        if(!view.isFocusable()){
            view.setFocusable(true);
        }
        if(!view.isFocusableInTouchMode() && view.isInTouchMode()){
            view.setFocusableInTouchMode(true);            
        }
        boolean bRet = view.requestFocus();
        view.setFocusableInTouchMode(bFocusableInTouchModeBak);
        return bRet;
    }
    
}
