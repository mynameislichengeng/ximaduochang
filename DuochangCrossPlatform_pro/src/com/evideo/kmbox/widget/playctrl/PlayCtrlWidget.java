package com.evideo.kmbox.widget.playctrl;

import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.evideo.kmbox.R;
import com.evideo.kmbox.SystemConfigManager;
import com.evideo.kmbox.activity.MainActivity.OnDialogKeyListener;
import com.evideo.kmbox.model.device.IDeviceConfig;
import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
import com.evideo.kmbox.model.player.KmAudioTrackMode;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrl;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrlType.PlayerCtrlState;
import com.evideo.kmbox.model.umeng.EventConst;
import com.evideo.kmbox.model.umeng.PageName;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.VerticalImageText;
import com.evideo.kmbox.widget.playctrl.TimeOutHandler.TimeOutListener;

/**
 * [播控按钮弹框界面]
 */
public class PlayCtrlWidget extends Dialog implements View.OnClickListener {

    private static final int PLAYCTRL_HIDE_MESSAGE = 20;
    private static final int PLAYCTRL_SHOW_TIMEOUT = 5000;
    private static final int DIALOG_VERTICAL_OFFSET = 50;

    public static final int PLAYCTRL_INDEX_SWITCH_GRADE_MODE = 1;
    public static final int PLAYCTRL_INDEX_SWITCH_TRACK = 2;
    public static final int PLAYCTRL_INDEX_NEXT_SONG = 3;
    public static final int PLAYCTRL_INDEX_REPLAY = 4;
    public static final int PLAYCTRL_INDEX_PLAY_LIST = 5;
    public static final int PLAYCTRL_INDEX_PLAY_PAUSE = 6;

    private PlayCtrlAnimLinearLayout mBtnSwitchTrack;
    private PlayCtrlAnimLinearLayout mBtnNextSong;
    private PlayCtrlAnimLinearLayout mBtnReplaySong;
    private PlayCtrlAnimLinearLayout mBtnPlayList;
    private PlayCtrlAnimLinearLayout mBtnPlayPause;

    private String mPlayStateTxt = null;
    private String mPauseStateTxt = null;
    private String mOrgSingTxt = null;
    private String mAccSingTxt = null;
    private IPlayCtrlWidgetListener mListener = null;
    private TimeOutHandler mHandler = null;

    private OnDialogKeyListener mKeyListener = null;

    /**
     * [设置弹框按键监听]
     *
     * @param listener 监听器
     */
    public void setDialogKeyListener(OnDialogKeyListener listener) {
        mKeyListener = listener;
    }

