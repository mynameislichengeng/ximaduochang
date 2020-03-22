package com.evideo.kmbox.exception;

public class DCDenyOfServiceException extends DataCenterCommuException {
    /** [描述变量作用] */
    private static final long serialVersionUID = 1L;
    String msg="";

    public DCDenyOfServiceException() {
        this.msg = "";
    }

    public DCDenyOfServiceException(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Data Center Deny Of Service Exception:" + this.msg;
    }
    
    @Override
    public String getMessage() {
        return "Data Center Deny Of Service Exception:" + this.msg;
    }
}
