package com.evideo.kmbox.model.dao.data;

import android.text.TextUtils;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.KmApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.dao.ConfigDAO;
import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.DAOUpdater;
import com.evideo.kmbox.exception.DataCenterCommuException;
import com.evideo.kmbox.exception.NoSerialNumberException;
import com.evideo.kmbox.exceptionhandler.DataCenterCommuExceptionHandler;
import com.evideo.kmbox.exceptionhandler.NoSerialNumberExceptionHandler;
import com.evideo.kmbox.exceptionhandler.UnKnownExceptionHandler;
import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.util.CommonUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;
import com.evideo.kmbox.util.HttpFile;
import com.evideo.kmbox.util.NetUtils;
import com.evideo.kmbox.util.RepeatTimerTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * [曲库数据库同步类]
 */
public final class DatabaseSynchronizer {
	
	private static DatabaseSynchronizer sInstance;
	private boolean mLock = false;
	private Object mLockObject = new Object();
	private boolean mUpdating = false;

	/**
     * [功能说明]获取DatabaseSynchronizer实例
     * @return DatabaseSynchronizer实例
     */
    public static DatabaseSynchronizer getInstance() {
        if (sInstance == null) {
            synchronized (DatabaseSynchronizer.class) {
                if (sInstance == null) {
                    sInstance = new DatabaseSynchronizer();
                }
            }
        }

        return sInstance;
    }
    
    private static final String TAG = DatabaseSynchronizer.class.getSimpleName();
    private String mDbUri = null;
    private String mRemoteDBVersion = null;
//    private Context mContext = null;
    private static final int BUFFER_SIZE = 1024;
    private static final int REPEAT_TASK_INTERVAL = 1000 * 60 * 10;
    private RepeatTimerTask mRepeatTimerTask;
    private UpdateAsyncPresenter mUpdateAsyncPresenter;
    private boolean mForceStop = false;
    private IDatabaseSyncListener mListener = null;

    /**
     * [曲库同步监听接口]
     */
    public interface IDatabaseSyncListener {
        /**
         * [曲库同步开始]
         */
        public void onStart();
        /**
         * [曲库同步结束]
         * @param result true:成功;false:失败
         */
        public void onFinish(boolean result);
        /**
         * [曲库同步进度]
         * @param progress 进度百分比
         */
        public void onProgress(float progress);
        /**
         * [曲库同步出错]
         * @param message 出错信息
         */
        public void onError(String message);
    }
    
    private DatabaseSynchronizer() {
    }
    
    /**
     * [设置监听器]
     * @param listener 监听器
     */
    public void setListener(IDatabaseSyncListener listener) {
        mListener = listener;
    }

    /**
     * [曲库更新异步任务类,实现了对网络状态的监听]
     */
    class UpdateAsyncPresenter extends AsyncPresenter<Boolean> {


        private synchronized boolean update() throws Exception {
            boolean result = false;
            if (needUpdateLocalDatabase()) {
                result = updateLocalDatabase();
            } else {
                result = true;
            }

            return result;
        }

        @Override
        protected Boolean doInBackground(Object... params) throws Exception {
            boolean result = false;
            
            synchronized (mLockObject) {
                if (mLock) {
                    return result;
                }
                
                mUpdating = true;
            }

            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            
            if (NetUtils.isNetworkConnected(KmApplication.getInstance().getContext())) {
                result = update();
            }
            
            synchronized (mLockObject) {
                mUpdating = false;
            }

            return result;
        }

        @Override
        protected void onCompleted(Boolean result, Object... params) {
            boolean downResult = false;
            if (result != null) {
                downResult = result;
            }

            EvLog.i(TAG, "update db result:" + (downResult ? "success" : "failed"));
        }

        @Override
        protected void onFailed(Exception exception, Object... params) {
            synchronized (mLockObject) {
                mUpdating = false;
            }
            if (exception != null) {
                String msg = handleException(exception);
                EvLog.e(TAG, "Database update error: " + msg);
                UmengAgentUtil.reportError(exception);
            }
        }
    }

    private String handleException(Exception ex) {

        String message = "";

        if (ex instanceof NoSerialNumberException) {

            message = BaseApplication.getInstance().getResources()
                    .getString(R.string.no_serial_number)
                    + ex.getLocalizedMessage();
            NoSerialNumberExceptionHandler.getInstance().handle(ex);
        } else if (ex instanceof DataCenterCommuException) {

            DataCenterCommuExceptionHandler.getInstance().handle(ex);
        } else if (ex instanceof UnknownHostException) {

            message = BaseApplication.getInstance().getResources()
                    .getString(R.string.datacenter_communication_unknow_host)
                    + ex.getLocalizedMessage();
        } else if (ex instanceof ConnectException) {

            message = BaseApplication.getInstance().getResources()
                    .getString(R.string.cannot_connect_to_datacenter)
                    + ex.getLocalizedMessage();
        } else {
            message = BaseApplication.getInstance().getResources()
                    .getString(R.string.unknown_error)
                    + ex.getLocalizedMessage();
            UnKnownExceptionHandler.getInstance().handle(ex);
        }

        return message;
    }

    /**
     * [异步更新曲库]
     */
    public void asyncUpdate() {
        
        EvLog.d(TAG, "DatabaseSynchronizer async update");

        mUpdateAsyncPresenter = new UpdateAsyncPresenter();
        
        mRepeatTimerTask = new RepeatTimerTask(new RepeatTimerTask.IActionCallback() {
            @Override
            public void stop() {
            }
            
            @Override
            public void start() {
            }
            
            @Override
            public void repeat() {
                if (mUpdateAsyncPresenter != null && !mUpdateAsyncPresenter.isStarted()) {
                    mUpdateAsyncPresenter.start();
                }
            }
        });
        
        mUpdateAsyncPresenter.start();
    }
    
