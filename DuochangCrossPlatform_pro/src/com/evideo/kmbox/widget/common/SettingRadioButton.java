package com.evideo.kmbox.widget.common;

import com.evideo.kmbox.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class SettingRadioButton extends LinearLayout {
    
    private View mContentView;
    private RadioButton mRadioButton;
//    private View mRootLay;
    private TextView mDescTv;

    public SettingRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public SettingRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SettingRadioButton(Context context) {
        super(context);
        initView(context);
    }
    
    private void initView(Context context) {
        mContentView = View.inflate(context, R.layout.setting_radio_btn_lay, this);
        
        this.setFocusable(true);
        this.setClickable(true);
        this.setBackgroundResource(R.drawable.common_focused_selector);
        
//        mRootLay = mContentView.findViewById(R.id.root_lay);
        mRadioButton = (RadioButton) mContentView.findViewById(R.id.setting_rb);
        mDescTv = (TextView) mContentView.findViewById(R.id.desc_tv);
        
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mRadioButton.isChecked()) {
                    mRadioButton.setChecked(true);
                    if(mCustomCheckChangeListener != null) {
                        mCustomCheckChangeListener.onChecked();
                    }
                }
            }
        });
        
        this.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(mCustomKeyListener != null) {
                    return mCustomKeyListener.onKey(v, keyCode, event);
                }
                return false;
            }
        });
        
    }
    
    public void setDescription(String desc) {
        mDescTv.setText(desc);
    }
    
    public void setDescription(int resid) {
        if(resid > 0) {
            mDescTv.setText(resid);
        }
    }
    
    public void setDescColor(int color) {
        mDescTv.setTextColor(color);
    }
    
    public boolean isChecked() {
        return mRadioButton.isChecked();
    }
    
    public void setChecked(boolean checked) {
        mRadioButton.setChecked(checked);
    }
    
    private OnCustomCheckChangeListener mCustomCheckChangeListener;
    
    public void setOnCustomCheckChangeListener(OnCustomCheckChangeListener onCustomCheckChangeListener) {
        mCustomCheckChangeListener = onCustomCheckChangeListener;
    }
    
    public interface OnCustomCheckChangeListener {
        public void onChecked();
    }
    
    private OnCustomKeyListener mCustomKeyListener;
    
    public void setOnCustomKeyListener(OnCustomKeyListener onCustomKeyListener) {
        mCustomKeyListener = onCustomKeyListener;
    }
    
    public interface OnCustomKeyListener {
        public boolean onKey(View v, int keyCode, KeyEvent event);
    }

}
