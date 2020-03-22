/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年6月1日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.myspace;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evideo.kmbox.R;

/**
 * [功能说明]
 */
public class ProgressDialog extends Dialog {
    private Context mContext = null;
    private TextView mTitle = null;
    private ProgressBar mProgressBar = null;
    
    public ProgressDialog(Context context) {
//        super(context, R.style.QrDialogStyle);
        super(context, R.style.CommonDialogStyle);
        setContentView(R.layout.dialog_progress_lay);
        getWindow().setBackgroundDrawableResource(R.drawable.qr_dialog_bg);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int)context.getResources().getDimension(R.dimen.px800);
        lp.height = (int)context.getResources().getDimension(R.dimen.px400);
        getWindow().setAttributes(lp);
        mContext = context;
        init();
    }
    
    public void setWindowWidth(int dimenId) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int)mContext.getResources().getDimension(dimenId);
        getWindow().setAttributes(lp);
    }
    
    public void setWindowHeight(int dimenId) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.height = (int)mContext.getResources().getDimension(dimenId);
        getWindow().setAttributes(lp);
    }
    
    private void init() {
        mTitle = (TextView) findViewById(R.id.progress_title);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_state);
        this.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
                if (arg2.getAction() == KeyEvent.ACTION_DOWN) {
                    if (arg1 == KeyEvent.KEYCODE_BACK || arg1 == KeyEvent.KEYCODE_MENU || arg1 == KeyEvent.KEYCODE_HOME) {
                        return true;
                    }
                }
                return false;
            }
        });
    }
    
    public void setTitle(String title) {
        mTitle.setText(title);
    }
    
    public void updateProgress(int progress) {
//        mTitle.setText(mContext.getString(R.string.main_my_space_down_cloud_record_text,progress));
        mProgressBar.setProgress(progress);
    }
}
