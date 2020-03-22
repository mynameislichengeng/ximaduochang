package com.evideo.kmbox.model.playerctrl.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.evideo.kmbox.SystemConfigManager;
import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.device.IDeviceConfig;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.model.songinfo.SongCategory;
import com.evideo.kmbox.model.songinfo.SongDataState;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;
import com.evideo.kmbox.util.MathUtil;
import com.evideo.kmbox.model.dao.data.Media;
/**
 * [公播列表管理者]
 */
public class BroadcastListManager {
    private static BroadcastListManager instance = null;
    private int mLocalExistBroadcastSongId;
    private List<KmPlayListItem> mRandomList;
   
    public BroadcastListManager() {
        mRandomList = new ArrayList<KmPlayListItem>();
        mLocalExistBroadcastSongId = KmSharedPreferences.getInstance().getInt(KeyName.KEY_LOCAL_BROADCAST_SONG_ID, 0);
        EvLog.i(" KEY_LOCAL_BROADCAST_SONG_ID:" + mLocalExistBroadcastSongId);
    }

    private void setLocalExistBroadcastSong(int songId) {
        mLocalExistBroadcastSongId = songId;
        KmSharedPreferences.getInstance().putInt(KeyName.KEY_LOCAL_BROADCAST_SONG_ID, songId);
    }
    
    private KmPlayListItem mCurrentBSongItem = null;
    
    public KmPlayListItem getLocalCompleteBSong() {
        return mCurrentBSongItem;
    }
    
    public void setCurrentBSongComplete(Media media,String path) {
        if (mCurrentBSongItem != null) {
            EvLog.i("onDownMediaFinish update getLocalBroadcastSongId");
            mCurrentBSongItem.updateMedia(media);
            mCurrentBSongItem.setVideoPath(path);
            mCurrentBSongItem.updateResourceToDB();
            mCurrentBSongItem.setDataState(SongDataState.STATE_COMPLETE);
            setLocalExistBroadcastSong(mCurrentBSongItem.getSongId());
        }
    }
    public int getCurrentBSongId() {
        return (mCurrentBSongItem != null) ? (mCurrentBSongItem.getSongId()) : (-1);
    }
    
    public static BroadcastListManager getInstance() {
        if(instance == null) {
            synchronized (BroadcastListManager.class) {
                BroadcastListManager temp = instance;
                if(temp == null) {
                  temp = new BroadcastListManager();
                  instance = temp;
                }
            }
         }
         return instance;
    }

    @Override
    public void finalize() {
        instance = null;
    }
    private boolean mNeedUpdateSong = false;
    
    public void setUpdateSong() {
        mNeedUpdateSong = true;
    }
    /**
     * [功能说明] 启动公播任务
     * @return
     */
    public KmPlayListItem getRandomSong() {
        if (mBroadcastScanner != null) {
            if (mBroadcastScanner.isTaskRunning()) {
                return null;
            }
            if (mBroadcastScanner.isCancel()) {
                mBroadcastScanner.cancel();
                mBroadcastScanner.setTaskRuning(false);
                mBroadcastScanner = null;
            }
        }
        
        mBroadcastScanner = new BroadcastScannerPresenter(mNeedUpdateSong);
        mBroadcastScanner.start();
        return null;
    }

    public void setBroadcastScannerListener(BroadcastPrepareListener listener) {
        mBroadcastPrepareListener = listener;
    }
    
    private BroadcastScannerPresenter mBroadcastScanner = null;
    private BroadcastPrepareListener mBroadcastPrepareListener = null;
    
    /**
     * [功能说明] 公播监听通知
     */
    public static interface BroadcastPrepareListener {
        public void onBroadcastPrepared(KmPlayListItem item);
    }
    
