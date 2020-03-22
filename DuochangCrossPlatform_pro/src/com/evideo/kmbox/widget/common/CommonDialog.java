package com.evideo.kmbox.widget.common;

import com.evideo.kmbox.R;
import com.evideo.kmbox.util.DimensionsUtil;
import com.evideo.kmbox.util.UIKit;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @brief : [应用通用的dialog]
 */
public class CommonDialog extends BaseDialog {
    
    private Button mOkBtn;
    private Button mCancelBtn;
    private View mBtnGap;
    private View mTitleGap;
    private TextView mTitleTV;
    private TextView mContentTV;
    private FrameLayout mContentLay;
    private View.OnClickListener mOkOnClickListener;
    private View.OnClickListener mCancelOnClickListener;
    private boolean dismissAfterBtnPressed = true;
    
    private boolean isDimStyle = true;
    
    public boolean isDimStyle() {
        return isDimStyle;
    }

    /**
     * @brief : [设置dialog显示时背景是否变暗]
     * @param isDimStyle
     */
    public void setDimStyle(boolean isDimStyle) {
        this.isDimStyle = isDimStyle;
    }

    /**
     * 设置按钮按下后对话框是否要消失
     * @brief : [功能说明]
     * @param dismissAfterBtnPressed
     */
    public void setDismissAfterBtnPressed(boolean dismissAfterBtnPressed) {
        this.dismissAfterBtnPressed = dismissAfterBtnPressed;
    }

    public CommonDialog(Context context) {
        super(context, R.style.CommonDialogStyle);
        init(context);
    }
    
