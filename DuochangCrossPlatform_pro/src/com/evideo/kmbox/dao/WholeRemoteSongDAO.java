/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-7-15     "liuyantao"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.update.db.RemoteSong;
import com.evideo.kmbox.util.EvLog;

/**
 * [本地全库歌曲表操作]
 */
public class WholeRemoteSongDAO extends WholeSongDAO {
   
    // table song
    private static final String TABLE_SONG_NAME = "tblSong";
    private static final String TABLE_MEDIA_NAME = "tblMedia";
    private static final String TABLE_SONG_COL_ID = "SongID";
    private static final String TABLE_SONG_COL_NAME = "SongName";
    private static final String TABLE_SONG_COL_SPELL = "SongPy";
    private static final String TABLE_SONG_COL_SONGWORD = "SongWord";
    private static final String TABLE_SONG_COL_LANGUAGE_TYPEID = "LanguageTypeID";
    private static final String TABLE_SONG_COL_SINGER = "songsterName";
    private static final String TABLE_SONG_COL_SINGERID1 = "SongsterID1";
    private static final String TABLE_SONG_COL_SINGERID2 = "SongsterID2";
    private static final String TABLE_SONG_COL_SINGERID3 = "SongsterID3";
    private static final String TABLE_SONG_COL_SINGERID4 = "SongsterID4";
    private static final String TABLE_SONG_COL_SONGTYPE_ID1 = "SongTypeID1";
    private static final String TABLE_SONG_COL_SONGTYPE_ID2 = "SongTypeID2";
    private static final String TABLE_SONG_COL_SONGTYPE_ID3 = "SongTypeID3";
    private static final String TABLE_SONG_COL_SONGTYPE_ID4 = "SongTypeID4";
    private static final String TABLE_SONG_COL_LANGUAGE_TYPEID2 = "LanguageTypeID2";
    private static final String TABLE_SONG_COL_LANGUAGE_TYPEID3 = "LanguageTypeID3";
    private static final String TABLE_SONG_COL_LANGUAGE_TYPEID4 = "LanguageTypeID4";
    private static final String TABLE_SONG_COL_PLAYNUM = "PlayNum";
    private static final String TABLE_SONG_COL_ISGRAND = "IsGrand";
    private static final String TABLE_SONG_COL_ISSHOW = "IsMShow";
    private static final String TABLE_SONG_COL_UPDATE_TIME = "LastUpdateTime";
    private static final String TABLE_SONG_COL_ALBUM = "Album";
    private static final String TABLE_SONG_COL_ERCVERSION = "ErcVersion";
    private static final String TABLE_SONG_COL_HASREMOTE = "hasRemote";

    private static final String TABLE_SONG_COL_MD5 = "Md5Value";
    public static final String[] TABLE_SONG_ALL_COL = { 
            TABLE_SONG_COL_ID, TABLE_SONG_COL_NAME, 
            TABLE_SONG_COL_SPELL, TABLE_SONG_COL_SONGWORD,
            TABLE_SONG_COL_LANGUAGE_TYPEID, TABLE_SONG_COL_SINGER,
            TABLE_SONG_COL_SINGERID1, TABLE_SONG_COL_SINGERID2,
            TABLE_SONG_COL_SINGERID3, TABLE_SONG_COL_SINGERID4,
            TABLE_SONG_COL_SONGTYPE_ID1, TABLE_SONG_COL_SONGTYPE_ID2,
            TABLE_SONG_COL_SONGTYPE_ID3, TABLE_SONG_COL_SONGTYPE_ID4,
            TABLE_SONG_COL_LANGUAGE_TYPEID2, TABLE_SONG_COL_LANGUAGE_TYPEID3,
            TABLE_SONG_COL_LANGUAGE_TYPEID4, TABLE_SONG_COL_PLAYNUM,
            TABLE_SONG_COL_ISGRAND, TABLE_SONG_COL_ISSHOW,
            TABLE_SONG_COL_UPDATE_TIME, TABLE_SONG_COL_ALBUM,
            TABLE_SONG_COL_ERCVERSION };

    public class QueryEvideoIdRet {
        public List<Song> songList = new ArrayList<Song>();
        public Map<Integer, Integer> singerMap = new HashMap<Integer, Integer>();
        public Map<Integer, Integer> notIdentifyMap =  new HashMap<Integer, Integer>();

