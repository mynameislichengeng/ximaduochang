/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年8月22日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.playerctrl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Handler;
import android.os.Message;

import com.evideo.kmbox.ErrorInfo;
import com.evideo.kmbox.model.dao.data.StorageVolume;
import com.evideo.kmbox.model.down.DownError;
import com.evideo.kmbox.model.down.DownFilePresenter;
import com.evideo.kmbox.model.down.KmDownThread;
import com.evideo.kmbox.model.down.KmDownThread.Task;
import com.evideo.kmbox.model.down.KmSongDownManager;
import com.evideo.kmbox.model.down.DownFilePresenter.DownFileItem;
import com.evideo.kmbox.model.down.DownFilePresenter.IDownListener;
import com.evideo.kmbox.model.down.GetMediaListPresenter.OnlineFileItem;
import com.evideo.kmbox.model.down.KmSongDownManager.IDownMsgListener;
import com.evideo.kmbox.util.DiskUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;

/**
 * [功能说明]
 */
public class PlayListItemDownManager implements IDownMsgListener{
    private static long mDownID = 0;
    private List<PlayListItemDown> mDownList;
    private static PlayListItemDownManager instance = null;
    private Handler mMainHandler = null;
    
    public static PlayListItemDownManager getInstance() {
        if(instance == null) {
            synchronized (PlayListItemDownManager.class) {
                PlayListItemDownManager temp = instance;
                if(temp == null) {
                  temp = new PlayListItemDownManager();
                  instance = temp;
                }
            }
         }
         return instance;
    }
    
    private PlayListItemDownManager() {
        mDownList = new ArrayList<PlayListItemDownManager.PlayListItemDown>();
    }
    
