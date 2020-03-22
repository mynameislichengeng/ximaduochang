package com.evideo.kmbox.dao;

import java.util.Map;

public abstract class SongIdDAO {

    /**
     * [请空表]
     * @return true成 false失败
     */
    public abstract boolean clearList();

    /**
     * 升级tblSong表语句
     * @param str songId的集合
     * @return true 成功： false 失败
     */
    public abstract boolean executeSongUpdateSql(String str);

    /**
     * [执行tblMedia表的升级操作]
     * @param str songId集合
     * @param formatIndex 格式索引
     * @param uuid 磁盘UUID
     * @return 成功或失败
     */
    public abstract boolean executeMediaUpdateSql(String str, int formatIndex,
            String uuid, String ext);

    /**
     * [执行tblMedia表的UUID字段升级]
     * @param uuid UUID
     * @param idCollection SongID集合
     * @return 成功或失败
     */
    public abstract boolean executeMediaUpdateUUID(String idCollection,
            String uuid);

    /**
     * [获取未识别视易编号歌曲集合]
     * @return list
     */
    public abstract Map<Integer, String> getNotIdentifiedEvSong();

}