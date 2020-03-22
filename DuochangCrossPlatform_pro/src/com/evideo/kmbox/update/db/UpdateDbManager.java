/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-7-13     "liuyantao"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.update.db;

import android.text.TextUtils;

import com.evideo.kmbox.model.dao.data.SongIdManager;
import com.evideo.kmbox.model.dao.data.WholeSongManager;
import com.evideo.kmbox.util.EvLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * [曲库生成、更新管理类]
 */
public final class UpdateDbManager {

    private static UpdateDbManager sInstance = null;

    private static final String STB_SUPPORT_SONG_FORMAT_TS = "ts";
    private static final String STB_SUPPORT_SONG_FORMAT_MPG = "mpg";
    private static final String STB_SUPPORT_SONG_FORMAT_MP4 = "mp4";
    private static final String STB_SUPPORT_SONG_FORMAT_MKV = "mkv";
    public static final int Type_UNKNOWN = -1;
    /**
     * [MKV格式]
     */
    public static final int Type_MKV = 1;
    /**
     * [TS格式]
     */
    public static final int Type_TS = 2;
    /**
     * [MPG格式]
     */
    public static final int Type_MPG = 3;
    /**
     * [AVI格式]
     */
    public static final int Type_AVI = 4;

    public static final String[] STB_SUPPORT_SONG_FORMAT = {
            STB_SUPPORT_SONG_FORMAT_TS, STB_SUPPORT_SONG_FORMAT_MPG,
            STB_SUPPORT_SONG_FORMAT_MP4, STB_SUPPORT_SONG_FORMAT_MKV
            /*STB_SUPPORT_SONG_FORMAT_EVB*/};

    private boolean mPermitImport = false;

    /**
     * [数据中心歌曲信息]
     */
    public static class DCSongItem {
        /**
         * [全曲库歌曲信息]
         */
        public RemoteSong song;
        /**
         * [操作]
         */
        public int option;
    }

    /**
     * [数据中心歌星信息]
     */
    public static class DCSingerItem {
        /**
         * [全曲库歌星数据]
         */
        public RemoteSinger singer;
        /**
         * [操作]
         */
        public int option;
    }

    /**
     * [数据中心media信息]
     */
    public static class DCMediaItem {
        /**
         * [全曲库媒体信息]
         */
        public RemoteMedia media;
        /**
         * [操作]
         */
        public int option;
    }

    private UpdateDbManager() {
    }

    /**
     * [曲库更新管理单例]
     *
     * @return 单例
     */
    public static synchronized UpdateDbManager getInstance() {
        if (sInstance == null) {
            sInstance = new UpdateDbManager();
        }

        return sInstance;
    }

    public boolean isPermitImport() {
        return mPermitImport;
    }

    public void setPermitImport(boolean mPermitImport) {
        this.mPermitImport = mPermitImport;
    }


    // modify by qiangv
    private int getFilesCount(String dir) {
        File fileResource = new File(dir);
        return fileResource.listFiles().length;
    }

    private boolean isSupportedMedia(String fileName) {
        return getFormat(fileName) != Type_UNKNOWN;
    }

    private boolean isValidName(String fileName) {
        return isSongNameDigit(fileName);
    }

    private boolean isValidMedia(String fileName) {
        return isSupportedMedia(fileName) && isValidName(getName(fileName));
    }

    private void clearRelatedDbList() {
        SongIdManager.getInstance().clearList();
    }

    //将String数组重新组装为Song
    private List<DCSongItem> changeSong(List<String> songList) {
        List<DCSongItem> songInfoList = new ArrayList<DCSongItem>();
        return songInfoList;
    }

    //将String数组重新组装为Singer
    private List<DCSingerItem> changeSinger(List<String> singerList) {
        List<DCSingerItem> singerInfoList = new ArrayList<DCSingerItem>();
        return singerInfoList;
    }

    //将String数组重新组装为Media
    private List<DCMediaItem> changeMedia(List<String> mediaList) {
        List<DCMediaItem> mediaInfoList = new ArrayList<DCMediaItem>();
        return mediaInfoList;
    }

    private String getName(String fileName) {
        char standard = '.';
        int index = 0;
        for (int i = 0; i < fileName.length(); i++) {
            if (fileName.charAt(i) == standard) {
                // 确保是最后一个“.”
                index = i;
            }
        }
        // 获取文件名
        fileName = fileName.substring(0, index);

        return fileName;
    }

    private boolean isEvideoNo(String fileName) {
        return WholeSongManager.getInstance().isEvideoNoSong(fileName);
    }

    private boolean isSongNameDigit(String name) {
        if (name.length() != WholeSongManager.EVIDEO_NUMBER_SONG_LENGTH
                && name.length() != WholeSongManager.EVIDEO_NUMBER_SONG_LENGTH + 1) {
            EvLog.i("evideo number song require 8/9 place");
            return false;
        }

        for (int i = name.length(); --i >= 0; ) {
            if (!Character.isDigit(name.charAt(i))) {
                EvLog.i("Non pure number");
                return false;
            }
        }
        // 非视易编号歌曲在本地全库、数据中心都未查找到，则本地配置文件中查找，再通信确定信息
        for (int i = name.length(); --i >= 0; ) {
            if (!Character.isDigit(name.charAt(i))) {
                EvLog.i("Non pure number");
                return false;
            }
        }
        return true;
    }

    /**
     * [检测是否是支持的歌曲格式文件]
     *
     * @param fileName 文件名
     * @return true是 false不是
     */
    private boolean checkFormat(String fileName) {
        String standard = ".";

        while (fileName.contains(standard)) {
            fileName = fileName.substring(fileName.indexOf(standard) + 1);
        }
        return isSupport(STB_SUPPORT_SONG_FORMAT, fileName);
    }

    private static int getFormat(String fileName) {
        String standard = ".";
        int index = -1;
        while (fileName.contains(standard)) {
            fileName = fileName.substring(fileName.indexOf(standard) + 1);
        }

        if (fileName.equals(STB_SUPPORT_SONG_FORMAT_TS)) {
            index = Type_TS;
        } else if (fileName.equals(STB_SUPPORT_SONG_FORMAT_MPG)) {
            index = Type_MPG;
        } else if (fileName.equals(STB_SUPPORT_SONG_FORMAT_MKV)) {
            index = Type_MKV;
        }
//        else if (fileName.equals(STB_SUPPORT_SONG_FORMAT_EVB)) {
//            index = Type_EVB;
//        }
        return index;
    }

    public static boolean isMediaFileSupport(File file) {
        if (file == null) {
            return false;
        }
        if (!file.exists()) {
            return false;
        }
        String fileName = file.getName();
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }

        return getFormat(fileName) != -1;
    }

    // String数组是否还有 String数据
    private static boolean isSupport(String[] strs, String s) {
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].indexOf(s) != -1) {
                return true;
            }
        }
        return false;
    }

}
