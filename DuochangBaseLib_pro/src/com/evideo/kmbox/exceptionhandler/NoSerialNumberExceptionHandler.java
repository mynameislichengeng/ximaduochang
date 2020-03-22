package com.evideo.kmbox.exceptionhandler;

import com.evideo.kmbox.util.EvLog;

public class NoSerialNumberExceptionHandler implements ExceptionHandlerInterface {

    private NoSerialNumberExceptionHandler() {        
    }

    public static final NoSerialNumberExceptionHandler getInstance() {

        return NoSerialNumberExceptionHandlerHolder.INSTANCE;
    }

    private static class NoSerialNumberExceptionHandlerHolder {
        private static final NoSerialNumberExceptionHandler INSTANCE = new NoSerialNumberExceptionHandler();
    }
    
    @Override
    public void handle(Exception ex) {
        EvLog.e("Serial Number is not exist, please contact...!"
                + ex.getLocalizedMessage());
    }
}
