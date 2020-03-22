package com.evideo.kmbox.thread;

import com.evideo.kmbox.thread.AsyncTaskManage.ThreadInfo;


public abstract class AbsTerminableThread implements ITerminableThread{
//    private static final String TAG = "AsyncTaskManage";
    
    private AsyncTaskManage mAsyncTaskManage;
    private ThreadInfo mThreadInfo;
    private Runnable mTask;
    private boolean isStarted = false;
    private boolean isCancel = true;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
//            Thread currentThread = Thread.currentThread();
//            EvLog.i(TAG, "InterruptibleThread run() -->开始:isCancel="+isCancel+";tId="+currentThread.getId()+";tHCode="+currentThread.hashCode());
            if(isCancel){
                return;
            }
            mThreadInfo = mAsyncTaskManage.registerThread();
            if(mTask != null){
                mTask.run();
            }
            AbsTerminableThread.this.run();
            mThreadInfo = null;
            mAsyncTaskManage.unregisterThread();
            isCancel = true;
        }
    };
    
    public AbsTerminableThread(){
        this(null);
    }
    
    public AbsTerminableThread(Runnable task){
        mAsyncTaskManage = AsyncTaskManage.getInstance();
        mTask = task;
    }
    
    public void setTask(Runnable task){
        mTask = task;
    }
    
    public void run(){
        
    }
    
    @Override
    public final void start(){
        if(isStarted){
            return;
        }
        isStarted = true;
        isCancel = false;
        runTask(mRunnable);
    }
    
    protected abstract void runTask(Runnable runnable);
    
    @Override
    public final void cancel(){
        isCancel = true;
        if(mThreadInfo != null){
            mAsyncTaskManage.cancelAsyncTask(mThreadInfo);
        }
    }

    @Override
    public boolean isCancel() {
        return isCancel;
    }
    
}
