/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-10-5     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mv.selected;

import java.util.ArrayList;
import java.util.List;

import com.evideo.kmbox.R;
import com.evideo.kmbox.model.dao.data.SongSubject;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.playerctrl.PlayCtrlHandler;
import com.evideo.kmbox.model.playerctrl.list.FavoriteListManager;
import com.evideo.kmbox.model.playerctrl.list.FavoriteListManager.IFavoriteListListener;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager.IPlayListListener;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.model.umeng.EventConst;
import com.evideo.kmbox.model.umeng.PageName;
import com.evideo.kmbox.presenter.BasePresenter;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.anim.SlideRightIn;
import com.evideo.kmbox.widget.anim.SlideRightOut;
import com.evideo.kmbox.widget.common.OnItemClickCallback;
import com.evideo.kmbox.widget.common.OrderedListView;
import com.evideo.kmbox.widget.mainview.MainViewManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * [功能说明]mv已点界面
 */
public class MvSelectedView extends FrameLayout implements IPlayListListener, IFavoriteListListener {
    
    private SlideRightIn mSlideRightIn;
    private SlideRightOut mSlideRightOut;
    
    private TextView mNumTv;
    private MvSelectedListView mListView;
    private MvSelectedListAdapter mAdapter;
    private ImageView mEmptyHint;
    private ArrayList<KmPlayListItem> mDatas;

    /**
     * @param context
     */
    public MvSelectedView(Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.mv_selected, this, true);
        init();
    }
    
    /**
     * [功能说明]初始化
     */
    private void init() {
//        PlayListManager.getInstance().registerListener(this);
        
        mEmptyHint = (ImageView) findViewById(R.id.mv_selected_empty_hint_iv);
        mNumTv = (TextView) findViewById(R.id.mv_selected_num_tv);
        mListView = (MvSelectedListView) findViewById(R.id.mv_selected_list);
        mListView.setEmptyView(mEmptyHint);
        
        updateCount();
        
        // 滑进动画
        mSlideRightIn = new SlideRightIn();
        // TODO set translation x
        mSlideRightIn.setAnimListener(new AnimatorListenerAdapter() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onAnimationStart(Animator animation) {
                setVisibility(View.VISIBLE);
                MainViewManager.getInstance().hideStatusBarSelectedNum();
            }
            /**
             * {@inheritDoc}
             */
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListView != null) {
                    mListView.requestFocus();
                }
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        
        // 滑出动画
        mSlideRightOut = new SlideRightOut();
        mSlideRightOut.setAnimListener(new AnimatorListenerAdapter() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.GONE);
                MainViewManager.getInstance().showStatusBarSelectedNum();
            }
        });
        
        setVisibility(View.GONE);
        
        updateData();
        
        EvLog.i("MvSelectedView mDatas.size:" + mDatas.size());
        mAdapter = new MvSelectedListAdapter(getContext(), mListView, mDatas);
        mListView.setAdapter(mAdapter);
        
        mListView.setOnItemClickCallback(new OnItemClickCallback() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id, int itemState) {
                final KmPlayListItem item = new KmPlayListItem();
                boolean ret = PlayListManager.getInstance().getCopyItemByPos(position,item);
                if ((ret ==  false) || (item == null)) {
                    return;
                }
                
                try {
                    if (itemState == OrderedListView.ITEM_STATE_DELETE) {
                        PlayListManager.getInstance().delSongByUser(item.getSerialNum());
//                        LogAnalyzeManager.onEvent(getContext(), EventConst.ID_CLICK_SELECTED_LIST_VIEW_DELETE_SONG);
                    } else if (itemState == OrderedListView.ITEM_STATE_TOP) {    
                        PlayListManager.getInstance().addSong(item.getCustomerid(),  item.getSongId(), true);
//                        LogAnalyzeManager.onEvent(getContext(), EventConst.ID_CLICK_SELECTED_LIST_VIEW_TOP_SONG);
                    } else if (itemState == OrderedListView.ITEM_STATE_CUT_SONG) {
                        cutSong();
//                        LogAnalyzeManager.onEvent(getContext(), EventConst.ID_CLICK_SELECTED_LIST_VIEW_CUT_SONG);
                     } else if (itemState == OrderedListView.ITEM_STATE_FAVORITE) {
                        
                        if (FavoriteListManager.getInstance().isAlreadyExists(item.getSongId())) {
                            if (FavoriteListManager.getInstance().delSong(item.getSongId())) {
//                                LogAnalyzeManagerUtil.onEventFavoriteAction(mContext, 
//                                        EventConst.ID_CLICK_SELECTED_LIST_VIEW_FAVORITE, false);
                            }   
                            return;
                        } else if (FavoriteListManager.getInstance().addSong(item.getSongId())) {
//                            LogAnalyzeManagerUtil.onEventFavoriteAction(mContext, 
//                                    EventConst.ID_CLICK_SELECTED_LIST_VIEW_FAVORITE, true);
                        }              
                    }
                } catch (Exception e) {
                    
                }
            }
        });
        
        mListView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    LogAnalyzeManager.getInstance().onPageStart(PageName.MV_SELECTED_LIST);
