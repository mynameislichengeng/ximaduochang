/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年11月17日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.huodong;

import com.evideo.kmbox.R;
import com.evideo.kmbox.util.BitmapUtil;
import com.evideo.kmbox.widget.common.BaseDialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

/**
 * [功能说明]
 */
public class HuodongDialog extends BaseDialog{
//    private Context mContext = null;
    private Bitmap mBitmap = null;
    private ImageView mIv = null;
    /**
     * @param context
     */
    public HuodongDialog(Context context) {
        super(context, R.style.ActivityDialogStyle);
        setContentView(R.layout.dialog_activity);
//        mContext = context;
        mIv  = (ImageView)findViewById(R.id.huodong_big);
    }
    
    public void setDrawable(Drawable drawable) {
        if (drawable != null && mIv != null) {
            mIv.setImageDrawable(drawable);
        }
    }
    
    public void setBmp(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Bitmap bmp = BitmapUtil.getBmpByPath(path);
        if (bmp == null) {
            return;
        }
        mIv.setImageBitmap(bmp);
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mBitmap = bmp;
    }
}
