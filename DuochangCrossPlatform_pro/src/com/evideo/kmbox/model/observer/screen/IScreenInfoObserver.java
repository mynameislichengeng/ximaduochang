package com.evideo.kmbox.model.observer.screen;

public interface IScreenInfoObserver {
    /**
     * @brief : [wifi状态改变，在UI线程中执行]
     * @param isConnected    是否连接
     */
    public void onScreenStateChange(boolean isScreenOn);
}
