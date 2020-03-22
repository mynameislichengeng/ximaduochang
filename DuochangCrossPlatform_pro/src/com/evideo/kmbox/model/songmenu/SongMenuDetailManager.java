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

package com.evideo.kmbox.model.songmenu;

import java.util.ArrayList;
import java.util.List;




import android.accounts.NetworkErrorException;
import android.util.Log;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.SongMenuDAO;
import com.evideo.kmbox.dao.SongMenuDetailDAO;
import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.presenter.PageDataInfo;
import com.evideo.kmbox.presenter.PageLoadPresenter;
import com.evideo.kmbox.presenter.PageLoadPresenter.ILoadCacheDataCallback;
import com.evideo.kmbox.presenter.PageLoadPresenter.IPageLoadCallback;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.NetUtils;

/**
 * [歌单详情管理者]
 */
public final class SongMenuDetailManager {
    
    /** [歌单详情数据有效时间] */
    private static final int SONG_MENU_DETAIL_DATA_VALID_TIME = 1000 * 60 * 3;
    /** [分页加载数据一页的数量] */
    private static final int PAGE_SIZE = 20;
    
    private SongMenuDetailsPageLoadPresenter mSongMenuDetailsPageLoadPresenter;
    
    private static SongMenuDetailManager sInstance;
    
    private SongMenuDetailManager() {
    }
    
    /**
     * [获取歌单详情管理者实例]
     * @return 歌单详情管理者实例
     */
    public static SongMenuDetailManager getInstace() {
        if (sInstance == null) {
            synchronized (SongMenuDetailManager.class) {
                if (sInstance == null) {
                    sInstance = new SongMenuDetailManager();
                }
            }
        }
        return sInstance;
    }
    
    /**
     * [开启获取歌单详情数据任务]
     * @param songMenuId 歌单id
     * @param pageLoadCallback 分页加载数据回调接口
     * @param loadCacheDataCallback 加载缓存数据回调接口
     */
    public void startGetSongMenuDetailsTask(int songMenuId, 
            IPageLoadCallback<Song> pageLoadCallback, ILoadCacheDataCallback<Song> loadCacheDataCallback) {
        if (mSongMenuDetailsPageLoadPresenter != null) {
            mSongMenuDetailsPageLoadPresenter.stopTask();
        }
        mSongMenuDetailsPageLoadPresenter = 
                new SongMenuDetailsPageLoadPresenter(songMenuId, PAGE_SIZE, pageLoadCallback);
        mSongMenuDetailsPageLoadPresenter.setLoadCacheDataCallback(loadCacheDataCallback);
        if (isFromLocal(songMenuId)) {
            mSongMenuDetailsPageLoadPresenter.loadData();
            return;
        }
        if (isCacheDataValid(songMenuId)) {
            EvLog.d("mSongMenuDetailsPageLoadPresenter load cache data");
            mSongMenuDetailsPageLoadPresenter.loadCacheData();
        } else {
            EvLog.d("mSongMenuDetailsPageLoadPresenter load net data");
            mSongMenuDetailsPageLoadPresenter.loadData();
        }
    }
    
    private boolean isFromLocal(int songMenuId) {
        return SongMenu.SONG_MENU_ID_CHILD == songMenuId 
                || SongMenu.SONG_MENU_ID_DRAMA == songMenuId;
    }
    
    /**
     * [加载下一页数据]
     */
    public void loadNextPage() {
//        EvLog.d("mSongMenuDetailsPageLoadPresenter.loadNextPage");
        if (mSongMenuDetailsPageLoadPresenter != null) {
            mSongMenuDetailsPageLoadPresenter.loadNextPage();
        }
    }
    
    /**
     * [停止加载数据任务]
     */
    public void stopLoadDataTask() {
        if (mSongMenuDetailsPageLoadPresenter != null) {
            mSongMenuDetailsPageLoadPresenter.stopTask();
            mSongMenuDetailsPageLoadPresenter = null;
        }
    }
    
    /**
     * [保存歌单详情数据到数据库中]
     * <p>调用者需保证列表中歌单详情的歌单id与传入的歌单id保持一致</p>
     * @param songMenuId 歌单id
     * @param details 歌单详情列表
     */
    public void saveSongMenuDetailsToDB(int songMenuId, List<SongMenuDetail> details) {
        SongMenuDetailDAO dao = DAOFactory.getInstance().getSongMenuDetailDAO();
//        dao.saveSongMenuDetails(songMenuId, details);
        dao.saveSongMenuDetaliList(details);
    }
    
