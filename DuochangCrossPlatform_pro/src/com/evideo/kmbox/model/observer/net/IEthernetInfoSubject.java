package com.evideo.kmbox.model.observer.net;

/**
 * @brief : [以太网信息主题]
 */
public interface IEthernetInfoSubject {

    /**
     * @brief : [注册以太网信息主题]
     */
    public void registEthernetInfoObserver(IEthernetInfoObserver observer);
    
    /**
     * @brief : [注销以太网信息主题]
     */
    public void unregistEthernetInfoObserver(IEthernetInfoObserver observer);
    
    /**
     * @brief : [通知以太网状态改变]
     * @param state
     */
    public void notifyEthernetStateChanged(int state);
    
}
