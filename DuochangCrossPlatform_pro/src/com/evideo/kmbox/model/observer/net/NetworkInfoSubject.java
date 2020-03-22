package com.evideo.kmbox.model.observer.net;

import java.util.ArrayList;
import java.util.List;


import com.evideo.kmbox.model.observer.BaseSubject;
import com.evideo.kmbox.util.NetUtils;

/**
 * @brief : [网络事件主题]
 */
public class NetworkInfoSubject extends BaseSubject<INetworkInfoObserver> implements INetworkInfoSubject {
    
    private static NetworkInfoSubject instance;
    
    private NetworkInfoSubject() {
    }
    
    public static NetworkInfoSubject getInstance() {
        if(instance == null) {
            instance = new NetworkInfoSubject();
        }
        return instance;
    }
    
    private Boolean isNetConnceted = null;

    @Override
    public void registNetworkInfoObserver(INetworkInfoObserver observer) {
        registObserver(observer);
    }

    @Override
    public void unregistNetworkInfoObserver(INetworkInfoObserver observer) {
        unregistObserver(observer);
    }

    @Override
    public void notifyNetworkChanged() {
        List<INetworkInfoObserver> observers = new ArrayList<INetworkInfoObserver>(getObservers());
        boolean isConnected = NetUtils.isNetworkConnected(getContext());
        for (INetworkInfoObserver iNetworkInfoObserver : observers) {
            if(isNetConnceted == null || isNetConnceted != isConnected) {
                iNetworkInfoObserver.onNetworkChanged(isConnected);
            }
        }
        isNetConnceted = isConnected;
    }
    
}
