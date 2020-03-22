/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年9月20日     "dinghaiqiang"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.songmenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.exception.DCNoResultException;
import com.evideo.kmbox.exception.DataCenterCommuException;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
import com.evideo.kmbox.model.playerctrl.list.FavoriteListManager;
import com.evideo.kmbox.model.playerctrl.list.FavoriteListManager.IFavoriteListListener;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager.IPlayListListener;
import com.evideo.kmbox.model.playerctrl.list.SongOperationManager;
import com.evideo.kmbox.model.playerctrl.list.SongOperationManager.IOrderSongResultListener;
import com.evideo.kmbox.model.playerctrl.list.SongOperationManager.ITopSongResultListener;
import com.evideo.kmbox.model.songmenu.SongMenu;
import com.evideo.kmbox.model.songmenu.SongMenuDetailManager;
import com.evideo.kmbox.model.songmenu.SongMenuManager;
import com.evideo.kmbox.model.songmenu.SongMenuManager.ISongMenuListListener;
import com.evideo.kmbox.model.umeng.EventConst;
import com.evideo.kmbox.model.umeng.UmengAgent;
import com.evideo.kmbox.presenter.BasePresenter;
import com.evideo.kmbox.presenter.PageLoadPresenter.ILoadCacheDataCallback;
import com.evideo.kmbox.presenter.PageLoadPresenter.IPageLoadCallback;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.AnimLoadingView;
import com.evideo.kmbox.widget.common.OnItemClickCallback;
import com.evideo.kmbox.widget.common.OnSongListKeyDownEventListener;
import com.evideo.kmbox.widget.common.RoundRectImageView;
import com.evideo.kmbox.widget.common.SongListView;
//import com.evideo.kmbox.widget.common.VerticalSeekBar;
import com.evideo.kmbox.widget.mainmenu.order.OrderSongListAdapter;
import com.evideo.kmbox.widget.mainview.AbsBaseView;
import com.evideo.kmbox.widget.mainview.BreadCrumbsWidget;
import com.evideo.kmbox.widget.mainview.MainViewId;
import com.evideo.kmbox.widget.mainview.MainViewManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * [主界面--歌单歌曲列表界面]
 */
