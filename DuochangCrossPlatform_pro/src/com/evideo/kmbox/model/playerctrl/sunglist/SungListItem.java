/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-6-1     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.playerctrl.sunglist;

import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;

/**
 * [功能说明]
 */
public class SungListItem {    
    boolean mCanScore;
    private int mId;//在数据结构中的序列号
    private int mSongId;
    private int mScore;
    private String mShareCode;
    private String mCustomerid;
    private String mSongName;
    private String mSingerDescription;
    
   /* public SungListItem(SungListItem item) {
        mIndex = item.getIndex();
        mCanScore = item.mCanScore;
        mSongId = item.mSongId;
        mScore = item.mScore;
        mShareCode = item.mShareCode;
        mCustomerid = item.mCustomerid;
        mSongName = item.mSongName;
        mSingerDescription = item.mSingerDescription;
    }*/
    
    public SungListItem(KmPlayListItem item) {
        mId = -1;
        mCanScore = item.isSongCanScore();
        mSongId = item.getSongId();
        mScore = item.getScoreValue();
        mShareCode = "";
        mCustomerid = item.getCustomerid();
        mSongName = item.getSongName();
        mSingerDescription = item.getSingerName();
    }
    
    public SungListItem(int id,Song song, boolean canScore, int score,
            String sharecode,String customerid) {
        mId = id;
        mCanScore = canScore;
        mSongId = song.getId();
        mScore = score;
        mShareCode = sharecode;
        mCustomerid = customerid;
        mSongName = song.getName();
        mSingerDescription = song.getSingerDescription();
    }
    
    public int getId() {
        return mId;
    }
    
    public void setId(int id) {
        mId = id;
    }
    public String getShareCode() {
        return mShareCode;
    }
    public void setShareCode(String shareCode) {
        this.mShareCode = shareCode;
    }
    
    public boolean canScore() {
        return mCanScore;
    }
    public String getSingerDescription() {
        return mSingerDescription;
    }

    public void setSingerDescription(String singerDescription) {
        this.mSingerDescription = singerDescription;
    }

    public String getSongName() {
        return mSongName;
    }

    public void setSongName(String songName) {
        this.mSongName = songName;
    }

    public int getSongId() {
        return mSongId;
    }

    public void setSongId(int songId) {
        this.mSongId = songId;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int mScore) {
        this.mScore = mScore;
    }

    public String getCustomerid() {
        return mCustomerid;
    }

    public void setCustomerid(String mCustomerid) {
        this.mCustomerid = mCustomerid;
    }
}
