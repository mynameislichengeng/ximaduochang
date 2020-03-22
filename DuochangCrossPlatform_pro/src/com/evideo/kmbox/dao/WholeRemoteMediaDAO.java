/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-7-24     "liuyantao"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.evideo.kmbox.model.dao.data.Media;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.update.db.RemoteMedia;
import com.evideo.kmbox.util.EvLog;

/**
 * [全库media]
 */
public class WholeRemoteMediaDAO extends WholeMediaDAO {
   
    // table media
    private static final String TABLE_MEDIA_NAME = "tblMedia";
    private static final String TABLE_MEDIA_COL_MEDIAID = "MediaID";
    private static final String TABLE_MEDIA_COL_SONGID = "SongID";
    private static final String TABLE_MEDIA_COL_SONGNAME = "SongFileName";
    private static final String TABLE_MEDIA_COL_ORIGINALTRACK = "OriginalTrack";
    private static final String TABLE_MEDIA_COL_COMPANYTRACK = "AccompanyTrack";
    private static final String TABLE_MEDIA_COL_VOLUME = "DefaultVolume";
    private static final String TABLE_MEDIA_COL_TYPE = "MediaType";
    private static final String TABLE_MEDIA_COL_SONGNAME_WORDTYPE = "SongNameWordType";
    private static final String TABLE_MEDIA_COL_VOLUME_BALANCE = "VolumeBalance";
    private static final String TABLE_MEDIA_COL_VOLUMEQUA = "VolumeQuality";
    private static final String TABLE_MEDIA_COL_IMAGEQUA = "ImageQuality";
    private static final String TABLE_MEDIA_COL_SONGVERSION = "SongVersion";
    private static final String TABLE_MEDIA_COL_TOTALQUALITY = "TotalQuality";
    private static final String TABLE_MEDIA_COL_PRICE = "Price";
    private static final String TABLE_MEDIA_COL_MD5VALUE = "Md5Value";
    private static final String TABLE_MEDIA_COL_ISTORRENT_EXPIRED = "IsTorrentExpired";
    private static final String TABLE_MEDIA_COL_UPDATE_DATETIME = "UpdateDateTime";

    public static final String[] TABLE_MEDIA_ALL_COL = {TABLE_MEDIA_COL_MEDIAID, TABLE_MEDIA_COL_TYPE, 
        TABLE_MEDIA_COL_SONGID, TABLE_MEDIA_COL_SONGNAME, TABLE_MEDIA_COL_ORIGINALTRACK, 
        TABLE_MEDIA_COL_COMPANYTRACK, TABLE_MEDIA_COL_VOLUME, TABLE_MEDIA_COL_SONGNAME_WORDTYPE,
        TABLE_MEDIA_COL_VOLUME_BALANCE, TABLE_MEDIA_COL_VOLUMEQUA, TABLE_MEDIA_COL_IMAGEQUA,
        TABLE_MEDIA_COL_SONGVERSION, TABLE_MEDIA_COL_TOTALQUALITY, TABLE_MEDIA_COL_PRICE, 
        TABLE_MEDIA_COL_MD5VALUE, TABLE_MEDIA_COL_ISTORRENT_EXPIRED, TABLE_MEDIA_COL_UPDATE_DATETIME};
    
    public WholeRemoteMediaDAO()
    {
    }
    
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeMediaDAO#getMedia(int)
     */
    @Override
    public List<Media> getMedia(int songId) {
        
        Cursor cursor = null;
        String selection;
        
        selection = TABLE_MEDIA_COL_SONGID + "=" + "'" + String.format("%08d", songId) + "'";
        SQLiteDatabase db = WholeDAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return Collections.emptyList();
        }
        
        List<Media> medias = new ArrayList<Media>();
        
