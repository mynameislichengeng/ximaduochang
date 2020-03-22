package com.evideo.kmbox.model.storage;

import com.evideo.kmbox.model.dao.data.Media;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.StorageManager;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrl;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.util.EvLog;

public class CacheManagerUtil {
    private static CacheManagerUtil instance = null;
    
    public static CacheManagerUtil getInstance() {
        if(instance == null) {
            instance = new CacheManagerUtil();
        }
        return instance;
    }
    
    
    // get mdeia id
//    public long getPlayingSongMediaId(Song song) {
//        Media video = KmPlayListItem.getVideoMedia(song);
//        if ( video == null ) {
//            return -1;
//        }
//        long videoId = video.getId();
//        return videoId;
//    }
    
    public void lockCurrentPlayingSong(){
        KmPlayListItem item = KmPlayerCtrl.getInstance().getPlayingSong();
        if (item == null) {
            return;
        }
        
//        Song song = item.getSong();
//        long mediaId = getPlayingSongMediaId(song);
        Media video = item.getVideoMedia();
        if ( video == null ) {
            return;
        }
        CacheManager.getInstance().lockResource(video.getId());
    }
    
    public void unlockResourceExceptPlayingSong() {
        KmPlayListItem item = KmPlayerCtrl.getInstance().getPlayingSong();
        if (item == null) {
            return;
        }
        
        Media video = item.getVideoMedia();
        if ( video == null ) {
            return;
        }

        CacheManager.getInstance().unlockResourceExcept(video.getId());
    }
    
    public void unlockCurrentPlayingSong(){
        KmPlayListItem item = KmPlayerCtrl.getInstance().getPlayingSong();
        if (item == null) {
            return;
        }

//        Song song = item.getSong();
//        long mediaId = getPlayingSongMediaId(song);
        Media video = item.getVideoMedia();
        if ( video == null ) {
            return;
        }
        CacheManager.getInstance().unlockResource(video.getId());
//        CacheManager.getInstance().unlockResource(mediaId);
    }
}
