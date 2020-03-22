package com.evideo.kmbox.model.umeng;

import java.util.HashMap;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
import com.evideo.kmbox.model.player.KmAudioTrackMode;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrl;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrlType.PlayerCtrlState;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;

public class UmengAgentUtil {
    
    private UmengAgentUtil() {
    }
    
    /**
     * @brief : [播放暂停，在动作执行前调用]
     * @param context
     * @param eventId
     */
    public static void onEventPlayPause(Context context, String eventId) {
        HashMap<String, String> m = new HashMap<String, String>();
        int state = KmPlayerCtrl.getInstance().getPlayerState();
        if(state == PlayerCtrlState.STATE_PAUSE) {
            m.put(EventConst.K_PLAY_PAUSE_ACTION, EventConst.V_PLAY_PAUSE_ACTION_PLAY);
        } else if (state == PlayerCtrlState.STATE_PLAY) {
            m.put(EventConst.K_PLAY_PAUSE_ACTION, EventConst.V_PLAY_PAUSE_ACTION_PAUSE);
        }
//        UmengAgent.onEvent(context, eventId, m);
        LogAnalyzeManager.onEvent(context, eventId, m);
    }
    
    /**
     * @brief : [原伴唱切换，在动作执行前调用]
     * @param context
     * @param eventId
     */
    public static void onEventSwitchTrack(Context context, String eventId) {
        HashMap<String, String> m = new HashMap<String, String>();
        m.put(EventConst.K_SWITCH_TRACK
                , KmPlayerCtrl.getInstance().getAudioSingMode() == KmAudioTrackMode.MODE_ACC
                ? EventConst.V_SWITCH_TRACK_ORIGIANL : EventConst.V_SWITCH_TRACK_ACCOMPANIMENT);
//        UmengAgent.onEvent(context, eventId, m);
        LogAnalyzeManager.onEvent(context, eventId, m);
    }
    
    public static void reportError(String error) {
        if (TextUtils.isEmpty(error)) {
            return;
        }
//        UmengAgent.reportError(BaseApplication.getInstance(), error);
        LogAnalyzeManager.reportError(BaseApplication.getInstance(), error);
    }
    
    public static void reportError(Throwable e) {
        if (e == null) {
            return;
        }
//        UmengAgent.reportError(BaseApplication.getInstance(), e);
        LogAnalyzeManager.reportError(BaseApplication.getInstance(), e);
    }
    
    /**
     * @brief : [音量调节]
     * @param context
     * @param eventId
     * @param up
     */
    public static void onEventVolumeAction(Context context, String eventId, boolean up) {
        HashMap<String, String> m = new HashMap<String, String>();
        m.put(EventConst.K_VOLUME_ACTION, up ? EventConst.V_VOLUME_UP : EventConst.V_VOLUME_DOWN);
//        UmengAgent.onEvent(context, eventId, m);
        LogAnalyzeManager.onEvent(context, eventId, m);
    }
    
    /**
     * [功能说明]收藏歌曲事件
     * @param context context
     * @param eventId 事件id
     * @param favorite true 收藏  false 取消收藏
     */
    public static void onEventFavoriteAction(Context context, String eventId, boolean favorite) {
        HashMap<String, String> m = new HashMap<String, String>();
        m.put(EventConst.K_FAVORITE_ACTION, favorite ? EventConst.V_FAVORITE : EventConst.V_CANCEL);
//        UmengAgent.onEvent(context, eventId, m);
        LogAnalyzeManager.onEvent(context, eventId, m);
    }
    
    /**
     * [功能说明]遥控器按键切歌事件
     * @param context
     * @param keyCode
     */
    public static void onEventCutSong(Context context, int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//            UmengAgent.onEvent(context, EventConst.ID_CLICK_RIGHT_TWICE_CUT_SONG);
            LogAnalyzeManager.onEvent(context, EventConst.ID_CLICK_RIGHT_TWICE_CUT_SONG);
        } else if (keyCode == KeyEvent.KEYCODE_F1) {
//            UmengAgent.onEvent(context, EventConst.ID_CLICK_REMOTE_F1_CUT_SONG);
            LogAnalyzeManager.onEvent(context, EventConst.ID_CLICK_REMOTE_F1_CUT_SONG);
        }
    }
    
}
