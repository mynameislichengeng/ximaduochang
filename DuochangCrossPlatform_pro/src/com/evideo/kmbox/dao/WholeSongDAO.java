package com.evideo.kmbox.dao;

import java.util.List;
import java.util.Map;

import com.evideo.kmbox.dao.WholeRemoteSongDAO.QueryEvideoIdRet;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.update.db.RemoteSong;

public abstract class WholeSongDAO {

    /**
     * [视易编号歌曲是否存在]
     * 
     * @param songId
     *            歌曲id
     * @return true存在 false不存在
     */
    public abstract boolean isExist(int songId);

    /**
     * [获取总的歌曲数量]
     * 
     * @return 数量
     */
    public abstract int getCount();

    /**
     * [获取歌曲信息]
     * 
     * @param id
     *            歌曲id
     * @return 歌曲信息song
     */
    public abstract Song getSongById(int id);

    /**
     * [获取歌曲信息]
     * 
     * @param md5
     *            歌曲md5值
     * @return 歌曲信息song
     */
    public abstract Song getSongById(String md5);

    /**
     * [添加歌曲]
     * 
     * @param song
     *            歌曲信息
     * @return true成功 false失败
     */
    public abstract boolean add(RemoteSong song);

    /**
     * [删除歌曲]
     * 
     * @param songId
     *            歌曲id
     * @return true成功 false失败
     */
    public abstract boolean deleteSong(int songId);

    /**
     * [更新单个歌曲信息]
     * 
     * @param song
     *            歌曲
     * @return true成功 false失败
     */
    public abstract boolean update(RemoteSong song);

    /**
     * [按列表添加、更新全库]
     * 
     * @param list
     *            数据中心获取的列表
     * @return true成功 false失败
     */
    public abstract boolean addUpdateByList(List<Song> list);

    /**
     * [删除歌曲信息]
     * 
     * @param list
     *            数据中心删除列表
     */
    public abstract void deleteByList(List<Song> list);

    /**
     * [保存]
     * 
     * @param list
     * @return true false
     */
    public abstract boolean save(List<RemoteSong> list);

    /**
     * [通过列表获取song表]
     * @param map  
     * @return song表
     */
    public abstract QueryEvideoIdRet getSongList(Map<Integer, String> map);

    /**
     * [计算集合中有多少条数据存在全曲库中]
     * @param collection songId的集合
     * @return 存在的数量
     */
    public abstract int calculateQualifyNum(String collection);

}