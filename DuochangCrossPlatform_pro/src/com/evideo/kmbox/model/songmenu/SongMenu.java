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

package com.evideo.kmbox.model.songmenu;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;

/**
 * [歌单]
 */
public class SongMenu {
    
    private static SongMenu sSongMenuChild = new SongMenu(
            SongMenu.SONG_MENU_ID_CHILD, "",
            BaseApplication.getInstance().getResources().getString(R.string.song_menu_child), 
            BaseApplication.getInstance().getResources().getString(R.string.song_menu_child_description));

    private static SongMenu sSongMenuDrama = new SongMenu(
            SongMenu.SONG_MENU_ID_DRAMA, "",
            BaseApplication.getInstance().getResources().getString(R.string.song_menu_drama), 
            BaseApplication.getInstance().getResources().getString(R.string.song_menu_drama_description));
    
    private static SongMenu sSongMenuNewSong = new SongMenu(
            SongMenu.SONG_MENU_ID_NEW_SONG, "",
            BaseApplication.getInstance().getResources().getString(R.string.song_menu_new_song),
            BaseApplication.getInstance().getResources().getString(R.string.song_menu_new_song_description));

    private static SongMenu sSongMenuWFSong = new SongMenu(
            SongMenu.SONG_MENU_ID_NEW_WF, "",
            BaseApplication.getInstance().getResources().getString(R.string.song_menu_new_song),
            BaseApplication.getInstance().getResources().getString(R.string.song_menu_new_song_description));

    private static SongMenu sSongMenuMBwSong = new SongMenu(
            SongMenu.SONG_MENU_ID_NEW_MB, "",
            BaseApplication.getInstance().getResources().getString(R.string.song_menu_new_song),
            BaseApplication.getInstance().getResources().getString(R.string.song_menu_new_song_description));
    
    /** [id-最新歌曲] */
    public static final int SONG_MENU_ID_NEW_SONG = 10011;
    public static final int SONG_MENU_ID_NEW_WF = 149;
    public static final int SONG_MENU_ID_NEW_MB = 10010;

    /** [id-童真童趣] */
    public static final int SONG_MENU_ID_CHILD = 11;
    /** [id-梨园戏曲] */
    public static final int SONG_MENU_ID_DRAMA = 122;
    
    /** [歌单id] */
    public int songMenuId;
    
    /** [歌单图片url] */
    public String imageUrl;
    
    /** [歌单图片url-大图] */
    public String imageUrlBig;
    
    /** [歌单名称] */
    public String name;
    
    /** [歌单描述] */
    public String description;
    
    /** [歌曲列表数据的保存时间] */
    public long timestamp;
    
    /** [歌曲总数] */
    public int totalNum;
    
    public SongMenu(int songMenuId, String imageUrl, String name, 
            String description) {
        this(songMenuId, imageUrl, name, description, 0, 0, null);
    }
    
    public SongMenu(int songMenuId, String imageUrl, String name, 
            String description, long timestamp, int totalNum, String imageUrlBig) {
        this.songMenuId = songMenuId;
        this.imageUrl = imageUrl;
        this.name = name;
        this.description = description;
        this.timestamp = timestamp;
        this.totalNum = totalNum;
        this.imageUrlBig = imageUrlBig;
    }
    
    /**
     * [功能说明]获取儿童歌单
     * @return 儿童歌单
     */
    public static SongMenu getSongMenuChild() {
        return sSongMenuChild;
    }
    
    /**
     * [功能说明]获取戏曲歌单
     * @return 戏曲歌单
     */
    public static SongMenu getSongMenuDrama() {
        return sSongMenuDrama;
    }
    
    public static SongMenu getSongMenuNewSong() {
        return sSongMenuNewSong;
    }

    /**
     * 获取麦霸歌单
     */
    public static SongMenu getSongMenuMBSong() {
        return sSongMenuMBwSong;
    }
    /**
     * 获取王菲歌单
     */
    public static SongMenu getSongMenuWFSong() {

        return sSongMenuWFSong;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "SongMenu [songMenuId=" + songMenuId + ", imageUrl=" + imageUrl
                + ", imageUrlBig=" + imageUrlBig + ", name=" + name
                + ", description=" + description + ", timestamp=" + timestamp
                + ", totalNum=" + totalNum + "]";
    }

}
