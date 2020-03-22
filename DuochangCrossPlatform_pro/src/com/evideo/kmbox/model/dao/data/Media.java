package com.evideo.kmbox.model.dao.data;

import android.text.TextUtils;

import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;

import java.io.File;

public class Media {
    
    public static final int Type_Unknown = -1;
    public static final int Type_AACX2 = 0;
    public static final int Type_MKV = 1;
    public static final int Type_TS = 2;
    public static final int Type_MPG = 3;
    public static final int Type_AVI = 4;
    public static final int Type_Max = Type_AVI + 1;
    
    public static final int Invalid_Id = 0;
    
    private int mId;
    private int mType;
    private int mSongId;
    private String mSongName;
    private String mResource;
    private int mOriginalInfo;
    private int mCompanyInfo;
    private int mVolume;
    private String mUUID;
    /** 本地media文件名 */
    private String mLocalFile;
    private String mSubtitle;
   
    /** 本地erc文件名 */
    private String mLocalSubtitle;
    private long mResourceSize;
    private int mDuration;
    
    /** [临时保存歌词文件路径,当uuid为空时,使用] */
    private String mTmpSubtitleFilePath = "";
    /** [临时保存本地media保存路径,当uuid为空时,使用] */
    private String mTmpLocalFilePath = "";
    
    public Media(int type, int songId, String songName, int originalInfo, 
            int companyInfo, int volume, String uuid, String subtitle) {
        
        this(Invalid_Id, type, songId, songName, "", originalInfo, companyInfo, volume, uuid, subtitle);
    }
    
    public Media(int id, int type, int songId, String songName, String resource, int originalInfo, 
            int companyInfo, int volume, String uuid, String subtitle,String localFile,int duration) {
        this(id,type,songId,songName,resource,originalInfo,companyInfo,volume,uuid,subtitle);
//        EvLog.d(songId + " new media mLocalFile set:" + localFile);
        mDuration = duration;
        mLocalFile = localFile;
    }
    
    public Media(int id, int type, int songId, String songName, String resource, int originalInfo, 
            int companyInfo, int volume, String uuid, String subtitle) {
        mId = id;
        mType = type;
        mSongId = songId;
        mSongName = songName;
        mResource = resource;
        mOriginalInfo = originalInfo;
        mCompanyInfo = companyInfo;
        mVolume = volume;
        mUUID = uuid;
        mSubtitle = subtitle;
        mLocalFile = "";
        mLocalSubtitle = "";
        mResourceSize = -1;
        mDuration = 0;
    }
    
    public Media(int id, int type, int songId, String songName, String resource, int originalInfo, 
            int companyInfo, int volume, String uuid) {
        mId = id;
        mType = type;
        mSongId = songId;
        mSongName = songName;
        mResource = resource;
        mOriginalInfo = originalInfo;
        mCompanyInfo = companyInfo;
        mVolume = volume;
        mUUID = uuid;
        mLocalFile = "";
        mLocalSubtitle = "";
        mResourceSize = -1;
        mDuration = 0;
    }
    
    public void setDuration(int duration) {
        mDuration = duration;
    }
    
    public int getDuration() {
        return mDuration;
    }
    
    public void setId(int id) {
        mId = id;
    }
    
    public int getId() {
        return mId;
    }
    
    public void setType(int type) {
        mType = type;
    }
    
    public void setUUID(String uuid) {
        mUUID = uuid;
    }
    
    public int getType() {
        return mType;
    }
    
    public String getResource() {
        return mResource;
    }
    
    public void setResource(String resource) {
        mResource = resource;
    }
    
    public String getURI() {
        return  mResource;
    }
    
    
    private String getMediaFileName() {
        String songIdStr = String.format("%08d", mSongId);
        String[] suffixList = { "aac", "mkv", "ts", "mpg", "avi" };
        String extension = suffixList[mType];

        if(mType == Media.Type_AACX2) {
            if (mOriginalInfo != -1) {
                extension += "y";
            } else if (mCompanyInfo != -1) {
                extension += "b";
            }
        }
        return songIdStr + "." + extension;
    }

