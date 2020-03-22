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

package com.evideo.kmbox.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * [功能说明]使用lifo算法的线程池
 */
public class LifoTerminableThreadPool extends AbsTerminableThread {
    
    /** [核心线程数量] */
    private static final int CORE_POOL_SIZE = 2;
    
    /** [线程池中线程的最大允许数量] */
    private static final int MAXIMUM_POOL_SIZE = 20;
    
    /** [线程数超过核心线程数量时,会回收超过此参数定义的时间的空闲线程,单位秒] */
    private static final int KEEP_ALIVE_TIME = 60;
    
    private LifoThreadPool mLifoThreadPool;
    
    public LifoTerminableThreadPool() {
        this(null);
    }
    
    public LifoTerminableThreadPool(Runnable task) {
        super(task);
        mLifoThreadPool = LifoThreadPool.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runTask(Runnable runnable) {
        mLifoThreadPool.addTask(runnable);
    }
    
    /**
     * [功能说明]使用lifo算法的线程池
     * 单例
     */
    private static final class LifoThreadPool extends AbsThreadPool {
        
        private static LifoThreadPool sInstance;
        
        private LifoThreadPool() {
        }
        
        public static LifoThreadPool getInstance() {
            if (sInstance == null) {
                synchronized (LifoThreadPool.class) {
                    if (sInstance == null) {
                        sInstance = new LifoThreadPool();
                    }
                }
            }
            return sInstance;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected int getCorePoolSize() {
            return CORE_POOL_SIZE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected int getMaximumPoolSize() {
            return MAXIMUM_POOL_SIZE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected long getKeepAliveTime() {
            return KEEP_ALIVE_TIME;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected TimeUnit getTimeUnit() {
            return TimeUnit.SECONDS;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected BlockingQueue<Runnable> newQueue() {
            return new LifoLinkedBlockingDeque<Runnable>();
        }
        
    }

}
