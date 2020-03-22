package com.evideo.kmbox.model.down;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.awirtz.util.RingBuffer;
import com.evideo.kmbox.ErrorInfo;
import com.evideo.kmbox.SystemConfigManager;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.device.DeviceName;
import com.evideo.kmbox.model.device.IDeviceConfig;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;
import com.evideo.kmbox.util.HttpFile;
import com.evideo.kmbox.util.HttpFile.HttpOpenResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


public class KmDownThread extends Thread {

    public static class Task {
        public boolean valid = false;
        public String url;
        public String localPath;
        public long fileTotalLen;
        public long fileDownedSize;
        public long id;

        public Task(String url, String localPath) {
            this.url = new String();
            this.url = url;
            this.localPath = localPath;
            this.fileTotalLen = 0;
            this.fileDownedSize = 0;
            synchronized (Task.class) {
                this.id = mDownID++;
            }
        }
    }

    ;

    private static long mDownID = 0;

    public static final int DOWNLOAD_MSG_ERROR = 201;
    public static final int DOWNLOAD_MSG_PROGRESS = 202;
    public static final int MSG_CLOSE_HTTP_FILE = 203;


    public static final int ERROR_NONE = 0;
    /**
     * [创建文件失败]
     */
    public static final int ERROR_FILE_CREATE_FAILED = 1;

    /**
     * [链接content-len大小非法]
     */
    public static final int ERROR_URL_CONTENTLEN_INVLAID = 2;
    /** [404，资源不存在] */
//    public static final int ERROR_RESOURCE_NOT_EXIST = 3;
    /**
     * [连接失败]
     */
    public static final int ERROR_CONNECT_FAILED = 4;

    /**
     * [文件写失败]
     */
    public static final int ERROR_IO = 5;

    /**
     * [读取信息超时]
     */
    public static final int ERROR_READ_TIMEOUT = 6;

    public static final int ERROR_INVALID_ITEM = 7;

    public static final int ERROR_PAUSE_WITH_CLOSEFILE = 8;

    //HTTP连接出错重试次数
    public static final int HTTP_CONNECT_MAX_RETRY_TIMES = 3;
    public static final int DOWN_THREAD_BUFFER_MAX_SIZE = 8 * 1024;//32


    private ArrayList<Task> mTaskList;
    private Handler mHandler = null;
    //    private boolean mExit = true;
    private byte[] mReadBuffer = null;
    private Task mCurrentDownItem = null;

    /**
     * [正常工作模式]
     */
    private static final int STATE_NORMAL = 0;
    /**
     * [暂停下载模式]
     */
    private static final int STATE_PAUSE = 1;
    /**
     * [恢复下载模式,这个是暂时态]
     */
    private static final int STATE_RESUME = 2;

    private int mState = STATE_NORMAL;

    private ErrorInfo mErrorInfo = null;
    private boolean mStop = false;

    public void setStop() {
        mStop = true;
    }

    public void pauseDown() {
        mState = STATE_PAUSE;
    }

    public void resumeDown() {
        mState = STATE_RESUME;
    }

    public interface IKmDownListener {
        public void onDownError(int serialNum, int errCode, String errMsg);

        public void onDownProgress(int serialNum, long downedSize, long totalSize, float speed);
    }

    ;

    public void addTask(Task task) {
        EvLog.d("KmDownThread,addTask:" + task.url + ",mTaskList.size=" + mTaskList.size());
        task.valid = true;
        synchronized (mTaskList) {
            if (DeviceConfigManager.getInstance().isThirdApp()) {
                if (mCurrentDownItem != null) {
                    synchronized (mCurrentDownItem) {
                        EvLog.d("KmDownThread,addTask:" + mCurrentDownItem.id + " is downing, set invalid");
                        mCurrentDownItem.valid = false;
                    }
                }
            }
            mTaskList.add(task);
        }
    }