    /**
     * [根据歌单id从数据库中获取对应的歌单详情数据]
     * @param songMenuId 歌单id
     * @return 歌单详情列表数据
     */
    public List<SongMenuDetail> getSongMenuDetailsFromDB(int songMenuId) {
        SongMenuDetailDAO dao = DAOFactory.getInstance().getSongMenuDetailDAO();
        return dao.getSongMenuDetails(songMenuId);
    }
    
    private List<Song> getSongsFromDB(int songMenuId) {
        List<Song> songs = new ArrayList<Song>();
        List<SongMenuDetail> details = getSongMenuDetailsFromDB(songMenuId);
        for (SongMenuDetail detail : details) {
            Song song = SongManager.getInstance().getSongById(detail.songId);
            if (song != null) {
                songs.add(song);
            }
        }
        return songs;
    }
    
    /**
     * [数据库中的缓存数据是否有效]
     * @param songMenuId 歌单id
     * @return true 有效  false 无效
     */
    public boolean isCacheDataValid(int songMenuId) {
        SongMenuDetailDAO dao = DAOFactory.getInstance().getSongMenuDetailDAO();
        int count = dao.getCountBySongMenuId(songMenuId);
        if (count <= 0) {
            return false;
        }
        SongMenu songMenu = SongMenuManager.getInstance().getSongMenuById(songMenuId);
        if (songMenu != null) {
            long timetamp = songMenu.timestamp;
            return (System.currentTimeMillis() - timetamp) <= SONG_MENU_DETAIL_DATA_VALID_TIME;
        }
        return false;
    }
    
    /**
     * [歌单详情分页数据提供者]
     * 只有儿童和戏曲才分页加载
     */
    private class SongMenuDetailsPageLoadPresenter extends PageLoadPresenter<Song> {
        
        private int mSongMenuId;

        /**
         * @param pageSize
         * @param pageLoadCallback
         */
        public SongMenuDetailsPageLoadPresenter(int songMenuId,
                int pageSize, IPageLoadCallback<Song> pageLoadCallback) {
            super(pageSize, pageLoadCallback);
            mSongMenuId = songMenuId;
        }
        
        private boolean isCacheDataValid() {
            SongMenuDAO dao = DAOFactory.getInstance().getSongMenuDAO();
            if (dao.getCountOfAllSongMenus() <= 0) {
                return false;
            }
            long savedTimestamp = KmSharedPreferences.getInstance().getLong(KeyName.KEY_SONG_MENU_DATA_TIMESTAMP, 0);
            return (System.currentTimeMillis() - savedTimestamp) <= SongMenuManager.SONG_MENU_DATA_VALID_TIME;
        }
        

        /**
         * {@inheritDoc}
         */
        @Override
        public PageDataInfo<Song> getData(int loadPage, int pageSize) throws Exception {
            PageInfo pageInfo = new PageInfo(loadPage - 1, pageSize);
            List<Song> songs = null;
            int totalNum = 0;
            if (SongMenu.SONG_MENU_ID_CHILD == mSongMenuId) {
                songs = SongManager.getInstance().getSongListOfChild(pageInfo);
            } else if (SongMenu.SONG_MENU_ID_DRAMA == mSongMenuId) {
                songs = SongManager.getInstance().getSongListOfDrama(pageInfo);
            }else {
                Log.i("gsp", "getData有走到这里的么: ");
                songs = DCDomain.getInstance().requestSongMenuDetailsInfo(mSongMenuId, 
                        (loadPage - 1) * pageSize + 1, pageSize);
            }
            totalNum = SongMenuManager.getInstance().getTotalNumBySongMenuId(mSongMenuId);
            EvLog.i("totalNum:" + totalNum + ",songs.size:" + songs.size());
            return new PageDataInfo<Song>(songs, totalNum);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public PageDataInfo<Song> getCacheData() throws Exception {
            List<Song> songs = getSongsFromDB(mSongMenuId);
            int totalNum = SongMenuManager.getInstance().getTotalNumBySongMenuId(mSongMenuId);
            return new PageDataInfo<Song>(songs, totalNum);
        }
        
    }
    
}
