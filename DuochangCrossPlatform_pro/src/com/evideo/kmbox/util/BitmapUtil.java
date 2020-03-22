package com.evideo.kmbox.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.text.TextUtils;

/**
 * @brief : Bitmap工具类
 */
public class BitmapUtil {

    public static Bitmap add2BitmapAlign(Bitmap first, Bitmap second) {
        int width = first.getWidth() + second.getWidth();
        int height = Math.max(first.getHeight(), second.getHeight());
        Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(first, 0, 0, null);
        canvas.drawBitmap(second, first.getWidth(), 26, null);
        return result;
    }
    
    public static Bitmap add2Bitmap(Bitmap first, Bitmap second) {
        int width = first.getWidth() + second.getWidth();
        int height = Math.max(first.getHeight(), second.getHeight());
        Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(first, 0, 0, null);
        canvas.drawBitmap(second, first.getWidth(), 0, null);
        return result;
    }
    
   
    
   /* public static Bitmap getBitmapByResID(Context context,int resID) {
        InputStream isFirst = context.getResources().openRawResource(resID); 
        return BitmapFactory.decodeStream(isFirst);
    }*/
    
    public static Bitmap big(Bitmap bitmap,float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale,scale); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
       }
    
    public static Bitmap getBmpByPath(String path){
        if (TextUtils.isEmpty(path)) {
            EvLog.i("getBmpByPath path is null");
            return null;
        }
        
        File file = new File(path);
        if(file != null){
            if (file.exists()) {
                BitmapFactory.Options opt = new BitmapFactory.Options();  
                opt.inPreferredConfig = Bitmap.Config.RGB_565;  
                opt.inPurgeable = true;  
                opt.inInputShareable = true;
                file = null;
                //获取资源图片   
                return BitmapFactory.decodeFile(path, opt); 
            }
        }
        return null;
    }
    
    public static Bitmap getBitmapByResId(Context context, int resId) {  
        BitmapFactory.Options opt = new BitmapFactory.Options();  
        opt.inPreferredConfig = Bitmap.Config.RGB_565;  
        opt.inPurgeable = true;  
        opt.inInputShareable = true;  
        //获取资源图片   
        InputStream is = context.getResources().openRawResource(resId);  
        return BitmapFactory.decodeStream(is, null, opt);  
    }  
    
    /**
     * @brief : [获取按指定比例缩放的bitmap]
     * @param src
     * @param sx
     * @param sy
     * @return
     */
    public static Bitmap getScaledBitmap(Bitmap src, float sx, float sy) {
        if(src == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        Bitmap scaledBitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return scaledBitmap;
    }
       
    public static void saveBitmap(Bitmap bmp , String filepath) throws Exception{
        if (bmp == null) {
            throw new IllegalArgumentException();
        }
        File f = new File(filepath);
       /* if ( f == null) {
            throw new Exception(" filepath qr is error ");
        } */
        FileOutputStream out = new FileOutputStream(f);
        try {
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            throw new Exception("save bitmap fail");
        }finally{
            if (out != null) {
                out.flush();
                out.close();
            }
            f = null;
        }
    }
    
    public static Bitmap  zoomBitmap(Bitmap icon,int h){
        Matrix m = new Matrix();
        float sx = (float) 2 * h / icon.getWidth();
        float sy = (float) 2 * h / icon.getHeight();
        m.setScale(sx, sy);
        return Bitmap.createBitmap(icon, 0, 0,icon.getWidth(), icon.getHeight(), m, false);
    }
    
    public static Bitmap scale(Bitmap b, float x, float y) {
        int w = b.getWidth();
        int h = b.getHeight();
        float sx = (float) x / w;// 要强制转换，不转换我的在这总是死掉。
        float sy = (float) y / h;
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w, h, matrix, true);
        return resizeBmp;
    }
}
