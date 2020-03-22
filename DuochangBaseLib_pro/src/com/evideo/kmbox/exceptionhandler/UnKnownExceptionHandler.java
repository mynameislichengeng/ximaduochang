package com.evideo.kmbox.exceptionhandler;

import com.evideo.kmbox.util.EvLog;

public class UnKnownExceptionHandler implements ExceptionHandlerInterface {

    private UnKnownExceptionHandler() {
    }

    public static final UnKnownExceptionHandler getInstance() {

        return UnKnownExceptionHandlerHolder.INSTANCE;
    }

    private static class UnKnownExceptionHandlerHolder {
        private static final UnKnownExceptionHandler INSTANCE = new UnKnownExceptionHandler();
    }

    @Override
    public void handle(Exception ex) {

        //TODO: 上报友盟
        EvLog.e("Serial Number is not exist, please contact...!" + ex.getLocalizedMessage());
    }
}