    public void init() {
        KmSongDownManager.getInstance().init();
        KmSongDownManager.getInstance().registerListener(this);
        mMainHandler = new Handler()          {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    Iterator<PlayListItemDown> it = mDownList.iterator();  
                    while(it.hasNext()) {
                        PlayListItemDown item = (PlayListItemDown)it.next();
                        if (!item.valid) {
                            if (mDownFilePresenter!= null && mDownFilePresenter.isStarted() ) {
                                mDownFilePresenter.cancel();
                                mDownFilePresenter = null;
                            }
                            Task mediaTask = item.getMediaTask();
                            if (mediaTask != null) {
                                KmSongDownManager.getInstance().delTask(mediaTask.id);
                            }
                            it.remove();
                            break;
                        }
                    }   
                    EvLog.d("mDownList.size():" + mDownList.size());
                    if (mDownList.size() == 0) {
                        KmSongDownManager.getInstance().setThreadPriority(Thread.NORM_PRIORITY);
                    } else {
                        KmSongDownManager.getInstance().setThreadPriority(Thread.MIN_PRIORITY);
                    }
                }
            }
        };
    }
    
    public void uninit() {
        KmSongDownManager.getInstance().uninit();
        KmSongDownManager.getInstance().unregisterListener(this);
    }
    
    public static class PlayListItemDown {
        private boolean valid;
        private long downId;
        private OnlineFileItem mErcDown;
        private Task mediaTask;
        
        public PlayListItemDown(OnlineFileItem ercItem,Task mediaTask/*List<Task> mediaDownList*/) {
            this.valid = true;
            this.downId = ++mDownID;
            this.mErcDown = ercItem;
            this.mediaTask = mediaTask;
        }
        
        public long getDownId() {
            return this.downId;
        }
        
        public OnlineFileItem getErcDownItem() {
            return mErcDown;
        }
        
        public Task getMediaTask() {
            return mediaTask;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        public boolean isDownFinish() {
            if (mediaTask.fileDownedSize != mediaTask.fileTotalLen) {
                return false;
            }
            return true;
        }
    };
    
    private List<IPlayItemDownListener> mListeners = new ArrayList<IPlayItemDownListener>();
    public void registerListener(/*int type,*/IPlayItemDownListener listener) {
        mListeners.add(listener);
    }
    
    public void unregisterListener(IPlayItemDownListener listener) {
        mListeners.remove(listener);
    }
    
    public long getDowningId() {
        long taskId = KmSongDownManager.getInstance().getDowningID();
        for (PlayListItemDown item : mDownList) {
            if (item != null) {
                Task task = item.getMediaTask();
                if (task != null) {
                    if (task.id == taskId) {
                        return item.getDownId();
                    }
                }
            }
        }
        return 0;
    }
    private DownFilePresenter mDownFilePresenter = null;
    private int downErc(OnlineFileItem ercItem,final long downId) {
        EvLog.i("begin down erc:" + ercItem.url);
        if (mDownFilePresenter != null) {
            mDownFilePresenter.cancel();
            mDownFilePresenter = null;
        }

        DownFileItem item = new DownFileItem(ercItem.url, "");
        mDownFilePresenter = new DownFilePresenter(item);
        mDownFilePresenter
                .setGetFilePathCallback(new DownFilePresenter.IGetFileSavePathCallback() {

                    @Override
                    public String getDownSavePath(long needSpace) {
                        StorageVolume volume = DiskUtil.getSuitableVolume(
                                needSpace,/* KmPlaySongDown.this */null);
                        // 空间不足
                        if (volume == null) {
                            return null;
                        } else {
                            return volume.getSubtitlePath();
                        }
                    }
                });
        mDownFilePresenter.setListener(new IDownListener() {
            @Override
            public void onSuccess(DownFileItem item) {
                EvLog.i("end down erc:" + item.filePath);
                //FIXME check is still in list and valid is true
                
                for (IPlayItemDownListener listener : mListeners) {
                    listener.onDownErcSuccess(downId, item.filePath);
                }
            }

            @Override
            public void onFail(DownFileItem item) {
                EvLog.e("down failed erc:" + item.url);
                FileUtil.deleteFile(item.filePath);

                //FIXME check is still in list and valid is true
                
                for (IPlayItemDownListener listener : mListeners) {
                    listener.onDownErcFailed(downId);
                }
            }

            @Override
            public void onProgress(DownFileItem item, int progress) {
            }
        });
        mDownFilePresenter.start(item, 3 * 1000, 3);
      return 0;
    }
    
    public long addItem(PlayListItemDown item) {
        if (item == null) {
            return -1;
        }
        synchronized (mDownList) {
            mDownList.add(item);
        }
        if (item.getErcDownItem() != null) {
            downErc(item.getErcDownItem(), item.getDownId());
        }
        Task mediaTask = item.getMediaTask();
        if (mediaTask != null) {
            KmSongDownManager.getInstance().addTask(mediaTask);
        }
        return item.getDownId();
    }
    
    public void insertItem(PlayListItemDown item) {
        if (item == null) {
            return;
        }
        
        List<PlayListItemDown> tmpList = new ArrayList<PlayListItemDown>();
        tmpList.addAll(mDownList);
        
        synchronized (mDownList) {
            mDownList.clear();
            mDownList.add(item);
            mDownList.addAll(tmpList);
        }
        KmSongDownManager.getInstance().cancelCurrentDown();
        Task mediaTask = item.getMediaTask();
        if (mediaTask != null) {
            KmSongDownManager.getInstance().addTask(mediaTask);
        }
    }
    
    public void delItem(long downId) {
        synchronized (mDownList) {
            for (PlayListItemDown item : mDownList) {
                if (item != null && item.getDownId() == downId) {
                    item.setValid(false);
                }
            }
        }
        return;
    }

    private int convertDownError(int errorCode,Task task) {
        int convertCode = DownError.ERROR_CODE_CONNECT_FAILED;
        
        switch(errorCode) {
            case KmDownThread.ERROR_FILE_CREATE_FAILED:
            case KmDownThread.ERROR_IO: {
                EvLog.w(task.localPath + " down  failed,error type" + errorCode);
                convertCode = DownError.ERROR_CODE_WRITE_FAILED;
                break;
            }
            case KmDownThread.ERROR_READ_TIMEOUT:  {
                EvLog.w(task.localPath + " down  failed,error type" + errorCode);
                convertCode = DownError.ERROR_CODE_READ_TIMEOUT;
                break;
            }
            case KmDownThread.ERROR_URL_CONTENTLEN_INVLAID: {
                EvLog.w(task.localPath + " down  failed,error type" + errorCode);
                convertCode = DownError.ERROR_CODE_URL_CONTENTLEN_INVLAID;
                break;
            }
            case KmDownThread.ERROR_CONNECT_FAILED: {
                EvLog.w(task.localPath + " down  failed,error type" + errorCode);
                convertCode = DownError.ERROR_CODE_CONNECT_FAILED;
                break;
            }
          /*  case KmDownThread.ERROR_RESOURCE_NOT_EXIST: {
                EvLog.w( itemWithType.downitem.localPath + " down " + itemWithType.type + " failed,error type=ERROR_RESOURCE_NOT_EXIST");
                convertCode = DownError.ERROR_CODE_RESOURCE_NOT_EXIST;
                break;
            }*/
        }
        
        return convertCode;
    }
    
    private void notifyError(long id,ErrorInfo errorInfo) {
        for (IPlayItemDownListener listener : mListeners) {
            listener.onDownMediaError(id, errorInfo);
        }
    }
    
    @Override
    public void onMessageReciver(Task downItem, Message msg) {
        if (downItem == null || msg == null) {
            return;
        }
        
        PlayListItemDown playListitem = null;
        Task rightMediaItem = null;
        for (PlayListItemDown item : mDownList) {
            if (item != null && item.valid) {
                Task mediaTask = item.getMediaTask();
                if (mediaTask.id == downItem.id) {
                    rightMediaItem = mediaTask;
                    playListitem = item;
                }
            }
        }
        if (rightMediaItem == null) {
            mMainHandler.sendEmptyMessage(0);
//            KmSongDownManager.getInstance().delTask(downItem.id);
            return;
        }
        
        rightMediaItem.localPath = downItem.localPath;
        rightMediaItem.fileDownedSize = downItem.fileDownedSize;
        rightMediaItem.fileTotalLen = downItem.fileTotalLen;
        
        switch(msg.what) {
            case KmDownThread.DOWNLOAD_MSG_ERROR: {
                int convertCode = convertDownError(msg.arg1,rightMediaItem);
                ErrorInfo errorInfo = new ErrorInfo();
                errorInfo.errorType =  DownError.ERROR_TYPE_DOWN_MEDIA_FAILED;
                errorInfo.errorCode = convertCode;
                if (msg.arg1 == KmDownThread.ERROR_CONNECT_FAILED) {
                    errorInfo.errorCodeSupplement = msg.arg2;
                }
                errorInfo.errorMessage = msg.getData().getString("errmsg");
                notifyError(playListitem.getDownId(), errorInfo);
                playListitem.valid = false;
                mMainHandler.sendEmptyMessage(0);
                break;
            }
           
            case KmDownThread.DOWNLOAD_MSG_PROGRESS: {
                if (playListitem.isDownFinish()) {
                    for (IPlayItemDownListener listener : mListeners) {
                        listener.onDownMediaFinish(playListitem.getDownId(),rightMediaItem.localPath);
                    }
                    EvLog.d("down finish,remove from downlist");
                    playListitem.valid = false;
                    mMainHandler.sendEmptyMessage(0);
                } else {
                    for (IPlayItemDownListener listener : mListeners) {
                        listener.onDownMediaProgress(playListitem.getDownId(),rightMediaItem.localPath,rightMediaItem.fileDownedSize,
                                rightMediaItem.fileTotalLen,msg.getData().getFloat("speed"));
                    }
                }
                break;
            }
            default:
                break;
        }
    }
}
