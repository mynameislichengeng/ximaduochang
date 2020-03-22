package com.evideo.kmbox;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.evideo.kmbox.activity.BaseActivity;
import com.evideo.kmbox.util.EvLog;

import java.util.ArrayList;
import java.util.List;

public class BaseApplication extends Application {

    private static Handler mHandler;

    private static BaseApplication instance;

    private List<BaseActivity> oList;//用于存放所有启动的Activity的集合

    public void addActivity(BaseActivity activity) {
        if (activity == null) {
            return;
        }
        if (oList == null) {
            oList = new ArrayList<BaseActivity>();
        }
        if (!oList.contains(activity)) {
            oList.add(activity);
        }
    }

    public void removeActivity(BaseActivity activity) {
        if (activity == null) {
            return;
        }
        if (oList == null) {
            return;
        }
        if (oList.contains(activity)) {
            oList.remove(activity);
        }
        oList.clear();
    }

    public void exit() {
        if (oList == null) {
            return;
        }
        EvLog.e("removeAllActivity " + oList.size());

        for (BaseActivity activity : oList) {
            activity.exitActivity();
        }
        oList.clear();
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onCreate() {
        EvLog.i("Application__onCreate");
        super.onCreate();
        instance = this;
    }


    public static BaseApplication getInstance() {
        return instance;
    }

    public static Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    public boolean isTopActivity(String cmdName) {
        boolean isTop = false;
        ActivityManager am = (ActivityManager) instance.getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//        EvLog.d("TopActivity : " + cn.getClassName());
        if (cn.getClassName().contains(cmdName)) {
            isTop = true;
        }
        return isTop;
    }

    /**
     * [功能说明] 获取当前运行app的包名
     *
     * @return
     */
    public String getTopPackageName() {
        ActivityManager am = (ActivityManager) instance.getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getPackageName();
    }

    public String getTopClassName() {
        ActivityManager am = (ActivityManager) instance.getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = null;

        List<RunningTaskInfo> list = am.getRunningTasks(3);
        for (RunningTaskInfo info : list) {
            EvLog.i("" + info.topActivity.getPackageName());
        }
        if (am.getRunningTasks(1) != null && am.getRunningTasks(1).get(0) != null) {
            cn = am.getRunningTasks(1).get(0).topActivity;
            EvLog.i("getPackageName：" + cn.getPackageName());
        }
        return (cn != null) ? (cn.getClassName()) : ("");
    }

}
