/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-8-31     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.playerctrl;

import java.util.List;

import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.SelectedListDAO;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.playerctrl.sunglist.SungListManager;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.util.EvLog;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

/**
 * [功能说明]
 */
public class PlayListDAOManager {
    
    private HandlerThread mDAOListThread = null;
    private ListHandler mDAOListHandler = null;
    
    private static boolean instanceFlag = false; // true if 1 instance
    private static PlayListDAOManager instance = null;
    
    public static PlayListDAOManager getInstance() {
        if (!instanceFlag) {
            instanceFlag = true;
            instance = new PlayListDAOManager();
            
            return instance;
        }
        return instance;
    }
    
    @Override
    public void finalize() {
        instanceFlag = false;
        instance = null;
    }
    
    private PlayListDAOManager() {
        mDAOListThread = new HandlerThread("PlayListDAOHandler");
        mDAOListThread.start();
        mDAOListHandler = new ListHandler(mDAOListThread.getLooper());
    }
    
    public Handler getHandler() {
        return mDAOListHandler;
    }
    
    public void uninit() {
        if (mDAOListThread != null) {
            mDAOListThread.getLooper().quit();
            mDAOListThread = null;
            EvLog.d("PlayListDAOManager　uninit success");
        }
    }
    
    public static class ListHandler extends Handler{
        /** [删除] */
        public static final int PLAYLIST_DEL_ITEM_BY_INDEX = 1;
        public static final int PLAYLIST_DEL_ITEM_BY_SONGID = 2;
        /** [置顶] */
        public static final int PLAYLIST_TOP_ITEM = 3;
        /** [添加] */
        public static final int PLAYLIST_ADD_ITEM = 4;
        
        public static final int PLAYLIST_CLEAR_ALL_ITEM = 5;
        
//        public static final int PLAYLIST_UPDATE_ITEM = 6;
        
        public static final int SUNGLIST_ADD_ITEM = 11;
        public static final int SUNGLIST_DEL_ITEM = 12;
        public static final int SUNGLIST_DEL_ITEM_BY_SONGID = 13;
        
        public static final int FAVORITE_LIST_ADD_ITEM = 30;
        public static final int FAVORITE_LIST_DEL_ITEM = 31;
        public static final int FAVORITE_LIST_DEL_ALL_ITEM = 32;
        
        public static final int FAVORITE_LIST_ADD_LIST = 35;
        public static final int FAVORITE_LIST_DEL_LIST = 36;
        
        public static final int FREESONG_LIST_UPDATE_LIST = 40;
        
        
//        public static final int CACHE_ADD_MEDIA = 22;
        
        public ListHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            if (msg == null) {
                return;
            }
            
            switch(msg.what) {
                case PLAYLIST_DEL_ITEM_BY_INDEX: {
                    int serialNum = msg.arg1;
                    if (DAOFactory.getInstance().getSelectedListDAO().deleteSong(serialNum)) {
                        EvLog.i("selectedlistdao: " + "serialNum=" + serialNum + ",del success");
                    } else {
                        EvLog.e("selectedlistdao: " + "serialNum=" + serialNum + ",del failed");
                    }
                    break;
                }
                case PLAYLIST_DEL_ITEM_BY_SONGID: {
                    int songid = msg.arg1;
                    if (DAOFactory.getInstance().getSelectedListDAO().deleteSongBySongId(songid)) {
                        EvLog.i("selectedlistdao: " + "songid=" + songid + ",del success");
                    } else {
                        EvLog.e("selectedlistdao: " + "songid=" + songid + ",del failed");
                    }
                    break;
                }
                case PLAYLIST_TOP_ITEM: {
                    int serialNum = msg.arg1;
                    if (DAOFactory.getInstance().getSelectedListDAO().topSong(serialNum)) {
                        EvLog.i("selectedlistdao: " + "top success id=" + serialNum);
                    } else {
                        EvLog.i("selectedlistdao: " + "top failed id=" + serialNum);
                    }
                    break;
                } 
                case PLAYLIST_ADD_ITEM: {
                    if (msg.obj == null) {
                        return;
                    }
                    KmPlayListItem item = (KmPlayListItem)msg.obj;
                    if (DAOFactory.getInstance().getSelectedListDAO().addSong(item) > 0) {
                        EvLog.i("selectedlistdao: " + "add success id=" + item.getSerialNum());
                    } else {
                        EvLog.e("selectedlistdao: " + "add failed id=" + item.getSerialNum());
                    }
                    break;
                }
                case PLAYLIST_CLEAR_ALL_ITEM: {
                    SelectedListDAO dao = DAOFactory.getInstance().getSelectedListDAO();
                    if (!dao.clearlist()) {
                        EvLog.e("selectedlistdao: clear list failed");
                    }
                    break;
                } 
               /* case PLAYLIST_UPDATE_ITEM: {
                    if (msg.obj == null) {
                        EvLog.e("SUNGLIST_ADD_ITEM msg.obj == null");
                        return;
                    }
                    KmPlayListItem item = (KmPlayListItem)msg.obj;
                    EvLog.d("mediadao: update to local" + item.getSongName());
                    MediaManager.getInstance().update(item.getVideoMedia());
                    break;
                }*/
                case SUNGLIST_ADD_ITEM: {
                    if (msg.obj == null) {
                        EvLog.e("SUNGLIST_ADD_ITEM msg.obj == null");
                        return;
                    }
                    KmPlayListItem item = (KmPlayListItem)msg.obj;
                    EvLog.d(item.getSongName() + " add to sunglist");
                    SungListManager.getInstance().addItem(item,msg.getData().getString("shareCode"),msg.getData().getInt("playTime"));
                    break;
                }
                case SUNGLIST_DEL_ITEM: {
                    SungListManager.getInstance().delItemByShareCode((String)msg.obj);
                    break;
                }
                case SUNGLIST_DEL_ITEM_BY_SONGID: {
                    int songid = msg.arg1;
                    SungListManager.getInstance().delItemBySongId(songid);
                    break;
                }
                case FAVORITE_LIST_ADD_ITEM: {
                    int songid = msg.arg1;
                    DAOFactory.getInstance().getFavoriteListDAO().addSong(songid);
                    break;
                }
                case FAVORITE_LIST_DEL_ITEM: {
                    int songid = msg.arg1;
                    DAOFactory.getInstance().getFavoriteListDAO().delSong(songid);
                    break;
                }
                case FAVORITE_LIST_DEL_ALL_ITEM: {
                    DAOFactory.getInstance().getFavoriteListDAO().delAllSong();
                    break;
                }
                case FAVORITE_LIST_ADD_LIST:{
                    if (msg.obj != null && msg.obj instanceof List<?>) {
                        @SuppressWarnings("unchecked")
                        List<Integer> songids = (List<Integer>)msg.obj;
                        boolean upload = (msg.arg1 == 1) ? (true) : (false);
                        DAOFactory.getInstance().getFavoriteListDAO().addSongList(songids, upload);
                    }
                    break;
                }
                case FAVORITE_LIST_DEL_LIST:{
                    if (msg.obj != null && msg.obj instanceof List<?>) {
                        @SuppressWarnings("unchecked")
                        List<Integer> songids = (List<Integer>)msg.obj;
                        DAOFactory.getInstance().getFavoriteListDAO().delSongList(songids);
                    }
                    break;
                }
                case FREESONG_LIST_UPDATE_LIST: {
                    if (msg.obj != null && msg.obj instanceof List<?>) {
                        @SuppressWarnings("unchecked")
                        List<Song> songList = (List<Song>)msg.obj;
                        DAOFactory.getInstance().getFreeSongDAO().update(songList);
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }
}
