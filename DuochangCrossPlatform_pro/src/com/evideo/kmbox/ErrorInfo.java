/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年4月25日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox;

/**
 * [功能说明]
 */
public class ErrorInfo {
    public int errorType;
    public int errorCode;
    /** [错误补充码，用于http错误的responseCode] */
    public int errorCodeSupplement;
    public String errorMessage;
    
    public ErrorInfo() {
        this.errorType = -1;
        this.errorCode = -1;
        this.errorCodeSupplement = 0;
        this.errorMessage = "";
    }
}
