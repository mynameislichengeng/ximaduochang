package com.evideo.kmbox.widget;

import com.evideo.kmbox.R;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;



/**
 * [功能说明]
 */
public class ContinuePayBtn extends FrameLayout {

    private Button mBtn = null;
    private TextView mTvProductName = null;
    private TextView mTvPriceNow = null;
    private TextView mTvPrice = null;
    private LinearLayout mLlyBg;
    
    public ContinuePayBtn(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public ContinuePayBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ContinuePayBtn(Context context) {
        super(context);
        initView(context);
    }
    
    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.widget_continue_pay_btn, this, true);
        mBtn = (Button)findViewById(R.id.btn_pay);
        mLlyBg = (LinearLayout) findViewById(R.id.lly_bg);
        mTvProductName = (TextView) findViewById(R.id.tv_product_name);
        mTvPriceNow = (TextView) findViewById(R.id.tv_price_now);
        mTvPrice = (TextView) findViewById(R.id.tv_price);
        mTvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG ); 
        mTvPrice.getPaint().setAntiAlias(true);
    }
    public void setBtnImageResource(int image) {
    	mLlyBg.setBackgroundResource(image);
	}
    public void setBtnBackground(int background) {
    	mBtn.setBackgroundResource(background);
    }
    public void setBtnClickListener(OnClickListener listener) {
    	mBtn.setOnClickListener(listener);
    }
    
    public void setFlag(int flag) {
    	mBtn.setTag(flag);
    }
    public void setBtnOnFocusChangeListener(OnFocusChangeListener listener){
    	mBtn.setOnFocusChangeListener(listener);
    }
    public void setProductNameText(String text) {
    	mTvProductName.setText(text);
	}
    public void setPriceNowText(String text) {
    	mTvPriceNow.setText(text);
	}
    public void setPriceText(String text) {
    	mTvPrice.setText(getResources().getString(R.string.huannet_price_hint,String.valueOf(text)));
	}
    public void setBtnPadding(int padding) {
    	mLlyBg.setPadding(padding, padding, padding, padding);
	}
}