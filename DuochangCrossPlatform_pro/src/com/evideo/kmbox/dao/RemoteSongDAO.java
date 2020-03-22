package com.evideo.kmbox.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;

public final class RemoteSongDAO {
    
    // remote table song
    private static final String TABLE_SONG_NAME = "tblSong";
    private static final String TABLE_SONG_COL_ID = "id";
    private static final String TABLE_SONG_COL_NAME = "name";
    private static final String TABLE_SONG_COL_SPELL = "spell";
    private static final String TABLE_SONG_COL_SINGER = "singer";
    private static final String TABLE_SONG_COL_SINGER0 = "singerId0";
    private static final String TABLE_SONG_COL_SINGER1 = "singerId1";
    private static final String TABLE_SONG_COL_SINGER2 = "singerId2";
    private static final String TABLE_SONG_COL_SINGER3 = "singerId3";
    private static final String TABLE_SONG_COL_LANGUAGE = "language";
    private static final String TABLE_SONG_COL_TYPE = "type";
    private static final String TABLE_SONG_COL_PLAYRATE = "playRate";
    private static final String TABLE_SONG_COL_ALBUM = "album";
    private static final String TABLE_SONG_COL_SCORE = "score";
    
    private static final String[] TABLE_SONG_ALL_COL = { TABLE_SONG_COL_ID, TABLE_SONG_COL_NAME, TABLE_SONG_COL_SPELL,
        TABLE_SONG_COL_SINGER, TABLE_SONG_COL_SINGER0, TABLE_SONG_COL_SINGER1, TABLE_SONG_COL_SINGER2, TABLE_SONG_COL_SINGER3,
        TABLE_SONG_COL_LANGUAGE, TABLE_SONG_COL_TYPE, TABLE_SONG_COL_PLAYRATE, TABLE_SONG_COL_ALBUM, TABLE_SONG_COL_SCORE };

    public RemoteSongDAO()
    {
    }
    
    public int getCountAfterDateTime(String datetime) {
        Cursor cursor = null;
        SQLiteDatabase db = RemoteDAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return 0;
        }
        
        try {
            cursor = db.query(TABLE_SONG_NAME, new String[]{ "count(*)" }, null, null, null, null, null);
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
    
    public List<Song> getListAfterDatetime(String datetime, PageInfo pageInfo)
    {
        Cursor cursor = null;
        String limit = "";
        if (pageInfo != null) {
            limit = pageInfo.getPageIndex() * pageInfo.getPageSize() + "," + pageInfo.getPageSize();
        }

        SQLiteDatabase db = RemoteDAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return Collections.emptyList();
        }

        List<Song> songs = new ArrayList<Song>();

        try {
            cursor = db.query(TABLE_SONG_NAME, TABLE_SONG_ALL_COL, null, null, null, null, null, limit);
            
            while(cursor.moveToNext())
            {
                Song song = new Song(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), 
                        new int[]{ cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getInt(7) },
                        cursor.getInt(8), cursor.getInt(9), cursor.getInt(10), cursor.getString(11), cursor.getString(12).compareTo("1") == 0);
                songs.add(song);
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
        
        return songs;
    }
    
    public int getCount() {
        Cursor cursor = null;
        SQLiteDatabase db = RemoteDAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return 0;
        }
        
        try {
            cursor = db.query(TABLE_SONG_NAME, new String[]{ "count(*)" }, null, null, null, null, null);
            if (cursor.moveToNext()) {
                return cursor.getInt(0);
            }
        }catch (Exception e) {
            // TODO: handle exception
            UmengAgentUtil.reportError(e);
            e.printStackTrace();
        } finally {
            if(cursor != null) {
                cursor.close();
            }
            
        }

        return 0;
    }
    
    public List<Song> getList(PageInfo pageInfo)
    {
        Cursor cursor = null;
        String limit = "";
        if (pageInfo != null) {
            limit = pageInfo.getPageIndex() * pageInfo.getPageSize() + "," + pageInfo.getPageSize();
        }

        SQLiteDatabase db = RemoteDAOHelper.getInstance().getReadableDatabase();
        
        if (db == null) {
            return Collections.emptyList();
        }

        List<Song> songs = new ArrayList<Song>();

        try {
            cursor = db.query(TABLE_SONG_NAME, TABLE_SONG_ALL_COL, null, null, null, null, null, limit);
            
            while(cursor.moveToNext())
            {
                Song song = new Song(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), 
                        new int[]{ cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getInt(7) },
                        cursor.getInt(8), cursor.getInt(9), cursor.getInt(10), cursor.getString(11), cursor.getString(12).compareTo("1") == 0);
                songs.add(song);
            }
        } catch (Exception e) {
            UmengAgentUtil.reportError(e);
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (cursor != null)
            {
                cursor.close();
            }

        }

        return songs;
    }
}
