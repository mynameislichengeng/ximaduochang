package com.evideo.kmbox.dao;

public interface DatabaseConstants {
    public static final String DATABASE_NAME = "local_kmbox.db";
    public static final int DATABASE_VERSION = 18; //13至14,recordList中加入合成字段与是否上传多唱字段
                                                   //14至15,favorite收藏添加标示是否同步服务器
                                                   //15至16,media表加入duration字段
                                                   //16到17,songTopDetail表加入orderRate字段
                                                   //17到18,增加freesonglist
    
    // songMenu表
    /** [歌单表名] */
    public static final String TABLE_SONG_MENU_TABLE_NAME = "tblSongMenu";
    /** [歌单-列-id(自增长主键)] */
    public static final String TABLE_SONG_MENU_COL_PRIMARY_ID = "_id";
    /** [歌单-列-歌单id] */
    public static final String TABLE_SONG_MENU_COL_SONG_MENU_ID = "songMenuId";
    /** [歌单-列-名称] */
    public static final String TABLE_SONG_MENU_COL_NAME = "name";
    /** [歌单-列-描述] */
    public static final String TABLE_SONG_MENU_COL_DESCRIPTION = "description";
    /** [歌单-列-图片url] */
    public static final String TABLE_SONG_MENU_COL_IMAGE_URL = "imageUrl";
    /** [歌单-列-时间戳（歌曲列表保存时间）] */
    public static final String TABLE_SONG_MENU_COL_TIMESTAMP = "timestamp";
    /** [歌单-列-歌曲总数] */
    public static final String TABLE_SONG_MENU_COL_TOTAL_NUM = "totalNum";
    /** [歌单-列-图片URL-大] */
    public static final String TABLE_SONG_MENU_COL_IMAGE_URL_BIG = "imageUrlBig";
    
    /** [歌单，列数组] */
    public static final String[] TABLE_SONG_MENU_COLUMNS = {TABLE_SONG_MENU_COL_PRIMARY_ID, 
        TABLE_SONG_MENU_COL_SONG_MENU_ID, TABLE_SONG_MENU_COL_NAME, 
        TABLE_SONG_MENU_COL_DESCRIPTION, TABLE_SONG_MENU_COL_IMAGE_URL, 
        TABLE_SONG_MENU_COL_TIMESTAMP, TABLE_SONG_MENU_COL_TOTAL_NUM,
        TABLE_SONG_MENU_COL_IMAGE_URL_BIG};
    
    /** [歌单表创建语句] */
    public static final String DDL_CREATE_TABLE_SONG_MENU = "create table if not exists "
            + TABLE_SONG_MENU_TABLE_NAME + " ("
            + TABLE_SONG_MENU_COL_PRIMARY_ID
            + " integer primary key autoincrement, "
            + TABLE_SONG_MENU_COL_SONG_MENU_ID + " integer, "
            + TABLE_SONG_MENU_COL_NAME + " text, "
            + TABLE_SONG_MENU_COL_DESCRIPTION + " text, "
            + TABLE_SONG_MENU_COL_IMAGE_URL + " text, "
            + TABLE_SONG_MENU_COL_TIMESTAMP + " text, "
            + TABLE_SONG_MENU_COL_TOTAL_NUM + " integer" + ");";
    
    /** [修改歌单表-添加列-大图url] */
    public static final String DDL_ALTER_TABLE_SONG_MENU_ADD_IMAGEURLBIG = "alter table "
            + TABLE_SONG_MENU_TABLE_NAME + " add " + TABLE_SONG_MENU_COL_IMAGE_URL_BIG + " text";
    
    public static final String DDL_ALTER_TABLE_STORAGEVOLEM_ADD_RESOURCESIZE = "ALTER TABLE \"tblStorageVolume\"  "
            + "ADD COLUMN \"resourcesize\" DOUBLE DEFAULT(-1);";
    
    /** [修改录音表-添加列-上传状态] */
    public static final String DDL_ALTER_TABLE_RECORD_ADD_RECORD_FILEID = "ALTER TABLE \"tblRecordList\"  "
            + "ADD COLUMN \"recordfileid\" INTEGER DEFAULT(-1);";
      
    public static final String DDL_ALTER_TABLE_RECORD_ADD_EUR_FILEID = "ALTER TABLE \"tblRecordList\"  "
            + "ADD COLUMN \"eurfileid\" INTEGER DEFAULT(-1);";
    
