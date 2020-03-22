package com.evideo.kmbox.model.songinfo;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import android.text.TextUtils;

import com.evideo.kmbox.model.dao.data.Media;
import com.evideo.kmbox.model.dao.data.MediaManager;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.dao.data.StorageManager;
import com.evideo.kmbox.model.dao.data.StorageVolume;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;
import com.evideo.kmbox.model.songinfo.SongDataState;
import com.evideo.kmbox.model.storage.CacheManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;
import com.evideo.kmbox.util.MediaUtil;


public class KmPlayListItem implements Serializable{    
    
    private static final long serialVersionUID = 538770603254937028L;
    private int serialNum ;    //流水号
    private int songCategory;
    private int score;//评分分数，已唱列表需要
    
    private String customerid; //会员ID
    private String mAudioPath;//音频路径，主要用于录音回放
    private SongInfoInDB mSongInfo;//歌曲信息
    
    private long mDownId = 0;

    private int mDuration = 0;

    private boolean mErcDownFinish = false;

    /**
     * [数据状态]
     */
    private int mDataState = SongDataState.STATE_NONE;

    public void setDownId(long id) {
        this.mDownId = id;
    }
    
    public long getDownId() {
        return this.mDownId;
    }

    public boolean isErcDownFinish() {
        return mErcDownFinish;
    }

    public void setErcDownFinish(boolean mErcDownFinish) {
        this.mErcDownFinish = mErcDownFinish;
    }

    public void setDataState(int dataState) {
        if (dataState < SongDataState.STATE_NONE || dataState > SongDataState.STATE_ERROR) {
            EvLog.e("setDataState invalid value=" + dataState);
            return;
        }
        this.mDataState = dataState;
    }
    
    public int getDataState() {
        return this.mDataState;
    }
    
    //test
    public void updateMediaList(int songId) {
//        SongManager.getInstance().getMediaList(songId);
        Song song = SongManager.getInstance().getSongById(songId);
        if (song == null) {
            EvLog.e("updateMediaList song is null");
            UmengAgentUtil.reportError(songId + ", updateMediaList but song is null");
            return;
        }
        this.mSongInfo.video = MediaUtil.getVideoMediaFromSong(song);
    }
    
    /**
     * @brief : [获取封面URL]
     * @return
     */
    public String getAlbumUrl() {
        return this.mSongInfo.albumUrl;
    }
    
    /**
     * @brief : [获取歌手信息]
     * @return
     */
    public String getSingerName() {
        return this.mSongInfo.singerName;
    }
    
    
    public Media getVideoMedia() {
        return this.mSongInfo.video;
    }
    
    /**
     * @brief : [获取歌名]
     * @return
     */
    public String getSongName() {
        return this.mSongInfo.songName;
    }

    
    public int getSongId() {
        return this.mSongInfo.songID;
    }
    
    public boolean isCanScoreInDB() {
        return this.mSongInfo.isCanScoreInDB;
    }
    
    public String getVideoPath() {
        Media video = this.mSongInfo.video;
        
        if( video == null ) {
            return null;
        }
        
        return video.getLocalFilePath();
    }
    
    public String getSubtitlePath() {
        if ( this.mSongInfo.video == null ) {
            return null;
        }
        
        return this.mSongInfo.video.getLocalSubtitlePath();
    }
    
    public void setSubtitlePath(String subtitlePath) {
//        this.mSubtitlePath = subtitlePath;
        Media video = this.mSongInfo.video;
        
        if( video == null ) {
            return;
        }
        video.setLocalSubtitlePath(subtitlePath);
    }
    
    public void setVideoPath(String videoPath) {
        if(this.mSongInfo.video != null ) {
            this.mSongInfo.video.setLocalFilePath(videoPath);
        } 
    }
    
    public void emptyMedia() {
        if (this.mSongInfo.video == null) {
            return;
        }
        this.mSongInfo.video.clearLocalPath();
    }
    
    public void checkSuffix() {
        if (this.mSongInfo.video == null) {
            return;
        }
        boolean needUpdate = false;
        
        if (isMediaAvailable()){
            EvLog.d("video source is local complete,check whether need to rename");
            String source = this.mSongInfo.video.getLocalFilePath();
            if (!TextUtils.isEmpty(source) && source.endsWith(ResourceSaverPathManager.FILE_TMP_SUFFIX)) {
                String destName = source.substring(0, source.length()-ResourceSaverPathManager.FILE_TMP_SUFFIX.length());
                if (FileUtil.renameFileWithPath(source,destName)) {
                    EvLog.i( this.mSongInfo.songName + " rename video path:" + destName);
//                    this.mVideoPath = destName;
                    this.mSongInfo.video.setLocalFilePath(destName);
                    needUpdate = true;
                    String uuid = this.mSongInfo.video.getVolumeUUID();
                    if (!TextUtils.isEmpty(uuid)) {
                        StorageVolume volume = StorageManager.getInstance().getVolume(uuid);
                        StorageManager.getInstance().updateStorageVolumeSize(volume);
                    }
                    //同步更新media路径到已点列表中
                    KmPlayListItem itemInPlayList = PlayListManager.getInstance().getItemBySerialNum(this.serialNum);
                    if (itemInPlayList != null) {
                        EvLog.e(itemInPlayList.getSongName() + " is in playlist,update media in checkSuffix fun");
                        itemInPlayList.updateMedia(this.mSongInfo.video);
                    }
                    CacheManager.getInstance().updateMediaCache(this.mSongInfo.video.getId(), destName);
                } else {
                    EvLog.e("video source rename failed");
                }
            }
        }
        
        if (needUpdate) {
            updateResourceToDB();
        }
    }
    
