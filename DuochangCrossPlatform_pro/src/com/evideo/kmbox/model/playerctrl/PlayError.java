/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年4月25日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.playerctrl;

//import javax.mail.Quota.Resource;

import android.content.Context;

import com.evideo.kmbox.ErrorInfo;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明]
 */
public class PlayError {

    /** [非解码器原因播放失败] */
    public static final int ERROR_TYPE_PLAY_FAILED = 300;
    
    /** [解码器原因造成播放失败] */
    public static final int ERROR_TYPE_PLAY_FAILED_BY_DECODER = 400;
    
    /** [播放item为空] */
    public static final int ERROR_CODE_ITEM_NULL = -301;
    
    /** [播放item的media为空] */
    public static final int ERROR_CODE_ITEM_MEDIA_NULL = -302;
    
    /** [播放item的media的URL为空] */
    public static final int ERROR_CODE_ITEM_MEDIA_URL_NULL = -303;
    
    /** [播放item的media本地路径不存在] */
    public static final int ERROR_CODE_ITEM_MEDIA_LOCALPATH_NULL = -304;
    
    /** 公播歌曲本地不存在! */
    public static final int ERROR_CODE_BROADCASTSONG_NOTEXIST_LOCAL = -305;
    
    /** [播放失败] */
    public static final int ERROR_CODE_ITEM_PLAY_FAILED = -306;
    
    private static int mDebugNo = 0;
    private static boolean DEBUG_ERROR = false;
    public static boolean debugPlayError(ErrorInfo info) {
        if (!DEBUG_ERROR) {
            return false;
        }
        mDebugNo++;
        if (mDebugNo == 1) {
            info.errorType = PlayError.ERROR_TYPE_PLAY_FAILED;
            info.errorCode = PlayError.ERROR_CODE_ITEM_NULL;
            info.errorMessage = "song item is null in start";
            return true;
        } else if (mDebugNo == 2) {
            info.errorType = PlayError.ERROR_TYPE_PLAY_FAILED;
            info.errorCode = PlayError.ERROR_CODE_BROADCASTSONG_NOTEXIST_LOCAL;
            info.errorMessage = "broadcastsong but not exist local";
            return true;
        } else if (mDebugNo == 3) {
            info.errorType = PlayError.ERROR_TYPE_PLAY_FAILED;
            info.errorCode = PlayError.ERROR_CODE_ITEM_MEDIA_NULL;
            info.errorMessage = "mCurSong media is null in initVideoPlayer";
            return true;
        } else if (mDebugNo == 4) {
            info.errorType = PlayError.ERROR_TYPE_PLAY_FAILED;
            info.errorCode = PlayError.ERROR_CODE_ITEM_MEDIA_LOCALPATH_NULL;
            info.errorMessage = "mCurSong media is null in initVideoPlayer";
            return true;
        } else if (mDebugNo == 5) {
            info.errorType = PlayError.ERROR_TYPE_PLAY_FAILED;
            info.errorCode = PlayError.ERROR_CODE_ITEM_PLAY_FAILED;
            info.errorMessage = "mCurSong media is null in initVideoPlayer";
            return true;
        } else if (mDebugNo == 6) {
            info.errorType = PlayError.ERROR_TYPE_PLAY_FAILED_BY_DECODER;
            info.errorCode = -1003;
            info.errorCodeSupplement = -102;
            return true;
        }
        
        return false;
    }
    
    public static String getTVShowMessage(Context context,ErrorInfo errInfo) {
        if (errInfo == null) {
            return "";
        }
        
        if (errInfo.errorType != ERROR_TYPE_PLAY_FAILED &&
                errInfo.errorType != ERROR_TYPE_PLAY_FAILED_BY_DECODER) {
            return "";
        }
        String info = "";
        if (errInfo.errorType == ERROR_TYPE_PLAY_FAILED) {
            switch(errInfo.errorCode) {
            case ERROR_CODE_ITEM_NULL:
                info = context.getString(R.string.toast_play_error_item_null);
                break;
            case ERROR_CODE_ITEM_MEDIA_NULL:
                info = context.getString(R.string.toast_play_error_item_media_null);
                break;
            case ERROR_CODE_ITEM_MEDIA_URL_NULL:
                info = context.getString(R.string.toast_play_error_item_media_url_null);
                break;
            case ERROR_CODE_ITEM_MEDIA_LOCALPATH_NULL:
                info = context.getString(R.string.toast_play_error_item_media_localpath_null);
                break;
            case ERROR_CODE_BROADCASTSONG_NOTEXIST_LOCAL:
                info = context.getString(R.string.toast_play_error_broadcastsong_notexist_local);
                break;
            default:
                info = context.getString(R.string.toast_play_error_unknow);
                break;
            }
        } else {
            info += context.getString(R.string.toast_play_error_decode_error);
            info += "("+ context.getString(R.string.error_code) + errInfo.errorCode + "," + errInfo.errorCodeSupplement + ")";
        }
      
        return info;
    }
    
    public static String getUmengErrorMessage(KmPlayListItem item,ErrorInfo errorInfo) {
        String errorMessage = "";
        if (item == null) {
            errorMessage = "[Play-Error],[" + errorInfo.errorCode + "][null],[null],[" + errorInfo.errorMessage+"]";
            return errorMessage;
        }
        if (errorInfo == null) {
            errorMessage = "[Play-Error],[][" + item.getSongName() + "],["+item.getSongId() + "],[null]";
            return errorMessage;
        }
        if (errorInfo.errorType == ERROR_TYPE_PLAY_FAILED) {
            errorMessage = "[Play-Error],[" + errorInfo.errorCode + "][" + item.getSongName() + "],["+item.getSongId() + "],[" + errorInfo.errorMessage+"]";
        } else if (errorInfo.errorType == ERROR_TYPE_PLAY_FAILED_BY_DECODER) {
            errorMessage = "[Play-Error][Decoder-Error],[" + errorInfo.errorCode + "][" + item.getSongName() + "],["+item.getSongId() + "],[" + errorInfo.errorMessage+"]";
        }
        EvLog.i(errorMessage);
        return "";
    }
}
