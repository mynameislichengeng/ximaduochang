/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年4月19日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget;



import com.evideo.kmbox.R;

import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * [功能说明]
 */
public class CommonPayBtn extends FrameLayout {

    private Button mBtn = null;
    private TextView mFirstLineTx = null;
    private TextView mSecondLineYuanTx = null;
    private TextView mSecondLineNowTx = null;
    
    public CommonPayBtn(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public CommonPayBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CommonPayBtn(Context context) {
        super(context);
        initView(context);
    }
    
    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.widget_common_pay_btn, this, true);
        mBtn = (Button)findViewById(R.id.btn_pay);
        mFirstLineTx = (TextView)findViewById(R.id.first_line_tx);
        mSecondLineYuanTx = (TextView)findViewById(R.id.second_line_yuan_tx);
        mSecondLineYuanTx.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG ); 
        mSecondLineYuanTx.getPaint().setAntiAlias(true);
        mSecondLineNowTx = (TextView)findViewById(R.id.second_line_now_tx);
    }

    public void setFirstLineText(String text) {
        mFirstLineTx.setText(text);
    }
    
    public void setSecondLineYuanText(String text) {
        mSecondLineYuanTx.setText(getResources().getString(R.string.huannet_charge_price_hint,String.valueOf(text)));
    }
    
    //兑换码专用通道
    public void setSecondLineText(String text){
    	mSecondLineNowTx.setText(text);
    }
    
    public void setSecondLineNowText(String text) {
        String newText = getResources().getString(R.string.huannet_charge_now_price_hint,String.valueOf(text));
        mSecondLineNowTx.setText(Html.fromHtml(newText));
    }
    
    public void setBtnClickListener(View.OnClickListener listener) {
        mBtn.setOnClickListener(listener);
    }
    
    public void setFlag(int flag) {
        mBtn.setTag(flag);
    }
}
