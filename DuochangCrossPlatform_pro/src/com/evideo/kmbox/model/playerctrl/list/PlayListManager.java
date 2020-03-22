package com.evideo.kmbox.model.playerctrl.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Message;
import android.util.SparseArray;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.SelectedListDAO;
import com.evideo.kmbox.model.dao.data.ISongObserver;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.dao.data.SongSubject;
import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
import com.evideo.kmbox.model.playerctrl.PlayListDAOManager;
import com.evideo.kmbox.model.playerctrl.PlayListDAOManager.ListHandler;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.model.songinfo.SongCategory;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.ToastUtil;

/**
 * [已点列表管理者]
 */
public final class PlayListManager implements ISongObserver {
    
    private static PlayListManager instance = null;
    private List<KmPlayListItem> mList = null;
    //已点列表时间戳
    private long mListChangeStamptime = System.currentTimeMillis();
    private SparseArray<IPlayListListener> mListeners = null;
    private int mId = 0;
    
    /**
     * [获取单例]
     * @return 返回单例
     */
    public static PlayListManager getInstance() {
        if(instance == null) {
            synchronized (PlayListManager.class) {
                PlayListManager temp = instance;
                if(temp == null) {
                  temp = new PlayListManager();
                  instance = temp;
                }
            }
         }
         return instance;  
    }
    
    /**
     * [获取已点列表更改时间戳]
     * @return 返回时间戳
     */
    public long getListChangeStamptime() {
        return mListChangeStamptime;
    } 
    
    private PlayListManager() {
        mList = new ArrayList<KmPlayListItem>();
        mListeners = new SparseArray<IPlayListListener>();
    }
    
    /**
     * [初始化]
     */
    public void init(/*boolean dataEffective*/) {
        SelectedListDAO dao = DAOFactory.getInstance().getSelectedListDAO();
        List<KmPlayListItem> list = dao.getlist();
        if (list.size() > 0) {
            EvLog.e("playlist init, play list data size=" + list.size());
           /* if (!dataEffective) {
                EvLog.e("playlist init, play list data invalid,clear play list");
                mId = 1;
                Message msg = new Message();
                msg.what = ListHandler.PLAYLIST_CLEAR_ALL_ITEM;
                PlayListDAOManager.getInstance().getHandler().sendMessage(msg);
                return;
            } */
            for (KmPlayListItem i : list) {
                this.addItem(i, false);
            }
            mId = dao.getMaxId() + 1;
            notifyListener();
        }
        EvLog.e("playlist init, play list data size=" + list.size());
    }
    
   
    public void uninit() {
        EvLog.i("PlayListManager uninit---------------");
        if ( mList != null ) {
            mList.clear();
            mList = null;
        }
        
        if ( mListeners != null ) {
            mListeners.clear();
            mListeners = null;
        }
        instance = null;
    }
    
