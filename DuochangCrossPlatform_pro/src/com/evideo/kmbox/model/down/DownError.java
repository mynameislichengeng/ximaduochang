/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年3月3日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.down;

import android.content.Context;

import com.evideo.kmbox.ErrorInfo;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明] 下载错误码定义
 */
public class DownError {
    /*****************************以下错误码为通用错误码*********************************/
    /** [通用错误之输入参数错误] */
    public static final int ERROR_CODE_INPUT_PARAM_INVALID = -1;
    /** [未知错误] */
    public static final int ERROR_UNKNOW = -2;
    /** [item为空] */
    public static final int ERROR_CODE_ITEM_NULL = -3;
    /** [item的media为空] */
    public static final int ERROR_CODE_ITEM_MEDIA_NULL = -4;
    /** [media,remote url is empty] */
    public static final int ERROR_CODE_MEDIA_URL_NULL = -5;
    
    /*****************************以下错误为获取播放链阶段错误*********************************/
    /** [错误类型为获取播放链失败] */
    public static final int ERROR_TYPE_GET_MEDIALIST_FAILED = 100;
    
    /** [发送请求发生json异常] */
    public static final int ERROR_CODE_REQUSET_CATCH_JSON_EXCEPTION = -101;
    /** [发送请求发生未知异常] */
    public static final int ERROR_CODE_REQUSET_CATCH_UNKNOW_EXCEPTION = -102;
    
    /** [发送请求播放链消失失败] */
    public static final int ERROR_CODE_SEND_GET_MEDIALIST_FAILED = -103;
    
    /** [服务器返回错误,补充码为错误码] */
    //("17", "歌曲ID不正确"),(122, "获得歌曲信息为空"),(19, "没有歌曲文件信息");
    public static final int ERROR_CODE_SERVER_RESPONSE_ERROR = -104;
    
    /** [json中没有medialist内容] */
    public static final int ERROR_CODE_GET_MEDIALIST_NULL = -105;
    
    /** [服务器回复medialist均无效] */
    public static final int ERROR_CODE_MEDIA_LIST_INVALID = -106;
    
    public static final int ERROR_CODE_SERVER_RESPONSE_CONTENT_INVALID = -107;
    
    /*****************************以下错误为下载阶段错误*********************************/
    /** [错误类型为下载] */
    public static final int ERROR_TYPE_DOWN_MEDIA_FAILED = 200;
    
    /** media url无法获取文件名与文件大小 ,此协议附带responseCode*/
    public static final int ERROR_CODE_MEDIA_URL_CAN_NOT_GET_SIZE_AND_NAME = -201;
    /** [下载空间不足] */
    public static final int ERROR_CODE_SPACE_SUFFICIENT = -202;
    
