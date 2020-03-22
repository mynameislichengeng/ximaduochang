package com.evideo.kmbox.model.playerctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.ErrorInfo;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
import com.evideo.kmbox.model.player.KmAudioTrackMode;
import com.evideo.kmbox.model.playerctrl.IPlayerManager.DecodeErrorInfo;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrlType.IPlayerManagerListener;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrlType.PlayCtrlMsg;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrlType.PlayDataState;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrlType.PlayMode;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrlType.PlayerCtrlState;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.model.songinfo.SongCategory;
import com.evideo.kmbox.model.umeng.EventConst;
import com.evideo.kmbox.util.EvLog;

public class KmPlayerCtrl implements IPlayerManagerListener{
    private static KmPlayerCtrl instance = null;
    private IPlayerManager mPlayManager = null;
    //预加载结束后是否可以播放
    private boolean mIsCanPlayAfterPreLoad = true; 
    private Handler mHandler = null;
    private int mSingMode = KmAudioTrackMode.MODE_ORI;
    private SurfaceHolder mHolder = null;
    private boolean mIsMute = false;
    private float mVolBeforeMute = 0.0f;
    private List<IPlayStateObserver> mObservers = null;
    
    public static KmPlayerCtrl getInstance() {
        if(instance == null) {
            synchronized (KmPlayerCtrl.class) {
                KmPlayerCtrl temp = instance;
                if(temp == null) {
                  temp = new KmPlayerCtrl();
                  instance = temp;
                }
            }
         }
         return instance;
    }

   /* @Override
    public void finalize() {
        instanceFlag = false;
        instance = null;
    }*/
    
    public void playListChange() {
        if (mPlayManager != null) {
            mPlayManager.listChange();
        }
    }
    
    public void registPlayStateObserver(IPlayStateObserver observer) {
        synchronized (mObservers) {
            if (!mObservers.contains(observer)) {
                mObservers.add(observer);
            }
        }
    }
    
    public void unregistPlayStateObserver(IPlayStateObserver observer) {
        synchronized (mObservers) {
            if (mObservers.contains(observer)) {
                mObservers.remove(observer);
            }
        }
    }
    
    public boolean isMute() {
        return mIsMute;
    }
    
    public void mute() {
        if (mIsMute) {
            return;
        }
        mIsMute = true;
        mVolBeforeMute = mPlayManager.getVolume();
        mPlayManager.setVolume(0.0f);
    }
    
    public void unmute() {
        if (!mIsMute) {
            return;
        }
        mIsMute = false;
        mPlayManager.setVolume(mVolBeforeMute);
        return;
    }
    
    public int getWorkMode() {
        return mPlayManager.getMode();
    }
    
    /**
     * [判断歌曲是否处于loading的状态]
     * @return true:loading状态, false:非loading状态
     */
    public boolean isLoadingData() {
        return mPlayManager.getDataState() == PlayDataState.STATE_LOADING;
    }
    
    public int getStateBeforeBuffering() {
        return mPlayManager.getStateBeforeBuffering();
    }
    
    public void setStateBeforeBuffering(int state) {
        mPlayManager.setStateBeforeBuffering(state);
    }
    
    private KmPlayerCtrl() {
        initEventLoop();
//        KmSongDownManager.getInstance().init();
        mPlayManager = KmPlayerFactory.getInstance().getNormalPlayer();
        mPlayManager.enable();
        mPlayManager.setListener(this);
        mObservers = new ArrayList<IPlayStateObserver>(); 
    }

    public void releasePlayer() {
        mPlayManager.stop(false);
        mPlayManager.destoryPlayer();
    }
    
    public boolean isPlayerValid() {
        return (mPlayManager.mPlayer != null);
    }
    
    public void createPlayer() {
        mPlayManager.createPlayer();
    }

