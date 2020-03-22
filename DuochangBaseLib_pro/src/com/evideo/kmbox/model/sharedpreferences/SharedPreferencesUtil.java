package com.evideo.kmbox.model.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * [SharedPreferences工具类]
 */
public class SharedPreferencesUtil {
    
    private SharedPreferences mSharedPreferences;
    
    private SharedPreferencesUtil(SharedPreferences sp) {
        mSharedPreferences = sp;
    }
    
    public SharedPreferencesUtil(Context context, String fileName) {
        this(context.getSharedPreferences(fileName, Context.MODE_PRIVATE));
    }
    
    private SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }
    
    private Editor getEditor() {
        SharedPreferences sp = getSharedPreferences();
        return sp.edit();
    }
    
    public boolean getBoolean(String key, boolean defValue) {
        return getSharedPreferences().getBoolean(key, defValue);
    }
    
    public boolean putBoolean(String key, boolean value) {
        Editor editor = getEditor();
        editor.putBoolean(key, value);
        return editor.commit();
    }
    
    public int getInt(String key, int defValue) {
        return getSharedPreferences().getInt(key, defValue);
    }
    
    public boolean putInt(String key, int value) {
        Editor editor = getEditor();
        editor.putInt(key, value);
        return editor.commit();
    }
    
    public long getLong(String key, long defValue) {
        return getSharedPreferences().getLong(key, defValue);
    }
    
    public boolean putLong(String key, long value) {
        Editor editor = getEditor();
        editor.putLong(key, value);
        return editor.commit();
    }
    
    public float getFloat(String key, float defValue) {
        return getSharedPreferences().getFloat(key, defValue);
    }
    
    public boolean putFloat(String key, float value) {
        Editor editor = getEditor();
        editor.putFloat(key, value);
        return editor.commit();
    }
    
    public String getString(String key, String defValue) {
        return getSharedPreferences().getString(key, defValue);
    }
    
    public boolean putString(String key, String value) {
        Editor editor = getEditor();
        editor.putString(key, value);
        return editor.commit();
    }
}
