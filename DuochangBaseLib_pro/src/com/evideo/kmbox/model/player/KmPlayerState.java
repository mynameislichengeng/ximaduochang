package com.evideo.kmbox.model.player;

/**
 * \brief 播放器状态
 */
public class KmPlayerState {

    public static final int PlayerState_eUNKNOW = -1;
    /**
     * < 播放器初始后处于空闲状态
     */
    public static final int PlayerState_eIdle = 0;
    /**
     * < 播放器初始后处于初始化状态
     */
    public static final int PlayerState_eInit = 1;
    /**
     * < 播放器初始后处于准备中状态
     */
    public static final int PlayerState_ePreparing = 2;
    /**
     * < 播放器初始后处于准备好状态
     */
    public static final int PlayerState_ePrepared = 3;
    /**
     * < 播放器处于播放状态
     */
    public static final int PlayerState_ePlay = 4;
    /**
     * < 播放器处于暂停状态
     */
    public static final int PlayerState_ePause = 5;
    /**
     * < 播放器处于停止状态
     */
    public static final int PlayerState_eStoped = 6;
    /**
     * < 播放器正在缓冲和识别
     */
    public static final int PlayerState_eBuffering = 7;
    /**
     * < 播放结束
     */
    public static final int PlayerState_ePlayComplete = 8;
    /**
     * < 播放器出错
     */
    public static final int PlayerState_eErrors = 9;

    /**
     * < 播放器初始后处于反初始化状态
     */
    public static final int PlayerState_eUnInit = 10;

    public static final int PlayerState_eMAX = 11;
}
