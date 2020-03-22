/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年11月23日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.common;

import com.evideo.kmbox.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * [功能说明]
 */
public class VerticalImageText extends LinearLayout {

    private TextView mText;
    private ImageView mImage;
    /**
     * @param context
     */
    public VerticalImageText(Context context) {
        super(context);
        init(context);
    }

    public VerticalImageText(Context context, AttributeSet attrs) {
        super(context,attrs);
        init(context);
    }
    
    private void init(Context context) {
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_vertical_image_text, this);
        mImage = (ImageView) findViewById(R.id.vertical_imagetext_image);
        mText =  (TextView) findViewById(R.id.vertical_imagetext_text);
    }
    
    public TextView getTextView() {
        return mText;
    }
    public ImageView getImageView() {
        return mImage;
    }
}
