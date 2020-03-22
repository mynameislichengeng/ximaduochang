package com.evideo.kmbox.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.TypeConvert;

public class SingerDAO {

    // table singer
    private static final String TABLE_SINGER_NAME = "tblSinger";
    private static final String TABLE_REMOTE_SINGER_NAME = "tblRemoteSinger";
    private static final String TABLE_SINGER_COL_ID = "id";
    private static final String TABLE_SINGER_COL_NAME = "name";
    private static final String TABLE_SINGER_COL_SPELL = "spell";
    private static final String TABLE_SINGER_COL_GENDER = "gender";
    private static final String TABLE_SINGER_COL_GROUP = "isGroup";
    private static final String TABLE_SINGER_COL_TYPE = "type";
    private static final String TABLE_SINGER_COL_COUNTRY = "country";
    private static final String TABLE_SINGER_COL_PLAYRATE = "playRate";
    private static final String TABLE_SINGER_COL_UPDATETIME = "updateTime";
    private static final String TABLE_SINGER_COL_PICTURE = "picture";
    private static final String[] TABLE_SINGER_ALL_COL = {TABLE_SINGER_COL_ID, TABLE_SINGER_COL_NAME, TABLE_SINGER_COL_SPELL,
            TABLE_SINGER_COL_GENDER, TABLE_SINGER_COL_GROUP, TABLE_SINGER_COL_TYPE, /*TABLE_SINGER_COL_COUNTRY,*/ TABLE_SINGER_COL_PLAYRATE,
            TABLE_SINGER_COL_PICTURE};

    public SingerDAO() {
    }

    public Singer getSingerById(int id) {

        Cursor cursor = null;
        String selection = TABLE_SINGER_COL_ID + "=" + id;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return null;
        }

        try {
            cursor = db.query(TABLE_SINGER_NAME, TABLE_SINGER_ALL_COL, selection, null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                Singer singer = new Singer(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getInt(3), cursor.getString(4) == null ? false : cursor.getString(4).compareTo("true") == 0,
                        cursor.getInt(5), cursor.getInt(6));
                singer.setPictureResourceId(cursor.getString(7));
                return singer;
            }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            //db.close();
        }

