/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年4月19日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.chargeproxy;

import java.util.ArrayList;
import java.util.List;

import com.evideo.kmbox.model.charge.ChargeProductInfo;
import com.evideo.kmbox.presenter.AsyncPresenter;

/**
 * [功能说明]
 */
public class ChargePrice {

  
    public static interface IGetPriceListener {
        public void onGetPriceSuccess();
        public void onGetPriceFailed();
    }
    private static ChargePrice  instance = null;
    public static ChargePrice getInstance() {
        if(instance == null) {
            synchronized (ChargePrice.class) {
                ChargePrice temp = instance;
                if(temp == null) {
                  temp = new ChargePrice();
                  instance = temp;
                }
            }
         }
         return instance;
    }
    
    private  List<ChargeProductInfo> mPriceList = null;
    private GetPricePresenter mPresenter = null;
    private IGetPriceListener mListener = null;
    public ChargePrice() {
        mPriceList = new ArrayList<ChargeProductInfo>();
    }
    
    public void setListener(IGetPriceListener listener) {
        mListener = listener;
    }
    
    public List<ChargeProductInfo> getPriceList() {
        return mPriceList;
    }
    
    public void startGetPrice() {
        if (mPresenter != null) {
            mPresenter.setStop();
            mPresenter.cancel();
            mPresenter = null;
        }
        mPresenter = new GetPricePresenter();
        mPresenter.start();
    }

    public class GetPricePresenter extends AsyncPresenter<Boolean> {
        private  List<ChargeProductInfo> mList = null;
        private boolean mStopFlag = false;
        
        public GetPricePresenter() {
            mList = new ArrayList<ChargeProductInfo>();
            mStopFlag = false;
        }
        
        public void setStop() {
            mStopFlag = true;
        }
        
        @Override
        protected Boolean doInBackground(Object... params) throws Exception {
            int result = -1;
            for (int i = 0; i < 3;i++) {
                if (mStopFlag) {
                    break;
                }
                result =  DeviceCommu.queryPayPrice(mList);
                if (result == 0) {
                    break;
                }
            }
           
            if (result != 0) {
                return false;
            }
            return true;
        }

        @Override
        protected void onCompleted(Boolean result, Object... params) {
            if (result == null || (!result)) {
                if (mListener != null) {
                    mListener.onGetPriceFailed();
                }
                return;
            }
            synchronized (mPriceList) {
                mPriceList.clear();
                mPriceList.addAll(mList);
            }
            if (mListener != null) {
                mListener.onGetPriceSuccess();
            }
        }

        @Override
        protected void onFailed(Exception exception, Object... params) {
            if (mListener != null) {
                mListener.onGetPriceFailed();
            }
        }
    }
}
