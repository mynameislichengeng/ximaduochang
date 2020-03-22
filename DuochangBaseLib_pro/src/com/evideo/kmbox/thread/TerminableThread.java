package com.evideo.kmbox.thread;

public class TerminableThread extends AbsTerminableThread {
    @Override
    protected void runTask(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName("TerminableThread");
        thread.start();
    }
}
