/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年7月21日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.songinfo;

import com.evideo.kmbox.model.dao.data.Media;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.util.MediaUtil;

/**
 * [功能说明]
 */
public  class SongInfoInDB{
    public Media video;
    public boolean isCanScoreInDB;//在数据库中是否可评分
    public String songName;
    public String singerName;
    public String albumUrl;
    public int songID;
    
    public SongInfoInDB() {
        this.singerName = null;
        this.songName = null;
        this.albumUrl = null;
        this.songID = -1;
        this.isCanScoreInDB = false;
        this.video = null;
    }
    public void initParamFromDB(Song song) {
        this.albumUrl = song.getAlbumURI();
        this.singerName = song.getSingerDescription();
        this.songName = song.getName();
        this.songID = song.getId();
        this.isCanScoreInDB = song.canScore();
        this.video = MediaUtil.getVideoMediaFromSong(song);
    }
    
    public void initParam(SongInfoInDB db) {
        this.albumUrl = db.albumUrl;
        this.singerName = db.singerName;
        this.songName = db.songName;
        this.songID = db.songID;
        this.isCanScoreInDB = db.isCanScoreInDB;
        this.video = db.video;
    }
};
