package com.evideo.kmbox.widget.mainview.selected;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.method.Touch;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.evideo.kmbox.data.TouchEventManager;
import com.evideo.kmbox.model.touch.TouchPostionParam;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.OnItemClickCallback;
import com.evideo.kmbox.widget.common.OnSongListKeyDownEventListener;
import com.evideo.kmbox.R;

/**
 * [已点列表ListView]
 */
public class SelectedListView extends ListView implements android.widget.AdapterView.OnItemClickListener {


    private final String TAG = SelectedListView.class.getSimpleName();

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

    private TouchPostionParam topTouchPostionParam;//置顶按钮的位置
    private TouchPostionParam favoriteTouchPostionParam;//搜查按钮位置
    private TouchPostionParam delTouchPositionParam;
    private TouchPostionParam cutTouchPositionParam;

    private int mTvLayoutPaddingLeft;
    private int mTvLayoutPaddingRight;
    private int mTvLayoutPaddingTop;
    private int mTvLayoutPaddingBottom;

    private int selectPositon = 0;

    public int getSelectPositon() {
        return selectPositon;
    }

    public void setSelectPositon(int selectPositon) {
        this.selectPositon = selectPositon;
    }

    public void resetState() {
        mItemState = ITEM_STATE_NORMAL;
    }


    /**
     * [设置按键监听]
     *
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

            int selectedPos = getSelectPositon();

            topTouchPostionParam = null;//置顶按钮的位置
            favoriteTouchPostionParam = null;//搜查按钮位置
            delTouchPositionParam = null;
            cutTouchPositionParam = null;
            int count = this.getAdapter().getCount();

            log("---当前还有的count:" + count + ", 位置position:" + selectedPos);
            if (count > selectedPos) {
                drawNormalSelector(canvas);
                if (selectedPos == 0) {
                    drawZeroPositionIcon(canvas);
                } else if (selectedPos == 1) {
                    drawFirstPostionIcon(canvas);
                } else if (selectedPos == -1) {

                } else {
                    drawOtherIcon(canvas);
                }
            }


        }
    }

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
     * 显示快切和喜爱
     *
     * @param canvas
     */
    private void drawZeroPositionIcon(Canvas canvas) {
        if (!isFocused()) {
            return;
        }

        // 切歌
        int moveLeft = mIconSideLen + mIconIntrSideLen + mIconSideLen;

        {
            int left = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2;
            left = left - moveLeft;
            int right = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2;
            right = right - moveLeft;
            int top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
            int bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;

            if (cutTouchPositionParam == null) {
                cutTouchPositionParam = new TouchPostionParam();

            }
            cutTouchPositionParam.setLeft(left);
            cutTouchPositionParam.setRight(right);
            cutTouchPositionParam.setUp(top);
            cutTouchPositionParam.setDown(bottom);
            //画切歌图标
            final Drawable cutSongIcon = mCutSongIcon;
            cutSongIcon.setBounds(left, top, right, bottom);
            cutSongIcon.draw(canvas);


            //
            if (mItemState == ITEM_STATE_CUT_SONG) {
                left = mListSelectorRect.right - mIconSideLen - mIconFocusPadding;
                left = left - moveLeft;
                right = mListSelectorRect.right + mIconFocusPadding;
                right = right - moveLeft;
                top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
                bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;

                final Drawable topIconFocusFrame = mFocusFrame;
                topIconFocusFrame.setBounds(left, top, right, bottom);
                topIconFocusFrame.draw(canvas);
            }
        }


        //喜爱
        {
            int leftFavorite = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2 - mIconSideLen;
            leftFavorite = leftFavorite - moveLeft;
            int rightFavorite = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2 - mIconSideLen;
            rightFavorite = rightFavorite - moveLeft;
            int topFavorite = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
            int bottomFavorite = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;
            final Drawable favoriteIcon = mFavoriteIcon;
            favoriteIcon.setBounds(leftFavorite, topFavorite, rightFavorite, bottomFavorite);
            favoriteIcon.draw(canvas);

            if (favoriteTouchPostionParam == null) {
                favoriteTouchPostionParam = new TouchPostionParam();
            }
            favoriteTouchPostionParam.setLeft(leftFavorite);
            favoriteTouchPostionParam.setRight(rightFavorite);
            favoriteTouchPostionParam.setUp(topFavorite);
            favoriteTouchPostionParam.setDown(bottomFavorite);

            if (mItemState == ITEM_STATE_FAVORITE) {
                leftFavorite = mListSelectorRect.right - mIconSideLen * 2 - mIconFocusPadding;
                leftFavorite = leftFavorite - moveLeft;
                rightFavorite = mListSelectorRect.right - mIconSideLen + mIconFocusPadding;
                rightFavorite = rightFavorite - moveLeft;
                topFavorite = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
                bottomFavorite = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
                final Drawable facoriteIconFocusFrame = mFocusFrame;
                facoriteIconFocusFrame.setBounds(leftFavorite, topFavorite, rightFavorite, bottomFavorite);
                facoriteIconFocusFrame.draw(canvas);
            }
        }


    }


