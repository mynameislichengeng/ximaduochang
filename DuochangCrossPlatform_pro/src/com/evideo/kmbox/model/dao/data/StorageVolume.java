package com.evideo.kmbox.model.dao.data;

import java.io.File;

import android.os.Environment;

import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.util.FileUtil;

public class StorageVolume {
    private String mUUID;
    private String mLabel;
    private String mPath;
    private boolean mIsScanned;
    private long mResourceSize;
    private boolean mIsReady;
    
    public StorageVolume(String uuid, String label, String path) {
        this(uuid, label, path, false);
    }
    
    public StorageVolume(String uuid, String label, String path, boolean scanned) {
        mUUID = uuid;
        mLabel = label;
        mPath = path;
        mIsScanned = scanned;
        mResourceSize = 0;
        mIsReady = false;
    }

    public String getUUID() {
        return mUUID;
    }
    
    public void setUUID(String uuid) {
        mUUID = uuid;
    }
    
    public boolean isScanned() {
        return mIsScanned;
    }
    
    public void setScanned(boolean scanned) {
        mIsScanned = scanned;
    }
    
    public String getLabel() {
        return mLabel;
    }
    
    public void setLabel(String label) {
        mLabel = label;
    }
    
    public String getPath() {
        return mPath;
    }
    
    public void setPath(String path) {
        mPath = path;
    }
    
    public String getResourcePath() {
    	// FXIME
        return ResourceSaverPathManager.getInstance().getResourceSavePath();
    }
    
    public String getMediaPath() {
        return ResourceSaverPathManager.getInstance().getMediaPath();
    }
    
    public String getSubtitlePath() {
        return ResourceSaverPathManager.getInstance().getSubtitlePath();
    }

    public long getReousrceSize() {
        return mResourceSize;
    }
    
    public void setResourceSize(long size) {
        mResourceSize = size;
    }
    
    public boolean isKmVolume() {
        File f = new File(mPath);
        
        if(f.exists() && f.isDirectory()) {
            String path1 = FileUtil.concatPath(mPath, StorageConstant.CONFIG_FILE_MEDIA_PATH);
            if(path1 == null) {
                return false;
            }
            File f1 = new File(path1);
            if(f1.exists()) {
                return true;
            }
        }

        return false;
    }
    
    public boolean isInternalSDCard() {
        return mPath.startsWith(Environment.getExternalStorageDirectory().toString());
    }

    public boolean isExternalSDCard() {
    	return mPath.startsWith("/mnt/external_sd"/*StorageConstant.EXTERNAL_SDCARD_ROOT*/);
    }

    public String getResVideoPath() {
        return ResourceSaverPathManager.getInstance().getResourceSavePath();
    }
    
    public boolean isReady() {
        return mIsReady;
    }
    
    public void setIsReady(boolean isReady) {
        mIsReady = isReady;
    }
    
    public static boolean isScanned(String path) {
        return false;
    }
}
