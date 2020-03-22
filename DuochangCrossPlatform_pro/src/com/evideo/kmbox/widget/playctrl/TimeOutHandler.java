/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-8-7     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.playctrl;

import android.os.Handler;
import android.os.Message;

/**
 * [功能说明]
 */
class TimeOutHandler extends Handler {
    private int mTimeout = 5000;
    private int mTimeoutMessage = 1;
    private TimeOutListener mListener = null;
   
    public interface TimeOutListener {
        public boolean onTimeOut();
    }
    
    public void setListener(TimeOutListener listener) {
        mListener = listener;
    }
    public TimeOutHandler(int timeOut,int timeOutMsgID) {
        mTimeout = timeOut;
        mTimeoutMessage = timeOutMsgID;
    }
   
    public void resend() {
        this.removeMessages(mTimeoutMessage);
        this.sendEmptyMessageDelayed(mTimeoutMessage, mTimeout);
    }
    
    public void clear() {
        this.removeMessages(mTimeoutMessage);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == mTimeoutMessage) {
            if (mListener!= null) {
                mListener.onTimeOut();
            }
        }
    }
}