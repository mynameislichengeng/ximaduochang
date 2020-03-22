/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年6月23日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.playerctrl;

import com.evideo.kmbox.ErrorInfo;

/**
 * [功能说明]
 */
public interface IPlayItemDownListener {
    /**
     * [功能说明]
     * @param serialNum
     */
    public void onNothingDown(int serialNum);
    
    public void onDownMediaError(long id,ErrorInfo errorInfo);
    public void onDownMediaStart(long id,String url,String savePath);
    public void onDownMediaProgress(long id,String savePath,long downedSize,long totalSize,float speed);
    public void onDownMediaFinish(long id,String savePath);
    
    public void onDownErcSuccess(long id,String path);
    public void onDownErcFailed(long id);
}
