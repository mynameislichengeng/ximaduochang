package com.evideo.kmbox;


/**
 * @brief: 系统配置
 */
public final class SystemConfigManager {
    public static final int VIDEO_DEFAULT_DURATION = 5 * 60;
    public static final float MAX_EXPAND_SCALE = 1.05f;

    public static int RING_BUFFER_MAX_SIZE = 5 * 1024 * 1024;
    public static int CAN_PLAY_BUFFER_FREE_SIZE = 1 * 1024 * 1024;
    public static int FREE_SING_MAX_TIME = 60;//免费试唱时长s,建议改成30秒免费
    public static boolean SHOW_SMALL_MV = true;
    public static boolean SUPPORT_FREE_SING = true;
    public static int WX_QR_WDN_SHOW_MAX_TIME = 20;//微信二维码小窗口显示最大时长s
    /**
     * [网速衰减系数]
     */
    public static float NET_SPEED_FADER = 0.9f;

    public static final String DC_TYPE_NORMAL = "normal";
    public static final String DC_TYPE_TEST = "test";

    /**
     * 第一次开机启动时，系统音量值
     */
    public static final int MUSIC_VOLUME_VALUE = 8;

    public static final String KMBOX_ACTIVITY = "com.evideo.kmbox.activity.MainActivity";
    public static final String SYNC_DB_ACTIVITY = "com.evideo.kmbox.activity.GuideActivity";
    public static final String PAY_ACTIVITY = "com.evideo.kmbox.model.pay.PayActivity";
    public static final String ALI_PAY_SDK_ACTIVITY_STRING = "com.yunos.tv.apppaysdk.activity.PayResultActivity";

    /**
     * httpd url
     */
    public static final String HTTPD_HEADER = "http://127.0.0.1:8800";

    /**
     * 本地资源访问http头部
     */
    public static final String LOCAL_HTTP_HEADER = HTTPD_HEADER + "/local";

    /**
     * 在线资源访问http头部
     */
    public static final String REMOTE_HTTP_HEADER = HTTPD_HEADER + "/remote";

    public static final int BROADCAST_SONG_NUM = 100;
    public static int SWITCH_SONG_EFFECT_TIME = 2000;
    public static int EXIT_EFFECT_TIME = 2000;

    public static int SUNG_LIST_MAX_SIZE = 50;

    private boolean mIsDebugVersion = true;

    public boolean isDebugVersion() {
        return mIsDebugVersion;
    }

    public void setDebugVersion(boolean flag) {
        mIsDebugVersion = false;
    }

    private static SystemConfigManager instance = null;

    public static SystemConfigManager getInstance() {
        if (instance == null) {
            synchronized (SystemConfigManager.class) {
                SystemConfigManager temp = instance;
                if (temp == null) {
                    temp = new SystemConfigManager();
                    instance = temp;
                }
            }
        }
        return instance;
    }
}
