package com.evideo.kmbox.model.dao.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.MediaDAO;
import com.evideo.kmbox.dao.SongDAO;
import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.device.DeviceConfigManager;

/**
 * [功能说明] 歌曲
 */
public class Song {
    private int mId;
    private String mName;
    private String mSpell;
    private String mSingerDescription;
    private int[] mSingerId;
    private int mLanguage;
    private int mType;
    private int mPlayRate;
    private String mAlbum;
    private boolean mCanScore;
    private int mUsageRate;
    private String mSubtitle;
    private String mTimeStamp;
    private int mHasRemoteFile = 0;
    private int mHasLocal = 0;
   
    public Song(int id, String name, String spell, String singerDescription, int[] singerIds, 
            int language, int type, int playRate, String album, boolean canScore) {
        this.mId = id;
        this.mName = name;
        this.mSpell = spell;
        this.mSingerDescription = singerDescription;
        this.mSingerId = singerIds;
        this.mLanguage = language;
        this.mType = type;
        this.mPlayRate = playRate;
        this.mAlbum = album;
        this.mCanScore = canScore;
    }
    
    public Song(int id, String name, String spell, String singerDescription,
            int[] singerIds, int language, int type, int playRate,
            String subtitle, String timeStamp, String album, boolean canScore) {
        this.mId = id;
        this.mName = name;
        this.mSpell = spell;
        this.mSingerDescription = singerDescription;
        this.mSingerId = singerIds;
        this.mLanguage = language;
        this.mType = type;
        this.mPlayRate = playRate;
        this.mSubtitle = subtitle;
        this.mTimeStamp = timeStamp;
        this.mAlbum = album;
        this.mCanScore = canScore;
    }

    public Song(int id, String name, String spell, String singerDescription,
            int[] singerIds, int language, int type, int playRate) {
        this(id, name, spell, singerDescription, singerIds, language, type,
                playRate, null, null, null, false, 0);
    }

    public Song(int id, String name, String singerDescription, int[] singerIds,
            int language, int type, int playRate, String subtitle,
            String album, boolean canScore, int usageRate) {
        this.mId = id;
        this.mName = name;
        this.mSingerDescription = singerDescription;
        this.mSingerId = singerIds;
        this.mPlayRate = playRate;
        this.mSubtitle = subtitle;
        this.mAlbum = album;
        this.mCanScore = canScore;
        this.mUsageRate = usageRate;
    }

    public Song(int id, String name, String spell, String singerDescription,
            int[] singerIds, int language, int type, int playRate,
            String subtitle, String subtitleLocalFile, String album,
            boolean canScore, int usageRate) {
        this.mId = id;
        this.mName = name;
        this.mSpell = spell;
        this.mSingerDescription = singerDescription;
        this.mSingerId = singerIds;
        this.mPlayRate = playRate;
        this.mSubtitle = subtitle;
        // this.mSubtitleLocalFile = subtitleLocalFile;
        this.mAlbum = album;
        this.mCanScore = canScore;
        this.mUsageRate = usageRate;
    }

    /**
     * [功能说明] 获取歌曲id
     * @return  歌曲id
     */
    public int getId() {
        return this.mId;
    }
    
    public void setId(int id) {
        this.mId = id;
    }
    
    public static boolean isCanScore(int isGrand) {
        return isGrand == 0;
    } 

    /**
     * [功能说明] 获取歌曲名称
     * @return  歌曲名称
     */
    public String getName() {
        return this.mName;
    }
    
    /**
     * [功能说明] 获取歌星描述
     * @return  歌星描述
     */
    public String getSingerDescription() {
        return this.mSingerDescription;
    }
    
    /**
     * [功能说明] 获取歌曲点播率
     * @return  点播率
     */
    public int getPlayRate() {
        return this.mPlayRate;
    }
    
    /**
     * [功能说明] 获取歌曲首拼
     * @return  歌曲首拼
     */
    public String getSpell() {
        return mSpell;
    }
    
