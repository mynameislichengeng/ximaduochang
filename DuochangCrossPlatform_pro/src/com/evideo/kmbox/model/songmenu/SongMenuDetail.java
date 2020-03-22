/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-3-26     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.model.songmenu;

/**
 * [功能说明]歌单详情
 */
public class SongMenuDetail {
    
    /** [歌单id] */
    public int songMenuId;
    
    /** [歌曲id] */
    public int songId;

    public SongMenuDetail(int songMenuId, int songId) {
        this.songMenuId = songMenuId;
        this.songId = songId;
    }

    @Override
    public String toString() {
        return "SongMenuDetail [songMenuId=" + songMenuId + ", songId="
                + songId + "]";
    }
    
}
