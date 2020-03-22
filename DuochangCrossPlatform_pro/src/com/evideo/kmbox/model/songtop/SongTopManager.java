/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-3-16     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.model.songtop;

import java.util.ArrayList;
import java.util.List;

import android.accounts.NetworkErrorException;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.SongTopDAO;
import com.evideo.kmbox.exceptionhandler.DataCenterCommuExceptionHandler;
import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.NetUtils;

/**
 * [歌单管理者]
 */
public final class SongTopManager {
    
    /** [歌单数据有效时间] */
    private static final int SONG_TOP_DATA_VALID_TIME = 1000 * 60 * 10;
    
    private ArrayList<SongTop> mDatas = new ArrayList<SongTop>();
    
    private List<ISongTopListListener> mSongTopListListeners = 
            new ArrayList<SongTopManager.ISongTopListListener>();
    
    private GetSongTopListPresenter mGetSongTopListPresenter = new GetSongTopListPresenter();
    
    private boolean mIsLoading = false;
    
    private static SongTopManager sInstance;
    
    private SongTopManager() {
    }
    
    /**
     * [获取SongTopManager实例]
     * @return SongTopManager实例
     */
    public static SongTopManager getInstance() {
        if (sInstance == null) {
            synchronized (SongTopManager.class) {
                if (sInstance == null) {
                    sInstance = new SongTopManager();
                }
            }
        }
        return sInstance;
    }
    
    /**
     * [数据库歌单数据是否有效]
     * @return true 有效    false 无效
     */
    public boolean isCacheDataValid() {
        SongTopDAO dao = DAOFactory.getInstance().getSongTopDAO();
        if (dao.getCountOfAllSongTops() <= 0) {
            return false;
        }
        long savedTimestamp = KmSharedPreferences.getInstance()
                .getLong(KeyName.KEY_SONG_TOP_DATA_TIMESTAMP, 0);
        return (System.currentTimeMillis() - savedTimestamp) <= SONG_TOP_DATA_VALID_TIME;
    }
    
    /**
     * [根据歌单id获取对应歌单歌曲的总数]
     * @param songTopId 歌单id
     * @return 对应歌单歌曲的总数
     */
    public int getTotalNumBySongTopId(int songTopId) {
        SongTop songTop = getSongTopById(songTopId);
        if (songTop != null) {
            return songTop.totalNum;
        }
        return 0;
    }
    
    /**
     * [保存歌单到数据库中]
     * @param songTop 歌单
     * @return true 成功    false 失败
     */
    public boolean save(SongTop songTop) {
        SongTopDAO dao = DAOFactory.getInstance().getSongTopDAO();
        return dao.save(songTop);
    }
    
    /**
     * [根据歌单id获取歌单]
     * @param songTopId 歌单id
     * @return 歌单
     */
    public SongTop getSongTopById(int songTopId) {
        SongTopDAO dao = DAOFactory.getInstance().getSongTopDAO();
        return dao.getSongTopById(songTopId);
    }
    
    /**
     * [获取歌单列表]
     * @return List<SongTop> 歌单列表
     */
    public List<SongTop> getSongTopList() {
        mDatas.clear();
//        addPreData();
        return mDatas;
    }
    
    
    public int getSongTopPosition(int songTopId) {
        for (int i = 0; i < mDatas.size();i++) {
            if (mDatas.get(i) != null && mDatas.get(i).songTopId == songTopId) {
                return i;
            }
        }
        return -1;
    }
    
    
    /**
     * [开启获取歌单列表数据任务]
     */
    public void startGetSongTopListTask() {
        if (mIsLoading) {
            return;
        } else {
            mIsLoading = true;
        }
        if (mGetSongTopListPresenter != null) {
            mGetSongTopListPresenter.start();
        }
        notifySongTopLoading();
    }
    
    /**
     * [保存歌单列表数据到数据库中]
     * @param songTops 歌单列表
     */
    public void saveSongTopDataToDB(List<SongTop> songTops) {
        SongTopDAO dao = DAOFactory.getInstance().getSongTopDAO();
        dao.saveList(songTops);
    }
    
    /**
     * [从数据库中获取歌单列表数据]
     * @return 歌单列表
     */
    public List<SongTop> getSongTopDataFromDB() {
        SongTopDAO dao = DAOFactory.getInstance().getSongTopDAO();
        return dao.getAllSongTops();
    }
    
//    /**
//     * [添加预置数据]
//     */
//    private void addPreData() {
//        if (!mDatas.contains(SongTop.getSongTopDrama())) {
//            mDatas.add(0, SongTop.getSongTopDrama()); 
//        }
//        if (!mDatas.contains(SongTop.getSongTopChild())) {
//            mDatas.add(0, SongTop.getSongTopChild());
//        }
//    }
    
