/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年7月26日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.player;

/**
 * [功能说明]
 */
public class KmPlayerError {
    //原生解码器会出现切换音轨的错误
    public static final int ERR_SELECT_TRACK = -20000;
   //原生解码器会出现初始化音轨的错误
    public static final int ERR_INIT_TRACK_INFO = -20001;
}
