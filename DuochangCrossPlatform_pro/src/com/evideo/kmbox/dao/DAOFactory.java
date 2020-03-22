package com.evideo.kmbox.dao;

import java.util.HashMap;

import android.content.Context;

import com.evideo.kmbox.KmApplication;

/**
 * [DAO工厂]
 */
public final class DAOFactory {
    private static DAOFactory sInstance = null;
    
    private boolean mCacheDAOInstances = true;
    private SongDAO mSongDAO = null;
    private MediaDAO mMediaDAO = null;
    private SingerDAO mSingerDAO = null;
    private ConfigDAO mConfigDAO = null;
    private StorageVolumeDAO mStorageVolumeDAO = null;
    private CacheManagerDAO mCacheManagerDAO = null;
    private SelectedListDAO mSelectedListDAO = null;
    private FavoriteListDAO mFavoriteListDAO = null;
    private SongMenuDAO mSongMenuDAO = null;
    private SongMenuDetailDAO mSongMenuDetailDAO = null;
	
	private SongTopDAO mSongTopDAO = null;
    private SongTopDetailDAO mSongTopDetailDAO = null;
    private WholeRemoteSongDAO mWholeRemoteSongDAO = null;
    private WholeLocalSongDAO mWholeLocalSongDAO = null;
    private WholeRemoteSingerDAO mWholeRemoteSingerDAO = null;
    private WholeLocalSingerDAO mWholeLocalSingerDAO = null;
    private WholeMediaDAO mWholeRemoteMediaDAO = null;
    private WholeMediaDAO mWholeLocalMediaDAO = null;
    private SongIdRemoteDAO mSongIdRemoteDAO = null;
    private SongIdLocalDAO mSongIdLocalDAO = null;
    private SearchHistoryDAO mSearchHistoryDAO = null;
    private FreeSongDAO mFreeSongDAO = null;
    
    private HashMap<String, StorageVolumeMediaDAO> mKmBoxStorageVolumeMediaDAOMap = 
    		new HashMap<String, StorageVolumeMediaDAO>();
    
    /** [已唱列表] */
    private SungListDAO mSungListDAO = null;
    
    /**
     * [获取单例]
     * @return 返回单例
     */
    public static synchronized DAOFactory getInstance() {
        if (sInstance == null) {
            sInstance = new DAOFactory();
        }
        
        return sInstance;
    }
    
    private DAOFactory() {
        
    }
    
    public SongDAO getSongDAO() {
        if (mCacheDAOInstances) {
            if (mSongDAO == null) {
                mSongDAO = new SongDAO();
            }
            
            return mSongDAO;
        }
        
        return new SongDAO(); 
    }
    
    public MediaDAO getMediaDAO() {
        if (mCacheDAOInstances) {
            if (mMediaDAO == null) {
                mMediaDAO = new MediaDAO();
            }
            
            return mMediaDAO;
        }
        
        return new MediaDAO(); 
    }
    
    public SingerDAO getSingerDAO() {
        if (mCacheDAOInstances) {
            if (mSingerDAO == null) {
                mSingerDAO = new SingerDAO();
            }
            
            return mSingerDAO;
        }
        
        return new SingerDAO(); 
    }
    
    public ConfigDAO getConfigDAO() {
        if (mCacheDAOInstances) {
            if (mConfigDAO == null) {
                mConfigDAO = new ConfigDAO();
            }
            
            return mConfigDAO;
        }
        
        return new ConfigDAO();
    }
    
    public StorageVolumeDAO getStorageVolumeDAO() {
        if (mCacheDAOInstances) {
            if (mStorageVolumeDAO == null) {
                mStorageVolumeDAO = new StorageVolumeDAO();
            }
            
            return mStorageVolumeDAO;
        }
        
        return new StorageVolumeDAO();
    }
    
    public CacheManagerDAO getCacheManagerDAO() {
        if (mCacheDAOInstances) {
            if (mCacheManagerDAO == null) {
                mCacheManagerDAO = new CacheManagerDAO();
            }
            
            return mCacheManagerDAO;
        }
        
        return new CacheManagerDAO();
    }
    
