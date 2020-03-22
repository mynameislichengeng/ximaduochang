package com.evideo.kmbox.model.observer.activity;

/**
 * @brief : [Activity观察者]
 */
public interface IActivityObserver {
    
    /**
     * @brief : [处理返回键，在UI线程中执行]
     * @return    true 事件被消费掉，不再传递
     */
    public boolean onBackPressed();

}
