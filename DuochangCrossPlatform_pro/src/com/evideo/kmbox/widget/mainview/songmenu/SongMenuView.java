/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年9月17日     "dinghaiqiang"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.songmenu;

import java.util.ArrayList;
import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
import com.evideo.kmbox.model.songmenu.SongMenu;
import com.evideo.kmbox.model.songmenu.SongMenuManager;
import com.evideo.kmbox.model.songmenu.SongMenuManager.ISongMenuListListener;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.AnimLoadingView;
import com.evideo.kmbox.widget.common.CustomSelectorGridView;
import com.evideo.kmbox.widget.mainview.AbsBaseView;
import com.evideo.kmbox.widget.mainview.MainViewId;
import com.evideo.kmbox.widget.mainview.MainViewManager;
import com.evideo.kmbox.R;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;


public class SongMenuView extends AbsBaseView implements ISongMenuListListener {
    private int mFadingEdgeLength;
    private CustomSelectorGridView mGridView;
    private SongMenuAdapter mAdapter;
    private AnimLoadingView mLoadingView; 
    
    public SongMenuView(Activity activity, int backViewId) {
        super(activity, backViewId);
        initView();
    }
    
    public boolean resumeFocus() {
        EvLog.e("songmenu resumeFocus");
        if (mGridView != null) {
            mGridView.requestFocus();
            return true;
        }
        return false;
    }
    
    private void initView() {
        mLoadingView = (AnimLoadingView)findViewById(R.id.song_menu_loading_widget);
        
        mFadingEdgeLength = getResources().getDimensionPixelSize(R.dimen.px98);
        
        int selectorPaddingLeft = getResources().getDimensionPixelSize( R.dimen.px10);
        int selectorPaddingTop = getResources().getDimensionPixelSize( R.dimen.px11);
        int selectorPaddingRight = getResources().getDimensionPixelSize( R.dimen.px11);
        int selectorPaddingBottom = getResources().getDimensionPixelSize( R.dimen.px10);

        mGridView = (CustomSelectorGridView) findViewById(R.id.song_menu_gv);
        mGridView.setSelectorPadding(selectorPaddingLeft, selectorPaddingTop, 
                selectorPaddingRight, selectorPaddingBottom);
        mGridView.setVerticalFadingEdgeEnabled(true);
        mGridView.setFadingEdgeLength(mFadingEdgeLength); 
        
        mAdapter = new SongMenuAdapter(mActivity, mGridView, (ArrayList<SongMenu>)SongMenuManager.getInstance().getSongMenuList()); 
        mGridView.setAdapter(mAdapter);
        mGridView.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    onPageStart();
                }
            }
        });
        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if (!mGridView.isFocused()) {
                    return;
                }
                SongMenu songMenu = (SongMenu) parent.getAdapter().getItem(position);
                SongMenuManager.getInstance().notifySongMenuClicked(songMenu);
                LogAnalyzeManager.onEventInSongMenuSubPage(mActivity, songMenu.songMenuId);
            }
        });
        int id = MainViewManager.getInstance().getStatusBar().getSelectedNumId();
        mGridView.setNextFocusRightId(id);
    }

    @Override
    protected int getLayResId() {
        return R.layout.main_song_menu;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (SongMenuManager.getInstance().getSongMenuList().size() == 0) {
            if (mLoadingView.getVisibility() != View.VISIBLE) {
                mLoadingView.setVisibility(View.VISIBLE);
                mLoadingView.startAnim();
            }
            SongMenuManager.getInstance().startGetSongMenuListTask();
        }
        SongMenuManager.getInstance().registSongMenuListListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mLoadingView.getVisibility() == View.VISIBLE) {
            mLoadingView.stopAnim();
        }
        SongMenuManager.getInstance().unregistSongMenuListListener(this);
    }

    @Override
    protected int getViewId() {
        return MainViewId.ID_SONG_MENU;
    }

    @Override
    protected void resetFocus() {
        if (mGridView != null) {
            mGridView.requestFocus();
        } 
    }

    @Override
    public void onSongMenuSelected(SongMenu songMenu) {
    }

    @Override
    public void onSongMenuDataChanged() {
        if (mLoadingView.getVisibility() == View.VISIBLE) {
            mLoadingView.stopAnim();
            mLoadingView.setVisibility(View.GONE);
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        mGridView.requestFocus();
    }

    @Override
    public void onSongMenuClicked(SongMenu songMenu) {
    }

    @Override
    protected void clickExitKey() {
        
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
        if (mGridView != null) {
            mGridView.requestFocus();
            return true;
        }
        return false;
    }
}
