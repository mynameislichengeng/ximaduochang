/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年10月25日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.update.resource;

import java.util.List;

import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.datacenter.HomePictureManager;
import com.evideo.kmbox.model.datacenter.IHomePictureUpdateListener;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.model.update.BackgroundUpdateManager;
import com.evideo.kmbox.model.update.UpdateTimer.IUpdateTimeOutListener;
import com.evideo.kmbox.model.update.huodong.HuoDongDataManager;
import com.evideo.kmbox.model.update.huodong.HuoDongDataManager.IHuodongDataListener;
import com.evideo.kmbox.model.update.huodong.HuodongItemInfo;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.mainview.MainViewManager;

/**
 * [功能说明] 后台更新资源管理
 */
public class UpdateResourceManager {

    public static interface IUpdateHomeRightBottomIconListener {
        public void onUpdateHomeRightBottomIcon(String path);
    }
    
    public static void init() {
        updateHomeSkin();
        updateHuoDongList();
    }
    
    private static IHomePictureUpdateListener mHomePictureUpdateListener = null;
    public static void setHomePictureUpdateListener(IHomePictureUpdateListener listener) {
        mHomePictureUpdateListener = listener;
    }
    
    private static void updateHomeSkin() {
        HomePictureManager.getInstance().registeListener(new IHomePictureUpdateListener() {
            @Override
            public void onHomePictureUpdate() {
                if (mHomePictureUpdateListener != null) {
                    mHomePictureUpdateListener.onHomePictureUpdate();
                }
            }
        });
        HomePictureManager.getInstance().init();
        BackgroundUpdateManager.getInstance().addUpdateTask(true,HomePictureManager.getInstance(),HomePictureManager.UPDATE_INTERVAL);
    }
    
    private static void updateHuoDongList() {
        HuoDongDataManager.getInstance().setListener(new IHuodongDataListener() {
            
            @Override
            public void onHuodongDataUpdate() {
                EvLog.d("recieve onHuodongDataUpdate ");
                MainViewManager.getInstance().huodongUpdate();
            }
            
            @Override
            public void onHuodongDataReady() {
                EvLog.d("recieve onHuodongDataReady ");
                MainViewManager.getInstance().huodongReady();
            }
        });
        
        final IUpdateTimeOutListener huodonglistener = new IUpdateTimeOutListener() {
            
            @Override
            public void timeOut() {
                try {
                    EvLog.e("timeOut requestHuoDongList------");
                    List<HuodongItemInfo> list = DCDomain.getInstance().requestHuoDongList();
                    if (list.size() == 0) {
                        return;
                    }
                    HuoDongDataManager.getInstance().onUpdate(list);
                } catch (Exception e) {
                    EvLog.e(e.getMessage());
                    UmengAgentUtil.reportError(e);
                }
                return;
            }
        };
        EvLog.d("add huodonglistener to BackgroundUpdateManager");
        BackgroundUpdateManager.getInstance().addUpdateTask(true,huodonglistener,HuoDongDataManager.UPDATE_INTERVAL);
    }
    
    /*private static void updateTopSlogan() {
        UpdateTopSlogan.init();
        final IUpdateTimeOutListener updateRankSlognlistener = new IUpdateTimeOutListener() {
            @Override
            public void timeOut() {
                EvLog.e("timeOut UpdateTopSlogan------");
                UpdateTopSlogan.updateResource();
            }
        };
        long timeStart = System.currentTimeMillis();
        BackgroundUpdateManager.getInstance().addUpdateTask(true,updateRankSlognlistener,UpdateTopSlogan.UPDATE_INTERVAL);
        EvLog.d("time:" + (System.currentTimeMillis() - timeStart));
    }
    
    private static void updateHomeRightBottomIcon() {
        UpdateHomeRightBottomIcon.init();
        final IUpdateTimeOutListener getHomeRightBottomIconlistener = new IUpdateTimeOutListener() {
            @Override
            public void timeOut() {
                try {
                    EvLog.e("timeOut UpdateHomeRightBottomIcon------");
                    String path = UpdateHomeRightBottomIcon.updateResource();
                    if (TextUtils.isEmpty(path)) {
                        return;
                    }
                    if (mUpdateHomeRightBottomIconListener != null) {
                        mUpdateHomeRightBottomIconListener.onUpdateHomeRightBottomIcon(path);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        };
        BackgroundUpdateManager.getInstance().addUpdateTask(true,getHomeRightBottomIconlistener,UpdateHomeRightBottomIcon.UPDATE_INTERVAL);
    }*/
    
    /*private static IUpdateHomeRightBottomIconListener mUpdateHomeRightBottomIconListener = null;
    public static void setUpdateHomeRightBottomIcon(IUpdateHomeRightBottomIconListener listener) {
        mUpdateHomeRightBottomIconListener = listener;
    }*/
}
