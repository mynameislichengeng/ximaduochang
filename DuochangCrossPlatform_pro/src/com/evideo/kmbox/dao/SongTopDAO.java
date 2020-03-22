/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-3-25     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.evideo.kmbox.model.songtop.SongTop;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明]歌单数据访问类
 */
public final class SongTopDAO implements DatabaseConstants {
    
    public SongTopDAO() {
    }
    
    private ContentValues getContentValues(SongTop songTop) {
        if (songTop == null) {
            return null;
        }
        
        ContentValues values = new ContentValues();
        values.put(TABLE_SONG_TOP_COL_SONG_TOP_ID, songTop.songTopId);
        values.put(TABLE_SONG_TOP_COL_NAME, songTop.name);
        values.put(TABLE_SONG_TOP_COL_IMAGE_URL, songTop.imageUrl);
        values.put(TABLE_SONG_TOP_COL_TIMESTAMP, songTop.timestamp + "");
        values.put(TABLE_SONG_TOP_COL_TOTAL_NUM, songTop.totalNum + "");
        values.put(TABLE_SONG_TOP_COL_TYPE_CODE, songTop.songTopTypeCode + "");
        values.put(TABLE_SONG_TOP_COL_SUBTITLE, songTop.subTitle);
        values.put(TABLE_SONG_TOP_COL_SONGLIST, songTop.songsList);
        values.put(TABLE_SONG_TOP_COL_SINGERLIST, songTop.singersList);
        return values;
    }
    
    /**
     * [添加一条歌单记录]
     * @param songTop 歌单
     * @return true 成功   false 失败
     */
    public boolean add(SongTop songTop) {
        if (songTop == null) {
            return false;
        }
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return false;
        }
        
        ContentValues values = getContentValues(songTop);
        
        long rowAffected = -1;
        try {
            rowAffected = db.insert(TABLE_SONG_TOP_TABLE_NAME, null, values);
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        
        return rowAffected > 0;
    }
    
    /**
     * [根据歌单Id获取songTop]
     * @param songTopId 歌单id
     * @return songTop 歌单
     */
    public SongTop getSongTopById(int songTopId) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            return null;
        }
        
        SongTop songTop = null;
        
        String selection = TABLE_SONG_TOP_COL_SONG_TOP_ID + "=" + songTopId;
        
        Cursor cursor = null;
        
        try {
            cursor = db.query(TABLE_SONG_TOP_TABLE_NAME, TABLE_SONG_TOP_COLUMNS, selection, null, null, null, null);
            if (cursor.moveToNext()) {
                songTop = getSongTop(cursor);
            }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        return songTop;
    }
    
    private SongTop getSongTop(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        SongTop songTop = new SongTop(
                cursor.getInt(cursor.getColumnIndex(TABLE_SONG_TOP_COL_SONG_TOP_ID)), 
                cursor.getString(cursor.getColumnIndex(TABLE_SONG_TOP_COL_IMAGE_URL)), 
                cursor.getString(cursor.getColumnIndex(TABLE_SONG_TOP_COL_NAME)), 
                Long.valueOf(cursor.getString(cursor.getColumnIndex(TABLE_SONG_TOP_COL_TIMESTAMP))),
                cursor.getInt(cursor.getColumnIndex(TABLE_SONG_TOP_COL_TOTAL_NUM)),
                cursor.getInt(cursor.getColumnIndex(TABLE_SONG_TOP_COL_TYPE_CODE)),
                cursor.getString(cursor.getColumnIndex(TABLE_SONG_TOP_COL_SUBTITLE)),
                cursor.getString(cursor.getColumnIndex(TABLE_SONG_TOP_COL_SONGLIST)),
                cursor.getString(cursor.getColumnIndex(TABLE_SONG_TOP_COL_SINGERLIST)));
        return songTop;
    }
    
    /**
     * [更新歌单]
     * @param songTop 歌单
     * @return true 成功  false 失败
     */
    public boolean update(SongTop songTop) {
        if (songTop == null) {
            return false;
        }
        
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return false;
        }
        
        ContentValues values = getContentValues(songTop);
        long rowAffected = -1;
        try {
            rowAffected = db.update(TABLE_SONG_TOP_TABLE_NAME, values, 
                    TABLE_SONG_TOP_COL_SONG_TOP_ID + " = ?", new String[]{songTop.songTopId + ""});
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        
        return rowAffected > 0;
    }
    
    /**
     * [删除歌单]
     * @param songTop 歌单
     */
    public void delete(SongTop songTop) {
        if (songTop == null) {
            return;
        }
        
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return;
        }
        
        try {
            db.delete(TABLE_SONG_TOP_TABLE_NAME, TABLE_SONG_TOP_COL_SONG_TOP_ID + " = ?", 
                    new String[]{songTop.songTopId + ""});
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
    }
    
    /**
     * [根据歌单id判断歌单是否存在]
     * @param songTopId 歌单id
     * @return true 存在  false 不存在
     */
    public boolean hasSongTop(int songTopId) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            return false;
        }
        
        String selection = TABLE_SONG_TOP_COL_SONG_TOP_ID + "=" + songTopId;
        boolean exists = false;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_TOP_TABLE_NAME, 
                    new String[]{TABLE_SONG_TOP_COL_SONG_TOP_ID}, 
                    selection, null, null, null, null);
            exists = cursor.moveToNext();
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exists;
    }
    
    /**
     * [保存歌单]
     * @param songTop 歌单
     * @return true 成功  fasle 失败
     */
    public boolean save(SongTop songTop) {
        if (songTop == null) {
            return false;
        }
        
        boolean isSuccessful = false;
        
        if (hasSongTop(songTop.songTopId)) {
            isSuccessful = update(songTop);
        } else {
            isSuccessful = add(songTop);
        }
        
        return isSuccessful;
    }
    
    /**
     * [保存歌单列表]
     * <p>先删除所有数据，再将歌单列表保存到db</p>
     * @param songTops 歌单列表
     */
    public void saveList(List<SongTop> songTops) {
        if (songTops == null || songTops.size() <= 0) {
            return;
        }
        
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return;
        }
        
        db.beginTransaction();
        
        try {
            // 删除全部数据
            db.delete(TABLE_SONG_TOP_TABLE_NAME, null, null);
            
            // 保存数据
            for (SongTop songTop : songTops) {
                ContentValues values = getContentValues(songTop);
                db.insert(TABLE_SONG_TOP_TABLE_NAME, null, values);
            }
            
            db.setTransactionSuccessful();
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            db.endTransaction();
        }
        
        return;
    }
    
    /**
     * [获取歌单总数]
     * @return 歌单总户数
     */
    public int getCountOfAllSongTops() {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            return 0;
        }
        
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_TOP_TABLE_NAME, new String[]{"count(*)"}, 
                    null, null, null, null, null);
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
        
        return count;
    }
    
    /**
     * [获取所有的歌单]
     * @return 歌单列表
     */
    public List<SongTop> getAllSongTops() {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            return Collections.emptyList();
        }
        
        List<SongTop> songTops = new ArrayList<SongTop>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_TOP_TABLE_NAME, 
                    null, null, null, null, null, TABLE_SONG_TOP_COL_PRIMARY_ID);
            while (cursor.moveToNext()) {
                songTops.add(getSongTop(cursor));
            }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        return songTops;
    }
    
}
