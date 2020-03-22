/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-5-15     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.model.dao.data;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.exceptionhandler.DataCenterCommuExceptionHandler;
import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.datacenter.PendingDeleteSongData;
import com.evideo.kmbox.model.observer.BaseSubject;
import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.NetUtils;
import com.evideo.kmbox.util.RepeatTimerTask;

import java.util.ArrayList;
import java.util.List;

/**
 * [功能说明]歌曲主题
 */
public final class SongSubject extends BaseSubject<ISongObserver> implements ISongSubject {
    
    private SongsToBeDeletedTask mSongsToBeDeletedTask;
    
    private int mNewVersion;
    
    private static SongSubject sInstance;
    
    private SongSubject() {
    }
    
    public static SongSubject getInstance() {
        if (sInstance == null) {
            synchronized (SongSubject.class) {
                if (sInstance == null) {
                    sInstance = new SongSubject();
                }
            }
        }
        return sInstance;
    }
    
    /**
     * [功能说明]设置新的版本号
     * @param newVersion 新版本号
     */
    public void setNewVersion(int newVersion) {
        mNewVersion = newVersion;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerSongObserver(ISongObserver observer) {
        registObserver(observer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterSongObserver(ISongObserver observer) {
        unregistObserver(observer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifySongsToBeDeleted(List<Integer> ids) {
        List<ISongObserver> observers = new ArrayList<ISongObserver>(getObservers());
        for (ISongObserver observer : observers) {
            observer.onSongsToBeDeleted(ids);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void notifySongsDeletedFinish() {
        List<ISongObserver> observers = new ArrayList<ISongObserver>(getObservers());
        for (ISongObserver observer : observers) {
            observer.onSongdDeletedFinish();
        }
    }
    
    /**
     * [功能说明]开始待删歌曲任务
     */
    public void startSongsToBeDeletedTask() {
        if (!NetUtils.isNetworkConnected(BaseApplication.getInstance().getBaseContext())) {
            EvLog.w("SongSubject","network is unavailable not start SongsToBeDeletedTask");
            return;
        }
        
        if (mSongsToBeDeletedTask == null) {
            mSongsToBeDeletedTask = new SongsToBeDeletedTask();
        }
        if (!mSongsToBeDeletedTask.isStarted()) {
            EvLog.d("zxh", "start songs to be deleted task");
            mSongsToBeDeletedTask.start();
        }
    }
    

    
   
    

    
    /**
     * [功能说明]待删歌曲任务
     */
    private class SongsToBeDeletedTask extends AsyncPresenter<List<Integer>> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected List<Integer> doInBackground(Object... params) throws Exception {
            EvLog.e("SongsToBeDeletedTask begin---------------------");
            // 从数据中心获取待删歌曲id列表
        	int version = getVersionSongsToBeDeleted();
        	PendingDeleteSongData data = DCDomain.getInstance().requestSongsToBeDeleted(version);
        	if (version == data.getVersion()) {
        		return null;
        	}

            final List<Integer> ids = data.getList();
            if (ids == null || ids.size() <= 0) {
                return null;
            }
            
            int size = ids.size();
            int id;
            // 遍历id，如果是缓存歌曲，删除歌曲文件和media表数据信息
            EvLog.i("begin delete songlist by center");
            for (int i = 0; i < size; i++) {
                id = ids.get(i);
                MediaManager.getInstance().deleteMediasBySongId(id);
            }
            // 删除song表数据
            SongManager.getInstance().deleteSongList(ids);
            setNewVersion(data.getVersion());
            notifySongsToBeDeleted(ids);
            return ids;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onCompleted(List<Integer> result, Object... params) {
            if (result != null && result.size() > 0) {
                EvLog.d("zxh", "songs to be deleted task on completed and notify observers");
                notifySongsDeletedFinish();
                saveVersionSongsToBeDeleted(mNewVersion);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onFailed(Exception exception, Object... params) {
            if (exception != null) {
                EvLog.e("song to be deleted task error --- \n" 
                        + DataCenterCommuExceptionHandler.getInstance().getStackTraceStr(exception));
                UmengAgentUtil.reportError(exception);
            }
        }
        
    }
   
    /**
     * [功能说明]获取待删歌曲版本
     * @return 待删歌曲版本
     */
    public static int getVersionSongsToBeDeleted() {
        return KmSharedPreferences.getInstance().getInt(
                KeyName.KEY_VERSION_SONGS_TO_BE_DELETED, 0);
    }
    
    /**
     * [功能说明]保存待删歌曲版本
     * @param version 待删歌曲版本
     * @return true 成功  false 失败
     */
    public static boolean saveVersionSongsToBeDeleted(int version) {
        return KmSharedPreferences.getInstance().putInt(
                KeyName.KEY_VERSION_SONGS_TO_BE_DELETED, version);
    }

    private RepeatTimerTask mRepeatTimerTask = new RepeatTimerTask(new RepeatTimerTask.IActionCallback() {
		
		@Override
		public void stop() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void start() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void repeat() {
			startSongsToBeDeletedTask();
		}
	});

    /**
     * [功能说明]启动删歌业务
     */
    public void start() {
    	mRepeatTimerTask.scheduleAtFixedRate(1000 * 60 * 3);
    }

    /**
     * [功能说明]停止删歌业务
     */
    public void stop() {
    	if (mRepeatTimerTask != null) {
            mRepeatTimerTask.stop();
        }
    }

   
}
