package com.evideo.kmbox.model.observer.net;

/**
 * @brief : [网络信息主题接口]
 */
public interface INetworkInfoSubject {
    
    /**
     * @brief : [注册网络信息观察者]
     * @param observer
     */
    public void registNetworkInfoObserver(INetworkInfoObserver observer);
    
    /**
     * @brief : [注销网络信息观察者]
     * @param observer
     */
    public void unregistNetworkInfoObserver(INetworkInfoObserver observer);
    
    /**
     * @brief : [派发网络变更事件]
     */
    public void notifyNetworkChanged();

}
