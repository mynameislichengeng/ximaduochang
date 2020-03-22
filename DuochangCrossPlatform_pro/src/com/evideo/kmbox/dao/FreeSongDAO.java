/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年3月10日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明]
 */
public class FreeSongDAO {
    public static final String TABLE_FREE_LIST = "tblFreeSongList";
    public static final String TABLE_FREE_LIST_COL_SONGID = "songid";
    
    public List<Song> getList() {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return Collections.emptyList();
        }
        Song song = null;
        ArrayList<Song> list = new ArrayList<Song>();
        Cursor cursor = null;
        String selection = TABLE_FREE_LIST_COL_SONGID + " > 0";
        
        try {
            cursor = db.query(TABLE_FREE_LIST, new String[]{TABLE_FREE_LIST_COL_SONGID},
                    selection, null, null, null, null, null);
            while (cursor.moveToNext()) {
                song = SongManager.getInstance().getSongById(cursor.getInt(0));
                if (song != null) {
                    list.add(song);
                }
            }
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }
    
    public void update(List<Song> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return;
        }
        String sqlDelete = "delete from " + TABLE_FREE_LIST;
        try {
            db.execSQL(sqlDelete);
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
        }
        
        String sql = "insert or replace into " + TABLE_FREE_LIST + "("
                + TABLE_FREE_LIST_COL_SONGID
                + ")"
                + " values(?)";
        
        try {
            SQLiteStatement stat = db.compileStatement(sql);
            
            db.beginTransaction();
            
            for (int i = 0; i < list.size(); ++i) {
                stat.bindLong(1, list.get(i).getId());
                stat.executeInsert();
            }
            
            db.setTransactionSuccessful();
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            try {
                db.endTransaction();
            } catch (Exception e) {
            }
        }
        return;
    }
}
