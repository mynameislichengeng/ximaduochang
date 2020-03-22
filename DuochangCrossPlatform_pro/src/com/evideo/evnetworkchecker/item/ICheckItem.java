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
 * [功能说明] 检测项接口
 */
public interface ICheckItem {

    /**
     * [开始]
     */
    public void run();

    /**
     * [获取结果]
     *
     * @return 成功或失败
     */
    public boolean getResult();

    /**
     * [获取结果信息]
     *
     * @return 结果信息
     */
    public String getResultMessage();

    /**
     * [获取解决方案]
     *
     * @return 解决方案
     */
    public String getResolution();

    /**
     * [获取错误类型]
     *
     * @return 错误类型
     */
    public int getErrorType();
}
