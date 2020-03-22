package com.evideo.kmbox.model.observer.net;

import java.util.ArrayList;
import java.util.List;

import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;

import com.evideo.kmbox.model.observer.BaseSubject;

/**
 * @brief : [wifi信息主题]
 */
public class WifiInfoSubject extends BaseSubject<IWifiInfoObserver> implements IWifiInfoSubject {
    
    private static WifiInfoSubject instance;
    
    private WifiInfoSubject() {
    }
    
    public static WifiInfoSubject getInstance() {
        if(instance == null) {
            instance = new WifiInfoSubject();
        }
        return instance;
    }

    @Override
    public void registWifiInfoObserver(IWifiInfoObserver observer) {
        registObserver(observer);
    }

    @Override
    public void unregistWifiInfoObserver(IWifiInfoObserver observer) {
        unregistObserver(observer);
    }

    @Override
    public void notifyWifiStateChanged(NetworkInfo info) {
        boolean isConnected = (info.isConnected() && info.getDetailedState() == DetailedState.CONNECTED);
        List<IWifiInfoObserver> observers = new ArrayList<IWifiInfoObserver>(getObservers());
        for (IWifiInfoObserver iWifiInfoObserver : observers) {
            iWifiInfoObserver.onWifiStateChange(isConnected);
        }
    }

    @Override
    public void notifyWifiRssiChanged() {
        List<IWifiInfoObserver> observers = new ArrayList<IWifiInfoObserver>(getObservers());
        for (IWifiInfoObserver iWifiInfoObserver : observers) {
            iWifiInfoObserver.onWifiRssiChange();
        }
    }

}
