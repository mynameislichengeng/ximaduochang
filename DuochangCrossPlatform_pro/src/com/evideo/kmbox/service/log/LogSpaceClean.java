package com.evideo.kmbox.service.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.os.HandlerThread;
import android.text.TextUtils;

import com.evideo.kmbox.KmApplication;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;
import com.evideo.kmbox.util.RepeatTimerTask;
import com.evideo.kmbox.util.RepeatTimerTask.IActionCallback;

public class LogSpaceClean {
    
    private static final String TAG = "LogSpaceClean";
    
    private static final int MAX_FILE_NUM_KEEP = 2;//当大小超过最大值时本地保存的文件个数
    // 日志文件最大值，3M
    private static final int LOG_FILE_MAX_SIZE = /*3 * 1024*/512 * 1024; 
    // log日志目录最大值，5M
    private static final long LOG_DIRECTORY_MAX_SIZE = 5 *1024 * 1024; 
    // 内存中的日志文件大小监控时间间隔，10分钟
    private static final int MEMORY_LOG_FILE_MONITOR_INTERVAL = 5 * 60 * 1000; 
    //// sd卡中日志文件的最多保存天数
    private static final int SDCARD_LOG_FILE_SAVE_DAYS = 2; 

    public static final String LOG_ZIP_NAME = "log";
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss");// 日志名称格式
    
    private RepeatTimerTask mMemoryMonitorTask;

    private Process mProcess;
    private String mCurrentLogFileName;
    
    public void start(){  
        prepareCreateNewLogFile();
    }  
    
    public void destroy() {
        EvLog.e(TAG,"onDestroy------");
        if( mMemoryMonitorTask != null) {
            mMemoryMonitorTask.stop();
            mMemoryMonitorTask = null;
        }
        
        if(mProcess != null) {
            mProcess.destroy();
        }
        if (mThread != null) {
//            mThread.getLooper().quit();
            mThread = null;
        }
    }
    
    private HandlerThread mThread = null;
    private PrepareLogPrestener mPrepareLogPresenter = null;
    
    private void prepareCreateNewLogFile() {
        if (mPrepareLogPresenter != null) {
            mPrepareLogPresenter.cancel();
            mPrepareLogPresenter = null;
        }
        EvLog.i(TAG,"prepare to create new log file-----");
        mPrepareLogPresenter = new PrepareLogPrestener();
        mPrepareLogPresenter.start();
    }
    
    private void startMonitorLog() {
        if (mThread != null && mMemoryMonitorTask != null) {
            EvLog.i(TAG,"repeat call startMonitorLog");
            return;
        }
        
        if (mThread == null) {
            mThread = new HandlerThread("collect_log_thread");
            mThread.start();
        }
        if(mMemoryMonitorTask == null) {
            
            mMemoryMonitorTask = new RepeatTimerTask(new IActionCallback() {
                @Override
                public void stop() {
                }
                
                @Override
                public void start() {
                }
                
                @Override
                public void repeat() {
                    checkLogSize();
                }
            },mThread.getLooper());
            mMemoryMonitorTask.scheduleAtFixedRate(MEMORY_LOG_FILE_MONITOR_INTERVAL);
        }
        EvLog.i(TAG,"first call startMonitorLog");
    }
    
    /**
     * 每次记录日志之前先清除日志的缓存, 不然会在两个日志文件中记录重复的日志
     */
    private void clearLogCache() {
        Process proc = null;
        List<String> commandList = new ArrayList<String>();
        commandList.add("logcat");
        commandList.add("-c");
        try {
            proc = Runtime.getRuntime().exec(
                    commandList.toArray(new String[commandList.size()]));
            /*if (proc.waitFor() != 0) {
                EvLog.e(TAG, " clearLogCache proc.waitFor() != 0");
//                recordLogServiceLog("clearLogCache clearLogCache proc.waitFor() != 0");
            }*/
        } catch (Exception e) {
            EvLog.e(TAG, "clearLogCache failed" + e.getMessage());
            UmengAgentUtil.reportError(e);
        } finally {
            try {
                proc.destroy();
            } catch (Exception e) {
                EvLog.e(TAG, "clearLogCache failed" + e.getMessage());
                UmengAgentUtil.reportError(e);
            }
        }
    }
    
