/*
 *  Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 *  All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *    Date           Author      Version     Description
 *  ----------------------------------------------------
 *  unknown        "unknown"       1.0       [修订说明]
 *  2016-8-1     "qiuyunliang"     1.1       [修订说明]
 */

package com.evideo.evnetworkchecker.item;

/**
 * [网络检测观察]
 */
public interface ICheckStepObserver {
    public void onCheckStart(int state);
    public void onCheckEnd(int state, int tryCount);
    public void onCheckError(int state, int type);
    /**[CDN测速]*/
    public void onCheckCDNSpeed(float speed);
}
