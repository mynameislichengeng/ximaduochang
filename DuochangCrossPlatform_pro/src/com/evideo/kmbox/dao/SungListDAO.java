package com.evideo.kmbox.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.playerctrl.sunglist.SungListItem;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;

/**
 * [已点列表数据库]
 */
public class SungListDAO {
    private static final String TABLE_SUNG_LIST = "tblSungList";
    private static final String TABLE_SUNG_LIST_COL_ID = "id";
    private static final String TABLE_SUNG_LIST_COL_SONGID = "songid";
    private static final String TABLE_SUNG_LIST_COL_CANSCORE = "canscore";
    private static final String TABLE_SUNG_LIST_COL_SCORE = "score";
    private static final String TABLE_SUNG_LIST_COL_CUSTOMERID = "customerId";
    private static final String TABLE_SUNG_LIST_COL_SHARECODE = "sharecode";
    private static final int INDEX_OF_ID = 0;
    private static final int INDEX_OF_CANSCORE = 2;
    private static final int INDEX_OF_SCORE = 3;
    private static final int INDEX_OF_CUSTOMERID = 4;
    private static final int INDEX_OF_SHARECODE = 5;
    
    private static final String[] TABLE_SUNG_LIST_ALL_COL = { 
        TABLE_SUNG_LIST_COL_ID, 
        TABLE_SUNG_LIST_COL_SONGID,
        TABLE_SUNG_LIST_COL_CANSCORE,
        TABLE_SUNG_LIST_COL_SCORE,
        TABLE_SUNG_LIST_COL_CUSTOMERID,
        TABLE_SUNG_LIST_COL_SHARECODE};
   
    public SungListDAO() {
    }
    
    public boolean delItem(long id) {
        boolean result = false;
        if (id < 0) {
            return result;
        }
        
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return result;
        }

        try {
            db.beginTransaction();
            
            if (db.delete(TABLE_SUNG_LIST, TABLE_SUNG_LIST_COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0){
                result = true;
            } else {
                EvLog.e(TABLE_SUNG_LIST + " del item failed");
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
    
    public boolean delItemBySongId(int songid) {
        boolean result = false;
        if (songid < 0) {
            return result;
        }
        
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return result;
        }

        try {
            db.beginTransaction();
            
            if (db.delete(TABLE_SUNG_LIST, TABLE_SUNG_LIST_COL_SONGID + " = ?", new String[]{String.valueOf(songid)}) > 0){
                result = true;
            } else {
                EvLog.e(TABLE_SUNG_LIST + " del item failed");
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
     * 添加已唱
     * @param sungItem 已唱数据
     * @return 是否成功
     */
    public int addItem(SungListItem sungItem) {
        
        int index = -1;
        
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return index;
        }
        
        try {
            db.beginTransaction();
            EvLog.d("begin add item " + sungItem.getSongId());
            ContentValues values = new ContentValues();
            values.put(TABLE_SUNG_LIST_COL_SONGID, sungItem.getSongId());
            values.put(TABLE_SUNG_LIST_COL_CANSCORE, sungItem.canScore() ? 1 : 0);
            values.put(TABLE_SUNG_LIST_COL_SCORE, sungItem.getScore());
            values.put(TABLE_SUNG_LIST_COL_CUSTOMERID, sungItem.getCustomerid());
            values.put(TABLE_SUNG_LIST_COL_SHARECODE, sungItem.getShareCode());
            
            index = (int)db.insert(TABLE_SUNG_LIST, null, values);
            if ( index < 0) {
                EvLog.e(TABLE_SUNG_LIST + " insert failed ");
            } 
            db.setTransactionSuccessful();
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
        } finally {
            db.endTransaction();
        }

        return index;
    }
    
    /**
     * [获取歌曲列表]
     * @return 返回歌曲列表
     */
    public List<SungListItem> getlist() {
        
        List<SungListItem> list = new ArrayList<SungListItem>();
        Cursor cursor = null;
        
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        String orderby = TABLE_SUNG_LIST_COL_ID + " asc";
        try {
            cursor = db.query(TABLE_SUNG_LIST, TABLE_SUNG_LIST_ALL_COL, "id > 0", null, null, null, orderby);
            while (cursor.moveToNext()) {
                Song song = SongManager.getInstance().getSongById(cursor.getInt(1)); 
                if (song == null) {
                    continue;
                }

                SungListItem item = new SungListItem(cursor.getInt(INDEX_OF_ID),
                        song, 
                        (cursor.getInt(INDEX_OF_CANSCORE) == 1) ? (true) : (false), 
                        cursor.getInt(INDEX_OF_SCORE), 
                        cursor.getString(INDEX_OF_SHARECODE),
                        cursor.getString(INDEX_OF_CUSTOMERID));
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
            db.execSQL("delete from " + TABLE_SUNG_LIST);
        } catch (SQLException e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
            return false;
        }
        return true;
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
            cursor = db.query(TABLE_SUNG_LIST, new String[]{"count(*)"}, null, null, null, null, null, null);
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
