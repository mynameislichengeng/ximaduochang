package com.evideo.kmbox.model.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.TrackInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.evideo.kmbox.util.EvLog;

public class KmVideoPlayer implements IKmPlayer, OnInfoListener,
        OnBufferingUpdateListener, OnPreparedListener, OnErrorListener,
        OnCompletionListener {

    public static final String TAG = "mediaplayer";
    
    private String mUrl = null;
    private MediaPlayer mPlayer = null;
    private IKmPlayerEvent mListener = null;
    int mState = KmPlayerState.PlayerState_eIdle;
    private int mSingMode = KmAudioTrackMode.MODE_ORI;
    
    private int mAudioOrgTrackIndex = -1;
    private int mAudioAccTrackIndex = -1;
    // add by qiangv
    private int mAudioTrackCount = 0;
    private int mOriginalInfo = 0;
    private int mAccompanyInfo = 0;
    
    private DefaultVideoRenderView mRenderView;
    private boolean mUnsafeMode;//使用用户创建的SurfaceView来使用该类,如果多个player共用该SurfaceView可能出现卡顿或不能播放视频的bug
    private SurfaceView mSurfaceView;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private final Object mReadyLock = new Object();
    private boolean mIsPrepared=false;
    private boolean mIsSurfaceCreated=false;
    public static boolean gOpenSoftDecode = false;

    
    public KmVideoPlayer(Object renderView) {
        initPlayer();
        mState = KmPlayerState.PlayerState_eIdle;
        
        mRenderView=(DefaultVideoRenderView)renderView;
        mUnsafeMode=false;
        EvLog.i(TAG + "use safe mode");
        if (mRenderView==null){
            EvLog.w(TAG + " render view is null, no video output");
        }
    }

    private void initPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnInfoListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnBufferingUpdateListener(this);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
       /* if (gOpenSoftDecode) {
            mPlayer.setSoftDecodeMode(1);
        } else {
            mPlayer.setSoftDecodeMode(0);
        }*/
    }

    
    static void runOnHandlerSync(final Runnable task, Handler handler){
        final Object lock = new Object();
        if (Looper.myLooper() == handler.getLooper()){
            task.run();
        }
        else{
            handler.post(new Runnable() {
                @Override
                public void run() {
                    task.run();
                    synchronized (lock){
                        lock.notify();
                    }
                }
            });
            synchronized (lock) {
                try {
                    Log.i(TAG, "runOnHandlerSync: begin wait");
                    lock.wait(5000);
                    Log.i(TAG, "runOnHandlerSync: finish wait");
                } catch (InterruptedException e) {
                    Log.e(TAG, "runOnHandlerSync: wait being interrupted:" + e.getMessage());
                }
            }
        }
    }

    @Override
    public int destroy() {
        
        synchronized (mReadyLock){
           /* mEvPlayer.stop();
            mEvPlayer.setSurface(null);
            mStarted =false;
            mEvPlayer.release();
            mEvPlayer = null;
            mIsPrepared=false;
            mIsSurfaceCreated=false;*/
            if (mPlayer != null) {
                mPlayer.setOnCompletionListener(null);
                mPlayer.setOnPreparedListener(null);
                mPlayer.setOnInfoListener(null);
                mPlayer.setOnErrorListener(null);
                mPlayer.setOnBufferingUpdateListener(null);
                mPlayer.setSurface(null);
                mPlayer.reset();
                mPlayer.release();
                mPlayer = null;
            }
            mIsPrepared=false;
            mIsSurfaceCreated=false;
        }
        
        if (!mUnsafeMode && mRenderView!=null && mSurfaceView!=null) {
            runOnHandlerSync(new Runnable() {
                @Override
                public void run() {
                    mRenderView.removeView(mSurfaceView);
                    mSurfaceView = null;
                    Log.d(TAG, "mSurfaceVIew removed");
                }
            }, mMainHandler);
        }
        mAudioOrgTrackIndex = -1;
        mAudioAccTrackIndex = -1;
        // add by qiangv
        mAudioTrackCount = 0;
        mOriginalInfo = 0;
        mAccompanyInfo = 0;
        
        return 0;
    }

    @Override
    public int setDisplay(SurfaceHolder holder) {
      /*  mHolder = holder;
        if (mPlayer != null) {
            mPlayer.setDisplay(mHolder);
        }*/
        return 0;
    }

    @Override
    public int setSource(String url) {
        if (mState == KmPlayerState.PlayerState_eIdle
                || mState == KmPlayerState.PlayerState_eStoped
                || mState == KmPlayerState.PlayerState_eErrors
                || mState == KmPlayerState.PlayerState_eInit) {
            mUrl = url;

            if (mPlayer == null) {
                initPlayer();
            }
            // resetPlayStatus();

            boolean setRet = false;
            try {
                mPlayer.setDataSource(mUrl);
                initDisplay();
                /*EvLog.i("mHolder:" + mHolder);
                if (mHolder != null) {
                    mPlayer.setDisplay(mHolder);
                }*/
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
                // } catch (IOException e) {
                // e.printStackTrace();
                // setRet = false;
            } catch (IOException e) {
                e.printStackTrace();
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
    public boolean setAudioSingMode(int mode) {
        if (mState == KmPlayerState.PlayerState_ePlay
                || mState == KmPlayerState.PlayerState_ePause
                || mState == KmPlayerState.PlayerState_eBuffering) {
            EvLog.d(mState + ", setAudioSingMode mode " + mode);
//            mPlayer.switchAudioTrack(/*player, */trackNo)
//            int track = (mode == KmAudioTrackMode.MODE_ORI) ? (mAudioOrgTrackIndex) : (mAudioAccTrackIndex);
            if (mPlayer != null) {
//                mPlayer.switchAudioTrack(track);
                switchTrack(mode);
            }
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
    public String getSource() {
        return mUrl;
    }

    @Override
    public int setListener(IKmPlayerEvent eventHandles) {
        mListener = eventHandles;
        return 0;
    }

    private FrameLayout.LayoutParams mSurfaceViewParam = null;
    private void initDisplay() {
        if (mUnsafeMode){
            if (mSurfaceView!=null) {
                mPlayer.setSurface(mSurfaceView.getHolder().getSurface());
            }
        }
        else {
            if (mRenderView!=null) {
                runOnHandlerSync(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "run: add surface view to render view");
                        mSurfaceView = new SurfaceView(mRenderView.getContext());
                        if (mSurfaceViewParam == null) {
                            mSurfaceViewParam = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                        }
                        mSurfaceView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("gsp", "onClick:我被点击了 哈哈哈哈哈哈哈哈哈哈 ");
                            }
                        });
                        mRenderView.addView(mSurfaceView, 0, mSurfaceViewParam);
//                        mRenderView.addView(mSurfaceView);
                        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                            @Override
                            public void surfaceCreated(SurfaceHolder holder) {
                                EvLog.d(TAG,"surfaceCreated Width="+ mSurfaceView.getWidth()+"|Height="+mSurfaceView.getHeight());
                                
                                boolean hasStart = false;
                                synchronized (mReadyLock) {
                                    if (mPlayer != null)
                                        mPlayer.setSurface(holder.getSurface());
                                    mIsSurfaceCreated = true;
                                    hasStart = startEvPlayer();
                                }
                                if (hasStart) {
                                    if (mListener != null) {
                                        mListener.onPrepared();
//                                        mListener.onPlay();
                                    }
                                }
//                                mSurfaceView.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                                EvLog.i("surfaceChanged width:" + width + ",height:" + height);
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
    

    @Override
    public int play() {
        if (mPlayer == null) {
            EvLog.e(TAG + " mPlayer is null");
            return -1;
        }
        
        EvLog.e(TAG + " player play:" + mState);
        if (mState == KmPlayerState.PlayerState_eInit) {
            try {
                EvLog.i(TAG + " player prepareAsync");
                mPlayer.prepareAsync();
                mState = KmPlayerState.PlayerState_ePreparing;
            } catch (IllegalStateException e) {
                // FIXME upload error message
                e.printStackTrace();
                mState = KmPlayerState.PlayerState_eIdle;
                return -1;
            }
        } else {
            EvLog.i(TAG + " player play");
            mPlayer.start();
            EvLog.d("-->>set playstate");
            mState = KmPlayerState.PlayerState_ePlay;
        }
        return 0;
    }

    @Override
    public int pause() {
        if (mPlayer == null) {
            EvLog.e(TAG + " mPlayer is null");
            return -1;
        }
        
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
        if (mPlayer == null) {
            EvLog.e(TAG + " mPlayer is null");
            return -1;
        }
        
        destroy();
        mState = KmPlayerState.PlayerState_eInit;
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mPlayer == null) {
            EvLog.e(TAG + " mPlayer is null");
            return -1;
        }
        
        return (int) mPlayer.getCurrentPosition();
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public int getTotalTime() {
        if (mPlayer == null) {
            EvLog.e(TAG + " mPlayer is null");
            return -1;
        }
        
        return (int) mPlayer.getDuration();
    }

    @Override
    public int seekToTime(long time) {
       /* if (mPlayer == null) {
            EvLog.e(TAG + " mPlayer is null");
            return -1;
        }
        mPlayer.seekTo(time);*/
        return 0;
    }

    @Override
    public int setVolume(float vol) {
        if (mPlayer == null) {
            EvLog.e(TAG + " mPlayer is null");
            return -1;
        }
        
        mPlayer.setVolume(vol, vol);
        return 0;
    }

    @Override
    public float getVolume() {
        EvLog.e(TAG + " not implement getVolume");
        return 0;
    }

    @Override
    public void onCompletion(MediaPlayer var1) {
        if (mListener != null) {
            mListener.onComplete();
        }
        mState = KmPlayerState.PlayerState_eIdle;
    }

    @Override
    public boolean onError(MediaPlayer var1, int what, int extra) {
        mState = KmPlayerState.PlayerState_eErrors;
        if (mListener != null) {
            mListener.onError(what, extra);
        }
        return false;
    }

    private boolean startEvPlayer(){
        synchronized (mReadyLock){
            if (mUnsafeMode || (mIsSurfaceCreated && mIsPrepared)){
                EvLog.i("startEvPlayer------------------");
                mPlayer.start();
                mState = KmPlayerState.PlayerState_ePlay;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void onPrepared(MediaPlayer var1) {
        EvLog.d(TAG + " onprepared");
        
        boolean hasStart = false;
        synchronized (mReadyLock){
            mIsPrepared=true;
            hasStart = startEvPlayer();
        }
        
        if (hasStart) {
            if (mListener != null) {
                mListener.onPrepared();
//                mListener.onPlay();
            }
        }
        
        if ( mState == KmPlayerState.PlayerState_ePreparing ) {
            mState = KmPlayerState.PlayerState_ePrepared;
        } else {
            EvLog.d( "onPrepared not start at " + mState );
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer var1, int percent) {
        if (mListener != null) {
            mListener.onBufferingUpdate(percent);
        }
    }

    @Override
    public boolean onInfo(MediaPlayer var1, int what, int extra) {
        EvLog.i(TAG + " onInfo " + what + "," + extra);
        switch (what) {
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                /*if (mSurfaceView != null && mSurfaceView.getVisibility() != View.VISIBLE) {
                    EvLog.i("recv MEDIA_INFO_VIDEO_RENDERING_START ,mSurfaceView is gone");
                    mSurfaceView.setVisibility(View.GONE);
                    mSurfaceView.setVisibility(View.VISIBLE);
                } else*/ 
//                mSurfaceView.setVisibility(View.VISIBLE);
                {
                    EvLog.i("recv MEDIA_INFO_VIDEO_RENDERING_START ,mSurfaceView is visible");
                }
                
                if ( mState == KmPlayerState.PlayerState_ePlay ) {
                    if ( mListener != null ) {
                        mListener.onPlay();
                    }
                }
                else {
                    EvLog.e( "onInfo state " +mState  );
                }
                break;
            }
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                EvLog.d("video recv MEDIA_INFO_BUFFERING_START ");
//                mStateBeforeBuffering = mState;
//                mState = PlayerState.PlayerState_eBuffering;
                if ( mListener != null )
                    mListener.onBufferingStart();
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                EvLog.d("video recv MEDIA_INFO_BUFFERING_END ");
                if ( mListener != null )
                    mListener.onBufferingEnd();
//                mState = mStateBeforeBuffering;
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public int getAudioTime() {
        return getCurrentPosition();
    }

    @Override
    public int getPlayerType() {
        return KmVideoPlayerType.MEDIAPLAYER;
    }

    @Override
    public void openSoftDecode() {
       /* if (mPlayer != null) {
            mPlayer.setSoftDecodeMode(1);
        }*/
    }

    @Override
    public void closeSoftDecodeMode() {
       /* if (mPlayer != null) {
            mPlayer.setSoftDecodeMode(0);
        }*/
    }
    
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
        if (mAudioTrackCount == 0) {
            initTrackInfo();
        }

        EvLog.d("switchTrack: mode " + mode + ", mAccompanyInfo = "
                + mAccompanyInfo);
        /*AudioManager audioManager = (AudioManager) BaseApplication
                .getInstance().getSystemService(Service.AUDIO_SERVICE);*/

        if (mAudioTrackCount == 1) {// 单音轨

            if (mode == KmAudioTrackMode.MODE_ACC) {
                if (mAccompanyInfo == 0) {// 伴唱在左声道
                    EvLog.d("single track to acc, select left channel");
                    // audioManager.setDtvOutputMode(AudioManager.EnumDtvSoundMode.E_LEFT);
                } else {// 伴唱在右声道
                    EvLog.d("single track to acc, select right channel");
                    // audioManager.setDtvOutputMode(AudioManager.EnumDtvSoundMode.E_RIGHT);
                }
            } else if (mode == KmAudioTrackMode.MODE_ORI) {
                if (mOriginalInfo == 0) {// 原唱在左声道
                    EvLog.d("single track to org, select left channel");
                    // audioManager.setDtvOutputMode(AudioManager.EnumDtvSoundMode.E_LEFT);
                } else {// 原唱在右声道
                    EvLog.d("single track to org, select right channel");
                    // audioManager.setDtvOutputMode(AudioManager.EnumDtvSoundMode.E_RIGHT);
                }
            }
        } else if (mAudioTrackCount >= 2) {
            if (mode == KmAudioTrackMode.MODE_ACC) {
                EvLog.d("KmVideoPlayer", mAudioTrackCount + " to acc index: "
                        + mAccompanyInfo);
                try {
                    mPlayer.selectTrack(mAccompanyInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mListener != null) {
                        mListener.onError(KmPlayerError.ERR_SELECT_TRACK, 0);
                    }
                }
            } else {
                EvLog.d("KmVideoPlayer", mAudioTrackCount + " to org index: "
                        + mOriginalInfo);
                try {
                    mPlayer.selectTrack(mOriginalInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mListener != null) {
                        mListener.onError(KmPlayerError.ERR_SELECT_TRACK, 0);
                    }
                }
            }
            // audioManager.setDtvOutputMode(AudioManager.EnumDtvSoundMode.E_STEREO);
        } else {
            // error
            EvLog.e("KmVideoPlayer", " error trackNum" + mAudioTrackCount);
        }
    }

    private void initTrackInfo() {
        /*
         * if (mState != PlayerState.PlayerState_ePrepared) { return; }
         */

        mAudioTrackCount = 0;
        TrackInfo[] trackInfoArray = null;
        
        try {
            trackInfoArray = mPlayer.getTrackInfo();
        } catch (Exception e) {
            e.printStackTrace();
            if (mListener != null) {
                mListener.onError(KmPlayerError.ERR_INIT_TRACK_INFO, 0);
            }
        }
        
        if (trackInfoArray == null) {
            return;
        }

        List<Integer> tracks = new ArrayList<Integer>();

        for (int i = 0; i < trackInfoArray.length; i++) {
            final TrackInfo info = trackInfoArray[i];

            if (info.getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                tracks.add(i);
            }
        }

        mAudioTrackCount = tracks.size();

        if (mAudioTrackCount == 0) {
            // error
            return;
        } else if (mAudioTrackCount == 1) {
            mOriginalInfo = mAudioOrgTrackIndex;
            mAccompanyInfo = mAudioAccTrackIndex;
        } else if (mAudioTrackCount >= 2) {
            if (mAudioAccTrackIndex >= mAudioTrackCount) {
                // error
                EvLog.e("invalid audio acc track index:" + mAudioAccTrackIndex);
                mAudioAccTrackIndex = mAudioTrackCount - 1;
            }

            if (mAudioOrgTrackIndex >= mAudioTrackCount) {
                // error
                EvLog.e("invalid audio org track index:" + mAudioOrgTrackIndex);
                mAudioOrgTrackIndex = mAudioTrackCount - 1;
            }

            mOriginalInfo = tracks.get(mAudioOrgTrackIndex);
            mAccompanyInfo = tracks.get(mAudioAccTrackIndex);
            EvLog.d("initTrackInfo trackInfoArray.size = "
                    + trackInfoArray.length + ",mAudioTrackCount="
                    + mAudioTrackCount + ", mAudioAccTrackIndex= "
                    + mAudioAccTrackIndex + ",mAudioOrgTrackIndex = "
                    + mAudioOrgTrackIndex + ",mOriginalInfo=" + mOriginalInfo
                    + ",mAccompanyInfo=" + mAccompanyInfo);
        }
    }
}
