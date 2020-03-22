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
 * [每隔半小时检测更新-状态EHH=EveryHalfHour]
 */
public enum EHHTimeUpdateStatus {
    /** [无] */
    Status_None,
    /** [歌星数据] */
    Status_UpdateSingerData,
    /** [media数据] */
    Status_UpdateMediaData,
    /** [歌曲数据] */
    Status_UpdateSongData,
    /** [完成] */
    Status_Finised
}
