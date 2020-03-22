/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年3月3日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.down;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.text.TextUtils;

import com.evideo.kmbox.model.down.KmDownThread.Task;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.thread.AsyncTaskManage;
import com.evideo.kmbox.thread.AsyncTaskManage.IAsyncTask;
import com.evideo.kmbox.util.CommonUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;
import com.evideo.kmbox.util.HttpFile;

/**
 * [功能说明] 下载文件异步线程
 */
public class DownFilePresenter extends AsyncPresenter<Boolean> {

    private static long mDownID = 0;
    
    public static class DownFileItem {
        public long id;
        public String url;
        public String filePath;
        public DownFileItem(String url,String filePath) {
            this.url = url;
            this.filePath = filePath;
            synchronized (DownFilePresenter.class) {
                this.id = mDownID++;
            }
        }
    }
    
    public interface IGetFileSavePathCallback {
        public String getDownSavePath(long needSpace);
    }
    
    public interface IDownListener {
        public void onSuccess(DownFileItem item);
        public void onProgress(DownFileItem item,int progress);
        public void onFail(DownFileItem item);
    }
    
    private DownFileItem mItem = null;
    private IDownListener mListener = null;
    private static final int DOWN_BUF_SIZE = 4*1024;
    private static final int RETRY_CONNECT_MAX_TIME = 3;
    private IGetFileSavePathCallback mDownFileCallback = null;
    private boolean mExit = false;
    
    public DownFilePresenter(DownFileItem item) {
        mItem = item;
        mExit = false;
    }
    
    public void setGetFilePathCallback(IGetFileSavePathCallback callback) {
        mDownFileCallback = callback;
    }
    
    public void setListener(IDownListener listener) {
        mListener = listener;
    }
    
    public DownFileItem getDowningItem() {
        return mItem;
    }
    
    private String getSavePath(HttpFile httpFile) {
        String fileName = httpFile.getFileName();
        if (TextUtils.isEmpty(fileName)) {
            String errorMessage = "[DC-ERROR] " + mItem.url + "can not get filename by http";
            EvLog.e(errorMessage);
            UmengAgentUtil.reportError(errorMessage);
            return "";
        } 
        
        String savePath = mDownFileCallback.getDownSavePath(httpFile.getContentLength());
        if (TextUtils.isEmpty(savePath)) {
            EvLog.e("[Local-Error]erc have no enough space to save");
            return "";
        } 
        return FileUtil.concatPath(savePath, fileName);
    }
    
    private int mMaxRetryTime = 3;
    
    public void setMaxRetryTime(int maxRetryTime) {
        if (maxRetryTime > 0) {
            mMaxRetryTime = maxRetryTime;
        }
    }
    
    private int mConnectTimeOut = 3000;
    public void setConnectTimeOut(int timeOut) {
        if (timeOut > 0) {
            mConnectTimeOut = timeOut;
        }
    }
    