    /**
     * [功能说明]开启定时更新任务，在MainActivity onStart时调用
     */
    public void start() {
        
        if (mForceStop) {
            return;
        }
        
        EvLog.d(TAG, "DatabaseSynchronizer startUpdateRepeatTask");
        if (mRepeatTimerTask != null) {
            mRepeatTimerTask.scheduleAtFixedRate(REPEAT_TASK_INTERVAL);
        }
    }
    
    /**
     * [功能说明]停止定时更新任务，在MainActivity onStop时调用
     */
    public void stop() {
        EvLog.d(TAG, "DatabaseSynchronizer stopUpdateRepeatTask");
        if (mRepeatTimerTask != null) {
            mRepeatTimerTask.stop();
        }
    }
    
    /**
     * [功能说明]强制停止定时更新任务
     * <p>执行此方法后，无法再通过startUpdateRepeatTask()启动定时更新任务</p>
     */
    public void forceStopUpdateRepeatTask() {
        mForceStop = true;
        stop();
    }

    private Boolean needUpdateLocalDatabase() throws Exception {
        ConfigDAO dao = DAOFactory.getInstance().getConfigDAO();
        DCDomain.getInstance().clearDatabaseInfo();
        
        mDbUri = DCDomain.getInstance().requestDatabaseURI();
        mRemoteDBVersion = DCDomain.getInstance().requestDatabaseVersion();
        
        if (dao.isForceUpdate()) {
            return true;
        }
        
        if (TextUtils.isEmpty(dao.getVersion())) {
            return true;
        }
        
        String version = dao.getVersion();
//        EvLog.i(TAG, "version:" + version + ",remote version:" + mRemoteDBVersion);
        
        return dao.getVersion().compareTo(mRemoteDBVersion) != 0;
    }
    
    private DAOUpdater.OnStartListener mOnStartListener = new DAOUpdater.OnStartListener() {
        
        @Override
        public void onStart() {
            if (mListener != null) {
                mListener.onStart();
            }
        }
    };
    
    private DAOUpdater.OnFinishListener mOnFinishListener = new DAOUpdater.OnFinishListener() {
        
        @Override
        public void onFinish(boolean result) {
            if (mListener != null) {
                mListener.onFinish(result);
            }
        }
    };
    
    private DAOUpdater.OnProgressListener mOnProgressListener = new DAOUpdater.OnProgressListener() {
        
        @Override
        public void onProgress(float progress) {
            if (mListener != null) {
                mListener.onProgress(progress);
            }
        }
    };

    private boolean updateLocalDatabase() {
        String localPath = ResourceSaverPathManager.getInstance().getDBSavePath()/*KmConfig.DB_SAVE_PATH*/;
        File dbDir = new File(localPath);

        if (!dbDir.exists()) {
            if (!dbDir.mkdirs()) {
                EvLog.e(TAG,localPath + "mkdir failed:" + dbDir.getAbsolutePath());
                return false;
            }
        }
        dbDir = null;

        String file = localPath + "/kmbox.db";

        EvLog.d("update db from " + mDbUri);
        if (!downloadDb(mDbUri, file)) {
            FileUtil.deleteFile(file);
            return false;
        }
    
/*        if (!HttpUtil.httpDownLoad(dbUri, file)) {
            FileUtil.deleteFile(file);
            return false;
        }*/
        
        DAOUpdater updater = new DAOUpdater();
        updater.setOnStartListener(mOnStartListener);
        updater.setOnFinishListener(mOnFinishListener);
        updater.setOnProgressListener(mOnProgressListener);

        EvLog.i(TAG,"begin to update db to version:" + mRemoteDBVersion);
        if (updater.update(file)) {
            DAOFactory.getInstance().getConfigDAO().updateVersion(mRemoteDBVersion);
            DAOFactory.getInstance().getConfigDAO().setForceUpdate(false);
        }
        EvLog.i(TAG,"end of update db to version:" + mRemoteDBVersion);
        
        FileUtil.deleteFile(file);

        return true;
    }

    /**
     * [下载曲库数据]
     * @param url 数据中心URL
     * @param filePath 本地路径
     * @return true:成功;false：失败
     */
    private boolean downloadDb(String url, String filePath) {
        HttpFile httpFile = new HttpFile();
        
        if (httpFile.open(mDbUri) != 0) {
            return false;
        }
        
        InputStream is = httpFile.getInputStream();
        FileOutputStream fileOutputStream = null;
        
        if (is != null) {
            try {
                fileOutputStream = new FileOutputStream(filePath, false);
                byte[] buf = new byte[BUFFER_SIZE];
                int ch = -1;
                while ((ch = is.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, ch);
                }
                
                fileOutputStream.flush();
                
                return true;
            } catch (FileNotFoundException e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
                return false;
            } catch (IOException e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
                return false;
            } finally {
                CommonUtil.safeClose(fileOutputStream);
                CommonUtil.safeClose(is);
                if (httpFile != null) {
                    httpFile.close();
                    httpFile = null;
                }
            }
        }
        
        return false;
    }
    
    public boolean lock() {
        synchronized (mLockObject) {
            if (mUpdating) {
                return false;
            }
            
            if (mLock) {
                return false;
            }
            
            mLock = true;
        }

        return true;
    }

    public void unlock() {
        synchronized (mLockObject) {
            mLock = false;
        }
    }
}
