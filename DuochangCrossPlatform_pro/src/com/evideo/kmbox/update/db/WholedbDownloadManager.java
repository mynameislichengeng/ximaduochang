
package com.evideo.kmbox.update.db;

import android.os.Environment;

public class WholedbDownloadManager {
    /** [本地全库、点播数据库路径] */
    public static final String LOCAL_WHOLE_DB_PATH = Environment
            .getExternalStorageDirectory() + "/kmbox/db";
    public static final String LOCAL_RES_BACK_WHOLE_DB_PATH = Environment
            .getExternalStorageDirectory() + "/kmbox/res_back/db/";
    private static final String WHOLE_DB_NAME_PATH = LOCAL_WHOLE_DB_PATH + "/whole_kmbox.db";
    //private static final String WHOLE_RES_BACK_DB_NAME_PATH = LOCAL_RES_BACK_WHOLE_DB_PATH + "/whole_kmbox.db";
    private static final String WHOLE_DB_NAME_CACHE_PATH = LOCAL_WHOLE_DB_PATH + "/whole_kmbox.db-journal";
    private static final int BUFFER_SIZE = 1024 * 8;
    private static final int OPTION_DELETE = 0;
    private static final int OPTION_ADD = 1;
    private static final int OPTION_UPDATE = 2;
    /** [网络错误] */
    public static final int ERROR_TYPE_NETWORK = -1;
    /** [下载中断] */
    public static final int ERROR_TYPE_DOWNABORT = -2;
    /** [协议出错] */
    public static final int ERROR_TYPE_DC_COMMU_ERROR = -3;
    private static WholedbDownloadManager sInstance = null;
    private DownloadListener mListener = null;
    private boolean mPermitDown = false;
    private boolean mIsDownFinished = false;
    
    public static final String WHOLE_DB_VERSION_DEFAULT = "1";

    
    /**
     * [获取单例]
     * @return 单例
     */
    public static WholedbDownloadManager getInstance() {
        if (sInstance == null) {
            sInstance = new WholedbDownloadManager();
        }
        return sInstance;
    }

    /**
     * [设置下载监听器]
     * @param listener 监听器
     */
    public void setDownloadProgressListener(DownloadListener listener) {
        mListener = listener;
    }
    
    /**
     * [允许下载]
     * @return 是否允许下载
     */
    public boolean getPermitDown() {
        return mPermitDown;
    }

    /**
     * [设置允许下载]
     * @param mPermitDown 是否允许下载
     */
    public void setPermitDown(boolean mPermitDown) {
        this.mPermitDown = mPermitDown;
    }
    
    /**
     * [是否下载完成]
     * @return 是否下载完成
     */
    public boolean isDownFinished() {
        return mIsDownFinished;
    }

    /**
     * [设置下载完成]
     * @param mIsDownFinished 是否下载完成
     */
    public void setDownFinished(boolean mIsDownFinished) {
        this.mIsDownFinished = mIsDownFinished;
    }
    

    /**
     * [下载监听器]
     */
    public interface DownloadListener {
        
        /**
         * [全曲库开始下载]
         */
        public void onWholeDownStart();
        /**
         * [全曲库下载进度]
         * @param progress 进度
         * @param length 文件长度
         */
        public void onWholeDownProgress(int progress, long length, int type);
        /**
         * [全曲库下载错误]
         * @param errorType 错误类型
         */
        public void onWholeDownError(int errorType);
        /**
         * [全曲库下载完成]
         */
        public void onWholeDownFinish();
        /**
         * [增量更新完成]
         */
        public void onIncrementFinish();
    }
    
    /**
     * [移除监听]
     */
    public void removeListener() {
        mListener = null;
    }
}
