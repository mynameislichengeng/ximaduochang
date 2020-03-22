package com.evideo.kmbox.exception;

public class NoSerialNumberException extends Exception {

    private static final long serialVersionUID = 1L;
    String msg="";

    public NoSerialNumberException() {
        this.msg = "";
    }

    public NoSerialNumberException(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "System has NO Serial Number:" + this.msg;
    }
    
    @Override
    public String getMessage() {
        return "Serial Number is empty:" + this.msg;
    }
}
