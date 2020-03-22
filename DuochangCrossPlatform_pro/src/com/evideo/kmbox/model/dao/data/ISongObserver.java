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
 * [功能说明]歌曲观察者
 */
public interface ISongObserver {
    
    /**
     * [功能说明]删歌
     * @param ids 待删歌曲id列表
     */
    public void onSongsToBeDeleted(List<Integer> ids);
    
    public void onSongdDeletedFinish();

}
