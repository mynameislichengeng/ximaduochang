/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  Aug 14, 2015     "dinghaiqiang"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.player;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.MediaPlayer.Event;
import org.videolan.libvlc.MediaPlayer.TrackDescription;
import org.videolan.libvlc.media.VlcPlayer;
import org.videolan.libvlc.util.AndroidUtil;
import org.videolan.libvlc.util.VLCInstance;

import android.media.TimedText;
import android.os.SystemClock;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.evideo.kmbox.util.EvLog;

/**
 * [基于vlc的MediaPlayer] //目前处理是EvMediaPlayer层外再包一层KmVideoPlayer
 */
public class EvVLCMediaPlayer implements MediaPlayer.EventListener {
    public static final int MEDIA_ERROR_UNKNOWN = 1;
    public static final int MEDIA_ERROR_SERVER_DIED = 100;
    public static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;
    public static final int MEDIA_ERROR_IO = -1004;
    public static final int MEDIA_ERROR_MALFORMED = -1007;
    public static final int MEDIA_ERROR_UNSUPPORTED = -1010;
    public static final int MEDIA_ERROR_TIMED_OUT = -110;

    public static final int MEDIA_INFO_UNKNOWN = 1;
    public static final int MEDIA_INFO_STARTED_AS_NEXT = 2;
    public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3;
    public static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;
    public static final int MEDIA_INFO_BUFFERING_START = 701;
    public static final int MEDIA_INFO_BUFFERING_END = 702;
    public static final int MEDIA_INFO_BAD_INTERLEAVING = 800;
    public static final int MEDIA_INFO_NOT_SEEKABLE = 801;
    public static final int MEDIA_INFO_METADATA_UPDATE = 802;
    public static final int MEDIA_INFO_EXTERNAL_METADATA_UPDATE = 803;
    public static final int MEDIA_INFO_TIMED_TEXT_ERROR = 900;
    public static final int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901;
    public static final int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902;

    public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT = 1;
    public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING = 2;

    /**
     * MediaPlayer error types
     */
    public static final int ERROR_NOTHING_SPECIAL = 0;
    public static final int ERROR_INPUT_THREAD_CREATE_FAILED = 1;
    public static final int ERROR_INPUT_STREAM_OUTPUT_START_FAILED = 2;
    public static final int ERROR_INPUT_RESUME_FAILED = 3;
    public static final int ERROR_INPUT_OPENMRL_FAILED = 4;
    public static final int ERROR_FORMAT_UNRECOGNIZE = 5;

    /**
     * Audio channel
     */
    public static final int AOUT_VAR_CHAN_UNSET = 0 ;/* must be zero */
    public static final int AOUT_VAR_CHAN_STEREO = 1;
    public static final int AOUT_VAR_CHAN_RSTEREO = 2;
    public static final int AOUT_VAR_CHAN_LEFT = 3;
    public static final int AOUT_VAR_CHAN_RIGHT = 4;
    public static final int AOUT_VAR_CHAN_DOLBYS = 5;

    /**
     * 播放器状态
     */
    public static final int LIBVLC_NOTHINGSEPCIAL = 0;
    public static final int LIBVLC_OPENING = 1;
    public static final int LIBVLC_BUFFERING = 2;
    public static final int LIBVLC_PLAYING = 3;
    public static final int LIBVLC_PAUSED = 4;
    public static final int LIBVLC_STOPPED = 5;
    public static final int LIBVLC_ENDED = 6;
    public static final int LIBVLC_ERROR = 7;

    private Media mCurrentMedia = null;
    private final LibVLC mLibVLC;
    private MediaPlayer mMediaPlayer;
    private int mAoutCount = 0;

    private int mFileCache = 1500;
    private int mNetWorkCache = 1500;
/*    private boolean mHwEnable = true;
    private boolean mHwForce = false;*/
    private long mTimeOffset = 0;

    public static final String TAG = "EvMediaPlayer";