    private void drawFirstPostionIcon(Canvas canvas) {
        if (!isFocused()) {
            return;
        }
        int moveLeft = mIconSideLen + mIconIntrSideLen + mIconSideLen;


        {
            int leftDel = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2 - mIconSideLen;
            leftDel = leftDel - moveLeft;
            int rightDel = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2 - mIconSideLen;
            rightDel = rightDel - moveLeft;
            int topDel = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
            int bottomDel = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;

            if (delTouchPositionParam == null) {
                delTouchPositionParam = new TouchPostionParam();

            }
            delTouchPositionParam.setLeft(leftDel);
            delTouchPositionParam.setRight(rightDel);
            delTouchPositionParam.setUp(topDel);
            delTouchPositionParam.setDown(bottomDel);

            //画删除图标
            final Drawable favoriteIcon = mDeleteIcon;
            favoriteIcon.setBounds(leftDel, topDel, rightDel, bottomDel);
            favoriteIcon.draw(canvas);

            if (mItemState == ITEM_STATE_DELETE) {
                leftDel = mListSelectorRect.right - mIconSideLen * 2 - mIconFocusPadding;
                leftDel = leftDel - moveLeft;
                rightDel = mListSelectorRect.right - mIconSideLen + mIconFocusPadding;
                rightDel = rightDel - moveLeft;
                topDel = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
                bottomDel = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
                final Drawable topIconFocusFrame = mFocusFrame;
                topIconFocusFrame.setBounds(leftDel, topDel, rightDel, bottomDel);
                topIconFocusFrame.draw(canvas);
            }

        }

        {
            int leftFavorite = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2;
            leftFavorite = leftFavorite - moveLeft;
            int rightFavorite = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2;
            rightFavorite = rightFavorite - moveLeft;
            int topFavorite = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
            int bottomFavorite = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;

            if (favoriteTouchPostionParam == null) {
                favoriteTouchPostionParam = new TouchPostionParam();
            }
            favoriteTouchPostionParam.setLeft(leftFavorite);
            favoriteTouchPostionParam.setRight(rightFavorite);
            favoriteTouchPostionParam.setUp(topFavorite);
            favoriteTouchPostionParam.setDown(bottomFavorite);

            // 画收藏图标
            final Drawable deleteIcon = mFavoriteIcon;
            deleteIcon.setBounds(leftFavorite, topFavorite, rightFavorite, bottomFavorite);
            deleteIcon.draw(canvas);

            if (mItemState == ITEM_STATE_FAVORITE) {
                leftFavorite = mListSelectorRect.right - mIconSideLen - mIconFocusPadding;
                leftFavorite = leftFavorite - moveLeft;
                rightFavorite = mListSelectorRect.right + mIconFocusPadding;
                rightFavorite = rightFavorite - moveLeft;
                topFavorite = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
                bottomFavorite = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;

                final Drawable topIconFocusFrame = mFocusFrame;
                topIconFocusFrame.setBounds(leftFavorite, topFavorite, rightFavorite, bottomFavorite);
                topIconFocusFrame.draw(canvas);
            }

        }


    }


