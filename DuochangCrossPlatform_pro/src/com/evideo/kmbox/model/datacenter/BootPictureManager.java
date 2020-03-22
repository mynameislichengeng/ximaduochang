package com.evideo.kmbox.model.datacenter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;

import com.evideo.kmbox.model.dao.data.StorageConstant;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.model.storage.CacheManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.model.update.BackgroundUpdateManager;
import com.evideo.kmbox.model.update.UpdateTimer.IUpdateTimeOutListener;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;
import com.evideo.kmbox.util.HttpFile;
import com.evideo.kmbox.util.SystemInfo;

public class BootPictureManager extends IUpdateTimeOutListener {
    final static String TAG = "BootPictureManager";
    
    private static BootPictureManager sInstance = null;
    /** [后台10分钟更新一次] */
    public static final int UPDATE_INTERVAL = 10;
    
    /**
     * [功能说明]获取单例
     */
    public static BootPictureManager getInstance() {
        if (sInstance == null) {
            synchronized (BootPictureManager.class) {
                if (sInstance == null) {
                    sInstance = new BootPictureManager();
                }
            }
        }
        return sInstance;
    }

   /* private final String PICTURE_PATH = FileUtil.concatPath(
            FileUtil.concatPath(StorageConstant.INTERNAL_SDCARD_ROOT, StorageConstant.STORAGE_VOLUME_KMBOX_ROOT), 
            "picture");*/
    
    private String PICTURE_PATH = "";
    private static  String RESOURCE_KEY = "BootPicture";
    
    private String mVersion = "";
    private long mDuration = 0;
    private String mFilename = "";
    
    public void init() {
        // init pictures
        PICTURE_PATH = FileUtil.concatPath(ResourceSaverPathManager.getInstance().getKmBoxPath(),"picture");
        mVersion = KmSharedPreferences.getInstance().getString(KeyName.KEY_BOOT_PICTURE_VERSION, "");
        mFilename = KmSharedPreferences.getInstance().getString(KeyName.KEY_BOOT_PICTURE_FILENAME, "");
        if (mVersion.length() <= 0 || mFilename.length() <= 0) {
            KmSharedPreferences.getInstance().putString(KeyName.KEY_BOOT_PICTURE_VERSION, "");
            KmSharedPreferences.getInstance().putString(KeyName.KEY_BOOT_PICTURE_FILENAME, "");
            BackgroundUpdateManager.getInstance().addUpdateTask(getInstance(),UPDATE_INTERVAL);
            return;
        }
        
        mDuration = KmSharedPreferences.getInstance().getLong(KeyName.KEY_BOOT_PICTURE_DURATION, 3000);
        if (mDuration <= 0) {
            mDuration = 3000;
        }
        
        BackgroundUpdateManager.getInstance().addUpdateTask(this,UPDATE_INTERVAL);
     }
    
    public boolean hasPicture() {
        if (mFilename.isEmpty() || mVersion.isEmpty()) {
            return false;
        }

        String path = FileUtil.concatPath(PICTURE_PATH, mFilename);
        if (FileUtil.isFileExist(path)) {
            return true;
        }

        KmSharedPreferences.getInstance().putString(KeyName.KEY_BOOT_PICTURE_VERSION, "");
        KmSharedPreferences.getInstance().putString(KeyName.KEY_BOOT_PICTURE_FILENAME, "");

        return false;
    }

    public String getPicutre() {
        if (mFilename.isEmpty() || mVersion.isEmpty()) {
            return "";
        }

        String path = FileUtil.concatPath(PICTURE_PATH, mFilename);
        if (FileUtil.isFileExist(path)) {
            return path;
        }
        
        KmSharedPreferences.getInstance().putString(KeyName.KEY_BOOT_PICTURE_VERSION, "");
        KmSharedPreferences.getInstance().putString(KeyName.KEY_BOOT_PICTURE_FILENAME, "");
        return "";
    }
    
    public long getDuration() {
        return mDuration;
    }
    
    private boolean downloadPicture(String url) {
        HttpFile httpFile = new HttpFile();
        
        if (httpFile.open(url) != 0) {
            return false;
        }

        String filename = httpFile.getFileName();
        if (filename.isEmpty()) {
            filename = "BootPicture.png";
        }

        String filePath = FileUtil.concatPath(PICTURE_PATH, filename);
        InputStream is = httpFile.getInputStream();
        FileOutputStream fileOutputStream = null;
        
        if (!FileUtil.isFileExist(filePath)) {
            FileUtil.createFile(filePath);
        }
        
        if (is != null) {
            try {
                fileOutputStream = new FileOutputStream(filePath, false);
                byte[] buf = new byte[2048];
                int ch = -1;
                while ((ch = is.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, ch);
                }
                
                fileOutputStream.flush();
                
                FileUtil.deleteFile(FileUtil.concatPath(PICTURE_PATH, mFilename));
                mFilename = filename;
                
                return true;
            } catch (FileNotFoundException e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
                return false;
            } catch (IOException e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
                return false;
            } finally {
                
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                        fileOutputStream = null;
                    } catch (IOException e) {
                        EvLog.e(e.getMessage());
                        UmengAgentUtil.reportError(e);
                    }
                }
                
                if (is != null) {
                    try {
                        is.close();
                        is = null;
                    } catch (IOException e) {
                        EvLog.e(e.getMessage());
                        UmengAgentUtil.reportError(e);
                    }
                }
                
                if (httpFile != null) {
                    httpFile.close();
                    httpFile = null;
                }  
            }
        }
        
        return false;
    }

    @Override
    public void timeOut() {
        BootPictureInfo info = null;

        EvLog.d("begin to requestPictureInfo");
        try {
            info = DCDomain.getInstance().requestPictureInfo(RESOURCE_KEY);
        } catch (Exception e) {
            UmengAgentUtil.reportError(e.getMessage());
            EvLog.e(e.getMessage());
            info = null;
        }
        
        if (info == null) {
            return;
        }
        
        if (mVersion.equals(info.getVersion())) {
            return;
        }

        if (downloadPicture(info.getUrl())) {
            mVersion = info.getVersion();
            mDuration = info.getDuration();
            KmSharedPreferences.getInstance().putString(KeyName.KEY_BOOT_PICTURE_VERSION, mVersion);
            KmSharedPreferences.getInstance().putString(KeyName.KEY_BOOT_PICTURE_FILENAME, mFilename);
            KmSharedPreferences.getInstance().putLong(KeyName.KEY_BOOT_PICTURE_DURATION, mDuration);
        }
    }

    /*@Override
    public boolean timeOutLimited() {
        return false;
    }*/
}
