package com.evideo.kmbox.model.dao.data;

public class StorageVolumeMedia {
    private int mType;
    private int mSongId;
    private int mOriginalInfo;
    private int mCompanyInfo;
    private int mVolume;
    private String mUUID;
    private String mSubtitle;
    
    private StorageVolumeMedia(Builder builder) {
        mType = builder.mType;
        mSongId = builder.mSongId;
        mOriginalInfo = builder.mOriginalInfo;
        mCompanyInfo = builder.mCompanyInfo;
        mVolume = builder.mVolume;
        mUUID = builder.mUUID;
        mSubtitle = builder.mSubtitle;
    }
    
    public static StorageVolumeMedia fromMedia(Media media) {
    	return new StorageVolumeMedia.Builder()
			.type(media.getType())
			.songId(media.getSongId())
			.originalInfo(media.getOriginalTrack())
			.companyInfo(media.getCompanyTrack())
			.volume(media.getVolume())
			.uuid(media.getVolumeUUID())
			.subtitle(media.getLocalSubtitleName()).build();
    }
    
    public Media media() {
    	Media media = new Media(Media.Invalid_Id, mType, mSongId, "", "", mOriginalInfo, mCompanyInfo, mVolume, mUUID, "");
    	media.setLocalSubtitleName(mSubtitle);
    	return media;
    }
    
    public static class Builder {
    	private int mType;
    	private int mSongId;
    	private int mOriginalInfo;
    	private int mCompanyInfo;
    	private int mVolume;
    	private String mUUID;
    	private String mSubtitle;
    	
    	public Builder() {
    		
    	}

    	public Builder type(int type) {
    		mType = type;
    		return this;
    	}
    	
    	public Builder songId(int songId) {
    		mSongId = songId;
    		return this;
    	}
    	
    	public Builder originalInfo(int originalInfo) {
    		mOriginalInfo = originalInfo;
    		return this;
    	}
    	
    	public Builder companyInfo(int companyInfo) {
    		mCompanyInfo = companyInfo;
    		return this;
    	}
    	
    	public Builder volume(int volume) {
    		mVolume = volume;
    		return this;
    	}
    	
    	public Builder uuid(String uuid) {
    		mUUID = uuid;
    		return this;
    	}
    	
    	public Builder subtitle(String subtitle) {
    		mSubtitle = subtitle;
    		return this;
    	}
    	
    	public StorageVolumeMedia build() {
    		return new StorageVolumeMedia(this);
    	}
    }
}
