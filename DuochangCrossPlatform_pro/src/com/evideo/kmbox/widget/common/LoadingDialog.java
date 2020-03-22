package com.evideo.kmbox.widget.common;

import com.evideo.kmbox.R;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

public class LoadingDialog extends Dialog {
    
    private TextView mContentTv;

    public LoadingDialog(Context context) {
        super(context, R.style.CommonDialogStyle);
        setContentView(R.layout.dialog_loading_lay);
        getWindow().setBackgroundDrawableResource(R.drawable.qr_dialog_bg);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.0f;
        getWindow().setAttributes(lp);
        
        init();
    }
    
    private void init() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        mContentTv = (TextView) findViewById(R.id.loading_content_tv);
    }
    
    public void setContent(String content) {
        if(!TextUtils.isEmpty(content)) {
            mContentTv.setText(content);
        }
    }
    
    public void setContent(int resId) {
        if(resId > 0) {
            mContentTv.setText(resId);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            return true;
        }
        
        return super.onKeyDown(keyCode, event);
    }
    
    

}