    public PlayCtrlWidget(Context context) {
        super(context, R.style.CommonDialogStyle);
        setContentView(R.layout.dialog_play_ctrl_widget);
        getWindow().getDecorView().setBackground(null);
        init();

        Resources res = context.getResources();
        mOrgSingTxt = res.getString(R.string.playctrl_org_mode);
        mAccSingTxt = res.getString(R.string.playctrl_acc_mode);
        mPlayStateTxt = res.getString(R.string.playctrl_state_play);
        mPauseStateTxt = res.getString(R.string.playctrl_state_pause);
        initListener();
        initHandler();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.0f;
        lp.y = lp.y - DIALOG_VERTICAL_OFFSET;
        lp.width = LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);
    }

    private void initHandler() {
        mHandler = new TimeOutHandler(PLAYCTRL_SHOW_TIMEOUT, PLAYCTRL_HIDE_MESSAGE);
        mHandler.setListener(new TimeOutListener() {

            @Override
            public boolean onTimeOut() {
                PlayCtrlWidget.this.dismiss();
                return false;
            }
        });
    }

    /**
     * [显示弹框]
     *
     */
    public void show() {
        if (KmPlayerCtrl.getInstance().getPlayingSong() == null) {
            mBtnPlayPause.setFocusable(false);
            mBtnPlayPause.setVisibility(View.GONE);
        } else {
            mBtnPlayPause.setFocusable(true);
            mBtnPlayPause.setVisibility(View.VISIBLE);
            changePlayStateIcon();
        }

        int mode = KmPlayerCtrl.getInstance().getAudioSingMode();
        changeTrackMode(mode == KmAudioTrackMode.MODE_ORI);

        super.show();
        mHandler.resend();
        LogAnalyzeManager.getInstance().onPageStart(PageName.PLAY_CTRL_DIALOG);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBtnSwitchTrack.requestFocus();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mHandler.clear();
        LogAnalyzeManager.getInstance().onPageStart(PageName.MAIN_ACTIVITY);
    }


    /**
     * [按钮点击监听接口]
     */
    public interface IPlayCtrlWidgetListener {
        void onClickPlayCtrlBtn(int index, View view);
    }

    /**
     * [设置按钮点击监听]
     *
     * @param listener 监听器
     */
    public void setOnPlayCtrlListener(IPlayCtrlWidgetListener listener) {
        mListener = listener;
    }

    private void changeTrackMode(boolean org) {
        if (org) {
            mBtnSwitchTrack.setText(mAccSingTxt);
            mBtnSwitchTrack.setImageResource(R.drawable.playctrl_icon_acc_mode);
        } else {
            mBtnSwitchTrack.setText(mOrgSingTxt);
            mBtnSwitchTrack.setImageResource(R.drawable.playctrl_icon_org_mode);
        }
    }

    private void changePlayStateIcon() {
        int state = KmPlayerCtrl.getInstance().getPlayerState();
        switch (state) {
            case PlayerCtrlState.STATE_PAUSE:
                mBtnPlayPause.setText(mPlayStateTxt);
                mBtnPlayPause.setImageResource(R.drawable.playctrl_icon_play);
                break;
            case PlayerCtrlState.STATE_PLAY:
                mBtnPlayPause.setText(mPauseStateTxt);
                mBtnPlayPause.setImageResource(R.drawable.playctrl_icon_pause);
                break;
            default:
                mBtnPlayPause.setText(mPauseStateTxt);
                mBtnPlayPause.setImageResource(R.drawable.playctrl_icon_pause);
                break;
        }
    }

    private void init() {
        mBtnSwitchTrack = (PlayCtrlAnimLinearLayout) findViewById(R.id.dialog_playctrl_orgswitch);
        mBtnNextSong = (PlayCtrlAnimLinearLayout) findViewById(R.id.dialog_playctrl_cutsong);
        mBtnReplaySong = (PlayCtrlAnimLinearLayout) findViewById(R.id.dialog_playctrl_replaysong);
        mBtnPlayList = (PlayCtrlAnimLinearLayout) findViewById(R.id.dialog_playctrl_playlist);
        mBtnPlayPause = (PlayCtrlAnimLinearLayout) findViewById(R.id.dialog_playctrl_playpause);

        mBtnSwitchTrack.setImageResource(R.drawable.playctrl_icon_org_mode);
        mBtnNextSong.setImageResource(R.drawable.playctrl_icon_cut_song);
        mBtnReplaySong.setImageResource(R.drawable.playctrl_icon_replay_song);
        mBtnPlayList.setImageResource(R.drawable.playctrl_icon_play_list);
        mBtnPlayPause.setImageResource(R.drawable.playctrl_icon_play);

        mBtnSwitchTrack.setText(R.string.playctrl_org_mode);
        mBtnNextSong.setText(R.string.playctrl_cut_song);
        mBtnReplaySong.setText(R.string.playctrl_replay_song);
        mBtnPlayList.setText(R.string.playctrl_play_list);
        mBtnPlayPause.setText(R.string.playctrl_state_play);
    }

    private void initListener() {
        mBtnSwitchTrack.setOnClickListener(this);
        mBtnNextSong.setOnClickListener(this);
        mBtnReplaySong.setOnClickListener(this);
        mBtnPlayList.setOnClickListener(this);
        mBtnPlayPause.setOnClickListener(this);

        this.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
                if ((arg1 == IDeviceConfig.KEYEVENT_SWITCH_MV/*KeyEvent.KEYCODE_MENU*/ || arg1 == KeyEvent.KEYCODE_HOME) && arg2.getAction() == KeyEvent.ACTION_DOWN) {
                    PlayCtrlWidget.this.dismiss();
                    if (mKeyListener != null) {
                        mKeyListener.onDialogKeyListener(arg1, arg2);
                    }
                    return true;
                } else if ((arg1 == KeyEvent.KEYCODE_VOLUME_UP)
                        || (arg1 == KeyEvent.KEYCODE_VOLUME_DOWN)
                        || (arg1 == KeyEvent.KEYCODE_F1)) {
                    PlayCtrlWidget.this.dismiss();
                    if (mKeyListener != null) {
                        mKeyListener.onDialogKeyListener(arg1, arg2);
                    }
                    return true;
                } else {
                    if (mHandler != null) {
                        mHandler.resend();
                    }
                }
                return false;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.dialog_playctrl_orgswitch:
                PlayCtrlWidget.this.dismiss();
                if (mListener != null) {
                    mListener.onClickPlayCtrlBtn(PLAYCTRL_INDEX_SWITCH_TRACK, arg0);
                    UmengAgentUtil.onEventSwitchTrack(getContext(), EventConst.ID_CLICK_PLAY_CTRL_SWITCH_TRACK);
                }
                break;
            case R.id.dialog_playctrl_cutsong:
                PlayCtrlWidget.this.dismiss();
                if (mListener != null) {
                    mListener.onClickPlayCtrlBtn(PLAYCTRL_INDEX_NEXT_SONG, arg0);
                    LogAnalyzeManager.onEvent(getContext(), EventConst.ID_CLICK_PLAY_CTRL_CUT_SONG);
                }
                break;
            case R.id.dialog_playctrl_replaysong:
                if (mListener != null) {
                    PlayCtrlWidget.this.dismiss();
                    mListener.onClickPlayCtrlBtn(PLAYCTRL_INDEX_REPLAY, arg0);
                    LogAnalyzeManager.onEvent(getContext(), EventConst.ID_CLICK_PLAY_CTRL_REPLAY);
                }
                break;
            case R.id.dialog_playctrl_playlist:
                PlayCtrlWidget.this.dismiss();
                if (mListener != null) {
                    mListener.onClickPlayCtrlBtn(PLAYCTRL_INDEX_PLAY_LIST, arg0);
                    LogAnalyzeManager.onEvent(getContext(), EventConst.ID_CLICK_PLAY_CTRL_PLAY_LIST);
                }
                break;
            case R.id.dialog_playctrl_playpause:
                PlayCtrlWidget.this.dismiss();
                if (mListener != null) {
                    UmengAgentUtil.onEventPlayPause(getContext(), EventConst.ID_CLICK_PLAY_CTRL_PLAY_PAUSE);
                    mListener.onClickPlayCtrlBtn(PLAYCTRL_INDEX_PLAY_PAUSE, arg0);
                }
                break;
            default:
                break;
        }
    }
}
