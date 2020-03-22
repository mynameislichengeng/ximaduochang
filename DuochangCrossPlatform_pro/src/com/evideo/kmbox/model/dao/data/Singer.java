package com.evideo.kmbox.model.dao.data;

import java.util.List;

import com.evideo.kmbox.dao.ConfigDAO;
import com.evideo.kmbox.dao.DAOFactory;

public class Singer {
    
    public static final int Gender_Unknown = 0;

    private int mId;
    private String mName;
    private String mSpell;
    private int mGender;
    private boolean mGroup;
    private int mType;
    private int mCountry;
    private int mPlayRate;
    private String mTimeStamp = "";
    private String mPictureResourceId = "";

    public Singer(int id, String name, String spell, int gender, boolean group, int type, int country,
            int playRate, String timeStamp, String pictureResourceId) {
        mId = id;
        mName = name;
        mSpell = spell;
        mGender = gender;
        mGroup = group;
        mType = type;
        mCountry = country;
        mPlayRate = playRate;
        mTimeStamp = timeStamp;
        mPictureResourceId = pictureResourceId;
    }
    
    public Singer(int id, String name, String spell, int type, int playRate) {
        this(id, name, spell, Gender_Unknown, false, type, playRate);
    }
    
    public Singer(int id, String name, String spell, int gender, boolean group, int type, int playRate) {
        mId = id;
        mName = name;
        mSpell = spell;
        mGender = gender;
        mGroup = group;
        mType = type;
        mPlayRate = playRate;
    }
    
    public int getId() {
        return mId;
    }
    
    public void setId(int id) {
        this.mId = id;
    }
    
    public String getName() {
        return mName;
    }
    
    public void setName(String name) {
        this.mName = name;
    }
    
    public String getSpell() {
        return mSpell;
    }
    
    public void setSpell(String spell) {
        this.mSpell = spell;
    }
    
    public int getPlayRate() {
        return mPlayRate;
    }
    
    public void setGender(int gender) {
        mGender = gender;
    }
    
    public int getGender() {
        return mGender;
    }
    
    public void setGroup(boolean group) {
        mGroup = group;
    }

    public boolean isGroup() {
        return mGroup;
    }
    
    public int getType() {
        return mType;
    }
    
    public void setType(int type) {
        this.mType = type;
    }
    
    public int getCountry() {
        return mCountry;
    }
    
    public void setCountry(int country) {
        this.mCountry = country;
    }
    
    public String getTimeStamp() {
        return mTimeStamp;
    }
    
    public String getPictureResourceId() {
        return mPictureResourceId;
    }
    
    public void setPictureResourceId(String resId) {
        mPictureResourceId = resId;
    }
    
    public String getPictureURI() {
        if (mPictureResourceId == null
                || mPictureResourceId.isEmpty()
                || mPictureResourceId.equals("0")
                || mPictureResourceId.equals("-1")) {
                return null;
            }
            
            ConfigDAO dao = DAOFactory.getInstance().getConfigDAO();
            return dao.getResourceUrl() + mPictureResourceId;
    }
    
    public static List<Song> getSongs() {
        return null;
    }
    
    public static List<Singer> getSingerBySpell(String spell, PageInfo pageInfo) {
        return null;
    }
    
    public static int getSingerCountBySpell(String spell) {
        return 0;
    }
}