    /**
     * [获取收藏列表DAO]
     * @return 返回收藏列表DAO
     */
    public FavoriteListDAO getFavoriteListDAO() {
        if (mCacheDAOInstances) {
            if (mFavoriteListDAO == null) {
                mFavoriteListDAO = new FavoriteListDAO();
            }
            
            return mFavoriteListDAO;
        }
        
        return new FavoriteListDAO();
    }
    
    /**
     * [获取歌单DAO]
     * @return 歌单DAO
     */
    public SongMenuDAO getSongMenuDAO() {
        if (mCacheDAOInstances) {
            if (mSongMenuDAO == null) {
                mSongMenuDAO = new SongMenuDAO();
            }
            return mSongMenuDAO;
        }
        return new SongMenuDAO();
    }
    
    /**
     * [获取歌单详情DAO]
     * @return 歌单详情DAO
     */
    public SongMenuDetailDAO getSongMenuDetailDAO() {
        if (mCacheDAOInstances) {
            if (mSongMenuDetailDAO == null) {
                mSongMenuDetailDAO = new SongMenuDetailDAO();
            }
            return mSongMenuDetailDAO;
        }
        return new SongMenuDetailDAO();
    }
    
    /**
     * [获取排行DAO]
     * @return 歌单DAO
     */
    public SongTopDAO getSongTopDAO() {
        if (mCacheDAOInstances) {
            if (mSongTopDAO == null) {
                mSongTopDAO = new SongTopDAO();
            }
            return mSongTopDAO;
        }
        return new SongTopDAO();
    }
    
    /**
     * [获取排行详情DAO]
     * @return 歌单详情DAO
     */
    public SongTopDetailDAO getSongTopDetailDAO() {
        if (mCacheDAOInstances) {
            if (mSongTopDetailDAO == null) {
                mSongTopDetailDAO = new SongTopDetailDAO();
            }
            return mSongTopDetailDAO;
        }
        return new SongTopDetailDAO();
    }
    
    /**
     * [获取搜索历史DAO]
     * @return 搜索历史DAO
     */
    public SearchHistoryDAO getSearchHistoryDAO() {
        if (mCacheDAOInstances) {
            if (mSearchHistoryDAO == null) {
                mSearchHistoryDAO = new SearchHistoryDAO();
            }
            return mSearchHistoryDAO;
        }
        return new SearchHistoryDAO();
    }

    public void setGlobalContext(Context context) {
        DAOHelper.init(context);
        RemoteDAOHelper.init(context);
    }

    public FreeSongDAO getFreeSongDAO() {
        if (mCacheDAOInstances) {
            if (mFreeSongDAO == null) {
                mFreeSongDAO = new FreeSongDAO();
            }
            
            return mFreeSongDAO;
        }
        return new FreeSongDAO();
    }
    /**
     * [获取已点列表DAO]
     * @return 返回已点列表DAO
     */
    public SelectedListDAO getSelectedListDAO() {
    
        if (mCacheDAOInstances) {
            if (mSelectedListDAO == null) {
                mSelectedListDAO = new SelectedListDAO();
            }
            
            return mSelectedListDAO;
        }
        
        return new SelectedListDAO();
    }
    
    /**
     * [获取已点列表DAO]
     * @return 返回已点列表DAO
     */
    public SungListDAO getSungListDAO() {
        if (mCacheDAOInstances) {
            if (mSungListDAO == null) {
                mSungListDAO = new SungListDAO();
            }
            return mSungListDAO;
        }
        return new SungListDAO();
    }
    
    public StorageVolumeMediaDAO getStorageVolumeMediaDAO(String path) {
    	
    	if (path == null || path.length() == 0) {
    		return null;
    	}

    	StorageVolumeMediaDAO dao = null;

    	if (mCacheDAOInstances) {
	    	synchronized (mKmBoxStorageVolumeMediaDAOMap) {
				dao = mKmBoxStorageVolumeMediaDAOMap.get(path);
				if (dao == null) {
					dao = new StorageVolumeMediaDAO(KmApplication.getInstance().getContext(), path);
					mKmBoxStorageVolumeMediaDAOMap.put(path, dao);
				}
			}
	    	
	    	return dao;
    	}
        
        return new StorageVolumeMediaDAO(KmApplication.getInstance().getContext(), path); 
    }
    
