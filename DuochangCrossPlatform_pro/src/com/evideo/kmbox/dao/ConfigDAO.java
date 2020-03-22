package com.evideo.kmbox.dao;

import java.util.HashMap;

import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * [曲库数据库配置及版本信息]
 */
public class ConfigDAO {
    
    private boolean mCached = false;
    private HashMap<String, String> mConfig;
    
    // table config
    private static final String TABLE_CONFIG_NAME = "tblConfig";
    private static final String TABLE_CONFIG_COL_KEY = "key";
    private static final String TABLE_CONFIG_COL_VALUE = "value";
    private static final String[] TABLE_CONFIG_ALL_COL = { TABLE_CONFIG_COL_KEY, TABLE_CONFIG_COL_VALUE };
    
    private static final String TABLE_VERSION_NAME = "tblVersion";
    private static final String TABLE_VERSION_COL_VERSION = "version";
    private static final String[] TABLE_VERSION_ALL_COL = { TABLE_VERSION_COL_VERSION };
    private static final String KEY_VERSION = "version";
    private static final String KEY_RESOURCEURL = "resourceUrl";
    private static final String KEY_FORCEUPDATE = "forceUpdate";
    private static final String VALUE_FORCEUPDATE = "1";
    private static final String VALUE_UNFORCEUPDATE = "0";
    
    ConfigDAO() {
        mConfig = new HashMap<String, String>();
    }
    
    /**
     * [获取版本]
     * @return 返回版本号
     */
    public String getVersion() {
        if (!mCached) {
            cacheConfig();
        }
        
        String value = mConfig.get(KEY_VERSION);
        
        if (value == null) {
            value = "";
            return value;
        }
        
        return value;
    }
    
    /**
     * [判断是否需要强制更新]
     * @return true:需要强制更新，false：不需要强制更新。
     */
    public boolean isForceUpdate() {
        if (!mCached) {
            cacheConfig();
        }
        
        String forceUpdate = mConfig.get(KEY_FORCEUPDATE);
        
        return VALUE_FORCEUPDATE.equals(forceUpdate);
    }
    
    /**
     * [设置是否强制更新]
     * @param forceUpdate 强制更新：true:是,false:否。
     * @return 标识设置是否成功
     */
    public boolean setForceUpdate(boolean forceUpdate) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        
        ContentValues values = new ContentValues();
        values.put(TABLE_CONFIG_COL_VALUE, forceUpdate ? 1 : 0);
        
        long effects = -1;
        try {
            effects = db.update(TABLE_CONFIG_NAME, values, 
                    TABLE_CONFIG_COL_KEY + "== ?", new String[]{KEY_FORCEUPDATE});
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        
        if (effects > 0) {
            mConfig.put(KEY_FORCEUPDATE, forceUpdate ? VALUE_FORCEUPDATE : VALUE_UNFORCEUPDATE);
        }
        
        return effects > 0;
    }
    
    /**
     * [更新版本]
     * @param version 版本号
     * @return 返回更新是否成功。
     */
    public boolean updateVersion(String version) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(TABLE_VERSION_COL_VERSION, version);
        
        long effects = -1;
        try {
            effects = db.update(TABLE_VERSION_NAME, values, null, null);
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        
        if (effects > 0) {
            mConfig.put(KEY_VERSION, version);
        }

        return effects > 0;
    }
    
    /**
     * [获取资源URL]
     * @return 返回资源URL
     */
    public String getResourceUrl() {
        if (!mCached) {
            cacheConfig();
        }
        
        String value = mConfig.get(KEY_RESOURCEURL);
        
        if (value == null) {
            value = "";
            return value;
        }
        
        return value;
    }
    
    private void cacheConfig() {

        Cursor cursor = null;
        SQLiteDatabase db = null;
        
        try {
            db = DAOHelper.getInstance().getWritableDatabase();
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
        }
        
        if (db == null) {
            return;
        }

        try {
            cursor = db.query(TABLE_CONFIG_NAME, TABLE_CONFIG_ALL_COL, null, null, null, null, null);
            
            while (cursor.moveToNext()) {
                mConfig.put(cursor.getString(0), cursor.getString(1));
            }
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        try {
            cursor = db.query(TABLE_VERSION_NAME, TABLE_VERSION_ALL_COL, null, null, null, null, null);
            
            while (cursor.moveToNext()) {
                mConfig.put(KEY_VERSION, cursor.getString(0));
            }
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
