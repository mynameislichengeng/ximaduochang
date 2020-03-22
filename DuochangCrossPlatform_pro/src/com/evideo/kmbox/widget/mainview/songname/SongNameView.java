/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-9-17     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.songname;

import java.util.ArrayList;
import java.util.List;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.evideo.kmbox.BaseApplication;

import android.widget.TextView;

import com.evideo.kmbox.R;
import com.evideo.kmbox.exception.DCNoResultException;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
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
import com.evideo.kmbox.presenter.BasePresenter;
import com.evideo.kmbox.presenter.PageLoadPresenter.IPageLoadCallback;
import com.evideo.kmbox.util.DimensionsUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.NetUtils;
import com.evideo.kmbox.widget.SearchWidget;
import com.evideo.kmbox.widget.SearchWidget.IRightEdgeListener;
import com.evideo.kmbox.widget.SearchWidget.ISearchBtnClickListener;
import com.evideo.kmbox.widget.SearchWidget.ISearchItemClickListener;
import com.evideo.kmbox.widget.common.AnimLoadingView;
import com.evideo.kmbox.widget.common.OnItemClickCallback;
import com.evideo.kmbox.widget.common.OnSongListKeyDownEventListener;
import com.evideo.kmbox.widget.common.SearchKeyboard.Key;
import com.evideo.kmbox.widget.common.SearchKeyboardView;
import com.evideo.kmbox.widget.common.SongListView;
import com.evideo.kmbox.widget.mainmenu.order.OrderSongListAdapter;
import com.evideo.kmbox.widget.mainmenu.order.OrderSongPageLoadPresenter;
import com.evideo.kmbox.widget.mainview.AbsBaseView;
import com.evideo.kmbox.widget.mainview.MainViewId;
import com.evideo.kmbox.widget.mainview.MainViewManager;

/**
 * [功能说明]
 */
public class SongNameView extends AbsBaseView implements IPageLoadCallback<Song>,
        IPlayListListener, IFavoriteListListener, IOrderSongResultListener, ITopSongResultListener {

    private static final int PAGE_SIZE = 50;

    private SearchWidget mSearchWidget;

    private OrderSongListAdapter mAdapter;
    private SongListView mListView;
    private AnimLoadingView mLoadingView;
    private ArrayList<Song> mDatas = null;
    private OrderSongPageLoadPresenter mPageLoadPresenter = null;
    private String mCurSpell = "";
    private int mSongNum;
    private float mTextHintSize;
    private float mTextCommonSize;

    /**
     * @param activity
     * @param backViewId
     */
    public SongNameView(Activity activity, int backViewId) {
        super(activity, backViewId);
        mDatas = new ArrayList<Song>();
        initSearchWidget();
        initView();
    }

    public boolean resumeFocus() {
//        EvLog.e("resumeFocus mDatas.size():" + mDatas.size());
        if (mDatas.size() > 0) {
            return false;
        }
        if (mSearchWidget != null && mSearchWidget.getKeyboardView() != null) {
            mSearchWidget.getKeyboardView().requestFocus();
            return true;
        }
        return false;
    }


    private void initSearchWidget() {
        mSearchWidget = (SearchWidget) findViewById(R.id.song_name_search);
        mSearchWidget.getKeyboardView().setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
//                    EvLog.d("umeng ------");
                    onPageStart();
                }
            }
        });

        mSearchWidget.setFirstTitle(getString(R.string.main_song_name_title));

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
//                    EvLog.i("SearchContent " + sb.toString());
                }
            }
        });

        mSearchWidget.setRightEdgeListener(new IRightEdgeListener() {

            @Override
            public void onRightEdge() {
                mListView.requestFocus();
            }
        });
    }

    private void initView() {
        mTextHintSize = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px39);
        mTextCommonSize = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px39);
