/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-7-16     "liuyantao"     1.0        [初版]
 *
 */

package com.evideo.kmbox.model.dao.data;

import java.util.List;
import java.util.Map;
import android.text.TextUtils;
import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.WholeRemoteSongDAO.QueryEvideoIdRet;
import com.evideo.kmbox.dao.WholeSongDAO;
import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.update.db.RemoteSong;
import com.evideo.kmbox.update.db.WholedbDownloadManager;
import com.evideo.kmbox.util.EvLog;

/**
 * [本地全库歌曲管理]
 */
public final class WholeSongManager {
    
    private static final int USER_DEFINED_MINIMUM_NUMBER = 90000000;
    private static final int USER_DEFINED_MAXIMUM_NUMBER = 99990000;
    private static final int USER_DEFINED_MINIMUM_KMBOX_NUMBER = 100000000;
    public static final int EVIDEO_NUMBER_SONG_LENGTH = 8;
    
    private static WholeSongManager sInstance;

    private WholeSongManager() {

    }

    /**
     * [全库歌星管理单例]
     * @return 管理单例
     */
    public static WholeSongManager getInstance() {
        if (sInstance == null) {
            synchronized (WholeSongManager.class) {
                if (sInstance == null) {
                    sInstance = new WholeSongManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * [判断歌曲是否为视易编号歌曲]
     * @param fileName 歌曲文件名
     * @return true是 false不是
     */
    public boolean isEvideoNoSong(String fileName) {
        if (fileName.length() != EVIDEO_NUMBER_SONG_LENGTH
                && fileName.length() != WholeSongManager.EVIDEO_NUMBER_SONG_LENGTH + 1) {
            EvLog.i("evideo number song require 8-9 place");
            return false;
        }
        
        for (int i = fileName.length(); --i >= 0;) {
            if (!Character.isDigit(fileName.charAt(i))) {
                EvLog.i("Non pure number");
                return false;
            }
        }
        //转换String为int
        int songId = Integer.parseInt(fileName);
        if (songId <= USER_DEFINED_MAXIMUM_NUMBER && songId >= USER_DEFINED_MINIMUM_NUMBER
                || songId >= USER_DEFINED_MINIMUM_KMBOX_NUMBER) {
            return false;
        }

        return true;
    }
    
    public WholeSongDAO getWholeSongDAO() {
        WholeSongDAO dao = DAOFactory.getInstance().getWholeRemoteSongDAO();
        String localDbVersion = KmSharedPreferences.getInstance().getString(KeyName.KEY_WHOLE_DB_VERSION, "");
        if (!TextUtils.isEmpty(localDbVersion) 
                || WholedbDownloadManager.WHOLE_DB_VERSION_DEFAULT.equals(localDbVersion)) {
            dao = DAOFactory.getInstance().getWholeRemoteSongDAO();
        } else {
            dao = DAOFactory.getInstance().getWholeLocalSongDAO();
        }
        return dao;
    }

    /**
     * [判断视易编号歌曲是否存在]
     * @param id 歌曲编号
     * @return true存在 false不存在
     */
    public boolean isExist(int id) {
        WholeSongDAO dao = getWholeSongDAO();
        return dao.isExist(id);
    }

    
    /**
     * [视易编号获取歌曲信息]
     * @param id 歌曲id
     * @return 歌曲信息
     */
    public Song getSongById(int id) {
        WholeSongDAO dao = getWholeSongDAO();
        return dao.getSongById(id);
    }
    
    /**
     * [非视易编号歌曲获取歌曲信息]
     * @param md5 歌曲文件md5值
     * @return 歌曲信息
     */
    public Song getSongById(String md5) {
        WholeSongDAO dao = getWholeSongDAO();
        return dao.getSongById(md5);
    }
    
    /**
     * [添加歌曲信息]
     * @param song 歌曲信息
     * @return true成功 false失败
     */
    public boolean add(RemoteSong song) {
        if (song == null) {
            return false;
        }
        WholeSongDAO dao = getWholeSongDAO();
        return dao.add(song);
    }
    
    /**
     * [删除歌曲信息]
     * @param songId 歌曲id
     * @return true成功 false失败
     */
    public boolean delete(int songId) {
        WholeSongDAO dao = getWholeSongDAO();
        return dao.deleteSong(songId);
    }
    
    /**
     * [更新歌曲信息]
     * @param song 歌曲信息
     * @return true成功 false失败
     */
    public boolean update(RemoteSong song) {
        if (song == null) {
            return false;
        }
        WholeSongDAO dao = getWholeSongDAO();
        return dao.update(song);
    }

    /**
     * [按列表添加、更新全库歌曲信息]
     * @param list 添加、更新列表
     * @return true成功 false失败
     */
    public boolean updateByList(List<Song> list) {
        if (list.size() <= 0) {
            return false;
        }
        WholeSongDAO dao = getWholeSongDAO();
        return dao.addUpdateByList(list);
    }
    
    /**
     * [保存]
     * @param list 
     * @return true false
     */ 
    public boolean save(List<RemoteSong> list) {
        if (list.size() == 0) {
            return true;
        }

        WholeSongDAO dao = getWholeSongDAO();
        dao.save(list);

        return true;
    }
    
    /**
     * [传入编号获取song表]
     * @param map  
     * @return 列表
     */
    public QueryEvideoIdRet getSongList(Map<Integer, String> map) {
        WholeSongDAO dao = getWholeSongDAO();
        return dao.getSongList(map);
    }
    
    public int calculateQualifyNum(String collection) {
        WholeSongDAO dao = getWholeSongDAO();
        return dao.calculateQualifyNum(collection);
    }
}
