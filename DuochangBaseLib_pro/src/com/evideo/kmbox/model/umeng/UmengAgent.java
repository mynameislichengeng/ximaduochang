package com.evideo.kmbox.model.umeng;

import java.util.HashMap;

import android.content.Context;
import android.text.TextUtils;

//import com.evideo.kmbox.model.device.DeviceConfigManager;
//import com.evideo.kmbox.util.DeviceInfoUtils;
import com.evideo.kmbox.util.EvLog;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * [友盟api代理]
 */
public final class UmengAgent {
    
    /** [测试appkey] */
    public static final String APPKEY_TEST = "APPKEY_TEST";
    /** [正式appkey] */
    public static final String APPKEY_FORMAL = "APPKEY_FORMAL";
    
  
    /** [初始appkey] */
    public static final String INIT_APPKEY = APPKEY_TEST;
    
    private static final int PAGE_STATE_START = 1;
    
    private static final int PAGE_STATE_END = 2;
    
    private String mLastPageName = "";
    
    private int mPageState = PAGE_STATE_END;
    
    private UmengAgent() {
    }
    
    private static UmengAgent sInstance = new UmengAgent();
    
    /**
     * [获取UmengAgent实例]
     * @return UmengAgent实例
     */
    public static UmengAgent getInstance() {
        return sInstance;
    }
    
    private static boolean sIsLogcatOpen = false;
    
