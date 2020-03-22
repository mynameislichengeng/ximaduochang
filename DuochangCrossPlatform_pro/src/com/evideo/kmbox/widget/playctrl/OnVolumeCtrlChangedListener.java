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

/**
 * [功能说明]
 */
public interface OnVolumeCtrlChangedListener {
    
    public void onMusicVolumeChanged(int vol);

    public void onMicrophoneVolumeChanged(int vol);

    public void onEffectVolumeChanged(int vol);
}
