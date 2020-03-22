/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年8月10日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.util;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * [功能说明]
 */
public class ApkInfoUtil {
    private static final String TAG = "ApkInfoUtil";
    
    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            EvLog.e(TAG, e.getMessage());
        }
        return verCode;
    }
   
    public static String getVerName(Context context) {
        String verName = "";
        try {
            EvLog.i("getPackageName:" + context.getPackageName());
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            EvLog.e(TAG, e.getMessage());
        }
        return verName;   
    }
    
    /*public static String getAppName(Context context) {
        String verName = context.getResources().getText(R.string.app_name).toString();
        return verName;
    }*/
    
    public static String getPackageVersion(Context context) {
        String version = "";
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            EvLog.e(e.getMessage());
//            UmengAgentUtil.reportError(context,e);
        }
        return version;
    }
    
    public static String getAndroidId(Context context) {
        return android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }
    
    public static String getAppName(Context context) {
        String appName = "";
        try {
            PackageManager pm = context.getPackageManager();
            /*
             * PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
             * PackageManager.GET_CONFIGURATIONS);
             */
            appName = context.getApplicationInfo().loadLabel(pm).toString();

        } catch (Exception e) {
            EvLog.e("getPackageInfo error \n" + e.getMessage());
        }
        return appName;
    }
    
    public static String getAppPackageName(Context context) {
        String appPackageName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            appPackageName = pi.packageName;

        } catch (Exception e) {
            EvLog.e("getPackageInfo error \n" + e.getMessage());

        }
        return appPackageName;
    }
    
}
