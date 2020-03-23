/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-11-2     wurongquan     1.0        [修订说明]
 *
 */
package com.evideo.kmbox.widget.mainview.singer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.exception.DCNoResultException;
import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.playerctrl.list.FavoriteListManager;
import com.evideo.kmbox.model.playerctrl.list.FavoriteListManager.IFavoriteListListener;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager.IPlayListListener;
import com.evideo.kmbox.model.playerctrl.list.SongOperationManager;
import com.evideo.kmbox.model.playerctrl.list.SongOperationManager.IOrderSongResultListener;
import com.evideo.kmbox.model.playerctrl.list.SongOperationManager.ITopSongResultListener;
import com.evideo.kmbox.model.umeng.EventConst;
import com.evideo.kmbox.model.umeng.UmengAgent;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.presenter.BasePresenter;
import com.evideo.kmbox.presenter.PageLoadPresenter.ILoadCacheDataCallback;
import com.evideo.kmbox.presenter.PageLoadPresenter.IPageLoadCallback;
import com.evideo.kmbox.util.BitmapUtil;
import com.evideo.kmbox.util.DimensionsUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.NetUtils;
import com.evideo.kmbox.widget.common.OnItemClickCallback;
import com.evideo.kmbox.widget.common.OnSongListKeyDownEventListener;
import com.evideo.kmbox.widget.common.RoundRectImageView;
import com.evideo.kmbox.widget.common.SongListView;
import com.evideo.kmbox.widget.mainmenu.order.OrderSongListAdapter;
import com.evideo.kmbox.widget.mainview.AbsBaseView;
import com.evideo.kmbox.widget.mainview.BreadCrumbsWidget;
import com.evideo.kmbox.widget.mainview.MainViewId;
import com.evideo.kmbox.widget.mainview.MainViewManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * [歌星详情]
 */
