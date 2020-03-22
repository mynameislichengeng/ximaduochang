/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年11月11日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.globalsearch;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.model.dao.data.SingerManager;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
import com.evideo.kmbox.model.playerctrl.list.FavoriteListManager;
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
import com.evideo.kmbox.presenter.PageLoadPresenter.IPageLoadCallback;
import com.evideo.kmbox.util.DimensionsUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.SearchWidget;
import com.evideo.kmbox.widget.SearchWidget.IRightEdgeListener;
import com.evideo.kmbox.widget.SearchWidget.ISearchBtnClickListener;
import com.evideo.kmbox.widget.SearchWidget.ISearchItemClickListener;
import com.evideo.kmbox.widget.SearchWidget.IUpEdgeListener;
import com.evideo.kmbox.widget.StatusBarWidget;
import com.evideo.kmbox.widget.common.AnimLoadingView;
import com.evideo.kmbox.widget.common.CircleImageView;
import com.evideo.kmbox.widget.common.CustomSelectorGridView;
import com.evideo.kmbox.widget.common.MaskFocusButton;
import com.evideo.kmbox.widget.common.OnItemClickCallback;
import com.evideo.kmbox.widget.common.OnSongListKeyDownEventListener;
import com.evideo.kmbox.widget.common.SearchKeyboard.Key;
import com.evideo.kmbox.widget.common.SearchKeyboardView;
import com.evideo.kmbox.widget.common.SmoothHorizontalScrollView;
import com.evideo.kmbox.widget.common.SongListView;
import com.evideo.kmbox.widget.mainmenu.order.OrderSongListAdapter;
import com.evideo.kmbox.widget.mainmenu.order.OrderSongPageLoadPresenter;
import com.evideo.kmbox.widget.mainview.AbsBaseView;
import com.evideo.kmbox.widget.mainview.MainViewId;
import com.evideo.kmbox.widget.mainview.MainViewManager;
import com.evideo.kmbox.widget.mainview.MainViewManager.IMVSwitchListener;
import com.evideo.kmbox.widget.mainview.globalsearch.AutoTextViewContainer.IEdgeListener;
import com.evideo.kmbox.widget.mainview.globalsearch.AutoTextViewContainer.IOnItemClickCallback;
import com.evideo.kmbox.widget.mainview.singer.PickSingerPageLoadPresenter;
import com.evideo.kmbox.widget.mainview.singer.SingerView.ISingerClickListener;

/**
 * [功能说明]
 */
