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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.text.TextUtils;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.ErrorInfo;
import com.evideo.kmbox.model.dao.data.Media;
import com.evideo.kmbox.model.dao.data.MediaManager;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.device.IDeviceConfig;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.down.DownError;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;

/**
 * [功能说明] 从数据中心获取播放链信息
 */
public class GetMediaListPresenter extends AsyncPresenter<Boolean> {
    private ErrorInfo mErrorInfo = null;
    private int mPrepareSerialNum = -1;
    private List<OnlineFileItem> mItemList = null;
    private IGetMediaListListener mListener = null;
    private boolean mStopFlag = false;
    
    public void setStop() {
        mStopFlag = true;
    }
    /**
     * [功能说明] 在线文件信息
     */
    public class OnlineFileItem {
        public static final int TYPE_ERC = 0;
        public static final int TYPE_MEDIA = 1;
        public String url;
        public String fileName;
        public String savePath;
        public int    type;
        public long    totalSize;
        public OnlineFileItem(int type,long totalSize,String fileName,String savePath,String url) {
            this.type = type;
            this.url = url;
            this.fileName = fileName;
            this.totalSize = totalSize;
            this.savePath = savePath;
        }
    }
    public interface IGetMediaCallback {
        public boolean isNeedGetMediaUrl(int serialNum);
        public boolean isNeedGetErcUrl(int serialNum);
        public String  getMediaSavePath(long needSpace);
    }
    
    public interface IGetMediaListListener{
        public void onGetMediaListSuccess(int serialNum, List<OnlineFileItem> itemList);
        public void onGetMediaListFailed(int serialNum, ErrorInfo errorInfo);
    }
    
    public void setListener(IGetMediaListListener listener) {
        mListener = listener;
    }
    
    private IGetMediaCallback mGetMediaCallback = null;
    public GetMediaListPresenter(IGetMediaCallback callback) {
        mErrorInfo = new ErrorInfo();
        mPrepareSerialNum = -1;
        mGetMediaCallback = callback;
        mItemList = new ArrayList<OnlineFileItem>();
    }
    
    public int getSerialNum() {
        return mPrepareSerialNum;
    }
    
    public void setSerialNum(int val) {
        mPrepareSerialNum = val;
    }
    
