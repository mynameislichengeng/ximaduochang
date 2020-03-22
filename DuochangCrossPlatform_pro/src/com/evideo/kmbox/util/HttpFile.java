package com.evideo.kmbox.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.httpproxy.KmHttpProxy;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;

public class HttpFile {
    public static final int HTTP_ERROR_RESOURCE_NOT_FOUND = 404;
    private HttpURLConnection mConnection = null;
    private InputStream mInputStream = null;
    private int mConnectTimeout = 8*1000;
    private int mReadTimeout = 8*1000;
    private String mUrl = "";
    
    private boolean mUseProxy = true;
    
    public HttpFile() {
    }
    
    public void setUseProxy(boolean userProxy) {
        this.mUseProxy = userProxy;
    }
    
   /* public void setReadTimeout(int timeoutMillis) {
        this.mReadTimeout = timeoutMillis;
    }
    
    public void setConnectTimeout(int timeoutMillis) {
        this.mConnectTimeout = timeoutMillis;
    }*/
    
    public static class HttpOpenResult {
        public int ret;
        public int responseCode;
        public HttpOpenResult() {
            this.ret = -1;
            this.responseCode = 0;
        }
        public void reset() {
            this.ret = -1;
            this.responseCode = 0;
        }
    }
    /**
     * @brief : [建立http链接 ]
     * @param url
     * @return
     */
    public int open(String url,HttpOpenResult result) {
        result = open(url, 0L);
        return result.ret;
    }

    public int open(String url) {
        HttpOpenResult result = open(url, 0L);
        return result.ret;
    }
    /**
     * @brief : [建立http链接，重试次数不超过retry]
     * @param uri
     * @param startRange
     * @param retry
     * @return
     */
    public int open(String uri, long startRange, int retry) {
        HttpOpenResult result = new HttpOpenResult();
        for ( int i = 0; i < retry;i++ ) {
            result = open(uri, startRange);
            if (result.ret == 0) {
                break;
            } else {
                continue;
            }
        }
        return result.ret;
    }
    
   /* public int openForCheckInternetAvailable(String uri) {
        int errorType = connect(uri, 0l);
        if (errorType == 0) {
            int code = 0;
            try {
                code = mConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
                errorType = TestConfig.ERROR_TIMEOUT;
            }

//            if (code < 200 || code >= 300) {
//                if (mConnection != null) {
//                    mConnection.disconnect();
//                    mConnection = null;
//                }
//            }
        }
        return errorType;
    }*/
    
    public HttpOpenResult openWithResult(String uri, long startRange, int retry) {
        HttpOpenResult result = new HttpOpenResult();
        for ( int i = 0; i < retry;i++ ) {
            result = open(uri, startRange);
            if (result.ret == 0) {
                break;
            } else {
                continue;
            }
        }
        return result;
    }
    
    public static final int HTTP_ERROR_CONNECT_MALFORMEDURLEXCEPTION = -1;
    public static final int HTTP_ERROR_CONNECT_IOEXCEPTION = -2;
    public static final int HTTP_ERROR_GET_RESPONSECODE_FAILED = -3;
    public static final int HTTP_ERROR_RESPONSECODE_INVALID = -4;
    
    
    public String getErrorMessage(int errorCode) {
        switch(errorCode) {
        case HTTP_ERROR_CONNECT_MALFORMEDURLEXCEPTION:
            return "http connect catch MalformedURLException";
        case HTTP_ERROR_CONNECT_IOEXCEPTION:
            return "http connect catch IOException";
        case HTTP_ERROR_GET_RESPONSECODE_FAILED:
            return "http open get responsecode failed";
        case HTTP_ERROR_RESPONSECODE_INVALID:
            return "http open get responsecode invalid";
        default:
            return "http get unknow error";
        }
    }
   
    private int connect(String uri, long startRange) {
        int ret = 0;
        
        try{
            URL url = new URL(uri);
//            mConnection = (HttpURLConnection) url.openConnection();
            if (this.mUseProxy) {
                mConnection = KmHttpProxy.getInstance().getProxyHttpUrlConnection(url);
            } else {
                mConnection = (HttpURLConnection) url.openConnection();
            }
            
            // FIXME
            mConnection.setConnectTimeout(mConnectTimeout);
            mConnection.setReadTimeout(mReadTimeout);
//            EvLog.i(url + ",Range:"+ startRange + ",use KmHttpProxy:" + mUseProxy + ", ConnectTimeout=" + mConnectTimeout + ",ReadTimeout=" + mReadTimeout);
            mConnection.setRequestMethod("GET");
            mConnection.setRequestProperty("Accept", "*/*");
            mConnection.setRequestProperty("Range", "bytes=" + startRange + "-");
            mConnection.setRequestProperty("Accept-Encoding", "identity"); 
            mConnection.setRequestProperty("version-info",DeviceConfigManager.getInstance().getUserAgent());
            mConnection.connect();
        } catch (MalformedURLException e1) {
            ret = HTTP_ERROR_CONNECT_MALFORMEDURLEXCEPTION;
            UmengAgentUtil.reportError(uri+ ", connect failed,catch MalformedURLException:" + e1.getMessage());
            EvLog.e(uri+ " connect failed" + e1.getMessage());
            e1.printStackTrace();
            if (mConnection != null) {
                mConnection.disconnect();
                mConnection = null;
            }
        } catch (IOException e1) {
            ret = HTTP_ERROR_CONNECT_IOEXCEPTION;
            UmengAgentUtil.reportError(uri+ ", connect failed,catch IOException:" + e1.getMessage());
            EvLog.e(uri+ " connect failed" + e1.getMessage());
            e1.printStackTrace();
            if (mConnection != null) {
                mConnection.disconnect();
                mConnection = null;
            }
        }
        return ret;
    }
    


