/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年2月14日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.service.log;



/**
 * [功能说明]
 */
public class LogSpaceManager {
    private static LogSpaceManager instance = null;
    public static LogSpaceManager getInstance() {
        if(instance == null) {
            synchronized (LogSpaceManager.class) {
                LogSpaceManager temp = instance;
                if(temp == null) {
                  temp = new LogSpaceManager();
                  instance = temp;
                }
            }
         }
         return instance;
    }
 
    
    private LogSpaceClean mLogSpaceClen = null;
    public void start(/*Context context*/) {
        if (mLogSpaceClen == null) {
            mLogSpaceClen = new LogSpaceClean();
        }
        mLogSpaceClen.start();
    }
    
    public void destory(/*Context context*/) {
        if (mLogSpaceClen != null) {
            mLogSpaceClen.destroy();
            mLogSpaceClen = null;
        }
    }
}
