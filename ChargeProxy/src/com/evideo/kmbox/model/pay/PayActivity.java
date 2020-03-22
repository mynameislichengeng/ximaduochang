package com.evideo.kmbox.model.pay;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.evideo.chargeproxy.R;
import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.model.chargeproxy.ChargeProxy;
import com.evideo.kmbox.model.chargeproxy.DeviceCommu;
import com.evideo.kmbox.presenter.CommuPresenter;
import com.evideo.kmbox.presenter.CommuPresenter.CommuCallback;
import com.evideo.kmbox.util.EvLog;



public class PayActivity extends Activity {
    private String mPrice = "";
    private int mProductId = 0;
    private String mTradeNo = "";
    private CommuPresenter mReportPresenter = null;
    private ReportPaySuccessCommu mReportCommu = null;
    
    private QueryTradeNoCommu mQueryTradeNoCommu = null;
    private CommuPresenter mQueryTradeNoPresenter = null;

    private TextView mGetTradeNoTx = null;
    private String mGetTradeNoFailedText = "";
    public static PayActivity payActivity = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay);
        mTradeNo = getIntent().getStringExtra("extra");
        mProductId = getIntent().getIntExtra("productId", 0);
        mPrice = getIntent().getStringExtra("Pprice");

        Log.i("gsp", "onCreate: 获取的参数数据是"+ mTradeNo+" mProductId  "+mProductId+" mPrice "+mPrice);
        mGetTradeNoTx = (TextView)findViewById(R.id.get_trade_no_tx);
        mGetTradeNoFailedText = getResources().getString(R.string.get_trade_no_failed_tx);
        
        if (TextUtils.isEmpty(mTradeNo)) {
            queryTradeNo(mProductId);

        } else {
            beginPay(mTradeNo);
        }
    }

    private void beginPay(String serialNum) {
    }
    
    public class QueryTradeNoCommu implements CommuCallback{
        private String mSerialNum = null;
        
        @Override
        public Boolean doCommu(Object... params) throws Exception {
            int productId = (Integer)params[0];
            mSerialNum = DeviceCommu.queryTradeNo(String.valueOf(productId));
            return !TextUtils.isEmpty(mSerialNum);
        }
        @Override
        public void commuSuccess() {
            mGetTradeNoTx.setVisibility(View.GONE);
            mQueryTradeNoPresenter = null;
            mTradeNo = mSerialNum;
            EvLog.i("QueryTradeNoCommu commuSuccess,mTradeNo:" + mTradeNo);
            beginPay(mSerialNum);
        }
        @Override
        public void commuFailed(Exception e) {
            mGetTradeNoTx.setVisibility(View.GONE);
            mQueryTradeNoPresenter = null;
            EvLog.i("QueryTradeNoCommu failed");
            ChargeProxy.getInstance().getCharge().sdkPayFailed(-1, mGetTradeNoFailedText);
            exit(3000);
        }
    }
    
    public void queryTradeNo(int productId) {
        if (mQueryTradeNoCommu == null) {
            mQueryTradeNoCommu = new QueryTradeNoCommu();
        }
        
        if (mQueryTradeNoPresenter != null) {
            mQueryTradeNoPresenter.cancel();
            mQueryTradeNoPresenter = null;
        }
        mQueryTradeNoPresenter = new CommuPresenter(mQueryTradeNoCommu);
        mQueryTradeNoPresenter.start(productId);
        mGetTradeNoTx.setVisibility(View.VISIBLE);
    }
    
    @Override
    protected void onDestroy() {
        EvLog.i("PayActivity onDestroy");
        if (mReportPresenter != null) {
            mReportPresenter.cancel();
            mReportPresenter = null;
        }
        if (mQueryTradeNoPresenter != null) {
            mQueryTradeNoPresenter.cancel();
            mQueryTradeNoPresenter = null;
        }
        super.onDestroy();
    }
    
    public void reportPaySuccess() {
        mGetTradeNoTx.setVisibility(View.VISIBLE);
        mGetTradeNoTx.setText(getResources().getString(R.string.get_valid_time_tx));
        Log.i("gsp", "reportPaySuccess:成功调用这个方法那 ");
        if (mReportCommu == null) {
            mReportCommu = new ReportPaySuccessCommu();
        }
        
        if (mReportPresenter != null) {
            mReportPresenter.cancel();
            mReportPresenter = null;
        }
        mReportPresenter = new CommuPresenter(mReportCommu);
        mReportPresenter.start(/*mTradeNo,mProductId,mPrice*/);

    }
    
    public class ReportPaySuccessCommu implements CommuCallback{
        private long mValidTime = -1;
        @Override
        public Boolean doCommu(Object... params) throws Exception {
            EvLog.i("ReportPaySuccessCommu mTradeNo:" + mTradeNo);
            mValidTime = DeviceCommu.sendPayResult(mTradeNo);
            EvLog.i("ReportPaySuccessCommu mTradeNo:局方支付返回的时间是" + mValidTime);
            return mValidTime > 0;
        }
        @Override
        public void commuSuccess() {
            mReportPresenter = null;
            EvLog.i("QueryTradeNoCommu commuSuccess");
            ChargeProxy.getInstance().getCharge().sdkPaySuccess(mValidTime, Integer.valueOf(mProductId), mTradeNo);
            exit(0);
        }
        @Override
        public void commuFailed(Exception e) {
            mReportPresenter = null;
            ChargeProxy.getInstance().getCharge().sdkPayFailed(-1, PayActivity.this.getResources().getString(R.string.report_dc_pay_result_failed_tx));
            exit(0);
        }
    }
    
    private void exit(long delayTime) {
        BaseApplication.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PayActivity.this.finish();
            }
        }, delayTime);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
