package com.evideo.kmbox.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import android.util.Log;



public class EvLog {

    private static String m_stag = "KmBox_Logger";

    private static boolean debug = true;
 
/*    private static EvLog instance = new EvLog();
 

    private EvLog() {
    }
 
    public static EvLog getLogger() {
        return instance;
    }*/
 
    private static String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
 
        if (sts == null) {
            return null;
        }
 
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
 
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
 
            if (st.getFileName().equals("EvLog.java")) {
                continue;
            }
 
            return "[" + Thread.currentThread().getName() + "(" + Thread.currentThread().getId()
                    + "): " + st.getFileName() + ":" + st.getLineNumber() + "]";
        }
 
        return null;
    }
 
    private static String createMessage(String msg) {
        String functionName = getFunctionName();
        String message = (functionName == null ? msg : (functionName + " - " + msg));
        return message;
    }
    
    public static void i(String tag, String msg) {
        if(debug) {
            Log.i(tag, createMessage(msg));
        }
    }
 
    /**
     * log.i
     */
    public static void i(String msg) {
        i(m_stag, msg);
    }
    
    public static void v(String tag, String msg) {
        if(debug) {
            Log.v(tag, createMessage(msg));
        }
    }
 
    /**
     * log.v
     */
    public static void v(String msg) {
        v(m_stag, msg);
    }
    
    public static void d(String tag, String msg) {
        if(debug) {
            Log.d(tag, createMessage(msg));
        }
    }
 
    /**
     * log.d
     */
    public static void d(String msg) {
        d(m_stag, msg);
    }
    
    public static void e(String tag, String msg) {
        if(debug) {
            Log.e(tag, createMessage(msg));
        }
    }
 
    /**
     * log.e
     */
    public static void e(String msg) {
        e(m_stag, msg);
    }
    
    public static void w(String tag, String msg) {
        if(debug) {
            Log.w(tag, createMessage(msg));
        }
    }
    
    /**
     * log.w
     */
    public static void w(String msg) {
        w(m_stag, msg);
    }
    
    /**
     * log.error 
     */
    public static void error(Exception e) {
        if(debug) {
            StringBuffer sb = new StringBuffer();
            String name = getFunctionName();
            StackTraceElement[] sts = e.getStackTrace();
 
            if (name != null) {
                sb.append(name+" - "+e+"\r\n");
            } else {
                sb.append(e+"\r\n");
            }
            if (sts != null && sts.length > 0) {
                for (StackTraceElement st:sts) {
                    if (st != null) {
                        sb.append("[ "+st.getFileName()+":"+st.getLineNumber()+" ]\r\n");
                    }
                }
            }
            Log.e(m_stag,sb.toString());
        }
    }
 
    public static void setTag(String tag) {
        m_stag = tag;
    }
 
    /**
     * set debug
     */
    public static void setDebug(boolean d) {
        debug = d;
    }
    
    public static boolean isDebug() {
        return debug;
    }
    
    /**
     * [功能说明]输出列表信息
     * @param list 列表
     */
    public static <T> void printList(List<T> list) {
        if (!debug) {
            return;
        }
        for (T t : list) {
            EvLog.d(t.toString());
        }
    }
    
    /**
     * [功能说明]输出列表信息
     * @param tag logcat的tag
     * @param list 列表
     */
    public static <T> void printList(String tag, List<T> list) {
        if (!debug) {
            return;
        }
        for (T t : list) {
            EvLog.d(tag, t.toString());
        }
    }
    
    /**
     * [功能说明]输出异常的栈堆信息
     * @param tag logcat的tag
     * @param e 异常
     */
    public static void eStackTrace(String tag, Exception e) {
        if (e == null) {
            return;
        }
        EvLog.e(tag, getStackTraceStr(e));
    }
    
    /**
     * [功能说明]输出异常的栈堆信息
     * @param e 异常
     */
    public static void eStackTrace(Exception e) {
        EvLog.eStackTrace(m_stag, e);
    }
    
    /**
     * [功能说明]获取异常堆栈信息
     * @param e 异常
     * @return 异常堆栈信息
     */
    public static String getStackTraceStr(Exception e) {
        if (e == null) {
            return null;
        }
        StringWriter sw = null;
        PrintWriter pw = null;
        String strackTraceStr = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
            strackTraceStr = sw.toString();
        } catch (Exception e2) {
            EvLog.e("getStackTraceStr error " + e2.getMessage());
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e3) {
                    EvLog.e("getStackTraceStr sw close error " + e3.getMessage());
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return strackTraceStr;
    }
}