package com.evideo.kmbox.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.text.TextUtils;

import com.evideo.kmbox.model.umeng.UmengAgentUtil;
//import org.apache.http.entity.mime.FormBodyPart;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.entity.mime.content.ContentBody;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.InputStreamBody;


/**
 * [功能说明]
 */
public class HttpUtil {
    private static final int BUFFER_SIZE = 1024 * 8;

    public static class HttpDownInfo {
        public boolean downRet;
        public String  downPath;
        public HttpDownInfo(boolean ret,String path) {
            this.downRet = ret;
            this.downPath = path;
        }
    }
    
    public static HttpDownInfo downloadFileWithBufferWrite(String url,String defaultName,String folder) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(folder)) {
            return null;
        }
        
        File dir = new File(folder);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                UmengAgentUtil.reportError("mkdir failed:" + folder);
                return null;
            }
        }
        
        HttpFile httpFile = new HttpFile();
        if (httpFile.open(url) != 0) {
            return null;
        }
        
        long fileTotalSize = httpFile.getContentLength(); 

        String filename = defaultName;
        if (TextUtils.isEmpty(filename)) {
            filename = httpFile.getFileName();
        }
        
        if (TextUtils.isEmpty(filename)) {
            EvLog.e("can not get fileName");
            httpFile.close();
            return null;
        }

        String filePath = FileUtil.concatPath(folder, filename);
        FileOutputStream fileOutputStream = null;
        HttpDownInfo info = new HttpDownInfo(true,filePath);
        int needReadSize = 0;
        int realReadSize = 0;
        long fileWriteSize = 0;
        byte[] buf = new byte[BUFFER_SIZE];
        File file = null;
        try {
            file = new File(filePath);
            if (file.exists()) {
                if (file.length() == fileTotalSize)  { //local file len == net len
                    EvLog.i(filePath + " already exist ,do not need down" );
                    httpFile.close();
                    return info;
                }
                file.delete();
            }
            file.createNewFile();
            fileOutputStream = new FileOutputStream(filePath, false);
        
            while (fileWriteSize < fileTotalSize) {
                if (fileWriteSize + buf.length < fileTotalSize) {
                    needReadSize = buf.length;
                } else {
                    needReadSize = (int) (fileTotalSize - fileWriteSize);
                    // EvLog.d("read tail needReadSize=" + needReadSize);
                }
                realReadSize = localRead(httpFile, buf, 0, needReadSize);

                if (realReadSize > 0) {
                    try {
                        fileOutputStream.write(buf, 0, realReadSize);
                    } catch (IOException e) {
                        EvLog.e(e.getMessage());
                        UmengAgentUtil.reportError(e);
                        info.downRet = false;
                        break;
                    }
                } else if (realReadSize == 0) {
                    // continue;
                } else {
                    info.downRet = false;
                    break;
                }

                if (realReadSize > 0) {
                    fileWriteSize += realReadSize;
                }
            }
            
            if (fileOutputStream != null) {
                fileOutputStream.flush();
            }
        } catch (IOException e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
            info.downRet = false;
        } finally {
            CommonUtil.safeClose(fileOutputStream);
            if (httpFile != null) {
                httpFile.close();
                httpFile = null;
            }  
            file = null;
        }
        return info;
    }
    
    private static int localRead(HttpFile file, byte[] buffer, int byteOffset,int byteCount) throws IOException {

        int totalReadNum = 0;
        int remainReadNum = byteCount;
        int startOffset = byteOffset;
        int singleRead = 0;

        int nRetryReadTime = 0;
        
        while (totalReadNum < byteCount) {

            singleRead = file.read(buffer, startOffset, remainReadNum);
            if (singleRead > 0) {
                totalReadNum += singleRead;

                if (totalReadNum >= byteCount) {
                    totalReadNum = byteCount;
                    break;
                } else {
                    startOffset = totalReadNum;
                    remainReadNum = byteCount - startOffset;
                }
            } else {
                nRetryReadTime ++;
                EvLog.d("KmDownThread localRead failed times = "+ nRetryReadTime + "startOffset = "
                        + startOffset + ", readcount " + remainReadNum);
                
                if ( nRetryReadTime > 3 ) {
                    break;
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        EvLog.e(e.getMessage());
                        UmengAgentUtil.reportError(e);
                    }
                    continue;
                }
            }
        }
        return totalReadNum;
    }
}
