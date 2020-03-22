/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年8月16日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.usercenter;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.evideo.kmbox.R;
import com.evideo.kmbox.widget.common.OnItemClickCallback;
import com.evideo.kmbox.widget.common.OnSongListKeyDownEventListener;
import com.evideo.kmbox.widget.common.OrderSongAnimController;
import com.evideo.kmbox.widget.common.OrderSongAnimView;

/**
 * [功能说明]
 */
public class UserCenterSungListView extends ListView implements android.widget.AdapterView.OnItemClickListener {
    
    /** [点歌状态] */
    public static final int ITEM_STATE_NORMAL = 1;
    /** [顶歌状态] */
    public static final int ITEM_STATE_TOP = 2;
    /** [删除状态] */
    public static final int ITEM_STATE_DELETE = 3;
    
    private int mItemState = ITEM_STATE_NORMAL;
    
    private Drawable mTopIcon;
//    private Drawable mUploadIcon;
    private Drawable mDeleteIcon;
    private Drawable mFocusFrame;
    private int mIconSideLen;
    private int mIconIntrSideLen;
    private int mIconFocusPadding;
    private int mFadingEdgeLength;
    
    private int mFromView = OrderSongAnimView.FROM_VIEW_SUNG_LIST;
    
    /** [歌曲序号、歌名、歌手名的焦点框微调] */
    private int mTvLayoutPaddingLeft;
    private int mTvLayoutPaddingRight;
    private int mTvLayoutPaddingTop;
    private int mTvLayoutPaddingBottom;
    
    private int mClickItemPos = -1;
    
    public void resetState() {
        mItemState = ITEM_STATE_NORMAL;
    }
    
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
    
    public UserCenterSungListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public UserCenterSungListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UserCenterSungListView(Context context) {
        super(context);
        init(context);
    }
    
