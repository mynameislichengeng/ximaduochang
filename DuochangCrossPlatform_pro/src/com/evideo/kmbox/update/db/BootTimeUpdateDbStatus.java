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
 * [启动时数据库更新说明]
 */
public enum BootTimeUpdateDbStatus {
    /** [无] */
    Status_None,
    /** [从数据中心下载数据全库或者下载差异化数据] */
    Status_UpdatingWholeDbData,
    /** [遍历硬盘中文件] */
    Status_Traversing,
    /** [加歌中] */
    Status_Adding
}
