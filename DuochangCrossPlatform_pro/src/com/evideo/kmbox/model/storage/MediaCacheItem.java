package com.evideo.kmbox.model.storage;

public class MediaCacheItem {
    public static final int Type_Unknown = -1;
    public static final int Type_Media = 0;
    public static final int Type_Subtitle = 1;
    public static final int Type_Album = 2;
    public static final int Type_SingerPicture = 3;

    private int mId;
    private int mMediaId;
    private String mFullFilePath;
    private int mReference;
    private int mType;
    private int mPriority;
    
    public MediaCacheItem(int id, int mediaId, String fullFilePath, int reference, int type, int priority) {
        mId = id;
        mMediaId = mediaId;
        mFullFilePath = fullFilePath;
        mReference = reference;
        mType = type;
        mPriority = priority;
    }
    
    public int getId() {
        return mId;
    }
    
    public int getMediaId() {
        return mMediaId;
    }
    
    public String getFullFilePath() {
        return mFullFilePath;
    }
    
    public long getReference() {
        return mReference;
    }
    
    public int getType() {
        return mType;
    }
    
    public int getPriority(){
        return mPriority;
    }
}