public class GlobalSearchView extends AbsBaseView implements IPageLoadCallback<Song>,
        IOrderSongResultListener, ITopSongResultListener, IPlayListListener, IMVSwitchListener, FavoriteListManager.IFavoriteListListener {
    private DisplayMetrics mDisMetrics;
    private SearchWidget mSearchWidget = null;
    private static final String INVALID_SPELL = "";
    private String mCurSpell = "";
    private String mCurKeyWord = "";
    private float mTextHintSize;
    private float mTextCommonSize;
    private boolean isHistory = false;

    private KeyWordPresenter mHotSearchPresenter;
    private AutoTextViewContainer mSearchKeywordLay;

    private LinearLayout mDefaultRightView = null;
    private LinearLayout mResultRightView = null;

    private TextView mSongSearchResultTV = null;

    private TextView mSingerSearchResultTV = null;
    private IPageLoadCallback<Singer> mSingerCallback;
    private CustomSelectorGridView mSingerGV;
    private ArrayList<Singer> mSingerDatas = null;
    private CustomSingerAdapter mSingerAdapter;
    private PickSingerPageLoadPresenter mSingerPageLoadPresenter;
    private static final int PAGE_SIZE = 50;
    private int mSingerGridViewItemWidth;
    private int mSingerGridViewUnitWidth;


    private OrderSongPageLoadPresenter mSongPageLoadPresenter;
    private SearchSongListView mSongListView;
    private ArrayList<Song> mSongDatas = null;
    private OrderSongListAdapter mSongAdapter;


    private SingerAndSongPresenter mClickKeywordPresenter;

    private SearchHistoryListView mHistoryListView;
    private SearchHistoryAdapter mHistoryAdapter;
    private ArrayList<SearchHistoryItem> mHistoryDatas = null;
    private ISingerClickListener mSingerClickListener = null;


    private MaskFocusButton mClearHistoryBtn;
    private int mClearHistoryBtnPadding;

    private AnimLoadingView mLoadingView = null;

    private boolean mLoadSingerFinish = false;
    private boolean mLoadSongFinish = false;

    private SmoothHorizontalScrollView mScrollView;
    private int mScrollViewFaddingLength;

    private List<String> mKeyWordArray = null;
    private int mSongNameSpec;

    public GlobalSearchView(Activity activity, int backViewId) {
        super(activity, backViewId);
        mKeyWordArray = new ArrayList<String>();
        initDimens();
        initSearchView();
        mLoadingView = (AnimLoadingView) findViewById(R.id.song_loading_lay);
        initDefaultRightView();
        initResultRightView();
    }


    private void initSongView() {
        mSongDatas = new ArrayList<Song>();
        mSongListView = (SearchSongListView) findViewById(R.id.order_song_song_lv);
        mSongAdapter = new OrderSongListAdapter(mActivity, mSongListView, mSongDatas);
//        mSongAdapter.setNormalSize(getResources().getDimensionPixelSize(R.dimen.px25));
        mSongListView.setAdapter(mSongAdapter);
        mSongAdapter.setSongNameSpecWidth(mSongNameSpec);

        mSongListView.setOnItemClickCallback(new OnItemClickCallback() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id, int itemState) {
                Song song = (Song) parent.getAdapter().getItem(position);
                if (song == null) {
                    return;
                }

                SearchHistoryManager.getInstance().save(
                        new SearchHistoryItem(song.getId(), SearchHistoryItem.SEARCH_ITEM_TYPE_SONG));
                EvLog.i("saving searchhistory item to dao");
                if (itemState == SongListView.ITEM_STATE_NORMAL) {
                  /*  mSearchWidget.setSearchText("");
                    mSearchWidget.getKeyboardView().updateKeyboardState("");*/
                    SongOperationManager.getInstance().orderSong(song.getId(), GlobalSearchView.this);

                } else if (itemState == SongListView.ITEM_STATE_TOP) {
                   /* mSearchWidget.setSearchText("");
                    mSearchWidget.getKeyboardView().updateKeyboardState("");*/
                    SongOperationManager.getInstance().topSong(song.getId(), GlobalSearchView.this);
                    UmengAgent.onEvent(mActivity, EventConst.ID_CLICK_ORDER_SONG_VIEW_TOP_SONG);
                } else if (itemState == SongListView.ITEM_STATE_FAVORITE) {

                    if (FavoriteListManager.getInstance().isAlreadyExists(song.getId())) {
                        if (FavoriteListManager.getInstance().delSong(song.getId())) {
                            UmengAgentUtil.onEventFavoriteAction(mActivity,
                                    EventConst.ID_CLICK_ORDER_SONG_VIEW_FAVORITE, false);
                        }
                        return;
                    } else if (FavoriteListManager.getInstance().addSong(song.getId())) {
//                        mListView.startFavoriteAnimnation(position);
                        UmengAgentUtil.onEventFavoriteAction(mActivity,
                                EventConst.ID_CLICK_ORDER_SONG_VIEW_FAVORITE, true);
                        return;
                    }
                }
            }
        });

        mSongListView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                isHistory = false;
                if (mSongAdapter == null || mSongPageLoadPresenter == null) {
                    return;
                }

                Song item = (Song) parent.getAdapter().getItem(position);

                if (item == null) {
                    return;
                }

                if (FavoriteListManager.getInstance().isAlreadyExists(item.getId())) {
                    mSongListView.highlightFavoriteIcon();
                } else {
                    mSongListView.restoreFavoriteIcon();
                }

                if (position <= (mSongAdapter.getCount() - 1)
                        && position > (mSongAdapter.getCount() - 20)) {
                    mSongPageLoadPresenter.loadNextPage();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSongListView.setOnSongListKeyDownEventListener(new OnSongListKeyDownEventListener() {

            @Override
            public void onRightEdgeKeyDown() {
//                MainViewManager.getInstance().getStatusBar().setSelectedNumFocus();
            }

            @Override
            public void onLeftEdgeKeyDown() {
                searchWidgetResumeFocusFromRight();
            }

            @Override
            public void onDownEdgeKeyDown() {
                MainViewManager.getInstance().setSmallMvFocus();
            }

            @Override
            public void onUpEdgeKeyDown() {
                if (mSingerGV.getVisibility() == View.VISIBLE) {
                    mSingerGV.requestFocus();
                    return;
                }
                MainViewManager.getInstance().getStatusBar().requestFocus();
            }
        });
//        setListViewHeightBasedOnChildren(mSongListView);

        mSongListView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    onPageStart();
                }
                if (mSongAdapter != null) {
                    mSongAdapter.refreshSelectedState(hasFocus, mSongListView.getSelectedItemPosition());
                }
            }
        });
    }


    public boolean resumeFocusFromSelectedNum() {
        if (mResultRightView.getVisibility() == View.VISIBLE) {
            if (mSingerGV.getVisibility() == View.VISIBLE) {
                mSingerGV.requestFocus();
                return true;
            }
            if (mSongListView.getVisibility() == View.VISIBLE) {
                mSongListView.requestFocus();
                return true;
            }
        }
        if (mDefaultRightView.getVisibility() == View.VISIBLE) {
            mSearchKeywordLay.resetFocus();
            return true;
        }
        mSearchWidget.getKeyboardView().requestFocus();
        return true;
    }

    private void changeSingerListViewFocusState() {
        if (mSingerGV == null) {
            return;
        }
        mSingerGV.setFocusable(false);
        mSingerGV.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mSingerGV.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSingerGV.setFocusable(true);
                mSingerGV.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            }
        }, 1000);
    }

    private void initSingerView() {
        mScrollView = (SmoothHorizontalScrollView) findViewById(R.id.search_view_scroller_view);
        mScrollView.setHorizontalFadingEdgeEnabled(true);
        mScrollView.setFadingEdgeLength(mScrollViewFaddingLength);

        mSingerGV = (CustomSelectorGridView) findViewById(R.id.search_view_singer_list);
        mSingerDatas = new ArrayList<Singer>();
        mSingerAdapter = new CustomSingerAdapter(mActivity, mSingerGV, mSingerDatas);
        mSingerGV.setAdapter(mSingerAdapter);

        mSingerGV.setEnableSquareSelector(true);
        mSingerGV.setCustomSelectorDrawable(BaseApplication.getInstance().getBaseContext().getResources().getDrawable(R.drawable.singer_icon_frame));
        mSingerGV.setSelectorPadding( // 左、上、右、下
                getResources().getDimensionPixelSize(R.dimen.global_search_singer_padding_left)
                , getResources().getDimensionPixelSize(R.dimen.global_search_singer_padding_top)
                , getResources().getDimensionPixelSize(R.dimen.global_search_singer_padding_right)
                , getResources().getDimensionPixelSize(R.dimen.global_search_singer_padding_bottom));

        mSingerCallback = new IPageLoadCallback<Singer>() {

            @Override
            public void onPreLoadData(boolean isReset, boolean isNext) {
                if (isReset) {
//                    showSingerLoadingView();
//                    mSingerGV.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPostLoadData(Exception e, boolean isReset,
                                       boolean isNext, List<Singer> datas) {
                EvLog.i("isReset:" + isReset + ",isNext:" + isNext);
                mLoadSingerFinish = true;
                showSearchResultView();
                if (isReset) {
                    int mSingerNum = mSingerPageLoadPresenter.getTotalSingerNum();
                    if (mSingerNum > 0) {
                        setupGridViewParams(mSingerNum);
                        mSingerDatas.clear();
                        changeSingerListViewFocusState();
                        mSingerDatas.addAll(datas);
                        mSingerAdapter.notifyDataSetChanged();
                        mSingerGV.setVisibility(View.VISIBLE);
                    } else {
                        mSingerDatas.clear();
                        mSingerAdapter.notifyDataSetChanged();
                        mSingerGV.setVisibility(View.INVISIBLE);
                    }
                    EvLog.e("singer isRet:" + isReset + ",mSingerNum:" + mSingerNum);
                    /*if (mResultRightView.getVisibility() == View.VISIBLE)*/
                    {
                        mSingerSearchResultTV.setText(getString(R.string.global_search_singer_num, mSingerNum));
                    }
                    return;
                }
                mSingerDatas.addAll(datas);
                mSingerAdapter.notifyDataSetChanged();
                mSingerGV.setVisibility(View.VISIBLE);
            }
        };

        mSingerGV.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (mSingerAdapter == null || mSingerPageLoadPresenter == null) {
                    return;
                }
                EvLog.i("singerList Item select at:" + position);
                Singer item = (Singer) parent.getAdapter().getItem(position);
                if (item == null) {
                    return;
                }

                if (position <= (mSingerAdapter.getCount() - 1)
                        && position >= (mSingerAdapter.getCount() - 30)) {
                    EvLog.i("singerList loadNextPage at pos:" + position);
                    mSingerPageLoadPresenter.loadNextPage();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
       
       /* mSingerAdapter.setSingerClickListner(new ISingerClickListener() {
            
            @Override
            public void onSingerItemClick(Singer singer, int backViewId) {
                if (mSingerClickListener != null) {
                    Singer info = mSingerAdapter.getItem(mSingerGV.getSelectedItemPosition());
                    if (info != null) {
                        mSingerClickListener.onSingerItemClick(info, MainViewId.ID_SEARCH);
                        SearchHistoryManager.getInstance().save(
                                new SearchHistoryItem(info.getId(), SearchHistoryItem.SEARCH_ITEM_TYPE_SINGER));
                    }
                }
            }
        });*/
        mSingerAdapter.setEdgeListener(new CustomSingerAdapter.IEdgeListener() {

            @Override
            public boolean onUpEdge() {
                MainViewManager.getInstance().getStatusBar().requestFocus();
                return true;
            }

            @Override
            public boolean onRightEdge() {
                return true;
            }

            @Override
            public boolean onLeftEdge() {
                EvLog.i("onLeftEdge >>>>>>>>>>>>");
                searchWidgetResumeFocusFromRight();
                return true;
            }

            @Override
            public boolean onDownEdge() {
                if (mSongListView.getVisibility() == View.VISIBLE) {
                    mSongListView.requestFocus();
                    return true;
                }
                MainViewManager.getInstance().setSmallMvFocus();
                return true;
            }
        });
       /* mSingerGV.setEdgeListener(new CustomSelectorGridView.IEdgeListener() {
            
            @Override
            public boolean onUpEdge() {
                MainViewManager.getInstance().getStatusBar().requestFocus();
                return true;
            }
            
            @Override
            public boolean onRightEdge() {
                EvLog.i("onRightEdge >>>>>>>>>>>>");
                return false;
            }
            
            @Override
            public boolean onLeftEdge() {
                EvLog.i("onLeftEdge >>>>>>>>>>>>");
                if (mSearchWidget != null) {
                    mSearchWidget.getKeyboardView().setSelection(5);
                    mSearchWidget.getKeyboardView().requestFocus();
                }
                return true;
            }
            
            @Override
            public boolean onDownEdge() {
                if (mSongListView.getVisibility() == View.VISIBLE) {
                    mSongListView.requestFocus();
                    return true;
                }
                MainViewManager.getInstance().setSmallMvFocus();
                return true;
            }
        });*/
    }

    private void searchWidgetResumeFocusFromRight() {
        if (mSearchWidget != null) {
            mSearchWidget.getKeyboardView().setSelection(5);
            mSearchWidget.getKeyboardView().requestFocus();
        }
    }

    private void initResultRightView() {
        mResultRightView = (LinearLayout) findViewById(R.id.search_result_view);
        mSingerSearchResultTV = (TextView) findViewById(R.id.singer_search_result_tv);
        mSongSearchResultTV = (TextView) findViewById(R.id.song_search_result_tv);
        initSingerView();
        initSongView();
    }

    private void showLoadingView() {
        mLoadingView.setVisibility(View.VISIBLE);
        mDefaultRightView.setVisibility(View.GONE);
        mResultRightView.setVisibility(View.GONE);
    }

    private void showLoadingErrorView(String txt) {
        mLoadingView.setVisibility(View.VISIBLE);
        mDefaultRightView.setVisibility(View.GONE);
        mResultRightView.setVisibility(View.GONE);
        mLoadingView.showLoadFail(txt);
    }

    private void initDefaultRightView() {
        mDefaultRightView = (LinearLayout) findViewById(R.id.search_result_view_default);
        mSearchKeywordLay = (AutoTextViewContainer) findViewById(R.id.search_view_everybody_search_lay);

        mSearchKeywordLay.setItemClickCallback(new IOnItemClickCallback() {

            @Override
            public void onItemClick(String keyword) {
                if (mClickKeywordPresenter == null) {
                    mClickKeywordPresenter = new SingerAndSongPresenter();
                }
                mClickKeywordPresenter.start(keyword);
                mCurKeyWord = keyword;
                showLoadingView();
                EvLog.i("wrq", "here starts the keyword presenter");
            }
        });
        mSearchKeywordLay.setEdgeListener(new IEdgeListener() {

            @Override
            public void onUpEdge() {
                MainViewManager.getInstance().getStatusBar().requestFocus();
            }

            @Override
            public void onRightEdge() {
//                MainViewManager.getInstance().getStatusBar().setSelectedNumFocus();
            }

            @Override
            public void onLeftEdge() {
                searchWidgetResumeFocusFromRight();
            }
        });

        mHistoryDatas = new ArrayList<SearchHistoryItem>();
        mHistoryListView = (SearchHistoryListView) findViewById(R.id.search_history_lv);
        mHistoryAdapter = new SearchHistoryAdapter(mActivity, mHistoryListView, mHistoryDatas);
        mHistoryListView.setAdapter(mHistoryAdapter);
        mHistoryAdapter.setSongNameSpecWidth(mSongNameSpec);
        mHistoryListView.setOnItemClickCallback(new OnItemClickCallback() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id, int itemState) {
                final SearchHistoryItem item = (SearchHistoryItem) parent.getAdapter().getItem(position);
                if (item == null) {
                    return;
                }
                if (item.mItemType == SearchHistoryItem.SEARCH_ITEM_TYPE_SONG) {
                    final Song song = SongManager.getInstance().getSongById(item.mId);
                    if (song == null) {
                        return;
                    }
                    if (itemState == SongListView.ITEM_STATE_NORMAL) {
                        SongOperationManager.getInstance().orderSong(song.getId(), GlobalSearchView.this);
                        UmengAgent.onEvent(mActivity, EventConst.ID_CLICK_ORDER_SONG_VIEW_SONG);
                    } else if (itemState == SongListView.ITEM_STATE_TOP) {
                        SongOperationManager.getInstance().topSong(song.getId(), GlobalSearchView.this);
                        UmengAgent.onEvent(mActivity, EventConst.ID_CLICK_ORDER_SONG_VIEW_TOP_SONG);
                    } else if (itemState == SongListView.ITEM_STATE_FAVORITE) {
                        if (FavoriteListManager.getInstance().isAlreadyExists(song.getId())) {
                            if (FavoriteListManager.getInstance().delSong(song.getId())) {
                                UmengAgentUtil.onEventFavoriteAction(mActivity,
                                        EventConst.ID_CLICK_ORDER_SONG_VIEW_FAVORITE, false);
                            }
                            return;
                        } else if (FavoriteListManager.getInstance().addSong(song.getId())) {
                            UmengAgentUtil.onEventFavoriteAction(mActivity,
                                    EventConst.ID_CLICK_ORDER_SONG_VIEW_FAVORITE, true);
                            return;
                        }
                    }
                } else {
                    //FIXME
                    Singer singer = SingerManager.getInstance().getSinger(item.mId);
                    if (mSingerClickListener != null && singer != null) {
                        mSingerClickListener.onSingerItemClick(singer, MainViewId.ID_SEARCH);
                    } else {
                        EvLog.e(" no SingerItem clicked!!");
                    }
                }
            }
        });

        mHistoryListView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mHistoryAdapter == null){
                    return;
                }
                SearchHistoryItem item = mHistoryAdapter.getItem(position);
                if (item == null){
                    return;
                }
                isHistory = true;
                if (FavoriteListManager.getInstance().isAlreadyExists(item.getmId())) {
                    mHistoryListView.highlightFavoriteIcon();
                } else {
                    mHistoryListView.restoreFavoriteIcon();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mHistoryListView.setOnSongListKeyDownEventListener(new OnSongListKeyDownEventListener() {

            @Override
            public void onRightEdgeKeyDown() {
//                MainViewManager.getInstance().getStatusBar().setSelectedNumFocus();
            }

            @Override
            public void onLeftEdgeKeyDown() {
                searchWidgetResumeFocusFromRight();
            }

            @Override
            public void onDownEdgeKeyDown() {
                MainViewManager.getInstance().setSmallMvFocus();
            }

            @Override
            public void onUpEdgeKeyDown() {

            }
        });

        mClearHistoryBtn = (MaskFocusButton) findViewById(R.id.search_view_clear_history_btn);
        mClearHistoryBtn.setFocusFrame(R.drawable.global_search_clear_history_btn);
        mClearHistoryBtn.setFocusPadding(mClearHistoryBtnPadding, mClearHistoryBtnPadding,
                mClearHistoryBtnPadding, mClearHistoryBtnPadding);
        mClearHistoryBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SearchHistoryManager.getInstance().clearSearchHistory();
                mHistoryDatas.clear();
                mHistoryAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initDimens() {
        mDisMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(mDisMetrics);
        mTextHintSize = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px39);
        mTextCommonSize = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px39);
        mClearHistoryBtnPadding = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px21);
        mScrollViewFaddingLength = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px60);
        mSongNameSpec = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px650);
        mSingerGridViewItemWidth = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px177);
        mSingerGridViewUnitWidth = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px190);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        PlayListManager.getInstance().registerListener(this);
        MainViewManager.getInstance().addMVSwitchListener(this);
        FavoriteListManager.getInstance().registerListener(this);
        MainViewManager.getInstance().getStatusBar().hideWithoutLogo();
        if (TextUtils.isEmpty(mSearchWidget.getSearchText())) {
            loadKeyWord();
            loadSearchHistoryDatas();
            mSearchWidget.getKeyboardView().setSelection(0);
            mSearchWidget.getKeyboardView().requestFocus();
        } else {
            EvLog.i("super.mBackViewId:" + super.mBackViewId);
            if (super.mBackViewId == MainViewId.ID_SINGER_DETAILS) {
                mSingerGV.requestFocus();
            }
        }
    }

    private void loadKeyWord() {
        if (mKeyWordArray.size() == 0) {
            showLoadingView();
            mHotSearchPresenter = new KeyWordPresenter();
            mHotSearchPresenter.start();
        } else {
            mSearchKeywordLay.addTexts(mKeyWordArray);
        }
    }

    private void loadSearchHistoryDatas() {
        isHistory = true;
        mHistoryListView.setVisibility(View.VISIBLE);
        mHistoryDatas.clear();
        mHistoryDatas.addAll(SearchHistoryManager.getInstance().getSearchHistoryList());
        mHistoryAdapter.notifyDataSetChanged();
        mHistoryListView.refreshDrawableState();
        EvLog.d("get Search History Count: " + mHistoryDatas.size());
    }

    @Override
    protected void onDetachedFromWindow() {
        MainViewManager.getInstance().getStatusBar().showAll();
        MainViewManager.getInstance().removeMVSwitchListener(this);
        PlayListManager.getInstance().unregisterListener(this);
        FavoriteListManager.getInstance().unregisterListener(this);
        mCurSpell = "";
        super.onDetachedFromWindow();
    }

    //FIXME
    private void loadSingerData() {
//        EvLog.i("please implement loadSingerData ");
        mSingerDatas.clear();
        mLoadSingerFinish = false;
        if (mSingerPageLoadPresenter != null) {
            mSingerPageLoadPresenter.stopTask();
        }
        mSingerPageLoadPresenter = new PickSingerPageLoadPresenter(PAGE_SIZE, mSingerCallback, mCurSpell, 0);
        mSingerPageLoadPresenter.loadData();
    }

    //FIXME
    private void loadSongData() {
        EvLog.i("loadSongData ");
        mLoadSongFinish = false;
        mSongDatas.clear();
        if (mSongPageLoadPresenter != null) {
            mSongPageLoadPresenter.stopTask();
        }
        mSongPageLoadPresenter = new OrderSongPageLoadPresenter(PAGE_SIZE, this, mCurSpell);
        mSongPageLoadPresenter.setOffLineSearch(false);
        mSongPageLoadPresenter.loadData();
    }

    //FIXME
    private void showDefaultView() {
        mLoadingView.setVisibility(View.GONE);
        mResultRightView.setVisibility(View.GONE);
        mDefaultRightView.setVisibility(View.VISIBLE);
    }

    //FIXME
    private void showSearchResultView() {
        if (mLoadSingerFinish && mLoadSongFinish) {
            mLoadingView.setVisibility(View.GONE);
            mDefaultRightView.setVisibility(View.GONE);
            mResultRightView.setVisibility(View.VISIBLE);
        }
    }

    private void onSearchContentChanged(String content) {
        String spell = changeNum2Letter(content);
        if (!mCurSpell.equals(spell)) {
            mCurSpell = spell;

            if (!INVALID_SPELL.equals(mCurSpell)) {
                showLoadingView();

                loadSingerData();
                loadSongData();
            }
        }
        if (INVALID_SPELL.equals(spell)) {
            showDefaultView();
        } else {
//            showSearchResultView();
        }
    }


    private void initSearchView() {
        mSearchWidget = (SearchWidget) findViewById(R.id.global_search_widget);
        mSearchWidget.setFirstTitle(getString(R.string.global_search_title));

        mSearchWidget.setBtnClickListener(new ISearchBtnClickListener() {

            @Override
            public void onClickBtn(int index) {
                if (index == SearchWidget.SEARCH_BTN_123) {
                    SearchKeyboardView boardView = mSearchWidget.getKeyboardView();
                    if (boardView == null) {
                        return;
                    }
                    boardView.switchKeyboard();
                    if (boardView.isAlphabetKeyboard()) {
                        mSearchWidget.setSwitchBtnText("123");
                    } else {
                        mSearchWidget.setSwitchBtnText("ABC");
                    }
                    LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_KEYBOARD_SWITCH);
                } else if (index == SearchWidget.SEARCH_BTN_CLEAN) {
                    mSearchWidget.setSearchText("");
                    mSearchWidget.setSearchTextSize(mTextHintSize);
                    onSearchContentChanged("");
                    LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_KEYBOARD_CLEAR);
                } else if (index == SearchWidget.SEARCH_BTN_DEL) {
                    String content = mSearchWidget.getSearchText();
                    if (!TextUtils.isEmpty(content)) {
                        String spell = content.substring(0, content.length() - 1);
                        mSearchWidget.setSearchTextSize(TextUtils.isEmpty(spell) ? mTextHintSize
                                : mTextCommonSize);
                        mSearchWidget.setSearchText(spell);
                        onSearchContentChanged(spell);

                        LogAnalyzeManager.onEvent(mActivity,
                                EventConst.ID_CLICK_ORDER_SONG_VIEW_BACKSPACE);
                    } else if (!TextUtils.isEmpty(mCurKeyWord)) {
                        EvLog.i("wrq", "back from hot search lay");
                        mCurKeyWord = "";
                       /* showDefaultView();
                        loadSongData();*/
                    }
                }
            }
        });


        mSearchWidget.setUpEdgeListener(new IUpEdgeListener() {

            @Override
            public void onUpEdge() {
                MainViewManager.getInstance().getStatusBar().requestFocus();
            }
        });
        mSearchWidget.setRightEdgeListener(new IRightEdgeListener() {

            @Override
            public void onRightEdge() {
                if (mDefaultRightView.getVisibility() == View.VISIBLE) {
//                    EvLog.i("mSearchKeywordLay requestFocus");
                    mSearchKeywordLay.resetFocus();
                }
                if (mResultRightView.getVisibility() == View.VISIBLE) {
                    EvLog.i("mResultRightView requestFocus");
                    if (mSingerGV.getVisibility() == View.VISIBLE) {
                        mSingerGV.requestFocus();
                    } else if (mSongListView.getVisibility() == View.VISIBLE) {
                        mSongListView.requestFocus();
                    } else {
                        EvLog.i("mSongListView getVisibility：s" + mSongListView.getVisibility());
                    }
                }
            }
        });
        mSearchWidget.setItemClickListener(new ISearchItemClickListener() {

            @Override
            public void onClickItem(Key key) {
                if (key != null && key.enable) {
                    StringBuilder sb = new StringBuilder(mSearchWidget.getSearchText());
                    mSearchWidget.setSearchTextSize(mTextCommonSize);
                    mSearchWidget.setSearchText(sb.append(key.label).toString());
                    onSearchContentChanged(sb.toString());
                    EvLog.i("SearchContent " + sb.toString());
                }
            }
        });
    }

    @Override
    protected void clickExitKey() {
        mSearchWidget.setSearchText("");
    }

    @Override
    protected int getLayResId() {
        return R.layout.main_search;
    }

    @Override
    protected int getViewId() {
        return MainViewId.ID_SEARCH;
    }

    @Override
    protected void resetFocus() {

    }

    //FIXME
    public void setSingerClickListener(ISingerClickListener listener) {
        EvLog.i("implement setSingerClickListener ");
        mSingerClickListener = listener;
        if (mSingerAdapter != null) {
            mSingerAdapter.setSingerClickListner(listener);
        }
    }

    private String changeNum2Letter(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }

        char[] chars = content.toCharArray();
        int size = chars.length;
        for (int i = 0; i < size; i++) {
            switch (chars[i] - '0') {
                case 0:
                case 6:
                    chars[i] = 'L';
                    break;
                case 1:
                    chars[i] = 'Y';
                    break;
                case 2:
                    chars[i] = 'E';
                    break;
                case 3:
                case 4:
                    chars[i] = 'S';
                    break;
                case 5:
                    chars[i] = 'W';
                    break;
                case 7:
                    chars[i] = 'Q';
                    break;
                case 8:
                    chars[i] = 'B';
                    break;
                case 9:
                    chars[i] = 'J';
                    break;
                default:
                    break;
            }
        }

        return new String(chars);
    }

    @Override
    public void onFavoriteListChange() {
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                if (!isHistory){
                    if (mSongListView == null || mSongAdapter == null) {
                        return;
                    }

                    Song song = (Song) mSongAdapter.getItem(mSongListView.getSelectedItemPosition());
                    if (song == null) {
                        return;
                    }
                    if (FavoriteListManager.getInstance().isAlreadyExists(song.getId())) {
                        mSongListView.highlightFavoriteIcon();
                    } else {
                        mSongListView.restoreFavoriteIcon();
                    }
                } else{
                   if (mHistoryListView == null || mHistoryAdapter == null){
                       return;
                   }
                   SearchHistoryItem item = (SearchHistoryItem)mHistoryAdapter.getItem(mHistoryListView.getSelectedItemPosition());
                   if (item == null){
                       return;
                   }
                   if (FavoriteListManager.getInstance().isAlreadyExists(item.getmId())){
                       mHistoryListView.highlightFavoriteIcon();
                   }else {
                       mHistoryListView.restoreFavoriteIcon();
                   }
                }
            }
        });
    }

    /**
     * [关键字异步任务类]
     */
    class KeyWordPresenter extends AsyncPresenter<List<String>> {

        @Override
        protected List<String> doInBackground(Object... params)
                throws Exception {
            List<String> keyList = DCDomain.getInstance().requestHotSearchWords();
            return keyList;
        }

        @Override
        protected void onCompleted(List<String> result, Object... params) {
            if (result != null) {
                if (mKeyWordArray.size() == 0) {
                    mKeyWordArray.clear();
                    mKeyWordArray.addAll(result);
                }
                mSearchKeywordLay.addTexts(mKeyWordArray);
                EvLog.i("set mSearchKeywordLay mSearchKeywordLay");
                if (TextUtils.isEmpty(mSearchWidget.getSearchText())) {
                    showDefaultView();
                }
            } else {
                //FIXME
                if (TextUtils.isEmpty(mSearchWidget.getSearchText())) {
                    showLoadingErrorView(getResources().getString(R.string.error_loading_data));
                }
//                showSingerLoadingErrorView(R.string.error_loading_data);
            }
            mHotSearchPresenter = null;
        }

        @Override
        protected void onFailed(Exception exception, Object... params) {
            //FIXME
            showLoadingView();
            mLoadingView.showLoadFail(R.string.error_loading_data);
            mHotSearchPresenter = null;
//            showSingerLoadingErrorView(R.string.global_search_network_error);
        }
    }

    private void setupGridViewParams(int num) {
        float density = 1.0f/*mDisMetrics.density*/;
        int numColums = num;

        int allWidth = (int) (mSingerGridViewUnitWidth * (numColums + 1) * density);
        int itemWidth = (int) (mSingerGridViewItemWidth * density);
        EvLog.i("allWidth:" + allWidth + ",itemWidth:" + itemWidth);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                allWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        mSingerGV.setLayoutParams(params);
        mSingerGV.setColumnWidth(itemWidth);
        mSingerGV.setHorizontalSpacing(0);
        mSingerGV.setStretchMode(GridView.NO_STRETCH);
        mSingerGV.setNumColumns(numColums);
    }

    @Override
    public void onPreLoadData(boolean isReset, boolean isNext) {
        mSongListView.showFootLoadingView();
    }

    private void showSongListView(int totalNum) {
        mSongListView.setVisibility(View.VISIBLE);
        if (mSongDatas.size() >= SongManager.getInstance().getCountByFuzzySpell(mCurSpell,
                false)) {
            mSongListView.removeFootLoadingView();
        } /*else {
            mSongListView.showFootLoadingView();
        }*/
    }

    @Override
    public void onPostLoadData(Exception e, boolean isReset, boolean isNext,
                               List<Song> datas) {
        EvLog.d("song updateList isReset: " + isReset + ", datas.size: " + datas.size());
//        mSongListView.showFootLoadingView();
        mLoadSongFinish = true;
        if (e != null) {
            //FIXME
//            handleException(e, isReset);
            EvLog.i("e return ");
            e.printStackTrace();
            showLoadingErrorView(getResources().getString(R.string.error_loading_data));
            return;
        }
        showSearchResultView();
        if (e == null && mSongAdapter != null && datas != null) {
            int mSongNum = mSongPageLoadPresenter.getTotalSongNum();
            if (isReset) {
                mSongDatas.clear();

                EvLog.d("mSongDatas.size: " + mSongDatas.size() + ", mSongNum: " + mSongNum);
                /*if (mResultRightView.getVisibility() == View.VISIBLE)*/
                {
                    mSongSearchResultTV.setText(getString(
                            R.string.global_search_song_num, mSongNum));
                }
            }
            
            /*if (mSongDatas.size() >= mSongNum) {
                mSongListView.showFootLoadingView();
            } else {
                mSongListView.removeFootLoadingView();
            }*/
            mSongDatas.addAll(datas);
            if (mSongDatas.size() == 0) {
                mSongListView.setVisibility(View.GONE);
                return;
            }
            showSongListView(mSongNum);

            mSongAdapter.setSearchSpell(mCurSpell);
            mSongAdapter.notifyDataSetChanged();
            if (isReset) {
                mSongListView.setSelection(0);
            }
        }
    }

    @Override
    public void onTopSongSuccess(int songId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTopSongFailed(int songId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOrderSongSuccess(int songId) {
        if (DeviceConfigManager.getInstance().isOrderNeedAnimation()) {
            if (mSongListView != null && mSongListView.isFocused()) {
                mSongListView.startOrderSongAnimDelayed();
                return;
            }
            if (mHistoryListView != null && mHistoryListView.isFocused()) {
                mHistoryListView.startOrderSongAnimDelayed();
                return;
            }
        }
        UmengAgent.onEvent(mActivity, EventConst.ID_CLICK_ORDER_SONG_VIEW_SONG);
    }

    @Override
    public void onOrderSongFailed(int songId) {
        // TODO Auto-generated method stub

    }

    private class SingerAndSongPresenter extends AsyncPresenter<Boolean> {
        private List<Song> songList;
        private List<Singer> singerList;

        @Override
        protected Boolean doInBackground(Object... params) throws Exception {
            String keyword = (String) params[0];
            songList = SongManager.getInstance().getSongByName(keyword);
            singerList = SingerManager.getInstance().getSingerByName(keyword);
            return !songList.isEmpty() || !singerList.isEmpty();
        }

        @Override
        protected void onCompleted(Boolean result, Object... params) {
            mLoadSingerFinish = true;
            mLoadSongFinish = true;
            showSearchResultView();

            mSingerDatas.clear();
            mSingerDatas.addAll(singerList);
            int mSingerNum = mSingerDatas.size();
            setupGridViewParams(mSingerNum);
            mSingerAdapter.notifyDataSetChanged();

            mSongDatas.clear();
            mSongDatas.addAll(songList);
            int mSongNum = mSongDatas.size();
            mSongAdapter.notifyDataSetChanged();
            showSongListView(mSongNum);
//            showSingerListView();
            //更改显示数量
            /*if (!mIsDefaultView)*/
            {
                mSingerSearchResultTV.setText(getString(
                        R.string.global_search_singer_num, mSingerNum));
                mSongSearchResultTV.setText(getString(
                        R.string.global_search_song_num, mSongNum));
            }
            EvLog.i("onCompleted mSingerNum:" + mSingerNum + ",mSongNum:" + mSongNum);
            //重置焦点
            if (mSingerNum == 0) {
                mSingerGV.setVisibility(View.INVISIBLE);
                if (mSongNum == 0) {
                    mSongListView.setVisibility(View.INVISIBLE);
                    searchWidgetResumeFocusFromRight();
                } else {
                    mSongListView.setSelection(0);
                    mSongListView.requestFocus();
                }
            } else {
                mSingerGV.setVisibility(View.VISIBLE);
                mSingerGV.requestFocus();
                mSingerGV.setSelection(0);
            }
        }

        @Override
        protected void onFailed(Exception exception, Object... params) {
            EvLog.e("Globalsearch", "search keyword error" + exception.toString());
            showLoadingErrorView(getResources().getString(R.string.error_loading_data));
            UmengAgentUtil.reportError(exception);
        }
    }


    @Override
    public void onPlayListChange() {
        BasePresenter.runInUI(new Runnable() {

            @Override
            public void run() {
                if (mSongAdapter != null) {
                    mSongAdapter.notifyDataSetChanged();
                }

                if (mHistoryAdapter != null) {
                    mHistoryAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onSwitchToMV() {

    }

    @Override
    public void onSwitchToMainView() {
        MainViewManager.getInstance().getStatusBar().hideWithoutLogo();
    }

    public class TestViewAdapter extends BaseAdapter {
        Context context;
        List<Singer> list;

        public TestViewAdapter(Context _context, List<Singer> _list) {
            this.list = _list;
            this.context = _context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.global_search_singer_item_lay, null);
            CircleImageView mCoverIv = (CircleImageView) convertView.findViewById(R.id.ItemImage);
            TextView mNameTv = (TextView) convertView.findViewById(R.id.ItemText);
            Singer info = list.get(position);
            mNameTv.setText(info.getName());
            //设置默认图片
            mCoverIv.setBackgroundResource(R.drawable.singer_default);
            return convertView;
        }
    }

    @Override
    public boolean onSmallMVUpKey() {
        EvLog.i("globalsearch handleMVUPKey ");
        mSearchWidget.getKeyboardView().setLastLineFocus();
        mSearchWidget.getKeyboardView().requestFocus();
        return true;
    }


    @Override
    public boolean onSmallMVRightKey() {
        EvLog.i("globalsearch handleMVRightKey ");
        if (mDefaultRightView.getVisibility() == View.VISIBLE) {
            isHistory = true;
//            mHistoryListView.setSelection(0);
            mSearchKeywordLay.resetFocus();
        } else if (mResultRightView.getVisibility() == View.VISIBLE) {
            mSongListView.requestFocus();
            isHistory = false;
        }
        return true;
    }

    @Override
    public boolean onStatusBarDownKey() {
        return false;
    }
}
