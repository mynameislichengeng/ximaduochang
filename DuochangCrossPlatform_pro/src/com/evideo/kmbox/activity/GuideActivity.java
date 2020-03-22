package com.evideo.kmbox.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.SystemConfigManager;
import com.evideo.kmbox.model.datacenter.BootPictureManager;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.device.ResourceSaverPathManager.ISyncDBListener;
import com.evideo.kmbox.model.thirdapp.ApkJumpParamParser;
import com.evideo.kmbox.presenter.BasePresenter;
import com.evideo.kmbox.util.BitmapUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.mainview.myspace.ProgressDialog;

/**
 * @brief      : [文件功能说明]
 */
public class GuideActivity extends BaseActivity {
    
    private ProgressDialog mCopyDbProgressDialog = null;
    private boolean mIsPrevExecStop = false;
    private boolean mIsSyncDBFinish = false;
    private  Bitmap mBgBmp = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BootPictureManager.getInstance().init();
        
        Bundle bundle = this.getIntent().getExtras();
        ApkJumpParamParser.getInstance().init(bundle);
        
        EvLog.i("GuideActivity__onCreate");

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FrameLayout mBootPictureView = (FrameLayout) View.inflate(this,R.layout.boot_root, null);
        long duration = 2000;
        if (ApkJumpParamParser.getInstance().getJumpType() == ApkJumpParamParser.JUMP_TYPE_NONE) {
            if (BootPictureManager.getInstance().hasPicture()) {
                duration = BootPictureManager.getInstance().getDuration();
                mBgBmp = BitmapUtil.getBmpByPath(BootPictureManager.getInstance().getPicutre());
                if (mBgBmp == null) {
                    mBgBmp = BitmapUtil.getBitmapByResId(this, R.drawable.default_boot_picture_k20);
                }
            } else {
                mBgBmp = BitmapUtil.getBitmapByResId(this, R.drawable.default_boot_picture_k20);
            }
        }
        mBootPictureView.setBackgroundDrawable(new BitmapDrawable(mBgBmp));
        setContentView(mBootPictureView);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
        BaseApplication.getInstance().addActivity(this);

        if (ResourceSaverPathManager.getInstance().needSyncDB()) {
            mCopyDbProgressDialog = new ProgressDialog(this);
            mCopyDbProgressDialog.setTitle("正在同步数据库，请稍等");
            mCopyDbProgressDialog.show();
            ResourceSaverPathManager.getInstance().startSyncDB(new ISyncDBListener() {
                @Override
                public void onSyncProgress(final int progress) {
                    BasePresenter.runInUI(new Runnable() {
                        @Override
                        public void run() {
                            mCopyDbProgressDialog.updateProgress(progress);
                        }
                    });
                }
                @Override
                public void onSyncFinish() {
                    mCopyDbProgressDialog.updateProgress(100);
                    mIsSyncDBFinish = true;
                    if (!BaseApplication.getInstance().isTopActivity(SystemConfigManager.SYNC_DB_ACTIVITY)) {
                        EvLog.e("Guide activity is in backgroud,wait------------");
                        return;
                    }
                    mCopyDbProgressDialog.hide();
                    startActivity(new Intent(GuideActivity.this, MainActivity.class));
                }
            });
            return;
        } else {
            BaseApplication.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(GuideActivity.this, MainActivity.class));
                }
            }, duration);
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        EvLog.i("guideactivity onDestroy");
        super.onDestroy();
    }
    
    @Override
    protected void onResume() {
//        EvLog.i("GuideActivity onResume");
        if (mIsPrevExecStop && ResourceSaverPathManager.getInstance().needSyncDB()) {
            if (mIsSyncDBFinish) {
                BaseApplication.getHandler().postDelayed(new Runnable() {
                    
                    @Override
                    public void run() {
                        EvLog.e("Guide activity is resume,wait------------");
                        mCopyDbProgressDialog.hide();
                        startActivity(new Intent(GuideActivity.this, MainActivity.class));
                    }
                }, 1000);
            } else {
                ResourceSaverPathManager.getInstance().resumeSyncDB();
            }
        }
        mIsPrevExecStop = false;
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        EvLog.i("GuideActivity onPause");
        super.onPause();
    }
    
    @Override
    protected void onStop() {
        EvLog.i("GuideActivity onStop");
        if (ResourceSaverPathManager.getInstance().needSyncDB()) {
            ResourceSaverPathManager.getInstance().pauseSyncDB();
        } else {
        }
        mIsPrevExecStop = true;
        super.onStop();
    }

    @Override
    public void exitActivity() {
        finish();
    }
}
