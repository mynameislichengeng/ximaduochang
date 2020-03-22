package com.evideo.kmbox.model.sharedpreferences;

import java.util.Arrays;
import java.util.List;

import com.evideo.kmbox.BaseApplication;

/**
 * <p>KmBox统一提供对sharedpreferences操作的类</p>
 * <p>文件名在FILE_NAME常量中定义</p>
 * <p>key的常量值统一在KeyName中定义</p>
 */
public final class KmSharedPreferences {
    
    private static final String FILE_NAME = "com_evideo_kmbox_model_sp";
    
    private SharedPreferencesUtil mSPUtil;
    
    private static KmSharedPreferences sInstance = new KmSharedPreferences();
    
    private KmSharedPreferences() {
        mSPUtil = new SharedPreferencesUtil(BaseApplication.getInstance(), FILE_NAME);
    }
    
    /**
     * [获取单例]
     * @return 返回单例
     */
    public static KmSharedPreferences getInstance() {
        return sInstance;
    }
    
    /**
     * [取布尔值]
     * @param key 键名称
     * @param defValue 缺省键值
     * @return 键值
     */
    public boolean getBoolean(String key, boolean defValue) {
        return mSPUtil.getBoolean(key, defValue);
    }
    
    /**
     * [设置布尔值]
     * @param key 键名
     * @param value 键值
     * @return true:成功;false：失败
     */
    public boolean putBoolean(String key, boolean value) {
        return mSPUtil.putBoolean(key, value);
    }
    
    /**
     * [取整型]
     * @param key 键名
     * @param defValue 缺省键值
     * @return 整型键值
     */
    public int getInt(String key, int defValue) {
        return mSPUtil.getInt(key, defValue);
    }
    
    /**
     * [设置整型]
     * @param key 键名
     * @param value 键值
     * @return true:成功;false：失败
     */
    public boolean putInt(String key, int value) {
        return mSPUtil.putInt(key, value);
    }
    
    /**
     * [取长整型]
     * @param key 键名
     * @param defValue 键值
     * @return 长整型键值
     */
    public long getLong(String key, long defValue) {
        return mSPUtil.getLong(key, defValue);
    }
    
    /**
     * [设置长整型键]
     * @param key 键名
     * @param value 键值
     * @return true:成功;false：失败
     */
    public boolean putLong(String key, long value) {
        return mSPUtil.putLong(key, value);
    }
    
    /**
     * [取浮点型键]
     * @param key 键名
     * @param defValue 键值
     * @return  true:成功;false：失败
     */
    public float getFloat(String key, float defValue) {
        return mSPUtil.getFloat(key, defValue);
    }
    
    /**
     * [设置浮点型键值对]
     * @param key 键名
     * @param value 键值
     * @return true:成功;false：失败
     */
    public boolean putFloat(String key, float value) {
        return mSPUtil.putFloat(key, value);
    }
    
    /**
     * [取字符串]
     * @param key 键名
     * @param defValue 键值
     * @return true:成功;false：失败
     */
    public String getString(String key, String defValue) {
        return mSPUtil.getString(key, defValue);
    }
    
    /**
     * [设置字符串键值对]
     * @param key 键名
     * @param value 键值
     * @return true:成功;false：失败
     */
    public boolean putString(String key, String value) {
        return mSPUtil.putString(key, value);
    }
    
    public List<String> getStringArray(String key, String defValue) {
        String regularEx = "#";
        String[] str = null;
        String values;
        values = mSPUtil.getString(key, defValue);
        str = values.split(regularEx);
        return Arrays.asList(str);
    }

    public void putStringArray(String key, List<String> values) {
        String regularEx = "#";
        String str = "";
        if (values != null) {
            for (String value : values) {
                str += value;
                str += regularEx;
            }
            mSPUtil.putString(key, str);
        }
    }
}
