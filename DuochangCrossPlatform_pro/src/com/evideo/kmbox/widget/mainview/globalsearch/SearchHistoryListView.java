
package com.evideo.kmbox.widget.mainview.globalsearch;

import java.lang.reflect.Field;

import com.evideo.kmbox.R;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.OnItemClickCallback;
import com.evideo.kmbox.widget.common.OnSongListKeyDownEventListener;
import com.evideo.kmbox.widget.common.OrderSongAnimController;
import com.evideo.kmbox.widget.common.OrderSongAnimView;
import com.evideo.kmbox.widget.mainmenu.order.BindCodeQrView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * [搜索历史listview包含了song与singer，要动态重绘selector]
 */
public class SearchHistoryListView extends ListView implements android.widget.AdapterView.OnItemClickListener {
    /** [点歌状态] */
    public static final int ITEM_STATE_NORMAL = 1;
    /** [顶歌状态] */
    public static final int ITEM_STATE_TOP = 2;
    /** [收藏状态] */
    public static final int ITEM_STATE_FAVORITE = 3;
    private int mFadingEdgeLength;
    
    private int mItemState = ITEM_STATE_NORMAL;
    
    private Drawable mTopIcon;
    private Drawable mFavoriteIcon;
    private Drawable mFocusFrame;
    private int mIconSideLen;
    private int mIconIntrSideLen;
    private int mIconFocusPadding;
    /** [是否显示点歌动画] */
    private int mFromView = OrderSongAnimView.FROM_VIEW_MAIN_MENU;
    
    private View mFootLoadingView;
    private View mFootCompleteView;
    private TextView mFootLoadingTv;
    private TextView mFootQrHintTv;
    private BindCodeQrView mFootQrView;
    private View mFootLoadingPb;
    /** [歌曲序号、歌名、歌手名的焦点框微调] */
    private int mTvLayoutPaddingLeft;
    private int mTvLayoutPaddingRight;
    private int mTvLayoutPaddingTop;
    private int mTvLayoutPaddingBottom;
    private int mClickItemPos = -1;
    
    /**
     * [设置点歌视图]
     * @param fromView 点歌视图
     */
    public void setOrderFromView(int fromView) {
        this.mFromView = fromView;
    }

    private OnItemClickCallback mOnItemClickCallback;
    private OnSongListKeyDownEventListener mOnSongListKeyDownEventListener;
    
    /**
     * [列表按键监听]
     * @param onSongListKeyDownEventListener 按键监听器
     */
    public void setOnSongListKeyDownEventListener(OnSongListKeyDownEventListener onSongListKeyDownEventListener) {
        mOnSongListKeyDownEventListener = onSongListKeyDownEventListener;
    }
    
    public SearchHistoryListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SearchHistoryListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchHistoryListView(Context context) {
        super(context);
        init(context);
    }
    
    private void init(Context context) {
        reflectSelectorRect();
        mFadingEdgeLength = getResources().getDimensionPixelSize(R.dimen.px90);
        mTopIcon = getResources().getDrawable(R.drawable.ic_top_song);
        mFavoriteIcon = getResources().getDrawable(R.drawable.ic_favorite);
        mFocusFrame = getResources().getDrawable(R.drawable.focus_frame_new);
        mIconSideLen = getResources().getDimensionPixelSize(R.dimen.px99);
        mIconFocusPadding = getResources().getDimensionPixelSize(R.dimen.px15);
        mIconIntrSideLen = mTopIcon.getIntrinsicWidth();
        mTvLayoutPaddingLeft = getResources().getDimensionPixelSize(
                R.dimen.px3);
        mTvLayoutPaddingRight = getResources().getDimensionPixelSize(
                R.dimen.px6);
        mTvLayoutPaddingTop = getResources().getDimensionPixelSize(
                R.dimen.px15);
        mTvLayoutPaddingBottom = getResources().getDimensionPixelSize(
                R.dimen.px15);
        setCacheColorHint(context.getResources().getColor(R.color.transparent));
        setDivider(null);
        setSelector(R.drawable.song_list_focus_frame);
        setDrawSelectorOnTop(true);
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(mFadingEdgeLength);
        setOnItemClickListener(this);
        
        mFootLoadingView = LayoutInflater.from(context).inflate(R.layout.song_list_item_loading, null);
        mFootCompleteView = LayoutInflater.from(context).inflate(R.layout.song_list_item_complete, null);
        mFootLoadingPb = mFootLoadingView.findViewById(R.id.song_list_item_foot_pb);
        mFootLoadingTv = (TextView) mFootLoadingView.findViewById(R.id.song_list_item_foot_tv);
        mFootQrHintTv = (TextView) mFootCompleteView.findViewById(R.id.song_list_item_foot_qrhint_tv);
        mFootQrView = (BindCodeQrView) mFootCompleteView.findViewById(R.id.song_list_item_foot_qr);
        
       // addFooterView(mFootCompleteView, null, false);
    }
    