    public  class BroadcastScannerPresenter extends AsyncPresenter<KmPlayListItem> {
        private  static final int BROADCAST_SCANNER_UNINIT = 0;
        private  static final int BROADCAST_SCANNER_INIT = 1;
        private  boolean isRunning = false;
        private int broadcastStatus = BROADCAST_SCANNER_UNINIT;
        private boolean mForceUpdate = false;
               
        public BroadcastScannerPresenter(boolean forceUpdate) {
            setTaskRuning(false);
            mForceUpdate = forceUpdate;
        }
        
        public  boolean isTaskRunning() {
            return isRunning;
        }
        
        public  void setTaskRuning(boolean isrunning) {
            isRunning = isrunning;
        }
        
        public int getPrepareBroadcastStatus() {
            return broadcastStatus;
        }
        
        private synchronized void generateLocalList() {
            
            synchronized(this) {
                mRandomList.clear();
        
                PageInfo pageinfo = new PageInfo(0, SystemConfigManager.BROADCAST_SONG_NUM);
        
                List<Song> songList = SongManager.getInstance().getCachedSongList(pageinfo);
        
                for (int i = 0; i < songList.size(); i++) {
                    KmPlayListItem item = new KmPlayListItem(songList.get(i), -1, null,SongCategory.CATEGORY_BROADCAST);
                    // 判断文件是否存在
                    if (songList.get(i).hasCachedMedia()) {
                        this.addItem(item);
                    }
                }
            }
        }
        
        private synchronized void generateOnlineSongList() {
            synchronized(this) {
                mRandomList.clear();
        
                List<Song> songidList = FreeSongListManager.getInstance().getList();
                if (songidList == null) {
                    EvLog.e("OnlineFreeSongManager getOnlineFreeList null");
                    return;
                }
                EvLog.d("generateOnlineSongList----------" + songidList.size());
                for (Song song : songidList) {
                    if (song == null) {
                        continue;
                    }
                    KmPlayListItem item = new KmPlayListItem(song, -1, null,SongCategory.CATEGORY_BROADCAST);
                    this.addItem(item);
                }
            }
        }
        
        private KmPlayListItem getRandomSong() {
            if(mRandomList == null) {
                mRandomList = new ArrayList<KmPlayListItem>();
            }
            if (mRandomList.size() == 0) {
                if (DeviceConfigManager.getInstance().getBroadcastSongType() == IDeviceConfig.BROADCAST_SONG_TYPE_ONLINE) {
                    this.generateOnlineSongList();
                } else {
                    this.generateLocalList();
                }
            } else if (mForceUpdate) {
                EvLog.e("force update list-------------");
                mForceUpdate = false;
                if (DeviceConfigManager.getInstance().getBroadcastSongType() == IDeviceConfig.BROADCAST_SONG_TYPE_ONLINE) {
                    this.generateOnlineSongList();
                } else {
                    this.generateLocalList();
                }
            }
            
            if (mRandomList.size() == 0) {
                EvLog.w("has no broadcast songs");
                return null;
            }
            
            int totalSize = mRandomList.size();
            KmPlayListItem item = null;
            for (int i = 0; i < totalSize;i++) {
                item = mRandomList.get(i);
                if (item.getSongId() == mLocalExistBroadcastSongId) {
                    EvLog.i(item.getSongName() + " is local exist,play");
                    return item;
                }
            }
            
            if (mLocalExistBroadcastSongId != 0) {
                String fileName = FileUtil.concatPath(ResourceSaverPathManager.getInstance().getResourceSavePath(), String.valueOf(mLocalExistBroadcastSongId)); 
                EvLog.i("del " + fileName);
                FileUtil.deleteFile(fileName+".ts");
                FileUtil.deleteFile(fileName+".ts.tmp");
                mLocalExistBroadcastSongId = 0;
            }
            
            int min = 0;
            int max = mRandomList.size();
            int index = MathUtil.getRandomNum(min,max);
            
            item = mRandomList.get(index);
          /*  if (item != null && SongManager.getInstance().isExist(item.getSongId())) {
                if (item.getSongId() == mLastSongId) {
                    EvLog.e("random song is same as last song,change>>>>>>>>>lastSongid=" + mLastSongId);
                    index = index +1;
                    if (index >= max) {
                        index = 0;
                    }
                    item = mRandomList.get(index);
                }
            }*/
           
            if (DeviceConfigManager.getInstance().getBroadcastSongType() == IDeviceConfig.BROADCAST_SONG_TYPE_ONLINE) {
                if (DeviceConfigManager.getInstance().getPlayMode() == IDeviceConfig.PLAY_MODE_DOWN_COMPLETE_PLAY) {
                    item.updateMediaList(item.getSongId());
                }
            } else {
                mRandomList.remove(index);
            }
            return item;
        }
        
