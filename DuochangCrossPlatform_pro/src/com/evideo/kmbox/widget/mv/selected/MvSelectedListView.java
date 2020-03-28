/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-10-6     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mv.selected;

import java.lang.reflect.Field;

import com.evideo.kmbox.R;
import com.evideo.kmbox.data.TouchEventManager;
import com.evideo.kmbox.model.touch.TouchPostionParam;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.OnItemClickCallback;
import com.evideo.kmbox.widget.common.OnSongListKeyDownEventListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * [功能说明]mv已点列表
 */
public class MvSelectedListView extends ListView implements android.widget.AdapterView.OnItemClickListener {


    private final String TAG = MvSelectedListView.class.getSimpleName();

    /**
     * [点歌状态]
     */
    public static final int ITEM_STATE_NORMAL = 1;
    /**
     * [顶歌状态]
     */
    public static final int ITEM_STATE_TOP = 2;
    /**
     * [删歌状态]
     */
    public static final int ITEM_STATE_DELETE = 3;
    /**
     * [切歌状态]
     */
    public static final int ITEM_STATE_CUT_SONG = 4;
    /**
     * [收藏状态]
     */
    public static final int ITEM_STATE_FAVORITE = 5;
    private int mFadingEdgeLength;

    private int mItemState = ITEM_STATE_NORMAL;

    /**
     * 图标在左边
     */
    private static final int ICON_POSITION_LEFT = 2;
    /**
     * 图标在中间
     */
    private static final int ICON_POSITION_CENTER = 1;
    /**
     * 图标在右边
     */
    private static final int ICON_POSITION_RIGHT = 0;

    private TouchPostionParam topTouchPostionParam;//置顶按钮的位置
    private TouchPostionParam favoriteTouchPostionParam;//搜查按钮位置
    private TouchPostionParam delTouchPostionParam;//搜查按钮位置
    private TouchPostionParam cutTouchPostionParam;//搜查按钮位置

    private Drawable mTopIcon;
    private Drawable mDeleteIcon;
    private Drawable mCutSongIcon;
    private Drawable mFavoriteIcon;
    private Drawable mFocusFrame;
    private int mIconSideLen;
    private int mIconIntrSideLen;
    private int mIconFocusPadding;

    private int positionSelect = -1;

    private OnItemClickCallback mOnItemClickCallback;
    private OnSongListKeyDownEventListener mOnSongListKeyDownEventListener;

    /**
     * [设置按键监听]
     *
     * @param onSongListKeyDownEventListener 按键监听器
     */
    public void setOnSongListKeyDownEventListener(OnSongListKeyDownEventListener onSongListKeyDownEventListener) {
        mOnSongListKeyDownEventListener = onSongListKeyDownEventListener;
    }

