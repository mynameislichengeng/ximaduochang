package com.evideo.kmbox.presenter;

/**
 * @brief : [异步任务加载数据的回调]
 */
public abstract class LoadDataCallback {
    
    public void onPreRun() {
        
    }
    
    public abstract void onPostRun(Object... params);
    
    public void onFailRun(Object... params) {
        
    }

}