    /**
     * [获取歌曲信息异步任务类]
     */
    class MyAsyncPresenter extends AsyncPresenter<Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) throws Exception {
            String customerId = (String) params[0];
            int songId = (Integer) params[1];
            boolean top = (Boolean) params[2];
            Song song = SongManager.getInstance().getSongFromDataCenter(songId);
            if (song == null) {
                // FIXME
                EvLog.d(songId + " get from dc failed!");
                return false;
            }
            
            if (!SongManager.getInstance().add(song)) {
                EvLog.d(songId + " add to dc failed!");
                return false;
            }
            
            return addSong(customerId, songId, top);
        }

        @Override
        protected void onCompleted(Boolean result, Object... params) {
            if (result != null) {
                if (result) {
                    
                } else {
                    ToastUtil.showToast(BaseApplication.getInstance(), R.string.request_song_info_from_datacenter_failed);
                }
            }
        }

        @Override
        protected void onFailed(Exception exception, Object... params) {
            ToastUtil.showToast(BaseApplication.getInstance(), R.string.request_song_info_from_datacenter_failed);
        }
    }
    
    /**
     * [根据songid加歌]
     * @param customerId 客户ID
     * @param songId 歌曲ID
     * @param top 是否置顶
     * @return true:成功;false：失败
     */
    public synchronized boolean addSong(String customerId, int songId, boolean top) {
        //统计点播
        LogAnalyzeManager.onEventOrderSong(BaseApplication.getInstance(),songId);
        
        if (SongManager.getInstance().isExist(songId)) {
            return this.addItem(customerId, songId, top);
        }
        
        EvLog.d(customerId + " order " + songId + ",but is not exist in song.db");
        MyAsyncPresenter presenter = new MyAsyncPresenter();
        presenter.start(customerId, songId, top);
        
        return true;
    }

    /**
     * [根据song类加歌]
     * @param customerId 客户ID
     * @param song 歌曲类
     * @param top 是否置顶
     * @return true:成功;false：失败
     */
    private synchronized boolean addItem(String customerId, int songid, boolean top) {
        Song song = SongManager.getInstance().getSongById(songid);
        if (song == null) {
            return false;
        }
        int serialNum = -1;
        boolean isExistInList  = isExistBySongId(songid);
        if (!isExistInList) {
            mId++;
            serialNum = mId;
            KmPlayListItem item = new KmPlayListItem(song, serialNum, customerId, SongCategory.CATEGORY_PLAYLIST);

            if (!this.addItem(item, false)) {
                return false;
            }
            Message msg = PlayListDAOManager.getInstance().getHandler().obtainMessage();
            msg.what = ListHandler.PLAYLIST_ADD_ITEM;
            msg.obj = item;
            PlayListDAOManager.getInstance().getHandler().sendMessage(msg);
        } else {
            serialNum = this.getSerialNum(songid);
        }
        
        if (top) {
            if (this.getPos(serialNum) == 0) {
                if (!isExistInList) {
                    notifyListener();
                    EvLog.d("song is add to pos 0");
                    return true;
                } else {
                    EvLog.d("song is already in orderlist pos 0");
                    return true;
                }
            }
            
            if (!this.topItem(serialNum)) {
                EvLog.d("top failed");
                return false;
            }
              
           /* if (!mSelectedListDao.topSong(serialNum)) {
                return false;
            }*/
            Message msg = PlayListDAOManager.getInstance().getHandler().obtainMessage();
            msg.what = ListHandler.PLAYLIST_TOP_ITEM;
            msg.arg1 = serialNum;
            PlayListDAOManager.getInstance().getHandler().sendMessage(msg);
        }
        notifyListener();
        return true;            
    }
    
    public synchronized boolean delSongByPos(int pos) {
        if (mList.size() <=0 ) {
            return false;
        }
        int serialNum = mList.get(0).getSerialNum();
        if (!this.delItem(serialNum)) {
            EvLog.e("delSongByPos: invalid at pos :" + pos);
            return false;
        }
        mIsUserOpertion = true;
        notifyListener();
        Message msg = PlayListDAOManager.getInstance().getHandler().obtainMessage();
        msg.what = ListHandler.PLAYLIST_DEL_ITEM_BY_INDEX;
        msg.arg1 = serialNum;
        PlayListDAOManager.getInstance().getHandler().sendMessage(msg);
        return true;
    }
    
    private boolean mIsUserOpertion = true;
    
    /**
     * [功能说明] 是否是用户主动操作
     * @return
     */
    public boolean isUserOpertion() {
        return mIsUserOpertion;
    }
    /**
     * [功能说明] 程序切歌时调用
     * @param serialNum
     * @return
     */
    public synchronized boolean delSong(int serialNum) {
        if (!this.delItem(serialNum)) {
            EvLog.e("delSongInOrderList: invalid serialNum:" + serialNum);
            return false;
        }
        mIsUserOpertion = false;
        notifyListener();
        Message msg = PlayListDAOManager.getInstance().getHandler().obtainMessage();
        msg.what = ListHandler.PLAYLIST_DEL_ITEM_BY_INDEX;
        msg.arg1 = serialNum;
        PlayListDAOManager.getInstance().getHandler().sendMessage(msg);
        return true;
    }
    
    /**
     * [删除歌曲] 用户主动删除歌曲
     * @param id 歌曲序列号
     * @return true:成功;false：失败
     */
    public synchronized boolean delSongByUser(int serialNum) {
        if (!this.delItem(serialNum)) {
            EvLog.e("delSongInOrderList: invalid serialNum:" + serialNum);
            return false;
        }
        mIsUserOpertion = true;
       /* if (!mSelectedListDao.deleteSong(serialNum)) {
            EvLog.e("delSongInOrderList: db not include serialNum:" + serialNum);
            return false;
        }*/
        notifyListener();
        Message msg = PlayListDAOManager.getInstance().getHandler().obtainMessage();
        msg.what = ListHandler.PLAYLIST_DEL_ITEM_BY_INDEX;
        msg.arg1 = serialNum;
        PlayListDAOManager.getInstance().getHandler().sendMessage(msg);
        return true;
    }
    
    /**
     * [删除歌曲]
     * @param songid 歌曲id
     * @return true:成功;false：失败
     */
    public synchronized boolean delSongBySongId(int songid) {
        if (!this.delItemBySongId(songid)) {
            EvLog.e("delSongInOrderList: invalid songid:" + songid);
            return false;
        }
        
        /*if (!mSelectedListDao.deleteSong(songid)) {
            EvLog.e("delSongInOrderList: db not include songid:" + songid);
            return false;
        }*/
        mIsUserOpertion = false;
        notifyListener();
        Message msg = PlayListDAOManager.getInstance().getHandler().obtainMessage();
        msg.what = ListHandler.PLAYLIST_DEL_ITEM_BY_SONGID;
        msg.arg1 = songid;
        PlayListDAOManager.getInstance().getHandler().sendMessage(msg);

        return true;
    }
    
    /**
     * [获取数量]
     * @return 歌曲数量
     */
    public synchronized int getCount() {
        return mList.size();
    }
    
    /**
     * [获取歌曲列表]
     * @return 返回歌曲列表
     */
    public  List<KmPlayListItem> getList() {
        final List<KmPlayListItem> list = new ArrayList<KmPlayListItem>();
        list.clear();
        synchronized(mList) {
            list.addAll(mList);
        }
        return list;
    }
    
    public int getListCount() {
        synchronized(mList) {
            return mList.size();
        }
    }
    /**
     * [往歌曲列表中加入项]
     * @param song KmPlayListItem 类
     * @param isAllowAlreadyExist 是否已存在于列表
     * @return true:成功;false：失败
     */
    private boolean addItem(KmPlayListItem song, boolean isAllowAlreadyExist) {
        synchronized (mList) {        
            
            if (!isAllowAlreadyExist) {
                if (isExistInList(song.getSongId())) {
                    return false;
                }
            }
//            KmPlayListItem item = new KmPlayListItem(song);
            mList.add(song);
            return true;
        }
    }
    private boolean isExistInList(int songID) {
//        synchronized (mList) {
            boolean isAlreadyExist = false;
            Iterator<KmPlayListItem> sListIterator = mList.iterator();
            while (sListIterator.hasNext()) {
                KmPlayListItem e = sListIterator.next();
                if (e.getSongId() == songID) {
                    isAlreadyExist = true;
                }
            }

            return isAlreadyExist;
//        }
    }
    /**
     * [据位置获取歌曲]
     * @param pos 位置
     * @return 返回歌曲的KmPlayListItem对类
     */
    public  KmPlayListItem getItemByPos(int pos) {
        synchronized (mList) {
            if (pos < 0 || pos >= mList.size()) {
                return null;
            } else {
                return mList.get(pos);
            }
        }
    }
    
    public  boolean getCopyItemByPos(int pos,KmPlayListItem item) {
        if (item == null) {
            return false;
        }
        synchronized (mList) {
            if (pos < 0 || pos >= mList.size()) {
                return false;
            } else {
//               
                item.copy(mList.get(pos));
                return true;
            }
        }
    }
    
    
    /**
     * [根据songid获取歌曲位置]
     * @param songId 歌曲的songid
     * @return 返回位置索引
     */
    public int getPosBySongId(int songId) {
        synchronized (mList) {
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getSongId() == songId) {
                    return i;
                }
            }
            return -1;
        }
    }
    
    /**
     * [获取序列号]
     * @param songid 歌曲songid
     * @return 返回序列号
     */
    public int getSerialNum(int songid) {
        synchronized (mList) {
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getSongId() == songid) {
                    return mList.get(i).getSerialNum();
                }
            }
            return -1;
        }
    }
    
    /**
     * [在列表中顶歌]
     * @param id 序列号
     * @return true:成功;false：失败
     */
    private boolean topItem(int id) {
        synchronized (mList) {
            KmPlayListItem e = null;
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getSerialNum() == id) {
                    e = mList.get(i);
                    mList.remove(i);
                    break;
                }
            }
    
            boolean result = false;
            if (e != null) {
                result = true;
                if (mList.size() > 0) {
                    mList.add(1, e);
                } else {
                    mList.add(e);
                }
            }
            return result;
        }
    }
    
    /**
     * [删除项]
     * @param serialNum 序列号
     * @return true:成功;false：失败
     */
    private boolean delItem(int serialNum) {
        synchronized (mList) {
            int index = -1;
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getSerialNum() == serialNum) {
                    index = i;
                    break;
                }
            }

            if (index == -1) {
                EvLog.w(serialNum + " not exist in orderlist,del failed");
                return false;
            }
            
            EvLog.w(mList.get(index).getSongName() + " del from playlist");
            mList.remove(index);
            if (mList.size() == 0) {
                mId = 1;
            }