    public void updateResourceToDB() {
        if ( this.mSongInfo.video == null ) {
            EvLog.e( " updateResourceToDB failed" );
            return;
        }
        EvLog.d(this.mSongInfo.songName + ",update mediaId=" + this.mSongInfo.video.getId());
        MediaManager.getInstance().update(this.mSongInfo.video);
        this.mSongInfo.video = MediaManager.getInstance().getMedia(mSongInfo.video.getId());
    }
    
    
    public void updateMedia(Media media) {
        this.mSongInfo.video = media;
        if (this.mSongInfo.video.isLocalFileComplete()) {
        }
    }
    
    public Song getSong() {
        return SongManager.getInstance().getSongById(this.mSongInfo.songID);
    }
    
    public KmPlayListItem(Song song,int serialNum, String customerid,int songCategory) throws IllegalArgumentException {
        if ( song ==null ) {
            throw new IllegalArgumentException("song is null ,can not construct class KmPlayListItem ");
        }
        
        this.mSongInfo = new SongInfoInDB();
        this.mSongInfo.initParamFromDB(song);
        this.serialNum = serialNum;
        this.customerid = customerid;
        this.songCategory = songCategory;
        this.score = 0;
        this.mAudioPath = ""; 
        if (this.mSongInfo.video!= null) {
            if (isResourceComplete()) {
                mDataState = SongDataState.STATE_COMPLETE;
            }
        }
        mDownId = -1;
        mDuration = 0;
    }
    
    public KmPlayListItem() {
        this.mSongInfo = null;
        this.serialNum = -1;
        this.customerid = "";
        this.songCategory = SongCategory.CATEGORY_NONE;
        this.score = 0;
        this.mAudioPath = ""; 
        mDataState = SongDataState.STATE_NONE;
        mDownId = -1;
        mDuration = 0;
    }
    
    public KmPlayListItem(KmPlayListItem other) throws IllegalArgumentException {
        if ( other == null ) {
            throw new IllegalArgumentException("other is null ,can not construct class KmPlayListItem ");
        }
        copy(other);
    }
    
    public void copy(KmPlayListItem other) throws IllegalArgumentException {
        if ( other == null ) {
            throw new IllegalArgumentException("other is null ,can not copy ");
        }
        this.mSongInfo = new SongInfoInDB();
        this.mSongInfo.initParam(other.mSongInfo);
        this.serialNum = other.serialNum;
        this.customerid = other.customerid;
        this.songCategory = other.songCategory;
        this.score = other.score;
        this.mAudioPath = other.mAudioPath;
        this.mDataState = other.mDataState;
        this.mDownId = other.mDownId;
        this.mDuration = other.mDuration;
    }
    
    public int getDuration() {
        return this.mDuration;
    }
    
    public void setDuration(int duration) {
        this.mDuration = duration;
    }
    
    public void setAudioPath(String audioPath) {
        this.mAudioPath = audioPath;
    }
    
    public String getAudioPath() {
        return this.mAudioPath;
    }
    public int getSerialNum() {
        return serialNum;
    }
    public void setSerialNum(int serialNum) {
        this.serialNum = serialNum;
    }
    public String getCustomerid() {
        return customerid;
    }
    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }
    
    public int getSongCategory() {
        return this.songCategory;
    }
    
    public void setScoreValue(int scoreVal) {
        this.score = scoreVal;
    }
    
    public int getScoreValue() {
        return this.score;
    }
    
    public boolean isSongCanScore() {
    
        boolean canScore = this.mSongInfo.isCanScoreInDB;
        
        if ( !canScore ) {
            EvLog.w( this.mSongInfo.songName + " can not score in db" );
            return false;
        }
        
        Media video = this.mSongInfo.video;
        if ( video == null ) {
            EvLog.w( this.mSongInfo.songName +  " video media is null " );
            return false;
        }
        
        String erc = this.getSubtitlePath();
        
        if (TextUtils.isEmpty(erc)) {
            EvLog.w(this.mSongInfo.songName +  " erc path is null");
            return false;
        }
        
        if (!FileUtil.isFileExist(erc)) {
            EvLog.w(this.mSongInfo.songName +  " erc file local not exist,erc=" + erc );
            return false;
        }
        
        return true;
    }
    
     public boolean isSubtitleAvailable() {
         //本身不可评分,则返回true
         if ( !this.mSongInfo.isCanScoreInDB )
             return true;
         
         Media video = this.mSongInfo.video;
         if ( video == null ) {
             EvLog.e( this.mSongInfo.songName + " video media null ");
             return false;
         }
         
         boolean available = video.hasLocalSubtitle();
         if (!available) {
             EvLog.d(this.mSongInfo.songName + " Subtitle file not exist local by db");
         } else {
         }
         return available;
     }
     
     //待删
     public boolean isMediaAvailable() {
        Media video = this.mSongInfo.video;
        
        if (video == null) {
            EvLog.d(this.mSongInfo.songName + " media video is null");
            return false;
        }
        
        boolean available = video.isLocalFileComplete()/*hasLocalFile()*/;
        /*if (!available) {
            EvLog.d(this.mSongInfo.songName + " media file not exist local by db," + video.getId());
        } else {
        }*/
        return available;
    }
     
     public boolean isResourceComplete() {
         return isMediaAvailable() && isSubtitleAvailable();
     }
    
   
}
