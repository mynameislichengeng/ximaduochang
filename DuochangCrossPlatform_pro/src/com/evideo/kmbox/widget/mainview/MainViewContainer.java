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

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.FrameLayout;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.SystemConfigManager;
import com.evideo.kmbox.activity.MainActivity;
import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
import com.evideo.kmbox.model.observer.activity.ActivitySubject;
import com.evideo.kmbox.model.observer.activity.IActivityObserver;
import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.model.songmenu.SongMenu;
import com.evideo.kmbox.model.songmenu.SongMenuManager;
import com.evideo.kmbox.model.songmenu.SongMenuManager.ISongMenuListListener;
import com.evideo.kmbox.model.umeng.EventConst;
import com.evideo.kmbox.model.umeng.PageName;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.anim.BaseAnim;
import com.evideo.kmbox.widget.anim.FadeIn;
import com.evideo.kmbox.widget.anim.FadeOut;
import com.evideo.kmbox.widget.anim.ZoomIn;
import com.evideo.kmbox.widget.anim.ZoomOut;
import com.evideo.kmbox.widget.common.CommonDialog;
import com.evideo.kmbox.widget.common.ToastUtil;
import com.evideo.kmbox.widget.mainview.about.HelpCenterView;
import com.evideo.kmbox.widget.mainview.freesong.FreeSongView;
import com.evideo.kmbox.widget.mainview.globalsearch.GlobalSearchView;
import com.evideo.kmbox.widget.mainview.homepage.HomePageView;
import com.evideo.kmbox.widget.mainview.selected.SelectedView;
import com.evideo.kmbox.widget.mainview.singer.SingerDetailsView;
import com.evideo.kmbox.widget.mainview.singer.SingerView;
import com.evideo.kmbox.widget.mainview.singer.SingerView.ISingerClickListener;
import com.evideo.kmbox.widget.mainview.songmenu.SongMenuDetailsView;
import com.evideo.kmbox.widget.mainview.songmenu.SongMenuView;
import com.evideo.kmbox.widget.mainview.songname.SongNameView;
import com.evideo.kmbox.widget.mainview.songtop.SongTopView;
import com.evideo.kmbox.widget.mainview.usercenter.UserCenterSimpleView;

/**
 * [功能说明]主界面
 */
