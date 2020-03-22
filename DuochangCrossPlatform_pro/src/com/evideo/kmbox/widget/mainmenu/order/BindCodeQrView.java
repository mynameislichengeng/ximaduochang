/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date			Author		Version		Description
 *  -----------------------------------------------
 *  2015-8-10		"zhaoyunlong"		1.0		[修订说明]
 *
 */

package com.evideo.kmbox.widget.mainmenu.order;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.evideo.kmbox.R;
import com.evideo.kmbox.model.bmp.BmpCacheManager;

/**
 * [功能说明] 缺歌反馈二维码
 */
public class BindCodeQrView extends LinearLayout {
    private ImageView mQrIv;
    private int mQrPicSize = 0;

    public BindCodeQrView(Context context) {
        super(context);
        initView(context);
    }

    public BindCodeQrView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BindCodeQrView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }
    
    private void initView(Context context) {
        mQrPicSize = getResources().getDimensionPixelSize(R.dimen.px420);
        View view = View.inflate(context, R.layout.common_bindcode_lay, this);
        mQrIv = (ImageView) view.findViewById(R.id.qr);
    }
        
    public void updateQr() {
        Bitmap bmp = BmpCacheManager.getInstance().getLackSongQrBmp(mQrPicSize);
        if (bmp != null) {
            mQrIv.setImageBitmap(bmp);
        }
    }
}
