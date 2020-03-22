package com.evideo.kmbox.dao;

import java.util.List;

import com.evideo.kmbox.model.dao.data.Media;
import com.evideo.kmbox.update.db.RemoteMedia;

public abstract class WholeMediaDAO {

    public abstract List<Media> getMedia(int songId);

    public abstract boolean update(Media media);

    public abstract Media getMediaById(int id);

    public abstract boolean save(List<RemoteMedia> list);

    public abstract void deleteMediasBySongId(int songId);

    public abstract boolean updateBaseInfo(List<Media> list);

    public abstract boolean updateOnlineMedia(int songId, List<RemoteMedia> list);

    /**
     * [获取最大ID用于自增]
     * @return 最大ID
     */
    public abstract int getMaxID();

    /**
     * [非视易编号歌曲是否存在]
     * 
     * @param md5
     *            歌曲md5值
     * @return true存在 false不存在
     */
    public abstract boolean isExist(String md5);
    
    public abstract int getSongIdByMd5(String md5);

}