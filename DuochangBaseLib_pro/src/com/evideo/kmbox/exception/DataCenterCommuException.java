package com.evideo.kmbox.exception;

public class DataCenterCommuException extends Exception {

    /** [描述变量作用] */
    private static final long serialVersionUID = 1L;
    String msg="";

    public DataCenterCommuException() {
        this.msg = "";
    }

    public DataCenterCommuException(String msg) {
        this.msg = msg;
    }

    public DataCenterCommuException(String function, String errorCode) {
        this.msg = "[function=" + function+ "][response errorCode=" + errorCode+"]";
    }
    
    public DataCenterCommuException(String function, String errorCode,String errorMessage) {
        this.msg = "[function=" + function+ "][response errorCode=" + errorCode+"]" + "[errorMessage:" + errorMessage+"]";
    }

    @Override
    public String toString() {
        return "[DataCenterCommuException]" + this.msg;
    }
    
    @Override
    public String getMessage() {
        return "[DataCenterCommuException]" + this.msg;
    }
}
