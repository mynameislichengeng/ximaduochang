package com.evideo.kmbox.model.storage;

import com.evideo.kmbox.dao.CacheManagerDAO;
import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.model.dao.data.Media;
import com.evideo.kmbox.model.dao.data.MediaManager;
import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.dao.data.SongUtil;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;

import java.io.File;
import java.util.List;

public class CacheManager {

    private static final String TAG = "CacheManager";

    private static CacheManager instance;

    public static CacheManager getInstance() {
        if (instance == null) {
            instance = new CacheManager();
        }

        return instance;
    }

    /**
     * [功能说明] 矫正智能缓存中垃圾数据
     */
    public void initSyncMediaCache() {
        new SyncMediaCachePresenter().start();
    }

    public synchronized boolean addMediaCache(int mediaId, String fullFilePath) {
        CacheManagerDAO dao = DAOFactory.getInstance().getCacheManagerDAO();

        if (dao.isExist(fullFilePath)) {
            return dao.updateMediaInfo(mediaId, fullFilePath);
        }

        return dao.addMediaCache(mediaId, fullFilePath);
    }

    public synchronized boolean addSubtitleCache(int mediaId, String fullFilePath) {
        CacheManagerDAO dao = DAOFactory.getInstance().getCacheManagerDAO();

        if (dao.isExist(fullFilePath)) {
            return dao.updateMediaInfo(mediaId, fullFilePath);
        }

        return dao.addSubtitleCache(mediaId, fullFilePath);
    }

    public synchronized boolean updateMediaCache(int mediaId, String fullFilePath) {
        CacheManagerDAO dao = DAOFactory.getInstance().getCacheManagerDAO();
        if (dao.isExist(mediaId)) {
            return dao.updateMediaCache(mediaId, fullFilePath);
        }
        return false;
    }

    public synchronized boolean updateSubtitleCache(int mediaId, String fullFilePath) {
        CacheManagerDAO dao = DAOFactory.getInstance().getCacheManagerDAO();
        if (dao.isExist(mediaId)) {
            return dao.updateSubtitleCache(mediaId, fullFilePath);
        }
        return false;
    }
    
    /*public boolean addSongAlbumCache(int songId, String file) {
        return false;
    }
    
    public boolean addSingerPicture(int singerId, String file) {
        return false;
    }*/

    public boolean lockResource(long mediaId) {
        CacheManagerDAO dao = DAOFactory.getInstance().getCacheManagerDAO();
        return dao.lockResource(mediaId);
    }

    public void unlockResource(long mediaId) {
        CacheManagerDAO dao = DAOFactory.getInstance().getCacheManagerDAO();
        dao.unlockResource(mediaId);
    }

    public void unlockResourceExcept(long mediaId) {
        CacheManagerDAO dao = DAOFactory.getInstance().getCacheManagerDAO();
        dao.unlockResourceExcept(mediaId);
    }

    public interface CacheManagerListener {
        public boolean isCanDelete(int songid);
    }

    synchronized long releaseCapacityWithCallback(String path, long size) {
        return releaseCapacityWithCallback(path, size, null);
    }

    public synchronized long releaseCapacityWithCallback(long size, CacheManagerListener callback) {
        return releaseCapacityWithCallback(null, size, callback);
    }

    public synchronized long releaseCapacityWithCallback(String path, long size, CacheManagerListener callback) {
        int i = 0;
        boolean isEnough = false;
        long releasedCapacity = 0;
        PageInfo pageInfo = new PageInfo(100);
        MediaCacheItem item = null;

        CacheManagerDAO dao = DAOFactory.getInstance().getCacheManagerDAO();
        List<MediaCacheItem> list = dao.getDeletableMediaCacheList(path, pageInfo);

        boolean canDel = true;
        while (list.size() > 0) {
            while (i < list.size()) {
                item = list.get(i);
                if (callback != null) {
                    canDel = callback.isCanDelete(item.getMediaId());

                    if (!canDel) {
                        EvLog.e("releaseCapacityWithCallback id=" + item.getId() + " can not delete");
                        i++;
                        continue;
                    }
                }

                EvLog.w(TAG, "releaseCapacityWithCallback id=" + item.getMediaId());
                releasedCapacity += releaseMediaFile(item);
                dao.deleteMediaCacheItem(item.getId());
                // 删除media数据
                MediaManager.getInstance().deleteMedia(item.getMediaId());

                if (releasedCapacity >= size) {
                    isEnough = true;
                    i++;
                    break;
                }
                i++;
            }

            if (isEnough) {
                break;
            }

            pageInfo.setPageIndex(pageInfo.getPageIndex() + 1);
            list = dao.getDeletableMediaCacheList(pageInfo);
        }
        // 触发一次删除本地歌曲事件
        /*if (size > 0) {
            releaseCacheTask(size, callback);            
        }*/
        return releasedCapacity;
    }

    public synchronized long releaseCapacity(long size) {
        return releaseCapacityWithCallback(null, size, null);
    }
    
    /*public long getCacheSize() {
        return 0;
    }
    
    public void clearCache() {
        
    }*/
    
    /*public boolean EnsureCapacity(long size) {
        return false;
    }*/