    /**
     * @brief : [建立http链接,Range起始位置为startRange]
     * @param uri
     * @param startRange
     * @return
     */
    public HttpOpenResult open(String uri, long startRange) {
        HttpOpenResult result = new HttpOpenResult();
        
        int ret = 0;
        ret = connect( uri, startRange);
        if (ret == 0) {
            int code = 0;
            try {
                code = mConnection.getResponseCode();
//                EvLog.e(uri + " code:" + code + ",len:" + getContentLength());
                if (code < 200 || code >= 400) {
                    EvLog.e(uri+ ", opened, getResponseCode=" + code);
                    UmengAgentUtil.reportError(uri+ ", opened, getResponseCode=" + code);
                    result.ret = HTTP_ERROR_RESPONSECODE_INVALID;
                    result.responseCode = code;
                } else {
                    result.ret = 0;
                    mUrl = uri;
                }
            } catch (IOException e) {
                result.ret = HTTP_ERROR_GET_RESPONSECODE_FAILED;
                result.responseCode = 0;
                EvLog.e(uri+ ", opened, getResponseCode catch IOException:" + e.getMessage());
                UmengAgentUtil.reportError(uri+ ", opened, getResponseCode catch IOException:" + e.getMessage());
                e.printStackTrace();
            }

            if (ret != 0) {
                if (mConnection != null) {
                    mConnection.disconnect();
                    mConnection = null;
                }
            }
        } else {
            result.ret = ret;
            result.responseCode = 0;
        }
        return result;
    }

    /**
     * @brief : [获取返回码]
     * @return
     */
    public int getResponseCode() {
        if (mConnection != null) {
            try {
                return mConnection.getResponseCode();
            } catch (IOException e) {
            	e.printStackTrace();
            	String errorMsg = mConnection.getURL().toString() + ", getResponseCode exception:" + e.getMessage();
            	UmengAgentUtil.reportError(errorMsg);
                EvLog.d(errorMsg);
                return -1;
            }
        }

        return -1;
    }
    /**
     * @brief : [关闭链接]
     */
    public void close() {
        if (mConnection != null) {
            /*if (!DeviceInfoUtils.isHUNGD_YHH())*/ {
                // 湖南广电海思盒子，执行disconnect方法可能会耗时几十秒，导致准备播放过程时长十分久
                // 因此，湖南广电海思盒子不执行此方法
                // 参考，http://kingori.co/minutae/2013/04/httpurlconnection-disconnect/
//                long timeStart = System.currentTimeMillis();
                mConnection.disconnect();
//                EvLog.d("close disconnect eclipse:" + (System.currentTimeMillis() - timeStart));
            } /*else {
                long timeStart = System.currentTimeMillis();
                if (mInputStream != null) {
                    try {
                        mInputStream.close();
                        mInputStream = null;
                    } catch (IOException e) {
                        EvLog.e("http file close inputstream error");
                        UmengAgentUtil.reportError(e);
                    }
                }
                EvLog.d("close mInputStream eclipse:" + (System.currentTimeMillis() - timeStart));
            }*/
            mConnection = null;
        }
    }
    
    /**
     * @brief : [获取http链接对应的文件大小]
     * @return
     */
    public long getContentLength() {
        if (mConnection != null) {
            return mConnection.getContentLength();
        }

        return -1;
    }

    /**
     * @brief : [获取http链接文件流]
     * @return
     */
    public InputStream getInputStream() {
        if (mConnection != null) {
            try {
                return mConnection.getInputStream();
            } catch (IOException e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
                mConnection.disconnect();
                return null;
            }
        }
        
        return null;
    }
    
    public String getHeaderField(String key) {
        if (mConnection != null) {
            return mConnection.getHeaderField(key);
        }
        return null;
    }
    /**
     * @brief : [获取http链接对应的文件名]
     * @return
     */
    public String getFileName() {
        String fileName = "";
        
        if (mConnection == null) {
            return fileName;
        }
        
        String disposition = mConnection.getHeaderField("Content-Disposition");
//            EvLog.i("disposition:" + disposition);
        if (disposition == null || disposition.length() <= 0) {
            return fileName;
        }
        
        String key = "filename=\"";
        
        int indexStart = -1;
        
        int indexEnd = -1;
        if (disposition.contains(key)) {
            indexStart = disposition.indexOf(key);
            indexEnd = disposition.length() - 1;
        } else if (disposition.contains("filename=")) {
            indexStart = disposition.indexOf("filename=");
            indexEnd = disposition.length();
        }
        
        if (indexStart > 0 && indexEnd > 0) {
            indexStart += key.length();
            fileName = disposition.substring(indexStart, indexEnd);
        }
        EvLog.d("http getFileName:" + fileName);
        return fileName;
    }


    /**
     * @brief : [读取http文件流到buffer中]
     * @param buffer
     * @return
     * @throws IOException
     */
    public int read(byte[] buffer) throws IOException {
        if (mConnection == null) {
            return -1;
        }
        
        mInputStream = mConnection.getInputStream();
        
        // int count = availableLength > buffer.length ? buffer.length : availableLength;
        
        return mInputStream.read(buffer);
    }

    /**
     * @brief : [读取http文件流到buffer中]
     * @param buffer
     * @return
     * @throws IOException
     */
    public int read(byte[] buffer,int offset,int len) throws IOException {
        if (mConnection == null) {
            return -1;
        }
        
        mInputStream = mConnection.getInputStream();
        
        return mInputStream.read(buffer,offset,len);
    }
    
    public String getUrl() {
        return mUrl;
    }
}
