package com.evideo.kmbox.model.datacenter;

public class ResourceInfo {
    private String mVersion = "";
    private String mUrl = "";
    private int mErrorCode;
    
    public ResourceInfo(String version, String url,int errorCode) {
        mVersion = version;
        mUrl = url;
        mErrorCode = errorCode;
    }
    
    public String getVersion() {
        return mVersion;
    }
    
    public String getUrl() {
        return mUrl;
    }
    
    public int getErrorCode() {
        return mErrorCode;
    }
}
