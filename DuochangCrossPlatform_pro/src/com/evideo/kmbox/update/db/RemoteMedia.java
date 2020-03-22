
package com.evideo.kmbox.update.db;

import com.evideo.kmbox.model.dao.data.Media;

public class RemoteMedia {
    
    private int mID;
    private int mSongID;
    private String mSongFileName;
    private int mDefaultVolume;
    private int mOriginalTrack;
    private int mAccompanyTrack;
    private String mMediaType;
    private String mSongNameWordType;
    private int mVolumeBalance;
    private String mVolumeQuality;
    private String mImageQuality;
    private String mSongVersion;
    private String mTotalQuality;
    private int mPrice;
    private String mMd5Value;
    private int mIsTorrentExpired;
    private String mUpdateDateTime;
    
    public RemoteMedia(int id,int songid, String songfileName, int defaultVolume, int originalTrack,
            int accompanyTrack, String mediaType, String songNameWord, int volumeBalance,
            String volumeQuality, String imageQuality, String songVersion, String totalQuality,
            int price, String md5Value, int isTorrentExpired, String updateTime) {
        this.mID = id;
        this.mSongID = songid;
        this.mSongFileName = songfileName;
        this.mDefaultVolume = defaultVolume;
        this.mOriginalTrack = originalTrack;
        this.mAccompanyTrack = accompanyTrack;
        this.mMediaType = mediaType;
        this.mSongNameWordType = songNameWord;
        this.mVolumeBalance = volumeBalance;
        this.mVolumeQuality = volumeQuality;
        this.mImageQuality = imageQuality;
        this.mSongVersion = songVersion;
        this.mTotalQuality = totalQuality;
        this.mPrice = price;
        this.mMd5Value = md5Value;
        this.mIsTorrentExpired = isTorrentExpired;
        this.mUpdateDateTime = updateTime;
    }
    
    public Media generateMeida() {
        Media media = new Media(Media.Invalid_Id, 1, mSongID, mSongFileName, "", mOriginalTrack - 1, 
                mAccompanyTrack - 1, mDefaultVolume, "", ""); 
        return media;
    }

    public int getID() {
        return mID;
    }

    public void setID(int mID) {
        this.mID = mID;
    }

    public int getSongID() {
        return mSongID;
    }

    public void setSongID(int mSongID) {
        this.mSongID = mSongID;
    }

    public String getSongFileName() {
        return mSongFileName;
    }

    public void setSongFileName(String mSongFileName) {
        this.mSongFileName = mSongFileName;
    }

    public int getDefaultVolume() {
        return mDefaultVolume;
    }

    public void setDefaultVolume(int mDefaultVolume) {
        this.mDefaultVolume = mDefaultVolume;
    }

    public int getOriginalTrack() {
        return mOriginalTrack;
    }

    public void setOriginalTrack(int mOriginalTrack) {
        this.mOriginalTrack = mOriginalTrack;
    }

    public int getAccompanyTrack() {
        return mAccompanyTrack;
    }

    public void setAccompanyTrack(int mAccompanyTrack) {
        this.mAccompanyTrack = mAccompanyTrack;
    }

    public String getMediaType() {
        return mMediaType;
    }

    public void setMediaType(String mMediaType) {
        this.mMediaType = mMediaType;
    }

    public String getSongNameWordType() {
        return mSongNameWordType;
    }

    public void setSongNameWordType(String mSongNameWordType) {
        this.mSongNameWordType = mSongNameWordType;
    }

    public int getVolumeBalance() {
        return mVolumeBalance;
    }

    public void setVolumeBalance(int mVolumeBalance) {
        this.mVolumeBalance = mVolumeBalance;
    }

    public String getVolumeQuality() {
        return mVolumeQuality;
    }

    public void setVolumeQuality(String mVolumeQuality) {
        this.mVolumeQuality = mVolumeQuality;
    }

    public String getImageQuality() {
        return mImageQuality;
    }

    public void setImageQuality(String mImageQuality) {
        this.mImageQuality = mImageQuality;
    }

    public String getSongVersion() {
        return mSongVersion;
    }

    public void setSongVersion(String mSongVersion) {
        this.mSongVersion = mSongVersion;
    }

    public String getTotalQuality() {
        return mTotalQuality;
    }

    public void setTotalQuality(String mTotalQuality) {
        this.mTotalQuality = mTotalQuality;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int mPrice) {
        this.mPrice = mPrice;
    }

    public String getMd5Value() {
        return mMd5Value;
    }

    public void setMd5Value(String mMd5Value) {
        this.mMd5Value = mMd5Value;
    }

    public int getIsTorrentExpired() {
        return mIsTorrentExpired;
    }

    public void setIsTorrentExpired(int mIsTorrentExpired) {
        this.mIsTorrentExpired = mIsTorrentExpired;
    }

    public String getUpdateDateTime() {
        return mUpdateDateTime;
    }

    public void setUpdateDateTime(String mUpdateDateTime) {
        this.mUpdateDateTime = mUpdateDateTime;
    }

}
