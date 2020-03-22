package com.evideo.kmbox.model.down;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.evideo.kmbox.model.device.DeviceConfig;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.device.DeviceName;
import com.evideo.kmbox.model.down.KmDownThread.Task;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.HttpFile;

public class KmSongDownManager {
    private HandlerThread mDownHandlerThread = null;
    private DownMessageHandler mDownMessageHandler = null; 
            
    private CloseHttpFileThread mCloseHttpFileThread = null;
    
    private KmDownThread mKmDownThread = null;
    private boolean mIsInit =false;
    public  static boolean instanceFlag = false; // true if 1 instance
    private static KmSongDownManager instance = null;
    
    public static final int TASK_TYPE_INVALID = -1;
    public static final int TASK_TYPE_PLAYLIST = 1;
    public static final int TASK_TYPE_PLAYBACKLIST = 2;
    
    public interface IDownMsgListener{
        public void onMessageReciver(Task task,Message msg) ;
    }
    
    private List<IDownMsgListener> mListeners = new ArrayList<IDownMsgListener>();
//    private List<Task> mTaskList = new ArrayList<Task>();
    
    public static KmSongDownManager getInstance() {
        if (!instanceFlag) {
            instanceFlag = true;
            instance = new KmSongDownManager();

            return instance;
        }
        return instance;
    }

    private KmSongDownManager() {
        mKmDownThread = new KmDownThread();
    }
    
    /**
     * [功能说明] 设置线程优先级，值越大，优先级越高
     * @param priority
     */
    public void setThreadPriority(int priority) {
        if (mKmDownThread != null) {
            if (mKmDownThread.getPriority() != priority) {
                EvLog.i("set down thread priority:" + priority);
                mKmDownThread.setPriority(priority);
            }
        }
    }
    
    public boolean init() {
        if (mIsInit) {
            return true;
        }
        mDownHandlerThread = new HandlerThread("DownMessageHandler");
        mDownHandlerThread.start();
        mDownMessageHandler = new DownMessageHandler(mDownHandlerThread.getLooper());
        
        mKmDownThread.setNotifyHandler(mDownMessageHandler);
        mKmDownThread.start();
        
        mIsInit = true;
        return true;
    }

    public void uninit() {
        if ( mIsInit == false ) {
            EvLog.w("uninit", " KmSongDownManager is not init" );
            return;
        }
        EvLog.d("begin to stop down thread");
        
        if (mKmDownThread != null) {
          /*  mKmDownThread.setStop();
           try {
               mKmDownThread.join();
           } catch (InterruptedException e) {
               EvLog.e(e.getMessage());
               UmengAgentUtil.reportError(e);
           }*/
           mKmDownThread.setStop();
           mKmDownThread.interrupt();
           mKmDownThread = null;
       }
        
        if (mCloseHttpFileThread != null) {
            mCloseHttpFileThread.interrupt();
            mCloseHttpFileThread = null;
        }
        EvLog.d("begin to stop handlerThread");
        if (mDownHandlerThread != null) {
            mDownHandlerThread.getLooper().quit();
            mDownHandlerThread = null;
        }
        mDownMessageHandler = null;
        mIsInit = false;
    }

    /**
     * @brief : [获取正在下载任务的downID]
     * @return
     */
    public long getDowningID() {
        return mKmDownThread.getRunningTaskId();
    }

    public void cancelCurrentDown() {
        long runningTaskId = mKmDownThread.getRunningTaskId();
        if (runningTaskId != -1) {
            mKmDownThread.dumpTask(runningTaskId);
        }
       /* for(Task item : mTaskList) {
            if (item.id == runningTaskId) {
                item.valid = false;
            }
        }*/
    }
    
    public void pauseDown() {
        if (mKmDownThread != null) {
            EvLog.d("pauseDown------------");
            mKmDownThread.pauseDown();
        }
    }
    
    public void resumeDown() {
        if (mKmDownThread != null) {
            EvLog.d("resumeDown------------");
            mKmDownThread.resumeDown();
        }
    }
    
