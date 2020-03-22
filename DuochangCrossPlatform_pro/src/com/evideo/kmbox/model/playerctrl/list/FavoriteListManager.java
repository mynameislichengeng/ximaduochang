/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date                  Author            Version  Description
 *  -----------------------------------------------
 *  2015年3月13日         "wurongquan"            1.0     [修订说明]
 */
 

package com.evideo.kmbox.model.playerctrl.list;

import android.os.Message;
import android.util.Log;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.FavoriteListDAO;
import com.evideo.kmbox.model.dao.data.ISongObserver;
import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.dao.data.SongSubject;
import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.datacenter.DataCenterCommu;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.playerctrl.PlayListDAOManager;
import com.evideo.kmbox.model.playerctrl.PlayListDAOManager.ListHandler;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.presenter.CommuPresenter;
import com.evideo.kmbox.presenter.CommuPresenter.CommuCallback;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.NetUtils;
import com.evideo.kmbox.widget.common.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * [收藏列表管理者]
 */
public final class FavoriteListManager implements ISongObserver{
    private static FavoriteListManager instance = null;
    private ArrayList<Integer> mList = null;
    private int mWaitHandleSongId = -1;
    private int mWaitAction = ACTION_NONE;
    private static final int ACTION_NONE = 0;
    private static final int ACTION_ADD = 1;
    private static final int ACTION_DEL = 2;
    
    private ArrayList<Integer> mWaitAddListInLoginMode = null;
    private ArrayList<Integer> mWaitDelListeInLoginMode = null;
    
    public static FavoriteListManager getInstance() {
        if (instance == null) {
            synchronized (FavoriteListManager.class) {
                FavoriteListManager temp = instance;
                if(temp == null) {
                  temp = new FavoriteListManager();
                  instance = temp;
                }
            }
        }
        return instance;    
    }
    
    private FavoriteListManager() {
        mWaitDelListeInLoginMode = new ArrayList<Integer>();
        mWaitAddListInLoginMode = new ArrayList<Integer>();
    }
    
    
    public void init() {
        if (mList != null) {
            return;
        }
        
        FavoriteListDAO mFavoriteListDao =  DAOFactory.getInstance().getFavoriteListDAO();    
        mList = new ArrayList<Integer>();
        mList.addAll(mFavoriteListDao.getList());
        Collections.reverse(mList);

    }
    
    public void uninit() {
        
        if (mList != null) {
            mList.clear();
            mList = null;
        }
        instance = null;
    }
    
    
    /**
     * [往数据库加歌]
     * @param songid 歌曲id
     * @return true:成功;false:失败。
     */
    public boolean addSongImpl(int songid) {
        
        Song song = SongManager.getInstance().getSongById(songid);
        if (song == null) {
            return false;
        }
        synchronized (mList) {
            mList.add(songid);
        }
        
        Message msg = PlayListDAOManager.getInstance().getHandler().obtainMessage();
        msg.what = ListHandler.FAVORITE_LIST_ADD_ITEM;
        msg.arg1 = songid;
        PlayListDAOManager.getInstance().getHandler().sendMessage(msg);
        return true;
    }
    /**
     * [往数据库加歌]
     * @param songid 歌曲id
     * @return true:成功;false:失败。
     */
    public boolean addSong(final int songid) {

        if (!DataCenterCommu.getInstance().isLoginSuccess()) {
            ToastUtil.showLongToast(BaseApplication.getInstance(), BaseApplication.getInstance().getResources().getString(R.string.dc_is_login));
            return false;
        }
        Log.i("gsp", "addSong: 收藏添加歌曲");
        if (addSongImpl(songid)) {
            Log.i("gsp", "addSong: 成功添加歌曲");
            notifyListener();
            if (DeviceConfigManager.getInstance().isSupportUserLogin()) {
                startFavoriateListSyncTask(ACTION_ADD,songid);
            }
            return true;
        }
        return false;
    }
    
    
  /*  private void startFavoriateListAddTask(int songid) {
        List<Integer> list = new ArrayList<Integer>();
        list.add(songid);
        if (mSyncListCommu == null) {
            mSyncListCommu = new FavoriteSyncListCommu();
        }
        mSyncListPresenter = new CommuPresenter(mSyncListCommu);
        mSyncListPresenter.start(ACTION_ADD,list);
    }*/
    
