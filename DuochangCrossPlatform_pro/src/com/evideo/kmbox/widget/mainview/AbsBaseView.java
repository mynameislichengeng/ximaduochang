/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-9-15     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview;

import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
import com.evideo.kmbox.widget.intonation.ISmallMVKeyListener;
import com.evideo.kmbox.widget.intonation.IStatusBarDownKeyListener;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 * [功能说明]主界面的基类
 */
public abstract class AbsBaseView extends FrameLayout implements ISmallMVKeyListener,IStatusBarDownKeyListener{
    
    // TODO view id 
    
    protected Activity mActivity;
    
    private String mPageName;
    
    protected int mBackViewId;
    
    private boolean mNeedResetFocus = true;
    
    public void disableNeedResetFocus() {
        mNeedResetFocus = false;
    }
    
    public void enableNeedResetFocus() {
        mNeedResetFocus = true;
    }
    
    public void setBackViewId(int backViewId) {
        mBackViewId = backViewId;
    }
    
    public AbsBaseView(Activity activity, int backViewId) {
        super(activity);
        mActivity = activity;
        mBackViewId = backViewId;
        LayoutInflater inflater = LayoutInflater.from(activity);
        inflater.inflate(getLayResId(), this, true);
    }
    
    /**
     * [功能说明]获取父view的id，用于指向返回哪个界面
     * @return 父view的id
     */
    public int getBackViewId() {
        return mBackViewId;
    }
    
    protected abstract void clickExitKey();
    /**
     * [实现此方法返回布局文件的id]
     * @return 布局文件的id
     */
    protected abstract int getLayResId();
    
    /**
     * [功能说明]获取界面的id
     * @return 界面id
     * @see MainViewId
     */
    protected abstract int getViewId();
    
    /**
     * [功能说明]重置焦点
     */
    protected abstract void resetFocus();
    
    /**
     * [设置页面名称--友盟相关]
     * @param pageName 页面名称
     */
    public void setPageName(String pageName) {
        mPageName = pageName;
    }
    
    public String getPageName() {
        return mPageName;
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mNeedResetFocus) {
            resetFocus();
        }
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.requestChildFocus(null, null);
        super.onDetachedFromWindow();
    }

    /**
     * [是否可见]
     * @return true 可见的  false 不可见的
     */
    public boolean isVisible() {
        return getVisibility() == View.VISIBLE;
    }
    
    /**
     * @brief : [页面统计，页面开始]
     */
    protected void onPageStart() {
        if (!TextUtils.isEmpty(mPageName)) {
//            UmengAgent.getInstance().onPageStart(mPageName);
            LogAnalyzeManager.getInstance().onPageStart(mPageName);
        }
    }
    
    /**
     * @brief : [页面统计，页面结束]
     */
    protected void onPageEnd() {
        if (!TextUtils.isEmpty(mPageName)) {
//            UmengAgent.getInstance().onPageEnd(mPageName);
            LogAnalyzeManager.getInstance().onPageEnd(mPageName);
        }
    }
    
    /**
     * [Return a localized string from the application's package's default string table.]
     * @param resId Resource id for the string
     * @return localized string
     */
    protected String getString(int resId) {
        return getContext().getString(resId);
    }
    
    /**
     * Return a localized formatted string from the application's package's
     * default string table, substituting the format arguments as defined in
     * {@link java.util.Formatter} and {@link java.lang.String#format}.
     * @param resId Resource id for the format string
     * @param formatArgs The format arguments that will be used for substitution.
     * @return localized formatted string
     */
    protected String getString(int resId, Object... formatArgs) {
        return getContext().getString(resId, formatArgs);
    }

}
