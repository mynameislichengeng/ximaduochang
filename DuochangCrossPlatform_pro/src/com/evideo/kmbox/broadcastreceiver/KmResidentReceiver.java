package com.evideo.kmbox.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.evideo.kmbox.BroadcastConstant;
import com.evideo.kmbox.model.observer.keyevent.KeyEventSubject;
import com.evideo.kmbox.model.observer.net.EthernetInfoSubject;
import com.evideo.kmbox.model.observer.net.NetworkInfoSubject;
import com.evideo.kmbox.model.observer.net.WifiInfoSubject;

public class KmResidentReceiver extends BroadcastReceiver {
    
    private static final String TAG = "KmResidentReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null) {
            return;
        }
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            //网络连接改变
            NetworkInfoSubject.getInstance().notifyNetworkChanged();
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            //wifi连接改变
            NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(
                    WifiManager.EXTRA_NETWORK_INFO);
            WifiInfoSubject.getInstance().notifyWifiStateChanged(info);
            
        } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
            //wifi信号强度改变
            WifiInfoSubject.getInstance().notifyWifiRssiChanged();
            
        } else if (BroadcastConstant.ETHERNET_STATE_CHANGED_ACTION.equals(action)) {
            //有线网状态改变
            int state = intent.getIntExtra(BroadcastConstant.EXTRA_ETHERNET_STATE, 0);
            EthernetInfoSubject.getInstance().notifyEthernetStateChanged(state);
            if (state == BroadcastConstant.ETHER_STATE_DISCONNECTED) {
                NetworkInfoSubject.getInstance().notifyNetworkChanged();
            }
        } else if (BroadcastConstant.ACTION_KEYEVENT_HOME.equals(action)) {
            // home按键事件
            KeyEventSubject.getInstance().notifyHomeKeyPressed();
        }
    }
}