    /**
     * 获取本程序的用户名称
     * 
     * @param packName
     * @param allProcList
     * @return
     */
    private String getAppUser(String packName, List<ProcessInfo> allProcList) {
        for (ProcessInfo processInfo : allProcList) {
            if (processInfo.name.equals(packName)) {
                return processInfo.user;
            }
        }
        return null;
    }

    /**
     * 根据ps命令得到的内容获取PID，User，name等信息
     * 
     * @param orgProcessList
     * @return
     */
    private List<ProcessInfo> getProcessInfoList(List<String> orgProcessList) {
        List<ProcessInfo> procInfoList = new ArrayList<ProcessInfo>();
        for (int i = 1; i < orgProcessList.size(); i++) {
            String processInfo = orgProcessList.get(i);
            String[] proStr = processInfo.split(" ");
            // USER PID PPID VSIZE RSS WCHAN PC NAME
            // root 1 0 416 300 c00d4b28 0000cd5c S /init
            List<String> orgInfo = new ArrayList<String>();
            for (String str : proStr) {
                if (!"".equals(str)) {
                    orgInfo.add(str);
                }
            }

            if (orgInfo.size() == 9) {
                ProcessInfo pInfo = new ProcessInfo();
                pInfo.user = orgInfo.get(0);
                pInfo.pid = orgInfo.get(1);
                pInfo.ppid = orgInfo.get(2);
                pInfo.name = orgInfo.get(8);
                procInfoList.add(pInfo);
            }
        }
        return procInfoList;
    }
    