    public void reCreatePlayer() {
        if (mPlayManager.getMode() == PlayMode.MODE_NORMAL) {
            EvLog.e("recreate play mode----");
            KmPlayerFactory.getInstance().destoryNormalPlayer();
            mPlayManager = KmPlayerFactory.getInstance().getNormalPlayer();
            mPlayManager.enable();
            mPlayManager.setListener(this);
        } /*else if (mPlayManager.getMode() == PlayMode.MODE_PLAYBACK) {
            EvLog.e("recreate playback mode----");
            KmPlayerFactory.getInstance().destoryPlayBackPlayer();
            mPlayManager = KmPlayerFactory.getInstance().getPlayBackPlayer();
            mPlayManager.enable();
            mPlayManager.setListener(this);
        } */else {
            EvLog.e( "reCreatePlayer invalid mode " + mPlayManager.getMode());
        }
    }
    
    /*public void setWorkMode(int mode) {
        if (mPlayManager.getMode() != mode) {
            if (mPlayManager.getMode() == PlayMode.MODE_PLAYBACK) {
                EvLog.i("change playback to normal");
                mPlayManager.disable();
                mPlayManager.setDisplay(null);
                mPlayManager = KmPlayerFactory.getInstance().getNormalPlayer();
                mPlayManager.enable();
                mPlayManager.setDisplay(mHolder);
                mPlayManager.setListener(this);
            } else if (mPlayManager.getMode() == PlayMode.MODE_NORMAL) {
                EvLog.i("change normal to playback");
                mPlayManager.setDisplay(null);
                mPlayManager.disable();
                mPlayManager = KmPlayerFactory.getInstance().getPlayBackPlayer();
                mPlayManager.enable();
                mPlayManager.setDisplay(mHolder);
                mPlayManager.setListener(this);
            } else {
                EvLog.i("set work mode unknow");
            }
        }
    }*/
    
