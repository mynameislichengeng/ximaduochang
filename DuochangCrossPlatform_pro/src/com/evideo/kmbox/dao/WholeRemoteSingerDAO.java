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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.update.db.RemoteSinger;
import com.evideo.kmbox.util.EvLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * [本地全库歌星表操作]
 */
public class WholeRemoteSingerDAO extends WholeSingerDAO {
    private static final String TABLE_SINGER_NAME = "tblSinger"; 
    private static final String TABLE_SINGER_COL_SINGERID = "SongsterID";
    private static final String TABLE_SINGER_COL_SINGERNAME = "SongsterName";
    private static final String TABLE_SINGER_COL_SPELL = "SongsterPy"; 
    private static final String TABLE_SINGER_COL_SONGERLOVE = "SongsterLove";
    private static final String TABLE_SINGER_COL_SINGERTYPEID = "SongsterTypeID";
    private static final String TABLE_SINGER_COL_PHOTO = "photopath";
    private static final String TABLE_SINGER_COL_SINGERORDER = "SongsterOrderRank";
    private static final String TABLE_SINGER_COL_UPDATETIME = "LastUpdateTime";
    private static final String TABLE_SINGER_COL_FILE_H = "Pic_FileID_H";
    private static final String TABLE_SINGER_COL_FILE_L = "Pic_FileID_L";
    private static final String TABLE_SINGER_COL_FILE_M = "Pic_FileID_M";
    private static final String TABLE_SINGER_COL_FILE_S = "Pic_FileID_S";
    private static final String TABLE_SINGER_COL_IMIT_PIC_0 = "Imitate_Pic_FileID_0";
    private static final String TABLE_SINGER_COL_IMIT_PIC_1 = "Imitate_Pic_FileID_1";
    private static final String TABLE_SINGER_COL_IMIT_PIC_2 = "Imitate_Pic_FileID_2";
    private static final String TABLE_SINGER_COL_ISGROUP = "isGroup";
    private static final String TABLE_SINGER_COL_GENDER = "gender";
    private static final String TABLE_SINGER_COL_COUNTRY = "country";

    public static final String[] TABLE_SINGER_ALL_COL = { TABLE_SINGER_COL_SINGERID,
        TABLE_SINGER_COL_SINGERNAME, TABLE_SINGER_COL_SPELL, TABLE_SINGER_COL_SONGERLOVE,
        TABLE_SINGER_COL_SINGERTYPEID, TABLE_SINGER_COL_PHOTO,
        TABLE_SINGER_COL_SINGERORDER, TABLE_SINGER_COL_UPDATETIME,
        TABLE_SINGER_COL_FILE_H, TABLE_SINGER_COL_FILE_L, TABLE_SINGER_COL_FILE_M,
        TABLE_SINGER_COL_FILE_S, TABLE_SINGER_COL_IMIT_PIC_0,
        TABLE_SINGER_COL_IMIT_PIC_1, TABLE_SINGER_COL_IMIT_PIC_2, TABLE_SINGER_COL_ISGROUP,
        TABLE_SINGER_COL_GENDER, TABLE_SINGER_COL_COUNTRY};
    
//    private Singer mSinger = null;

    public WholeRemoteSingerDAO() {
    }


    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSingerDAO#getSingerBySpell(java.lang.String, com.evideo.kmbox.model.dao.data.PageInfo)
     */
    @Override
    public List<Singer> getSingerBySpell(String spell, PageInfo pageInfo) {
        Cursor cursor = null;
        String selection;
        String limit = "";

        if (pageInfo != null) {
            limit = pageInfo.getPageIndex() * pageInfo.getPageSize() + ","
                    + pageInfo.getPageSize();
        }

        selection = TABLE_SINGER_COL_SPELL + " like '%" + spell + "%'";
        String orderBy = TABLE_SINGER_COL_SINGERORDER + " desc";
        SQLiteDatabase db = WholeDAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return Collections.emptyList();
        }

        List<Singer> singers = new ArrayList<Singer>();