    /**
     * [功能说明] 获取歌曲演唱者id
     * @param index 索引，最多支持4个演唱者
     * @return  演唱者id
     */
    public int getSingerId(int index) {
        if (index >= 0 && index < mSingerId.length) {
            return mSingerId[index];
        }
        
        return -1;
    }
    
    public void setSingerId(int[] singerIds) {
        this.mSingerId = singerIds;
    }
    
    /**
     * [功能说明] 获取歌曲语言类型
     * @return  语言类型
     */
    public int getLanguage() {
        return mLanguage;
    }
    
    /**
     * [功能说明] 获取歌曲类别
     * @return  歌曲类别
     */
    public int getType() {
        return mType;
    }
    
    /**
     * [功能说明] 获取歌曲媒体文件
     * @return  媒体文件
     */
    public List<Media> getMedia() {
        SongDAO dao = DAOFactory.getInstance().getSongDAO();
        return dao.getMedia(this);
    }
    
    /**
     * [功能说明] 获取歌曲专辑封面URL
     * @return  专辑封面URL
     */
    public String getAlbumURI() {
        if (mAlbum == null
            || mAlbum.isEmpty()
            || mAlbum.equals("0")
            || mAlbum.equals("-1")) {
            return null;
        }

        return DeviceConfigManager.getInstance().getPicurhead() + "?fileid=" + mAlbum;
    }

    /**
     * [功能说明] 获取专辑封面远程资源id
     * @return  专辑封面远程资源id
     */
    public String getAlbumResource() {
        return mAlbum;
    }

    /**
     * [功能说明] 获取歌曲是否可评分
     * @return  是否可评分标识
     */
    public boolean canScore() {
        return this.mCanScore;
    }

    /**
     * [功能说明] 设置评分标识
     * @param canScore  评分标识
     */
    public void setCanScore(boolean canScore) {
        mCanScore = canScore;
    }

    /**
     * [功能说明] 获取本地点播率
     * @return  点播率
     */
    public int getUsageRate() {
        return this.mUsageRate;
    }

    /**
     * [功能说明] 设置本地点播率
     * @param usageRate 点播率
     */
    public void setUsageRate(int usageRate) {
        /*SongDAO dao = DAOFactory.getInstance().getSongDAO();

        Song song = new Song(mId, mName, mSpell, mSingerDescription, mSingerId,
                mLanguage, mType, mPlayRate, mAlbum, mCanScore);*/

    }
    
    public String getSubtitleResource() {
        return mSubtitle;
    }
    
    public List<Integer> getSingeIdList() {
        List<Integer> singerList = new ArrayList<Integer>();
        for (int i = 0; i < 4; i++) {
            singerList.add(mSingerId[i]);
        }

        return singerList;
    }

    /**
     * [功能说明] 判断歌曲是否已有本地缓存文件
     * @return  如果本地已缓存则返回true，否则返回false
     */
    public boolean hasCachedMedia() {
        MediaDAO dao = DAOFactory.getInstance().getMediaDAO();
        List<Media> list = dao.getMedia(mId);
        for (int i = 0; i < list.size(); i++) {
        	if (list.get(i).isLocalFileComplete()/*hasLocalFile()*/) {
        		return true;
        	}
        }

        return false;
    }
    
    public void setHasRemoteFile(int hasRemote) {
        this.mHasRemoteFile = hasRemote;
    }
    
    public int getHasRemoteFile() {
        return this.mHasRemoteFile;
    }
    
    public void setHasLocal(int hasLocal) {
        this.mHasLocal = hasLocal;
    }
    
    public int getHasLocal() {
        return this.mHasLocal;
    }

    @Override
    public String toString() {
        return "Song [mId=" + mId + ", mName=" + mName + ", mSpell=" + mSpell
                + ", mSingerDescription=" + mSingerDescription + ", mSingerId="
                + Arrays.toString(mSingerId) + ", mLanguage=" + mLanguage
                + ", mType=" + mType + ", mPlayRate=" + mPlayRate
                + ", mAlbum=" + mAlbum
                + ", mCanScore=" + mCanScore
                + ", mUsageRate=" + mUsageRate + "]";
    }
    
}
