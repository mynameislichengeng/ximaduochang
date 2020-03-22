package com.evideo.kmbox.util;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by PoPEyE on 2019/11/18.
 */

public class HttpUtils {

    private static String sign;


    public static String getXML(final String xml, final String path) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                InputStream in = null;   //读
                int responseCode = 0;    //远程主机响应的HTTP状态码
                Log.i("gsp", "run: ++XML文件是"+xml.toString());
                try {


                    byte[] xmlbyte= xml.getBytes("UTF-8");

                    URL url = new URL(path);
                    HttpURLConnection conn= (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(30000);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestProperty("Content-Type","text/xml");
                    conn.setRequestProperty("Content-Length",
                            String.valueOf(xmlbyte.length));

                    conn.getOutputStream().write(xmlbyte);
                    conn.getOutputStream().flush();
                    conn.getOutputStream().close();
                    Log.i("gsp", "run: 返回状态"+conn.getResponseCode());
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("请求url失败");
                    }
                    InputStream is = conn.getInputStream();// 获取返回数据
                    // 使用输出流来输出字符(可选)
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = is.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                    sign = out.toString("UTF-8");

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sign.getBytes());

                    XMLUtlis.signXml(byteArrayInputStream);

                    Log.i("gsp", "run: 获取到的数据"+sign);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return sign ;
    }
}
