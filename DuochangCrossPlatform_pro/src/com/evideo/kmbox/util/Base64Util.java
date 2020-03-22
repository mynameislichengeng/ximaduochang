package com.evideo.kmbox.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.commons.codec.binary.Base64;

import com.evideo.kmbox.model.umeng.UmengAgentUtil;


public class Base64Util {
    
    /**
     * @brief : [把Serializable对象进行base64编码成字符串]
     * @param s
     * @return
     */
    public static String encodeBase64(Serializable s) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(s);
            String base64Str = new String(Base64.encodeBase64(baos.toByteArray()));
            return base64Str;
        } catch (Exception e) {
            EvLog.e("base64编码异常: " + e.getMessage());
            return null;
        } finally {
            if(baos != null) {
                try {
                    baos.close();
                    baos = null;
                } catch (IOException e) {
                    EvLog.e("base64编码关闭资源异常: " + e.getMessage());
                    UmengAgentUtil.reportError(e);
                }
            }
            if(oos != null) {
                try {
                    oos.close();
                    oos = null;
                } catch (IOException e) {
                    EvLog.e("base64编码关闭资源异常: " + e.getMessage());
                    UmengAgentUtil.reportError(e);
                }
            }
        }
    }
    
    /**
     * @brief : [把字符串进行base64解码]
     * @param base64Str
     * @return
     */
    public static Object decodeBase64(String base64Str) {
        byte[] base64 = Base64.decodeBase64(base64Str.getBytes());
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            EvLog.e("base64解码异常: " + e.getMessage());
            UmengAgentUtil.reportError(e);
            return null;
        } finally {
            if(bais != null) {
                try {
                    bais.close();
                    bais = null;
                } catch (IOException e) {
                    EvLog.e("base64解码关闭资源异常: " + e.getMessage());
                    UmengAgentUtil.reportError(e);
                }
            }
            if(ois != null) {
                try {
                    ois.close();
                    ois = null;
                } catch (IOException e) {
                    EvLog.e("base64解码关闭资源异常: " + e.getMessage());
                    UmengAgentUtil.reportError(e);
                }
            }
        }
    }

}
