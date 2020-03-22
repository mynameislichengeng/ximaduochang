/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年7月25日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.playctrl;

import android.content.Context;
import android.media.AudioManager;
import android.view.KeyEvent;

import com.evideo.kmbox.activity.MainActivity.OnDialogKeyListener;
import com.evideo.kmbox.model.device.IDeviceConfig;
import com.evideo.kmbox.widget.playctrl.PlayCtrlWidget.IPlayCtrlWidgetListener;


/**
 * [功能说明]
 */
public class MVCtrlWidgetManager {
    //    private static MVCtrlWidgetManager instance = null;
    private PlayCtrlWidget mPlayWidget;
    //    private PlayBackCtrlWidget mPlayBackWidget = null;
    private BaseVolumeCtrlWidget mVolWidget = null;

    /*public static MVCtrlWidgetManager getInstance() {
        if(instance == null) {
            synchronized (MVCtrlWidgetManager.class) {
                MVCtrlWidgetManager temp = instance;
                if(temp == null) {
                  temp = new MVCtrlWidgetManager();
                  instance = temp;
                }
            }
         }
         return instance;
    }*/

    public void init(Context context) {
        initPlayCtrlWidget(context);
//        initPlayBackWidget(context);
        initVolWidget(context);
    }

    private void initVolWidget(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolWidget = new ThirdPlatformVolumeCtrlWidget(context);
        mVolWidget.setMusicMaxVolume(max);
        mVolWidget.setMusicCurrentVolume(vol);
        mVolWidget.setDialogKeyListener(new OnDialogKeyListener() {
            @Override
            public void onDialogKeyListener(int arg1, KeyEvent arg2) {
                if (arg2.getAction() == KeyEvent.ACTION_DOWN &&
                        arg2.getKeyCode() == KeyEvent.KEYCODE_F1) {
                    if (mMVKeyListener != null) {
                        mMVKeyListener.onMVPlayPauseKeyEvent();
                    }
                }
            }
        });
    }

    public void setMVKeyListener(IMVKeyListener listener) {
        mMVKeyListener = listener;
    }

    /* public void setPlayBackCtrlListener(IPlayBackCtrlListener listener) {
         mPlayBackWidget.setListener(listener);
     }
     */
    public void setPlayCtrlWidgetListener(IPlayCtrlWidgetListener listener) {
        mPlayWidget.setOnPlayCtrlListener(listener);
    }

    private IMVKeyListener mMVKeyListener = null;

    public interface IMVKeyListener {
        public void onMVVolumeUpKeyEvent();

        public void onMVVolumeDownKeyEvent();

        public void onMVMenuKeyEvent();

        public void onMVHomeKeyEvent();

        public void onMVPlayPauseKeyEvent();
    }

    private OnDialogKeyListener mPlayCtrlDialogKeyListener = new OnDialogKeyListener() {
        @Override
        public void onDialogKeyListener(int arg1, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                int code = event.getKeyCode();
                switch (code) {
                    case KeyEvent.KEYCODE_VOLUME_UP:
                        if (mMVKeyListener != null) {
                            mMVKeyListener.onMVVolumeUpKeyEvent();
                        }
                        return;
                    case KeyEvent.KEYCODE_VOLUME_DOWN:
                        if (mMVKeyListener != null) {
                            mMVKeyListener.onMVVolumeDownKeyEvent();
                        }
                        return;
                    case KeyEvent.KEYCODE_F1:
                        if (mMVKeyListener != null) {
                            mMVKeyListener.onMVPlayPauseKeyEvent();
                        }
                        return;
//                    case KeyEvent.KEYCODE_MENU:
                    case IDeviceConfig.KEYEVENT_SWITCH_MV:
                        if (mMVKeyListener != null) {
                            mMVKeyListener.onMVMenuKeyEvent();
                        }
                        return;
                    case KeyEvent.KEYCODE_HOME:
                        if (mMVKeyListener != null) {
                            mMVKeyListener.onMVHomeKeyEvent();
                        }
                        return;
                    default:
                        break;
                }
                return;
            }
        }
    };

    /* private void initPlayBackWidget(Context context) {
         if (mPlayBackWidget == null) {
             mPlayBackWidget = new PlayBackCtrlWidget(context);
         }
         mPlayBackWidget.setDialogKeyListener(mPlayCtrlDialogKeyListener);
     }*/
    private void initPlayCtrlWidget(Context context) {
        if (mPlayWidget == null) {
            mPlayWidget = new PlayCtrlWidget(context);
        }
        mPlayWidget.setDialogKeyListener(mPlayCtrlDialogKeyListener);
    }


    public void hide() {
        if (mPlayWidget != null) {
            mPlayWidget.hide();
        }
        /*if (mPlayBackWidget != null) {
            mPlayBackWidget.hide();
        }*/
        if (mVolWidget != null) {
            mVolWidget.hide();
        }
    }

    public void dismiss() {
        if (mPlayWidget != null) {
            mPlayWidget.dismiss();
            mPlayWidget = null;
        }
        /*if (mPlayBackWidget != null) {
            mPlayBackWidget.dismiss();
            mPlayBackWidget = null;
        }*/
        if (mVolWidget != null) {
            mVolWidget.dismiss();
            mVolWidget = null;
        }
    }

    public boolean isPlayCtrlWidgetShowing() {
        return (mPlayWidget != null) ? mPlayWidget.isShowing() : (false);
    }

    public void showPlayWidgetWithGradeBtn(boolean gradeOpen) {
        if (mPlayWidget != null) {
            mPlayWidget.show();
        }
    }

    public void showPlayWidgetWithoutGradeBtn() {
        if (mPlayWidget != null) {
            mPlayWidget.show();
        }
    }

    public void showPlayBackWidget() {
       /* if (mPlayBackWidget != null) {
            mPlayBackWidget.show();
        }*/
    }

    public void showVolWidget() {
        if (!mVolWidget.isShowing()) {
            mVolWidget.show();
        }
    }

    public void showVolWidgetByEvent(Context context, int index, boolean up) {
        if (up) {
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

            if (currentVolume < maxVol) {
                currentVolume++;
            }
            am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, am.FLAG_SHOW_UI);
        } else {
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (currentVolume > 0) {
                currentVolume--;
            }
            am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, am.FLAG_SHOW_UI);
        }
    }

    public void hideVolWidget() {
        if (mVolWidget != null && mVolWidget.isShowing()) {
            mVolWidget.dismiss();
        }
    }
}
