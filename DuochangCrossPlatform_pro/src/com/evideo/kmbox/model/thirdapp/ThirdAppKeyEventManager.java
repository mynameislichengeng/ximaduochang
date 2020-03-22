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

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.evideo.kmbox.model.thirdapp.HomeWatchReceiver.IHomeKeyPressListener;
import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明]
 */
public class ThirdAppKeyEventManager {
    private static ThirdAppKeyEventManager instance = null;
    private HomeWatchReceiver mHomeKeyReceiver = null;
    public static ThirdAppKeyEventManager getInstance() {
        if(instance == null) {
            synchronized (ThirdAppKeyEventManager.class) {
                ThirdAppKeyEventManager temp = instance;
                if(temp == null) {
                  temp = new ThirdAppKeyEventManager();
                  instance = temp;
                }
            }
         }
         return instance;
    }
    
    private ThirdAppKeyEventManager() {
        mHomeKeyReceiver = new HomeWatchReceiver();
    }

    public  void registerKeyReceiver(Context context) {
        EvLog.i("registerKeyReceiver");
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.registerReceiver(mHomeKeyReceiver, homeFilter);
    }

    public  void unregisterKeyReceiver(Context context) {
        EvLog.i("unregisterKeyReceiver");
        if (mHomeKeyReceiver != null) {
            context.unregisterReceiver(mHomeKeyReceiver);
        }
    }
    
    public void setHomeKeyListener(IHomeKeyPressListener listener) {
        if (mHomeKeyReceiver != null) {
            mHomeKeyReceiver.setHomeKeyListener(listener);
        }
    }
}