    private boolean convertLocalFileName() {
        boolean ret = false;
        
        if (mLocalFile.contains("/")) {
            int endFlag = mLocalFile.lastIndexOf("/");
            if (endFlag > 0 && endFlag < (mLocalFile.length()-1)) {
                mLocalFile = mLocalFile.substring(endFlag+1);
                EvLog.e("getLocalFile mLocalFile=" + mLocalFile);
            } else {
                EvLog.e(mLocalFile + " is invalid,set localfile empty");
                //FIXME del file
                mLocalFile = "";
                mUUID = "";
            }
            ret = true;
        } 
        return ret;
    }
    /**
     * [功能说明] 获取本地media文件名
     * @return
     */
    public String getLocalFileName() {
        if (TextUtils.isEmpty(mLocalFile)) {
            mLocalFile = "";
            return mLocalFile;
        }
        
        if (convertLocalFileName()) {
            DAOFactory.getInstance().getMediaDAO().updateLocalResource(mId, mLocalFile, mUUID,mResourceSize); 
        }
        
        return mLocalFile;
    }
    /**
     * [功能说明] 获取media本地完整路径
     * @return
     */
    public String getLocalFilePath() {
        boolean needUpdateDB = false;
        String localFileFullPath = "";
        
        //uuid为空，文件不完整
        if (TextUtils.isEmpty(mUUID)) {
//            EvLog.d(mId + "mUUID is empty, mTmpLocalFilePath=" + mTmpLocalFilePath);
            localFileFullPath = mTmpLocalFilePath;
        } else {
            if (TextUtils.isEmpty(mLocalFile)) {
                mLocalFile = getMediaFileName();
                needUpdateDB = true;
            }
            
            //获取文件保存父目录
            String path = getPathByVolumeId(ResourceSaverPathManager.DIR_MEDIA);
            if (TextUtils.isEmpty(path)) {
                EvLog.e(mId + "getPathByVolumeId DIR_MEDIA failed");
                mLocalFile = "";
                return localFileFullPath;
            }
            //去掉路径信息
            if (convertLocalFileName()) {
                needUpdateDB = true;
            }
            
            //判断文件是否存在
            String mediaFullPath = FileUtil.concatPath(path,mLocalFile);
            if (FileUtil.isFileExist(mediaFullPath)) {
                localFileFullPath = mediaFullPath;
            } else {
                EvLog.e(mId + ",mLocalFile=" + mLocalFile + ",mediaFullPath=" + mediaFullPath + " is not exist,set localfile empty");
                mLocalFile = "";
                //FIXME ,uuid 非空，但localResource为空，如何处理
                if(!StorageManager.getInstance().isExistInDbByUUID(mUUID)) {
                    mUUID = "";
                }
//                mResourceSize = 0;
                needUpdateDB = true;
            }
        }
        
        if (needUpdateDB) {
            EvLog.d("getLocalFilePath need update media db ---" + mId);
            DAOFactory.getInstance().getMediaDAO().updateLocalResource(mId, mLocalFile, mUUID,mResourceSize);
        }
        return localFileFullPath;
    }
    
    /**
     * [功能说明] 判断本地media文件是否完整
     * @return
     */
    public boolean isLocalFileComplete() {
        if (mResourceSize > 0) {
            String mediaFullPath = getLocalFilePath();
            if (TextUtils.isEmpty(mediaFullPath)) {
                return false;
            }
            
            long size = 0;
            boolean isLocalEncrypted = false;;
            size = FileUtil.getFileSize(mediaFullPath);
            EvLog.i(mediaFullPath + "is not local encrypted,fileSize:" + size);

            if (size != mResourceSize) {
                EvLog.i(mediaFullPath + ":invalid size,resourceSize:" + mResourceSize + ", decryptedSize:" + size);
                return false;
            }
            return true;
        } else {
          //from version 12 to 13,all local file resourceSize column val is -1
            if (!TextUtils.isEmpty(mUUID)) {
                String mediaFullPath = getLocalFilePath();
                if (TextUtils.isEmpty(mediaFullPath)) {
                    return false;
                }
                if (!FileUtil.isFileExist(mediaFullPath)) {
                    return false;
                }
                return true;
            }
            return false;
        }
    }
    
    public void clearLocalPath() {
        mLocalFile = "";
        mUUID = "";
        mTmpLocalFilePath = "";
        DAOFactory.getInstance().getMediaDAO().updateLocalResource(mId, mLocalFile, mUUID,mResourceSize);
    }
    