public class SingerDetailsView extends AbsBaseView implements
        IPageLoadCallback<Song>, ILoadCacheDataCallback<Song>, IPlayListListener,
        IFavoriteListListener, IOrderSongResultListener, ITopSongResultListener {
    private Singer mSinger = null;

    /**
     * [分页加载数据一页的数量]
     */
    private static final int PAGE_SIZE = 20;
    private static final int PAGE_LOAD_EDGE_COUNT = 8;
    private static final String INVALID_SINGER_NAME = "";
    private float mItemCoverRoundRectRadius;

    private SongListView mListView;
    private OrderSongListAdapter mAdapter;
    private SingerDetailsPageLoadPresenter mPageLoadPresenter;
    private SingerCoverPresenter mCoverPresenter;
    private ArrayList<Song> mDatas = null;

    //    private VerticalSeekBar mSeekBar;
    private View mLoadingView;
    private TextView mLoadingErrorTv;
    private View mSinerItemView;

    // private int mCurrentSongMenuId = Integer.MAX_VALUE;

    private int mTotalNum;

    // private SongMenu mFirstSongMenu;

    /**
     * [歌单简介视图]
     */
    RoundRectImageView mCoverIv;
    TextView mNameTv;
    TextView mDescriptionTv;

    private DisplayImageOptions mOptions;

    private BreadCrumbsWidget mBreadCrumbsWidget = null;

    private void setImageOptions() {
        mOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.singer_default_large)
                .showImageOnFail(R.drawable.singer_default_large)
                .showImageOnLoading(R.drawable.singer_default_large)
                .build();
    }

    private void newIntroductionView() {

        mSinerItemView = (View) findViewById(R.id.singer_details_item_lay);
        mCoverIv = (RoundRectImageView) mSinerItemView
                .findViewById(R.id.singer_details_item_cover);
        Bitmap bmp = BitmapUtil.getBitmapByResId(BaseApplication.getInstance(), R.drawable.singer_default_large);
        mCoverIv.setImageBitmap(bmp);

        mCoverIv.setRadius(mItemCoverRoundRectRadius);
        mNameTv = (TextView) mSinerItemView
                .findViewById(R.id.singer_details_item_name_tv);

        mBreadCrumbsWidget = (BreadCrumbsWidget) findViewById(R.id.singer_details_crumb);

        if (getBackViewId() == MainViewId.ID_HOME_PAGE) {
            mBreadCrumbsWidget.setFirstTitle(getString(R.string.home_page));
        } else {
            mBreadCrumbsWidget.setFirstTitle(getString(R.string.singer_view_tv));
        }
    }

    /**
     * [填弃介绍]
     */
    protected void fillIntroductionViewData() {
        mNameTv.setText(mSinger.getName());
        mBreadCrumbsWidget.setSecondTitle(mSinger.getName());
    }

    /**
     * [显示歌单封面]
     */
    /**
     * [功能说明]
     */
    protected void showSingerCover() {
        if (mCoverPresenter == null) {
            mCoverPresenter = new SingerCoverPresenter();
        }
        mCoverPresenter.start(mSinger.getId());
    }

    /**
     * @param activity
     * @param songMenu
     * @param backViewId
     */
    public SingerDetailsView(Activity activity, Singer singer, int backViewId) {
        super(activity, backViewId);
        mSinger = singer;
        mDatas = new ArrayList<Song>();
        initView();
        setImageOptions();
        newIntroductionView();
        fillIntroductionViewData();
        showSingerCover();
    }

    /**
     * [返回相应的singer]
     *
     * @return singer类
     */
    public Singer getSinger() {
        return mSinger;
    }

    private void initView() {
        mItemCoverRoundRectRadius = DimensionsUtil.getDimension(mActivity, R.dimen.px15);
        mLoadingView = findViewById(R.id.main_singer_loading_lay);
        mLoadingErrorTv = (TextView) findViewById(R.id.singer_detail_loading_error_tv);
        mListView = (SongListView) findViewById(R.id.singer_details_lv);

        mAdapter = new OrderSongListAdapter(mActivity, mListView, mDatas);
        mAdapter.setSongNameSpecWidth(BaseApplication.getInstance().getBaseContext().getResources().getDimensionPixelSize(R.dimen.px600));
        mListView.setAdapter(mAdapter);


        mListView.setOnItemClickCallback(new OnItemClickCallback() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id, int itemState) {
                Song song = (Song) parent.getAdapter().getItem(position);
                if (song == null) {
//                    mListView.resetUi();
                    return;
                }

                if (itemState == SongListView.ITEM_STATE_NORMAL) {
                    mListView.requestFocus();
                    SongOperationManager.getInstance().orderSong(song.getId(), SingerDetailsView.this);
                    operateSelectItem(parent, view, position, id);
                } else if (itemState == SongListView.ITEM_STATE_TOP) {
                    SongOperationManager.getInstance().topSong(song.getId(), SingerDetailsView.this);
                    mListView.resetUi();
                    operateSelectItem(parent, view, position, id);

                } else if (itemState == SongListView.ITEM_STATE_FAVORITE) {

                    if (FavoriteListManager.getInstance().isAlreadyExists(
                            song.getId())) {
                        if (FavoriteListManager.getInstance().delSong(
                                song.getId())) {

                            onUmengAgentFavoriteSong(false);
                        }
                        mListView.resetUi();
                        operateSelectItem(parent, view, position, id);
                        return;
                    } else if (FavoriteListManager.getInstance().addSong(
                            song.getId())) {
                        // mListView.startFavoriteAnimnation(position);
                        mListView.resetUi();
                        operateSelectItem(parent, view, position, id);
                        onUmengAgentFavoriteSong(true);
                        return;
                    }
                    mListView.resetUi();
                    operateSelectItem(parent, view, position, id);
                }else {
                    mListView.resetUi();
                    operateSelectItem(parent, view, position, id);
                }
            }

        });

        mListView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
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
                    View view = mListView.getSelectedView();
                    if (view != null) {
                        view.setSelected(true);
                    }
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
                MainViewManager.getInstance().setSmallMvFocus();
            }

            @Override
            public void onUpEdgeKeyDown() {
                if (MainViewManager.getInstance().getStatusBar() != null) {
                    MainViewManager.getInstance().getStatusBar().requestFocus();
                }
            }
        });
        mLoadingView.setVisibility(View.GONE);
        mLoadingErrorTv.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
    }

    private void operateSelectItem(AdapterView<?> parent, View view,
                                   int position, long id) {


        Song item = (Song) parent.getAdapter().getItem(position);
        if (item == null) {
            return;
        }

        if (FavoriteListManager.getInstance().isAlreadyExists(
                item.getId())) {
            mListView.highlightFavoriteIcon();
        } else {
            mListView.restoreFavoriteIcon();
        }

        // updateSeekBar(position);
        if (mDatas.size() >= mTotalNum) {
            return;
        }
        if (position <= (mAdapter.getCount() - 1)
                && position > (mAdapter.getCount() - PAGE_LOAD_EDGE_COUNT)) {
            mPageLoadPresenter.loadNextPage();
        }

    }

    private void onUmengAgentOrderSong() {
        String name = getSongMenuName();
        if (name == null) {
            return;
        }
        HashMap<String, String> m = new HashMap<String, String>();
        m.put(EventConst.K_SONG_MENU_NAME, name);
        UmengAgent.onEvent(mActivity,
                EventConst.ID_CLICK_SONG_MENU_DETAILS_ORDER_SONG, m);
    }

    private void onUmengAgentTopSong() {
        String name = getSongMenuName();
        if (name == null) {
            return;
        }
        HashMap<String, String> m = new HashMap<String, String>();
        m.put(EventConst.K_SONG_MENU_NAME, name);
        UmengAgent.onEvent(mActivity,
                EventConst.ID_CLICK_SONG_MENU_DETAILS_TOP_SONG, m);
    }

    private void onUmengAgentFavoriteSong(boolean favorite) {
        String name = getSongMenuName();
        if (name == null) {
            return;
        }
        HashMap<String, String> m = new HashMap<String, String>();
        m.put(EventConst.K_SONG_MENU_NAME, name);
        m.put(EventConst.K_FAVORITE_ACTION, favorite ? EventConst.V_FAVORITE
                : EventConst.V_CANCEL);
        UmengAgent.onEvent(mActivity,
                EventConst.ID_CLICK_SONG_MENU_DETAILS_FAVORITE, m);
    }

    private String getSongMenuName() {
        // switch (mCurrentSongMenuId) {
        // case SongMenu.SONG_MENU_ID_CHILD:
        // return getString(R.string.song_menu_child);
        // case SongMenu.SONG_MENU_ID_DRAMA:
        // return getString(R.string.song_menu_drama);
        // default:
        // SongMenu songMenu =
        // SongMenuManager.getInstance().getSongMenuById(mCurrentSongMenuId);
        // if (songMenu != null) {
        // return songMenu.name;
        // }
        // break;
        // }
        return null;
    }

    private void loadData(String singerName) {
        if (INVALID_SINGER_NAME.equals(singerName) || singerName == null) {
            showLoadingErrorView(R.string.error_loading_song_no_result);
            return;
        }
        if (mPageLoadPresenter != null) {
            mPageLoadPresenter.stopTask();
            mPageLoadPresenter = null;
        }
        mPageLoadPresenter = new SingerDetailsPageLoadPresenter(PAGE_SIZE, this, singerName);
        mPageLoadPresenter.setOfflineSearch(!NetUtils.isNetworkConnected(BaseApplication.getInstance().getBaseContext()));
        mPageLoadPresenter.loadData();
    }

    private void showLoadingView() {
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingErrorTv.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
    }

    private void showLoadingErrorView(int resid) {
        mLoadingView.setVisibility(View.GONE);
        mLoadingErrorTv.setVisibility(View.VISIBLE);
        mLoadingErrorTv.setText(resid);
        mListView.setVisibility(View.GONE);
        // mSeekBar.setVisibility(View.GONE);
    }

    private void showListView() {
        mLoadingView.setVisibility(View.GONE);
        mLoadingErrorTv.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        // mSeekBar.setVisibility(View.VISIBLE);
        if (mDatas.size() >= mTotalNum) {
            mListView.showFootView(R.string.loading_song_no_more);
            mListView.setNextFocusDownId(R.id.small_mv_frame);
        } else {
            mListView.removeFootLoadingView();
        }
    }

    public boolean resumeFocus() {
        if (mListView != null) {
            mListView.requestFocus();
            return true;
        }
        return false;
    }
    
   
   
    
   /* private void orderSongDelayed(final Song song, final boolean top) {
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
        PlayListManager.getInstance().registerListener(this);
        FavoriteListManager.getInstance().registerListener(this);
        loadData(mSinger.getName());

        EvLog.d("mSinger id=" + mSinger.getId());
        if (mAdapter == null || mListView == null) {
            return;
        }
        mListView.requestFocus();
        if (mListView.getCount() <= 0) {
            return;
        }
        Song item = (Song) mAdapter
                .getItem(mListView.getSelectedItemPosition());
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
        PlayListManager.getInstance().unregisterListener(this);
        FavoriteListManager.getInstance().unregisterListener(this);
    }

    @Override
    protected int getLayResId() {
        return R.layout.main_menu_singer_details;
    }

    @Override
    protected int getViewId() {
        return MainViewId.ID_SINGER_DETAILS;
    }

    @Override
    protected void resetFocus() {
        if (mListView != null) {
            mListView.requestFocus();
        }
    }

    @Override
    public void onFavoriteListChange() {
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

    @Override
    public void onPlayListChange() {
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onPostLoadCacheData(Exception e, List<Song> datas) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPreLoadData(boolean isReset, boolean isNext) {
        if (mDatas.size() == 0 || isReset) {
            showLoadingView();
        } else {
            mListView.showFootLoadingView();
        }
        mListView.showFootLoadingView();
    }

    @Override
    public void onPostLoadData(Exception e, boolean isReset, boolean isNext,
                               List<Song> datas) {
        if (e != null) {
            handleException(e, isReset);
        }
        if (e == null && mAdapter != null && datas != null) {
            if (isReset) {
                mDatas.clear();
            }
            mDatas.addAll(datas);
            mAdapter.notifyDataSetChanged();
            mTotalNum = mPageLoadPresenter.getTotalNum();
            showListView();
            if (isReset) {
                mListView.requestFocus();
//              mListView.setSelection(0);
            }
        }
    }

    @Override
    public void onPreLoadCacheData() {
        // TODO Auto-generated method stub
    }

    /**
     * [功能说明]歌星头像异步任务类
     */
    private class SingerCoverPresenter extends AsyncPresenter<SingerCoverInfo> {

        @Override
        protected SingerCoverInfo doInBackground(Object... params) throws Exception {
            EvLog.i("start request singer header");
            int singerId = (Integer) params[0];
            if (singerId < 0) {
                return null;
            }
            return DCDomain.getInstance().requestSingerCover(singerId);
        }

        @Override
        protected void onCompleted(SingerCoverInfo result, Object... params) {
            SingerCoverInfo info = (SingerCoverInfo) result;
            if (info != null && info.mCoverPicH != null) {
                int id = -1;
                try {
                    id = Integer.valueOf(info.mCoverPicH);
                } catch (Exception e) {
                    UmengAgentUtil.reportError("illegal format of singer cover ID, singerID:" + mSinger.getId());
                    EvLog.e("illegal format of singer cover ID, singerID:" + mSinger.getId());
                }
                if (id <= 0) {
                    return;
                }
                ImageLoader.getInstance().displayImage(
                        info.mPicHeadUrl.trim() + "?fileid=" + info.mCoverPicH, mCoverIv, mOptions);
                EvLog.i("Success to Request Singer Cover!\n" + info.mPicHeadUrl.trim() + "?fileid=" + info.mCoverPicH);
            }
        }

        @Override
        protected void onFailed(Exception exception, Object... params) {
            EvLog.e("REQUEST SINGER COVER", "faile to request singer cover");
        }
    }

    @Override
    public void onOrderSongSuccess(int songId) {
        if (DeviceConfigManager.getInstance().isOrderNeedAnimation()) {
            if (mListView != null) {
                mListView.startOrderSongAnimDelayed();
            }
        }
        onUmengAgentOrderSong();
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
        // TODO Auto-generated method stub

    }

    @Override
    public void onOrderSongFailed(int songId) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void clickExitKey() {
        if (mDatas != null) {
            mDatas.clear();
        }
    }

    @Override
    public boolean onSmallMVUpKey() {
        if (mListView != null) {
            mListView.requestFocus();
            return true;
        }
        return false;
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
