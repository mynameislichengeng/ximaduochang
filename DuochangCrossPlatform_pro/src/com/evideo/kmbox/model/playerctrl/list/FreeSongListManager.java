/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年3月10日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.playerctrl.list;

import java.util.ArrayList;
import java.util.List;

import android.os.Message;
import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.FreeSongDAO;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.freesong.FreeSongGetter;
import com.evideo.kmbox.model.freesong.FreeSongGetter.IFressSongGetterListener;
import com.evideo.kmbox.model.playerctrl.PlayListDAOManager;
import com.evideo.kmbox.model.playerctrl.PlayListDAOManager.ListHandler;
import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明]
 */
public class FreeSongListManager implements IFressSongGetterListener {
    private List<Song> mList = null;
    private static FreeSongListManager instance = null;
    
    /*如果数据库已经有数据的情况下，后台更新查询*/
    private boolean mUpdateBackground = false;
    
    
    private FreeSongListManager() {
        mList = new ArrayList<Song>();
    }
    

    
    public static FreeSongListManager getInstance() {
        if(instance == null) {
            synchronized (FreeSongListManager.class) {
                FreeSongListManager temp = instance;
                if(temp == null) {
                  temp = new FreeSongListManager();
                  instance = temp;
                }
            }
         }
         return instance;
    }
    
    public void init() {
        FreeSongDAO dao = DAOFactory.getInstance().getFreeSongDAO();
        List<Song> list = dao.getList();
        if (list != null && list.size() > 0) {
            mList.clear();
            mList.addAll(list);
            EvLog.e("freeSonglist init size=" + list.size());
        }
        
        if (mList.size() == 0) {
            startUpdateFreeSong();
            for (IGetFreeSongEventListener listener : mListeners) {
                listener.onStartGetFreeSong();
            }
        } else {
            for (IGetFreeSongEventListener listener : mListeners) {
                listener.onFinishGetFreeSong();
            }
            mUpdateBackground = true;
            BaseApplication.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startUpdateFreeSong();
                }
            }, 3*60*1000);
        }
    }
    public void uninit() {
        mList.clear();
        mListeners.clear();
    }
    private FreeSongGetter mOnlineFreeSong = null;
    
    public void startUpdateFreeSong() {
        if (mOnlineFreeSong == null) {
            mOnlineFreeSong = new FreeSongGetter();
            mOnlineFreeSong.init();
            mOnlineFreeSong.setListener(this);
        }
        mOnlineFreeSong.startGetFreeSongList();
    }
    
    private void update(List<Song> list) {
        synchronized (mList) {
            mList.clear();
            mList.addAll(list);
            Message msg = PlayListDAOManager.getInstance().getHandler().obtainMessage();
            msg.what = ListHandler.FREESONG_LIST_UPDATE_LIST;
            msg.obj = list;
            PlayListDAOManager.getInstance().getHandler().sendMessageDelayed(msg, 500);
        }
    }
    public synchronized int getListCount() {
        return mList.size();
    }
    public synchronized List<Song> getList() {
        return mList;
    }
    
    public synchronized boolean isFreeSong(int songid) {
        for (Song song : mList) {
            if (song.getId() == songid) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void onGetFreeSongSuccess() {
        if (mOnlineFreeSong == null) {
            return;
        }
        boolean needUpdate = false;
        int size = mOnlineFreeSong.getList().size();
        if (size == 0) {
            EvLog.e("get no freesong ");
        } else {
           
            if (size != mList.size()) {
                needUpdate = true;
            } else {
                List<Song> updateList = mOnlineFreeSong.getList();
                Song item = null;
                for (int i = 0; i < size;i++) {
                    item = updateList.get(i);
                    if (item == null) {
                        continue;
                    }
                    if (mList.get(i) == null) {
                        continue;
                    }
                    if (item.getId() != mList.get(i).getId()) {
                        needUpdate = true;
                        break;
                    }
                }
            }
            EvLog.i("onGetFreeSongSuccess mUpdateBackground:" + mUpdateBackground + ",needUpdate=" + needUpdate);
            if (needUpdate) {
                update(mOnlineFreeSong.getList());
            }
        }
        
        if (!mUpdateBackground) {
            for (IGetFreeSongEventListener listener : mListeners) {
                listener.onFinishGetFreeSong();
            }
        } else {
            if (needUpdate) {
                for (IGetFreeSongEventListener listener : mListeners) {
                    listener.onUpdateFreeSong();
                }
            }
        }
    }

    @Override
    public void onUpdateLineFreeSong() {
        if (mOnlineFreeSong != null) {
            int size = mOnlineFreeSong.getList().size();
            if (size > 0) {
                update(mOnlineFreeSong.getList());
            }
        }
    }

    @Override
    public void onGetFreeSongFailed() {
        if ( !mUpdateBackground && mListeners != null) {
            for (IGetFreeSongEventListener listener : mListeners) {
                listener.onErrorGetFreeSong();
            }
        }
    }
    
    public interface IGetFreeSongEventListener {
        public void onStartGetFreeSong();
        public void onErrorGetFreeSong();
        public void onFinishGetFreeSong();
        //数据中心主动推送
        public void onUpdateFreeSong();
    }
    
    private List<IGetFreeSongEventListener> mListeners = null;
    public void addGetFreeSongEventListener(IGetFreeSongEventListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<FreeSongListManager.IGetFreeSongEventListener>();
        }
        mListeners.add(listener);
    }
    
    public void removeGetFreeSongEventListener(IGetFreeSongEventListener listener) {
        if (mListeners != null) {
            if (mListeners.contains(listener)) {
                mListeners.remove(listener);
            }
        }
    }
}
