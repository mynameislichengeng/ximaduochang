package com.evideo.kmbox.widget;

import com.evideo.kmbox.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class LoadingView extends FrameLayout {
    private TextView mContentTv;

    public LoadingView(Context context) {
        super(context);
        
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.launcher_main_loading, this, true);
        
        initView();
    }
    
    private void initView() {
        mContentTv = (TextView) findViewById(R.id.loading_content_tv);
        setVisibility(View.GONE);
    }
    
    public void showLoadingView(int resid) {
        if(resid > 0) {
            mContentTv.setText(resid);
        }
        setVisibility(View.VISIBLE);
    }
    
    public void showLoadingView(String content) {
        if(!TextUtils.isEmpty(content)) {
            mContentTv.setText(content);
        }
        setVisibility(View.VISIBLE);
    }
    
    public String getLoadingViewText() {
        if (mContentTv != null) {
            return (String) mContentTv.getText();
        }
        return "";
    }
    public void dismissLoadingView() {
        setVisibility(View.GONE);
    }

}
