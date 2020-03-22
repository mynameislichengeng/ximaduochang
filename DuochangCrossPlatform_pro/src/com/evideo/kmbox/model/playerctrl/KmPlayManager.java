
package com.evideo.kmbox.model.playerctrl;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.awirtz.util.RingBuffer;
import com.evideo.kmbox.ErrorInfo;
import com.evideo.kmbox.SystemConfigManager;
import com.evideo.kmbox.model.dao.data.Media;
import com.evideo.kmbox.model.dao.data.StorageVolume;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.device.IDeviceConfig;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.down.DownError;
import com.evideo.kmbox.model.down.GetMediaListPresenter;
import com.evideo.kmbox.model.down.KmDownThread.Task;
import com.evideo.kmbox.model.down.GetMediaListPresenter.IGetMediaListListener;
import com.evideo.kmbox.model.down.GetMediaListPresenter.OnlineFileItem;
import com.evideo.kmbox.model.player.IKmPlayerEvent;
import com.evideo.kmbox.model.player.KmPlayerState;
import com.evideo.kmbox.model.player.KmVideoPlayerType;
import com.evideo.kmbox.model.player.MediaPlayerFactory;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrlType.PlayDataState;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrlType.PlayMode;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrlType.PlayerCtrlState;
import com.evideo.kmbox.model.playerctrl.PlayListItemDownManager.PlayListItemDown;
import com.evideo.kmbox.model.playerctrl.list.BroadcastListManager;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;
import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.model.songinfo.SongCategory;
import com.evideo.kmbox.model.songinfo.SongDataState;
import com.evideo.kmbox.model.storage.CacheManager;
import com.evideo.kmbox.model.storage.CacheManager.CacheManagerListener;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.DiskUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;
import com.evideo.kmbox.widget.mainview.MainViewManager;

/**
 * [功能说明]
 */
