/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年8月11日     User     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.loganalyze;

import java.util.HashMap;

import android.content.Context;

import com.evideo.kmbox.SystemConfigManager;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.umeng.EventConst;
import com.evideo.kmbox.model.umeng.UmengAgent;
import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明]日志分析统计类
 */
public final class LogAnalyzeManager {
    
    private static LogAnalyzeManager sInstance;
    
    private LogAnalyzeManager() { 
    }
    
    public  void init(Context context) {
        initUmeng(context);
    }
    
    
    private static void initUmeng(Context context) {
        UmengAgent.setIsLogcatOpen(false);
        UmengAgent.openActivityDurationTrack(false);
    }
    /**
     * [功能说明]获取LogAnalyzeManager实例
     * @return LogAnalyzeManager实例
     */
  
    public static LogAnalyzeManager getInstance() {
        if(sInstance == null) {
            synchronized (LogAnalyzeManager.class) {
                LogAnalyzeManager temp = sInstance;
                if(temp == null) {
                  temp = new LogAnalyzeManager();
                  sInstance = temp;
                }
            }
         }
         return sInstance;
    }
    
    /**
     * [功能说明]activity返回
     * @param context context
     */
    public static void onResume(Context context) {
        UmengAgent.onResume(context);
    }

    /**
     * [功能说明]activity离开
     * @param context context
     */
    public static void onPause(Context context) {
        UmengAgent.onPause(context);
    }
    
    /**
     * [功能说明]进入页面
     * @param pageName 页面名称
     */
    public void onPageStart(String pageName) {
        UmengAgent.getInstance().onPageStart(pageName);
    }
    
    /**
     * [功能说明]离开页面
     * @param pageName 页面名称
     */
    public void onPageEnd(String pageName) {
        UmengAgent.getInstance().onPageEnd(pageName);
    }
    
    /**
     * [功能说明]上报错误
     * @param context context
     * @param error 异常信息
     */
    public static void reportError(Context context, String error) {
        UmengAgent.reportError(context, error);
//       mLogFileAgent.reportError(context, error);
    }
    
    /**
     * [功能说明]上报错误
     * @param context context
     * @param e exception对象
     */
    public static void reportError(Context context, Throwable e) {
        UmengAgent.reportError(context, e);
    }
    
    /**
     * [功能说明]在杀死进程时调用
     * @param context context
     */
    public static void onKillProcess(Context context) {
        UmengAgent.onKillProcess(context);
    }
    
    /**
     * [功能说明]统计事件发送次数
     * @param context 当前activity
     * @param eventId 事件id
     */
    public static void onEvent(Context context, String eventId) {
        UmengAgent.onEvent(context, eventId);
    }
    
    /**
     * [功能说明]统计事件2与事件1同时发送的次数
     * @param context 当前activity
     * @param eventId1  事件1id
     * @param eventId2  事件2id
     * <p>示例：验证关卡难度，即监控”player_dead”这个事件,示例代码如下:</p>
     * <pre>
     * 监控在关卡1的死亡率
     * UmengAgent.onEvent(this, "level_one","player_dead");
     * </pre>
     */
    public static void onEvent(Context context, String eventId1, String eventId2) {
        UmengAgent.onEvent(context, eventId1, eventId2);
    }
    
    /**
     * [功能说明]统计事件各属性被触发的次数
     * @param context 当前activity
     * @param eventId 事件id
     * @param m 当前事件的属性和取值
     * 
     * <p>示例：统计电商应用中“购买”事件发生的次数，以及购买的商品类型及数量，那么在购买的函数里调用：</p>
     * <pre>    
     *     HashMap<String,String> map = new HashMap<String,String>();
     *    map.put("type","book");
     *    map.put("quantity","3"); 
     *    UmengAgent.onEvent(mContext, "purchase", map);
     * </pre>
     */
    public static void onEvent(Context context, String eventId, HashMap<String, String> m) {
        //Map m=new HashMap<String, String>();
        UmengAgent.onEvent(context, eventId, m);
    }
    
    /**
     * [功能说明] 统计歌曲点播接口
     * @param context
     */
    public static void onEventOrderSong(Context context, int songId) {
        //umeng中只统计事件
        UmengAgent.onEvent(context, EventConst.ID_CLICK_SELECT_SONG);
    }
    
    /**
     * [功能说明] 统计歌单二级子页面的点击事件
     * @param context
     * @param menuid
     */
    public static void onEventInSongMenuSubPage(Context context, int menuid) {
        UmengAgent.onEvent(context, EventConst.ID_CLICK_SONG_MENU_SUB_PAGE);
    }
    
    /**
     * [功能说明] 统计歌单详情页面点歌事件
     * @param context
     * @param menuid 歌单id
     * @param songid 歌曲id
     */
    public static void onEventInSongMenuDetailsPage(Context context, int menuid,int songid) {
        UmengAgent.onEvent(context, EventConst.ID_CLICK_SONG_MENU_DETAILS_ORDER_SONG);
    }
    
}
