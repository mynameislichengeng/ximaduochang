/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年3月15日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget;

import com.evideo.kmbox.R;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.QrGenerator;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * [功能说明]
 */
public class WxOrderSongSmallView extends LinearLayout{

    private ImageView mQrIv = null;
    private Bitmap mQrBmp = null;
    private int mQrWidth = 0;
    private int mQrHeight = 0;
    
    public WxOrderSongSmallView(Context context) {
        super(context);
    }

    public WxOrderSongSmallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.wx_ordersong_small_view, this);
        mQrIv = (ImageView)findViewById(R.id.phone_order_qrcode_img);
        mQrWidth = context.getResources().getDimensionPixelSize(R.dimen.px232);
        mQrHeight = context.getResources().getDimensionPixelSize(R.dimen.px232);
    }
    
    public void updateQR(String key) {
        EvLog.i("WX updateQR:" + key);
        if (!TextUtils.isEmpty(key)) {
            Bitmap bmp;
            try {
                bmp = QrGenerator.createQRImage(key, mQrWidth,mQrHeight,true);
                mQrIv.setImageBitmap(bmp);
                if (mQrBmp != null && !mQrBmp.isRecycled()) {
                    mQrBmp.recycle();
                    mQrBmp = null;
                }
                mQrBmp = bmp;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                mQrIv.setImageResource(R.drawable.wx_qr_code_default);
            }
        }else {
            mQrIv.setImageResource(R.drawable.wx_qr_code_default);
        }         
    }
}