public class SongMenuDetailsView extends AbsBaseView
        implements IPageLoadCallback<Song>, ILoadCacheDataCallback<Song>,
        ISongMenuListListener, IPlayListListener, IFavoriteListListener,
        IOrderSongResultListener, ITopSongResultListener {

    private final String TAG = SongMenuDetailsView.class.getName();

    private static final int PAGE_LOAD_EDGE_COUNT = 8;
    private float mItemCoverRoundRectRadius;

    private SongListView mListView;
    private OrderSongListAdapter mAdapter;
    private ArrayList<Song> mDatas = null;

    private View mSongMenuItemView;

    private int mCurrentSongMenuId = Integer.MAX_VALUE;
    private int mTotalNum;
    private SongMenu mFirstSongMenu;

    /**
     * [歌单简介视图]
     */
    RoundRectImageView mCoverIv;
    TextView mNameTv;
    TextView mDescriptionTv;
    private TextView mTotalNumTv = null;
    private DisplayImageOptions mOptions;
    private BreadCrumbsWidget mBreadcrumbsWidget = null;
    private AnimLoadingView mLoadingView = null;

    private void setImageOptions() {
        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.song_menu_cover_default_big)
                .showImageOnFail(R.drawable.song_menu_cover_default_big)
                .showImageOnLoading(R.drawable.song_menu_cover_default_big)
                .build();
    }

    private void newIntroductionView() {

        mSongMenuItemView = (View) findViewById(R.id.song_menu_details_item_lay);
        mCoverIv = (RoundRectImageView) findViewById(R.id.song_menu_details_item_cover);
        mCoverIv.setRadius(mItemCoverRoundRectRadius);
        mNameTv = (TextView) mSongMenuItemView.findViewById(R.id.song_menu_details_item_name_tv);
        mDescriptionTv = (TextView) mSongMenuItemView.findViewById(
                R.id.song_menu_details_item_description_tv);

        mBreadcrumbsWidget = (BreadCrumbsWidget) findViewById(R.id.song_menu_details_crumb);
    }

    /**
     * [填充歌单简介的视图数据，除了非儿童和戏曲的封面]
     */
    protected void fillIntroductionViewData(SongMenu songMenu) {

        if (SongMenu.SONG_MENU_ID_CHILD == songMenu.songMenuId) {
            if (getBackViewId() == MainViewId.ID_HOME_PAGE) {
                mBreadcrumbsWidget.setFirstTitle(getString(R.string.song_menu_child));
            } else {
                mBreadcrumbsWidget.setFirstTitle(getString(R.string.song_menu));
                mBreadcrumbsWidget.setSecondTitle(getString(R.string.song_menu_child));
            }
            mDescriptionTv.setText(R.string.song_menu_child_description);
            mNameTv.setText(R.string.song_menu_child);
            ImageLoader.getInstance().cancelDisplayTask(mCoverIv);
            mCoverIv.setImageResource(R.drawable.ic_song_menu_child_big);
        } else if (SongMenu.SONG_MENU_ID_DRAMA == songMenu.songMenuId) {
            if (getBackViewId() == MainViewId.ID_HOME_PAGE) {
                mBreadcrumbsWidget.setFirstTitle(getString(R.string.song_menu_drama));
            } else {
                mBreadcrumbsWidget.setFirstTitle(getString(R.string.song_menu));
                mBreadcrumbsWidget.setSecondTitle(getString(R.string.song_menu_drama));
            }
            mDescriptionTv.setText(R.string.song_menu_drama_description);
            mNameTv.setText(R.string.song_menu_drama);
            ImageLoader.getInstance().cancelDisplayTask(mCoverIv);
            mCoverIv.setImageResource(R.drawable.ic_song_menu_drama_big);
        } else {
            mNameTv.setText(songMenu.name);
            mBreadcrumbsWidget.setFirstTitle(getString(R.string.song_menu));
            mBreadcrumbsWidget.setSecondTitle(songMenu.name);
            if (TextUtils.isEmpty(songMenu.description)) {
                mDescriptionTv.setVisibility(View.GONE);
                mDescriptionTv.setText("");
            } else {
                mDescriptionTv.setVisibility(View.VISIBLE);
                mDescriptionTv.setText(songMenu.description);
            }
        }
    }

    /**
     * [显示歌单封面]
     */
    protected void showSongMenuCover() {
//        EvLog.d("showSongMenuCover mCurrentSongMenuId:" + mCurrentSongMenuId);
        if (SongMenu.SONG_MENU_ID_CHILD == mCurrentSongMenuId || SongMenu.SONG_MENU_ID_DRAMA == mCurrentSongMenuId) {
            return;
        }
        SongMenu songMenu = SongMenuManager.getInstance().getSongMenuById(mCurrentSongMenuId);
        if (songMenu != null) {
            ImageLoader.getInstance().displayImage(songMenu.imageUrlBig, mCoverIv, mOptions);
        } else {
            EvLog.e(mCurrentSongMenuId + ",null");
        }
    }

    /**
     * @param activity
     * @param songMenu
     * @param backViewId
     */
    public SongMenuDetailsView(Activity activity, SongMenu songMenu, int backViewId) {
        super(activity, backViewId);
        mFirstSongMenu = songMenu;
        mItemCoverRoundRectRadius = activity.getResources().getDimension(R.dimen.px9);
        mDatas = new ArrayList<Song>();
        initView();
        setImageOptions();
        newIntroductionView();
        fillIntroductionViewData(songMenu);
    }

    private void initView() {
        Log.d("gsp", TAG + ">>initView()");
        mLoadingView = (AnimLoadingView) findViewById(R.id.song_menu_details_loading_widget);

        mListView = (SongListView) findViewById(R.id.song_menu_details_lv);

        mAdapter = new OrderSongListAdapter(mActivity, mListView, mDatas);
        mAdapter.setSongNameSpecWidth(BaseApplication.getInstance().getBaseContext().getResources().getDimensionPixelSize(R.dimen.px650));
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickCallback(new OnItemClickCallback() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id, int itemState) {

                Log.d("gsp", TAG + ">>onItemClick事件---当前的状态:" + itemState);
                Song song = (Song) parent.getAdapter().getItem(position);
                if (song == null) {
                    return;
                }

                if (itemState == SongListView.ITEM_STATE_NORMAL) {
//                    orderSongDelayed(song, false);
                    SongOperationManager.getInstance().orderSong(song.getId(), SongMenuDetailsView.this);
                    operateSelectItem(parent, view, position, id);
                } else if (itemState == SongListView.ITEM_STATE_TOP) {
                    SongOperationManager.getInstance().topSong(song.getId(), SongMenuDetailsView.this);

                    mListView.resetUi();
                    operateSelectItem(parent, view, position, id);

                } else if (itemState == SongListView.ITEM_STATE_FAVORITE) {

                    if (FavoriteListManager.getInstance().isAlreadyExists(song.getId())) {
                        if (FavoriteListManager.getInstance().delSong(song.getId())) {
                            onUmengAgentFavoriteSong(false);
                        }
                        mListView.resetUi();
                        operateSelectItem(parent, view, position, id);
                        return;
                    } else if (FavoriteListManager.getInstance().addSong(song.getId())) {
//                        mListView.startFavoriteAnimnation(position);
                        onUmengAgentFavoriteSong(true);
                        mListView.resetUi();
                        operateSelectItem(parent, view, position, id);
                        return;
                    }
                }
            }

        });

        mListView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.d("gsp", TAG + ">>onItemSelected---");
                operateSelectItem(parent, view, position, id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
        mListView.setOnFocusChangeListener(new OnFocusChangeListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    onPageStart();
                }
                if (mAdapter != null) {
                    mAdapter.refreshSelectedState(hasFocus, mListView.getSelectedItemPosition());
                }
            }
        });

        mListView.setOnSongListKeyDownEventListener(new OnSongListKeyDownEventListener() {

            @Override
            public void onRightEdgeKeyDown() {
                if (MainViewManager.getInstance().getStatusBar() != null) {
                    MainViewManager.getInstance().getStatusBar().setSelectedNumFocus();
                }
            }

            @Override
            public void onLeftEdgeKeyDown() {
                MainViewManager.getInstance().setSmallMvFocus();
            }

            @Override
            public void onDownEdgeKeyDown() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onUpEdgeKeyDown() {
                MainViewManager.getInstance().getStatusBar().requestFocus();
            }
        });


        mListView.setVisibility(View.GONE);
