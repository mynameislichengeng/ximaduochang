/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-3-26     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.evideo.kmbox.model.songtop.SongTopDetail;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * [功能说明]歌单详情数据访问类
 */
public final class SongTopDetailDAO implements DatabaseConstants {
    
    public SongTopDetailDAO() {
    }
    
    private ContentValues getContentValues(SongTopDetail SongTopDetail) {
        if (SongTopDetail == null) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(TABLE_SONG_TOP_DETAIL_COL_SONG_TOP_ID, SongTopDetail.songTopId);
        values.put(TABLE_SONG_TOP_DETAIL_COL_SONG_ID, SongTopDetail.songId);
        values.put(TABLE_SONG_TOP_DETAIL_COL_SONG_NAME, SongTopDetail.songName);
        values.put(TABLE_SONG_TOP_DETAIL_COL_SINGER_NAME, SongTopDetail.singerName);
        values.put(TABLE_SONG_TOP_DETAIL_COL_SCORE, SongTopDetail.score ? 1 : 0);
        values.put(TABLE_SONG_TOP_DETAIL_COL_ORDER_RATE, SongTopDetail.orderRate);
        return values;
    }
    
    private SongTopDetail getSongTopDetail(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        SongTopDetail detail = new SongTopDetail(
                cursor.getInt(cursor.getColumnIndex(TABLE_SONG_TOP_DETAIL_COL_SONG_TOP_ID)), 
                cursor.getInt(cursor.getColumnIndex(TABLE_SONG_TOP_DETAIL_COL_SONG_ID)),
                cursor.getString(cursor.getColumnIndex(TABLE_SONG_TOP_DETAIL_COL_SONG_NAME)),
                cursor.getString(cursor.getColumnIndex(TABLE_SONG_TOP_DETAIL_COL_SINGER_NAME)),
                cursor.getInt(cursor.getColumnIndex(TABLE_SONG_TOP_DETAIL_COL_SCORE)) == 1,
                cursor.getInt(cursor.getColumnIndex(TABLE_SONG_TOP_DETAIL_COL_ORDER_RATE)));
        return detail;
    }
    
    /**
     * [功能说明]插入一条歌单详情记录
     * @param SongTopDetail 歌单详情
     * @return true 成功  false 失败
     */
    public boolean add(SongTopDetail SongTopDetail) {
        if (SongTopDetail == null) {
            return false;
        }
        
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return false;
        }
        
        ContentValues values = getContentValues(SongTopDetail);
        long rowsAffected = -1;
        try {
            rowsAffected = db.insert(TABLE_SONG_TOP_DETAIL_TABLE_NAME, null, values);
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        return rowsAffected > 0;
    }
    
    /**
     * [功能说明]保存歌单详情列表
     * @param details 歌单详情列表
     */
    public void saveSongTopDetaliList(List<SongTopDetail> details) {
        if (details == null || details.isEmpty()) {
            return;
        }
        
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return;
        }
        
        db.beginTransaction();
        
        try {
            
            for (SongTopDetail detail : details) {
                ContentValues values = getContentValues(detail);
                if (hasSongTopDetail(db, detail)) {
                    db.update(TABLE_SONG_TOP_DETAIL_TABLE_NAME, values, 
                            TABLE_SONG_TOP_COL_SONG_TOP_ID + " = ? & " + TABLE_SONG_TOP_DETAIL_COL_SONG_ID + " = ?", 
                            new String[]{detail.songTopId + "", detail.songId + ""});
                } else {
                    db.insert(TABLE_SONG_TOP_DETAIL_TABLE_NAME, null, values); 
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            db.endTransaction();
        }
        
        
    }
    
    private boolean hasSongTopDetail(SQLiteDatabase db, SongTopDetail detail) {
        if (db == null || detail == null) {
            return false;
        }
        
        boolean exists = false;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_TOP_DETAIL_TABLE_NAME, TABLE_SONG_TOP_DETAIL_COLUMNS, 
                    TABLE_SONG_TOP_COL_SONG_TOP_ID + " = ? and " + TABLE_SONG_TOP_DETAIL_COL_SONG_ID + " = ?", 
                    new String[]{detail.songTopId + "", detail.songId + ""}, null, null, null);
            exists = cursor.moveToNext();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exists;
    }
    
    /**
     * [功能说明]保存歌单详情列表
     * <p>先根据歌单id删除对应的歌单详情记录，然后再保存歌单详情列表</p>
     * <p>调用者需保证列表中歌单详情的歌单id与传入的歌单id保持一致</p>
     * @param songTopId 歌单id
     * @param details 歌单详情列表
     * @deprecated
     */
    public void saveSongTopDetails(int songTopId, List<SongTopDetail> details) {
        if (details == null) {
            return;
        }
        
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return;
        }
        
        db.beginTransaction();
        
        try {
            // 根据歌单id删除对应的详情记录
            db.delete(TABLE_SONG_TOP_DETAIL_TABLE_NAME, 
                    TABLE_SONG_TOP_DETAIL_COL_SONG_TOP_ID + " = ?", new String[]{songTopId + ""});
            
            // 保存歌单详情记录
            for (SongTopDetail detail : details) {
                ContentValues values = getContentValues(detail);
                db.insert(TABLE_SONG_TOP_DETAIL_TABLE_NAME, null, values);
            }
            
            db.setTransactionSuccessful();
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            db.endTransaction();
        }
    }
    
    /**
     * [功能说明]根据歌单id获取对应的歌单详情信息
     * @param songTopId 歌单id
     * @return 歌单详情列表
     */
    public List<SongTopDetail> getSongTopDetails(int songTopId) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            return Collections.emptyList();
        }
        
        List<SongTopDetail> details = new ArrayList<SongTopDetail>();
        String selection = TABLE_SONG_TOP_DETAIL_COL_SONG_TOP_ID + "=" + songTopId;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_TOP_DETAIL_TABLE_NAME, TABLE_SONG_TOP_DETAIL_COLUMNS, 
                    selection, null, null, null, null);
            while (cursor.moveToNext()) {
                details.add(getSongTopDetail(cursor));
            }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        return details;
    }
    
    /**
     * [功能说明]根据歌单id获取对应歌单详情的个数
     * @param songTopId 歌单id
     * @return 数量
     */
    public int getCountBySongTopId(int songTopId) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            return 0;
        }
        
        int count = 0;
        String selection = TABLE_SONG_TOP_DETAIL_COL_SONG_TOP_ID + "=" + songTopId;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_TOP_DETAIL_TABLE_NAME, new String[]{"count(*)"}, 
                    selection, null, null, null, null);
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

}
