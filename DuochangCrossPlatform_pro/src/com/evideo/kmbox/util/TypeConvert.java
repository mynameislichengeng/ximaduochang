package com.evideo.kmbox.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import android.text.TextUtils;

import com.evideo.kmbox.model.umeng.UmengAgentUtil;

/**
 * @brief      : String 和 bytebuffer相互之间的转换,K米，单包多机采用UTF-8编码
 */
public class TypeConvert {
    /** 
     * String 转换 ByteBuffer 
     * @param str 
     * @return 
     */  
    public static ByteBuffer getByteBuffer(String str)  
    {  
        if(str != null){
            try {
                return ByteBuffer.wrap(str.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
            }
        }
        return null;  
    }  
  
    /** 
     * ByteBuffer 转换 String 
     * @param buffer 
     * @return 
     */  
    public static String getString(ByteBuffer buffer)  
    {  
        Charset charset = null;  
        CharsetDecoder decoder = null;  
        CharBuffer charBuffer = null;  
        if(buffer != null){
            ByteBuffer bu = ByteBuffer.wrap(buffer.array());
            try  
            {  
                charset = Charset.forName("UTF-8");  
                decoder = charset.newDecoder();  
                // charBuffer = decoder.decode(buffer);//用这个的话，只能输出来一次结果，第二次显示为空  
                charBuffer = decoder.decode(bu.asReadOnlyBuffer());  
                return charBuffer.toString();  
            }  
            catch (Exception ex)  
            {  
                EvLog.e(ex.getMessage());
                UmengAgentUtil.reportError(ex); 
                return null;  
            }  
        }
        return null;
    }  
    
    
    
    /** 
     * string 转换 byte[] 
     * @param buffer 
     * @return 
     */ 
    public static byte[] getBytes(String str){
        byte[] temp = null;
        if(str != null){
            try {
                temp = str.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
            }
        }
        return temp;
    }
    
    /** 
     * 截取buffer 
     * @param buffer 
     * @return 
     */ 
    public static String getString(ByteBuffer buffer, int start, int end){         
        Charset charset = null;  
        CharsetDecoder decoder = null;  
        CharBuffer charBuffer = null;
        String str = null;
        if(buffer != null){
            ByteBuffer bu = ByteBuffer.wrap(buffer.array());
            bu.clear();
            if(start >= 0 && end >= 0 && start <= end  && end <= bu.capacity()){
                try{ 
                    bu.position(start);
                    bu.limit(end);
                    charset = Charset.forName("UTF-8");  
                    decoder = charset.newDecoder();  
                    // charBuffer = decoder.decode(buffer);//用这个的话，只能输出来一次结果，第二次显示为空  
                    charBuffer = decoder.decode(bu.asReadOnlyBuffer());  
                    str =  charBuffer.toString();  
                }  
                catch (Exception ex)  
                {  
                    EvLog.e(ex.getMessage());
                    UmengAgentUtil.reportError(ex); 
                    str = null;  
                }  
            }else{
                str = null;
            }
        }
        return str;
    }
    
    public static String getString(byte[] data){
        String temp = null;
        try {
            temp = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
            temp = null;
        }
        return temp;
    }
    
    // 完整的判断中文汉字和符号
    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }
    
    public static String strTrim(String str){
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        return str.replaceAll("\\s*", "");
    }
    
}