    /**
     * [删除底部加载界面]
     */
    public void removeFootLoadingView() {
        EvLog.d("----removeFootLoadingView----");
        removeFooterView(mFootLoadingView);
    }
    
    /**
     * [显示底部加载界面]
     */
    public void showFootLoadingView() {
        EvLog.d("----showFootLoadingView----");
        mFootLoadingPb.setVisibility(View.VISIBLE);
        mFootLoadingTv.setText(R.string.loading_song);
        removeFooterView(mFootLoadingView);
        addFooterView(mFootLoadingView, null, false);
    }
    
    /**
     * [显示底部提示界面]
     * @param resid 文字资源id
     */
    public void showFootView(int resid) {
        EvLog.d("----showFootView----");
        mFootQrView.updateQr();
        mFootQrHintTv.setText(resid);
        removeFooterView(mFootLoadingView);
        removeFooterView(mFootCompleteView);
        addFooterView(mFootCompleteView, null, false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isFocused()) {
            drawListSelector(canvas);
        }
    }
    
    /**[返回选中item的类型：singer or song]
     * @return
     */
    private int getSelectedItemType() {
        if (getAdapter() == null) {
            return -1;
        }
        int position = getSelectedItemPosition();
        if (getAdapter() == null || position >= getAdapter().getCount()) {
            return -1;
        }
        SearchHistoryItem item = (SearchHistoryItem) getAdapter().getItem(position);
        if (item == null) {
            return -1;
        }
        EvLog.i("wrq", "selected item type:" + item.mItemType);
        return item.mItemType;
    }
    
    private void drawListSelector(Canvas canvas) {
        
        if (!mListSelectorRect.isEmpty()) {
            switch(getSelectedItemType()) {
                case SearchHistoryItem.SEARCH_ITEM_TYPE_SINGER:
                    drawSingerSelector(canvas);
                    break;
                case SearchHistoryItem.SEARCH_ITEM_TYPE_SONG:
                    drawNormalSelector(canvas);
                    drawFavoriteIcon(canvas);
                    drawSelectorIcon(canvas);
                    break;
                default:
                    break;
            }
        }
    }
    
    private Rect mListSelectorRect;
    
