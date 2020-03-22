/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年10月8日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.playerctrl.list;

import android.util.Log;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.activity.MainActivity;
import com.evideo.kmbox.model.chargeproxy.ChargeProxy;
import com.evideo.kmbox.model.datacenter.DataCenterCommu;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.presenter.BasePresenter;
import com.evideo.kmbox.widget.charge.ChargeViewManager;
import com.evideo.kmbox.widget.common.ToastUtil;
import com.evideo.kmbox.widget.mainview.MainViewManager;

/**
 * [功能说明]
 */
public class SongOperationManager{
    /** [点歌延时时间] */
    public static final int ORDER_SONG_DELAY_DURATION = 300;

    private static SongOperationManager  instance = null;
    public static SongOperationManager getInstance() {
        if(instance == null) {
            synchronized (SongOperationManager.class) {
                SongOperationManager temp = instance;
                if(temp == null) {
                  temp = new SongOperationManager();
                  instance = temp;
                }
            }
         }
         return instance;
    }

    public interface IOrderSongResultListener {
        public void onOrderSongSuccess(int songId);
        public void onOrderSongFailed(int songId);
    }
    
    public interface ITopSongResultListener {
        public void onTopSongSuccess(int songId);
        public void onTopSongFailed(int songId);
    }
    

    public boolean topSong(final int songId,final ITopSongResultListener listener) {
        /*if (!NetUtils.isNetworkConnected(context)) {
            BasePresenter.runInUI(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showLongToast(context, context.getResources().getString(R.string.toast_network_error));
                    MainViewManager.getInstance().updateMainViewCurrentSong(context.getResources().getString(R.string.toast_network_error));
                }
            });
            return false;
        }*/

    	if (!DataCenterCommu.getInstance().isLoginSuccess()) {
            ToastUtil.showLongToast(BaseApplication.getInstance(), BaseApplication.getInstance().getResources().getString(R.string.dc_is_login));
            return false;
        }

        //修改 1个！去除和 订够去除
        if (!DeviceConfigManager.getInstance().isSupportCharge()) {
            realTopSong(null, songId,listener);
            return true;
        }

        if (DeviceConfigManager.getInstance().forbitPaySongPlay()/*SystemConfigManager.SUPPORT_FREE_SING*/) {
            realTopSong(null, songId,listener);
            return true;
        }


        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                // TODO 支付
//                MainActivity.mainActivity.getXML();
//                ChargeViewManager.getInstance().clickChargeView(MainViewManager.getInstance().getActivity());
            }
        });
        if (!ChargeProxy.getInstance().isAuthed()) {
            realTopSong(null, songId,listener);
            return true;
        }
        return false;
    }
    
    private void realTopSong(final String customerId, final int songId, final ITopSongResultListener listener) {
        BaseApplication.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean ret = PlayListManager.getInstance().addSong(customerId, songId, true);
                if (listener != null) {
                    if (ret) {
                        listener.onTopSongSuccess(songId);
                    } else {
                        listener.onTopSongFailed(songId);
                    }
                }
            }
        }, ORDER_SONG_DELAY_DURATION);
    }
    
    
    public boolean orderSong(/*final Context context,*/final int songId,final IOrderSongResultListener listener) {
    	if (!DataCenterCommu.getInstance().isLoginSuccess()) {
            ToastUtil.showLongToast(BaseApplication.getInstance(), BaseApplication.getInstance().getResources().getString(R.string.dc_is_login));
            return false;
        }
    	
        if (!DeviceConfigManager.getInstance().isSupportCharge()) {
            realOrderSong(null, songId,listener);
            return true;
        }
        //
        if (!DeviceConfigManager.getInstance().forbitPaySongPlay()/*SystemConfigManager.SUPPORT_FREE_SING*/) {
            realOrderSong(null, songId,listener);
            return true;
        }

        if (ChargeProxy.getInstance().isAuthed()) {
            realOrderSong(null, songId,listener);
            return true;
        }


        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                // TODO 支付
//                MainActivity.mainActivity.getXML();
//                ChargeViewManager.getInstance().clickChargeView(MainViewManager.getInstance().getActivity());
            }
        });
        if (!DeviceConfigManager.getInstance().isFree()){
            realOrderSong(null, songId,listener);
            DeviceConfigManager.getInstance().setFree(false);
            return true;
        }
        return false;
    }
    
    private void realOrderSong(final String customerId, final int songId, final IOrderSongResultListener listener) {
        BaseApplication.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean ret = PlayListManager.getInstance().addSong(customerId, songId, false);
                if (listener != null) {
                    if (ret) {
                        listener.onOrderSongSuccess(songId);
                    } else {
                        listener.onOrderSongFailed(songId);
                    }
                }
            }
        }, ORDER_SONG_DELAY_DURATION);
    }
    
}