        @Override
        protected KmPlayListItem doInBackground(Object... params) throws Exception {
            KmPlayListItem item = null;
            setTaskRuning(true);
            broadcastStatus = BROADCAST_SCANNER_UNINIT;
            while (true && isTaskRunning()) {
                item = getRandomSong();
                
                if (item == null || item.getSong() == null) {
                    break;
                }
                
                if (DeviceConfigManager.getInstance().getBroadcastSongType() == IDeviceConfig.BROADCAST_SONG_TYPE_ONLINE) {
                    break;
                } else {
                    if (item.getSong().hasCachedMedia()) {
                        break;
                    }
                }
            }
            
            if (item != null) {
               /* if (mLastSongId != item.getSongId()) {
                    EvLog.d(item.getSongId() + ", lastSongid=" + mLastSongId);
                    if (mLastSongId != 0) {
                        String fileName = FileUtil.concatPath(ResourceSaverPathManager.getInstance().getResourceSavePath(), String.valueOf(mLastSongId)); 
                        EvLog.i("del " + fileName);
                        FileUtil.deleteFile(fileName+".ts");
                        FileUtil.deleteFile(fileName+".ts.tmp");
                    }
                    mLastSongId = item.getSongId();
                }*/
            }
            
            return item;
        }
        
        private boolean addItem(KmPlayListItem item) {
            if (isAlreadyExistInList(item.getSongId())) {
                EvLog.e( item.getSongId() + " is already exist in randomList");
                return false;
            }
            else {
                mRandomList.add(item);
                return true;
            }
        }
        
        private boolean isAlreadyExistInList(int songID) {
            boolean isAlreadyExist = false;
            Iterator<KmPlayListItem> sListIterator = mRandomList.iterator();
            while (sListIterator.hasNext()) {
                KmPlayListItem e = sListIterator.next();
                if (e.getSongId() == songID) {
                    isAlreadyExist = true;
                }
            }
            
            return isAlreadyExist;
        }

        @Override
        protected void onCompleted(KmPlayListItem result, Object... params) {
            broadcastStatus = BROADCAST_SCANNER_UNINIT;
            if (result != null) {
                broadcastStatus = BROADCAST_SCANNER_INIT;
                // TODO 公播数据准备好，抛出item到播控进行公播
                if (mBroadcastPrepareListener != null) {
                    mBroadcastPrepareListener.onBroadcastPrepared(result);
                }
                if (mCurrentBSongItem == null) {
                    mCurrentBSongItem = result;
                }
            } else {
                if (mBroadcastPrepareListener != null) {
                    mBroadcastPrepareListener.onBroadcastPrepared(result);
                }
            }
            setTaskRuning(false);
            mBroadcastScanner = null;
        }

        @Override
        protected void onFailed(Exception exception, Object... params) {
            broadcastStatus = BROADCAST_SCANNER_UNINIT;
            EvLog.e(exception.getMessage());
            UmengAgentUtil.reportError(exception);
            setTaskRuning(false);
            if (mBroadcastPrepareListener != null) {
                mBroadcastPrepareListener.onBroadcastPrepared(null);
            }
            mBroadcastScanner = null;
        }
    }
}
