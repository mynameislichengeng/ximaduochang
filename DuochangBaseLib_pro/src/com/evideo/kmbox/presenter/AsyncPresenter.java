package com.evideo.kmbox.presenter;


import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.thread.TerminableThreadPool;

/**
 * @brief : [异步任务执行类]
 * @param <T>
 */
public abstract class AsyncPresenter<T> extends BasePresenter {
    
    private T mResult;
    
    private Exception mException;
    
    private TerminableThreadPool mTerminableThreadPool;
    
    private boolean mStarted = false;
    
    public AsyncPresenter() {
    }

    /**
     * @brief : [这个方法里的代码在子线程中执行]
     * @param params
     * @return
     * @throws Exception
     */
    protected abstract T doInBackground(Object... params) throws Exception;
    
    /**
     * @brief : [任务结束后执行的方法]
     * @param result 任务结束响应的结果，result有可能为null，需要做判空处理
     */
    protected abstract void onCompleted(T result, Object... params);
    
    /**
     * @brief : [任务执行发生异常]
     * @param exception
     * @param params
     */
    protected abstract void onFailed(Exception exception, Object... params);
    
    /**
     * @brief : [开始执行]
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
                        if(mException != null) {
                            onFailed(mException, params);
                        } else {
                            onCompleted(mResult, params);
                        }
                    }
                });
                mStarted = false;
            }
        };
        mTerminableThreadPool = new TerminableThreadPool(runnable);
        mTerminableThreadPool.start();
    }
    
    /**
     * @brief : [任务是否已经启动]
     * @return
     */
    public boolean isStarted() {
        return mStarted;
    }
    
    /**
     * @brief : [取消任务]
     */
    public void cancel() {
        if(mTerminableThreadPool != null) {
            mTerminableThreadPool.cancel();
        }
    }
    
    /**
     * @brief : [任务是否已取消]
     * @return
     */
    public boolean isCancel() {
        if(mTerminableThreadPool != null) {
            return mTerminableThreadPool.isCancel();
        }
        return false;
    }
    
}
