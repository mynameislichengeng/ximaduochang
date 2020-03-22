/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年8月23日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.huodong;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.evideo.kmbox.R;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.BaseDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * [功能说明]
 */
public class BmpDialog extends BaseDialog{
    private Context mContext = null;
    private ImageView mIv = null;
    private String mUrl = "";
//    private DisplayImageOptions mOptions = null;
    private Bitmap mBmp = null;
    
    public BmpDialog(Context context) {
        super(context, R.style.ActivityDialogStyle);
        setContentView(R.layout.dialog_bmp_online);
        mContext = context;
        init();
    }
    
    private void init() {
        /*mOptions = new DisplayImageOptions.Builder()
        .cacheInMemory(false)
        .cacheOnDisk(true)
        .build();*/
        mIv = (ImageView)findViewById(R.id.bmp_online_iv);
        mIv.setImageBitmap(null);
    }
    
    public void setBmpUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        mUrl = url;
        EvLog.e("setBmpUrl mUrl:" + mUrl);
    }
    
    public void setBmpResId(int resId) {
        mIv.setImageResource(resId);
    }
    
    @Override
    public void dismiss() {
        if (mBmp != null && !mBmp.isRecycled()) {
            mBmp.recycle();
            mBmp = null;
        }
        mIv.setImageBitmap(null);
        super.dismiss();
    }
    
    @Override
    public void show() {
        super.show();
        if (TextUtils.isEmpty(mUrl)) {
            return;
        }
        ImageLoader.getInstance().loadImage(mUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                updateBmp(loadedImage);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                updateBmp(null);
            }
        });
    }
    
    private void updateBmp(Bitmap loadedImage) {
       /* if (loadedImage == null) {
            loadedImage = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.km_qr_code_connect_bg);
        }*/
        mIv.setImageBitmap(loadedImage);
        if (mBmp != null && !mBmp.isRecycled()) {
            mBmp.recycle();
            mBmp = null;
        }
        mBmp = loadedImage;
    }
}
