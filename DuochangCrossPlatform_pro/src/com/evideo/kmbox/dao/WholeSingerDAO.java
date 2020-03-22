package com.evideo.kmbox.dao;

import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.update.db.RemoteSinger;

import java.util.List;
import java.util.Map;

public abstract class WholeSingerDAO {

    /**
     * [歌星首拼模糊匹配查询]
     * 
     * @param spell
     *            [歌手拼音]
     * @param pageInfo
     *            [分页信息]
     * @return [返回满足拼音模糊匹配spell的歌星信息]
     */
    public abstract List<Singer> getSingerBySpell(String spell,
            PageInfo pageInfo);

    /**
     * [通过歌星ID查询歌星]
     * 
     * @param id
     *            [歌星id]
     * @return [返回根据id上搜索到的歌星]
     */
    public abstract Singer getSingerById(int id);

    /**
     * [增加歌星]
     * @param singer 歌星信息
     * @return true成功 false失败
     */
    public abstract boolean add(RemoteSinger singer);

    /**
     * [通过歌星ID删除歌星]
     * @param id 歌星id
     */
    public abstract void delete(int id);

    /**
     * [修改歌星信息]
     * @param singer 歌星信息
     * @return true成功 false失败
     */
    public abstract boolean update(RemoteSinger singer);

    /**
     * [增加、替换相关歌星信息]
     * @param list 数据中心添加、更新列表
     */
    public abstract void addUpdateByList(List<Singer> list);


    public abstract boolean save(List<Singer> list);

    public abstract List<Singer> getSingerList(Map<Integer, Integer> map);

    public abstract boolean isExist(int singerId);

}