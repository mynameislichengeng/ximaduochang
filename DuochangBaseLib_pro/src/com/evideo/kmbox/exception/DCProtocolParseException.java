package com.evideo.kmbox.exception;

public class DCProtocolParseException extends DataCenterCommuException {

    /** [描述变量作用] */
    private static final long serialVersionUID = 1L;
    String msg="";

    public DCProtocolParseException() {
        this.msg = "";
    }

    public DCProtocolParseException(String msg) {
        this.msg = msg;
    }
    
    public DCProtocolParseException(String expectedId, String actualId) {
        this.msg = "Expected function:" + expectedId +", but recieved function: " + actualId;
    }

    @Override
    public String toString() {
        return "[DataCenterProtocolParseException]" + this.msg;
    }
    
    @Override
    public String getMessage() {
        return "[DataCenterProtocolParseException]" + this.msg;
    }
}
