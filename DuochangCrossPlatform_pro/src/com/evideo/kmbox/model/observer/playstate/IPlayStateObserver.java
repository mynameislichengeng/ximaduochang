/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年1月5日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.observer.playstate;

/**
 * [功能说明]
 */
public interface IPlayStateObserver {
    /** [歌曲没有播放就停止] */
    public static final int STOP_IDLE = 0;
    /** [歌曲播放中途停止] */
    public static final int STOP_MIDDLE = 1;
    /** [歌曲播放自动结束] */
    public static final int STOP_AUTO = 2;
    
    public void onDowningStart();
    public void onPlayStart();
    
    /**
     * [功能说明]
     * @param stopState
     * @return
     */
    public boolean onPlayStop(int stopState,String shareCode);
    public void onPlayError(String errMessage);
}
