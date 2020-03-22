package com.evideo.kmbox.model.observer.net;

import android.net.NetworkInfo;

/**
 * @brief : [wifi信息主题接口]
 */
public interface IWifiInfoSubject {
    
    /**
     * @brief : [注册wifi信息观察者]
     * @param observer
     */
    public void registWifiInfoObserver(IWifiInfoObserver observer);
    
    /**
     * @brief : [注销wifi信息观察者]
     * @param observer
     */
    public void unregistWifiInfoObserver(IWifiInfoObserver observer);
    
    /**
     * @brief : [通知wifi状态改变]
     */
    public void notifyWifiStateChanged(NetworkInfo info);
    
    /**
     * @brief : [通知当前连接wifi信号强度改变]
     * @param level
     */
    public void notifyWifiRssiChanged();

}