   /* public void cancleDown(long taskId) {
        long runningTaskId = mKmDownThread.getRunningTaskId();
        if ( (runningTaskId != -1 ) &&  (runningTaskId == taskId) ) {
            mKmDownThread.dumpTask(runningTaskId);
        }
        remove(taskId);
    }*/
    
    public void registerListener(/*int type,*/IDownMsgListener listener) {
        mListeners.add(listener);
    }
    
    public void unregisterListener(IDownMsgListener listener) {
        mListeners.remove(listener);
    }
    
    /*public boolean isTaskDowning(int serialNum) {
        for (KmDownItem item : mTaskList) {
            if (item.serialNum == serialNum) {
                return true;
            }
        }
        return false;
    }*/
    
    public void addTask(Task task) {
        if (mKmDownThread != null) {
            EvLog.d("downthread,add task:" + task.url);
//            mTaskList.add(task);
            mKmDownThread.addTask(task);
        }
    }
    
    public void insertTask(Task downItem) {
        if (mKmDownThread != null) {
           /* List<Task> tmpList = new ArrayList<Task>();
            tmpList.addAll(mTaskList);
            
            mTaskList.clear();
            mTaskList.add(downItem);
            mTaskList.addAll(tmpList);*/
            
            mKmDownThread.insertTask(downItem);
        }
    }
    
    public void delTask(long taskId) {
        if (mKmDownThread != null) {
            mKmDownThread.dumpTask(taskId);
        }
        /*for (Task task : mTaskList) {
            if (task.id == taskId) {
                EvLog.d("delTask taskId:" + taskId);
                task.valid = false;
                mKmDownThread.dumpTask(task.id);
            }
        }*/
    }
    
   /* private void remove(long taskId) {
        for (int i = 0; i< mTaskList.size();i++) {
            if (mTaskList.get(i).id == taskId) {
                mTaskList.remove(i);
                break;
            }
        }
        return;
    }
    */
    private void notifyListener(Message message,Task item) {
        for (int i = 0 ;i < mListeners.size();i++) {
            mListeners.get(i).onMessageReciver(item, message);
        }
    }
    
    public class DownMessageHandler extends Handler{
        public static final int MSG_QUIT = 0;
        
        
        public DownMessageHandler(Looper looper) {
            super(looper);
        }
        
        @Override
        public void handleMessage(Message msg) {
            if (msg == null) {
                return;
            }
            
            if (msg.what == 0) {
                super.getLooper().quit();
                return;
            } 
            
            if (msg.what == KmDownThread.MSG_CLOSE_HTTP_FILE) {
                if (mCloseHttpFileThread != null) {
                    HttpFile file = (HttpFile)msg.obj;
                    mCloseHttpFileThread.addHttpFile(file);
                }
                return;
            }
            
            Task task = (Task) msg.obj;
            if ( task == null ) {
                return ;
            }
            notifyListener(msg, task);
            return;
        }
    }
    
    public class CloseHttpFileThread extends Thread {

        private ArrayList<HttpFile> mArrays = new ArrayList<HttpFile>();
        public void addHttpFile(HttpFile file) {
            if (file == null) {
                return;
            }
            EvLog.e("CloseHttpFileThread : recv add HttpFileLink:" + file.getUrl());
            synchronized (mArrays) {
                mArrays.add(file);
            }
        }
        @Override
        public void run() {
            while (true) {
                if (mArrays.size() > 0) {
                    HttpFile file = null;
                    synchronized (mArrays) {
                        file = mArrays.get(0);
                        mArrays.remove(0);
                    }
                    if (file != null) {
                        long timeStart = System.currentTimeMillis();
                        EvLog.i("CloseHttpFileThread :begin close HttpFileLink:" + file.getUrl());
                        file.close();
                        EvLog.i("CloseHttpFileThread :end to close HttpFileLink:" + file.getUrl() + ",eclipse=" + (System.currentTimeMillis()-timeStart));
                    }
                }
                try {
                    Thread.sleep(3*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}