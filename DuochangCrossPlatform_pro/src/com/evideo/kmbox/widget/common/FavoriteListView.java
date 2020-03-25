/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date                 Author             Version  Description
 *  -----------------------------------------------
 *  2015年3月16日        "wurongquan"            1.0	      [修订说明]
 *
 */

package com.evideo.kmbox.widget.common;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.evideo.kmbox.R;
import com.evideo.kmbox.data.TouchEventManager;
import com.evideo.kmbox.model.touch.TouchPostionParam;

/**
 * [功能说明]
 */
public class FavoriteListView extends ListView implements android.widget.AdapterView.OnItemClickListener {

    private final String TAG = FavoriteListView.class.getSimpleName();
    public static final int ITEM_STATE_NORMAL = 1;
    public static final int ITEM_STATE_TOP = 2;
    public static final int ITEM_STATE_DELETE = 3;
    public int mFadingEdgeLength;

    private int mItemState = ITEM_STATE_NORMAL;

    private Drawable mTopIcon;
    private Drawable mFavoriteIcon;
    private Drawable mFocusFrame;
    private int mIconSideLen;
    private int mIconIntrSideLen;
    private int mIconFocusPadding;
    private int mFromView = OrderSongAnimView.FROM_VIEW_MAIN_MENU;

    /**
     * [歌曲序号、歌名、歌手名的焦点框微调]
     */
    private int mTvLayoutPaddingLeft;
    private int mTvLayoutPaddingRight;
    private int mTvLayoutPaddingTop;
    private int mTvLayoutPaddingBottom;
    private int mClickItemPos = -1;

    private TouchPostionParam topTouchPostionParam;//置顶按钮的位置
    private TouchPostionParam delTouchPositionParam;

    public int getmClickItemPos() {
        return mClickItemPos;
    }

    public void setmClickItemPos(int mClickItemPos) {
        this.mClickItemPos = mClickItemPos;
    }

    public void resetState() {
        mItemState = ITEM_STATE_NORMAL;
    }

    /**
     * [执行点歌视图]
     *
     * @param fromView 来源视图
     */
    public void setOrderFromView(int fromView) {
        this.mFromView = fromView;
    }

    private OnItemClickCallback mOnItemClickCallback;
    private OnSongListKeyDownEventListener mOnSongListKeyDownEventListener;

    /**
     * [列表按键监听]
     *
     * @param onSongListKeyDownEventListener 按键监听器
     */
    public void setOnSongListKeyDownEventListener(OnSongListKeyDownEventListener onSongListKeyDownEventListener) {
        mOnSongListKeyDownEventListener = onSongListKeyDownEventListener;
    }

    public FavoriteListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public FavoriteListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FavoriteListView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        reflectSelectorRect();
        mFadingEdgeLength = getResources().getDimensionPixelSize(R.dimen.px90);
        mTopIcon = getResources().getDrawable(R.drawable.ic_top_song);
        mFavoriteIcon = getResources().getDrawable(R.drawable.ic_delete);
        mFocusFrame = getResources().getDrawable(R.drawable.focus_frame_new);
        mIconSideLen = getResources().getDimensionPixelSize(R.dimen.px99);
        mIconFocusPadding = getResources().getDimensionPixelSize(R.dimen.px15);
        mIconIntrSideLen = mTopIcon.getIntrinsicWidth();

