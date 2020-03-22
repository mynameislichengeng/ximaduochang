package com.evideo.kmbox.model.httpd;

import com.evideo.kmbox.util.EvLog;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class KmHttpdServer extends NanoHTTPD {
    private static final String KEY_URL = "url";
    private static final String KEY_LOCAL = "local";
    public static final int LOCAL_HTTP_PORT = 8800;
    public static final String MEDIA_HTTP_LOCAL_FLAG = "/local";
    public static final String MEDIA_HTTP_REMOTE_FLAG = "/remote";
    public static final String TAG = KmHttpdServer.class.getSimpleName();
    private long mSessionId = 0;


    public KmHttpdServer() {
        super(LOCAL_HTTP_PORT);
    }

    private String getMimeTypeFromUri(String uriString) {
        String mimeType = "video/mpeg";
        if (uriString.endsWith(".mkv")) {
            mimeType = "video/x-matroska";
        } else if (uriString.endsWith(".mov")) {
            mimeType = "video/quicktime";
        } else if (uriString.endsWith(".mp4")) {
            mimeType = "video/mp4";
        } else if (uriString.endsWith(".avi")) {
            mimeType = "video/x-msvideo";
        } else if (uriString.endsWith(".flv")) {
            mimeType = "video/x-flv";
        } else if (uriString.endsWith(".ts")) {
            mimeType = "video/mp2t";
        } else if (uriString.endsWith(".ts.tmp")) {
            mimeType = "video/mp2t";
        } else if (uriString.endsWith(".aac")) {
            mimeType = "audio/x-aac";
        } else if (uriString.endsWith(".tmp")) {
            mimeType = "audio/x-aac";
        }
        return mimeType;
    }


    @Override
    public Response serve(IHTTPSession session) {

        boolean supportSeek = false;
        String remoteUrl = null;

//		EvLog.d(TAG, "recv new request ##########uri:" + session.getUri());

        Map<String, String> params = session.getParms();
        if (params.containsKey(KEY_URL)) {
            remoteUrl = params.get(KEY_URL);
            int index = remoteUrl.indexOf("?support");
            if (index > 0) {
                remoteUrl = remoteUrl.substring(0, index);
                supportSeek = true;
            }
        }

        boolean forceLocal = false;

        if (params.containsKey(KEY_LOCAL)) {
            forceLocal = params.get(KEY_LOCAL).compareTo("1") == 0;
        }

        // 非ts资源支持seek操作
        String path = session.getUri();
        if (!path.endsWith(".ts") && !path.endsWith(".ts.tmp")) {
            supportSeek = true;
        }

        String range = session.getHeaders().get("range");
//		EvLog.d(TAG, supportSeek + " ,path=" + path + ",range=" + range);

        boolean local = false;
        // FIXME
        if (path.startsWith(MEDIA_HTTP_LOCAL_FLAG)) {
            path = path.substring(MEDIA_HTTP_LOCAL_FLAG.length(), path.length());
            local = true;
        } else if (path.startsWith(MEDIA_HTTP_REMOTE_FLAG)) {
            path = path.substring(MEDIA_HTTP_REMOTE_FLAG.length(), path.length());
        } else {
            EvLog.e(TAG, path + " not valid,reject");
            return null;
        }

        //非本地歌曲，会话id增加，便于查看在线歌曲获取时长时的调试信息
        if (!local) {
            mSessionId++;
        }

//		EvLog.i(TAG,"path=" + path);
        EvLog.i(TAG, "url=" + remoteUrl + ",range:" + range);

        InputStream fis = null;

        long totalSize = 0;
        if (local) {
            File tmp = new File(path);
            totalSize = tmp.length();
            tmp = null;
        } else {
            if (params.containsKey("total_size")) {
                totalSize = Integer.valueOf(params.get("total_size"));
            } else {
                EvLog.e(TAG, "please take total_size");
                return null;
            }
        }

        long fileLen = 0;
        try {
            fis = new ForceReadLocalInputStream(path, mSessionId, totalSize);
            if (!((ForceReadLocalInputStream) fis).initFirstSection()) {
                fis.close();
                return null;
            }
            fileLen = ((ForceReadLocalInputStream) fis).getDataTotalLength();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // reponse
        long range_start = 0;
        long range_end = -1;

        if (range != null) {
//            EvLog.d(TAG, "range info: " + range );
            range = range.substring("bytes=".length());
            int minus = range.indexOf('-');

            if (minus > 0) {
                range_start = Long.parseLong(range.substring(0,
                        minus));
                if (minus < (range.length() - 1)) {
                    range_end = Long.parseLong(range.substring(minus + 1));
                } else {
                    //              EvLog.d( "KmHttpdServer", "not found rangeEnd info" );
                }
            }
        }

        boolean skipSuccess = false;

        if (range_end <= 0) {
            //FIXME
            range_end = fileLen;
        }

        Status returnStatus = Status.OK;
        if (range_start > 0 || range_end > 0) {
            returnStatus = Status.PARTIAL_CONTENT;
        }

        String mimeType = getMimeTypeFromUri(path);
        Response response = new Response(returnStatus, mimeType, fis);
        Random rnd = new Random();
        String etag = Integer.toHexString(rnd.nextInt());
        response.addHeader("ETag", etag);
//        EvLog.i(TAG,"supportSeek="+ supportSeek +",mSessionId=" +mSessionId + ",forceLocal=" + forceLocal);
        EvLog.i(TAG, "supportSeek:" + supportSeek + ",range[" + range_start + "-" + range_end + "],forceLocal=" + forceLocal);
        //ts 格式视频不回复字段，否则ffmpeg会读取尾部数据，在线播放会prepare很久
        if (supportSeek || skipSuccess/*|| range_start > 0*/) {
            EvLog.i(TAG, " response Range info");
            response.addHeader("Accept-Ranges", "bytes");
            response.addHeader("Content-Range", "bytes " + range_start + "-"
                    + range_end + "/" + fileLen);
        }

        response.addHeader("Content-Length", "" + (range_end - range_start));
        response.addHeader("Connection", "close");
        response.setChunkedTransfer(false);

        return response;
    }
}
