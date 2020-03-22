package com.evideo.kmbox.widget.common;

import android.app.Activity;
import android.view.View.OnClickListener;

import com.evideo.kmbox.R;

/**
 * @brief : [通用dialog的工具类]
 */
public class DialogUtil {
    
    /**
     * @brief : [显示加载对话框]
     * @param activity
     * @param content 提示文字
     * @return
     */
    public static LoadingDialog getLoadingDialog(Activity activity, String content) {
        LoadingDialog dialog = new LoadingDialog(activity);
        dialog.setContent(content);
        return dialog;
    }
    
    /**
     * @brief : [显示加载对话框]
     * @param activity
     * @param resId 提示文字资源id
     * @return
     */
    public static LoadingDialog getLoadingDialog(Activity activity, int resId) {
        if(resId <= 0) {
            resId = R.string.loading_data;
        }
        return getLoadingDialog(activity, activity.getString(resId));
    }
    
    /**
     * 显示通用的对话框
     * @brief : [功能说明]
     * @param context
     * @param title
     * @param content
     * @param okResId
     * @param okOnClickListener
     * @param cancelResId
     * @param cancelOnClickListener
     * @return
     */
    public static CommonDialog showCommonDialog(Activity context, String title, String content, 
            int okResId, OnClickListener okOnClickListener, int cancelResId, OnClickListener cancelOnClickListener) {
        CommonDialog dialog = new CommonDialog(context);
        dialog.setTitle(title);
        dialog.setContent(content);
        dialog.setButton(okResId, okOnClickListener, cancelResId, cancelOnClickListener);
        if(!context.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }
    
    /**
     * 显示含标题的通用对话框
     * @brief : [功能说明]
     * @param context
     * @param title
     * @param content
     * @param okOnClickListener
     * @param cancelOnClickListener
     * @return
     */
    public static CommonDialog showCommonDialog(Activity context, String title, String content, OnClickListener okOnClickListener, OnClickListener cancelOnClickListener) {
        return showCommonDialog(context, title, content, -1, okOnClickListener, -1, cancelOnClickListener);
    }
    
    /**
     * 显示不带标题的通用对话框
     * @brief : [功能说明]
     * @param context
     * @param content
     * @param okResId
     * @param okOnClickListener
     * @param cancelResId
     * @param cancelOnClickListener
     * @return
     */
    public static CommonDialog showCommonDialog(Activity context, String content, 
            int okResId, OnClickListener okOnClickListener, int cancelResId, OnClickListener cancelOnClickListener ) {
        return showCommonDialog(context, null, content, okResId, okOnClickListener, cancelResId, cancelOnClickListener);
    }
    
    /**
     * 显示不带标题的通用对话框
     * @brief : [功能说明]
     * @param context
     * @param content
     * @param okOnClickListener
     * @param cancelOnClickListener
     * @return
     */
    public static CommonDialog showCommonDialog(Activity context, String content, OnClickListener okOnClickListener, OnClickListener cancelOnClickListener) {
        return showCommonDialog(context, content, -1, okOnClickListener, -1, cancelOnClickListener);
    }
    
    /**
     * 显示不带标题的通用对话框
     * @brief : [功能说明]
     * @param context
     * @param contentResId
     * @param okResId
     * @param okOnClickListener
     * @param cancelResId
     * @param cancelOnClickListener
     * @return
     */
    public static CommonDialog showCommonDialog(Activity context, int contentResId,
            int okResId, OnClickListener okOnClickListener, int cancelResId, OnClickListener cancelOnClickListener) {
        CommonDialog dialog = showCommonDialog(context, null, okResId, okOnClickListener, cancelResId, cancelOnClickListener);
        dialog.setContent(contentResId);
        return dialog;
    }
    
    /**
     * 显示不带标题的通用对话框
     * @brief : [功能说明]
     * @param context
     * @param contentResId
     * @param okOnClickListener
     * @param cancelOnClickListener
     * @return
     */
    public static CommonDialog showCommonDialog(Activity context, int contentResId, 
            OnClickListener okOnClickListener, OnClickListener cancelOnClickListener) {
        return showCommonDialog(context, contentResId, -1, okOnClickListener, -1, cancelOnClickListener);
    }
    
    /**
     * @brief : [显示只有一个确认按钮的通用对话框，可以设置标题和内容，按钮的文字和点击事件]
     * @param context
     * @param title
     * @param content
     * @param okResId
     * @param okOnClickListener
     * @return
     */
    public static CommonDialog showOneOkBtnDialog(Activity context, String title, String content
            , int okResId, OnClickListener okOnClickListener) {
        CommonDialog dialog = new CommonDialog(context);
        dialog.setTitle(title);
        dialog.setContent(content);
        dialog.setOneOkButton(okResId, okOnClickListener);
        if(!context.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }
    
    /**
     * @brief : [显示一个不带标题的只有一个确认按钮的通用对话框]
     * @param context
     * @param content
     * @param okResId
     * @param okOnClickListener
     * @return
     */
    public static CommonDialog showOneOkBtnDialog(Activity context, String content
            , int okResId, OnClickListener okOnClickListener) {
        return showOneOkBtnDialog(context, null, content, okResId, okOnClickListener);
    }
    
    /**
     * @brief : [显示一个不带标题的只有一个确认按钮的通用对话框]
     * @param context
     * @param content
     * @param okOnClickListener
     * @return
     */
    public static CommonDialog showOneOkBtnDialog(Activity context, String content
            ,OnClickListener okOnClickListener) {
        return showOneOkBtnDialog(context, content, -1, okOnClickListener);
    }
    
    /**
     * @brief : [显示只有一个取消按钮的通用对话框，可以设置标题和内容，按钮的文字和点击事件]
     * @param context
     * @param title
     * @param content
     * @param cancelResId
     * @param cancelOnClickListener
     * @return
     */
    public static CommonDialog showOneCancelBtnDialog(Activity context, String title, String content,
            int cancelResId, OnClickListener cancelOnClickListener) {
        CommonDialog dialog = new CommonDialog(context);
        dialog.setTitle(title);
        dialog.setContent(content);
        dialog.setOneCancelButton(cancelResId, cancelOnClickListener);
        if(!context.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }
    
    /**
     * @brief : [显示一个不带标题的只有一个取消按钮的通用对话框]
     * @param context
     * @param content
     * @param cancelResId
     * @param cancelOnClickListener
     * @return
     */
    public static CommonDialog showOneCancelBtnDialog(Activity context, String content
            ,int cancelResId, OnClickListener cancelOnClickListener) {
        return showOneCancelBtnDialog(context, null, content, cancelResId, cancelOnClickListener);
    }
    
    /**
     * @brief : [显示一个不带标题的只有一个取消按钮的通用对话框]
     * @param context
     * @param content
     * @param cancelOnClickListener
     * @return
     */
    public static CommonDialog showOneCancelBtnDialog(Activity context, String content
            ,OnClickListener cancelOnClickListener) {
        return showOneCancelBtnDialog(context, content, -1, cancelOnClickListener);
    }
    
    /**
     * @brief : [显示一个不带标题的含有两个确认按钮的通用对话框]
     * @param context
     * @param content
     * @param leftResId
     * @param leftOnClickListener
     * @param rightResId
     * @param rightOnClickListener
     * @return
     */
    public static CommonDialog showTwoConfirmBtnDialog(Activity context, String content
            , int leftResId, OnClickListener leftOnClickListener
            , int rightResId, OnClickListener rightOnClickListener) {
        CommonDialog dialog = new CommonDialog(context);
        dialog.setTitle(-1);
        dialog.setContent(content);
        dialog.setTwoConfirmButton(leftResId, leftOnClickListener, rightResId, rightOnClickListener);
        return dialog;
    }
    
    /**
     * @brief : [显示一个不带标题的含有两个确认按钮的通用对话框]
     * @param context
     * @param contentResId
     * @param leftResId
     * @param leftOnClickListener
     * @param rightResId
     * @param rightOnClickListener
     * @return
     */
    public static CommonDialog showTwoConfirmBtnDialog(Activity context, int contentResId
            , int leftResId, OnClickListener leftOnClickListener
            , int rightResId, OnClickListener rightOnClickListener) {
        CommonDialog dialog = new CommonDialog(context);
        dialog.setTitle(-1);
        dialog.setContent(contentResId);
        dialog.setTwoConfirmButton(leftResId, leftOnClickListener, rightResId, rightOnClickListener);
        return dialog;
    }
    

    /**
     * [功能说明] 显示设备检查弹出框
     * @param context
     * @param title 设备名称
     * @param listener
     * @return
     */
   /* public static TvAlertDialog showTvAlertDialog(Context context, String title,OnUsbClickListener listener) {
        TvAlertDialog dialog = new TvAlertDialog(context);
        dialog.setUsbClickListener(listener);
        dialog.setDeviceTitle(title);
        dialog.show();

        return dialog;
    }*/
    
}