    public static final String TABLE_MEDIA_TABLE_NAME = "tblMedia";

    /** [Media表加入localResource] */
    public static final String DDL_ALTER_TABLE_MEDIA_ADD_RESOURCE_PATH = "ALTER TABLE "+TABLE_MEDIA_TABLE_NAME
            + " ADD COLUMN \"localResource\" TEXT;";
    
    public static final String DDL_ALTER_TABLE_MEDIA_ADD_RESOURCE_SIZE = "ALTER TABLE "+TABLE_MEDIA_TABLE_NAME
            + " ADD COLUMN \"resourceSize\" LONG DEFAULT(-1);";
    
    public static final String DDL_ALTER_TABLE_MEDIA_ADD_DURATION = "ALTER TABLE "+ TABLE_MEDIA_TABLE_NAME
            + " ADD COLUMN \"duration\" INT DEFAULT(0);";
    
    // songMenuDetail表
    /** [歌单详情表名] */
    public static final String TABLE_SONG_MENU_DETAIL_TABLE_NAME = "tblSongMenuDetail";
    /** [歌单详情-列-id（自增长主键）] */
    public static final String TABLE_SONG_MENU_DETAIL_COL_PRIMARY_ID = "_id";
    /** [歌单详情-列-歌单id] */
    public static final String TABLE_SONG_MENU_DETAIL_COL_SONG_MENU_ID = "songMenuId";
    /** [歌单详情-列-歌曲id] */
    public static final String TABLE_SONG_MENU_DETAIL_COL_SONG_ID = "songId";
    
    /** [歌单详情，列数组] */
    public static final String[] TABLE_SONG_MENU_DETAIL_COLUMNS = {TABLE_SONG_MENU_DETAIL_COL_PRIMARY_ID,
        TABLE_SONG_MENU_DETAIL_COL_SONG_MENU_ID, TABLE_SONG_MENU_DETAIL_COL_SONG_ID};
    
    /** [歌单详情表创建语句] */
    public static final String DDL_CREATE_TABLE_SONG_MENU_DETAIL = "create table if not exists "
            + TABLE_SONG_MENU_DETAIL_TABLE_NAME + " ("
            + TABLE_SONG_MENU_DETAIL_COL_PRIMARY_ID
            + " integer primary key autoincrement, "
            + TABLE_SONG_MENU_DETAIL_COL_SONG_MENU_ID + " integer, "
            + TABLE_SONG_MENU_DETAIL_COL_SONG_ID + " integer" + ");";
    
    /** [触发器创建语句--在删除songmenu后触发] */
    public static final String DDL_TRG_AFTER_DELETE_SONG_MENU = 
            "create trigger if not exists   trgAfterDeleteSongMenu after delete on " + TABLE_SONG_MENU_TABLE_NAME
            + " begin delete from " + TABLE_SONG_MENU_DETAIL_TABLE_NAME + " where "
            + TABLE_SONG_MENU_DETAIL_COL_SONG_MENU_ID + " = old." + TABLE_SONG_MENU_COL_SONG_MENU_ID + ";"
            + " end";
    
    public static final String TABLE_NAME_SUNG_LIST = "tblSungList";
    public static final String TABLE_NAME_RECORD_LIST = "tblRecordList";
    
    public static final String DDL_CREATE_SUNG_TABLE = "create table if not exists "+TABLE_NAME_SUNG_LIST+"(id INTEGER PRIMARY KEY,songid int,canscore int," +
            "score int,customerId NVARCHAR,sharecode NVARCHAR);";
    
    public static final String DDL_CREATE_RECORD_TABLE = "create table if not exists " + TABLE_NAME_RECORD_LIST + "(id INTEGER PRIMARY KEY," +
            "songid int,customerId NVARCHAR,score int,songType int,sharecode NVARCHAR," +
            "recordLen int,recordPath NVARCHAR,eurPath NVARCHAR);";
    
    /** [Media表加入localResource] */
    public static final String DDL_ALTER_TABLE_RECORD_ADD_MIX_PROGRESS = "ALTER TABLE "+TABLE_NAME_RECORD_LIST
            + " ADD COLUMN \"mixProgress\" INT DEFAULT(0);";
    
