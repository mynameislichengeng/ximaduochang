/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-7-29     "liuyantao"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.dao.data;

import java.util.Map;

import android.text.TextUtils;

import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.SongIdDAO;
import com.evideo.kmbox.dao.WholeSongDAO;
import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.update.db.WholedbDownloadManager;

/**
 * [遍历生成歌曲信息是歌曲 id表]
 */
public final class SongIdManager {
    
    
    private static SongIdManager sInstance;

    private SongIdManager() {

    }

    /**
     * [视易编号歌曲id管理单例]
     * @return 管理单例
     */
    public static SongIdManager getInstance() {
        if (sInstance == null) {
            synchronized (SongIdManager.class) {
                if (sInstance == null) {
                    sInstance = new SongIdManager();
                }
            }
        }
        return sInstance;
    }
 
    public SongIdDAO getSongIdDAO() {
        SongIdDAO dao = DAOFactory.getInstance().getSongIdRemoteDAO();
        String localDbVersion = KmSharedPreferences.getInstance().getString(KeyName.KEY_WHOLE_DB_VERSION, "");
        if (!TextUtils.isEmpty(localDbVersion) 
                || WholedbDownloadManager.WHOLE_DB_VERSION_DEFAULT.equals(localDbVersion)) {
            dao = DAOFactory.getInstance().getSongIdRemoteDAO();
        } else {
            dao = DAOFactory.getInstance().getSongIdLocalDAO();
        }
        return dao;
    }
    
    /**
     * [清空表]
     * @return true 成功 false失败
     */
    public boolean clearList() {
        SongIdDAO dao = getSongIdDAO();
        return dao.clearList();
    }
    
    /**
     * 执行tblSong表升级
     * @param sql songId集合
     * @return 成功或失败
     */
    public boolean executeSongUpdateSql(String sql) {
        SongIdDAO dao = getSongIdDAO();
        return dao.executeSongUpdateSql(sql);
    }
    
    /**
     * 执行tblMedia表升级
     * @param sql songId的集合
     * @param formatIndex 类型索引
     * @return 成功或失败
     */
    public boolean executeMediaUpdateSql(String sql, int formatIndex, String uuid, String ext) {
        SongIdDAO dao = getSongIdDAO();
        return dao.executeMediaUpdateSql(sql, formatIndex, uuid, ext);
    }
    
    /**
     * 执行tblMedia表UUID字段更新
     * @param uuid UUID
     * @param idCollection ID集合
     * @return true or false
     */
    public boolean executeMediaUpdateUUID(String idCollection, String uuid) {
        SongIdDAO dao = getSongIdDAO();
        return dao.executeMediaUpdateUUID(idCollection, uuid);
    }
    /**
     * [获取未识别视易编号歌曲集合]
     * @return list
     */
    public Map<Integer, String> getNotIdentifiedEvSong() {
        SongIdDAO dao = getSongIdDAO();
        return dao.getNotIdentifiedEvSong();
    }
}
