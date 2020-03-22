
package com.evideo.kmbox.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.mainview.globalsearch.SearchHistoryItem;

/**
 * [搜索历史DAO]
 */
public class SearchHistoryDAO implements DatabaseConstants {
    
    public SearchHistoryDAO() {
    }
    private ContentValues getContentValues(SearchHistoryItem item) {
        if (item == null) {
            return null;
        }
        
        ContentValues values = new ContentValues();
        values.put(TABLE_SEARCH_HISTORY_COL_ID, item.mId);
        values.put(TABLE_SEARCH_HISTORY_COL_TYPE, item.mItemType);
        
        return values;
    }
    
    /**
     * [添加一条搜索历史]
     * @param item item
     * @return true or false
     */
    public boolean add(SearchHistoryItem item) {
        if (item == null) {
            return false;
        }
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return false;
        }
        
        ContentValues values = getContentValues(item);
        
        if (hasHistoryItem(item)) {
            return false;
        }

        long rowAffected = -1;
        try {
            rowAffected = db.insert(TABLE_SEARCH_HISTORY_NAME, null, values);
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        
        return rowAffected > 0;
    }

    private SearchHistoryItem getSearchHistoryItem(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        SearchHistoryItem item = new SearchHistoryItem(
                cursor.getInt(cursor.getColumnIndex(TABLE_SEARCH_HISTORY_COL_ID)), 
                cursor.getInt(cursor.getColumnIndex(TABLE_SEARCH_HISTORY_COL_TYPE)));
        return item;
    }

    /**
     * [功能说明]
     * @param item SearchHistory Item
     * @return exists or not
     */
    public boolean hasHistoryItem(SearchHistoryItem item) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            return false;
        }
        
        String selection = TABLE_SEARCH_HISTORY_COL_ID + " = " + item.mId
                + " AND " + TABLE_SEARCH_HISTORY_COL_TYPE + " = " + item.mItemType;
        boolean exists = false;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SEARCH_HISTORY_NAME, 
                    new String[]{TABLE_SEARCH_HISTORY_COL_PRIMARY_ID}, 
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
     * [获取所有的搜索记录]
     * @return 记录列表
     */
    public List<SearchHistoryItem> getAllSearchItems() {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            return Collections.emptyList();
        }
        
        List<SearchHistoryItem> items = new ArrayList<SearchHistoryItem>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SEARCH_HISTORY_NAME, 
                    null, null, null, null, null, TABLE_SEARCH_HISTORY_COL_PRIMARY_ID);
            while (cursor.moveToNext()) {
                items.add(getSearchHistoryItem(cursor));
            }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        return items;
    }
    
    /**
     * [删除所有的搜索历史]
     */
    public void clearList() {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return;
        }
        try {
            db.delete(TABLE_SEARCH_HISTORY_NAME, null, null);
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
    }
}
