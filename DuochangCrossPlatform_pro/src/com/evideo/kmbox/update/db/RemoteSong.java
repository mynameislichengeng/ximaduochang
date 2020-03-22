
package com.evideo.kmbox.update.db;

import com.evideo.kmbox.model.dao.data.Song;

public class RemoteSong {
    
    private int mId;
    private String mName;
    private String mSpell;
    private String mSongWord;
    private int mLanguageTypeID;
    private String mSingerName;
    private int[] mSingerId;
    private int[] mSongTypeID;
    private int mLanguageTypeID2;
    private int mLanguageTypeID3;
    private int mLanguageTypeID4;
    private int mPlayNum;
    private int mIsGrand;
    private int mIsShow;
    private String mLastUpdateTime;
    private String mAlbum;
    private String mErcVersion;
    private int mHasRemote;
    
    public RemoteSong(int id, String name, String spell, String songword,
            int languageTypeID, String singerName, int[] singerID, int[] songTypeID,
            int languageTypeID2, int languageTypeID3, int languageTypeID4,
            int playNum, int isGrand, int isShow, String lastUpdateTime, String album, String ercVersion,
            int hasRemote) {
        this.mId = id;
        this.mName = name;
        this.mSpell = spell;
        this.mSongWord = songword;
        this.mLanguageTypeID = languageTypeID;
        this.mSingerName = singerName;
        this.mSingerId = singerID;
        this.mSongTypeID = songTypeID;
        this.mLanguageTypeID2 = languageTypeID2;
        this.mLanguageTypeID3 = languageTypeID3;
        this.mLanguageTypeID4 = languageTypeID4;
        this.mPlayNum = playNum;
        this.mIsGrand = isGrand;
        this.mIsShow = isShow;
        this.mLastUpdateTime = lastUpdateTime;
        this.mAlbum = album;
        this.mErcVersion = ercVersion;
        this.mHasRemote = hasRemote;
    }
    
    public Song generateSong() {
        Song song = new Song(mId, mName, mSpell, mSingerName, mSingerId, mLanguageTypeID, mSongTypeID[0], mPlayNum, 
                "", "", mAlbum, mIsGrand == 1, 0);
        song.setHasRemoteFile(mHasRemote);
        return song;
    }

    public int getmId() {
        return mId;
    }

    public String getmName() {
        return mName;
    }

    public String getmSpell() {
        return mSpell;
    }

    public String getmSongWord() {
        return mSongWord;
    }

    public String getmSingerName() {
        return mSingerName;
    }

    public int[] getmSingerId() {
        return mSingerId;
    }
    
    public void setSingerId(int[] singerId) {
        mSingerId = singerId;
    }

    public int[] getmSongTypeID() {
        return mSongTypeID;
    }
    
    public void setSongTypeID(int[] songTypeID) {
        mSongTypeID = songTypeID;
    }

    public int getmPlayNum() {
        return mPlayNum;
    }

    public int getmIsGrand() {
        return mIsGrand;
    }

    public int getmIsShow() {
        return mIsShow;
    }

    public String getmLastUpdateTime() {
        return mLastUpdateTime;
    }

    public int getLanguageTypeID() {
        return mLanguageTypeID;
    }

    public void setLanguageTypeID(int mLanguageTypeID) {
        this.mLanguageTypeID = mLanguageTypeID;
    }

    public int getLanguageTypeID2() {
        return mLanguageTypeID2;
    }

    public void setLanguageTypeID2(int mLanguageTypeID2) {
        this.mLanguageTypeID2 = mLanguageTypeID2;
    }

    public int getLanguageTypeID3() {
        return mLanguageTypeID3;
    }

    public void setLanguageTypeID3(int mLanguageTypeID3) {
        this.mLanguageTypeID3 = mLanguageTypeID3;
    }

    public int getLanguageTypeID4() {
        return mLanguageTypeID4;
    }

    public void setLanguageTypeID4(int mLanguageTypeID4) {
        this.mLanguageTypeID4 = mLanguageTypeID4;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    public String getErcVersion() {
        return mErcVersion;
    }

    public void setErcVersion(String mErcVersion) {
        this.mErcVersion = mErcVersion;
    }

    public int getHasRemote() {
        return mHasRemote;
    }

    public void setHasRemote(int hasRemote) {
        this.mHasRemote = hasRemote;
    }

}
