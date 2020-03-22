
package com.evideo.kmbox.widget.mainview.globalsearch;

import java.util.List;

import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.SearchHistoryDAO;


public class SearchHistoryManager {
    
//    private ArrayList<SearchHistoryItem> mDatas = new ArrayList<SearchHistoryItem>();
    
    private static SearchHistoryManager sInstance;
    
    private SearchHistoryManager() {
    }
    
    /**
     * [获取SongTopManager实例]
     * @return SongTopManager实例
     */
    public static SearchHistoryManager getInstance() {
        if (sInstance == null) {
            synchronized (SearchHistoryManager.class) {
                if (sInstance == null) {
                    sInstance = new SearchHistoryManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * [保存搜索历史到数据库中]
     * @param item 搜索历史项
     * @return true 成功    false 失败
     */
    public boolean save(SearchHistoryItem item) {
        SearchHistoryDAO dao = DAOFactory.getInstance().getSearchHistoryDAO();
        return dao.add(item);
    }
  
    
    /**
     * [获取歌单列表]
     * @return List<SongTop> 歌单列表
     */
    public List<SearchHistoryItem> getSearchHistoryList() {
        SearchHistoryDAO dao = DAOFactory.getInstance().getSearchHistoryDAO();
        return dao.getAllSearchItems();
    }
    
    /**
     * [清空搜索历史]
     */
    public void clearSearchHistory() {
        SearchHistoryDAO dao = DAOFactory.getInstance().getSearchHistoryDAO();
        dao.clearList();
    }
}