//        mSearchBtnPadding = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px19);

        mSongNum = SongManager.getInstance().getCountByFuzzySpell("", !NetUtils.isNetworkConnected(BaseApplication.getInstance().getBaseContext()));
        mPageLoadPresenter = new OrderSongPageLoadPresenter(PAGE_SIZE, this, "");
        mPageLoadPresenter.setOffLineSearch(!NetUtils.isNetworkConnected(BaseApplication.getInstance().getBaseContext()));
        mLoadingView = (AnimLoadingView) findViewById(R.id.song_name_loading_widget);

        mListView = (SongListView) findViewById(R.id.order_song_song_lv);


        mAdapter = new OrderSongListAdapter(mActivity, mListView, mDatas);
        mAdapter.setSongNameSpecWidth(BaseApplication.getInstance().getBaseContext().getResources().getDimensionPixelSize(R.dimen.px590));

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickCallback(new OnItemClickCallback() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id, int itemState) {
                Song song = (Song) parent.getAdapter().getItem(position);
                if (song == null) {
                    return;
                }
                if (itemState == SongListView.ITEM_STATE_NORMAL) {
                    /*mSearchWidget.setSearchText("");
                    mSearchWidget.getKeyboardView().updateKeyboardState("");*/
                    mListView.requestFocus();
                    SongOperationManager.getInstance().orderSong(song.getId(), SongNameView.this);
                    operateSelectItem(parent, view, position, id);
                } else if (itemState == SongListView.ITEM_STATE_TOP) {
                   /* mSearchWidget.setSearchText("");
                    mSearchWidget.getKeyboardView().updateKeyboardState("");*/
                    SongOperationManager.getInstance().topSong(song.getId(), SongNameView.this);
                    mListView.resetUi();
                    operateSelectItem(parent, view, position, id);
                } else if (itemState == SongListView.ITEM_STATE_FAVORITE) {

                    if (FavoriteListManager.getInstance().isAlreadyExists(song.getId())) {
                        if (FavoriteListManager.getInstance().delSong(song.getId())) {
                            mListView.resetUi();
                            operateSelectItem(parent, view, position, id);
                            UmengAgentUtil.onEventFavoriteAction(mActivity,
                                    EventConst.ID_CLICK_ORDER_SONG_VIEW_FAVORITE, false);
                        }
                        return;
                    } else if (FavoriteListManager.getInstance().addSong(song.getId())) {
//                        mListView.startFavoriteAnimnation(position);
                        mListView.resetUi();
                        operateSelectItem(parent, view, position, id);
                        UmengAgentUtil.onEventFavoriteAction(mActivity,
                                EventConst.ID_CLICK_ORDER_SONG_VIEW_FAVORITE, true);
                        return;
                    }
                }

//                if (!TextUtils.isEmpty(mSearchContentTv.getText())) {
//                    mSearchContentTv.setText("");
//                    onSearchContentChanged("");
//                    if (!mKeyboardView.isAlphabetKeyboard()) {
//                        mKeyboardView.switchKeyboard();
//                        mKeyboardSwitcherBtn.setText("123");
//                    }
//                    mSearchContentTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextHintSize);
//                    mKeyboardView.requestFocus();
//                    mKeyboardView.setSelection(0);
//                }
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

        mListView.setOnSongListKeyDownEventListener(new OnSongListKeyDownEventListener() {

            @Override
            public void onRightEdgeKeyDown() {
                if (MainViewManager.getInstance().getStatusBar() != null) {
                    MainViewManager.getInstance().getStatusBar().setSelectedNumFocus();
                }
            }

            @Override
            public void onLeftEdgeKeyDown() {
                mSearchWidget.getKeyboardView().setSelection(5);
                mSearchWidget.getKeyboardView().requestFocus();
            }

            @Override
            public void onDownEdgeKeyDown() {
            }

            @Override
            public void onUpEdgeKeyDown() {
                MainViewManager.getInstance().getStatusBar().requestFocus();
            }
        });

        mListView.setOnFocusChangeListener(new OnFocusChangeListener() {

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

//        mPageLoadPresenter.loadData();
    }

    private void operateSelectItem(AdapterView<?> parent, View view,
                                   int position, long id) {

        if (mAdapter == null || mPageLoadPresenter == null) {
            return;
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
//                mAdapter.notifyDataSetChanged();

        if (position <= (mAdapter.getCount() - 1) && position > (mAdapter.getCount() - 8)) {
            mPageLoadPresenter.loadNextPage();
        }

    }


    private void onSearchContentChanged(String content) {
        String spell = changeNum2Letter(content);
        if (!mCurSpell.equals(spell)) {
            mCurSpell = spell;
            loadData();
        }
    }

    private void loadData() {
        if (mPageLoadPresenter != null) {
            mPageLoadPresenter.stopTask();
        }
        mPageLoadPresenter = new OrderSongPageLoadPresenter(PAGE_SIZE, this, mCurSpell);
        mPageLoadPresenter.setOffLineSearch(!NetUtils.isNetworkConnected(BaseApplication.getInstance().getBaseContext()));
        mPageLoadPresenter.loadData();
    }

    private String changeNum2Letter(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
//        EvLog.d("changeNum2Letter content " + content);
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
//        EvLog.d("changeNum2Letter after " + new String(chars));
        return new String(chars);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getLayResId() {
        return R.layout.main_song_name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getViewId() {
        return MainViewId.ID_SONG_NAME;
    }

    @Override
    protected void resetFocus() {
      /*  if (mSearchWidget.getKeyboardView() != null) {
            mSearchWidget.getKeyboardView().requestFocus();
        }*/
        mSearchWidget.getKeyboardView().setSelection(0);
        mSearchWidget.getKeyboardView().requestFocus();
        mListView.resetState();
        mListView.setSelection(0);
    }

    private void showLoadingView() {
        if (mLoadingView.getVisibility() != View.VISIBLE) {
            mLoadingView.setVisibility(View.VISIBLE);
        }
        mLoadingView.startAnim();
        mListView.setVisibility(View.GONE);
    }

    private void showLoadingErrorView(int resid) {
        if (mLoadingView.getVisibility() != View.VISIBLE) {
            mLoadingView.setVisibility(View.VISIBLE);
        }
        mLoadingView.stopAnim();
        mLoadingView.showLoadFail(resid);
        mListView.setVisibility(View.GONE);
    }

    private void showListView() {
        if (mLoadingView.getVisibility() != View.GONE) {
            mLoadingView.stopAnim();
            mLoadingView.setVisibility(View.GONE);
        }
        mListView.setVisibility(View.VISIBLE);
        if (mDatas.size() >= SongManager.getInstance().getCountByFuzzySpell(mCurSpell,
                !NetUtils.isNetworkConnected(BaseApplication.getInstance().getBaseContext()))) {
            mListView.showFootView(R.string.loading_song_no_more);
        } else {
            mListView.removeFootLoadingView();
        }
    }

    @Override
    public void onPreLoadData(boolean isReset, boolean isNext) {
        if (mDatas.size() == 0 || isReset) {
            showLoadingView();
        } else {
            mListView.showFootLoadingView();
        }
    }

    @Override
    public void onPostLoadData(Exception e, boolean isReset, boolean isNext,
                               List<Song> datas) {
//        EvLog.d("songNameView updateList isReset: " + isReset + " isNext: " + isNext);
        if (e != null) {
            handleException(e, isReset);
        }
        if (e == null && mAdapter != null && datas != null) {
            if (isReset) {
                mDatas.clear();
            }
            mDatas.addAll(datas);
            mAdapter.setSearchSpell(mCurSpell);
            mAdapter.notifyDataSetChanged();
            mSongNum = SongManager.getInstance().getCountByFuzzySpell(
                    changeNum2Letter(mSearchWidget.getSearchText()), !NetUtils.isNetworkConnected(BaseApplication.getInstance().getBaseContext()));
            if (mDatas.size() >= mSongNum) {
                mListView.showFootView(R.string.loading_song_no_more);
                mListView.setNextFocusDownId(R.id.small_mv_frame);
            } else {
                mListView.removeFootLoadingView();
            }
            showListView();
            if (isReset) {
//                mListView.requestFocus();
                mListView.setAdapter(mAdapter);
                mListView.setSelection(0);
            }
        }
    }

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
    
    /*private void orderSongDelayed(final Song song, final boolean top) {
        // clear search text
        mSearchWidget.setSearchText("");
        mSearchWidget.getKeyboardView().updateKeyboardState("");
        postDelayed(new Runnable() {
            @Override
            public void run() {
                PlayListManager.getInstance().addSong(null, song.getId(), top);
            }
        }, KmConfig.ORDER_SONG_DELAY_DURATION);
    }
*/

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        PlayListManager.getInstance().registerListener(this);
        FavoriteListManager.getInstance().registerListener(this);

        if (mDatas.size() == 0) {
            mPageLoadPresenter.loadData();
        }
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

    @Override
    protected void onDetachedFromWindow() {
        PlayListManager.getInstance().unregisterListener(this);
        FavoriteListManager.getInstance().unregisterListener(this);
        /*if (mDatas != null) {
            mDatas.clear();
        }*/
        super.onDetachedFromWindow();
    }

    /**
     * {@inheritDoc}
     */
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
    public void onOrderSongSuccess(int songId) {
        if (DeviceConfigManager.getInstance().isOrderNeedAnimation()) {
            if (mListView != null) {
                mListView.startOrderSongAnimDelayed();
            }
        }
        UmengAgent.onEvent(mActivity, EventConst.ID_CLICK_ORDER_SONG_VIEW_SONG);
    }

    @Override
    public void onTopSongSuccess(int songId) {
        if (DeviceConfigManager.getInstance().isOrderNeedAnimation()) {
            if (mListView != null) {
                mListView.startOrderSongAnimDelayed();
            }
        }
        UmengAgent.onEvent(mActivity, EventConst.ID_CLICK_ORDER_SONG_VIEW_TOP_SONG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTopSongFailed(int songId) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOrderSongFailed(int songId) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void clickExitKey() {
        mSearchWidget.setSearchText("");
        if (mDatas != null) {
            mDatas.clear();
        }
    }

    @Override
    public boolean onSmallMVUpKey() {
        mSearchWidget.getKeyboardView().setLastLineFocus();
        mSearchWidget.getKeyboardView().requestFocus();
        return true;
    }

    @Override
    public boolean onSmallMVRightKey() {
        if (mListView != null && mListView.getVisibility() == View.VISIBLE) {
            mListView.requestFocus();
            return true;
        }
        return false;
    }

    @Override
    public boolean onStatusBarDownKey() {
        if (mListView != null && mListView.getVisibility() == View.VISIBLE) {
            mListView.requestFocus();
        } else {
            mSearchWidget.getKeyboardView().setLastLineFocus();
            mSearchWidget.getKeyboardView().requestFocus();
        }
        return true;
    }
}
