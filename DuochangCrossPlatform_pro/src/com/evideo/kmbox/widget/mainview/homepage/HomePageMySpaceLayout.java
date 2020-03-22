/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date			Author		Version		Description
 *  -----------------------------------------------
 *  2016-5-25		"zhaoyunlong"		1.0		[修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.homepage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.evideo.kmbox.R;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.MaskFocusAnimLinearLayout;
import com.evideo.kmbox.widget.msgview.GetAnnouncerCoverPresenter;

/**
 * [功能说明] 首页我的空间
 */
public class HomePageMySpaceLayout extends MaskFocusAnimLinearLayout {
    
    private Context mContext = null;
    
    /**
     * @param context
     * @param attrs
     */
    public HomePageMySpaceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private int getResId() {
        return R.layout.main_home_page_myspace_lay;
    }
    
    
    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(getResId(), this);
    }
    
    public void updateBg(Drawable draw) {
        if (draw == null) {
            return;
        }
        LinearLayout root = (LinearLayout) this.findViewById(R.id.home_page_my_space_bg);
        root.setBackground(draw);
    }
    
    public void updateBgByResId(int resId) {
        EvLog.i("updateBgByResId>>>>>>>>>>>>>>>>>>");
        LinearLayout root = (LinearLayout) this.findViewById(R.id.home_page_my_space_bg);
        root.setBackgroundResource(resId);
    }
    
}