    public EvVLCMediaPlayer(boolean enableSoftDecode) {
//        mLibVLC = new LibVLC(VLCOptions.getLibOptions());
        mLibVLC = VLCInstance.get();
        mMediaPlayer = new MediaPlayer(mLibVLC);
        mMediaPlayer.setEventListener(this);
        
        if (enableSoftDecode) {
            EvLog.e("vlc enable software decode");
            Media.setHwOption(false, false);
        } 
    }

    /**
     * [功能说明]
     *
     * @param path Android MediaPlayer style path.
     */
    public void setDataSource(String path) {
        release();
        if (path.startsWith("http://") || path.startsWith("rstp://") || path.startsWith("rtp://")
                || path.startsWith("udp://")) {
            mCurrentMedia = new Media(mLibVLC, AndroidUtil.LocationToUri(path));
        } else {
            mCurrentMedia = new Media(mLibVLC, path);
        }
        mMediaPlayer = new MediaPlayer(mLibVLC);
        mMediaPlayer.setEventListener(this);
        mMediaPlayer.setMedia(mCurrentMedia);
    }


    public void prepareAsync() {
        if (mMediaPlayer == null) {
            return;
        }
        mCurrentMedia.addOption(":video-paused");
        mMediaPlayer.play();
    }

    /**
     * add attachViews(); [功能说明]
     *
     * @param sh
     */
    public void setDisplay(SurfaceHolder sh) {
        if (sh == null) {
            return;
        }
        if (mMediaPlayer == null) {
            return;
        }
        if (!mMediaPlayer.getVLCVout().areViewsAttached()) {
            mMediaPlayer.getVLCVout().setVideoSurface(sh.getSurface(), sh);
            mMediaPlayer.getVLCVout().attachViews();
        }
    }

    public void setSurface(Surface surface) {
        mMediaPlayer.getVLCVout().setVideoSurface(surface, null);
    }

