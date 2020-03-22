/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-10-5     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mv.selected;

import android.app.Activity;
import android.view.View;

/**
 * [功能说明]MV已点界面管理者
 */
public final class MvSelectedManager {
    
    private MvSelectedView mMvSelectedView;
    
    private static MvSelectedManager sInstance;
    
    private MvSelectedManager() {
    }
    
    /**
     * [功能说明]获取MV已点界面管理者实例
     * @return
     */
   /* public static MvSelectedManager getInstance() {
        if (sInstance == null) {
            synchronized (MvSelectedManager.class) {
                if (sInstance == null) {
                    sInstance = new MvSelectedManager();
                }
            }
        }
        return sInstance;
    }*/
    
    /**
     * [功能说明]初始化mv已点界面
     * @param activity
     * @return mv已点界面
     */
    public View init(Activity activity) {
        mMvSelectedView = new MvSelectedView(activity);
        return mMvSelectedView;
    }
    
    /**
     * [功能说明]显示mv已点界面
     */
    public void showMvSelectedView() {
        if (mMvSelectedView != null) {
            mMvSelectedView.show();
        }
    }
    
    /**
     * [功能说明]隐藏mv已点界面
     */
    public void hideMvSelectedView() {
        if (mMvSelectedView != null) {
            mMvSelectedView.hide();
        }
    }
    
    /**
     * [功能说明]mv已点界面是否可见
     * @return
     */
    public boolean isVisible() {
        if (mMvSelectedView != null) {
            return mMvSelectedView.isVisible();
        }
        return false;
    }

}
