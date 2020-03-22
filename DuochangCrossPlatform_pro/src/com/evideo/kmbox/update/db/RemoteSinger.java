
package com.evideo.kmbox.update.db;

import com.evideo.kmbox.model.dao.data.Singer;

public class RemoteSinger {
    private int mSongsterID;
    private String mSongsterName;
    private String mSongsterPy;
    private String mSongsterLove;
    private int mSongsterTypeID;
    private int mSongsterOrderRank;
    private String mLastUpdateTime;
    private int mPicFileIDH;
    private int mPicFileIDL;
    private int mPicFileIDM;
    private int mPicFileIDS;
    private int mImitatePicFileID0;
    private int mImitatePicFileID1;
    private int mImitatePicFileID2;
    private String mPhotopath;
    private boolean mIsGroup;
    private int mGender;
    private int mCountry;
    
    public RemoteSinger(int singerID, String singerName, String singerPy, String singerLove,
            int singerTypeID, int singerOrderRank, String lastUpdateTime, int picH, int picL,
            int picM, int picS, int imitatePic0, int imitatePic1, int imitatePic2,
            String photoPath, boolean isGroup, int gender, int country) {
        this.mSongsterID = singerID;
        this.mSongsterName = singerName;
        this.mSongsterPy = singerPy;
        this.mSongsterLove = singerLove;
        this.mSongsterID = singerTypeID;
        this.mPhotopath = photoPath;
        this.mSongsterOrderRank = singerOrderRank;
        this.mLastUpdateTime = lastUpdateTime;
        this.mPicFileIDH = picH;
        this.mPicFileIDL = picL;
        this.mPicFileIDM = picM;
        this.mPicFileIDS = picS;
        this.mImitatePicFileID0 = imitatePic0;
        this.mImitatePicFileID1 = imitatePic1;
        this.mImitatePicFileID2 = imitatePic2;
        this.mPhotopath = photoPath;
        this.mIsGroup = isGroup;
        this.mGender = gender;
        this.mCountry = country;
    };
    
    /**
     * [由全曲库产生本地曲库歌手信息]
     * @return singer
     */
    public Singer generageSinger() {
        Singer singer = new Singer(mSongsterID, mSongsterName, mSongsterPy, mSongsterTypeID, mSongsterOrderRank);
        return singer;
    }

    public int getSongsterID() {
        return mSongsterID;
    }

    public String getSongsterName() {
        return mSongsterName;
    }

    public String getSongsterPy() {
        return mSongsterPy;
    }

    public String getSongsterLove() {
        return mSongsterLove;
    }

    public int getSongsterTypeID() {
        return mSongsterTypeID;
    }

    public int getSongsterOrderRank() {
        return mSongsterOrderRank;
    }

    public String getLastUpdateTime() {
        return mLastUpdateTime;
    }

    public int getPicFileIDH() {
        return mPicFileIDH;
    }

    public int getPicFileIDL() {
        return mPicFileIDL;
    }

    public int getPicFileIDM() {
        return mPicFileIDM;
    }

    public int getPicFileIDS() {
        return mPicFileIDS;
    }

    public int getImitatePicFileID0() {
        return mImitatePicFileID0;
    }

    public int getImitatePicFileID1() {
        return mImitatePicFileID1;
    }

    public int getImitatePicFileID2() {
        return mImitatePicFileID2;
    }
    
    public String getPhotopath() {
        return mPhotopath;
    }

    public void setPhotopath(String mPhotopath) {
        this.mPhotopath = mPhotopath;
    }

    public boolean getIsGroup() {
        return mIsGroup;
    }

    public void setIsGroup(boolean mIsGroup) {
        this.mIsGroup = mIsGroup;
    }

    public int getGender() {
        return mGender;
    }

    public void setGender(int mGender) {
        this.mGender = mGender;
    }

    public int getCountry() {
        return mCountry;
    }

    public void setCountry(int mCountry) {
        this.mCountry = mCountry;
    }
}
