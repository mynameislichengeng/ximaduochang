package com.evideo.kmbox.widget.intonation;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.evideo.kmbox.R;
import com.evideo.kmbox.SystemConfigManager;
import com.evideo.kmbox.activity.MainActivity;
import com.evideo.kmbox.model.player.DefaultVideoRenderView;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrl;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.LoadingView;
import com.evideo.kmbox.widget.MainBottomWidget;
import com.evideo.kmbox.widget.StatusBarWidget;
import com.evideo.kmbox.widget.StatusBarWidget.IStatusBarKeyListener;
import com.evideo.kmbox.widget.common.ToastUtil;
import com.evideo.kmbox.widget.playctrl.MVCtrlWidgetManager;
import com.evideo.kmbox.widget.playctrl.PlayCtrlWidget;

public class KmMainView {

    private KmPlayListItem currentSong;
    private PlayCtrlWidget playCtrlWidget;

    public KmMainView(Context context) {
        mContext = context;
        this.init(mContext);
    }

    private Context mContext = null;

    private FrameLayout mRootView = null;

    public ViewGroup view() {
        return mRootView;
    }

    private LinearLayout mLoadingLay;

    private LoadingView mLoadingView;

    private LinearLayout mOsdContainer;

    private StatusBarWidget mStatusBar;

    public StatusBarWidget getStatusBar() {
        return mStatusBar;
    }

    private void init(Context context) {
        this.prepareSubview(context);
        gotoMainView();
    }

    private DefaultVideoRenderView mVideoView = null;

    public DefaultVideoRenderView getVideoView() {
        return mVideoView;
    }

    public void showVideoView() {
        if (mVideoView != null && mVideoView.getVisibility() != View.VISIBLE) {
            mVideoView.setVisibility(View.VISIBLE);
        }
    }

    private LinearLayout mAudioDisplayRect = null;
    private MainBottomWidget mBottomWidget = null;
   private MVCtrlWidgetManager mMVCtrlWidget = null;

    public LinearLayout getAudioDisplayRect() {
        return mAudioDisplayRect;
    }

    public void setSmallMVKeyListener(View.OnKeyListener listener) {
        if (mBottomWidget == null) {
            return;
        }
        ImageView view = mBottomWidget.getMvFrame();
        if (view == null) {
            return;
        }
        view.setOnKeyListener(listener);
    }

    public void setMvFrameFocus() {
        mBottomWidget.getMvFrame().requestFocus();
    }

    public void setSearchFocus() {
        mStatusBar.setSearchViewFocus();
    }

    public void setStatusBarKeyListener(IStatusBarKeyListener listener) {
        if (mStatusBar == null) {
            return;
        }
        mStatusBar.setKeyListener(listener);
        Log.i("gsp", "setStatusBarKeyListener: 设置搜索key的事件");
    }

    public static interface IBottomMVClickListener {
        public void onBottomMVClick();
    }

