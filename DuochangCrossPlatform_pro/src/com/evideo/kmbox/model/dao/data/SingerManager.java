package com.evideo.kmbox.model.dao.data;

import java.util.List;

import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.SingerDAO;

public class SingerManager {
    private static SingerManager instance;
    
    private SingerManager() {
    }
    
    public static SingerManager getInstance() {
        if (instance == null) {
            synchronized (SingerManager.class) {
                if(instance == null) {
                    instance = new SingerManager();
                }
            }
        }
        return instance;
    }
    
    public List<Song> getSongs() {
        return null;
    }
    
    public Singer getSinger(int id) {
        return DAOFactory.getInstance().getSingerDAO().getSingerById(id);
    }
    
    public void deleteSinger(int id) {
        DAOFactory.getInstance().getSingerDAO().delete(id);
    }

    public void deleteSinger(Singer singer) {
        DAOFactory.getInstance().getSingerDAO().delete(singer.getId());
    }
    
    public List<Singer> getSingerBySpell(String spell, PageInfo pageInfo) {
        SingerDAO dao = DAOFactory.getInstance().getSingerDAO();
        return dao.getSingerBySpell(spell, pageInfo);
    }
    
    public List<Singer> getSingerBySpell(String spell, PageInfo pageInfo, int typeIndex) {
        SingerDAO dao = DAOFactory.getInstance().getSingerDAO();
        return dao.getSingerBySpell(spell, pageInfo, typeIndex);
    }
    
    public List<Singer> getSingerByName(String singerName) {
        SingerDAO dao = DAOFactory.getInstance().getSingerDAO();
        return dao.getSingerByName(singerName);
    }
    
    public int getSingerCountBySpell(String spell, int typeIndex) {
        SingerDAO dao = DAOFactory.getInstance().getSingerDAO();
        return dao.getSingerCountBySpell(spell, typeIndex);
    }
    
    public int getSingerCountBySpell(String spell) {
        SingerDAO dao = DAOFactory.getInstance().getSingerDAO();
        return dao.getSingerCountBySpell(spell);
    }
    
    public List<Singer> getSingerList(PageInfo pageInfo) {
        return DAOFactory.getInstance().getSingerDAO().getList(pageInfo);
    }
    
    /**
     * [本地歌星数据生成-tblSong表生成完毕后调用]
     */
    public void createLocalSinger() {
        SingerDAO dao = DAOFactory.getInstance().getSingerDAO();
        dao.createLocalSinger();
    }
    
    public boolean save(List<Singer> list) {
        if (list.size() == 0) {
            return true;
        }
        
        SingerDAO dao = DAOFactory.getInstance().getSingerDAO();
        dao.save(list);

        return true;
    }
    
    /**
     * [清空歌星表]
     */
    public void clearList() {
        SingerDAO dao = DAOFactory.getInstance().getSingerDAO();
        dao.clearList();
    }
    
}
