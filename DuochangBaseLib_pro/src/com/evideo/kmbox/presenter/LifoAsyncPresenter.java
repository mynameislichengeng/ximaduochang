/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-3-30     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.presenter;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.thread.LifoTerminableThreadPool;

/**
 * [功能说明]异步任务执行类,线程池中的队列使用lifo算法
 * @param <T> 结果数据类型
 */
public abstract class LifoAsyncPresenter<T> extends BasePresenter {
    
    private T mResult;
    
    private Exception mException;
    
    private LifoTerminableThreadPool mLifoTerminableThreadPool;
    
    private boolean mStarted = false;
    
    public LifoAsyncPresenter() {
    }
    
    /**
     * [这个方法里的代码在子线程中执行]
     * @param params 参数
     * @return T
     * @throws Exception 异常
     */
    protected abstract T doInBackground(Object... params) throws Exception;
    
    /**
     * [任务结束后执行的方法]
     * @param result 任务结束响应的结果，result有可能为null，需要做判空处理
     * @param params 启动时传入的参数
     */
    protected abstract void onCompleted(T result, Object... params);
    
    /**
     * [任务执行发生异常]
     * @param exception 异常
     * @param params 启动时传入的参数
     */
    protected abstract void onFailed(Exception exception, Object... params);
    
    /**
     * [开始执行]
     * @param params 传入的参数
     */
    public void start(final Object... params) {
        mStarted = true;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mResult = null;
                mException = null;
                try {
                    mResult = doInBackground(params);
                } catch (Exception e) {
                    mException = e;
                }
                runInUI(BaseApplication.getHandler(),new Runnable() {
                    @Override
                    public void run() {
                        if (mException != null) {
                            onFailed(mException, params);
                        } else {
                            onCompleted(mResult, params);
                        }
                    }
                });
                mStarted = false;
            }
        };
        mLifoTerminableThreadPool = new LifoTerminableThreadPool(runnable);
        mLifoTerminableThreadPool.start();
    }
    
    /**
     * [任务是否已经启动]
     * @return true 已经启动   false 未启动
     */
    public boolean isStarted() {
        return mStarted;
    }
    
    /**
     * [取消任务]
     */
    public void cancel() {
        if (mLifoTerminableThreadPool != null) {
            mLifoTerminableThreadPool.cancel();
        }
    }
    
    /**
     * [任务是否已取消]
     * @return true 已取消   false 未取消
     */
    public boolean isCancel() {
        if (mLifoTerminableThreadPool != null) {
            return mLifoTerminableThreadPool.isCancel();
        }
        return false;
    }

}
