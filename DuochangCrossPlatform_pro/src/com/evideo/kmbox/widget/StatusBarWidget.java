package com.evideo.kmbox.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evideo.kmbox.R;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrl;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager.IPlayListListener;
import com.evideo.kmbox.model.song.MembersInfoList;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.ThreadUtil;
import com.evideo.kmbox.widget.common.OrderSongNumAnimController;
import com.evideo.kmbox.widget.common.OrderSongNumAnimController.AnimationEndListener;
import com.evideo.kmbox.widget.mainview.MainViewManager;
import com.evideo.kmbox.widget.msgview.GetAnnouncerCoverPresenter;

import java.io.File;

/**
 * @brief : [文件功能说明]
 * @verbatim All rights reserved by 福建星网视易信息系统有限公司
 * @endverbatim
 */
public class StatusBarWidget extends RelativeLayout implements IPlayListListener, View.OnKeyListener {

    private int mSongNameMsgId = -1;

    private ImageView mWifiStateView;
    private ImageView mKmIcon;
    private ImageView mMemPic = null;
    private TextView mMemName = null;
    private TextView mDbUpdateTv = null;
    private StatusBarMsgView mMsgView;
    private View mSearchBtn;
    private View mSelectedBtn;
    private TextView mSelectedNumTv;
    private int mCount;
    private View mFocusedView;
    private int mLogoResId = R.drawable.home_page_logo;
    private View mChargePayButton = null;

    public StatusBarWidget(Context context) {
        this(context, null);
    }

