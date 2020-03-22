package com.evideo.kmbox.model.observer.screen;



public interface IScreenInfoSubject {
    /**
     * @brief : [注册screen信息观察者]
     * @param observer
     */
    public void registerScreenInfoObserver(IScreenInfoObserver observer);
    
    /**
     * @brief : [注销screen信息观察者]
     * @param observer
     */
    public void unregisterScreenInfoObserver(IScreenInfoObserver observer);
    
    /**
     * @brief : [通知screen状态改变]
     */
    public void notifyScreenStateChanged(boolean isScreenOn);

}
