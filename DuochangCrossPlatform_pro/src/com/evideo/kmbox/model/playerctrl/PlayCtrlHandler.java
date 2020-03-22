package com.evideo.kmbox.model.playerctrl;


import android.os.Handler;
import android.os.Message;

import com.evideo.kmbox.widget.playctrl.PlayCtrlWidget;

public class PlayCtrlHandler extends Handler {
    public static final int PLAY_CTRL_MSG_DELAY_HANDLE_KM_MSG = 7;
    public static final int PLAY_CTRL_MSG_WAKE_RESUME = 8;
    public static final int PLAY_CTRL_MSG_START_PLAY_SONG = 9;
    public static final int PLAY_CTRL_MSG_PLAY_PAUSE = 10;
    public static final int PLAY_CTRL_MSG_SWITCH_TRACK = 11;
    public static final int PLAY_CTRL_MSG_NEXT_SONG = 12;
    public static final int PLAY_CTRL_MSG_REPLAY_SONG = 13;
    public static final int PLAY_CTRL_MSG_CHANGE_GRADE_MODE = 14;
    public static final int PLAY_CTRL_MSG_MICPHONE_VOL_UP = 15;
    public static final int PLAY_CTRL_MSG_MICPHONE_VOL_DOWN = 16;
    public static final int PLAY_CTRL_MSG_MUSIC_VOL_UP = 17;
    public static final int PLAY_CTRL_MSG_MUSIC_VOL_DOWN = 18;

    /**
     * [切歌]
     */
    public static final int PLAY_CTRL_MSG_CUT_SONG = 19;
    public static final int MSG_UPDATE_PLAY_TIME = 20;

    public static final int EVENT_PALY_PAUSE = 100;
    public static final int EVENT_SWITCH_TRACK = 101;
    public static final int EVENT_PLAY_NEXT_SONG = 102;
    public static final int EVENT_CUT_SONG = 103;
    public static final int EVENT_REPLAY = 104;
    public static final int EVENT_GRADE_CHANGE = 105;
    public static final int EVENT_UPDATE_PLAYBACK_TIME = 106;
    public static final int EVENT_VOL_UP = 110;
    public static final int EVENT_VOL_DOWN = 111;
    public static final int EVENT_VOL_MUTE = 112;
    public static final int EVENT_MIC_UP = 113;
    public static final int EVENT_MIC_DOWN = 114;
    public static final int EVENT_WAKE_RESUME = 120;

    private IPlayCtrlEventListener mListener;

    private static boolean instanceFlag = false; // true if 1 instance
    private static PlayCtrlHandler instance = null;

    public static PlayCtrlHandler getInstance() {
        if (!instanceFlag) {
            instanceFlag = true;
            instance = new PlayCtrlHandler();

            return instance;
        }
        return instance;
    }

    public interface IPlayCtrlEventListener {
        public void onEvent(int eventId, Message msg);
    }

    public void setListener(IPlayCtrlEventListener listener) {
        mListener = listener;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (mListener == null) {
            return;
        }

        switch (msg.what) {
            case PLAY_CTRL_MSG_START_PLAY_SONG:
                mListener.onEvent(EVENT_PALY_PAUSE, null);
                break;
            case PLAY_CTRL_MSG_PLAY_PAUSE:
                mListener.onEvent(EVENT_PALY_PAUSE, null);
                break;
            case PLAY_CTRL_MSG_SWITCH_TRACK:
                mListener.onEvent(EVENT_SWITCH_TRACK, null);
                break;
            case PLAY_CTRL_MSG_NEXT_SONG:
                mListener.onEvent(EVENT_PLAY_NEXT_SONG, null);
                break;
            case PLAY_CTRL_MSG_REPLAY_SONG:
                Message replayMessage = null;
                if (msg.arg1 == PlayCtrlWidget.PLAYCTRL_INDEX_REPLAY) {
                    replayMessage = new Message();
                    replayMessage.arg1 = PlayCtrlWidget.PLAYCTRL_INDEX_REPLAY;
                }
                mListener.onEvent(EVENT_REPLAY, replayMessage);
                break;
            case PLAY_CTRL_MSG_CHANGE_GRADE_MODE:
                mListener.onEvent(EVENT_GRADE_CHANGE, null);
                break;
            case PLAY_CTRL_MSG_MUSIC_VOL_UP:
                mListener.onEvent(EVENT_VOL_UP, null);
                break;
            case PLAY_CTRL_MSG_MUSIC_VOL_DOWN:
                mListener.onEvent(EVENT_VOL_DOWN, null);
                break;
            case PLAY_CTRL_MSG_MICPHONE_VOL_UP:
                mListener.onEvent(EVENT_MIC_UP, null);
                break;
            case PLAY_CTRL_MSG_MICPHONE_VOL_DOWN: {
                mListener.onEvent(EVENT_MIC_DOWN, null);
                break;
            }
            case PLAY_CTRL_MSG_WAKE_RESUME: {
                mListener.onEvent(EVENT_WAKE_RESUME, null);
                break;
            }
            case PLAY_CTRL_MSG_CUT_SONG: {
                mListener.onEvent(EVENT_CUT_SONG, null);
                break;
            }
            case MSG_UPDATE_PLAY_TIME: {
                mListener.onEvent(EVENT_UPDATE_PLAYBACK_TIME, null);
                break;
            }
            default:
                break;
        }
    }
}
