package com.evideo.kmbox.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.storage.MediaCacheItem;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;

public class CacheManagerDAO {
    // table config
    private static final String TAG = CacheManagerDAO.class.getSimpleName();
    
    private static final String TABLE_CACHE_MANAGER_NAME = "tblMediaCache";
    private static final String TABLE_CACHE_MANAGER_COL_ID = "id";
    private static final String TABLE_CACHE_MANAGER_COL_MEDIAID = "mediaId";
    private static final String TABLE_CACHE_MANAGER_COL_FILE = "file";
    private static final String TABLE_CACHE_MANAGER_COL_TYPE = "type";
    private static final String TABLE_CACHE_MANAGER_COL_PRIORITY = "priority";
    
    private static final String[] TABLE_CACHE_MANAGER_ALL_COL = { TABLE_CACHE_MANAGER_COL_ID, TABLE_CACHE_MANAGER_COL_MEDIAID,
        TABLE_CACHE_MANAGER_COL_FILE, TABLE_CACHE_MANAGER_COL_TYPE, TABLE_CACHE_MANAGER_COL_PRIORITY};
    
    public boolean addMediaCache(int mediaId, String fullFilePath) {
        
        if (fullFilePath == null || fullFilePath.isEmpty()) {
            return false;
        }
        
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if(db == null) {
            return false;
        }
        
        ContentValues values = new ContentValues();

        values.put(TABLE_CACHE_MANAGER_COL_MEDIAID, mediaId);
        values.put(TABLE_CACHE_MANAGER_COL_FILE, fullFilePath);
        values.put(TABLE_CACHE_MANAGER_COL_TYPE, MediaCacheItem.Type_Media);
        
        long rowEffect = -1;
        try {
            rowEffect = db.insert(TABLE_CACHE_MANAGER_NAME, null, values);
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        
        return rowEffect > 0;
    }
    
    public boolean addSubtitleCache(int mediaId, String fullFilePath) {
        
        if (fullFilePath == null || fullFilePath.isEmpty()) {
            return false;
        }
        
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if(db == null) {
            return false;
        }
        
        ContentValues values = new ContentValues();

        values.put(TABLE_CACHE_MANAGER_COL_MEDIAID, mediaId);
        values.put(TABLE_CACHE_MANAGER_COL_FILE, fullFilePath);
        values.put(TABLE_CACHE_MANAGER_COL_TYPE, MediaCacheItem.Type_Subtitle);
        
        long rowEffect = -1;
        try {
            rowEffect = db.insert(TABLE_CACHE_MANAGER_NAME, null, values);
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        
        return rowEffect > 0;
    }
    
    public List<MediaCacheItem> getDeletableMediaCacheList(String path, PageInfo pageInfo) {
        Cursor cursor = null;
        String limit = "";
        if (pageInfo != null) {
            limit = pageInfo.getPageIndex() * pageInfo.getPageSize() + "," + pageInfo.getPageSize();
        }

        String orderBy = TABLE_CACHE_MANAGER_COL_ID + " asc";
        String selection = TABLE_CACHE_MANAGER_COL_PRIORITY + "<=" + 0;

        if (path != null && !path.isEmpty()) {
        	selection += " and " + TABLE_CACHE_MANAGER_COL_FILE + " like '"
            		+ path + "%'";
        }

        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return Collections.emptyList();
        }

        List<MediaCacheItem> list = new ArrayList<MediaCacheItem>();

        try {
            cursor = db.query(TABLE_CACHE_MANAGER_NAME, TABLE_CACHE_MANAGER_ALL_COL, selection, null, null, null, orderBy, limit);
            
            while(cursor.moveToNext()) {
                MediaCacheItem item = new MediaCacheItem(cursor.getInt(0), cursor.getInt(1), cursor.getString(2),
                        0, cursor.getInt(3), cursor.getInt(4));
                list.add(item);
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

        return list;
    }
    
    public List<MediaCacheItem> getDeletableMediaCacheList(PageInfo pageInfo) {
        Cursor cursor = null;
        String limit = "";
        if (pageInfo != null) {
            limit = pageInfo.getPageIndex() * pageInfo.getPageSize() + "," + pageInfo.getPageSize();
        }

        String orderBy = TABLE_CACHE_MANAGER_COL_ID + " asc";
        String selection = TABLE_CACHE_MANAGER_COL_PRIORITY + "<=" + 0;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return Collections.emptyList();
        }

        List<MediaCacheItem> list = new ArrayList<MediaCacheItem>();

        try {
            cursor = db.query(TABLE_CACHE_MANAGER_NAME, TABLE_CACHE_MANAGER_ALL_COL, selection, null, null, null, orderBy, limit);
            
            while(cursor.moveToNext()) {
                MediaCacheItem item = new MediaCacheItem(cursor.getInt(0), cursor.getInt(1), cursor.getString(2),
                        0, cursor.getInt(3), cursor.getInt(4));
                list.add(item);
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

        return list;
    }
    
    public void deleteMediaCacheItem(int id) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return;
        }
        
        try {
            db.delete(TABLE_CACHE_MANAGER_NAME, TABLE_CACHE_MANAGER_COL_ID + " = ?", new String[]{id + ""});
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
    }
    
    public boolean isExist(int MediaId) {
        
        boolean exist = false;
        Cursor cursor = null;
        String selection = TABLE_CACHE_MANAGER_COL_MEDIAID + "=" + MediaId;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return false;
        }
        
        try {
            cursor = db.query(TABLE_CACHE_MANAGER_NAME, new String[]{TABLE_CACHE_MANAGER_COL_ID}, selection, null, null, null, null);
            exist = cursor.moveToNext();
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
            e.printStackTrace();
            return false;
        } finally {
            if(cursor != null) {
                cursor.close();
            }
            
            //db.close();
        }

        return exist;
    }
    
    public boolean isExist(String fullFilePath) {
        
        if (fullFilePath == null) {
            return false;
        }
        
        boolean exist = false;
        Cursor cursor = null;
        String selection = TABLE_CACHE_MANAGER_COL_FILE + "='" + fullFilePath + "'";
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return false;
        }
        
        try {
            cursor = db.query(TABLE_CACHE_MANAGER_NAME, new String[]{TABLE_CACHE_MANAGER_COL_ID}, selection, null, null, null, null);
            exist = cursor.moveToNext();
        } catch (Exception e) {
            e.printStackTrace();
            UmengAgentUtil.reportError(e);
            return false;
        } finally {
            if(cursor != null) {
                cursor.close();
            }
            
            //db.close();
        }

        return exist;
    }
    
    public boolean updateMediaInfo(int mediaId, String fullFilePath) {
        
        if (fullFilePath == null) {
            return false;
        }

        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(TABLE_CACHE_MANAGER_COL_MEDIAID, mediaId);
            db.update(TABLE_CACHE_MANAGER_NAME, cv, TABLE_CACHE_MANAGER_COL_FILE + "=?", new String[]{fullFilePath});
        } catch (Exception e) {
            UmengAgentUtil.reportError("update media(" + mediaId + ") info failed:" + fullFilePath + "." + e.getMessage());
            return false;
        }

        return true;
    }
    
