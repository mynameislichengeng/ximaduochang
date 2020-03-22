package com.evideo.kmbox.model.sharedpreferences;


/**
 * <p>统一定义KmSharedPreferences的key</p>
 * <p>key的常量名命名规则务必以KEY_开头，以下划线分隔，同时确保key的常量值是唯一的,命名尽量做到能够从字面上读懂该key对应保存的是什么值。</p>
 */
public final class KeyName {

    /**********************************************设置**************************************************************/

    /**
     * [全局原唱]
     */
    public static final String KEY_SETTING_GLOBAL_ORIGINAL_SING = "key_setting_global_original";

    /**********************************************会员信息**************************************************************/

    /**
     * [会员列表信息]
     */
    public static final String KEY_MEMBER_INFO_LIST = "key_member_info_list";

    /**********************************************友盟**************************************************************/

    /**
     * [友盟appkey] 
     * @deprecated
     * 已过时，请勿重复定义
     */
//    public static final String KEY_UMENG_APPKEY = "key_umeng_appkey";

    /**********************************************存储事件**************************************************************/

    /**
     * [存储事件开关]
     */
    public static final String KEY_STORAGE_EVENT_SWITCH = "key_storage_event_switch";

    /**********************************************数据中心**************************************************************/

    /**
     * [数据中心uri类型]
     */
    public static final String KEY_DATA_CENTER_URI_TYPE = "key_data_center_uri_type";

    /**
     * [歌单数据时间戳]
     */
    public static final String KEY_SONG_MENU_DATA_TIMESTAMP = "key_song_menu_data_timestamp";

    /**
     * [排行数据时间戳]
     */
    public static final String KEY_SONG_TOP_DATA_TIMESTAMP = "key_song_top_data_timestamp";

    /**********************************************收藏列表提示**************************************************************/
    /**
     * [待删歌曲版本]
     */
    public static final String KEY_VERSION_SONGS_TO_BE_DELETED = "key_version_songs_to_be_deleted";

    /**********************************************缺歌反馈**************************************************************/
    /**
     * [缺歌反馈url]
     */
    public static final String KEY_NOSONG_LACK_URL = "key_nosong_lack_url";

    /**
     * [全曲库版本号]
     */
    public static final String KEY_WHOLE_DB_VERSION = "key_whole_db_version";

    /**
     * [全曲库智能曲库开关]
     */
    public static final String KEY_DB_SMART_SWTICH = "key_db_smart_switch";

    /**
     * [启动图片]
     */
    public static final String KEY_BOOT_PICTURE_VERSION = "key_boot_picture_version";
    public static final String KEY_BOOT_PICTURE_FILENAME = "key_boot_picture_filename";
    public static final String KEY_BOOT_PICTURE_DURATION = "key_boot_picture_duration";

    /**
     * [HOME动态换肤]
     */
    public static final String KEY_HOME_PICTURE_VERSION = "key_home_picture_version";
    public static final String KEY_HOME_PICTURE_ = "key_home_picture_";

    /**
     * [歌星图片头URL地址]
     */
    public static final String KEY_SINGER_ICON_URL_HEAD = "key_singer_icon_url_head";
    /****************************************用户登录*****************************************/

    public static final String KEY_MONKEY_TEST = "key_monkey_test";

    public static final String KEY_LOCAL_BROADCAST_SONG_ID = "key_local_broadcast_song_id";

    public static final String KEY_USE_VLC_DECODE = "key_use_vlc_decode";

    public static final String KEY_FIRST_USE_APP = "key_first_use_app";
}
