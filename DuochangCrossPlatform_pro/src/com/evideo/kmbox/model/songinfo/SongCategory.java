package com.evideo.kmbox.model.songinfo;
public interface SongCategory {
    public static final int CATEGORY_NONE = 0;
    /** [公播类型歌曲] */
    public static final int CATEGORY_BROADCAST = 1;
    /** [点播类型歌曲] */
    public static final int CATEGORY_PLAYLIST = 2;
    /** [回放类型歌曲] */
    public static final int CATEGORY_PLAYBACK = 3;
}