    /**
     * [数据库加歌]
     * @param songids 歌曲id
     * @return true:成功;false:失败。
     */
    //FIXME
    private boolean addSongList(List<Integer> songids, boolean upload) {
        boolean addDB = false;
        
        synchronized (mList) {
            Song song = null;
            for (int i = 0 ; i < songids.size(); i++) {
                song = SongManager.getInstance().getSongById(songids.get(i));
                if (song != null) {
                    mList.add(songids.get(i));
                    addDB = true;
                }
            }
        }
        
        if (addDB) {
            Message msg = PlayListDAOManager.getInstance().getHandler().obtainMessage();
            msg.what = ListHandler.FAVORITE_LIST_ADD_LIST;
            msg.obj = songids;
            msg.arg1 = (upload) ? (1) : (0);
            PlayListDAOManager.getInstance().getHandler().sendMessage(msg);
            this.notifyListener();
            return true;
        }
        
        return false;
    }
    
    
    /**
     * [获取收藏歌曲]
     * @param songid 歌曲id
     * @return true:成功;false:失败。
     */
    public List<Integer> getSongIdsListByUpload(boolean upload) {
        FavoriteListDAO mFavoriteListDao =  DAOFactory.getInstance().getFavoriteListDAO();
        return mFavoriteListDao.getSongIdsListByUpload(upload);
    }
    
    /**
     * [删歌]
     * @param songid 歌曲id
     * @return true:成功;false:失败。
     */
    private boolean delSongList(List<Integer> songids) {
        /*if (mFavoriteListDao.delSongList(songids)) {
            this.notifyListener();
            return true;
        }*/
        boolean updateDB = false;
        synchronized (mList) {
            Song song  = null;
            for (int i = 0 ; i < songids.size(); i++) {
                song = SongManager.getInstance().getSongById(songids.get(i));
                if (song != null) {
                    if (delSongImpl(songids.get(i))) {
                        updateDB = true;
                    }
                }
            }
        }
        if (updateDB) {
            Message msg = PlayListDAOManager.getInstance().getHandler().obtainMessage();
            msg.what = ListHandler.FAVORITE_LIST_DEL_LIST;
            msg.obj = songids;
            PlayListDAOManager.getInstance().getHandler().sendMessage(msg);
            this.notifyListener();
            return true;
        }
        return false;
    }
    
    /**
     * [删歌]
     * @param upload 是否同步过
     * @return true:成功;false:失败。
     */
    public boolean delSongByUploadFlag(boolean upload) {
        FavoriteListDAO mFavoriteListDao =  DAOFactory.getInstance().getFavoriteListDAO();
        if (mFavoriteListDao.delSongListByUploadFlag(upload)) {
            this.notifyListener();
            return true;
        }
        return false;
    }
    
    /**
     * [删歌]
     * @param upload 是否同步过
     * @return true:成功;false:失败。
     */
    public boolean delAllSong() {
        synchronized (mList) {
            mList.clear();
        }
        Message msg = PlayListDAOManager.getInstance().getHandler().obtainMessage();
        msg.what = ListHandler.FAVORITE_LIST_DEL_ALL_ITEM;
        PlayListDAOManager.getInstance().getHandler().sendMessage(msg);
        this.notifyListener();
        return true;
    }
    
    /**
     * [删歌]
     * @param songid 歌曲id
     * @return true:成功;false:失败。
     */
    private boolean delSongImpl(int songid) {
        boolean updateDB = false;
        synchronized (mList) {
            Song song = SongManager.getInstance().getSongById(songid);
            if (song == null) {
                return false;
            }
            for (Integer listItem : mList) {
                if (listItem == songid) {
                    mList.remove(listItem);
                    updateDB = true;
                    break;
                }
            }
        }
        
        if (updateDB) {
            Message msg = PlayListDAOManager.getInstance().getHandler().obtainMessage();
            msg.what = ListHandler.FAVORITE_LIST_DEL_ITEM;
            msg.arg1 = songid;
            PlayListDAOManager.getInstance().getHandler().sendMessage(msg);
            return true;
        }
        return false;
    }
    
