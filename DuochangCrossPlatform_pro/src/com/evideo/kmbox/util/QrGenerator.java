package com.evideo.kmbox.util;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


public class QrGenerator {
	
	private static int FOREGROUND_COLOR=0xff000000;
	private static int BACKGROUND_COLOR=0xffffffff;

	
	
/*	public static Bitmap getQrBitmap(String url,int destWidth,int destHeight) throws Exception{     
	    if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url or bitmap is null");
        }
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 1);
        
        BitMatrix matrix = new MultiFormatWriter().encode(url,
                BarcodeFormat.QR_CODE, destWidth, destHeight, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = FOREGROUND_COLOR;
                } else {
                    pixels[y * width + x] = BACKGROUND_COLOR;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
	}
*/
    public static Bitmap createQRImage(String url,int destWidth,int destHeight,boolean highCorrection) throws Exception{        
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url or bitmap is null");
        }
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        if (highCorrection) {
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        } else {
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        }
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix matrix = new MultiFormatWriter().encode(url,
                BarcodeFormat.QR_CODE, destWidth, destHeight, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = FOREGROUND_COLOR;
                } else {
                    pixels[y * width + x] = BACKGROUND_COLOR;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    } 
	

 
    public static Bitmap createQRCodeWithFixeWhiteSpace(String code, int widthAndHeight,int whiteSpace/*,String fileName*/) throws Exception {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix matrix = new MultiFormatWriter().encode(code,
                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);
 
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
 
        boolean isFirstBlackPoint = false;
        int startX = 0;
        int startY = 0;
 
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    if (isFirstBlackPoint == false)
                    {
                        isFirstBlackPoint = true;
                        startX = x;
                        startY = y;
                        EvLog.d("createQRCode", "x y = " + x + " " + y);
                    }
                    pixels[y * width + x] = FOREGROUND_COLOR;
                } else {
                    pixels[y * width + x] = BACKGROUND_COLOR;
                }
            }
        }
 
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
         
        // 剪切中间的二维码区域，减少padding区域
        if (startX <= whiteSpace) {
            return bitmap;
        }
 
        int x1 = startX - whiteSpace;
        int y1 = startY - whiteSpace;
        if (x1 < 0 || y1 < 0) {
            return bitmap;
        }
 
        int w1 = width - x1 * 2;
        int h1 = height - y1 * 2;
 
        Bitmap bitmapQR = Bitmap.createBitmap(bitmap, x1, y1, w1, h1);
        if ( !bitmap.isRecycled() ) {
            bitmap.recycle();
            System.gc();
        }
        return bitmapQR;
    }
}
