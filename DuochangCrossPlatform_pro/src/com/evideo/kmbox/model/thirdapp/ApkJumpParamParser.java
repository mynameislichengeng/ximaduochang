/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年1月18日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.thirdapp;

import android.os.Bundle;
import android.text.TextUtils;

import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明]
 */
public class ApkJumpParamParser {
    public static final int JUMP_TYPE_NONE = 0;
    /** [歌单跳转] */
    public static final int JUMP_TYPE_SONG_MENU = 1;
    /** [排行跳转] */
    public static final int JUMP_TYPE_RANK = 2;
    /** [活动跳转] */
    public static final int JUMP_TYPE_HUODONG_BMP = 3;
    /** [单曲跳转] */
    public static final int JUMP_TYPE_HUODONG_HTML = 4;
    
//    private int mExit = 0;
    private int mJumpType = JUMP_TYPE_NONE;
    private String mJumpParam = "";
    
    private static ApkJumpParamParser instance = null;
    public static ApkJumpParamParser getInstance() {
        if(instance == null) {
            synchronized (ApkJumpParamParser.class) {
                ApkJumpParamParser temp = instance;
                if(temp == null) {
                  temp = new ApkJumpParamParser();
                  instance = temp;
                }
            }
         }
         return instance;
    }
    
    public void init(Bundle bundle) {
        EvLog.e(">>>>>>>>>>>init bundle=" +bundle);
        if (bundle == null) {
            return;
        }
        String param = bundle.getString("param");
        if (TextUtils.isEmpty(param)) {
            return;
        }
        String a[] = param.split("&");
        if (a.length < 2) {
            return;
        }
        EvLog.e("0:" + a[0] + ",1:" + a[1] );
        if ( (a[0].indexOf("=") <= 0) || (a[1].indexOf("=") <=0) /*|| (a[2].indexOf("=") <= 0)*/) {
            return;
        }
        
//        mExit = Integer.valueOf(a[0].substring(a[0].indexOf("=")+1));
        mJumpType = Integer.valueOf(a[0].substring(a[0].indexOf("=")+1));
        mJumpParam = (a[1].substring(a[1].indexOf("=")+1));
        EvLog.e("mJumpType:" + mJumpType + ",mJumpParam=" + mJumpParam);
    }       
    
    /*public int getExitParam() {
        return this.mExit;
    }*/
    
    public int getJumpType() {
        return this.mJumpType;
    }
    
    public String getJumpParam() {
        return this.mJumpParam;
    }
}
