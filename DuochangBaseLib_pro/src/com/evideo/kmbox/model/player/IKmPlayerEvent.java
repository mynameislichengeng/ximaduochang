/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年5月4日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.player;

/**
 * [功能说明]
 */
public interface IKmPlayerEvent
{
    public void onPrepared();
    /**
     * \brief   player在启动解码器后真正开始播放时调用
     * \return  void
     */
    public void onPlay();
    
    public void onComplete();

    /**
     * \brief   player在播放过程中出现错误时调用
     * \return  void
     */
    public void onError(int arg1,int arg2);

    /**
     * \brief   player在播放结束时调用
     * \return  void
     */
    public void onStop();

    /**
     * \brief   缓冲开始
     * \return  void
     */
    public void onBufferingStart();

    /**
     * \brief   更新缓冲进度
     * \return  void
     */
    public void onBufferingUpdate(int percent);
    
    /**
     * \brief   缓冲结束
     * \return  void
     */
    public void onBufferingEnd();
};