    public static final String DDL_ALTER_TABLE_RECORD_ADD_UPLOAD_DUOCHANG = "ALTER TABLE "+TABLE_NAME_RECORD_LIST
            + " ADD COLUMN \"uploadDuochang\" INT DEFAULT(0);";
    
    
    //收藏列表
    /** [表名：收藏列表] */
    public static final String TABLE_FAVORITE_LIST_NAME = "tblFavoriteList";
    public static final String DDL_ALTER_TABLE_FAVORITE_LIST_UPLOAD_DUOCHANG = "ALTER TABLE "+ TABLE_FAVORITE_LIST_NAME
            + " ADD COLUMN \"upload\" INT DEFAULT(0);";
    
    // songTop表
    /** [歌单表名] */
    public static final String TABLE_SONG_TOP_TABLE_NAME = "tblSongTop";
    /** [歌单-列-id(自增长主键)] */
    public static final String TABLE_SONG_TOP_COL_PRIMARY_ID = "_id";
    /** [歌单-列-歌单id] */
    public static final String TABLE_SONG_TOP_COL_SONG_TOP_ID = "songTopId";
    /** [歌单-列-名称] */
    public static final String TABLE_SONG_TOP_COL_NAME = "name";
    /** [歌单-列-图片url] */
    public static final String TABLE_SONG_TOP_COL_IMAGE_URL = "imageUrl";
    /** [歌单-列-时间戳（歌曲列表保存时间）] */
    public static final String TABLE_SONG_TOP_COL_TIMESTAMP = "timestamp";
    /** [歌单-列-歌曲总数] */
    public static final String TABLE_SONG_TOP_COL_TOTAL_NUM = "totalNum";
    /** [类型编码] */
    public static final String TABLE_SONG_TOP_COL_TYPE_CODE = "songTopTypeCode";
    /** [子标题] */
    public static final String TABLE_SONG_TOP_COL_SUBTITLE = "subTitle";
    /** [歌曲列表] */
    public static final String TABLE_SONG_TOP_COL_SONGLIST = "songList";
    /** [歌星列表] */
    public static final String TABLE_SONG_TOP_COL_SINGERLIST = "singerList";
    
   
    /** [歌单，列数组] */
    public static final String[] TABLE_SONG_TOP_COLUMNS = {TABLE_SONG_TOP_COL_PRIMARY_ID, 
        TABLE_SONG_TOP_COL_SONG_TOP_ID, TABLE_SONG_TOP_COL_NAME, 
        TABLE_SONG_TOP_COL_IMAGE_URL, TABLE_SONG_TOP_COL_TIMESTAMP, 
        TABLE_SONG_TOP_COL_TOTAL_NUM, TABLE_SONG_TOP_COL_TYPE_CODE,
        TABLE_SONG_TOP_COL_SUBTITLE, TABLE_SONG_TOP_COL_SONGLIST, TABLE_SONG_TOP_COL_SINGERLIST
        };
    
    /** [歌单表创建语句] */
    public static final String DDL_CREATE_TABLE_SONG_TOP = "create table if not exists "
            + TABLE_SONG_TOP_TABLE_NAME + " ("
            + TABLE_SONG_TOP_COL_PRIMARY_ID
            + " integer primary key autoincrement, "
            + TABLE_SONG_TOP_COL_SONG_TOP_ID + " integer, "
            + TABLE_SONG_TOP_COL_NAME + " text, "
            + TABLE_SONG_TOP_COL_IMAGE_URL + " text, "
            + TABLE_SONG_TOP_COL_TIMESTAMP + " text, "
            + TABLE_SONG_TOP_COL_TOTAL_NUM + " integer, " 
            + TABLE_SONG_TOP_COL_TYPE_CODE + " integer, "
            + TABLE_SONG_TOP_COL_SUBTITLE + " text, "
            + TABLE_SONG_TOP_COL_SONGLIST + " text, "
            + TABLE_SONG_TOP_COL_SINGERLIST + " text" + ");";
    // songTopDetail表
    /** [歌单详情表名] */
    public static final String TABLE_SONG_TOP_DETAIL_TABLE_NAME = "tblSongTopDetail";
    /** [歌单详情-列-id（自增长主键）] */
    public static final String TABLE_SONG_TOP_DETAIL_COL_PRIMARY_ID = "_id";
    /** [歌单详情-列-歌单id] */
    public static final String TABLE_SONG_TOP_DETAIL_COL_SONG_TOP_ID = "songTopId";
    /** [歌单详情-列-歌曲id] */
    public static final String TABLE_SONG_TOP_DETAIL_COL_SONG_ID = "songId";
    /** [歌单详情-列-歌名] */
    public static final String TABLE_SONG_TOP_DETAIL_COL_SONG_NAME = "songName";
    /** [歌单详情-列-歌星] */
    public static final String TABLE_SONG_TOP_DETAIL_COL_SINGER_NAME = "singerName";
    /** [可否评分] */
    public static final String TABLE_SONG_TOP_DETAIL_COL_SCORE = "score";
    /** [点播率] */
    public static final String TABLE_SONG_TOP_DETAIL_COL_ORDER_RATE = "orderRate";
    