public class KmPlayManager extends IPlayerManager implements IPlayItemDownListener,/*IDurationGetListener,*/
        IGetMediaListListener, CacheManagerListener, GetMediaListPresenter.IGetMediaCallback {


    private IKmPlayerEvent mPlayerEvent = null;
    //当前歌曲长度
//    private int mCurrentSongDuration = 0;


    private static final int MSG_UPDATE_GET_MEDIA_STATE = 99;
    private static final int MSG_DOWN_PROGRESS = 100;
    private static final int MSG_DOWN_ERROR = 101;
    private static final int PLAY_LIST_CHANGE = 102;
    private int mBroadcastSongId = -1;

    Handler mHandler = null;
    private ErrorInfo mErrorInfo = new ErrorInfo();

    private static final String TAG = KmPlayManager.class.getSimpleName();

    public KmPlayManager() {
        initPlayer();
        initHandler();
        /*if (!DeviceConfigManager.DIRECT_ONLINE_PLAY)*/
        {
            PlayListItemDownManager.getInstance().init();
            PlayListItemDownManager.getInstance().registerListener(this);
        }
    }


    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_UPDATE_GET_MEDIA_STATE:
                        notifyUpdateDownState();
                        break;
                    case MSG_DOWN_PROGRESS:
                        handleDownProgress(msg.arg1, msg.arg2, msg.getData().getFloat("speed"));
                        break;
                    case MSG_DOWN_ERROR:
                        ErrorInfo errorInfo = (ErrorInfo) msg.obj;
                        handleDownError(errorInfo);
                        break;
                    case PLAY_LIST_CHANGE:
                        onPlayListChangeEvent();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void handleDownError(/*int errorCode,String errMsg*/ErrorInfo errorInfo) {
        //非解码状态下，如video资源存在则可直接播放视频
        if (mCurSong != null && mCurSong.isMediaAvailable()) {
            if (!isDecoding()) {
                mDataState = PlayDataState.STATE_READY;
                EvLog.w(mCurSong.getSongName() + " is down error,but can still play ");
                /*if (mListener != null && mEnable) {
                    mListener.onDataReady();
                }*/
                notifyOnDataReady(mCurSong.getSerialNum());
            } else {
                EvLog.w(TAG, mCurSong.getSongName() + " erc down error,video is complete,not handle error");
            }
            return;
        } else {
            mDataState = PlayDataState.STATE_ERROR;
        }

        if (mCurSong != null) {
            EvLog.e(TAG, mCurSong.getSongName() + " down error,notify error state !");
        } else {
            EvLog.e(TAG, "NULL down error,notify error state !");
        }

        stopImpl(false);
        if (errorInfo == null) {
            mErrorInfo.errorType = DownError.ERROR_TYPE_DOWN_MEDIA_FAILED;
            mErrorInfo.errorCode = DownError.ERROR_UNKNOW;
        } else {
            mErrorInfo = errorInfo;
        }
        setErrorState();
    }

    private void calcBufferRate(long downedSize, long totalSize, float speed) {
        KmBufferAlgorithm.setTotalSize(totalSize);
        KmBufferAlgorithm.setDuration(mCurSong.getDuration());
        KmBufferAlgorithm.updateSpeed(speed);
        KmBufferAlgorithm.updateDownSize(downedSize);
    }

    private void handleDownProgress(long downedSize, long totalSize, float speed) {
//        EvLog.d("handleDownProgress ," +  downedSize + " / " + totalSize);
        int progress = (int) ((downedSize * 100) / totalSize);

        if (mDataState == PlayDataState.STATE_BUFFER) {
            if (DeviceConfigManager.getInstance().getPlayMode() == IDeviceConfig.PLAY_MODE_BUFFER_FILE_PLAY) {
                boolean canResumePlay = false;
                if (downedSize == totalSize) {
                    canResumePlay = true;
                }

                if (!canResumePlay) {
                    canResumePlay = KmBufferAlgorithm.isCanPlay();
                }
                if (canResumePlay) {
                    EvLog.d(TAG, "DATA_BUFFER_STATE resume play");
                    //恢复歌曲
                    if (getStateBeforeBuffering() == PlayerCtrlState.STATE_PLAY) {
                        play(false);
                    }
                    mDataState = PlayDataState.STATE_READY;
                    notifyOnBufferingChange(100);
                } else {
                    EvLog.w(TAG, "DATA_BUFFER_STATE still can not play");
//                    notifyUpdateDownPercent(progress, speed);
                    int precent = KmBufferAlgorithm.getPercent();
                    if (precent > 100) {
                        precent = 100;
                    }
                    notifyOnBufferingChange(precent);
                    return;
                }
            }
            return;
        }
        //data loading state
        notifyUpdateDownPercent(progress, speed);

        if (downedSize == totalSize) {
            if (!isDecoding()) {
                mDataState = PlayDataState.STATE_READY;
                notifyOnDataReady(mCurSong.getSerialNum());
            }
        }

        if (DeviceConfigManager.getInstance().getPlayMode() == IDeviceConfig.PLAY_MODE_DOWN_COMPLETE_PLAY) {
            return;
        }
        boolean canPlay = false;
        if (DeviceConfigManager.getInstance().getPlayMode() == IDeviceConfig.PLAY_MODE_BUFFER_MEM_PLAY) {
//            EvLog.i("getFreeSpace=" + RingBuffer.getInstance().getFreeSpace() + ",isCanPlay=" + KmBufferAlgorithm.isCanPlay());
            if (RingBuffer.getInstance().getFreeSpace() <= SystemConfigManager.CAN_PLAY_BUFFER_FREE_SIZE) {
                canPlay = true;
            }
        }

        if (KmBufferAlgorithm.isCanPlay()) {
            canPlay = true;
        }

        if (canPlay) {
            if (!isDecoding()) {
                //notifyOnDataReady(mCurSong.getSerialNum());
                if (mCurSong.isCanScoreInDB()) {
                    if (mCurSong.isErcDownFinish()) {
                        EvLog.d(TAG, "erc down finish, play");
                        mDataState = PlayDataState.STATE_READY;
                        notifyOnDataReady(mCurSong.getSerialNum());
                    } else {
                        EvLog.d(TAG, "erc down not finish,not play");
                        return;
                    }
                } else {
                    EvLog.d(TAG, "not grade song, direct play");
                    mDataState = PlayDataState.STATE_READY;
                    notifyOnDataReady(mCurSong.getSerialNum());
                }

            } else {
                EvLog.d(TAG, "is decoding-------------");
                EvLog.d(TAG, "can play，is decoding");
            }
        }
        return;
    }

    @Override
    protected boolean initPlayer() {
        if (mIsInited == true) {
            EvLog.w(TAG, " have already init");
            return true;
        }

        mPlayerEvent = new IKmPlayerEvent() {
            @Override
            public void onComplete() {
                mState = PlayerCtrlState.STATE_AUTOSTOP;
                /*if (mListener != null&& mEnable) {
                    mListener.onStateChange(mState, mCurSong);
                }*/
                notifyOnStateChange(mState, mCurSong);
            }

            @Override
            public void onBufferingStart() {
                if (mState == PlayerCtrlState.STATE_PREPARING) {
                    EvLog.d(TAG, "STATE_ePreparing state not handle bufferingstart msg");
                    return;
                }

                if (isDecoding() == false) {
                    EvLog.d(TAG, mState + "  not handle bufferingstart msg");
                    return;
                }
                mStateBeforeBuffering = mState;
                mState = PlayerCtrlState.STATE_BUFFERING;

                EvLog.d(TAG, "recv OnBufferingStart msg, mStateBeforeBufferingStart" + mStateBeforeBuffering);
               /* if ( mListener != null&& mEnable) {
                    mListener.onBufferingChange(0);
                }*/

                //正在下载
                if (notifyOnBufferingChange(0) && mCurSong != null && !mCurSong.isMediaAvailable()) {
                    EvLog.e(TAG, "can not play,enter data loading state");
                    if (DeviceConfigManager.getInstance().getPlayMode() == IDeviceConfig.PLAY_MODE_BUFFER_FILE_PLAY) {
                        mDataState = PlayDataState.STATE_BUFFER;
                        KmBufferAlgorithm.resetSpeed();
                        KmBufferAlgorithm.updatePlayedTime(getPlayedTime() / 1000);
                        KmBufferAlgorithm.updatePlayedSize(KmBufferAlgorithm.getDownSize());
                    }
                }
            }

            @Override
            public void onBufferingEnd() {
                if (mState != PlayerCtrlState.STATE_BUFFERING) {
                    EvLog.d(TAG, mState + " is not in buffering state not handle OnBufferingEnd msg");
                    return;
                }
                mState = mStateBeforeBuffering;
                EvLog.d(TAG, "recv OnBufferingEnd msg,mState=" + mState + ",mStateAfterBufferingEnd=" + mStateBeforeBuffering);

                if (mDataState == PlayDataState.STATE_BUFFER) {
                    EvLog.e("STATE_BUFFER ,wait");
                    return;
                }

                notifyOnBufferingChange(100);

                if (mStateBeforeBuffering == PlayerCtrlState.STATE_PAUSE) {
                    mPlayer.pause();
                } else {
                    mPlayer.play();
                }
            }

            @Override
            public void onBufferingUpdate(int percent) {
                if (mState == PlayerCtrlState.STATE_BUFFERING) {
                    notifyOnBufferingChange(percent);
                }
            }

            @Override
            public void onStop() {
            }

            @Override
            public void onPlay() {
                EvLog.i("KmPlayerManager onPlay:" + mState);
                if (mState == PlayerCtrlState.STATE_PREPARED) {
                    if (mIsCurrentSongValid) {
                        EvLog.w(TAG, mCurSong.getSongName() + " is start playing ");
                        mState = PlayerCtrlState.STATE_PLAY;
                        notifyOnStateChange(mState, true);
                    } else {
                        EvLog.w(TAG, mCurSong.getSongName() + " is set invalid");
                        stop(true);
                    }
                } else {
                    EvLog.w(TAG, "OnPlay invalid " + mState);
                }
            }

            @Override
            public void onError(int arg1, int arg2) {
                if (mDecodeErrorInfo == null) {
                    mDecodeErrorInfo = new DecodeErrorInfo();
                }
                mDecodeErrorInfo.arg1 = arg1;
                mDecodeErrorInfo.arg2 = arg2;

                if (arg1 == -1003) {
                    if (mCurSong != null) {
                        EvLog.e("recv -1003 error, del file:" + mCurSong.getVideoPath());
                    }
                }
                stop(false);
                mErrorInfo.errorType = PlayError.ERROR_TYPE_PLAY_FAILED_BY_DECODER;
                mErrorInfo.errorCode = arg1;
                mErrorInfo.errorCodeSupplement = arg2;
                mErrorInfo.errorMessage = "player onError recv arg1=" + arg1 + ",arg2=" + arg2;
                UmengAgentUtil.reportError(PlayError.getUmengErrorMessage(mCurSong, mErrorInfo));
                setErrorState();
            }

            @Override
            public void onPrepared() {
                /*if (mListener != null) {
                    mListener.onVideoChange(true);
                }*/
                mState = PlayerCtrlState.STATE_PREPARED;
                EvLog.i("KmPlayerManager onPrepared");
                /*if (mPlayer.getPlayerType() == KmVideoPlayerType.VLC) {
                    mPlayer.play();
                }*/
            }
        };

        createPlayer();
        mIsInited = true;
        return true;
    }


    @Override
    public boolean stop(boolean notify) {
        return stopImpl(notify);
    }

    private boolean stopImpl(boolean notify) {
        boolean result = false;

        if (mPlayer == null) {
            EvLog.e(TAG + "player null,stop failed");
            return result;
        }

        int state = mPlayer.getState();
        if (state == KmPlayerState.PlayerState_ePreparing) {
            EvLog.e(TAG + "can not stop at PlayerState_ePreparing State," + mState);
            return result;
        }

        if (state == KmPlayerState.PlayerState_eIdle) {
            //FIXME
            if (isDecoding()) {
                EvLog.e(TAG + " PlayerState_eIdle,but isDecoding, invalid state, correct it");

                mState = PlayerCtrlState.STATE_STOP;
                if (notify) {
                    notifyOnStateChange(mState, false);
                }
                return true;
            }
            return false;
        }

        int ret = mPlayer.stop();
        if (ret != 0) {
            EvLog.e(TAG + "stop failed:" + ret);
        } else {
            EvLog.d(TAG + "stop success");
            result = true;
        }
        mState = PlayerCtrlState.STATE_STOP;
        if (notify) {
            notifyOnStateChange(mState, false);
        }

        return result;
    }

    private void setState(int state) {
        mState = state;
        /*if (mListener != null&& mEnable) {
            mListener.onStateChange(state, false);
        } else {
            EvLog.e(TAG,"no listener");
        }*/
        notifyOnStateChange(state, false);
    }

    private void setErrorState() {
        mState = PlayerCtrlState.STATE_ERROR;
        notifyOnStateChange(mState, mErrorInfo);
    }

    private boolean startPlayListSong() {
        boolean isLocalComplete = false;

        isLocalComplete = mCurSong.isMediaAvailable();

        if (isLocalComplete) {
            mDataState = PlayDataState.STATE_COMPLETE;
            EvLog.e(TAG, mCurSong.getSongName() + " local exist,direct play ");
            return startDecode();
        }

        /* if (!DeviceConfigManager.DIRECT_ONLINE_PLAY) */
        {
            long downingId = PlayListItemDownManager.getInstance().getDowningId();
            if (downingId != 0) {
                /*//重唱
                if (downingId == mCurSong.getDownId()) {
                    mState = PlayerCtrlState.STATE_IDLE;
                    mDataState = PlayDataState.STATE_LOADING;
                    return true;
                }*/
                EvLog.e("startPlayListSong PlayListItemDownManager.getInstance().delItem: " + downingId);
                PlayListItemDownManager.getInstance().delItem(downingId);
            }
        }

        KmBufferAlgorithm.reset();
        mState = PlayerCtrlState.STATE_IDLE;
        mDataState = PlayDataState.STATE_LOADING;
        EvLog.e(TAG, mCurSong.getSongName() + " resource not complete, need down," + mCurSong.getSerialNum());
        startGetMediaList(PlayListManager.getInstance().getItemByPos(0));
        return true;
    }

    private boolean startBroadcastSong() {
        if (DeviceConfigManager.getInstance().getPlayMode() == IDeviceConfig.PLAY_MODE_DOWN_COMPLETE_PLAY) {
            if (mCurSong.isMediaAvailable()) {
                mDataState = PlayDataState.STATE_COMPLETE;
                EvLog.e(TAG, "broadcast song," + mCurSong.getSongName() + " local exist,direct play ");
                return startDecode();
            }

            long downingId = PlayListItemDownManager.getInstance().getDowningId();
            PlayListItemDownManager.getInstance().delItem(downingId);
            mState = PlayerCtrlState.STATE_IDLE;
            mDataState = PlayDataState.STATE_LOADING;
            int serialNum = mBroadcastSongId--/*MathUtil.getRandomNum(10000,20000)*/;
            Log.d("gsp", TAG + ">>>setSerialNum---11---");
            mCurSong.setSerialNum(serialNum);
            EvLog.e(mCurSong.getSongName() + ", resource not complete,need down,serialNum:" + serialNum);
            startGetMediaList(mCurSong);
            return true;
        }

        if (DeviceConfigManager.getInstance().getBroadcastSongType() == IDeviceConfig.BROADCAST_SONG_TYPE_ONLINE) {
            if (mCurSong.isMediaAvailable()) {
                mDataState = PlayDataState.STATE_COMPLETE;
                EvLog.e(TAG, "broadcast song," + mCurSong.getSongName() + " local exist,direct play ");
                return startDecode();
            } else {
                /*if (!DeviceConfigManager.DIRECT_ONLINE_PLAY)*/
                {
                    long downingId = PlayListItemDownManager.getInstance().getDowningId();
                    PlayListItemDownManager.getInstance().delItem(downingId);
                }
            }
            KmBufferAlgorithm.reset();
            mState = PlayerCtrlState.STATE_IDLE;
            mDataState = PlayDataState.STATE_LOADING;
            int serialNum = mBroadcastSongId--/*MathUtil.getRandomNum(10000,20000)*/;
            Log.d("gsp", TAG + ">>>setSerialNum-222--");
            mCurSong.setSerialNum(serialNum);
            EvLog.e(mCurSong.getSongName() + ", resource not complete,need down,serialNum:" + serialNum);
            startGetMediaList(mCurSong);
            return true;
        } else {
            if (!mCurSong.isMediaAvailable()) {
                mErrorInfo.errorType = PlayError.ERROR_TYPE_PLAY_FAILED;
                mErrorInfo.errorCode = PlayError.ERROR_CODE_BROADCASTSONG_NOTEXIST_LOCAL;
                mErrorInfo.errorMessage = "broadcastsong but not exist local";
                setErrorState();
                return false;
            }
            mDataState = PlayDataState.STATE_COMPLETE;
            EvLog.e(TAG, mCurSong.getSongName() + " local exist,direct play ");
            return startDecode();
        }
    }

    @Override
    public boolean start() {
        if (PlayError.debugPlayError(mErrorInfo)) {
            setErrorState();
            return false;
        }

        if (mState == PlayerCtrlState.STATE_PREPARING) {
            EvLog.e(TAG, "STATE_ePreparing state not handle cutsong msg ");
            return false;
        }

        if (mCurSong == null) {
            EvLog.e(TAG, "cursong is null, start failed");
            mErrorInfo.errorType = PlayError.ERROR_TYPE_PLAY_FAILED;
            mErrorInfo.errorCode = PlayError.ERROR_CODE_ITEM_NULL;
            mErrorInfo.errorMessage = "song item is null in start";
            setErrorState();
            return false;
        }

        int category = mCurSong.getSongCategory();
        if (category == SongCategory.CATEGORY_PLAYLIST) {
            return startPlayListSong();
        } else if (category == SongCategory.CATEGORY_BROADCAST) {
            return startBroadcastSong();
        } else {
            EvLog.e(TAG, mCurSong.getSongName() + ",unknow song category=" + category);
            return false;
        }
    }

    /*private void checkSoftDecode() {
        if (DeviceConfigManager.getInstance().needSoftDecode()) {
            EvLog.i("checkSoftDecode needSoftDecode,not dynamic set");
            return;
        }
        boolean enableSoftDecode = KmSharedPreferences.getInstance().getBoolean(KeyName.KEY_OPEN_SOFT_DECODE, false);
        EvLog.i("checkSoftDecode enableSoftDecode:" + enableSoftDecode);

        if (enableSoftDecode) {
            mPlayer.openSoftDecode();
        } else {
            mPlayer.closeSoftDecodeMode();
        }
    }*/

    private boolean startDecode() {
        setState(PlayerCtrlState.STATE_PREPARING);
        if (!initVideoPlayer()) {
            EvLog.e("init Player failed");
            stop(false);
            setErrorState();
            return false;
        }
        if (mPlayer.play() != 0) {
            EvLog.e("play failed");
            stop(false);
            mErrorInfo.errorType = PlayError.ERROR_TYPE_PLAY_FAILED;
            mErrorInfo.errorCode = PlayError.ERROR_CODE_ITEM_PLAY_FAILED;
            mErrorInfo.errorMessage = "play failed";
            setErrorState();
            return false;
        }
        //test
//        PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_CUT_SONG);
        return true;
    }

    @Override
    public boolean play(boolean stateNotify) {
        if (mPlayer == null) {
            EvLog.e(TAG + "play Player Null");
            return false;
        }

        int state = mPlayer.getState();
        if (state == KmPlayerState.PlayerState_ePause ||
                state == KmPlayerState.PlayerState_eBuffering) {
            if (mPlayer.play() == 0) {
                mState = PlayerCtrlState.STATE_PLAY;
                if (stateNotify) {
                    notifyOnStateChange(mState, false);
                }
            }
            return true;
        } else if (mState == PlayerCtrlState.STATE_IDLE ||
                mState == PlayerCtrlState.STATE_STOP ||
                mState == PlayerCtrlState.STATE_AUTOSTOP) {
            if (mDataState == PlayDataState.STATE_READY
                    || mDataState == PlayDataState.STATE_COMPLETE) {
                return startDecode();
            }
        }

        EvLog.e(TAG, "play invalid,mState=" + mState + ",mDataState=" + mDataState);
        return false;
    }

    public boolean revertPlayStatusWhenBuffering() {
        if (mState != PlayerCtrlState.STATE_BUFFERING) {
            EvLog.e(TAG, "updatePlayStatusWhenBuffering mState invalid" + mState);
            return false;
        }

        if (mStateBeforeBuffering == PlayerCtrlState.STATE_PLAY) {
            EvLog.d(TAG, "buffering state, change play to pause ");
            mStateBeforeBuffering = PlayerCtrlState.STATE_PAUSE;
            /*if (mListener != null&& mEnable) {
                mListener.onStateChange(PlayerCtrlState.STATE_PAUSE, null);
            }*/
            notifyOnStateChange(PlayerCtrlState.STATE_PAUSE, null);
        } else {
            EvLog.d(TAG, "buffering state, change pause to play ");
            mStateBeforeBuffering = PlayerCtrlState.STATE_PLAY;
           /* if (mListener != null&& mEnable) {
                mListener.onStateChange(PlayerCtrlState.STATE_PLAY, null);
            }*/
            notifyOnStateChange(PlayerCtrlState.STATE_PLAY, null);
        }

        return true;
    }

    @Override
    public boolean pause(boolean stateNotify) {
        if (mPlayer == null) {
            EvLog.e(TAG, "HandlePause mPlayer Null");
            return false;
        }

        int state = mPlayer.getState();

        if (state == KmPlayerState.PlayerState_ePlay && mPlayer.pause() == 0) {
            mState = PlayerCtrlState.STATE_PAUSE;
            if (stateNotify) {
                notifyOnStateChange(mState, false);
            }
            return true;
        }

        EvLog.w(TAG, "pause invalid, state:" + mState);
        return false;
    }

    @Override
    public boolean uninit() {
        if (!mIsInited) {
            EvLog.w(TAG, "uninit KmPlayerCtrlImpl is not init");
            return true;
        }

        if (mPlayer != null) {
            mPlayer.setDisplay(null);
            mPlayer.destroy();
            mPlayer = null;
        }

        mIsInited = false;
        /*if (!DeviceConfigManager.DIRECT_ONLINE_PLAY)*/
        {
            PlayListItemDownManager.getInstance().unregisterListener(this);
            PlayListItemDownManager.getInstance().uninit();
        }
        return true;
    }

    private boolean initVideoPlayer() {
        if (mPlayer != null) {
            EvLog.i(TAG, " mPlayer.getPlayerType():" + mPlayer.getPlayerType());
            if (mPlayer.getPlayerType() != KmVideoPlayerType.VLC) {
                EvLog.i(TAG, " destroy player");
                mPlayer.destroy();
                mPlayer = null;
            }
        }

        if (mPlayer == null) {
            createPlayer();
            if (mHolder != null) {
                EvLog.d(TAG, "initVideoPlayer setDisplay ");
                mPlayer.setDisplay(mHolder);
            }
        }

        if (mCurSong == null) {
            mErrorInfo.errorType = PlayError.ERROR_TYPE_PLAY_FAILED;
            mErrorInfo.errorCode = PlayError.ERROR_CODE_ITEM_NULL;
            mErrorInfo.errorMessage = "mCurSong is null in initVideoPlayer";
            UmengAgentUtil.reportError(PlayError.getUmengErrorMessage(mCurSong, mErrorInfo));
            EvLog.e(TAG, "mCurSong invalid,initPlayer failed");
            return false;
        }
        Media video = mCurSong.getVideoMedia();
        if (video == null) {
            mErrorInfo.errorType = PlayError.ERROR_TYPE_PLAY_FAILED;
            mErrorInfo.errorCode = PlayError.ERROR_CODE_ITEM_MEDIA_NULL;
            mErrorInfo.errorMessage = "mCurSong media is null in initVideoPlayer";
            EvLog.e(TAG, mCurSong.getSongName() + ",media is null");
            UmengAgentUtil.reportError(PlayError.getUmengErrorMessage(mCurSong, mErrorInfo));
            return false;
        }

        String mediaURL = video.getURI();
        Log.i("gsp", "initVideoPlayer: +++" + mediaURL);
        if (TextUtils.isEmpty(mediaURL)) {
            EvLog.e(TAG, mCurSong.getSongName() + ",getURI is null");
            mErrorInfo.errorType = PlayError.ERROR_TYPE_PLAY_FAILED;
            mErrorInfo.errorCode = PlayError.ERROR_CODE_ITEM_MEDIA_URL_NULL;
            mErrorInfo.errorMessage = "media getURL is null";
            UmengAgentUtil.reportError(PlayError.getUmengErrorMessage(mCurSong, mErrorInfo));
            return false;
        }

        String videoPath = null;

        /*if (DeviceConfigManager.DIRECT_ONLINE_PLAY) {
            videoPath = mediaURL;
        } else*/
        if (DeviceConfigManager.getInstance().getPlayMode() == IDeviceConfig.PLAY_MODE_BUFFER_MEM_PLAY) {
            EvLog.i(" video.getLocalFile=" + video.getLocalFilePath());
            try {
                String url = URLEncoder.encode(mediaURL, "UTF-8");
                videoPath = SystemConfigManager.REMOTE_HTTP_HEADER + video.getLocalFilePath() +
                        "?url=" + url + "&total_size=" + String.valueOf(video.getResourceSize());
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
            }

        } else {
            if (TextUtils.isEmpty(video.getLocalFilePath())) {
                mErrorInfo.errorType = PlayError.ERROR_TYPE_PLAY_FAILED;
                mErrorInfo.errorCode = PlayError.ERROR_CODE_ITEM_MEDIA_LOCALPATH_NULL;
                mErrorInfo.errorMessage = "getLocalFilePath is null";
                EvLog.e(TAG, mCurSong.getSongName() + ",getLocalFile is null");
                UmengAgentUtil.reportError(PlayError.getUmengErrorMessage(mCurSong, mErrorInfo));
                return false;
            }

            //无论是否是本地歌曲，都携带total_size给ForceLocalFileStream
            if (!video.isLocalFileComplete()) {
                try {
                    EvLog.i(" video.getLocalFile=" + video.getLocalFilePath());
                    String url = URLEncoder.encode(mediaURL, "UTF-8");
                    videoPath = SystemConfigManager.REMOTE_HTTP_HEADER + video.getLocalFilePath() + "?url=" + url
                            + "&local=1" + "&total_size=" + String.valueOf(video.getResourceSize());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                videoPath = SystemConfigManager.LOCAL_HTTP_HEADER + mCurSong.getVideoPath();
            }
        }

//        videoPath = "/sdcard/video.mp4";
        EvLog.d(TAG, mCurSong.getSongName() + ", source " + videoPath);
        if (mPlayer.setSource(videoPath) != 0) {
            EvLog.d(TAG, mCurSong.getSongName() + " setSource failed " + videoPath);
            return false;
        }

        float volume = getMediaVolume(mCurSong.getSongId(), video.getVolume());

        //广电曲库不用设置音量，其他曲库要设置音量
        if (DeviceConfigManager.getInstance().isUseChannelConfigByDB()) {
            EvLog.d(TAG + "set volume " + volume);
            mPlayer.setVolume(volume);
        }
        mPlayer.setAudioTrackInfo(video.getOriginalTrack(), video.getCompanyTrack());
        return true;
    }


    private float getMediaVolume(int songid, float volumeFromDB) {
        //第三方平台，不使用VOL做音量均衡，视频源上已经做了处理
        if (DeviceConfigManager.getInstance().isThirdApp()) {
            return 1.0f;
        }

        float volume = volumeFromDB / 100.0f;
        if (volume > 1.0f) {
            volume = 1.0f;
        } else if (volume <= 0.0f) {
            String errorInfo = songid + " have invalid vol = " + volumeFromDB;
            EvLog.e(TAG, errorInfo);
            volume = 0.8f;
            UmengAgentUtil.reportError(errorInfo);
        }
        return volume;
    }

    @Override
    public int getAudioTime() {
        if (mPlayer == null) {
            return 0;
        }
        return mPlayer.getAudioTime();
    }

    @Override
    public void onDownMediaError(long downId, ErrorInfo errorInfo) {
        EvLog.e("onDownMediaError " + downId + ",errorType=" + errorInfo.errorType
                + ",errorCode=" + errorInfo.errorCode);
        if (isBroadcastSongDowning(downId)) {

        } else {
            if (PlayListManager.getInstance().getPosByDownId(downId) != 0) {
                EvLog.w(TAG, "onDownMediaError," + downId + ",is not pos 0,dump");
                return;
            }

            KmPlayListItem listItem = PlayListManager.getInstance().getItemByDownId(downId);
            if (listItem == null) {
                EvLog.d(TAG, "onDownMediaError," + downId + ",is not in orderlist");
                return;
            }
            listItem.setDataState(SongDataState.STATE_ERROR);
        }
        if (mCurSong == null || mCurSong.getDownId() != downId) {
            EvLog.w(TAG, "onDownMediaError not current song onDownError,return");
            return;
        }
        mCurSong.setDataState(SongDataState.STATE_ERROR);
        EvLog.e("notify down error " + downId);
        Message msg = mHandler.obtainMessage(MSG_DOWN_ERROR);
        msg.obj = errorInfo;
        mHandler.sendMessage(msg);
        return;
    }

    private boolean getDurationByPlayer(long downId) {
        if (mCurSong == null || mCurSong.getDownId() != downId) {
            EvLog.w(TAG, "getDuration mCurSong is invalid");
            return false;
        }
        EvLog.d(TAG, mCurSong.getSongName() + ",begin to get duration >>>>> ");

        Media curSongMedia = mCurSong.getVideoMedia();
        if (curSongMedia == null) {
            EvLog.e(mCurSong.getSongName() + ",media is null");
            return false;
        }

        if (curSongMedia.getDuration() > 0) {
            mCurSong.setDuration(curSongMedia.getDuration());
            EvLog.e("direct get duration:" + curSongMedia.getDuration());
            return true;
        }

        mCurSong.setDuration(SystemConfigManager.VIDEO_DEFAULT_DURATION);
        return true;

       /* if (DeviceConfigManager.getInstance().isThirdApp()) {
            EvLog.e("third app,direct set duration 5*60");
            mCurSong.setDuration(5*60);
            return true;
        }

        if (TextUtils.isEmpty(curSongMedia.getLocalFilePath())) {
            EvLog.e(mCurSong.getSongName() + ",getLocalFile is null");
            return false;
        }

        if (TextUtils.isEmpty(curSongMedia.getURI())) {
            EvLog.e(mCurSong.getSongName() + ",getURI is null");
            return false;
        }

        EvLog.i("curSongMedia.getLocalFile=" + mCurSong.getVideoPath());
        //here should get duration
        if (mCurSong.getDuration() == 0) {
            if (this.mUseHttpd) {
                try {
                    String encodeUrl = URLEncoder.encode(curSongMedia.getURI(), "UTF-8");
                    String videoPath = SystemConfigManager.REMOTE_HTTP_HEADER + curSongMedia.getLocalFilePath()
                            + "?url=" + encodeUrl + "?support=seek";
                    DurationPlayerManager.getInstance().startGetDuration(videoPath,this);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                DurationPlayerManager.getInstance().startGetDuration(mCurSong.getVideoMedia().getURI(), this);
            }
        }
        return true;*/
    }

    @Override
    public void onDownMediaStart(long downId, String url, String savePath) {
        if (TextUtils.isEmpty(url)) {
            EvLog.w(TAG, "onDownMediaStart url is null");
            return;
        }

        KmPlayListItem item = null;
        if (isBroadcastSongDowning(downId)) {
            item = mCurSong;
            item.setVideoPath(savePath);
            item.setDataState(SongDataState.STATE_DOWNING);
        } else {
            item = PlayListManager.getInstance().getItemByDownId(downId);
            if (item == null) {
                EvLog.d(TAG, "onDownMediaStart" + "," + downId + ",downId is not in orderlist");
                return;
            }
            item.setVideoPath(savePath);
            item.setDataState(SongDataState.STATE_DOWNING);

            int pos = PlayListManager.getInstance().getPosByDownId(downId);
            if (pos < 0) {
                EvLog.d(TAG, "onDownMediaStart" + "," + downId + ",downId is not in orderlist");
                return;
            }

            if (pos > 0) {
                EvLog.d(TAG, "onDownMediaStart" + "," + downId + ",is not in pos 0");
                return;
            }
            mCurSong.copy(item);
            EvLog.d("cursong datastate = " + item.getDataState());
        }
        getDurationByPlayer(downId);
        return;
    }

    @Override
    public void onDownMediaProgress(long downId, String savePath, long downedSize, long totalSize,
                                    float speed) {
//        EvLog.d("onDownMediaProgress---------------" + downId);

        if (totalSize <= 0 || downedSize < 0) {
            EvLog.e(TAG, "onDownProgress totalSize invalid " + totalSize);
            return;
        }

        if (isBroadcastSongDowning(downId)) {
            if (mCurSong.getVideoPath() == null) {
                mCurSong.setVideoPath(savePath);
            }
        } else {
            int pos = PlayListManager.getInstance().getPosByDownId(downId);
            if (pos != 0) {
                return;
            }

            if (mCurSong == null || mCurSong.getDownId() != downId) {
                EvLog.w(TAG, "not handleDownProgress");
                return;
            }

            if (mCurSong.getVideoPath() == null) {
                mCurSong.setVideoPath(savePath);
            }
        }

        if (DeviceConfigManager.getInstance().getPlayMode() != IDeviceConfig.PLAY_MODE_DOWN_COMPLETE_PLAY) {
            calcBufferRate(downedSize, totalSize, speed);
            if (KmPlayerCtrl.getInstance().getWorkMode() == PlayMode.MODE_PLAYBACK) {
                return;
            }
//            EvLog.d("mDataState:" + mDataState);
            if (mDataState == PlayDataState.STATE_READY /*&& KmPlayerCtrl.getInstance().isSongCanPlayAfterPreLoad()*/) {
                return;
            }
        }

        Message msg = mHandler.obtainMessage(MSG_DOWN_PROGRESS);
        msg.arg1 = (int) downedSize;
        msg.arg2 = (int) totalSize;
        msg.getData().putFloat("speed", speed);
        mHandler.sendMessage(msg);
        return;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDownErcSuccess(long downId, String path) {
        if (isBroadcastSongDowning(downId)) {
            //FIXME
        } else {
            //是否存在于已点列表中
            KmPlayListItem listItem = PlayListManager.getInstance().getItemByDownId(downId);
            listItem.setErcDownFinish(true);
            if (listItem == null) {
                EvLog.d(TAG, "onDownErcSuccess," + downId + ",not in orderlist,return");
                return;
            }
            listItem.setSubtitlePath(path);
            if (listItem.isMediaAvailable()) {
                listItem.setDataState(SongDataState.STATE_COMPLETE);
                listItem.updateResourceToDB();
            }

            Media media = listItem.getVideoMedia();

            int pos = PlayListManager.getInstance().getPosByDownId(downId);

            if (pos != 0) {
                EvLog.w(TAG, "onDownErcSuccess," + pos);
                if (/*media.hasLocalFile()*/media.isLocalFileComplete()) {
                    EvLog.i(TAG, "onDownErcSuccess," + pos + ",traverseOrderList");
                    mHandler.sendEmptyMessageDelayed(PLAY_LIST_CHANGE, 1000);
                }
                return;
            }

            if (mCurSong != null) {
                listItem.setDuration(mCurSong.getDuration());
                mCurSong.copy(listItem);
            }

            if (mCurSong == null || mCurSong.getDownId() != downId) {
                EvLog.w(TAG, "onDownErcSuccess," + downId + ",not current song,return");
                return;
            }
            if (mCurSong.isMediaAvailable()) {
                mCurSong.setDataState(listItem.getDataState());
                mCurSong.updateMedia(media);
            }
        }

        mCurSong.setSubtitlePath(path);
        if (mCurSong.isMediaAvailable() || mDataState == PlayDataState.STATE_READY) {
//            mDataState = PlayDataState.STATE_READY;
            EvLog.d(TAG, "erc down finish,video ok, play");
            EvLog.w(TAG, mCurSong.getSongName() + " is data ready onDownErcSuccess,call onDataReady");
            notifyOnDataReady(mCurSong.getSerialNum());
            if (PlayListManager.getInstance().getCount() > 1) {
                EvLog.i(TAG, "onDownErcSuccess pos 0,traverseOrderList");
                mHandler.sendEmptyMessageDelayed(PLAY_LIST_CHANGE, 1000);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDownErcFailed(long downId) {
        if (isBroadcastSongDowning(downId)) {
            mCurSong.setDataState(SongDataState.STATE_INCOMPLETE);
        } else {
            //是否存在于已点列表中
            KmPlayListItem listItem = PlayListManager.getInstance().getItemByDownId(downId);
            if (listItem == null) {
                EvLog.d(TAG, "onDownErcFailed " + downId + " not in orderlist,return");
                return;
            }
            listItem.setErcDownFinish(true);
            listItem.setDataState(SongDataState.STATE_INCOMPLETE);

            if (PlayListManager.getInstance().getPosByDownId(downId) != 0) {
                EvLog.w(TAG, "onDownErcFailed " + downId + " is not pos 0,return");
                //FIXME is need continue traverseOrderList
                return;
            }
            if (mCurSong == null || mCurSong.getDownId() != downId) {
                EvLog.w(TAG, "onDownErcFailed not current songdump");
                return;
            }
            mCurSong.copy(listItem);
        }

        if (mCurSong.isMediaAvailable() || mDataState == PlayDataState.STATE_READY) {
//            mCurSong.setDataState(listItem.getDataState());
//            mDataState = PlayDataState.STATE_READY;
            EvLog.w(mCurSong.getSongName() + " is data ready onDownErcFailed,call onDataReady");
            /*if (mListener != null) {
                mListener.onDataReady();
            }*/
            notifyOnDataReady(mCurSong.getSerialNum());
            if (PlayListManager.getInstance().getCount() > 1) {
                EvLog.i(TAG, "onDownErcFailed pos 0,traverseOrderList");
                mHandler.sendEmptyMessageDelayed(PLAY_LIST_CHANGE, 1000);
            }
        }
    }

    @Override
    public void onDownMediaFinish(long downId, String savePath) {
        EvLog.i("onDownMediaFinish downId=" + downId);

        if (DeviceConfigManager.getInstance().getPlayMode() == IDeviceConfig.PLAY_MODE_BUFFER_MEM_PLAY) {
            EvLog.i("SUPPORT_BUFFER_PLAY mode,just return");
            return;
        }

        if (isBroadcastSongDowning(downId)) {
            EvLog.i("onDownMediaFinish BroadcastSong name=" + mCurSong.getSongName());
            mCurSong.setVideoPath(savePath);
            if (mCurSong.getSongId() == BroadcastListManager.getInstance().getCurrentBSongId()) {
                BroadcastListManager.getInstance().setCurrentBSongComplete(mCurSong.getVideoMedia(), savePath);
            }
        } else {
            KmPlayListItem listItem = PlayListManager.getInstance().getItemByDownId(downId);
            if (listItem == null) {
                EvLog.d(TAG, "onDownMediaFinish " + downId + " not in orderlist,return");
                return;
            }
            EvLog.i("onDownMediaFinish name=" + listItem.getSongName());
            listItem.setVideoPath(savePath);

            EvLog.d("third app, not update media resource to db");

            if (mCurSong == null || mCurSong.getDownId() != downId) {
                EvLog.w(TAG, "onDownMediaFinish not current song,return");
                return;
            }
            mCurSong.copy(listItem);
        }

        Media videoMedia = mCurSong.getVideoMedia();

        //第三方应用，非第0位歌曲，不下载
        if (videoMedia != null) {
            mDataState = PlayDataState.STATE_READY;
            EvLog.w(mCurSong.getSongName() + " is data ready onDownMediaFinish,call onDataReady");
            notifyOnDataReady(mCurSong.getSerialNum());
        }
    }

    @Override
    public int getMode() {
        return PlayMode.MODE_NORMAL;
    }

    @Override
    public void onNothingDown(int serialNum) {
        EvLog.e(TAG, "onNothingDown serialNum=" + serialNum);

        //是否存在于已点列表中
        KmPlayListItem listItem = PlayListManager.getInstance().getItemBySerialNum(serialNum);
        if (listItem == null) {
            EvLog.d(TAG, "onNothingDown " + serialNum + " not in orderlist,return");
            return;
        }

        listItem.setDataState(SongDataState.STATE_NONE);

        if (PlayListManager.getInstance().getPos(serialNum) != 0) {
            EvLog.w(TAG, "onNothingDown " + serialNum + " is not pos 0,return");
            //FIXME is need continue traverseOrderList
            return;
        }
        if (mCurSong == null || mCurSong.getSerialNum() != serialNum) {
            EvLog.w(TAG, "onDownErcFailed not current songdump");
            return;
        }

        mCurSong.copy(listItem);

        if (mCurSong.isMediaAvailable()) {
//            mCurSong.setDataState(listItem.getDataState());
            mDataState = PlayDataState.STATE_READY;
            EvLog.w(mCurSong.getSongName() + " is data ready onDownErcFailed,call onDataReady");
            notifyOnDataReady(mCurSong.getSerialNum());
            if (PlayListManager.getInstance().getCount() > 1) {
                EvLog.i(TAG, "onNothingDown,traverseOrderList");
                mHandler.sendEmptyMessageDelayed(PLAY_LIST_CHANGE, 1000);
            }
        } else {
            mErrorInfo.errorType = DownError.ERROR_TYPE_DOWN_MEDIA_FAILED;
            mErrorInfo.errorCode = DownError.ERROR_CODE_ITEM_MEDIA_NULL;
            mErrorInfo.errorMessage = "onNothingDown";
            setErrorState();
        }
    }

    private void onPlayListChangeEvent() {
        if (PlayListManager.getInstance().getCount() == 0) {
            EvLog.d("clear mDowningList-------------");
            if (mGetMediaListPresenter != null && mGetMediaListPresenter.isStarted()) {
                mGetMediaListPresenter.setSerialNum(-1);
                mGetMediaListPresenter.cancel();
                mGetMediaListPresenter = null;
            }

            long downingId = PlayListItemDownManager.getInstance().getDowningId();
            PlayListItemDownManager.getInstance().delItem(downingId);
            return;
        }
        getNextItemToDown();
    }

    @Override
    public void listChange() {
        EvLog.d("third app, not down media when play list change");
    }

    private void getNextItemToDown() {
        EvLog.e("getNextItemToDown-----------");
        int i = 0;
        for (; i < PlayListManager.getInstance().getCount(); i++) {
            KmPlayListItem item = PlayListManager.getInstance().getItemByPos(i);
            if (item == null) {
                continue;
            }
            if (item.isResourceComplete()) {
//                EvLog.i(item.getSongName() + " isResourceComplete");
                continue;
            }

            if (mGetMediaListPresenter != null && mGetMediaListPresenter.isStarted()) {
                if (mGetMediaListPresenter.getSerialNum() == item.getSerialNum()) {
                    EvLog.d(item.getSongName() + " is in GetMediaList stage");
                    return;
                } else {
                    mGetMediaListPresenter.cancel();
                    mGetMediaListPresenter = null;
                }
            }

            long downingId = PlayListItemDownManager.getInstance().getDowningId();

            if (downingId != mCurSong.getDownId()) {
                PlayListItemDownManager.getInstance().delItem(downingId);
            } else {
                EvLog.i(item.getSongName() + " is Downing media");
                return;
            }

            EvLog.e(item.getSongName() + " need to down,pos in playlist=" + i);
            startGetMediaList(item);
            break;
        }
       /* if (i == 0) {
            KmSongDownManager.getInstance().setThreadPriority(Thread.NORM_PRIORITY);
        } else {
            KmSongDownManager.getInstance().setThreadPriority(Thread.MIN_PRIORITY);
        }*/
        return;
    }

    private GetMediaListPresenter mGetMediaListPresenter = null;

    private void startGetMediaList(KmPlayListItem item) {
        if (item == null) {
            EvLog.e("startGetMediaList ,but item is null");
            return;
        }
        if (mGetMediaListPresenter != null) {
            mGetMediaListPresenter.setStop();
            mGetMediaListPresenter.cancel();
            mGetMediaListPresenter = null;
        }
        EvLog.e("begin to startGetMediaList:" + item.getSongName());

        mGetMediaListPresenter = new GetMediaListPresenter(this);
        mGetMediaListPresenter.setListener(this);
        mGetMediaListPresenter.start(item);
        item.setDataState(SongDataState.STATE_GET_MEDIA);

        updateCurrentSongDataState(item.getSerialNum(), SongDataState.STATE_GET_MEDIA);

        mHandler.sendEmptyMessage(MSG_UPDATE_GET_MEDIA_STATE);
    }

    private void updateCurrentSongDataState(int serialNum, int state) {
        if (mCurSong == null || mCurSong.getSerialNum() != serialNum) {
            EvLog.w(TAG, "serialNum not same, not update CurrentSong DataState");
            return;
        }
        mCurSong.setDataState(state);
    }


    private boolean isItemMediaValid(KmPlayListItem item) {
        if (item == null) {
            return false;
        }
        Media media = item.getVideoMedia();
        if (media == null) {
            EvLog.d(item.getSongName() + " media is null");
            ErrorInfo errorInfo = new ErrorInfo();
            errorInfo.errorType = DownError.ERROR_TYPE_DOWN_MEDIA_FAILED;
            errorInfo.errorCode = DownError.ERROR_CODE_ITEM_MEDIA_NULL;
            errorInfo.errorMessage = "get no video media in onGetMediaListSuccess";
            UmengAgentUtil.reportError(DownError.getUmengErrorMessage(item, errorInfo));
            onDownMediaError(item.getSerialNum(), errorInfo);
            return false;
        } else if (TextUtils.isEmpty(media.getURI())) {
            ErrorInfo errorInfo = new ErrorInfo();
            errorInfo.errorType = DownError.ERROR_TYPE_DOWN_MEDIA_FAILED;
            errorInfo.errorCode = DownError.ERROR_CODE_MEDIA_URL_NULL;
            errorInfo.errorMessage = "video media url is empty";
            UmengAgentUtil.reportError(DownError.getUmengErrorMessage(item, errorInfo));
            onDownMediaError(item.getSerialNum(), errorInfo);
            return false;
        }
        return true;
    }

    @Override
    public void onGetMediaListSuccess(int serialNum, List<OnlineFileItem> itemList) {
        EvLog.e("onGetMediaListSuccess ,itemList.size=" + itemList.size());
        mGetMediaListPresenter = null;
        KmPlayListItem item = null;
        if (PlayListManager.getInstance().getCount() == 0) {
            if (mCurSong != null
                    && mCurSong.getSerialNum() == serialNum
                    && mCurSong.getSongCategory() == SongCategory.CATEGORY_BROADCAST
                    && DeviceConfigManager.getInstance().getBroadcastSongType() == IDeviceConfig.BROADCAST_SONG_TYPE_ONLINE) {
                item = mCurSong;
                mCurSong.setDataState(SongDataState.STATE_PREPARE_DOWN);
            }
        } else {
            item = PlayListManager.getInstance().getItemBySerialNum(serialNum);
            if (item == null) {
                EvLog.e("not in playlist,dump!");
                return;
            }
            EvLog.e("onGetMediaListSuccess , find " + item.getSongName());

            item.setDataState(SongDataState.STATE_PREPARE_DOWN);
            mCurSong.copy(item);
            //更新当前歌曲的下载状态
//            updateCurrentSongDataState(serialNum,SongDataState.STATE_PREPARE_DOWN);
        }

        mHandler.sendEmptyMessage(MSG_UPDATE_GET_MEDIA_STATE);

        if (itemList.size() == 0) {
//            onNothingDown(serialNum);
            EvLog.d("onGetMediaListSuccess itemList size=0");
            mErrorInfo.errorType = DownError.ERROR_TYPE_DOWN_MEDIA_FAILED;
            mErrorInfo.errorCode = DownError.ERROR_CODE_ITEM_MEDIA_NULL;
            mErrorInfo.errorMessage = "onGetMediaListSuccess itemList size=0";
            setErrorState();
            return;
        }

        if (!isItemMediaValid(item)) {
            return;
        }

        Media media = item.getVideoMedia();
        //判断是否需要下载media
        boolean needDownMedia = !media.isLocalFileComplete();

        Task mediatask = null;
        if (needDownMedia) {
            EvLog.d(item.getSongName() + ",need down video=" + media.getURI());
            OnlineFileItem onlineItem = null;
            for (int i = 0; i < itemList.size(); i++) {
                if (itemList.get(i).type == OnlineFileItem.TYPE_MEDIA) {
                    onlineItem = itemList.get(i);
                    break;
                }
            }

            if (onlineItem == null) {
                ErrorInfo errorInfo = new ErrorInfo();
                errorInfo.errorType = DownError.ERROR_TYPE_DOWN_MEDIA_FAILED;
                //FIXME
                errorInfo.errorCode = DownError.ERROR_CODE_MEDIA_URL_NULL;
                errorInfo.errorMessage = "get no media list from dc";
                UmengAgentUtil.reportError(DownError.getUmengErrorMessage(item, errorInfo));
                onDownMediaError(serialNum, errorInfo);
                return;
            }
            String filePath = FileUtil.concatPath(onlineItem.savePath, onlineItem.fileName);
            filePath += ResourceSaverPathManager.FILE_TMP_SUFFIX;
            FileUtil.createFile(filePath);
//            downMedia(onlineItem,serialNum,filePath,media.getId());
            mediatask = new Task(onlineItem.url, filePath);
            if (onlineItem.totalSize > 0) {
                mediatask.fileTotalLen = onlineItem.totalSize;
            }
        }

        OnlineFileItem ercItem = null;

        if (!needDownMedia) {
            EvLog.e("1473=========================");
            onDownMediaFinish(serialNum, media.getLocalFilePath());
        } else {
            PlayListItemDown itemDown = new PlayListItemDown(ercItem, mediatask);
            PlayListItemDownManager.getInstance().addItem(itemDown);
            item.setDownId(itemDown.getDownId());
            EvLog.d(item.getSongName() + " set DownId=" + itemDown.getDownId());
            onDownMediaStart(itemDown.getDownId(), mediatask.url, mediatask.localPath);
        }
    }

    @Override
    public void onGetMediaListFailed(int serialNum, ErrorInfo errorInfo) {
//        onGetMediaListStateChange(serialNum,DownState.GET_MEDIA_FAILED,errorInfo);
        mGetMediaListPresenter = null;
        if (PlayListManager.getInstance().getCount() == 0) {
            if (mCurSong != null
                    && mCurSong.getSerialNum() == serialNum
                    && mCurSong.getSongCategory() == SongCategory.CATEGORY_BROADCAST
                    && DeviceConfigManager.getInstance().getBroadcastSongType() == IDeviceConfig.BROADCAST_SONG_TYPE_ONLINE) {
                mCurSong.setDataState(SongDataState.STATE_NONE);
            }
        } else {
            KmPlayListItem item = PlayListManager.getInstance().getItemBySerialNum(serialNum);
            if (item == null) {
                EvLog.d(TAG, "onGetMediaListStateChange," + serialNum + ",is not in orderlist");
                return;
            }
            item.setDataState(SongDataState.STATE_NONE);
            if (mCurSong == null || mCurSong.getSerialNum() != serialNum) {
                EvLog.w(TAG, "onGetMediaListStateChange not current song,return");
                return;
            }
            mCurSong.setDataState(SongDataState.STATE_NONE);
        }
        EvLog.e("onGetMediaListFailed, serialNum=" + serialNum + ",type=" + errorInfo.errorType + ",errorCode=" + errorInfo.errorCode);
        EvLog.e("notify down error " + serialNum);
        Message msg = mHandler.obtainMessage(MSG_DOWN_ERROR);
        msg.obj = errorInfo;
        mHandler.sendMessage(msg);
        return;
    }

    @Override
    public boolean isCanDelete(int mediaId) {
        int indexEnd = PlayListManager.getInstance().getCount();

        long downingId = PlayListItemDownManager.getInstance().getDowningId();
        if (downingId != -1) {
            indexEnd = PlayListManager.getInstance().getPosByDownId(downingId);
        }

        if (indexEnd < 0 || indexEnd > PlayListManager.getInstance().getCount()) {
            EvLog.e("find downing serialNum " + downingId + ",not in orderlist");
            indexEnd = PlayListManager.getInstance().getCount();
        }

        KmPlayListItem item = null;
        EvLog.d("isCanDelete," + mediaId + ",check whether in playlist[0-" + indexEnd + "]");
        for (int i = 0; i < indexEnd; i++) {
            item = PlayListManager.getInstance().getItemByPos(i);
            if (item != null && item.getVideoMedia() != null && mediaId == item.getVideoMedia().getId()) {
                EvLog.d("isCanDelete can not release " + item.getSongName());
                return false;
            }
        }
        return true;
    }


    private boolean isBroadcastSongDowning(long downId) {
        if (mCurSong != null &&
                mCurSong.getDownId() == downId &&
                mCurSong.getSongCategory() == SongCategory.CATEGORY_BROADCAST &&
                DeviceConfigManager.getInstance().getBroadcastSongType() == IDeviceConfig.BROADCAST_SONG_TYPE_ONLINE) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isNeedGetMediaUrl(int serialNum) {

        KmPlayListItem item = null;

        if (PlayListManager.getInstance().getCount() == 0) {
            if (mCurSong != null
                    && mCurSong.getSerialNum() == serialNum
                    && mCurSong.getSongCategory() == SongCategory.CATEGORY_BROADCAST
                    && DeviceConfigManager.getInstance().getBroadcastSongType() == IDeviceConfig.BROADCAST_SONG_TYPE_ONLINE) {
                item = mCurSong;
            }
        } else {
            item = PlayListManager.getInstance().getItemBySerialNum(serialNum);
        }

        if (item == null) {
            EvLog.e("not in playlist,dump!");
            return false;
        }

        Media media = item.getVideoMedia();
        if (media == null) {
            EvLog.e("item video media is null!");
            return true;
        }

        if (media.isLocalFileComplete()) {
            EvLog.e(serialNum + " do not need get media url");
        } else {
            EvLog.e(serialNum + " do need get media url");
        }
        return !media.isLocalFileComplete()/*hasLocalFile()*/;
    }

    @Override
    public boolean isNeedGetErcUrl(int serialNum) {
        return false;
    }

    @Override
    public String getMediaSavePath(long needSpace) {
        StorageVolume volume = DiskUtil.getSuitableVolume(needSpace, KmPlayManager.this);
        //空间不足
        if (volume == null) {
            return null;
        } else {
            return volume.getMediaPath();
        }
    }

    @Override
    public void seekToTime(long time) {
        if (mPlayer != null) {
            mPlayer.seekToTime(time);
        }
    }

    /*private boolean isNeedEnableSoftDecode() {
        boolean systemCheck = DeviceConfigManager.getInstance().needSoftDecode();
        boolean enableSoftDecode = KmSharedPreferences.getInstance().getBoolean(KeyName.KEY_OPEN_SOFT_DECODE, false);
        EvLog.i("isNeedEnableSoftDecode systemCheck:" + systemCheck + ",enableSoftDecode:" + enableSoftDecode);
        return (systemCheck || enableSoftDecode) ;
    }*/

    private MediaPlayerFactory mFactory = null;

    @Override
    public void createPlayer() {
        /*if (isNeedEnableSoftDecode()) {
            KmVideoPlayer.gOpenSoftDecode = true;
        }*/
        if (mFactory == null) {
            mFactory = new MediaPlayerFactory();
        }
        int playerId = KmSharedPreferences.getInstance().getInt(KeyName.KEY_USE_VLC_DECODE, KmVideoPlayerType.VLC);
        EvLog.i(TAG, " createPlayer player:" + playerId);
        mPlayer = mFactory.getPlayer(playerId, MainViewManager.getInstance().getVideoView());
        mPlayer.setListener(mPlayerEvent);
    }

    @Override
    public boolean setAudioSingMode(int mode) {
        if (mPlayer != null) {
            mPlayer.setAudioSingMode(mode);
        }
        return true;
    }
}
