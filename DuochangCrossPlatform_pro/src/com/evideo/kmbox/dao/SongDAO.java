package com.evideo.kmbox.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.evideo.kmbox.model.dao.data.Media;
import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.model.dao.data.SingerManager;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.TypeConvert;

/**
 * [功能说明]歌曲数据类
 */
public final class SongDAO {

    // table song
    private static final String TABLE_SONG_NAME = "tblSong";
    private static final String TABLE_REMOTE_SONG_NAME = "tblRemoteSong";
    private static final String TABLE_SONG_COL_ID = "id";
    private static final String TABLE_SONG_COL_NAME = "name";
    private static final String TABLE_SONG_COL_SPELL = "spell";
    private static final String TABLE_SONG_COL_LANGUAGE = "language";
    private static final String TABLE_SONG_COL_TYPE = "type";
    private static final String TABLE_SONG_COL_PLAYRATE = "playRate";

    private enum TableSongColumn {
        ID("id"), NAME("name"), SPELL("spell"), SINGER("singer"), SINGER0("singerId0"),
        SINGER1("singerId1"), SINGER2("singerId2"), SINGER3("singerId3"), LANGUAGE("language"),
        TYPE("type"), PLAYRATE("playRate"), ALBUM("album"), SCORE("score"), hasRemote("hasRemote"),
        hasLocal("hasLocal");
        private String mName;

        private TableSongColumn(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }
    }

    ;

    private static String[] TABLE_SONG_ALL_COL = null;

    public SongDAO() {
        TABLE_SONG_ALL_COL = new String[TableSongColumn.values().length];

        for (int i = 0; i < TABLE_SONG_ALL_COL.length; i++) {
            TABLE_SONG_ALL_COL[TableSongColumn.values()[i].ordinal()] = TableSongColumn.values()[i].getName();
        }
    }

    public Song getRandomSongWithCanScore() {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return null;
        }

        String limit = "";
        /*if (pageInfo != null)*/
        {
            int min = 0;
            int max = 500;
            Random random = new Random();

            int index = random.nextInt(max) % (max - min + 1) + min;
            limit = index + "," + 1;
        }