        public QueryEvideoIdRet(List<Song> songList,
                Map<Integer, Integer> singerMap,
                Map<Integer, Integer> notIdentifyMap) {
            this.songList = songList;
            this.singerMap = singerMap;
            this.notIdentifyMap = notIdentifyMap;
        }
    }

    public WholeRemoteSongDAO() {
    }

    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSongDAO#isExist(int)
     */
    @Override
    public boolean isExist(int songId) {

        boolean exist = false;
        Cursor cursor = null;
        String selection = TABLE_SONG_COL_ID + "=" + songId;
        SQLiteDatabase db = WholeDAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return false;
        }

        try {
            cursor = db.query(TABLE_SONG_NAME,
                    new String[] { TABLE_SONG_COL_ID }, selection, null, null,
                    null, null);
            if (cursor != null && cursor.moveToNext()) {
                exist = true;     
            } else {
                exist = false;
            }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exist;
    }

    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSongDAO#getCount()
     */
    @Override
    public int getCount() {
        int count = 0;
        Cursor cursor = null;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            
            return count;
        }
        
        cursor = db.query(TABLE_SONG_NAME, new String[]{"count(*)"}, null, null, null, null, null, null);
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        
        if (cursor != null) {
            cursor.close();
        }
        return count;
    }

    
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSongDAO#getSongById(int)
     */
    @Override
    public Song getSongById(int id) {
        Cursor cursor = null;
        String selection = TABLE_SONG_COL_ID + "=" + "'" + String.format("%08d", id) + "'";
        SQLiteDatabase db = WholeDAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return null;
        }

        try {
            cursor = db.query(TABLE_SONG_NAME, TABLE_SONG_ALL_COL, selection,
                    null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                Song song = new Song(cursor.getInt(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(5), new int[] {
                                cursor.getInt(6), cursor.getInt(7),
                                cursor.getInt(8), cursor.getInt(9) },
                        cursor.getInt(4), cursor.getInt(10), cursor.getInt(17),
                        null, null,
                        cursor.getString(21), Song.isCanScore(cursor.getInt(18)),
                        0);
                return song;
            }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSongDAO#getSongById(java.lang.String)
     */
    @Override
    public Song getSongById(String md5) {

        Cursor cursor = null;
        String selection = TABLE_SONG_COL_MD5 + "=" + "'" + md5 + "'";
        SQLiteDatabase db = WholeDAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return null;
        }

        Song song = null;
        try {
            cursor = db.query(TABLE_MEDIA_NAME, new String[]{TABLE_SONG_COL_ID}, selection,
                    null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                selection = TABLE_SONG_COL_ID + "=" + "'" + cursor.getString(0)+"'";
                cursor = db.query(TABLE_SONG_NAME, TABLE_SONG_ALL_COL, selection,
                        null, null, null, null);
                if (cursor != null && cursor.moveToNext()) {
                    song = new Song(cursor.getInt(0), cursor.getString(1),
                            cursor.getString(2), cursor.getString(5), new int[] {
                                    cursor.getInt(6), cursor.getInt(7),
                                    cursor.getInt(8), cursor.getInt(9) },
                            cursor.getInt(4), cursor.getInt(10), cursor.getInt(17),
                            null, null,
                            cursor.getString(21), Song.isCanScore(cursor.getInt(18)),
                            0);
                }                
            }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return song;
    }

    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSongDAO#add(com.evideo.kmbox.update.db.RemoteSong)
     */
    @Override
    public boolean add(RemoteSong song) {
        SQLiteDatabase db = WholeDAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        
        String sql = "insert or ignore into " + TABLE_SONG_NAME + "(" 
                + TABLE_SONG_COL_ID + ","
                + TABLE_SONG_COL_NAME + ","
                + TABLE_SONG_COL_SPELL + ","
                + TABLE_SONG_COL_SONGWORD + ","
                + TABLE_SONG_COL_LANGUAGE_TYPEID + ","
                + TABLE_SONG_COL_SINGER + ","
                + TABLE_SONG_COL_SINGERID1 + ","
                + TABLE_SONG_COL_SINGERID2 + ","
                + TABLE_SONG_COL_SINGERID3 + ","
                + TABLE_SONG_COL_SINGERID4 + ","
                + TABLE_SONG_COL_SONGTYPE_ID1 + ","
                + TABLE_SONG_COL_SONGTYPE_ID2 + ","
                + TABLE_SONG_COL_SONGTYPE_ID3 + ","
                + TABLE_SONG_COL_SONGTYPE_ID4 + ","
                + TABLE_SONG_COL_LANGUAGE_TYPEID2 + ","
                + TABLE_SONG_COL_LANGUAGE_TYPEID3 + ","
                + TABLE_SONG_COL_LANGUAGE_TYPEID4 + ","
                + TABLE_SONG_COL_PLAYNUM + ","
                + TABLE_SONG_COL_ISGRAND + ","
                + TABLE_SONG_COL_ISSHOW + ","
                + TABLE_SONG_COL_UPDATE_TIME + ","
                + TABLE_SONG_COL_ALBUM + ","
                + TABLE_SONG_COL_ERCVERSION + ","
                + TABLE_SONG_COL_HASREMOTE 
                + ")"
                + " values(?,?,?,?,?,   ?,?,?,?,?,"
                        + "?,?,?,?,?,   ?,?,?,?,?,"
                        + "?,?,?,?)";
        
        SQLiteStatement stat = db.compileStatement(sql);
        
        db.beginTransaction();
        
        stat.bindLong(1, song.getmId());
        stat.bindString(2, song.getmName());
        stat.bindString(3, song.getmSpell());
        stat.bindLong(4, Integer.parseInt(song.getmSongWord()));
        stat.bindLong(5, song.getLanguageTypeID()); 
        stat.bindString(6, song.getmSingerName());
        stat.bindLong(7, song.getmSingerId()[0]);
        stat.bindLong(8, song.getmSingerId()[1]);
        stat.bindLong(9, song.getmSingerId()[2]); 
        stat.bindLong(10, song.getmSingerId()[3]);
        stat.bindLong(11, song.getmSongTypeID()[0]);
        stat.bindLong(12, song.getmSongTypeID()[1]);
        stat.bindLong(13, song.getmSongTypeID()[2]);
        stat.bindLong(14, song.getmSongTypeID()[3]);
        stat.bindLong(15, song.getLanguageTypeID2());
        stat.bindLong(16, song.getLanguageTypeID3());
        stat.bindLong(17, song.getLanguageTypeID4());
        stat.bindLong(18, song.getmPlayNum());
        stat.bindLong(19, song.getmIsGrand());
        stat.bindLong(20, song.getmIsShow());
        stat.bindString(21, song.getmLastUpdateTime());
        stat.bindString(22, song.getAlbum());
        stat.bindString(23, song.getErcVersion());
        stat.bindLong(24, song.getHasRemote());
        stat.executeInsert();
        
        
        db.setTransactionSuccessful();
        db.endTransaction();

        return true;
    }
    
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSongDAO#deleteSong(int)
     */
    @Override
    public boolean deleteSong(int songId) {
        SQLiteDatabase db = WholeDAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return false;
        }

        db.delete(TABLE_SONG_NAME, TABLE_SONG_COL_ID + " = ?",
                new String[] { songId + "" });

        return true;
    }

    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSongDAO#update(com.evideo.kmbox.update.db.RemoteSong)
     */
    @Override
    public boolean update(RemoteSong song) {

        SQLiteDatabase db = WholeDAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        
        String sql = "insert or replace into " + TABLE_SONG_NAME + "(" 
                + TABLE_SONG_COL_ID + ","
                + TABLE_SONG_COL_NAME + ","
                + TABLE_SONG_COL_SPELL + ","
                + TABLE_SONG_COL_SONGWORD + ","
                + TABLE_SONG_COL_LANGUAGE_TYPEID + ","
                + TABLE_SONG_COL_SINGER + ","
                + TABLE_SONG_COL_SINGERID1 + ","
                + TABLE_SONG_COL_SINGERID2 + ","
                + TABLE_SONG_COL_SINGERID3 + ","
                + TABLE_SONG_COL_SINGERID4 + ","
                + TABLE_SONG_COL_SONGTYPE_ID1 + ","
                + TABLE_SONG_COL_SONGTYPE_ID2 + ","
                + TABLE_SONG_COL_SONGTYPE_ID3 + ","
                + TABLE_SONG_COL_SONGTYPE_ID4 + ","
                + TABLE_SONG_COL_LANGUAGE_TYPEID2 + ","
                + TABLE_SONG_COL_LANGUAGE_TYPEID3 + ","
                + TABLE_SONG_COL_LANGUAGE_TYPEID4 + ","
                + TABLE_SONG_COL_PLAYNUM + ","
                + TABLE_SONG_COL_ISGRAND + ","
                + TABLE_SONG_COL_ISSHOW + ","
                + TABLE_SONG_COL_UPDATE_TIME + ","
                + TABLE_SONG_COL_ALBUM + ","
                + TABLE_SONG_COL_ERCVERSION + ","
                + TABLE_SONG_COL_HASREMOTE 
                + ")"
                + " values(?,?,?,?,?,   ?,?,?,?,?,"
                        + "?,?,?,?,?,   ?,?,?,?,?,"
                        + "?,?,?,?)";
        
        SQLiteStatement stat = db.compileStatement(sql);
        
        db.beginTransaction();
        
        stat.bindLong(1, song.getmId());
        stat.bindString(2, song.getmName());
        stat.bindString(3, song.getmSpell());
        stat.bindLong(4, Integer.parseInt(song.getmSongWord()));
        stat.bindLong(5, song.getLanguageTypeID()); 
        stat.bindString(6, song.getmSingerName());
        stat.bindLong(7, song.getmSingerId()[0]);
        stat.bindLong(8, song.getmSingerId()[1]);
        stat.bindLong(9, song.getmSingerId()[2]); 
        stat.bindLong(10, song.getmSingerId()[3]);
        stat.bindLong(11, song.getmSongTypeID()[0]);
        stat.bindLong(12, song.getmSongTypeID()[1]);
        stat.bindLong(13, song.getmSongTypeID()[2]);
        stat.bindLong(14, song.getmSongTypeID()[3]);
        stat.bindLong(15, song.getLanguageTypeID2());
        stat.bindLong(16, song.getLanguageTypeID3());
        stat.bindLong(17, song.getLanguageTypeID4());
        stat.bindLong(18, song.getmPlayNum());
        stat.bindLong(19, song.getmIsGrand());
        stat.bindLong(20, song.getmIsShow());
        stat.bindString(21, song.getmLastUpdateTime());
        stat.bindString(22, song.getAlbum());
        stat.bindString(23, song.getErcVersion());
        stat.bindLong(24, song.getHasRemote());
        stat.executeInsert();
        
        
        db.setTransactionSuccessful();
        db.endTransaction();

        return true;
    }

    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSongDAO#addUpdateByList(java.util.List)
     */
    @Override
    public boolean addUpdateByList(List<Song> list) {
        SQLiteDatabase db = WholeDAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return false;
        }

        // TODO 这里没开启事物时 SQLiteStatement较快，但开启事物时，execSQL较快网上说的，待验证
        String sql = null;
        SQLiteStatement stat = db.compileStatement(sql);

        Song song = null;

        db.beginTransaction();

        for (int i = 0; i < list.size(); ++i) {
            song = list.get(i);

            if (song == null) {
                continue;
            }

            stat.bindLong(1, song.getId());
            stat.bindString(2, song.getName());
            stat.bindString(3, song.getSpell());
            stat.bindString(4, song.getSingerDescription());
            stat.bindLong(5, song.getSingerId(0));
            stat.bindLong(6, song.getSingerId(1));
            stat.bindLong(7, song.getSingerId(2));
            stat.bindLong(8, song.getSingerId(3));
            stat.bindLong(9, song.getLanguage());
            stat.bindLong(10, song.getType());
            stat.bindLong(11, song.getPlayRate());
            stat.bindString(12, song.getSubtitleResource());
            stat.bindLong(13, song.getId());
            stat.bindString(14, song.getAlbumResource());
            stat.bindLong(15, song.canScore() ? 1 : 0);
            stat.bindLong(16, song.getId());
            stat.bindLong(17, song.getId());
            stat.execute();
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        return true;
    }

    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSongDAO#deleteByList(java.util.List)
     */
    @Override
    public void deleteByList(List<Song> list) {
        SQLiteDatabase db = WholeDAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return;
        }

        // TODO 这里没开启事物时 SQLiteStatement较快，但开启事物时，execSQL较快网上说的，待验证

        Song song = null;

        db.beginTransaction();

        for (int i = 0; i < list.size(); ++i) {
            song = list.get(i);

            if (song == null) {
                continue;
            }

            db.delete(TABLE_SONG_NAME, TABLE_SONG_COL_ID + " = ?",
                    new String[] { String.valueOf(song.getId()) });
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSongDAO#save(java.util.List)
     */
    @Override
    public boolean save(List<RemoteSong> list) {
        SQLiteDatabase db = WholeDAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return false;
        }

        String sql = null;

        SQLiteStatement stat = db.compileStatement(sql);

        RemoteSong song = null;

        db.beginTransaction();

        for (int i = 0; i < list.size(); ++i) {
            song = list.get(i);

            if (song == null) {
                continue;
            }

            stat.bindLong(1, song.getmId());
            stat.bindString(2, song.getmName());
            stat.bindString(3, song.getmSpell());
            stat.bindString(4, null);
            stat.bindLong(5, song.getmSingerId()[0]);
            stat.bindLong(6, song.getmSingerId()[1]);
            stat.bindLong(7, song.getmSingerId()[2]);
            stat.bindLong(8, song.getmSingerId()[3]);
            stat.bindLong(9, 0);
            stat.bindLong(10, 0);
            stat.bindLong(11, song.getmPlayNum());
            stat.execute();
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        return true;
    }

    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSongDAO#getSongList(java.util.Map)
     */
    @Override
    public QueryEvideoIdRet getSongList(Map<Integer, String> map) {
        Cursor cursor = null;
        SQLiteDatabase db = WholeDAOHelper.getInstance().getReadableDatabase();
        List<Song> songList = new ArrayList<Song>();
        Map<Integer, Integer> singerMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> notIdentifyMap =  new HashMap<Integer, Integer>();

        Song song = null;

        db.beginTransaction();

        if (db == null || map.size() <= 0) {
            return null;
        }

        long traverseStartTime = System.currentTimeMillis();
        for (Integer key : map.keySet()) {  
//            startTime = System.currentTimeMillis();
            int id = key;
            try {
                String selection = TABLE_SONG_COL_ID + "=" + id;
                cursor = db.query(TABLE_SONG_NAME, TABLE_SONG_ALL_COL,
                        selection, null, null, null, null);
                if (cursor == null) {
                    notIdentifyMap.put(id, id);
                    continue;
                }
                if (cursor.moveToNext()) {
                    song = new Song(cursor.getInt(0), cursor.getString(1),
                            cursor.getString(2), cursor.getString(5), new int[] {
                                    cursor.getInt(6), cursor.getInt(7),
                                    cursor.getInt(8), cursor.getInt(9) },
                            cursor.getInt(4), cursor.getInt(10), cursor.getInt(17),
                            null, null,
                            cursor.getString(21), Song.isCanScore(cursor.getInt(18)),
                            0);

                    songList.add(song);
                    for (int i = 4; i < 8; i++) {
                        singerMap.put(cursor.getInt(i), cursor.getInt(i));
                    }
                }
            } catch (Exception e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        
        db.setTransactionSuccessful();
        db.endTransaction();
        
        QueryEvideoIdRet ret = new QueryEvideoIdRet(songList, singerMap,
                notIdentifyMap);
        
        return ret;
    }
    
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSongDAO#calculateQualifyNum(java.lang.String)
     */
    @Override
    public int calculateQualifyNum(String collection) {
        Cursor cursor = null;
        int count = -1;
        SQLiteDatabase db = WholeDAOHelper.getInstance().getReadableDatabase();
        String selection = TABLE_SONG_COL_ID + " in (" + collection + ")";
        if (db == null) {
            return -1;
        }
        
        try {
            cursor = db.query(TABLE_SONG_NAME, new String[]{"count(*)"}, selection, null, null, null, null, null);
            if (cursor == null) {
                return -1;
            }
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return count;
    }
}
