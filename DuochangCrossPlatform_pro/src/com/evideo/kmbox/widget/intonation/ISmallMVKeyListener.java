/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年11月20日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.intonation;

/**
 * [功能说明] 小视频窗口解码监听
 */
public interface ISmallMVKeyListener {
    
    /**
     * [功能说明] 小视频向上按键
     */
    public boolean onSmallMVUpKey();
    
    /**
     * [功能说明] 小视频向右按键
     */
    public boolean onSmallMVRightKey();
}