        String selection = TableSongColumn.SCORE.getName() + "=" + "1";
        String orderBy = TableSongColumn.PLAYRATE.getName() + " desc";
        Song song = null;

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_NAME, TABLE_SONG_ALL_COL, selection, null, null, null, orderBy, limit);

            while (cursor.moveToNext()) {
                song = new Song(cursor.getInt(TableSongColumn.ID.ordinal()),
                        cursor.getString(TableSongColumn.NAME.ordinal()),
                        cursor.getString(TableSongColumn.SPELL.ordinal()),
                        cursor.getString(TableSongColumn.SINGER.ordinal()),
                        new int[]{cursor.getInt(TableSongColumn.SINGER0.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER1.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER2.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER3.ordinal())},
                        cursor.getInt(TableSongColumn.LANGUAGE.ordinal()),
                        cursor.getInt(TableSongColumn.TYPE.ordinal()),
                        cursor.getInt(TableSongColumn.PLAYRATE.ordinal()),
                        cursor.getString(TableSongColumn.ALBUM.ordinal()),
                        cursor.getInt(TableSongColumn.SCORE.ordinal()) == 1);
                song.setHasRemoteFile(cursor.getInt(TableSongColumn.hasRemote.ordinal()));
                song.setHasLocal(cursor.getInt(TableSongColumn.hasLocal.ordinal()));
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

    public Song getRandomSong(String spell/*,PageInfo pageInfo*/) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return null;
        }

        String limit = "";
        int min = 0;
        int max = 5000;
        Random random = new Random();
        int index = random.nextInt(max) % (max - min + 1) + min;
        limit = index + "," + 1;

        String selection = TableSongColumn.SPELL.getName() + " like '%" + spell + "%'";
        String orderBy = TableSongColumn.PLAYRATE.getName() + " desc";
        Song song = null;

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_NAME, TABLE_SONG_ALL_COL, selection, null, null, null, orderBy, limit);

            while (cursor.moveToNext()) {
                song = new Song(cursor.getInt(TableSongColumn.ID.ordinal()),
                        cursor.getString(TableSongColumn.NAME.ordinal()),
                        cursor.getString(TableSongColumn.SPELL.ordinal()),
                        cursor.getString(TableSongColumn.SINGER.ordinal()),
                        new int[]{cursor.getInt(TableSongColumn.SINGER0.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER1.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER2.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER3.ordinal())},
                        cursor.getInt(TableSongColumn.LANGUAGE.ordinal()),
                        cursor.getInt(TableSongColumn.TYPE.ordinal()),
                        cursor.getInt(TableSongColumn.PLAYRATE.ordinal()),
                        cursor.getString(TableSongColumn.ALBUM.ordinal()),
                        cursor.getInt(TableSongColumn.SCORE.ordinal()) == 1);
                song.setHasRemoteFile(cursor.getInt(TableSongColumn.hasRemote.ordinal()));
                song.setHasLocal(cursor.getInt(TableSongColumn.hasLocal.ordinal()));
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

    /**
     * [功能说明] 根据首拼查找歌曲
     *
     * @param spell    首拼
     * @param pageInfo 分页信息
     * @return 歌曲列表
     */
    public List<Song> getSongBySpell(String spell, PageInfo pageInfo) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return Collections.emptyList();
        }

        String limit = "";
        if (pageInfo != null) {
            limit = pageInfo.getPageIndex() * pageInfo.getPageSize() + "," + pageInfo.getPageSize();
        }

        String selection = TableSongColumn.SPELL.getName() + " like '%" + spell + "%'";
        String orderBy = TableSongColumn.PLAYRATE.getName() + " desc";
        List<Song> songs = new ArrayList<Song>();

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_NAME, TABLE_SONG_ALL_COL, selection, null, null, null, orderBy, limit);

            while (cursor.moveToNext()) {
                Song song = new Song(cursor.getInt(TableSongColumn.ID.ordinal()),
                        cursor.getString(TableSongColumn.NAME.ordinal()),
                        cursor.getString(TableSongColumn.SPELL.ordinal()),
                        cursor.getString(TableSongColumn.SINGER.ordinal()),
                        new int[]{cursor.getInt(TableSongColumn.SINGER0.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER1.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER2.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER3.ordinal())},
                        cursor.getInt(TableSongColumn.LANGUAGE.ordinal()),
                        cursor.getInt(TableSongColumn.TYPE.ordinal()),
                        cursor.getInt(TableSongColumn.PLAYRATE.ordinal()),
                        cursor.getString(TableSongColumn.ALBUM.ordinal()),
                        cursor.getInt(TableSongColumn.SCORE.ordinal()) == 1);
                song.setHasRemoteFile(cursor.getInt(TableSongColumn.hasRemote.ordinal()));
                song.setHasLocal(cursor.getInt(TableSongColumn.hasLocal.ordinal()));
                songs.add(song);
            }
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }


        return songs;
    }

    public List<Song> getSongByName(String keyWord) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return Collections.emptyList();
        }

        String selection = TableSongColumn.NAME.getName() + " like '%" + keyWord + "%'"
                + " OR " + TableSongColumn.SINGER.getName() + " like '%" + keyWord + "%'";
        String orderBy = TableSongColumn.PLAYRATE.getName() + " desc";
        List<Song> songs = new ArrayList<Song>();

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_NAME, TABLE_SONG_ALL_COL, selection, null, null, null, orderBy, null);

            while (cursor.moveToNext()) {
                Song song = new Song(cursor.getInt(TableSongColumn.ID.ordinal()),
                        cursor.getString(TableSongColumn.NAME.ordinal()),
                        cursor.getString(TableSongColumn.SPELL.ordinal()),
                        cursor.getString(TableSongColumn.SINGER.ordinal()),
                        new int[]{cursor.getInt(TableSongColumn.SINGER0.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER1.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER2.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER3.ordinal())},
                        cursor.getInt(TableSongColumn.LANGUAGE.ordinal()),
                        cursor.getInt(TableSongColumn.TYPE.ordinal()),
                        cursor.getInt(TableSongColumn.PLAYRATE.ordinal()),
                        cursor.getString(TableSongColumn.ALBUM.ordinal()),
                        cursor.getInt(TableSongColumn.SCORE.ordinal()) == 1);
                song.setHasRemoteFile(cursor.getInt(TableSongColumn.hasRemote.ordinal()));
                song.setHasLocal(cursor.getInt(TableSongColumn.hasLocal.ordinal()));
                songs.add(song);
            }
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return songs;
    }

    /**
     * [功能说明] 获取缓存歌曲数量
     *
     * @return 已缓存歌曲数量
     */
    public int getCachedCount() {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return 0;
        }

        int count = 0;
        // String selection = TABLE_SONG_COL_LOCALRESOURCETICK + ">0";
        StringBuilder selectionBuilder = new StringBuilder();
        selectionBuilder.append(TABLE_SONG_NAME);
        selectionBuilder.append(".");
        selectionBuilder.append(TableSongColumn.ID.getName());
        selectionBuilder.append("=");
        selectionBuilder.append(TableMedia.NAME);
        selectionBuilder.append(".");
        selectionBuilder.append(TableMedia.Column.SONG_ID.getName());
        selectionBuilder.append(" and ");
        selectionBuilder.append("length(");
        selectionBuilder.append(TableMedia.NAME);
        selectionBuilder.append(".");
        selectionBuilder.append(TableMedia.Column.VOLUME_UUID.getName());
        selectionBuilder.append(")>0");

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select ");
        sqlBuilder.append("count(");
        sqlBuilder.append("distinct ");
        sqlBuilder.append(TABLE_SONG_NAME);
        sqlBuilder.append(".");
        sqlBuilder.append(TableSongColumn.ID.getName());
        sqlBuilder.append(") ");
        sqlBuilder.append(" from ");
        sqlBuilder.append(TABLE_SONG_NAME);
        sqlBuilder.append(",");
        sqlBuilder.append(TableMedia.NAME);
        sqlBuilder.append(" where ");
        sqlBuilder.append(selectionBuilder);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sqlBuilder.toString(), null);

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

    /**
     * [功能说明] 获取缓存歌曲列表
     *
     * @param pageInfo 分页信息
     * @return 已缓存歌曲的列表
     */
    public List<Song> getCachedList(PageInfo pageInfo) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return Collections.emptyList();
        }

        StringBuilder fieldBuilder = new StringBuilder();

        for (int i = 0; i < TableSongColumn.values().length; i++) {
            fieldBuilder.append(TABLE_SONG_NAME);
            fieldBuilder.append(".");
            fieldBuilder.append(TableSongColumn.values()[i].getName());
            fieldBuilder.append(",");
        }

        fieldBuilder.deleteCharAt(fieldBuilder.length() - 1);

        StringBuilder selectionBuilder = new StringBuilder();
        selectionBuilder.append(TABLE_SONG_NAME);
        selectionBuilder.append(".");
        selectionBuilder.append(TableSongColumn.ID.getName());
        selectionBuilder.append("=");
        selectionBuilder.append(TableMedia.NAME);
        selectionBuilder.append(".");
        selectionBuilder.append(TableMedia.Column.SONG_ID.getName());
        selectionBuilder.append(" and ");
        selectionBuilder.append("length(");
        selectionBuilder.append(TableMedia.NAME);
        selectionBuilder.append(".");
        selectionBuilder.append(TableMedia.Column.VOLUME_UUID.getName());
        selectionBuilder.append(")>0");

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select distinct ");
        sqlBuilder.append(fieldBuilder);
        sqlBuilder.append(" from ");
        sqlBuilder.append(TABLE_SONG_NAME);
        sqlBuilder.append(",");
        sqlBuilder.append(TableMedia.NAME);
        sqlBuilder.append(" where ");
        sqlBuilder.append(selectionBuilder);
        sqlBuilder.append(" order by ");
        sqlBuilder.append(TableSongColumn.PLAYRATE.getName());
        sqlBuilder.append(" asc");

        if (pageInfo != null) {
            sqlBuilder.append(" limit ");
            sqlBuilder.append(pageInfo.getPageIndex() * pageInfo.getPageSize());
            sqlBuilder.append(",");
            sqlBuilder.append(pageInfo.getPageSize());
        }

        List<Song> songs = new ArrayList<Song>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sqlBuilder.toString(), null);

            while (cursor.moveToNext()) {
                Song song = new Song(cursor.getInt(TableSongColumn.ID.ordinal()),
                        cursor.getString(TableSongColumn.NAME.ordinal()),
                        cursor.getString(TableSongColumn.SPELL.ordinal()),
                        cursor.getString(TableSongColumn.SINGER.ordinal()),
                        new int[]{cursor.getInt(TableSongColumn.SINGER0.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER1.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER2.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER3.ordinal())},
                        cursor.getInt(TableSongColumn.LANGUAGE.ordinal()),
                        cursor.getInt(TableSongColumn.TYPE.ordinal()),
                        cursor.getInt(TableSongColumn.PLAYRATE.ordinal()),
                        cursor.getString(TableSongColumn.ALBUM.ordinal()),
                        cursor.getInt(TableSongColumn.SCORE.ordinal()) == 1);
                song.setHasRemoteFile(cursor.getInt(TableSongColumn.hasRemote.ordinal()));
                song.setHasLocal(cursor.getInt(TableSongColumn.hasLocal.ordinal()));
                songs.add(song);
            }
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return songs;
    }

    /**
     * [功能说明] 获取歌曲的媒体文件
     *
     * @param song 歌曲
     * @return 歌曲媒体文件列表
     */
    public List<Media> getMedia(Song song) {
        if (song == null) {
            return null;
        }

        return DAOFactory.getInstance().getMediaDAO().getMedia(song.getId());
    }

    /**
     * [功能说明] 获取歌曲
     *
     * @param id 歌曲id
     * @return 歌曲
     */
    public Song getSongById(int id) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return null;
        }

        Song song = null;
        String selection = TableSongColumn.ID.getName() + "=" + id;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_NAME, TABLE_SONG_ALL_COL, selection, null, null, null, null);

            if (cursor.moveToNext()) {
                song = new Song(cursor.getInt(TableSongColumn.ID.ordinal()),
                        cursor.getString(TableSongColumn.NAME.ordinal()),
                        cursor.getString(TableSongColumn.SPELL.ordinal()),
                        cursor.getString(TableSongColumn.SINGER.ordinal()),
                        new int[]{cursor.getInt(TableSongColumn.SINGER0.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER1.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER2.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER3.ordinal())},
                        cursor.getInt(TableSongColumn.LANGUAGE.ordinal()),
                        cursor.getInt(TableSongColumn.TYPE.ordinal()),
                        cursor.getInt(TableSongColumn.PLAYRATE.ordinal()),
                        cursor.getString(TableSongColumn.ALBUM.ordinal()),
                        cursor.getInt(TableSongColumn.SCORE.ordinal()) == 1);
                song.setHasRemoteFile(cursor.getInt(TableSongColumn.hasRemote.ordinal()));
                song.setHasLocal(cursor.getInt(TableSongColumn.hasLocal.ordinal()));
            }
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return song;
    }

    /**
     * [功能说明] 判断歌曲是否存在
     *
     * @param songId 歌曲id
     * @return 如果存在则返回true，否则返回false
     */
    public boolean isExist(int songId) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return false;
        }

        String selection = TableSongColumn.ID.getName() + "=" + songId;
        boolean exist = false;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_NAME, new String[]{TableSongColumn.ID.getName()}, selection, null, null, null, null);
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
     * [功能说明] 删除歌曲
     *
     * @param song 歌曲
     */
    public void deleteSong(Song song) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return;
        }

        try {
            db.delete(TABLE_SONG_NAME, TableSongColumn.ID.getName() + " = ?", new String[]{song.getId() + ""});
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
    }

    /**
     * [功能说明] 根据歌曲id删除歌曲列表
     *
     * @param ids 歌曲id列表
     * @throws Exception exception
     */
    public void deleteSongList(List<Integer> ids) throws Exception {
        if (ids == null || ids.size() <= 0) {
            return;
        }

        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();
        if (db == null) {
            return;
        }

        db.beginTransaction();

        try {
            StringBuilder sb = new StringBuilder(TableSongColumn.ID.getName() + " in (");
            int size = ids.size();
            for (int i = 0; i < size; i++) {
                if (i == (size - 1)) {
                    sb.append(ids.get(i) + ")");
                } else {
                    sb.append(ids.get(i) + ",");
                }
            }
            db.delete(TABLE_SONG_NAME, sb.toString(), null);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
            throw e;
        } finally {
            db.endTransaction();
        }

    }

    /**
     * [功能说明] 更新歌曲信息
     *
     * @param song 歌曲信息
     * @return 更新成功则返回true，否则返回false
     */
    public boolean update(Song song) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(TableSongColumn.NAME.getName(), song.getName());
        values.put(TableSongColumn.SPELL.getName(), song.getSpell());
        values.put(TableSongColumn.SINGER.getName(), song.getSingerDescription());
        values.put(TableSongColumn.SINGER0.getName(), song.getSingerId(0));
        values.put(TableSongColumn.SINGER1.getName(), song.getSingerId(1));
        values.put(TableSongColumn.SINGER2.getName(), song.getSingerId(2));
        values.put(TableSongColumn.SINGER3.getName(), song.getSingerId(3));
        values.put(TableSongColumn.LANGUAGE.getName(), song.getLanguage());
        values.put(TableSongColumn.TYPE.getName(), song.getType());
        values.put(TableSongColumn.PLAYRATE.getName(), song.getPlayRate());
        values.put(TableSongColumn.ALBUM.getName(), song.getAlbumResource());
        values.put(TableSongColumn.SCORE.getName(), song.canScore() ? 1 : 0);
        values.put(TableSongColumn.hasRemote.getName(), song.getHasRemoteFile());
        values.put(TableSongColumn.hasLocal.getName(), song.getHasLocal());
        // TODO
        long rowsEffect = -1;
        try {
            rowsEffect = db.update(TABLE_SONG_NAME, values, TableSongColumn.ID.getName() + " = ?",
                    new String[]{String.valueOf(song.getId())});
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        }

        return rowsEffect > 0;
    }

    /**
     * [功能说明] 添加歌曲
     *
     * @param song 歌曲
     * @return 添加成功则返回true，否则返回false
     */
    public boolean add(Song song) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return false;
        }

        ContentValues values = new ContentValues();

        values.put(TableSongColumn.ID.getName(), song.getId());
        values.put(TableSongColumn.NAME.getName(), song.getName());
        values.put(TableSongColumn.SPELL.getName(), song.getSpell());
        values.put(TableSongColumn.SINGER.getName(), song.getSingerDescription());
        values.put(TableSongColumn.SINGER0.getName(), song.getSingerId(0));
        values.put(TableSongColumn.SINGER1.getName(), song.getSingerId(1));
        values.put(TableSongColumn.SINGER2.getName(), song.getSingerId(2));
        values.put(TableSongColumn.SINGER3.getName(), song.getSingerId(3));
        values.put(TableSongColumn.LANGUAGE.getName(), song.getLanguage());
        values.put(TableSongColumn.TYPE.getName(), song.getType());
        values.put(TableSongColumn.PLAYRATE.getName(), song.getPlayRate());
        values.put(TableSongColumn.ALBUM.getName(), song.getAlbumResource());
        values.put(TableSongColumn.SCORE.getName(), song.canScore() ? 1 : 0);
        values.put(TableSongColumn.hasRemote.getName(), song.getHasRemoteFile());
        values.put(TableSongColumn.hasLocal.getName(), song.getHasLocal());

        long rowEffect = 0;
        if (!isExist(song.getId())) {
            try {
                rowEffect = db.insert(TABLE_SONG_NAME, null, values);
            } catch (Exception e) {
                EvLog.i(e.getMessage());
                UmengAgentUtil.reportError(e);
            }
        }

        return rowEffect > 0;
    }

    /**
     * [功能说明] 保存歌曲
     *
     * @param list 歌曲列表
     * @return 保存成功则返回true，否则返回false
     */
    public boolean save(List<Song> list) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return false;
        }

        String sql = "insert OR IGNORE into " + TABLE_SONG_NAME + "("
                + TableSongColumn.ID.getName() + ","
                + TableSongColumn.NAME.getName() + ","
                + TableSongColumn.SPELL.getName() + ","
                + TableSongColumn.SINGER.getName() + ","
                + TableSongColumn.SINGER0.getName() + ","
                + TableSongColumn.SINGER1.getName() + ","
                + TableSongColumn.SINGER2.getName() + ","
                + TableSongColumn.SINGER3.getName() + ","
                + TableSongColumn.LANGUAGE.getName() + ","
                + TableSongColumn.TYPE.getName() + ","
                + TableSongColumn.PLAYRATE.getName() + ","
                + TableSongColumn.hasRemote.getName() + ","
                + TableSongColumn.hasLocal.getName() + ")"
                + " values(?,?,?,?,?,?,?,?,?,?,?,?,?)";

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
            stat.bindLong(9, 0);
            stat.bindLong(10, 0);
            stat.bindLong(11, song.getPlayRate());
            stat.bindLong(12, song.getHasRemoteFile());
            stat.bindLong(13, song.getHasLocal());
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

        String sql = "CREATE TABLE if not exists [tblRemoteSong] ("
                + "[id] INTEGER NOT NULL PRIMARY KEY,"
                + "[name] TEXT(100), "
                + "[spell] TEXT(100), "
                + "[singer] TEXT(100), "
                + "[singerId0] INTEGER, "
                + "[singerId1] INTEGER, "
                + "[singerId2] INTEGER, "
                + "[singerId3] INTEGER, "
                + "[language] INTEGER, "
                + "[type] INTEGER, "
                + "[playRate] INTEGER, "
                + "[subtitle] TEXT, "
                + "[ercVersion] TEXT(100), "
                + "[updateTime] datetime, "
                + "[album] TEXT(100), "
                + "[score] INT DEFAULT false,"
                + "[usageRate] INT DEFAULT 0,"
                + "[localResourceTick] INT NOT NULL DEFAULT 0,"
                + "[hasRemote] INT NOT NULL DEFAULT 0,"
                + "[hasLocal] INT NOT NULL DEFAULT 0"
                + ");delete * from tblRemoteSong;";

        SQLiteStatement stat = db.compileStatement(sql);

        db.beginTransaction();
        stat.execute();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * [功能说明] 更新歌曲信息
     *
     * @param list 歌曲列表
     * @return 更新成功则返回true，否则返回false
     */
    public boolean updateRemoteData(List<Song> list) {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return false;
        }

        String sql = "insert or replace into " + TABLE_REMOTE_SONG_NAME + "("
                + TableSongColumn.ID.getName() + ","
                + TableSongColumn.NAME.getName() + ","
                + TableSongColumn.SPELL.getName() + ","
                + TableSongColumn.SINGER.getName() + ","
                + TableSongColumn.SINGER0.getName() + ","
                + TableSongColumn.SINGER1.getName() + ","
                + TableSongColumn.SINGER2.getName() + ","
                + TableSongColumn.SINGER3.getName() + ","
                + TableSongColumn.LANGUAGE.getName() + ","
                + TableSongColumn.TYPE.getName() + ","
                + TableSongColumn.PLAYRATE.getName() + ","
                + TableSongColumn.ALBUM.getName() + ","
                + TableSongColumn.SCORE.getName() + ","
                + TableSongColumn.hasRemote.getName() + ","
                + TableSongColumn.hasLocal.getName() + ")"
                + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

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
            stat.bindString(12, song.getAlbumResource());
            stat.bindLong(13, song.canScore() ? 1 : 0);
            stat.bindLong(14, 1);
            stat.bindLong(15, song.getHasLocal());
            stat.execute();
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        return true;
    }

    public void endUpdateRemoteData() {
        SQLiteDatabase db = DAOHelper.getInstance().getWritableDatabase();

        if (db == null) {
            return;
        }

        String sql = null;
        db.beginTransaction();
        sql = "update tblRemoteSong set hasRemote = 1";
        SQLiteStatement stat = db.compileStatement(sql);
        stat.execute();

        sql = "insert or replace into tblRemoteSong(id,name,spell,singer,singerId0,singerId1,singerId2,singerId3,language,type,playRate,subtitle,ercVersion,updateTime,"
                + "album,score,usageRate,localResourceTick,hasLocal) select id,name,spell,singer,singerId0,singerId1,singerId2,singerId3,language,type,playRate,subtitle,ercVersion,updateTime,"
                + "album,score,usageRate,localResourceTick,hasLocal from tblSong where hasLocal=1";
        stat = db.compileStatement(sql);
        stat.execute();

        sql = "alter table tblSong rename to tblTempSong";
        stat = db.compileStatement(sql);
        stat.execute();

        sql = "alter table tblRemoteSong rename to tblSong";
        stat = db.compileStatement(sql);
        stat.execute();
        sql = "drop table tblTempSong";
        stat = db.compileStatement(sql);
        stat.execute();

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * [功能说明] 输入字母，并根据已输入的字幕获取符合条件的歌曲的后续字母列表
     *
     * @param spell 当前首拼
     * @return 字母列表
     */
    public char[] getCharacterList(String spell) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return null;
        }

        StringBuilder selectionBuilder = new StringBuilder();
        // length(spell) > length('A')
        selectionBuilder.append("length(").append(TableSongColumn.SPELL.getName()).append(")>").append(spell.length());
        selectionBuilder.append(" and ");
        selectionBuilder.append(TableSongColumn.SPELL.getName()).append(" like '%").append(spell).append("%'");
        selectionBuilder.append(" and ");
        // for example(search spell is 'A'): length(spell)>(instr(spell,'A')+length(spell))
        selectionBuilder.append("length(").append(TableSongColumn.SPELL.getName()).append(")>(instr(").append(TableSongColumn.SPELL.getName())
                .append(",'").append(spell).append("')+").append(spell.length()).append("-1)");

        String selection = selectionBuilder.toString();

        StringBuilder sqlCharacterbuilder = new StringBuilder();
        sqlCharacterbuilder.append("distinct substr(").append(TableSongColumn.SPELL.getName()).append(",instr(")
                .append(TableSongColumn.SPELL.getName()).append(",'").append(spell).append("')+")
                .append(spell.length()).append(",1)");

        StringBuilder builder = new StringBuilder();

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_NAME, new String[]{sqlCharacterbuilder.toString()},
                    selection, null, null, null, null, null);

            while (cursor.moveToNext()) {
                builder.append(cursor.getString(0));
            }
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return builder.toString().toCharArray();
    }

    /**
     * [功能说明] 根据已输入的首拼，获取符合条件的歌曲数量
     *
     * @param spell 首拼
     * @return 歌曲数量
     */
    public int getCountByFuzzySpell(String spell, boolean local) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return 0;
        }

        int count = 0;
        String selection = TableSongColumn.SPELL.getName() + " like '%" + spell + "%'";

        if (local) {
            String and = " and ";

            StringBuilder localOption = new StringBuilder();
            localOption.append(and).append(TABLE_SONG_NAME).append(".")
                    .append(TableSongColumn.ID.getName()).append(" in (select ")
                    .append(TableMedia.NAME).append(".")
                    .append(TableMedia.Column.SONG_ID.getName()).append(" from ")
                    .append(TableMedia.NAME).append(" where ").append("length(")
                    .append(TableMedia.NAME).append(".").append(TableMedia.Column.VOLUME_UUID.getName())
                    .append(") > 0 )");
            selection += localOption.toString();
        }
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_NAME, new String[]{"count(*)"}, selection, null, null, null, null);

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

    /**
     * [功能说明] 根据首拼进行模糊查找
     *
     * @param spell    首拼
     * @param pageInfo 分页信息
     * @param local    是否离线
     * @return 返回满足条件的歌曲列表
     */
    public List<Song> getListByFuzzySpell(String spell, PageInfo pageInfo, boolean local) {
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

        if (!TextUtils.isEmpty(spell)) {
            orderBy = TableSongColumn.PLAYRATE.getName() + " desc";
            StringBuilder selectionBuilder = new StringBuilder();
            selectionBuilder.append(TableSongColumn.SPELL.getName()).append(" like '%").append(spell).append("%'");
            selection = selectionBuilder.toString();
        } else {
            orderBy = TableSongColumn.PLAYRATE.getName() + " desc";
        }

        if (local) {
            String and = " and ";
            if (selection == null) {
                selection = "";
                and = "";
            }

            StringBuilder localOption = new StringBuilder();
            localOption.append(and).append(TABLE_SONG_NAME).append(".")
                    .append(TableSongColumn.ID.getName()).append(" in (select ")
                    .append(TableMedia.NAME).append(".")
                    .append(TableMedia.Column.SONG_ID.getName()).append(" from ")
                    .append(TableMedia.NAME).append(" where ").append("length(")
                    .append(TableMedia.NAME).append(".").append(TableMedia.Column.VOLUME_UUID.getName())
                    .append(") > 0 )");
            selection += localOption.toString();
        }

        Cursor cursor = null;
        List<Song> songs = new ArrayList<Song>();

        try {
            cursor = db.query(TABLE_SONG_NAME, TABLE_SONG_ALL_COL, selection, null, null, null, orderBy, limit);

            while (cursor.moveToNext()) {
                Song song = new Song(cursor.getInt(TableSongColumn.ID.ordinal()),
                        cursor.getString(TableSongColumn.NAME.ordinal()),
                        cursor.getString(TableSongColumn.SPELL.ordinal()),
                        cursor.getString(TableSongColumn.SINGER.ordinal()),
                        new int[]{cursor.getInt(TableSongColumn.SINGER0.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER1.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER2.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER3.ordinal())},
                        cursor.getInt(TableSongColumn.LANGUAGE.ordinal()),
                        cursor.getInt(TableSongColumn.TYPE.ordinal()),
                        cursor.getInt(TableSongColumn.PLAYRATE.ordinal()),
                        cursor.getString(TableSongColumn.ALBUM.ordinal()),
                        cursor.getInt(TableSongColumn.SCORE.ordinal()) == 1);
                song.setHasRemoteFile(cursor.getInt(TableSongColumn.hasRemote.ordinal()));
                song.setHasLocal(cursor.getInt(TableSongColumn.hasLocal.ordinal()));
                songs.add(song);
            }
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return songs;
    }

    /**
     * [描述变量作用]  戏曲
     */
    public static final int  SONGTYPE_DRAMA = 32;
    /**
     * [描述变量作用]  儿童
     */
    public static final int SONGTYPE_CHILD = 256;

    /**
     * [功能说明] 根据歌曲类型获取歌曲数量
     *
     * @param type 歌曲类型
     * @return 歌曲数量
     */
    public int getSongCountByType(int type) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return 0;
        }

        int count = 0;
        String selection = TableSongColumn.TYPE.getName() + "=" + type;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_NAME, new String[]{"count(*)"}, selection, null, null, null, null);

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

    public boolean removeNoRemoteSong() {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        String sql = "delete from tblSong where hasRemote = 0";
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
            return false;
        }
        return true;
    }

    /**
     * [功能说明]
     *
     * @param singerName 歌星歌曲数量
     * @param local      本地搜索使能
     * @return 数量
     */
    public int getCountBySingerName(String singerName/*, boolean local*/) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return 0;
        }

        int count = 0;
        String selection = TableSongColumn.SINGER + " ='" + singerName + "'";
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_NAME, new String[]{"count(*)"},
                    selection, null, null, null, null);

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

    /**
     * [功能说明]
     *
     * @param singerName 歌手名
     * @param pageInfo   分页
     * @param local      是否仅搜索本地歌曲
     * @return SongList
     */
    public List<Song> getSongBySingerName(String singerName, PageInfo pageInfo, boolean local) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        List<Song> songs = new ArrayList<Song>();

        String limit = "";
        if (pageInfo != null) {
            limit = pageInfo.getPageIndex() * pageInfo.getPageSize() + "," + pageInfo.getPageSize();
        }

        long startTime = System.currentTimeMillis();

        String orderBy = TableSongColumn.PLAYRATE.getName() + " desc";
        String selection = TableSongColumn.SINGER.getName() + " like '%" + singerName + "%'";
        //离线搜索选项
        if (local) {
            String and = " and ";
            StringBuilder localOption = new StringBuilder();
            localOption.append(and).append(TABLE_SONG_NAME).append(".")
                    .append(TableSongColumn.ID.getName()).append(" in (select ")
                    .append(TableMedia.NAME).append(".")
                    .append(TableMedia.Column.SONG_ID.getName()).append(" from ")
                    .append(TableMedia.NAME).append(" where ").append("length(")
                    .append(TableMedia.NAME).append(".").append(TableMedia.Column.VOLUME_UUID.getName())
                    .append(") > 0 )");
            selection += localOption.toString();
        }
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_NAME, TABLE_SONG_ALL_COL, selection, null, null, null, orderBy, limit);

            while (cursor.moveToNext()) {
                Song song = new Song(cursor.getInt(TableSongColumn.ID.ordinal()),
                        cursor.getString(TableSongColumn.NAME.ordinal()),
                        cursor.getString(TableSongColumn.SPELL.ordinal()),
                        cursor.getString(TableSongColumn.SINGER.ordinal()),
                        new int[]{cursor.getInt(TableSongColumn.SINGER0.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER1.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER2.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER3.ordinal())},
                        cursor.getInt(TableSongColumn.LANGUAGE.ordinal()),
                        cursor.getInt(TableSongColumn.TYPE.ordinal()),
                        cursor.getInt(TableSongColumn.PLAYRATE.ordinal()),
                        cursor.getString(TableSongColumn.ALBUM.ordinal()),
                        cursor.getInt(TableSongColumn.SCORE.ordinal()) == 1);
                song.setHasRemoteFile(cursor.getInt(TableSongColumn.hasRemote.ordinal()));
                song.setHasLocal(cursor.getInt(TableSongColumn.hasLocal.ordinal()));
                songs.add(song);
            }
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        long endTime = System.currentTimeMillis();

        EvLog.d("wrq", "query song list by singer name with local search cost times:" + (endTime - startTime));

        return songs;
    }

    /**
     * [功能说明] 根据歌曲类型获取歌曲列表
     *
     * @param type     歌曲类型，参见SONGTYPE_DRAMA、SONGTYPE_CHILD
     * @param pageInfo 分页数据
     * @return 歌曲列表
     */
    public List<Song> getSongByTypeCombination(int type, int typeOther, PageInfo pageInfo) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        List<Song> songs = new ArrayList<Song>();

        String limit = "";
        if (pageInfo != null) {
            limit = pageInfo.getPageIndex() * pageInfo.getPageSize() + "," + pageInfo.getPageSize();
        }

        String orderBy = TableSongColumn.PLAYRATE.getName() + " desc";
        String selection = TableSongColumn.TYPE.getName() + "=" + type;
        selection += " or " + TableSongColumn.TYPE.getName() + "=" + typeOther;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_NAME, TABLE_SONG_ALL_COL, selection, null, null, null, orderBy, limit);

            while (cursor.moveToNext()) {
                Song song = new Song(cursor.getInt(TableSongColumn.ID.ordinal()),
                        cursor.getString(TableSongColumn.NAME.ordinal()),
                        cursor.getString(TableSongColumn.SPELL.ordinal()),
                        cursor.getString(TableSongColumn.SINGER.ordinal()),
                        new int[]{cursor.getInt(TableSongColumn.SINGER0.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER1.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER2.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER3.ordinal())},
                        cursor.getInt(TableSongColumn.LANGUAGE.ordinal()),
                        cursor.getInt(TableSongColumn.TYPE.ordinal()),
                        cursor.getInt(TableSongColumn.PLAYRATE.ordinal()),
                        cursor.getString(TableSongColumn.ALBUM.ordinal()),
                        cursor.getInt(TableSongColumn.SCORE.ordinal()) == 1);
                song.setHasRemoteFile(cursor.getInt(TableSongColumn.hasRemote.ordinal()));
                song.setHasLocal(cursor.getInt(TableSongColumn.hasLocal.ordinal()));
                songs.add(song);
            }
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return songs;
    }

    /**
     * [功能说明] 根据歌曲类型获取歌曲列表
     *
     * @param type     歌曲类型，参见SONGTYPE_DRAMA、SONGTYPE_CHILD
     * @param pageInfo 分页数据
     * @return 歌曲列表
     */
    public List<Song> getSongByType(int type, PageInfo pageInfo) {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        List<Song> songs = new ArrayList<Song>();

        String limit = "";
        if (pageInfo != null) {
            limit = pageInfo.getPageIndex() * pageInfo.getPageSize() + "," + pageInfo.getPageSize();
        }

        String orderBy = TableSongColumn.PLAYRATE.getName() + " desc";
        String selection = TableSongColumn.TYPE.getName() + "=" + type;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SONG_NAME, TABLE_SONG_ALL_COL, selection, null, null, null, orderBy, limit);

            while (cursor.moveToNext()) {
                Song song = new Song(cursor.getInt(TableSongColumn.ID.ordinal()),
                        cursor.getString(TableSongColumn.NAME.ordinal()),
                        cursor.getString(TableSongColumn.SPELL.ordinal()),
                        cursor.getString(TableSongColumn.SINGER.ordinal()),
                        new int[]{cursor.getInt(TableSongColumn.SINGER0.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER1.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER2.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER3.ordinal())},
                        cursor.getInt(TableSongColumn.LANGUAGE.ordinal()),
                        cursor.getInt(TableSongColumn.TYPE.ordinal()),
                        cursor.getInt(TableSongColumn.PLAYRATE.ordinal()),
                        cursor.getString(TableSongColumn.ALBUM.ordinal()),
                        cursor.getInt(TableSongColumn.SCORE.ordinal()) == 1);
                song.setHasRemoteFile(cursor.getInt(TableSongColumn.hasRemote.ordinal()));
                song.setHasLocal(cursor.getInt(TableSongColumn.hasLocal.ordinal()));
                songs.add(song);
            }
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return songs;
    }

    /**
     * [返回ID集合]
     *
     * @return ID集合字符串
     */
    public String getSongIdCollection() {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
        Cursor cursor = null;
        StringBuilder sb = new StringBuilder();
        if (db == null) {
            return null;
        }
        try {
            cursor = db.query(TABLE_SONG_NAME, new String[]{TableSongColumn.ID.getName()}, null, null, null, null, null);
            while (cursor.moveToNext()) {
                sb.append(",");
                sb.append(cursor.getInt(0));
            }
            sb.deleteCharAt(0);
        } catch (Exception e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return sb.toString();
    }

    /**
     * [请空表]
     *
     * @return true false
     */
    public boolean clearList() {
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return false;
        }
        try {
            db.execSQL("delete from " + TABLE_SONG_NAME);
        } catch (SQLException e) {
            EvLog.i(e.getMessage());
            UmengAgentUtil.reportError(e);
            return false;
        }
        return true;
    }

    /**
     * [复合条件查询歌曲]
     *
     * @param songId     歌曲id
     * @param songWord   歌曲字数
     * @param songName   歌名
     * @param songSpell  简拼
     * @param singerId   歌手id
     * @param langTypeId 语种
     * @param songTypeId 曲种
     * @param startPos   起始位置
     * @param requestNum 请求数量
     * @return 歌曲列表
     */
    public List<Song> getListByComplex(int songId, int songWord,
                                       String songName, String songSpell, int singerId, int langTypeId,
                                       int songTypeId, int startPos, int requestNum) {
        String limit = "";
        if ((requestNum > 0) && (startPos >= 0)) {
            limit = Integer.toString(startPos) + ","
                    + Integer.toString(requestNum);
        }
        String songInput = "";
        String inputcolume = "";
        int count = 0;
        String[] strs = new String[2];

        if (requestNum <= 0 && startPos < 0) {
            return Collections.emptyList();
        }

        List<Song> songs = new ArrayList<Song>();

        if (!TextUtils.isEmpty(songName)) {
            songName = songName.toLowerCase();
            songInput = songName;
            inputcolume = "lower(" + TABLE_SONG_COL_NAME + ")";
            strs = getArgsByComplex(songId, songWord, songInput,
                    inputcolume, singerId, langTypeId, songTypeId);
            count = getCountQureySong(strs[0]);
            songs = getQureySongList(TABLE_SONG_NAME, TABLE_SONG_ALL_COL,
                    strs[0], null, null, null, strs[1], limit);
        }
        if (songs.size() < requestNum && !TextUtils.isEmpty(songSpell)) {
            requestNum = requestNum - songs.size();
            if (startPos > count) {
                startPos = startPos - count;
            } else {
                startPos = 0;
            }
            if ((requestNum > 0) && (startPos >= 0)) {
                limit = Integer.toString(startPos) + ","
                        + Integer.toString(requestNum);
            }
            songSpell = songSpell.toUpperCase();
            songInput = songSpell;
            inputcolume = "upper(" + TABLE_SONG_COL_SPELL + ")";
            strs = getArgsByComplex(songId, songWord, songInput,
                    inputcolume, singerId, langTypeId, songTypeId);
            songs.addAll(getQureySongList(TABLE_SONG_NAME, TABLE_SONG_ALL_COL,
                    strs[0], null, null, null, strs[1], limit));
        }

        if (songs.size() == 0 && TextUtils.isEmpty(songName) && TextUtils.isEmpty(songSpell)) {
            strs = getArgsByComplex(songId, songWord, songInput,
                    inputcolume, singerId, langTypeId, songTypeId);
            count = getCountQureySong(strs[0]);
            songs = getQureySongList(TABLE_SONG_NAME, TABLE_SONG_ALL_COL,
                    strs[0], null, null, null, strs[1], limit);
        }
        return songs;
    }

    private String[] getArgsByComplex(int songId, int songWord,
                                      String songInput, String inputcolume, int singerId, int langTypeId,
                                      int songTypeId) {
        String[] strs = new String[2];
        StringBuffer selectionStrb = new StringBuffer();
        //标记需要复合搜索条件
        boolean isExit = false;

        if (songId > 0) {
            selectionStrb.append(TABLE_SONG_COL_ID + "=" + songId);
            isExit = true;
        }

        if (!TextUtils.isEmpty(songInput)) {
            if (isExit) {
                selectionStrb.append(" and ");
            }
            String str = TypeConvert.strTrim(songInput);
            selectionStrb.append(inputcolume + " like '%" + songInput
                    + "%' ");
            selectionStrb.append(" or ");
            selectionStrb.append(inputcolume + " like '%" + str
                    + "%' ");
            isExit = true;
        }

        if (singerId > 0) {
            if (isExit) {
                selectionStrb.append(" and ");
            }
            //selectionStrb.append(TABLE_SONG_COL_SINGER0 + "=" + singerId);
            Singer singer = SingerManager.getInstance().getSinger(singerId);
            String singerName = "";
            if (singer != null) {
                singerName = singer.getName();
            }
            selectionStrb.append(TableSongColumn.SINGER.getName() + " like '%" + singerName + "%'");
            isExit = true;
        }
        if (langTypeId > 0) {
            if (isExit) {
                selectionStrb.append(" and ");
            }
            selectionStrb.append(TABLE_SONG_COL_LANGUAGE + "=" + langTypeId);
            isExit = true;
        }
        if (songTypeId > 0) {
            if (isExit) {
                selectionStrb.append(" and ");
            }
            selectionStrb.append(TABLE_SONG_COL_TYPE + "=" + songTypeId);
            isExit = true;
        }

        // 字数查询附加
        if (songWord > 0) {
            if (isExit) {
                selectionStrb.append(" and ");
            }
            selectionStrb.append("length(" + TABLE_SONG_COL_NAME + ")="
                    + songWord);
        }

        String orderBy = TABLE_SONG_COL_PLAYRATE + " desc";
        orderBy = TABLE_SONG_COL_PLAYRATE + " desc";
        strs[0] = selectionStrb.toString();
        strs[1] = orderBy;
        return strs;
    }


    private List<Song> getQureySongList(String table, String[] columns, String selection,
                                        String[] selectionArgs, String groupBy, String having,
                                        String orderBy, String limit) {
        Cursor cursor = null;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return Collections.emptyList();
        }

        List<Song> songs = new ArrayList<Song>();

        try {
            cursor = db.query(table, columns,
                    selection, selectionArgs, groupBy, having, orderBy, limit);

            while (cursor.moveToNext()) {
                Song song = new Song(cursor.getInt(TableSongColumn.ID.ordinal()),
                        cursor.getString(TableSongColumn.NAME.ordinal()),
                        cursor.getString(TableSongColumn.SPELL.ordinal()),
                        cursor.getString(TableSongColumn.SINGER.ordinal()),
                        new int[]{cursor.getInt(TableSongColumn.SINGER0.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER1.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER2.ordinal()),
                                cursor.getInt(TableSongColumn.SINGER3.ordinal())},
                        cursor.getInt(TableSongColumn.LANGUAGE.ordinal()),
                        cursor.getInt(TableSongColumn.TYPE.ordinal()),
                        cursor.getInt(TableSongColumn.PLAYRATE.ordinal()),
                        cursor.getString(TableSongColumn.ALBUM.ordinal()),
                        cursor.getInt(TableSongColumn.SCORE.ordinal()) == 1);
                song.setHasRemoteFile(cursor.getInt(TableSongColumn.hasRemote.ordinal()));
                song.setHasLocal(cursor.getInt(TableSongColumn.hasLocal.ordinal()));
                songs.add(song);
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
        return songs;
    }

    private int getCountQureySong(String selection) {
        int count = 0;
        Cursor cursor = null;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();

        if (db == null) {
            return 0;
        }

        try {
            cursor = db.query(TABLE_SONG_NAME, new String[]{"count(*)"},
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

    public int calculateQualifyNum(String collection) {
        Cursor cursor = null;
        int count = -1;
        SQLiteDatabase db = DAOHelper.getInstance().getReadableDatabase();
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
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return count;
    }

    /**
     * [智能清理时获取待删列表-10首]
     *
     * @return 清理列表
     */
    public List<Integer> getListIntelligent() {
        return null;
    }

}