    private long releaseMediaFile(MediaCacheItem item) {
        long length = 0;
        String fullFilePath = item.getFullFilePath();

        if (fullFilePath != null && !fullFilePath.isEmpty()) {
            File file = new File(fullFilePath);
            if (file.exists()) {
                length += file.length();
                boolean success = file.delete();
                if (success) {
                    EvLog.d(TAG, "releaseMediaFile file path " + file.getAbsolutePath() + " successful");
                } else {
                    EvLog.d(TAG, "releaseMediaFile file path " + file.getAbsolutePath() + " failed");
                }

                if (item.getType() == MediaCacheItem.Type_Media
                        || item.getType() == MediaCacheItem.Type_Subtitle) {
                    updateMediaData(item);
                }
            }
        }

        return length;
    }

    private void updateMediaData(MediaCacheItem item) {
        Media media = MediaManager.getInstance().getMedia(item.getMediaId());

        if (media == null) {
            EvLog.e("update media failed:mediaId=" + item.getMediaId());
            return;
        }

        if (item.getType() == MediaCacheItem.Type_Media) {
            media.setLocalFileName("");
        } else if (item.getType() == MediaCacheItem.Type_Subtitle) {
            media.setLocalSubtitleName("");
        } else {
            String msg = "invalid cache item type:" + item.getType();
            EvLog.e(msg);
            UmengAgentUtil.reportError(msg);
            return;
        }

        MediaManager.getInstance().update(media);
    }

    private void releaseCacheTask(long size, CacheManagerListener callback) {
        if (mCacheMediaScannerTask == null) {
            CacheScannerPresenter.setTaskRuning(false);
            mCacheMediaScannerTask = new CacheScannerPresenter(size, callback);
            mCacheMediaScannerTask.start();
            return;
        }
        if (CacheScannerPresenter.isTaskRunning()) {
            return;
        }
        if (mCacheMediaScannerTask.isCancel()) {
            mCacheMediaScannerTask.cancel();
        }
        CacheScannerPresenter.setTaskRuning(false);
        mCacheMediaScannerTask = null;
        mCacheMediaScannerTask = new CacheScannerPresenter(size, callback);
        mCacheMediaScannerTask.start();
    }

    private CacheScannerPresenter mCacheMediaScannerTask = null;

    private static class CacheScannerPresenter extends AsyncPresenter<Void> {

        public static int CacheMediaScanner_UNINIT = 0;
        public static int CacheMediaScanner_INIT = 1;

        private static boolean isRunning = false;
        private int cacheStatus = CacheMediaScanner_UNINIT;
        private CacheManagerListener mCallback;
        private long mDeleteSize = 0l;

        public CacheScannerPresenter(long size, CacheManagerListener callback) {
            mCallback = callback;
            mDeleteSize = size;
            setTaskRuning(false);
        }

        public static boolean isTaskRunning() {
            return isRunning;
        }

        public static void setTaskRuning(boolean isrunning) {
            isRunning = isrunning;
        }

        public int getCacheScanStatus() {
            return cacheStatus;
        }

        @Override
        protected void onFailed(Exception exception, Object... params) {
            cacheStatus = CacheMediaScanner_UNINIT;
            EvLog.e(exception.getMessage());
            UmengAgentUtil.reportError(exception);
            setTaskRuning(false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Void doInBackground(Object... params) throws Exception {
            EvLog.e("SyncMediaCachePresenter doInBackground --------");
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            setTaskRuning(true);
            cacheStatus = CacheMediaScanner_UNINIT;
            List<Integer> datas = SongManager.getInstance().getListIntelligent();
            EvLog.d(" get datas size=" + datas.size());
            for (int i = 0; i < datas.size() && isTaskRunning(); i++) {
                try {
                    EvLog.e("songid " + datas.get(i) + " going to del");
                    SongUtil.deleteFileBySongId(datas.get(i));
                    MediaManager.getInstance().deleteMediasBySongId(datas.get(i));
                } catch (Exception e) {
                    EvLog.e(e.getMessage());
                    UmengAgentUtil.reportError(e);
                }
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onCompleted(Void result, Object... params) {
            cacheStatus = CacheMediaScanner_UNINIT;
            setTaskRuning(false);
        }

    }

    class SyncMediaCachePresenter extends AsyncPresenter<Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) throws Exception {
            EvLog.e("SyncMediaCachePresenter doInBackground --------");
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            FileUtil.emptyDir(ResourceSaverPathManager.getInstance().getResourceSavePath()/*KmConfig.RESOURCE_SAVE_PATH*/);
            return true;
        }

        private void clearSdcardFile(File[] files) {
            CacheManagerDAO dao = DAOFactory.getInstance().getCacheManagerDAO();
            if (files == null) {
                return;
            }

            for (int i = 0; i < files.length; i++) {
                String fullFilePath = files[i].getAbsolutePath();
                if (!dao.isExist(fullFilePath)) {
                    FileUtil.deleteFile(fullFilePath);
                }
            }
        }

        @Override
        protected void onCompleted(Boolean result, Object... params) {

        }

        @Override
        protected void onFailed(Exception exception, Object... params) {

        }
    }
}
