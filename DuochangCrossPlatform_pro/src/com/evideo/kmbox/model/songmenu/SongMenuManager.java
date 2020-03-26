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

package com.evideo.kmbox.model.songmenu;

import java.util.ArrayList;
import java.util.List;

import android.accounts.NetworkErrorException;
import android.util.Log;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.SongMenuDAO;
import com.evideo.kmbox.exceptionhandler.DataCenterCommuExceptionHandler;
import com.evideo.kmbox.model.dao.data.SongManager;
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
public final class SongMenuManager {
    
    /** [歌单数据有效时间] */
    public static final int SONG_MENU_DATA_VALID_TIME = 1000 * 60 * 3;
    
    private ArrayList<SongMenu> mDatas = new ArrayList<SongMenu>();
    
    private List<ISongMenuListListener> mSongMenuListListeners = 
            new ArrayList<SongMenuManager.ISongMenuListListener>();
    
    private GetSongMenuListPresenter mGetSongMenuListPresenter = new GetSongMenuListPresenter();
    
    private boolean mIsLoading = false;
    
    private static SongMenuManager sInstance;
    
    private SongMenuManager() {
        KmSharedPreferences.getInstance().putLong(KeyName.KEY_SONG_MENU_DATA_TIMESTAMP, 0);
    }
    
