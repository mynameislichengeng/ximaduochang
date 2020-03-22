package com.evideo.kmbox;

/**
 * @brief : [广播常量，此部分常量定义请勿修改]
 */
public class BroadcastConstant {
    /**-----------------------------以太网相关广播-------------------------------------------------------------------*/

    /** [以太网状态改变] */
    public static final String ETHERNET_STATE_CHANGED_ACTION = "android.net.ethernet.ETHERNET_STATE_CHANGED";
    /** [以太网状态extra name] */
    public static final String EXTRA_ETHERNET_STATE = "ethernet_state";
    /** [以太网断开连接] */
    public static final int ETHER_STATE_DISCONNECTED=0;
    /** [以太网连接中] */
    public static final int ETHER_STATE_CONNECTING=1;
    /** [以太网连接完成] */
    public static final int ETHER_STATE_CONNECTED=2;

    
    /**-----------------------------home键-------------------------------------------------------------------*/

    /** [home按键事件] */
    public static final String ACTION_KEYEVENT_HOME = "com.kmbox.keyevent.home";
    
    /** --------------------------------------------end------------------------------------------------------------ */
    
    
}