    public void start() throws IllegalStateException {
        if (mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.play();
    }

    public void stop() throws IllegalStateException {
        if (mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.stop();
    }

    public void pause() {
        // FIXME, this is toggling for now.
        if (mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.pause();
    }

    public boolean isPlaying() {
        if (mMediaPlayer == null) {
            return false;
        }
        return mMediaPlayer.isPlaying();
    }

    // This is of course, less precise than VLC
    public long getCurrentTime() {
        long time = mMediaPlayer.getTime();
        time = time - mTimeOffset;
        if (time < 0) {
            return 0;
        } else {
            return time;
        }
    }

    // This is of course, less precise than VLC
    public int getDuration() {
        if (mMediaPlayer == null) {
            return 0;
        }
        return (int) mMediaPlayer.getLength();
    }

    public void setNextMediaPlayer(EvVLCMediaPlayer next) {
    }

    public void release() {
        if (mMediaPlayer == null) {
            return;
        }
        if (!mMediaPlayer.isReleased()) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mCurrentMedia != null && (!mCurrentMedia.isReleased())) {
            mCurrentMedia.release();
            mCurrentMedia = null;
        }

    }

    /**
     * [重置]
     */
    public void reset(SurfaceHolder sh) {
        if (sh == null) {
            EvLog.e(TAG, "surfaceHolder is null");
        }
        if (mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.release();
        mMediaPlayer = new MediaPlayer(mLibVLC);
        mMediaPlayer.setEventListener(this);
        mMediaPlayer.setMedia(mCurrentMedia);
        setDisplay(sh);
    }

    public void setVolume(int volume) {
//        if (mMediaPlayer == null) {
//            return;
//        }
//        if (volume < 0) {
//            mMediaPlayer.setVolume(0);
//        } else if ( volume > 100) {
//            mMediaPlayer.setVolume(100);
//        } else {
//            mMediaPlayer.setVolume(volume);
//        }
    }
    public void setVolume(float leftVolume, float rightVolume) {
        EvLog.i("set volume:"+leftVolume);
        if (mMediaPlayer == null) {
            return;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume((int) ((leftVolume + rightVolume) * 100 / 2));
        }

    }

    /**
     * Clip volume into min-max range
     * @param volume
     * @param min
     * @param max
     * @return
     */
    private int clipVolume(int volume, int min, int max) {
        if (min > max) {
            return -1;
        } else if (volume < min) {
            return min;
        } else if (volume > max) {
            return max;
        } else {
            return volume;
        }

    }

    private CrossFadeThread mVolumeFadein = null ;
    private int mCrossFadeThreadCnt = 0;

    /**
     * 音量渐增
     * @param start range:0--100
     * @param end
     */
    public void volumeFadeIn(int start, int end) {
        if (mCrossFadeThreadCnt == 0) {
            mVolumeFadein = new CrossFadeThread(start, end);
            mCrossFadeThreadCnt++;
            mVolumeFadein.start();
        } else {
            //For some reason, there is already a thread running
            EvLog.w(TAG, "CrossFadeThread already running");
        }
    }


    public static int VOLUME_MAX = 100;
    public static int VOLUME_MIN = 0;
    public static int VOLUME_FADE_STEP = 5;

    /**
     * 音量渐变处理线程，目前只有渐增
     */
    private class CrossFadeThread extends Thread {
        int mStart = 0;
        int mEnd = 0;
        public CrossFadeThread(int start, int end) {
            super();
            mStart = clipVolume(start, VOLUME_MIN, VOLUME_MIN);
            mEnd = clipVolume(end, VOLUME_MIN, VOLUME_MAX);
        }

        public void setStart(int start) {
            mStart = clipVolume(start, VOLUME_MIN, VOLUME_MIN);
        }

        public void setEnd(int end) {
            mEnd = clipVolume(end, VOLUME_MIN, VOLUME_MAX);
        }

        @Override
        public void run() {
            int volume = mStart;
            for(;;) {
                if (mMediaPlayer == null) {
                    break;
                }
                EvLog.d(TAG, "LibVLC state is " + mMediaPlayer.getPlayerState());
                if (mMediaPlayer.getPlayerState() == LIBVLC_PLAYING) {
                    volume += VOLUME_FADE_STEP;
                    EvLog.d(TAG, "volumeFadeIn, volume = " + volume);
                    if (volume >= mEnd) {
                        mMediaPlayer.setVolume(mEnd);
                        break;
                    } else {
                        mMediaPlayer.setVolume(volume);
                    }
                } else {
                    //TODO handle other states
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mCrossFadeThreadCnt--;
        }
    }

    public int getPlayerState() {
            if (mMediaPlayer == null) {
            return -1;
        }
        return mMediaPlayer.getPlayerState();
    }

    public void setAudioSessionId(int sessionId)
            throws IllegalArgumentException, IllegalStateException {
    }

    public int getAudioSessionId() {
        return 0;
    }

    public void attachAuxEffect(int effectId) {
    }

    private boolean mOpeningFlag = false;
    private boolean mBufferingStartFlag = false;

    @Override
    public void onEvent(Event event) {
        if (event == null) {
            return;
        }
        switch (event.type) {
            
            case Event.Stopped:
                if (mOnStopedListener != null) {
                    mOnStopedListener.onStoped();
                }
                break;
            case Event.EndReached:
                if (mOnCompletionListener != null)
                    mOnCompletionListener.onCompletion();
                break;
            case Event.EncounteredError:
                EvLog.d(TAG, "EncounteredError");
                if (mOnErrorListener != null)
                    mOnErrorListener.onError((int)event.getArg1());
                break;
            case Event.Opening:
                mOpeningFlag = true;
                mAoutCount = 0;
                break;
            case Event.Buffering:
                // Don't trigger this callback when receive a 0% percent buffering when buffering hasn't started yet
                // input_SendEventCache( p_input, 0.0 ) is called at input.c and es_out.c
                if (!mBufferingStartFlag) {
                    break;
                }
                if (mOnBufferingUpdateListener != null) {
                    mOnBufferingUpdateListener.onBufferingUpdate( (int)event.getArg1());
                }
                break;
            case Event.Playing:
                if(mSavedTime != 0l) {
                    long length = mMediaPlayer.getLength();
                    EvLog.i("recv Event.playing ,setTime: " + mSavedTime + ",length=" + length + ",mTimeOffset=" + mTimeOffset);
//                    mMediaPlayer.setTime(mSavedTime);
//                    mMediaPlayer.setPosition(0.05f);
                }
                mSavedTime = 0l;
                break;
            case Event.Paused:
                if (mOnPauseListener != null)
                    mOnPauseListener.onPause();
                break;
            case Event.TimeChanged:
                break;
            case Event.PositionChanged:
                break;
            case Event.Vout:
                break;
            case Event.ESAdded:
                break;
            case Event.ESDeleted:
                break;
            case Event.MediaChanged:
                break;
            case Event.NothingSpecial:
                break;
            case Event.Rendering:
                EvLog.d(TAG, "EvMediaPlayer onFirstFrame ");
                // 这个时候获取的vlc的实际时间大概是300多毫秒，正式开始播放将时间重置
//                mTimeOffset = mMediaPlayer.getTime();
                EvLog.d(TAG, "EvMeidaPlayer onFirstFrame, time offset is " + mTimeOffset);
                if (mOnFirstFrameListener != null) {
                    mOnFirstFrameListener.onFirstFrame();
                }
                break;
            case Event.AoutCreate:
                EvLog.d(TAG, "EvMediaPlayer onAudioCreated");
                mAoutCount++;
                if (mAoutCount >= (mMediaPlayer.getAudioTracksCount()-1)) {
                    if (mOnAudioCreateListener != null) {
                        mOnAudioCreateListener.OnAudioCreated();
                    }
                }
                break;
            case Event.BufferingStarted:
                EvLog.d(TAG, "EvMediaPlayer onBufferingStart");
                mBufferingStartFlag = true;
                if (mOnBufferingUpdateListener != null) {
                    mOnBufferingUpdateListener.onBufferingStart();
                }
                break;
            case Event.BufferingEnded:
                EvLog.d(TAG, "EvMediaPlayer onBufferingEnded");
                mBufferingStartFlag = false;
                if (mOnBufferingUpdateListener != null) {
                    mOnBufferingUpdateListener.onBufferingEnd();
                }
                if (mOpeningFlag) {
                    mOpeningFlag = false;
                    if(mOnPreparedListener != null)
                        mOnPreparedListener.onPrepared();
                }
                break;
        }
    }

//    static public class TrackInfo implements Parcelable {
//
//        public static final int MEDIA_TRACK_TYPE_UNKNOWN = 0;
//        public static final int MEDIA_TRACK_TYPE_VIDEO = 1;
//        public static final int MEDIA_TRACK_TYPE_AUDIO = 2;
//        public static final int MEDIA_TRACK_TYPE_TIMEDTEXT = 3;
//        public static final int MEDIA_TRACK_TYPE_SUBTITLE = 4;
//
//        TrackInfo(Parcel in) {
//        }
//
//        public int getTrackType() {
//            return MEDIA_TRACK_TYPE_UNKNOWN;
//        }
//
//        public String getLanguage() {
//            return "und";
//        }
//
//        public MediaFormat getFormat() {
//            return null;
//        }
//
//        @Override
//        public int describeContents() {
//            return 0;
//        }
//
//        @Override
//        public void writeToParcel(Parcel dest, int flags) {
//        }
//
//        @Override
//        public String toString() {
//            return "";
//        }
//    }
//
//    public TrackInfo[] getTrackInfo() throws IllegalStateException {
//        // FIXME
//        TrackInfo trackInfo[] = new TrackInfo[1];
//        return trackInfo;
//    }



    /**
     * @return 单音轨歌曲获取channel
     */
    public int getChannel() {
        if (mMediaPlayer == null) {
            EvLog.e("sMediaPlayer NULL");
            return 0;
        }
        EvLog.d("channel:" + mMediaPlayer.nativeGetAudioChannel());
        return mMediaPlayer.nativeGetAudioChannel();
    }

    public int setChannel(int channel) {
        if (mMediaPlayer == null) {
            EvLog.e("sMediaPlayer NULL");
            return 0;
        }
        mMediaPlayer.nativeSetAudioChannel(channel);
        return 1;
    }

    @Override
    protected void finalize() {
    }

    public TrackDescription[] getAudioTracks() throws IllegalStateException {
        if (mMediaPlayer == null) {
            return null;
        }
        return mMediaPlayer.getAudioTracks();
    }

    public int getAudioTrackCount() throws IllegalStateException {
        if (mMediaPlayer == null) {
            return 0;
        }
        return mMediaPlayer.getAudioTracksCount();
    }

    /**
     * Get the current audio track.
     *
     * @return the audio track ID or -1 if no active input
     */
    public int getAudioTrack() throws IllegalStateException {
        if (mMediaPlayer == null) {
            return 0;
        }
        return mMediaPlayer.getAudioTrack();
    }

    public boolean setAudioTrack(int index) throws IllegalStateException {
        if (mMediaPlayer == null) {
            return false;
        }
        return mMediaPlayer.setAudioTrack(index);
    }

    private int mFirstAudioTrackIndex;
    private int mSecondAudioTrackIndex;
    private int mCurrentAudioTrackIndex;

    /**
     * [初始化音轨信息]
     */
    public boolean initAudioTrackInfo() {
        if (mMediaPlayer == null) {
            return false;
        }

        TrackDescription[] audioTracks = mMediaPlayer.getAudioTracks();
        if (audioTracks == null) {
            //若是取出的音轨信息为null则再尝试3次
            for (int i = 0; i < 3; i++) {
                SystemClock.sleep(100);
                audioTracks = mMediaPlayer.getAudioTracks();
                if (audioTracks != null) {
                    break;
                }
            }
        }
        //尝试3次后仍为null则直接返回
        if (audioTracks == null) {
            EvLog.e("audioTracks null");
            return false;
        }
        EvLog.d("SIZE:" + audioTracks.length);
        for (int i = 0; i < audioTracks.length; i++) {
            EvLog.d(i + ":" + audioTracks[i].id);
        }
        if (audioTracks.length >= 3) {
            mFirstAudioTrackIndex = audioTracks[1].id;
            mSecondAudioTrackIndex = audioTracks[2].id;
        }

        mCurrentAudioTrackIndex = mMediaPlayer.getAudioTrack();

        return true;
    }

    /**
     * [音轨1]
     *
     * @return 音轨1序号
     */
    public int getFirstAudioTrackIndex() {
        return mFirstAudioTrackIndex;
    }

    /**
     * [音轨2]
     *
     * @return 音轨2序号
     */
    public int getSecondAudioTrackIndex() {
        return mSecondAudioTrackIndex;
    }

    public int getCurrentAudioTrackIndex() {
        return mCurrentAudioTrackIndex;
    }

    public boolean switchAudioTrack() {
        if (mMediaPlayer == null) {
            return false;
        }
        mCurrentAudioTrackIndex = (mFirstAudioTrackIndex != mCurrentAudioTrackIndex) ? mFirstAudioTrackIndex
                : mSecondAudioTrackIndex;
        if (!mMediaPlayer.setAudioTrack(mCurrentAudioTrackIndex)) {
            return false;
        }
        return true;
    }

    public void setFileCache(int fileCache) {
        mFileCache = fileCache;
        Media.setCaching(mFileCache, mNetWorkCache);
    }

    public void setNetworkCache(int networkCache) {
        mNetWorkCache = networkCache;
        Media.setCaching(mFileCache, mNetWorkCache);
    }

    /**
     * Callback Listeners
     */
    public interface OnPreparedListener
    {
        void onPrepared();
    }

    public void setOnPreparedListener(OnPreparedListener listener) {
        mOnPreparedListener = listener;
    }

    private OnPreparedListener mOnPreparedListener = null;

    public interface OnFirstFrameListener {
        void onFirstFrame();
    }

    public void setOnFirstFrameListener(OnFirstFrameListener listener) {
        mOnFirstFrameListener = listener;
    }

    private OnFirstFrameListener mOnFirstFrameListener = null;

    public interface OnStopedListener {
        void onStoped();
    }

    public void setOnStopedListener(OnStopedListener listener) {
        mOnStopedListener = listener;
    }

    private OnStopedListener mOnStopedListener = null;

    public interface OnCompletionListener
    {
        void onCompletion();
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }
    private OnCompletionListener mOnCompletionListener;

    public interface OnBufferingUpdateListener
    {
        void onBufferingUpdate(int percent);
        void onBufferingStart();
        void onBufferingEnd();
    }

    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
        mOnBufferingUpdateListener = listener;
    }
    private OnBufferingUpdateListener mOnBufferingUpdateListener;

    public interface OnSeekCompleteListener
    {
        public void onSeekComplete(VlcPlayer mp);
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        mOnSeekCompleteListener = listener;
    }
    private OnSeekCompleteListener mOnSeekCompleteListener;


    public interface OnVideoSizeChangedListener
    {
        public void onVideoSizeChanged(VlcPlayer mp, int width, int height);
    }

    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) {
        mOnVideoSizeChangedListener = listener;
    }
    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;

    public interface OnTimedTextListener
    {
        public void onTimedText(VlcPlayer mp, TimedText text);
    }

    public void setOnTimedTextListener(OnTimedTextListener listener) {
        mOnTimedTextListener = listener;
    }
    private OnTimedTextListener mOnTimedTextListener;

    public interface OnErrorListener
    {
        /**
         * [功能说明] onError事件回调
         * @param errorType @see
         * {@link VlcPlayer#ERROR_NOTHING_SPECIAL}
         * {@link VlcPlayer#ERROR_NOTHING_SPECIAL}
         * {@link VlcPlayer#ERROR_INPUT_THREAD_CREATE_FAILED}
         * {@link VlcPlayer#ERROR_INPUT_STREAM_OUTPUT_START_FAILED}
         * {@link VlcPlayer#ERROR_INPUT_RESUME_FAILED}
         * {@link VlcPlayer#ERROR_INPUT_OPENMRL_FAILED}
         * {@link VlcPlayer#ERROR_FORMAT_UNRECOGNIZE}
         * @return
         */
        boolean onError(int errorType);
    }

    public void setOnErrorListener(OnErrorListener listener) {
        mOnErrorListener = listener;
    }
    private OnErrorListener mOnErrorListener = null;

    public interface OnInfoListener
    {
        boolean onInfo(VlcPlayer mp, int what, int extra);
    }

//    public void setOnInfoListener(OnInfoListener listener) {
//        mOnInfoListener = listener;
//    }
    private OnInfoListener mOnInfoListener;

    public interface OnRenderingListener
    {
        boolean onRendering();
    }

    public void setOnRenderingListener(OnRenderingListener listener) {
        mOnRenderingListener = listener;
    }
    private OnRenderingListener mOnRenderingListener;

    public interface OnPauseListener
    {
        void onPause();
    }
    public void setOnPauseListener(OnPauseListener listener)
    {
        mOnPauseListener = listener;
    }
    private OnPauseListener mOnPauseListener;

    public interface OnDestroyListener
    {
        void onDestroy();
    }
    public void setOnDestroyListener(OnDestroyListener listener)
    {
        mOnDestroyListener = listener;
    }
    private OnDestroyListener mOnDestroyListener;

    public interface OnAudioCreatedListener
    {
        void OnAudioCreated();
    }
    public void setOnAudioCreatedListener(OnAudioCreatedListener listener)
    {
        mOnAudioCreateListener = listener;
    }
    private OnAudioCreatedListener mOnAudioCreateListener;
    
    /* 跳转到某一位置开始播放
    * @param pos -- 跳转的位置占总时长的百分比
    * 在总时长无法获取时可以用此函数进行seek
    */
   public void setPosition(float pos) {
       if (mMediaPlayer == null) {
           return;
       }
       mMediaPlayer.setPosition(pos);
   }
   
   private long mSavedTime = 0l;
   /**
    * 跳转到某一位置开始播放
    * @param time -- 单位ms, 跳转的具体时间戳
    */
   public void setTime(long time) {
       if (mMediaPlayer != null) {
           int state = mMediaPlayer.getPlayerState();
           if (state == LIBVLC_NOTHINGSEPCIAL
              || state == LIBVLC_STOPPED
              || state == LIBVLC_ENDED
              || state == LIBVLC_ERROR) {
               mSavedTime = time;
           }
           mMediaPlayer.setTime(time);
       }
   }
   
}
