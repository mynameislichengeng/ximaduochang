/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-9-17     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview;

import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.model.songmenu.SongMenu;

/**
 * [功能说明]主界面回调接口
 */
public interface IMainViewCallback {
    
    /**
     * [功能说明]根据view id 打开指定界面
     * @param viewId 界面id
     * @param parentViewId 父view的id
     * @see MainViewId
     */
    public void openView(int viewId, int parentViewId);
    
    /**
     * [功能说明]打开指定歌单详情页面
     * @param songMenu 歌单 
     */
    public void openSongMenuDetailsView(SongMenu songMenu);
    
    public void openSingerDetailsView(Singer singer);
    
    /**
     * [功能说明] 打开指定的排行
     */
    public void openAssignRankView(int rankId);
}