        mTvLayoutPaddingLeft = getResources().getDimensionPixelSize(R.dimen.px3);
        mTvLayoutPaddingRight = getResources().getDimensionPixelSize(R.dimen.px6);
        mTvLayoutPaddingTop = getResources().getDimensionPixelSize(R.dimen.px15);
        mTvLayoutPaddingBottom = getResources().getDimensionPixelSize(R.dimen.px15);

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
        }
    }

    private void drawListSelector(Canvas canvas) {
        if (!mListSelectorRect.isEmpty()) {
            drawNormalSelector(canvas);
            drawSelectorIcon(canvas);
            drawDeleteIcon(canvas);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isFocused()) {

            int count = this.getAdapter().getCount();

            if (count > getmClickItemPos()) {
                if (getmClickItemPos() != -1) {
                    drawListSelector(canvas);
                }
            }

        }
    }

    /**
     * [画点歌图标]
     *
     * @param canvas 画布
     */
    private void drawNormalSelector(Canvas canvas) {
//        if (mItemState == ITEM_STATE_NORMAL) {
        final Drawable selector = mFocusFrame;
        selector.setBounds(mListSelectorRect.left - mTvLayoutPaddingLeft,
                mListSelectorRect.top - mTvLayoutPaddingTop,
                mListSelectorRect.right - mIconSideLen * 2 - mTvLayoutPaddingRight,
                mListSelectorRect.bottom + mTvLayoutPaddingBottom);
        selector.draw(canvas);
//        }
    }

    /**
     * [画顶歌图标]
     *
     * @param canvas 画布
     */
    private void drawSelectorIcon(Canvas canvas) {
        if (!isFocused()) {
            return;
        }
        int moveLef = (int) (mIconSideLen + mIconSideLen + mIconSideLen * 0.5);
        int left = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2 - mIconSideLen;
        left = left - moveLef;
        int right = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2 - mIconSideLen;
        right = right - moveLef;
        int top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
        int bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;
        if (topTouchPostionParam == null) {
            topTouchPostionParam = new TouchPostionParam();
        }
        topTouchPostionParam.setLeft(left);
        topTouchPostionParam.setRight(right);
        topTouchPostionParam.setUp(top);
        topTouchPostionParam.setDown(bottom);

        final Drawable topIcon = mTopIcon;
        topIcon.setBounds(left, top, right, bottom);
        topIcon.draw(canvas);
        if (mItemState == ITEM_STATE_TOP) {
            left = mListSelectorRect.right - mIconSideLen * 2 - mIconFocusPadding;
            left = left - moveLef;
            right = mListSelectorRect.right - mIconSideLen + mIconFocusPadding;
            right = right - moveLef;
            top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
            bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
            final Drawable topIconFocusFrame = mFocusFrame;
            topIconFocusFrame.setBounds(left, top, right, bottom);
            topIconFocusFrame.draw(canvas);
        }
    }

    /**
     * [画删除图标]
     *
     * @param canvas 画布
     */
    public void drawDeleteIcon(Canvas canvas) {
        if (!isFocused()) {
            return;
        }
        int moveLef = (int) (mIconSideLen + mIconSideLen + mIconSideLen * 0.5);
        int left = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2;
        left = left - moveLef;
        int right = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2;
        right = right - moveLef;
        int top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
        int bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;

        if (delTouchPositionParam == null) {
            delTouchPositionParam = new TouchPostionParam();
        }
        delTouchPositionParam.setLeft(left);
        delTouchPositionParam.setRight(right);
        delTouchPositionParam.setUp(top);
        delTouchPositionParam.setDown(bottom);

        final Drawable favoriteIcon = mFavoriteIcon;
        favoriteIcon.setBounds(left, top, right, bottom);
        favoriteIcon.draw(canvas);

        if (mItemState == ITEM_STATE_DELETE) {
            left = mListSelectorRect.right - mIconSideLen - mIconFocusPadding;
            left = left - moveLef;
            right = mListSelectorRect.right + mIconFocusPadding;
            right = right - moveLef;
            top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
            bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
            final Drawable facoriteIconFocusFrame = mFocusFrame;
            facoriteIconFocusFrame.setBounds(left, top, right, bottom);
            facoriteIconFocusFrame.draw(canvas);
        }
    }

    float down_x = 0;
    float down_y = 0;

    float up_x = 0;
    float up_y = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
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
                    } else if (TouchEventManager.isTouchCommon(delTouchPositionParam, ev)) {
                        log("\n----选择了----删除按钮\n");
                        if (mItemState != ITEM_STATE_DELETE) {
                            mItemState = ITEM_STATE_DELETE;
                            invalidate();
                            return true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                up_x = ev.getX();
                up_y = ev.getY();
                float temp_x = up_x - down_x;
                float temp_y = up_y - down_y;

                if (Math.abs(temp_x) > 10 || Math.abs(temp_y) > 10) {
                    mItemState = ITEM_STATE_NORMAL;
                    invalidate();
                    return true;
                }


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

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mItemState == ITEM_STATE_NORMAL) {
                    mItemState = ITEM_STATE_TOP;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_TOP) {
                    mItemState = ITEM_STATE_DELETE;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_DELETE) {
                    if (mOnSongListKeyDownEventListener != null) {
                        mOnSongListKeyDownEventListener.onRightEdgeKeyDown();
                    }
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mItemState == ITEM_STATE_TOP) {
                    mItemState = ITEM_STATE_NORMAL;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_DELETE) {
                    mItemState = ITEM_STATE_TOP;
                    invalidate();
                    return true;
                } else if (mItemState == ITEM_STATE_NORMAL) {
                    if (mOnSongListKeyDownEventListener != null) {
                        mOnSongListKeyDownEventListener.onLeftEdgeKeyDown();
                    }
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (getSelectedItemPosition() == 0) {
                    if (mOnSongListKeyDownEventListener != null) {
                        mOnSongListKeyDownEventListener.onUpEdgeKeyDown();
                    }
                    return true;
                }
                if (mItemState == ITEM_STATE_TOP || mItemState == ITEM_STATE_DELETE) {
                    mItemState = ITEM_STATE_NORMAL;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (getSelectedItemPosition() == getAdapter().getCount() - 1) {
                    if (mOnSongListKeyDownEventListener != null) {
                        mOnSongListKeyDownEventListener.onDownEdgeKeyDown();
                    }
                    return true;
                }
                if (mItemState == ITEM_STATE_TOP || mItemState == ITEM_STATE_DELETE) {
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
        /*
        if (mNeedShowOrderSongAnim 
                &&  (mItemState == ITEM_STATE_NORMAL || mItemState == ITEM_STATE_TOP)) {
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
     * [项目点击回调]
     *
     * @param onItemClickCallback 回调监听函数
     */
    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        mOnItemClickCallback = onItemClickCallback;
    }


    private void log(String tag) {
        Log.d("gsp", TAG + ">>>" + tag);
    }
}
