/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年3月17日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.about;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.util.FileUtil;
import com.evideo.kmbox.widget.common.CommonDialog;
import com.evideo.kmbox.widget.common.ToastUtil;
import com.evideo.kmbox.widget.mainview.AbsBaseView;
import com.evideo.kmbox.widget.mainview.MainViewId;

/**
 * [功能说明]
 */
public class HelpCenterView extends AbsBaseView implements View.OnClickListener{

    private SingleHelpBtn mHelpBtn = null;
    private SingleHelpBtn mDecodeSettingBtn = null;
    private SingleHelpBtn mDBSettingBtn = null;
    private SingleHelpBtn mNetCheckBtn = null;
    private SingleHelpBtn mProblemFeedBackBtn = null;
    private SingleHelpBtn mContactBtn = null;
    private ProblemDialog mProblemDialog = null;
    private DecodeSetDialog mDecodeDialog = null;
    private NetWorkCheckDialog mNetDialog = null;
    private ContactUSDialog mContactDialog = null;
    private HelpImgDialog mHelpImgDialog = null;
    private CommonDialog mResetDBDialog = null;
    
    public HelpCenterView(Activity activity, int backViewId) {
        super(activity, backViewId);
        initBtns();
    }

    private void initBtns() {
        Context context = BaseApplication.getInstance();
        mHelpBtn = (SingleHelpBtn)findViewById(R.id.help);
        mHelpBtn.setHelpBtnBg(R.drawable.btn_single_help_selector);
        mHelpBtn.setHelpTxt(context.getString(R.string.help_image_tx));
        mHelpBtn.setHelpIcon(R.drawable.help_ic_use);
        mHelpBtn.getBtn().setTag("helpImage");
        mHelpBtn.getBtn().setOnClickListener(this);
        
        mDecodeSettingBtn = (SingleHelpBtn)findViewById(R.id.decode_setting);
        mDecodeSettingBtn.setHelpBtnBg(R.drawable.btn_single_help_selector);
        mDecodeSettingBtn.setHelpTxt(context.getString(R.string.help_decode_tx));
        mDecodeSettingBtn.setHelpIcon(R.drawable.help_ic_decode);
        mDecodeSettingBtn.getBtn().setTag("decode");
        mDecodeSettingBtn.getBtn().setOnClickListener(this);
        
        mDBSettingBtn = (SingleHelpBtn)findViewById(R.id.db_setting);
        mDBSettingBtn.setHelpBtnBg(R.drawable.btn_single_help_selector);
        mDBSettingBtn.setHelpTxt(context.getString(R.string.help_db_tx));
        mDBSettingBtn.setHelpIcon(R.drawable.help_ic_db);
        mDBSettingBtn.getBtn().setTag("dbreset");
        mDBSettingBtn.getBtn().setOnClickListener(this);
        
        mNetCheckBtn = (SingleHelpBtn)findViewById(R.id.net_check);
        mNetCheckBtn.setHelpBtnBg(R.drawable.btn_single_help_selector);
        mNetCheckBtn.setHelpTxt(context.getString(R.string.help_net_tx));
        mNetCheckBtn.setHelpIcon(R.drawable.help_ic_net);
        mNetCheckBtn.getBtn().setTag("netcheck");
        mNetCheckBtn.getBtn().setOnClickListener(this);
        
        mProblemFeedBackBtn = (SingleHelpBtn)findViewById(R.id.problem_feedback);
        mProblemFeedBackBtn.setHelpBtnBg(R.drawable.btn_single_help_selector);
        mProblemFeedBackBtn.setHelpTxt(context.getString(R.string.help_problem_tx));
        mProblemFeedBackBtn.setHelpIcon(R.drawable.help_ic_contact);
        mProblemFeedBackBtn.getBtn().setTag("problem");
        mProblemFeedBackBtn.getBtn().setOnClickListener(this);
        
        mContactBtn = (SingleHelpBtn)findViewById(R.id.contact_us);
        mContactBtn.setHelpBtnBg(R.drawable.btn_single_help_selector);
        mContactBtn.setHelpTxt(context.getString(R.string.help_contact_tx));
        mContactBtn.setHelpIcon(R.drawable.help_ic_about_us);
        mContactBtn.getBtn().setTag("contact");
        mContactBtn.getBtn().setOnClickListener(this);
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHelpBtn.requestFocus();
    }
  
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mContactDialog != null) {
            mContactDialog.dismiss();
            mContactDialog = null;
        }
    }
    
    @Override
    protected int getLayResId() {
        return R.layout.main_help_center_view;
    }

    @Override
    protected int getViewId() {
        return MainViewId.ID_ABOUT;
    }

    @Override
    protected void resetFocus() {
    }
  
    
    @Override
    public void onClick(View v) {
        if (v.getTag().equals("problem")) {
            if (mProblemDialog == null) {
                mProblemDialog = new ProblemDialog(mActivity);
            }
            mProblemDialog.show();
        } else if (v.getTag().equals("dbreset")) {
            if (mResetDBDialog == null) {
                mResetDBDialog = new CommonDialog(mActivity);
//                int width = BaseApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.px1100);
//                mResetDBDialog.setDialogWidth(width);
            }
            mResetDBDialog.setTitle(-1);
            mResetDBDialog.setContent(BaseApplication.getInstance().getResources().getString(R.string.db_reset_confirm_tx));
            mResetDBDialog.setButton(R.string.confirm, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileUtil.deleteDir(ResourceSaverPathManager.getInstance().getLogSavePath());
                    FileUtil.deleteDir(ResourceSaverPathManager.getInstance().getResourceSavePath());
                    FileUtil.deleteDir(ResourceSaverPathManager.getInstance().getDBSavePath());
                    ToastUtil.showLongToast(mActivity, BaseApplication.getInstance().getString(R.string.db_reset_toast_tx));
                }
            }, R.string.cancel, null);
            mResetDBDialog.setOkBtnBg(R.drawable.btn_red_bg);
            mResetDBDialog.show();
        } else if (v.getTag().equals("decode")) {
            if (mDecodeDialog == null) {
                mDecodeDialog = new DecodeSetDialog(mActivity);
            }
            mDecodeDialog.show();
        } else if (v.getTag().equals("netcheck")) {
            if (mNetDialog == null) {
                mNetDialog = new NetWorkCheckDialog(mActivity);
            }
            mNetDialog.show();
        } else if (v.getTag().equals("contact")) {
            if (mContactDialog == null) {
                mContactDialog = new ContactUSDialog(mActivity);
            }
            mContactDialog.show();
        } else if (v.getTag().equals("helpImage")) {
            if (mHelpImgDialog == null) {
                mHelpImgDialog = new HelpImgDialog(mActivity);
            }
            mHelpImgDialog.show();
        }
    }

    @Override
    protected void clickExitKey() {
        
    }

    @Override
    public boolean onSmallMVUpKey() {
        if (mNetCheckBtn != null) {
            mNetCheckBtn.requestFocus();
            return true;
        }
        return false;
    }

    @Override
    public boolean onSmallMVRightKey() {
        return false;
    }

    @Override
    public boolean onStatusBarDownKey() {
        if (mHelpBtn != null) {
            mHelpBtn.requestFocus();
            return true;
        }
        return false;
    }
}