    /**
     * [删歌]
     * @param songid 歌曲id
     * @return true:成功;false:失败。
     */
    public boolean delSong(final int songid) {
        if (!DataCenterCommu.getInstance().isLoginSuccess()) {
            ToastUtil.showLongToast(BaseApplication.getInstance(), BaseApplication.getInstance().getResources().getString(R.string.dc_is_login));
            return false;
        }
        if (delSongImpl(songid)) {
            this.notifyListener();
            if (DeviceConfigManager.getInstance().isSupportUserLogin()) {
                startFavoriateListSyncTask(ACTION_DEL,songid);
            }
            return true;
        }
        return false;
    }
    
    private void startFavoriateListSyncTask(int action,int songid) {
        final List<Integer> list = new ArrayList<Integer>();
        list.clear();
        list.add(songid);
        if (mSyncListCommu != null) {
            if (action == ACTION_ADD) {
                if (mWaitAddListInLoginMode == null) {
                    mWaitAddListInLoginMode = new ArrayList<Integer>();
                }
                synchronized (mWaitAddListInLoginMode) {
                    EvLog.i("add " + songid + " to wait add");
                    mWaitAddListInLoginMode.add(songid);
                }
                
            } else if (action == ACTION_DEL) {
                if (mWaitDelListeInLoginMode == null) {
                    mWaitDelListeInLoginMode = new ArrayList<Integer>();
                }
                synchronized (mWaitAddListInLoginMode) {
                    EvLog.i("add " + songid + " to wait del");
                    mWaitDelListeInLoginMode.add(songid);
                }
            }
            return;
        }
        
        if (mSyncListCommu == null) {
            mSyncListCommu = new FavoriteSyncListCommu();
        }
        
        mSyncListPresenter = new CommuPresenter(mSyncListCommu);
        mSyncListPresenter.start(action,list);
    }
        
    private CommuPresenter mSyncListPresenter = null;
    private FavoriteSyncListCommu mSyncListCommu = null;
    public class FavoriteSyncListCommu implements CommuCallback{
        @Override
        public Boolean doCommu(Object... params) throws Exception {
            int action = (Integer)params[0];
            List<Integer> songids = (List<Integer>) params[1];
            if (action == ACTION_DEL) {
                EvLog.i("add FavoriteSyncListCommu");
                return DCDomain.getInstance().requestFavoriteDel(DeviceConfigManager.getInstance().getChipId(), songids);
            } else {
                EvLog.i("del FavoriteSyncListCommu");
                return DCDomain.getInstance().requestFavoriteAdd(DeviceConfigManager.getInstance().getChipId(), songids);
            }
        }

        @Override
        public void commuSuccess() {
            EvLog.d("FavoriateListDel task success");
            mSyncListCommu = null;
            if (mWaitAddListInLoginMode != null && mWaitAddListInLoginMode.size() > 0) {
                List<Integer> ids = new ArrayList<Integer>() ;
                
                synchronized (mWaitAddListInLoginMode) {
                    ids.addAll(mWaitAddListInLoginMode);
                    mWaitAddListInLoginMode.clear();
                }
                
                if (mSyncListCommu == null) {
                    mSyncListCommu = new FavoriteSyncListCommu();
                }
                
                mSyncListPresenter = new CommuPresenter(mSyncListCommu);
                mSyncListPresenter.start(ACTION_ADD,ids);
            } else if (mWaitDelListeInLoginMode != null && mWaitDelListeInLoginMode.size() > 0) {
                List<Integer> ids = new ArrayList<Integer>() ;
                
                synchronized (mWaitDelListeInLoginMode) {
                    ids.addAll(mWaitDelListeInLoginMode);
                    mWaitDelListeInLoginMode.clear();
                }
                
                if (mSyncListCommu == null) {
                    mSyncListCommu = new FavoriteSyncListCommu();
                }
                
                mSyncListPresenter = new CommuPresenter(mSyncListCommu);
                mSyncListPresenter.start(ACTION_DEL,ids);
            }
        }

