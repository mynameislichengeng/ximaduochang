/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年1月5日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.observer.playstate;


import java.util.ArrayList;
import java.util.List;
import com.evideo.kmbox.model.observer.BaseSubject;

/**
 * [功能说明]
 */
public class PlayStateSubject  extends BaseSubject<IPlayStateObserver> implements IPlayStateSubject {
    
    private static PlayStateSubject instance;
    
    private PlayStateSubject() {
    }
    
    public static PlayStateSubject getInstance() {
        if(instance == null) {
            instance = new PlayStateSubject();
        }
        return instance;
    }

    @Override
    public void registPlayStateObserver(IPlayStateObserver observer) {
        registObserver(observer);
    }

    @Override
    public void unregistPlayStateObserver(IPlayStateObserver observer) {
        unregistObserver(observer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyPlayStart() {
        List<IPlayStateObserver> observers = new ArrayList<IPlayStateObserver>(getObservers());
        for (IPlayStateObserver iObserver : observers) {
            iObserver.onPlayStart();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyDowningStart() {
        List<IPlayStateObserver> observers = new ArrayList<IPlayStateObserver>(getObservers());
        for (IPlayStateObserver iObserver : observers) {
            iObserver.onDowningStart();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean notifyPlayStop(int stopState,String shareCode) {
        List<IPlayStateObserver> observers = new ArrayList<IPlayStateObserver>(getObservers());
        boolean ret = false;
        for (IPlayStateObserver iObserver : observers) {
            if (iObserver.onPlayStop(stopState,shareCode)) {
                ret = true;
            }
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyPlayError(String errMessage) {
        List<IPlayStateObserver> observers = new ArrayList<IPlayStateObserver>(getObservers());
        for (IPlayStateObserver iObserver : observers) {
            iObserver.onPlayError(errMessage);
        }
    }

}
