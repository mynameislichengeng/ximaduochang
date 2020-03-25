/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年10月9日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.usercenter;

import android.app.Activity;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
import com.evideo.kmbox.model.playerctrl.PlayListDAOManager;
import com.evideo.kmbox.model.playerctrl.PlayListDAOManager.ListHandler;
import com.evideo.kmbox.model.playerctrl.list.FavoriteListManager;
import com.evideo.kmbox.model.playerctrl.list.FavoriteListManager.IFavoriteListListener;
import com.evideo.kmbox.model.playerctrl.list.FavoriteListManager.ISyncFavoriteCloudListListener;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager.IPlayListListener;
import com.evideo.kmbox.model.playerctrl.list.SongOperationManager;
import com.evideo.kmbox.model.playerctrl.list.SongOperationManager.IOrderSongResultListener;
import com.evideo.kmbox.model.playerctrl.list.SongOperationManager.ITopSongResultListener;
import com.evideo.kmbox.model.playerctrl.sunglist.SungListItem;
import com.evideo.kmbox.model.playerctrl.sunglist.SungListManager;
import com.evideo.kmbox.model.playerctrl.sunglist.SungListManager.IPlayHistoryListener;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.model.umeng.EventConst;
import com.evideo.kmbox.model.umeng.UmengAgent;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.BasePresenter;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.charge.ChargeViewManager;
import com.evideo.kmbox.widget.charge.ChargeViewManager.IChargeFinishListener;
import com.evideo.kmbox.widget.common.FavoriteListView;
import com.evideo.kmbox.widget.common.LoadingAndRetryWidget;
import com.evideo.kmbox.widget.common.LoadingAndRetryWidget.IClickRetryBtnListener;
import com.evideo.kmbox.widget.common.OnItemClickCallback;
import com.evideo.kmbox.widget.common.OnSongListKeyDownEventListener;
import com.evideo.kmbox.widget.mainview.AbsBaseView;
import com.evideo.kmbox.widget.mainview.MainViewId;
import com.evideo.kmbox.widget.mainview.MainViewManager;
import com.evideo.kmbox.widget.mainview.MainViewManager.IMVSwitchListener;
import com.evideo.kmbox.widget.mainview.favorite.FavoriteListAdapter;
import com.evideo.kmbox.widget.mainview.selected.SelectedListAdapter;
import com.evideo.kmbox.widget.mainview.selected.SelectedListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * [功能说明]
 */
