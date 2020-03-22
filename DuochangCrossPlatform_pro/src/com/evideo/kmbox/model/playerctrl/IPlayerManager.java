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

import android.view.SurfaceHolder;
import com.evideo.kmbox.model.player.IKmPlayer;
import com.evideo.kmbox.model.player.KmAudioTrackMode;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrlType.IPlayerManagerListener;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrlType.PlayDataState;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrlType.PlayerCtrlState;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明]
 */
public abstract class IPlayerManager {
    public static final int DEFAULT_SONG_DURATION = 1000*5*60;
    /** [是否使用httpd代理访问播放链] */
    public boolean mUseHttpd = true;
    protected boolean mIsCurrentSongValid = true;
    protected KmPlayListItem mCurSong = null;
    protected IKmPlayer mPlayer = null;
    protected SurfaceHolder mHolder = null;
    protected IPlayerManagerListener mListener = null;
    protected int mStateBeforeBuffering = PlayerCtrlState.STATE_IDLE;
    protected boolean mIsInited = false;
    protected int mState = PlayerCtrlState.STATE_IDLE;
    protected DecodeErrorInfo mDecodeErrorInfo = null;
    protected int mDataState = PlayDataState.STATE_NONE;
    protected boolean mEnable = true;
    private static final String TAG = IPlayerManager.class.getSimpleName();
    
    public void enable() {
        mEnable = true;
    }
    
    public void disable() {
        mEnable = false;
    }

    public int getDataState() {
        return mDataState;
    }
    public void setListener(IPlayerManagerListener listener) {
        mListener = listener;
    }
    
    protected void notifyOnStateChange(int state,  Object param ){
        if (mListener != null&& mEnable) {
            mListener.onStateChange(state, param);
        }
    }
    
    protected boolean notifyOnBufferingChange(int percent) {
        if (mListener != null&& mEnable) {
            mListener.onBufferingChange(percent);
            return true;
        }
        return false;
    }
    
    protected boolean notifyUpdateDownPercent(int percent,float rate){
        if (mListener != null&& mEnable) {
            mListener.updateDownPercent(percent, rate);
            return true;
        }
        return false;
    }
    
    protected boolean notifyUpdateDownState() {
        if (mListener != null&& mEnable) {
            mListener.updateDownState();
            return true;
        }
        return false;
    }
    
    protected boolean notifyOnDataReady(int serialNum) {
        if (mListener != null&& mEnable) {
            mListener.onDataReady(serialNum);
            return true;
        }
        return false;
    }
    /**
     * [功能说明] 获取当前模式
     * @return
     */
    public abstract int getMode();
    /**
     * [功能说明] 初始化播放器
     * @return
     */
    protected abstract boolean initPlayer();
    /**
     * [功能说明] 开始播放
     * @return
     */
    public abstract boolean start();
    /**
     * [功能说明] 恢复播放
     * @param stateNotify 是否需要通知监听者状态变化
     * @return
     */
    public abstract boolean play(boolean stateNotify);
    /**
     * [功能说明] 暂停播放
     * @param stateNotify 是否需要通知监听者状态变化
     * @return
     */
    public abstract boolean pause(boolean stateNotify);
    /**
     * [功能说明] 停止播放
     * @param stateNotify 是否需要通知监听者状态变化
     * @return
     */
    public abstract boolean stop(boolean stateNotify);
    /**
     * [功能说明] 销毁资源
     * @return
     */
    public abstract boolean uninit();
    
    /**
     * [功能说明] 播放列表变化
     */
    public abstract void listChange();
    
    public abstract void seekToTime(long time);
    
    public abstract void createPlayer();
    
    public void setSong(KmPlayListItem song) {
        if (song == null) {
            mCurSong = null;
            return;
        }
        
        if (mCurSong == null) {
            mCurSong = new KmPlayListItem(song);
        } else {
            synchronized (mCurSong)  {
                mCurSong.copy(song);
            }
        }
    }
    
    public void setCurrentSongValid(boolean valid) {
        mIsCurrentSongValid = valid;
    }
    /**
     * 获取在播歌曲
     * @return 返回当前在播歌曲
     */
    public KmPlayListItem getSong() {
        return mCurSong;
    }
    public boolean isDecoding() {
        if (mState == PlayerCtrlState.STATE_PLAY
                || mState == PlayerCtrlState.STATE_PAUSE
                || mState == PlayerCtrlState.STATE_BUFFERING
                || mState == PlayerCtrlState.STATE_PREPARING
                || mState == PlayerCtrlState.STATE_PREPARED) {
            return true;
        } else {
            if (mCurSong != null) {
                EvLog.d(mCurSong.getSongName() + " is not Decoding, mState="
                        + mState);
            }
        }
        return false;
    }

    public boolean isInit() {
        return mIsInited;
    }

    public int getPlayerState() {
        return mState;
    }
    
    
    /**
     * 获取播放器时间进度
     * @return 返回当前已播放时间
     */
    public int getPlayedTime() {
        return (mPlayer != null) ? (mPlayer.getCurrentPosition()) : (-1);
    }
    
    public int getAudioTime() {
        if (mPlayer == null) {
            return 0;
        }
        return mPlayer.getAudioTime();
    }
    public int getDuration() {
        return (mPlayer != null) ? (mPlayer.getTotalTime()) : (-1);
    }
    /**
     * 设置原伴唱模式
     * @param mode 
     * @return 是否成功
     */
    public abstract boolean setAudioSingMode(int mode);
    /**
     * 获取原伴唱模式
     * @return 当前原伴唱模式
     */
    public int getAudioSingMode() {
        return (mPlayer != null) ? (mPlayer.getAudioSingMode()) : (KmAudioTrackMode.MODE_UNKNOW);
    }
    /**
     * @brief : 设置音量
     * @param volume 音量
     */
    public void setVolume(float volume) {
        if (mPlayer != null) {
            mPlayer.setVolume(volume);
        }
        return;
    }
    /**
     * @brief : 获取音量
     * @return 返回当前音量
     */
    public float getVolume() {
        return (mPlayer != null) ? (mPlayer.getVolume()) : (-1);
    }
    public boolean isDisplaySet() {
        return (mHolder != null);
    }
    public void setDisplay(SurfaceHolder holder) {
        mHolder = holder;
        mPlayer.setDisplay(holder);
        return;
    }
    public int getStateBeforeBuffering() {
        return mStateBeforeBuffering;
    }
    public void setStateBeforeBuffering(int state) {
        if (state != PlayerCtrlState.STATE_PAUSE
                && state != PlayerCtrlState.STATE_PLAY) {
            EvLog.e("setStateBeforeBuffering input param invalid=" + state);
            return;
        }
        mStateBeforeBuffering = state;
    }
    /**
     * [功能说明] 销毁
     */
    public void destoryPlayer() {
        if (mPlayer !=null ) {
            mPlayer.setDisplay(null);
            mPlayer.destroy();
            mPlayer = null;
        }
    }
    /**
     * [功能说明] 获取实际解码错误
     * @return
     */
    public DecodeErrorInfo getRealDecodeError() {
        return mDecodeErrorInfo;
    }
    
    
    /**
     * [功能说明] mediaplayer返回的错误码
     */
    public class DecodeErrorInfo {
        public int arg1;
        public int arg2;
    }
    

}
