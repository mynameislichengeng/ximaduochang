package com.evideo.kmbox.dao;

import java.util.ArrayList;
import java.util.List;

import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.model.songinfo.SongCategory;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * [已点列表数据库]
 */
public class SelectedListDAO {
    private static final int INVALID_SELECTEDSONG_ID = 0;
    private static final String TABLE_SELECTED_LIST = "tblSelectedList";
    private static final String TABLE_SELECTED_LIST_COL_ID = "id";
    private static final String TABLE_SELECTED_LIST_COL_SONGID = "songid";
    private static final String TABLE_SELECTED_LIST_COL_CANSCORE = "canscore";
    private static final String TABLE_SELECTED_LIST_COL_SEQUENCE = "sequence";
    private static final String TABLE_SELECTED_LIST_COL_CUSTOMERID = "customerId";
    
    private static final String[] TABLE_SELECTED_LIST_ALL_COL = { TABLE_SELECTED_LIST_COL_ID, 
        TABLE_SELECTED_LIST_COL_SONGID, TABLE_SELECTED_LIST_COL_CANSCORE,
        TABLE_SELECTED_LIST_COL_SEQUENCE, TABLE_SELECTED_LIST_COL_CUSTOMERID };
    
    public SelectedListDAO() {
        
    }
    
    /**
     * [顶歌]
     * @param id 序列号
     * @return true:成功;false：失败
     */
    public boolean topSong(int id) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return false;
        }
        
        boolean result = false;
        Cursor cursor = null;
        
        try {
            db.beginTransaction();
            int sequence = 0;
            String selection = TABLE_SELECTED_LIST_COL_ID + " = " + id;
            cursor = db.query(TABLE_SELECTED_LIST, TABLE_SELECTED_LIST_ALL_COL, selection, null, null, null, null);
            
            if (cursor.moveToNext()) {
                sequence = cursor.getInt(3); 
            }
            
            if (cursor != null) {
                cursor.close();
            }
            
            if (sequence > 0) {
                //先把指定顺序的歌优先级置为0，最后再恢复为2.
                ContentValues values = new ContentValues();
                values.put(TABLE_SELECTED_LIST_COL_SEQUENCE , 0);
                db.update(TABLE_SELECTED_LIST, values, TABLE_SELECTED_LIST_COL_SEQUENCE + " = ?",
                        new String[]{String.valueOf(sequence)});
                //把前面第二首歌开始的sequence都加1
                StringBuilder sql = new StringBuilder();
                sql.append("update ").append(TABLE_SELECTED_LIST)
                .append(" set sequence = sequence + 1 where sequence >1 and sequence <").append(sequence);
                db.execSQL(sql.toString());
                //把置为0的歌曲sequence重置为2
                values.clear();
                values.put(TABLE_SELECTED_LIST_COL_SEQUENCE , 2);
                db.update(TABLE_SELECTED_LIST, values, TABLE_SELECTED_LIST_COL_SEQUENCE + " = ?",
                        new String[]{String.valueOf(0)});
                db.setTransactionSuccessful();
                result = true;
            }
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
        } finally {
            db.endTransaction();
        }

        return result;
    }
    
    public boolean deleteSongBySongId(int songId) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        
        Cursor cursor = null;
        boolean result = false;
        
        try {
            db.beginTransaction();
            int sequence = 0;
            String selection = TABLE_SELECTED_LIST_COL_SONGID + " = " + songId;
            cursor = db.query(TABLE_SELECTED_LIST, TABLE_SELECTED_LIST_ALL_COL, selection, null, null, null, null);
            
            if (cursor.moveToNext()) {
                sequence = cursor.getInt(3);    
            }

            if (cursor != null) {
                cursor.close();
            }
            
            if (sequence > 0) {    
                db.delete(TABLE_SELECTED_LIST, TABLE_SELECTED_LIST_COL_SONGID + " = ?", new String[]{String.valueOf(songId)});
                StringBuilder sql = new StringBuilder();
                sql.append("update ").append(TABLE_SELECTED_LIST)
                .append(" set sequence = sequence -1 where sequence >").append(sequence);
                db.execSQL(sql.toString());
                result = true;
            } else {
                result = false;
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
        } finally {
            db.endTransaction();
        }
        return result;
    }
    
    /**
     * [删除歌曲]
     * @param id 序列号
     * @return true:成功;false：失败
     */
    public boolean deleteSong(int id) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        
        Cursor cursor = null;
        boolean result = false;
        
        try {
            db.beginTransaction();
            int sequence = 0;
            String selection = TABLE_SELECTED_LIST_COL_ID + " = " + id;
            cursor = db.query(TABLE_SELECTED_LIST, TABLE_SELECTED_LIST_ALL_COL, selection, null, null, null, null);
            
            if (cursor.moveToNext()) {
                sequence = cursor.getInt(3);    
            }

            if (cursor != null) {
                cursor.close();
            }
            
            if (sequence > 0) {    
                db.delete(TABLE_SELECTED_LIST, TABLE_SELECTED_LIST_COL_ID + " = ?", new String[]{String.valueOf(id)});
                StringBuilder sql = new StringBuilder();
                sql.append("update ").append(TABLE_SELECTED_LIST)
                .append(" set sequence = sequence -1 where sequence >").append(sequence);
                db.execSQL(sql.toString());
                result = true;
            } else {
                result = false;
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
        } finally {
            db.endTransaction();
        }
        return result;
    }

    /**
     * [加歌]
     * @param songId 歌曲songid
     * @param canScore 歌曲是否可评分
     * @param customerId 客户ID
     * @return true:成功;false:失败
     */
    public int addSong(KmPlayListItem item/*int songId, boolean canScore, String customerId*/) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return INVALID_SELECTEDSONG_ID;
        }
        
        int id = INVALID_SELECTEDSONG_ID;
        Cursor cursor = null;

        if (item == null || item.getSerialNum() <= 0) {
            return INVALID_SELECTEDSONG_ID;
        }
        
        try {
            db.beginTransaction();
            int count = 0;
            cursor = db.query(TABLE_SELECTED_LIST, new String[]{ "count(*)" }, null, null, null, null, null);
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }

            if (cursor != null) {
                cursor.close();
            }

            ContentValues values = new ContentValues();
            values.put(TABLE_SELECTED_LIST_COL_SONGID, item.getSongId());
            values.put(TABLE_SELECTED_LIST_COL_CANSCORE, item.isSongCanScore() ? 1 : 0);
            values.put(TABLE_SELECTED_LIST_COL_SEQUENCE, count + 1);
            values.put(TABLE_SELECTED_LIST_COL_CUSTOMERID, item.getCustomerid());
            EvLog.d("add songid=" + item.getSongId() + ",id=" + item.getSerialNum());
            values.put(TABLE_SELECTED_LIST_COL_ID, item.getSerialNum());
            id = (int)db.insert(TABLE_SELECTED_LIST, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
        } finally {
            db.endTransaction();
        }
        return id;
    }
    
    /**
     * [据songid获取序列号]
     * @param songid 歌曲songid
     * @return 返回序列号
     */
    public int getSerialNum(int songid) {
        int result = 0;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        Cursor cursor = null;
        String selection = TABLE_SELECTED_LIST_COL_SONGID + "=" + songid;
        try {
            cursor = db.query(TABLE_SELECTED_LIST, TABLE_SELECTED_LIST_ALL_COL , selection, null, null, null, null);
            
            if (cursor.moveToNext()) {
                result =  cursor.getInt(0);
            }
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }
    
    public int getMaxId() {
        int id = 0;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
         Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SELECTED_LIST, new String[]{"max(id)"}, null, null, null, null, null, null);
            if (cursor.moveToNext()) {
                id = cursor.getInt(0);
            }
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        EvLog.d("getmaxid id= " + id);
        return id;
    }
    /**
     * [获取歌曲列表]
     * @return 返回歌曲列表
     */
    public List<KmPlayListItem> getlist() {
        
        List<KmPlayListItem> list = new ArrayList<KmPlayListItem>();
        Cursor cursor = null;
        
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        String orderby = TABLE_SELECTED_LIST_COL_SEQUENCE + " asc";
        try {
            cursor = db.query(TABLE_SELECTED_LIST, TABLE_SELECTED_LIST_ALL_COL, "id > 0", null, null, null, orderby);
            while (cursor.moveToNext()) {
                Song song = SongManager.getInstance().getSongById(cursor.getInt(1)); 
                if (song == null) {
                    continue;
                }
                KmPlayListItem item = new KmPlayListItem(song, cursor.getInt(0), cursor.getString(4), SongCategory.CATEGORY_PLAYLIST);
                list.add(item);
            }
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
        } finally {
            
            if (cursor != null) {
                cursor.close();
            }    
        }    
        return list;    
    }
      
    /**
     * [清空列表]
     * @return true:成功;false：失败
     */
    public boolean clearlist() {
        
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return false;
        }
        try {
            db.execSQL("delete from " + TABLE_SELECTED_LIST);
        } catch (SQLException e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
            return false;
        }
        return true;
    }
    
    /**
     * [据序列号判断是否存在]
     * @param id 歌曲序列号
     * @return true:成功;false：失败
     */
    public boolean isExist(int id) {
        
        boolean exist = false;
        Cursor cursor = null;
        String selection = TABLE_SELECTED_LIST_COL_ID + " = " + id;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return false;
        }
        
        try {
            cursor = db.query(TABLE_SELECTED_LIST, new String[]{TABLE_SELECTED_LIST_COL_ID}, 
                    selection, null, null, null, null);
            exist = cursor.moveToNext();
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }    
        }
        return exist;
    }
    
    /**
     * [获取歌曲数量]
     * @return 返回歌曲数量
     */
    public int getCount() {
        
        int count = 0;
        Cursor cursor = null;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            
            return count;
        }
        
        try {
            cursor = db.query(TABLE_SELECTED_LIST, new String[]{"count(*)"}, null, null, null, null, null, null);
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
}
