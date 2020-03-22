/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年11月17日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.device;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.util.CommonUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

/**
 * [功能说明] 资源保存路径管理
 */
public class ResourceSaverPathManager {
    private static ResourceSaverPathManager instance = null;
    
    public static ResourceSaverPathManager getInstance() {
        if(instance == null) {
            synchronized (ResourceSaverPathManager.class) {
                ResourceSaverPathManager temp = instance;
                if(temp == null) {
                  temp = new ResourceSaverPathManager();
                  instance = temp;
                }
            }
         }
         return instance;
    }
    
    private boolean createChildDir(String name) {
        String path = FileUtil.concatPath(mKmBoxDir, name);
        
        if (!isFileExist(path)) {
            mkdir(path);
            if (!isFileExist(path)) {
                EvLog.e(path + " create failed ");
                return false;
            }
        }
        return true;
    }
    
    //返回以KB为单位
    private long getPartitionFreeSpace(File partition) {
        StatFs sf = new StatFs(partition.getPath()); 
        long availSize = (sf.getBlockSize()/1024)*sf.getAvailableBlocks(); 
        return availSize;
    }
    
    private boolean mNeedSyncDB = false;
    
    public void init(Context context) {
        if (context == null) {
            return;
        }
        
        String root = "";
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            /*long availSize = getPartitionFreeSpace( Environment.getExternalStorageDirectory()); 
            EvLog.e("avail size:" + availSize + " KB");
            root = Environment.getExternalStorageDirectory().getAbsolutePath();*/
            String kmDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/kmbox";
            String oldDBPath = FileUtil.concatPath(kmDir, DIR_DB);
            if (isFileExist(oldDBPath)) {
                EvLog.e("need sync db--------------");
                mNeedSyncDB = true;
            }
        } else {
            EvLog.i(Environment.getExternalStorageDirectory().toString() + " is not in mount state" );
        }
        
        if (TextUtils.isEmpty(root)) {
            root = context.getFilesDir().getAbsolutePath();
        }
//        EvLog.d("root=" + root);
        mRootDir = root;
        mKmBoxDir = mRootDir + "/kmbox";
        if (!isFileExist(mKmBoxDir)) {
            mkdir(mKmBoxDir);
        } 
        
        EvLog.i("dir = " + mKmBoxDir);
        
