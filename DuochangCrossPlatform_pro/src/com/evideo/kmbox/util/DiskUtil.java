package com.evideo.kmbox.util;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.evideo.kmbox.model.dao.data.StorageManager;
import com.evideo.kmbox.model.dao.data.StorageVolume;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.storage.CacheManager;
import com.evideo.kmbox.model.storage.CacheManager.CacheManagerListener;

/**
 * @brief      : [文件功能说明]
 */
public class DiskUtil {
    
    public static long getSDFreeDisk(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long blockCount = sf.getBlockCount();
            long availCount = sf.getAvailableBlocks();
            Log.d("", "block大小:"+ blockSize+",block数目:"+ blockCount+",总大小:"+blockSize*blockCount/1024+"KB");
            Log.d("", "可用的block数目：:"+ availCount+",剩余空间:"+ availCount*blockSize/1024+"KB");
            return availCount*blockSize/1024;
        }else{
            return 0;
        }
    }
    
    /**
     * [功能说明] 获取指定大小空间的卷标
     * @param needSpace 需要的空间大小
     * @param callback  释放空间的回调
     * @return
     */
    public static StorageVolume getSuitableVolume(long needSpace,CacheManagerListener callback) {
        StorageVolume volume = StorageManager.getInstance().getSuitableVolume(needSpace);
        
        if (volume == null) {
            //获取sdcard目录大小
//            long sdcardDirSize = FileUtil.countLength(StorageConstant.INTERNAL_SDCARD_RES);
            long sdcardDirSize = FileUtil.countLength(ResourceSaverPathManager.getInstance().getResourceSavePath());
            long releaseSize = 0;
            
            //当sdcard目录的实际大小大于1G时，需要释放的空间更多
            if (sdcardDirSize > StorageManager.getInstance().getStorageLimitSize()) {
                releaseSize = sdcardDirSize - StorageManager.getInstance().getStorageLimitSize() + needSpace;
            } else {
                releaseSize = needSpace;
            }
            long cacheReleasedSize = CacheManager.getInstance().releaseCapacityWithCallback(releaseSize, callback);
            EvLog.d("CacheManager release cacheSize=" + cacheReleasedSize + ", need releaseSize=" + releaseSize);
            if (cacheReleasedSize >= releaseSize) {
                volume = StorageManager.getInstance().getSuitableVolume(needSpace);
            } else if (FileUtil.getAvailableSize(ResourceSaverPathManager.getInstance().getResourceSavePath()) 
                    > needSpace + StorageManager.INTERNAL_STORAGE_RESERVED_CAPACITY){
                volume = StorageManager.getInstance().getSuitableVolume(needSpace);
            }
        } 
        
        return volume;
    }
    
}
