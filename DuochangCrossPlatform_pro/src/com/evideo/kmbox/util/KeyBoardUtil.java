package com.evideo.kmbox.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


public class KeyBoardUtil {
    
    /**
     * @brief : [显示键盘]
     * @param context
     * @param view
     * @return
     */
    public static void showKeyBoard(Context context, View view) {
        if(context == null || view == null) {
            return;
        }
        InputMethodManager inputMethodManager
        = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    
    /**
     * @brief : [隐藏键盘]
     * @param context
     * @param view
     * @return
     */
    public static boolean hideKeyBoard(Context context, View view) {
        if(context == null || view == null) {
            return false;
        }
        InputMethodManager inputMethodManager
        = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
