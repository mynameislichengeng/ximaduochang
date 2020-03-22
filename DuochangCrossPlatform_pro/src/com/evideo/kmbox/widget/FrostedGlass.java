/*package com.evideo.kmbox.widget;

import com.evideo.kmbox.util.EvLog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.os.SystemProperties;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

public class FrostedGlass {
	static final boolean DEBUG = true;

	public Bitmap getFrostedGlassBitmap(Context context)
	  {
	    int width, height;
        WindowManager localWindowManager = (WindowManager)context.getSystemService("window");
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        localWindowManager.getDefaultDisplay().getMetrics(localDisplayMetrics);
        width = localDisplayMetrics.widthPixels/4;
        height = localDisplayMetrics.heightPixels/4;
	    return getFrostedGlassBitmap(context, 5, width, height);
	  }
	
	public Bitmap getFrostedGlassBitmap(Context context, int radius, int width, int height) {
		if (!lowPerformance()) {
			Bitmap localBitmap1 = getScreenShot2(context);
		    
			Bitmap localBitmap2 = null;
			if (localBitmap1 != null) {
				boolean bool = localBitmap1.isRecycled();
		        localBitmap2 = null;
		        
		        if (!bool) {
		            if (localBitmap1.getWidth() <=0 || localBitmap1.getHeight() <= 0) {
		                EvLog.e("localBitmap1 width invalid=" + localBitmap1.getWidth());
		                return null;
		            }
		        	localBitmap2 = Bitmap.createScaledBitmap(localBitmap1, width, height, true);
		        	localBitmap1.recycle();
		        	localBitmap2 = blurBitmap(context, localBitmap2);
		        }
			}
	
			return localBitmap2;
		}

		return null;
	}
	
    private static Bitmap takeScreenShot(Activity activity){   
        //View是你需要截图的View   
        View view = activity.getWindow().getDecorView();   
        view.setDrawingCacheEnabled(true);   
        view.buildDrawingCache();   
        Bitmap b1 = view.getDrawingCache();   
            
        //获取状态栏高度   
        Rect frame = new Rect();     
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);     
        int statusBarHeight = frame.top;     
        System.out.println(statusBarHeight);   
            
        //获取屏幕长和高   
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();     
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();     
        //去掉标题栏   
        //Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);   
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);   
        view.destroyDrawingCache();   
        return b;   
    }  
	
	public Bitmap getScreenShot2(Context context) {
		WindowManager localWindowManager = (WindowManager)context.getSystemService("window");
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        localWindowManager.getDefaultDisplay().getMetrics(localDisplayMetrics);
        Bitmap bitmap = Surface.screenshot(localDisplayMetrics.widthPixels, localDisplayMetrics.heightPixels);
        bitmap.setHasAlpha(false);  
        bitmap.prepareToDraw();
        
        return bitmap;
	}
	
	public boolean lowPerformance() {
		return SystemProperties.getInt("ro.product.performance", 9) == 1;
	}

	@SuppressLint("NewApi")
	public Bitmap blurBitmap(Context context, Bitmap bitmap){  
        
        //Let's create an empty bitmap with the same size of the bitmap we want to blur  
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);  
          
        //Instantiate a new Renderscript  
        RenderScript rs = RenderScript.create(context);  
          
        //Create an Intrinsic Blur Script using the Renderscript  
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));  
          
        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps  
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);  
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);  
          
        //Set the radius of the blur  
        blurScript.setRadius(1.f);  
          
        //Perform the Renderscript  
        blurScript.setInput(allIn);  
        blurScript.forEach(allOut);  
          
        //Copy the final bitmap created by the out Allocation to the outBitmap  
        allOut.copyTo(outBitmap);  
          
        //recycle the original bitmap  
        bitmap.recycle();  
          
        //After finishing everything, we destroy the Renderscript.  
        rs.destroy();  

        return outBitmap;  
          
          
    }  
	
	public static Bitmap fastBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) { 
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }
 
        if (radius < 1) {
            return (null);
        }
 
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
 
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
 
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;
 
        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];
 
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }
 
        yw = yi = 0;
 
        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;
 
        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;
 
            for (x = 0; x < w; x++) {
 
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
 
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
 
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
 
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
 
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];
 
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
 
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
 
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
 
                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];
 
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
 
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
 
                yi++;
            }
            yw += w;
        }

        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;

            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
 
                sir = stack[i + radius];
 
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
 
                rbs = r1 - Math.abs(i);
 
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
 
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
 
                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];
 
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
 
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
 
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
 
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];
 
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
 
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
 
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
 
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
 
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
 
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
 
                yi += w;
            }
        }
 
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
 
        return (bitmap);
    }
}
*/