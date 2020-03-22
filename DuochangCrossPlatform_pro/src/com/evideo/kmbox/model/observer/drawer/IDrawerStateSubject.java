package com.evideo.kmbox.model.observer.drawer;

/**
 * @brief : [抽屉状态主题接口]
 */
public interface IDrawerStateSubject {
    
    /**
     * @brief : [注册抽屉状态观察者]
     * @param observer
     */
    public void registDrawerStateObserver(IDrawerStateObserver observer);
    
    /**
     * @brief : [注销抽屉状态观察者]
     * @param observer
     */
    public void unregistDrawerStateObserver(IDrawerStateObserver observer);
    
    /**
     * @brief : [通知抽屉已完全关闭]
     * @param level
     */
    public void notifyDrawerClosed(int level);
    
    /**
     * @brief : [通知抽屉已完全打开]
     * @param level
     */
    public void notifyDrawerOpened(int level);
    
    /**
     * @brief : [通知菜单抽屉重置选中项id]
     * @param level
     */
    public void notifyDrawerResetSeletedId(int level);
    
    /**
     * @brief : [通知网络设置界面改变]
     * @param isEthernet
     */
    public void notifyNetSettingViewChanged(boolean isEthernet);
    
}
