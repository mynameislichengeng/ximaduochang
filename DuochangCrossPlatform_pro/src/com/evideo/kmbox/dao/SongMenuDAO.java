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

import com.evideo.kmbox.model.songmenu.SongMenu;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明]歌单数据访问类
 */
public final class SongMenuDAO implements DatabaseConstants {
    
    public SongMenuDAO() {
    }
    
    private ContentValues getContentValues(SongMenu songMenu) {
        if (songMenu == null) {
            return null;
        }
        
        ContentValues values = new ContentValues();
        values.put(TABLE_SONG_MENU_COL_SONG_MENU_ID, songMenu.songMenuId);
        values.put(TABLE_SONG_MENU_COL_NAME, songMenu.name);
        values.put(TABLE_SONG_MENU_COL_DESCRIPTION, songMenu.description);
        values.put(TABLE_SONG_MENU_COL_IMAGE_URL, songMenu.imageUrl);
        values.put(TABLE_SONG_MENU_COL_TIMESTAMP, songMenu.timestamp + "");
        values.put(TABLE_SONG_MENU_COL_TOTAL_NUM, songMenu.totalNum + "");
        values.put(TABLE_SONG_MENU_COL_IMAGE_URL_BIG, songMenu.imageUrlBig + "");
        return values;
    }
    
    /**
     * [添加一条歌单记录]
     * @param songMenu 歌单
     * @return true 成功   false 失败
     */
    public boolean add(SongMenu songMenu) {
        if (songMenu == null) {
            return false;
        }
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return false;
        }
        
        ContentValues values = getContentValues(songMenu);
        
        long rowAffected = -1;
        try {
            rowAffected = db.insert(TABLE_SONG_MENU_TABLE_NAME, null, values);
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        
        return rowAffected > 0;
    }
    
    /**
     * [根据歌单Id获取songMenu]
     * @param songMenuId 歌单id
     * @return songMenu 歌单
     */
    public SongMenu getSongMenuById(int songMenuId) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            return null;
        }
        
        SongMenu songMenu = null;
        
        String selection = TABLE_SONG_MENU_COL_SONG_MENU_ID + "=" + songMenuId;
        
        Cursor cursor = null;
        
        try {
            cursor = db.query(TABLE_SONG_MENU_TABLE_NAME, TABLE_SONG_MENU_COLUMNS, selection, null, null, null, null);
            if (cursor.moveToNext()) {
                songMenu = getSongMenu(cursor);
            }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        return songMenu;
    }
    
    private SongMenu getSongMenu(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        SongMenu songMenu = new SongMenu(
                cursor.getInt(cursor.getColumnIndex(TABLE_SONG_MENU_COL_SONG_MENU_ID)), 
                cursor.getString(cursor.getColumnIndex(TABLE_SONG_MENU_COL_IMAGE_URL)), 
                cursor.getString(cursor.getColumnIndex(TABLE_SONG_MENU_COL_NAME)), 
                cursor.getString(cursor.getColumnIndex(TABLE_SONG_MENU_COL_DESCRIPTION)),
                Long.valueOf(cursor.getString(cursor.getColumnIndex(TABLE_SONG_MENU_COL_TIMESTAMP))),
                cursor.getInt(cursor.getColumnIndex(TABLE_SONG_MENU_COL_TOTAL_NUM)),
                cursor.getString(cursor.getColumnIndex(TABLE_SONG_MENU_COL_IMAGE_URL_BIG)));
        return songMenu;
        
    }
    
    /**
     * [更新歌单]
     * @param songMenu 歌单
     * @return true 成功  false 失败
     */
    public boolean update(SongMenu songMenu) {
        if (songMenu == null) {
            return false;
        }
        
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return false;
        }
        
        ContentValues values = getContentValues(songMenu);
        long rowAffected = -1;
        try {
            rowAffected = db.update(TABLE_SONG_MENU_TABLE_NAME, values, 
                    TABLE_SONG_MENU_COL_SONG_MENU_ID + " = ?", new String[]{songMenu.songMenuId + ""});
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        
        return rowAffected > 0;
    }
    
    /**
     * [删除歌单]
     * @param songMenu 歌单
     */
    public void delete(SongMenu songMenu) {
        if (songMenu == null) {
            return;
        }
        
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return;
        }
        
        try {
            db.delete(TABLE_SONG_MENU_TABLE_NAME, TABLE_SONG_MENU_COL_SONG_MENU_ID + " = ?", 
                    new String[]{songMenu.songMenuId + ""});
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
    }
    
    /**
     * [根据歌单id判断歌单是否存在]
     * @param songMenuId 歌单id
     * @return true 存在  false 不存在
     */
    public boolean hasSongMenu(int songMenuId) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            return false;
        }
        
        String selection = TABLE_SONG_MENU_COL_SONG_MENU_ID + "=" + songMenuId;
        boolean exists = false;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_MENU_TABLE_NAME, 
                    new String[]{TABLE_SONG_MENU_COL_SONG_MENU_ID}, 
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
     * @param songMenu 歌单
     * @return true 成功  fasle 失败
     */
    public boolean save(SongMenu songMenu) {
        if (songMenu == null) {
            return false;
        }
        
        boolean isSuccessful = false;
        
        if (hasSongMenu(songMenu.songMenuId)) {
            isSuccessful = update(songMenu);
        } else {
            isSuccessful = add(songMenu);
        }
        
        return isSuccessful;
    }
    
    /**
     * [保存歌单列表]
     * <p>先删除所有数据，再将歌单列表保存到db</p>
     * @param songMenus 歌单列表
     */
    public void saveList(List<SongMenu> songMenus) {
        if (songMenus == null || songMenus.size() <= 0) {
            return;
        }
        
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return;
        }
        
        db.beginTransaction();
        
        try {
            // 删除全部数据
            db.delete(TABLE_SONG_MENU_TABLE_NAME, null, null);
            
            // 保存数据
            for (SongMenu songMenu : songMenus) {
                ContentValues values = getContentValues(songMenu);
                db.insert(TABLE_SONG_MENU_TABLE_NAME, null, values);
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
    public int getCountOfAllSongMenus() {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            return 0;
        }
        
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_MENU_TABLE_NAME, new String[]{"count(*)"}, 
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
    public List<SongMenu> getAllSongMenus() {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            return Collections.emptyList();
        }
        
        List<SongMenu> songMenus = new ArrayList<SongMenu>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_MENU_TABLE_NAME, 
                    null, null, null, null, null, TABLE_SONG_MENU_COL_PRIMARY_ID);
            while (cursor.moveToNext()) {
                songMenus.add(getSongMenu(cursor));
            }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        return songMenus;
    }
    
}
