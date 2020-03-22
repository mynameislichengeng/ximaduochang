package com.evideo.kmbox.model.playerctrl.sunglist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.text.TextUtils;
import android.util.SparseArray;

import com.evideo.kmbox.SystemConfigManager;
import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.SungListDAO;
import com.evideo.kmbox.model.dao.data.ISongObserver;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.dao.data.SongSubject;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.util.EvLog;

/**
 * [已唱列表管理者]
 */
public final class SungListManager implements ISongObserver {
    private static final String TAG =  SungListManager.class.getSimpleName();
    private static SungListManager instance = null;
    private List<SungListItem> mSungList = null;
    private SungListDAO mSungListDao = null;
    
    /** [时间戳] */
    public long mTimeStamp = 0L;
    private boolean mNeedUpate = false;
    private SparseArray<IPlayHistoryListener> mListeners = null;
    
    /**
     * [已唱列表监听器]
     */
    public interface IPlayHistoryListener { 
        /**
         * [已唱变化时动作]
         */
        void onPlayHistoryChanged(); 
    }; 

    
    /**
     * [获取时间戳]
     * @return 返回时间戳
     */
    public long getTimeStamp() {
        return mTimeStamp;
    }
    
    public boolean isNeedUpdate() {
        return mNeedUpate;
    }
    
    public void resetUpdate() {
        mNeedUpate = false;
    }

    /**
     * [获取单例]
     * @return 返回单例
     */
    public static SungListManager getInstance() {
        
        if (instance == null) {
            synchronized (SungListManager.class) {
                SungListManager temp = instance;
                if(temp == null) {
                  temp = new SungListManager();
                  instance = temp;
                }
            }
        }
        
        return instance;    
    }
    
    private SungListManager() {
        mSungList = new ArrayList<SungListItem>();
        mTimeStamp = System.currentTimeMillis();
        mSungListDao = DAOFactory.getInstance().getSungListDAO();
        mListeners = new SparseArray<IPlayHistoryListener>();
    }
    
    /**
     * [初始化]
     */
    public void init() {
        List<SungListItem> list = mSungListDao.getlist();
        
        for (SungListItem i : list) {
            mSungList.add(i);
            if (mSungList.size() == SystemConfigManager.SUNG_LIST_MAX_SIZE) {
                break;
            }
        }
        EvLog.d("int sunglist item count " + mSungList.size());
    }
    
    public void uninit() {
        if (mSungList != null) {
            mSungList.clear();
            mSungList = null;
        }
        
        if (mListeners != null) {
            mListeners.clear();
            mListeners = null;
        }
        instance = null;
    }
    /**
     * [加入项]
     * @param song 歌曲对象
     * @param isAllowAlreadyExist 是否允许重复
     * @return true:成功;false：失败
     */
    public boolean addItem(KmPlayListItem item,String shareCode,int time) {
        if (item == null) {
            return false;
        }
        
        SungListItem sungItem = new SungListItem(item);
        sungItem.setShareCode(shareCode);
        /*if (RecordConfig.OPEN_RECORD_SAVE_MODE && time > 0) {
//            String shareCode = RecordShare.getUUID();
            if (RecordListManager.getInstance().addItem(sungItem,shareCode,time)) {
                sungItem.setShareCode(shareCode);
            }
        }*/
        
        int id = -1;
        synchronized(mSungListDao) {
            id = mSungListDao.addItem(sungItem);
            if (id < 0) {
                EvLog.e("SungListDAO add failed!");
                return false;
            }
            EvLog.d(sungItem.getSongName() + " get dao index=" + id);
            sungItem.setId(id);
        }
            
        synchronized(mSungList) {
            mSungList.add(sungItem);
            if (mSungList.size() > SystemConfigManager.SUNG_LIST_MAX_SIZE) {
                EvLog.e("sunglist in mem size exceed max size,remove item 0");
                long delId = mSungList.get(0).getId();
                String sharecode = mSungList.get(0).getShareCode();
                mSungList.remove(0);
                mSungListDao.delItem(delId);
            }
            notifyListener();
            return true;
        }
    }
    
    
    public List<SungListItem> getData() {
        final List<SungListItem> list = new ArrayList<SungListItem>();
        list.clear();
        synchronized (mSungList) {  
            list.addAll(mSungList);
        }
        return list;
    }
    
   /* public void updateItem(String shareCode) {
        synchronized (mSungList) {
            if (mSungList.size() == 0) {
                return ;
            }
            
            if (shareCode == null) {
                return;
            }
            
            for(int i = 0; i < mSungList.size();i++) {
                if (mSungList.get(i).getShareCode().equals(shareCode)) {
                    EvLog.d( mSungList.get(i).getSongName() + " update sharecode null");
                    mSungList.get(i).setShareCode("");
                    break;
                }
            }
            
            mSungListDao.updateItem(shareCode);
            notifyListener();
            mNeedUpate = true;
            return;
        }
    }*/
    
    private boolean delItemInDB(int songid) {
        synchronized (mSungListDao) {
            return mSungListDao.delItemBySongId(songid);
        }
    }
    
