package com.evideo.kmbox.thread;

public interface ITerminableThread {
    public void start();
    public void cancel();
    public boolean isCancel();
}