    private void init(Context context) {
        reflectSelectorRect();
        mFadingEdgeLength = getResources().getDimensionPixelSize(R.dimen.px90);
        mTopIcon = getResources().getDrawable(R.drawable.ic_top_song);
//        mUploadIcon = getResources().getDrawable(R.drawable.ic_upload);
        mDeleteIcon = getResources().getDrawable(R.drawable.ic_delete);
        mFocusFrame = getResources().getDrawable(R.drawable.focus_frame_new);
        
        mIconSideLen = getResources().getDimensionPixelSize(R.dimen.px99);
        mIconIntrSideLen = mTopIcon.getIntrinsicWidth();
        mIconFocusPadding = getResources().getDimensionPixelSize(R.dimen.px15);
        
        mTvLayoutPaddingLeft = getResources().getDimensionPixelSize( R.dimen.px3);
        mTvLayoutPaddingRight = getResources().getDimensionPixelSize( R.dimen.px6);
        mTvLayoutPaddingTop = getResources().getDimensionPixelSize( R.dimen.px15);
        mTvLayoutPaddingBottom = getResources().getDimensionPixelSize( R.dimen.px15);
        
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
    
    private int mIconNum = 2;
    
    private void drawListSelector(Canvas canvas) {
        if (!mListSelectorRect.isEmpty()) {
            drawNormalSelector(canvas);
            drawTopIcon(canvas);
            drawDeleteSelector(canvas);
            /*
            SungListItem item = (SungListItem)getSelectedItem();
            if (item == null || TextUtils.isEmpty(item.getShareCode())) {
                mIconNum = 1;
                drawFirstDelSelector(canvas);
            } else {
                mIconNum = 3;
                drawReplayIcon(canvas);
                drawUploadIcon(canvas);
                drawDeleteSelector(canvas);
            }*/
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
    
    private void drawNormalSelector(Canvas canvas) {
        if (mItemState == ITEM_STATE_NORMAL) {
            final Drawable selector = mFocusFrame;
            selector.setBounds(mListSelectorRect.left - mTvLayoutPaddingLeft,
                    mListSelectorRect.top - mTvLayoutPaddingTop,
                    mListSelectorRect.right - mIconSideLen * mIconNum - mTvLayoutPaddingRight,
                    mListSelectorRect.bottom + mTvLayoutPaddingBottom);
            selector.draw(canvas);
        }
    }
    
    private void drawDeleteSelector(Canvas canvas) {
        if (!isFocused()) {
            return;
        }
        int left = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2;
        int right = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2;
        int top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
        int bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;     
        final Drawable favoriteIcon = mDeleteIcon;
        favoriteIcon.setBounds(left, top, right, bottom);
        favoriteIcon.draw(canvas);
        
        if (mItemState == ITEM_STATE_DELETE) {
            left = mListSelectorRect.right - mIconSideLen - mIconFocusPadding;
            right = mListSelectorRect.right + mIconFocusPadding;
            top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
            bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
            final Drawable facoriteIconFocusFrame = mFocusFrame;
            facoriteIconFocusFrame.setBounds(left, top, right, bottom);
            facoriteIconFocusFrame.draw(canvas);
        }        
    }
    public void drawTopIcon(Canvas canvas) {
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
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        EvLog.d("SongListView onKeyDown keyCode: " + keyCode);
        
        if (getAdapter().getCount() <= 0) {
            return super.onKeyDown(keyCode, event);
        }
        
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mIconNum == 1) {
                    if (mItemState == ITEM_STATE_NORMAL) {
                        mItemState = ITEM_STATE_DELETE;
                        invalidate();
                        return true;
                    } else if (mItemState == ITEM_STATE_DELETE) {
                        if (mOnSongListKeyDownEventListener != null) {
                            mOnSongListKeyDownEventListener.onRightEdgeKeyDown();
                        }
                        return true;
                    }
                } else {
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
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mIconNum == 1) {
                    if (mItemState == ITEM_STATE_DELETE) {
                        mItemState = ITEM_STATE_NORMAL;
                        invalidate();
                        return true;
                    } else if (mItemState == ITEM_STATE_NORMAL) {
                        if (mOnSongListKeyDownEventListener != null) {
                            mOnSongListKeyDownEventListener.onLeftEdgeKeyDown();
                        }
                        return true;
                    }
                } else {
                    if (mItemState == ITEM_STATE_DELETE) {
                        mItemState = ITEM_STATE_TOP;
                        invalidate();
                        return true;
                   } else if (mItemState == ITEM_STATE_TOP) {
                        mItemState = ITEM_STATE_NORMAL;
                        invalidate();
                        return true;
                    } else if (mItemState == ITEM_STATE_NORMAL) {
                        if (mOnSongListKeyDownEventListener != null) {
                            mOnSongListKeyDownEventListener.onLeftEdgeKeyDown();
                        }
                        return true;
                    }
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
        
       /* if (mNeedShowOrderSongAnim 
                &&  (mItemState == ITEM_STATE_NORMAL || mItemState == ITEM_STATE_SHARE)) {
            startOrderSongAnimDelayed(position);
        }*/
        mClickItemPos = position;
            
        if (mOnItemClickCallback != null) {
            mOnItemClickCallback.onItemClick(parent, view, position, id, mItemState);
            /*if (mItemState == ITEM_STATE_DELETE) {
                mItemState = ITEM_STATE_NORMAL;
            }*/
        }
    }
    
    public void startOrderSongAnimDelayed() {
//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
//            }
//        }, 200);
//        startOrderSongAnim(mClickItemPos);
    }
    
    private void startOrderSongAnim(int position) {
//        EvLog.d("startOrderSongAnim position " + position);
        int[] location = new int[2];
        getLocationOnScreen(location);
        View itemView = getChildAt(position - getFirstVisiblePosition());
        itemView.destroyDrawingCache();
        itemView.setDrawingCacheEnabled(true);
        Bitmap srcBitmap = itemView.getDrawingCache();
        Bitmap resultBitmap = Bitmap.createBitmap(mListSelectorRect.right - mListSelectorRect.left - mIconSideLen * 2 
                , mListSelectorRect.bottom - mListSelectorRect.top, Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(srcBitmap, 0, 0, null);
//        mFocusFrame.setBounds(0, 0, mSelectorRect.right - mSelectorRect.left - mIconSideLen, mSelectorRect.bottom - mSelectorRect.top);
//        mFocusFrame.draw(canvas);
        OrderSongAnimController.getInstance().startOrderSongAnim(
                resultBitmap, location[0] + mListSelectorRect.left, location[1] + mListSelectorRect.top, mFromView);
        srcBitmap.recycle();
        itemView.setDrawingCacheEnabled(false);
    }
    
    /**
     * [设置点击回调]
     * @param onItemClickCallback 回调函数
     */
    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        mOnItemClickCallback = onItemClickCallback;
    } 
}