        if (isFileExist(mKmBoxDir)) {
            createChildDir(DIR_HUODONG);
            createChildDir(DIR_DB);
            createChildDir(DIR_APK);
            createChildDir(DIR_RESOURCE);
            createChildDir(DIR_LOG);
            createChildDir(DIR_RECORDS);
            createChildDir(DIR_HOMERIGHTBOTTOMICON);
            createChildDir(DIR_TFTP);
            createChildDir(DIR_MEDIA);
            createChildDir(DIR_SUBTITLE);
            createChildDir(DIR_QR);
            createChildDir(DIR_TOP_SLOGAN);
            createChildDir(DIR_CLOUD_LIST);
        } else {
            EvLog.e(">>>>>>>>>>>>>>>> mKmBoxDir create failed");
        }
    }
    
    public boolean needSyncDB() {
        return mNeedSyncDB;
    }
    
    public void pauseSyncDB() {
        mPauseSyncDB = true;
    }
    
    public void resumeSyncDB() {
        mPauseSyncDB = false;
    }
    
    public interface ISyncDBListener {
        public void onSyncProgress(int progress);
        public void onSyncFinish();
    }
    
    private boolean mPauseSyncDB = false;
    private ISyncDBListener mListener = null;
    private SyncDBPresenter mSyncDBPresenter = null;
    public void startSyncDB(ISyncDBListener listener) {
        mListener = listener;
        if (mSyncDBPresenter != null) {
            mSyncDBPresenter.cancel();
            mSyncDBPresenter = null;
        }
        mSyncDBPresenter = new SyncDBPresenter();
        mSyncDBPresenter.start();
    }
    
    public class SyncDBPresenter extends AsyncPresenter<Boolean> {
        
        @Override
        protected Boolean doInBackground(Object... params) throws Exception {
            String oldPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/kmbox/" + DIR_DB;
            String newPath = FileUtil.concatPath(mKmBoxDir, DIR_DB);
            EvLog.e("begin to copy " + oldPath + " to " + newPath );
            
            FileInputStream fis = null;
            FileInputStream input = null;
            FileOutputStream output = null;
            try {  
                (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
//                grantWriteReadAccess(newPath);
                File oldFile = new File(oldPath);  
                String[] file = oldFile.list();  
                File temp = null;  
                byte[] buffer = new byte[1024 * 16]; 
                long timeInterval = 0;
                long prevSendTime = 0;
                long totalDirSize = 0;
                
                for (int i = 0; i < file.length; i++) {  
                    if (oldPath.endsWith(File.separator)) {  
                        temp = new File(oldPath + file[i]);  
                    } else {  
                        temp = new File(oldPath + File.separator + file[i]);  
                    }  
                    if (temp.isFile() && file[i].contains("local_kmbox")) {
                        fis = new FileInputStream(temp);
                        totalDirSize += fis.available();
                        CommonUtil.safeClose(fis);
                    }
                    temp = null;
                }
                
                EvLog.e("totalDirSize=" + totalDirSize);
                long alreadyReadCount = 0;
                int len;  
                for (int i = 0; i < file.length; i++) {  
                    if (oldPath.endsWith(File.separator)) {  
                        temp = new File(oldPath + file[i]);  
                    } else {  
                        temp = new File(oldPath + File.separator + file[i]);  
                    }  
     
                    if (temp.isFile()&& file[i].contains("local_kmbox")) {
                        EvLog.i(mPauseSyncDB + " >>>>>>>> copy file :" + file[i]);
                        input = new FileInputStream(temp);  
                        output = new FileOutputStream(newPath  
                                + "/" + (temp.getName()).toString());  
                        while (/*(len = input.read(buffer)) != -1*/true) {  
                            if (mPauseSyncDB) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    EvLog.e(e.getMessage());
                                    break;
                                }
                                continue;
                            }
                            
                            len = input.read(buffer);
                            if (len == -1) {
                                break;
                            }
                            
                            output.write(buffer, 0, len); 
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                EvLog.e(e.getMessage());
                                break;
                            }
                            alreadyReadCount += len;
                            timeInterval = System.currentTimeMillis() - prevSendTime;
                            if  ( timeInterval >= 1000 ) {
                                prevSendTime = System.currentTimeMillis();
                                int progress = (int) ((alreadyReadCount * 100) / totalDirSize);
                                if (progress < 0) {
                                    EvLog.i(alreadyReadCount + ", " + totalDirSize);
                                }
                                
                                if (mListener != null) {
                                    mListener.onSyncProgress(progress);
                                }
                            }
                        }  
                        output.flush();  
                        CommonUtil.safeClose(output);
                        CommonUtil.safeClose(input);
                    }  
                    temp = null;
                }  
            } catch (Exception e) {  
                EvLog.e("sync db failed:" + e.getMessage());
                UmengAgentUtil.reportError("sync db failed:" + e.getMessage());
            } finally {
                CommonUtil.safeClose(fis);
                CommonUtil.safeClose(output);
                CommonUtil.safeClose(input);
            }  
            EvLog.e("end of copy " + oldPath + " to " + newPath );
            String kmboxDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/kmbox/";
            deleteAllFiles(kmboxDir);
            deleteDir(kmboxDir);
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onCompleted(Boolean result, Object... params) {
            if (mListener != null) {
                mListener.onSyncFinish();
            }
            mSyncDBPresenter = null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onFailed(Exception exception, Object... params) {
            if (mListener != null) {
                mListener.onSyncFinish();
            }
            mSyncDBPresenter = null;
        }
    }
    
    public void uninit() {
        if (mSyncDBPresenter != null) {
            mSyncDBPresenter.cancel();
            mSyncDBPresenter = null;
        }
        if (mClearPresenter != null) {
            mClearPresenter.cancel();
            mClearPresenter = null;
        }
    }
    private ClearPresenter mClearPresenter = null;
    private String mRootDir = "";
    private String mKmBoxDir = "";
    
    private String DIR_RECORDS = "records";
    private String DIR_HUODONG = "huodong";
    private String DIR_DB = "db";
    private String DIR_APK = "apks";
    private String DIR_RESOURCE = "resource";
    private String DIR_LOG = "log";
    private String DIR_HOMERIGHTBOTTOMICON = "homeRightBottomIcon";
    private String DIR_TOP_SLOGAN = "topslogan";
    private String DIR_TFTP = "tftproot";
    private String DIR_QR = "qr";
    private String DIR_CLOUD_LIST = "cloudlist";
    
    public static final String RECORD_MIX_PATH_NAME = "/mix/";
    public static final String DIR_SUBTITLE = "subtitle";
    public static final String DIR_MEDIA = "resource";
    
    /** [视频文件后缀] */
    public static final String VIDEO_FILE_SUFFIX = ".ts";
    public static final String FILE_TYPE_SUFFIX_EUR = ".eur";
    public static final String FILE_TMP_SUFFIX = ".tmp";
    public static final String FILE_TYPE_SUFFIX_MP3 = ".mp3";

    
    public String getRootPath() {
        return mRootDir;
    }
    
    public String getTopSlogan() {
        return FileUtil.concatPath(mKmBoxDir, DIR_TOP_SLOGAN);
    }
    
    public String getQrPath() {
        return FileUtil.concatPath(mKmBoxDir, DIR_QR);
    }
    
    public String getMediaPath() {
        return FileUtil.concatPath(mKmBoxDir, DIR_MEDIA);
    }
    
    public String getSubtitlePath() {
        return FileUtil.concatPath(mKmBoxDir, DIR_SUBTITLE);
    }
    
    public String getHomeRightIconSavePath() {
        return FileUtil.concatPath(mKmBoxDir, DIR_HOMERIGHTBOTTOMICON);
    }
    public String getRecordSavePath() {
        return FileUtil.concatPath(mKmBoxDir, DIR_RECORDS);
    }
    
    public String getCloudListSavePath() {
        return FileUtil.concatPath(mKmBoxDir, DIR_CLOUD_LIST);
    }
    
    public String getKmBoxPath() {
        return mKmBoxDir;
    }
    public String getLogSavePath() {
        return FileUtil.concatPath(mKmBoxDir, DIR_LOG);
    }
    public String getHuodongSavePath() {
        return FileUtil.concatPath(mKmBoxDir, DIR_HUODONG);
    }
    
    public String getDBSavePath() {
        return FileUtil.concatPath(mKmBoxDir, DIR_DB);
    }
    
    public String getApkSavePath() {
        return FileUtil.concatPath(mKmBoxDir, DIR_APK);
    }
    
    public String getResourceSavePath() {
        return FileUtil.concatPath(mKmBoxDir, DIR_RESOURCE);
    }
    
    public String getTftpSavePath() {
        return FileUtil.concatPath(mKmBoxDir, DIR_TFTP);
    }
    
    public class ClearPresenter extends AsyncPresenter<Boolean> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected Boolean doInBackground(Object... params) throws Exception {
            String dir = (String)params[0];
            if (TextUtils.isEmpty(dir)) {
                return false;
            }
            deleteAllFiles(dir);
            deleteDir(dir);
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onCompleted(Boolean result, Object... params) {
            // TODO Auto-generated method stub
            
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onFailed(Exception exception, Object... params) {
            // TODO Auto-generated method stub
            
        }
        
    }
    
   /* private String concatPath(String path, String append) {
        if(path == null) {
            return null;
        }
        String temp = null;
        if(path.endsWith(File.separator)) {
            temp = path + append;
        } else {
            temp = path + File.separator + append;
        }
        return temp;
    }*/
    
    private void mkdir(String dir) {
        File file =new File(dir);    
        if (!file.exists() && !file.isDirectory()) {       
            file.mkdir();
//            grantWriteReadAccess(file.getAbsolutePath());
        }  
    }
    
    private boolean isFileExist(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return true;
        }
        return false;
    }
    
    private void deleteAllFiles(String path) {
        if(path == null) {
            return;
        }
        EvLog.i("deleteAllFiles in " + path);
        File file = new File(path);
        if(!file.exists() || !file.isDirectory()) {    //文件不存在或不是目录
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            
            if(path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if(temp.isFile()) {
                EvLog.d("del file: " + temp.getName());
                temp.delete();
            } else if(temp.isDirectory()) {
                deleteAllFiles(temp.getAbsolutePath());
                EvLog.d("del dir:" + file.getName());
                temp.delete();
            }
            
        }
    }
    
    public static void deleteDir(String dir) {
        File file = new File(dir);

        if ( file.exists() ) {
            if (file.isDirectory()) { // 如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    files[i].delete(); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        }
    }

}
