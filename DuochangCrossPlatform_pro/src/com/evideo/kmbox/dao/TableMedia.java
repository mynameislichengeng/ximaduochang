package com.evideo.kmbox.dao;

public class TableMedia {
	public static final String NAME = "tblMedia";
	public enum Column {
        ID("id"), TYPE("type"), SONG_ID("songId"), SONG_NAME("songName"), RESOURCE_ID("resourceId"), 
        ORIGINAL_INFO("originalInfo"), COMPANY_INFO("companyInfo"), VOLUME("volume"), VOLUME_UUID("volumeUUID"), 
        SUBTITLE("subtitle"), LOCAL_SUBTITLE("localSubtitle"),
        LOCAL_RESOURCE("localResource"),RESOURCE_SIZE("resourceSize"),DURATION("duration");
        private String mName;
        
        private Column(String name) {
            mName = name;
        }
        
        public String getName() {
            return mName;
        }
    };
}