    /**
     * 运行PS命令得到进程信息
     * 
     * @return USER PID PPID VSIZE RSS WCHAN PC NAME root 1 0 416 300 c00d4b28
     *         0000cd5c S /init
     */
    private List<String> getAllProcess() {
        List<String> orgProcList = new ArrayList<String>();
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec("ps");
//            StreamConsumer errorConsumer = new StreamConsumer(
//                    proc.getErrorStream());
//
            StreamConsumer outputConsumer = new StreamConsumer(
                    proc.getInputStream(), orgProcList);
            
//
//            errorConsumer.start();
            outputConsumer.run();
            if (proc.waitFor() != 0) {
                EvLog.e(TAG, "getAllProcess proc.waitFor() != 0");
                // recordLogServiceLog("getAllProcess proc.waitFor() != 0");
            }
        } catch (Exception e) {
            EvLog.e(TAG, "getAllProcess failed" + e.getMessage());
            UmengAgentUtil.reportError(e);
            // recordLogServiceLog("getAllProcess failed");
        } finally {
            try {
                proc.destroy();
            } catch (Exception e) {
                EvLog.e(TAG, "getAllProcess failed" + e.getMessage());
                UmengAgentUtil.reportError(e);
                // recordLogServiceLog("getAllProcess failed");
            }
        }
        return orgProcList;
    }
    
    /**
     * 关闭由本程序开启的logcat进程： 根据用户名称杀死进程(如果是本程序进程开启的Logcat收集进程那么两者的USER一致)
     * 如果不关闭会有多个进程读取logcat日志缓存信息写入日志文件
     * 
     * @param allProcList
     * @return
     */
    private void killLogcatProc(List<ProcessInfo> allProcList) {
        if (mProcess != null) {
            mProcess.destroy();
        }
        
        String packName = KmApplication.getInstance().getPackageName();
        String myUser = getAppUser(packName, allProcList);
        /*
         * recordLogServiceLog("app user is:"+myUser);
         * recordLogServiceLog("========================"); for (ProcessInfo
         * processInfo : allProcList) {
         * recordLogServiceLog(processInfo.toString()); }
         * recordLogServiceLog("========================");
         */
        for (ProcessInfo processInfo : allProcList) {
            if (processInfo.name.toLowerCase().equals("logcat")
                    && processInfo.user.equals(myUser)) {
                android.os.Process.killProcess(Integer
                        .parseInt(processInfo.pid));
                // recordLogServiceLog("kill another logcat process success,the process info is:"
                // + processInfo);
            }
        }
    }
    
    /**
     * 开始收集日志信息
     */
    private void createLogCollector() {
        List<String> commandList = new ArrayList<String>();
        commandList.add("logcat");
        commandList.add("-f");
        commandList.add(getLogPath());
        commandList.add("-v");
        commandList.add("time");
        try {
            mProcess = Runtime.getRuntime().exec(
                    commandList.toArray(new String[commandList.size()]));
        } catch (Exception e) {
            EvLog.e(TAG, "CollectorThread == >" + e.getMessage());
            UmengAgentUtil.reportError(e);
        }
    }
    
    /**
     * 根据当前的存储位置得到日志的绝对存储路径
     * 
     * @return
     */
    private String getLogPath() {
        createLogDir();
        String logFileName = "";
        
        synchronized (sdf) {
            logFileName = sdf.format(new Date()) + ".log";// 日志文件名称
        }
        
        mCurrentLogFileName = logFileName;
        return ResourceSaverPathManager.getInstance().getLogSavePath() + File.separator + logFileName;
    }
    
    /**
     * 检查日志文件大小是否超过了规定大小 如果超过了重新开启一个日志收集进程
     */
    private void checkLogSize() {
        String logDir = ResourceSaverPathManager.getInstance().getLogSavePath();
        long dirSzie = FileUtil.countLength(logDir);
        EvLog.d(TAG,"checkLogSize dirSzie:" + dirSzie);
        
        //判断日志目录大小是否超过最大值
        if(dirSzie > LOG_DIRECTORY_MAX_SIZE) {
            EvLog.d(TAG,"dir size is bigger > " + LOG_DIRECTORY_MAX_SIZE);
            deleteOverflowLog();
            prepareCreateNewLogFile();
            return;
        }
        if(!TextUtils.isEmpty(mCurrentLogFileName)) {
            String path = logDir + File.separator + mCurrentLogFileName;
            File file = new File(path);
            if(file.exists()) {
                //
                if (file.length() >= LOG_FILE_MAX_SIZE) {
                    EvLog.d(TAG, path + " file len > " + LOG_FILE_MAX_SIZE + ",create new log");
                    prepareCreateNewLogFile();
                } else if (hasPassOneDay(getFileNameWithoutExtension(mCurrentLogFileName))) {
                    EvLog.d(TAG, "new day is come,create new log file");
                    prepareCreateNewLogFile();
                }
            }
            file = null;
        }
        return;
    }
    
    class PrepareLogPrestener extends AsyncPresenter<Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) throws Exception {
            try {
                clearLogCache();
                List<String> orgProcessList = getAllProcess();
                List<ProcessInfo> processInfoList = getProcessInfoList(orgProcessList);
                killLogcatProc(processInfoList);
                createLogCollector();
                Thread.sleep(1000);// 休眠，创建文件，然后处理文件，不然该文件还没创建，会影响文件删除
//                checkLogSizeAndDate();
//                handleLog();
                deleteExpiredLog();
            } catch (Exception e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
                return false;
            }
            return true;
        }

        @Override
        protected void onCompleted(Boolean result, Object... params) {
            if (result != null && result) {
                startMonitorLog();
            } else {
                EvLog.i(TAG,"PrepareLogPrestener failed");
            }
            mPrepareLogPresenter = null;
        }

        @Override
        protected void onFailed(Exception exception, Object... params) {
            EvLog.i(TAG,"PrepareLogPrestener failed");
            mPrepareLogPresenter = null;
        }
    }
    
    /**
     * @brief : [删除过量的日志]
     * log目录大于50M时删除，如果文件个数大于5个，保留最近的5个日志文件
     * 如果文件个数小于或等于5个，则只保留最近的1个日志文件
     */
    private void deleteOverflowLog() {
        EvLog.d(TAG, " deleteOverflowLog>>>>>>>>>>>>");
        File file = new File(ResourceSaverPathManager.getInstance().getLogSavePath());
        if (file.isDirectory()) {
            File[] allFiles = file.listFiles();
            try {
                Arrays.sort(allFiles, new FileComparator());
            } catch (Exception e) {
                EvLog.e(TAG, "arrays sort e: " + e);
                UmengAgentUtil.reportError(e);
            }
            File singleFile = null;
            int filesNum = allFiles.length;
            EvLog.d(TAG, " dir file nums:" + filesNum + ",MAX_FILE_NUM_KEEP:" + MAX_FILE_NUM_KEEP);
            if(filesNum > MAX_FILE_NUM_KEEP) {    //保存最近的５个日志文件
                for(int i = 0; i < filesNum - MAX_FILE_NUM_KEEP; i++) {
                    singleFile = allFiles[i];
                    singleFile.delete();
                    EvLog.d(TAG, "delete overflow log, path is " + singleFile.getAbsolutePath());
                }
            } else if (filesNum > 1 && filesNum <= MAX_FILE_NUM_KEEP) {
                for(int i = 0; i <filesNum - 1; i++) {
                    singleFile = allFiles[i];
                    singleFile.delete();
                    EvLog.d(TAG, "delete overflow log, path is " + singleFile.getAbsolutePath());
                }
            }
        }
        file = null;
    }
    
