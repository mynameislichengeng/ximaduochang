package com.evideo.kmbox.dao;

import java.io.File;
import java.io.IOException;

import com.evideo.kmbox.model.dao.data.StorageConstant;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class StorageVolumeDbContext extends ContextWrapper {
	
	private String mVolumePath = "";

	public StorageVolumeDbContext(Context base, String volumePath) {
		super(base);
		mVolumePath = volumePath;
	}

	@Override
    public File getDatabasePath(String name) {

        String dbDir = FileUtil.concatPath(mVolumePath, StorageConstant.STORAGE_VOLUME_KMBOX_ROOT);
//        EvLog.e(" getDatabasePath dbPath " + dbDir);

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
        	try {
				dbFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        return dbFile;
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
