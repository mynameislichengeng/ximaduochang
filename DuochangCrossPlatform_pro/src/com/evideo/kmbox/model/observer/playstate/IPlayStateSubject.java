/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年1月5日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.observer.playstate;


/**
 * [功能说明]
 */
public interface IPlayStateSubject {
    /**
     * @brief : [注册外部存储设备观察者]
     * @param observer
     */
    public void registPlayStateObserver(IPlayStateObserver observer);
    
    /**
     * @brief : [注销外部存储设备观察者]
     * @param observer
     */
    public void unregistPlayStateObserver(IPlayStateObserver observer);
    
    public void notifyDowningStart();
    public void notifyPlayStart();
    public boolean notifyPlayStop(int stopState,String shareCode);
    public void notifyPlayError(String errorMessage);
}