//    /**
//     * 删除内存中的过期日志，删除规则： 除了当前的日志和离当前时间最近的日志保存其他的都删除
//     */
//    private void deleteMemoryExpiredLog()
//    {
//        File file = new File(LOG_PATH_MEMORY_DIR);
//        if (file.isDirectory())
//        {
//            File[] allFiles = file.listFiles();
//            Arrays.sort(allFiles, new FileComparator());
//            for (int i = 0; i < allFiles.length - 2; i++)
//            { // "-2"保存最近的两个日志文件
//                File _file = allFiles[i];
//                if (logServiceLogName.equals(_file.getName())
//                        || _file.getName().equals(CURR_INSTALL_LOG_NAME))
//                {
//                    continue;
//                }
//                _file.delete();
//                Log.d(TAG, "delete expired log success,the log path is:"
//                        + _file.getAbsolutePath());
//            }
//        }
//    }
    
    /**
     * @brief : [创建日志目录]
     */
    private void createLogDir() {
        File file = new File(ResourceSaverPathManager.getInstance().getLogSavePath());
        boolean mkOk;
        if (!file.isDirectory()) {
            mkOk = file.mkdirs();
            if (!mkOk) {
                mkOk = file.mkdirs();
            }
        }
        file = null;
    }
    
    
    /*class LogCollectorThread extends HandlerThread {
        
        public LogCollectorThread() {
            super("LogCollectorThread");
            EvLog.d(TAG, "LogCollectorThread is create");
        }
        
        @Override
        public void run() {
            try {
                clearLogCache();
                List<String> orgProcessList = getAllProcess();
                List<ProcessInfo> processInfoList = getProcessInfoList(orgProcessList);
                killLogcatProc(processInfoList);

                createLogCollector();

                Thread.sleep(1000);// 休眠，创建文件，然后处理文件，不然该文件还没创建，会影响文件删除

                handleLog();
//                startMemoryMonitorTask();
            } catch (Exception e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
            }
        }
        
    }*/
    
    /**
     * @brief : [处理日志文件：]
     * 大于50m时，只保留最近的5个日志
     * 删除5天之前的日志
     */
  /*  private void checkLogSizeAndDate() {
//        EvLog.d(TAG, "handleLog");
        long length = FileUtil.countLength(ResourceSaverPathManager.getInstance().getLogSavePath());
        EvLog.d(TAG, "checkLogSizeAndDate dir size: " + length);
        if(length > MEMORY_LOG_DIRECTORY_MAX_SIZE) {
            deleteOverflowLog();
        }
        deleteExpiredLog();
    }*/
    
    class ProcessInfo {
        public String user;
        public String pid;
        public String ppid;
        public String name;

        @Override
        public String toString() {
            String str = "user=" + user + " pid=" + pid + " ppid=" + ppid
                    + " name=" + name;
            return str;
        }
    }
    
    class StreamConsumer extends Thread {
        InputStream is;
        List<String> list;

        StreamConsumer(InputStream is) {
            this.is = is;
        }

        StreamConsumer(InputStream is, List<String> list) {
            this.is = is;
            this.list = list;
        }

        public void run() {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (list != null) {
                        list.add(line);
                    }
                }
            } catch (IOException ioe) {
                EvLog.e(ioe.getMessage());
                UmengAgentUtil.reportError(ioe);
            } finally {
                if(br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        EvLog.e(e.getMessage());
                        UmengAgentUtil.reportError(e);
                    }
                }
            }
        }
    }
    
    /**
     * 删除内存下过期的日志
     */
    private void deleteExpiredLog() {
        File file = new File(ResourceSaverPathManager.getInstance().getLogSavePath());
        if (file.isDirectory()) {
            File[] allFiles = file.listFiles();
            for (File logFile : allFiles) {
                String fileName = logFile.getName();
                String createDateInfo = getFileNameWithoutExtension(fileName);
                if (canDeleteLog(createDateInfo)) {
                    logFile.delete();
                    EvLog.d(TAG, " delete expired log success,the log path is:"+ logFile.getAbsolutePath());
                }
            }
        }
        file = null;
    }
    
    private boolean hasPassOneDay(String createDateStr) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date expiredDate = calendar.getTime();
        try {
            Date createDate = null;
            synchronized (sdf) {
                createDate = sdf.parse(createDateStr);
            }
            
            return createDate.before(expiredDate);
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        }

        return false;
    }
    
    /**
     * 判断日志文件是否可以删除
     * 
     * @param createDateStr
     * @return
     */
    private boolean canDeleteLog(String createDateStr) {
        boolean canDel = false;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1 * SDCARD_LOG_FILE_SAVE_DAYS);// 删除5天之前日志
        Date expiredDate = calendar.getTime();
        try {
            Date createDate = null;
            synchronized (sdf) {
                createDate = sdf.parse(createDateStr);
            }

            canDel = createDate.before(expiredDate);
        } catch (ParseException e) {
            EvLog.e(TAG, e.getMessage());
            UmengAgentUtil.reportError(e);
            canDel = true;
        }

        return canDel;
    }
    
    /**
     * 去除文件的扩展类型（.log）
     * 
     * @param fileName
     * @return
     */
    private String getFileNameWithoutExtension(String fileName) {
        return fileName.substring(0, fileName.indexOf("."));
    }
    
    class FileComparator implements Comparator<File> {
        public int compare(File file1, File file2) {
//            if (logServiceLogName.equals(file1.getName()))
//            {
//                return -1;
//            } else if (logServiceLogName.equals(file2.getName()))
//            {
//                return 1;
//            }

            String createInfo1 = getFileNameWithoutExtension(file1.getName());
            String createInfo2 = getFileNameWithoutExtension(file2.getName());

            try {
                Date create1 = null;
                Date create2 = null;

                synchronized (sdf) {
                    create1 = sdf.parse(createInfo1);
                    create2 = sdf.parse(createInfo2);
                }

                if (create1.before(create2)) {
                    return -1;
                } else {
                    return 1;
                }
            } catch (Exception e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
                return 0;
            }
        }
    }
    
