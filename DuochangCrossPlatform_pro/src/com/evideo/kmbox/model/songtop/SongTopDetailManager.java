/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-3-30     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.model.songtop;

import java.util.List;

import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.SongTopDetailDAO;
import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.presenter.PageDataInfo;
import com.evideo.kmbox.presenter.PageLoadPresenter;
import com.evideo.kmbox.presenter.PageLoadPresenter.ILoadCacheDataCallback;
import com.evideo.kmbox.presenter.PageLoadPresenter.IPageLoadCallback;
import com.evideo.kmbox.util.EvLog;

/**
 * [歌单详情管理者]
 */
public final class SongTopDetailManager {
    
    /** [歌单详情数据有效时间] */
    private static final int SONG_MENU_DETAIL_DATA_VALID_TIME = 1000 * 60 * 10;
    /** [分页加载数据一页的数量] */
    private static final int PAGE_SIZE = 20;
    
    private SongTopDetailsPageLoadPresenter mSongTopDetailsPageLoadPresenter;
    
    private static SongTopDetailManager sInstance;
    
    private SongTopDetailManager() {
    }
    
    /**
     * [获取歌单详情管理者实例]
     * @return 歌单详情管理者实例
     */
    public static SongTopDetailManager getInstace() {
        if (sInstance == null) {
            synchronized (SongTopDetailManager.class) {
                if (sInstance == null) {
                    sInstance = new SongTopDetailManager();
                }
            }
        }
        return sInstance;
    }
    
    /**
     * [开启获取歌单详情数据任务]
     * @param songTopId 歌单id
     * @param pageLoadCallback 分页加载数据回调接口
     * @param loadCacheDataCallback 加载缓存数据回调接口
     */
    public void startGetSongTopDetailsTask(int songTopId,
            IPageLoadCallback<SongTopDetail> pageLoadCallback, ILoadCacheDataCallback<SongTopDetail> loadCacheDataCallback) {
        if (mSongTopDetailsPageLoadPresenter != null) {
            mSongTopDetailsPageLoadPresenter.stopTask();
        }
        mSongTopDetailsPageLoadPresenter = 
                new SongTopDetailsPageLoadPresenter(songTopId, PAGE_SIZE, pageLoadCallback);
        mSongTopDetailsPageLoadPresenter.setLoadCacheDataCallback(loadCacheDataCallback);
        if (isCacheDataValid(songTopId)) {
            EvLog.d("mSongTopDetailsPageLoadPresenter load cache data");
            mSongTopDetailsPageLoadPresenter.loadCacheData();
        } else {
            EvLog.d("mSongTopDetailsPageLoadPresenter load net data");
            mSongTopDetailsPageLoadPresenter.loadData();
        }
    }
    
    /**
     * [加载下一页数据]
     */
    public void loadNextPage() {
//        EvLog.d("mSongTopDetailsPageLoadPresenter.loadNextPage");
        if (mSongTopDetailsPageLoadPresenter != null) {
            mSongTopDetailsPageLoadPresenter.loadNextPage();
        }
    }
    
    /**
     * [停止加载数据任务]
     */
    public void stopLoadDataTask() {
        if (mSongTopDetailsPageLoadPresenter != null) {
            mSongTopDetailsPageLoadPresenter.stopTask();
            mSongTopDetailsPageLoadPresenter = null;
        }
    }
    
    /**
     * [保存歌单详情数据到数据库中]
     * <p>调用者需保证列表中歌单详情的歌单id与传入的歌单id保持一致</p>
     * @param songTopId 歌单id
     * @param details 歌单详情列表
     */
    public void saveSongTopDetailsToDB(int songTopId, List<SongTopDetail> details) {
        //zxs
        SongTopDetailDAO dao = DAOFactory.getInstance().getSongTopDetailDAO();
//        dao.saveSongTopDetails(songTopId, details);
        dao.saveSongTopDetaliList(details);
    }
    
    /**
     * [根据歌单id从数据库中获取对应的歌单详情数据]
     * @param songTopId 歌单id
     * @return 歌单详情列表数据
     */
    public List<SongTopDetail> getSongTopDetailsFromDB(int songTopId) {
        //zxs
        SongTopDetailDAO dao = DAOFactory.getInstance().getSongTopDetailDAO();
        return dao.getSongTopDetails(songTopId);
    }
    
    /*private List<Song> getSongsFromDB(int songTopId) {
        List<Song> songs = new ArrayList<Song>();
        List<SongTopDetail> details = getSongTopDetailsFromDB(songTopId);
        for (SongTopDetail detail : details) {
            Song song = SongManager.getInstance().getSongById(detail.songId);
            if (song != null) {
                songs.add(song);
            }
        }
        return songs;
    }*/
    
    /**
     * [数据库中的缓存数据是否有效]
     * @param songTopId 歌单id
     * @return true 有效  false 无效
     */
    public boolean isCacheDataValid(int songTopId) {
        SongTopDetailDAO dao = DAOFactory.getInstance().getSongTopDetailDAO();
        int count = dao.getCountBySongTopId(songTopId);
        if (count <= 0) {
            return false;
        }
        SongTop songTop = SongTopManager.getInstance().getSongTopById(songTopId);
        if (songTop != null) {
            long timetamp = songTop.timestamp;
            return (System.currentTimeMillis() - timetamp) <= SONG_MENU_DETAIL_DATA_VALID_TIME;
        }
        return false;
    }
    
    /**
     * [歌单详情分页数据提供者]
     */
    private class SongTopDetailsPageLoadPresenter extends PageLoadPresenter<SongTopDetail> {
        
        private int mSongTopId;

        /**
         * @param pageSize
         * @param pageLoadCallback
         */
        public SongTopDetailsPageLoadPresenter(int songTopId,
                int pageSize, IPageLoadCallback<SongTopDetail> pageLoadCallback) {
            super(pageSize, pageLoadCallback);
            mSongTopId = songTopId;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PageDataInfo<SongTopDetail> getData(int loadPage, int pageSize) throws Exception {
            List<SongTopDetail> songs = null;
            int totalNum = 0;
            EvLog.d("SongTopDetailsPageLoadPresenter getData DCDomain.getInstance().requestSongTopDetailsInfo");
            songs = DCDomain.getInstance().requestSongTopDetailsInfo(mSongTopId, 
                    (loadPage - 1) * pageSize, pageSize);
            totalNum = SongTopManager.getInstance().getTotalNumBySongTopId(mSongTopId);
            return new PageDataInfo<SongTopDetail>(songs, totalNum);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PageDataInfo<SongTopDetail> getCacheData() throws Exception {
//            List<Song> songs = getSongsFromDB(mSongTopId);
            List<SongTopDetail> details = getSongTopDetailsFromDB(mSongTopId);
            int totalNum = SongTopManager.getInstance().getTotalNumBySongTopId(mSongTopId);
            return new PageDataInfo<SongTopDetail>(details, totalNum);
        }
    }
}
