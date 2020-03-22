package com.evideo.kmbox.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.evideo.kmbox.model.dao.data.Media;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.dao.TableMedia;

public class MediaDAO {
    
    // table media
    private static final String TABLE_MEDIA_NAME = "tblMedia";
    private static final String TABLE_MEDIA_COL_ID = "id";
    private static final String TABLE_MEDIA_COL_TYPE = "type";
    private static final String TABLE_MEDIA_COL_SONGID = "songId";
    private static final String TABLE_MEDIA_COL_SONGNAME = "songName";
    private static final String TABLE_MEDIA_COL_RESOURCEID = "resourceId";
    private static final String TABLE_MEDIA_COL_ORIGINALTRACK = "originalInfo";
    private static final String TABLE_MEDIA_COL_COMPANYTRACK = "companyInfo";
    private static final String TABLE_MEDIA_COL_VOLUME = "volume";
    private static final String TABLE_MEDIA_COL_VOLUMEUUID = "volumeUUID";
    private static final String TABLE_MEDIA_COL_SUBTITLE = "subtitle";
    private static final String TABLE_MEDIA_COL_LOCALSUBTITLE = "localSubtitle";
    private static final String TABLE_MEDIA_COL_LOCALRESOURCE = "localResource";
    private static final String TABLE_MEDIA_COL_RESOURCESIZE = "resourceSize";
    
    private static String[] TABLE_MEDIA_ALL_COL = { TABLE_MEDIA_COL_ID, TABLE_MEDIA_COL_TYPE, TABLE_MEDIA_COL_SONGID, 
        TABLE_MEDIA_COL_SONGNAME, TABLE_MEDIA_COL_RESOURCEID, TABLE_MEDIA_COL_ORIGINALTRACK, TABLE_MEDIA_COL_COMPANYTRACK,
        TABLE_MEDIA_COL_VOLUME, TABLE_MEDIA_COL_VOLUMEUUID, TABLE_MEDIA_COL_SUBTITLE, TABLE_MEDIA_COL_LOCALSUBTITLE,
        TABLE_MEDIA_COL_LOCALRESOURCE,TABLE_MEDIA_COL_RESOURCESIZE};
    
    public MediaDAO()
    {
    	TABLE_MEDIA_ALL_COL = new String[TableMedia.Column.values().length];
        
        for (int i = 0; i < TABLE_MEDIA_ALL_COL.length; i++) {
        	TABLE_MEDIA_ALL_COL[TableMedia.Column.values()[i].ordinal()] = TableMedia.Column.values()[i].getName();
        }
    }
    
    public List<Media> getMedia(int songId) { 
        
        Cursor cursor = null;
        String selection;
        
        selection = "songId=" + songId;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return Collections.emptyList();
        }
        
        List<Media> medias = new ArrayList<Media>();
        
        try {
            cursor = db.query(TableMedia.NAME, TABLE_MEDIA_ALL_COL, selection, null, null, null, null);
            
            while(cursor.moveToNext())
            {
                Media media = new Media(cursor.getInt(TableMedia.Column.ID.ordinal()), 
                		cursor.getInt(TableMedia.Column.TYPE.ordinal()), 
                		cursor.getInt(TableMedia.Column.SONG_ID.ordinal()), 
                		cursor.getString(TableMedia.Column.SONG_NAME.ordinal()), 
                        cursor.getString(TableMedia.Column.RESOURCE_ID.ordinal()), 
                        cursor.getInt(TableMedia.Column.ORIGINAL_INFO.ordinal()), 
                        cursor.getInt(TableMedia.Column.COMPANY_INFO.ordinal()), 
                        cursor.getInt(TableMedia.Column.VOLUME.ordinal()), 
                        cursor.getString(TableMedia.Column.VOLUME_UUID.ordinal()),
                        cursor.getString(TableMedia.Column.SUBTITLE.ordinal()),
                        cursor.getString(TableMedia.Column.LOCAL_RESOURCE.ordinal()),
                        cursor.getInt(TableMedia.Column.DURATION.ordinal()));
                media.setLocalSubtitleName(cursor.getString(TableMedia.Column.LOCAL_SUBTITLE.ordinal()));
//                media.setLocalFileName(cursor.getString(TableMedia.Column.LOCAL_RESOURCE.ordinal()));
                media.setResourceSize(cursor.getLong(TableMedia.Column.RESOURCE_SIZE.ordinal()));
                medias.add(media);
            }
        } catch (Exception e) {
            e.printStackTrace();
            UmengAgentUtil.reportError(e);
            return Collections.emptyList();
        } finally {
            if (cursor != null)
            {
                cursor.close();
            }
            
            //db.close();
        }
        
