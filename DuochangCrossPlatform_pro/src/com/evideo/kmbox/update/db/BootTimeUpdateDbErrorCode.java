/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-7-27     "liuyantao"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.update.db;

/**
 * [启动时更新数据库-错误码]
 */
public enum BootTimeUpdateDbErrorCode {
    /** [无] */
    Error_None,
    /** [解析文件失败] */
    Error_ParseFileFailed,
    /** [下载全库时下载失败] */
    Error_DownloadFailed
}
