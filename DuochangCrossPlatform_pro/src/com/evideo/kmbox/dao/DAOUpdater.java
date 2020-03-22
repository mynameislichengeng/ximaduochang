package com.evideo.kmbox.dao;

import java.util.List;

import android.os.SystemClock;

import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;

/**
 * [曲库数据库升级者]
 */
public class DAOUpdater {

    public static final int MILLISECONDS_IN_SECOND = 1000;
    public static final int PAGE_SIZE = 1000;
    public static final int SLEEP_TIME = 500;
    private int mCount = 0;
    private int mProgress = 0;
    private RemoteSingerDAO mSingerDAO = null;
    private RemoteSongDAO mSongDAO = null;
    
    private OnStartListener mOnStartListener = null;
    private OnFinishListener mOnFinishListener = null;
    private OnProgressListener mOnProgressListener = null;
    
    private int mSleepTime = SLEEP_TIME;
    private String mDatetime = "";
    
    /**
     * [数据库升级接口]
     * @param file kmbox.db文件位置
     * @return 升级是否成功
     */
    public boolean update(String file) {
        EvLog.i(">>>>>>>>>>>>>> update " + file);
        
        mDatetime = "1970-02-16 18:29:02";
        mSingerDAO = new RemoteSingerDAO();
        mSongDAO = new RemoteSongDAO();
        mCount = mSingerDAO.getCountAfterDatetime(mDatetime) + mSongDAO.getCountAfterDateTime(mDatetime);
        mProgress = 0;

        if (mOnStartListener != null) {
            mOnStartListener.onStart();
        }
        long starttime = System.currentTimeMillis();
        EvLog.d("----database update begin time----", starttime + "");
        boolean result = true;

        try {
            updateSinger();
            updateSong();
            long endtime = System.currentTimeMillis();
            EvLog.d("----database update finished, time spent:",
                    (endtime - starttime) / MILLISECONDS_IN_SECOND + "  (s)");
        } catch (Exception e) {
            EvLog.e("dao update failed:" + e.getLocalizedMessage());
            e.printStackTrace();
            UmengAgentUtil.reportError(e);
            result = false;
        }
        
        if (mOnFinishListener != null) {
            mOnFinishListener.onFinish(result);
        }
        
        RemoteDAOHelper.getInstance().close();
        
        return true;
    }

    private void updateSinger() {
        List<Singer> list = null;
        int index = 0;
        int size = PAGE_SIZE;
        
        DAOFactory.getInstance().getSingerDAO().beginUpateRemoteData();
        
        do {
            list = mSingerDAO.getListAfterDatetime(mDatetime, new PageInfo(index, size));
            DAOFactory.getInstance().getSingerDAO().updateRemoteData(list);
            index++;
            mProgress += list.size();
            
            if (mOnProgressListener != null) {
                mOnProgressListener.onProgress(((float) mProgress) / ((float) mCount));
            }
            
            SystemClock.sleep(mSleepTime);
        } while (list.size() == size);
        
        DAOFactory.getInstance().getSingerDAO().endUpdateRemoteData();
    }
    
    private void updateSong() {
        List<Song> list = null;
        int index = 0;
        int size = PAGE_SIZE;
        
        DAOFactory.getInstance().getSongDAO().beginUpateRemoteData();
        
        do {
            EvLog.i("updateSong: " + mDatetime + ",begin to getListAfterDatetime");
            list = mSongDAO.getListAfterDatetime(mDatetime, new PageInfo(index, size));
            EvLog.i("updateSong: " + mDatetime + ",list.size=" + list.size());
            
            DAOFactory.getInstance().getSongDAO().updateRemoteData(list);
            index++;
            mProgress += list.size();
            
            if (mOnProgressListener != null) {
                mOnProgressListener.onProgress(((float) mProgress) / ((float) mCount));
            }
            
            SystemClock.sleep(mSleepTime);
        } while (list.size() == size);
        
        DAOFactory.getInstance().getSongDAO().endUpdateRemoteData();
    }
    
    /**
     * [开始监听器]
     */
    public interface OnStartListener {
        /**
         * [开始时的动作]
         */
        void onStart();
    }
    
    /**
     * [完成监听器]
     */
    public interface OnFinishListener {
        /**
         * [完成时的动作]
         * @param result true:成功;false：失败
         */
        void onFinish(boolean result);
    }
    
    /**
     * [过程监听器]
     */
    public interface OnProgressListener {
        /**
         * [过程中动作]
         * @param progress 完成进度
         */
        void onProgress(float progress);
    }
    
    /**
     * [设置开始监听器]
     * @param listener 监听器
     */
    public void setOnStartListener(OnStartListener listener) {
        mOnStartListener = listener;
    }
    
    /**
     * [设置过程监听器]
     * @param listener 监听器
     */
    public void setOnProgressListener(OnProgressListener listener) {
        mOnProgressListener = listener;
    }
    
    /**
     * [设置完成监听器]
     * @param listener 监听器
     */
    public void setOnFinishListener(OnFinishListener listener) {
        mOnFinishListener = listener;
    }
}
