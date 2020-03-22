/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-9-15     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.evideo.kmbox.model.playerctrl.PlayCtrlHandler;
import com.evideo.kmbox.model.songmenu.SongMenu;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.StatusBarWidget;
import com.evideo.kmbox.widget.intonation.KmMainView;
import com.evideo.kmbox.widget.mainview.MainViewContainer.IAnimEndListener;

import java.util.ArrayList;
import java.util.List;

/**
 * [功能说明]主界面管理者
 */
public final class MainViewManager{
    
    private MainViewContainer mMainViewContainer;
    
    private KmMainView mMainView;
    
    private static MainViewManager sInstance = new MainViewManager();
    
    private MainViewManager() {
    }

    public static MainViewManager getInstance() {
        return sInstance;
    }
    
    public View getVideoView() {
        if (mMainView != null) {
            return mMainView.getVideoView();
        }
        return null;
    }
    
    /**
     * [功能说明]初始化
     * @param activity 构造主界面的activity
     * @return 主界面
     */
    public void init(Activity activity,KmMainView parentView) {
        mMainViewContainer = new MainViewContainer(activity);
        mMainView = parentView;
        mMainView.view().addView(mMainViewContainer);
    }
  
    public Activity getActivity() {
        if (mMainViewContainer != null) {
            return mMainViewContainer.getActivity();
        }
        return null;
    }
    public void updateActivityView(String path) {
        mMainViewContainer.updateActivityView(path);
    }
    
    public void updateHomeView() {
        mMainViewContainer.updateHomeView();
    }
    
    /**
     * [功能说明]设置mv界面
     * @param
     */
    public void setParentView(KmMainView mainView) {
        mMainView = mainView;
    }
    
    public void updateMainViewCurrentSong(String text) {
        if (mMainView != null) {
            mMainView.updateMainViewCurrentSong(text);
            mMainView.getStatusBar().updateSongInfo(text);
        }
        if (mMainViewContainer != null) {
            if (mMainViewContainer.getCurrentViewId() == MainViewId.ID_FREE_SONG) {
                mMainViewContainer.updateFreeSong(text);
            }
        }
    }
    /**
     * [功能说明]保存焦点
     */
    public void saveFocus() {
        if (mMainViewContainer != null) {
            mMainViewContainer.saveFocusedView();
        }
        if (mMainView != null) {
            mMainView.saveFocusedView();
        }
    }
    
    /**
     * [功能说明]恢复焦点
     */
    public void restoreFocus() {
        if (mMainViewContainer != null) {
            if (!mMainViewContainer.restoreFocusedView()) {
                if (mMainView != null) {
                    mMainView.restoreFocusedView();
                }
            }
        }
    }
    
    public String getPlayingSongInfo() {
        return mMainView.getBottomWidget().getSongTvText();
    }
    
    public View getSearchButton() {
        if (mMainView.getStatusBar() != null) {
            return mMainView.getStatusBar().getSearchBtn();
        } else {
            return null;
        }
    }
    
    public StatusBarWidget getStatusBar() {
        if (mMainView != null) {
            return mMainView.getStatusBar();
        }
        return null;
    }
    
    public interface IMVSwitchListener {
        public void onSwitchToMV();
        public void onSwitchToMainView();
    }
    
    private List<IMVSwitchListener> mListeners = new ArrayList<IMVSwitchListener>();
    public void addMVSwitchListener(IMVSwitchListener listener) {
        mListeners.add(listener);
    }
    
    public void removeMVSwitchListener(IMVSwitchListener listener) {
        mListeners.remove(listener);
    }
    
    public void uninit() {
        mListeners.clear();
    }
    
    private void notifyMVSwitchListener(boolean toMV) {
        if (toMV) {
            for (IMVSwitchListener listener : mListeners) {
                listener.onSwitchToMV();
            }
        } else {
            for (IMVSwitchListener listener : mListeners) {
                listener.onSwitchToMainView();
            }
        }
    }
    /**
     * [功能说明]切换主界面和mv界面
     */
    public void switchMainView() {
        if (mMainViewContainer == null || mMainView == null) {
            return;
        }
        if (mMainViewContainer.isOnAnim()) {
            EvLog.e("isOnAnim-----");
            return;
        }
        EvLog.e("isMainViewVisible:" + mMainView.isMainViewVisible());
        if (mMainView.isMainViewVisible()) {
            saveFocus();
            mMainViewContainer.huodongPause();
            mMainViewContainer.hideViewWithFadeAnim(new IAnimEndListener() {
                @Override
                public void onAnimEnd() {
                    mMainView.gotoMvView();
                }
            });
            notifyMVSwitchListener(true);
        } else {
            mMainViewContainer.showViewWithFadeAnim();
            mMainViewContainer.huodongResume();
            mMainView.gotoMainView();
            restoreFocus();
            notifyMVSwitchListener(false);
        }
    }
    
    public void showMvAtAssignRect(LinearLayout.LayoutParams lp) {
        mMainView.showMvAtAssignRect(lp);
    }
    
    public void resumeMvRect() {
        mMainView.resetMvRect();
        mMainView.zoomOutSurfaceView();
    }
    
    public void gotoMV() {
        if (mMainViewContainer == null || mMainView == null) {
            return;
        }
        if (mMainViewContainer.isOnAnim()) {
            return;
        }
//        mMainViewContainer.hideViewWithAlphaAnim();
        mMainViewContainer.hideViewWithFadeAnim(new IAnimEndListener() {
            
            @Override
            public void onAnimEnd() {
                mMainView.gotoMvView();
            }
        });
    }
    
