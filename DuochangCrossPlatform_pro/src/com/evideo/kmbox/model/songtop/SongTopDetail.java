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

package com.evideo.kmbox.model.songtop;

/**
 * [功能说明]歌单详情
 */
public class SongTopDetail {
    
    /** [歌单id] */
    public int songTopId;
    
    /** [歌曲id] */
    public int songId;
    
    /** [歌曲名称] */
    public String songName;
    
    /** [歌星名称] */
    public String singerName;
    
    /** [可否评分] */
    public boolean score;
    
    /** [点播率] */
    public int orderRate;
    

    public SongTopDetail(int songTopId, int songId) {
        this.songTopId = songTopId;
        this.songId = songId;
    }
    
    public SongTopDetail(int songTopId, int songId, String songName, String singerName, boolean score,int orderRate) {
        this.songTopId = songTopId;
        this.songId = songId;
        this.songName = songName;
        this.singerName = singerName;
        this.score = score;
        this.orderRate = orderRate;
    }

    @Override
    public String toString() {
        return "SongTopDetail [songTopId=" + songTopId + ", songId="
                + songId + ", songName=" + songName + ", singerName=" + singerName + "]";
    }
    
}
