package com.evideo.kmbox.model.observer.drawer;

/**
 * @brief : [菜单抽屉状态观察者,实现方法都在UI线程中执行]
 */
public interface IDrawerStateObserver {
    
    /**
     * @brief : [抽屉完全关闭，在UI线程中执行]
     * @param level 第几级
     * @param needResetFocus 是否需要重新设置焦点
     */
    public void onDrawerClosed(int level, boolean needResetFocus);
    
    /**
     * @brief : [抽屉完全打开，在UI线程中执行]
     * @param level
     */
    public void onDrawerOpened(int level);
    
    /**
     * @brief : [重置选中项，在UI线程中执行]
     * @param level
     */
    public void onResetSeletedId(int level);
    
    /**
     * @brief : [第三级菜单的网络设置界面改变，在UI线程中执行]
     * @param isEthernet true 以太网  false wifi
     */
    public void onNetSettingViewChanged(boolean isEthernet);
    
}
