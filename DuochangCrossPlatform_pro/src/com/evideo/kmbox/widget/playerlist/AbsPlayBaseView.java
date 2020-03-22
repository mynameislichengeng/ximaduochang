package com.evideo.kmbox.widget.playerlist;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

/**
 * [已点已唱视图基类]
 */
public abstract class AbsPlayBaseView extends LinearLayout {
    
    protected Activity mActivity;
    
    public AbsPlayBaseView(Activity context) {
        super(context);
        mActivity = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(getLayResId(), this, true);
    }
   
    /**
     * [实现此方法返回布局文件的id]
     * @return 返回布局文件ID
     */
    protected abstract int getLayResId();
    
    /**
     * [更新焦点]
     * @param direction 方向
     * @return true:成功;false：失败
     */
    public  boolean updateFocus(int direction) {
        return false; 
    };
}
