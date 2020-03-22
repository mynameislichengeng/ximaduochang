/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-4-7     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.exception;

/**
 * [功能说明]数据中心通信无结果异常
 */
public class DCNoResultException extends DataCenterCommuException {

    private static final long serialVersionUID = 3680551600702426328L;
    
    public DCNoResultException(String message) {
        msg = message;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Data center communication no result: " + msg;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "Data center communication no result: " + msg;
    }

}