//            notifyListener();
            return true;
        }
    }
    
    private boolean delItemBySongId(int songid) {
        synchronized (mList) {
            int index = -1;
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getSongId() == songid) {
                    index = i;
                    break;
                }
            }

            if (index == -1) {
                EvLog.w(songid + " not exist in orderlist,del failed");
                return false;
            }
            
            EvLog.w("del " + mList.get(index).getSongName());
            mList.remove(index);
            if (mList.size() == 0) {
                mId = 1;
            }
            return true;
        }
    }
    // FIXME
    /**
     * [歌曲是否已存在]
     * @param id 歌曲序列号
     * @return true:已存在;false：不存在
     */
    public boolean isExists(int serialNum) {
        synchronized (mList) {
            boolean isAlreadyExist = false;
            Iterator<KmPlayListItem> sListIterator = mList.iterator();
            while (sListIterator.hasNext()) {
                KmPlayListItem e = sListIterator.next();
                if (e.getSerialNum() == serialNum) {
                    isAlreadyExist = true;
                    break;
                }
            }
            return isAlreadyExist;
        }
//        return mSelectedListDao.isExist(id);
    }
    
    /**
     * [根据序列号获取位置]
     * @param id 序列号
     * @return 位置索引
     */
    public int getPos(int id) {
        synchronized (mList) {
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getSerialNum() == id) {
                    return i;
                }
            }
            return -1;
        }
    }
    
    public int getPosByDownId(long downId) {
        synchronized (mList) {
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getDownId() == downId) {
                    return i;
                }
            }
            return -1;
        }
    }
    
    /**
     * [根据序列号获取歌曲项]
     * @param id 序列号
     * @return 歌曲项
     */
    public KmPlayListItem getItemBySerialNum(int id) {
        synchronized (mList) {
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getSerialNum() == id) {
                   return mList.get(i);
                }
            }
            return null;
        }
    }
    
    public KmPlayListItem getItemByDownId(long downId) {
        synchronized (mList) {
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getDownId() == downId) {
                   return mList.get(i);
                }
            }
            return null;
        }
    }
    
    /**
     * [清空列表]
     */
    public void clearList() {
        synchronized (mList) {
            mList.clear();
        }
        /*synchronized (mSelectedListDao) {
            mSelectedListDao.clearlist();
        }   */
        mId = 1;
        Message msg = PlayListDAOManager.getInstance().getHandler().obtainMessage();
        msg.what = ListHandler.PLAYLIST_CLEAR_ALL_ITEM;
        PlayListDAOManager.getInstance().getHandler().sendMessage(msg);
        notifyListener();
    }
    /**
     * [据songid判断是否已存在于列表]
     * @param songId 歌曲songid
     * @return true:存在;false：不存在
     */
    public boolean isExistBySongId(int songId) {
        // TODO FIXME 以内存为判断依据，需重构为以数据库为判断依据
//        if (songId < 0) {
//            return false;
//        }
//        int pos = this.getPosBySongId(songId);
//        return pos >= 0;
        synchronized (mList) {
            boolean isAlreadyExist = false;
            Iterator<KmPlayListItem> sListIterator = mList.iterator();
            KmPlayListItem item = null;
            while (sListIterator.hasNext()) {
                item = sListIterator.next();
                if (item.getSongId() == songId) {
                    isAlreadyExist = true;
                }
            }
            return isAlreadyExist;
        }
    }
   
    /**
     * [已点列表监听器]
     */
    public interface IPlayListListener { 
        /**
         * [列表变化时的动作]
         */
        void onPlayListChange(); 
    }; 

     

    /**
     * [注册监听器]
     * @param listener 监听器
     */
    public void registerListener(IPlayListListener listener) { 
        if (listener == null) { 
            return; 
        } 
    
        synchronized (this.mListeners) { 
            int index = this.mListeners.indexOfValue(listener);
            if (index < 0) {
                this.mListeners.append(this.mListeners.size(), listener);
            }
        } 
    } 

    /**
     * [注销监听器]
     * @param listener 监听器
     */
    public void unregisterListener(IPlayListListener listener) { 
        if (listener == null) { 
            return; 
        } 

        synchronized (this.mListeners) { 
            int index = this.mListeners.indexOfValue(listener);
            if (index >= 0) {
                this.mListeners.removeAt(index);
            }
        } 
    }
    
    /**
     * [通知注册者]
     */
    public void notifyListener() {
        EvLog.i("notifyListener size=" + this.mListeners.size());
        synchronized (this.mListeners) { 
            mListChangeStamptime = System.currentTimeMillis();
            for (int i = 0; i < this.mListeners.size(); i++) { 
                mListeners.get(i).onPlayListChange();
            } 
        }    
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSongsToBeDeleted(List<Integer> ids) {
        EvLog.i("play list recv song to del begin");
        long timeStart = System.currentTimeMillis();
        if (ids == null || ids.isEmpty()) {
            return;
        }
        final List<KmPlayListItem> list = new ArrayList<KmPlayListItem>();
        synchronized (mList) {
            list.addAll(mList);
        }
        EvLog.d("need del ids.size=" + ids.size() + " ,playlist.size=" + list.size());
        realDel = false;
        /*if (ids.size() > list.size())*/ {
            for (int i = 0; i < list.size();i++) {
                if (ids.contains(list.get(i).getSongId())) {
                    delSongBySongId(list.get(i).getSongId());
                    realDel = true;
                }
            }
        } /*else {
            for(int i = 0; i < ids.size();i++) {
                delSongBySongId(ids.get(i));
                realDel = true;
            }
        }*/
        EvLog.i("play list recv song to del over"+ (System.currentTimeMillis()-timeStart));
    }

    private boolean realDel =false;
    @Override
    public void onSongdDeletedFinish() {
        if (realDel) {
            notifyListener();
        }
    }
}
