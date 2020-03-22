package com.evideo.kmbox.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.evideo.kmbox.model.dao.data.SongSubject;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.CommonUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;

/**
 * [SQLite数据库管理类]
 */
public final class DAOHelper extends SQLiteOpenHelper implements
        DatabaseConstants {
    private static DAOHelper sInstance = null;
    private static Context sContext = null;
    private static final int COPY_BUFFER_LENGTH = 1444;
    private static final int INIT_BUFFER_LENGTH = 8192;
    private static final String WHOLE_DB = "wholedb";
    
    /**
     * 如果kmbox.jpg添加新表，请注册检查
     */
    private static final String[] CheckTableNames = new String[]{
            "tblConfig", "tblFavoriteList",
            "tblMedia", "tblMediaCache",
            "tblRecordList", "tblSelectedList",
            "tblSinger",/* "tblSingerSel",*/
            "tblSong", "tblSongId",
            "tblSongMenu", "tblSongMenuDetail",
           /* "tblSongSel",*/ "tblStorageVolume",
            "tblSubtitle", "tblSungList",
            "tblVersion"
     };
    /**
     * [获取单例]
     * @return 返回单例
     */
    public static DAOHelper getInstance() {
        if (sInstance == null) {
            synchronized (DAOHelper.class) {
                if (sInstance == null) {
                    sInstance = new DAOHelper(sContext, DATABASE_NAME, null, DATABASE_VERSION);
                }
            }
        }

        return sInstance;
    }

    public DAOHelper(Context context, String name, CursorFactory factory,
                int version) {
        super(context, name, factory, version);
        // TODO
        
    }
    
    private static boolean isDatabaseValid() {
        SQLiteDatabase db = null;
        try {
            db = DAOHelper.getInstance().getReadableDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
            db = null;
        }
        return (db != null);
    }
    /**
     * [初始化]
     * @param context 对Context的引用
     */
    public static synchronized void init(Context context) {
        sContext = new DatabaseContext(context);
        
        boolean init = true;
        
        if (isExistDatabase()) {
            boolean needResetDB = false;
            if (!isDatabaseValid()) {
                EvLog.e("DAOHelper init data base invalid,reset database ");
                needResetDB = true;
            }
            if (!DAOHelper.getInstance().checkDAOTables()) {
                EvLog.e("DAOHelper init checkDAOTables failed,reset database ");
                needResetDB = true;
            }
            
            // 重置曲库
            if (needResetDB) {
                FileUtil.deleteAllFiles(ResourceSaverPathManager.getInstance().getDBSavePath()/*KmConfig.DB_SAVE_PATH*/);
                FileUtil.deleteAllFiles(ResourceSaverPathManager.getInstance().getResourceSavePath()/*KmConfig.RESOURCE_SAVE_PATH*/);
                FileUtil.deleteAllFiles(ResourceSaverPathManager.getInstance().getRecordSavePath()/*RecordConfig.LOCAL_RECORD_SAVE_PATH*/);
                
                File dbDir = new File(ResourceSaverPathManager.getInstance().getDBSavePath()/*KmConfig.DB_SAVE_PATH*/);

                if (!dbDir.exists()) {
                    dbDir.mkdirs();
                }
                
                File resourceDir = new File(ResourceSaverPathManager.getInstance().getResourceSavePath()/*KmConfig.RESOURCE_SAVE_PATH*/);

                if (!resourceDir.exists()) {
                    resourceDir.mkdirs();
                }
                
                //重置曲库，需要重置待删歌曲版本
                SongSubject.saveVersionSongsToBeDeleted(0);
            } else {
                init = false;
            }
        } else {
            EvLog.i("db is not exist,need initDatabase");
        }
        
        if (init) {
            initDatabase();
        }
    }
    
    public boolean attachDatabase(String fullFilePath, String name) {
        String sql = "Attach '" + fullFilePath + "' as '" + name + "'";
        
        this.getWritableDatabase().execSQL(sql);
        
        return true;
    }
    
    public void detachDatabase(String name) {
        String sql = "Detach database '" + name + "'";
        this.getWritableDatabase().execSQL(sql);
    }
    
    /**
    * [attach wholedb数据库]
    */
    public void initBaseData() {
        
        String wholedbPath = FileUtil.concatPath(ResourceSaverPathManager.getInstance().getDBSavePath(), "whole_kmbox.db");
        try {
            this.close();
        } catch (Exception e) {
            EvLog.w("close local_kmbox.db Failed!");
            UmengAgentUtil.reportError(e);
        }
        try {
            this.attachDatabase(wholedbPath, WHOLE_DB);
        } catch (Exception e) {
            EvLog.i(e.toString());
            UmengAgentUtil.reportError(e);
        }

        String sql = "delete from tblSongId";
        this.getWritableDatabase().execSQL(sql);
    }
    
    public static void copy(String oldFullFilePath, String newPath) {

        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldFullFilePath);

            if (oldfile.exists()) {
                inStream = new FileInputStream(oldfile);
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[COPY_BUFFER_LENGTH];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    EvLog.i(bytesum + "");
                    fs.write(buffer, 0, byteread);
                }
            }
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
        } finally {
            CommonUtil.safeClose(inStream);
            CommonUtil.safeClose(fs);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // upgrade database
        EvLog.i("DAOHelper oldVersion ", String.valueOf(oldVersion));
        EvLog.i("DAOHelper newVersion ", String.valueOf(newVersion));
        
        try {
            if (oldVersion <= 2) {
                String sql = "ALTER TABLE tblMediaCache ADD priority int default 0";
                db.execSQL(sql);
                oldVersion = 3;
            }
            
            if (oldVersion <= 3) {
                db.execSQL("create table if not exists tblSelectedList(id INTEGER PRIMARY KEY,"
                        + "songid int,canscore int,sequence int)");
                oldVersion = 4;
            }
            
            if (oldVersion == 4) {
                db.execSQL("alter table tblSelectedList add customerId NVARCHAR");
                oldVersion++;
            }
            
            if (oldVersion == 5) {
                db.execSQL("insert into tblConfig values('forceUpdate',1)");
                oldVersion++;
            }
    
            db.execSQL("create table if not exists tblFavoriteList(songid int)");
    
            if (oldVersion == 6) {
                db.execSQL(DDL_CREATE_TABLE_SONG_MENU);
                db.execSQL(DDL_CREATE_TABLE_SONG_MENU_DETAIL);
                db.execSQL(DDL_TRG_AFTER_DELETE_SONG_MENU);
                oldVersion++;
            }
            
            if (oldVersion == 7) {
                db.execSQL(DDL_CREATE_SUNG_TABLE);
                db.execSQL(DDL_CREATE_RECORD_TABLE);
                oldVersion++;
            }
            
            if (oldVersion == 8) {
                db.execSQL("create table if not exists tblSongId(id int,path NVARCHAR,uuid NVARCHAR)");
                db.execSQL("ALTER TABLE tblSong ADD hasLocal int default 0");
                if (!checkColumnExists(db, "tblSong", "hasRemote")) {
                	db.execSQL("ALTER TABLE tblSong ADD hasRemote int default 0");
                }
                db.execSQL("drop trigger [trgAfterDeleteMedia]");
                db.execSQL("drop trigger [trgAfterInsertMedia]");
                db.execSQL("drop trigger [trgAfterUpdateMediaOf]");
                db.execSQL("CREATE TRIGGER [trgAfterDeleteVolume] AFTER DELETE ON [tblMedia] "
                        + "BEGIN DELETE  FROM tblSong where id = old.[songId] and hasRemote = 0;END");
                db.execSQL("update tblSong set hasRemote = 1");
                oldVersion++;
            }
            
            if (oldVersion == 9) {
                db.execSQL(DDL_ALTER_TABLE_SONG_MENU_ADD_IMAGEURLBIG);
                db.execSQL(DDL_CREATE_TABLE_SONG_TOP);
                db.execSQL(DDL_CREATE_TABLE_SONG_TOP_DETAIL);
                db.execSQL(DDL_TRG_AFTER_DELETE_SONG_TOP);
                db.execSQL(DDL_CREATE_TABLE_SEARCH_HISTORY);
                oldVersion++;
            }
			
			  
       		 if (oldVersion <= 10) {
           		 db.execSQL(DDL_ALTER_TABLE_STORAGEVOLEM_ADD_RESOURCESIZE);
           		 oldVersion++;
       		 }
       		 
       		 if (oldVersion <= 11) {
       		     EvLog.d(DDL_ALTER_TABLE_RECORD_ADD_RECORD_FILEID);
       		     db.execSQL(DDL_ALTER_TABLE_RECORD_ADD_RECORD_FILEID);
       		     db.execSQL(DDL_ALTER_TABLE_RECORD_ADD_EUR_FILEID);
       		     oldVersion++;
       		 }
       		 //media 表加入resourceSize与resourceId字段
            if (oldVersion <= 12) {
                EvLog.d(DDL_ALTER_TABLE_MEDIA_ADD_RESOURCE_PATH);
                db.execSQL(DDL_ALTER_TABLE_MEDIA_ADD_RESOURCE_PATH);
                db.execSQL(DDL_ALTER_TABLE_MEDIA_ADD_RESOURCE_SIZE);
                oldVersion++;
            }
       		 
            //recordList 表中加入mixprogress与uploadduochang
            if (oldVersion <= 13) {
                db.execSQL(DDL_ALTER_TABLE_RECORD_ADD_MIX_PROGRESS);
                db.execSQL(DDL_ALTER_TABLE_RECORD_ADD_UPLOAD_DUOCHANG);
                oldVersion++;
            }
            
            //recordList 表中加入mixprogress与uploadduochang
            if (oldVersion <= 14) {
                db.execSQL(DDL_ALTER_TABLE_FAVORITE_LIST_UPLOAD_DUOCHANG);
                oldVersion++;
            }
            
            
            //media 表添加duration字段
            if (oldVersion <= 15) {
                EvLog.e(">>>>>>>>>>=" + DDL_ALTER_TABLE_MEDIA_ADD_DURATION);
                db.execSQL(DDL_ALTER_TABLE_MEDIA_ADD_DURATION);
                oldVersion++;
            }
            if (oldVersion <= 16) {
                EvLog.e(">>>>>>>>>>=" + DDL_ALTER_TABLE_SONG_TOP_DETAIL_ADD_ORDERRATE);
                db.execSQL(DDL_ALTER_TABLE_SONG_TOP_DETAIL_ADD_ORDERRATE);
                oldVersion++;
            }
            if (oldVersion <= 17) {
                EvLog.e(">>>>>>>>>>=" + DDL_CREATE_TABLE_FREESONG);
                db.execSQL(DDL_CREATE_TABLE_FREESONG);
                oldVersion++;
            }
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
        }
    }
    

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.enableWriteAheadLogging();
    }
    
    private boolean checkColumnExists(SQLiteDatabase db, String tableName, String columnName) {
        boolean result = false ;
        Cursor cursor = null ;

        try {
            cursor = db.rawQuery( "select * from sqlite_master where name = ? and sql like ?"
               , new String[]{tableName , "%" + columnName + "%"} );
            result = null != cursor && cursor.moveToFirst() ;
        } catch (Exception e) {
            Log.e("DAOHelper","checkColumnExists2..." + e.getMessage()) ;
            UmengAgentUtil.reportError(e);
        } finally {
            if(null != cursor && !cursor.isClosed()) {
                cursor.close() ;
            }
        }

        return result ;
    }

    private static boolean isExistDatabase()    {
        File dbFile = sContext.getDatabasePath(DATABASE_NAME);
        
        if (dbFile == null) {
            return false;
        }
        
        return dbFile.exists();
    }
        
    private static void initDatabase() {
        String dbFullPath = ResourceSaverPathManager.getInstance().getDBSavePath()/*KmConfig.DB_SAVE_PATH*/ + "/" + DATABASE_NAME;
        
        File dbDir = new File(ResourceSaverPathManager.getInstance().getDBSavePath()/*KmConfig.DB_SAVE_PATH*/);

        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
        
        FileOutputStream os = null;
        
        try {
            os = new FileOutputStream(dbFullPath);
        } catch (FileNotFoundException e) {
            UmengAgentUtil.reportError(e);
        }
        
        InputStream is = null;
        try {
            is = sContext.getAssets().open("kmbox.jpg");
        } catch (IOException e1) {
            CommonUtil.safeClose(os);
            UmengAgentUtil.reportError(e1);    
            return;
        }
        
        // mContext.getResources().openRawResource(R..kmbox);
        EvLog.e("db copy from asserts");
        byte[] buffer = new byte[INIT_BUFFER_LENGTH];
        int count = 0;
        
        try {
            while ((count = is.read(buffer)) > 0) {
                os.write(buffer, 0, count);
                os.flush();
            }
        } catch (IOException e) {
            UmengAgentUtil.reportError(e);
        } finally {
            CommonUtil.safeClose(os);
            CommonUtil.safeClose(is);
        }
    }
    
    /**
     * detach whole_db
     */
    public void initBaseDataFinished() {
        this.detachDatabase(WHOLE_DB);
    }
    
    /**
     *  检查表是否存在
     * @return
     */
    private boolean checkDAOTables(){
        EvLog.d("check dao tables !");
        return tabbleIsExist(CheckTableNames);
    }
    
    /**
     * 判断某张表是否存在
     * @param tabName 表名
     * @return
     */
    public boolean tabbleIsExist(String[] tableNames) {
        boolean result = false;
        if (tableNames == null) {
            return false;
        }
        if (tableNames.length <= 0) {
            return false;
        }
        SQLiteDatabase db = null;
        Cursor cursor = null;
        StringBuilder sb = new StringBuilder();
        try {
            sb = new StringBuilder();
            for (int i = 0; i < tableNames.length; i++) {
                sb.append("'");
                sb.append(tableNames[i].trim());
                sb.append("',");
            }
            if (sb.length() <= 0) {
                return false;
            }
            sb.deleteCharAt(sb.length() - 1);
            
            db = this.getReadableDatabase();
            // 这里表名可以是sqlite_master
            String sql = "select count(*) asc from "
                    + "sqlite_master"
                    + " where type ='table' and name in (" + sb.toString()
                    + ") ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count >= tableNames.length) {
                    result = true;
                }
            }
        } catch (Exception e) {
            EvLog.e("check tables : " + sb.toString() + " " 
                            + e.getLocalizedMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
        return result;
    }
}
