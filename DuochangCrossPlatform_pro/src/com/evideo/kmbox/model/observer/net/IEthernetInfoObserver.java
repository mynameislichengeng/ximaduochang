package com.evideo.kmbox.model.observer.net;

/**
 * @brief : [以太网信息观察者接口]
 */
public interface IEthernetInfoObserver {
    
    /**
     * @brief : [以太网状态改变，在UI线程中执行]
     * @param state 与BroadcastConstant的常量进行匹配
     *     <p>ETHER_STATE_DISCONNECTED
     *     <p>ETHER_STATE_CONNECTING
     *     <p>ETHER_STATE_CONNECTED
     */
    public void onEthernetStateChanged(int state);

}