    /**
     * [功能说明] 根据分享码删除已唱数据
     * @param shareCode 分享码
     * @return
     */
    public boolean delItemByShareCode(String shareCode) {
        synchronized (mSungList) {
            if (mSungList.size() == 0) {
                return false;
            }

            for (SungListItem item : mSungList) {
                if (item.getShareCode().equals(shareCode)) {
                    EvLog.i("find " +shareCode);
                    mSungList.remove(item);
                    mSungListDao.delItem(item.getId());
                    notifyListener();
                    return true;
                }
            }
            EvLog.e("sunglistitem do not find shareCode= "+shareCode);
            /*int delId = mSungList.get(index).getId();
            String sharecode = mSungList.get(index).getShareCode();
            EvLog.d(mSungList.get(index).getSongName() + " del from sunglist" );
            
            mSungList.remove(index);
            mSungListDao.delItem(delId);
            RecordListManager.getInstance().delItem(sharecode);
            notifyListener();*/
            return false;
        }
    }
    private boolean delItemInListBySongId(int songid) {
        synchronized (mSungList) {
            if (mSungList.size() == 0) {
                return false;
            }
            
            int index  = -1;
            
            int itemId = 0; 
            for (int i = 0; i < mSungList.size();i++) {
                itemId = mSungList.get(i).getSongId();
//                EvLog.i("itemId:" + itemId);
                if (itemId == songid) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                EvLog.e(TAG,songid+ " not exist in sunglist,del failed");
                return false;
            }
            EvLog.d(TAG, mSungList.get(index).getSongName() + " del from sunglist,del before:" + mSungList.size() );
            mSungList.remove(index);
            EvLog.d(TAG,  " del after size:" + mSungList.size() );
            notifyListener();
            return true;
        }
    } 
    

    /**
     * [获取歌曲数量]
     * @return 返回歌曲数量
     */
    public int getCount() {
        synchronized (mSungList) {
            return mSungList.size();
        }
    }
    
    /**
     * [据位置索引获取歌曲对象]
     * @param pos 位置索引
     * @return 歌曲对象
     */
    public SungListItem getItemByPos(int pos) {
        synchronized (mSungList) {
            if ( pos <0 || pos >= mSungList.size() ) {
                return null;
            }
            return mSungList.get(pos);
        }
    }
    
    public SungListItem getItem(int id) {
        synchronized (mSungList) {
           for (int i = 0; i < mSungList.size();i++) {
               if (mSungList.get(i).getId() == id) {
                   return mSungList.get(i);
               }
           }
           return null;
        }
    }
    
    public SungListItem getItemByShareCode(String shareCode) {
        if (TextUtils.isEmpty(shareCode)) {
            return null;
        }
        
        synchronized (mSungList) {
            for (int i = 0; i < mSungList.size();i++) {
                if (mSungList.get(i).getShareCode().equals(shareCode)) {
                    return mSungList.get(i);
                }
            }
            return null;
         }
    }
    
    
    /**
     * [注册监听器]
     * @param listener 监听器
     */
    public void registerListener(IPlayHistoryListener listener) { 
        if (listener == null) { 
            return; 
        } 
    
        synchronized (this.mListeners) { 
            int size = mListeners.size();
            int index = mListeners.indexOfValue(listener);
            if (index < 0) {
                mListeners.append(size, listener);
            }
            /*if (!this.mListeners.contains(listener)) {
                this.mListeners.add(listener); 
            }*/
        } 
    } 

    /**
     * [注销监听器]
     * @param listener 监听器
     */
    public void unregisterListener(IPlayHistoryListener listener) { 
        if (listener == null) { 
            return; 
        } 

        synchronized (this.mListeners) { 
            int index = mListeners.indexOfValue(listener);
            if (index >= 0) {
                mListeners.remove(index);
            }
//            mListeners.remove(listener);
        } 
    }
    
    /**
     * [通知监听器]
     */
    public void notifyListener() {
        synchronized (this.mListeners) { 
            for (int i = 0; i < this.mListeners.size(); i++) { 
                mListeners.get(i).onPlayHistoryChanged();
            } 
        }
        
        mTimeStamp = System.currentTimeMillis();
    }
    
    /**
     * [功能说明]注册歌曲观察者
     */
    public void registerSongObserver() {
        SongSubject.getInstance().registerSongObserver(this);
    }
    
    /**
     * [功能说明]注销歌曲观察者
     */
    public void unregisterSongObserver() {
        SongSubject.getInstance().unregisterSongObserver(this);
    }
    
    public void delItemBySongId(int songId) {
        delItemInListBySongId(songId);
        delItemInDB(songId);
    }
    
    
    @Override
    public void onSongsToBeDeleted(List<Integer> ids) {
        EvLog.i("sung list recv song to del begin");
        long timeStart = System.currentTimeMillis();
        if (ids == null || ids.isEmpty()) {
            return;
        }
        final List<SungListItem> list = new ArrayList<SungListItem>();
        list.addAll(mSungList);
        EvLog.d("need del ids.size=" + ids.size() + " ,mSungList.size=" + list.size());
        
        Iterator<SungListItem> iter = list.iterator();
        int songid = 0;
        realDel = false;
        while (iter.hasNext()) {
            songid = iter.next().getSongId();
            if (!SongManager.getInstance().isExist(songid)) {
                iter.remove();
                delItemBySongId(songid);
                realDel = true;
            }
        }
        EvLog.i("sung list recv song to del over"+ (System.currentTimeMillis()-timeStart));
    }


    private boolean realDel =false;
    @Override
    public void onSongdDeletedFinish() {
        if (realDel) {
            notifyListener();
        }
    }
}