        try {
            cursor = db.query(TABLE_SINGER_NAME, TABLE_SINGER_ALL_COL,
                    selection, null, null, null, orderBy, limit);

            while (cursor.moveToNext()) {
                Singer singer = new Singer(cursor.getInt(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getInt(16) /*gender*/,
                        cursor.getInt(15) == 1 /*group*/,
                        cursor.getInt(4) /*typeID*/, 
                        cursor.getInt(17) /*country*/,
                        cursor.getInt(6) /*playNum*/,
                        cursor.getString(7)/*updateTime*/,
                        cursor.getString(5)/*pictureID*/);
                singers.add(singer);
            }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
            return Collections.emptyList();
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            // db.close();
        }

        return singers;
    }
    
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSingerDAO#getSingerById(int)
     */
    @Override
    public Singer getSingerById(int id) {

        Cursor cursor = null;
        String selection = TABLE_SINGER_COL_SINGERID + "=" + id;
        SQLiteDatabase db = WholeDAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return null;
        }

        try {
            cursor = db.query(TABLE_SINGER_NAME, TABLE_SINGER_ALL_COL,
                    selection, null, null, null, null);
            if (cursor.moveToNext()) {
                Singer singer = new Singer(cursor.getInt(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getInt(16) /*gender*/,
                        cursor.getInt(15) == 1 /*group*/,
                        cursor.getInt(4) /*typeID*/, 
                        cursor.getInt(17) /*country*/,
                        cursor.getInt(6) /*playNum*/,
                        cursor.getString(7)/*updateTime*/,
                        cursor.getString(5)/*pictureID*/);

                return singer;
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
     * @see com.evideo.kmbox.dao.WholeSingerDAO#add(com.evideo.kmbox.update.db.RemoteSinger)
     */
    @Override
    public boolean add(RemoteSinger singer) {
        SQLiteDatabase db = WholeDAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        
        String sql = "insert or ignore into " + TABLE_SINGER_NAME + "(" 
                + TABLE_SINGER_COL_SINGERID + ","
                + TABLE_SINGER_COL_SINGERNAME + ","
                + TABLE_SINGER_COL_SPELL + ","
                + TABLE_SINGER_COL_SONGERLOVE + ","
                + TABLE_SINGER_COL_SINGERTYPEID + ","
                + TABLE_SINGER_COL_PHOTO + ","
                + TABLE_SINGER_COL_SINGERORDER + ","
                + TABLE_SINGER_COL_UPDATETIME + ","
                + TABLE_SINGER_COL_FILE_H + ","
                + TABLE_SINGER_COL_FILE_L + ","
                + TABLE_SINGER_COL_FILE_M + ","
                + TABLE_SINGER_COL_FILE_S + ","
                + TABLE_SINGER_COL_IMIT_PIC_0 + ","
                + TABLE_SINGER_COL_IMIT_PIC_1 + ","
                + TABLE_SINGER_COL_IMIT_PIC_2 + ","
                + TABLE_SINGER_COL_ISGROUP + ","
                + TABLE_SINGER_COL_GENDER + ","
                + TABLE_SINGER_COL_COUNTRY
                + ")"
                + " values(?,?,?,?,?,   ?,?,?,?,?,"
                        + "?,?,?,?,?,   ?,?,?)";
        
        SQLiteStatement stat = db.compileStatement(sql);
        
        db.beginTransaction();
        
        stat.bindLong(1, singer.getSongsterID());
        stat.bindString(2, singer.getSongsterName());
        stat.bindString(3, singer.getSongsterPy());
        stat.bindString(4, singer.getSongsterLove());
        stat.bindLong(5, singer.getSongsterTypeID());
        stat.bindString(6, singer.getPhotopath());
        stat.bindLong(7, singer.getSongsterOrderRank());
        stat.bindString(8, singer.getLastUpdateTime());
        stat.bindLong(9, singer.getPicFileIDH());
        stat.bindLong(10, singer.getPicFileIDL());
        stat.bindLong(11, singer.getPicFileIDM());
        stat.bindLong(12, singer.getPicFileIDS());
        stat.bindLong(13, singer.getImitatePicFileID0());
        stat.bindLong(14, singer.getImitatePicFileID1());
        stat.bindLong(15, singer.getImitatePicFileID2());
        stat.bindLong(16, singer.getIsGroup()?1:0);
        stat.bindLong(17, singer.getGender());
        stat.bindLong(18, singer.getCountry());
        stat.executeInsert();
        
        
        db.setTransactionSuccessful();
        db.endTransaction();

        return true;
    }
    
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSingerDAO#delete(int)
     */
    @Override
    public void delete(int id) {
        SQLiteDatabase db = WholeDAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return;
        }

        db.delete(TABLE_SINGER_NAME, TABLE_SINGER_COL_SINGERID + " = ?",
                new String[] { id + "" });
    }
    
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSingerDAO#update(com.evideo.kmbox.update.db.RemoteSinger)
     */
    @Override
    public boolean update(RemoteSinger singer) {
        SQLiteDatabase db = WholeDAOHelper.getInstance().getWritableDatabase();
        
        if (db == null) {
            return false;
        }
        
        String sql = "insert or replace into " + TABLE_SINGER_NAME + "(" 
                + TABLE_SINGER_COL_SINGERID + ","
                + TABLE_SINGER_COL_SINGERNAME + ","
                + TABLE_SINGER_COL_SPELL + ","
                + TABLE_SINGER_COL_SONGERLOVE + ","
                + TABLE_SINGER_COL_SINGERTYPEID + ","
                + TABLE_SINGER_COL_PHOTO + ","
                + TABLE_SINGER_COL_SINGERORDER + ","
                + TABLE_SINGER_COL_UPDATETIME + ","
                + TABLE_SINGER_COL_FILE_H + ","
                + TABLE_SINGER_COL_FILE_L + ","
                + TABLE_SINGER_COL_FILE_M + ","
                + TABLE_SINGER_COL_FILE_S + ","
                + TABLE_SINGER_COL_IMIT_PIC_0 + ","
                + TABLE_SINGER_COL_IMIT_PIC_1 + ","
                + TABLE_SINGER_COL_IMIT_PIC_2 + ","
                + TABLE_SINGER_COL_ISGROUP + ","
                + TABLE_SINGER_COL_GENDER + ","
                + TABLE_SINGER_COL_COUNTRY
                + ")"
                + " values(?,?,?,?,?,   ?,?,?,?,?,"
                        + "?,?,?,?,?,   ?,?,?)";
        
        SQLiteStatement stat = db.compileStatement(sql);
        
        db.beginTransaction();
        
        stat.bindLong(1, singer.getSongsterID());
        stat.bindString(2, singer.getSongsterName());
        stat.bindString(3, singer.getSongsterPy());
        stat.bindString(4, singer.getSongsterLove());
        stat.bindLong(5, singer.getSongsterTypeID());
        stat.bindString(6, singer.getPhotopath());
        stat.bindLong(7, singer.getSongsterOrderRank());
        stat.bindString(8, singer.getLastUpdateTime());
        stat.bindLong(9, singer.getPicFileIDH());
        stat.bindLong(10, singer.getPicFileIDL());
        stat.bindLong(11, singer.getPicFileIDM());
        stat.bindLong(12, singer.getPicFileIDS());
        stat.bindLong(13, singer.getImitatePicFileID0());
        stat.bindLong(14, singer.getImitatePicFileID1());
        stat.bindLong(15, singer.getImitatePicFileID2());
        stat.bindLong(16, singer.getIsGroup()?1:0);
        stat.bindLong(17, singer.getGender());
        stat.bindLong(18, singer.getCountry());
        stat.executeInsert();
        
        
        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }

    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSingerDAO#addUpdateByList(java.util.List)
     */
    @Override
    public void addUpdateByList(List<Singer> list) {
        SQLiteDatabase db = WholeDAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return;
        }
        
        //TODO 这里没开启事物时 SQLiteStatement较快，但开启事物时，execSQL较快网上说的，待验证

        Singer singer = null;

        String sql = "insert OR replace into " + TABLE_SINGER_NAME + "("
                + " values(?,?,?,?,?,?,?,?,?,?)";

        SQLiteStatement stat = db.compileStatement(sql);

        db.beginTransaction();

        for (int i = 0; i < list.size(); ++i) {
            singer = list.get(i);

            if (singer == null) {
                continue;
            }

            stat.bindLong(1, singer.getId());
            stat.bindString(2, singer.getName());
            stat.bindString(3, singer.getSpell());
            stat.bindLong(4, singer.getGender());
            stat.bindString(5, singer.isGroup() ? "true" : "false");
            stat.bindLong(6, singer.getType());
            stat.bindLong(7, singer.getCountry());
            stat.bindLong(8, singer.getPlayRate());
            stat.bindString(9, singer.getTimeStamp());
            stat.bindString(10, singer.getPictureResourceId());
            stat.execute();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }
    
    /**
     * [歌星首拼全词匹配查询]
     * 
     * @param spell
     *            [歌手拼音]
     * @param pageInfo
     *            [分页信息]
     * @return [返回满足拼音全词匹配spell的歌星信息]
     */
    private List<Singer> getSingerBySpellWholeMatch(String spell) {
        Cursor cursor = null;
        String selection;

        selection = TABLE_SINGER_COL_SPELL + "'" + spell + "'";
        String orderBy = TABLE_SINGER_COL_SINGERORDER + " desc";
        SQLiteDatabase db = WholeDAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return Collections.emptyList();
        }

        List<Singer> singers = new ArrayList<Singer>();

        try {
            cursor = db.query(TABLE_SINGER_NAME, TABLE_SINGER_ALL_COL,
                    selection, null, null, null, orderBy, null);

            while (cursor.moveToNext()) {
                Singer singer = new Singer(cursor.getInt(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4).compareTo("true") == 0,
                        cursor.getInt(5), cursor.getInt(6), cursor.getInt(7),
                        cursor.getString(8), cursor.getString(9));
                singers.add(singer);
            }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
            return Collections.emptyList();
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }

        return singers;
    }
    
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSingerDAO#save(java.util.List)
     */
    @Override
    public boolean save(List<Singer> list) {
        SQLiteDatabase db = WholeDAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return false;
        }

        Singer singer = null;

        String sql = "insert OR IGNORE into " + TABLE_SINGER_NAME + "(";

        SQLiteStatement stat = db.compileStatement(sql);

        db.beginTransaction();

        for (int i = 0; i < list.size(); ++i) {
            singer = list.get(i);

            if (singer == null) {
                continue;
            }

            stat.bindLong(1, singer.getId());
            stat.bindString(2, singer.getName());
            stat.bindString(3, singer.getSpell());
            stat.bindLong(4, singer.getGender());
            stat.bindLong(5, 0);
            stat.bindLong(6, singer.getType());
            stat.bindLong(7, singer.getPlayRate());
            stat.execute();

        }

        db.setTransactionSuccessful();
        db.endTransaction();

        return true;
    }
    
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSingerDAO#getSingerList(java.util.Map)
     */
    @Override
    public List<Singer> getSingerList(Map<Integer, Integer> map) {

        Cursor cursor = null;
        SQLiteDatabase db = WholeDAOHelper.getInstance().getReadableDatabase();
        
        List<Singer> singerList = new ArrayList<Singer>();

        Singer singer = null;

        db.beginTransaction();

        if (db == null || map.size() <= 0) {
            return null;
        }
        
        for (Integer key : map.keySet()) {
            int id = key;
            try {
                String selection = TABLE_SINGER_COL_SINGERID + "=" + id;
                cursor = db.query(TABLE_SINGER_NAME, TABLE_SINGER_ALL_COL,
                        selection, null, null, null, null);
                if (cursor == null) {
                    continue;
                }
                if (cursor.moveToNext()) {
                    singer = new Singer(cursor.getInt(0),
                            cursor.getString(1), cursor.getString(2),
                            cursor.getInt(3),
                            cursor.getString(4).compareTo("true") == 0,
                            cursor.getInt(5), cursor.getInt(6), cursor.getInt(7),
                            cursor.getString(8), cursor.getString(9));
                    singerList.add(singer);
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
        
        return singerList;
    }
    
    /* (non-Javadoc)
     * @see com.evideo.kmbox.dao.WholeSingerDAO#isExist(int)
     */
    @Override
    public boolean isExist(int singerId) {
        Cursor cursor = null;
        SQLiteDatabase db = WholeDAOHelper.getInstance().getReadableDatabase();
        String selection = TABLE_SINGER_COL_SINGERID + "=" + singerId;
        cursor = db.query(TABLE_SINGER_NAME, TABLE_SINGER_ALL_COL,
                selection, null, null, null, null);
        if (cursor.moveToNext()) {
            return true;
        }
        return false;
    }
}
