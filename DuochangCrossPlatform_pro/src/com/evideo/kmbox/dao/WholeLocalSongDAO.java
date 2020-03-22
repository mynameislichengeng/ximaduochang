package com.evideo.kmbox.dao;

import java.util.List;
import java.util.Map;

import com.evideo.kmbox.dao.WholeRemoteSongDAO.QueryEvideoIdRet;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.update.db.RemoteSong;

public class WholeLocalSongDAO extends WholeSongDAO {

    public WholeLocalSongDAO() {
    }

    @Override
    public boolean isExist(int songId) {
        return SongManager.getInstance().isExist(songId);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Song getSongById(int id) {
        return SongManager.getInstance().getSongById(id);
    }

    @Override
    public Song getSongById(String md5) {
        // 网络异常，可以忽略
        return null;
    }

    @Override
    public boolean add(RemoteSong song) {
        // TODO
        return false;
    }

    @Override
    public boolean deleteSong(int songId) {
        // TODO
        return false;
    }

    @Override
    public boolean update(RemoteSong song) {
        // TODO 
        return false;
    }

    @Override
    public boolean addUpdateByList(List<Song> list) {
        // 网络异常，可以忽略
        return false;
    }

    @Override
    public void deleteByList(List<Song> list) {
        // 网络异常，可以忽略
    }

    @Override
    public boolean save(List<RemoteSong> list) {
        // 网络异常，可以忽略
        return false;
    }

    @Override
    public QueryEvideoIdRet getSongList(Map<Integer, String> map) {
        // 网络异常，可以忽略
        return null;
    }

    @Override
    public int calculateQualifyNum(String collection) {
        return SongManager.getInstance().calculateQualifyNum(collection);
    }

}
