package com.evideo.kmbox.model.observer.net;

import java.util.ArrayList;
import java.util.List;

import com.evideo.kmbox.model.observer.BaseSubject;

/**
 * @brief : [以太网信息主题]
 */
public class EthernetInfoSubject extends BaseSubject<IEthernetInfoObserver> implements IEthernetInfoSubject {
    
    private static EthernetInfoSubject instance;
    
    private EthernetInfoSubject() {
    }
    
    public static EthernetInfoSubject getInstance() {
        if(instance == null) {
            instance = new EthernetInfoSubject();
        }
        return instance;
    }

    @Override
    public void registEthernetInfoObserver(IEthernetInfoObserver observer) {
        registObserver(observer);
    }

    @Override
    public void unregistEthernetInfoObserver(IEthernetInfoObserver observer) {
        unregistObserver(observer);
    }

    @Override
    public void notifyEthernetStateChanged(int state) {
        List<IEthernetInfoObserver> observers = new ArrayList<IEthernetInfoObserver>(getObservers());
        for (IEthernetInfoObserver iEthernetInfoObserver : observers) {
            iEthernetInfoObserver.onEthernetStateChanged(state);
        }
    }

}
