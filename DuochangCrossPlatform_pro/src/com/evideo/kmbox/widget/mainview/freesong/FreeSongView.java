/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年3月10日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.freesong;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.playerctrl.list.FavoriteListManager;
import com.evideo.kmbox.model.playerctrl.list.FreeSongListManager;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;
import com.evideo.kmbox.model.playerctrl.list.FavoriteListManager.IFavoriteListListener;
import com.evideo.kmbox.model.playerctrl.list.FreeSongListManager.IGetFreeSongEventListener;
import com.evideo.kmbox.model.playerctrl.list.SongOperationManager;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager.IPlayListListener;
import com.evideo.kmbox.model.playerctrl.list.SongOperationManager.IOrderSongResultListener;
import com.evideo.kmbox.model.playerctrl.list.SongOperationManager.ITopSongResultListener;
import com.evideo.kmbox.presenter.BasePresenter;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.AnimLoadingView;
import com.evideo.kmbox.widget.common.OnItemClickCallback;
import com.evideo.kmbox.widget.common.OnSongListKeyDownEventListener;
import com.evideo.kmbox.widget.common.SongListView;
import com.evideo.kmbox.widget.mainmenu.order.OrderSongListAdapter;
import com.evideo.kmbox.widget.mainview.AbsBaseView;
import com.evideo.kmbox.widget.mainview.MainViewId;
import com.evideo.kmbox.widget.mainview.MainViewManager;

/**
 * [功能说明]
 */