//        mSeekBar.setVisibility(View.GONE); 

        mTotalNumTv = (TextView) findViewById(R.id.songmenu_total_song_num);
    }


    private void operateSelectItem(AdapterView<?> parent, View view,
                                   int position, long id) {
        try {
            Thread.sleep(5);
        } catch (Exception e) {

        }
        Song item = (Song) parent.getAdapter().getItem(position);
        if (item == null) {
            return;
        }

        if (FavoriteListManager.getInstance().isAlreadyExists(item.getId())) {
            mListView.highlightFavoriteIcon();
        } else {
            mListView.restoreFavoriteIcon();
        }

//                updateSeekBar(position);
        if (mDatas.size() >= mTotalNum) {
            return;
        }
        if (position <= (mAdapter.getCount() - 1)
                && position > (mAdapter.getCount() - PAGE_LOAD_EDGE_COUNT)) {
            SongMenuDetailManager.getInstace().loadNextPage();
        }
    }


    private void onUmengAgentOrderSong() {
        String name = getSongMenuName();
        if (name == null) {
            return;
        }
        HashMap<String, String> m = new HashMap<String, String>();
        m.put(EventConst.K_SONG_MENU_NAME, name);
        UmengAgent.onEvent(mActivity, EventConst.ID_CLICK_SONG_MENU_DETAILS_ORDER_SONG, m);
    }

    private void onUmengAgentTopSong() {
        String name = getSongMenuName();
        if (name == null) {
            return;
        }
        HashMap<String, String> m = new HashMap<String, String>();
        m.put(EventConst.K_SONG_MENU_NAME, name);
        UmengAgent.onEvent(mActivity, EventConst.ID_CLICK_SONG_MENU_DETAILS_TOP_SONG, m);
    }

    private void onUmengAgentFavoriteSong(boolean favorite) {
        String name = getSongMenuName();
        if (name == null) {
            return;
        }
        HashMap<String, String> m = new HashMap<String, String>();
        m.put(EventConst.K_SONG_MENU_NAME, name);
        m.put(EventConst.K_FAVORITE_ACTION, favorite ? EventConst.V_FAVORITE : EventConst.V_CANCEL);
        UmengAgent.onEvent(mActivity, EventConst.ID_CLICK_SONG_MENU_DETAILS_FAVORITE, m);
    }

    private String getSongMenuName() {
        switch (mCurrentSongMenuId) {
            case SongMenu.SONG_MENU_ID_CHILD:
                return getString(R.string.song_menu_child);
            case SongMenu.SONG_MENU_ID_DRAMA:
                return getString(R.string.song_menu_drama);
            default:
                SongMenu songMenu = SongMenuManager.getInstance().getSongMenuById(mCurrentSongMenuId);
                if (songMenu != null) {
                    return songMenu.name;
                }
                break;
        }
        return null;
    }
    
