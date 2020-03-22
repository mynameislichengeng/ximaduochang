package com.evideo.kmbox.exceptionhandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.os.Looper;
import android.text.TextUtils;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;
import com.evideo.kmbox.util.ShellUtils;
import com.evideo.kmbox.util.SystemInfo;
import com.evideo.kmbox.widget.common.ToastUtil;

public class KmCrashExceptionHandler implements UncaughtExceptionHandler {
    
    public static final String TAG = "CrashHandler";
//    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static KmCrashExceptionHandler instance = null;
    private int mErrorType = EXCEPTION_NONE;
    private static final int EXCEPTION_NONE = 0;
    //内存溢出
    private static final int EXCEPTION_OUT_OF_MEMORY = 1;
    //空间已满
    private static final int EXCEPTION_DISK_IS_FULL = 2;
    //空指针
    private static final int EXCEPTION_NULL_POINTER = 3;
    //主线程执行网络操作
//    private static final int EXCEPTION_NETWORKONMAINTHREADEXCEPTION = 3;
    private static final int EXCEPTION_OTHER = 10;

    public static KmCrashExceptionHandler getInstance() {
        if(instance == null) {
            synchronized (KmCrashExceptionHandler.class) {
                KmCrashExceptionHandler temp = instance;
                if(temp == null) {
                  temp = new KmCrashExceptionHandler();
                  instance = temp;
                }
            }
         }
         return instance;
    }

    public KmCrashExceptionHandler() {
    }
    
    public void init(/*Context context*/) {
        if (instance != null) {
//            mContext = context;
            mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            // 监听app exception
            Thread.setDefaultUncaughtExceptionHandler(instance);
        }
    }
    
    public void uninit() {
//        mContext = null;
        instance = null;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 主线程中执行
        StringBuilder errorMsg = new StringBuilder("Cause by:" + ex.getCause().toString());
        StackTraceElement[] trace = ex.getCause().getStackTrace();
        for (StackTraceElement traceElement : trace) {
            errorMsg.append("\nat " + traceElement);
        }
        EvLog.e(">>>>>>>>>>> Duochang Application Exception!\n" + errorMsg);

        ex.printStackTrace();
        UmengAgentUtil.reportError(ex);
        String stacktraceStr = getStackTraceStr(ex);
        if (!TextUtils.isEmpty(stacktraceStr)) {
            if (stacktraceStr.contains("OutOfMemoryError")) {
                mErrorType = EXCEPTION_OUT_OF_MEMORY;
            } else if (stacktraceStr.contains("SQLiteFullException")) {
                mErrorType = EXCEPTION_DISK_IS_FULL;
            } else {
                mErrorType = EXCEPTION_OTHER;
            }
        }
        String logpath = FileUtil.concatPath(ResourceSaverPathManager.getInstance().getLogSavePath(), "caught.log");
        ShellUtils.execCommand("echo -----------"+SystemInfo.getSystemVersion()
                                                +"--------------- >> " + logpath, false);
        ShellUtils.execCommand("date >> " + logpath, false);
        ShellUtils.execCommand("echo \"" + stacktraceStr + "\" >> " + logpath, false);
        // 自定义错误处理
        boolean res = handleException(ex);
        if (!res && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                EvLog.e(TAG, "error : "+ e.getMessage());
            }
            // 退出程序
            /*android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);*/
        }
        BaseApplication.getInstance().exit();
    }
    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     * 
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                String tip = "";
                String qq = "";
                if (DeviceConfigManager.getInstance().getDevice() != null) {
                    qq = DeviceConfigManager.getInstance().getDevice().getQQ();
                }
                
                if (mErrorType == EXCEPTION_DISK_IS_FULL) {
                    tip = BaseApplication.getInstance().getString(R.string.crash_error_full_of_disk);
                } else if (mErrorType == EXCEPTION_OUT_OF_MEMORY) {
                    tip = BaseApplication.getInstance().getString(R.string.crash_error_out_of_memory);
                } else {
                    tip = BaseApplication.getInstance().getString(R.string.crash_error_other);
                }
                if (!TextUtils.isEmpty(qq)) {
                    tip += BaseApplication.getInstance().getString(R.string.contact_qq) + qq;
                }
                ToastUtil.showLongToast(BaseApplication.getInstance(), tip);
                Looper.loop();
            }
        }.start();
        return true;
    }
    
    /**
     * [功能说明]获取异常堆栈信息
     * @param e 异常
     * @return 异常堆栈信息
     */
    public String getStackTraceStr(Throwable e) {
        if (e == null) {
            return null;
        }
        StringWriter sw = null;
        PrintWriter pw = null;
        String strackTraceStr = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
            strackTraceStr = sw.toString();
        } catch (Exception e2) {
            EvLog.e("getStackTraceStr error " + e2.getMessage());
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e3) {
                    EvLog.e("getStackTraceStr sw close error " + e3.getMessage());
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return strackTraceStr;
    }
}