        @Override
        public void commuFailed(Exception exception) {
            EvLog.d("FavoriateListDel task failed");
            mSyncListCommu = null;
        }
    }
    /**
     * [返回歌曲列表]
     * @return 歌曲列表
     */
    public ArrayList<Integer> getList() {
        final ArrayList<Integer> tempList = new ArrayList<Integer>();
        tempList.clear();
        synchronized (mList) {
            tempList.addAll(mList);
        }
        return tempList;
    }
    
    /**
     * [返回歌曲列表]
     * @return 歌曲列表
     */
    public ArrayList<Integer> getList(PageInfo pageInfo) {
        FavoriteListDAO mFavoriteListDao =  DAOFactory.getInstance().getFavoriteListDAO();
        ArrayList<Integer> tempList = (ArrayList<Integer>) mFavoriteListDao.getList(pageInfo);
        Collections.reverse(tempList);
        return tempList;
    }
    
    /**
     * [获取数量]
     * @return 返回歌曲数量
     */
    public int getCount() {
        synchronized (mList) {
            return mList.size();
        }
    }
    
    /**
     * [判断是否已经存在]
     * @param songid 歌曲id
     * @return true:存在;false：不存在
     */
    public boolean isAlreadyExists(int songid) {
        synchronized (mList) {
            Song song = SongManager.getInstance().getSongById(songid);
            if (song == null) {
                return false;
            }
            for (Integer songItem : mList) {
                if (songItem == songid) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * [收藏变化监听]
     */
    public interface IFavoriteListListener { 
        /**
         * [列表变化时的动作]
         */
        void onFavoriteListChange(); 
    }; 

    private List<IFavoriteListListener> mListeners = new ArrayList<IFavoriteListListener>(); 

    /**
     * [注册监听器]
     * @param listener 监听器
     */
    public void registerListener(IFavoriteListListener listener) { 
        if (listener == null) { 
            return; 
        } 
    
        synchronized (this.mListeners) { 
            if (!this.mListeners.contains(listener)) {
                this.mListeners.add(listener); 
            }
        } 
    } 

    /**
     * [注销监听器]
     * @param listener 监听器
     */
    public void unregisterListener(IFavoriteListListener listener) { 
        if (listener == null) { 
            return; 
        } 

        synchronized (this.mListeners) { 
            mListeners.remove(listener);
        } 
    }
    
    /**
     * [通知注册者]
     */
    public void notifyListener() {
        synchronized (this.mListeners) { 
            for (IFavoriteListListener listener : mListeners) { 
                listener.onFavoriteListChange();
            } 
        }    
    }
    
    /**
     * [功能说明]注册歌曲观察者
     */
    public void registerSongObserver() {
        SongSubject.getInstance().registerSongObserver(this);
    }
    
    /**
     * [功能说明]注销歌曲观察者
     */
    public void unregisterSongObserver() {
        SongSubject.getInstance().unregisterSongObserver(this);
    }

    private boolean realDel = false;
    /**
     * {@inheritDoc}
     */
    @Override
    public void onSongsToBeDeleted(List<Integer> ids) {
        EvLog.i("favoriteList recv song to del message begin");
        long timeStart = System.currentTimeMillis();
        if (ids == null || ids.size() == 0) {
            return;
        }
        List<Integer> list = this.getList();
        realDel = false;
        for (Integer item : list) {
            if (!SongManager.getInstance().isExist(item)) {
                delSong(item);
                realDel = true;
            }
        }
        EvLog.i("favoriteList recv song to del message over" + (System.currentTimeMillis()-timeStart));
    }
    
    @Override
    public void onSongdDeletedFinish() {
        if (realDel) {
            notifyListener();
        }
    }


    public static final int SYNC_STATE_SUCCESS = 0;
    public static final int SYNC_STATE_RUNNING = 1;
    public static final int SYNC_STATE_FAILED = 2;
    private int mSyncState = SYNC_STATE_SUCCESS;
    
    public int getSyncCloudFavoriteState() {
        return mSyncState;
    }
    
    private SyncFavoriateCloudListTask mFavoriateListUploadTask = null;
    /**
     * [功能说明]更新个人收藏歌曲任务
     */
    public void startSyncCloudFavoriateListTask() {
        if (!NetUtils.isNetworkConnected(BaseApplication.getInstance().getBaseContext())) {
            ToastUtil.showLongToast(BaseApplication.getInstance(), BaseApplication.getInstance().getResources().getString(R.string.toast_network_error));
            return;
        }
        
        if (mFavoriateListUploadTask != null) {
            EvLog.i("is FavoriateListUploadTask ing");
            return;
        }
        mSyncState = SYNC_STATE_RUNNING;
        if (mSyncListener != null) {
            mSyncListener.onSyncFavoriteCloudListStart();
        }
        mFavoriateListUploadTask = new SyncFavoriateCloudListTask();
        mFavoriateListUploadTask.start();
    }
    
    //同步云端收藏列表的监听
    public interface ISyncFavoriteCloudListListener {
        public void onSyncFavoriteCloudListSuccess();
        public void onSyncFavoriteCloudListFailed();
        public void onSyncFavoriteCloudListStart();
    }
    
    private ISyncFavoriteCloudListListener mSyncListener = null;
    public void setSyncFavoriteCloudListListener(ISyncFavoriteCloudListListener listener) {
        mSyncListener = listener;
    }
    
    /**
     * [功能说明]收藏列表同步数据中心歌曲任务
     */
    private class SyncFavoriateCloudListTask extends AsyncPresenter<Boolean> {
        
        List<Integer> mDownLoadList = new ArrayList<Integer>();
        @Override
        protected Boolean doInBackground(Object... params) throws Exception {
            EvLog.i("begin to SyncFavoriateCloudListTask ");
            // 向数据中心通知收藏添加歌曲id列表
            boolean ret = false;
            
            List<Integer> list = FavoriteListManager.getInstance().getSongIdsListByUpload(false);
            if (list.size() > 0) {
                ret = DCDomain.getInstance().requestFavoriteAdd(DeviceConfigManager.getInstance().getChipId(), list);
                if (!ret) {
                    EvLog.i("SyncFavoriateCloudListTask requestFavoriteAdd failed");
                    return false;
                }
            }
            
            try {
                int startPos = 0;
                int requstNum = -1;
                mDownLoadList.clear();
                PendingFavoriteListData data = DCDomain.getInstance()
                        .requestFavoriteDownload(DeviceConfigManager.getInstance().getChipId(), startPos, requstNum);
                mDownLoadList.clear();
                mDownLoadList.addAll(data.getList());
                Collections.reverse(mDownLoadList);
            } catch (Exception e){
                return false;
            }
            ret = true;
            return ret;
        }
        

        @Override
        protected void onCompleted(Boolean result, Object... params) {
            if (result != null && result) {
                EvLog.d("FavoriateListUpload task on completed,List.size=" + mDownLoadList.size());
                delAllSong();
                addSongList(mDownLoadList, true);
                mSyncState = SYNC_STATE_SUCCESS;
                if (mSyncListener != null) {
                    mSyncListener.onSyncFavoriteCloudListSuccess();
                }
            } else {
                EvLog.d("FavoriateListUpload task failed");
                mSyncState = SYNC_STATE_FAILED;
                if (mSyncListener != null) {
                    mSyncListener.onSyncFavoriteCloudListFailed();
                }
            }
            mDownLoadList.clear();
            mFavoriateListUploadTask = null;
        }

        @Override
        protected void onFailed(Exception exception, Object... params) {
            if (exception != null) {
                EvLog.e("FavoriateListUpload task error --- \n" );
                UmengAgentUtil.reportError(exception);
            }
            EvLog.d("FavoriateListUpload task failed");
            mDownLoadList.clear();
            mSyncState = SYNC_STATE_FAILED;
            if (mSyncListener != null) {
                mSyncListener.onSyncFavoriteCloudListFailed();
            }
            mFavoriateListUploadTask = null;
        }
    }
}
