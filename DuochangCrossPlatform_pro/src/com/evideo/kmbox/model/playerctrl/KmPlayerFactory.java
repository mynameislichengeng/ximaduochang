/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-8-12     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.playerctrl;

/**
 * [功能说明]
 */
public class KmPlayerFactory {
    private static KmPlayerFactory instance = null;
    
    private KmPlayManager mNormalPlayer = null;
//    private KmPlayBackManager mBackPlayer = null;
    
    
    public static KmPlayerFactory getInstance() {
        if(instance == null) {
            synchronized (KmPlayerFactory.class) {
                KmPlayerFactory temp = instance;
                if(temp == null) {
                  temp = new KmPlayerFactory();
                  instance = temp;
                }
            }
         }
        return instance;
    }

    @Override
    public void finalize() {
        instance = null;
    }
    
    private KmPlayerFactory() {
    }
        
    public void destoryNormalPlayer() {
        if (mNormalPlayer != null) {
            mNormalPlayer.destoryPlayer();
            mNormalPlayer = null;
        }
    }
    
    /*public void destoryPlayBackPlayer() {
        if (mBackPlayer != null) {
            mBackPlayer.destoryPlayer();
            mBackPlayer = null;
        }
    }*/
    
    public KmPlayManager getNormalPlayer() {
        if (mNormalPlayer == null) {
            mNormalPlayer = new KmPlayManager();
        }
        return mNormalPlayer;
    }
    
   /* public KmPlayBackManager getPlayBackPlayer() {
        if (mBackPlayer == null) {
            mBackPlayer = new KmPlayBackManager();
        }
        return mBackPlayer;
    }*/
   
    public void unInit() {
        /*if (mBackPlayer != null) {
            mBackPlayer.uninit();
            mBackPlayer = null;
        } */
        if (mNormalPlayer != null) {
            mNormalPlayer.uninit();
            mNormalPlayer = null;
        } 
    }
}
