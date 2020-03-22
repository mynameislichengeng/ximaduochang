/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年8月18日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.httpproxy;

import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import org.apache.http.client.HttpClient;
import org.apache.http.params.HttpParams;
import android.util.Log;


/**
 * [功能说明]
 */
public class KmHttpProxy {
    private static KmHttpProxy instance = null;
    
    public static KmHttpProxy getInstance() {
        if(instance == null) {
            synchronized (KmHttpProxy.class) {
                KmHttpProxy temp = instance;
                if(temp == null) {
                  temp = new KmHttpProxy();
                  instance = temp;
                }
            }
         }
         return instance;
    }
    
    private BaseHttpProxy mProxy = null;
    private KmHttpProxy() {
        Log.i("KmBox_Logger", "http use no proxy-----");
//        mProxy = new BaseHttpProxy();
    }
    
    public void init(BaseHttpProxy proxy) {
        mProxy = proxy;
    }
    
    public int getSocketProxyPort() {
        return (mProxy != null) ? (mProxy.getSocketProxyPort()) : (0);
    }
    
    public String getSocketProxyIP() {
        return (mProxy != null) ? (mProxy.getSocketProxyIP()) : ("");
    }
    
    public String getHttpProxyIP() {
        return (mProxy != null) ? (mProxy.getHttpProxyIP()) : ("");
    }
    
    public int getHttpProxyPort() {
        return (mProxy != null) ? (mProxy.getHttpProxyPort()) : (0);
    }
    
    public HttpClient getProxyHttpClient(HttpParams params) {
        if (mProxy != null) {
            return mProxy.getProxyHttpClient(params);
        }
        return null;
    }
    
    public Socket getProxySocket(String destIP,int destPort,int timeout) {
        if (mProxy != null) {
            return mProxy.getProxySocket(destIP,destPort,timeout);
        }
        return null;
    }
    
    public HttpURLConnection getProxyHttpUrlConnection(URL url) {
        if (mProxy != null) {
            return mProxy.getProxyHttpUrlConnection(url);
        }
        return null;
    } 
}
