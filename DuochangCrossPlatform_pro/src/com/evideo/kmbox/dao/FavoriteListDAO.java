/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date                 Author             Version Description
 *  -----------------------------------------------
 *  2015年3月13日                 "wurongquan"         1.0    [修订说明]
 *
 */

package com.evideo.kmbox.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;

/**
 * [收藏列表]
 */
public class FavoriteListDAO {
    private static final String TABLE_FAVORITE_LIST = "tblFavoriteList";
    private static final String TABLE_FAVORITE_LIST_COL_SONGID = "songid";
    private static final String TABLE_FAVORITE_LIST_COL_UPLOAD = "upload";
    
    public FavoriteListDAO() {
        
    }
    
    /**
     * [加入到收藏列表]
     * @param songid 歌曲id
     * @return true:成功;false：失败
     */
    public boolean addSong(int songid) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        
        if (isAlreadyExists(songid)) {
            return true;
        }
        ContentValues values = new ContentValues();
        values.put(TABLE_FAVORITE_LIST_COL_SONGID, songid);
        long roweffect = -1;
        try {
            roweffect = db.insert(TABLE_FAVORITE_LIST, TABLE_FAVORITE_LIST_COL_SONGID, values);
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        return roweffect > 0;
        
    }

    /**
     * [功能说明]
     * @param songids
     * @param upload
     * @return
     */
    public boolean addSongList(List<Integer> songids, boolean upload) {
        if (songids == null || songids != null && songids.isEmpty()) {
            return false;
        }
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        try {
            delSongList(songids);
        } catch (Exception e) {
        }
        int bupload = upload ? 1 : 0;
        String sql = "insert or replace into " + TABLE_FAVORITE_LIST + "("
                + TABLE_FAVORITE_LIST_COL_SONGID + ","
                + TABLE_FAVORITE_LIST_COL_UPLOAD
                + ")"
                + " values(?,?)";
        
        try {
            SQLiteStatement stat = db.compileStatement(sql);
            
            db.beginTransaction();
            
            for (int i = 0; i < songids.size(); ++i) {
                stat.bindLong(1, songids.get(i));
                stat.bindLong(2, bupload);
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
        return true;
    }
 
    /**
     * [功能说明]
     * @param songids
     * @return
     */
    public boolean delSongList(List<Integer> songids) {
        if (songids == null || songids != null && songids.isEmpty()) {
            return false;
        }
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        String sqlDelete = "delete from " + TABLE_FAVORITE_LIST + " where " + TABLE_FAVORITE_LIST_COL_SONGID + "=?";
        try {
            SQLiteStatement statDelete = db.compileStatement(sqlDelete);
            db.beginTransaction();
            for (int i = 0; i < songids.size(); i++) {
                statDelete.bindLong(1, songids.get(i));
                statDelete.executeUpdateDelete();
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
        return true;
    }
    
    /**
     * [功能说明]
     * @param songids
     * @return
     */
    public boolean delSongListByUploadFlag(boolean upload) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        int bupload = upload ? 1 : 0;
        String sqlDelete = "delete from " + TABLE_FAVORITE_LIST + " where " 
                            + TABLE_FAVORITE_LIST_COL_UPLOAD + "=" + bupload;
        try {
            db.execSQL(sqlDelete);
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
        }
        return true;
    }

    /**
     * [功能说明] 通过是否同步过查询
     * @param upload
     * @return
     */
    public List<Integer> getSongIdsListByUpload(boolean upload) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return Collections.emptyList();
        }
        List<Integer> list = new ArrayList<Integer>();
        Cursor cursor = null;
        int bupload = upload ? 1 : 0;
        String selection = TABLE_FAVORITE_LIST_COL_UPLOAD + " = " + bupload;
        
        try {
            cursor = db.query(TABLE_FAVORITE_LIST, new String[]{TABLE_FAVORITE_LIST_COL_SONGID},
                    selection, null, null, null, null, null);
            while (cursor.moveToNext()) {
                list.add(cursor.getInt(0));
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
    
    /**
     * [删除歌曲]
     * @param songid 歌曲ID
     * @return true:成功;false：失败
     */
    public boolean delSong(int songid) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        
        if (!isAlreadyExists(songid)) {
            return false;
        }
        try {
            db.beginTransaction();
            db.delete(TABLE_FAVORITE_LIST, TABLE_FAVORITE_LIST_COL_SONGID + " = ?",
                    new String[]{String.valueOf(songid)});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
        } finally {
            try {
                db.endTransaction();
            } catch (Exception e) {
            }
        }       
        
        return false;
    }
    
    /**
     * [是否已存在于数据库]
     * @param songid 歌曲ID
     * @return true:存在;false：不存在
     */
    public boolean isAlreadyExists(int songid) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        
        Cursor cursor = null;
        String selection = TABLE_FAVORITE_LIST_COL_SONGID + " = " + songid;
        try {
            cursor = db.query(TABLE_FAVORITE_LIST, new String[]{TABLE_FAVORITE_LIST_COL_SONGID},
                    selection, null, null, null, null);
            
            if (cursor.moveToNext()) {
                return true;
            }
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }
    
    /**
     * [获取歌曲列表]
     * @return 返回歌曲列表
     */
    public List<Integer> getList() {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return Collections.emptyList();
        }
        Song song = null;
        ArrayList<Integer> list = new ArrayList<Integer>();
        Cursor cursor = null;
        String selection = TABLE_FAVORITE_LIST_COL_SONGID + " > 0";
        
        try {
            cursor = db.query(TABLE_FAVORITE_LIST, new String[]{TABLE_FAVORITE_LIST_COL_SONGID},
                    selection, null, null, null, null, null);
            while (cursor.moveToNext()) {
                song = SongManager.getInstance().getSongById(cursor.getInt(0));
                if (song != null) {
//                    list.add(song);
                    list.add(cursor.getInt(0));
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
    
    /**
     * [功能说明] 获取收藏歌曲列表
     * @param pageInfo  分页信息
     * @return  已缓存歌曲的列表
     */
    public List<Integer> getList(PageInfo pageInfo) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return Collections.emptyList();
        }
        Song song = null;
        ArrayList<Integer> list = new ArrayList<Integer>();
        Cursor cursor = null;
        String selection = TABLE_FAVORITE_LIST_COL_SONGID + " > 0";
        String limit = null;
        if (pageInfo != null) {
            limit  = "";
            limit += pageInfo.getPageIndex() * pageInfo.getPageSize();
            limit += ",";
            limit += pageInfo.getPageSize();
        }
        try {
            cursor = db.query(TABLE_FAVORITE_LIST, new String[]{TABLE_FAVORITE_LIST_COL_SONGID},
                    selection, null, null, null, null, limit);
            while (cursor.moveToNext()) {
                song = SongManager.getInstance().getSongById(cursor.getInt(0));
                if (song != null) {
                    list.add(cursor.getInt(0));
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
    
    /**
     * [数量]
     * @return 数量
     */
    public int getCount() {
        int count = 0;
        Cursor cursor = null;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            
            return count;
        }
        try {
            cursor = db.query(TABLE_FAVORITE_LIST, new String[]{"count(*)"}, null, null, null, null, null, null);
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

    /**
     * [功能说明] 删除所有的歌
     * @return
     */
    public boolean delAllSong() {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        String sqlDelete = "delete from " + TABLE_FAVORITE_LIST;
        try {
            db.execSQL(sqlDelete);
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
        }
        return true;
    }
}