    /** [下载播放链连接失败] */
    public static final int ERROR_CODE_CONNECT_FAILED = -205;
    /** [文件写入失败] */
    public static final int ERROR_CODE_WRITE_FAILED = -206;
    /** [下载播放链读取超时] */
    public static final int ERROR_CODE_READ_TIMEOUT = -207;
    /** [下载播放链无效,此处responseCode需要] */
    public static final int ERROR_CODE_URL_CONTENTLEN_INVLAID = -208;

    
    /**
     * [功能说明] 获取umeng上报错误
     * @param item
     * @param errorInfo
     * @return
     */
    public static String getUmengErrorMessage(KmPlayListItem item,ErrorInfo errorInfo) {
        String errorMessage = "";
        if (errorInfo == null) {
            return "errorInfo is null";
        }
        
        if (item == null) {
            errorMessage = "[DownMedia-Error],[" + errorInfo.errorCode + "][null],[null],[" + errorInfo.errorMessage+"]";
            return errorMessage;
        }
        
        if (errorInfo.errorType == ERROR_TYPE_GET_MEDIALIST_FAILED) {
            errorMessage = "[GetMediaListUrl-Error],[" + errorInfo.errorCode + "][" + item.getSongName() + "],["+item.getSongId() + "],[" + errorInfo.errorMessage+"]";
        } else if (errorInfo.errorType == ERROR_TYPE_DOWN_MEDIA_FAILED) {
            errorMessage = "[DownMedia-Error],[" + errorInfo.errorCode + "][" + item.getSongName() + "],["+item.getSongId() + "],[" + errorInfo.errorMessage+"]";
        }
        EvLog.i(errorMessage);
        return errorMessage;
    }
    
    
    public static String getTVShowMessage(Context context,ErrorInfo errorInfo) {
        if (errorInfo == null) {
            return "未知错误";
        }
        String info = "";
        
        if (errorInfo.errorType == ERROR_TYPE_GET_MEDIALIST_FAILED) {
            switch(errorInfo.errorCode) {
            case ERROR_UNKNOW:
                info = context.getString(R.string.toast_getmedia_error_unknow);
                break;
            case ERROR_CODE_SEND_GET_MEDIALIST_FAILED:
                info = context.getString(R.string.toast_getmedia_error_send_get_medialist_failed);
                break;
            case ERROR_CODE_REQUSET_CATCH_JSON_EXCEPTION:
                info = context.getString(R.string.toast_getmedia_error_catch_json_exception);
                break;
            case ERROR_CODE_REQUSET_CATCH_UNKNOW_EXCEPTION:
                info = context.getString(R.string.toast_getmedia_error_catch_unknow_exception);
                break;
            case ERROR_CODE_SERVER_RESPONSE_ERROR:
                info = context.getString(R.string.toast_getmedia_error_server_response_error) + "(" + context.getString(R.string.error_code) + errorInfo.errorCodeSupplement + ")";
                break;
            case ERROR_CODE_GET_MEDIALIST_NULL:
                info = context.getString(R.string.toast_getmedia_error_list_null);
                break;
            case ERROR_CODE_MEDIA_LIST_INVALID:
                info = context.getString(R.string.toast_getmedia_error_list_invalid);
                if (errorInfo.errorCodeSupplement != 0) {
                    info += "(" + context.getString(R.string.error_code) + errorInfo.errorCodeSupplement + ")";
                }
                break;
            default:
                info = context.getString(R.string.toast_getmedia_error_unknow);
                break;
            }
        } else if (errorInfo.errorType == ERROR_TYPE_DOWN_MEDIA_FAILED) {
            switch(errorInfo.errorCode) {
                case ERROR_CODE_MEDIA_URL_CAN_NOT_GET_SIZE_AND_NAME:
                    info = context.getString(R.string.toast_down_error_media_url_can_not_get_size_and_name);
                    if (errorInfo.errorCodeSupplement != 0) {
                        info += "("+ context.getString(R.string.error_code) + errorInfo.errorCodeSupplement + ")";
                    }
                    break;
                case ERROR_CODE_SPACE_SUFFICIENT:{
                    info = context.getString(R.string.toast_down_error_space_sufficient);
                    break;
                }
                case ERROR_CODE_CONNECT_FAILED:{
                    info = context.getString(R.string.toast_down_error_connect_failed);
                    if (errorInfo.errorCodeSupplement != 0) {
                        info += "(" + context.getString(R.string.error_code) + errorInfo.errorCodeSupplement + ")";
                    }
                    break;
                }
                case ERROR_CODE_WRITE_FAILED:{
                    info = context.getString(R.string.toast_down_error_write_failed);
                    break;
                }
                case ERROR_CODE_READ_TIMEOUT:{
                    info = context.getString(R.string.toast_down_error_read_timeout);
                    break;
                }
                case ERROR_CODE_URL_CONTENTLEN_INVLAID:{
                    info = context.getString(R.string.toast_down_error_url_contentlen_invlaid);
                    break;
                }
                case ERROR_CODE_ITEM_NULL: {
                    info = context.getString(R.string.toast_down_error_item_null);
                    break;
                }
                case ERROR_CODE_ITEM_MEDIA_NULL:{
                    info = context.getString(R.string.toast_down_error_item_media_null);
                    break;
                }
                case ERROR_CODE_MEDIA_URL_NULL:{
                    info = context.getString(R.string.toast_down_error_media_url_null);
                    break;
                }
                default: {
                    info = context.getString(R.string.toast_down_error_unknow);
                    break;
                }
            }
        }
        return info;
    }
    