    public boolean updateMediaCache(int mediaId, String fullFilePath) {
        if (fullFilePath == null) {
            return false;
        }

        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(TABLE_CACHE_MANAGER_COL_FILE, fullFilePath);
            String whareClause = TABLE_CACHE_MANAGER_COL_MEDIAID + " = ? " + " and " + TABLE_CACHE_MANAGER_COL_TYPE + " = ?";
            db.update(TABLE_CACHE_MANAGER_NAME, cv, whareClause, new String[]{String.valueOf(mediaId),String.valueOf(MediaCacheItem.Type_Media)});
        } catch (Exception e) {
            UmengAgentUtil.reportError("update media(" + mediaId + ") info failed:" + fullFilePath + "." + e.getMessage());
            return false;
        }

        return true;
    }
    
    public boolean updateSubtitleCache(int mediaId, String fullFilePath) {
        if (fullFilePath == null) {
            return false;
        }

        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(TABLE_CACHE_MANAGER_COL_FILE, fullFilePath);
            String whareClause = TABLE_CACHE_MANAGER_COL_MEDIAID + " = ? " + " and " + TABLE_CACHE_MANAGER_COL_TYPE + " = ?";
            db.update(TABLE_CACHE_MANAGER_NAME, cv, whareClause, new String[]{String.valueOf(mediaId),String.valueOf(MediaCacheItem.Type_Subtitle)});
        } catch (Exception e) {
            UmengAgentUtil.reportError("update media(" + mediaId + ") info failed:" + fullFilePath + "." + e.getMessage());
            return false;
        }

        return true;
    }
    
    public boolean lockResource(long mediaId) {
        EvLog.i(TAG, "locked mediaId :" + String.valueOf(mediaId));
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if(db == null){
            EvLog.d(TAG, "open db fail.");
            return false;
        }
        try {
            ContentValues cv = new ContentValues();
            cv.put("priority", 1);
            db.update(TABLE_CACHE_MANAGER_NAME, cv, TABLE_CACHE_MANAGER_COL_MEDIAID + "=?", new String[]{String.valueOf(mediaId)});
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    public boolean unlockResource(long mediaId) {
        EvLog.i(TAG,"unlocked mediaId :" + String.valueOf(mediaId));
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if(db == null){
            EvLog.d(TAG,"open db fail.");
            return false;
        }
        try {
            ContentValues cv = new ContentValues();
            cv.put("priority", 0);
            db.update(TABLE_CACHE_MANAGER_NAME, cv, TABLE_CACHE_MANAGER_COL_MEDIAID + "=?", new String[]{String.valueOf(mediaId)});
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    public void unlockResourceExcept(long mediaId) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if(db == null){
            EvLog.d(TAG,"open db fail.");
            return;
        }
        try {
            ContentValues cv = new ContentValues();
            cv.put("priority", 0);
            db.update(TABLE_CACHE_MANAGER_NAME, cv, TABLE_CACHE_MANAGER_COL_MEDIAID + "!=?", new String[]{String.valueOf(mediaId)});
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
            e.printStackTrace();
        }
    }
    
}
