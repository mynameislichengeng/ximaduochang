/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年9月20日     "zhanxingshan"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.songtop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.exception.DCNoResultException;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.playerctrl.list.FavoriteListManager;
import com.evideo.kmbox.model.playerctrl.list.FavoriteListManager.IFavoriteListListener;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager.IPlayListListener;
import com.evideo.kmbox.model.playerctrl.list.SongOperationManager;
import com.evideo.kmbox.model.playerctrl.list.SongOperationManager.IOrderSongResultListener;
import com.evideo.kmbox.model.playerctrl.list.SongOperationManager.ITopSongResultListener;
import com.evideo.kmbox.model.songtop.SongTop;
import com.evideo.kmbox.model.songtop.SongTopDetail;
import com.evideo.kmbox.model.songtop.SongTopDetailManager;
import com.evideo.kmbox.model.songtop.SongTopManager;
import com.evideo.kmbox.model.songtop.SongTopManager.ISongTopListListener;
import com.evideo.kmbox.model.umeng.EventConst;
import com.evideo.kmbox.model.umeng.UmengAgent;
import com.evideo.kmbox.presenter.BasePresenter;
import com.evideo.kmbox.presenter.PageLoadPresenter.ILoadCacheDataCallback;
import com.evideo.kmbox.presenter.PageLoadPresenter.IPageLoadCallback;
import com.evideo.kmbox.util.BitmapUtil;
import com.evideo.kmbox.util.DimensionsUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.AnimLoadingView;
import com.evideo.kmbox.widget.common.LoadingAndRetryWidget;
import com.evideo.kmbox.widget.common.OnItemClickCallback;
import com.evideo.kmbox.widget.common.OnSongListKeyDownEventListener;
import com.evideo.kmbox.widget.common.SongListView;
import com.evideo.kmbox.widget.mainview.AbsBaseView;
import com.evideo.kmbox.widget.mainview.BreadCrumbsWidget;
import com.evideo.kmbox.widget.mainview.MainViewId;
import com.evideo.kmbox.widget.mainview.MainViewManager;

/**
 * [排行界面]
 */
public class SongTopView extends AbsBaseView 
    implements IPageLoadCallback<SongTopDetail>, ILoadCacheDataCallback<SongTopDetail>,
    ISongTopListListener, IPlayListListener, IFavoriteListListener,IOrderSongResultListener,ITopSongResultListener {

    private static final int PAGE_LOAD_EDGE_COUNT = 8;
    private Activity mActivity;
  
    private int mCurrentSongTopId = -1;
    private int mTotalNum;

    private AnimLoadingView mGvLoading = null;
    private GridView mGridView;
    
    private AnimLoadingView mListLoading = null;
    private SongTopAdapter mSongTopAdapter;
    private SongListView mSongListView;
    private SongTopDetailAdapter mSongTopDetailAdapter;
    private ArrayList<SongTopDetail> mDatas = null;

    private SongTop mFirstSongTop;
    private int mFadingEdgeLength;
    private BreadCrumbsWidget mTitle = null;
    private int mDefaultFocusId = 0;
    private boolean mNeedResetListViewWhenFocus = true;
//    private ImageView mTopSloganIv = null;
            
    /**
     * @param activity
     * @param songMenu
     * @param backViewId
     */

    public SongTopView(Activity activity, int backViewId) {
        super(activity, backViewId);
        mActivity = activity;
        mDatas = new ArrayList<SongTopDetail>();
        initViewSongTop();
        initViewSongTopDetail();
    }
    
    private int mDefaultFocusSongTopId = 0;
    
    public void setDefaultFocusSongTopId(int focusRankId) {
        mDefaultFocusSongTopId = focusRankId;
    }
    
    public SongTopView(Activity activity, int defaultFocusId,int backViewId) {
        super(activity, backViewId);
        mActivity = activity;
        mDefaultFocusId = defaultFocusId;
        initViewSongTop();
        initViewSongTopDetail();
    }
    
    private void initViewSongTop() {
        FrameLayout rect = (FrameLayout) findViewById(R.id.song_top_details_item_lay);
        Bitmap bmp = BitmapUtil.getBitmapByResId(BaseApplication.getInstance(), R.drawable.main_view_top_song_left_lay_bg);
        rect.setBackgroundDrawable(new BitmapDrawable(bmp));
        
        mTitle = (BreadCrumbsWidget) findViewById(R.id.main_songname_title_crumb);
        mTitle.setFirstTitle(getString(R.string.main_top_song_title));
        
        mGvLoading = (AnimLoadingView) findViewById(R.id.song_top_gv_loading_widget);
        mFadingEdgeLength = getResources().getDimensionPixelSize(R.dimen.px98);
        mGridView = (GridView) findViewById(R.id.main_view_song_top_gv);
        mGridView.setVerticalFadingEdgeEnabled(true);
        mGridView.setFadingEdgeLength(mFadingEdgeLength);

        mSongTopAdapter = new SongTopAdapter(BaseApplication.getInstance().getBaseContext(), mGridView,
                (ArrayList<SongTop>) SongTopManager.getInstance().getSongTopList());
        mGridView.setAdapter(mSongTopAdapter);
        mSongTopAdapter.notifyDataSetChanged();
        mGridView.setOnKeyListener(new View.OnKeyListener() {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        mNeedResetListViewWhenFocus = true;
                    }
                }
                return false;
            }
        });
 
        mGridView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                if (!mGridView.isFocused()) {
                    return;
                }