/*    private TimeChangeReceiver mTimeChangeReceiver;
    
    private void registTimeChangeReceiver() {
        EvLog.d(TAG, "registTimeChangeReceiver");
        if(mTimeChangeReceiver == null) {
            mTimeChangeReceiver = new TimeChangeReceiver();
        }
        registerReceiver(mTimeChangeReceiver, new IntentFilter(Intent.ACTION_TIME_CHANGED));
    }
    
    private void unregistTimeChangeReceiver() {
        EvLog.d(TAG, "unregistTimeChangeReceiver");
        if(mTimeChangeReceiver != null) {
            try {
                unregisterReceiver(mTimeChangeReceiver);
            } catch (Exception e) {
                UmengAgentUtil.reportError(e);
                EvLog.e(TAG, "unregistTimeChangeReceiver: " + e.getMessage());
            }
            mTimeChangeReceiver = null;
        }
    }
    
    private class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null && Intent.ACTION_TIME_CHANGED.equals(intent.getAction())) {
                EvLog.d(TAG, "时间改变了");
                Intent serviceIntent = new Intent("android.kmbox.log.service");
                serviceIntent.putExtra("command", LogSpaceClean.COMMAND_START_COLLECT_LOG);
                startService(serviceIntent);
            }
        }
    }*/
}
