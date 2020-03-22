package com.evideo.kmbox.dao;

import java.util.List;

import com.evideo.kmbox.model.dao.data.Media;
import com.evideo.kmbox.model.dao.data.MediaManager;
import com.evideo.kmbox.update.db.RemoteMedia;

public class WholeLocalMediaDAO extends WholeMediaDAO {

    public WholeLocalMediaDAO() {
    }

    @Override
    public List<Media> getMedia(int songId) {
       return MediaManager.getInstance().getMediaListBySong(songId);
    }

    @Override
    public boolean update(Media media) {
        // 网络异常，可以忽略
        return false;
    }

    @Override
    public Media getMediaById(int id) {
        return MediaManager.getInstance().getMedia(id);
    }

    @Override
    public boolean save(List<RemoteMedia> list) {
        // 网络异常，可以忽略
        return false;
    }

    @Override
    public void deleteMediasBySongId(int songId) {
       // 网络异常，可以忽略
    }

    @Override
    public boolean updateBaseInfo(List<Media> list) {
       // 网络异常，可以忽略
        return false;
    }

    @Override
    public boolean updateOnlineMedia(int songId, List<RemoteMedia> list) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getMaxID() {
        // 网络异常，可以忽略
        return 0;
    }

    @Override
    public boolean isExist(String md5) {
     // 网络异常，可以忽略
        return false;
    }

    @Override
    public int getSongIdByMd5(String md5) {
        // TODO Auto-generated method stub
        return 0;
    }

}
