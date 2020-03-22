/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年10月7日     "dinghaiqiang"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.selected;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
import com.evideo.kmbox.model.playerctrl.list.FavoriteListManager;
import com.evideo.kmbox.model.playerctrl.list.FavoriteListManager.IFavoriteListListener;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager.IPlayListListener;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.model.umeng.EventConst;
import com.evideo.kmbox.model.umeng.PageName;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.BasePresenter;
import com.evideo.kmbox.util.BitmapUtil;
import com.evideo.kmbox.util.CommonUtil;
import com.evideo.kmbox.widget.common.OnItemClickCallback;
import com.evideo.kmbox.widget.common.OnSongListKeyDownEventListener;
import com.evideo.kmbox.widget.mainview.AbsBaseView;
import com.evideo.kmbox.widget.mainview.MainViewId;
import com.evideo.kmbox.widget.mainview.MainViewManager;
import com.evideo.kmbox.widget.mainview.MainViewManager.IMVSwitchListener;

/**
 * [功能说明]
 */
public class SelectedView extends AbsBaseView implements IPlayListListener, 
IFavoriteListListener,IMVSwitchListener ,OnSongListKeyDownEventListener{
    
    private TextView mTitle;
    private SelectedListAdapter mAdapter;
    private SelectedListView mListView;
    private ImageView mEmptyHint;
    private ArrayList<KmPlayListItem> mDatas = null;
    private Bitmap mEmptyBmp = null;
    /**
     * @param activity
     * @param backViewId
     */
    public SelectedView(Activity activity, int backViewId) {
        super(activity, backViewId);
        init();
    }
    
    private void init() {
    	mEmptyHint = (ImageView) findViewById(R.id.selected_empty_hint_iv);
        Bitmap bmp = BitmapUtil.getBitmapByResId(BaseApplication.getInstance(), R.drawable.selected_empty_hint);
        mEmptyHint.setImageBitmap(bmp);
        
        mTitle = (TextView) findViewById(R.id.selected_title);
        mListView = (SelectedListView) findViewById(R.id.selected_list_view);
        mDatas = (ArrayList<KmPlayListItem>) PlayListManager.getInstance().getList();
        mAdapter = new SelectedListAdapter(mActivity, mListView, mDatas);
        updateTitle(mDatas.size());
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(mEmptyHint);
        mListView.setOnItemClickCallback(new OnItemClickCallback() {
        
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                long id, int itemState) {
                KmPlayListItem item = PlayListManager.getInstance().getItemByPos(position);
                if (itemState == SelectedListView.ITEM_STATE_DELETE) {
                    PlayListManager.getInstance().delSong(item.getSerialNum());
                    LogAnalyzeManager.onEvent(getContext(), EventConst.ID_CLICK_SELECTED_LIST_VIEW_DELETE_SONG);
                } else if (itemState == SelectedListView.ITEM_STATE_TOP) {    
                    PlayListManager.getInstance().addSong(item.getCustomerid(),  item.getSongId(), true);
                    LogAnalyzeManager.onEvent(getContext(), EventConst.ID_CLICK_SELECTED_LIST_VIEW_TOP_SONG);
                } else if (itemState == SelectedListView.ITEM_STATE_CUT_SONG) {
                    MainViewManager.getInstance().cutSong();
                    LogAnalyzeManager.onEvent(getContext(), EventConst.ID_CLICK_SELECTED_LIST_VIEW_CUT_SONG);
                } else if (itemState == SelectedListView.ITEM_STATE_FAVORITE) {
                    
                    if (FavoriteListManager.getInstance().isAlreadyExists(item.getSongId())) {
                        if (FavoriteListManager.getInstance().delSong(item.getSongId())) {
                            UmengAgentUtil.onEventFavoriteAction(BaseApplication.getInstance().getBaseContext(),
                                    EventConst.ID_CLICK_SELECTED_LIST_VIEW_FAVORITE, false);
                        }   
                        return;
                    } else if (FavoriteListManager.getInstance().addSong(item.getSongId())) {
                        UmengAgentUtil.onEventFavoriteAction(BaseApplication.getInstance().getBaseContext(),
                                EventConst.ID_CLICK_SELECTED_LIST_VIEW_FAVORITE, true);
                    }              
                }
            }
        });
        
        mListView.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    LogAnalyzeManager.getInstance().onPageStart(PageName.ORDERED_LIST);
                    onPageStart();
                }
                if (mAdapter != null) {
                    mAdapter.refreshSelectedState(hasFocus, mListView.getSelectedItemPosition());
                }
            }
        });
        
        mListView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                mListView.restoreFavoriteIcon();
                
                KmPlayListItem item = (KmPlayListItem) parent.getAdapter().getItem(position);
                
                if (item == null) {
                    return;
                }
                
                if (FavoriteListManager.getInstance().isAlreadyExists(item.getSongId())) {
                    mListView.highlightFavoriteIcon();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mListView.setOnSongListKeyDownEventListener(this);
    }
    
    /**
     * [获取已点数量]
     * @return 歌曲数量
     */
    public int getCount() {
        return mAdapter.getCount();
    }

    private void updateTitle(int count) {
        String mTitleFormat = getResources().getString(R.string.ordered_list_title, count);          
        mTitle.setText(Html.fromHtml(mTitleFormat));
    }

    @Override
    protected int getLayResId() {
        return R.layout.main_selected;
    }

    @Override
    protected int getViewId() {
        return MainViewId.ID_SELECTED;
    }

    @Override
    protected void resetFocus() {
        if (mAdapter.getCount() > 0 && mListView != null) {
            mListView.requestFocus();
        } 
        
        if (mListView == null) {
            return;
        }
        KmPlayListItem song = (KmPlayListItem) mAdapter.getItem(mListView.getSelectedItemPosition());
        if (song == null) {
            return;
        }
        if (FavoriteListManager.getInstance().isAlreadyExists(song.getSongId())) {
            mListView.highlightFavoriteIcon();
        } else {
            mListView.restoreFavoriteIcon();
        }
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        MainViewManager.getInstance().addMVSwitchListener(this);
        MainViewManager.getInstance().getStatusBar().hideWithoutLogo();
        FavoriteListManager.getInstance().registerListener(this);
        PlayListManager.getInstance().registerListener(this);
       
    }

    @Override
    protected void onDetachedFromWindow() {
        MainViewManager.getInstance().removeMVSwitchListener(this);
        FavoriteListManager.getInstance().unregisterListener(this);
        PlayListManager.getInstance().unregisterListener(this);
        MainViewManager.getInstance().getStatusBar().showAll();
        super.onDetachedFromWindow();
    }

    @Override
    public void onPlayListChange() {
        if (this.getVisibility() != View.VISIBLE) {
            return;
        }
        
        if (mDatas == null) {
            mDatas = new ArrayList<KmPlayListItem>();
        }
        
        mDatas.clear();
        List<KmPlayListItem> datas = PlayListManager.getInstance().getList();
        if (datas != null) {
            mDatas.addAll(PlayListManager.getInstance().getList());
        }
        
        updateTitle(mDatas.size());

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        } 
        
        if (mListView == null) {
            return;
        }

        KmPlayListItem song =  (KmPlayListItem) mAdapter.getItem(mListView.getSelectedItemPosition());
        if (song == null) {
            return;
        }
        if (FavoriteListManager.getInstance().isAlreadyExists(song.getSongId())) {
            mListView.highlightFavoriteIcon();
        } else {
            mListView.restoreFavoriteIcon();
        }
    }

    /**
     * [重置selector到第一项item的收藏icon]
     */
    public void resetSelectorPosition() {
        if (mAdapter.getCount() > 0 && mListView != null) {
            mListView.requestFocus();
            mListView.resetItemState();
        }
//        mListView.setSelection(0);
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
                KmPlayListItem song =  (KmPlayListItem) mAdapter.getItem(mListView.getSelectedItemPosition());
                if (song == null) {
                    return;
                }
                if (FavoriteListManager.getInstance().isAlreadyExists(song.getSongId())) {
                    mListView.highlightFavoriteIcon();
                } else {
                    mListView.restoreFavoriteIcon();
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
    public void onSwitchToMV() {
        
    }

    @Override
    public void onSwitchToMainView() {
        MainViewManager.getInstance().getStatusBar().hideWithoutLogo();
    }

    @Override
    public void onRightEdgeKeyDown() {
        
    }

    @Override
    public void onLeftEdgeKeyDown() {
        
    }

    @Override
    public void onDownEdgeKeyDown() {
        MainViewManager.getInstance().setSmallMvFocus();
    }

    @Override
    public void onUpEdgeKeyDown() {
        
    }

    @Override
    public boolean onStatusBarDownKey() {
        return false;
    }
}
