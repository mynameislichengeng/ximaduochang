/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年8月2日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.about;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.uploadlog.UploadLogPresenter;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.BaseDialog;
import com.evideo.kmbox.widget.common.MaskFocusButton;
import com.evideo.kmbox.widget.common.ToastUtil;

/**
 * [功能说明]
 */
public class ProblemDialog extends BaseDialog implements OnCheckedChangeListener, UploadLogPresenter.IUploadLogFileListener{

    private EditText mPhoneText = null;
    private CheckBox mCxChargeProblem = null;
    private CheckBox mCxCutsongProblem = null;
    private CheckBox mCxOtherProblem = null;
    private static final String PROBLEM_CHARGE = "chargeError";
    private static final String PROBLEM_CUT_SONG = "cutsongError";
    private static final String PROBLEM_OTHER = "otherError";
    private String mProblemType = PROBLEM_CHARGE;
    private MaskFocusButton mCommitProblemBtn = null;
    private String mUserPhoneNum = "";
    private UploadLogPresenter mUploadPresenter = null;
    private String defaultPhoneText = "";
    
    public ProblemDialog(Context context) {
        super(context, R.style.ActivityDialogStyle);
        setContentView(R.layout.dialog_help_problem_feedback);
        init();
    }
    
    private void init() {
        mCxChargeProblem = (CheckBox)findViewById(R.id.problem_charge_cx);
        mCxChargeProblem.setOnCheckedChangeListener(this);
        mCxCutsongProblem = (CheckBox)findViewById(R.id.problem_cut_song_cx);
        mCxCutsongProblem.setOnCheckedChangeListener(this);
        mCxOtherProblem = (CheckBox)findViewById(R.id.problem_others);
        mCxOtherProblem.setOnCheckedChangeListener(this);
        
        defaultPhoneText = BaseApplication.getInstance().getString(R.string.please_input_phonenum);
        mPhoneText = (EditText)findViewById(R.id.phone_num_edit_text);
        int color = BaseApplication.getInstance().getResources().getColor(R.color.edit_tx_bg);
        mPhoneText.setBackgroundColor(color);
        mPhoneText.setInputType(InputType.TYPE_CLASS_NUMBER); 
        mPhoneText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    String text = mPhoneText.getText().toString();
                    if (text.equals(defaultPhoneText)) {
                        mPhoneText.setText("");
                    }
                    InputMethodManager m=(InputMethodManager) BaseApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
                    m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
//                    mPhoneText.setInputType(InputType.TYPE_NULL); // 关闭软键盘
                    InputMethodManager imm = (InputMethodManager) BaseApplication.getInstance()  
                            .getSystemService(Context.INPUT_METHOD_SERVICE);  
                    imm.hideSoftInputFromWindow(mPhoneText.getWindowToken(), 0);  
                }
            }
        });
        mCommitProblemBtn = (MaskFocusButton)findViewById(R.id.problem_commit_btn);
        mCommitProblemBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (!mCxChargeProblem.isChecked() && !mCxCutsongProblem.isChecked() && !mCxOtherProblem.isChecked()) {
                    ToastUtil.showLongToast(BaseApplication.getInstance(), BaseApplication.getInstance().getResources().getString(R.string.problem_select_type));
                    return;
                }
                mUserPhoneNum = mPhoneText.getText().toString();
                if (TextUtils.isEmpty(mUserPhoneNum)) {
                    ToastUtil.showLongToast(BaseApplication.getInstance(), BaseApplication.getInstance().getResources().getString(R.string.problem_input_phone_num));
                    return;
                }
                if (mUserPhoneNum.length() < 11) {
                    ToastUtil.showLongToast(BaseApplication.getInstance(), BaseApplication.getInstance().getResources().getString(R.string.problem_input_phone_num_len_invalid));
                    return;
                }
                
                if (mUserPhoneNum.length() > 12) {
                    ToastUtil.showLongToast(BaseApplication.getInstance(), BaseApplication.getInstance().getResources().getString(R.string.problem_input_phone_num_len_too_long));
                    return;
                }
                
                Pattern p = Pattern.compile("[0-9]*");
                Matcher m = p.matcher(mUserPhoneNum);
                if (!m.matches()) {
                    ToastUtil.showLongToast(BaseApplication.getInstance(), BaseApplication.getInstance().getResources().getString(R.string.problem_input_phone_num_format_invalid));
                    return;
                } 
                
                if (mUploadPresenter != null) {
                    ToastUtil.showLongToast(BaseApplication.getInstance(), BaseApplication.getInstance().getResources().getString(R.string.problem_already_uploading_log));
                    return;
                }
                
                mUploadPresenter = new UploadLogPresenter();
                mUploadPresenter.setListener(ProblemDialog.this);
                mUploadPresenter.start(mUserPhoneNum,mProblemType);
                mCommitProblemBtn.setClickable(false);
                mCommitProblemBtn.setText(BaseApplication.getInstance().getResources().getString(R.string.problem_uploading_log));
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView,
            boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.problem_charge_cx) {
            if (isChecked) {
                mCxCutsongProblem.setChecked(false);
                mCxOtherProblem.setChecked(false);
                mProblemType = PROBLEM_CHARGE;
            }
        } else if (id == R.id.problem_cut_song_cx) {
            if (isChecked) {
                mCxChargeProblem.setChecked(false);
                mCxOtherProblem.setChecked(false);
                mProblemType = PROBLEM_CUT_SONG;
            }
        } else if (id == R.id.problem_others) {
            if (isChecked) {
                mCxChargeProblem.setChecked(false);
                mCxCutsongProblem.setChecked(false);
                mProblemType = PROBLEM_OTHER;
            }
        }
    }

    @Override
    public void onUploadLogSuccess() {
        EvLog.i(">>>>>>>>>>>onUploadLogSuccess");
        ToastUtil.showLongToast(BaseApplication.getInstance(), "上传日志成功");
        mUploadPresenter = null;
        mCommitProblemBtn.setText("上传日志");
        mCommitProblemBtn.setClickable(true);
        this.dismiss();
    }

    @Override
    public void onUploadLogFailed(int errorCode) {
        Context context = BaseApplication.getInstance();
        String errorMessage = context.getResources().getString(R.string.upload_log_failed_header);
        if (errorCode == UploadLogPresenter.ERROR_FTP_PARAMS) {
            errorMessage += context.getResources().getString(R.string.upload_log_error_ftp_params);
        } else if (errorCode == UploadLogPresenter.ERROR_SEND_FAILED) {
            errorMessage += context.getResources().getString(R.string.upload_log_error_send_failed);
        } else if (errorCode == UploadLogPresenter.ERROR_ZIP_FILE) {
            errorMessage += context.getResources().getString(R.string.upload_log_error_zip_failed);
        } else if (errorCode == UploadLogPresenter.ERROR_UPLOAD_FILE) {
            errorMessage += context.getResources().getString(R.string.upload_log_error_upload_failed);
        } else if (errorCode == UploadLogPresenter.ERROR_INPUT_PARAMS_INVALID) {
            errorMessage += context.getResources().getString(R.string.upload_log_error_invalid_input_params);
        } else {
            errorMessage += context.getResources().getString(R.string.dc_commu_failed);
        }
        ToastUtil.showLongToast(context, errorMessage);
        mUploadPresenter = null;
        mCommitProblemBtn.setText(context.getResources().getString(R.string.problem_commit));
        mCommitProblemBtn.setClickable(true);
        this.dismiss();
    }
    
    @Override
    public void show() {
        super.show();
        mCxCutsongProblem.setChecked(false);
        mCxOtherProblem.setChecked(false);
        mCxChargeProblem.setChecked(true);
        mCxChargeProblem.requestFocus();
        mPhoneText.setText(defaultPhoneText);
//        mPhoneText.setInputType(InputType.TYPE_NULL); // 关闭软键盘
    }
}