    private int getMediaListFromDataCenter(KmPlayListItem item,ErrorInfo errorInfo) {
        int ret = -1;
        
        if (item == null ) {
            errorInfo.errorType = DownError.ERROR_TYPE_DOWN_MEDIA_FAILED;
            errorInfo.errorCode = DownError.ERROR_CODE_ITEM_NULL;
            errorInfo.errorMessage = "GetMediaListPresenter item null";
            return ret;
        }
        
        List<Media> list = new ArrayList<Media>();
        String rwMac = "";
        String token = "";
        String exception = "";
        
        int tryTime = 0;
        
        for (; tryTime < 3; tryTime++) {
            if (mStopFlag) {
                UmengAgentUtil.reportError("getmedialist recv stop cmd");
                return -1;
            }
            
            try {
                ret = DCDomain.getInstance().requestSongMediaList(item.getSongId(), rwMac, token, list,errorInfo);
                if (ret == 0) {
                    break;
                }
                continue;
             } catch (Exception e) {
                 errorInfo.errorType = DownError.ERROR_TYPE_GET_MEDIALIST_FAILED;
                 if (e instanceof JSONException) {
                     errorInfo.errorCode = DownError.ERROR_CODE_REQUSET_CATCH_JSON_EXCEPTION;
                 } else {
                     errorInfo.errorCode = DownError.ERROR_CODE_REQUSET_CATCH_UNKNOW_EXCEPTION;
                 }
                 errorInfo.errorMessage = e.getMessage();
                 UmengAgentUtil.reportError(e.getMessage());
                 exception = e.getMessage();
                 EvLog.e(exception);
                 continue;
             }
        }
        
        if (tryTime == 3 && ret != 0) {
            return ret;
        }
        
        if (DownError.debugGetMediaListError(errorInfo)) {
            return errorInfo.errorCode;
        }
        
        EvLog.d("GetMediaList list.size() = " + list.size());
        
        if (list.size() <= 0) {
            errorInfo.errorCode = DownError.ERROR_CODE_MEDIA_LIST_INVALID;
            ret = -1;
            return ret;
        }
        
        MediaManager.getInstance().updateOnlineMedia(item.getSongId(), list);
        
        Song song = SongManager.getInstance().getSongById(item.getSongId());
        if (song == null) {
            UmengAgentUtil.reportError(item.getSongId() + ", getmedialistpresenter, find song is null,get from net");
            EvLog.e(item.getSongId() + " getSongFromDataCenter from net");
            try {
                song = SongManager.getInstance().getSongFromDataCenter(item.getSongId());
                if (song == null || !SongManager.getInstance().add(song)) {
                    EvLog.e(item.getSongId() + " add to db failed");
                } else {
                    EvLog.e(item.getSongId() + " add to db success");
                }
            } catch (Exception e) {
                e.printStackTrace();
                UmengAgentUtil.reportError(item.getSongId() + ", getmedialistpresenter, find song is null,get from net failed");
                EvLog.e(item.getSongId() + " getSongFromDataCenter failed");
            }
        }
        
        item.updateMediaList(item.getSongId());
        if (item.getVideoMedia() == null) {
            EvLog.e(item.getSongName() + " video media is null,songId="+ item.getSongId() + ",isExistDB:" +SongManager.getInstance().isExist(item.getSongId()));
        } /*else {
            EvLog.i(item.getSongName() + " video media is not null ");
        }*/
        ret = 0;
        return ret;
    }

    
    @Override
    protected Boolean doInBackground(Object... params) throws Exception {
        
        KmPlayListItem item = (KmPlayListItem) params[0];
        if (item == null) {
            mErrorInfo.errorType = DownError.ERROR_TYPE_DOWN_MEDIA_FAILED;
            mErrorInfo.errorCode = DownError.ERROR_CODE_ITEM_NULL;
            mErrorInfo.errorMessage = "GetMediaListPresenter item null";
            UmengAgentUtil.reportError(DownError.getUmengErrorMessage(item, mErrorInfo));
            EvLog.e(DownError.getUmengErrorMessage(item, mErrorInfo));
            return false;
        }

        mPrepareSerialNum = item.getSerialNum();
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.errorType = DownError.ERROR_TYPE_GET_MEDIALIST_FAILED;
        //从数据中心获取medialist列表
        int retmedialist = getMediaListFromDataCenter(item,errorInfo);
        if (retmedialist != 0) {
            mErrorInfo = errorInfo;
            UmengAgentUtil.reportError(DownError.getUmengErrorMessage(item, mErrorInfo));
            EvLog.e(DownError.getUmengErrorMessage(item, mErrorInfo));
            return false;
        }
        
       /* if (DownError.debugGetMediaListError(mErrorInfo)) {
            return false;
        }
        
        if (DownError.debugBeforeDownThreadError(mErrorInfo)) {
            return false;
        }*/
        mItemList.clear();

        Media media = item.getVideoMedia();
        if (media == null) {
            mErrorInfo.errorType = DownError.ERROR_TYPE_DOWN_MEDIA_FAILED;
            mErrorInfo.errorCode = DownError.ERROR_CODE_ITEM_MEDIA_NULL;
            mErrorInfo.errorMessage = "get no video media";
            UmengAgentUtil.reportError(DownError.getUmengErrorMessage(item, mErrorInfo));
            EvLog.e(DownError.getUmengErrorMessage(item, mErrorInfo));
            return false;
        }
        if (!TextUtils.isEmpty(media.getRemoteSubtitle())) {
            if (mGetMediaCallback != null && mGetMediaCallback.isNeedGetErcUrl(item.getSerialNum())) {
                mItemList.add(new OnlineFileItem(OnlineFileItem.TYPE_ERC,-1,"","",media.getRemoteSubtitle()));
            } else {
                EvLog.d("do not need get erc url in GetMediaListPresenter");
            }
        } else {
            EvLog.d("medialist from dc ,do not have erc url");
        }
        if (TextUtils.isEmpty(media.getURI())) {
            mErrorInfo.errorType = DownError.ERROR_TYPE_DOWN_MEDIA_FAILED;
            mErrorInfo.errorCode = DownError.ERROR_CODE_MEDIA_URL_NULL;
            mErrorInfo.errorMessage = "media getURI null";
            UmengAgentUtil.reportError(DownError.getUmengErrorMessage(item, mErrorInfo));
            EvLog.e(DownError.getUmengErrorMessage(item, mErrorInfo));
            return false;
        }
        
        /*if (!mGetMediaCallback.isNeedGetMediaUrl(item.getSerialNum())) {
            EvLog.d("do not need get media url");
            return true;
        }*/
        String fileDestName = String.format("%08d", item.getSongId())+ ResourceSaverPathManager.VIDEO_FILE_SUFFIX;
        String savePath = "";
        if (/*DeviceConfigManager.SUPPORT_BUFFER_PLAY*/DeviceConfigManager.getInstance().getPlayMode() == IDeviceConfig.PLAY_MODE_BUFFER_MEM_PLAY) {
            savePath = ResourceSaverPathManager.getInstance().getResourceSavePath();
        } else {
            savePath = mGetMediaCallback.getMediaSavePath(media.getResourceSize());
        }
        
        if (TextUtils.isEmpty(savePath)) {
            mErrorInfo.errorType = DownError.ERROR_TYPE_DOWN_MEDIA_FAILED;
            mErrorInfo.errorCode = DownError.ERROR_CODE_SPACE_SUFFICIENT;
            mErrorInfo.errorMessage = "space sufficient";
            UmengAgentUtil.reportError(DownError.getUmengErrorMessage(item, mErrorInfo));
            EvLog.e(DownError.getUmengErrorMessage(item, mErrorInfo));
            return false;
        }
        mItemList.add(new OnlineFileItem(OnlineFileItem.TYPE_MEDIA,/*info.size*/media.getResourceSize(),fileDestName/*info.name*/,savePath,media.getURI()));
        return true;
    }

    
    @Override
    protected void onCompleted(Boolean result, Object... params) {
        KmPlayListItem item = (KmPlayListItem) params[0];
        EvLog.d("GetMediaListPresenter  onCompleted," + item.getSongName() + ",serialNum=" + mPrepareSerialNum +",result=" + result + ",mStopFlag=" + mStopFlag);
        if (!mStopFlag) {
            if (result != null) {
                if (!result) {
                    if (mListener != null) {
                        mListener.onGetMediaListFailed(mPrepareSerialNum,mErrorInfo);
                    }
                } else {
                    if (mListener != null) {
                        mListener.onGetMediaListSuccess(mPrepareSerialNum,mItemList);
                    }
                }
            }
        } else {
            
        }
        mPrepareSerialNum = -1;
        return;
    }

    @Override
    protected void onFailed(Exception exception, Object... params) {
        KmPlayListItem item = (KmPlayListItem) params[0];
        EvLog.d( "GetMediaListPresenter  onFailed," + item.getSongName() + ",reason=" + exception.getMessage());
        if (mListener != null) {
            mErrorInfo.errorCode = DownError.ERROR_UNKNOW;
            mErrorInfo.errorMessage = "GetMediaListPresenter onFailed:" + exception.getMessage();
            UmengAgentUtil.reportError(DownError.getUmengErrorMessage(item, mErrorInfo));
            mListener.onGetMediaListFailed(mPrepareSerialNum,mErrorInfo);
        }
        mPrepareSerialNum = -1;
    }
}