    /**
     * [注册歌单列表监听]
     * @param listener 歌单列表监听器
     */
    public void registSongTopListListener(ISongTopListListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (mSongTopListListeners) {
            if (!mSongTopListListeners.contains(listener)) {
                mSongTopListListeners.add(listener);
            }
        }
    }
    
    /**
     * [注销歌单列表监听]
     * @param listener 歌单列表监听器
     */
    public void unregistSongTopListListener(ISongTopListListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (mSongTopListListeners) {
            mSongTopListListeners.remove(listener);
        }
    }
    
    /**
     * [通知歌单被选中]
     * @param songTop 歌单
     */
    public void notifySongTopSelected(SongTop songTop) {
        if (songTop == null) {
            return;
        }
        synchronized (mSongTopListListeners) {
            int size = mSongTopListListeners.size();
            for (int i = 0; i < size; i++) {
                mSongTopListListeners.get(i).onSongTopSelected(songTop);
            }
        }
    }
    
    /**
     * [通知歌单数据变更]
     */
    public void notifySongTopDataChanged() {
        synchronized (mSongTopListListeners) {
            int size = mSongTopListListeners.size();
            for (int i = 0; i < size; i++) {
                mSongTopListListeners.get(i).onSongTopDataChanged();
            }
        }
    }
    
    /**
     * [通知歌单被点击]
     * @param songTop
     */
    public void notifySongTopClicked(SongTop songTop) {
        if (songTop == null) {
            return;
        }
        synchronized (mSongTopListListeners) {
            int size = mSongTopListListeners.size();
            for (int i = 0; i < size; i++) {
                EvLog.d("zxs","notifySongMenuClicked Top i =" + i);
                mSongTopListListeners.get(i).onSongTopClicked(songTop);
            }
        }
    }
    
    /**
     * [通知歌单加载中]
     */
    public void notifySongTopLoading() {
        synchronized (mSongTopListListeners) {
            int size = mSongTopListListeners.size();
            EvLog.i("wrg", "start loading song top list");
            for (int i = 0; i < size; i++) {
                mSongTopListListeners.get(i).onSongTopLoading();
            }
        }
    }
    
    /**
     * [通知歌单加载中]
     */
    public void notifySongTopLoadFail() {
        synchronized (mSongTopListListeners) {
            int size = mSongTopListListeners.size();
            EvLog.i("wrg", "load song top list fail！");
            for (int i = 0; i < size; i++) {
                mSongTopListListeners.get(i).onSongTopLoadFail();
            }
        }
    }
   
    /**
     * [歌单列表监听器]
     */
    public interface ISongTopListListener {
        
        /**
         * [歌单被选中]
         * @param songTop songTop
         */
        public void onSongTopSelected(SongTop songTop);
        
        /**
         * [通知歌单列表数据变更]
         */
        public void onSongTopDataChanged();

        /**
         * [歌单被点击]
         * @param songTop songTop
         */
        public void onSongTopClicked(SongTop songTop);
        
        /**
         * [歌单加载中]
         */
        public void onSongTopLoading();
        
        /**
         * [加载失败]
         */
        public void onSongTopLoadFail();
  
    }
    
    /**
     * [获取歌单列表数据提供者]
     */
    private class GetSongTopListPresenter extends AsyncPresenter<List<SongTop>> {
        
        /**
         * {@inheritDoc}
         */
        @Override
        protected List<SongTop> doInBackground(Object... params) throws Exception {
            List<SongTop> list = new ArrayList<SongTop>();
            if (isCacheDataValid()) {
                EvLog.d("zxs", "get song top data from db");
                list = getSongTopDataFromDB();
            } else {
                EvLog.d("get song top data from net");
                if (!NetUtils.isNetworkConnected(BaseApplication.getInstance())) {
                    throw new NetworkErrorException("Network conn error.");
                }
                list = DCDomain.getInstance().requestSongTopList();
            }
            return list;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onCompleted(List<SongTop> result, Object... params) {
            EvLog.d("get song top data onCompleted");
            if (result != null && !result.isEmpty()) {
                mDatas.clear();
                mDatas.addAll(result);
            }
            mIsLoading = false;
            notifySongTopDataChanged();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onFailed(Exception exception, Object... params) {
            EvLog.e("get song top data onFailed");
            DataCenterCommuExceptionHandler.getInstance().handle(exception);
            UmengAgentUtil.reportError(exception);
            mIsLoading = false;
            notifySongTopLoadFail();
        }
    }
}