//                EvLog.d("zxs", "mGridView.setOnItemClickListener Top run 1");
                SongTop songTop = (SongTop) parent.getAdapter().getItem(position);
                
                if (songTop == null) {
                    return;
                }
                mSongTopAdapter.setCheckedView(view);
                //zxs
                EvLog.d("zxs", "setOnItemClickListener songMenuID===>" + songTop.songTopId);
                SongTopManager.getInstance().notifySongTopClicked(songTop);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        
        mGridView.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    View view = mGridView.getSelectedView();
                    if (view != null) {
                        mSongTopAdapter.setCheckedView(view);
                    }
                } else {
                    View view = mSongTopAdapter.getView(mGridView.getSelectedItemPosition(), null, mGridView);
                    EvLog.i("SongTopList", "Focus Lose");
                    if (view != null) {
                        mSongTopAdapter.setViewSelectedButNotFocus();
                    }
                }
            }
        });
        mGridView.requestFocus();
        mGridView.setSelection(mDefaultFocusId);
        SongTopManager.getInstance().startGetSongTopListTask();
    }
    
    private void initViewSongTopDetail() {
//        ImageView topSloganIv = (ImageView) findViewById(R.id.top_slogan);
//        Bitmap bmp = BitmapUtil.getBitmapByResId(BaseApplication.getInstance(), R.drawable.top_slogan);
//        topSloganIv.setImageBitmap(bmp);
//        mDetailsLoadingView = findViewById(R.id.loading_lay);
//        mLoadingErrorTv = (TextView) findViewById(R.id.loading_error_tv);
        
        mListLoading = (AnimLoadingView)findViewById(R.id.song_top_list_loading_widget);
        mSongListView = (SongListView) findViewById(R.id.main_view_song_top_details_lv);

        mSongListView.setOnItemClickCallback(new OnItemClickCallback() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id, int itemState) {
                SongTopDetail item = (SongTopDetail) parent.getAdapter().getItem(position);
                if (item == null) {
                    return;
                }
              
                if (itemState == SongListView.ITEM_STATE_NORMAL) {
                    SongOperationManager.getInstance().orderSong(item.songId, SongTopView.this);
                   
                } else if (itemState == SongListView.ITEM_STATE_TOP) {
                    SongOperationManager.getInstance().topSong(item.songId, SongTopView.this);
                } else if (itemState == SongListView.ITEM_STATE_FAVORITE) {
                    if (FavoriteListManager.getInstance().isAlreadyExists(item.songId)) {
                        if (FavoriteListManager.getInstance().delSong(item.songId)) {
                            onUmengAgentFavoriteSong(false);
                        }                      
                        return;
                    } else if (FavoriteListManager.getInstance().addSong(item.songId)) {
//                      mListView.startFavoriteAnimnation(position);
                        onUmengAgentFavoriteSong(true);
                        return;
                    }
                }
            }
        });
        
        mSongListView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                
                SongTopDetail item = (SongTopDetail) parent.getAdapter().getItem(position);
                if (item == null) {
                    return;
                }

                if (FavoriteListManager.getInstance().isAlreadyExists(item.songId/*getId()*/)) {
                    mSongListView.highlightFavoriteIcon();
                } else {
                    mSongListView.restoreFavoriteIcon();
                }
                
                if (position <= (mSongTopDetailAdapter.getCount() - 1)
                        && position > (mSongTopDetailAdapter.getCount() - PAGE_LOAD_EDGE_COUNT)) {
                    SongTopDetailManager.getInstace().loadNextPage();
                    EvLog.i("wrq", "start loading next page");
                } 
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
        mSongListView.setOnFocusChangeListener(new OnFocusChangeListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (mNeedResetListViewWhenFocus) {
                        mSongListView.setAdapter(mSongTopDetailAdapter);
                        mSongListView.setSelection(0);
                        mNeedResetListViewWhenFocus = false;
                    } else {
                        EvLog.e("not need reset listview");
                    }
                } else {
                    mSongListView.resetState();
                }
                if (mSongTopDetailAdapter != null) {
                    mSongTopDetailAdapter.refreshSelectedState(hasFocus, mSongListView.getSelectedItemPosition());
                }
            }
            
        });
        
        mSongListView.setOnSongListKeyDownEventListener(new OnSongListKeyDownEventListener() {
            
            @Override
            public void onRightEdgeKeyDown() {
                if (MainViewManager.getInstance().getStatusBar() != null) {
                    MainViewManager.getInstance().getStatusBar().setSelectedNumFocus();
                }
            }
            
            @Override
            public void onLeftEdgeKeyDown() {
                if (mGridView != null) {
                    mGridView.requestFocus();
                    mSongListView.setSelection(0);
                }
            }
            @Override
            public void onDownEdgeKeyDown() {
                
            }

            @Override
            public void onUpEdgeKeyDown() {
                MainViewManager.getInstance().getStatusBar().requestFocus();
            }
        });