    public StatusBarWidget(Context context, AttributeSet paramAttributeSet) {
        super(context, paramAttributeSet);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.launcher_status_bar, this);
        initChildViews();
//        updateSystemTime();
        // updateSystemDate();
    }

    public void setSearchViewFocus() {
        mSearchBtn.requestFocus();
    }

    public void hide() {
        RelativeLayout rect = (RelativeLayout) this.findViewById(R.id.main_status_bar);
        rect.setVisibility(View.GONE);
    }

    public void hideWithoutLogo() {
        hideSearchBar();
        hideSelectedNum();
        hideChargePayBtn();
    }

    public void showAll() {
        showSearchBar();
        showSelectedNum();
        showChargePayBtn();
    }

    public void show() {
        RelativeLayout rect = (RelativeLayout) this.findViewById(R.id.main_status_bar);
        rect.setVisibility(View.VISIBLE);
    }

    public View getSearchBtn() {
        return mSearchBtn;
    }

    private void initChildViews() {
        PlayListManager.getInstance().registerListener(this);
        mSearchBtn = this.findViewById(R.id.status_bar_search);
        mSearchBtn.setNextFocusLeftId(R.id.status_bar_search);

        mSelectedBtn = this.findViewById(R.id.status_bar_selected);
        mSelectedNumTv = (TextView) this.findViewById(R.id.status_bar_selected_num_tv);
        mSelectedNumTv.setText(0 + "");

        mKmIcon = (ImageView) this.findViewById(R.id.imageview_km_icon);
        mDbUpdateTv = (TextView) this.findViewById(R.id.songbook_update_db);

        mKmIcon.setImageResource(mLogoResId);
        mMemPic = (ImageView) this.findViewById(R.id.imageview_km_memberpic);
        mMemName = (TextView) this.findViewById(R.id.imageview_km_membername);
        mMemPic.setVisibility(INVISIBLE);
        mMemName.setVisibility(INVISIBLE);
        mWifiStateView = (ImageView) this.findViewById(R.id.imageview_wifi_state);
        mMsgView = (StatusBarMsgView) this.findViewById(R.id.status_bar_msg_view);

        //搜索点击
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("gsp", "onClick  new View的onClickLine事件 ");
                MainViewManager.getInstance().openGlobalSearchView();
            }
        });


        mSelectedBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainViewManager.getInstance().openSelectedView();
            }
        });
        mSearchBtn.setOnKeyListener(this);
        mChargePayButton = findViewById(R.id.status_bar_unicom_pay);
        mChargePayButton.setOnKeyListener(this);
    }

    public void setChargeOrderButtonClickListener(View.OnClickListener listener) {
        if (mChargePayButton != null) {
            mChargePayButton.setOnClickListener(listener);
        }
    }


    /**
     * [功能说明]显示已点数量控件
     */
    public void showSelectedNum() {
        if (mSelectedBtn != null) {
            mSelectedBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * [功能说明]隐藏已点数量控件
     */
    public void hideSelectedNum() {
        if (mSelectedBtn != null) {
            mSelectedBtn.setVisibility(View.INVISIBLE);
        }
    }

    public int getSelectedNumId() {
        if (mSelectedBtn != null) {
            return mSelectedBtn.getId();
        }
        return -1;
    }

    public void setSelectedNumFocus() {
        if (mSelectedBtn != null) {
            mSelectedBtn.requestFocus();
        }
    }

    /**
     * [显示搜索栏]
     */
    public void showSearchBar() {
        if (mSearchBtn != null && mSearchBtn.getVisibility() != View.VISIBLE) {
            mSearchBtn.setVisibility(View.VISIBLE);
        }
    }

    public void setChargePayBtnResId(int resId) {
        if (mChargePayButton != null) {
            mChargePayButton.setBackgroundResource(resId);
        }
    }
    
   /* public void showUnicomPayBtn() {
        if (mUnicomPayButton != null) {
            mUnicomPayButton.setVisibility(View.VISIBLE);
        }
    }*/

    /**
     * [重置焦点]
     */
    public void resetSearchBtnFocus() {
        if (mSearchBtn != null) {
            mSearchBtn.setVisibility(View.VISIBLE);
            mSearchBtn.requestFocus();
        }
    }

    /**
     * [隐藏搜索栏]
     */
    public void hideSearchBar() {
        if (mSearchBtn != null) {
            mSearchBtn.setVisibility(View.INVISIBLE);
        }
    }

    public void showChargePayBtn() {
        if (mChargePayButton != null) {
            mChargePayButton.setVisibility(View.VISIBLE);
        }
    }

    public void hideChargePayBtn() {
        if (mChargePayButton != null) {
            mChargePayButton.setVisibility(View.GONE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        PlayListManager.getInstance().unregisterListener(this);
        /*if (mResolver != null) {
            mResolver.unregisterContentObserver(mDebugTimeContentObserver);
        }*/
    }

//    public void updateSystemTime() {
//        String DEFAULT_TIME_FORMAT = "HH:mm";// hh:mm 12hours
//        String time = DateUtil.getSystemTime(DEFAULT_TIME_FORMAT);
//        mTime.setText(time);
//    }

    /**
     * 更新歌曲名
     */
    public void updateSongInfo(String text) {
//        _currentsongNameTextView.setText(text);
        StatusBarMsg msg = new StatusBarMsg();
        msg.content = text;
        if (mSongNameMsgId < 0) {
//            mSongNameMsgId = mMsgView.addStatusBarMsg(msg);
            mSongNameMsgId = mMsgView.addSongMsg(msg);
        } else {
            mMsgView.updateStatusBarMsgById(mSongNameMsgId, msg);
        }
    }

    public int addDatabaseMsg(StatusBarMsg msg) {
        return mMsgView.addDatabaseMsg(msg);
    }

    public void updateStatusBarMsg(int id, StatusBarMsg msg) {
        mMsgView.updateStatusBarMsgById(id, msg);
    }

    public void removeStatusBarMsg(int id) {
        mMsgView.removeStatusBarMsgById(id);
    }

    public void showDbUpdateTv() {
        mDbUpdateTv.setVisibility(View.VISIBLE);
    }

    public void hideDbUpdateTv() {
        mDbUpdateTv.setVisibility(View.INVISIBLE);
    }

    public void updateDbUpdateTv(String text) {
        mDbUpdateTv.setText(text);
    }

    // 显示头像
    public boolean showUserHeadImage() {
        String path = "";
        KmPlayListItem currentInfo = KmPlayerCtrl.getInstance().getPlayingSong();
        if ((currentInfo != null) && (currentInfo.getCustomerid() != null)) {
            path = MembersInfoList.getInstance().getPicPath(currentInfo.getCustomerid());
        }
        return setUserIcon(path);
    }

    public boolean setUserIcon(String url) {
        if (TextUtils.isEmpty(url)) {
            url = "";
        }
        File file = new File(url);
        if (file == null || file != null && !file.exists()) {
            url = "";
        }
        if (TextUtils.isEmpty(url)) {
            enableUserIconToLogo(true);
            return false;
        } else {
            GetAnnouncerCoverPresenter.displayFromDisk(url, mMemPic);
        }
        return true;
    }

    public void enableUserIconToLogo(boolean enable) {
        mKmIcon.setVisibility(enable ? VISIBLE : INVISIBLE);
        mMemPic.setVisibility(enable ? INVISIBLE : VISIBLE);
        mMemName.setVisibility(enable ? INVISIBLE : VISIBLE);
    }

    /**
     * [功能说明]切换到mv状态
     */
    public void changeToMvState() {
        if (mMsgView != null) {
            mMsgView.setVisibility(View.VISIBLE);
        }
        if (mSearchBtn != null) {
            mSearchBtn.setVisibility(View.GONE);
            mSearchBtn.setFocusable(false);
        }
        if (mSelectedBtn != null) {
            mSelectedBtn.setFocusable(false);
        }
        if (showUserHeadImage()) {
            enableUserIconToLogo(false);
        }
        if (mChargePayButton != null) {
            mChargePayButton.setVisibility(View.GONE);
        }
    }

    /**
     * [功能说明]切换到主界面状态
     */
    public void changeToMainViewState() {
        if (mMsgView != null) {
            mMsgView.setVisibility(View.INVISIBLE);
        }

        if (mSearchBtn != null) {
            mSearchBtn.setVisibility(View.VISIBLE);
            mSearchBtn.setFocusable(true);
        }

        if (mSelectedBtn != null) {
            mSelectedBtn.setFocusable(true);
        }

        if (DeviceConfigManager.getInstance().isSupportCharge()) {
            if (mChargePayButton != null) {
                mChargePayButton.setVisibility(View.VISIBLE);
            }
        }
        enableUserIconToLogo(true);
       /* if (!DeviceConfigManager.TEST_ON_OTHER_BOX && DeviceConfigManager.getInstance().getDeviceName().equals(DeviceName.UNICOM_DEVICE_NAME)) {
            if (!UnicomCharge.getInstance().isAuthed()) {
               if (mChargePayButton != null) {
                   mChargePayButton.setVisibility(View.VISIBLE);
               }
            }
        }*/
    }

    private void updateCount(int count) {
//        EvLog.d("zxh", "status bar widget update count " + count);
        mCount = count;
        if (mSelectedNumTv != null) {
            mSelectedNumTv.setText(count + "");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPlayListChange() {
        if (mSelectedBtn == null || mSelectedNumTv == null) {
            return;
        }
        ThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final int count = PlayListManager.getInstance().getCount();
                if (count > mCount) {
                    OrderSongNumAnimController.setAnimEndListener(new AnimationEndListener() {
                        @Override
                        public void onAnimationEnd() {
                            int newCount = PlayListManager.getInstance().getCount();
                            updateCount(newCount);
                        }
                    });
                    OrderSongNumAnimController.startAnim(mSelectedBtn);
                } else {
                    updateCount(count);
                }
            }
        });
    }

    public void saveFocusedView() {
        if (hasFocus()) {
            mFocusedView = findFocus();
        } else {
            mFocusedView = null;
        }
    }

    public void restoreFocusedView() {
        if (mFocusedView != null) {
            mFocusedView.requestFocus();
        } else {
            mSearchBtn.requestFocus();
        }
    }

    public static final int CLICK_SEARCH = 4;
    public static final int CLICK_CHARGEBTN = 5;

    private IStatusBarKeyListener mKeyListener = null;

    public interface IStatusBarKeyListener {
        public boolean onKey(int btnType, int keyCode, KeyEvent event);
    }

    public void setKeyListener(IStatusBarKeyListener listener) {
        mKeyListener = listener;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
//        EvLog.i("onKey : ");
        int clickId = 0;
        switch (v.getId()) {
            case R.id.status_bar_search: {
                clickId = CLICK_SEARCH;
                break;
            }
            case R.id.status_bar_unicom_pay: {
                clickId = CLICK_CHARGEBTN;
                break;
            }
            default:
                break;
        }
        if (mKeyListener != null) {
            return mKeyListener.onKey(clickId, keyCode, event);
        }
        return false;
    }

    /***********************************
     * debug 状态应用运行时间显示************************************ [显示应用运行时间]
     */
   /* private ContentResolver mResolver = null;
    private ContentObserver mDebugTimeContentObserver = null;*/
//    private int mTime = 0;
//    private TextView mDebugCountTimeTV = null;
    /*private void startDebugCountTime() {
        boolean enableDb = isDebugMode();
        mTime = 0;
        if (enableDb) {
            post(runnable);
        }
    }
    
    private void stopDebugCountTime() {
        if (mDebugCountTimeTV.getVisibility() == View.VISIBLE) {
            mDebugCountTimeTV.setVisibility(View.GONE);
        }
        mTime = 0;
    }*/
    
    /*private void updateChangeDebugState() {
        boolean enableDb = isDebugMode();
        if (enableDb) {
            startDebugCountTime();
        } else {
            stopDebugCountTime();
        } 
    }
    
    private boolean isDebugMode() {
        return KmBoxProviderMetaData.getBoolean(mResolver, 
                KeyName.KEY_DEBUG_MODE_SWITCH, false);
    }*/
    
    /*Runnable runnable = new Runnable() {
        @Override
        public void run() {
            boolean enableDb = isDebugMode();
            if (enableDb) {
                postDelayed(this, 1000);
                mTime++;

                if (mDebugCountTimeTV != null) {
                    if (mDebugCountTimeTV.getVisibility() != View.VISIBLE) {
                        mDebugCountTimeTV.setVisibility(View.VISIBLE);
                    }
                    mDebugCountTimeTV.setText(TimeUtil.formatTime(mContext, mTime));
                }
            } else {
                stopDebugCountTime();
            }
        }
    };*/

}
