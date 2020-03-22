/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-3-16     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.model.songtop;

/**
 * [歌单]
 */
public class SongTop {

    /** [歌单id] */
    public int songTopId;
    
    /** [歌单图片url] */
    public String imageUrl;
    
//    /** [歌单图片url-大图] */
//    public String imageUrlBig;
    
    /** [歌单名称] */
    public String name;
    
//    /** [歌单描述] */
//    public String description;
    
    /** [歌曲列表数据的保存时间] */
    public long timestamp;
    
    /** [歌曲总数] */
    public int totalNum;
    
    /** [分类编码] */
    public int songTopTypeCode;
    
    /** [子标题] */
    public String subTitle;
    
    /** [歌名列表] */
    public String songsList;
    
    /** [歌手列表] */
    public String singersList;
    
    public SongTop(int songTopId, String imageUrl, String name) {
        this(songTopId, imageUrl, name, 0, 0, 0, null, null, null);
    }
    
    public SongTop(int songTopId, String imageUrl, String name, long timestamp, int totalNum) {
        this(songTopId, imageUrl, name, timestamp, totalNum, 0, null, null, null);
    }
    
    public SongTop(int songTopId, String imageUrl, String name, long timestamp, int totalNum,
            int typeCode, String subtitle, String songsList, String singersList) {
        this.songTopId = songTopId;
        this.imageUrl = imageUrl;
        this.name = name;
//        this.description = description;
        this.timestamp = timestamp;
        this.totalNum = totalNum;
//        this.imageUrlBig = imageUrlBig;
        this.songTopTypeCode = typeCode;
        this.subTitle = subtitle;
        this.songsList = songsList;
        this.singersList = singersList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "SongTop [songTopId=" + songTopId + ", imageUrl=" + imageUrl
                + ", imageUrlBig=" +  ", name=" + name
                + ", description=" +  ", timestamp=" + timestamp
                + ", totalNum=" + totalNum + ",songTopTypeCode=" + songTopTypeCode  
                + ",subtitle=" + subTitle + ",songlist=" + songsList
                + ",singerlist=" + singersList + "]";
    }

}
