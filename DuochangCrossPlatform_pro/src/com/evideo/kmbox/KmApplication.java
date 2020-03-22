package com.evideo.kmbox;


import android.content.Context;
import android.text.TextUtils;

import com.evideo.kmbox.exceptionhandler.KmCrashExceptionHandler;
import com.evideo.kmbox.model.datacenter.DataCenterCommu;
import com.evideo.kmbox.model.device.DeviceConfig;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.httpproxy.DeviceHttpProxy;
import com.evideo.kmbox.model.httpproxy.KmHttpProxy;
import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
import com.evideo.kmbox.model.observer.activity.ActivitySubject;
import com.evideo.kmbox.model.observer.drawer.DrawerStateSubject;
import com.evideo.kmbox.model.observer.net.EthernetInfoSubject;
import com.evideo.kmbox.model.observer.net.NetworkInfoSubject;
import com.evideo.kmbox.model.observer.net.WifiInfoSubject;
import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.ImageLoaderUtil;
import com.evideo.kmbox.util.NetUtils;

/**
 * : [KmApplication]
 */
public class KmApplication extends BaseApplication {

    private static Context mContext;

    private long mPayValidTimeSetTimestamp = -1;

    /**
     * [获取Application实例]
     *
     * @return Application
     */
    public static KmApplication getInstance() {
        return (KmApplication) BaseApplication.getInstance();
    }

    public void setPayValidTimeSetTimestamp(long mills) {
        mPayValidTimeSetTimestamp = mills;
    }

    public long getPayValidTimeSetTimestamp() {
        return mPayValidTimeSetTimestamp;
    }

    @Override
    public void onCreate() {
        EvLog.d("KmApplication onCreate ----------------");
        super.onCreate();

        mContext = getApplicationContext();
        KmCrashExceptionHandler.getInstance().init();

        //设置设备信息
        DeviceConfig device = new DeviceConfig();
        try {
            System.loadLibrary("mg20pbase");
        } catch(Exception e){
        }
        String chipId = "";
        chipId = NetUtils.getMacAddr();
        if (TextUtils.isEmpty(chipId)) {
            chipId = android.provider.Settings.Secure.getString(
                    mContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UmengAgentUtil.reportError("get mac failed,use androidId:" + chipId);
        }

        DeviceConfigManager.getInstance().init(mContext, device, chipId);

        //设置http代理信息
        KmHttpProxy.getInstance().init(new DeviceHttpProxy());

        ResourceSaverPathManager.getInstance().init(mContext);

        initSubject();

        String type = KmSharedPreferences.getInstance().getString(KeyName.KEY_DATA_CENTER_URI_TYPE, SystemConfigManager.DC_TYPE_NORMAL);
//            String dataCenterUri = getResources().getString(R.string.strDataCenterURI);
        String dataCenterUri = DeviceConfigManager.getInstance().getNormalDataCenterUrl();
        if (!type.equals(SystemConfigManager.DC_TYPE_NORMAL)) {
            dataCenterUri = DeviceConfigManager.getInstance().getTestDataCenterUrl();
        }
        EvLog.e(">>>>>>>>>>>setLoginURI :" + dataCenterUri);
        DataCenterCommu.getInstance().setLoginURI(dataCenterUri);

        LogAnalyzeManager.getInstance().init(this);
        //初始化ImageLoader配置
        ImageLoaderUtil.initImageLoaderConfiguration(mContext);
    }

    public void uninit() {
        KmCrashExceptionHandler.getInstance().uninit();
    }

    private void initSubject() {
        NetworkInfoSubject.getInstance().initSubject();
        WifiInfoSubject.getInstance().initSubject();
        EthernetInfoSubject.getInstance().initSubject();
        DrawerStateSubject.getInstance().initSubject();
        ActivitySubject.getInstance().initSubject();
    }

    public Context getContext() {
        return mContext;
    }
}
