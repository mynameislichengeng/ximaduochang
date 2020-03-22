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

package com.evideo.kmbox.util;

import java.util.Collections;
import java.util.List;

import com.evideo.kmbox.model.dao.data.Media;
import com.evideo.kmbox.model.dao.data.Song;

/**
 * [功能说明]
 */
public class MediaUtil {
    public static  Media getVideoMediaFromSong(/*List<Media> list*/Song song) {
        if (song == null) {
            EvLog.e("getVideoMediaFromSong song is null");
            return null;
        }
        
        Media defaultMedia = null;
        List<Media> list = song.getMedia();
        
        Collections.reverse(list);//倒序排列
        
//        EvLog.e("getVideoMediaFromSong media list size= " + list.size());
        for (Media media : list) {
            if (!media.isVideo()) {
                continue;
            }

            if (defaultMedia == null) {
                defaultMedia = media;
            }
            
            if (media.isLocalFileComplete()/*hasLocalFile()*/) {
                defaultMedia = media;
                break;
            }
        }
        return defaultMedia;
    }
}
