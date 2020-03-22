package com.evideo.kmbox.model.observer.drawer;

import java.util.ArrayList;
import java.util.List;

import com.evideo.kmbox.model.observer.BaseSubject;
import com.evideo.kmbox.util.EvLog;

/**
 * @brief : [抽屉状态主题]
 */
public class DrawerStateSubject extends BaseSubject<IDrawerStateObserver> implements IDrawerStateSubject {
    
    private static DrawerStateSubject instance;
    
    private DrawerStateSubject() {
    }
    
    public static DrawerStateSubject getInstance() {
        if(instance == null) {
            instance = new DrawerStateSubject();
        }
        return instance;
    }
    
    private boolean needResetFocus = false;
    
    public boolean isNeedResetFocus() {
        return needResetFocus;
    }

    public void setNeedResetFocus(boolean needResetFocus) {
        this.needResetFocus = needResetFocus;
    }

    @Override
    public void registDrawerStateObserver(IDrawerStateObserver observer) {
        registObserver(observer);
    }

    @Override
    public void unregistDrawerStateObserver(IDrawerStateObserver observer) {
        unregistObserver(observer);
    }

    @Override
    public void notifyDrawerClosed(int level) {
        List<IDrawerStateObserver> observers = new ArrayList<IDrawerStateObserver>(getObservers());
        EvLog.d("DrawerStateSubject__notifyDrawerClosed__observer.size: " + observers.size());
        for (IDrawerStateObserver iDrawerStateObserver : observers) {
            iDrawerStateObserver.onDrawerClosed(level, needResetFocus);
        }
    }
    
    @Override
    public void notifyDrawerOpened(int level) {
        List<IDrawerStateObserver> observers = new ArrayList<IDrawerStateObserver>(getObservers());
        for (IDrawerStateObserver iDrawerStateObserver : observers) {
            iDrawerStateObserver.onDrawerOpened(level);
        }
    }
    
    @Override
    public void notifyDrawerResetSeletedId(int level) {
        List<IDrawerStateObserver> observers = new ArrayList<IDrawerStateObserver>(getObservers());
        for (IDrawerStateObserver iDrawerStateObserver : observers) {
            iDrawerStateObserver.onResetSeletedId(level);
        }
    }

    @Override
    public void notifyNetSettingViewChanged(boolean isEthernet) {
        List<IDrawerStateObserver> observers = new ArrayList<IDrawerStateObserver>(getObservers());
        for (IDrawerStateObserver iDrawerStateObserver : observers) {
            iDrawerStateObserver.onNetSettingViewChanged(isEthernet);
        }
    }

}
