package com.evideo.kmbox.model.observer.activity;

import java.util.ArrayList;
import java.util.List;

import com.evideo.kmbox.model.observer.BaseSubject;
import com.evideo.kmbox.util.EvLog;

/**
 * @brief : [activity主题]
 */
public class ActivitySubject extends BaseSubject<IActivityObserver> implements IActivitySubject {
    
    private static ActivitySubject instance;
    
    private ActivitySubject() {
    }
    
    public static ActivitySubject getInstance() {
        if(instance == null) {
            instance = new ActivitySubject();
        }
        return instance;
    }

    @Override
    public void registActivityObserver(IActivityObserver observer) {
        registObserver(observer);
    }

    @Override
    public void unregistActivityObserver(IActivityObserver observer) {
        unregistObserver(observer);
    }

    @Override
    public void notifyBackPressed() {
        List<IActivityObserver> observers = new ArrayList<IActivityObserver>(getObservers());
        EvLog.d("ActivitySubject__notifyBackPressed    observers__size: " + observers.size());
        for (IActivityObserver iActivityObserver : observers) {
            if(iActivityObserver.onBackPressed()) {
                break;
            }
        }
    }

}
