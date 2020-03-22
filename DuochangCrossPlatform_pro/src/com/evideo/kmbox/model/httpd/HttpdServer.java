package com.evideo.kmbox.model.httpd;

import java.io.IOException;
import com.evideo.kmbox.util.EvLog;


public class HttpdServer {
    private static HttpdServer instance = null;
    
    public static HttpdServer getInstance() {
        if(instance == null) {
            synchronized (HttpdServer.class) {
                HttpdServer temp = instance;
                if(temp == null) {
                  temp = new HttpdServer();
                  instance = temp;
                }
            }
         }
         return instance;
    }
    
    public void stop(){
        if (mServer != null) {
            mServer.stop();
        }
    }
    
    public boolean isAlive() {
        return (mServer != null) ? (mServer.isAlive()) : (false);
    }
    
    /*public void setResponseUrl(String url) {
        if (mService != null) {
            mService.setResponseUrl(url);
        }
    }*/
    
    private KmHttpdServer mServer = null;
    
    public void start(){
        if (mServer == null) {
            mServer = new KmHttpdServer();
        }
        if (mServer.isAlive()) {
            EvLog.i("httpserver is already alive");
            return;
        }
        try {
            mServer.start();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
