/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年4月6日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.chargeproxy;

import android.text.TextUtils;
import android.util.Log;

import com.evideo.kmbox.model.charge.ChargeProductInfo;
import com.evideo.kmbox.model.datacenter.DataCenterCommu;
import com.evideo.kmbox.model.datacenter.proxy.data.DataCenterMessage;
import com.evideo.kmbox.util.EvLog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * [功能说明]
 */
public class DeviceCommu {

    /*查询产品价格*/
    private static final String PRODUCT = "product";
    /*查询流水号*/
    private static final String GET_TRADENO = "get_tradeno";
    /*上报支付成功*/
    private static final String REPORT_PAYRESULT = "report_payresult";
    /*鉴权*/
    private static final String AUTH = "auth";
    private long time;

    public static class AuthResultInfo {
        public int errorCode;
        public long validTime;
        public int productId;
        public String appSerialNo;
        public String errorMessage;

        public AuthResultInfo() {
            this.errorCode = -1;
            this.validTime = 0;
            this.productId = 0;
            this.appSerialNo = "";
            this.errorMessage = "";
        }
    }


    public static AuthResultInfo queryAuth() throws Exception {
        DataCenterMessage request = new DataCenterMessage();
        request.put("function", AUTH);

        DataCenterMessage response = new DataCenterMessage();
        response = DataCenterCommu.getInstance().sendMessage(request);

        EvLog.i(AUTH + " response:" + response.getContentString());


        AuthResultInfo info = new AuthResultInfo();
        info.errorCode = Integer.valueOf(response.get("errorcode"));
        info.errorMessage = response.get("errormessage");

        String remainTime = response.get("remain_time");
        if (!TextUtils.isEmpty(remainTime)) {
            info.validTime = Long.valueOf(remainTime);
        }

        if (!TextUtils.isEmpty(response.get("trade_no"))) {
            info.appSerialNo = response.get("trade_no");
        }

        if (!TextUtils.isEmpty(response.get("product_id"))) {
            info.productId = Integer.valueOf(response.get("product_id"));
        }

        return info;
    }

    public static String queryTradeNo(String productId) throws JSONException {
        DataCenterMessage request = new DataCenterMessage();
        request.put("function", GET_TRADENO);
        request.put("productId",  productId);

        DataCenterMessage response = new DataCenterMessage();
        try {
            response = DataCenterCommu.getInstance().sendMessage(request);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        String tradeNo = response.get("trade_no");
        String payUrl = response.get("qrurl");
        String errorCode = response.get("errorcode");
        String errormessage = response.get("errormessage");

        if (!response.get("errorcode").equals("0")) {
            EvLog.e("sendPayResult errorcode:" + response.get("errorcode") + "," + response.get("errormessage"));
            return "";
        }
        return response.get("trade_no");
    }
    
    //查询价格
    public static int queryPayPrice(List<ChargeProductInfo> list) {
        if (list == null) {
            return -1;
        }
        try {
            DataCenterMessage request = new DataCenterMessage();
            request.put("function", PRODUCT);
            DataCenterMessage response = new DataCenterMessage();
            response = DataCenterCommu.getInstance().sendMessage(request);
            EvLog.i("queryPayPrice response:" + response.getContentString());
            int code = Integer.valueOf(response.get("errorcode"));
            if (code != 0) {
                return code;
            }
            JSONArray rJsonArray = response.getJSONArray("productInfo");
            if (rJsonArray == null) {
                return -2;
            }

            for (int i = 0; i < rJsonArray.length(); i++) {
                ChargeProductInfo item = new ChargeProductInfo();
                item.productId = rJsonArray.getJSONObject(i).getInt("product_id");
                item.productName = rJsonArray.getJSONObject(i).getString("product_name");
                item.productPrice = Integer.valueOf(rJsonArray.getJSONObject(i).getString("product_price"));
                item.productNowPrice = Integer.valueOf(rJsonArray.getJSONObject(i).getString("product_now_price"));
                list.add(item);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -3;
        }
    }

    public static long sendPayResult(String appSerialNum/*,String productId,String productPrice*/) throws JSONException {
        DataCenterMessage request = new DataCenterMessage();
        request.put("function", REPORT_PAYRESULT);
        request.put("trade_no", appSerialNum);

        Log.i("gsp", "sendPayResult: 上传的时间服务器的时间"+appSerialNum);
        DataCenterMessage response = new DataCenterMessage();
        try {
            response = DataCenterCommu.getInstance().sendMessage(request);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return -1;
        }
        EvLog.i(appSerialNum + ",sendPayResult response:" + response.getContentString());
        Log.i("gsp", "sendPayResult: 订购返回的数据是"+response.getContentString());
        if (!response.get("errorcode").equals("0")) {
            EvLog.e("sendPayResult errorcode:" + response.get("errorcode") + "," + response.get("errormessage"));
            return -1;
        }

        String validTime = response.get("remain_time");
        long time = -1;
        try {
            time = Long.valueOf(validTime);
        } catch (Exception e) {
            e.printStackTrace();
            time = -1;
        }

        return time;
    }

}