    private void drawOtherIcon(Canvas canvas) {
        if (!isFocused()) {
            return;
        }
        int moveLeft = mIconSideLen + mIconIntrSideLen + mIconSideLen;
        {
            //置顶
            int leftTop = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2;
            leftTop = leftTop - moveLeft;
            int rightTop = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2;
            rightTop = rightTop - moveLeft;
            int topTop = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
            int bottomTop = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;

            if (topTouchPostionParam == null) {
                topTouchPostionParam = new TouchPostionParam();
            }
            topTouchPostionParam.setLeft(leftTop);
            topTouchPostionParam.setRight(rightTop);
            topTouchPostionParam.setUp(topTop);
            topTouchPostionParam.setDown(bottomTop);

            final Drawable topIcon = mTopIcon;
            topIcon.setBounds(leftTop, topTop, rightTop, bottomTop);
            topIcon.draw(canvas);


            if (mItemState == ITEM_STATE_TOP) {
                leftTop = mListSelectorRect.right - mIconSideLen - mIconFocusPadding;
                leftTop = leftTop - moveLeft;
                rightTop = mListSelectorRect.right + mIconFocusPadding;
                rightTop = rightTop - moveLeft;
                topTop = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
                bottomTop = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
                final Drawable topIconFocusFrame = mFocusFrame;
                topIconFocusFrame.setBounds(leftTop, topTop, rightTop, bottomTop);
                topIconFocusFrame.draw(canvas);
            }
        }
        {
            //喜爱
            int leftFavorite = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2 - mIconSideLen;
            leftFavorite = leftFavorite - moveLeft;
            int rightFavorite = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2 - mIconSideLen;
            rightFavorite = rightFavorite - moveLeft;
            int topFavorite = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
            int bottomFavorite = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;

            if (favoriteTouchPostionParam == null) {
                favoriteTouchPostionParam = new TouchPostionParam();
            }
            favoriteTouchPostionParam.setLeft(leftFavorite);
            favoriteTouchPostionParam.setRight(rightFavorite);
            favoriteTouchPostionParam.setUp(topFavorite);
            favoriteTouchPostionParam.setDown(bottomFavorite);


            final Drawable favoriteIcon = mFavoriteIcon;
            favoriteIcon.setBounds(leftFavorite, topFavorite, rightFavorite, bottomFavorite);
            favoriteIcon.draw(canvas);

            if (mItemState == ITEM_STATE_FAVORITE) {
                leftFavorite = mListSelectorRect.right - mIconSideLen * 2 - mIconFocusPadding;
                leftFavorite = leftFavorite - moveLeft;
                rightFavorite = mListSelectorRect.right - mIconSideLen + mIconFocusPadding;
                rightFavorite = rightFavorite - moveLeft;
                topFavorite = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
                bottomFavorite = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
                final Drawable facoriteIconFocusFrame = mFocusFrame;
                facoriteIconFocusFrame.setBounds(leftFavorite, topFavorite, rightFavorite, bottomFavorite);
                facoriteIconFocusFrame.draw(canvas);
            }
        }
        {
            //删除
//            moveLeft = (int) (moveLeft - (0.2 * moveLeft));
            int leftDel = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2 - mIconSideLen * 2;
            leftDel = leftDel - moveLeft;
            int rightDel = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2 - mIconSideLen * 2;
            rightDel = rightDel - moveLeft;
            int topDel = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
            int bottomDel = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;

            if (delTouchPositionParam == null) {
                delTouchPositionParam = new TouchPostionParam();

            }
            log("leftDel:" + leftDel + ",rightDel:" + rightDel);
            delTouchPositionParam.setLeft(leftDel);
            delTouchPositionParam.setRight(rightDel);
            delTouchPositionParam.setUp(topDel);
            delTouchPositionParam.setDown(bottomDel);

            final Drawable deleteIcon = mDeleteIcon;
            deleteIcon.setBounds(leftDel, topDel, rightDel, bottomDel);
            deleteIcon.draw(canvas);

            if (mItemState == ITEM_STATE_DELETE) {
                leftDel = mListSelectorRect.right - mIconSideLen * 3 - mIconFocusPadding;
                leftDel = leftDel - moveLeft;
                rightDel = mListSelectorRect.right - mIconSideLen * 2 + mIconFocusPadding;
                rightDel = rightDel - moveLeft;
                topDel = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
                bottomDel = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
                final Drawable topIconFocusFrame = mFocusFrame;
                topIconFocusFrame.setBounds(leftDel, topDel, rightDel, bottomDel);
                topIconFocusFrame.draw(canvas);
            }

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
                } else if (selectedPos == 2) {
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

    float down_x = 0;
    float down_y = 0;

    float up_x = 0;
    float up_y = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                log("----MotionEvent.ACTION_DOWN---");
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
                    } else if (TouchEventManager.isTouchCommon(cutTouchPositionParam, ev)) {
                        log("\n----选择了----切歌按钮\n");
                        if (mItemState != ITEM_STATE_CUT_SONG) {
                            mItemState = ITEM_STATE_CUT_SONG;
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
                log("----MotionEvent.ACTION_MOVE---");


                break;
            case MotionEvent.ACTION_UP:
                log("----MotionEvent.ACTION_UP---");
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
        setSelectPositon(position);
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
     * [重置mItemState使selector位于收藏图标处]
     */
    public void resetItemState() {
        mItemState = ITEM_STATE_FAVORITE;
    }

    private void log(String tag) {
        Log.d("gsp1", TAG + ">>>" + tag);
    }


}
