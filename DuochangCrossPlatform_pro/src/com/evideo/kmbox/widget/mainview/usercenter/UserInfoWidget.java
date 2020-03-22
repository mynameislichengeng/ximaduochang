/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年8月31日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.usercenter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evideo.kmbox.R;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.util.TimeUtil;

/**
 * [功能说明]
 */
public class UserInfoWidget extends LinearLayout {
    private TextView mNickName = null;
    private TextView mVipTime = null;

    public UserInfoWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public UserInfoWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UserInfoWidget(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.widget_user_info, this);
        mNickName = (TextView) findViewById(R.id.nickname);
        mVipTime = (TextView) findViewById(R.id.vip_remain_time);
    }
    private static final long MINUTE_BY_ONE_DAY = 1440;

    public void updateVIPRemainTime(long time) {
        String formatTime = "";
        if (time < 0) {
            time = 0;
        }

        if (time < MINUTE_BY_ONE_DAY) {
            formatTime = TimeUtil.formatTimeByMinuteToHour(time);
        } else {
            formatTime = TimeUtil.formatTimeByMinuteToDay(time);
        }

        mVipTime.setText(formatTime);
    }

    public void updateUserInfo() {
        mNickName.setText(DeviceConfigManager.getInstance().getChipId());
        updateVIPRemainTime(DeviceConfigManager.getInstance().getRemainVipTime());
    }

}
