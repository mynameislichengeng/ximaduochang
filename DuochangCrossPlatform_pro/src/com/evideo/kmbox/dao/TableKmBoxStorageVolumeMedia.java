package com.evideo.kmbox.dao;

public class TableKmBoxStorageVolumeMedia {
	public static final String NAME = "tblMedia";
	public enum Column {
        TYPE("type"), SONG_ID("songId"), ORIGINAL_INFO("originalInfo"), COMPANY_INFO("companyInfo"), 
        VOLUME("volume"), VOLUME_UUID("volumeUUID"), SUBTITLE("subtitle");
        private String mName;
        
        private Column(String name) {
            mName = name;
        }
        
        public String getName() {
            return mName;
        }
    };
}