//        mDetailsLoadingView.setVisibility(View.GONE);
//        mLoadingErrorTv.setVisibility(View.GONE);
        mSongListView.setVisibility(View.GONE);
        mSongTopDetailAdapter = new SongTopDetailAdapter(mActivity, mSongListView, mDatas);
        mSongTopDetailAdapter.setSongNameSpecWidth(DimensionsUtil.getDimensionPixelSize(mActivity, R.dimen.px698));
        mSongListView.setAdapter(mSongTopDetailAdapter);
    }    
 
    private class SloganInfo {
        public String sloganPath;
        public Bitmap sloganBmp;
        public SloganInfo() {
            this.sloganBmp = null;
            this.sloganPath = "";
        }
    }
    
    private SloganInfo mShowSloganInfo = null;
    
    private void updateTopSlogan() {
        /*if (mTopSloganIv != null) {
            String localVersion =  KmSharedPreferences.getInstance().getString(KeyName.KEY_TOP_SLOGAN_VERSION, "1.0");
            String fileName = localVersion + ".png";
//            String fileFullPath = UpdateTopSlogan.TOP_SLOGAN_DIR + fileName;
            String fileFullPath = FileUtil.concatPath(ResourceSaverPathManager.getInstance().getTopSlogan(), fileName);
            if (FileUtil.isFileExist(fileFullPath)) {
                EvLog.d("update top slogan");
                Bitmap bmp = BitmapUtil.getBmpByPath(fileFullPath);
                if (bmp != null) {
                    mTopSloganIv.setImageBitmap(bmp);
                }
                if (mShowSloganInfo == null) {
                    mShowSloganInfo = new SloganInfo();
                } else {
                    if (mShowSloganInfo.sloganBmp != null) {
                        mShowSloganInfo.sloganBmp.recycle();
                        mShowSloganInfo.sloganBmp = null;
                    }
                }
                mShowSloganInfo.sloganPath = fileFullPath;
                mShowSloganInfo.sloganBmp = bmp;
            }
        }*/
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        SongTopManager.getInstance().registSongTopListListener(this);
        PlayListManager.getInstance().registerListener(this);
        FavoriteListManager.getInstance().registerListener(this);
        updateTopSlogan();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SongTopManager.getInstance().unregistSongTopListListener(this);
        PlayListManager.getInstance().unregisterListener(this);
        FavoriteListManager.getInstance().unregisterListener(this);
       
       /* if (mDatas != null) {
            mDatas.clear();
        }*/
    }
    
    private String getSongTopName() {
        SongTop songTop = SongTopManager.getInstance().getSongTopById(mCurrentSongTopId);
        if (songTop != null) {
            return songTop.name;
        }
        return null;
    }
    
    private void onUmengAgentOrderSong() {
        String name = getSongTopName();
        if (name == null) {
            return;
        }
        HashMap<String, String> m = new HashMap<String, String>();
        m.put(EventConst.K_SONG_MENU_NAME, name);
        UmengAgent.onEvent(mActivity, EventConst.ID_CLICK_SONG_MENU_DETAILS_ORDER_SONG, m);
    }
    
    private void onUmengAgentTopSong() {
        String name = getSongTopName();
        if (name == null) {
            return;
        }
        HashMap<String, String> m = new HashMap<String, String>();
        m.put(EventConst.K_SONG_MENU_NAME, name);
        UmengAgent.onEvent(mActivity, EventConst.ID_CLICK_SONG_MENU_DETAILS_TOP_SONG, m);
    }
    
    private void onUmengAgentFavoriteSong(boolean favorite) {
        String name = getSongTopName();
        if (name == null) {
            return;
        }
        HashMap<String, String> m = new HashMap<String, String>();
        m.put(EventConst.K_SONG_MENU_NAME, name);
        m.put(EventConst.K_FAVORITE_ACTION, favorite ? EventConst.V_FAVORITE : EventConst.V_CANCEL);
        UmengAgent.onEvent(mActivity, EventConst.ID_CLICK_SONG_MENU_DETAILS_FAVORITE, m);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getLayResId() {
        return R.layout.main_view_song_toptopdetail;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getViewId() {
        return MainViewId.ID_TOP;
    }

    public boolean resumeFocus() {
        if (mGridView != null) {
            mGridView.requestFocus();
            return true;
        }
        return false;
    }
    
   
   
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetFocus() {
        if (mCurrentSongTopId < 0) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mGridView.requestFocus();
                    mGridView.setSelection(0);
                }
            }, 50);
        } else {
          /*  View view = mSongTopAdapter.getView(mGridView.getSelectedItemPosition(), null, mGridView);
            EvLog.i("SongTopList", "Focus Lose");
            if (view != null) {
                mSongTopAdapter.setViewSelectedButNotFocus();
            }*/
            mSongListView.resetState();
            mSongListView.requestFocus();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onPreLoadData(boolean isReset, boolean isNext) {
        if (mDatas.size() == 0 || isReset) {
            showTopDetailsLoadingView();
        } else {
            mSongListView.showFootLoadingView();
        }
    }
    
    private void showSongTopLoadingView() {
        mGridView.setVisibility(View.GONE);
    }
    
    private void hideSongTopLoadingView() {
        mGvLoading.setVisibility(View.GONE);
        mGridView.setVisibility(View.VISIBLE);
    }
    
    private void showSongTopLoadErrorView() {
        mGridView.setVisibility(View.GONE);
       /* if (!NetUtils.isNetworkConnected(mContext)) {
            mTopLoadingErrorTv.setText(getString(R.string.error_network));
        }*/
    }
    
    private void showTopDetailsLoadingView() {
//        mDetailsLoadingView.setVisibility(View.VISIBLE);
//        mLoadingErrorTv.setVisibility(View.GONE);
        mListLoading.setVisibility(View.VISIBLE);
        mListLoading.startAnim();
        mSongListView.setVisibility(View.GONE);
    }
    
    private void updateCheckedState() {
        if (mGridView != null && mSongTopAdapter != null && mSongTopAdapter.getCount() > 0) {
            View view = mGridView.getSelectedView();
            if (view == null) {
                return;
            }
            mSongTopAdapter.setCheckedView(view);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPostLoadData(Exception e, boolean isReset, boolean isNext,
            List<SongTopDetail> datas) {
        if (e != null) {
            handleException(e, isReset);
        }
        if (e == null && mSongTopDetailAdapter != null && datas != null) {
            if (isReset) {
                mDatas.clear();
            }
            mDatas.addAll(datas);
            mSongTopDetailAdapter.notifyDataSetChanged();
            mTotalNum = SongTopManager.getInstance().getTotalNumBySongTopId(mCurrentSongTopId);
            EvLog.i("songTop: totalNum of " + mCurrentSongTopId + " is " + mTotalNum);
            showListView();
            if (isReset) {
                mSongListView.setSelection(0);
            }
        }        
    }
    
    private void handleException(Exception e, boolean isReset) {
        EvLog.e(e.getMessage());
        if (e instanceof NetworkErrorException) {
            if (isReset) {
                showLoadingErrorView(R.string.error_loading_song_network);
            } else {
                mSongListView.showFootView(R.string.error_list_foot_loading_song_network);
            }
        } else if (e instanceof DCNoResultException) {
            if (isReset) {
                showLoadingErrorView(R.string.error_loading_song_no_result);
            } else {
                mSongListView.showFootView(R.string.error_list_foot_loading_song);
            }
        } else {
            showLoadingErrorView(R.string.error_loading_song);
        }
    }

    private void showLoadingErrorView(int resid) {
//        mDetailsLoadingView.setVisibility(View.GONE);
/*        mLoadingErrorTv.setVisibility(View.VISIBLE);
        mLoadingErrorTv.setText(resid);*/
        mSongListView.setVisibility(View.GONE);
    }

    private void showListView() {
//        mDetailsLoadingView.setVisibility(View.GONE);
//        mLoadingErrorTv.setVisibility(View.GONE);
        mListLoading.stopAnim();
        mListLoading.setVisibility(View.GONE);
        mSongListView.setVisibility(View.VISIBLE);
        if (mDatas.size() >= SongTopManager.getInstance().getTotalNumBySongTopId(mCurrentSongTopId)) {
            mSongListView.showFootView(R.string.loading_song_no_more);
            mSongListView.setNextFocusDownId(R.id.small_mv_frame);
        } else {
            mSongListView.removeFootLoadingView();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPreLoadCacheData() {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPostLoadCacheData(Exception e, List<SongTopDetail> datas) {
        if (e != null) {
            handleException(e, true);
        } else if (mSongTopDetailAdapter != null && datas != null) {
            mDatas.clear();
            mDatas.addAll(datas);
            mSongTopDetailAdapter.notifyDataSetChanged();
            mTotalNum = SongTopManager.getInstance().getTotalNumBySongTopId(mCurrentSongTopId);
            showListView();
            mSongListView.setSelection(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSongTopSelected(SongTop songTop) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSongTopDataChanged() {
        if (mSongTopAdapter == null || mGridView == null) {
            return;
        }
        hideSongTopLoadingView();
        mSongTopAdapter.notifyDataSetChanged();

        mGridView.requestFocus();
        
        int position = 0;
        EvLog.i("mDefaultFocusSongTopId:" + mDefaultFocusSongTopId);
        if (mDefaultFocusSongTopId == 0) {
            position = mGridView.getSelectedItemPosition();
            if (position <= 0) {
                position = 0;
                mGridView.setSelection(0);
            }
            SongTop item = (SongTop) mSongTopAdapter.getItem(position);
            SongTopManager.getInstance().notifySongTopClicked(item);
        } else {
            position = SongTopManager.getInstance().getSongTopPosition(mDefaultFocusSongTopId);
            EvLog.i("position:" + position);
            mGridView.setSelection(position);
        }
        updateCheckedState();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void onSongTopClicked(SongTop songTop) {
        mFirstSongTop = songTop;
        mCurrentSongTopId = mFirstSongTop.songTopId;
        EvLog.d("top click " + mFirstSongTop.name + ",songTopId===>>" + mCurrentSongTopId);
        SongTopDetailManager.getInstace().startGetSongTopDetailsTask(mCurrentSongTopId, this, this);
  
        if (mSongTopDetailAdapter == null || mSongListView == null) {
            return;
        }
  
        if (mSongListView.getCount() <= 0) {
            return;
        }
        SongTopDetail item = (SongTopDetail) mSongTopDetailAdapter.getItem(mSongListView.getSelectedItemPosition());
        if (item == null) {
            return;
        }
        if (FavoriteListManager.getInstance().isAlreadyExists(item.songId/*getId()*/)) {
            mSongListView.highlightFavoriteIcon();
        } else {
            mSongListView.restoreFavoriteIcon();
        }
        mSongTopDetailAdapter.refreshOrderedState();
    }
    
    @Override
    public void onSongTopLoading() {
        EvLog.i("SONG TOP LIST LOADING");
        showSongTopLoadingView();
    }
    
    @Override
    public void onSongTopLoadFail() {
        showSongTopLoadErrorView();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPlayListChange() {
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                if (mSongTopDetailAdapter != null) {
                    mSongTopDetailAdapter.notifyDataSetChanged();
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
                if (mSongListView == null || mSongTopDetailAdapter == null) {
                    return;
                }
                SongTopDetail song = (SongTopDetail) mSongTopDetailAdapter.getItem(mSongListView.getSelectedItemPosition());
                if (song == null) {
                    return;
                }
                if (FavoriteListManager.getInstance().isAlreadyExists(song.songId/*getId()*/)) {
                    mSongListView.highlightFavoriteIcon();
                } else {
                    mSongListView.restoreFavoriteIcon();
                }
            }
        }); 
    }

    @Override
    public void onOrderSongSuccess(int songId) {
        EvLog.d(" recv onOrderSongSuccess");
        if (DeviceConfigManager.getInstance().isOrderNeedAnimation()) {
            if (mSongListView != null) {
                mSongListView.startOrderSongAnimDelayed();
            }
        }
        onUmengAgentOrderSong();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTopSongSuccess(int songId) {
        EvLog.d(" recv onTopSongSuccess");
        
        if (DeviceConfigManager.getInstance().isOrderNeedAnimation()) {
            if (mSongListView != null) {
                mSongListView.startOrderSongAnimDelayed();
            }
        }
        onUmengAgentTopSong();
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
        mDefaultFocusSongTopId = 0;
        mSongTopAdapter.emptyCheckedView();
        mGridView.setSelection(-1);
        mSongListView.resetState();
        if (mDatas != null) {
            mDatas.clear();
        }
    }

    
    @Override
    public boolean onSmallMVUpKey() {
        if (mGridView != null) {
            mGridView.requestFocus();
            return true;
        }
        return false;
    }

    
    @Override
    public boolean onSmallMVRightKey() {
        mNeedResetListViewWhenFocus = false;
        if (mSongListView != null && mSongListView.getVisibility() == View.VISIBLE) {
            mSongListView.requestFocus();
            return true;
        }
        return false;
    }

   
    @Override
    public boolean onStatusBarDownKey() {
        if (mSongListView != null && mSongListView.getVisibility() == View.VISIBLE) {
            mSongListView.requestFocus();
            return true;
        } else {
            if (mGridView != null) {
                mGridView.requestFocus();
                return true;
            }
        }
        return false;
    }
}
