package com.evideo.kmbox.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.evideo.kmbox.model.dao.data.StorageVolume;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;

public class StorageVolumeDAO {
    
    // table singer
    private static final String TABLE_STORAGE_VOLUME_NAME = "tblStorageVolume";
    private static final String TABLE_STORAGE_VOLUME_COL_UUID = "uuid";
    private static final String TABLE_STORAGE_VOLUME_COL_LABEL = "label";
    private static final String TABLE_STORAGE_VOLUME_COL_PATH = "path";
    private static final String TABLE_STORAGE_VOLUME_COL_RESSIZE = "resourcesize";
    private static final String[] TABLE_STORAGE_VOLUME_ALL_COL = { TABLE_STORAGE_VOLUME_COL_UUID, 
        TABLE_STORAGE_VOLUME_COL_LABEL, TABLE_STORAGE_VOLUME_COL_PATH };

    public StorageVolumeDAO() {
    }
    
    public boolean isExist(StorageVolume volume) {
    	SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return false;
        }
 
        int count = 0;
        String selection = TABLE_STORAGE_VOLUME_COL_UUID + "='" + volume.getUUID() + "'";
        Cursor cursor = null;
        
        try {
	        cursor = db.query(TABLE_STORAGE_VOLUME_NAME, new String[]{"count(*)"}, selection, null, null, null, null);
	
	        if (cursor.moveToNext()) {
	            count = cursor.getInt(0);
	        }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
        	if (cursor != null) {
                cursor.close();
            }
        }

        return count > 0;
    }

    public boolean add(StorageVolume volume) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        
        ContentValues values = new ContentValues();

        values.put(TABLE_STORAGE_VOLUME_COL_UUID, volume.getUUID());
        values.put(TABLE_STORAGE_VOLUME_COL_LABEL, volume.getLabel());
        values.put(TABLE_STORAGE_VOLUME_COL_PATH, volume.getPath());
        
        long rowEffect = 0;
        
        try {
        	rowEffect = db.insert(TABLE_STORAGE_VOLUME_NAME, null, values);
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        
        return rowEffect > 0;
    }
    
    public boolean updateStorageVolumeResSize(String uuid, long size) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null || TextUtils.isEmpty(uuid)) {
            return false;
        }
        
        ContentValues values = new ContentValues();
        values.put(TABLE_STORAGE_VOLUME_COL_RESSIZE, size);
        long rowEffect = 0;
        
        try {
            rowEffect = db.update(TABLE_STORAGE_VOLUME_NAME, values, TABLE_STORAGE_VOLUME_COL_UUID + " = ?", 
                    new String[]{uuid});
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        
        return rowEffect > 0;
    }
    
    public boolean updateStorageVolumePath(String uuid, String path) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null || TextUtils.isEmpty(uuid)) {
            return false;
        }
        
        ContentValues values = new ContentValues();
        values.put(TABLE_STORAGE_VOLUME_COL_PATH, path);
        long rowEffect = 0;
        
        try {
            rowEffect = db.update(TABLE_STORAGE_VOLUME_NAME, values, TABLE_STORAGE_VOLUME_COL_UUID + " = ?", 
                    new String[]{uuid});
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        
        return rowEffect > 0;
    }
    
    public long getStorageVolumeResSize(String uuid) {
        Cursor cursor = null;
        String selection = TABLE_STORAGE_VOLUME_COL_UUID + "='" + uuid + "'";
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        long size = 0;
        if (db == null || TextUtils.isEmpty(uuid)) {
            return 0;
        }
        
        try {
            cursor = db.query(TABLE_STORAGE_VOLUME_NAME, new String[]{TABLE_STORAGE_VOLUME_COL_RESSIZE},
                    selection, null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                size = cursor.getLong(0);
            }
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return size;
    }
    
    public String getStorageVolumePath(String uuid) {
        Cursor cursor = null;
        String selection = TABLE_STORAGE_VOLUME_COL_UUID + "='" + uuid + "'";
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        String path = "";
        if (db == null || TextUtils.isEmpty(uuid)) {
            return "";
        }
        
        try {
            cursor = db.query(TABLE_STORAGE_VOLUME_NAME, new String[]{TABLE_STORAGE_VOLUME_COL_PATH},
                    selection, null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                path = cursor.getString(0);
            }
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
            return path;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return path;
    }
    
    

    public void remove(String uuid) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return;
        }
        
        try {
            db.delete(TABLE_STORAGE_VOLUME_NAME, TABLE_STORAGE_VOLUME_COL_UUID + " = ?",
                    new String[]{String.valueOf(uuid)});
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
    }
    
    public List<StorageVolume> getList() {
        Cursor cursor = null;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return Collections.emptyList();
        }
        
        List<StorageVolume> volumes = new ArrayList<StorageVolume>();
        
        try {
            cursor = db.query(TABLE_STORAGE_VOLUME_NAME, TABLE_STORAGE_VOLUME_ALL_COL, null, null, null, null, null);
            
            while (cursor.moveToNext()) {
                StorageVolume volume = new StorageVolume(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                volumes.add(volume);
            }
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
            return Collections.emptyList();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            
            //db.close();
        }
        
        return volumes;
    }
    
    public boolean isExist(String uuid) {
        boolean exist = false;
        Cursor cursor = null;
        String selection = TABLE_STORAGE_VOLUME_COL_UUID + "='" + uuid + "'";
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return false;
        }
        
        try {
            cursor = db.query(TABLE_STORAGE_VOLUME_NAME, new String[]{TABLE_STORAGE_VOLUME_COL_UUID},
                    selection, null, null, null, null);
            exist = cursor.moveToNext();
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            
            //db.close();
        }

        return exist;
    }
    
    public boolean isExist(String uuid, String path) {
        boolean exist = false;
        exist = isExist(uuid);
        if (exist) {
            if (!getStorageVolumePath(uuid).equals(path)) {
                updateStorageVolumePath(uuid, path);
            }
        }
        return exist;
    }
}