    /**
     * [功能说明] 设置本地文件完整路径
     * @param localPath
     */
    public void setLocalFilePath(String localPath) {
        if (TextUtils.isEmpty(localPath)) {
            return;
        }
        
        StorageVolume volume = StorageManager.getInstance().getVolumeOfFile(localPath);
        boolean updateDB = false;
        
        if (volume != null) {
            if (mResourceSize > 0) {
                boolean isLocalEncrypted = false;
                long size = 0;
                
                size = FileUtil.getFileSize(localPath);
                //本地文件是否完整，不完整不更新muuid
                if (size != mResourceSize) {
                    EvLog.d("tmp file,not update to muuid");
                    mTmpLocalFilePath = localPath;
                    mUUID = "";
                } else {
                    mUUID = volume.getUUID();
                }
                
                if (localPath.contains("/")) {
                    int endFlag = localPath.lastIndexOf("/");
                    if (endFlag > 0 && endFlag < (localPath.length()-1)) {
                        mLocalFile = localPath.substring(endFlag+1);
                        EvLog.i("setLocalFile mLocalFile=" + mLocalFile);
                        updateDB = true;
                    } else {
                        EvLog.i("setLocalFile localPath invalid:"+localPath);
                    }
                } else {
                    EvLog.i("setLocalFile localPath invalid:"+localPath);
                } 
            } else {
                EvLog.e(mId + " setLocalFilePath mResourceSize is 0");
            }
        } else {
            mUUID = "";
            mLocalFile = "";
        }
        
        if (updateDB) {
            DAOFactory.getInstance().getMediaDAO().updateLocalResource(mId, mLocalFile, mUUID,mResourceSize);
        }
        return;
    }

    
    /**
     * [功能说明] 设置本地media文件名称
     * @param localFile
     */
    public void setLocalFileName(String localFile) {
        if (localFile == null) {
            mLocalFile = "";
        } else {
            if (mLocalFile == null) {
                mLocalFile = "";
            }
            
            if (!mLocalFile.equals(localFile)) {
                mLocalFile = localFile;
            }
        }
        
        if (TextUtils.isEmpty(mLocalFile)) {
            mUUID = "";
            EvLog.d(mId + " update mUUID empty ");
        }
    }
    
    public boolean hasLocalSubtitle() {
        if (TextUtils.isEmpty(mLocalSubtitle)) {
            return false;
        }
        
        String localSubtitlePath = getLocalSubtitlePath();
        if (TextUtils.isEmpty(localSubtitlePath)) {
            return false;
        }
        
        File file = new File(localSubtitlePath);
        
        boolean exist = file.exists();
        
        if (!exist) {
            FileUtil.deleteFile(localSubtitlePath);
        	mLocalSubtitle = "";
        	DAOFactory.getInstance().getMediaDAO().updateLocalSubtitle(mId, mLocalSubtitle);
        }
        
        return exist;
    }
    
    public void setResourceSize(long size) {
        mResourceSize = size;
    }
    
    public long getResourceSize() {
        return mResourceSize;
    }
    
    
    
    /**
     * [功能说明] 设置本地字幕完整路径，由外部调用
     * @param subtitlePath
     */
    public void setLocalSubtitlePath(String subtitlePath) {
        if (TextUtils.isEmpty(subtitlePath)) {
            return;
        }
        
        if (TextUtils.isEmpty(mUUID)) {
            mTmpSubtitleFilePath = subtitlePath;
        }
        
        boolean updateDB = false;
        
        if (subtitlePath.contains("/")) {
            int endFlag = subtitlePath.lastIndexOf("/");
            if (endFlag > 0 && endFlag < (subtitlePath.length()-1)) {
                mLocalSubtitle = subtitlePath.substring(endFlag+1);
                updateDB = true;
                EvLog.i("setLocalFile mLocalSubtitle=" + mLocalSubtitle);
            } else {
                EvLog.i("setLocalSubtitlePath subtitlePath invalid:"+subtitlePath);
            }
        } else {
            EvLog.i("setLocalSubtitlePath subtitlePath invalid:"+subtitlePath);
        }
        
        if (updateDB) {
            DAOFactory.getInstance().getMediaDAO().updateLocalSubtitle(mId, mLocalSubtitle);
        }
        return;
    }
    
    /**
     * [功能说明] 设置字幕文件名称，目前只有mediadao调用
     * @param subtitle
     */
    public void setLocalSubtitleName(String subtitle) {
        
        if (subtitle == null) {
            mLocalSubtitle = "";
        } else {
            if (mLocalSubtitle == null) {
                mLocalSubtitle = "";
            }
            
            if (!mLocalSubtitle.equals(subtitle)) {
                mLocalSubtitle = subtitle;
            }
        }
    }
    
