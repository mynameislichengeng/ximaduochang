/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-12-28     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.playctrl;

import com.evideo.kmbox.activity.MainActivity.OnDialogKeyListener;

import android.app.Dialog;
import android.content.Context;

/**
 * [功能说明]音量调节对话框基类
 */
public abstract class BaseVolumeCtrlWidget extends Dialog {

    /**
     * @param context
     */
    public BaseVolumeCtrlWidget(Context context) {
        super(context);
    }
    
    /**
     * @param context
     * @param cancelable
     * @param cancelListener
     */
    public BaseVolumeCtrlWidget(Context context, boolean cancelable,
            OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    /**
     * @param context
     * @param theme
     */
    public BaseVolumeCtrlWidget(Context context, int theme) {
        super(context, theme);
    }



    public abstract void setDialogKeyListener(OnDialogKeyListener listener);
    
    public abstract void handleVolumeChangeEvent(boolean up);
    
    public abstract void setMusicCurrentVolume(int value);
    
    public abstract void setMusicMaxVolume(int max);
    
    public abstract void setMicrophoneCurrentVolume(int value);
    
    public abstract void setMicrophoneMaxVolume(int max);
    
    public abstract void setEffectCurrentVol(int value);
    
    public abstract void setEffectVolRange(int min,int max);
    
    public abstract void setFocus(int index);
    
    public abstract int getFocusIndex();
    
    public abstract void setVolumeCtrlListener(OnVolumeCtrlChangedListener listener);
    
}
