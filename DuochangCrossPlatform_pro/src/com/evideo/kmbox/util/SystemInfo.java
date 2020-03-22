package com.evideo.kmbox.util;

import com.evideo.kmbox.model.umeng.UmengAgentUtil;

import java.lang.reflect.Method;

public class SystemInfo {

    private static final String TAG = SystemInfo.class.getSimpleName();

    /**
     * [系统硬件版本号]
     */
    public static final String HW_VERSION = "1.10";

    /**
     * @return
     * @brief : [获取系统版本号]
     */
    public static String getSystemVersion() {
        String version = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method m = c.getMethod("get", String.class);
            version = (String) m.invoke(c.newInstance(), "ro.product.version");
        } catch (Exception e) {
            EvLog.e(TAG, e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        return version;
    }
}
