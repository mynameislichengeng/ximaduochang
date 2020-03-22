package com.evideo.kmbox.model.observer.activity;

/**
 * @brief : [activity主题接口]
 */
public interface IActivitySubject {
    
    /**
     * @brief : [注册activity观察者]
     * @param observer
     */
    public void registActivityObserver(IActivityObserver observer);
    
    /**
     * @brief : [注销activity观察者]
     * @param observer
     */
    public void unregistActivityObserver(IActivityObserver observer);
    
    /**
     * @brief : [通知返回键按下]
     */
    public void notifyBackPressed();

}
