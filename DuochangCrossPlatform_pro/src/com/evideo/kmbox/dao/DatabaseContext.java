package com.evideo.kmbox.dao;

import java.io.File;

import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.util.EvLog;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * 用于支持对存储在SD卡上的数据库的访问
 **/
public class DatabaseContext extends ContextWrapper {

    /**
     * 构造函数
     * 
     * @param base
     *            上下文环境
     */
    public DatabaseContext(Context base) {
        super(base);
    }

    /**
     * 获得数据库路径，如果不存在，则创建对象对象
     * @param name
     */
    @Override
    public File getDatabasePath(String name) {

        String dbDir = ResourceSaverPathManager.getInstance().getDBSavePath()/*KmConfig.DB_SAVE_PATH*/;

        File dirFile = new File(dbDir);
        if (!dirFile.exists()) {
            EvLog.e(dbDir + " is not exist  ");
            return null;
        }
        // 数据库路径
        String dbPath = dbDir + "/" + name;
        // 判断文件是否存在，不存在则创建该文件
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            EvLog.e(dbPath + " is not exist  ");
            return null;
        } else {
         // 返回数据库文件对象
            return dbFile;
        }
    }

    /**
     * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
     * 
     * @param name
     * @param mode
     * @param factory
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
            SQLiteDatabase.CursorFactory factory) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(
                getDatabasePath(name), null);
        return result;
    }

    /**
     * Android 4.0会调用此方法获取数据库。
     * 
     * @see android.content.ContextWrapper#openOrCreateDatabase(java.lang.String,
     *      int, android.database.sqlite.SQLiteDatabase.CursorFactory,
     *      android.database.DatabaseErrorHandler)
     * @param name
     * @param mode
     * @param factory
     * @param errorHandler
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
            CursorFactory factory, DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(
                getDatabasePath(name), null);
        return result;
    }
}