    public void removeStorageVolumeMediaDAO(String path) {
    	if (path == null || path.length() == 0) {
    		return;
    	}

    	if (mCacheDAOInstances) {
	    	synchronized (mKmBoxStorageVolumeMediaDAOMap) {
	    		mKmBoxStorageVolumeMediaDAOMap.remove(path);
			}
    	}
    }
    
    public WholeMediaDAO getWholeRemoteMediaDAO() {
        if (mCacheDAOInstances) {
            if (mWholeRemoteMediaDAO == null) {
                mWholeRemoteMediaDAO = new WholeRemoteMediaDAO();
            }

            return mWholeRemoteMediaDAO;
        }

        return new WholeRemoteMediaDAO();
    }
    
    public WholeMediaDAO getWholeLocalMediaDAO() {
        if (mCacheDAOInstances) {
            if (mWholeLocalMediaDAO == null) {
                mWholeLocalMediaDAO = new WholeLocalMediaDAO();
            }

            return mWholeLocalMediaDAO;
        }

        return new WholeLocalMediaDAO();
    }
    
    /**
     * [获取全库歌星表操作权限]
     * 
     * @return  全库歌星表操作
     */
    public WholeSingerDAO getWholeRemoteSingerDAO() {

        if (mCacheDAOInstances) {
            if (mWholeRemoteSingerDAO == null) {
                mWholeRemoteSingerDAO = new WholeRemoteSingerDAO();
            }

            return mWholeRemoteSingerDAO;
        }

        return new WholeRemoteSingerDAO();
    }
    
    /**
     * [获取全库歌星表操作权限]
     * 
     * @return  全库歌星表操作
     */
    public WholeSingerDAO getWholeLocalSingerDAO() {

        if (mCacheDAOInstances) {
            if (mWholeLocalSingerDAO == null) {
                mWholeLocalSingerDAO = new WholeLocalSingerDAO();
            }

            return mWholeLocalSingerDAO;
        }

        return new WholeLocalSingerDAO();
    }
    
    /**
     * [获取全库歌曲表操作权限]
     * 
     * @return 全库歌曲表权限
     */
    public WholeSongDAO getWholeRemoteSongDAO() {

        if (mCacheDAOInstances) {
            if (mWholeRemoteSongDAO == null) {
                mWholeRemoteSongDAO = new WholeRemoteSongDAO();
            }

            return mWholeRemoteSongDAO;
        }

        return new WholeRemoteSongDAO();
    }
    
    /**
     * [获取全库歌曲表操作权限]
     * 
     * @return 全库歌曲表权限
     */
    public WholeSongDAO getWholeLocalSongDAO() {

        if (mCacheDAOInstances) {
            if (mWholeLocalSongDAO == null) {
                mWholeLocalSongDAO = new WholeLocalSongDAO();
            }

            return mWholeLocalSongDAO;
        }

        return new WholeLocalSongDAO();
    }
    
    /**
     * [获取全库歌曲表操作权限]
     * 
     * @return 全库歌曲表权限
     */
    public SongIdDAO getSongIdRemoteDAO() {

        if (mCacheDAOInstances) {
            if (mSongIdRemoteDAO == null) {
                mSongIdRemoteDAO = new SongIdRemoteDAO();
            }

            return mSongIdRemoteDAO;
        }

        return new SongIdRemoteDAO();
    }
    
    /**
     * [获取全库歌曲表操作权限]
     * 
     * @return 全库歌曲表权限
     */
    public SongIdDAO getSongIdLocalDAO() {

        if (mCacheDAOInstances) {
            if (mSongIdLocalDAO == null) {
                mSongIdLocalDAO = new SongIdLocalDAO();
            }

            return mSongIdLocalDAO;
        }

        return new SongIdLocalDAO();
    }
}
