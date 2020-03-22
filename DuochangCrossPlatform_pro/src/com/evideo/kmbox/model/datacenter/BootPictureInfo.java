package com.evideo.kmbox.model.datacenter;

import android.util.Log;

public class BootPictureInfo {

    private String mKey = "";
    private String mVersion = "";
    private long mDuration = 0;
    private String mRemoteVersion = "";
    private String mUrl = "";
    
    public BootPictureInfo(String key, String remoteVersion, String url, long duration) {
        mKey = key;
        mVersion = remoteVersion;
        mUrl = url;
        mDuration = duration;
        Log.e("chenqm", mKey + "/" + mVersion + "/" + mUrl + "/" + mDuration);
    }
    
    public String getKey() {
        return mKey;
    }
    
    public String getVersion() {
        return mVersion;
    }
    
    public String getRemoteVersion() {
        return mRemoteVersion;
    }
    
    public String getUrl() {
        return mUrl;
    }
    
    public long getDuration() {
        return mDuration;
    }

}