        return null;
    }

    public boolean update(Singer singer) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(TABLE_SINGER_COL_NAME, singer.getName());
        values.put(TABLE_SINGER_COL_GENDER, singer.getGender());
        values.put(TABLE_SINGER_COL_GROUP, singer.isGroup());
        values.put(TABLE_SINGER_COL_TYPE, singer.getType());
        //values.put(TABLE_SINGER_COL_COUNTRY, "");
        values.put(TABLE_SINGER_COL_PLAYRATE, singer.getPlayRate());

        // TODO
        long rowsEffect = -1;
        try {
            rowsEffect = db.update(TABLE_SINGER_NAME, values, TABLE_SINGER_COL_ID + " = ?", new String[]{String.valueOf(singer.getId())});
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        //db.close();

        return rowsEffect > 0;
    }

    public boolean add(Singer singer) {
        // TODO verify song info
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return false;
        }

        ContentValues values = new ContentValues();

        values.put(TABLE_SINGER_COL_ID, singer.getId());
        values.put(TABLE_SINGER_COL_NAME, singer.getName());
        values.put(TABLE_SINGER_COL_SPELL, singer.getSpell());
        values.put(TABLE_SINGER_COL_GENDER, singer.getGender());
        values.put(TABLE_SINGER_COL_GROUP, singer.isGroup());
        values.put(TABLE_SINGER_COL_TYPE, singer.getType());
        //values.put(TABLE_SINGER_COL_COUNTRY, singer.getCountry());
        values.put(TABLE_SINGER_COL_PLAYRATE, singer.getPlayRate());

        long rowEffect = -1;
        try {
            rowEffect = db.insert(TABLE_SINGER_NAME, null, values);
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }

        return rowEffect > 0;
    }

    public void delete(int id) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return;
        }

        try {
            db.delete(TABLE_SINGER_NAME, TABLE_SINGER_COL_ID + " = ?", new String[]{id + ""});
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        //db.close();
    }

    public List<Singer> getList(PageInfo pageInfo) {
        Cursor cursor = null;
        String limit = "";
        if (pageInfo != null) {
            limit = pageInfo.getPageIndex() * pageInfo.getPageSize() + "," + pageInfo.getPageSize();
        }

        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return Collections.emptyList();
        }

        List<Singer> singers = new ArrayList<Singer>();

        try {
            cursor = db.query(TABLE_SINGER_NAME, TABLE_SINGER_ALL_COL, null, null, null, null, null, limit);

            while (cursor.moveToNext()) {
                Singer singer = new Singer(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getInt(3), cursor.getString(4) == null ? false : cursor.getString(4).compareTo("true") == 0,
                        cursor.getInt(5), cursor.getInt(6));
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

            //db.close();
        }

        return singers;
    }

    public boolean save(List<Singer> list) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return false;
        }

        Singer singer = null;

        String sql = "insert OR IGNORE into " + TABLE_SINGER_NAME + "("
                + TABLE_SINGER_COL_ID + ","
                + TABLE_SINGER_COL_NAME + ","
                + TABLE_SINGER_COL_SPELL + ","
                + TABLE_SINGER_COL_GENDER + ","
                + TABLE_SINGER_COL_GROUP + ","
                + TABLE_SINGER_COL_TYPE + ","
                + TABLE_SINGER_COL_PLAYRATE + ")"
                + " values(?,?,?,?,?,?,?)";

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

    public void beginUpateRemoteData() {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return;
        }

        String sql = "CREATE TABLE if not exists [tblRemoteSinger] ("
                + "[id] INT NOT NULL, "
                + "[name] NVARCHAR NOT NULL, "
                + "[spell] NVARCHAR NOT NULL, "
                + "[gender] INT DEFAULT 0, "
                + "[isGroup] BOOL, "
                + "[type] INT, "
                + "[country] INT DEFAULT 0, "
                + "[playRate] INT DEFAULT 0, "
                + "[updateTime] datetime, "
                + "[picture] NVARCHAR(20), "
                + "CONSTRAINT [sqlite_autoindex_tblSinger_1] PRIMARY KEY ([id] COLLATE NOCASE ASC));delete * from tblRemoteSinger;";

        SQLiteStatement stat = db.compileStatement(sql);

        db.beginTransaction();
        stat.execute();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void updateRemoteData(List<Singer> list) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return;
        }

        Singer singer = null;

        String sql = "insert OR replace into " + TABLE_REMOTE_SINGER_NAME + "("
                + TABLE_SINGER_COL_ID + ","
                + TABLE_SINGER_COL_NAME + ","
                + TABLE_SINGER_COL_SPELL + ","
                + TABLE_SINGER_COL_GENDER + ","
                + TABLE_SINGER_COL_GROUP + ","
                + TABLE_SINGER_COL_TYPE + ","
                + TABLE_SINGER_COL_COUNTRY + ","
                + TABLE_SINGER_COL_PLAYRATE + ","
                + TABLE_SINGER_COL_UPDATETIME + ","
                + TABLE_SINGER_COL_PICTURE + ")"
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
     * [本地歌星数据生成-tblSong表生成完毕后调用]
     */
    public void createLocalSinger() {
        //本地库local_kmbox.db tblSinger表插入最新数据
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        if (db == null) {
            return;
        }
        String sql = "insert or replace into tblSinger(id,name,spell,type,updateTime) select SongsterID,SongsterName,SongsterPy,SongsterTypeID,"
                + "LastUpdateTime from "
                + "(select distinct * from (select tblSinger.* from tblSong, wholedb.tblSinger where tblSong.[singerId0]=wholedb.tblSinger.[SongsterID]) union"
                + " select distinct * from (select tblSinger.* from tblSong, wholedb.tblSinger where tblSong.[singerId1]=wholedb.tblSinger.[SongsterID]) union"
                + " select distinct * from (select tblSinger.* from tblSong, wholedb.tblSinger where tblSong.[singerId2]=wholedb.tblSinger.[SongsterID]) union"
                + " select distinct * from (select tblSinger.* from tblSong, wholedb.tblSinger where tblSong.[singerId3]=wholedb.tblSinger.[SongsterID]));";
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
    }

    /**
     * [功能说明]
     *
     * @param spell     拼音
     * @param pageInfo  分页
     * @param typeIndex 类型索引
     * @return singerList
     */
    public List<Singer> getSingerBySpell(String spell, PageInfo pageInfo, int typeIndex) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return Collections.emptyList();
        }

        String limit = "";
        if (pageInfo != null) {
            limit = pageInfo.getPageIndex() * pageInfo.getPageSize() + "," + pageInfo.getPageSize();
        }

        String orderBy = null;
        String selection = null;
        if (spell != null) {
            orderBy = TABLE_SINGER_COL_PLAYRATE + " desc";
            StringBuilder selectionBuilder = new StringBuilder();
            selectionBuilder.append(TABLE_SINGER_COL_SPELL).append(" like '%").append(spell).append("%'");
            if (typeIndex > 0 && typeIndex <= 7) {
                selectionBuilder.append(" AND ").append(TABLE_SINGER_COL_TYPE).append(" = ").append(typeIndex);
            }
            selection = selectionBuilder.toString();
        } else {
            orderBy = TABLE_SINGER_COL_PLAYRATE + " desc";
        }

        List<Singer> singers = new ArrayList<Singer>();

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SINGER_NAME, TABLE_SINGER_ALL_COL, selection, null, null, null, orderBy, limit);

            while (cursor.moveToNext()) {
                Singer singer = new Singer(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getInt(3), cursor.getString(4).compareTo("true") == 0,
                        cursor.getInt(5), cursor.getInt(6));
                singer.setPictureResourceId(cursor.getString(7));
                singers.add(singer);
            }
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return singers;
    }

    /**
     * [功能说明] 根据首拼查找歌星
     *
     * @param spell    首拼
     * @param pageInfo 分页信息
     * @return 歌曲列表
     */
    public List<Singer> getSingerBySpell(String spell, PageInfo pageInfo) {
        return this.getSingerBySpell(spell, pageInfo, 0);
    }

    public List<Singer> getSingerByName(String singerName) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return Collections.emptyList();
        }

        String limit = "";
        String orderBy = null;
        String selection = null;
        if (singerName != null) {
            orderBy = TABLE_SINGER_COL_PLAYRATE + " desc";
            StringBuilder selectionBuilder = new StringBuilder();
            selectionBuilder.append(TABLE_SINGER_COL_NAME).append(" like '%").append(singerName).append("%'");
            selection = selectionBuilder.toString();
        } else {
            orderBy = TABLE_SINGER_COL_PLAYRATE + " desc";
        }

        List<Singer> singers = new ArrayList<Singer>();

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SINGER_NAME, TABLE_SINGER_ALL_COL, selection, null, null, null, orderBy, limit);
            while (cursor.moveToNext()) {
                Singer singer = new Singer(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getInt(3), cursor.getString(4).compareTo("true") == 0,
                        cursor.getInt(5), cursor.getInt(6));
                singer.setPictureResourceId(cursor.getString(7));
                singers.add(singer);
            }
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return singers;
    }

    /**
     * [功能说明]
     *
     * @param spell 首拼
     * @return count 数量
     */
    public int getSingerCountBySpell(String spell) {
        return this.getSingerCountBySpell(spell, 0);
    }

    /**
     * [功能说明]
     *
     * @param spell     首拼
     * @param typeIndex 类型索引
     * @return singerList
     */
    public int getSingerCountBySpell(String spell, int typeIndex) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        int count = 0;
        if (db == null) {
            return count;
        }

        String selection = TABLE_SINGER_COL_SPELL + " like '%" + spell + "%'";
        if (typeIndex > 0 && typeIndex <= 7) {
            selection = selection + " AND " + TABLE_SINGER_COL_TYPE + " = " + typeIndex;
        }
        String orderBy = TABLE_SINGER_COL_PLAYRATE + " desc";

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SINGER_NAME, TABLE_SINGER_ALL_COL, selection, null, null, null, orderBy, null);


        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                count = cursor.getCount();
                cursor.close();
            }
        }
        return count;
    }

    public void endUpdateRemoteData() {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return;
        }

        String sql = "alter table tblSinger rename to tblTempSinger";
        SQLiteStatement stat = db.compileStatement(sql);

        db.beginTransaction();
        stat.execute();
        sql = "alter table tblRemoteSinger rename to tblSinger";
        stat = db.compileStatement(sql);
        stat.execute();
        sql = "drop table tblTempSinger";
        stat = db.compileStatement(sql);
        stat.execute();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * [清空歌星表]
     */
    public void clearList() {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return;
        }
        db.execSQL("delete from " + TABLE_SINGER_NAME);
        return;
    }

    /**
     * [通过复合条件查询歌星]
     *
     * @param singerId     歌星id
     * @param singerName   歌星名称
     * @param singerSpell  歌星简拼
     * @param singerTypeId 歌星类型id
     * @param startPos     起始位置
     * @param requestNum   请求数量
     * @return 歌星列表
     */
    public List<Singer> getSingerByComplex(int singerId, String singerName,
                                           String singerSpell, int singerTypeId, int startPos, int requestNum) {
        String limit = "";
        if ((requestNum > 0) && (startPos >= 0)) {
            limit = Integer.toString(startPos) + ","
                    + Integer.toString(requestNum);
        }
        String singerInput = "";
        String inputcolume = "";
        int count = 0;
        String[] strs = new String[2];

        if (requestNum <= 0 && startPos < 0) {
            return Collections.emptyList();
        }

        List<Singer> singers = new ArrayList<Singer>();

        if (!TextUtils.isEmpty(singerName)) {
            singerName = singerName.toLowerCase();
            singerInput = singerName;
            inputcolume = "lower(" + TABLE_SINGER_COL_NAME + ")";
            strs = getArgsByComplex(singerId, singerInput,
                    inputcolume, singerTypeId);
            count = getCountQureySinger(strs[0]);
            singers = getQureySingerList(TABLE_SINGER_NAME, TABLE_SINGER_ALL_COL,
                    strs[0], null, null, null, strs[1], limit);
        }
        if (singers.size() < requestNum && !TextUtils.isEmpty(singerSpell)) {
            requestNum = requestNum - singers.size();
            if (startPos > count) {
                startPos = startPos - count;
            } else {
                startPos = 0;
            }
            if ((requestNum > 0) && (startPos >= 0)) {
                limit = Integer.toString(startPos) + ","
                        + Integer.toString(requestNum);
            }
            singerSpell = singerSpell.toUpperCase();
            singerInput = singerSpell;
            inputcolume = "upper(" + TABLE_SINGER_COL_SPELL + ")";
            strs = getArgsByComplex(singerId, singerInput,
                    inputcolume, singerTypeId);
            singers.addAll(getQureySingerList(TABLE_SINGER_NAME, TABLE_SINGER_ALL_COL,
                    strs[0], null, null, null, strs[1], limit));
        }

        if (singers.size() == 0 && TextUtils.isEmpty(singerName) && TextUtils.isEmpty(singerSpell)) {
            strs = getArgsByComplex(singerId, singerInput,
                    inputcolume, singerTypeId);
            count = getCountQureySinger(strs[0]);
            singers = getQureySingerList(TABLE_SINGER_NAME, TABLE_SINGER_ALL_COL,
                    strs[0], null, null, null, strs[1], limit);
        }

        return singers;
    }

    private String[] getArgsByComplex(int singerId, String singerInput,
                                      String inputcolume, int singerTypeId) {
        String[] strs = new String[2];
        StringBuffer selectionStrb = new StringBuffer();
        //标记需要复合搜索条件
        boolean isExit = false;

        if (singerId > 0) {
            selectionStrb.append(TABLE_SINGER_COL_ID + "=" + singerId);
            isExit = true;
        }

        if (!TextUtils.isEmpty(singerInput)) {
            String str = TypeConvert.strTrim(singerInput);
            if (isExit) {
                selectionStrb.append(" and ");
            }
            selectionStrb.append(inputcolume + " like '%" + singerInput + "%' ");
            selectionStrb.append(" or ");
            selectionStrb.append(inputcolume + " like '%" + str + "%' ");
            isExit = true;
        }
        if (singerTypeId > 0) {
            if (isExit) {
                selectionStrb.append(" and ");
            }
            selectionStrb.append(TABLE_SINGER_COL_TYPE + "=" + singerTypeId);
        }

        String orderBy = "";
        if (!TextUtils.isEmpty(singerInput)) {
            orderBy = TABLE_SINGER_COL_PLAYRATE + " desc";
        } else {
            orderBy = TABLE_SINGER_COL_PLAYRATE + " desc";
        }

        strs[0] = selectionStrb.toString();
        strs[1] = orderBy;
        return strs;
    }


    private List<Singer> getQureySingerList(String table, String[] columns, String selection,
                                            String[] selectionArgs, String groupBy, String having,
                                            String orderBy, String limit) {
        Cursor cursor = null;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return Collections.emptyList();
        }

        List<Singer> singers = new ArrayList<Singer>();

        try {
            cursor = db.query(table, columns,
                    selection, selectionArgs, groupBy, having, orderBy, limit);

            while (cursor.moveToNext()) {
                Singer singer = new Singer(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getInt(3), cursor.getString(4) == null ? false : cursor.getString(4).compareTo("true") == 0,
                        cursor.getInt(5), cursor.getInt(6));
                singer.setPictureResourceId(cursor.getString(7));
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

    private int getCountQureySinger(String selection) {
        int count = 0;
        Cursor cursor = null;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return 0;
        }

        try {
            cursor = db.query(TABLE_SINGER_NAME, new String[]{"count(*)"},
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
