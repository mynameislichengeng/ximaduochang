/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年2月14日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.tftpserver;

import java.io.File;
import java.io.IOException;

import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;



/**
 * [功能说明]
 */
public class TftpService {
    /*private static TftpServiceManager instance = null;
    public static TftpServiceManager getInstance() {
        if(instance == null) {
            synchronized (TftpServiceManager.class) {
                TftpServiceManager temp = instance;
                if(temp == null) {
                  temp = new TftpServiceManager();
                  instance = temp;
                }
            }
         }
         return instance;
    }*/
  
    private static final String TAG = "TftpServer";
    /** [TftpServer命令] */
    public static final String TFTPSERVER_COMMAND = "TFTP__COMMAND";
    /** [TftpServer启动命令] */
    public static final int TFTPSERVER_START = 1;
    /** [TftpServer停止命令] */
    public static final int TFTPSERVER_STOP = 2;
    /** [TftpServer的服务端口号] */
    public static final int TFTPSERVER_PORT = 9500;
    /** [Socket的超时时间] */
    public static final int SOCKET_TIMEOUT = 5000;
    /** [声明一个TftpServer服务] */
    private TftpServer mTftpServer;
    /** [声明一个File文件] */
    private File mOpenFile = null;
    
/*    private HandlerThread mHandlerThread = null;
    private volatile WorkHandler mWorkHandler = null;*/
    
    public TftpService() {
        
    }
    public void start() {
        
        if (mOpenFile == null) {
            String tftpPath = ResourceSaverPathManager.getInstance().getTftpSavePath();
            if (!FileUtil.isFileExist(tftpPath)) {
                EvLog.d(TAG, tftpPath + " is not exist");
                return;
            }
            mOpenFile = new File(tftpPath);
        }
       
        /*if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread("TftpServerService");
            mHandlerThread.start();
            mWorkHandler = new WorkHandler(mHandlerThread.getLooper());
        }*/
        
        EvLog.d(TAG, "TFTPServerService start finish");
        startTftpServer();
    }
    
    private void startTftpServer() {
        if (mTftpServer != null) {
            shutDownTftpServer();
        }
        try {
            shutDownTftpServer();
            mTftpServer = new TftpServer(mOpenFile, mOpenFile, TFTPSERVER_PORT, TftpServer.GET_AND_PUT, null, null);
            mTftpServer.setSocketTimeout(SOCKET_TIMEOUT);
            EvLog.d(TAG, "TFTPServer start port-->" + TFTPSERVER_PORT);
        } catch (IOException e) {
            EvLog.d(TAG, "TFTPServer error !!!");
        }
    }
    
    private void shutDownTftpServer() {
        if (mTftpServer != null) {
            mTftpServer.shutdown();
            mTftpServer = null;      
        }
    }
    
    public void stop() {
        shutDownTftpServer(); 
        
       /* if (mHandlerThread != null) {
            mHandlerThread.getLooper().quit();
            mHandlerThread = null;
        }
        mWorkHandler = null;*/
    }
    
    /*private final class WorkHandler extends Handler {
        public WorkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            int command = msg.what;
            switch (command) {
                case TFTPSERVER_START:
                    startTftpServer();
                    break;
                case TFTPSERVER_STOP:
                    shutDownTftpServer();
                    break;
                default:
                    break;
            }
        }
    }*/
}
