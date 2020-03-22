package com.evideo.kmbox.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TerminableThreadPool extends AbsTerminableThread {
    
    /** [核心线程数量] */
    private static final int CORE_POOL_SIZE = 6;
    
    /** [线程池中线程的最大允许数量] */
    private static final int MAXIMUM_POOL_SIZE = 50;
    
    /** [线程数超过核心线程数量时,会回收超过此参数定义的时间的空闲线程,单位秒] */
    private static final int KEEP_ALIVE_TIME = 60;
    
    private ThreadPool mThreadPool;
    public TerminableThreadPool(){
        this(null);
    }
    public TerminableThreadPool(Runnable task){
        super(task);
        mThreadPool = ThreadPool.getInstance();
    }
    
    @Override
    protected void runTask(Runnable runnable) {
        mThreadPool.addTask(runnable);
    }
    
    public static void releaseRes(){
        ThreadPool.getInstance().releaseRes();
    }
    
    private static class ThreadPool extends AbsThreadPool{
        /* 单例 */
        private static ThreadPool sInstance;

        public static ThreadPool getInstance() {
            if (sInstance == null){
                synchronized (ThreadPool.class) {
                    if (sInstance == null) {
                        sInstance = new ThreadPool();
                    }
                }
            }
            return sInstance;
        }
        
        private ThreadPool(){
            
        }

        @Override
        protected int getCorePoolSize() {
            return CORE_POOL_SIZE;
        }

        @Override
        protected int getMaximumPoolSize() {
            return MAXIMUM_POOL_SIZE;
        }

        @Override
        protected long getKeepAliveTime() {
            return KEEP_ALIVE_TIME;
        }

        @Override
        protected TimeUnit getTimeUnit() {
            return TimeUnit.SECONDS;
        }

        @Override
        protected BlockingQueue<Runnable> newQueue() {
            return new LinkedBlockingQueue<Runnable>();
        }
    }
}