    public void setSmallMvFocus() {
        if (mMainViewContainer == null || mMainView == null) {
            return;
        }
        
        if (mMainViewContainer.isOnAnim()) {
            return;
        }
        if (mMainViewContainer.isVisible()) {
            mMainView.setMvFrameFocus();
        }
    }
    
    public int getSmallMvId() {
        if (mMainView != null) {
            mMainView.getBottomWidget().getMvFrame().getId();
        }
        return -1;
    }
    
    public void setSmallMvNextLeftFocusId(int id) {
        if (mMainView != null) {
            mMainView.getBottomWidget().getMvFrame().setNextFocusLeftId(id);
        }
        return;
    }
    
    public void setSmallMvNextRightFocusId(int id) {
        if (mMainView != null) {
            mMainView.getBottomWidget().getMvFrame().setNextFocusRightId(id);
        }
        return;
    }
    
    public void setSmallMvNextDownFocusId(int id) {
        if (mMainView != null) {
            mMainView.getBottomWidget().getMvFrame().setNextFocusDownId(id);
        }
        return;
    }
    
    public void setSmallMvNextUpFocusId(int id) {
        if (mMainView != null) {
            mMainView.getBottomWidget().getMvFrame().setNextFocusUpId(id);
        }
        return;
    }
    
    /**
     * [功能说明] home按键处理
     */
    public void backToHome() {
        if (mMainViewContainer == null || mMainView == null) {
            return;
        }
        
        if (mMainViewContainer.isOnAnim()) {
            return;
        }
        if (!mMainViewContainer.isVisible()) {
            mMainViewContainer.showViewWithAlphaAnim();
            mMainView.gotoMainView();
        }
        mMainViewContainer.backToHome();
    }
    
    public boolean handleStatusBarKeyEvent(int btnType,int arg1, KeyEvent arg2) {
        if (mMainViewContainer == null) {
            return false;
        }
        return mMainViewContainer.handleStatusBarKeyEvent(btnType,arg1,arg2);
    }
    
    public boolean handleSmallMVKeyEvent( int arg1, KeyEvent arg2) {
        if (mMainViewContainer == null) {
            return false;
        }
        return mMainViewContainer.handleSmallMVKeyEvent(arg1,arg2);
    }
    
    public boolean focusBackToScrollView() {
        if (mMainViewContainer == null) {
            return false;
        }
        return mMainViewContainer.focusBackToScrollView();
    }
    
    public void huodongUpdate() {
        if (mMainViewContainer != null) {
            mMainViewContainer.huodongUpdate();
        }
    }
    
    public void huodongReady() {
        if (mMainViewContainer != null) {
            mMainViewContainer.huodongReady();
        }
    }
    /**
     * [功能说明]主界面是否是可见状态
     * @return true 可见  false 不可见
     */
    public boolean isMainViewVisible() {
        return mMainView != null && mMainView.isMainViewVisible();
//        return mMainViewContainer != null && mMainViewContainer.isVisible();
    }
    
    /**
     * [功能说明]显示状态栏已点数量控件
     */
    public void showStatusBarSelectedNum() {
        if (mMainView != null) {
            mMainView.showStatusBarSelectedNum();
        }
    }
    
    /**
     * [功能说明]隐藏状态栏已点数量控件
     */
    public void hideStatusBarSelectedNum() {
        if (mMainView != null) {
            mMainView.hideStatusBarSelectedNum();
        }
    }
    
    /**
     * [功能说明]显示状态栏搜索框
     */
    public void showStatuBarSearchBtn() {
        if (mMainView != null) {
            mMainView.showSearchBtn();
        }
    }
    
    /**
     * [功能说明]重置搜索栏焦点
     */
    public void resetSearchBtnFocus() {
        if (mMainView != null) {
            mMainView.resetSearchBtnFocus();
        }
    }
    
    /**
     * [功能说明]隐藏状态栏搜索框
     */
    public void hideStatusBarSearchBtn() {
        if (mMainView != null) {
            mMainView.hideSearchBtn();
        }
    }
    
    public void setChargePayBtnResId(int resId) {
        if (mMainView != null) {
            mMainView.getStatusBar().setChargePayBtnResId(resId);
        }
    }
    
    public void openSelectedView() {
        mMainViewContainer.openSelectedView();
    }
    
    public void openGlobalSearchView() {
        mMainViewContainer.openGlobalSearchView();
    }
    
    /*private PlayCtrlHandler mPlayCtrlHandler;

    public void setPlayCtrlHandler(PlayCtrlHandler handler) {
        mPlayCtrlHandler = handler;
        
    }*/
    
    public void cutSong() {
      /*  if (mPlayCtrlHandler != null) {
            EvLog.i("start cut song --------");
            Message msg = new Message();
            msg.what = PlayCtrlHandler.PLAY_CTRL_MSG_CUT_SONG;
            mPlayCtrlHandler.sendMessage(msg);
        }   */
        PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_CUT_SONG);
    }
    
    public void openSongMenuDetailsView(SongMenu menu) {
        if (mMainViewContainer != null && menu != null) {
            Log.i("gsp", "handleJump:打开歌单里面的精选热门歌曲的详情页是什么 "+menu);
            mMainViewContainer.openSongMenuDetailsView(menu);
        }
    }
    
    public void openRankView(int rankId) {
        if (mMainViewContainer != null) {
            mMainViewContainer.openAssignRankView(rankId);
        }
    }
}
