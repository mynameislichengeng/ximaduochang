package com.evideo.kmbox.model.umeng;


/**
 * @brief : [友盟统计事件常量]
 * <p>以ID_开头的表示event_id, 以K_开头的表示参数的key， 以V_开头的表示参数的value</p>
 * <p>event_id与友盟后台定义保持一致，请勿随意修改</p>
 */
public interface EventConst {
    /**
     * [缓冲开始事件]
     */
    public static final String ID_BUFFER_BEGIN = "buffer_begin";
    /**
     * [缓冲结束事件]
     */
    public static final String ID_BUFFER_END = "buffer_end";
    /**
     * [缓冲事件(结束和开始)中的平均下载速度]
     */
    public static final String K_AVERAGE_SPEED = "average_speed";
    /**
     * [缓冲事件(结束和开始)中的平均最高速度]
     */
    public static final String K_MAX_SPEED = "max_speed";
    /**
     * [缓冲事件(结束和开始)中的平均最低速度]
     */
    public static final String K_MIN_SPEED = "min_speed";
    /**
     * [缓冲事件(结束和开始)中的平均最高波动速度]
     */
    public static final String K_MAX_FLUCTUATION_SPEED = "max_fluctuation_speed";
    /**
     * [缓冲事件(结束和开始)中的歌曲id]
     */
    public static final String K_SONG_ID = "sont_id";

    /**
     * [播控按钮原伴唱切换]
     */
    public static final String ID_CLICK_PLAY_CTRL_SWITCH_TRACK = "click_play_ctrl_switch_track";

    public static final String K_SWITCH_TRACK = "switch_track";

    public static final String V_SWITCH_TRACK_ACCOMPANIMENT = "accompaniment";

    public static final String V_SWITCH_TRACK_ORIGIANL = "original";

    /**
     * [播控按钮切歌]
     */
    public static final String ID_CLICK_PLAY_CTRL_CUT_SONG = "click_play_ctrl_cut_song";

    /**
     * [向右按两次切歌]
     */
    public static final String ID_CLICK_RIGHT_TWICE_CUT_SONG = "click_right_twice_cut_song";

    /**
     * [遥控器F1键按两次切歌]
     */
    public static final String ID_CLICK_REMOTE_F1_CUT_SONG = "click_remote_f1_cut_song";

    /**
     * [播控按钮重唱]
     */
    public static final String ID_CLICK_PLAY_CTRL_REPLAY = "click_play_ctrl_replay";

    /**
     * [播控按钮已点]
     */
    public static final String ID_CLICK_PLAY_CTRL_PLAY_LIST = "click_play_ctrl_play_list";

    /**
     * [播控按钮播放暂停]
     */
    public static final String ID_CLICK_PLAY_CTRL_PLAY_PAUSE = "click_play_ctrl_play_pause";

    /**
     * [音乐音量调节]
     */
    public static final String ID_CLICK_VOLUME_MUSIC = "click_volume_music";

    public static final String K_VOLUME_ACTION = "volume_action";

    public static final String V_VOLUME_UP = "up";

    public static final String V_VOLUME_DOWN = "down";

    /**
     * [音量设置参数]
     */
    public static final String ID_VOLUME_PARAMS = "volume_params";

    public static final String K_VOLUME_MUSIC = "volume_music";

    /**
     * [遥控器播放暂停]
     */
    public static final String ID_CLICK_REMOTE_PLAY_PAUSE = "click_remote_play_pause";

    public static final String K_PLAY_PAUSE_ACTION = "play_pause_action";

    public static final String V_PLAY_PAUSE_ACTION_PLAY = "play";

    public static final String V_PLAY_PAUSE_ACTION_PAUSE = "pause";

    /**
     * [歌曲点播]
     */
    public static final String ID_CLICK_SELECT_SONG = "click_select_song";

    /**
     * [已点列表切歌]
     */
    public static final String ID_CLICK_SELECTED_LIST_VIEW_CUT_SONG = "click_selected_list_view_cut_song";

    /**
     * [已点列表删歌]
     */
    public static final String ID_CLICK_SELECTED_LIST_VIEW_DELETE_SONG = "click_selected_list_view_delete_song";

    /**
     * [已点列表顶歌]
     */
    public static final String ID_CLICK_SELECTED_LIST_VIEW_TOP_SONG = "click_selected_list_view_top_song";

    /**
     * [已唱列表加歌]
     */
    public static final String ID_CLICK_SUNG_LIST_VIEW_ADD_SONG = "click_sung_list_view_add_song";

    /**
     * [已唱歌曲顶歌]
     */
    public static final String ID_CLICK_SUNG_LIST_VIEW_TOP_SONG = "click_sung_list_view_top_song";

    /**
     * [已唱列表删歌]
     */
    public static final String ID_CLICK_SUNG_LIST_VIEW_DEL_SONG = "click_sung_list_view_del_song";

