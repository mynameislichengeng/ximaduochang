/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-5-15     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.model.dao.data;

import java.io.File;
import java.util.List;

import android.text.TextUtils;

import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明]song工具类
 */
public final class SongUtil {
    
    private SongUtil() {
    }
    
    /**
     * [据song对象删除文件]
     * @param song 歌曲对象
     */
    public static void deleteFileBySong(Song song) {
        if (song == null) {
            return;
        }
        
        File file = null;
        List<Media> medias = SongManager.getInstance().getMediaList(song.getId());

        if (medias != null && !medias.isEmpty()) {
            for (Media m : medias) {
                // 删除歌曲和字幕文件
                String localPath = m.getLocalFilePath();
                String subtitlePath = m.getLocalSubtitlePath();

                if (!TextUtils.isEmpty(localPath)) {
                    file = new File(localPath);
                    if (file.isFile() && file.exists()) {
                        EvLog.i("deleteFileBySong deleteLocalFile song name: " 
                                + song.getName() + " id: " + song.getId());
                        file.delete();
                    }
                }
                
                if (!TextUtils.isEmpty(subtitlePath)) {
                    file = new File(subtitlePath);
                    if (file.isFile() && file.exists()) {
                        EvLog.i("deleteFileBySong deleteSubtitle song name: " 
                                + song.getName() + " id: " + song.getId());
                        file.delete();
                    }
                }
            }
        }
    }
    
    /**
     * [功能说明]根据歌曲id删除文件
     * @param songId 歌曲id
     */
    public static void deleteFileBySongId(int songId) {
        File file = null;
        List<Media> medias = SongManager.getInstance().getMediaList(songId);
        if (medias != null && !medias.isEmpty()) {
            for (Media m : medias) {
                // 删除歌曲和字幕文件
                String localPath = m.getLocalFilePath();
                String subtitlePath = m.getLocalSubtitlePath();

                if (!TextUtils.isEmpty(localPath)) {
                    file = new File(localPath);
                    if (file.isFile() && file.exists()) {
                        EvLog.i("deleteFileBySong deleteLocalFile song name: " 
                                + " id: " + songId);
                        file.delete();
                    }
                }
                
                if (!TextUtils.isEmpty(subtitlePath)) {
                    file = new File(subtitlePath);
                    if (file.isFile() && file.exists()) {
                        EvLog.i("deleteFileBySong deleteSubtitle song name: " 
                                + " id: " + songId);
                        file.delete();
                    }
                }
            }
        }
    }
    
    /**
     * [功能说明]id数组中是否包含制定的songId
     * @param songId 歌曲id
     * @param ids id数组
     * @return
     */
    public static boolean containsId(int songId, List<Integer> ids) {
        if (ids == null) {
            return false;
        }
        for (int id : ids) {
            if (id == songId) {
                return true;
            }
        }
        return false;
    }

}
