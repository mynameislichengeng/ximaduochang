package com.evideo.kmbox.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class SongIdLocalDAO extends SongIdDAO {
    
    // table song
    private static final String TABLE_SONG_ID = "tblSongId";
    private static final String TABLE_SONG_COL_ID = "id";

    public SongIdLocalDAO() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean clearList() {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            return false;
        }
        db.execSQL("delete from " + TABLE_SONG_ID);
        return true;
    }

    @Override
    public boolean executeSongUpdateSql(String str) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return false;
        }
        String sql = "update tblSong set hasLocal=1 where id in ("+str+");";
        db.execSQL(sql);
        return true;
    }

    @Override
    public boolean executeMediaUpdateSql(String str, int formatIndex, String uuid, String ext) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return false;
        }
        if (TextUtils.isEmpty(uuid)) {
            return false;
        }
        
        String fileName = "substr('00000000'||id, -8, 8)||'.'||'" + ext + "'";
        String sql = "insert into tblMedia(type,songId,originalInfo,companyInfo,volume,updateTime,volumeUUID,localResource)"
                + " select " + formatIndex + ",[id],0,1,10,"
                        + "[updateTime], '" + uuid + "'," + fileName + " from tblSong where [id] in (" + str + ")";
        db.execSQL(sql);
        return false;
    }

    @Override
    public boolean executeMediaUpdateUUID(String idCollection, String uuid) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return false;
        }
        String localSongIds = SongManager.getInstance().getSongIdCollection();
        String sql = "update tblMedia set volumeUUID = '" + uuid + "' where SongId in (" + idCollection + ")"
                + " not in (" + localSongIds + ")";
        db.execSQL(sql);
        return true;
    }

    @Override
    public Map<Integer, String> getNotIdentifiedEvSong() {
        Cursor cursor = null;
        Map<Integer, String> map = new HashMap<Integer, String>();
        
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        String sql = "drop table if exists tblNotindentified";
        db.execSQL(sql);
        sql = "create table if not exists tblNotindentified ([id] integer PRIMARY KEY autoincrement, path VARCHAR, uuid VARCHAR);";
        db.execSQL(sql);
        sql = "delete from tblnotindentified";
        db.execSQL(sql);
        sql = "insert into tblnotindentified select tblSongId.* "
                + "from tblSongId where tblSongId.[id] not in (select tblSongId.[id] from tblSong)";
        db.execSQL(sql);
        
        String selection = TABLE_SONG_COL_ID + ">0";
        String[] tblnotindentify = { "id", "path" };
        try {
            cursor = db.query("tblnotindentified", tblnotindentify, selection,
                    null, null, null, null, null);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                map.put(id, name);
            }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
            return (Map<Integer, String>) Collections.emptyList();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        sql = "drop table tblnotindentified";
        db.execSQL(sql);

        return map;          
    }

}
