/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年7月18日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.common;

import com.evideo.kmbox.R;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * [功能说明]
 */
public class SelectDecodeDialog extends BaseDialog {

    private Button mOkBtn;
    private TextView mTitleTV;
    private TextView mContentTV;
    private View.OnClickListener mOkOnClickListener;
    private boolean dismissAfterBtnPressed = true;
    
    public SelectDecodeDialog(Context context) {
        super(context, R.style.ActivityDialogStyle);
        init(context);
    }
    
    public void setContent(String context) {
        if (mContentTV != null) {
            mContentTV.setText(context);
        }
    }
    
    public void setTitle(String context) {
        if (mTitleTV != null) {
            mTitleTV.setText(context);
        }
    }
    
    public void setOneOkButton(int okResId, View.OnClickListener okOnClickListener) {
        mOkOnClickListener = okOnClickListener;
        if(okResId <= 0) {
            okResId = R.string.confirm;
        }
        mOkBtn.setText(okResId);
//        mOkBtn.setVisibility(View.VISIBLE);
    }
    
    private void init(Context context) {
        setContentView(R.layout.dialog_select_decode_layout);
        getWindow().setBackgroundDrawableResource(R.drawable.toast_bg);
        
        mTitleTV = (TextView) findViewById(R.id.title_tv);
        mContentTV = (TextView) findViewById(R.id.content_tv);
       
        mOkBtn = (Button) findViewById(R.id.dialog_ok);
       
        
        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOkOnClickListener != null) {
                    mOkOnClickListener.onClick(v);
                }
                if(dismissAfterBtnPressed) {
                    dismiss();
                }
            }
        });
    }
    

}