    private static boolean DEBUG_ERROR = false;
    private static int mDownErrorDebugNo = 0;
    public static boolean debugDownThreadError(ErrorInfo errInfo) {
        if (!DEBUG_ERROR) {
            return false;
        }
        mDownErrorDebugNo++;
        
        if (mDownErrorDebugNo == 1) {
            errInfo.errorCode = KmDownThread.ERROR_CONNECT_FAILED;
            //补充responseCode
            errInfo.errorCodeSupplement = 101;//
            errInfo.errorMessage = "http connect failed";
            return true;
        } else if (mDownErrorDebugNo == 2) {
            errInfo.errorCode = KmDownThread.ERROR_URL_CONTENTLEN_INVLAID;
            errInfo.errorMessage  = "http getContentLength <= 0";
            return true;
        } else if (mDownErrorDebugNo == 3) {
            errInfo.errorCode = KmDownThread.ERROR_FILE_CREATE_FAILED;
            errInfo.errorMessage = "open local file failed:";
            return true;
        } else if (mDownErrorDebugNo == 4) {
            errInfo.errorCode = KmDownThread.ERROR_IO;
            errInfo.errorMessage = "catch IO error when write file:";
            return true;
        } else if (mDownErrorDebugNo == 5) {
            errInfo.errorCode = KmDownThread.ERROR_READ_TIMEOUT;
            errInfo.errorMessage = "http read timeout";
            return true;
        }
        
        return false;
    }
    
    private static int mBeforDownThreadNo = 0;
    public static boolean debugBeforeDownThreadError(ErrorInfo errorInfo) {
        if (!DEBUG_ERROR) {
            return false;
        }
        mBeforDownThreadNo++;
        errorInfo.errorType = DownError.ERROR_TYPE_DOWN_MEDIA_FAILED;
        if (mBeforDownThreadNo == 1) {
            errorInfo.errorCode = DownError.ERROR_CODE_MEDIA_URL_CAN_NOT_GET_SIZE_AND_NAME;
            return true;
        } else if (mBeforDownThreadNo == 2) {
            errorInfo.errorCode = DownError.ERROR_CODE_SPACE_SUFFICIENT;
            return true;
        } else if (mBeforDownThreadNo == 3) {
            errorInfo.errorCode = DownError.ERROR_CODE_ITEM_NULL;
            return true;
        } else if (mBeforDownThreadNo == 4) {
            errorInfo.errorCode = DownError.ERROR_CODE_ITEM_MEDIA_NULL;
            return true;
        } else if (mBeforDownThreadNo == 5) {
            errorInfo.errorCode = DownError.ERROR_CODE_MEDIA_URL_NULL;
            return true;
        }
        errorInfo.errorType = 0;
        return false;
    }
    
    
    private static int mGetMediaListDebugNo = 0;
    public static boolean debugGetMediaListError(ErrorInfo errorInfo) {
        if (!DEBUG_ERROR) {
            return false;
        }
        mGetMediaListDebugNo++;
        if (mGetMediaListDebugNo == 1) {
            errorInfo.errorCode = DownError.ERROR_CODE_SEND_GET_MEDIALIST_FAILED;
            errorInfo.errorMessage = "send requestSongMediaList message failed";
            return true;
        } else if (mGetMediaListDebugNo == 2) {
            errorInfo.errorCode = DownError.ERROR_CODE_SERVER_RESPONSE_ERROR;
            errorInfo.errorCodeSupplement = 17;
            errorInfo.errorMessage = "111";
            return true;
        } else if (mGetMediaListDebugNo == 3) {
            errorInfo.errorCode = DownError.ERROR_CODE_GET_MEDIALIST_NULL;
            errorInfo.errorMessage = "get no medialist array in json";
            return true;
        } else if (mGetMediaListDebugNo == 4) {
            errorInfo.errorCode = DownError.ERROR_CODE_MEDIA_LIST_INVALID;
            errorInfo.errorCodeSupplement = 201;
            errorInfo.errorMessage = "all medai url in json is invalid";
            return true;
        } else if (mGetMediaListDebugNo == 5) {
            errorInfo.errorCode = DownError.ERROR_CODE_REQUSET_CATCH_JSON_EXCEPTION;
            errorInfo.errorMessage = "catch json exception";
            return true;
        } else if (mGetMediaListDebugNo == 6) {
            errorInfo.errorCode = DownError.ERROR_CODE_REQUSET_CATCH_UNKNOW_EXCEPTION;
            errorInfo.errorMessage = "catch unknow exception";
            return true;
        }
        return false;
    }
    
}
