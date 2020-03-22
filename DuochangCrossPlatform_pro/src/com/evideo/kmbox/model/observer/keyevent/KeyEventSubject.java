/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-5-8     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.model.observer.keyevent;

import java.util.ArrayList;
import java.util.List;

import com.evideo.kmbox.model.observer.BaseSubject;

/**
 * [功能说明]
 */
public final class KeyEventSubject extends BaseSubject<IKeyEventObserver> implements
        IKeyEventSubject {
    
    private static KeyEventSubject sInstance;
    
    private KeyEventSubject() {
    }
    
    public static KeyEventSubject getInstance() {
        if (sInstance == null) {
            sInstance = new KeyEventSubject();
        }
        return sInstance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerKeyEventObserver(IKeyEventObserver observer) {
        registObserver(observer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterKeyEventObserver(IKeyEventObserver observer) {
        unregistObserver(observer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyHomeKeyPressed() {
        List<IKeyEventObserver> observers = new ArrayList<IKeyEventObserver>(getObservers());
        for (IKeyEventObserver observer : observers) {
            observer.onHomeKeyPressed();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean notifyBackKeyPressed() {
        List<IKeyEventObserver> observers = new ArrayList<IKeyEventObserver>(getObservers());
        boolean ret = false;
        for (IKeyEventObserver observer : observers) {
            if (observer.onBackKeyPressed()) {
                ret = true;
            }
        }
        return ret;
    }
}