public class FreeSongView extends AbsBaseView implements IGetFreeSongEventListener, IOrderSongResultListener, ITopSongResultListener,
        IPlayListListener, IFavoriteListListener {

    private final String TAG = FreeSongView.class.getSimpleName();

    private SongListView mListView;
    private OrderSongListAdapter mAdapter;
    private ArrayList<Song> mDatas = null;
    private ImageView mTvShowRectTv = null;
    private TextView mPlayingSongInfoTv = null;
    private AnimLoadingView mLoadingView = null;

    public FreeSongView(Activity activity, int backViewId) {
        super(activity, backViewId);
//        initPayBtn();
        mDatas = new ArrayList<Song>();
        initView();
    }

    public void updatePlayInfo(String info) {
        if (mPlayingSongInfoTv != null) {
            mPlayingSongInfoTv.setText(info);
        }
    }

    private void initView() {
        mTvShowRectTv = (ImageView) findViewById(R.id.mv_show_rect);
        mTvShowRectTv.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    mTvShowRectTv.setBackgroundResource(R.drawable.freesong_mv_focus_frame);
                } else {
                    mTvShowRectTv.setBackground(null);
                    mListView.setAdapter(mAdapter);
                    mListView.setSelection(0);
                }
            }
        });
        mTvShowRectTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                MainViewManager.getInstance().switchMainView();
            }
        });

        mPlayingSongInfoTv = (TextView) findViewById(R.id.freesong_page_songname_tv);

        mLoadingView = (AnimLoadingView) findViewById(R.id.free_song_loading_widget);

        mListView = (SongListView) findViewById(R.id.free_song_lv);

        mAdapter = new OrderSongListAdapter(mActivity, mListView, mDatas);
        mAdapter.setSongNameSpecWidth(BaseApplication.getInstance().getBaseContext().getResources().getDimensionPixelSize(R.dimen.px650));
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickCallback(new OnItemClickCallback() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id, int itemState) {
                log("---onItemClick()----itemState:" + itemState);
                Song song = (Song) parent.getAdapter().getItem(position);
                if (song == null) {
                    mListView.resetUi();
                    return;
                }

                if (itemState == SongListView.ITEM_STATE_NORMAL) {
                    DeviceConfigManager.getInstance().setFree(true);
                    SongOperationManager.getInstance().orderSong(song.getId(), FreeSongView.this);
                    operateSelectItem(parent, view, position, id);
                } else if (itemState == SongListView.ITEM_STATE_TOP) {
                    SongOperationManager.getInstance().topSong(song.getId(), FreeSongView.this);
                    mListView.resetUi();
                    operateSelectItem(parent, view, position, id);
                } else if (itemState == SongListView.ITEM_STATE_FAVORITE) {
                    if (FavoriteListManager.getInstance().isAlreadyExists(song.getId())) {
                        Log.i("gsp", "onItemClick:如果收藏成功的话删除这个歌单ID ");
                        if (FavoriteListManager.getInstance().delSong(song.getId())) {
//                            onUmengAgentFavoriteSong(false);
                            Log.i("gsp", "onItemClick:成功删除这个歌单ID ");
                            mListView.resetUi();
                            operateSelectItem(parent, view, position, id);
                        }
                        return;
                    } else if (FavoriteListManager.getInstance().addSong(song.getId())) {
                        Log.i("gsp", "onItemClick:成功添加到收藏的这个歌单ID ");
                        mListView.resetUi();
                        operateSelectItem(parent, view, position, id);
//                        mListView.startFavoriteAnimnation(position);
//                        onUmengAgentFavoriteSong(true);
                        return;
                    }
                    mListView.resetUi();
                    operateSelectItem(parent, view, position, id);
                } else {
//                    mListView.highlightFavoriteIcon();
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

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int selectPos = mListView.getSelectedItemPosition();


                EvLog.i(hasFocus + ",getSelectedItemPosition:" + selectPos);
                if (mAdapter != null) {
                    mAdapter.refreshSelectedState(hasFocus, selectPos);
                }
                if (hasFocus) {
//                 onPageStart();
//                   mListView.setSelection(selectPos);
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
//                MainViewManager.getInstance().setSmallMvFocus();
                mTvShowRectTv.requestFocus();
            }

            @Override
            public void onDownEdgeKeyDown() {

            }

            @Override
            public void onUpEdgeKeyDown() {
                if (MainViewManager.getInstance().getStatusBar() != null) {
                    MainViewManager.getInstance().getStatusBar().requestFocus();
                }
            }
        });

        mListView.setVisibility(View.GONE);
    }

    private void operateSelectItem(AdapterView<?> parent, View view,
                                   int position, long id) {
        EvLog.i("mListView onItemSelected:" + position);
        Song item = (Song) parent.getAdapter().getItem(position);
        if (item == null) {
            return;
        }
        Log.i("gsp", "onItemSelected: 什么时候调用他来进行收藏");
        if (FavoriteListManager.getInstance().isAlreadyExists(item.getId())) {
            mListView.highlightFavoriteIcon();
        } else {
            mListView.restoreFavoriteIcon();
        }

//        updateSeekBar(position);

//        if (mDatas.size() >= mTotalNum) {
//            return;
//        }
//        if (position <= (mAdapter.getCount() - 1)
//                && position > (mAdapter.getCount() - PAGE_LOAD_EDGE_COUNT)) {
//            SongMenuDetailManager.getInstace().loadNextPage();
//        }
    }


    @Override
    protected int getLayResId() {
        return R.layout.main_freesong_view;
    }

    @Override
    protected int getViewId() {
        return MainViewId.ID_FREE_SONG;
    }

    @Override
    protected void resetFocus() {
    }


    private void showMV() {
        int width = getResources().getDimensionPixelSize(R.dimen.px680);
        int height = getResources().getDimensionPixelSize(R.dimen.px510);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        lp.topMargin = getResources().getDimensionPixelSize(R.dimen.px251);//+11
        lp.leftMargin = getResources().getDimensionPixelSize(R.dimen.px80);
        MainViewManager.getInstance().showMvAtAssignRect(lp);
    }

    public void focusBackFromMV() {
        showMV();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        PlayListManager.getInstance().registerListener(this);
        FavoriteListManager.getInstance().registerListener(this);
        showMV();
        mPlayingSongInfoTv.setText(MainViewManager.getInstance().getPlayingSongInfo());

        FreeSongListManager.getInstance().addGetFreeSongEventListener(this);
        if (FreeSongListManager.getInstance().getListCount() == 0) {
            showLoadingView();
            FreeSongListManager.getInstance().startUpdateFreeSong();
        } else {
            updateData();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        MainViewManager.getInstance().resumeMvRect();
        FreeSongListManager.getInstance().removeGetFreeSongEventListener(this);
        PlayListManager.getInstance().unregisterListener(this);
        FavoriteListManager.getInstance().unregisterListener(this);
        DeviceConfigManager.getInstance().setFree(false);
    }

    private void updateData() {
        EvLog.e("updateData------------");
        mDatas.clear();
        mDatas.addAll(FreeSongListManager.getInstance().getList());
        mAdapter.notifyDataSetChanged();
        showListView();
        mListView.requestFocus();
    }

    private void showListView() {

        if (mLoadingView != null && mLoadingView.getVisibility() != View.GONE) {
            mLoadingView.stopAnim();
            mLoadingView.setVisibility(View.GONE);
        }

        if (mListView == null) {
            return;
        }
        if (mListView.getVisibility() != View.VISIBLE) {
            mListView.setVisibility(View.VISIBLE);
        }
        mListView.showFootView(R.string.loading_song_no_more);
    }

    private void showLoadingView() {

        if (mListView != null && mListView.getVisibility() != View.GONE) {
            mListView.setVisibility(View.GONE);
        }
        if (mLoadingView != null && mLoadingView.getVisibility() != View.VISIBLE) {
            mLoadingView.setVisibility(View.VISIBLE);
            mLoadingView.startAnim();
        }
    }

    private void showLoadFail() {
        if (mLoadingView != null && mLoadingView.getVisibility() != View.VISIBLE) {
            mLoadingView.setVisibility(View.VISIBLE);
        }
        mLoadingView.showLoadFail(R.string.error_loading_data);

        if (mListView != null && mListView.getVisibility() != View.GONE) {
            mListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStartGetFreeSong() {
        mDatas.clear();
        showLoadingView();
    }

    @Override
    public void onErrorGetFreeSong() {
        mDatas.clear();
        showLoadFail();
    }

    @Override
    public void onFinishGetFreeSong() {
        updateData();
    }

    @Override
    public void onUpdateFreeSong() {
        updateData();
    }


    @Override
    public void onOrderSongSuccess(int songId) {
        if (DeviceConfigManager.getInstance().isOrderNeedAnimation()) {
            if (mListView != null) {
                mListView.startOrderSongAnimDelayed();
            }
        }
//        onUmengAgentOrderSong();
//        LogAnalyzeManager.onEventInSongMenuDetailsPage(mActivity,mCurrentSongMenuId,songId);
    }

    @Override
    public void onOrderSongFailed(int songId) {

    }

    @Override
    public void onTopSongSuccess(int songId) {
        if (DeviceConfigManager.getInstance().isOrderNeedAnimation()) {
            if (mListView != null) {
                mListView.startOrderSongAnimDelayed();
            }
        }
//        onUmengAgentTopSong();o
    }

    @Override
    public void onTopSongFailed(int songId) {

    }

    @Override
    public void onFavoriteListChange() {
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                if (mListView == null || mAdapter == null) {
                    Log.i("gsp", "run: mListView+" + mListView + mAdapter);
                    return;
                }
                Log.i("gsp", "run: mListView11111+" + mListView.getSelectedItemPosition() + mAdapter);
                Song song = (Song) mAdapter.getItem(mListView.getSelectedItemPosition());
                if (song == null) {


                    return;
                }
                if (FavoriteListManager.getInstance().isAlreadyExists(song.getId())) {
                    mListView.highlightFavoriteIcon();
                    Log.i("gsp", "run:收藏店家 我这个方法 ");
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
    protected void clickExitKey() {
        if (mDatas != null) {
            mDatas.clear();
        }

    }

    @Override
    public boolean onSmallMVUpKey() {
        return false;
    }

    @Override
    public boolean onSmallMVRightKey() {
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

    private void log(String tag) {
        Log.d("gsp", TAG + ">>>" + tag);
    }
}