    /**
     * [获取deviceInfo字符串]
     * @param context context
     * @return deviceInfo字符串
     */
    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String deviceId = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(deviceId)) {
                deviceId = mac;
            }

            if (TextUtils.isEmpty(deviceId)) {
                deviceId = android.provider.Settings.Secure.getString(
                        context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", deviceId);

            return json.toString();
        } catch (Exception e) {
        }
        return null;
    }
    
    /**
     * [友盟logcat是否打开]
     * @return true 打开  false 关闭
     */
    public static boolean isLogcatOpen() {
        return sIsLogcatOpen;
    }

    /**
     * [设置友盟logcat是否打开]
     * @param open true 打开  false 关闭
     */
    public static void setIsLogcatOpen(boolean open) {
        sIsLogcatOpen = open;
        MobclickAgent.setDebugMode(open);
    }
    
    /**
     * [打开activity持续时间轨迹]
     * @param open true 只统计activity  false 可以统计页面
     */
    public static void openActivityDurationTrack(boolean open) {
        MobclickAgent.openActivityDurationTrack(open);
    }

    /**
     * [activity返回]
     * @param context context
     */
    public static void onResume(Context context) {
        MobclickAgent.onResume(context);
    }

    /**
     * [activity离开]
     * @param context context
     */
    public static void onPause(Context context) {
        MobclickAgent.onPause(context);
    }
    
    /**
     * [进入页面]
     * @param pageName 页面名称
     */
    public void onPageStart(String pageName) {
        if (pageName == null) {
            return;
        }
        if (!pageName.equals(mLastPageName)) {
            if (mPageState == PAGE_STATE_START) {
                if (sIsLogcatOpen) {
                    EvLog.d("mob", "onPageEnd pageName: " + mLastPageName);
                }
                MobclickAgent.onPageEnd(mLastPageName);
            }
            mLastPageName = pageName;
            mPageState = PAGE_STATE_START;
            if (sIsLogcatOpen) {
                EvLog.d("mob", "onPageStart pageName: " + pageName);
            }
            MobclickAgent.onPageStart(pageName);
        } else {
            if (mPageState == PAGE_STATE_END) {
                mLastPageName = pageName;
                mPageState = PAGE_STATE_START;
                if (sIsLogcatOpen) {
                    EvLog.d("mob", "onPageStart pageName: " + pageName);
                }
                MobclickAgent.onPageStart(pageName);
            }
        }
    }
    
    /**
     * [离开页面]
     * @param pageName 页面名称
     */
    public void onPageEnd(String pageName) {
        if (pageName == null) {
            return;
        }
        if (!pageName.equals(mLastPageName)) {
            if (mPageState == PAGE_STATE_START) {
                mPageState = PAGE_STATE_END;
                if (sIsLogcatOpen) {
                    EvLog.d("mob", "onPageEnd pageName: " + mLastPageName);
                }
                MobclickAgent.onPageEnd(mLastPageName);
            }
        } else {
            if (mPageState == PAGE_STATE_START) {
                mLastPageName = pageName;
                mPageState = PAGE_STATE_END;
                if (sIsLogcatOpen) {
                    EvLog.d("mob", "onPageEnd pageName: " + pageName);
                }
                MobclickAgent.onPageEnd(pageName);
            }
        }
    }
    
    /**
     * [上报错误]
     * @param context context
     * @param error 异常信息
     */
    public static void reportError(Context context, String error) {
        MobclickAgent.reportError(context, error);
    }
    
    /**
     * [上报错误]
     * @param context context
     * @param e exception对象
     */
    public static void reportError(Context context, Throwable e) {
        MobclickAgent.reportError(context, e);
    }
    
    /**
     * [在杀死进程时调用]
     * @param context context
     */
    public static void onKillProcess(Context context) {
        MobclickAgent.onKillProcess(context);
    }
    
    /**
     * @brief : [统计事件发送次数]
     * @param context 当前activity
     * @param eventId 事件id
     */
    public static void onEvent(Context context, String eventId) {
        MobclickAgent.onEvent(context, eventId);
    }
    
    /**
     * @brief : [统计事件2与事件1同时发送的次数]
     * @param context 当前activity
     * @param eventId1  事件1id
     * @param eventId2  事件2id
     * <p>示例：验证关卡难度，即监控”player_dead”这个事件,示例代码如下:</p>
     * <pre>
     * 监控在关卡1的死亡率
     * UmengAgent.onEvent(this, "level_one","player_dead");
     * </pre>
     */
    public static void onEvent(Context context, String eventId1, String eventId2) {
        
    }
    
    /**
     * @brief : [统计事件各属性被触发的次数]
     * @param context 当前activity
     * @param eventId 事件id
     * @param m 当前事件的属性和取值
     * 
     * <p>示例：统计电商应用中“购买”事件发生的次数，以及购买的商品类型及数量，那么在购买的函数里调用：</p>
     * <pre>    
     *     HashMap<String,String> map = new HashMap<String,String>();
     *    map.put("type","book");
     *    map.put("quantity","3"); 
     *    UmengAgent.onEvent(mContext, "purchase", map);
     * </pre>
     */
    public static void onEvent(Context context, String eventId, HashMap<String, String> m) {
        MobclickAgent.onEvent(context, eventId, m);
    }
    
    /**
     * @brief : [事件开始]
     * @param context 当前activity
     * @param eventId 事件id
     * <p>与onEventEnd成对出现,并且参数要保持一致</p>
     */
    /*public static void onEventBegin(Context context, String eventId) {
        MobclickAgent.onEventBegin(context, eventId);
    }*/
    
    /**
     * @brief : [事件开始]
     * @param context 当前activity
     * @param eventId1  事件id1
     * @param eventId2  事件id2
     * <p>与onEventEnd成对出现,并且参数要保持一致</p>
     */
   /* public static void onEventBegin(Context context, String eventId1, String eventId2) {
        MobclickAgent.onEventBegin(context, eventId1, eventId2);
    }*/
    
    /**
     * @brief : [事件结束]
     * @param context 当前activity
     * @param eventId 事件id
     * <p>与onEventBegin成对出现,并且参数要保持一致</p>
     */
    /*public static void onEventEnd(Context context, String eventId) {
        MobclickAgent.onEventEnd(context, eventId);
    }*/
    
    /**
     * @brief : [事件结束]
     * @param context 当前activity
     * @param eventId1  事件id1
     * @param eventId2  事件id2
     * <p>与onEventBegin成对出现,并且参数要保持一致</p>
     */
   /* public static void onEventEnd(Context context, String eventId1, String eventId2) {
        MobclickAgent.onEventEnd(context, eventId1, eventId2);
    }*/
    
    /**
     * @brief : [计算事件,统计数值型变量的值的分布]
     * @param context 当前activity
     * @param eventId 事件id
     * @param m 当前事件的属性和取值
     * @param value 当前事件的数值
     * <p>统计一个数值类型的连续变量（该变量必须为整数），用户每次触发的数值的分布情况，如事件持续时间、每次付款金额等</p>
     * <p>示例：统计一次音乐播放，包括音乐类型，作者和播放时长，可以在音乐播放结束后这么调用：</p>
     * <pre>
     * int duration = 12000; //开发者需要自己计算音乐播放时长
     * Map<String, String> map_value = new HashMap<String, String>();
     * map_value.put("type", "popular");
     * map_value.put("artist", "JJLin");
     * UmengAgent.onEventValue(this, "music", map_value, duration);
     * </pre>
     */
    public static void onEventValue(Context context, String eventId, HashMap<String, String> m, int value) {
        MobclickAgent.onEventValue(context, eventId, m, value);
    }
    
}
