/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-5-8     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.model.observer.keyevent;

/**
 * [功能说明]
 */
public interface IKeyEventSubject {
    
    /**
     * [功能说明]注册按键事件观察者
     * @param observer 按键事件观察者
     */
    public void registerKeyEventObserver(IKeyEventObserver observer);
    
    /**
     * [功能说明]注销按键事件观察者
     * @param observer 按键事件观察者
     */
    public void unregisterKeyEventObserver(IKeyEventObserver observer);
    
    /**
     * [功能说明]通知home键按下
     */
    public void notifyHomeKeyPressed();
    
    
    /**
     * [功能说明]通知back键按下
     */
    public boolean notifyBackKeyPressed();
}