    private String getPathByVolumeId(String pathType) {
        if (pathType != ResourceSaverPathManager.DIR_SUBTITLE && pathType != ResourceSaverPathManager.DIR_MEDIA) {
            return "";
        }
        
        StorageVolume volume = StorageManager.getInstance().getVolume(mUUID);
        
        if (volume == null) {
            EvLog.d("volume is null,mUUID=" + mUUID);
            return "";
        }

        String path = "";
        if (volume.isInternalSDCard()) {
            path = volume.getResourcePath();
//            path = FileUtil.concatPath(volume.getResourcePath(), pathType);
        } else {
            if (pathType == ResourceSaverPathManager.DIR_SUBTITLE) {
                path = volume.getSubtitlePath();
            } else if (pathType == ResourceSaverPathManager.DIR_MEDIA) {
                path = volume.getResourcePath();
            }
        }

        return path;
    }
    
    /**
     * [功能说明] 获取本地歌词文件名称
     * @return
     */
    public String getLocalSubtitleName() {
        if (TextUtils.isEmpty(mLocalSubtitle)) {
            mLocalSubtitle = "";
            return mLocalSubtitle;
        }
        
        boolean updateDB = false;
        if (convertLocalSubtitle()) {
            updateDB = true;
        }
        //更新数据库
        if (updateDB) {
            EvLog.d("local subtitle change, update to db,mLocalSubtitle=" + mLocalSubtitle);
            DAOFactory.getInstance().getMediaDAO().updateLocalSubtitle(mId,mLocalSubtitle);
        }
        return mLocalSubtitle;
    }
    
    private boolean convertLocalSubtitle() {
        boolean ret = false;
        
        if (mLocalSubtitle.contains("/")) {
            int endFlag = mLocalSubtitle.lastIndexOf("/");
            if (endFlag > 0 && endFlag < (mLocalSubtitle.length()-1)) {
                mLocalSubtitle = mLocalSubtitle.substring(endFlag+1);
                EvLog.i("convertLocalSubtitle mLocalSubtitle=" + mLocalSubtitle);
            } else {
                EvLog.e(mLocalSubtitle + " is invalid");
                //FIXME del file
                mLocalSubtitle = "";
            }
            ret = true;
        } else {
        }
        return ret;
    }
    /**
     * [功能说明] 获取歌词文件完成路径
     * @return
     */
    public String getLocalSubtitlePath() {
        EvLog.d("getLocalSubtitlePath mLocalSubtitle=" + mLocalSubtitle);
        String ret = "";
        if (TextUtils.isEmpty(mLocalSubtitle)) {
            return ret;
        }

        if (TextUtils.isEmpty(mUUID)) {
            return mTmpSubtitleFilePath;
        }
        
        boolean needUpdateDb = false;
        //如果存储的是完整路径，修改成只有文件名，并更新到数据库中
        if (convertLocalSubtitle()) {
            needUpdateDb = true;
        }
        
        if (TextUtils.isEmpty(mLocalSubtitle)) {
            return ret;
        }
        
        String path = getPathByVolumeId(ResourceSaverPathManager.DIR_SUBTITLE);
        if (!TextUtils.isEmpty(path)) {
            String ercFullPath = FileUtil.concatPath(path, mLocalSubtitle);
            
            File file = new File(ercFullPath);
            if (!file.exists()) {
                mLocalSubtitle = "";
                needUpdateDb = true;
                //FIXME del file
            } else {
                ret = ercFullPath;
            }
        } else {
            //FIXME
        }
        
        //更新数据库
        if (needUpdateDb) {
            EvLog.d("local subtitle change, update to db,mLocalSubtitle=" + mLocalSubtitle);
            DAOFactory.getInstance().getMediaDAO().updateLocalSubtitle(mId,mLocalSubtitle);
        }
        return ret;
    }
    
    public String getRemoteSubtitle() {
        return mSubtitle;
    }
    
    public int getOriginalTrack() {
        return mOriginalInfo;
    }
    
    public int getCompanyTrack() {
        return mCompanyInfo;
    }

    public int getVolume() {
        return mVolume;
    }
    
    public String getVolumeUUID() {
        return mUUID;
    }
    
    public int getSongId() {
        return mSongId;
    }
    
    public void setSongId(int songId) {
        mSongId = songId;
    }
    
    public String getSongName() {
        return mSongName;
    }
    
   /* public static Media getMediaById(int id) {
        MediaDAO dao = DAOFactory.getInstance().getMediaDAO();
        return dao.getMediaById(id);
    }*/
    
    public boolean isVideo() {
        if ( mType == Media.Type_MKV || mType == Media.Type_TS
                || mType == Media.Type_MPG || mType == Media.Type_AVI ) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public String toString() {
    	StringBuilder str = new StringBuilder();
    	return str.append("id:").append(this.mId).append(",songId:").append(this.mSongId).toString();
    }
}