public class UserCenterSimpleView extends AbsBaseView implements
        IPlayListListener, IFavoriteListListener, IPlayHistoryListener,
        View.OnFocusChangeListener, AdapterView.OnItemSelectedListener,
        IOrderSongResultListener, ITopSongResultListener, IChargeFinishListener,
        ISyncFavoriteCloudListListener, IClickRetryBtnListener, IMVSwitchListener {


    private final String TAG = UserCenterSimpleView.class.getSimpleName();


    private static final int TAB_ID_USER = 1;
    private static final int TAB_ID_SELECTED = 2;
    private static final int TAB_ID_SUNG_RECORD = 3;
    private static final int TAB_ID_MY_FAVORITE = 4;

    public static class UserCenterTabItem {
        public int tabId;
        public String tabName;

        public UserCenterTabItem(int id, String name) {
            this.tabId = id;
            this.tabName = name;
        }
    }

    private TextView mFavoriteTitle;
    private FavoriteListView mFavoriteListView;
    private FavoriteListAdapter mFavoriteAdapter;
    private TextView mFavoriteEmptyIv;
    private ArrayList<Integer> mFavoriteDatas;

    private SungListAdapter mSungAdapter = null;
    private UserCenterSungListView mSungListView = null;
    private ArrayList<SungListItem> mSungDatas = null;
    private TextView mSungEmptyTv;

    private SelectedListAdapter mSelectedAdapter;
    private SelectedListView mSelectedListView;
    private TextView mSelectedEmptyHint;
    private ArrayList<KmPlayListItem> mSelectedDatas;
    private TextView mSelectedTitle;

    private GridView mGridView = null;

    private LinearLayout mSungRect = null;
    private LinearLayout mFavoriteRect = null;
    private LinearLayout mSelectedRect = null;
    private LinearLayout mLoginRect = null;
    private UserInfoWidget mUserInfoView = null;
    private LoadingAndRetryWidget mLoadingFavorite = null;

    public UserCenterSimpleView(Activity activity, int backViewId) {
        super(activity, backViewId);
        mSungRect = (LinearLayout) findViewById(R.id.sung_list_rect);
        mFavoriteRect = (LinearLayout) findViewById(R.id.favorite_list_rect);
        mSelectedRect = (LinearLayout) findViewById(R.id.selected_list_rect);
        mLoginRect = (LinearLayout) findViewById(R.id.login_rect);
        initLeftTab();
        initSungView();
        initFavoriteView();
        initSelectedView();
    }

    @Override
    protected int getLayResId() {
        return R.layout.main_user_simple_space;
    }

    private UserCenterTabAdapter mTabAdapter = null;

    private void initLeftTab() {
        mGridView = (GridView) findViewById(R.id.main_view_my_space_gv);

        ArrayList<UserCenterTabItem> mTabs = new ArrayList<UserCenterTabItem>();

        if (DeviceConfigManager.getInstance().isSupportUserLogin()) {
            mTabs.add(new UserCenterTabItem(TAB_ID_USER,
                    getString(R.string.main_my_space_user_info)));
            mLoginRect.setVisibility(View.VISIBLE);
        }

        mTabs.add(new UserCenterTabItem(TAB_ID_SELECTED,
                getString(R.string.main_my_song_selected_tab)));
        mTabs.add(new UserCenterTabItem(TAB_ID_SUNG_RECORD,
                getString(R.string.main_my_song_sung_tab)));
        mTabs.add(new UserCenterTabItem(TAB_ID_MY_FAVORITE,
                getString(R.string.main_my_space_favorite_tab)));

        mTabAdapter = new UserCenterTabAdapter(mActivity, mGridView, mTabs);
        mGridView.setAdapter(mTabAdapter);
        mGridView.setOnFocusChangeListener(this);
        mGridView.setOnItemSelectedListener(this);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                log("--mGridView----onItemClick()---position:" + position);
                operateLeftTabItemSelect(parent, view, position, id);
                if (position == 1) {
                    mSelectedListView.requestFocus();

                } else if (position == 2) {
                    mSungListView.requestFocus();
                } else if (position == 3) {
                    mFavoriteListView.requestFocus();
                }

            }
        });
        mGridView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (mLoginRect.getVisibility() == View.VISIBLE) {
                            if (mUserInfoView != null && mUserInfoView.getVisibility() == View.VISIBLE) {
//                                mUserInfoView.setLogOutButtonFocus();
                            }
                            return true;
                        } else if (mSelectedRect.getVisibility() == View.VISIBLE) {
                            if (mSelectedDatas.size() > 0) {
                                mSelectedListView.requestFocus();
                            }
                            return true;
                        } else if (mSungRect.getVisibility() == View.VISIBLE) {
                            if (mSungDatas.size() > 0) {
                                mSungListView.requestFocus();
                            }
                            return true;
                        } else if (mFavoriteRect.getVisibility() == View.VISIBLE) {
                            if (mFavoriteDatas.size() > 0) {
                                mFavoriteListView.requestFocus();
                            }
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    private void initSelectedView() {
        mSelectedEmptyHint = (TextView) findViewById(R.id.selected_empty_hint_tv);
        mSelectedTitle = (TextView) findViewById(R.id.selected_title);
        mSelectedListView = (SelectedListView) findViewById(R.id.my_song_selected_list_view);
        mSelectedDatas = (ArrayList<KmPlayListItem>) PlayListManager
                .getInstance().getList();
        mSelectedAdapter = new SelectedListAdapter(mActivity,
                mSelectedListView, mSelectedDatas);

        mSelectedAdapter.setSongNameWidth(getResources()
                .getDimensionPixelOffset(R.dimen.px1000), getResources()
                .getDimensionPixelOffset(R.dimen.px1000));
        updateSelectedTitle(mSelectedDatas.size());
        mSelectedListView.setAdapter(mSelectedAdapter);
        mSelectedListView.setEmptyView(mSelectedEmptyHint);
        mSelectedListView.setOnItemClickCallback(new OnItemClickCallback() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id, int itemState) {

                log("--mSelectedListView--onItemClick()----position：" + position + "，itemState：" + itemState);
                KmPlayListItem item = PlayListManager.getInstance()
                        .getItemByPos(position);
                mSelectedListView.setSelectPositon(position);

                if (itemState == SelectedListView.ITEM_STATE_DELETE) {
                    PlayListManager.getInstance().delSong(item.getSerialNum());
                    LogAnalyzeManager.onEvent(getContext(),
                            EventConst.ID_CLICK_SELECTED_LIST_VIEW_DELETE_SONG);
                    mSelectedListView.resetState();
                    mSelectedListView.invalidate();
                } else if (itemState == SelectedListView.ITEM_STATE_TOP) {
                    PlayListManager.getInstance().addSong(item.getCustomerid(),
                            item.getSongId(), true);
                    LogAnalyzeManager.onEvent(getContext(),
                            EventConst.ID_CLICK_SELECTED_LIST_VIEW_TOP_SONG);
                    mSelectedListView.resetState();
                    mSelectedListView.invalidate();
                    operateAlreadyOrderSelectItem(parent, view, position, id);
                } else if (itemState == SelectedListView.ITEM_STATE_CUT_SONG) {
                    MainViewManager.getInstance().cutSong();
                    LogAnalyzeManager.onEvent(getContext(),
                            EventConst.ID_CLICK_SELECTED_LIST_VIEW_CUT_SONG);
                    mSelectedListView.resetState();
                    mSelectedListView.invalidate();
                    operateAlreadyOrderSelectItem(parent, view, position, id);

                } else if (itemState == SelectedListView.ITEM_STATE_FAVORITE) {

                    if (FavoriteListManager.getInstance().isAlreadyExists(
                            item.getSongId())) {
                        if (FavoriteListManager.getInstance().delSong(
                                item.getSongId())) {
                            mSelectedListView.resetState();
                            mSelectedListView.invalidate();
                            operateAlreadyOrderSelectItem(parent, view, position, id);
                            UmengAgentUtil
                                    .onEventFavoriteAction(
                                            BaseApplication.getInstance().getBaseContext(),
                                            EventConst.ID_CLICK_SELECTED_LIST_VIEW_FAVORITE,
                                            false);
                        }
                        return;
                    } else if (FavoriteListManager.getInstance().addSong(
                            item.getSongId())) {
                        mSelectedListView.resetState();
                        mSelectedListView.invalidate();
                        operateAlreadyOrderSelectItem(parent, view, position, id);
                        UmengAgentUtil
                                .onEventFavoriteAction(
                                        BaseApplication.getInstance().getBaseContext(),
                                        EventConst.ID_CLICK_SELECTED_LIST_VIEW_FAVORITE,
                                        true);
                    } else {
                        mSelectedListView.resetState();
                        mSelectedListView.invalidate();
                        operateAlreadyOrderSelectItem(parent, view, position, id);
                    }
                } else if (itemState == SelectedListView.ITEM_STATE_NORMAL) {
                    mSelectedListView.resetState();
                    mSelectedListView.invalidate();
                    operateAlreadyOrderSelectItem(parent, view, position, id);
                }
            }
        });

        mSelectedListView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                log("---mSelectedListView---onFocusChange()---hasFocus:" + hasFocus);
                /*
                 * if (hasFocus) {
                 * LogAnalyzeManager.getInstance().onPageStart(PageName
                 * .ORDERED_LIST); onPageStart(); }
                 */
                if (hasFocus) {
                    mSelectedListView.setAdapter(mSelectedAdapter);
                    mSelectedListView.setSelection(0);
                } else {
                    mSelectedListView.resetState();
                }

                /*
                 * if (mSelectedAdapter.getCount() > 0 && mSelectedListView !=
                 * null) { mSelectedListView.requestFocus();
                 * mSelectedListView.resetItemState(); }
                 */
                EvLog.e("mSelectedListView :" + hasFocus
                        + mSelectedListView.getSelectedItemPosition());
                if (mSelectedAdapter != null) {
                    mSelectedAdapter.refreshSelectedState(hasFocus,
                            mSelectedListView.getSelectedItemPosition());
                }
            }
        });

        mSelectedListView
                .setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {
                        operateAlreadyOrderSelectItem(parent, view, position, id);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
        mSelectedListView
                .setOnSongListKeyDownEventListener(new OnSongListKeyDownEventListener() {

                    @Override
                    public void onRightEdgeKeyDown() {
                    }

                    @Override
                    public void onLeftEdgeKeyDown() {
                        if (mGridView != null) {
                            mGridView.requestFocus();
                        }
                    }

                    @Override
                    public void onDownEdgeKeyDown() {
                        MainViewManager.getInstance().setSmallMvFocus();
                    }

                    @Override
                    public void onUpEdgeKeyDown() {

                    }
                });
    }

    private void operateAlreadyOrderSelectItem(AdapterView<?> parent,
                                               View view, int position, long id) {

        log("--mSelectedListView--operateSelectItem()----");
        mSelectedListView.restoreFavoriteIcon();

        KmPlayListItem item = (KmPlayListItem) parent
                .getAdapter().getItem(position);

        if (item == null) {
            return;
        }

        if (FavoriteListManager.getInstance().isAlreadyExists(
                item.getSongId())) {
            mSelectedListView.highlightFavoriteIcon();
        }
    }


    private void initSungView() {
        mSungEmptyTv = (TextView) findViewById(R.id.sung_empty_hint_tv);

        mSungListView = (UserCenterSungListView) findViewById(R.id.my_song_sung_list_view);
//        mSungListView.setEmptyView(mSungEmptyIv);
        if (mSungDatas == null) {
            mSungDatas = new ArrayList<SungListItem>();
        }


        mSungAdapter = new SungListAdapter(mActivity, mSungListView, mSungDatas);
        mSungListView.setAdapter(mSungAdapter);
        mSungListView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    EvLog.d("mSungListView hasFocus" + hasFocus);
                    mSungListView.setAdapter(mSungAdapter);
                    mSungListView.setSelection(0);
                    mSungAdapter.refreshOrderedState();
                    // onPageStart();
                }
            }
        });

        mSungListView.setOnItemClickCallback(new OnItemClickCallback() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id, int itemState) {
                SungListItem item = (SungListItem) parent.getAdapter().getItem(
                        position);

                if (item == null) {
                    return;
                }
                mSungListView.setmClickItemPos(position);
                if (itemState == UserCenterSungListView.ITEM_STATE_DELETE) {
                    EvLog.d("del sung item:" + item.getSongName() + ","
                            + item.getShareCode());
                    Message msg = PlayListDAOManager.getInstance()
                            .getHandler().obtainMessage();
                    msg.what = ListHandler.SUNGLIST_DEL_ITEM;
                    msg.obj = item.getShareCode();
                    PlayListDAOManager.getInstance().getHandler()
                            .sendMessage(msg);

                    mSungListView.resetState();
                    mSungListView.invalidate();
//                    mSungListView.setSelection(position - 1);
                    LogAnalyzeManager.onEvent(BaseApplication.getInstance().getBaseContext(),
                            EventConst.ID_CLICK_SUNG_LIST_VIEW_DEL_SONG);
                } else if (itemState == UserCenterSungListView.ITEM_STATE_NORMAL) {
                    SongOperationManager.getInstance().orderSong(
                            item.getSongId(), UserCenterSimpleView.this);
                    LogAnalyzeManager.onEvent(BaseApplication.getInstance().getBaseContext(),
                            EventConst.ID_CLICK_SUNG_LIST_VIEW_ADD_SONG);

                } else if (itemState == UserCenterSungListView.ITEM_STATE_TOP) {
                    SongOperationManager.getInstance().topSong(
                            item.getSongId(), UserCenterSimpleView.this);
                    mSungListView.resetState();
                    mSungListView.invalidate();
                    LogAnalyzeManager.onEvent(BaseApplication.getInstance().getBaseContext(),
                            EventConst.ID_CLICK_SUNG_LIST_VIEW_TOP_SONG);
                } else {
                    mSungListView.resetState();
                    mSungListView.invalidate();
                }
            }
        });

        mSungListView
                .setOnSongListKeyDownEventListener(new OnSongListKeyDownEventListener() {

                    @Override
                    public void onRightEdgeKeyDown() {
                    }

                    @Override
                    public void onLeftEdgeKeyDown() {
                        if (mGridView != null) {
                            mGridView.requestFocus();
                        }
                    }

                    @Override
                    public void onDownEdgeKeyDown() {
                        MainViewManager.getInstance().setSmallMvFocus();
                    }

                    @Override
                    public void onUpEdgeKeyDown() {

                    }
                });
    }