    /** [歌单详情，列数组] */
    public static final String[] TABLE_SONG_TOP_DETAIL_COLUMNS = {TABLE_SONG_TOP_DETAIL_COL_PRIMARY_ID,
        TABLE_SONG_TOP_DETAIL_COL_SONG_TOP_ID, TABLE_SONG_TOP_DETAIL_COL_SONG_ID,
        TABLE_SONG_TOP_DETAIL_COL_SONG_NAME, TABLE_SONG_TOP_DETAIL_COL_SINGER_NAME,
        TABLE_SONG_TOP_DETAIL_COL_SCORE,TABLE_SONG_TOP_DETAIL_COL_ORDER_RATE};
    
    /** [歌单详情表创建语句] */
    public static final String DDL_CREATE_TABLE_SONG_TOP_DETAIL = "create table if not exists "
            + TABLE_SONG_TOP_DETAIL_TABLE_NAME + " ("
            + TABLE_SONG_TOP_DETAIL_COL_PRIMARY_ID
            + " integer primary key autoincrement, "
            + TABLE_SONG_TOP_DETAIL_COL_SONG_TOP_ID + " integer, "
            + TABLE_SONG_TOP_DETAIL_COL_SONG_ID + " integer,"
            + TABLE_SONG_TOP_DETAIL_COL_SONG_NAME + " text, "
            + TABLE_SONG_TOP_DETAIL_COL_SINGER_NAME + " text, " 
            + TABLE_SONG_TOP_DETAIL_COL_SCORE + " integer" + ");";
    
    public static final String DDL_ALTER_TABLE_SONG_TOP_DETAIL_ADD_ORDERRATE = "ALTER TABLE "+ TABLE_SONG_TOP_DETAIL_TABLE_NAME
            + " ADD COLUMN \"orderRate\" INT DEFAULT(0);";
    
    /** [触发器创建语句--在删除songmenu后触发] */
    public static final String DDL_TRG_AFTER_DELETE_SONG_TOP = 
            "create trigger trgAfterDeleteSongTop after delete on " + TABLE_SONG_TOP_TABLE_NAME
            + " begin delete from " + TABLE_SONG_TOP_DETAIL_TABLE_NAME + " where "
            + TABLE_SONG_TOP_DETAIL_COL_SONG_TOP_ID + " = old." + TABLE_SONG_TOP_COL_SONG_TOP_ID + ";"
            + " end";
    //搜索历史表创建
    /** [表名：搜索历史] */
    public static final String TABLE_SEARCH_HISTORY_NAME = "tblSearchHistory";
    /** [主键] */
    public static final String TABLE_SEARCH_HISTORY_COL_PRIMARY_ID = "_id";
    /** [列：id] */
    public static final String TABLE_SEARCH_HISTORY_COL_ID = "id";
    /** [类型] */
    public static final String TABLE_SEARCH_HISTORY_COL_TYPE = "type";
    /** [创建搜索历史表动作] */
    public static final String DDL_CREATE_TABLE_SEARCH_HISTORY = "create table if not exists " 
            + TABLE_SEARCH_HISTORY_NAME + " ("
            + TABLE_SEARCH_HISTORY_COL_PRIMARY_ID
            + " integer primary key autoincrement, "
            + TABLE_SEARCH_HISTORY_COL_ID + " integer, "
            + TABLE_SEARCH_HISTORY_COL_TYPE + " integer" + ");";
    
    public static final String DDL_CREATE_TABLE_FREESONG = "create table if not exists " 
            + FreeSongDAO.TABLE_FREE_LIST + " ("
            + TABLE_SEARCH_HISTORY_COL_PRIMARY_ID
            + " integer primary key autoincrement, "
            + FreeSongDAO.TABLE_FREE_LIST_COL_SONGID + " integer" + ");";
    
}