    private void initEventLoop() {
        if (mHandler != null) {
            return;
        }
        
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                
                case PlayCtrlMsg.MSG_START:
                    mHandler.removeMessages(PlayCtrlMsg.MSG_PLAY);
                   /* if (mPlayManager.getMode() == PlayMode.MODE_PLAYBACK) {
                        mPlayManager.setAudioSingMode(AudioSingMode.MODE_ACC);
                    } else {
//                        mPlayManager.setAudioSingMode(mSingMode);
                    }*/
                    mIsMute = false;
                    
                   /* if (!SystemConfigManager.DIRECT_ONLINE_PLAY) {
                        if (mPlayManager.getSong() != null) {
                            String responseUrl = String.format("%08d", mPlayManager.getSong().getSongId());
                            EvLog.d("before start,set httpd response url=" + responseUrl);
                            HttpdServiceProxy.getInstance().setResponseUrl(responseUrl);
                        } else {
                            EvLog.e("mPlayManager.getSong() == null");
                        }
                    }*/
                    mPlayManager.start();
                    break;
                case PlayCtrlMsg.MSG_PLAY:
                    if (mPlayManager.getSong() != null && (msg.arg2 != mPlayManager.getSong().getSerialNum())) {
                        EvLog.e("not same song,not play");
                        return;
                    }
                    mPlayManager.play(msg.arg1 != -1);
                    break;
                case PlayCtrlMsg.MSG_PAUSE:
                    mPlayManager.pause(msg.arg1 != -1);
                    break;

                case PlayCtrlMsg.MSG_STOP:
                    if (mPlayManager != null) {
                        EvLog.i("MSG_STOP PlayManager mode=" + mPlayManager.getMode());
                        mPlayManager.stop(true);
                        // 清除挂起的通知
                        mHandler.removeMessages(PlayCtrlMsg.MSG_PLAY);
                    }
                    break;
                default:
                    break;
                }
            }
        };
    }

    public boolean uninit() {
//        KmSongDownManager.getInstance().uninit();
        KmPlayerFactory.getInstance().unInit();
        mPlayManager = null;
        return true;
    }

    public boolean play() {
        KmPlayListItem curSong = mPlayManager.getSong();
        if (curSong == null) {
            EvLog.d(" curSong invalid ");
            return false;
        }
        
        int dataState = mPlayManager.getDataState();
        if (dataState == PlayDataState.STATE_LOADING) {
            EvLog.d(" dataloading state, song can play after preload");
            mIsCanPlayAfterPreLoad = true;
            return true;
        } else {
            EvLog.d(" dataready state, song play at dataState=" + dataState);
            mIsCanPlayAfterPreLoad = true;
        }
        mPlayManager.play(true);
        return false;
    }

    public boolean pause() {
        KmPlayListItem curSong = mPlayManager.getSong();
        if (curSong == null) {
            EvLog.d(" curSong null  ");
            return false;
        }
        
        if (mPlayManager.getDataState() == PlayDataState.STATE_LOADING) {
            EvLog.w("dataloading state, song cannot play after preload");
            mIsCanPlayAfterPreLoad = false;
            /*if (mListener != null) {
                mListener.onStateChange(PlayerCtrlState.STATE_PAUSE, null);
            }*/
            synchronized (mObservers) {
                for (IPlayStateObserver iObserver : mObservers) {
                    iObserver.onPlayPause();
                }
            }
            return true;
        } 
        
        return mHandler.sendEmptyMessage(PlayCtrlMsg.MSG_PAUSE);
    }

    public boolean stop() {
        return mHandler.sendEmptyMessage(PlayCtrlMsg.MSG_STOP);
    }
    
    public boolean playNextSong(KmPlayListItem item) {
        if (item == null) {
            EvLog.e("playNextSong item is null");
            return false;
        }
        if (mPlayManager.isDecoding()) {
            mPlayManager.stop(true);
        }
        
        if (mPlayManager.getPlayerState() == PlayerCtrlState.STATE_PREPARING) {
            EvLog.e("PlayerCtrlState_ePreparing state not handle cutsong msg ");
            return false;
        } 
        mIsCanPlayAfterPreLoad = true;
        
        /*if (item.getSongCategory() == SongCategory.CATEGORY_PLAYBACK) {
            if (mPlayManager.getMode() != PlayMode.MODE_PLAYBACK) {
                EvLog.e("play back mode----");
                mPlayManager.setDisplay(null);
                mPlayManager.disable();
                
                mPlayManager = KmPlayerFactory.getInstance().getPlayBackPlayer();
                mPlayManager.enable();
                mPlayManager.setDisplay(mHolder);
                
                mPlayManager.setListener(this);
                mPlayManager.setSong(item);
                synchronized (mObservers) {
                    for (IPlayStateObserver iObserver : mObservers) {
                        iObserver.onModeChange();
                    }
                }
            } else {
                mPlayManager.setSong(item);
            }
        } else {
            if (mPlayManager.getMode() == PlayMode.MODE_PLAYBACK) {
                EvLog.e("normal mode----");
                mPlayManager.disable();
                mPlayManager.setDisplay(null);
                mPlayManager = KmPlayerFactory.getInstance().getNormalPlayer();
                mPlayManager.enable();
                mPlayManager.setDisplay(mHolder);
                mPlayManager.setListener(this);
            } 
            mPlayManager.setSong(item);
        }*/
        mPlayManager.setSong(item);
        return mHandler.sendEmptyMessage(PlayCtrlMsg.MSG_START);
    }

    public boolean replay() {
        int state = mPlayManager.getPlayerState();
        int dataState = mPlayManager.getDataState();
        EvLog.d("replay in state " + state + ",mDataState=" + dataState);

        if (state == PlayerCtrlState.STATE_IDLE) {
            // 在线歌曲处于下载缓冲状态时不允许重唱
            if (dataState != PlayDataState.STATE_LOADING) {
                EvLog.d("begin to replay");
                Message msg = mHandler.obtainMessage(PlayCtrlMsg.MSG_START, -1,-1);
                return mHandler.sendMessage(msg);
            } else {
                EvLog.d("STATE_LOADING, can not replay");
                return false;
            }
        } else if (state == PlayerCtrlState.STATE_PREPARING) {
            EvLog.d("STATE_PREPARING, can not replay");
            return false;
        } else {
            EvLog.d("begin to replay");
            long downingId = PlayListItemDownManager.getInstance().getDowningId();
            EvLog.d("KmPlayerCtrl PlayListItemDownManager.getInstance().delItem " + downingId);
            PlayListItemDownManager.getInstance().delItem(downingId);
            
            mPlayManager.stop(false);
            Message msg = mHandler.obtainMessage(PlayCtrlMsg.MSG_START, -1,-1);
            return mHandler.sendMessage(msg);
        }
    }

    public boolean setAudioSingMode(int mode) {
        mSingMode = mode;
        if (mPlayManager != null) {
            mPlayManager.setAudioSingMode(mode);
        }
        return true;
    }

    public int getAudioSingMode() {
        if (mPlayManager != null) {
            return mPlayManager.getAudioSingMode();
//            return (mPlayManager.getMode() == PlayMode.MODE_PLAYBACK) ? (KmAudioTrackMode.MODE_ACC) : (mPlayManager.getAudioSingMode());
        }
        return KmAudioTrackMode.MODE_ORI;
    }
    
    public int getPlayerState() {
        return mPlayManager.getPlayerState();
    }

    public void setPlayingSong(KmPlayListItem item) {
        if (mPlayManager!=null) {
            mPlayManager.setSong(item);
        }
    }
    public KmPlayListItem getPlayingSong() {
        if (mPlayManager != null) {
            return mPlayManager.getSong();
        }
        return null;
    }

    public int getPlayedTime() {
        return mPlayManager.getPlayedTime();
    }

    public void setVolume(float volume) {
        mPlayManager.setVolume(volume);
    }

    public float getVolume() {
        return mPlayManager.getVolume();
    }

    public void setDisplay(SurfaceHolder view) {
        mHolder = view;
        mPlayManager.setDisplay(view);
    }

    public int getDuration() {
        return mPlayManager.getDuration();
    }

    public int getMode() {
        return mPlayManager.getMode();
    }

    /**
     * [歌曲缓冲完后是否可直接播放]
     * @return true,直接播放，false，不播放
     */
    public  boolean isSongCanPlayAfterPreLoad() {
        return mIsCanPlayAfterPreLoad;
    }
    
    public void setSongCanPlayAfterPreLoad(boolean can) {
        mIsCanPlayAfterPreLoad = can;
    }
    /**
     * [歌曲是否处于解码状态]
     * @return true:解码中
     */
    public boolean isPlayerDecoding() {
        return mPlayManager.isDecoding();
    }
  
    public int getPlayerDataState() {
        return mPlayManager.getDataState();
    }
    /*public void setMode(int mode) {
        mPlayManager.setMode(mode);
    }*/
    
    public int getAudioTime() {
        if (mPlayManager.getMode() == PlayMode.MODE_PLAYBACK) {
            return 0;
        }
        return mPlayManager.getAudioTime();
    }
    
    public DecodeErrorInfo getDecodeErrorInfo() {
        return (mPlayManager != null) ? (mPlayManager.getRealDecodeError()) : (null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStateChange(int state, Object param) {
        switch (state) {
        case PlayerCtrlState.STATE_PLAY: {
            if (param != null && (Boolean) param) {
                if (mPlayManager != null) {
//                    EvLog.i("KmPlayerCtrl","STATE_PLAY,setAudioSingMode:" + mSingMode);
                    mPlayManager.setAudioSingMode(mSingMode);
                }
                
                synchronized (mObservers) {
                    for (IPlayStateObserver iObserver : mObservers) {
                        iObserver.onPlayStart();
                    }
                }
            } else {
                synchronized (mObservers) {
                    for (IPlayStateObserver iObserver : mObservers) {
                        iObserver.onPlay();
                    }
                }
            }
            break;
        }
        case PlayerCtrlState.STATE_PAUSE: {
            EvLog.w("recv PlayerCtrl Pause msg");
            synchronized (mObservers) {
                for (IPlayStateObserver iObserver : mObservers) {
                    iObserver.onPlayPause();
                }
            }
            break;
        }
        case PlayerCtrlState.STATE_STOP:
            EvLog.d("KmPlayerCtrl recv STATE_eStop msg,workmode="
                    + KmPlayerCtrl.getInstance().getWorkMode());
            synchronized (mObservers) {
                for (IPlayStateObserver iObserver : mObservers) {
                    iObserver.onPlayStop();
                }
            }
            break;
        case PlayerCtrlState.STATE_ERROR:
            EvLog.d("recv STATE_ERROR msg");
            synchronized (mObservers) {
                for (IPlayStateObserver iObserver : mObservers) {
                    iObserver.onPlayError((ErrorInfo) param);
                }
            }
            break;
        case PlayerCtrlState.STATE_AUTOSTOP: {
            synchronized (mObservers) {
                for (IPlayStateObserver iObserver : mObservers) {
                    iObserver.onPlayAutoStop();
                }
            }
            break;
        }
        default:
            break;
        }
    }

    private void logBufferingInfo(String event ) {
        KmPlayListItem curSong = mPlayManager.getSong();
        if (curSong != null) {
            // 日志统计begin
            HashMap<String, String> m = new HashMap<String, String>();
            // m.put(EventConst.K_SONG_ID, KmBufferAlgorithm. )
            m.put(EventConst.K_AVERAGE_SPEED, String.valueOf(KmBufferAlgorithm.getAverageSpeed()));
            m.put(EventConst.K_MAX_SPEED, String.valueOf(KmBufferAlgorithm.getMaxSpeed()));
            m.put(EventConst.K_MIN_SPEED, String.valueOf(KmBufferAlgorithm.getMinSpeed()));
            m.put(EventConst.K_MAX_FLUCTUATION_SPEED, String .valueOf(KmBufferAlgorithm.getMaxFluctuationSpeed()));
            m.put(EventConst.K_SONG_ID, String.valueOf(curSong.getSongId()));
            LogAnalyzeManager.onEvent(BaseApplication.getInstance().getApplicationContext(),
                    event, m);
            // 日志统计end
        }
    }

    @Override
    public void onBufferingChange(int percent) {
        EvLog.e(" onBufferingChange percent:" + percent);
       
        if (percent == 0) {
            logBufferingInfo(EventConst.ID_BUFFER_BEGIN);
        }

        if (percent != 100) {
           /* if (mListener != null) {
                mListener.onBufferingChange(percent);
            }*/
            synchronized (mObservers) {
                for (IPlayStateObserver iObserver : mObservers) {
                    iObserver.onBufferingChange(percent);
                }
            }
        } else { // 100
            logBufferingInfo(EventConst.ID_BUFFER_END);
                // 日志统计end
            if (mPlayManager.getDataState() == PlayDataState.STATE_BUFFER) {
                EvLog.e("buffering end,waiting data");
                return;
            } else {
                /*if (mListener != null) {
                    mListener.onBufferingChange(percent);
                }*/
                synchronized (mObservers) {
                    for (IPlayStateObserver iObserver : mObservers) {
                        iObserver.onBufferingChange(percent);
                    }
                }
            }
        }
    }

    @Override
    public void onDataReady(int serialNum) {
        if (mPlayManager != null && mPlayManager.isDecoding()) {
            EvLog.d("is alrady decoding,not handle dataready message");
            return;
        }
        /*if (!mIsCanPlayAfterPreLoad) {
            EvLog.d("user set pause, not auto play");
            return;
        }*/
        
        setAudioSingMode(getAudioSingMode());
        Message msg = mHandler.obtainMessage();
        msg.what = PlayCtrlMsg.MSG_PLAY;
        msg.arg2 = serialNum;
        mHandler.sendMessage(msg);
    }

    @Override
    public void updateDownState() {
        synchronized (mObservers) {
            for (IPlayStateObserver iObserver : mObservers) {
                iObserver.updateDownState();
            }
        }
    }

    @Override
    public void updateDownPercent(int percent, float rate) {
        synchronized (mObservers) {
            for (IPlayStateObserver iObserver : mObservers) {
                iObserver.updateDownPercent(percent, rate);
            }
        }
    }
    
    public void seekToTime(long time) {
        if (mPlayManager != null) {
            mPlayManager.seekToTime(time);
        }
    }
    
    public boolean isPlayerRelease() {
        return (mPlayManager == null);
    }
};