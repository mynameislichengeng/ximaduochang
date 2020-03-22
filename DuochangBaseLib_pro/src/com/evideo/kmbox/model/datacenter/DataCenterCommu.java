package com.evideo.kmbox.model.datacenter;

import android.text.TextUtils;
import android.util.Log;

import com.evideo.kmbox.exception.DataCenterCommuException;
import com.evideo.kmbox.model.datacenter.proxy.data.DataCenterMessage;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.util.EvLog;
//import com.evideo.kmbox.util.NetUtils;

// TODO: 依赖了BaseApplication，以获取数据中心uri等字符串定义
public class DataCenterCommu {


    private String mRequestURI = "";
    private String mValidatecode = "";
    private String mLoginUri = "";
    private String mRemainVipTime = "";
    private String mCode = "";
    private String mPicurhead = "";
    private String  userid, stdid, areacode, stbtype, usertoken;

    private boolean isLogin = false;

    public boolean isLoginSuccess() {
        return isLogin;
    }

    private DataCenterCommu() {

    }

    private static DataCenterCommu instance = null;

    public static DataCenterCommu getInstance() {
        if (instance == null) {
            synchronized (DataCenterCommu.class) {
                DataCenterCommu temp = instance;
                if (temp == null) {
                    temp = new DataCenterCommu();
                    instance = temp;
                }
            }
        }
        return instance;
    }

    public void setLoginURI(String uri) {
        mValidatecode = "";//登出
        mRequestURI = "";
        mLoginUri = uri;
    }

    public String getLoginURI() {
        return mLoginUri;
    }

    private boolean mHasCallFirstLogin = false;

    public synchronized DataCenterMessage sendMessage(DataCenterMessage request) throws Exception {

        DataCenterMessage response = new DataCenterMessage();

        try {
            EvLog.d("-------------------------------- mValidatecode=" + mValidatecode);
            if (TextUtils.isEmpty(mValidatecode)) {
                login();

            }
            response = this.request(request, mRequestURI);
            EvLog.d("datacenter mRequestURI is:" + mRequestURI);
        } catch (Exception e) {
            logout();
            throw e;
        }

        return response;
    }

    public interface IDCLoginResultListener {
        public void onDCLoginSuccess();

        public void onDCLoginFailed();
    }

    private IDCLoginResultListener mListener = null;

    public void setLoginResultListener(IDCLoginResultListener listener,String userid, String  stdid,String areacode,String stbtype,String usertoken) {
        mListener = listener;
        this.userid =userid;
        this.stdid =stdid;
        this.areacode =areacode;
        this.stbtype =stbtype;
        this.usertoken =usertoken;
        Log.i("gsp", "setLoginResultListener: "+this.stdid+"从Manacvitity传递的参数是"+stdid);
    }

    public void login() throws Exception {
        EvLog.i("login>");
        DataCenterMessage request = new DataCenterMessage();
        request.put("function", "login");
//        request.put("mac", DeviceConfigManager.getInstance().getChipId());

        DataCenterMessage response = null;
        try {
            response = this.request(request, getLoginURI() + "login");
        } catch (Exception e) {
            /*if (mListener != null) {
                mListener.onDCLoginFailed();
            }*/
            throw e;
        }

        String errorCode = response.get("errorcode");
        if (!errorCode.equals("0")) {
            throw new DataCenterCommuException(
                    "DataCenter communication protocal parse error: Expect recieved errorcode is 0, but get: " + errorCode);
        }

        mRemainVipTime = response.get("vip_remain_time");
        Log.i("gsp", "login: 请求获取的有效期是"+mRemainVipTime);
        mRequestURI = response.get("serverip");
        String test =response.get("stbid");
        mValidatecode = "validatecode";
        mCode = response.get("code");
        mPicurhead = response.get("picurhead");

        Log.i("gsp", "login: ++登录获取的参数"+"stbid    "+test+"mRemainVipTime  "+mRemainVipTime+"mRequestURI  "+mRequestURI+"mValidatecode  "+mValidatecode+"mCode "+mCode);
        DeviceConfigManager.getInstance().setCode(mCode);
        DeviceConfigManager.getInstance().setRemainVipTime(Long.valueOf(mRemainVipTime));
        DeviceConfigManager.getInstance().setPicurhead(mPicurhead);

        if (mListener != null && !mHasCallFirstLogin) {
            mListener.onDCLoginSuccess();
            mHasCallFirstLogin = true;
        }
        isLogin = true;
    }

    private void logout() {
        mValidatecode = "";
    }

    // Post data to Data Center.
    private DataCenterMessage request(DataCenterMessage msg, String url) throws Exception {
        final DataCenterMessage msgRecv = new DataCenterMessage();
        final DataCenterHttps http = new DataCenterHttps();

        String response = "";
        if (url.startsWith("http:")) {
            url = new StringBuilder(url).insert(4, "s").toString();
        }
        try {
            http.addHeader("Accept-Charset", "utf8");
            http.addHeader("Accept-Encoding", "");
            http.addHeader("Accept", "text/json");
            http.addHeader("doubledecode", "");
            // user-agent
            String userAgent = DeviceConfigManager.getInstance().getUserAgent();
            http.addHeader("version-info", userAgent);
            http.addHeader("sessionid", "");
            http.addHeader("validcode", mValidatecode);
            http.addHeader("sn", DeviceConfigManager.getInstance().getChipId());
            http.addHeader("userid","15124562470");
            http.addHeader("areacode","86987923112");
            http.addHeader("stbid","31231313");
            http.addHeader("stbtype","1");
            http.addHeader("usertoken","crb22132v33234j");
//            Log.i("gsp", "request: 登录添加的参数是"+DeviceConfigManager.getInstance().getChipId()+" data 数据是"+msg.getContentString()+"地址是"+url+"stbid"+stdid);
            http.addContent("data", msg.getContentString());
            response = http.post(url);
            if (TextUtils.isEmpty(response) == false) {
                msgRecv.setContentString(response);
            }
            EvLog.d("datacenter msgRecv is:" + msgRecv.getContentString());
            EvLog.d("datacenter http header is:" + http.getContent());

        } catch (Exception e) {
            EvLog.e("DataCenter POST exception. url=" + url + ". Request content:"
                    + http.getContent() + ".Recieved:" + response + " e: " + e);
            throw e;
        }

        //EvLog.d("response: " +msgRecv.getContentString() );
        return msgRecv;
    }
}
