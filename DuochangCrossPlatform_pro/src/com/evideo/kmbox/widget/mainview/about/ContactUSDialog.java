/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年8月4日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.about;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.BaseDialog;

/**
 * [功能说明]
 */
public class ContactUSDialog extends BaseDialog {

    private TextView mVersionInfo = null;

    public ContactUSDialog(Context context) {
        super(context, R.style.ActivityDialogStyle);
        setContentView(R.layout.dialog_help_contact_us);
        mVersionInfo = (TextView) findViewById(R.id.version_info);
        StringBuffer text = new StringBuffer(context.getResources().getString(
                R.string.contact_version_header));
        text.append(getPackageVersion());
        text.append(context.getResources()
                .getString(R.string.contact_sn_header));
        text.append(DeviceConfigManager.getInstance().getChipId());
        text.append(context.getResources().getString(
                R.string.contact_channel_header));
        text.append(DeviceConfigManager.getInstance().getChannelName());
        mVersionInfo.setText(text.toString());
    }

    private String getPackageVersion() {
        String version = "";
        try {
            version = BaseApplication
                    .getInstance()
                    .getPackageManager()
                    .getPackageInfo(
                            BaseApplication.getInstance().getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        return version;
    }
}
