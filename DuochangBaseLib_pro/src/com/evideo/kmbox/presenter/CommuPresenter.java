/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年6月8日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.presenter;


/**
 * [功能说明]
 */

public class CommuPresenter extends AsyncPresenter<Boolean> {
    
    public interface CommuCallback {
        public Boolean doCommu(Object... params) throws Exception;
        public void commuSuccess();
        public void commuFailed(Exception exception);
    }
    
    private CommuCallback mCommuCallback;
    public CommuPresenter(CommuCallback callBack) {
        mCommuCallback = callBack;
    }
    
    @Override
    protected Boolean doInBackground(Object... params) throws Exception {
        if (mCommuCallback != null) {
            return mCommuCallback.doCommu(params);
        } else {
            return false;
        }
    }
    
    @Override
    protected void onCompleted(Boolean result, Object... params) {
        if (result != null && result) {
            if (mCommuCallback != null) {
                mCommuCallback.commuSuccess();
            }
        } else {
            if (mCommuCallback != null) {
                mCommuCallback.commuFailed(null);
            }
        }
    }

    @Override
    protected void onFailed(Exception exception, Object... params) {
        if (mCommuCallback != null) {
            mCommuCallback.commuFailed(exception);
        }
    }
}