        return medias;
    }
    
    public boolean updateLocalResource(int mediaId,String localResource,String uuid,long resourceSize) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }

        // FIXME
        String sqlUpdate = "update tblMedia set "
                + TableMedia.Column.LOCAL_RESOURCE.getName()
                + "=?, "
                + TableMedia.Column.VOLUME_UUID.getName()
                + "=?, "
                 + TableMedia.Column.RESOURCE_SIZE.getName()
                + "=? "
                + " where id=?;";

        SQLiteStatement statUpdate = db.compileStatement(sqlUpdate);
        db.beginTransaction();
            
        statUpdate.bindString(1, localResource);
        statUpdate.bindString(2, uuid);
        statUpdate.bindLong(3, resourceSize);
        statUpdate.bindLong(4, mediaId);
        statUpdate.executeUpdateDelete();
        db.setTransactionSuccessful();
        db.endTransaction();

        return true;
    }
    public boolean updateLocalSubtitle(int mediaId,String subtitle) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }

        // FIXME
        String sqlUpdate = "update tblMedia set "
                + TableMedia.Column.LOCAL_SUBTITLE.getName()
                + "=? "
                + " where id=?;";

        SQLiteStatement statUpdate = db.compileStatement(sqlUpdate);
        db.beginTransaction();
            
        statUpdate.bindString(1, subtitle);
        statUpdate.bindLong(2, mediaId);
        statUpdate.executeUpdateDelete();
        db.setTransactionSuccessful();
        db.endTransaction();

        return true;
    }
    
    public boolean update(Media media) {

        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            EvLog.e("update db null");
            return false;
        }
        
        try {
            ContentValues values = new ContentValues();
            values.put(TableMedia.Column.TYPE.getName(), media.getType());
            values.put(TableMedia.Column.SONG_ID.getName(), media.getSongId());
            values.put(TableMedia.Column.SONG_NAME.getName(), media.getSongName());
            values.put(TableMedia.Column.RESOURCE_ID.getName(), media.getResource());
            values.put(TableMedia.Column.ORIGINAL_INFO.getName(), media.getOriginalTrack());
            values.put(TableMedia.Column.COMPANY_INFO.getName(), media.getCompanyTrack());
            values.put(TableMedia.Column.VOLUME.getName(), media.getVolume());
            values.put(TableMedia.Column.VOLUME_UUID.getName(), media.getVolumeUUID());
            values.put(TableMedia.Column.SUBTITLE.getName(), media.getRemoteSubtitle());
            values.put(TableMedia.Column.LOCAL_SUBTITLE.getName(), media.getLocalSubtitleName());
            values.put(TableMedia.Column.LOCAL_RESOURCE.getName(), media.getLocalFileName());
            values.put(TableMedia.Column.RESOURCE_SIZE.getName(), media.getResourceSize());
            values.put(TableMedia.Column.DURATION.getName(), media.getDuration());
            
            int effectRows = db.update(TableMedia.NAME, values, 
            		TableMedia.Column.ID.getName() + "=?", new String[]{String.valueOf(media.getId())});
            EvLog.e("effectRows=" + effectRows + ",id=" + media.getId());
            return effectRows == 1;
        } catch (Exception e) {
            e.printStackTrace();
            UmengAgentUtil.reportError(e);
        }
        
        return false;
    }
    
    public Media getMediaById(int id) {
        
        Cursor cursor = null;
        String selection = TableMedia.Column.ID.getName() + "=" + id;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return null;
        }
        
        try {
            cursor = db.query(TableMedia.NAME, TABLE_MEDIA_ALL_COL, selection, null, null, null, null);
            if (cursor.moveToNext()) {
                Media media = new Media(cursor.getInt(TableMedia.Column.ID.ordinal()), 
                		cursor.getInt(TableMedia.Column.TYPE.ordinal()), 
                		cursor.getInt(TableMedia.Column.SONG_ID.ordinal()), 
                		cursor.getString(TableMedia.Column.SONG_NAME.ordinal()), 
                        cursor.getString(TableMedia.Column.RESOURCE_ID.ordinal()), 
                        cursor.getInt(TableMedia.Column.ORIGINAL_INFO.ordinal()), 
                        cursor.getInt(TableMedia.Column.COMPANY_INFO.ordinal()), 
                        cursor.getInt(TableMedia.Column.VOLUME.ordinal()), 
                        cursor.getString(TableMedia.Column.VOLUME_UUID.ordinal()),
                        cursor.getString(TableMedia.Column.SUBTITLE.ordinal()),
                        cursor.getString(TableMedia.Column.LOCAL_RESOURCE.ordinal()),
                        cursor.getInt(TableMedia.Column.DURATION.ordinal()));
                media.setLocalSubtitleName(cursor.getString(TableMedia.Column.LOCAL_SUBTITLE.ordinal()));
//                media.setLocalFileName(cursor.getString(TableMedia.Column.LOCAL_RESOURCE.ordinal()));
                media.setResourceSize(cursor.getLong(TableMedia.Column.RESOURCE_SIZE.ordinal()));
                return media;
            }
        }catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }

        return null;
    }
    
    public boolean save(List<Media> list) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        
        String sql = "insert into " + TableMedia.NAME + "(" 
                + TableMedia.Column.TYPE.getName() + ","
                + TableMedia.Column.SONG_ID.getName() + ","
                + TableMedia.Column.SONG_NAME.getName() + ","
                + TableMedia.Column.RESOURCE_ID.getName() + ","
                + TableMedia.Column.ORIGINAL_INFO.getName() + ","
                + TableMedia.Column.COMPANY_INFO.getName() + ","
                + TableMedia.Column.VOLUME.getName() + ","
                + TableMedia.Column.VOLUME_UUID.getName() + ","
                + TableMedia.Column.SUBTITLE.getName() + ","
                + TableMedia.Column.LOCAL_SUBTITLE.getName() + ","
                + TableMedia.Column.LOCAL_RESOURCE.getName() + ","
                + TableMedia.Column.RESOURCE_SIZE.getName()
                + ")"
                + " values(?,?,?,?,?,?,?,?,?,?,?,?)";
        
        SQLiteStatement stat = db.compileStatement(sql);
        
        Media media = null;
        
        db.beginTransaction();
        
        for (int i = 0; i < list.size(); ++i) {
            media = list.get(i);
            
            if (media == null) {
                continue;
            }
            
            stat.bindLong(1, media.getType());
            stat.bindLong(2, Integer.valueOf(media.getSongId()));
            stat.bindString(3, media.getSongName());
            stat.bindString(4, media.getResource());
            stat.bindLong(5, media.getOriginalTrack());
            stat.bindLong(6, media.getCompanyTrack());
            stat.bindLong(7, media.getVolume());
            stat.bindString(8, media.getVolumeUUID());
            stat.bindString(9, media.getRemoteSubtitle());
            stat.bindString(10, media.getLocalSubtitleName());
            stat.bindString(11, media.getLocalFileName());
            stat.bindLong(12,media.getResourceSize());
            stat.executeInsert();
        }
        
        db.setTransactionSuccessful();
        db.endTransaction();

        return true;
    }
    
    public void removeMediaByStorageVolumeUUID(String uuid) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return;
        }
        
        try {
            db.delete(TableMedia.NAME, TableMedia.Column.VOLUME_UUID.getName() + " = ?", new String[]{uuid});
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
    }
    
    public void delete(int id) {
    	SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return;
        }
        
        try {
            db.delete(TableMedia.NAME, TableMedia.Column.ID.getName() + " = ?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
    }
    
    public void syncWithUUID(List<String> uuids) {
        if (uuids.size() == 0) {
            return;
        }

        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return;
        }
        
        String sqlWhere = TABLE_MEDIA_COL_VOLUMEUUID + " NOT IN (";
        for (int i = 0; i < uuids.size(); i++) {
            if (i > 0) {
                sqlWhere += ",";
            }

            sqlWhere += "'" + uuids.get(i) + "'";
        }
        sqlWhere += ")";
        sqlWhere += " and length(" + TABLE_MEDIA_COL_VOLUMEUUID + ")>0";
        
        try {
            db.delete(TableMedia.NAME, sqlWhere, null);
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
    }
    
    public boolean updateMediaBaseInfo(int songId, List<Media> list) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        
        String sqlDelete = "delete from " + TABLE_MEDIA_NAME + " where " + TABLE_MEDIA_COL_SONGID + "=?"
                + " and length(" + TABLE_MEDIA_COL_VOLUMEUUID + ")==0";
        String sqlInsert = "insert into " + TABLE_MEDIA_NAME + "(" 
                + TABLE_MEDIA_COL_TYPE + ","
                + TABLE_MEDIA_COL_SONGID + ","
                + TABLE_MEDIA_COL_SONGNAME + ","
                + TABLE_MEDIA_COL_RESOURCEID + ","
                + TABLE_MEDIA_COL_ORIGINALTRACK + ","
                + TABLE_MEDIA_COL_COMPANYTRACK + ","
                + TABLE_MEDIA_COL_VOLUME + ","
                + TABLE_MEDIA_COL_SUBTITLE + ")"
                + " values(?,?,?,?,?,?,?,?)";
        
        SQLiteStatement statInsert = db.compileStatement(sqlInsert);
        SQLiteStatement statDelete = db.compileStatement(sqlDelete);
        
        Media media = null;
        
        db.beginTransaction();
        
        statDelete.bindLong(1,  songId);
        statDelete.execute();
        
        for (int i = 0; i < list.size(); ++i) {
            media = list.get(i);
            
            if (media == null) {
                continue;
            }
            
            if (media.getSongId() != songId) {
                continue;
            }
            
            statInsert.bindLong(1, media.getType());
            statInsert.bindLong(2, Integer.valueOf(media.getSongId()));
            statInsert.bindString(3, media.getSongName());
            statInsert.bindString(4, media.getResource());
            statInsert.bindLong(5, media.getOriginalTrack());
            statInsert.bindLong(6, media.getCompanyTrack());
            statInsert.bindLong(7, media.getVolume());
            statInsert.bindString(8, media.getRemoteSubtitle());
            statInsert.executeInsert();
        }
        
        db.setTransactionSuccessful();
        db.endTransaction();

        return true;
    }
    
    public boolean updateOnlineMedia(int songId, List<Media> list) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        
        String sqlDelete = "delete from " + TableMedia.NAME + " where " + TableMedia.Column.SONG_ID.getName() + "=?"
                + " and length(" + TableMedia.Column.VOLUME_UUID.getName() + ")==0";
        String sqlInsert = "insert into " + TableMedia.NAME + "(" 
                + TableMedia.Column.TYPE.getName() + ","
                + TableMedia.Column.SONG_ID.getName() + ","
                + TableMedia.Column.SONG_NAME.getName() + ","
                + TableMedia.Column.RESOURCE_ID.getName() + ","
                + TableMedia.Column.ORIGINAL_INFO.getName() + ","
                + TableMedia.Column.COMPANY_INFO.getName() + ","
                + TableMedia.Column.VOLUME.getName() + ","
                + TableMedia.Column.VOLUME_UUID.getName() + ","
                + TableMedia.Column.SUBTITLE.getName() + ","
                + TableMedia.Column.LOCAL_SUBTITLE.getName() + ","
                + TableMedia.Column.LOCAL_RESOURCE.getName() + ","
                + TableMedia.Column.RESOURCE_SIZE.getName() + ","
                + TableMedia.Column.DURATION.getName()
                + ")"
                + " values(?,?,?,?,?,?,?,?,?,?,?,?,?)";

        // FIXME
        String sqlUpdate = "update tblMedia set "
                + TableMedia.Column.SUBTITLE.getName()
                + "=?, "
                + TableMedia.Column.RESOURCE_ID.getName()
                + "=? "
                + " where songID=?;";

        SQLiteStatement statInsert = db.compileStatement(sqlInsert);
        SQLiteStatement statDelete = db.compileStatement(sqlDelete);
        SQLiteStatement statUpdate = db.compileStatement(sqlUpdate);
        
        Media media = null;
        
        db.beginTransaction();
        
        statDelete.bindLong(1,  songId);
        statDelete.execute();
        
        for (int i = 0; i < list.size(); ++i) {
            media = list.get(i);
            
            if (media == null) {
                continue;
            }
            
            if (media.getSongId() != songId) {
                continue;
            }
            
            statInsert.bindLong(1, media.getType());
            statInsert.bindLong(2, media.getSongId());
            statInsert.bindString(3, media.getSongName());
            statInsert.bindString(4, media.getResource());
            statInsert.bindLong(5, media.getOriginalTrack());
            statInsert.bindLong(6, media.getCompanyTrack());
            statInsert.bindLong(7, media.getVolume());
            statInsert.bindString(8, media.getVolumeUUID());
            statInsert.bindString(9, media.getRemoteSubtitle());
            statInsert.bindString(10, media.getLocalSubtitleName());
            statInsert.bindString(11, media.getLocalFileName());
            statInsert.bindLong(12,media.getResourceSize());
            statInsert.bindLong(13, media.getDuration());
            statInsert.executeInsert();
            
            statUpdate.bindString(1, media.getRemoteSubtitle());
            statUpdate.bindString(2, media.getResource());
            statUpdate.bindLong(3, media.getSongId());
            statUpdate.executeUpdateDelete();
        }
        
        db.setTransactionSuccessful();
        db.endTransaction();

        return true;
    }
    
    public boolean hasCachedMedia(int songId) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return false;
        }
        
        Cursor cursor = null;
        StringBuilder selectionBuilder = new StringBuilder();
        selectionBuilder.append(TableMedia.Column.SONG_ID.getName());
        selectionBuilder.append("=");
        selectionBuilder.append(songId);
        selectionBuilder.append(" and ");
        selectionBuilder.append("length(");
        selectionBuilder.append(TableMedia.Column.VOLUME_UUID.getName());
        selectionBuilder.append(")>0");
        
        try {
            cursor = db.query(true, TableMedia.NAME, new String[] { "count(*)" }, 
            		selectionBuilder.toString(), null, null, null, null, null);
            
            if (cursor.moveToNext()) {
            	return cursor.getInt(0) > 0;
            }
        } catch (Exception e) {
        	EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        
        return false;
    }
    
    public int[] getCachedMediaList() {
        Cursor cursor = null;
        String selection = "length(" + TableMedia.Column.VOLUME_UUID.getName() + ")>0";
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return null;
        }
        
        int[] array = null;
        
        try {
            cursor = db.query(true, TableMedia.NAME, new String[] {TableMedia.Column.SONG_ID.getName()}, selection, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                array = new int[cursor.getCount()];
            }

            int i = 0;
            while (cursor.moveToNext()) {
                array[i] = cursor.getInt(0);
                i++;
            }
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            UmengAgentUtil.reportError(e);
        } finally {
            if(cursor != null) {
                cursor.close();
            }
            
            //db.close();
        }

        return array;
    }
    
    /**
     * [清空表]
     * @return  true false
     */
    public boolean cleaList() {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return false;
        }
        try {
            db.execSQL("delete from " + TABLE_MEDIA_NAME);
        } catch (SQLException e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
            return false;
        }
        return true;
    }
    
    /**
     * [返回数量]
     * @return
     */
    public int getCount() {
        int count = 0;
        Cursor cursor = null;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            
            return count;
        }
        
        try {
            cursor = db.query(TABLE_MEDIA_NAME, new String[]{"count(*)"}, null, null, null, null, null, null);
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }
    
    public int getCountByUUID(String uuid) {
        int count = 0;
        Cursor cursor = null;
        String selection = TableMedia.Column.VOLUME_UUID.getName() + " =? ";
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null || TextUtils.isEmpty(uuid)) {
            return count;
        }
        
        try {
            cursor = db.query(TABLE_MEDIA_NAME, new String[]{"count(*)"}, selection, new String[]{uuid}, null, null, null, null);
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }
}
