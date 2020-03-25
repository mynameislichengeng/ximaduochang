package com.evideo.kmbox.widget.common;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.evideo.kmbox.R;
import com.evideo.kmbox.data.TouchEventManager;
import com.evideo.kmbox.model.touch.TouchPostionParam;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.mainmenu.order.BindCodeQrView;

/**
 * [点歌列表ListView]
 */
public class SongListView extends ListView implements android.widget.AdapterView.OnItemClickListener {

    private final String TAG = SongListView.class.getName();
    /**
     * 长按键处理
     */
    private static final int MAX_REPEAT_EVENT_COUNT = 8;

    /**
     * [点歌状态]
     */
    public static final int ITEM_STATE_NORMAL = 1;
    /**
     * [顶歌状态]
     */
    public static final int ITEM_STATE_TOP = 2;
    /**
     * [收藏状态]
     */
    public static final int ITEM_STATE_FAVORITE = 3;
    private int mFadingEdgeLength;

    private int mItemState = ITEM_STATE_NORMAL;

    private int favoriteLeft;

    private Drawable mTopIcon;
    private Drawable mFavoriteIcon;
    private Drawable mFocusFrame;
    private int mIconSideLen;
    private int mIconIntrSideLen;
    private int mIconFocusPadding;
    private int mFromView = OrderSongAnimView.FROM_VIEW_MAIN_MENU;

    private View mFootLoadingView;
    private View mFootCompleteView;
    private TextView mFootLoadingTv;
    private TextView mFootQrHintTv;
    private BindCodeQrView mFootQrView;
    private View mFootLoadingPb;
    /**
     * [歌曲序号、歌名、歌手名的焦点框微调]
     */
    private int mTvLayoutPaddingLeft;
    private int mTvLayoutPaddingRight;
    private int mTvLayoutPaddingTop;
    private int mTvLayoutPaddingBottom;

    private int mClickItemPos = -1;

    private TouchPostionParam topTouchPostionParam;//置顶按钮的位置
    private TouchPostionParam favoriteTouchPostionParam;//搜查按钮位置


    public void resetState() {
        mItemState = ITEM_STATE_NORMAL;
    }

    public void resetUi() {
        mItemState = ITEM_STATE_NORMAL;
//        mFavoriteIcon = getResources().getDrawable(R.drawable.ic_favorite);
        invalidate();
    }

    /**
     * [设置点歌视图]
     *
     * @param fromView 点歌视图
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

    public SongListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SongListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SongListView(Context context) {
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

        addFooterView(mFootLoadingView, null, false);


    }

    /**
     * [删除底部加载界面]
     */
    public void removeFootLoadingView() {
//        EvLog.d("----removeFootLoadingView----");
        removeFooterView(mFootLoadingView);
    }

    /**
     * [显示底部加载界面]
     */
    public void showFootLoadingView() {
//        EvLog.d("----showFootLoadingView----");
        mFootLoadingPb.setVisibility(View.VISIBLE);
        mFootLoadingTv.setText(R.string.loading_song);
        removeFooterView(mFootLoadingView);
        addFooterView(mFootLoadingView, null, false);
    }

    /**
     * [显示底部提示界面]
     *
     * @param resid 文字资源id
     */
    public void showFootView(int resid) {
//        EvLog.d("----showFootView----");
        mFootQrView.updateQr();
        mFootQrHintTv.setText(resid);
        removeFooterView(mFootLoadingView);
        removeFooterView(mFootCompleteView);
        addFooterView(mFootCompleteView, null, false);
    }

    /**
     * {@inheritDoc}
     * 焦点上画标
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
            drawNormalSelector(canvas);
            drawFavoriteIcon(canvas);
            drawSelectorIcon(canvas);
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

    private void drawFavoriteIcon(Canvas canvas) {
        if (!isFocused()) {
            return;
        }
        int moveLeft = mIconSideLen + mIconIntrSideLen + mIconSideLen;
        int left = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2;
        left = left - moveLeft;
        int right = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2;
        right = right - moveLeft;
        int top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
        int bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;
        // 自己注释掉

        final Drawable favoriteIcon = mFavoriteIcon;
        favoriteIcon.setBounds(left, top, right, bottom);
        favoriteIcon.draw(canvas);

        favoriteTouchPostionParam = new TouchPostionParam();
        favoriteTouchPostionParam.setLeft(left);
        favoriteTouchPostionParam.setRight(right);
        favoriteTouchPostionParam.setUp(top);
        favoriteTouchPostionParam.setDown(bottom);

        if (mItemState == ITEM_STATE_FAVORITE) {
            left = mListSelectorRect.right - mIconSideLen - mIconFocusPadding;
            left = left - moveLeft;
            right = mListSelectorRect.right + mIconFocusPadding;
            right = right - moveLeft;
            top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
            bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
            final Drawable facoriteIconFocusFrame = mFocusFrame;
            facoriteIconFocusFrame.setBounds(left, top, right, bottom);
            facoriteIconFocusFrame.draw(canvas);
        }
    }


    /**
     * [描绘顶歌图标]
     *
     * @param canvas 画布
     */
    public void drawSelectorIcon(Canvas canvas) {
        if (!isFocused()) {
            return;
        }
        int moveLeft = (mIconSideLen + mIconIntrSideLen) + mIconSideLen;
        int left = mListSelectorRect.right - (mIconSideLen + mIconIntrSideLen) / 2 - mIconSideLen;
        left = left - moveLeft;
        int right = mListSelectorRect.right - (mIconSideLen - mIconIntrSideLen) / 2 - mIconSideLen;
        right = right - moveLeft;
        int top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconIntrSideLen) / 2;
        int bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconIntrSideLen) / 2;
        final Drawable topIcon = mTopIcon;
        topIcon.setBounds(left, top, right, bottom);
        topIcon.draw(canvas);

        topTouchPostionParam = new TouchPostionParam();
        topTouchPostionParam.setLeft(left);
        topTouchPostionParam.setRight(right);
        topTouchPostionParam.setUp(top);
        topTouchPostionParam.setDown(bottom);