    /**
     * [获取SongMenuManager实例]
     * @return SongMenuManager实例
     */
    public static SongMenuManager getInstance() {
        if(sInstance == null) {
            synchronized (SongMenuManager.class) {
                SongMenuManager temp = sInstance;
                if(temp == null) {
                  temp = new SongMenuManager();
                  sInstance = temp;
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
        SongMenuDAO dao = DAOFactory.getInstance().getSongMenuDAO();
        if (dao.getCountOfAllSongMenus() <= 0) {
            return false;
        }
        long savedTimestamp = KmSharedPreferences.getInstance()
                .getLong(KeyName.KEY_SONG_MENU_DATA_TIMESTAMP, 0);
        long eclipseTime = (System.currentTimeMillis() - savedTimestamp);
        if (eclipseTime <= SONG_MENU_DATA_VALID_TIME) {
            EvLog.d(eclipseTime + ",savedTimestamp=" + savedTimestamp + ",not need update from dc");
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * [根据歌单id获取对应歌单歌曲的总数]
     * @param songMenuId 歌单id
     * @return 对应歌单歌曲的总数
     */
    public int getTotalNumBySongMenuId(int songMenuId) {
        if (songMenuId == SongMenu.SONG_MENU_ID_CHILD) {
            return SongManager.getInstance().getSongCountOfChild();
        } else if (songMenuId == SongMenu.SONG_MENU_ID_DRAMA) {
            return SongManager.getInstance().getSongCountOfDrama();
        }
        SongMenu songMenu = getSongMenuById(songMenuId);
        if (songMenu != null) {
            Log.i("gsp", "getTotalNumBySongMenuId: 跟单数据总数是"+songMenu.totalNum);
            return songMenu.totalNum;
        }
        return 0;
    }
    
    /**
     * [保存歌单到数据库中]
     * @param songMenu 歌单
     * @return true 成功    false 失败
     */
    public boolean save(SongMenu songMenu) {
        SongMenuDAO dao = DAOFactory.getInstance().getSongMenuDAO();
        return dao.save(songMenu);
    }
    
    /**
     * [根据歌单id获取歌单]
     * @param songMenuId 歌单id
     * @return 歌单
     */
    public SongMenu getSongMenuById(int songMenuId) {
        SongMenuDAO dao = DAOFactory.getInstance().getSongMenuDAO();
        return dao.getSongMenuById(songMenuId);
    }
    
    /**
     * [获取歌单列表]
     * @return List<SongMenu> 歌单列表
     */
    public List<SongMenu> getSongMenuList() {
        /*mDatas.clear();*/
        /*addPreData();*/
//        Log.i("gsp", "getSongMenuList: 歌单列表是"+mDatas.size());
        return mDatas;
    }
    
    /**
     * [开启获取歌单列表数据任务]
     */
    public void startGetSongMenuListTask() {
        if (mIsLoading) {
            return;
        } else {
            mIsLoading = true;
        }
        if (mGetSongMenuListPresenter != null) {
            mGetSongMenuListPresenter.start();
        }
    }
    
    /**
     * [保存歌单列表数据到数据库中]
     * @param songMenus 歌单列表
     */
    public void saveSongMenuDataToDB(List<SongMenu> songMenus) {
        SongMenuDAO dao = DAOFactory.getInstance().getSongMenuDAO();
        dao.saveList(songMenus);
    }
    
    /**
     * [从数据库中获取歌单列表数据]
     * @return 歌单列表
     */
    public List<SongMenu> getSongMenuDataFromDB() {
        SongMenuDAO dao = DAOFactory.getInstance().getSongMenuDAO();
        Log.i("gsp", "getSongMenuDataFromDB:从数据库中获取歌单列表数据 "+dao.getAllSongMenus());
        return dao.getAllSongMenus();
    }
    
    /**
     * [添加预置数据]
     */
    private void addPreData() {
         //  TODO 删除歌单后俩个数据

//        if (!mDatas.contains(SongMenu.getSongMenuChild())) {
//            Log.i("gsp", "addPreData: 添加的歌单数据为"+/*0, */SongMenu.getSongMenuChild());
//            mDatas.add(/*0, */SongMenu.getSongMenuChild());
//        }
//
//        if (!mDatas.contains(SongMenu.getSongMenuDrama())) {
//            Log.i("gsp", "addPreData: 添加的歌单数据为2"+/*0, */SongMenu.getSongMenuChild());
//            mDatas.add(/*0, */SongMenu.getSongMenuDrama());
//        }
    }
    
    /**
     * [注册歌单列表监听]
     * @param listener 歌单列表监听器
     */
    public void registSongMenuListListener(ISongMenuListListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (mSongMenuListListeners) {
            if (!mSongMenuListListeners.contains(listener)) {
                mSongMenuListListeners.add(listener);
            }
        }
    }
    
    /**
     * [注销歌单列表监听]
     * @param listener 歌单列表监听器
     */
    public void unregistSongMenuListListener(ISongMenuListListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (mSongMenuListListeners) {
            mSongMenuListListeners.remove(listener);
        }
    }
    
    /**
     * [通知歌单被选中]
     * @param songMenu 歌单
     */
    public void notifySongMenuSelected(SongMenu songMenu) {
        if (songMenu == null) {
            return;
        }
        synchronized (mSongMenuListListeners) {
            int size = mSongMenuListListeners.size();
            for (int i = 0; i < size; i++) {
                mSongMenuListListeners.get(i).onSongMenuSelected(songMenu);
            }
        }
    }
    
    /**
     * [通知歌单数据变更]
     */
    public void notifySongMenuDataChanged() {
        synchronized (mSongMenuListListeners) {
            int size = mSongMenuListListeners.size();
            for (int i = 0; i < size; i++) {
                mSongMenuListListeners.get(i).onSongMenuDataChanged();
            }
        }
    }
    
    private SongMenu mPrevClickSongMenu = null;
    public SongMenu getPrevClickSongMenu() {
        return mPrevClickSongMenu;
    }
    /**
     * [通知歌单被点击]
     * @param songMenu
     */
    public void notifySongMenuClicked(SongMenu songMenu) {
        if (songMenu == null) {
            return;
        }
        mPrevClickSongMenu = songMenu;
        
        synchronized (mSongMenuListListeners) {
            int size = mSongMenuListListeners.size();
            for (int i = 0; i < size; i++) {
                mSongMenuListListeners.get(i).onSongMenuClicked(songMenu);
            }
        }
    }
    
    /**
     * [歌单列表监听器]
     */
    public interface ISongMenuListListener {
        
        /**
         * [歌单被选中]
         * @param songMenu songMenu
         */
        public void onSongMenuSelected(SongMenu songMenu);
        
        /**
         * [通知歌单列表数据变更]
         */
        public void onSongMenuDataChanged();

        /**
         * [歌单被点击]
         * @param songMenu
         */
        public void onSongMenuClicked(SongMenu songMenu);
        
    }
    
    /**
     * [获取歌单列表数据提供者]
     */
    private class GetSongMenuListPresenter extends AsyncPresenter<Boolean> {
        List<SongMenu> list = new ArrayList<SongMenu>();
        
        @Override
        protected Boolean doInBackground(Object... params) throws Exception {
            if (isCacheDataValid()) {
                EvLog.d("get song menu data from db");
                list = getSongMenuDataFromDB();
            } else {
                EvLog.d("get song menu data from net");
                if (!NetUtils.isNetworkConnected(BaseApplication.getInstance())) {
                    throw new NetworkErrorException("Network conn error.");
                }
                list = DCDomain.getInstance().requestSongMenuList();
            }
            return true;
        }

        @Override
        protected void onCompleted(Boolean result, Object... params) {
            EvLog.d("get song menu data onCompleted");
            if (result != null && result) {
                mDatas.clear();
                mDatas.addAll(list);
                addPreData();
                
            }
            notifySongMenuDataChanged();
            mIsLoading = false;
            list.clear();
            list = null;
        }

        @Override
        protected void onFailed(Exception exception, Object... params) {
            EvLog.e("get song menu data onFailed");
            DataCenterCommuExceptionHandler.getInstance().handle(exception);
            UmengAgentUtil.reportError(exception);
            mIsLoading = false;
            list.clear();
            list = null;
        }
    }
}
