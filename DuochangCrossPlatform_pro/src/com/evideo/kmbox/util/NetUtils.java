/**
 * @file NetUtils.java
 * @brief
 * @author 陈节省
 * @date 2011-3-16 上午10:01:40
 * @version v1.0
 * @note CopyRight 2011 福建星网视易信息系统有限公司 All Rights Reserved.
 */
package com.evideo.kmbox.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.ethernet.IEthernetManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.SystemProperties;
import android.provider.Settings.System;
import android.text.TextUtils;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;

/**
 * 
 * @brief
 * @author 陈节省
 * @date 2011-3-16 上午10:01:40
 * @version v1.0
 */
public class NetUtils {

    public static final String ETHERNET_USE_STATIC_IP = "ethernet_use_static_ip";
    
    
    public static final String ETHERNET_STATIC_IP = "ethernet_static_ip";
    
    public static final String ETHERNET_SERVICE = "ethernet";
    /**
     * 
     * @brief 将IP地址转换成bytes
     * @param ip
     * @return
     * @throws
     */
    public static byte[] IP2Bytes(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            if (address != null) {
                return address.getAddress();
            }
        } catch (UnknownHostException e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        return null;
    }

    /**
     * 
     * @brief 将bytes转换成String的IP地址
     * @param bytes
     * @return
     * @throws
     */
    public static String Bytes2IP(byte[] bytes) {
        try {
            InetAddress address = InetAddress.getByAddress(bytes);
            if (address != null) {
                return address.getHostAddress();
            }
        } catch (UnknownHostException e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        return null;
    }

    /**
     * 
     * @brief 将Int的地址转换成String IP
     * @param addr
     * @return
     * @throws
     */
    public static String Addr2IP(int addr) {
        // return ((addr >> 24 ) & 0xFF ) + "." +((addr >> 16 ) & 0xFF) + "." +
        // ((addr >> 8 ) & 0xFF) + "." + ( addr & 0xFF);
        return (addr & 0xFF) + "." + ((addr >> 8) & 0xFF) + "."
                + ((addr >> 16) & 0xFF) + "." + ((addr >> 24) & 0xFF);
    }

    /**
     * @brief 打印二进制数据, 调试用
     * @throws
     */
    public static String printByteArray(byte[] ba) {
        return printByteArray(ba, 0, ba.length);
    }

    public static String printByteArray(byte[] ba, int offset, int count) {
        String sLog = "";
        for (int i = offset; i < 80 && i < ba.length && i < offset + count; ++i) {
            if ((i - offset) % 4 == 0) {
                sLog += " ";
            }
            sLog += String.format("%02X", ba[i]);
        }
        return sLog;
    }

    /**
     * @brief 格式化回车为本地可显示的内容
     * @param sSrc
     *            源数据 (不改变)
     * @return 转换后的数据
     * @throws
     */
    public static String formatLinebreak(String sSrc) {
        return sSrc.replaceAll("(\r\n)|(\n\r)|(\r)", "\n");
    }
    
    /**
     * [功能说明]使用jdk接口获取mac地址
     * @return mac地址
     */
    public static String getMacByNetworkInterface() {
        String mac = "";
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) networkInterfaces
                        .nextElement();
                if (ni.getName().startsWith("eth0")) {
                    byte[] macBytes = ni.getHardwareAddress();
                    StringBuffer sb = new StringBuffer("");
                    for(int i=0; i < macBytes.length; i++) {
                        if(i!=0) {
                            sb.append(":");
                        }
                        //字节转换为整数
                        int temp = macBytes[i]&0xff;
                        String str = Integer.toHexString(temp);
                        if(str.length() == 1) {
                            sb.append("0"+str);
                        }else {
                            sb.append(str);
                        }
                    }
                    mac = sb.toString();
                    break;
                }
            }
        } catch (Exception e) {
            EvLog.eStackTrace(e);
            UmengAgentUtil.reportError(e);
        }
        
        return mac;
    }
    
    /**
     * @brief : [获取网络连接类型]
     * @return
     */
    public static int getConnectType(Context context) {
        ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cManager.getActiveNetworkInfo();
        if(netInfo != null) {
            return netInfo.getType();
        }
        return -1;
    }
    
    /**
     * @brief : [判断当前网络是否是wifi]
     * @param context
     * @return
     */
    public static boolean isCurrNetworkWifi(Context context) {
        return isCurrNetworkMatched(context, ConnectivityManager.TYPE_WIFI);
    }
    
    /**
     * @brief : [判断当前网络是否是以太网]
     * @param context
     * @return
     */
    public static boolean isCurrNetworkEthernet(Context context) {
        return isCurrNetworkMatched(context, ConnectivityManager.TYPE_ETHERNET);
    }
    
    /**
     * @brief : [判断当前网络与指定的网络类型是否匹配]
     * @param context
     * @param netType
     * @return
     */
    private static boolean isCurrNetworkMatched(Context context, int netType) {
        int type = getConnectType(context);
        return type == netType;
    }
    
    /**
     * @brief : [判断是否有网络连接]
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {  
        if (context != null) {  
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
                    .getSystemService(Context.CONNECTIVITY_SERVICE);  
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
            if (mNetworkInfo != null) {  
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();  
            }  
        }  
        return false;  
    }
    
    
    /**
     * 
     * @brief : 获取本机IP地址
     * @return
     */
    public static String getLocalIP() {
//        String IP = null;
//        StringBuilder IPStringBuilder = new StringBuilder();
        try {
          Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
          while (networkInterfaceEnumeration.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
            Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
            while (inetAddressEnumeration.hasMoreElements()) {
              InetAddress inetAddress = inetAddressEnumeration.nextElement();
              if (!inetAddress.isLoopbackAddress()&& 
                  !inetAddress.isLinkLocalAddress()&& 
                  inetAddress.isSiteLocalAddress()) {
//                 IPStringBuilder.append(inetAddress.getHostAddress().toString()+"\n");*/
                   return inetAddress.getHostAddress().toString();
              }
            }
          }
        } catch (SocketException ex) {
            EvLog.e(ex.getMessage());
            UmengAgentUtil.reportError(ex);
        }
        return "";
//        IP = IPStringBuilder.toString();
//        return IP;
      }
    
    public static String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public static String getLocalMacAddress() {
        byte[] mac = new byte[1];
        String mac_s = "";
       try {                
            String localIP = getIpAddressString();
            if (TextUtils.isEmpty(localIP)) {
                localIP = getLocalIP();
            }
            EvLog.e("localIP:" + localIP);
            
            if (TextUtils.isEmpty(localIP)) {
                UmengAgentUtil.reportError("get Local Ip failed");
                return mac_s;
            }
            
            InetAddress addr = InetAddress.getByName(localIP);
            NetworkInterface ne = NetworkInterface.getByInetAddress(addr);
            mac = ne.getHardwareAddress();
            if (mac != null) {
                mac_s = NetUtils.byte2hex(mac);
            } 
       } catch (Exception e) {
           EvLog.e(e.getMessage());
           UmengAgentUtil.reportError(e);
           mac_s = "";
       }
       if (TextUtils.isEmpty(mac_s)) {
           UmengAgentUtil.reportError("get mac failed");
       }
       return mac_s;
    }
    
    public static  String byte2hex(byte[] b) {
        if (b == null) {
            return "";
        }
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        int len = b.length;
        for (int n = 0; n < len; n++) {
         stmp = Integer.toHexString(b[n] & 0xFF);
         if (stmp.length() == 1)
          hs = hs.append("0").append(stmp);
         else {
          hs = hs.append(stmp);
         }
        }
        return String.valueOf(hs);
    }
    
    /**
     * @brief : [根据掩码ip获取前缀长度]
     * @param maskIp
     * @return
     */
    public static int getPrefixLen(String maskIp) {
        try {
            InetAddress address = NetworkUtils.numericToInetAddress(maskIp);
            return countPrefixLength(address.getAddress());
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
            return -1;
        }
    }
    
    private static short countPrefixLength(byte[] maskIpAdd) {
        short count = 0;
        for (byte b : maskIpAdd) {
            for (int i = 0; i < 8; ++i) {
                if ((b & (1 << i)) != 0) {
                    ++count;
                }
            }
        }
        return count;
    }
    
    /**
     * @brief : [根据前缀长度获取掩码字符串]
     * @param prefixLen
     * @return
     */
    public static String getNetmask(int prefixLen) {
        int[] ip = new int[4];
        for(int i = 0; i < prefixLen; i++) {
            if(i < 8) {
                ip[0] += Math.pow(2, (7 - i));
            } else if (i >=8 && i < 16) {
                ip[1] += Math.pow(2, (15 - i));
            } else if (i >= 16 && i < 24) {
                ip[2] += Math.pow(2, (23 - i));
            } else if (i >= 24 && i < 32) {
                ip[3] += Math.pow(2, (31 - i));
            }
        }
        return ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
    }
    
    /**
     * 指定的String是否是 有效的IP地址. 
     */
    public static boolean isValidIpAddress(String value) {
        
        int start = 0;
        int end = value.indexOf('.');
        int numBlocks = 0;
        
        while (start < value.length()) {
            
            if ( -1 == end ) {
                end = value.length();
            }

            try {
                int block = Integer.parseInt(value.substring(start, end));
                if ((block > 255) || (block < 0)) {
                    EvLog.w("isValidIpAddress() : invalid 'block', block = " + block);
                    return false;
                }
            } catch (NumberFormatException e) {
                EvLog.w("isValidIpAddress() : e = " + e);
                UmengAgentUtil.reportError(e);
                return false;
            }
            
            numBlocks++;
            
            start = end + 1;
            end = value.indexOf('.', start);
        }
        
        return numBlocks == 4;
    }
    
    /**
     * @brief : [前缀长度是否有效]
     * @param prefixLen
     * @return
     */
    public static boolean isValidPrefixLen(String prefixLen) {
        if(TextUtils.isEmpty(prefixLen)) {
            return false;
        }
        int len = -1;
        try {
            len = Integer.parseInt(prefixLen);
            return len >= 0 && len <= 32;
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
            return false;
        }
    }
    
    /**
     * @brief : [判断子网掩码是否有效]
     * @param netmask
     * @return
     */
    public static boolean isValidNetmask(String netmask) {
        if(TextUtils.isEmpty(netmask)) {
            return false;
        }
        ArrayList<String> validNetmaskList = getValidNetmaskList();
        for (String validNetmask : validNetmaskList) {
            if(netmask.equals(validNetmask)) {
                return true;
            }
        }
        return false;
    }
    
    private static ArrayList<String> getValidNetmaskList() {
        ArrayList<String> validNetmaskList = new ArrayList<String>();
        for(int i = 0; i < 32; i++) {
            validNetmaskList.add(getNetmask(i));
        }
        return validNetmaskList;
    }
    
    public static class NetSpeedInfo {
        public int speed;
        public String unit;
    }
    public static NetSpeedInfo getNetSpeedWithUnit(float speed) {
        String speedUnit = "B/S";
        int speedInfo = (int)speed;
        if (speedInfo >= 1024*1024) {
            speedInfo = (int)(speedInfo/(1024*1024));
            speedUnit = "MB/S";
        } else if (speedInfo >= 1024) {
            speedInfo = (int)(speedInfo/1024);
            speedUnit = "KB/S";
        }
        final NetSpeedInfo speedInfoWithUnit = new NetSpeedInfo();
        speedInfoWithUnit.speed = speedInfo;
        speedInfoWithUnit.unit = speedUnit;
        return speedInfoWithUnit;
    }
    
   /* public static String getLocalIp(Context context){
        String host = null;
        if (context == null) {
            return "";
        }
        try {
            if (NetUtils.isCurrNetworkEthernet(context)) {
                if (System.getInt(context.getContentResolver(), ETHERNET_USE_STATIC_IP, 0) != 0) {
                    //静态
                    host = System.getString(context.getContentResolver(), ETHERNET_STATIC_IP);      
                } else {
                    String iface = getEthernetIfaceName();
                    if(iface != null) {
                        host = SystemProperties.get("dhcp."+ iface +".ipaddress");
                    }               
                }
            } else if (NetUtils.isCurrNetworkWifi(context)) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                host = NetworkUtils.intToInetAddress(wifiManager.getConnectionInfo().getIpAddress()).getHostAddress();
            }               
        } catch (Exception e) {
        }
        
        return host;
    }*/
    
    public static String getEthernetIfaceName() {
        try {
            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{ETHERNET_SERVICE});
            IEthernetManager ethernetManager = IEthernetManager.Stub.asInterface(binder);
            return ethernetManager.getEthernetIfaceName();
        } catch (Exception e) {
        }
        return null;
    }
    
    public static String convertIP(String ipv4){
        if (TextUtils.isEmpty(ipv4)){
            return "";
        }
        String temp = "";
        String[] ipnos = ipv4.split("\\.");
        for (int i = 0; i < ipnos.length; i++){
            if (ipnos[i].length() < 3) {
                if (ipnos[i].length() >= 2) {
                    ipnos[i] = "0" + ipnos[i];
                }else {
                    ipnos[i] = "00" + ipnos[i];
                }
            }
            temp = temp + ipnos[i];
        }
        return temp;
    }
    
    public static List<InetAddress> getInetAddressList(Context context){

        List<InetAddress> address = new ArrayList<InetAddress>();
        String host = null;
        if (context == null) {
            return address;
        }
        try {
            if (NetUtils.isCurrNetworkEthernet(context)) {
                if (System.getInt(context.getContentResolver(), ETHERNET_USE_STATIC_IP, 0) != 0) {
                    //静态
                    host = System.getString(context.getContentResolver(), ETHERNET_STATIC_IP);      
                } else {
                    String iface = getEthernetIfaceName();
                    if(iface != null) {
                        host = SystemProperties.get("dhcp."+ iface +".ipaddress");
                    }               
                }
            } else if (NetUtils.isCurrNetworkWifi(context)) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                host = NetworkUtils.intToInetAddress(wifiManager.getConnectionInfo().getIpAddress()).getHostAddress();
            }   
            if (!TextUtils.isEmpty(host)) {
                address.add(InetAddress.getByName(host));
            }           
        } catch (Exception e) {
        }
        
        return address;
    }
    
    /**
     * 
     * @brief : 获取本机IP地址
     * @return
     */
    public static List<InetAddress> getLocalIPList() {
        List<InetAddress> address = new ArrayList<InetAddress>();
        try {
          Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
          while (networkInterfaceEnumeration.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
            Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
            while (inetAddressEnumeration.hasMoreElements()) {
              InetAddress inetAddress = inetAddressEnumeration.nextElement();
              if (!inetAddress.isLoopbackAddress()&& 
                      !inetAddress.isLinkLocalAddress()&& 
                      inetAddress.isSiteLocalAddress()) {
                  address.add(InetAddress.getByName(inetAddress.getHostAddress().toString()));
              }
            }
          }
        } catch (SocketException ex) {
        }catch (UnknownHostException e) {
        }
        return address;
    }
    
    /**
     * [功能说明] 获取Eth0 MAC ，格式：00:00:00:00:00:00
     * @return
     */
    public static String getEthernetMacFormatString() {
        String result = "";
        String Mac = "";
        result = callCmd("busybox ifconfig", "HWaddr");

        // 如果返回的result == null，则说明网络不可取
        if (result == null) {
            return "网络出错，请检查网络";
        }

        // 对该行数据进行解析
        // 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
        if (result.length() > 0 && result.contains("HWaddr") == true) {
            Mac = result.substring(result.indexOf("HWaddr") + 6,
                    result.length() - 1);

            if (Mac.length() > 1) {
                Mac = Mac.replaceAll(" ", "");
                result = Mac;
            }
        }
        return result;
    }
    
    /**
     * [功能说明] 执行shell命令
     * @param cmd
     * @param filter
     * @return
     */
    public static String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            // 执行命令cmd，只取结果中含有filter的这一行
            while ((line = br.readLine()) != null
                    && line.contains(filter) == false) {
                // result += line;
            }

            result = line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static String getMacAddr() {
        final String fileEthPath = "/sys/class/net/eth0/address";
        final String fileWlanPath = "/sys/class/net/wlan0/address";
        String filePath = fileEthPath;
        File file = new File(filePath);
        if (!file.exists()) {
            EvLog.e("getMacAddr " + filePath + " not exist");
            file = null;
            filePath = fileWlanPath;
            file = new File(filePath);
            if (!file.exists()) {
                EvLog.e("getMacAddr " + filePath + " not exist");
                UmengAgentUtil.reportError("getMacAddr failed");
                return "";
            }
        }

        String macSerial = "";
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat " + filePath);
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        macSerial = macSerial.replace(":", "");
        return macSerial;
    }

}