    public void setBottomMVClickListener(final IBottomMVClickListener listener) {
        mBottomWidget.getMvFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBottomMVClick();
                }
            }
        });
    }

    public View getRootView() {
        return mRootView;
    }

    private void prepareSubview(Context context) {
        mRootView = (FrameLayout) View.inflate(context,
                R.layout.launcher_main_view, null);
        mVideoView = (DefaultVideoRenderView) mRootView.findViewById(R.id.main_surfaceView);

        mStatusBar = (StatusBarWidget) mRootView.findViewById(R.id.main_status_bar);
        mBottomWidget = (MainBottomWidget) mRootView.findViewById(R.id.main_bottom_widget);
        mLoadingLay = (LinearLayout) mRootView.findViewById(R.id.launcher_main_loading_lay);
        initLoadView();

        mOsdContainer = (LinearLayout) mRootView.findViewById(R.id.linearlayout_osdview_container);
        mMVCtrlWidget= new MVCtrlWidgetManager();
        currentSong = KmPlayerCtrl.getInstance().getPlayingSong();
        playCtrlWidget = new PlayCtrlWidget(mContext);
        mVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("gsp", "onClick:MV点击事   显示播放暂停");
                MainActivity.mainActivity.showPlayCtrlView();
//                playCtrlWidget.show();

            }
        });
    }

    public LinearLayout getOSDContainer() {
        return mOsdContainer;
    }

    private void initLoadView() {
        if (mLoadingView == null) {
            mLoadingView = new LoadingView(mContext);
            if (mLoadingLay != null) {
                mLoadingLay.addView(mLoadingView);
            }
        }
    }

    public void showLoadingView(String content) {
        if (mLoadingView != null) {
            mLoadingView.showLoadingView(content);
        }
    }

    public void showLoadingView(int resid) {
        if (mLoadingView != null) {
            mLoadingView.showLoadingView(resid);
        }
    }

    public void dismissLoadingView() {
        if (mLoadingView != null) {
            mLoadingView.dismissLoadingView();
        }
    }

    private LinearLayout mCountDownContainer = null;

    /**
     * [功能说明]更新主界面正在播放歌曲信息
     *
     * @param currentSong 正在播放的歌曲
     */
    public void updateMainViewCurrentSong(String currentSong) {
        if (mBottomWidget != null) {
            mBottomWidget.updateSongTv(currentSong == null ? "" : currentSong);
        }
    }

    public MainBottomWidget getBottomWidget() {
        return mBottomWidget;
    }

    public boolean isMainViewVisible() {
        if (mBottomWidget != null) {
            return mBottomWidget.getVisibility() == View.VISIBLE;
        }
        return false;
    }

    /**
     * [功能说明]进入mv界面
     */
    public void gotoMvView() {
        zoomInSurfaceView();
        if (mOsdContainer != null) {
            mOsdContainer.setVisibility(View.VISIBLE);
        }
        if (mLoadingLay != null) {
            mLoadingLay.setVisibility(View.VISIBLE);
        }
        if (mCountDownContainer != null) {
            mCountDownContainer.setVisibility(View.VISIBLE);
        }

        if (mBottomWidget != null) {
            mBottomWidget.setVisibility(View.GONE);
        }
        getStatusBar().changeToMvState();
        getStatusBar().hideDbUpdateTv();

        showVideoView();
    }

    /**
     * [功能说明]进入主界面
     */
    public void gotoMainView() {
        zoomOutSurfaceView();
        if (mOsdContainer != null) {
            mOsdContainer.setVisibility(View.INVISIBLE);
        }
        if (mLoadingLay != null) {
            mLoadingLay.setVisibility(View.INVISIBLE);
        }
        if (mCountDownContainer != null) {
            mCountDownContainer.setVisibility(View.INVISIBLE);
        }
        if (mBottomWidget != null) {
            mBottomWidget.setVisibility(View.VISIBLE);
        }

        getStatusBar().changeToMainViewState();
        getStatusBar().showDbUpdateTv();
    }



    /**
     * [功能说明]放大mv界面
     */
    public void zoomInSurfaceView() {
        if (mVideoView == null) {
            return;
        }
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mVideoView.getLayoutParams();
        lp.width = (int) mContext.getResources().getDimension(R.dimen.px1920);
        lp.height = (int) mContext.getResources().getDimension(R.dimen.px1080);
        lp.topMargin = 0;
        lp.leftMargin = 0;

        mVideoView.setLayoutParams(lp);
        mOsdContainer.setVisibility(View.VISIBLE);
        showVideoView();

    }


    private void showPlayCtrlView() {


        if (mMVCtrlWidget.isPlayCtrlWidgetShowing()) {
            Log.i("gsp", "onClick: 我被点击了 哈哈哈哈哈哈哈哈哈哈哈哈");
            return;
        }

        boolean gradeOpen = false;
        boolean showGradeBtn = false;



        if (showGradeBtn) {
            mMVCtrlWidget.showPlayWidgetWithGradeBtn(gradeOpen);
        } else {
            Log.i("gsp", "onClick: MV界面点击事件显示框");
            mMVCtrlWidget.showPlayWidgetWithoutGradeBtn();
        }
    }

    private LinearLayout.LayoutParams mSmallVideoPrevRect = null;

    public void showMvAtAssignRect(LinearLayout.LayoutParams lp) {
        if (mSmallVideoPrevRect == null) {
            mSmallVideoPrevRect = new LinearLayout.LayoutParams(lp.width, lp.height);
            mSmallVideoPrevRect.leftMargin = lp.leftMargin;
            mSmallVideoPrevRect.topMargin = lp.topMargin;
        }
        mVideoView.setLayoutParams(lp);
        mBottomWidget.hideSmallMv();
    }

    public void resetMvRect() {
        mSmallVideoPrevRect = null;
    }

    private int mSmallMVWidth = 0;
    private int mSmallMVHeight = 0;

    /**
     * [功能说明]缩小mv界面
     */
    public void zoomOutSurfaceView() {
        if (mVideoView == null) {
            return;
        }

        if (SystemConfigManager.SHOW_SMALL_MV) {
            if (mSmallVideoPrevRect == null) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mVideoView
                        .getLayoutParams();
                if (mSmallMVWidth == 0) {
                    mSmallMVWidth = mContext.getResources().getDimensionPixelSize(
                            R.dimen.px320);
                }
                lp.width = mSmallMVWidth;
                if (mSmallMVHeight == 0) {
                    mSmallMVHeight = mContext.getResources().getDimensionPixelSize(
                            R.dimen.px185);
                }
                lp.height = mSmallMVHeight;
                lp.topMargin = mBottomWidget.getTVFrameMarginTop() + mContext.getResources().getDimensionPixelSize(
                        R.dimen.px2);
                lp.leftMargin = mBottomWidget.getTVFrameMarginLeft();

                mVideoView.setLayoutParams(lp);
                mBottomWidget.showSmallMvFrame();
            } else {
                mVideoView.setLayoutParams(mSmallVideoPrevRect);
            }
        }
        mOsdContainer.setVisibility(View.INVISIBLE);
    }

    public void saveFocusedView() {
        if (mBottomWidget != null) {
            mBottomWidget.saveFocusedView();
        }
        if (mStatusBar != null) {
            mStatusBar.saveFocusedView();
        }
    }

    public void restoreFocusedView() {
        if (mBottomWidget != null && mBottomWidget.restoreFocusedView()) {
            return;
        }
        if (mStatusBar != null) {
            mStatusBar.restoreFocusedView();
        }
    }

    public void showStatusBarSelectedNum() {
        if (mStatusBar != null) {
            mStatusBar.showSelectedNum();
        }
    }

    public void hideStatusBarSelectedNum() {
        if (mStatusBar != null) {
            mStatusBar.hideSelectedNum();
        }
    }

    private LinearLayout mPlayBackHint = null;

    public void updatePlayBackInfo(String info) {
        if (mPlayBackHint != null) {
            mPlayBackHint.setVisibility(View.VISIBLE);
        }
    }

    public void hidePlayBackHint() {
        if (mPlayBackHint != null) {
            mPlayBackHint.setVisibility(View.GONE);
        }
    }

    public void showSearchBtn() {
        if (mStatusBar != null) {
            mStatusBar.showSearchBar();
        }
    }

    public void resetSearchBtnFocus() {
        if (mStatusBar != null) {
            mStatusBar.resetSearchBtnFocus();
        }
    }

    public void hideSearchBtn() {
        if (mStatusBar != null) {
            mStatusBar.hideSearchBar();
        }
    }
}