    /**
     * [功能说明]反射获取mSelectorRect的引用
     */
    private void reflectSelectorRect() {
        try {
            Class<?> c = Class.forName("android.widget.AbsListView");
            Field f = c.getDeclaredField("mSelectorRect");
            f.setAccessible(true);
            mListSelectorRect = (Rect) f.get(this);
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
    }
    
    private void drawSingerSelector(Canvas canvas) {
        final Drawable selector = mFocusFrame;
        selector.setBounds(mListSelectorRect.left - mTvLayoutPaddingLeft,
                           mListSelectorRect.top - mTvLayoutPaddingTop,
                           mListSelectorRect.right - mTvLayoutPaddingRight,
                           mListSelectorRect.bottom + mTvLayoutPaddingBottom);
        selector.draw(canvas);
    }
    
    private void drawNormalSelector(Canvas canvas) {
        if (mItemState == ITEM_STATE_NORMAL) {
            final Drawable selector = mFocusFrame;
            selector.setBounds(mListSelectorRect.left - mTvLayoutPaddingLeft,
                               mListSelectorRect.top - mTvLayoutPaddingTop,
                               mListSelectorRect.right - mIconSideLen * 2 - mTvLayoutPaddingRight,
                               mListSelectorRect.bottom + mTvLayoutPaddingBottom);
            selector.draw(canvas);
        }
    }
    
    private void drawFavoriteIcon(Canvas canvas) {
        if (!isFocused()) {
            return;
        }
        int left = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2;
        int right = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2;
        int top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
        int bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;
        
        final Drawable favoriteIcon = mFavoriteIcon;
        favoriteIcon.setBounds(left, top, right, bottom);
        favoriteIcon.draw(canvas);

        if (mItemState == ITEM_STATE_FAVORITE) {
            left = mListSelectorRect.right - mIconSideLen - mIconFocusPadding;
            right = mListSelectorRect.right + mIconFocusPadding;
            top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
            bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
            final Drawable facoriteIconFocusFrame = mFocusFrame;
            facoriteIconFocusFrame.setBounds(left, top, right, bottom);
            facoriteIconFocusFrame.draw(canvas);
        }
    }
    
    /**
     * [描绘顶歌图标]
     * @param canvas 画布
     */
    public void drawSelectorIcon(Canvas canvas) {
        if (!isFocused()) {
            return;
        }
        
        int left = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2 - mIconSideLen;
        int right = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2 - mIconSideLen;
        int top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
        int bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;    
        final Drawable topIcon = mTopIcon;
        topIcon.setBounds(left, top, right, bottom);
        topIcon.draw(canvas);
        
        if (mItemState == ITEM_STATE_TOP) {
            left = mListSelectorRect.right - mIconSideLen * 2 - mIconFocusPadding;
            right = mListSelectorRect.right - mIconSideLen + mIconFocusPadding;
            top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
            bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
            final Drawable topIconFocusFrame = mFocusFrame;
            topIconFocusFrame.setBounds(left, top, right, bottom);
            topIconFocusFrame.draw(canvas);
        }        
    }
    
    /**
     * [高亮显示收藏图标]
     */
    public void highlightFavoriteIcon() {
        mFavoriteIcon = getResources().getDrawable(R.drawable.ic_favorite_hl);
        invalidate();
    }
    
    /**
     * [恢复默认显示收藏图标]
     */
    public void restoreFavoriteIcon() {
        mFavoriteIcon = getResources().getDrawable(R.drawable.ic_favorite);
        invalidate();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        EvLog.d("SongListView onKeyDown keyCode: " + keyCode);
        
        if (getAdapter().getCount() <= 0) {
            return super.onKeyDown(keyCode, event);
        }
        
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mItemState == ITEM_STATE_NORMAL) {
                    mItemState = ITEM_STATE_TOP;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_TOP) {
                    mItemState = ITEM_STATE_FAVORITE;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_FAVORITE) {
                    if (mOnSongListKeyDownEventListener != null) {
                        mOnSongListKeyDownEventListener.onRightEdgeKeyDown();
                        return true;
                    }
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mItemState == ITEM_STATE_TOP) {
                    mItemState = ITEM_STATE_NORMAL;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_FAVORITE) {
                    mItemState = ITEM_STATE_TOP;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_NORMAL) {
                    if (mOnSongListKeyDownEventListener != null) {
                        mOnSongListKeyDownEventListener.onLeftEdgeKeyDown();
                        return true;
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (getSelectedItemPosition() == 0) {
                    return super.onKeyDown(keyCode, event);
                }
                if (mItemState == ITEM_STATE_TOP || mItemState == ITEM_STATE_FAVORITE) {
                    mItemState = ITEM_STATE_NORMAL;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (getSelectedItemPosition() == getAdapter().getCount() - 1) {
                    return super.onKeyDown(keyCode, event);
                }
                if (mItemState == ITEM_STATE_TOP || mItemState == ITEM_STATE_FAVORITE) {
                    mItemState = ITEM_STATE_NORMAL;
                }
                break;
            default:
                break;
        }
        
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        
       /* if (mNeedShowOrderSongAnim 
                &&  (mItemState == ITEM_STATE_NORMAL || mItemState == ITEM_STATE_TOP)
                && getSelectedItemType() == SearchHistoryItem.SEARCH_ITEM_TYPE_SONG) {
            startOrderSongAnimDelayed(position);
        }*/
        mClickItemPos = position;   
        if (mOnItemClickCallback != null) {
            mOnItemClickCallback.onItemClick(parent, view, position, id, mItemState);
        }
    }
    
 
 
    public void startOrderSongAnimDelayed() {
//        startOrderSongAnim(mClickItemPos);
    }
    
   
    /**
     * [设置点击回调]
     * @param onItemClickCallback 回调函数
     */
    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        mOnItemClickCallback = onItemClickCallback;
    } 
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
            Rect previouslyFocusedRect) {
        int lastSelectedItemPosition = getSelectedItemPosition();
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus && lastSelectedItemPosition == 0) {
            setSelection(lastSelectedItemPosition);
        }
    }
}
