/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年3月24日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.playerctrl;

import com.evideo.kmbox.ErrorInfo;

/**
 * [功能说明] 播控状态
 */
public interface IPlayStateObserver {
    
    /**
     * [功能说明] 更新下载状态
     */
    public void updateDownState();
    
    /**
     * [功能说明] 更新下载百分比
     * @param percent 
     * @param speed
     */
    public void updateDownPercent(int percent,float speed);
    /**
     * [功能说明] 歌曲开始播放
     */
    public void onPlayStart();
    /**
     * [功能说明] 播放
     */
    public void onPlay();
    /**
     * [功能说明] 暂停
     */
    public void onPlayPause();
    /**
     * [功能说明] 停止
     */
    public void onPlayStop();
    /**
     * [功能说明] 播放自动停止
     */
    public void onPlayAutoStop();
    /**
     * [功能说明] 播放错误
     * @param info
     */
    public void onPlayError(ErrorInfo info);
    /**
     * [功能说明] 播放缓冲
     * @param percent
     */
    public void onBufferingChange(int percent);
    /**
     * [功能说明] 播放模式变化
     */
    /*public void onModeChange();*/
}