        try {
            cursor = db.query(TABLE_MEDIA_NAME, TABLE_MEDIA_ALL_COL, selection, null, null, null, null);
            
            while (cursor.moveToNext()) {
                Media media = new Media(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(3), 
                        null, cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), null,
                        null);
                medias.add(media);
            }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
            return Collections.emptyList();
        } finally {
            if (cursor != null)
            {
                cursor.close();
            }
        }
        
        return medias;
    }
    
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeMediaDAO#update(com.evideo.kmbox.model.dao.data.Media)
     */
    @Override
    public boolean update(Media media) {

        SQLiteDatabase db = WholeDAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return false;
        }
        
        try {
            ContentValues values = new ContentValues();
            values.put(TABLE_MEDIA_COL_TYPE, media.getType());
            values.put(TABLE_MEDIA_COL_SONGID, media.getSongId());
            values.put(TABLE_MEDIA_COL_SONGNAME, media.getSongName());
            values.put(TABLE_MEDIA_COL_ORIGINALTRACK, media.getOriginalTrack());
            values.put(TABLE_MEDIA_COL_COMPANYTRACK, media.getCompanyTrack());
            values.put(TABLE_MEDIA_COL_VOLUME, media.getVolume());
            int effectRows = db.update(TABLE_MEDIA_NAME, values, TABLE_MEDIA_COL_MEDIAID+"=?", 
                    new String[]{String.valueOf(media.getId())});
            
            return effectRows == 1;
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        
        return false;
    }
    
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeMediaDAO#getMediaById(int)
     */
    @Override
    public Media getMediaById(int id) {
        return null;
    }
   
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeMediaDAO#save(java.util.List)
     */
    @Override
    public boolean save(List<RemoteMedia> list) {
        SQLiteDatabase db = WholeDAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        
        String sql = "insert or replace into " + TABLE_MEDIA_NAME + "("
                + TABLE_MEDIA_COL_TYPE + ","
                + TABLE_MEDIA_COL_SONGID + ","
                + TABLE_MEDIA_COL_SONGNAME + ","
                + TABLE_MEDIA_COL_ORIGINALTRACK + ","
                + TABLE_MEDIA_COL_COMPANYTRACK + ","
                + TABLE_MEDIA_COL_VOLUME + ","
                + TABLE_MEDIA_COL_SONGNAME_WORDTYPE + ","
                + TABLE_MEDIA_COL_VOLUME_BALANCE + ","
                + TABLE_MEDIA_COL_VOLUMEQUA + ","
                + TABLE_MEDIA_COL_IMAGEQUA + ","
                + TABLE_MEDIA_COL_SONGVERSION + ","
                + TABLE_MEDIA_COL_TOTALQUALITY + ","
                + TABLE_MEDIA_COL_PRICE + ","
                + TABLE_MEDIA_COL_MD5VALUE + "," 
                + TABLE_MEDIA_COL_ISTORRENT_EXPIRED + ","
                + TABLE_MEDIA_COL_UPDATE_DATETIME
                + ")"
                + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
        SQLiteStatement stat = db.compileStatement(sql);
        
        RemoteMedia media = null;
        
        db.beginTransaction();
        
        for (int i = 0; i < list.size(); ++i) {
            media = list.get(i);
            
            if (media == null) {
                continue;
            }
            
            stat.bindString(1, media.getMediaType());
            stat.bindLong(2, media.getSongID());
            stat.bindString(3, media.getSongFileName());
            stat.bindLong(4, media.getOriginalTrack());
            stat.bindLong(5, media.getAccompanyTrack());
            stat.bindLong(6, media.getDefaultVolume());
            stat.bindString(7, media.getSongNameWordType());
            stat.bindLong(8, media.getVolumeBalance());
            stat.bindString(9, media.getVolumeQuality());
            stat.bindString(10, media.getImageQuality());
            stat.bindString(11, media.getSongVersion());
            stat.bindString(12, media.getTotalQuality());
            stat.bindLong(13, media.getPrice());
            stat.bindString(14, media.getMd5Value());
            stat.bindLong(15, media.getIsTorrentExpired());
            stat.bindString(16, media.getUpdateDateTime());
            stat.executeInsert();
        }
        
        db.setTransactionSuccessful();
        db.endTransaction();

        return true;
    }
 
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeMediaDAO#deleteMediasBySongId(int)
     */
    @Override
    public void deleteMediasBySongId(int songId) {
        SQLiteDatabase db = WholeDAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return;
        }
        
        db.delete(TABLE_MEDIA_NAME, TABLE_MEDIA_COL_SONGID + " = ?", new String[]{String.valueOf(songId)});
    }
    
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeMediaDAO#updateBaseInfo(java.util.List)
     */
    @Override
    public boolean updateBaseInfo(List<Media> list) {
        SQLiteDatabase db = WholeDAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }

        String sqlInsert = "insert into " + TABLE_MEDIA_NAME + "(" 
              
                + TABLE_MEDIA_COL_SONGID + ","
                + TABLE_MEDIA_COL_SONGNAME + ","
                + TABLE_MEDIA_COL_VOLUME + ","
                + TABLE_MEDIA_COL_ORIGINALTRACK + "," 
                + TABLE_MEDIA_COL_COMPANYTRACK + ","
                + TABLE_MEDIA_COL_SONGNAME_WORDTYPE + ","
                + TABLE_MEDIA_COL_VOLUME_BALANCE + ","
                + TABLE_MEDIA_COL_VOLUMEQUA + ","
                + TABLE_MEDIA_COL_IMAGEQUA + ","
                + TABLE_MEDIA_COL_SONGVERSION + ","
                + TABLE_MEDIA_COL_TOTALQUALITY + "," 
                + TABLE_MEDIA_COL_PRICE + ","
                + TABLE_MEDIA_COL_MD5VALUE + ","
                + TABLE_MEDIA_COL_ISTORRENT_EXPIRED + ","
                + TABLE_MEDIA_COL_UPDATE_DATETIME + "," 
                + ")"
                + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
        SQLiteStatement statInsert = db.compileStatement(sqlInsert);
        
        Media media = null;
        
        db.beginTransaction();
        
        for (int i = 0; i < list.size(); ++i) {
            media = list.get(i);
            
            if (media == null) {
                continue;
            }
            statInsert.bindLong(1, media.getSongId());
            statInsert.bindString(2, media.getSongName());
            statInsert.bindLong(3, media.getVolume());
            statInsert.bindLong(4, media.getOriginalTrack());
            statInsert.bindLong(5, media.getCompanyTrack());
            statInsert.bindString(6, "");
            statInsert.bindLong(7, 0);
            statInsert.bindString(8, "");
            statInsert.bindString(9, "");
            statInsert.bindString(10,"");
            statInsert.bindString(11,"");
            statInsert.bindLong(12, 0);
            statInsert.bindString(13, "");
            statInsert.bindLong(14, 0);
            statInsert.bindString(15, "");
            statInsert.executeInsert();
        }
        
        db.setTransactionSuccessful();
        db.endTransaction();

        return true;
    }
    
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeMediaDAO#updateOnlineMedia(int, java.util.List)
     */
    @Override
    public boolean updateOnlineMedia(int songId, List<RemoteMedia> list) {
        SQLiteDatabase db = WholeDAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }

        String sqlInsert = "insert or replace into " + TABLE_MEDIA_NAME + "(" 
                
                + TABLE_MEDIA_COL_SONGID + ","
                + TABLE_MEDIA_COL_SONGNAME + ","
                + TABLE_MEDIA_COL_VOLUME + ","
                + TABLE_MEDIA_COL_ORIGINALTRACK + "," 
                + TABLE_MEDIA_COL_COMPANYTRACK + ","
                + TABLE_MEDIA_COL_TYPE + ","
                + TABLE_MEDIA_COL_SONGNAME_WORDTYPE + ","
                + TABLE_MEDIA_COL_VOLUME_BALANCE + ","
                + TABLE_MEDIA_COL_VOLUMEQUA + ","
                + TABLE_MEDIA_COL_IMAGEQUA + ","
                + TABLE_MEDIA_COL_SONGVERSION + ","
                + TABLE_MEDIA_COL_TOTALQUALITY + "," 
                + TABLE_MEDIA_COL_PRICE + ","
                + TABLE_MEDIA_COL_MD5VALUE + ","
                + TABLE_MEDIA_COL_ISTORRENT_EXPIRED + ","
                + TABLE_MEDIA_COL_UPDATE_DATETIME + "," 
                + ")"
                + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
        SQLiteStatement statInsert = db.compileStatement(sqlInsert);
        
        RemoteMedia remoteMedia = null;
        
        db.beginTransaction();
        
        for (int i = 0; i < list.size(); ++i) {
            remoteMedia = list.get(i);
            
            if (remoteMedia == null) {
                continue;
            }
            
            if (remoteMedia.getSongID() != songId) {
                continue;
            }
            statInsert.bindLong(1, remoteMedia.getSongID());
            statInsert.bindString(2, remoteMedia.getSongFileName());
            statInsert.bindLong(3, remoteMedia.getDefaultVolume());
            statInsert.bindLong(4, remoteMedia.getOriginalTrack());
            statInsert.bindLong(5, remoteMedia.getAccompanyTrack());
            statInsert.bindString(6, remoteMedia.getSongNameWordType());
            statInsert.bindLong(7, remoteMedia.getVolumeBalance());
            statInsert.bindString(8, remoteMedia.getVolumeQuality());
            statInsert.bindString(9, remoteMedia.getImageQuality());
            statInsert.bindString(10, remoteMedia.getSongVersion());
            statInsert.bindString(11, remoteMedia.getTotalQuality());
            statInsert.bindLong(12, remoteMedia.getPrice());
            statInsert.bindString(13, remoteMedia.getMd5Value());
            statInsert.bindLong(14, remoteMedia.getIsTorrentExpired());
            statInsert.bindString(15, remoteMedia.getUpdateDateTime());
            statInsert.executeInsert();
        }
        
        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }
    
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeMediaDAO#getMaxID()
     */
    @Override
    public int getMaxID() {
        SQLiteDatabase db = null;
        try {
            db = WholeDAOHelper.getInstance().getReadableDatabase();
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        
        Cursor cursor = null;
        
        if (db == null) {
            return -1;
        }

        String sqlMax = "select max(" + TABLE_MEDIA_COL_MEDIAID + ") FROM " + TABLE_MEDIA_NAME;
        cursor = db.rawQuery(sqlMax, null);
        while (cursor.moveToNext()) {
            return cursor.getInt(0);
        }
        return -1;
    }
    
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeMediaDAO#isExist(java.lang.String)
     */
    @Override
    public boolean isExist(String md5) {

        boolean exist = false;
        Cursor cursor = null;
        String selection = TABLE_MEDIA_COL_MD5VALUE + "=?";
        SQLiteDatabase db = WholeDAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return false;
        }

        try {
            cursor = db.query(TABLE_MEDIA_NAME,
                    new String[] { TABLE_MEDIA_COL_SONGID }, selection, new String[]{md5}, null,
                    null, null);
            if (cursor != null && cursor.moveToNext()) {
                cursor.getInt(0);
                exist = true;                
            } else {
                exist = false;
            }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }

        return exist;
    }

    @Override
    public int getSongIdByMd5(String md5) {
        SQLiteDatabase db = WholeDAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return 0;
        }

        int SongId = 0;
        Cursor cursor = null;
        String selection = TABLE_MEDIA_COL_MD5VALUE + "=?";

        try {
            cursor = db.query(TABLE_MEDIA_NAME, new String[] { TABLE_MEDIA_COL_SONGID }, selection, new String[]{md5}, null,
                    null, null);
            if (cursor != null && cursor.moveToNext()) {
                SongId = cursor.getInt(0);             
            }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return SongId;
    }
}
