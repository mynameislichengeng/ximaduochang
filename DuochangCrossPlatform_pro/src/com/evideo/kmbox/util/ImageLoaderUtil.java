/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date            Author          Version     Description
 *  -----------------------------------------------
 *  2015-3-13       "zhouxinghua"   1.0         [修订说明]
 *
 */

package com.evideo.kmbox.util;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * [ImageLoader工具类, 提供默认的ImageLoaderConfiguration和DisplayImageOptions]
 */
public final class ImageLoaderUtil {
    
    /** [配置-线程池线程数量] */
    private static final int CONFIG_THREAD_THREAD_POOL_SIZE = 3;
    
    /** [配置-线程优先级] */
    private static final int CONFIG_THREAD_THREAD_PRIORITY = 3;
    
    /** [配置-sdcard缓存大小] */
    private static final int CONFIG_DISK_CACHE_SIZE = 50 * 1024 * 1024;
    
    private ImageLoaderUtil() {
    }
    
    /**
     * [获取默认的ImageLoaderConfiguration]
     * 内存大小限制采用默认的最大运行内存的1/8
     * @param context context
     * @return ImageLoaderConfiguration
     */
    private static ImageLoaderConfiguration getDefaultImageLoaderConfiguration(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(CONFIG_THREAD_THREAD_POOL_SIZE)
                .threadPriority(CONFIG_THREAD_THREAD_PRIORITY)
                .diskCacheSize(CONFIG_DISK_CACHE_SIZE)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        return config;
    }
    
    /**
     * [初始化ImageLoader配置]
     * @param context context
     */
    public static void initImageLoaderConfiguration(Context context) {
        ImageLoaderConfiguration config = getDefaultImageLoaderConfiguration(context);
        ImageLoader.getInstance().init(config);
    }

}
