/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年4月27日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.songinfo;

/**
 * [功能说明]
 */
public class SongDataState {
    /** [本地没有数据] */
    public static final int STATE_NONE = 0;
    /** [获取播放链阶段] */
    public static final int STATE_GET_MEDIA = 1;
    /** [准备下载阶段] */
    public static final int STATE_PREPARE_DOWN = 2;
    /** [歌曲下载] */
    public static final int STATE_DOWNING = 3;
    /** [歌曲数据本地不完整,例如下载到一半切歌，media完成，erc不存在] */
    public static final int STATE_INCOMPLETE = 4;
    /** [歌曲数据本地已完整存在] */
    public static final int STATE_COMPLETE = 5;
    /** [数据出错] */
    public static final int STATE_ERROR = 6;
}
