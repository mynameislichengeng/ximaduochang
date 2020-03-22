package com.evideo.kmbox.model.observer.net;

/**
 * @brief : [网络状态观察者,在UI线程中执行]
 */
public interface INetworkInfoObserver {
    
    /**
     * @brief : [网络连接状态改变，在UI线程中执行]
     * @param isConnected true 连接   false 断开
     */
    public void onNetworkChanged(boolean isConnected);

}