    /**
     * [VGA点歌]
     */
    public static final String ID_CLICK_ORDER_SONG_VIEW_BACKSPACE = "click_order_song_view_backspace";
    public static final String ID_CLICK_ORDER_SONG_VIEW_SONG = "click_order_song_view_song";
    public static final String ID_CLICK_ORDER_SONG_VIEW_TOP_SONG = "click_order_song_view_top_song";

    /**
     * [点歌界面-收藏或取消收藏歌曲]
     */
    public static final String ID_CLICK_ORDER_SONG_VIEW_FAVORITE = "click_order_song_view_favorite";

    public static final String K_FAVORITE_ACTION = "favorite_action";

    public static final String V_FAVORITE = "favorite";

    public static final String V_CANCEL = "cancel";

    /**
     * [收藏列表加歌]
     */
    public static final String ID_CLICK_FAVORITE_LIST_VIEW_ADD_SONG = "click_favorite_list_view_add_song";

    /**
     * [收藏列表顶歌]
     */
    public static final String ID_CLICK_FAVORITE_LIST_VIEW_TOP_SONG = "click_favorite_list_view_top_song";

    /**
     * [收藏列表取消收藏]
     */
    public static final String ID_CLICK_FAVORITE_LIST_VIEW_CANCEL_FAVORITE = "click_favorite_list_view_cancel_favorite";

    /**
     * [已点列表-收藏或取消收藏歌曲]
     */
    public static final String ID_CLICK_SELECTED_LIST_VIEW_FAVORITE = "click_selected_list_view_favorite";

    /**
     * [歌单二级页面点击事件]
     */
    public static final String ID_CLICK_SONG_MENU_SUB_PAGE = "click_song_menu_sub_page";

    /**
     * [歌单详情页面点击事件]
     */
    public static final String ID_CLICK_SONG_MENU_DETAILS_ORDER_SONG = "click_song_menu_details_order_song";

    public static final String K_SONG_MENU_NAME = "song_menu_name";

    /**
     * [歌单详情列表-顶歌]
     */
    public static final String ID_CLICK_SONG_MENU_DETAILS_TOP_SONG = "click_song_menu_details_top_song";

    /**
     * [歌单详情列表-收藏或取消收藏歌曲]
     */
    public static final String ID_CLICK_SONG_MENU_DETAILS_FAVORITE = "click_song_menu_details_favorite";

    /**
     * [歌星搜索]
     */
    public static final String ID_CLICK_SINGER_SEARCH = "click_singer_search";

    /**
     * [歌名搜索]
     */
    public static final String ID_CLICK_SONG_SEARCH = "click_song_search";

    /**
     * [歌单]
     */
    public static final String ID_CLICK_SONGMENU = "click_songmenu";

    /**
     * [排行]
     */
    public static final String ID_CLICK_TOP = "click_top";

    /**
     * [最新歌曲]
     */
    public static final String ID_CLICK_NEWSONG = "click_newsong";

    /**
     * [免费试听]
     */
    public static final String ID_CLICK_FREESONG = "click_freesong";

    /**
     * [儿童歌曲]
     */
    public static final String ID_CLICK_CHILD = "click_child";

    /**
     * [梨园戏曲]
     */
    public static final String ID_CLICK_DRAMA = "click_drama";

    /**
     * [活动]
     */
    public static final String ID_CLICK_HUO_DONG = "click_huo_dong";

    /**
     * [手机点歌]
     */
    public static final String ID_CLICK_PHONE_ORDER_SONG = "click_phone_order_song";

    /**
     * [个人中心]
     */
    public static final String ID_CLICK_USER_CENTER = "click_user_center";

    /**
     * [客服]
     */
    public static final String ID_CLICK_CUSTOMER_SERVICE = "click_customer_service";

    public static final String ID_THIRD_APP_STAY_DURATION = "third_app_stay_duration";

    public static final String K_SWITCH = "switch";
    public static final String V_OPEN = "open";
    public static final String V_CLOSE = "close";

    /**
     * [主界面-设置]
     */
    public static final String ID_CLICK_SYS_SETTING = "click_sys_setting";

    /**
     * [键盘 - 切换]
     */
    public static final String ID_CLICK_KEYBOARD_SWITCH = "click_keyboard_switch";

    /**
     * [键盘 - 清空]
     */
    public static final String ID_CLICK_KEYBOARD_CLEAR = "click_keyboard_clear";

    /**
     * [歌星 - 点歌]
     */
    public static final String ID_CLICK_ALL_SINGER_LIST = "click_all_singer_list";

    /**
     * [歌星 - 类别]
     */
    public static final String ID_CLICK_ALL_SINGER_TYPE_LIST = "click_all_singer_type_list";

    /**
     * [点击订购按钮]
     */
    public static final String ID_CLICK_CHARGE_ORDER = "click_charge_order";
}