//        log("top-position置顶按钮位置: left: " + left + ", top: " + top + ", right: " + right + ", bottom: " + bottom);

        if (mItemState == ITEM_STATE_TOP) {
            left = mListSelectorRect.right - mIconSideLen * 2 - mIconFocusPadding;
            left = left - moveLeft;
            right = mListSelectorRect.right - mIconSideLen + mIconFocusPadding;
            right = right - moveLeft;
            top = (mListSelectorRect.top + mListSelectorRect.bottom - mIconSideLen) / 2 - mIconFocusPadding;
            bottom = (mListSelectorRect.top + mListSelectorRect.bottom + mIconSideLen) / 2 + mIconFocusPadding;
            final Drawable topIconFocusFrame = mFocusFrame;
            topIconFocusFrame.setBounds(left, top, right, bottom);
            topIconFocusFrame.draw(canvas);
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
//                log("onTouchEvent(MotionEvent ev)---MotionEvent.ACTION_DOWN");
//                float x = ev.getX();
//                float y = ev.getY();
//                log("onTouchEvent(MotionEvent ev)---position-down位置--x: " + x + ", y:" + y);
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

    //  自己写的  先注释掉

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        if (selectorLeft == 0 || ev.getY() < mListSelectorRect.top || ev.getY() > mListSelectorRect.bottom) {
//            return super.onTouchEvent(ev);
//        }
//        if (ev.getX() < selectorLeft) {
//            mItemState = ITEM_STATE_NORMAL;
//        } else if (ev.getX() < favoriteLeft) {
//            mItemState = ITEM_STATE_TOP;
//        } else {
//            mItemState = ITEM_STATE_FAVORITE;
//        }
//        return super.onTouchEvent(ev);
//
//    }
//

    /**
     * [高亮显示收藏图标]
     */
    public void highlightFavoriteIcon() {
        mFavoriteIcon = getResources().getDrawable(R.drawable.ic_favorite_hl);
        Log.i("gsp", TAG + ">>highlightFavoriteIcon()");
        invalidate();
    }

    /**
     * [恢复默认显示收藏图标]
     */
    public void restoreFavoriteIcon() {
        mFavoriteIcon = getResources().getDrawable(R.drawable.ic_favorite);
        Log.i("gsp", TAG + ">>restoreFavoriteIcon()");
        invalidate();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        EvLog.d("SongListView onKeyDown keyCode: " + keyCode);
        Log.i("gsp", TAG + ">>onKeyDown():" + keyCode);
        if (getAdapter().getCount() <= 0) {
            return super.onKeyDown(keyCode, event);
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                Log.i("gsp", TAG + ">>KEYCODE_DPAD_RIGHT");
                Log.i("gsp", "onKeyDown: 显示最后边右边的坐标");
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
                Log.i("gsp", TAG + ">>KEYCODE_DPAD_LEFT");
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
                Log.i("gsp", TAG + ">>KEYCODE_DPAD_UP");
                if (event.getRepeatCount() > MAX_REPEAT_EVENT_COUNT) {
                    // EvLog.i("reject getRepeatCount " + MAX_REPEAT_EVENT_COUNT);
                    return true;
                }
                if (getSelectedItemPosition() == 0) {
                    if (mOnSongListKeyDownEventListener != null) {
                        mOnSongListKeyDownEventListener.onUpEdgeKeyDown();
                        return true;
                    }
                }
                if (mItemState == ITEM_STATE_TOP || mItemState == ITEM_STATE_FAVORITE) {
                    mItemState = ITEM_STATE_NORMAL;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                Log.i("gsp", TAG + ">>KEYCODE_DPAD_DOWN");
                if (event.getRepeatCount() > MAX_REPEAT_EVENT_COUNT) {
                    // EvLog.i("reject getRepeatCount " + MAX_REPEAT_EVENT_COUNT);
                    return true;
                }
//                EvLog.i("getSelectedItemPosition:" +  getSelectedItemPosition() + ",getCount:" + getAdapter().getCount());
                if (getSelectedItemPosition() == getAdapter().getCount() - 1) {
                    if (mOnSongListKeyDownEventListener != null) {
                        mOnSongListKeyDownEventListener.onDownEdgeKeyDown();
                        return true;
                    }
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
                &&  (mItemState == ITEM_STATE_NORMAL || mItemState == ITEM_STATE_TOP)) {
            startOrderSongAnimDelayed(position);
        }*/
        mClickItemPos = position;

        if (mOnItemClickCallback != null) {
            mOnItemClickCallback.onItemClick(parent, view, position, id, mItemState);
        }
    }


    public void startOrderSongAnimDelayed(/*final int position*/) {
//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
//            }
//        }, 200);
//        startOrderSongAnim(mClickItemPos);
    }

    /**
     * [设置点击回调]
     *
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


    private void log(String tag) {
        Log.d("gsp", TAG + " >>" + tag);
    }
}
