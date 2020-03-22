/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-7-27     "liuyantao"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.update.db;

/**
 * [启动后每隔半小时检测更新观察者-EHH=EveryHalfHour]
 */
public interface IEHHTimeUpdateObserver {
    /**
     * [网络未连接-无法检测更新]
     */
    public void onNetNotConnected();
    
    /**
     * [开始]
     */
    public void onStart();
    
    /**
     * [结束]
     */
    public void onFinished();
    
    /**
     * [半小时检测更新-状态]
     * @param status 
     */
    public void onUpdate(EHHTimeUpdateStatus status);
    
    /**
     * [错误回调]
     * @param error 错误码
     */
    public void onError(EHHTimeUpdateErrorCode error);
}