    public void insertTask(Task task) {
        task.valid = true;
        synchronized (mTaskList) {
            EvLog.d("before insertTask size=" + mTaskList.size());
            ArrayList<Task> tmpList = new ArrayList<Task>();
            tmpList.add(task);
            if (mCurrentDownItem != null) {
                Task tmpTask = new Task(mCurrentDownItem.url, mCurrentDownItem.localPath);
                tmpTask.id = mCurrentDownItem.id;
                tmpTask.valid = true;
                tmpList.add(tmpTask);
            }
            tmpList.addAll(mTaskList);

            mTaskList.clear();
            mTaskList.addAll(tmpList);
            EvLog.d("after insertTask size=" + mTaskList.size());
            for (int i = 0; i < mTaskList.size(); i++) {
                EvLog.d("wait down:" + mTaskList.get(i).url);
            }
        }

        if (mCurrentDownItem != null) {
            synchronized (mCurrentDownItem) {
                EvLog.d("dump down " + mCurrentDownItem.localPath);
                mCurrentDownItem.valid = false;
            }
        }
    }

    public KmDownThread() {
        mReadBuffer = new byte[DOWN_THREAD_BUFFER_MAX_SIZE];
        mTaskList = new ArrayList<Task>();
        mErrorInfo = new ErrorInfo();
        if (DeviceConfigManager.getInstance().getPlayMode() == IDeviceConfig.PLAY_MODE_BUFFER_MEM_PLAY) {
            RingBuffer.getInstance().init(SystemConfigManager.RING_BUFFER_MAX_SIZE);
        }
//        mErrorInfo.errorType = DownError.ERROR_TYPE_DOWN_MEDIA_FAILED;
    }

    public void setNotifyHandler(Handler handler) {
        mHandler = handler;
    }

    public long getRunningTaskId() {
        long id = -1;

        //synchronized (mCurrentDownItem) {
        if (mCurrentDownItem != null) {
            id = mCurrentDownItem.id;
        }
        //}

        return id;
    }

    public void dumpTask(long id) {
        EvLog.e("dumpTask " + id);
        if (mCurrentDownItem != null) {
            synchronized (mCurrentDownItem) {
                if ((mCurrentDownItem != null) && (mCurrentDownItem.id == id)) {
                    mCurrentDownItem.valid = false;
                }
            }
        }

        synchronized (mTaskList) {
            int index = -1;
            for (int i = 0; i < mTaskList.size(); i++) {
                if (mTaskList.get(i).id == id) {
                    index = i;
                    break;
                }
            }

            if (index >= 0) {
                Log.d("KmDownThread", "down queue remove " + mTaskList.get(index).id);
                mTaskList.remove(index);
            }
        }
    }

    private Task getNextItem() {
        Task item = null;

        synchronized (this) {
            while (mTaskList.size() > 0) {
                item = mTaskList.get(0);
                mTaskList.remove(0);

                if (item.valid) {
                    break;
                } else {
                    item = null;
                }
            }
        }

        return item;
    }

