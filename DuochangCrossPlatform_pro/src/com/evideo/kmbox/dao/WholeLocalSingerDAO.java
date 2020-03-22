package com.evideo.kmbox.dao;

import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.model.dao.data.SingerManager;
import com.evideo.kmbox.update.db.RemoteSinger;

import java.util.List;
import java.util.Map;

public class WholeLocalSingerDAO extends WholeSingerDAO {

    public WholeLocalSingerDAO() {
    }

    @Override
    public List<Singer> getSingerBySpell(String spell, PageInfo pageInfo) {
        // 网络异常，可以忽略
        return null;
    }

    @Override
    public Singer getSingerById(int id) {
        return SingerManager.getInstance().getSinger(id);
    }

    @Override
    public boolean add(RemoteSinger singer) {
        // 网络异常，可以忽略
        return false;
    }

    @Override
    public void delete(int id) {
        // 网络异常，可以忽略
    }

    @Override
    public boolean update(RemoteSinger singer) {
        // 网络异常，可以忽略
        return false;
    }

    @Override
    public void addUpdateByList(List<Singer> list) {
        // 网络异常，可以忽略

    }

    @Override
    public boolean save(List<Singer> list) {
        // 网络异常，可以忽略
        return false;
    }

    @Override
    public List<Singer> getSingerList(Map<Integer, Integer> map) {
        // 网络异常，可以忽略
        return null;
    }

    @Override
    public boolean isExist(int singerId) {
        return false;
    }

}
