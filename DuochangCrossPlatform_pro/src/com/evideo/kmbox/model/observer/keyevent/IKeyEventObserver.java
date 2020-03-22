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
 * [功能说明]按键事件观察者
 */
public interface IKeyEventObserver {
    
    /**
     * [功能说明]home键按下
     */
    public void onHomeKeyPressed();
    
    /**
     * [功能说明]back键按下
     */
    public boolean onBackKeyPressed();
}
