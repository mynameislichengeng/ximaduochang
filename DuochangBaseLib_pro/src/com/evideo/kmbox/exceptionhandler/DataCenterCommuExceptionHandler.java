package com.evideo.kmbox.exceptionhandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.evideo.kmbox.exception.DCDenyOfServiceException;
import com.evideo.kmbox.exception.DCProtocolParseException;
import com.evideo.kmbox.util.EvLog;

public class DataCenterCommuExceptionHandler implements ExceptionHandlerInterface {

    private DataCenterCommuExceptionHandler() {        
    }

    public static final DataCenterCommuExceptionHandler getInstance() {

        return DataCenterCommuExceptionHandlerHolder.INSTANCE;
    }

    private static class DataCenterCommuExceptionHandlerHolder {
        
        private static final DataCenterCommuExceptionHandler INSTANCE = new DataCenterCommuExceptionHandler();
    }
    
    @Override
    public void handle(Exception ex) {
        
        if (ex == null) {
            return;
        }
        
        if(ex instanceof DCDenyOfServiceException) {
            EvLog.e("Login failed:" + ex.getLocalizedMessage());
        } else if (ex instanceof DCProtocolParseException) {
            EvLog.e("Protocol Parse error:" + ex.getLocalizedMessage());
        } else {
            EvLog.e("Other error:" + ex.getLocalizedMessage());
        }
    }
    
    /**
     * [功能说明]获取异常堆栈信息
     * @param e 异常
     * @return 异常堆栈信息
     */
    public String getStackTraceStr(Exception e) {
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