/*    private void updateSeekBar(int position) {
//        mSeekBar.setMax(mTotalNum - 1);
//        mSeekBar.setProgress(position);
    }*/

    private void showLoadingView() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.VISIBLE);
            mLoadingView.startAnim();
        }

        mListView.setVisibility(View.GONE);
    }

    private void showLoadingErrorView(int resid) {
        if (mLoadingView != null) {
            if (mLoadingView.getVisibility() != View.VISIBLE) {
                mLoadingView.setVisibility(View.VISIBLE);
            }
            mLoadingView.showLoadFail(resid);
        }
        mListView.setVisibility(View.GONE);
    }

    private void showLoadingErrorView(String error) {
        if (mLoadingView != null) {
            if (mLoadingView.getVisibility() != View.VISIBLE) {
                mLoadingView.setVisibility(View.VISIBLE);
            }
            mLoadingView.showLoadFail(error);
        }
        mListView.setVisibility(View.GONE);
    }

    private void showListView() {

        String format = getResources().getString(
                R.string.songmenu_total_size, mTotalNum);
        mTotalNumTv.setText(Html.fromHtml(format));

        if (mLoadingView != null) {
            mLoadingView.stopAnim();
            mLoadingView.setVisibility(View.GONE);
        }

        mListView.setVisibility(View.VISIBLE);
//        mSeekBar.setVisibility(View.VISIBLE);
        if (mDatas.size() >= SongMenuManager.getInstance().getTotalNumBySongMenuId(mCurrentSongMenuId)) {
            mListView.showFootView(R.string.loading_song_no_more);
            mListView.setNextFocusDownId(R.id.small_mv_frame);
        } else {
            mListView.removeFootLoadingView();
        }
    }
    
    /*private void orderSongDelayed(final Song song, final boolean top) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                PlayListManager.getInstance().addSong(null, song.getId(), top);
            }
        }, KmConfig.ORDER_SONG_DELAY_DURATION);
    }*/

    private void handleException(Exception e, boolean isReset) {
        EvLog.e(e.getMessage());
        if (e instanceof NetworkErrorException) {
            if (isReset) {
                showLoadingErrorView(R.string.error_loading_song_network);
            } else {
                mListView.showFootView(R.string.error_list_foot_loading_song_network);
            }
        } else if (e instanceof DCNoResultException) {
            if (isReset) {
                showLoadingErrorView(R.string.error_loading_song_no_result);
            } else {
                mListView.showFootView(R.string.error_list_foot_loading_song);
            }
        } else if (e instanceof DataCenterCommuException) {
            EvLog.e("recv DataCenterCommuException");
            if (isReset) {
                DataCenterCommuException exception = (DataCenterCommuException) e;
                showLoadingErrorView(exception.getMessage());
            } else {
                mListView.showFootView(R.string.error_list_foot_loading_song);
            }
        } else {
            if (isReset) {
                showLoadingErrorView(R.string.error_loading_song);
            } else {
                mListView.showFootView(R.string.error_list_foot_loading_song);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d("gsp", TAG + ">>>onAttachedToWindow()");
        SongMenuManager.getInstance().registSongMenuListListener(this);
        PlayListManager.getInstance().registerListener(this);
        FavoriteListManager.getInstance().registerListener(this);
        mCurrentSongMenuId = mFirstSongMenu.songMenuId;
        SongMenuDetailManager.getInstace().startGetSongMenuDetailsTask(mCurrentSongMenuId, this, this);

        if (mAdapter == null || mListView == null) {
            return;
        }

        if (mListView.getCount() <= 0) {
            return;
        }
        Song item = (Song) mAdapter.getItem(mListView.getSelectedItemPosition());
        if (item == null) {
            return;
        }
        if (FavoriteListManager.getInstance().isAlreadyExists(item.getId())) {
            mListView.highlightFavoriteIcon();
        } else {
            mListView.restoreFavoriteIcon();
        }
        mAdapter.refreshOrderedState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SongMenuManager.getInstance().unregistSongMenuListListener(this);
        PlayListManager.getInstance().unregisterListener(this);
        FavoriteListManager.getInstance().unregisterListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFavoriteListChange() {
        Log.d("gsp", TAG + ">>>onFavoriteListChange()");
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                if (mListView == null || mAdapter == null) {
                    return;
                }
                Song song = (Song) mAdapter.getItem(mListView.getSelectedItemPosition());
                if (song == null) {
                    return;
                }
                if (FavoriteListManager.getInstance().isAlreadyExists(song.getId())) {
                    mListView.highlightFavoriteIcon();
                } else {
                    mListView.restoreFavoriteIcon();
                }
            }
        });
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPreLoadCacheData() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPostLoadCacheData(Exception e, List<Song> datas) {
        EvLog.i("onPostLoadCacheData-------------");
        Log.d("gsp", TAG + ">>onPostLoadCacheData()");
        if (e != null) {
            handleException(e, true);
        } else if (mAdapter != null && datas != null) {
            mDatas.clear();
            mDatas.addAll(datas);
            mAdapter.notifyDataSetChanged();
            mTotalNum = SongMenuManager.getInstance().getTotalNumBySongMenuId(mCurrentSongMenuId);
            showSongMenuCover();
            showListView();
            mListView.requestFocus();
//            mListView.setSelection(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPreLoadData(boolean isReset, boolean isNext) {
        if (mDatas.size() == 0 || isReset) {
            if (mCurrentSongMenuId != SongMenu.SONG_MENU_ID_CHILD
                    && mCurrentSongMenuId != SongMenu.SONG_MENU_ID_DRAMA) {
                EvLog.d("show loading view");
                showLoadingView();
            }
        } else {
            mListView.showFootLoadingView();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPostLoadData(Exception e, boolean isReset, boolean isNext,
                               List<Song> datas) {
//        EvLog.i("onPostLoadData -------------");
        Log.d("gsp", TAG + ">>onPostLoadData()");
        if (e != null) {
            handleException(e, isReset);
        }
        if (e == null && mAdapter != null && datas != null) {
            if (isReset) {
                mDatas.clear();
            }
            mDatas.addAll(datas);
            mAdapter.notifyDataSetChanged();
            mTotalNum = SongMenuManager.getInstance().getTotalNumBySongMenuId(mCurrentSongMenuId);
            showSongMenuCover();
            showListView();
            if (isReset) {
                mListView.requestFocus();
            }
            updateSongMenuDetailsInfo();
        }
    }

    private void updateSongMenuDetailsInfo() {
        SongMenu songMenu = SongMenuManager.getInstance().getSongMenuById(mCurrentSongMenuId);
        if (songMenu == null) {
            return;
        }
        fillIntroductionViewData(songMenu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getLayResId() {
        return R.layout.main_song_menu_details;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPlayListChange() {
        Log.d("gsp", TAG + ">>>onPlayListChange()");
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getViewId() {
        return MainViewId.ID_SONG_MENU_DETAILS;
    }


    public boolean resumeFocus() {
        if (mListView != null) {
            mListView.requestFocus();
            return true;
        }
        return false;
    }

    @Override
    protected void resetFocus() {
        if (mListView != null) {
//            EvLog.d("mListView resetFocus");
            mListView.requestFocus();
        }
    }

    @Override
    public void onOrderSongSuccess(int songId) {
        Log.d("gsp", TAG + ">>>onOrderSongSuccess()>>>songId:" + songId);
        if (DeviceConfigManager.getInstance().isOrderNeedAnimation()) {
            if (mListView != null) {
                mListView.startOrderSongAnimDelayed();
            }
        }
        onUmengAgentOrderSong();
        LogAnalyzeManager.onEventInSongMenuDetailsPage(mActivity, mCurrentSongMenuId, songId);
    }

    @Override
    public void onTopSongSuccess(int songId) {
        if (DeviceConfigManager.getInstance().isOrderNeedAnimation()) {
            if (mListView != null) {
                mListView.startOrderSongAnimDelayed();
            }
        }
        onUmengAgentTopSong();
    }

    @Override
    public void onTopSongFailed(int songId) {

    }

    @Override
    public void onOrderSongFailed(int songId) {

    }

    @Override
    protected void clickExitKey() {
        if (mDatas != null) {
            mDatas.clear();
        }
    }

    @Override
    public boolean onSmallMVUpKey() {
        return onSmallMVRightKey();
    }

    @Override
    public boolean onSmallMVRightKey() {
        if (mListView != null) {
            mListView.requestFocus();
            return true;
        }
        return false;
    }

    @Override
    public boolean onStatusBarDownKey() {
        if (mListView != null) {
            mListView.requestFocus();
            return true;
        }
        return false;
    }
}
