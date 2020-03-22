/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年11月2日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.thirdapp;

import com.evideo.kmbox.util.EvLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * [功能说明]
 */
public class HomeWatchReceiver extends BroadcastReceiver {
    private static final String EvLog_TAG = "HomeReceiver";
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
//    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
//    private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
//    private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

    
    public interface IHomeKeyPressListener {
        public void onHomeKeyPress();
    }
    private IHomeKeyPressListener mListener = null;
    public void setHomeKeyListener(IHomeKeyPressListener listener) {
        mListener = listener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        EvLog.i(EvLog_TAG, "onReceive: action: " + action);
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            // android.intent.action.CLOSE_SYSTEM_DIAEvLogS
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            EvLog.i(EvLog_TAG, "reason: " + reason);
            if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                // 短按Home键
                EvLog.i(EvLog_TAG, "homekey");
                if (mListener != null) {
                    mListener.onHomeKeyPress();
                }
            }
        }
    }
}