//    private void delSungListItem(final int sungItemId, final String auidoPath,
//                                 final String shareCode) {
//        Message msg = new Message();
//        msg.what = ListHandler.SUNGLIST_DEL_ITEM;
//        msg.obj = shareCode;
//        PlayListDAOManager.getInstance().getHandler()
//                .sendMessage(Message.obtain(msg));
//    }

    private void initFavoriteView() {
        mFavoriteEmptyIv = (TextView) findViewById(R.id.favorite_empty_hint_tv);
        mFavoriteTitle = (TextView) findViewById(R.id.favorite_title);
        mFavoriteListView = (FavoriteListView) findViewById(R.id.favorite_list_view);
//        mFavoriteListView.setEmptyView(mFavoriteEmptyIv);

        mFavoriteDatas = (ArrayList<Integer>) FavoriteListManager.getInstance()
                .getList();
        updateTitle(mFavoriteDatas.size());

        mFavoriteAdapter = new FavoriteListAdapter(mActivity,
                mFavoriteListView, mFavoriteDatas);
        mFavoriteListView.setAdapter(mFavoriteAdapter);
        mFavoriteListView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EvLog.e("mFavoriteListView hasFocus:" + hasFocus);
                if (hasFocus) {
                    mFavoriteListView.setAdapter(mFavoriteAdapter);
                    mFavoriteListView.setSelection(0);
                    // onPageStart();
                }

                if (mFavoriteAdapter != null) {
                    mFavoriteAdapter.refreshSelectedState(hasFocus,
                            mFavoriteListView.getSelectedItemPosition());
                }
            }
        });
        mFavoriteListView
                .setOnSongListKeyDownEventListener(new OnSongListKeyDownEventListener() {
                    @Override
                    public void onRightEdgeKeyDown() {
                    }

                    @Override
                    public void onLeftEdgeKeyDown() {
                        if (mGridView != null) {
                            mGridView.requestFocus();
                        }
                    }

                    @Override
                    public void onDownEdgeKeyDown() {
                        EvLog.i("mFavoriteListView onDownEdgeKeyDown");
                        MainViewManager.getInstance().setSmallMvFocus();
                    }

                    @Override
                    public void onUpEdgeKeyDown() {

                    }
                });
        mFavoriteListView.setOnItemClickCallback(new OnItemClickCallback() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id, int itemState) {
                Integer item = (Integer) parent.getAdapter().getItem(position);
                if (item == null) {
                    return;
                }
                mFavoriteListView.setmClickItemPos(position);
                if (itemState == FavoriteListView.ITEM_STATE_DELETE) {
                    if (FavoriteListManager.getInstance().delSong(item)) {
                        mFavoriteListView.resetState();
                        mFavoriteListView.invalidate();
                        LogAnalyzeManager
                                .onEvent(
                                        BaseApplication.getInstance().getBaseContext(),
                                        EventConst.ID_CLICK_FAVORITE_LIST_VIEW_CANCEL_FAVORITE);
                    }
                } else if (itemState == FavoriteListView.ITEM_STATE_TOP) {
                    SongOperationManager.getInstance().topSong(item,
                            UserCenterSimpleView.this);
                    mFavoriteListView.resetState();
                    mFavoriteListView.invalidate();
                    LogAnalyzeManager.onEvent(BaseApplication.getInstance().getBaseContext(),
                            EventConst.ID_CLICK_FAVORITE_LIST_VIEW_TOP_SONG);
                } else if (itemState == FavoriteListView.ITEM_STATE_NORMAL) {
                    SongOperationManager.getInstance().orderSong(item,
                            UserCenterSimpleView.this);
                    LogAnalyzeManager.onEvent(BaseApplication.getInstance().getBaseContext(),
                            EventConst.ID_CLICK_FAVORITE_LIST_VIEW_ADD_SONG);
                }
            }
        });

        mLoadingFavorite = (LoadingAndRetryWidget) findViewById(R.id.cloud_favorite_loading_widget);
    }

    /*
     * private void orderSongDelayed(final Song song, final boolean top) {
     * postDelayed(new Runnable() {
     *
     * @Override public void run() { PlayListManager.getInstance().addSong(null,
     * song.getId(), top); } }, KmConfig.ORDER_SONG_DELAY_DURATION); }
     */

    private void updateSelectedTitle(int count) {
        String mSelectedTitleFormat = getResources().getString(
                R.string.selected_list_title, count);
        mSelectedTitle.setText(Html.fromHtml(mSelectedTitleFormat));
    }

    private void updateTitle(int count) {
        String mFavoriteTitleFormat = getResources().getString(
                R.string.favorite_list_title, count);
        mFavoriteTitle.setText(Html.fromHtml(mFavoriteTitleFormat));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getViewId() {
        return MainViewId.ID_USER_SPACE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetFocus() {
    }

    public boolean resumeFocus() {
        if (mGridView != null) {
            mGridView.requestFocus();
            return true;
        }
        return false;
    }

    private void initSelectedData() {
        if (mSelectedDatas == null) {
            mSelectedDatas = new ArrayList<KmPlayListItem>();
        }

        mSelectedDatas.clear();
        List<KmPlayListItem> datas = PlayListManager.getInstance().getList();
        if (datas != null) {
            mSelectedDatas.addAll(PlayListManager.getInstance().getList());
        }

        if (mSelectedDatas.size() > 0) {
            mSelectedEmptyHint.setVisibility(View.GONE);
            mSelectedListView.setVisibility(View.VISIBLE);
            mSelectedAdapter.notifyDataSetChanged();
            mGridView.setNextFocusRightId(mSelectedListView.getId());
        } else {
            mSelectedEmptyHint.setVisibility(View.VISIBLE);
            mSelectedListView.setVisibility(View.GONE);
            mGridView.requestFocus();
        }
       /* if (mSelectedDatas.size() > 0) {
            mGridView.setNextFocusRightId(mSelectedListView.getId());
        } else {
            mGridView.setNextFocusRightId(-1);
        }*/

    }

    private void initSungData() {
        mSungDatas.clear();
        ArrayList<SungListItem> tempData = (ArrayList<SungListItem>) SungListManager
                .getInstance().getData();
        if (tempData != null) {
            mSungDatas.addAll(tempData);
            Collections.reverse(mSungDatas);
        }

        if (mSungDatas.size() > 0) {
            mSungListView.setVisibility(View.VISIBLE);
            mSungEmptyTv.setVisibility(View.GONE);
            mSungAdapter.refreshOrderedState();
            mSungAdapter.notifyDataSetChanged();
        } else {
            mSungListView.setVisibility(View.GONE);
            mSungEmptyTv.setVisibility(View.VISIBLE);
            mGridView.requestFocus();
        }
    }


    private void updateLoginRect() {
        if (!DeviceConfigManager.getInstance().isSupportUserLogin()) {
            return;
        }
        if (mUserInfoView == null) {
            mUserInfoView = new UserInfoWidget(mActivity);
        }
        mLoginRect.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mLoginRect.removeAllViews();
        mLoginRect.addView(mUserInfoView, params);
        mUserInfoView.updateUserInfo();
        if (!mGridView.isFocused()) {
            mGridView.requestFocus();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        MainViewManager.getInstance().addMVSwitchListener(this);
        MainViewManager.getInstance().getStatusBar().hideWithoutLogo();

        PlayListManager.getInstance().registerListener(this);
        SungListManager.getInstance().registerListener(this);
        FavoriteListManager.getInstance().registerListener(this);
        FavoriteListManager.getInstance().setSyncFavoriteCloudListListener(this);

        ChargeViewManager.getInstance().addListener(this);

        if (DeviceConfigManager.getInstance().isSupportUserLogin()) {
        } else {
            mSelectedRect.setVisibility(View.VISIBLE);
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mGridView.requestFocus();
                mGridView.setSelection(0);
                if (!DeviceConfigManager.getInstance().isSupportUserLogin()) {
                    KmPlayListItem song = (KmPlayListItem) mSelectedAdapter.getItem(mSelectedListView
                            .getSelectedItemPosition());
                    if (song == null) {
                        return;
                    }
                    if (FavoriteListManager.getInstance().isAlreadyExists(
                            song.getSongId())) {
                        mSelectedListView.highlightFavoriteIcon();
                    } else {
                        mSelectedListView.restoreFavoriteIcon();
                    }
                } else {
                    updateLoginRect();
                }
            }
        }, 10);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        MainViewManager.getInstance().removeMVSwitchListener(this);
        MainViewManager.getInstance().getStatusBar().showAll();

        mTabAdapter.emptyCheckedView();
        mGridView.setSelection(-1);
        PlayListManager.getInstance().unregisterListener(this);
        SungListManager.getInstance().unregisterListener(this);
        FavoriteListManager.getInstance().unregisterListener(this);
        FavoriteListManager.getInstance().setSyncFavoriteCloudListListener(null);
        ChargeViewManager.getInstance().removeListener(this);

        if (DeviceConfigManager.getInstance().isSupportUserLogin()) {
            if (mSelectedRect.getVisibility() != View.GONE) {
                mSelectedRect.setVisibility(View.GONE);
            }
            if (mLoginRect.getVisibility() != View.VISIBLE) {
                mLoginRect.setVisibility(View.VISIBLE);
            }
            if (mLoadingFavorite != null) {
                mLoadingFavorite.setRetryCallback(null);
            }
        } else {
            if (mSelectedRect.getVisibility() != View.VISIBLE) {
                mSelectedRect.setVisibility(View.VISIBLE);
            }
        }

        mSelectedListView.resetState();
        mSungListView.resetState();
        mFavoriteListView.resetState();
        if (mSungRect.getVisibility() != View.GONE) {
            mSungRect.setVisibility(View.GONE);
        }
        if (mSelectedRect.getVisibility() != View.GONE) {
            mSelectedRect.setVisibility(View.GONE);
        }


    }

    @Override
    public void onPlayListChange() {
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                if (mSelectedAdapter != null
                        && mSelectedRect.getVisibility() == View.VISIBLE) {
                    initSelectedData();
                    updateSelectedTitle(mSelectedDatas.size());
                    mSelectedAdapter.notifyDataSetChanged();
                    if (mSelectedDatas.size() == 0) {
                        mGridView.requestFocus();
                    }
                }
                if (mSungAdapter != null
                        && mSungRect.getVisibility() == View.VISIBLE) {
                    mSungAdapter.notifyDataSetChanged();
                }
                if (mFavoriteAdapter != null
                        && mFavoriteRect.getVisibility() == View.VISIBLE) {
                    mFavoriteAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFavoriteListChange() {
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                if (mSelectedListView != null && mSelectedAdapter != null
                        && mSelectedListView.getVisibility() == View.VISIBLE) {
                    KmPlayListItem song = (KmPlayListItem) mSelectedAdapter
                            .getItem(mSelectedListView
                                    .getSelectedItemPosition());
                    if (song != null) {
                        if (FavoriteListManager.getInstance().isAlreadyExists(
                                song.getSongId())) {
                            mSelectedListView.highlightFavoriteIcon();
                        } else {
                            mSelectedListView.restoreFavoriteIcon();
                        }
                    }
                }
                showFavoriteDatas();
                /*if (mFavoriteDatas != null) {
                    mFavoriteDatas.clear();
                    mFavoriteDatas.addAll(FavoriteListManager.getInstance()
                            .getList());
                    updateTitle(mFavoriteDatas.size());
                }
                if (mFavoriteAdapter != null
                        && mFavoriteListView.getVisibility() == View.VISIBLE) {
                    mFavoriteAdapter.notifyDataSetChanged();
                    if (mFavoriteDatas.size() == 0) {
                        mGridView.requestFocus();
                    }
                }*/
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.main_view_my_space_gv) {
            // EvLog.d("mGridView onFocusChange hasFocus=" + hasFocus);
            if (hasFocus) {
                View view = mGridView.getSelectedView();
                if (view != null) {
                    mTabAdapter.setCheckedView(view);
                }
            } else {
                View view = mTabAdapter.getView(
                        mGridView.getSelectedItemPosition(), null, mGridView);
                EvLog.i("mGridView", "Focus Lose,getSelectedItemPosition= "
                        + mGridView.getSelectedItemPosition() + ",view=" + view);
                if (view != null) {
                    mTabAdapter.setViewSelectedButNotFocus();
                }
            }
        }
    }

    private int mTabId = 0;

    private void showSubViewByTabId(int tabId) {
//        EvLog.e("showSubViewByTabId :" + tabId);
        if (tabId == TAB_ID_USER) {
            if (mLoadingFavorite.getVisibility() != View.GONE) {
                mLoadingFavorite.setVisibility(View.GONE);
            }
            if (mLoginRect.getVisibility() != View.VISIBLE) {
                mLoginRect.setVisibility(View.VISIBLE);
            }
            updateLoginRect();

            if (mSungRect.getVisibility() == View.VISIBLE) {
                mSungRect.setVisibility(View.GONE);
            }

            if (mFavoriteRect.getVisibility() == View.VISIBLE) {
                mFavoriteRect.setVisibility(View.GONE);
            }

            if (mSelectedRect.getVisibility() == View.VISIBLE) {
                mSelectedRect.setVisibility(View.GONE);
            }
        }
        if (tabId == TAB_ID_SUNG_RECORD) {
            if (mLoadingFavorite.getVisibility() != View.GONE) {
                mLoadingFavorite.setVisibility(View.GONE);
            }
            initSungData();


            if (mLoginRect.getVisibility() == View.VISIBLE) {
                mLoginRect.setVisibility(View.GONE);
            }
            if (mSelectedRect.getVisibility() == View.VISIBLE) {
                mSelectedRect.setVisibility(View.GONE);
            }
            if (mFavoriteRect.getVisibility() == View.VISIBLE) {
                mFavoriteRect.setVisibility(View.GONE);
            }
            if (mSungRect.getVisibility() != View.VISIBLE) {
                mSungRect.setVisibility(View.VISIBLE);
            }
            mGridView.setNextFocusRightId(mSungListView.getId());
        } else if (tabId == TAB_ID_MY_FAVORITE) {
            if (mFavoriteDatas == null || mFavoriteAdapter == null) {
                return;
            }

            // mFavoriteListView.setVisibility(View.GONE);
            if (mSungRect.getVisibility() == View.VISIBLE) {
                mSungRect.setVisibility(View.GONE);
            }
            if (mSelectedRect.getVisibility() == View.VISIBLE) {
                mSelectedRect.setVisibility(View.GONE);
            }

            if (mLoginRect.getVisibility() == View.VISIBLE) {
                mLoginRect.setVisibility(View.GONE);
            }

            int state = FavoriteListManager.getInstance().getSyncCloudFavoriteState();
            if (state != FavoriteListManager.SYNC_STATE_SUCCESS) {
                if (mLoadingFavorite.getVisibility() != View.VISIBLE) {
                    mLoadingFavorite.setVisibility(View.VISIBLE);
                }
                if (state == FavoriteListManager.SYNC_STATE_FAILED) {
                    showRetryGetFavoriteDatas();
                } else {
                    mLoadingFavorite.showLoading();
                }
                return;
            }
            if (mFavoriteRect.getVisibility() != View.VISIBLE) {
                mFavoriteRect.setVisibility(View.VISIBLE);
            }
            showFavoriteDatas();
            return;
        } else if (tabId == TAB_ID_SELECTED) {
            if (mLoadingFavorite.getVisibility() != View.GONE) {
                mLoadingFavorite.setVisibility(View.GONE);
            }

            initSelectedData();
            if (mSungRect.getVisibility() == View.VISIBLE) {
                mSungRect.setVisibility(View.GONE);
            }
            if (mLoginRect.getVisibility() == View.VISIBLE) {
                mLoginRect.setVisibility(View.GONE);
            }
            if (mFavoriteRect.getVisibility() == View.VISIBLE) {
                mFavoriteRect.setVisibility(View.GONE);
            }

            if (mSelectedRect.getVisibility() != View.VISIBLE) {
                mSelectedRect.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showRetryGetFavoriteDatas() {
        if (mLoadingFavorite.getVisibility() != View.VISIBLE) {
            mLoadingFavorite.setVisibility(View.VISIBLE);
        }
        mLoadingFavorite.showRetry();
        mGridView.setNextFocusRightId(mLoadingFavorite.getRetryBtnId());
        mLoadingFavorite.setRetryCallback(this);
    }

    private void showFavoriteDatas() {
        mFavoriteDatas.clear();
        mFavoriteDatas.addAll(FavoriteListManager.getInstance().getList());
        updateTitle(mFavoriteDatas.size());
        EvLog.i("showFavoriteDatas " + mFavoriteDatas.size());

        if (mFavoriteDatas.size() == 0) {
            mFavoriteListView.setVisibility(View.GONE);
            mFavoriteEmptyIv.setVisibility(View.VISIBLE);
            mGridView.requestFocus();
            return;
        }
        mFavoriteEmptyIv.setVisibility(View.GONE);
        mFavoriteListView.setVisibility(View.VISIBLE);
        mFavoriteAdapter.refreshOrderedState();
        mFavoriteAdapter.notifyDataSetChanged();
        mFavoriteListView.setFocusable(true);
        return;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
//        EvLog.d("onItemselected " + position);
        if (!mGridView.isFocused()) {
            return;
        }
        operateLeftTabItemSelect(parent, view, position, id);
    }

    private void operateLeftTabItemSelect(AdapterView<?> parent, View view, int position,
                                          long id) {

        UserCenterTabItem tab = (UserCenterTabItem) parent.getAdapter()
                .getItem(position);

        if (tab == null) {
            return;
        }
        mTabAdapter.setCheckedView(view);
        mTabId = tab.tabId;
        showSubViewByTabId(tab.tabId);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPlayHistoryChanged() {
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                EvLog.e("onPlayHistoryChanged----");
                initSungData();
               /* if (mSungDatas.size() > 0) {
                    mSungListView.setVisibility(View.VISIBLE);
                    mSungEmptyTv.setVisibility(View.GONE);
                    mSungAdapter.refreshOrderedState();
                    mSungAdapter.notifyDataSetChanged();
                } else {
                   mSungListView.setVisibility(View.GONE);
                   mSungEmptyTv.setVisibility(View.VISIBLE);
                }
                if (mSungDatas.size() == 0) {
                    mGridView.requestFocus();
                }*/
            }
        });
    }

    @Override
    public void onOrderSongSuccess(int songId) {
        if (DeviceConfigManager.getInstance().isOrderNeedAnimation()) {
            if (mSungListView != null && mSungListView.isFocused()) {
                mSungListView.startOrderSongAnimDelayed();
                return;
            }
            if (mFavoriteListView != null && mFavoriteListView.isFocused()) {
                mFavoriteListView.startOrderSongAnimDelayed();
                return;
            }
        }
        UmengAgent.onEvent(mActivity, EventConst.ID_CLICK_ORDER_SONG_VIEW_SONG);
    }

    @Override
    public void onTopSongSuccess(int songId) {
        if (DeviceConfigManager.getInstance().isOrderNeedAnimation()) {
            if (mSungListView != null && mSungListView.isFocused()) {
                mSungListView.startOrderSongAnimDelayed();
                return;
            }
            if (mFavoriteListView != null && mFavoriteListView.isFocused()) {
                mFavoriteListView.startOrderSongAnimDelayed();
                return;
            }
        }
        UmengAgent.onEvent(mActivity,
                EventConst.ID_CLICK_ORDER_SONG_VIEW_TOP_SONG);
    }

    @Override
    public void onTopSongFailed(int songId) {
    }

    @Override
    public void onOrderSongFailed(int songId) {
    }

    @Override
    public void onChargeSuccess() {
        if (!DeviceConfigManager.getInstance().isSupportUserLogin()) {
            return;
        }
        if (mUserInfoView != null) {
            mUserInfoView.updateVIPRemainTime(DeviceConfigManager.getInstance().getRemainVipTime());
        }
    }

    @Override
    public void onChargeFailed() {

    }

    @Override
    public void onChargeCancel() {

    }

    @Override
    public void onSyncFavoriteCloudListSuccess() {
        EvLog.i(">>>>onSyncFavoriteCloudListSuccess");
       /* if (mTabId != TAB_ID_MY_FAVORITE) {
            return;
        }*/
        if (mLoadingFavorite.getVisibility() == View.VISIBLE) {
            mLoadingFavorite.setVisibility(View.GONE);
            mFavoriteRect.setVisibility(View.VISIBLE);
            showFavoriteDatas();
        }
    }

    @Override
    public void onSyncFavoriteCloudListFailed() {
        //FIXME
        EvLog.i(">>>>onSyncFavoriteCloudListFailed" + mTabId);
        /*if (mTabId != TAB_ID_MY_FAVORITE) {
            return;
        }*/
        if (mLoadingFavorite.getVisibility() == View.VISIBLE) {
            showRetryGetFavoriteDatas();
        }
    }

    @Override
    public void onSyncFavoriteCloudListStart() {
        EvLog.i(">>>>onSyncFavoriteCloudListStart");
      
       /* if (mTabId != TAB_ID_MY_FAVORITE) {
            return;
        }*/
        if (mFavoriteRect.getVisibility() != View.VISIBLE) {
            return;
        }

        if (mFavoriteRect.getVisibility() != View.GONE) {
            mFavoriteRect.setVisibility(View.GONE);
        }
        if (mLoadingFavorite.getVisibility() != View.VISIBLE) {
            mLoadingFavorite.setVisibility(View.VISIBLE);
        }
        mLoadingFavorite.showLoading();
    }

    @Override
    public void onClickRetryBtn() {
        FavoriteListManager.getInstance().startSyncCloudFavoriateListTask();
    }

    @Override
    protected void clickExitKey() {
        if (mSelectedDatas != null) {
            mSelectedDatas.clear();
        }
        if (mSungDatas != null) {
            mSungDatas.clear();
        }
        if (mFavoriteDatas != null) {
            mFavoriteDatas.clear();
        }
    }

    @Override
    public boolean onSmallMVUpKey() {
        mGridView.requestFocus();
        return true;
    }

    @Override
    public boolean onSmallMVRightKey() {
        if (mSelectedRect.getVisibility() == View.VISIBLE) {
//                mNeedResetListViewWhenFocus = false;
            mSelectedListView.requestFocus();
            return true;
        }
        if (mSungRect.getVisibility() == View.VISIBLE) {
//                mNeedResetListViewWhenFocus = false;
            mSungListView.requestFocus();
            return true;
        }
        if (mFavoriteRect != null && mFavoriteRect.getVisibility() == View.VISIBLE) {
            if (mFavoriteListView.getVisibility() == View.VISIBLE) {
                mFavoriteListView.requestFocus();
            }
        }
        return false;
    }

    @Override
    public void onSwitchToMV() {

    }

    @Override
    public void onSwitchToMainView() {
        MainViewManager.getInstance().getStatusBar().hideWithoutLogo();
    }

    @Override
    public boolean onStatusBarDownKey() {
        return false;
    }

    private void log(String tag) {
        Log.d("gsp", TAG + ">>>" + tag);
    }
}
