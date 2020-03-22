/*
 *  Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 *  All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *    Date           Author      Version     Description
 *  ----------------------------------------------------
 *  unknown        "unknown"       1.0       [修订说明]
 */

package com.evideo.kmbox.widget.mainview.about;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.evideo.evnetworkchecker.item.ICheckStepObserver;
import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.util.CommonUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;
import com.evideo.kmbox.util.HttpFile;
import com.evideo.kmbox.util.NetUtils;
import com.evideo.kmbox.widget.mainview.about.NetWorkCheckDialog.PingStep;

/**
 * [功能说明] 网络管理
 */
public final class NetWorkCheckManager {

    private static NetWorkCheckManager sInstance = null;

    public final class FileDownState {
        /** [无错误] */
        public static final int ERROR_NONE = 1000;
        /** [创建文件失败] */
        public static final int ERROR_CREATE_FILE_FAILED = 1001;
        /** [空间不足] */
        public static final int ERROR_NOT_ENOUGH_SPACE = 1002;
        /** [不可用url] */
        public static final int ERROR_INVALID_URL = 1003;
        /** [连接失败] */
        public static final int ERROR_CONNECT_FAILED = 1004;
        /** [资源不存在] */
        public static final int ERROR_RESOURCE_NOT_EXIST = 1006;
        /** [读超时] */
        public static final int ERROR_READ_TIMEOUT = 1007;
        /** [文件未找到] */
        public static final int ERROR_FILE_NO_FOUND = 1008;
        /** [写失败] */
        public static final int ERROR_WRITE_FAIL = 1009;
        /** [网络已断开] */
        public static final int ERROR_NET_ERROR = 1015;
        /** [未知异常] */
        public static final int ERROR_UNKNOWN = 1017;
    }

    public class TestConfig {
        /** [互联网测试地址] */
        public static final String INTERNET_TEST_URL = "http://www.baidu.com";
        /** [网络按钮：空闲状态] */
        public static final int CHECK_STATE_IDLE = 0;
        /** [网络按钮：开始状态] */
        public static final int CHECK_STATE_STARTED = 1;
        /** [网络按钮：结束状态] */
        public static final int CHECK_STATE_END = 2;
        /** [网络按钮：正在取消] */
        public static final int CHECK_STATE_CANCEL = 3;
        /** [超时错误] */
        public static final int ERROR_TIMEOUT = 1;
        /** [协议出错] */
        public static final int ERROR_DATACENTER_ERROR_PROTOCOL = 2;
        /** [CDN地址错误] */
        public static final int ERROR_RAINBOW_ERROR_ADDR = 3;
        /** [HTTP响应码无效] */
        public static final int ERROR_HTTPCODE_ILLEAGL = 4;
        /** [下载出错] */
        public static final int ERROR_DOWNLOAD = 5;
    }

    private Process mProc;
    private Handler mHandler;
    private HttpFile mCurConn = null;
    private long mSpeedAvg = 0L;
    private int mCountdown = 0;
    private int mHttpCode = 0;
    private int mError;
    public boolean mIsCanceled = false;

    /**
     * [功能说明] 更新CDN速度
     */
    public static final int MSG_UPDATE_SPEED = 0;
    public static final String MSG_PER_SPEED = "perSpeed";
    public static final String MSG_TIME_REMAIN = "tiemRemain";

    /**
     * [功能说明] 网络状态单例
     *
     * @return 网络状态管理单例
     */
    public static NetWorkCheckManager getInstance() {
        if (sInstance == null) {
            sInstance = new NetWorkCheckManager();
        }
        return sInstance;
    }

    private NetWorkCheckManager() {
        initIPInfo();
    }

    /**
     * [功能说明] 设置UI更新handler
     *
     * @param handler
     *            Handler
     */
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    /**
     * [功能说明] 检测指定ip是否可连通
     *
     * @param ipAddr
     *            ip地址
     * @return true or false
     */
    public boolean checkIPAvailable(String ipAddr) {
        String cmd = "ping -c 2 " + ipAddr;
        EvLog.e("checkIPAvailable,cmd:" + cmd);
        return pingConn(cmd);
    }

