package com.evideo.kmbox.model.observer.net;

/**
 * @brief : [wifi信息观察者]
 */
public interface IWifiInfoObserver {

    /**
     * @brief : [wifi状态改变，在UI线程中执行]
     * @param isConnected    是否连接
     */
    public void onWifiStateChange(boolean isConnected);
    
    /**
     * @brief : [wifi信号强度改变，在UI线程中执行]
     */
    public void onWifiRssiChange();
    
}