    @Override
    protected Boolean doInBackground(Object... params) /*throws Exception*/ {
        int result = AsyncTaskManage.getInstance().registerHttpTask(new IAsyncTask() {
            @Override
            public void onCancel() {
                EvLog.i("recv DownFilePresenter onCancel message");
                mExit = true;
            }
        });
        
        if (result == AsyncTaskManage.RESULT_STOP) {
            return null;
        }
        
        if (mItem == null) {
            EvLog.e("mItem is null");
            return false;
        }
        
        boolean flag = false;
        EvLog.d(mConnectTimeOut + ", retryTime:" + mMaxRetryTime + "down:" + mItem.url);
        
        HttpFile file = new HttpFile();
      /*  file.setReadTimeout(mConnectTimeOut);
        file.setConnectTimeout(mConnectTimeOut);*/
        
        int i = 0;
        int openRet = 0;
        for (; i < mMaxRetryTime;i++) {
            if (mExit) {
                EvLog.e("DownFilePresenter cancle: " + mItem.id);
                return false;
            }
            openRet = file.open(mItem.url);
            if (openRet == 0) {
                break;
            }
            continue;
        }

        if (openRet != 0) {
            EvLog.e("DownFilePresenter,open error:" + file.getErrorMessage(openRet));
            return false;
        }
            
        if (TextUtils.isEmpty(mItem.filePath)) {
            mItem.filePath =  getSavePath(file);
            if (TextUtils.isEmpty(mItem.filePath)) {
                EvLog.e("can not get valid savePath");
                return false;
            }
        }
        File localFile = new File(mItem.filePath);
        if (!localFile.exists()) {
            try {
                localFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                file.close();
                localFile = null;
                return flag;
            }
        } 
        int readCount = 0;
        long readLen = 0;
        long readStartPos = localFile.length();
        long fileSize = file.getContentLength();
        byte[] buffer = new byte[DOWN_BUF_SIZE];
        
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(localFile, true);
            long timeInterval = 0;
            long prevSendTime = 0;
            while (readStartPos < fileSize) {
                if (mExit) {
                    EvLog.e("DownFilePresenter cancle: " + mItem.id);
                    break;
                }
                
                if ((readStartPos + buffer.length) < fileSize) {
                    readLen = buffer.length;
                } else {
                    readLen = fileSize - readStartPos;
                }
                readCount = localRead(file,buffer, 0, (int)readLen);
                if (readCount > 0) {
                    readStartPos += readCount;
                    os.write(buffer, 0, readCount);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        EvLog.e(e.getMessage());
                        break;
                    }
                    timeInterval = System.currentTimeMillis() - prevSendTime;
                    if  ( timeInterval >= 1000 ) {
                        prevSendTime = System.currentTimeMillis();
                        int progress = (int) ((readStartPos * 100) / fileSize);
                        if (progress < 0) {
                            EvLog.i(readStartPos + ", " + fileSize);
                        }
                        if (mListener != null) {
                            mListener.onProgress(mItem, progress);
                        }
                    }
                } else {
                    break;
                }
            }
            os.flush();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            CommonUtil.safeClose(os);
        }
        if (readStartPos == fileSize) {
            flag = true;
        }
        if (mListener != null) {
            mListener.onProgress(mItem, 100);
        }
        if (file != null) {
            file.close();
            file = null;
        }
        return flag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCompleted(Boolean result, Object... params) {
        if (result != null) {
            if (!result) {
                EvLog.d("onCompleted failed-----------");
                if (mListener != null) {
                    mListener.onFail(mItem);
                }
            } else {
                if (mListener != null) {
                    mListener.onSuccess(mItem);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFailed(Exception exception, Object... params) {
        exception.printStackTrace();
        if (mListener != null) {
            mListener.onFail(mItem);
        }
    }
    
    private int localRead(HttpFile file, byte[] buffer, int byteOffset,int byteCount) throws IOException {
        int totalReadNum = 0;
        int remainReadNum = byteCount;
        int startOffset = byteOffset;
        long singleRead = 0;

        int nRetryReadTime = 0;
        
        while (totalReadNum < byteCount) {

            singleRead = file.read(buffer, startOffset, remainReadNum);
            if (singleRead > 0) {
                totalReadNum += singleRead;

                if (totalReadNum >= byteCount) {
                    totalReadNum = byteCount;
                    break;
                } else {
                    startOffset = totalReadNum;
                    remainReadNum = byteCount - startOffset;
                }
            } else {
                nRetryReadTime ++;
                EvLog.d("KmDownThread localRead failed times = "+ nRetryReadTime + "startOffset = "
                        + startOffset + ", readcount " + remainReadNum);
                
                if ( nRetryReadTime > 3 ) {
                    break;
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        EvLog.e(e.getMessage());
                        UmengAgentUtil.reportError(e);
                    }
                    continue;
                }
            }
        }
        return totalReadNum;
    }
}
