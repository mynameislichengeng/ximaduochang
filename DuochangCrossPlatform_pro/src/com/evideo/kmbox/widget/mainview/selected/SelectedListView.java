package com.evideo.kmbox.widget.mainview.selected;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.OnItemClickCallback;
import com.evideo.kmbox.widget.common.OnSongListKeyDownEventListener;
import com.evideo.kmbox.R;

/**
 * [已点列表ListView]
 */
public class SelectedListView extends ListView implements android.widget.AdapterView.OnItemClickListener {
    
    /** [点歌状态] */
    public static final int ITEM_STATE_NORMAL = 1;
    /** [顶歌状态] */
    public static final int ITEM_STATE_TOP = 2;
    /** [删歌状态] */
    public static final int ITEM_STATE_DELETE = 3;
    /** [切歌状态] */
    public static final int ITEM_STATE_CUT_SONG = 4;
    /** [收藏状态] */
    public static final int ITEM_STATE_FAVORITE = 5;
    private int mFadingEdgeLength;
    
    private int mItemState = ITEM_STATE_CUT_SONG;

    private Drawable mTopIcon;
    private Drawable mDeleteIcon;
    private Drawable mCutSongIcon;
    private Drawable mFavoriteIcon;
    private Drawable mFocusFrame;
    private int mIconSideLen;
    private int mIconIntrSideLen;
    private int mIconFocusPadding;
    
    private OnItemClickCallback mOnItemClickCallback;
    private OnSongListKeyDownEventListener mOnSongListKeyDownEventListener;
    
    
    public void resetState() {
        mItemState = ITEM_STATE_CUT_SONG;
    }
    /**
     * [设置按键监听]
     * @param onSongListKeyDownEventListener 按键监听器
     */
    public void setOnSongListKeyDownEventListener(OnSongListKeyDownEventListener onSongListKeyDownEventListener) {
        mOnSongListKeyDownEventListener = onSongListKeyDownEventListener;
    }

    public SelectedListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SelectedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SelectedListView(Context context) {
        super(context);
        init(context);
    }
    
