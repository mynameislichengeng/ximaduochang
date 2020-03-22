/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年11月17日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.update;

/**
 * [功能说明]
 */
public interface IBackgroundUpdateTimeOutListener {
    public void timeOut();
    
    public boolean timeOutLimited();
}
