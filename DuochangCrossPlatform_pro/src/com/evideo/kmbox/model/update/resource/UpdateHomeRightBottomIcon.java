/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年10月26日     hemm     1.0        [修订说明]
 *
 

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

*//**
 * [功能说明]
 *//*
public class UpdateHomeRightBottomIcon {
    public static final int UPDATE_INTERVAL = 11;
    
    private static String HOME_RIGHT_BOTTOM_ICON_DIR =  Environment.getExternalStorageDirectory()
            + "/kmbox/download/homeRightBottomIcon/";
    
    public static void init() {
        //应用启动时，清空文件夹
        if (FileUtil.isFileExist(ResourceSaverPathManager.getInstance().getHomeRightIconSavePath())) {
            FileUtil.emptyDir(ResourceSaverPathManager.getInstance().getHomeRightIconSavePath());
        }
    }
    public static String updateResource() throws Exception {
        ResourceInfo info = null;
        EvLog.d("UpdateHomeRightBottomIcon begin to requestResourceInfo");
        try {
            info = DCDomain.getInstance().requestResourceInfo("HomeRightBottomIcon");
        } catch (Exception e) {
            UmengAgentUtil.reportError("HomeRightBottomIcon update catch exception:" + e.getMessage());
            EvLog.e(e.getMessage());
            info = null;
        }
        
        if (info == null) {
            return "";
        }
        
        if (TextUtils.isEmpty(info.getVersion()) && info.getErrorCode() != 0) {
            String path = ResourceSaverPathManager.getInstance().getHomeRightIconSavePath();
            if (!FileUtil.isDirEmpty(path)) {
                EvLog.e("HomeRightIcon empty " + path);
                FileUtil.emptyDir(path);
            }
            return "";
        }
        
        String version = KmSharedPreferences.getInstance().getString(KeyName.KEY_HOME_RIGHT_BOTTOM_VERSION, "1.0");
        String fileName = info.getVersion() + ".png";
//        String fileFullPath = HOME_RIGHT_BOTTOM_ICON_DIR + fileName;
        String fileFullPath = FileUtil.concatPath(ResourceSaverPathManager.getInstance().getHomeRightIconSavePath(), fileName);
        
        EvLog.i("UpdateHomeRightBottomIcon getVerison=" + info.getVersion() + ",version=" +version);
        if (version.equals(info.getVersion())) {
            if (FileUtil.isFileExist(fileFullPath)) {
                return "";
            }
        }
        
        EvLog.d("requestHomeRightBottomIcon begin to update version " + version);
        FileUtil.deleteFile(fileFullPath);
        
        HttpDownInfo downInfo = HttpUtil.downloadFileWithBufferWrite(info.getUrl(), fileName, ResourceSaverPathManager.getInstance().getHomeRightIconSavePath());
        if (downInfo == null || (downInfo.downRet ==  false) || TextUtils.isEmpty(downInfo.downPath)) {
            return "";
        }
        
        KmSharedPreferences.getInstance().putString(KeyName.KEY_HOME_RIGHT_BOTTOM_VERSION, info.getVersion());
        EvLog.d("UpdateHomeRightBottomIcon down success: " + fileFullPath);
        return fileFullPath;
    }
}
*/