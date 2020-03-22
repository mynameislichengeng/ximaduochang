package com.evideo.kmbox.widget.common;

import java.lang.reflect.Field;

import com.evideo.kmbox.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * [已点列表ListView]
 */
public class OrderedListView extends ListView implements android.widget.AdapterView.OnItemClickListener {
    
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
    
    private int mItemState = ITEM_STATE_NORMAL;
    
    private Drawable mTopIcon;
    private Drawable mDeleteIcon;
    private Drawable mCutSongIcon;
    private Drawable mFavoriteIcon;
    private Drawable mFocusFrame;
    private int mIconSideLen;
    private int mIconIntrSideLen;
    
    private OnItemClickCallback mOnItemClickCallback;
    private OnSongListKeyDownEventListener mOnSongListKeyDownEventListener;
    
    private Rect mListSelectorRect;
    private void reflectSelectorRect() {
        try {
            Class<?> c = Class.forName("android.widget.AbsListView");
            Field f = c.getDeclaredField("mSelectorRect");
            f.setAccessible(true);
            mListSelectorRect = (Rect) f.get(this);
        } catch (Exception e) {
        }
    }
    /**
     * [设置按键监听]
     * @param onSongListKeyDownEventListener 按键监听器
     */
    public void setOnSongListKeyDownEventListener(OnSongListKeyDownEventListener onSongListKeyDownEventListener) {
        mOnSongListKeyDownEventListener = onSongListKeyDownEventListener;
    }

    public OrderedListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public OrderedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OrderedListView(Context context) {
        super(context);
        init(context);
    }
    
    private void init(Context context) {
        reflectSelectorRect();
        mFadingEdgeLength = getResources().getDimensionPixelSize(R.dimen.song_list_fading_edge_length);
        mTopIcon = getResources().getDrawable(R.drawable.ic_top_song);
        mDeleteIcon = getResources().getDrawable(R.drawable.ic_delete);
        mCutSongIcon = getResources().getDrawable(R.drawable.ic_cut_song);
        mFavoriteIcon = getResources().getDrawable(R.drawable.ic_favorite);
        mFocusFrame = getResources().getDrawable(R.drawable.focus_frame_new);
        mIconSideLen = getResources().getDimensionPixelSize(R.dimen.song_list_item_icon_width);
        mIconIntrSideLen = mTopIcon.getIntrinsicWidth();
        setCacheColorHint(context.getResources().getColor(R.color.transparent));
        setDivider(null);
        setSelector(R.drawable.common_focused_selector);
        setDrawSelectorOnTop(true);
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(mFadingEdgeLength);
        setOnItemClickListener(this);
    }
/*
    @Override
    protected void drawSelector(Canvas canvas) {
        if (!mSelectorRect.isEmpty()) {
            drawNormalSelector(canvas);
            drawFavoriteIcon(canvas);
            drawSelectorIcon(canvas);
        }
    }*/
    
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
//       EvLog.d("SongListView onKeyDown keyCode: " + keyCode);
        
        if (getAdapter().getCount() <= 0) {
            return super.onKeyDown(keyCode, event);
        }
        
        int seletedPos = getSelectedItemPosition();
        
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (dealRightKeyDownEvent(seletedPos)) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (dealLeftKeyDownEvent(seletedPos)) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (getSelectedItemPosition() == 0) {
                    return super.onKeyDown(keyCode, event);
                }
                mItemState = ITEM_STATE_NORMAL;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (getSelectedItemPosition() == getAdapter().getCount() - 1) {
                    return super.onKeyDown(keyCode, event);
                }
                mItemState = ITEM_STATE_NORMAL;
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
                if (mItemState == ITEM_STATE_NORMAL) {
                    mItemState = ITEM_STATE_FAVORITE;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_FAVORITE) {
                    mItemState = ITEM_STATE_CUT_SONG;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_CUT_SONG) {
                    dealRightEdgeKeyDownEvent();
                    return true;
                }
                break;
            case 1:
                if (mItemState == ITEM_STATE_NORMAL) {
                    mItemState = ITEM_STATE_FAVORITE;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_FAVORITE) {
                    mItemState = ITEM_STATE_DELETE;
                    invalidate();
                } else if (mItemState == ITEM_STATE_DELETE) {
                    dealRightEdgeKeyDownEvent();
                    return true;
                }
                break;
            default:
                if (mItemState == ITEM_STATE_NORMAL) {
                    mItemState = ITEM_STATE_TOP;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_TOP) {
                    mItemState = ITEM_STATE_FAVORITE;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_FAVORITE) {
                    mItemState = ITEM_STATE_DELETE;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_DELETE) {
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
                    mItemState = ITEM_STATE_NORMAL;
                    invalidate();
                    return true;
                }
                break;
            case 1:
                if (mItemState == ITEM_STATE_DELETE) {
                    mItemState = ITEM_STATE_FAVORITE;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_FAVORITE) {
                    mItemState = ITEM_STATE_NORMAL;
                    invalidate();
                    return true;
                }
                break;
            default:
                if (mItemState == ITEM_STATE_TOP) {
                    mItemState = ITEM_STATE_NORMAL;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_FAVORITE) {
                    mItemState = ITEM_STATE_TOP;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_DELETE) {
                    mItemState = ITEM_STATE_FAVORITE;
                    invalidate();
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
}