public class MainViewContainer extends FrameLayout implements IActivityObserver, IMainViewCallback,
        ISongMenuListListener, ISingerClickListener {

    /**
     * [渐隐渐现动画持续时间]
     */
    public static final int ANIM_DURATION = 100;

    private Activity mActivity;

    private AbsBaseView mCurrentView;

    private HomePageView mHomePageView;

    private View mFocusedView;

    private BaseAnim mZoomInAnim;
    private BaseAnim mZoomOutAnim;

    private BaseAnim mFadeInAnim;
    private BaseAnim mFadeOutAnim;
    private GlobalSearchView mSearchView;
    private SongMenuView mSongMenuView;
    private SongNameView mSongNameView;
    private UserCenterSimpleView mUserCenterView;
    private HelpCenterView mHelpCenterView;
    private SelectedView mSelectedView;
    private SingerView mSingerView;
    private SongTopView mTopView;
    private SingerDetailsView mSingerDetailsView;
    private SongMenuDetailsView mSongMenuDetailsView;
    private FreeSongView mFreeSongView;

    /**
     * [我的空间]
     */
//    private MySpaceView mMySpaceView = null;
    private View mLastViewInHomePageScrollView = null;


    public Activity getActivity() {
        return mActivity;
    }

    /**
     * @param
     */
    public MainViewContainer(Activity activity) {
        super(activity);
        mActivity = activity;
        initView(activity);
        initAnimator();
        mLastViewInHomePageScrollView = findViewById(R.id.singer);
    }

    public int getCurrentViewId() {
        if (mCurrentView != null) {
            return mCurrentView.getViewId();
        }
        return -1;
    }

    public void updateFreeSong(String text) {
        if (mFreeSongView != null) {
            mFreeSongView.updatePlayInfo(text);
        }
    }

    private void initView(Activity activity) {
        mHomePageView = new HomePageView(activity, -1, this);
        mHomePageView.setPageName(PageName.HOME_PAGE);
        addContentView(mHomePageView);
    }

    public void huodongUpdate() {
        mHomePageView.huodongUpdate();
    }

    public void huodongReady() {
        mHomePageView.huodongReady();
    }

    public void huodongPause(){
        mHomePageView.removeHandlerHuadong();
    }
    public void huodongResume(){
        mHomePageView.sendHandlerHuadong();
    }

    public void updateHomeView() {
        mHomePageView.updateHomeView();
    }

    public void updateActivityView(String path) {
//        mHomePageView.updateActivityView(path);
    }


    private void addContentView(AbsBaseView view) {
        if (view == null) {
            return;
        }
        removeAllViews();
        addView(view);
        mCurrentView = view;
    }

    private void initAnimator() {
        // 渐进动画
        mZoomInAnim = new ZoomIn();
        mZoomInAnim.setDuration(ANIM_DURATION);
        mZoomInAnim.setAnimListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                setVisibility(View.VISIBLE);
            }
        });

        // 渐出动画
        mZoomOutAnim = new ZoomOut();
        mZoomOutAnim.setDuration(ANIM_DURATION);
        mZoomOutAnim.setAnimListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.GONE);
            }
        });

        mFadeInAnim = new FadeIn();
        mFadeInAnim.setDuration(ANIM_DURATION);
        mFadeInAnim.setAnimListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                setVisibility(View.VISIBLE);
            }
        });

        mFadeOutAnim = new FadeOut();
        mFadeOutAnim.setDuration(ANIM_DURATION);
        mFadeOutAnim.setAnimListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mAnimEndListener != null) {
                    mAnimEndListener.onAnimEnd();
                }
                setVisibility(View.GONE);
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

    public boolean restoreFocusedView() {
        boolean flag = false;
        if (mFocusedView != null) {
            flag = mFocusedView.requestFocus();
        }
        if (mFreeSongView != null) {
            mFreeSongView.focusBackFromMV();
            flag = true;
        }
        EvLog.d("restoreFocusedView flag=" + flag + ",mFocusedView=" + mFocusedView);
        return flag;
    }

    public boolean handleStatusBarKeyEvent(int btnType, int arg1, KeyEvent arg2) {
        if (arg2.getAction() == KeyEvent.ACTION_DOWN) {
            if (arg1 == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (mCurrentView != null) {
                    EvLog.i(mCurrentView.getViewId() + " onStatusBarDownKey");
                    return mCurrentView.onStatusBarDownKey();
                }
            } else if (arg1 == KeyEvent.KEYCODE_DPAD_UP) {
                return true;
            }
        }
        return false;
    }

    public boolean handleSmallMVKeyEvent(int arg1, KeyEvent arg2) {
        //FIXME
        if (mCurrentView != null && mCurrentView.getViewId() == MainViewId.ID_HOME_PAGE) {
            if (arg2.getAction() == KeyEvent.ACTION_DOWN) {
                if (arg1 == KeyEvent.KEYCODE_DPAD_RIGHT ||
                    /*arg1 == KeyEvent.KEYCODE_DPAD_LEFT ||
                    arg1 == KeyEvent.KEYCODE_DPAD_DOWN ||*/
                        arg1 == KeyEvent.KEYCODE_DPAD_UP) {
                    return focusBackToScrollView();
                }
            }
        } else {
            if (arg2.getAction() == KeyEvent.ACTION_DOWN) {
                if (arg1 == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    if (mCurrentView != null) {
                        EvLog.i(mCurrentView.getViewId() + " onSmallMVRightKey");
                        return mCurrentView.onSmallMVRightKey();
                    }
                } else if (arg1 == KeyEvent.KEYCODE_DPAD_UP) {
                    if (mCurrentView != null) {
                        EvLog.i(mCurrentView.getViewId() + " onSmallMVUpKey");
                        return mCurrentView.onSmallMVUpKey();
                    }
                }
            }
        }
        return false;
    }

    private static final int THRESHOLD_TIME = 500;
    private static final int MIN_KEYDOWN_NUM_TO_SHOW_ALL_OPTIONS = 8;
    private ArrayList<Integer> mKeyCodeList = new ArrayList<Integer>();
    private long mCurrTime = 0;



    public boolean focusBackToScrollView() {
        if (mLastViewInHomePageScrollView != null) {
            mLastViewInHomePageScrollView.requestFocus();
            return true;
        } else {

        }
        return false;
    }

    /**
     * [渐进动画显示]
     */
    public void showViewWithAlphaAnim() {
        if (isOnAnim()) {
            return;
        }
        mZoomInAnim.start(this);
//        mAlphaInAnim.start();
    }

    public void showViewWithFadeAnim() {
        boolean isOnAlphaInAnim = false;
        if (mFadeInAnim != null) {
            isOnAlphaInAnim = mFadeInAnim.isStarted();
        }
        boolean isOnAlphaOutAnim = false;
        if (mFadeOutAnim != null) {
            isOnAlphaOutAnim = mFadeOutAnim.isStarted();
        }
        if (!isOnAlphaInAnim && !isOnAlphaOutAnim) {
            mFadeInAnim.start(this);
        }
    }

    public interface IAnimEndListener {
        public void onAnimEnd();
    }

    private IAnimEndListener mAnimEndListener = null;

    public void hideViewWithFadeAnim(IAnimEndListener listener) {
        boolean isOnAlphaInAnim = false;
        //   boolean isOnAlphaInAnim = false;
        if (mFadeInAnim != null) {
            isOnAlphaInAnim = mFadeInAnim.isStarted();
        }
        boolean isOnAlphaOutAnim = false;
        if (mFadeOutAnim != null) {
            isOnAlphaOutAnim = mFadeOutAnim.isStarted();
        }
        if (!isOnAlphaInAnim && !isOnAlphaOutAnim) {
            EvLog.e("fade out anim start");
            mAnimEndListener = listener;
            mFadeOutAnim.start(this);
        }
    }

    /**
     * [是否可见]
     *
     * @return true 可见的  false 不可见的
     */
    public boolean isVisible() {
        return getVisibility() == View.VISIBLE;
    }

    /**
     * [渐出动画隐藏菜单]
     */
    public void hideViewWithAlphaAnim() {
//        EvLog.d("something", "menu width " + width);
        if (isOnAnim()) {
            return;
        }
        mZoomOutAnim.start(this);
        // TODO zxh umeng
//        UmengAgent.getInstance().onPageStart(PageName.MAIN_ACTIVITY);
    }

    /**
     * [功能说明]是否处于动画中
     *
     * @return true 是  false 不是
     */
    public boolean isOnAnim() {
        boolean isOnAlphaInAnim = false;
        if (mZoomInAnim != null) {
            isOnAlphaInAnim = mZoomInAnim.isStarted();
        }
        boolean isOnAlphaOutAnim = false;
        if (mZoomOutAnim != null) {
            isOnAlphaOutAnim = mZoomOutAnim.isStarted();
        }
        return isOnAlphaInAnim || isOnAlphaOutAnim;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ActivitySubject.getInstance().registActivityObserver(this);
        SongMenuManager.getInstance().registSongMenuListListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ActivitySubject.getInstance().unregistActivityObserver(this);
        SongMenuManager.getInstance().unregistSongMenuListListener(this);
    }

    public void backToHome() {
        if (mCurrentView != null && mCurrentView == mHomePageView) {
            mHomePageView.firstItemRequestFocus();
            return;
        }

//        int backViewId = mCurrentView.getBackViewId();
        int currentViewId = mCurrentView.getViewId();

        addContentView(mHomePageView);
        MainViewManager.getInstance().showStatuBarSearchBtn();
        switch (currentViewId) {
            case MainViewId.ID_SINGER_DETAILS:
                mSingerDetailsView = null;
                break;
            case MainViewId.ID_SONG_NAME:
                mSongNameView = null;
                break;
            case MainViewId.ID_SONG_MENU:
                mSongMenuView = null;
                break;

            case MainViewId.ID_USER_SPACE:
                mUserCenterView = null;
                break;
            case MainViewId.ID_FREE_SONG:
                mFreeSongView = null;
                break;
            case MainViewId.ID_SONG_MENU_DETAILS:
                mSongMenuDetailsView = null;
                break;
            case MainViewId.ID_ABOUT:
                mHelpCenterView = null;
                break;
            case MainViewId.ID_SELECTED:
                mSelectedView = null;
                break;
            case MainViewId.ID_SINGER:
                mSingerView = null;
                break;
          /*  case MainViewId.ID_LOCALSONG:
                mLocalSongView = null;
                break;*/
            case MainViewId.ID_SEARCH:
                mSearchView = null;
                break;
            case MainViewId.ID_TOP:
                mTopView = null;
                break;
            default:
                break;
        }

        mHomePageView.firstItemRequestFocus();
        return;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onBackPressed() {
        if (mCurrentView != null && mCurrentView == mHomePageView) {
            exitApp();
            return true;
        }

        int backViewId = mCurrentView.getBackViewId();
        int currentViewId = mCurrentView.getViewId();
        
       /* if (currentViewId == MainViewId.ID_LOCALSONG && !mLocalSongView.isInNormalMode()) {
            EvLog.d("backToNormalMode ");
            mLocalSongView.backToNormalMode();
            return true;
        }*/
        if (backViewId == MainViewId.ID_HOME_PAGE && mCurrentView != null) {
            mCurrentView.clickExitKey();
        }
        EvLog.d("on back pressed " + backViewId + "," + currentViewId);
        if (backViewId == currentViewId) {
            EvLog.e("error view,backViewId=" + backViewId);
        }
        switch (backViewId) {
            case MainViewId.ID_SINGER_DETAILS:
                if (mSingerDetailsView != null) {
                    addContentView(mSingerDetailsView);
                } else {
                    addContentView(mHomePageView);
                }
                break;
            case MainViewId.ID_SONG_NAME:
                addContentView(mSongNameView);
                break;
            case MainViewId.ID_SONG_MENU:
                addContentView(mSongMenuView);
                break;

            case MainViewId.ID_USER_SPACE:
                addContentView(mUserCenterView);
                break;
            case MainViewId.ID_FREE_SONG:
                addContentView(mFreeSongView);
                break;
            case MainViewId.ID_SONG_MENU_DETAILS:
                addContentView(mSongMenuDetailsView);
                break;
            case MainViewId.ID_ABOUT:
                addContentView(mHelpCenterView);
                break;
            case MainViewId.ID_HOME_PAGE:
                addContentView(mHomePageView);
                break;
            case MainViewId.ID_SINGER:
                addContentView(mSingerView);
                break;
           /* case MainViewId.ID_LOCALSONG:
                addContentView(mLocalSongView);
                break;*/
            case MainViewId.ID_TOP:
                addContentView(mTopView);
                break;
            case MainViewId.ID_SEARCH:
                addContentView(mSearchView);
                break;
            case MainViewId.ID_SELECTED:
                addContentView(mSelectedView);
                break;
            default:
                break;
        }
        switch (currentViewId) {
            case MainViewId.ID_SINGER_DETAILS:
                mSingerDetailsView = null;
                break;
            case MainViewId.ID_SONG_NAME:
                mSongNameView = null;
                break;
            case MainViewId.ID_SONG_MENU:
                mSongMenuView = null;
                break;

            case MainViewId.ID_USER_SPACE:
                mUserCenterView = null;
                break;
            case MainViewId.ID_FREE_SONG:
                mFreeSongView = null;
                break;
            case MainViewId.ID_SONG_MENU_DETAILS:
                mSongMenuDetailsView = null;
                break;
            case MainViewId.ID_ABOUT:
                mHelpCenterView = null;
                break;
            case MainViewId.ID_SELECTED:
                mSelectedView = null;
                break;
            case MainViewId.ID_SINGER:
                mSingerView = null;
                break;
            /*case MainViewId.ID_LOCALSONG:
                mLocalSongView = null;
                break;*/
            case MainViewId.ID_SEARCH:
                mSearchView = null;
                MainViewManager.getInstance().showStatuBarSearchBtn();
                MainViewManager.getInstance().resetSearchBtnFocus();
                break;
            case MainViewId.ID_TOP:
                mTopView = null;
                break;
            default:
                break;
        }

        return false;
    }

    private void exitApp() {
        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).showExitDialog();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openView(int viewId, int backViewId) {
        switch (viewId) {
            case MainViewId.ID_SONG_NAME:
                openSongNameView(backViewId);
                break;
            case MainViewId.ID_SONG_MENU:
                openSongMenuView(backViewId);
                break;

            case MainViewId.ID_USER_SPACE:
                openUserCenterView(backViewId);
                break;
            case MainViewId.ID_ABOUT:
                openAboutView(backViewId);
                break;
            case MainViewId.ID_SELECTED:
                openSelectedView(backViewId);
                break;
            case MainViewId.ID_SINGER:
                openSingerView(backViewId);
                break;
           /* case MainViewId.ID_LOCALSONG:
                openLocalSongView(backViewId);
                break;*/
            case MainViewId.ID_SETTING:
                openSettingView(backViewId);
                break;
//            case MainViewId.ID_SEARCH:
//                openGlobalSearchView();
//                MainViewManager.getInstance().hideStatusBarSearchBtn();
//                break;
            case MainViewId.ID_TOP:
                openSongTopView(0, backViewId);
                break;
            case MainViewId.ID_FREE_SONG: {
                if (mFreeSongView == null) {
                    mFreeSongView = new FreeSongView(mActivity, backViewId);
                }
                mFreeSongView.setPageName(PageName.FREE_SONG_PAGE);
                addContentView(mFreeSongView);
                break;
            }
            default:
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openSongMenuDetailsView(SongMenu songMenu) {
        Log.i("gsp", "openSongMenuDetailsView: 调用的歌单是"+songMenu.toString());
        openSongMenuDetailsView(songMenu, MainViewId.ID_HOME_PAGE);
    }


   /* private void openLocalSongView(int backViewId) {
        if (mLocalSongView == null) {
            mLocalSongView = new LocalSongView(mActivity, backViewId);
        }
        mLocalSongView.setPageName(PageName.MENU_LOCALDBMNG);
        addContentView(mLocalSongView);
    }*/

    private void openSettingView(int backViewId) {
        /*ComponentName cn = new ComponentName("com.evideo.kmbox",
                "com.evideo.kmbox.activity.DebugActivity");
        Intent intent = new Intent();
        intent.setComponent(cn);
        mActivity.startActivity(intent);*/
//        ThirdPartyApps.getInstance().startKmBoxSetting();
        LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_SYS_SETTING);
    }

    private void openSingerView(int backViewId) {
        if (mSingerView == null) {
            mSingerView = new SingerView(mActivity, backViewId);
        }
        mSingerView.setSingerClickListener(this);
        mSingerView.setPageName(PageName.MENU_SINGER);
        addContentView(mSingerView);
    }

    private void openSongNameView(int backViewId) {
        if (mSongNameView == null) {
            mSongNameView = new SongNameView(mActivity, backViewId);
            mSongNameView.setPageName(PageName.MENU_SONGNAME);
        }
        addContentView(mSongNameView);
    }

    private void openSongMenuView(int backViewId) {
        if (mSongMenuView == null) {
            mSongMenuView = new SongMenuView(mActivity, backViewId);
            mSongMenuView.setPageName(PageName.MENU_SONGMENUDETAILS);
        }
        addContentView(mSongMenuView);
    }


    private void openUserCenterView(int backViewId) {
        if (mUserCenterView == null) {
            mUserCenterView = new UserCenterSimpleView(mActivity, backViewId);
        }
        mUserCenterView.setPageName(PageName.USER_SPACE);
        addContentView(mUserCenterView);
    }

    private void openSongMenuDetailsView(SongMenu songMenu, int backViewId) {
        if (mSongMenuDetailsView == null) {
            mSongMenuDetailsView = new SongMenuDetailsView(mActivity, songMenu, backViewId);
        }
        mSongMenuDetailsView.setPageName(PageName.MENU_SONG_MENU_DETAILS);
        addContentView(mSongMenuDetailsView);
    }

    private void openSingerDeatailsView(Singer singer, int backViewId) {
        if (singer == null) {
            return;
        }

        if (mSingerDetailsView == null
                || mSingerDetailsView.getSinger().getId() != singer.getId()) {
            mSingerDetailsView = null;
            mSingerDetailsView = new SingerDetailsView(mActivity, singer, backViewId);
        }
        mSingerDetailsView.setPageName(PageName.MENU_SINGER_MENU_DETAILS);
        addContentView(mSingerDetailsView);
    }

    private void openSongTopView(int assginRankId, int backViewId) {
        if (mTopView == null) {
            mTopView = new SongTopView(mActivity, backViewId);
        }
        mTopView.setDefaultFocusSongTopId(assginRankId);
        mTopView.setPageName(PageName.MENU_RANK);
        addContentView(mTopView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSongMenuSelected(SongMenu songMenu) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSongMenuDataChanged() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSongMenuClicked(SongMenu songMenu) {
        openSongMenuDetailsView(songMenu, MainViewId.ID_SONG_MENU);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSingerItemClick(Singer singer, int backId) {
        if (backId == MainViewId.ID_SEARCH) {
            MainViewManager.getInstance().showStatuBarSearchBtn();
        }
        openSingerDeatailsView(singer, backId);
    }

    private void openAboutView(int backViewId) {
        /*if (DeviceInfoUtils.isLianTongShenYin()) {
            if (mAboutView == null) {
                mAboutView = new AboutLianTongView(mActivity, backViewId);
            }
        } else {
            if (mAboutView == null) {
                mAboutView = new AboutView(mActivity, backViewId);
            }
        }*/
        if (mHelpCenterView == null) {
            mHelpCenterView = new HelpCenterView(mActivity, backViewId);
        }
        mHelpCenterView.setPageName(PageName.HELP_CENTER);
        addContentView(mHelpCenterView);
    }


    private void checkFocus() {
        View view = findFocus();
        if (view != null) {
//            EvLog.d("find focus view id=" + view.getId());
            mLastViewInHomePageScrollView = view;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mCurrentView.getViewId() == MainViewId.ID_HOME_PAGE) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                int keyCode = event.getKeyCode();
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    mHomePageView.playSoundEffect(SoundEffectConstants.NAVIGATION_DOWN);
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    mHomePageView.playSoundEffect(SoundEffectConstants.NAVIGATION_DOWN);
                }
                checkFocus();
            }
        } else {
            mLastViewInHomePageScrollView = null;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        EvLog.d("zxh", "main view container onKeyDown keycode " + keyCode);
        return super.onKeyDown(keyCode, event);
    }

    private void openSelectedView(int backViewId) {
        if (backViewId == MainViewId.ID_SELECTED) {
            if (mSelectedView != null) {
                mSelectedView.resetSelectorPosition();
                return;
            }
        }

        if (mSelectedView == null) {
            mSelectedView = new SelectedView(mActivity, backViewId);
        }
        mSelectedView.setPageName(PageName.ORDERED_LIST);
        addContentView(mSelectedView);
    }

    public void openSelectedView() {
        int backViewId = mCurrentView.getViewId();
        if (backViewId == MainViewId.ID_SELECTED) {
            if (mSelectedView != null) {
                mSelectedView.resetSelectorPosition();
                return;
            }
        }

        if (mSelectedView == null) {
            mSelectedView = new SelectedView(mActivity, backViewId);
        }
        addContentView(mSelectedView);
    }

    /**
     * [全局搜索视图]
     */
    public void openGlobalSearchView() {
        if (mCurrentView != null) {
            String pageName = mCurrentView.getPageName();
            if (pageName != null && pageName.equals(PageName.MENU_SEARCH)) {
                return;
            }
        }

        int backViewId = mCurrentView.getViewId();
        if (mSearchView == null) {
            mSearchView = new GlobalSearchView(mActivity, backViewId);
        }
        mSearchView.setSingerClickListener(this);
        mSearchView.setPageName(PageName.MENU_SEARCH);
        addContentView(mSearchView);
    }

    @Override
    public void openSingerDetailsView(Singer singer) {
        if (singer == null) {
            return;
        }

        if (mSingerDetailsView == null
                || mSingerDetailsView.getSinger().getId() != singer.getId()) {
            mSingerDetailsView = null;
            mSingerDetailsView = new SingerDetailsView(mActivity, singer, MainViewId.ID_HOME_PAGE);
        }
        mSingerDetailsView.setPageName(PageName.MENU_SINGER_MENU_DETAILS);
        addContentView(mSingerDetailsView);
    }

    @Override
    public void openAssignRankView(int rankId) {
        openSongTopView(rankId, MainViewId.ID_HOME_PAGE);
    }
}
