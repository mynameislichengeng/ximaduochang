package com.evideo.kmbox.model.observer.screen;

import java.util.ArrayList;
import java.util.List;

import com.evideo.kmbox.model.observer.BaseSubject;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;


public class ScreenInfoSubject  extends BaseSubject<IScreenInfoObserver> implements IScreenInfoSubject {
    
    private static ScreenInfoSubject instance;
    private ScreenStatusReciver screen = null;
    private boolean isScreenOn = true;
    
    private ScreenInfoSubject() {
        
    }
    
    public static ScreenInfoSubject getInstance() {
        if(instance == null) {
            instance = new ScreenInfoSubject();
        }
        return instance;
    }

    @Override
    public void registerScreenInfoObserver(IScreenInfoObserver observer) {
        registObserver(observer);
    }

    @Override
    public void unregisterScreenInfoObserver(IScreenInfoObserver observer) {
        unregistObserver(observer);
    }

    @Override
    public void notifyScreenStateChanged(boolean isScreenOn) {
        List<IScreenInfoObserver> observers = new ArrayList<IScreenInfoObserver>(getObservers());
        for (IScreenInfoObserver iScreenInfoObserver : observers) {
            iScreenInfoObserver.onScreenStateChange(isScreenOn);
        }
    }

    public boolean getScreenStatus() {
        return isScreenOn;
    }
    
    public boolean isScreenOn() {
        return isScreenOn;
    }

    public void start(Context context) {
        screen = new ScreenStatusReciver(context);
        screen.startWatch();
    }
    
    public void stop() {
        if ( screen != null ) {
            screen.stopWatch();
            screen = null;
        }
    }
    
    // 回调接口
    public interface ScreenStatus {
        public void onScreenStatusChange(boolean screenOn);
    }
    
    public class ScreenStatusReciver {

        static final String TAG = "HomeWatcher";
        private Context mContext;
        private IntentFilter mFilter;
        private ScreenStatusReceiver mRecevier;

        public ScreenStatusReciver(Context context) {
            mContext = context;
            mRecevier = new ScreenStatusReceiver();
            mFilter = new IntentFilter(); 
            mFilter.addAction(Intent.ACTION_SCREEN_ON);  
            mFilter.addAction(Intent.ACTION_CALL);  
            mFilter.addAction(Intent.ACTION_SCREEN_OFF);  
        }

        /**
         * 开始监听，注册广播
         */
        public void startWatch() {
            if (mRecevier != null) {
                mContext.registerReceiver(mRecevier, mFilter);
            }
        }

        /**
         * 停止监听，注销广播
         */
        public void stopWatch() {
            if (mRecevier != null) {
                mContext.unregisterReceiver(mRecevier);
            }
        }

        /**
         * 广播接收者
         */
        class ScreenStatusReceiver extends BroadcastReceiver {  
            String SCREEN_ON = "android.intent.action.SCREEN_ON";  
            String SCREEN_OFF = "android.intent.action.SCREEN_OFF";  

            @Override  
            public void onReceive(Context context, Intent intent) {  
                Log.e("ScreenStatusReciver" , " intent.getAction() " + intent.getAction());
                // 屏幕唤醒  
                if(SCREEN_ON.equals(intent.getAction())){
                    isScreenOn = true;
                    notifyScreenStateChanged(true);
                }  
                // 屏幕休眠  
                else if(SCREEN_OFF.equals(intent.getAction())){
                    isScreenOn = false;
                    notifyScreenStateChanged(false);
                }  
            }
        }
    }

}


