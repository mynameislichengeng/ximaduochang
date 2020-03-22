/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年8月2日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.freesong;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;

import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.freesong.FreeSongCommu.IFreeSongCommuListener;
import com.evideo.kmbox.presenter.CommuPresenter;
import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明] 在线歌曲推送管理
 */
public class FreeSongGetter implements IFreeSongCommuListener{
    private List<Song> mSongList = null;
    private CommuPresenter mPresenter = null;
    private FreeSongCommu mGetListCommu = null;
    private IFressSongGetterListener mListener = null;
    private Handler mHandler = null;

    public interface IFressSongGetterListener {
        public void onGetFreeSongSuccess();
        public void onUpdateLineFreeSong();
        public void onGetFreeSongFailed();
    }
    
    public void setListener(IFressSongGetterListener listener) {
        mListener = listener;
    }
    
    public FreeSongGetter() {
        mSongList = new ArrayList<Song>();
        mSongList.clear();
    }

    private static final int MSG_GET_SUCCESS = 1;
    private static final int MSG_GET_FAILED = 2;
    private static final int MSG_UPDATE = 3;
    
    public void init() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_GET_SUCCESS) {
                    if (msg.obj != null) {
                        List<Song> list = (List<Song>)(msg.obj);
                        synchronized (mSongList) {
                            mSongList.clear();
                            mSongList.addAll(list);
                        }
                        if (mListener != null) {
                            mListener.onGetFreeSongSuccess();
                        }
                    }
                } else if (msg.what == MSG_GET_FAILED) {
                    synchronized (mSongList) {
                        mSongList.clear();
                    }
                    if (mListener != null) {
                        mListener.onGetFreeSongFailed();
                    }
                } else if (msg.what == MSG_UPDATE) {
                    if (msg.obj != null) {
                        List<Song> list = (List<Song>)(msg.obj);
                        synchronized (mSongList) {
                            mSongList.clear();
                            mSongList.addAll(list);
                        }
                        if (mListener != null) {
                            mListener.onUpdateLineFreeSong();
                        }
                    }
                }
            }  
        };
    }
    
    public void uninit() {
        if (mPresenter != null) {
            mPresenter.cancel();
            mPresenter = null;
        }
    }
    
    public void startGetFreeSongList() {
        if (mGetListCommu == null) {
            mGetListCommu = new FreeSongCommu();
            mGetListCommu.setListener(this);
        }

        if (mPresenter != null) {
            mPresenter.cancel();
            mPresenter = null;
        }

        mPresenter = new CommuPresenter(mGetListCommu);
        mPresenter.start();
    }
    
    public List<Song> getList() {
        final List<Song> list = new ArrayList<Song>();
        list.clear();
        synchronized (mSongList) {
            list.addAll(mSongList);
        }
        return list;
    }
    
    @Override
    public void onFreeSongGetSuccess(List<Song> list) {
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage();
            msg.what = MSG_GET_SUCCESS;
            msg.obj = list;
            mHandler.sendMessage(msg);
        }
        mPresenter = null;
    }

    @Override
    public void onFreeSongGetFailed() {
        EvLog.d("UpdateFreeSongListCommu failed");
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_GET_FAILED);
        }
        mPresenter = null;
    }
}
