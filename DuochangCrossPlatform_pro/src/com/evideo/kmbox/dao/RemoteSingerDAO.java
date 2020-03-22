package com.evideo.kmbox.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RemoteSingerDAO {
    
    // remote table singer
    private static final String TABLE_SINGER_NAME = "tblSinger";
    private static final String TABLE_SINGER_COL_ID = "id";
    private static final String TABLE_SINGER_COL_NAME = "name";
    private static final String TABLE_SINGER_COL_SPELL = "spell";
    private static final String TABLE_SINGER_COL_GENDER = "gender";
    private static final String TABLE_SINGER_COL_GROUP = "'group'";
    private static final String TABLE_SINGER_COL_TYPE = "type";
    private static final String TABLE_SINGER_COL_COUNTRY = "country";
    private static final String TABLE_SINGER_COL_PLAYRATE = "playRate";
    private static final String TABLE_SINGER_COL_TIMESTAMP = "updateTime";
    private static final String TABLE_SINGER_COL_PICTURE = "picture";
    private static final String[] TABLE_SINGER_ALL_COL = { TABLE_SINGER_COL_ID, TABLE_SINGER_COL_NAME, TABLE_SINGER_COL_SPELL,
        TABLE_SINGER_COL_GENDER, TABLE_SINGER_COL_GROUP, TABLE_SINGER_COL_TYPE, TABLE_SINGER_COL_COUNTRY, 
        TABLE_SINGER_COL_PLAYRATE, TABLE_SINGER_COL_TIMESTAMP, TABLE_SINGER_COL_PICTURE };

    public RemoteSingerDAO()
    {
    }
    
    public int getCountAfterDatetime(String datetime) {
        Cursor cursor = null;
        SQLiteDatabase db = RemoteDAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return 0;
        }
        
        try {
            cursor = db.query(TABLE_SINGER_NAME, new String[]{ "count(*)" }, null, null, null, null, null);
            if (cursor.moveToNext()) {
                return cursor.getInt(0);
            }
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            UmengAgentUtil.reportError(e);
        } finally {
            if(cursor != null) {
                cursor.close();
            }
            
        }

        return 0;
    }
    
    public List<Singer> getListAfterDatetime(String datetime, PageInfo pageInfo) {
        Cursor cursor = null;
        String limit = "";
        if (pageInfo != null) {
            limit = pageInfo.getPageIndex() * pageInfo.getPageSize() + "," + pageInfo.getPageSize();
        }

        SQLiteDatabase db = RemoteDAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return Collections.emptyList();
        }

        List<Singer> singers = new ArrayList<Singer>();

        try {
            cursor = db.query(TABLE_SINGER_NAME, TABLE_SINGER_ALL_COL, null, null, null, null, null, limit);
            
            while(cursor.moveToNext())
            {
                Singer singer = new Singer(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getInt(3), cursor.getString(4).compareTo("true") == 0, 
                        cursor.getInt(5), cursor.getInt(6), cursor.getInt(7), 
                        cursor.getString(8), cursor.getString(9));
                singers.add(singer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            UmengAgentUtil.reportError(e);
            return Collections.emptyList();
        } finally {
            if (cursor != null)
            {
                cursor.close();
            }
            
        }
        
        return singers;
    }
    
    public int getCount() {
        Cursor cursor = null;
        SQLiteDatabase db = RemoteDAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return 0;
        }
        
        try {
            cursor = db.query(TABLE_SINGER_NAME, new String[]{ "count(*)" }, null, null, null, null, null);
            if (cursor.moveToNext()) {
                return cursor.getInt(0);
            }
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            UmengAgentUtil.reportError(e);
        } finally {
            if(cursor != null) {
                cursor.close();
            }
            
        }

        return 0;
    }
    
    public List<Singer> getList(PageInfo pageInfo) {
        Cursor cursor = null;
        String limit = "";
        if (pageInfo != null) {
            limit = pageInfo.getPageIndex() * pageInfo.getPageSize() + "," + pageInfo.getPageSize();
        }

        SQLiteDatabase db = RemoteDAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return Collections.emptyList();
        }

        List<Singer> singers = new ArrayList<Singer>();

        try {
            cursor = db.query(TABLE_SINGER_NAME, TABLE_SINGER_ALL_COL, null, null, null, null, null, limit);
            
            while(cursor.moveToNext())
            {
                Singer singer = new Singer(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getInt(3), cursor.getString(4).compareTo("true") == 0, 
                        cursor.getInt(5), cursor.getInt(6), cursor.getInt(7), 
                        cursor.getString(8), cursor.getString(9));
                singers.add(singer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            UmengAgentUtil.reportError(e);
            return Collections.emptyList();
        } finally {
            if (cursor != null)
            {
                cursor.close();
            }
        }
        
        return singers;
    }
}