    /**
     * [功能说明] 检查互联网连通性
     *
     * @return
     */
    public long checkInternetAvaliable() {
        long result = -1L;
        long startTime = System.currentTimeMillis();
        HttpFile file = new HttpFile();
        int ret = -1;
        try {
            // file.open(testUrl);
            ret = file.open(TestConfig.INTERNET_TEST_URL);
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                result = -1L;
            }
        }
        
        if (ret == 0) {
            int responseCode = file.getResponseCode();

            EvLog.d("net",
                    "net work checker check internet avaliable response code: "
                            + Integer.toString(responseCode));
            if (responseCode >= 200 && responseCode < 400) {
                long endTime = System.currentTimeMillis();
                result = endTime - startTime;
            }
        }
        
        if (file != null) {
            file.close();
            file = null;
        }
        return result;
    }

    /**
     * [功能说明] 数据中心检测
     *
     * @return 通信时间
     * @throws Exception
     *             异常
     */
    public String startCheckDataCenter() throws Exception {
        return DCDomain.getInstance().requestCDNTestUrl();
    }

    /**
     * [功能说明] 中止当前下载
     */
    public void stopDownloadTask() {
        if (mCurConn != null) {
            try {
                mIsCanceled = true;
            } catch (Exception e) {
                EvLog.i(e.getMessage());
            }
        }
    }

    /**
     * [功能说明] 获取平均速度
     *
     * @return 平均速度
     */
    public long getAverageSpeed() {
        return mSpeedAvg;
    }

    /**
     * [功能说明] 获取返回的HttpCode
     *
     * @return httpCode
     */
    public int getErrorCode() {
        return mError;
    }

    /**
     * [功能说明] 获取返回的HttpCode
     *
     * @return httpCode
     */
    public int getHttpCode() {
        return mHttpCode;
    }

    /**
     * [功能说明] 七牛曲库检测
     *
     * @param testUrl
     *            目标测试文件
     * @param filePath
     *            本地临时文件
     * @return true or false
     */
    public int startCheckCloudSongUrl(String testUrl, String filePath) {
        // 
        mCountdown = 10;
        mSpeedAvg = 0;
    
        HttpFile httpFile = new HttpFile();
        int errorType = httpFile.open(testUrl);
        mHttpCode = httpFile.getResponseCode();
        if (errorType != 0) {
            // 相应码404提示资源不存在，否则提示连接错误 1006
            if (mHttpCode == -1) {
                return errorType;
            } else if (mHttpCode == HttpFile.HTTP_ERROR_RESOURCE_NOT_FOUND) {
                mError = FileDownState.ERROR_RESOURCE_NOT_EXIST;
                return TestConfig.ERROR_DOWNLOAD;
            } else if (mHttpCode < 200 || mHttpCode > 300) {
                // // 连接错误，1004
                // mError = FileDownState.ERROR_CONNECT_FAILED;
                return TestConfig.ERROR_HTTPCODE_ILLEAGL;
            }
        }

        mCurConn = httpFile;

        // 非法链接，文件大小为空 1003
        long databaseSize = httpFile.getContentLength();
        if (databaseSize <= 0) {
            EvLog.d("文件大小非法");
            mError = FileDownState.ERROR_INVALID_URL;
            if (httpFile != null) {
                httpFile.close();
                httpFile = null;
            }
            return TestConfig.ERROR_DOWNLOAD;
        }

        String fileName = FileUtil.concatPath(filePath, "1.tmp");
        EvLog.i(testUrl + " >> " + fileName);
        
        File localFile = checkLocalFile(databaseSize, fileName);
        if (localFile == null) {
            EvLog.e("open local file failed:" + filePath);
            if (httpFile != null) {
                httpFile.close();
                httpFile = null;
            }
            // 创建文件失败 1001
            mError = FileDownState.ERROR_CREATE_FILE_FAILED;
            return TestConfig.ERROR_DOWNLOAD;
        }

        float speedInterval = 0.0f;
        long prevWriteCount = 0;
        long startDownTime = System.currentTimeMillis();
        long prevSendTime = System.currentTimeMillis();
        long timeInterval = 0;

        try {
            FileOutputStream os = new FileOutputStream(fileName, true);

            try {
                byte[] buf = new byte[1024 * 8];
                int ch = -1;
                long count = 0;
                while (!mIsCanceled && mCountdown > 0) {

                    if (!NetUtils.isNetworkConnected(BaseApplication
                            .getInstance())) {
                        // 读超时 1015
                        mError = FileDownState.ERROR_NET_ERROR;
                        if (httpFile != null) {
                            httpFile.close();
                            httpFile = null;
                        }
                        CommonUtil.safeClose(os);
                        return TestConfig.ERROR_DOWNLOAD;
                    }

                    try {
                        ch = httpFile.read(buf);
                    } catch (IOException e) {
                        // 读超时 1007
                        mError = FileDownState.ERROR_READ_TIMEOUT;
                        if (httpFile != null) {
                            httpFile.close();
                            httpFile = null;
                        }
                        CommonUtil.safeClose(os);
                        return TestConfig.ERROR_DOWNLOAD;
                    }

                   /* if (filePath.startsWith("/mnt/sdcard")) {
                        if (FileUtil.getAvailableSize("/mnt/sdcard") < (databaseSize - count)) {
                            // 空间不足 1002
                            mError = FileDownState.ERROR_NOT_ENOUGH_SPACE;
                            if (httpFile != null) {
                                httpFile.close();
                                httpFile = null;
                            }
                            CommonUtil.safeClose(os);
                            return TestConfig.ERROR_DOWNLOAD;
                        }
                    }*/

                    if (ch > 0) {
                        try {
                            os.write(buf, 0, ch);
                        } catch (IOException e) {
                            // 写失败 1009
                            mError = FileDownState.ERROR_WRITE_FAIL;
                            if (httpFile != null) {
                                httpFile.close();
                                httpFile = null;
                            }
                            CommonUtil.safeClose(os);
                            return TestConfig.ERROR_DOWNLOAD;
                        }
                    } else if (ch == 0) {
                        // continue;
                    } else {
                        // 读取缓存结束
                        break;
                    }

                    if (ch > 0) {
                        count += ch;
                        prevWriteCount += ch;
                        timeInterval = System.currentTimeMillis()
                                - prevSendTime;

                        // 隔1秒发送消息
                        if (timeInterval >= 1000) {
                            mCountdown--;
                            speedInterval = prevWriteCount / timeInterval;
                            if (mHandler != null) {
                                Bundle bundle = new Bundle();
                                bundle.putFloat(MSG_PER_SPEED, speedInterval);
                                bundle.putInt(MSG_TIME_REMAIN, mCountdown);

                                Message msg = mHandler
                                        .obtainMessage(MSG_UPDATE_SPEED);
                                msg.setData(bundle);
                                mHandler.sendMessage(msg);
                                EvLog.d((10 - mCountdown)
                                        + "send,speed=" + speedInterval
                                        + ",count=" + count);
                            }
                            prevSendTime = System.currentTimeMillis();
                            prevWriteCount = 0;
                        }
                    }
                }

                if (!mIsCanceled) {
                    mSpeedAvg = count
                            / (System.currentTimeMillis() - startDownTime);
                    Message msg = mHandler.obtainMessage(MSG_UPDATE_SPEED);
                    Bundle bundle = new Bundle();
                    bundle.putFloat(MSG_PER_SPEED, -1f);
                    bundle.putInt(MSG_TIME_REMAIN, mCountdown - 1);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                    EvLog.d("total send times:" + (10 - mCountdown + 1)+ ",use time:" + (10 - mCountdown) + "s");
                }
                if (mIsCanceled) {
                    EvLog.d("文件下载取消");
                    return 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            } finally {
                if (mIsCanceled) {
                    mCurConn.close();
                }
                if (httpFile != null) {
                    httpFile.close();
                    httpFile = null;
                }
                CommonUtil.safeClose(os);
            }
        } catch (FileNotFoundException e) {
            EvLog.e("error:FileNotFoundException");
            // 文件未找到 1008
            mError = FileDownState.ERROR_FILE_NO_FOUND;
            return TestConfig.ERROR_DOWNLOAD;
        }
        FileUtil.deleteFile(fileName);
        return errorType;
    }

    private File checkLocalFile(long contentLength, String fileName) {
        FileUtil.deleteFile(fileName);
        
        File file = new File(fileName);
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
                EvLog.d("checkLocalFile parent dir no exists,create path :" + file.getParentFile().getPath());
            }
            file.createNewFile();
            EvLog.i("file.createNewFile:" + file.getPath());
        } catch (IOException e) {
            EvLog.i("file.create fail:" + file.getPath());
            EvLog.i("error:" + e.getMessage());
            e.printStackTrace();
            file = null;
        }
        return file;
    }

    /**
     * [功能说明] Ping连通性
     *
     * @param s
     *            ping语句
     * @return 成功或失败
     */
    private boolean pingConn(String s) {
        boolean result = false;
        try {
            mProc = Runtime.getRuntime().exec(s);
            if (mProc.waitFor() == 0) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    private List<ICheckStepObserver> mListeners = new ArrayList<ICheckStepObserver>();

    /**
     * [功能说明] 注册监听器
     *
     * @param listener
     *            监听器
     */
    public void registerListener(ICheckStepObserver listener) {
        if (listener == null) {
            return;
        }

        synchronized (this.mListeners) {
            if (!this.mListeners.contains(listener)) {
                this.mListeners.add(listener);
            }
        }
    }

    /**
     * [功能说明] 注销监听器
     *
     * @param listener
     *            监听器
     */
    public void unregisterListener(ICheckStepObserver listener) {
        if (listener == null) {
            return;
        }

        synchronized (this.mListeners) {
            mListeners.remove(listener);
        }
    }

    /**
     * [功能说明] 通知开始检测
     *
     * @param pingStep
     *            步骤
     */
    public void notifyCheckStart(int pingStep) {
        synchronized (this.mListeners) {
            for (int i = 0; i < this.mListeners.size(); i++) {
                mListeners.get(i).onCheckStart(pingStep);
            }
        }
    }

    /**
     * [功能说明] 结束检测
     *
     * @param pingStep
     *            步骤
     */
    public void notifyCheckFinish(int pingStep, int tryCount) {
        synchronized (this.mListeners) {
            for (int i = 0; i < this.mListeners.size(); i++) {
                mListeners.get(i).onCheckEnd(pingStep, tryCount);
            }
        }
    }

    /**
     * [功能说明] 检测出错
     *
     * @param pingStep
     *            步骤
     * @param type
     *            类型
     */
    public void notifyCheckError(int pingStep, int type) {
        synchronized (this.mListeners) {
            for (int i = 0; i < this.mListeners.size(); i++) {
                mListeners.get(i).onCheckError(pingStep, type);
            }
        }
    }

    private String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "."
                + (0xFF & paramInt >> 16) + "." + (0xFF & paramInt >> 24);
    }

//    private String mGateWay = "";
    private String mIP = "";
    private String mDNS = "";

   /* public String getGateWay() {
        return mGateWay;
    }*/

    public String getIP() {
        return mIP;
    }

    public String getDNS() {
        return mDNS;
    }

    /**
     * [功能说明] 获取本地IP信息
     *
     * @return mIps IP信息
     */
    public boolean initIPInfo() {
        boolean ret = false;

        boolean isConnect = NetUtils.isNetworkConnected(BaseApplication
                .getInstance().getBaseContext());

        if (!isConnect) {
            NetWorkCheckManager.getInstance().notifyCheckError(
                    PingStep.PING_STEP_LOCAL_CABLE, 0);
            return false;
        } else {
            boolean mIsWifi = NetUtils.isCurrNetworkWifi(BaseApplication
                    .getInstance().getBaseContext());
            if (mIsWifi) {
                // http://blog.csdn.net/lsong89/article/details/44339063
                WifiManager wifiManager = (WifiManager) BaseApplication
                        .getInstance().getSystemService("wifi");
                DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//                mGateWay = intToIp(dhcpInfo.gateway);
                mDNS = intToIp(dhcpInfo.dns1);
                mIP = intToIp(wifiInfo.getIpAddress());
                ret = true;
            } else {
                ret = getLocalIp();
            }
            return ret;
        }
    }

    private boolean getLocalIp() {
        try {
            // 获取本地设备的所有网络接口
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                List<InterfaceAddress> mList = intf.getInterfaceAddresses();
                for (InterfaceAddress l : mList) {
                    InetAddress inetAddress = l.getAddress();
                    if (inetAddress.isLoopbackAddress()) {
                        continue;
                    }
                    if (!(inetAddress instanceof Inet4Address)) {
                        continue;
                    }
                    
                    String hostAddress = inetAddress.getHostAddress();
                    if (hostAddress.indexOf(":") > 0) {
                        // case : ipv6
                        continue;
                    } 
                    mIP = inetAddress.getHostAddress();
                    String maskAddress = calcMaskByPrefixLength(l.getNetworkPrefixLength());
                    EvLog.e("maskAddress:" + maskAddress);
                    
                  /*  Context context = BaseApplication.getInstance();
                    ConnectivityManager mConnectivityManager = (ConnectivityManager)context.getSystemService(
                                    Context.CONNECTIVITY_SERVICE);*/
                    mDNS = getLocalDNS();
                    EvLog.e("mDNS=" + mDNS);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static String calcMaskByPrefixLength(int length) {    
        int mask = -1 << (32 - length);    
        int partsNum = 4;    
        int bitsOfPart = 8;    
        int maskParts[] = new int[partsNum];    
        int selector = 0x000000ff;    
    
        for (int i = 0; i < maskParts.length; i++) {    
            int pos = maskParts.length - 1 - i;    
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;    
        }    
    
        String result = "";    
        result = result + maskParts[0];    
        for (int i = 1; i < maskParts.length; i++) {    
            result = result + "." + maskParts[i];    
        }    
        return result;    
    }    
    
    private String getGW(String ip) {
        int pos = ip.lastIndexOf(".") + 1;
        String gw = ip.substring(0, pos) + "1";
        return gw;
           /* WifiManager wifi_service = (WifiManager) BaseApplication.getInstance().getSystemService(Context.WIFI_SERVICE);  
            DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();  
            WifiInfo wifiinfo = wifi_service.getConnectionInfo();  
            System.out.println("Wifi info----->"+wifiinfo.getIpAddress());  
            System.out.println("DHCP info gateway----->"+Formatter.formatIpAddress(dhcpInfo.gateway));  
            System.out.println("DHCP info netmask----->"+Formatter.formatIpAddress(dhcpInfo.netmask));  
            //DhcpInfo中的ipAddress是一个int型的变量，通过Formatter将其转化为字符串IP地址  
            return Formatter.formatIpAddress(dhcpInfo.gateway);  */
        
       /* Process cmdProcess = null;
        BufferedReader reader = null;
        String dnsIP = "";
        try {
            cmdProcess = Runtime.getRuntime().exec("getprop dhcp.eth0.gateway");
            reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
            dnsIP = reader.readLine();
            return dnsIP;
        } catch (IOException e) {
            return null;
        } finally{
            try {
                reader.close();
            } catch (IOException e) {
            }
            cmdProcess.destroy();
        }*/
    }
    
    private String getLocalDNS(){
        Process cmdProcess = null;
        BufferedReader reader = null;
        String dnsIP = "";
        try {
            cmdProcess = Runtime.getRuntime().exec("getprop net.dns1");
            reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
            dnsIP = reader.readLine();
            return dnsIP;
        } catch (IOException e) {
            return "";
        } finally{
            try {
                reader.close();
            } catch (IOException e) {
            }
            cmdProcess.destroy();
        }
    }
}
