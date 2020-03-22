/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年8月23日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.update.huodong;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.HttpUtil;
import com.evideo.kmbox.util.HttpUtil.HttpDownInfo;


/**
 * [功能说明]
 */
public class HuoDongDataManager {
    private static HuoDongDataManager instance = null;
    public static final int UPDATE_INTERVAL = 15;
    private static final int UPDATE_MESSAGE = 0;
    private Handler mUpdateHandler;
    private List<HuodongLocalInfo> mLocalDatas = new ArrayList<HuodongLocalInfo>();
    private IHuodongDataListener mListener = null;
    
    public static HuoDongDataManager getInstance() {
        if(instance == null) {
            synchronized (HuoDongDataManager.class) {
                HuoDongDataManager temp = instance;
                if(temp == null) {
                  temp = new HuoDongDataManager();
                  instance = temp;
                }
            }
         }
         return instance;
    }
    
    public static class HuodongLocalInfo {
        public int type;
        public int id;
        public String smallBmpPath;
        public String arg0;
        public boolean expire;
        
        public HuodongLocalInfo(int id,int type) {
            this.id = id;
            this.type = type;
            expire = false;
            smallBmpPath = "";
            arg0 = "";
        }
    }
    
    
    public List<HuodongLocalInfo> getList() {
        final List<HuodongLocalInfo> tmpList = new ArrayList<HuoDongDataManager.HuodongLocalInfo>();
        tmpList.clear();
        synchronized (mLocalDatas) {
            tmpList.addAll(mLocalDatas);
        }
        return tmpList;
    }
    
    
    public interface IHuodongDataListener {
        public void onHuodongDataReady();
        public void onHuodongDataUpdate();
    }
    
    public void setListener(IHuodongDataListener listener) {
        mListener = listener;
    }
    
    private boolean mIsReadyCall = false;
    
    private HuoDongDataManager() {
        mLocalDatas.clear();
        mUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == UPDATE_MESSAGE) {
                    synchronized (mLocalDatas) {
                        Iterator<HuodongLocalInfo> itDel = mLocalDatas.iterator();
                        while (itDel.hasNext()) {
                            if (itDel.next().expire) {
                                itDel.remove();
                            }
                        }
                        if (msg.obj == null || !(msg.obj instanceof List<?>)) {
                            return;
                        }
                        
                        @SuppressWarnings("unchecked")
                        List<HuodongLocalInfo> newAddList = (List<HuodongLocalInfo>)msg.obj;
                        if (newAddList.size() > 0) {
                            mLocalDatas.addAll(newAddList);
                        }
                        if (!mIsReadyCall) {
                            mIsReadyCall = true;
                            if (mListener != null) {
                                mListener.onHuodongDataReady();
                            }
                        } else {
                            if (mListener != null) {
                                mListener.onHuodongDataUpdate();
                            }
                        }
                    }
                }
            }
        };
    }
    
    //子线程中调用
    public void onUpdate(List<HuodongItemInfo> list) {
        // EvLog.d(TAG, "onUpdate --------" + list.size());
        boolean needUpdate = false;
        List<HuodongItemInfo> newAddItemList = new ArrayList<HuodongItemInfo>();
        for (HuodongItemInfo item : list) {
            if (!isExistLocal(item)) {
                newAddItemList.add(item);
            }
        }

        for (HuodongLocalInfo item : mLocalDatas) {
            if (!isExistOnline(item, list)) {
                item.expire = true;
                EvLog.d(item.id + " is expire");
                needUpdate = true;
            }
        }
        List<HuodongLocalInfo> saveList = new ArrayList<HuodongLocalInfo>();
        saveList.clear();
        if (newAddItemList.size() > 0) {
            downActivityFile(newAddItemList, saveList);
            if (saveList.size() > 0) {
                needUpdate = true;
            }
        } else {
            EvLog.d("activitylist not add new ,nothing need down");
        }

        if (needUpdate) {
            Message msg = mUpdateHandler.obtainMessage();
            msg.what = UPDATE_MESSAGE;
            msg.obj = saveList;
            mUpdateHandler.sendMessage(msg);
        }
    }
    
    private boolean isExistLocal(HuodongItemInfo item) {
        for (HuodongLocalInfo itemLocal : mLocalDatas) {
            if (item.activity_id == itemLocal.id) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isExistOnline(HuodongLocalInfo item,List<HuodongItemInfo> list) {
        for (HuodongItemInfo itemLocal : list) {
            if (item.id == itemLocal.activity_id) {
                return true;
            }
        }
        return false;
    }
    
    private void downActivityFile(List<HuodongItemInfo> downList, List<HuodongLocalInfo> saveList) {
        String iconDownUrl = "";
        
        EvLog.d("DownActivityPresenter  downList.size() = " + downList.size());
        for (HuodongItemInfo item : downList) {
            if (TextUtils.isEmpty(item.imgUrl)) {
                continue;
            }
            
            HuodongLocalInfo info = new HuodongLocalInfo(item.activity_id,item.activity_type);
            
            iconDownUrl = item.imgUrl;
            String fileName = item.activity_id + "_small.png";
            EvLog.d(iconDownUrl + " 11>> " + fileName);
            HttpDownInfo downInfo = HttpUtil.downloadFileWithBufferWrite(iconDownUrl, fileName, ResourceSaverPathManager.getInstance().getHuodongSavePath()/*KmConfig.ACTIVITY_RESOURCE_SAVE_PATH*/);
            
            if (downInfo == null || (downInfo.downRet ==  false) || TextUtils.isEmpty(downInfo.downPath)) {
                EvLog.e(iconDownUrl + " down failed");
                continue;
            }
            info.smallBmpPath = downInfo.downPath;

            int type = item.activity_type;
            if (type == HuodongType.HUODONG_TYPE_SINGER) {
                info.arg0 = item.activity_arg0;
            } else if (type == HuodongType.HUODONG_TYPE_SONGMENU) {
                info.arg0 = item.activity_arg0;
            } else if (type == HuodongType.HUODONG_TYPE_RANK) {
                info.arg0 = item.activity_arg0;
            } else if (type == HuodongType.HUODONG_TYPE_BMP) {
                iconDownUrl = item.activity_arg0;
                fileName = item.activity_id + "_big.png";
                downInfo = HttpUtil.downloadFileWithBufferWrite(iconDownUrl, fileName, ResourceSaverPathManager.getInstance().getHuodongSavePath()/*KmConfig.ACTIVITY_RESOURCE_SAVE_PATH*/);
                if (downInfo == null || (downInfo.downRet ==  false) || TextUtils.isEmpty(downInfo.downPath)) {
                    EvLog.e(iconDownUrl + " down failed");
                    continue;
                }
                info.arg0 = downInfo.downPath;
            } else if (type == HuodongType.HUODONG_TYPE_HTML) {
                info.arg0 = item.activity_arg0;
            }
            saveList.add(info);
        }
    }
}
