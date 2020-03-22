/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年8月13日     "wurongquan"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.update.db;

/**
 * [功能说明]
 */
public interface IUpdateProgressObserver {
    
    public void onInitFail();

    public void onUpdateStart();
    
    public void onUpdateProgress(int progress, int scanedNum, int totalNum);
    
    public void onUpdateFinished(int totalNum, int importedNum);
    
    public void onUpdateError();

}