//                } else {
//                    LogAnalyzeManager.getInstance().onPageStart(PageName.MV_VIEW);
//                }
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
        
    }
    
//    private PlayCtrlHandler mPlayCtrlHandler;
    
    /**
     * [功能说明]设置播控handler
     * @param handler
     */
  /*  public void setPlayCtrlHandler(PlayCtrlHandler handler) {
        mPlayCtrlHandler = handler;
    }*/
    
    /**
     * [切歌动作]
     */
    private void cutSong() {
        PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_CUT_SONG);
//        if (mPlayCtrlHandler != null) {
//            mPlayCtrlHandler.sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_CUT_SONG);
//        }    
    }
    
    private void updateCount() {
        int count = PlayListManager.getInstance().getCount();
        String content = getContext().getResources().getString(R.string.mv_selected_list_title, count);
        mNumTv.setText(Html.fromHtml(content));
    }
    
    private void updateData() {
        if (mDatas == null) {
            mDatas = new ArrayList<KmPlayListItem>();
        }
        mDatas.clear();
        mDatas.addAll(PlayListManager.getInstance().getList());
    }
    /**
     * [功能说明]显示
     */
    public void show() {
        if (isOnAnim()) {
            return;
        }
        mSlideRightIn.start(this);
    }
    
    /**
     * [功能说明]隐藏
     */
    public void hide() {
        if (isOnAnim()) {
            return;
        }
        mSlideRightOut.start(this);
    }
    
    /**
     * [功能说明]是否可见
     * @return true 可见  false 不可见
     */
    public boolean isVisible() {
        return getVisibility() == View.VISIBLE;
    }
    
    /**
     * [功能说明]是否处于动画中
     * @return true 是 false 不是
     */
    public boolean isOnAnim() {
        boolean isOnSlideInAnim = false;
        if (mSlideRightIn != null) {
            isOnSlideInAnim = mSlideRightIn.isStarted();
        }
        boolean isOnSlideOutAnim = false;
        if (mSlideRightOut != null) {
            isOnSlideOutAnim = mSlideRightOut.isStarted();
        }
        return isOnSlideInAnim || isOnSlideOutAnim;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//        EvLog.d("zxh", "Mv selected view dispatch key event keycode " + event.getKeyCode());
        if (isOnAnim()) {
            return true;
        }
        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            return super.dispatchKeyEvent(event);
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK/* || event.getKeyCode() == KeyEvent.KEYCODE_MENU*/) {
            hide();
            return true;
        } /*else if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && DeviceConfigManager.getInstance().isBesTvLittleRed()) {
            hide();
            return true;
        }*/
        return super.dispatchKeyEvent(event);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        EvLog.d("zxh", "mv selected on key down keycode " + keyCode);
        return super.onKeyDown(keyCode, event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPlayListChange() {
        if (mDatas == null) {
            mDatas = new ArrayList<KmPlayListItem>();
        }
        
        updateCount();
        
        mDatas.clear();
        List<KmPlayListItem> datas = PlayListManager.getInstance().getList();
        if (datas != null) {
            mDatas.addAll(PlayListManager.getInstance().getList());
        }
        
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
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
        PlayListManager.getInstance().registerListener(this);
        FavoriteListManager.getInstance().registerListener(this);
        updateData();
        updateCount();
        mAdapter.notifyDataSetChanged();
    }
    
    @Override
    protected void onDetachedFromWindow() {
//        EvLog.d("zxh", "mv selected view on detached to window");
        FavoriteListManager.getInstance().unregisterListener(this);
        PlayListManager.getInstance().unregisterListener(this);
        if (mDatas != null) {
            mDatas.clear();
            mDatas = null;
        }
        super.onDetachedFromWindow();
    }

    /**
     * [重置selector到第一项item的收藏icon]
     */
    public void resetSelectorPosition() {
        //重置焦点到第一个
        if (mAdapter.getCount() > 0 && mListView != null) {
            mListView.requestFocus();
            mListView.resetItemState();
            mListView.setSelection(0);
        }
    }

    /**
     * {@inheritDoc}
     * 只在其它页面的收藏按钮变化引起的回调且焦点所在的当前歌曲与改变的歌曲一致时才做处理
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
}
