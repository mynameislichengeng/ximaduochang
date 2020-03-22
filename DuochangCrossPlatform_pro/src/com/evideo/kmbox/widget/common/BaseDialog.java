/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-5-5     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.widget.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

/**
 * [功能说明]
 */
public class BaseDialog extends Dialog {

    /**
     * @param context
     */
    public BaseDialog(Context context) {
        super(context);
    }
    
    public BaseDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show() {
        Context context = getContext();
        if(context instanceof Activity) {
            if(((Activity) context).isFinishing()) {
                return;
            }
        }
        super.show();
    }
    
    @Override
    public void dismiss() {
        Context context = getContext();
        if(context instanceof Activity) {
            if(((Activity) context).isFinishing()) {
                return;
            }
        }
        super.dismiss();
    }
    
    @Override
    public void cancel() {
        Context context = getContext();
        if(context instanceof Activity) {
            if(((Activity) context).isFinishing()) {
                return;
            }
        }
        super.cancel();
    }
    
}