    public MvSelectedListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public MvSelectedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MvSelectedListView(Context context) {
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
        setSelector(R.drawable.mv_selected_list_selector);
        setDrawSelectorOnTop(false);
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

    private void drawListSelector(Canvas canvas) {
        if (!mListSelectorRect.isEmpty()) {
//            drawSelectorIcon(canvas);

            int count = this.getAdapter().getCount();
            if (count > positionSelect) {
                if (positionSelect == 0) {
                    drawZreoIcon(canvas);
                } else if (positionSelect == 1) {
                    drawFirstIcon(canvas);
                } else if (positionSelect == -1) {

                } else {
                    drawOtherIcon(canvas);
                }
            }


        }
    }


    private void drawZreoIcon(Canvas canvas) {
        if (!isFocused()) {
            return;
        }

        int left = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2;
        int right = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2;
        int top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
        int bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;

        if (cutTouchPostionParam == null) {
            cutTouchPostionParam = new TouchPostionParam();
        }
        cutTouchPostionParam.setLeft(left);
        cutTouchPostionParam.setRight(right);
        cutTouchPostionParam.setUp(top);
        cutTouchPostionParam.setDown(bottom);

        //画切歌图标
        final Drawable cutSongIcon = mCutSongIcon;
        cutSongIcon.setBounds(left, top, right, bottom);
        cutSongIcon.draw(canvas);

        if (mItemState == ITEM_STATE_CUT_SONG) {
            left = mListSelectorRect.right - mIconSideLen - mIconFocusPadding;
            right = mListSelectorRect.right + mIconFocusPadding;
            top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
            bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
            final Drawable topIconFocusFrame = mFocusFrame;
            topIconFocusFrame.setBounds(left, top, right, bottom);
            topIconFocusFrame.draw(canvas);

        }

        drawFavoriteIcon(canvas, ICON_POSITION_CENTER);

    }

    private void drawFirstIcon(Canvas canvas) {
        if (!isFocused()) {
            return;
        }
        drawFavoriteIcon(canvas, 0);
        drawDeleteIcon(canvas, 1);
    }

    private void drawOtherIcon(Canvas canvas) {
        if (!isFocused()) {
            return;
        }

        int left = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2;
        int right = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2;
        int top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
        int bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;

        if (topTouchPostionParam == null) {
            topTouchPostionParam = new TouchPostionParam();
        }
        topTouchPostionParam.setLeft(left);
        topTouchPostionParam.setRight(right);
        topTouchPostionParam.setUp(top);
        topTouchPostionParam.setDown(bottom);


        // Draw topSong icon
        final Drawable topIcon = mTopIcon;
        topIcon.setBounds(left, top, right, bottom);
        topIcon.draw(canvas);
        if (mItemState == ITEM_STATE_TOP) {
            left = mListSelectorRect.right - mIconSideLen - mIconFocusPadding;
            right = mListSelectorRect.right + mIconFocusPadding;
            top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
            bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
            final Drawable topIconFocusFrame = mFocusFrame;
            topIconFocusFrame.setBounds(left, top, right, bottom);
            topIconFocusFrame.draw(canvas);
        }

        //
        drawFavoriteIcon(canvas, 1);
        drawDeleteIcon(canvas, 2);

    }


    /**
     * [画收藏图标]
     *
     * @param canvas 画布
     * @param pos    {@link MvSelectedListView#ICON_POSITION_LEFT}
     *               {@link MvSelectedListView#ICON_POSITION_CENTER}
     */
    private void drawFavoriteIcon(Canvas canvas, int pos) {
        if (!isFocused()) {
            return;
        }

        int left = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2 - mIconSideLen * pos;
        int right = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2 - mIconSideLen * pos;
        int top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
        int bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;

        if (favoriteTouchPostionParam == null) {
            favoriteTouchPostionParam = new TouchPostionParam();
        }
        favoriteTouchPostionParam.setLeft(left);
        favoriteTouchPostionParam.setRight(right);
        favoriteTouchPostionParam.setUp(top);
        favoriteTouchPostionParam.setDown(bottom);


        final Drawable favoriteIcon = mFavoriteIcon;
        favoriteIcon.setBounds(left, top, right, bottom);
        favoriteIcon.draw(canvas);

        if (mItemState == ITEM_STATE_FAVORITE) {
            left = mListSelectorRect.right - mIconSideLen * (pos + 1) - mIconFocusPadding;
            right = mListSelectorRect.right - mIconSideLen * pos + mIconFocusPadding;
            top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
            bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
            final Drawable facoriteIconFocusFrame = mFocusFrame;
            facoriteIconFocusFrame.setBounds(left, top, right, bottom);
            facoriteIconFocusFrame.draw(canvas);
        }
    }

    /**
     * [画删除图标]
     *
     * @param canvas 画布
     * @param pos    {@link MvSelectedListView#ICON_POSITION_LEFT}
     *               {@link MvSelectedListView#ICON_POSITION_CENTER}
     *               {@link MvSelectedListView#ICON_POSITION_RIGHT}
     */
    private void drawDeleteIcon(Canvas canvas, int pos) {
        if (!isFocused()) {
            return;
        }

        int left = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2 - mIconSideLen * pos;
        int right = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2 - mIconSideLen * pos;
        int top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
        int bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;

        if (delTouchPostionParam == null) {
            delTouchPostionParam = new TouchPostionParam();
        }
        delTouchPostionParam.setLeft(left);
        delTouchPostionParam.setRight(right);
        delTouchPostionParam.setUp(top);
        delTouchPostionParam.setDown(bottom);

        final Drawable deleteIcon = mDeleteIcon;
        deleteIcon.setBounds(left, top, right, bottom);
        deleteIcon.draw(canvas);

        if (mItemState == ITEM_STATE_DELETE) {
            left = mListSelectorRect.right - mIconSideLen * (pos + 1) - mIconFocusPadding;
            right = mListSelectorRect.right - mIconSideLen * pos + mIconFocusPadding;
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


    float down_x = 0;
    float down_y = 0;

    float up_x = 0;
    float up_y = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
//                log("onTouchEvent(MotionEvent ev)---MotionEvent.ACTION_DOWN");
//                float x = ev.getX();
//                float y = ev.getY();
//                log("onTouchEvent(MotionEvent ev)---position-down位置--x: " + x + ", y:" + y);

                log("--MotionEvent.ACTION_DOWN--");
                down_x = ev.getX();
                down_y = ev.getY();
                if (isFocused()) {
                    if (TouchEventManager.isTouchCommon(topTouchPostionParam, ev)) {
                        log("\n----选择了----topButton置顶按钮--\n");

                        if (mItemState != ITEM_STATE_TOP) {
                            mItemState = ITEM_STATE_TOP;
                            invalidate();
                            return true;
                        }
                    } else if (TouchEventManager.isTouchCommon(favoriteTouchPostionParam, ev)) {
                        log("\n----选择了----favoriteButton按钮\n");
                        if (mItemState != ITEM_STATE_FAVORITE) {
                            mItemState = ITEM_STATE_FAVORITE;
                            invalidate();
                            return true;
                        }
                    } else if (TouchEventManager.isTouchCommon(delTouchPostionParam, ev)) {
                        if (mItemState != ITEM_STATE_DELETE) {
                            mItemState = ITEM_STATE_DELETE;
                            invalidate();
                            return true;
                        }
                    } else if (TouchEventManager.isTouchCommon(cutTouchPostionParam, ev)) {
                        if (mItemState != ITEM_STATE_CUT_SONG) {
                            mItemState = ITEM_STATE_CUT_SONG;
                            invalidate();
                            return true;
                        }
                    } else {
                        if (mItemState != ITEM_STATE_NORMAL) {
                            mItemState = ITEM_STATE_NORMAL;
                            invalidate();
                            return true;
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
//                log("onTouchEvent(MotionEvent ev)---MotionEvent.ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
//                log("onTouchEvent(MotionEvent ev)---MotionEvent.ACTION_UP");
                up_x = ev.getX();
                up_y = ev.getY();
                float temp_x = up_x - down_x;
                float temp_y = up_y - down_y;
                log("位置移动距离temp_x:" + temp_x + ", temp_y:" + temp_y);
                if (Math.abs(temp_x) > 10 || Math.abs(temp_y) > 10) {
                    mItemState = ITEM_STATE_NORMAL;
                    invalidate();
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
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
                    return super.onKeyDown(keyCode, event);
                } else if (selectedPos == 1) {
                    if (mItemState == ITEM_STATE_FAVORITE) {
                        mItemState = ITEM_STATE_CUT_SONG;
                    }
                    if (mItemState == ITEM_STATE_DELETE) {
                        mItemState = ITEM_STATE_FAVORITE;
                    }
                } else if (selectedPos == 2) {
                    if (mItemState == ITEM_STATE_FAVORITE || mItemState == ITEM_STATE_TOP) {
                        mItemState = ITEM_STATE_FAVORITE;
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (selectedPos == getAdapter().getCount() - 1) {
                    return super.onKeyDown(keyCode, event);
                } else if (mItemState == ITEM_STATE_CUT_SONG) {
                    mItemState = ITEM_STATE_FAVORITE;
                } else if (selectedPos == 1) {
                    if (mItemState == ITEM_STATE_FAVORITE) {
                        mItemState = ITEM_STATE_TOP;
                    } else if (mItemState == ITEM_STATE_DELETE) {
                        mItemState = ITEM_STATE_FAVORITE;
                    }
                }
                break;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * [处理按键右移]
     *
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
                }
                break;
            case 1:
                if (mItemState == ITEM_STATE_FAVORITE) {
                    mItemState = ITEM_STATE_DELETE;
                    invalidate();
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
                }
                break;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        log("--onItemClick()--");
        this.positionSelect = position;
        if (mOnItemClickCallback != null) {
            if (getCount() == 1 && mItemState == ITEM_STATE_DELETE) {
                mItemState = ITEM_STATE_CUT_SONG;
            }
            mOnItemClickCallback.onItemClick(parent, view, position, id, mItemState);
        }
    }

    /**
     * [设置项点击回调]
     *
     * @param onItemClickCallback 回调函数
     */
    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        mOnItemClickCallback = onItemClickCallback;
    }

    /**
     * [重置mItemState使selector位于切歌图标处]
     */
    public void resetItemState() {
        mItemState = ITEM_STATE_NORMAL;
    }

    private void log(String msg) {
        Log.d("gsp", TAG + ">>>" + msg);
    }
}
