package com.evideo.kmbox.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.evideo.kmbox.model.dao.data.StorageVolumeMedia;
import com.evideo.kmbox.model.dao.data.Media;
import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.dao.TableKmBoxStorageVolumeMedia;

public class StorageVolumeMediaDAO {
//	Context mContext = null;
	String mPath = "";
	StorageVolumeDAOHelper mDAOHelper = null;

    private static String[] TABLE_MEDIA_ALL_COL = null; 
    
    public StorageVolumeMediaDAO(Context context, String path) {
//    	mContext = context;
    	mPath = path;
    	mDAOHelper = new StorageVolumeDAOHelper(context, mPath);

    	TABLE_MEDIA_ALL_COL = new String[TableKmBoxStorageVolumeMedia.Column.values().length];
        
        for (int i = 0; i < TABLE_MEDIA_ALL_COL.length; i++) {
        	TABLE_MEDIA_ALL_COL[TableKmBoxStorageVolumeMedia.Column.values()[i].ordinal()] = 
        			TableKmBoxStorageVolumeMedia.Column.values()[i].getName();
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
    	mDAOHelper.getWritableDatabase().close();
    	super.finalize();
    }

    public List<StorageVolumeMedia> getList(PageInfo pageInfo) { 
        SQLiteDatabase db = mDAOHelper.getReadableDatabase();

        if (db == null) {
            return Collections.emptyList();
        }

        String limit = "";
        if (pageInfo != null) {
            limit = pageInfo.getPageIndex() * pageInfo.getPageSize() + "," + pageInfo.getPageSize();
        }
        
        Cursor cursor = null;
        List<StorageVolumeMedia> medias = new ArrayList<StorageVolumeMedia>();
        
        try {
            cursor = db.query(TableKmBoxStorageVolumeMedia.NAME, TABLE_MEDIA_ALL_COL, null, null, null, null, null, limit);
            
            while(cursor.moveToNext()) {
            	StorageVolumeMedia media = new StorageVolumeMedia.Builder()
            		.type(cursor.getInt(TableKmBoxStorageVolumeMedia.Column.TYPE.ordinal()))
            		.songId(cursor.getInt(TableKmBoxStorageVolumeMedia.Column.SONG_ID.ordinal()))
            		.originalInfo(cursor.getInt(TableKmBoxStorageVolumeMedia.Column.ORIGINAL_INFO.ordinal()))
            		.companyInfo(cursor.getInt(TableKmBoxStorageVolumeMedia.Column.COMPANY_INFO.ordinal()))
            		.volume(cursor.getInt(TableKmBoxStorageVolumeMedia.Column.VOLUME.ordinal()))
            		.uuid(cursor.getString(TableKmBoxStorageVolumeMedia.Column.VOLUME_UUID.ordinal()))
            		.subtitle(cursor.getString(TableKmBoxStorageVolumeMedia.Column.SUBTITLE.ordinal()))
            		.build();

                medias.add(media);
            }
        } catch (Exception e) {
            e.printStackTrace();
            UmengAgentUtil.reportError(e.getMessage());
            return Collections.emptyList();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            
            db.close();
        }
        
        return medias;
    }
    
    public boolean save(StorageVolumeMedia media) {
    	SQLiteDatabase db = mDAOHelper.getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        
        String sqlDelete = "delete from " 
        		+ TableKmBoxStorageVolumeMedia.NAME 
        		+ " where " + TableKmBoxStorageVolumeMedia.Column.SONG_ID.getName() 
        		+ "=?";

        String sqlInsert = "insert into " + TableKmBoxStorageVolumeMedia.NAME + "(" 
                + TableKmBoxStorageVolumeMedia.Column.TYPE.getName() + ","
                + TableKmBoxStorageVolumeMedia.Column.SONG_ID.getName() + ","
                + TableKmBoxStorageVolumeMedia.Column.ORIGINAL_INFO.getName() + ","
                + TableKmBoxStorageVolumeMedia.Column.COMPANY_INFO.getName() + ","
                + TableKmBoxStorageVolumeMedia.Column.VOLUME.getName() + ","
                + TableKmBoxStorageVolumeMedia.Column.VOLUME_UUID.getName() + ","
                + TableKmBoxStorageVolumeMedia.Column.SUBTITLE.getName() + ")"
                + " values(?,?,?,?,?,?,?)";

        try {
	        SQLiteStatement statInsert = db.compileStatement(sqlInsert);
	        SQLiteStatement statDelete = db.compileStatement(sqlDelete);
	        Media m = media.media();
	        
	        db.beginTransaction();
	        
	        statDelete.bindLong(1,  m.getSongId());
	        statDelete.execute();
	        
	        statInsert.bindLong(1, m.getType());
	        statInsert.bindLong(2, Integer.valueOf(m.getSongId()));
	        statInsert.bindLong(3, m.getOriginalTrack());
	        statInsert.bindLong(4, m.getCompanyTrack());
	        statInsert.bindLong(5, m.getVolume());
	        statInsert.bindString(6, m.getVolumeUUID());
	        statInsert.bindString(7, m.getLocalSubtitleName());
	        statInsert.executeInsert();
	        
	        db.setTransactionSuccessful();
	        db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            UmengAgentUtil.reportError(e.getMessage());
            return false;
        } finally {
            db.close();
        }

        return true;
    }
    
    public void delete(StorageVolumeMedia media) {
    	SQLiteDatabase db = mDAOHelper.getWritableDatabase();
        
        if (db == null) {
            return;
        }
        
        String sqlDelete = "delete from " 
        		+ TableKmBoxStorageVolumeMedia.NAME 
        		+ " where " + TableKmBoxStorageVolumeMedia.Column.SONG_ID.getName() 
        		+ "=?";
        
        try {
	        SQLiteStatement statDelete = db.compileStatement(sqlDelete);
	        Media m = media.media();
	        
	        db.beginTransaction();
	        
	        statDelete.bindLong(1,  m.getSongId());
	        statDelete.execute();
	        
	        db.setTransactionSuccessful();
	        db.endTransaction();
        
        } catch (Exception e) {
            e.printStackTrace();
            UmengAgentUtil.reportError(e.getMessage());
        } finally {
            db.close();
        }
    }
}
