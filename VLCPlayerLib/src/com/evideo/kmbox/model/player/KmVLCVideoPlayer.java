package com.evideo.kmbox.model.player;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.util.EvLog;

public class KmVLCVideoPlayer implements IKmPlayer, EvVLCMediaPlayer.OnBufferingUpdateListener,
        EvVLCMediaPlayer.OnCompletionListener, EvVLCMediaPlayer.OnErrorListener,
        EvVLCMediaPlayer.OnPreparedListener, EvVLCMediaPlayer.OnStopedListener,
        EvVLCMediaPlayer.OnFirstFrameListener, EvVLCMediaPlayer.OnAudioCreatedListener {

    public static final String TAG = "vlc";
    private EvVLCMediaPlayer mPlayer = null;

    private String mUrl = null;
    private float mVolume = 1.0f;
    //    private float mBeforeMuteVolume = 1.0f;
    int mState = KmPlayerState.PlayerState_eIdle;
    int mStateBeforeBuffering = KmPlayerState.PlayerState_eIdle;
    private int mSingMode = KmAudioTrackMode.MODE_ORI;

    //    private int[] mAudioTrackInfo;
//    private int mAudioTrackNum = -1;
    private int mAudioOrgTrackIndex = -1;
    private int mAudioAccTrackIndex = -1;

    // add by qiangv
    private int mAudioTrackCount = 0;
    private int mOriginalInfo = 0;
    private int mAccompanyInfo = 0;

    private IKmPlayerEvent mListener = null;

//    private SurfaceHolder mHolder = null;

    private boolean mIsAutoStop = true;

    // add by lyt -vlc media
    private int mFirstTrack;
    private int mSecondTrack;
//    private int mIndex;

    private DefaultVideoRenderView mRenderView = null;
    private SurfaceView mSurfaceView = null;
    private boolean mIsSurfaceCreated = false;
    private final Object mReadyLock = new Object();
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    public static boolean gOpenSoftDecode = false;

    public KmVLCVideoPlayer(Object renderView) {
        mRenderView = (DefaultVideoRenderView) renderView;
        initDisplay();
        mPlayer = new EvVLCMediaPlayer(gOpenSoftDecode);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnPreparedListener(this);
//        mPlayer.setOnInfoListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnBufferingUpdateListener(this);
        mPlayer.setOnStopedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnFirstFrameListener(this);
        mPlayer.setOnAudioCreatedListener(this);
        mPlayer.setOnErrorListener(this);
//        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mState = KmPlayerState.PlayerState_eIdle;
//        mAudioTrackNum = -1;
        mAudioOrgTrackIndex = -1;
        mAudioAccTrackIndex = -1;
    }

    private void initDisplay() {
        if (mSurfaceView != null) {
            EvLog.i("mPlayer setSurface--------------");
            mPlayer.setDisplay(mSurfaceView.getHolder());
        } else {
            if (mRenderView != null) {
                runOnHandlerSync(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "run: add surface view to render view");
                        mSurfaceView = new SurfaceView(mRenderView.getContext());
                        mRenderView.addView(mSurfaceView);

                        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                            @Override
                            public void surfaceCreated(SurfaceHolder holder) {
                                EvLog.i(TAG + " surfaceCreated");
                                synchronized (mReadyLock) {
                                   /* if (mPlayer != null)
                                        mPlayer.setSurface(holder.getSurface());*/
                                    mIsSurfaceCreated = true;
                                }
                            }

                            @Override
                            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                            }

                            @Override
                            public void surfaceDestroyed(SurfaceHolder holder) {

                            }
                        });

                    }
                }, mMainHandler);
            }
        }
    }

    void runOnHandlerSync(final Runnable task, Handler handler) {
        final Object lock = new Object();
        if (Looper.myLooper() == handler.getLooper()) {
            task.run();
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    task.run();
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            });
            synchronized (lock) {
                try {
                    EvLog.i(TAG + "runOnHandlerSync: begin wait");
                    lock.wait(5000);
                    EvLog.i(TAG + "runOnHandlerSync: finish wait");
                } catch (InterruptedException e) {
                    EvLog.e(TAG + "runOnHandlerSync: wait being interrupted:" + e.getMessage());
                }
            }
        }
    }

   /* @Override
    public int create() {
        return 0;
    }*/

    @Override
    public int destroy() {
        if (mPlayer != null) {
            stop();
            mPlayer.release();
            mPlayer = null;
            mState = KmPlayerState.PlayerState_eUnInit;
        }
        mIsSurfaceCreated = false;
        if (mRenderView != null && mSurfaceView != null) {
            runOnHandlerSync(new Runnable() {
                @Override
                public void run() {
                    mRenderView.removeView(mSurfaceView);
                    mSurfaceView = null;
                    EvLog.d(TAG + " mSurfaceVIew removed");
                }
            }, mMainHandler);
        }
        return 0;
    }

    @Override
    public int setSource(String url) {

        if (mState == KmPlayerState.PlayerState_eIdle
                || mState == KmPlayerState.PlayerState_eStoped
                || mState == KmPlayerState.PlayerState_eErrors
                || mState == KmPlayerState.PlayerState_eInit) {
            mUrl = url;

            resetPlayStatus();

            boolean setRet = false;
            try {
                // 这里也可不要
//                if (mHolder != null) {
//                    mPlayer.reset(mHolder);
//                }
                mPlayer.setDataSource(mUrl);
                /*if (mHolder != null) {
                    mPlayer.setDisplay(mHolder);
                }*/
                initDisplay();
                mState = KmPlayerState.PlayerState_eInit;
                setRet = true;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                setRet = false;
            } catch (SecurityException e) {
                e.printStackTrace();
                setRet = false;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                setRet = false;
//            } catch (IOException e) {
//                e.printStackTrace();
//                setRet = false;
            }

            if (setRet == false) {
                return -1;
            } else {
                return 0;
            }
        } else {
            EvLog.w(" setSource invalid state " + mState);
            return -1;
        }
    }

   /* @Override
    public int setAudioSource(List<String> audioList) {
        EvLog.e(" video player is not need to set audio source");
        return 0;
    }

    @Override
    public int setAudioSource(String path) {
        return 0;
    }*/

    @Override
    public String getSource() {

        return mUrl;
    }

    @Override
    public int setListener(IKmPlayerEvent eventHandles) {
        mListener = eventHandles;
        return 0;
    }

    @Override
    public int play() {
        EvLog.e(TAG, "vlc player play:" + mState);
        if (mState == KmPlayerState.PlayerState_eInit) {
            try {
                EvLog.i(TAG, "vlc player prepareAsync");
                mPlayer.prepareAsync();
                mState = KmPlayerState.PlayerState_ePreparing;
            } catch (IllegalStateException e) {
                // FIXME upload error message
                e.printStackTrace();
                mState = KmPlayerState.PlayerState_eIdle;
                return -1;
            }
        } else {
            EvLog.i(TAG, "vlc player play");
            mPlayer.start();
            EvLog.d("-->>set playstate");
            mState = KmPlayerState.PlayerState_ePlay;
        }

        return 0;
    }

    @Override
    public int pause() {
        if (mState == KmPlayerState.PlayerState_ePlay) {
            EvLog.d("KmVideoPlayer pause");
            mPlayer.pause();
            mState = KmPlayerState.PlayerState_ePause;
        } else {
            EvLog.w(" pause invalid state " + mState);
        }
        return 0;
    }

    @Override
    public int stop() {
        if (mState == KmPlayerState.PlayerState_ePlay
                || mState == KmPlayerState.PlayerState_ePause
                || mState == KmPlayerState.PlayerState_eBuffering
                || mState == KmPlayerState.PlayerState_ePrepared
                || mState == KmPlayerState.PlayerState_ePreparing) {

            mPlayer.stop();
            EvLog.w(" stop in state: " + mState);
            mState = KmPlayerState.PlayerState_eIdle;

//            resetPlayStatus();
        } else {
            EvLog.e("stop invalid state " + mState);
        }
        return 0;
    }

    private void resetPlayStatus() {
//        mPlayer.reset();
//        mAudioTrackNum = -1;
        mAudioTrackCount = 0;
//        mVolume = 0.0f;
        // mState = KmPlayerState.PlayerState_eIdle;
    }

    @Override
    public int getCurrentPosition() {
//        if (mState == KmPlayerState.PlayerState_ePlay
//                || mState == KmPlayerState.PlayerState_ePause) {
        return (int) mPlayer.getCurrentTime();
/*        } else {
            EvLog.w(" getCurrentPosition invalid state " + mState);
            return -1;
        }*/
    }

    @Override
    public int getState() {
        return mState;
    }

    /*@Override
    public PlayerInfo getInfo() {

        return null;
    }*/

    @Override
    public int getTotalTime() {

        if (mState == KmPlayerState.PlayerState_ePlay
                || mState == KmPlayerState.PlayerState_eBuffering
                || mState == KmPlayerState.PlayerState_ePause) {
            return mPlayer.getDuration();
        } else {
            EvLog.w(" should not get video TotalTime in " + mState);
            return -1;
        }
    }

    @Override
    public int seekToTime(long time) {

        /*if (time < 0 || time > mPlayer.getDuration()) {
            Log.e("videoplayer", " time is invalid " + time);
            return -1;
        }

        if (mState == KmPlayerState.PlayerState_ePlay
                || mState == KmPlayerState.PlayerState_ePause) {
            mPlayer.setTime(time);
        } else {
            EvLog.w("seekToTime invalid state to seektime");
        }*/
        if (mPlayer != null) {
            mPlayer.setTime(time);
        }
        return 0;
    }

  /*  @Override
    public List<String> getAudioSource() {
        EvLog.e("video player should not call getAudioSource");
        return null;
    }*/

    @Override
    public boolean setAudioSingMode(int mode) {

        if (mState == KmPlayerState.PlayerState_ePlay
                || mState == KmPlayerState.PlayerState_ePause
                || mState == KmPlayerState.PlayerState_eBuffering) {
            EvLog.d(mState + ", setAudioSingMode mode " + mode);
            switchTrack(mode);
        } else {
            EvLog.d(mState + ", save mode " + mode);
        }
        mSingMode = mode;

        return true;
    }

    @Override
    public int getAudioSingMode() {
        return mSingMode;
    }

    @Override
    public int setVolume(float volume) {
        if (mPlayer != null) {
            mVolume = volume;
            Log.w("VLCVideoplayer", mState + " ,setVolume to " + volume);
            mPlayer.setVolume(mVolume, mVolume);
            /*if (mState == KmPlayerState.PlayerState_ePlay
                    || mState == KmPlayerState.PlayerState_ePause
                    || mState == KmPlayerState.PlayerState_eBuffering) {
                Log.w("VLCVideoplayer", "setVolume to " + volume);
                mPlayer.setVolume(mVolume, mVolume);
            } else {
                Log.w("videoplayer", mState + " invalid, not  setVolume");
            }*/
        }

        return 0;
    }

    @Override
    public float getVolume() {
        return mVolume;
    }

    /*@Override
    public void mute() {
        mPlayer.setVolume(0.0f, 0.0f);
    }

    @Override
    public void unmute() {
        mPlayer.setVolume(1.0f, 1.0f);
    }

    @Override
    public EvMediaPlayer getMediaPlayer() {
        return mPlayer;
    }

    @Override
    public KmVideoPlayer getVideoPlayer() {
        return this;
    }

    @Override
    public void destroyAudioPlayer() {

    }

    @Override
    public void switchTrackOrChannel() {

    }*/

    // @Override
    // public void onCompletion(EvMediaPlayer arg0) {
    // if (mListener != null) {
    // mListener.OnComplete();
    // }
    //
    // mState = KmPlayerState.PlayerState_eIdle;
    // }

    // TODO vlc方案中暂时弃用
    //弃用原因说明：在原生MediaPlayer中onPrepared之后才有onBuffering，但是在vlc中onPrepared状态不好确定，目前
    //是把onBuffering（100%）定义为onPrepared，和原生的流程有很大的冲突
    @Override
    public void onPrepared() {
        EvLog.i(TAG, "onPrepared");
/*        if (mState == KmPlayerState.PlayerState_ePreparing) {
            mState = KmPlayerState.PlayerState_ePrepared;
//            initBaseInfo();
//            if (mListener != null) {
//                mListener.OnPlay();
//            }
        } else {
            EvLog.d("onPrepared not start at " + mState);
        }*/
    }

    private void initTrackInfo() {
        if (mPlayer == null) {
            return;
        }

        boolean ret = mPlayer.initAudioTrackInfo();
        if (!ret) {
            EvLog.e("initTrackInfo failed");
            return;
        }
        EvLog.d("mPlayer.getAudioTrackCount() = " + mPlayer.getAudioTrackCount());
        if (mPlayer.getAudioTrackCount() >= 3) {
            mFirstTrack = mPlayer.getFirstAudioTrackIndex();
            mSecondTrack = mPlayer.getSecondAudioTrackIndex();
//            mIndex = mPlayer.getCurrentAudioTrackIndex();

            EvLog.w("first track:" + mFirstTrack);
            EvLog.w("second track:" + mSecondTrack);

            if (mAudioOrgTrackIndex == 1) {
                mOriginalInfo = mSecondTrack;
                mAccompanyInfo = mFirstTrack;
            } else {
                mOriginalInfo = mFirstTrack;
                mAccompanyInfo = mSecondTrack;
            }
        }

        if (mPlayer.getAudioTrackCount() <= 2) {
            EvLog.d("track count =2 ->>");
            mOriginalInfo = mAudioOrgTrackIndex;
            mAccompanyInfo = mAudioAccTrackIndex;
        }

    }


    private void initBaseInfo() {
        EvLog.i(TAG, "initBaseInfo");
//        mAudioTrackNum = -1;
        EvLog.d("-->>set playstate");
        mState = KmPlayerState.PlayerState_ePlay;
        // 音轨初始化
        initTrackInfo();
        this.switchTrack(mSingMode);
        // 切换默认音轨

        // 设置音量
        EvLog.d(TAG, "-->>initBaseInfo mVolume = " + mVolume);
        if (mVolume <= 0) {
            mPlayer.setVolume(mVolume, mVolume);
        } else {
            mPlayer.setVolume(mVolume, mVolume);
//            mPlayer.volumeFadeIn(0, (int) (100 * mVolume));
        }
    }

    @Override
    public void onBufferingUpdate(int arg1) {
        if (mListener != null) {
            EvLog.d("-->>buffering percent:" + arg1);
            mListener.onBufferingUpdate(arg1);
        }
    }

    @Override
    public void onBufferingStart() {
        EvLog.i("onBufferingStart");
        if (mListener != null) {
            EvLog.d("buffering start");
            mListener.onBufferingStart();
        }
    }

    @Override
    public void onBufferingEnd() {
        if (mListener != null) {
            EvLog.d("buffering end");
            mListener.onBufferingEnd();
        }
    }

    public boolean onInfo(EvVLCMediaPlayer arg0, int arg1, int arg2) {
        switch (arg1) {
            case EvVLCMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                if (mState == KmPlayerState.PlayerState_ePlay) {
                    if (mListener != null) {
                        EvLog.i("onInfo recv MEDIA_INFO_VIDEO_RENDERING_START,call onPlay");
                        mListener.onPlay();
                    }
                } else
                    EvLog.e("onInfo errror state " + mState);
                break;
            case EvVLCMediaPlayer.MEDIA_INFO_BUFFERING_START:
                EvLog.d("video recv MEDIA_INFO_BUFFERING_START ");
                // mStateBeforeBuffering = mState;
                // mState = KmPlayerState.PlayerState_eBuffering;
                if (mListener != null)
                    mListener.onBufferingStart();
                break;
            case EvVLCMediaPlayer.MEDIA_INFO_BUFFERING_END:
                EvLog.d("video recv MEDIA_INFO_BUFFERING_END ");
                if (mListener != null)
                    mListener.onBufferingEnd();
                // mState = mStateBeforeBuffering;
                break;
            default:
                break;
        }

        return true;
    }

    // private void switchTrackOfSingleTrack(boolean accompany) {
    // if (mAudioTrackCount == 1) {
    // float leftVolume = 1.0f;
    // float rightVolume = 1.0f;
    //
    // // set to original
    // if (mAccompanyInfo == 0) {
    // leftVolume = 0.0f;
    // } else {
    // rightVolume = 0.0f;
    // }
    //
    // if (accompany) {
    // float temp = leftVolume;
    // leftVolume = rightVolume;
    // rightVolume = temp;
    // }
    //
    // mPlayer.setVolume(leftVolume, rightVolume);
    // }
    // }

    private void switchTrack(int mode) {
        if (mState == KmPlayerState.PlayerState_eInit
                || mState == KmPlayerState.PlayerState_eErrors
                || mState == KmPlayerState.PlayerState_eIdle) {
            EvLog.i("invalid play state, playMode=" + mode);
            return;
        }

        // track info is not yet ready
        if (mState == KmPlayerState.PlayerState_ePreparing) {
            return;
        }

        if (mPlayer == null) {
            return;
        }

        mAudioTrackCount = mPlayer.getAudioTrackCount();
        EvLog.d("mAudioTrackCount = " + mAudioTrackCount);

        if (mAudioTrackCount >= 3) {
            if (mode == KmAudioTrackMode.MODE_ACC) {
                EvLog.i("multi track,伴唱:" + mAccompanyInfo);
                mPlayer.setAudioTrack(mAccompanyInfo);
            } else {
                EvLog.i("multi track,原唱:" + mOriginalInfo);
                mPlayer.setAudioTrack(mOriginalInfo);
            }
        } else {
            //三方歌曲直接左声道是原唱
            if (DeviceConfigManager.getInstance().isUseChannelConfigByDB()) {
                switchChannelOfSingleTrackInThirdApp(mode);
                return;
            }

            int ret = 0;
            if (mode == KmAudioTrackMode.MODE_ACC) {
                if (mAccompanyInfo == 0) {//伴唱在左声道
                    ret = mPlayer.setChannel(EvVLCMediaPlayer.AOUT_VAR_CHAN_LEFT);
                    EvLog.d("single track to acc, select left channel,ret:" + ret);
                } else {//伴唱在右声道
                    EvLog.d("single track to acc, select right channel,ret:" + ret);
                    ret = mPlayer.setChannel(EvVLCMediaPlayer.AOUT_VAR_CHAN_RIGHT);
                }
            } else {
                if (mOriginalInfo == 0) { //原唱在左声道
                    EvLog.d("single track to org, select left channel,ret=" + ret);
                    ret = mPlayer.setChannel(EvVLCMediaPlayer.AOUT_VAR_CHAN_LEFT);
                } else {//原唱在右声道
                    EvLog.d("single track to org, select right channel,ret=" + ret);
                    ret = mPlayer.setChannel(EvVLCMediaPlayer.AOUT_VAR_CHAN_RIGHT);
                }
            }
        }
    }

    private int switchChannelOfSingleTrackInThirdApp(int mode) {
        int ret = 0;
        //三方曲库直接左声道原唱，右声道伴唱
        if (mode == KmAudioTrackMode.MODE_ACC) {
            ret = mPlayer.setChannel(EvVLCMediaPlayer.AOUT_VAR_CHAN_RIGHT);
            EvLog.d("thirdapp, single track to acc, select right channel,ret:" + ret);
        } else {
            ret = mPlayer.setChannel(EvVLCMediaPlayer.AOUT_VAR_CHAN_LEFT);
            EvLog.d("thirdapp,single track to org, select left channel,ret=" + ret);
        }
        return ret;
    }

    @Override
    public int setDisplay(SurfaceHolder holder) {
       /* mHolder = holder;
        mPlayer.setDisplay(mHolder);*/
        return 0;
    }

   /* @Override
    public PlayerCtrlPlayerType_t getPlayerType() {
        return PlayerCtrlPlayerType_t.PlayerCtrlPlayerType_eVideo;
    }*/

    @Override
    public int setAudioTrackInfo(int org, int acc) {
        mAudioOrgTrackIndex = org % 2;
        mAudioAccTrackIndex = acc % 2;

        EvLog.d("org:" + mAudioOrgTrackIndex);
        EvLog.d("acc:" + mAudioAccTrackIndex);

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStoped() {
        if (mListener != null && mIsAutoStop) {
//            mListener.OnComplete();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCompletion() {
        if (mListener != null) {
            mListener.onComplete();
        }

        mState = KmPlayerState.PlayerState_eIdle;
    }

    @Override
    public void onFirstFrame() {
        EvLog.e(">>>>>>>>>>>>>>onFirstFrame");
        if (mListener != null) {
            mListener.onPrepared();
            mListener.onPlay();
        } else {
            EvLog.i("onFirstFrame,mListener=null");
        }
    }

    @Override
    public void OnAudioCreated() {
        EvLog.d("OnAudioCreated");
        if (mPlayer.getAudioTrackCount() >= 3) {
            EvLog.d("AoutCreate, Multi Track, CurrentTrack " + mPlayer.getAudioTrack());
        } else {
            EvLog.d("AoutCreate, Single Track, CurrentChannel + " + mPlayer.getChannel());
        }
        initBaseInfo();
    }

    @Override
    public boolean onError(int errorType) {
        mState = KmPlayerState.PlayerState_eErrors;

        if (mListener != null) {
            //FIXME
            mListener.onError(errorType, errorType);
        }

        return true;
    }

    @Override
    public int getAudioTime() {
//        EvLog.d("KmVLCVideoPlayer" , "not implement getAudioTime fun");
        return (int) mPlayer.getCurrentTime();
    }

    @Override
    public int getPlayerType() {
        return KmVideoPlayerType.VLC;
    }


    @Override
    public void openSoftDecode() {

    }

    @Override
    public void closeSoftDecodeMode() {

    }
}
