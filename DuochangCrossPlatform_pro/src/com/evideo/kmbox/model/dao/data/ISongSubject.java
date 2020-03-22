/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-5-15     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.model.dao.data;

import java.util.List;

/**
 * [功能说明]歌曲主题接口
 */
public interface ISongSubject {
    
    /**
     * [功能说明]注册歌曲观察者
     * @param observer 歌曲观察者
     */
    public void registerSongObserver(ISongObserver observer);
    
    /**
     * [功能说明]注销歌曲观察者
     * @param observer 歌曲观察者
     */
    public void unregisterSongObserver(ISongObserver observer);
    
    /**
     * [功能说明]通知待删歌曲列表
     * @param ids 歌曲id列表
     */
    public void notifySongsToBeDeleted(List<Integer> ids);
    
    public void notifySongsDeletedFinish();

}