    private void init(Context context) {
        reflectSelectorRect();
        mFadingEdgeLength = getResources().getDimensionPixelSize(R.dimen.px90);
        mTopIcon = getResources().getDrawable(R.drawable.ic_top_song);
        mDeleteIcon = getResources().getDrawable(R.drawable.ic_delete);
        mCutSongIcon = getResources().getDrawable(R.drawable.ic_cut_song);
        mFavoriteIcon = getResources().getDrawable(R.drawable.ic_favorite);
        mFocusFrame = getResources().getDrawable(R.drawable.focus_frame_new);
        mIconSideLen = getResources().getDimensionPixelSize(R.dimen.px99);
        mIconFocusPadding = getResources().getDimensionPixelSize(R.dimen.px15);
        mIconIntrSideLen = mTopIcon.getIntrinsicWidth();

        setCacheColorHint(context.getResources().getColor(R.color.transparent));
        setDivider(null);
        setSelector(R.drawable.song_list_focus_frame);
        setDrawSelectorOnTop(true);
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(mFadingEdgeLength);
        setOnItemClickListener(this);
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
    
    private void drawListSelector(Canvas canvas) {
        if (!mListSelectorRect.isEmpty()) {
            int selectedPos = getSelectedItemPosition();
            if (selectedPos != 1) {
                drawFavoriteIcon(canvas);
                drawSelectorIcon(canvas);
            } else {
                drawFirstPostionIcon(canvas);
            }
        }
    }
    
    private void drawFirstPostionIcon(Canvas canvas) {
        if (!isFocused()) {
            return;
        }
        
        int left = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2 - mIconSideLen;
        int right = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2 - mIconSideLen;
        int top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
        int bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;     
        //画删除图标
        final Drawable favoriteIcon = mDeleteIcon;
        favoriteIcon.setBounds(left, top, right, bottom);
        favoriteIcon.draw(canvas);
      
        
        left = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2;
        right = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2;
//        top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
//        bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;
        
        // 画收藏图标
        final Drawable deleteIcon = mFavoriteIcon;
        deleteIcon.setBounds(left, top, right, bottom);
        deleteIcon.draw(canvas);
    
        // 画焦点框
        if (mItemState == ITEM_STATE_FAVORITE) {
            left = mListSelectorRect.right - mIconSideLen - mIconFocusPadding;
            right = mListSelectorRect.right + mIconFocusPadding;
            top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
            bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
        } else if (mItemState == ITEM_STATE_DELETE) {
            left = mListSelectorRect.right - mIconSideLen * 2 - mIconFocusPadding;
            right = mListSelectorRect.right - mIconSideLen + mIconFocusPadding;
            top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
            bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
        }     
        
        final Drawable topIconFocusFrame = mFocusFrame;
        topIconFocusFrame.setBounds(left, top, right, bottom);
        topIconFocusFrame.draw(canvas);
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
    
    private void drawSelectorIcon(Canvas canvas) {
        if (!isFocused()) {
            return;
        }
        
        int selectedPos = getSelectedItemPosition();
        int left = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2;
        int right = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2;
        int top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
        int bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;

        
        if (selectedPos == 1) {
            // 画删除图标
            /*final Drawable deleteIcon = mDeleteIcon;
            deleteIcon.setBounds(left, top, right, bottom);
            deleteIcon.draw(canvas);*/
            final Drawable deleteIcon = mFavoriteIcon;
            deleteIcon.setBounds(left, top, right, bottom);
            deleteIcon.draw(canvas);
        } else if (selectedPos == 0) {
            //画切歌图标
            final Drawable cutSongIcon = mCutSongIcon;
            cutSongIcon.setBounds(left, top, right, bottom);
            cutSongIcon.draw(canvas);
        } else {
            // Draw topSong icon
            final Drawable topIcon = mTopIcon;
            topIcon.setBounds(left, top, right, bottom);
            topIcon.draw(canvas);
        }
        
        // Draw deleteIcon
        if (selectedPos != 0 && selectedPos != 1) {
            left = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2 - mIconSideLen * 2;
            right = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2 - mIconSideLen * 2;
            top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
            bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;
            final Drawable deleteIcon = mDeleteIcon;
            deleteIcon.setBounds(left, top, right, bottom);
            deleteIcon.draw(canvas);
        }
        
        // 画焦点框
        if (mItemState == ITEM_STATE_TOP || mItemState == ITEM_STATE_CUT_SONG
                || (selectedPos == 1 && mItemState == ITEM_STATE_DELETE)) {
            left = mListSelectorRect.right - mIconSideLen - mIconFocusPadding;
            right = mListSelectorRect.right + mIconFocusPadding;
            top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
            bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
            final Drawable topIconFocusFrame = mFocusFrame;
            topIconFocusFrame.setBounds(left, top, right, bottom);
            topIconFocusFrame.draw(canvas);
        } else if (selectedPos > 1 && mItemState == ITEM_STATE_DELETE) {
            left = mListSelectorRect.right - mIconSideLen * 3 - mIconFocusPadding;
            right = mListSelectorRect.right - mIconSideLen * 2 + mIconFocusPadding;
            top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
            bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
            final Drawable topIconFocusFrame = mFocusFrame;
            topIconFocusFrame.setBounds(left, top, right, bottom);
            topIconFocusFrame.draw(canvas);
        }
    }
    
    /**
     * [画收藏图标]
     * @param canvas 画布
     */
    private void drawFavoriteIcon(Canvas canvas) {
        if (!isFocused()) {
            return;
        }
        
        int left = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2 - mIconSideLen;
        int right = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2 - mIconSideLen;
        int top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
        int bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;     
        final Drawable favoriteIcon = mFavoriteIcon;
        favoriteIcon.setBounds(left, top, right, bottom);
        favoriteIcon.draw(canvas);
        
        if (mItemState == ITEM_STATE_FAVORITE) {
            left = mListSelectorRect.right - mIconSideLen * 2 - mIconFocusPadding;
            right = mListSelectorRect.right - mIconSideLen + mIconFocusPadding;
            top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
            bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
            final Drawable facoriteIconFocusFrame = mFocusFrame;
            facoriteIconFocusFrame.setBounds(left, top, right, bottom);
            facoriteIconFocusFrame.draw(canvas);
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
     * [恢复默认显示图标]
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
        
        int selectedPos = getSelectedItemPosition();

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (dealRightKeyDownEvent(selectedPos)) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (dealLeftKeyDownEvent(selectedPos)) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (selectedPos == 0) {
                    mItemState = ITEM_STATE_CUT_SONG;
                    if (mOnSongListKeyDownEventListener != null) {
                        mOnSongListKeyDownEventListener.onUpEdgeKeyDown();
                    }
                    return true;
                } else if (selectedPos == 1) {
                    mItemState = ITEM_STATE_CUT_SONG;
                } else if (selectedPos == 2){
                    mItemState = ITEM_STATE_FAVORITE;
                } else {
                    mItemState = ITEM_STATE_TOP;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (selectedPos == getAdapter().getCount() - 1) {
                    if (mOnSongListKeyDownEventListener != null) {
                        mOnSongListKeyDownEventListener.onDownEdgeKeyDown();
                    }
                    return true;
                } else if (selectedPos == 0/*mItemState == ITEM_STATE_CUT_SONG*/) {
                    mItemState = ITEM_STATE_FAVORITE;
                } else if (selectedPos >= 1 /*&& mItemState == ITEM_STATE_DELETE*/) {
                    mItemState = ITEM_STATE_TOP;
                }
                break;
            default:
                break;
        }
        
        return super.onKeyDown(keyCode, event);
    }
    
    /**
     * [处理按键右移]
     * @param seletedPos 选中位置
     * @return true:成功;false：失败;
     */
    private boolean dealRightKeyDownEvent(int seletedPos) {
        switch (seletedPos) {
            case 0:
                if (mItemState == ITEM_STATE_FAVORITE) {
                    mItemState = ITEM_STATE_CUT_SONG;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_CUT_SONG) {
                    dealRightEdgeKeyDownEvent();
                    return true;
                }
                break;
            case 1:
                if (mItemState == ITEM_STATE_DELETE) {
                    mItemState = ITEM_STATE_FAVORITE;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_FAVORITE) {
                    dealRightEdgeKeyDownEvent();
                    return true;
                }
                break;
            default:
                if (mItemState == ITEM_STATE_DELETE) {
                    mItemState = ITEM_STATE_FAVORITE;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_FAVORITE) {
                    mItemState = ITEM_STATE_TOP;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_TOP) {
                    dealRightEdgeKeyDownEvent();
                    return true;
                }
                break;
        }
        return false;
    }
    
    private void dealRightEdgeKeyDownEvent() {
        if (mOnSongListKeyDownEventListener != null) {
            mOnSongListKeyDownEventListener.onRightEdgeKeyDown();
        }
    }
    
    private boolean dealLeftKeyDownEvent(int seletedPos) {
        switch (seletedPos) {
            case 0:
                if (mItemState == ITEM_STATE_CUT_SONG) {
                    mItemState = ITEM_STATE_FAVORITE;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_FAVORITE) {
                    if (mOnSongListKeyDownEventListener != null) {
                        mOnSongListKeyDownEventListener.onLeftEdgeKeyDown();
                    }
                    return true;
                }
                break;
            case 1:
                if (mItemState == ITEM_STATE_FAVORITE) {
                    mItemState = ITEM_STATE_DELETE;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_DELETE) {
                    if (mOnSongListKeyDownEventListener != null) {
                        mOnSongListKeyDownEventListener.onLeftEdgeKeyDown();
                    }
                    return true;
                }
                break;
            default:
                if (mItemState == ITEM_STATE_TOP) {
                    mItemState = ITEM_STATE_FAVORITE;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_FAVORITE) {
                    mItemState = ITEM_STATE_DELETE;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_DELETE) {
                    if (mOnSongListKeyDownEventListener != null) {
                        mOnSongListKeyDownEventListener.onLeftEdgeKeyDown();
                    }
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        if (mOnItemClickCallback != null) {
            if (getCount() == 1 && mItemState == ITEM_STATE_DELETE) {
                mItemState = ITEM_STATE_CUT_SONG;
            }
            mOnItemClickCallback.onItemClick(parent, view, position, id, mItemState);
        }
    }
    
    /**
     * [设置项点击回调]
     * @param onItemClickCallback 回调函数
     */
    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        mOnItemClickCallback = onItemClickCallback;
    }
    
    /**
     * [重置mItemState使selector位于收藏图标处]
     */
    public void resetItemState() {
        mItemState = ITEM_STATE_FAVORITE;
    }
}
