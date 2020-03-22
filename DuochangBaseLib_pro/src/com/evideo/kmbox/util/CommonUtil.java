/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年7月17日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.util;

import java.io.Closeable;
import java.io.IOException;

import android.graphics.Bitmap;


public class CommonUtil {
    public static void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
                closeable = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
   /* public static void recycleBmp(Bitmap bmp) {
        if (bmp != null && !bmp.isRecycled()) {
            bmp.recycle();
            bmp = null;
        }
    }*/
}
