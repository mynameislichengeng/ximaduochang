/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年6月3日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.uploadlog;

import android.text.TextUtils;

import com.evideo.kmbox.exception.DataCenterCommuException;
import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.datacenter.proxy.data.DataCenterMessage;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;

import java.io.File;

/**
 * [功能说明]
 */
public class UploadLogPresenter extends AsyncPresenter<Boolean> {

    private int mErrorCode = ERROR_NONE;
    public static final int ERROR_NONE = 0;
    public static final int ERROR_SEND_FAILED = -1;
    public static final int ERROR_FTP_PARAMS = -2;
    public static final int ERROR_ZIP_FILE = -3;
    public static final int ERROR_UPLOAD_FILE = -4;
    public static final int ERROR_INPUT_PARAMS_INVALID = -5;

    private boolean isSuccess = false;

    public interface IUploadLogFileListener {
        public void onUploadLogSuccess();
        public void onUploadLogFailed(int errorCode);
    }
    private IUploadLogFileListener mListener = null;
    
    public void setListener(IUploadLogFileListener listener) {
        mListener = listener;
    }
    
    @Override
    protected Boolean doInBackground(Object... params) throws Exception {

        String phoneNum = (String)params[0];
        String problemType = (String)params[1];
        if (TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(problemType)) {
            mErrorCode = ERROR_INPUT_PARAMS_INVALID;
            return false;
        }
        DataCenterMessage msg = null;
        EvLog.d("begin to requestLogUploadInfo");
        try {
            msg = DCDomain.getInstance().requestLogUploadInfo();
        } catch (Exception e) {
            mErrorCode = ERROR_SEND_FAILED;
            return false;
        }
       
        EvLog.d("requestLogUploadInfo:" + msg.getContentString());

        String url = msg.get("url");
        String filename = msg.get("filename");
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(filename)) {
            mErrorCode = ERROR_FTP_PARAMS;
            return false;
        }

        String zipName = phoneNum + "-" + problemType + "-" + filename;
        FileUtil.doZip(ResourceSaverPathManager.getInstance().getLogSavePath(),
                zipName);
        String zipFile = FileUtil.concatPath(ResourceSaverPathManager
                .getInstance().getKmBoxPath(), zipName);
        if (!FileUtil.isFileExist(zipFile)) {
            EvLog.d(zipFile + " is not exist,can not upload to dc");
            mErrorCode = ERROR_ZIP_FILE;
            return false;
        }
        
        boolean isSucceed = sendLogFile(url,zipFile);
        FileUtil.deleteFile(zipFile);
        if (isSucceed) {
            mErrorCode = ERROR_NONE;
            String[] sourceStrArray=filename.split("-");
            if (sourceStrArray.length >= 3) {
                String date = sourceStrArray[2];
                EvLog.i("find date:" + date);
                try {
                    sendFeedback(phoneNum, problemType, date);
                } catch (Exception e) {
                    e.printStackTrace();
                    UmengAgentUtil.reportError("[bs_product_feedback-error], send failed");
                }
            } else {
                UmengAgentUtil.reportError("[bs_product_feedback-error], " + filename + " is not valid");
            }
            return true;
        } else {
            mErrorCode = ERROR_UPLOAD_FILE;
            return false;
        }
    }

    private void sendFeedback(String phoneNum,String error_type,String date) throws Exception {
        DataCenterMessage request = new DataCenterMessage();
        DataCenterMessage response = new DataCenterMessage();
        
        request.put("function", "product_feedback");
        request.put("phone_num", phoneNum);
        request.put("error_type", error_type);
        request.put("date", date);
        response = DCDomain.sendMessage(request);
        
        String errorCode = response.get("errorcode");
        if (!errorCode.equals("0")) {
            throw new DataCenterCommuException("product_feedback", errorCode);
        }
//        checkErrorCode(errorCode);

        return ;
    }

    private boolean sendLogFile(String url, String filename) {
        try {
            //异步访问服务器端
            SyncHttpClient client = new SyncHttpClient();
            client.addHeader("version-info", DeviceConfigManager.getInstance().getUserAgent());
            client.addHeader("sn",DeviceConfigManager.getInstance().getChipId());
            RequestParams params = new RequestParams();
            //上传文件
            params.put("file", new File(filename));
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    isSuccess = true;
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    isSuccess = false;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return isSuccess;
    }

    @Override
    protected void onCompleted(Boolean result, Object... params) {
        if (result != null) {
            if (result) {
                if (mListener != null) {
                    mListener.onUploadLogSuccess();
                }
            } else {
                if (mListener != null) {
                    mListener.onUploadLogFailed(mErrorCode);
                }
            }
        }
    }

    @Override
    protected void onFailed(Exception exception, Object... params) {
        if (mListener != null) {
            mListener.onUploadLogFailed(mErrorCode);
        }
    }
}

