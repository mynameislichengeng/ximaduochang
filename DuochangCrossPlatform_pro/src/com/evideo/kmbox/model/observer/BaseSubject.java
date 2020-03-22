package com.evideo.kmbox.model.observer;

import java.util.LinkedList;
import java.util.List;

import com.evideo.kmbox.BaseApplication;

import android.content.Context;

/**
 * @brief : [主题的基类]
 * @param <Observer>
 */
public class BaseSubject<Observer> {
    
    private LinkedList<Observer> mObservers = new LinkedList<Observer>();
    
    /**
     * @brief : [注册观察者]
     * @param observer
     */
    protected void registObserver(Observer observer) {
        if(!mObservers.contains(observer)){
            mObservers.add(observer);
        }
    }

    /**
     * @brief : [注销观察者]
     * @param observer
     */
    protected void unregistObserver(Observer observer) {
        if(mObservers.contains(observer)){
            mObservers.remove(observer);
        }
    }
    
    /**
     * @brief : [初始化主题]
     */
    public void initSubject() {
        mObservers.clear();
    }

    /**
     * @brief : [资源释放]
     */
    public void release() {
        mObservers.clear();
    }

    /**
     * @brief : [获取所有的观察者]
     * @return
     */
    public List<Observer> getObservers(){
        return mObservers;
    }
    
    public Context getContext() {
        return BaseApplication.getInstance();
    }

}
