/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年7月3日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.player;

/**
 * [功能说明]
 */
public class MediaPlayerFactory {

    public IKmPlayer getPlayer(int mediaId,Object renderView) {
      if (mediaId == KmVideoPlayerType.MEDIAPLAYER){
            return new KmVideoPlayer(renderView);
        } else {
            return new KmVLCVideoPlayer(renderView);
        }
    }
}