    private void init(Context context) {
        setContentView(R.layout.dialog_common_layout);
        getWindow().setBackgroundDrawableResource(R.drawable.toast_bg);
        
        mTitleTV = (TextView) findViewById(R.id.dialog_title_tv);
        mContentTV = (TextView) findViewById(R.id.dialog_content_tv);
        mContentLay = (FrameLayout) findViewById(R.id.dialog_content_layout);
        mOkBtn = (Button) findViewById(R.id.dialog_ok);
        mCancelBtn = (Button) findViewById(R.id.dialog_cancel);
        mTitleGap = findViewById(R.id.dialog_title_top_gap);
        mBtnGap = findViewById(R.id.dialog_btn_gap);
        
        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOkOnClickListener != null) {
                    mOkOnClickListener.onClick(v);
                }
                if(dismissAfterBtnPressed) {
                    dismiss();
                }
            }
        });
        
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCancelOnClickListener != null) {
                    mCancelOnClickListener.onClick(v);
                }
                if(dismissAfterBtnPressed) {
                    dismiss();
                }
            }
        });
    }
    
    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = isDimStyle ? 0.5f : 0.0f;
        getWindow().setAttributes(lp);
    }
    
    public void setDialogWidth(float width) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = DimensionsUtil.DIPToPX(getContext(), width);
        getWindow().setAttributes(lp);
    }
    
    /**
     * 设置标题
     * @param resId
     */
    @Override
    public void setTitle(int resId){
        if(resId <= 0){
            mTitleTV.setVisibility(View.GONE);
        } else {
            mTitleTV.setVisibility(View.VISIBLE);
            mTitleTV.setText(resId);
        }
    }
    
    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title){
        if(TextUtils.isEmpty(title)){
            mTitleTV.setVisibility(View.GONE);
        } else {
            mTitleTV.setVisibility(View.VISIBLE);
            mTitleTV.setText(title);
        }
    }
    
    /**
     * 设置文字内容
     * @param resId
     */
    public void setContent(int resId){
        if(resId <= 0) {
            return;
        }
        mContentTV.setText(resId);
        resetContentGravity();
        mContentLay.setVisibility(View.GONE);
    }
    
    /**
     * 设置文字内容
     * @param content
     */
    public void setContent(String content) {
        mContentTV.setText(content);
        resetContentGravity();
        mContentLay.setVisibility(View.GONE);
    }
    
    private void resetContentGravity(){
//        String str = mContentTV.getText().toString().trim();
//        if (!TextUtils.isEmpty(str)) {
//            if (str.length() <= 10) {
//                mContentTV.setGravity(Gravity.CENTER);
//            } else {
//                mContentTV.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
//            }
//        }
    }
    
    /**
     * 设置对话框内容
     * @param view
     */
    public void setContentLayout(View view){
        if(view != null){
            setBtnVisible(false);
            mContentLay.removeAllViews();
            mContentLay.addView(view);
            mContentLay.setVisibility(View.VISIBLE);
            mContentTV.setVisibility(View.GONE);
            mTitleGap.setVisibility(View.GONE);
        }
    }
    
    /**
     * 设置是否显示按钮
     * @param visible
     */
    public void setBtnVisible(boolean visible) {
        if(visible) {
            findViewById(R.id.dialog_btn_lay).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.dialog_btn_lay).setVisibility(View.GONE);
        }
    }
    
    /**
     * @brief : [设置按钮文字和点击事件]
     * @param okResId
     * @param okOnClickListener
     * @param cancelResId
     * @param cancelOnClickListener
     */
    public void setButton(int okResId, View.OnClickListener okOnClickListener,
            int cancelResId, View.OnClickListener cancelOnClickListener) {
        mOkOnClickListener = okOnClickListener;
        mCancelOnClickListener = cancelOnClickListener;
        if(okResId <= 0) {
            okResId = R.string.confirm;
        }
        if(cancelResId <= 0) {
            cancelResId = R.string.cancel;
        }
        mOkBtn.setText(okResId);
        mCancelBtn.setText(cancelResId);
    }
    
    /**
     * @brief : [设置只有一个确认按钮的文字和点击事件]
     * @param okResId
     * @param okOnClickListener
     */
    public void setOneOkButton(int okResId, View.OnClickListener okOnClickListener) {
        mOkOnClickListener = okOnClickListener;
        if(okResId <= 0) {
            okResId = R.string.confirm;
        }
        mOkBtn.setText(okResId);
        mOkBtn.setVisibility(View.VISIBLE);
        mCancelBtn.setVisibility(View.GONE);
        mBtnGap.setVisibility(View.GONE);
    }
    
    /**
     * @brief : [设置只有一个确认按钮的文字和点击事件]
     * @param cancelResId
     * @param cancelOnClickListener
     */
    public void setOneCancelButton(int cancelResId, View.OnClickListener cancelOnClickListener) {
        mCancelOnClickListener = cancelOnClickListener;
        if(cancelResId <= 0) {
            cancelResId = R.string.cancel;
        }
        mCancelBtn.setText(cancelResId);
        mCancelBtn.setVisibility(View.VISIBLE);
        mOkBtn.setVisibility(View.GONE);
        mBtnGap.setVisibility(View.GONE);
    }
    
    /**
     * @brief : [设置两个确认按钮的文字和点击事件]
     * @param leftResId
     * @param leftOnClickListener
     * @param rightResId
     * @param rightOnClickListener
     */
    public void setTwoConfirmButton(int leftResId, View.OnClickListener leftOnClickListener
            , int rightResId, View.OnClickListener rightOnClickListener) {
        mCancelOnClickListener = leftOnClickListener;
        mOkOnClickListener = rightOnClickListener;
        UIKit.setBackgroundResource(mCancelBtn, R.drawable.btn_green_bg);
        mCancelBtn.setText(leftResId);
        mOkBtn.setText(rightResId);
    }
    
    /**
     * @brief : [设置两个确认按钮的文字和点击事件]
     * @param left
     * @param leftOnClickListener
     * @param right
     * @param rightOnClickListener
     */
    public void setTwoConfirmButton(String left, View.OnClickListener leftOnClickListener
            , String right, View.OnClickListener rightOnClickListener) {
        mCancelOnClickListener = leftOnClickListener;
        mOkOnClickListener = rightOnClickListener;
        UIKit.setBackgroundResource(mCancelBtn, R.drawable.btn_green_bg);
        mCancelBtn.setText(left);
        mOkBtn.setText(right);
    }
    
    /**
     * @brief : [设置确认按钮文字]
     * @param resid
     */
    public void setOkText(int resid) {
        if(resid > 0) {
            mOkBtn.setText(resid);
        }
    }
    
    /**
     * @brief : [设置取消按钮文字]
     * @param resid
     */
    public void setCancelText(int resid) {
        if(resid > 0) {
            mCancelBtn.setText(resid);
        }
    }
    
    /**
     * @brief : [设置确认按钮背景]
     * @param resid
     */
    public void setOkBtnBg(int resid) {
        if(resid > 0) {
            UIKit.setBackgroundResource(mOkBtn, resid);
        }
    }
    
    /**
     * @brief : [设置取消按钮背景]
     * @param resid
     */
    public void setCancelBtnBg(int resid) {
        if(resid > 0) {
            UIKit.setBackgroundResource(mCancelBtn, resid);
        }
    }

}