    private void checkLocalEncryptedFile() {
        File file = new File(mCurrentDownItem.localPath);

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    FileUtil.grantWriteReadAccess(mCurrentDownItem.localPath);
                } else {
                    String msg = "create " + mCurrentDownItem.localPath + "failed.";
                    EvLog.e(msg);
                    UmengAgentUtil.reportError(msg);
                }
            } catch (IOException e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
                file = null;
            }
        }
    }

    // FIXME 逻辑错误(公播卡住)
    private File checkLocalFile(long contentLength) {
        File file = new File(mCurrentDownItem.localPath);
        if (file.exists()) {
            if (file.length() > contentLength) { //local file len is larger than net len
                EvLog.e(mCurrentDownItem.localPath + " local file.length=" + file.length() + "> net len=" + mCurrentDownItem.localPath);
                FileUtil.deleteFile(mCurrentDownItem.localPath);
            }
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
                file = null;
            }
        }

        return file;
    }

    public String getDownFileError(Task item, String errMsg) {
        return "[" + item.url + "],[" + errMsg + "]";
    }


    private int saveFile(Task item) throws IOException {
        EvLog.d("saveFile:" + item.url);
        if (DownError.debugDownThreadError(mErrorInfo)) {
            return mErrorInfo.errorCode;
        }

        long contentLength = item.fileTotalLen;
        if (contentLength <= 0) {
            EvLog.d("get contentLen by httpfile");
            HttpFile httpFile = new HttpFile();

            HttpOpenResult result = httpFile.openWithResult(item.url, 0, HTTP_CONNECT_MAX_RETRY_TIMES);
            if (result.ret != 0) {
                httpFile.close();
                mErrorInfo.errorCode = ERROR_CONNECT_FAILED;
                //补充responseCode
                if (result.ret == HttpFile.HTTP_ERROR_RESPONSECODE_INVALID) {
                    mErrorInfo.errorCodeSupplement = result.responseCode;//
                }
                mErrorInfo.errorMessage = getDownFileError(item, "http connect failed," + httpFile.getErrorMessage(result.ret));
                EvLog.e(mErrorInfo.errorMessage);
                return ERROR_CONNECT_FAILED;
            }

            contentLength = httpFile.getContentLength();
            if (contentLength <= 0) {
                httpFile.close();
                mErrorInfo.errorCode = ERROR_URL_CONTENTLEN_INVLAID;
                mErrorInfo.errorMessage = getDownFileError(item, "http getContentLength <= 0");
                EvLog.e(mErrorInfo.errorMessage);
                return ERROR_URL_CONTENTLEN_INVLAID;
            }
            httpFile.close();
        }


        File localFile = checkLocalFile(contentLength);
        if (localFile == null) {
            mErrorInfo.errorCode = ERROR_FILE_CREATE_FAILED;
            mErrorInfo.errorMessage = getDownFileError(item, "open local file failed:" + item.localPath);
            EvLog.e(mErrorInfo.errorMessage);
            return ERROR_FILE_CREATE_FAILED;
        }

        OutputStream os = null;
        try {
            os = new FileOutputStream(localFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            mErrorInfo.errorCode = ERROR_FILE_CREATE_FAILED;
            mErrorInfo.errorMessage = getDownFileError(item, "open local file failed:" + item.localPath);
            EvLog.e(mErrorInfo.errorMessage);
            return ERROR_FILE_CREATE_FAILED;
        }
        item.fileDownedSize = localFile.length();
        item.fileTotalLen = contentLength;
        EvLog.d("[" + item.url + "->>" + item.localPath + "],from pos:" + item.fileDownedSize + ",totalLen=" + item.fileTotalLen);

        if (item.fileDownedSize == item.fileTotalLen) {
            EvLog.e(item.localPath + " is complete,not need down");
            sendMessageWithLongData(DOWNLOAD_MSG_PROGRESS, item, 0);
            if (os != null) {
                os.close();
                os = null;
            }
            return ERROR_NONE;
        }

        int ret = ERROR_NONE;
        for (int retry = 0; retry < HTTP_CONNECT_MAX_RETRY_TIMES; retry++) {
            if (!mCurrentDownItem.valid) {
                EvLog.i(">>>>>>>>>>" + mCurrentDownItem.id + " is invalid");
                ret = ERROR_INVALID_ITEM;
                break;
            }

            try {
                //ret = writeFile(localFile, item);
                ret = writeToOutputStream(os, item);
                if (ret == ERROR_NONE) {
                    break;
                } else if (ret == ERROR_INVALID_ITEM) {
                    break;
                } else if (ret == ERROR_CONNECT_FAILED) {
                    //mErrorInfo已经在writeFile中设置过了
                    break;
                } else if (ret == ERROR_IO) {
                    mErrorInfo.errorCode = ERROR_IO;
                    mErrorInfo.errorMessage = getDownFileError(item, "catch IO error when write file:" + item.localPath);
                    FileUtil.deleteFile(item.localPath);
                    break;
                } else if (ret == ERROR_READ_TIMEOUT) {
                    EvLog.d(retry + " reconnect " + item.url);
                    mErrorInfo.errorCode = ERROR_READ_TIMEOUT;
                    mErrorInfo.errorMessage = getDownFileError(item, "http read timeout");
                    continue;
                } else if (ret == ERROR_PAUSE_WITH_CLOSEFILE) {
                    EvLog.d("recv pause with close file");
                    break;
                }
            } catch (FileNotFoundException e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
                mErrorInfo.errorCode = ERROR_IO;
                mErrorInfo.errorMessage = getDownFileError(item, "catch IO error when write file:" + item.localPath);
                ret = mErrorInfo.errorCode;
                break;
            } finally {

            }
        }

        if (os != null) {
            try {
//                long timeStart = System.currentTimeMillis();
                os.close();
//                EvLog.i("OutputStream close eclipse=" + (System.currentTimeMillis()-timeStart));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        EvLog.d(">>>>>>>>>>>>>>saveFile exit");
        return ret;
    }

    private int localRead(HttpFile file, byte[] buffer, int byteOffset, int byteCount) throws IOException {

        int totalReadNum = 0;
        int remainReadNum = byteCount;
        int startOffset = byteOffset;
        int singleRead = 0;

        int nRetryReadTime = 0;
        long readStartTime = System.currentTimeMillis();

        while (totalReadNum < byteCount) {
            if (!mCurrentDownItem.valid) {
                EvLog.i("localRead >>>>>>>>>>" + mCurrentDownItem.id + " is invalid,break");
                break;
            }

            singleRead = file.read(buffer, startOffset, remainReadNum);
            if (singleRead > 0) {
                totalReadNum += singleRead;
                if (totalReadNum >= byteCount) {
                    totalReadNum = byteCount;
                    break;
                } else {
                    if ((System.currentTimeMillis() - readStartTime) > 1000) {
                        EvLog.e("localRead eclipse time >= 1000,speed=" + (singleRead) / (System.currentTimeMillis() - readStartTime) + "byte/s");
                        break;
                    }
                    startOffset = totalReadNum;
                    remainReadNum = byteCount - startOffset;
                }
            } else {
                nRetryReadTime++;
                EvLog.e("KmDownThread localRead failed times = " + nRetryReadTime + "startOffset = "
                        + startOffset + ", readcount " + remainReadNum);

                if (nRetryReadTime > 3) {
                    break;
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        EvLog.e(e.getMessage());
                        UmengAgentUtil.reportError(e);
                    }
                }
            }
        }
        return totalReadNum;
    }

    private int writeToBuffer(Task item) {
        int count = 0;
        int error = ERROR_NONE;

        HttpFile httpFile = new HttpFile();
        //视频访问不需要经过代理
        httpFile.setUseProxy(false);
       /* httpFile.setReadTimeout(6*1000);
        httpFile.setConnectTimeout(6*1000);*/

        HttpOpenResult result = httpFile.openWithResult(item.url, item.fileDownedSize, 1);
        if (result.ret != 0) {
            mErrorInfo.errorCode = ERROR_CONNECT_FAILED;
            mErrorInfo.errorMessage = getDownFileError(item, "http connect failed," + httpFile.getErrorMessage(result.ret));
            if (result.ret == HttpFile.HTTP_ERROR_RESPONSECODE_INVALID) {
                mErrorInfo.errorCodeSupplement = result.responseCode;
            }
            return ERROR_CONNECT_FAILED;
        }

        long eclipse = 0;
        long startDownSize = item.fileDownedSize;
        float speedInterval = 0.0f;
        long prevWriteCount = 0;
        long startDownTime = System.currentTimeMillis();
        long prevSendTime = System.currentTimeMillis();
        long timeInterval = 0;

        int needReadSize = 0;

        EvLog.d("begin to HttpFileLink :" + item.url);
        while (item.fileDownedSize < item.fileTotalLen) {
            if (mState == STATE_PAUSE) {
                error = ERROR_PAUSE_WITH_CLOSEFILE;
                break;
            }

            try {
//                count = httpFile.read(mReadBuffer);
                if (item.fileDownedSize + mReadBuffer.length < item.fileTotalLen) {
                    needReadSize = mReadBuffer.length;
                } else {
                    needReadSize = (int) (item.fileTotalLen - item.fileDownedSize);
                    EvLog.d("read tail needReadSize=" + needReadSize);
                }
                count = localRead(httpFile, mReadBuffer, 0, needReadSize);
//                EvLog.d("download", "read " + count);
            } catch (IOException e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
                error = ERROR_READ_TIMEOUT;
                break;
            }

            if (count > 0) {
                int write = RingBuffer.getInstance().write(mReadBuffer, 0, count);
                int alreadyWrited = write;
                while (alreadyWrited != count) {
                    EvLog.e("alreadyWrited:" + alreadyWrited + ",singleRead=" + count);
                    while (RingBuffer.getInstance().getFreeSpace() == 0) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                        }
                        if (!mCurrentDownItem.valid) {
                            EvLog.i("writeToBuffer >>>>>>>>>>" + mCurrentDownItem.id + " is invalid");
                            error = ERROR_INVALID_ITEM;
                            break;
                        }
                    }
                    if (!mCurrentDownItem.valid) {
                        EvLog.i("writeToBuffer >>>>>>>>>>" + mCurrentDownItem.id + " is invalid");
                        error = ERROR_INVALID_ITEM;
                        break;
                    }
                    write = RingBuffer.getInstance().write(mReadBuffer, alreadyWrited, count - alreadyWrited);
                    alreadyWrited += write;
                }
            } else if (count == 0) {
                // continue;
                EvLog.i("read count 0 ,send ERROR_READ_TIMEOUT");
                error = ERROR_READ_TIMEOUT;
                break;
            } else {
                error = ERROR_IO;
                break;
            }

            if (!mCurrentDownItem.valid) {
                EvLog.i("writeToBuffer >>>>>>>>>>" + mCurrentDownItem.id + " is invalid");
                error = ERROR_INVALID_ITEM;
                break;
            }

            if (count > 0) {
                item.fileDownedSize += count;
                prevWriteCount += count;
                timeInterval = System.currentTimeMillis() - prevSendTime;
//                EvLog.d("download", "download " + item.fileDownedSize + ",timeInterval=" + timeInterval);
                if (timeInterval >= 500) {
                    speedInterval = prevWriteCount * 1000 / timeInterval;
//                     EvLog.d("download", "speed " + speedInterval);
                    sendMessageWithLongData(DOWNLOAD_MSG_PROGRESS, item, speedInterval);
                    prevSendTime = System.currentTimeMillis();
                    prevWriteCount = 0;
                }
            }
        }

        Message msg = mHandler.obtainMessage();
        msg.what = MSG_CLOSE_HTTP_FILE;
        msg.obj = httpFile;
        mHandler.sendMessage(msg);

        if (error == ERROR_NONE && mHandler != null) {
            eclipse = (System.currentTimeMillis() - startDownTime);
            if (eclipse == 0) {
                eclipse = 1;
            }
            long speed = (item.fileTotalLen - startDownSize) * 1000 / eclipse;
            sendMessageWithLongData(DOWNLOAD_MSG_PROGRESS, item, speed);
        }

        return error;
    }

    private int writeToOutputStream(OutputStream os, Task item) throws FileNotFoundException {
        int count = 0;
        int error = ERROR_NONE;

        HttpFile httpFile = new HttpFile();
        //视频访问不需要经过代理
        httpFile.setUseProxy(false);
      /*  httpFile.setReadTimeout(8*1000);
        httpFile.setConnectTimeout(8*1000);*/

        HttpOpenResult result = httpFile.openWithResult(item.url, item.fileDownedSize, 1);
        if (result.ret != 0) {
            mErrorInfo.errorCode = ERROR_CONNECT_FAILED;
            mErrorInfo.errorMessage = getDownFileError(item, "http connect failed," + httpFile.getErrorMessage(result.ret));
            if (result.ret == HttpFile.HTTP_ERROR_RESPONSECODE_INVALID) {
                mErrorInfo.errorCodeSupplement = result.responseCode;
            }
            return ERROR_CONNECT_FAILED;
        }

        if (item.fileTotalLen != httpFile.getContentLength()) {
            String wrongMsg = item.url + ",dc give wrong fileSize:" + item.fileTotalLen + ",real size=" + httpFile.getContentLength() + ",start down from" + item.fileDownedSize;
            UmengAgentUtil.reportError(wrongMsg);
            EvLog.e(wrongMsg);
        } else {
            EvLog.i(item.url + " , getContentLength:" + httpFile.getContentLength());
        }

        // FileOutputStream os = new FileOutputStream(local, true);

        long eclipse = 0;
        long startDownSize = item.fileDownedSize;
        float speedInterval = 0.0f;
        long prevWriteCount = 0;
        long startDownTime = System.currentTimeMillis();
        long prevSendTime = System.currentTimeMillis();
        long timeInterval = 0;

        int needReadSize = 0;

        EvLog.d("begin to HttpFileLink :" + item.url);
        while (item.fileDownedSize < item.fileTotalLen) {
            if (mState == STATE_PAUSE) {
                error = ERROR_PAUSE_WITH_CLOSEFILE;
                break;
            }

            try {
//                count = httpFile.read(mReadBuffer);
                if (item.fileDownedSize + mReadBuffer.length < item.fileTotalLen) {
                    needReadSize = mReadBuffer.length;
                } else {
                    needReadSize = (int) (item.fileTotalLen - item.fileDownedSize);
                    EvLog.d("read tail needReadSize=" + needReadSize);
                }
                count = localRead(httpFile, mReadBuffer, 0, needReadSize);
//                EvLog.d("download", "read " + count);
            } catch (IOException e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
                error = ERROR_READ_TIMEOUT;
                break;
            }

            if (count > 0) {
                try {
                    //EvLog.d("download", "write " + count);
                    os.write(mReadBuffer, 0, count);
                } catch (IOException e) {
                    EvLog.e(e.getMessage());
                    UmengAgentUtil.reportError(e);
                    error = ERROR_IO;
                    break;
                }
            } else if (count == 0) {
                // continue;
                EvLog.i("read count 0 ,send ERROR_READ_TIMEOUT");
                error = ERROR_READ_TIMEOUT;
                break;
            } else {
                error = ERROR_IO;
                break;
            }

            if (!mCurrentDownItem.valid) {
                EvLog.i("writeToOutputStream >>>>>>>>>>" + mCurrentDownItem.id + " is invalid");
                error = ERROR_INVALID_ITEM;
                break;
            }

            if (count > 0) {
                item.fileDownedSize += count;
                prevWriteCount += count;
                timeInterval = System.currentTimeMillis() - prevSendTime;
//                EvLog.d("download", "download " + item.fileDownedSize + ",timeInterval=" + timeInterval);
                if (timeInterval >= 1000) {
                    speedInterval = prevWriteCount * 1000 / timeInterval;
//                     EvLog.d("download", "speed " + speedInterval);
                    sendMessageWithLongData(DOWNLOAD_MSG_PROGRESS, item, speedInterval);
                    prevSendTime = System.currentTimeMillis();
                    prevWriteCount = 0;
                }
            }
        }

        try {
            long timeStart = System.currentTimeMillis();
            Message msg = mHandler.obtainMessage();
            msg.what = MSG_CLOSE_HTTP_FILE;
            msg.obj = httpFile;
            mHandler.sendMessage(msg);

            EvLog.d("httpFile close=" + (System.currentTimeMillis() - timeStart));
            timeStart = System.currentTimeMillis();
            os.flush();
            EvLog.d("os.flush=" + (System.currentTimeMillis() - timeStart));
            //os.close();
        } catch (IOException e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        }

        if (error == ERROR_NONE && mHandler != null) {
            eclipse = (System.currentTimeMillis() - startDownTime);
            if (eclipse == 0) {
                eclipse = 1;
            }
            long speed = (item.fileTotalLen - startDownSize) * 1000 / eclipse;
            sendMessageWithLongData(DOWNLOAD_MSG_PROGRESS, item, speed);
        }

        return error;
    }
    
   /* public void setStop() {
        mExit = true;
        if (mCurrentDownItem != null) {
            mCurrentDownItem.valid = false;
        }
    }*/

    @Override
    public void run() {
        this.setName("KmDownThread");

        Task item = null;
//        mExit = false;

        while (!isInterrupted()) {
            if (mState == STATE_PAUSE) {
                EvLog.d("KmDownThread mState is STATE_PAUSE");
                SystemClock.sleep(3000);
                continue;
            } else if (mState == STATE_NORMAL) {
                item = getNextItem();
//                mCurrentDownItem = item;
            } else if (mState == STATE_RESUME) {
                EvLog.d("KmDownThread mState is STATE_RESUME,mCurrentDownItem=" + mCurrentDownItem);
                mState = STATE_NORMAL;
            } else {
                EvLog.d("KmDownThread mState is invalid," + mState);
            }

            if (item != null) {
                mCurrentDownItem = item;
                int error = 0;
                if (DeviceConfigManager.getInstance().getPlayMode() == IDeviceConfig.PLAY_MODE_BUFFER_MEM_PLAY) {
                    error = saveBuffer(item);
                } else {
                    try {
                        error = saveFile(item);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (error == ERROR_INVALID_ITEM) {
                    EvLog.i(">>>>>>>>>>> dump down task" + item.localPath);
                } else if (error == ERROR_PAUSE_WITH_CLOSEFILE) {
                } else if (error != ERROR_NONE) {
                    mErrorInfo.errorCode = error;
                    sendMessage(DOWNLOAD_MSG_ERROR, mErrorInfo, item);
                } else {
                    // EvLog.d("KmDownThread" ,"ERROR_NONE");
                }
            }
            SystemClock.sleep(200);
            continue;
        }
        EvLog.e(">>>>>>>>>>>>>>>>>>>KmDownthread exit");
    }


    private int saveBuffer(Task item) {
        EvLog.d("saveBuffer:" + item.url);
        RingBuffer.getInstance().reset();

        long contentLength = item.fileTotalLen;
        if (contentLength <= 0) {
            EvLog.d("get contentLen by httpfile");
            HttpFile httpFile = new HttpFile();

            HttpOpenResult result = httpFile.openWithResult(item.url, 0, HTTP_CONNECT_MAX_RETRY_TIMES);
            if (result.ret != 0) {
                httpFile.close();
                mErrorInfo.errorCode = ERROR_CONNECT_FAILED;
                //补充responseCode
                if (result.ret == HttpFile.HTTP_ERROR_RESPONSECODE_INVALID) {
                    mErrorInfo.errorCodeSupplement = result.responseCode;//
                }
                mErrorInfo.errorMessage = getDownFileError(item, "http connect failed," + httpFile.getErrorMessage(result.ret));
                EvLog.e(mErrorInfo.errorMessage);
                return ERROR_CONNECT_FAILED;
            }

            contentLength = httpFile.getContentLength();
            if (contentLength <= 0) {
                httpFile.close();
                mErrorInfo.errorCode = ERROR_URL_CONTENTLEN_INVLAID;
                mErrorInfo.errorMessage = getDownFileError(item, "http getContentLength <= 0");
                EvLog.e(mErrorInfo.errorMessage);
                return ERROR_URL_CONTENTLEN_INVLAID;
            }
            httpFile.close();
        }

        item.fileDownedSize = 0;
        item.fileTotalLen = contentLength;
        EvLog.d("[" + item.url + "->> buffer]" + ",totalLen=" + item.fileTotalLen);

        int ret = ERROR_NONE;
        for (int retry = 0; retry < HTTP_CONNECT_MAX_RETRY_TIMES; retry++) {
            if (!mCurrentDownItem.valid) {
                EvLog.i(">>>>>>>>>>" + mCurrentDownItem.id + " is invalid");
                ret = ERROR_INVALID_ITEM;
                break;
            }

            ret = writeToBuffer(item);
            if (ret == ERROR_NONE) {
                break;
            } else if (ret == ERROR_INVALID_ITEM) {
                break;
            } else if (ret == ERROR_CONNECT_FAILED) {
                //mErrorInfo已经在writeFile中设置过了
                break;
            } else if (ret == ERROR_IO) {
                mErrorInfo.errorCode = ERROR_IO;
                mErrorInfo.errorMessage = getDownFileError(item, "catch IO error when write file:" + item.localPath);
                FileUtil.deleteFile(item.localPath);
                break;
            } else if (ret == ERROR_READ_TIMEOUT) {
                EvLog.d(retry + " reconnect " + item.url);
                mErrorInfo.errorCode = ERROR_READ_TIMEOUT;
                mErrorInfo.errorMessage = getDownFileError(item, "http read timeout");
                continue;
            } else if (ret == ERROR_PAUSE_WITH_CLOSEFILE) {
                EvLog.d("recv pause with close file");
                break;
            }
        }

        EvLog.d(">>>>>>>>>>>>>>saveBuffer exit");
        return ret;
    }


    private void sendMessageWithLongData(int what, Object obj, float speed) {
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage(what, obj);
            msg.getData().putFloat("speed", speed);
            if (mStop) {
                EvLog.i("sendMessageWithLongData dump,because thread is stop");
                return;
            }
            mHandler.sendMessage(msg);
        }
    }

    private void sendMessage(int what, ErrorInfo errorInfo,/*int arg1,int arg2,String message,*/Object obj) {
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage(what, obj);
            msg.arg1 = errorInfo.errorCode;
            msg.arg2 = errorInfo.errorCodeSupplement;
            msg.getData().putString("errmsg", errorInfo.errorMessage);
            if (mStop) {
                EvLog.i("sendMessage dump,because thread is stop");
                return;
            }
            mHandler.sendMessage(msg);
        }
    }
}