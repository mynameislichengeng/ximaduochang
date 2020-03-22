package com.evideo.kmbox.model.dao.data;

import java.util.List;

import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.MediaDAO;
import com.evideo.kmbox.dao.SongDAO;
import com.evideo.kmbox.model.datacenter.DCDomain;

/**
 * [功能说明] 歌曲管理类
 */
public final class SongManager {

    private static SongManager sInstance;

    private SongManager() {

    }

    /**
     * [功能说明] 单例
     * @return  歌曲管理类
     */
    public static SongManager getInstance() {
        if (sInstance == null) {
            synchronized (SongManager.class) {
                if (sInstance == null) {
                    sInstance = new SongManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * [功能说明] 判断歌曲是否存在
     * @param id  歌曲id
     * @return  歌曲存在则返回true，否则返回false
     */
    public boolean isExist(int id) {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.isExist(id);
    }

    /**
     * [功能说明] 获取歌曲
     * @param id  歌曲id
     * @return  歌曲
     */
    public Song getSongById(int id) {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getSongById(id);
    }
    
    /**
     * [功能说明]
     * @param songName 根据歌名返回歌曲
     * @return 歌曲列表
     */
    public List<Song> getSongByName(String songName) {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getSongByName(songName);
    }

    public Song getRandomSongBySpell(String spell/*, PageInfo pageInfo*/) {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getRandomSong(spell/*, pageInfo*/);
    }
    
    public Song getRandomSongWithCanScore() {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getRandomSongWithCanScore();
    }
    /**
     * [功能说明] 根据首拼获取歌曲列表
     * @param spell 首拼
     * @param pageInfo  分页信息
     * @return  歌曲列表
     */
    public List<Song> getSongBySpell(String spell, PageInfo pageInfo) {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getSongBySpell(spell, pageInfo);
    }
    
    public List<Song> getSongBySingerName(String singerName, PageInfo pageInfo, boolean local) {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getSongBySingerName(singerName, pageInfo, local);
    }

    /**
     * [功能说明] 根据首拼获取符合条件的歌曲数量
     * @param spell 首拼
     * @return  歌曲数量
     */
    public int getSongCountBySpell(String spell) {
        return 0;
    }

    /**
     * [功能说明] 获取已缓存本地的歌曲列表
     * @param pageInfo  分页信息
     * @return  歌曲列表
     */
    public List<Song> getCachedSongList(PageInfo pageInfo) {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getCachedList(pageInfo);
    }

    /**
     * [功能说明] 获取已缓存本地的歌曲数量
     * @return  歌曲数量
     */
    public int getCachedSongCount() {
        return DAOFactory.getInstance().getSongDAO().getCachedCount();
    }

    /**
     * [功能说明] 删除歌曲
     * @param song  歌曲
     */
    public void delete(Song song) {
        if (song == null) {
            return;
        }

        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        dao.deleteSong(song);
    }
    
    /**
     * [功能说明] 根据歌曲id删除歌曲列表
     * @param ids 歌曲id列表
     * @throws Exception exception
     */
    public void deleteSongList(List<Integer> ids) throws Exception {
        if (ids == null || ids.size() <= 0) {
            return;
        }
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        dao.deleteSongList(ids);
    }

    /**
     * [功能说明] 添加歌曲
     * @param song  歌曲
     * @return  添加成功则返回true，否则返回false
     */
    public boolean add(Song song) {
        if (song == null) {
            return false;
        }

        SongDAO dao = DAOFactory.getInstance().getSongDAO();

        return dao.add(song);
    }

    /**
     * [功能说明] 更新歌曲
     * @param song  歌曲
     * @return  更新成功则返回true，否则返回false
     */
    public boolean update(Song song) {
        if (song == null) {
            return false;
        }

        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.update(song);
    }

    /**
     * [功能说明] 获取歌曲媒体文件列表
     * @param id  歌曲id
     * @return  歌曲媒体文件列表
     */
    public List<Media> getMediaList(int id) {
        return DAOFactory.getInstance().getMediaDAO().getMedia(id);
    }

    /**
     * [功能说明] 保存歌曲，歌曲存在则更新，不存在则插入
     * @param song  歌曲
     * @return  保存成功则返回true，否则返回false
     */
    public boolean save(Song song) {
        if (song == null) {
            return false;
        }

        if (isExist(song.getId())) {
            return update(song);
        }

        return add(song);
    }
    
    /**
     * [功能说明] 保存歌曲列表
     * @param list  歌曲列表
     * @return  保存成功则返回true，否则返回false
     */
    public boolean save(List<Song> list) {
        if (list.size() == 0) {
            return true;
        }

        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        dao.save(list);

        return true;
    }
    
    /**
     * [功能说明] 根据首拼获取候选字母列表
     * @param spell 首拼
     * @return  候选字母列表
     */
    public char[] getCharacterList(String spell) {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getCharacterList(spell);
    }
    
    /**
     * [功能说明] 根据首拼获取歌曲列表，该接口首拼匹配采用的是模糊匹配
     * @param spell 首拼
     * @param pageInfo  分页信息
     * @param local 是否仅查询本地歌曲
     * @return  歌曲列表
     */
    public List<Song> getSongListByFuzzySpell(String spell, PageInfo pageInfo, boolean local) {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getListByFuzzySpell(spell, pageInfo, local);
    }
    
    /**
     * [功能说明] 根据首拼获取歌曲总数，该接口首拼匹配采用的是模糊匹配
     * @param spell 首拼
     * @return  歌曲总数
     */
    public int getCountByFuzzySpell(String spell, boolean local) {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getCountByFuzzySpell(spell, local);
    }
    
    /**
     * [功能说明] 根据歌星名字，获取歌曲总数
     * @param name 歌星名字
     * @return 数量
     */
    public int getCountBySingerName(String name/*, boolean local*/) {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getCountBySingerName(name/*, local*/);
    }
    
    /**
     * [功能说明] 从数据中心获取歌曲信息
     * @param id  歌曲id
     * @return  歌曲信息
     * @throws Exception  该过程出错的时候会抛出异常
     */
    public Song getSongFromDataCenter(int id) throws Exception {
        return DCDomain.getInstance().requestSongInfo(id);
    }
    
    /**
     * [功能说明]歌曲是否已缓存
     * @param songId 歌曲id
     * @return true 已缓存  false 未缓存
     */
    public boolean isSongCached(int songId) {
        MediaDAO dao = DAOFactory.getInstance().getMediaDAO();
        try {
			return dao.hasCachedMedia(songId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return false;
    }
    
    /**
     * [功能说明] 获取儿童歌曲列表
     * @param pageInfo  分页信息
     * @return  歌曲列表
     */
    public List<Song> getSongListOfChild(PageInfo pageInfo) {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getSongByType(SongDAO.SONGTYPE_CHILD, pageInfo);
    }
    
    /**
     * [功能说明] 获取儿童歌曲数量
     * @return  歌曲数量
     */
    public int getSongCountOfChild() {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getSongCountByType(SongDAO.SONGTYPE_CHILD);
    }
    
    /**
     * [功能说明] 获取戏曲列表
     * @param pageInfo  分页信息
     * @return 返回戏曲列表
     */
    public List<Song> getSongListOfDrama(PageInfo pageInfo) {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getSongByType(SongDAO.SONGTYPE_DRAMA, pageInfo);
    }
    
    /**
     * [功能说明] 获取戏曲数量
     * @return  戏曲数量
     */
    public int getSongCountOfDrama() {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getSongCountByType(SongDAO.SONGTYPE_DRAMA);
    }
    

    public String getSongIdCollection() {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getSongIdCollection();
    }
    
    /**
     * [清空表]
     * @return 
     */
    public boolean clearList() {
        return DAOFactory.getInstance().getSongDAO().clearList();
    }
    
    /**
     * [复合条件查找歌曲]
     * @param songId 歌曲id
     * @param songWord 歌曲字数
     * @param songName 歌曲名称
     * @param songSpell 歌曲首拼
     * @param singerId 歌手id
     * @param langTypeId 语种
     * @param songTypeId 曲种
     * @param startPos 起始位置
     * @param requestNum 请求数量
     * @return 符合条件歌曲列表
     */
    public List<Song> getSongByCondition(int songId, int songWord, String songName, 
            String songSpell, int singerId, int langTypeId,
            int songTypeId, int startPos, int requestNum) {
        return DAOFactory.getInstance().getSongDAO()
                .getListByComplex(songId, songWord, songName, songSpell,
                        singerId, langTypeId, songTypeId, startPos, requestNum);
    }
    
    public int calculateQualifyNum(String collection) {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.calculateQualifyNum(collection);
    }
    
    /**
     * [智能清理时获取待删列表]
     * @return 清理列表
     */
    public List<Integer> getListIntelligent() {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getListIntelligent();
    }
}
