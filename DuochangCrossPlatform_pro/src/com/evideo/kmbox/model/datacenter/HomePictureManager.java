package com.evideo.kmbox.model.datacenter;

import java.util.HashMap;
import java.util.Map;

import android.os.Environment;
import android.text.TextUtils;

import com.evideo.kmbox.model.dao.data.StorageConstant;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.model.update.UpdateTimer.IUpdateTimeOutListener;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;
import com.evideo.kmbox.util.HttpUtil;
import com.evideo.kmbox.util.HttpUtil.HttpDownInfo;

public class HomePictureManager extends IUpdateTimeOutListener {
    final static String TAG = "HomePictureManager";
    public static final int BUFFER_MAX_SIZE = 8 * 1024;
    /** [后台10分钟更新一次] */
    public static final int UPDATE_INTERVAL = 20;
    
    public enum PictureType {
        WINDOW("window"),               //首页大背景
        SONG_MENU_BG("song_menu_bg"),   //歌单背景
        SONG_MENU("song_menu"),         //歌单
        SONG("song"),                   //歌名点歌 
        SINGER("singer"),               //歌星点歌
        NEWEST_SONG("newest_song"),     //最新歌曲
        RED_SONG("red_song"),           //免费试唱
        CHILDREN_SONG("children_song"), //儿童歌曲
        OPERA_SONG("opera_song"),       //梨园戏曲
        MY_SPACE("my_space"),       //我的空间
        TOP("top"),                     //排行
        MARKET("market"),               //应用市场
        ORDER_BY_PHONE("order_by_phone"), //手机点歌
        USER_CENTER("user_center"),      //个人中心
        ABOUT_US("about_us");           //帮助中心
        /*FAVORITE("favorite"),*/ 
        /*SQUARE_DANCE_SONG("square_dance_song"), */ 
        
        private PictureType(String name) {
            mName = name;
        }
        
        public String getName() {
            return mName;
        }
        
        private String mName;
    }
    
    private static HomePictureManager sInstance = null;

    public static HomePictureManager getInstance() {
        if (sInstance == null) {
            synchronized (HomePictureManager.class) {
                if (sInstance == null) {
                    sInstance = new HomePictureManager();
                }
            }
        }
        return sInstance;
    }
    
    private Map<String, String> mFileMap = new HashMap<String, String>();
    private IHomePictureUpdateListener mListener = null;
    private String mVersion = "";
    private String mFolder = "";/*FileUtil.concatPath(
            FileUtil.concatPath(StorageConstant.INTERNAL_SDCARD_ROOT, StorageConstant.STORAGE_VOLUME_KMBOX_ROOT), 
            "picture");*/
    
/*    private HomePictureManager() {
        initInfo();
    }*/
    
    private HomePictureManager() {
        mFolder = FileUtil.concatPath(ResourceSaverPathManager.getInstance().getKmBoxPath(), "picture");
    }
    
    private boolean initInfo() {
        boolean realUpdate = false;
        
        mFileMap.clear();
        mVersion = KmSharedPreferences.getInstance().getString(KeyName.KEY_HOME_PICTURE_VERSION, "");
        EvLog.i("HomePictureManager initInfo:mVersion=" + mVersion);
        if (!mVersion.isEmpty()) {
            String path = "";
            for (int i = 0; i < PictureType.values().length; i++) {
                path = FileUtil.concatPath(mFolder, "home/" + PictureType.values()[i].getName() + ".png");
                
                if (FileUtil.isFileExist(path)) {
                    mFileMap.put(PictureType.values()[i].getName(), path);
                    //EvLog.d("PictureType.values()[i].getName() + ":" + path);
                    realUpdate = true;
                }
            }
        }
        EvLog.e("initInfo size:" + mFileMap.size());
        return realUpdate;
    }

    public void init() {
        if(initInfo()) {
            if (mListener != null) {
                mListener.onHomePictureUpdate();
            }
        }
    }
    
    public boolean isNeedUpdateHome() {
        return mFileMap.size() > 0;
    }
    
    public boolean hasPicture(PictureType type) {
        return mFileMap.containsKey(type.getName());
    }

    public String getPicturePath(PictureType type) {
        return mFileMap.get(type.getName());
    }

    public String getVersion() {
        return mVersion;
    }
    
    public void registeListener(IHomePictureUpdateListener listener) {
        mListener = listener;
    }
    
    public void unregisteListener(IHomePictureUpdateListener listener) {
        if (mListener == listener) {
            mListener = null;
        }
    }

    @Override
    public void timeOut() {
        ResourceInfo info = null;
        
        EvLog.d("timeOut requestResourceInfo");
        
        try {
            info = DCDomain.getInstance().requestResourceInfo("HomeTheme");
        } catch (Exception e) {
            UmengAgentUtil.reportError(e.getMessage());
            EvLog.e(e.getMessage());
            info = null;
        }
        
        String path = FileUtil.concatPath(mFolder, "home");
        if (info == null || info.getErrorCode() != 0) {
            if (FileUtil.isFileExist(path)) {
                EvLog.e("HomeTheme is overdue,delete " + path);
                FileUtil.deleteDir(path);
                if (mListener != null) {
                    mListener.onHomePictureUpdate();
                }
            }
            return;
        }
        
        EvLog.w("HomePictureManager getVerison=" + info.getVersion() + ",mVersion=" +mVersion);
        if (mVersion.equals(info.getVersion())){
            if (FileUtil.isFileExist(path)) {
                return;
            }
        }
        
        EvLog.d("HomePictureManager update version " + info.getVersion() + " >> "+ mFolder);
        EvLog.d("picture url is:"+info.getUrl());
        HttpDownInfo downInfo = HttpUtil.downloadFileWithBufferWrite(info.getUrl(), "HomeTheme.zip", mFolder);
        
        if (downInfo == null || (downInfo.downRet ==  false) || TextUtils.isEmpty(downInfo.downPath)) {
            EvLog.d("HomePictureManager update version " + info.getVersion() + " failed");
            return;
        }
        FileUtil.deleteDir(FileUtil.concatPath(mFolder, "home"));
        FileUtil.unZip(downInfo.downPath, mFolder);
        FileUtil.deleteFile(downInfo.downPath);
            
        KmSharedPreferences.getInstance().putString(KeyName.KEY_HOME_PICTURE_VERSION, info.getVersion());
        if (initInfo()) {
            if (mListener != null) {
                mListener.onHomePictureUpdate();
            }
        } else {
            EvLog.e("get new version hometheme,but not update---");
        }
    }
}
