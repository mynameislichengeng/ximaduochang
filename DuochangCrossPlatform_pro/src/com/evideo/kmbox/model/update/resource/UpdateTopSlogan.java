/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年10月26日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.update.resource;

import android.text.TextUtils;

import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.datacenter.ResourceInfo;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;
import com.evideo.kmbox.util.HttpUtil;
import com.evideo.kmbox.util.HttpUtil.HttpDownInfo;

/**
 * [功能说明] 更新排行子页面的顶部提示
 */
public class UpdateTopSlogan {
    public static final int UPDATE_INTERVAL = 15;
   /* public static final String TOP_SLOGAN_DIR =  Environment.getExternalStorageDirectory()
            + "/kmbox/download/topslogan/";*/
    
    public static final String TAG = "UpdateTopSlogan";
    
    public static void init() {
        //应用启动时，清空文件夹
        if (FileUtil.isFileExist(ResourceSaverPathManager.getInstance().getTopSlogan())) {
            FileUtil.deleteDir(ResourceSaverPathManager.getInstance().getTopSlogan());
        }
    }
    
}
