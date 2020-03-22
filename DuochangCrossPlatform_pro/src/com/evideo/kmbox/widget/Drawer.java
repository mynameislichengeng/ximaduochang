package com.evideo.kmbox.widget;

import com.evideo.kmbox.R;
import com.evideo.kmbox.model.observer.drawer.DrawerStateSubject;
import com.evideo.kmbox.util.EvLog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
/**
 * 抽屉控件
 * [具有从左到右打开和从右到左关闭功能的抽屉控件]
 */
public class Drawer extends FrameLayout {
    
    private static final boolean DEBUG = false;
    
    /** [状态标志--打开前] */
    private static final int STATE_PRE_OPEN = 1;
    /** [状态标志--打开动作中] */
    private static final int STATE_OPENING = 2;
    /** [状态标志--完全打开] */
    private static final int STATE_OPENED = 3;
    /** [状态标志--关闭动作中] */
    private static final int STATE_CLOSING = 4;
    /** [状态标志--完全关闭] */
    private static final int STATE_CLOSED = 5;
    
    /** [抽屉状态] */
    private int mState = STATE_CLOSED;
    
    private int mOffset = 0;
    private static final int MOVE_COUNT = 10;
    private static final int MOVE_DELAY_TIME = 20;
    private int mMinOffset;
    private View mContentView;
    private View mNextView;
    
    /** [是否需要通知上一级菜单关闭] */
    private boolean mNeedNotifyClose = false;
    
    /**
     * [是否要通知上一级菜单关闭]
     * @return true 需要  false 不需要
     */
    public boolean isNeedNotifyClose() {
        return mNeedNotifyClose;
    }

    /**
     * [设置是否要通知上一级菜单关闭]
     * @param needNotifyClose 是否要通知上一级菜单关闭
     */
    public void setNeedNotifyClose(boolean needNotifyClose) {
        this.mNeedNotifyClose = needNotifyClose;
    }
    
    /** [是否需要通知下一级菜单打开] */
    private boolean mNeedNotifyOpen = false;
    
    /**
     * [是否需要通知下一级菜单打开]
     * @return true 需要  false 不需要
     */
    public boolean isNeedNotifyOpen() {
        return mNeedNotifyOpen;
    }

    /**
     * [设置是否需要通知下一级菜单打开]
     * @param needNotifyOpen 是否需要通知下一级菜单打开
     */
    public void setNeedNotifyOpen(boolean needNotifyOpen) {
        this.mNeedNotifyOpen = needNotifyOpen;
    }

    private boolean mNeedOpenAfterClosed = false;
    
    /**
     * [关闭后是否需要打开]
     * @return true 需要  false 不需要
     */
    public boolean isNeedOpenAfterClosed() {
        return mNeedOpenAfterClosed;
    }

    /**
     * [设置关闭后是否需要打开]
     * @param needOpenAfterClosed 关闭后是否需要打开
     */
    public void setNeedOpenAfterClosed(boolean needOpenAfterClosed) {
        this.mNeedOpenAfterClosed = needOpenAfterClosed;
    }
    
    private int mLevel = -1;
    
    /**
     * [获取菜单等级]
     * @return 等级
     */
    public int getLevel() {
        return mLevel;
    }

    /**
     * [设置等级]
     * @param level 等级
     */
    public void setLevel(int level) {
        this.mLevel = level;
    }

    public Drawer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public Drawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Drawer(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mMinOffset = getResources().getDimensionPixelSize(R.dimen.drawer_min_offset);
        mState = STATE_CLOSED;
    }
    
    /**
     * @brief : [设置抽屉菜单的内容节目]
     * @param view 内容view
     */
    public void setContentView(View view) {
//        if(mContentView != null) {
//            mNextView = view;
//        } else {
//            mContentView = view;
//            mContentView.setVisibility(View.INVISIBLE);
//            removeAllViews();
//            addView(view);
//        }
        mNextView = view;
        if (mContentView == null) {
            mContentView = view;
            mContentView.setVisibility(View.INVISIBLE);
        }
    }
    
    /**
     * [获取内容view]
     * @return view
     */
    public View getContentView() {
        return mContentView;
    }
    
    /**
     * @brief : [只供第一级菜单调用]
     * @param view 内容view
     */
    public void setOneContentView(View view) {
        mContentView = null;
        mContentView = view;
//        removeAllViews();
//        addView(view);
    }
    
    /**
     * [抽屉是否处于完全关闭状态]
     * @return true 完全关闭  false 未完全关闭
     */
    public boolean isClosed() {
        return mState == STATE_CLOSED;
    }
    
    /**
     * [抽屉是否处于完全打开状态]
     * @return true 完全打开  false 未完全打开
     */
    public boolean isOpened() {
        return mState == STATE_OPENED;
    }
    
    /**
     *  [打开抽屉]
     */
    public void openDrawer() {
        if (DEBUG) {
            EvLog.v("something", "打开抽屉  level: " + mLevel);
        }
         
        // 如果抽屉处于完全打开、正在打开或正在关闭状态时，需要先关闭抽屉再打开抽屉
        if (mState == STATE_CLOSING || mState == STATE_OPENED || mState == STATE_OPENING) {
            if (mContentView == null) {
                return;
            }
            if (mState != STATE_OPENED) {
                mContentView.setVisibility(View.INVISIBLE);
            }
            DrawerController.getInstance().closeDrawer(this);
            mNeedOpenAfterClosed = true;
            return;
        }
        // XXX
//        if(mContentView != null) {
//            if(mNextView != null && !mNextView.equals(mContentView)) {
//                mContentView = mNextView;
//                removeAllViews();
//                addView(mContentView);
//            }
//            mContentView.setVisibility(View.INVISIBLE);
//        }
        if (mNextView != null && !mNextView.equals(mContentView)) {
            mContentView = mNextView;
        }
        mNextView = null;
        removeAllViews();
        if (mContentView == null) {
            return;
        }
        addView(mContentView);
        mContentView.setVisibility(View.INVISIBLE);
        mState = STATE_PRE_OPEN;
        int width = getWidth();
        if (width != 0) {
            mOffset = -width;
            postOpenAction();
        }
    }
    
    /**
     *  [关闭抽屉]
     */
    public void closeDrawer() {
        if (mState != STATE_CLOSING && mState != STATE_CLOSED) {
            postCloseAction();
        }
        
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        EvLog.v("something", "---onSizeChanged---");
//        EvLog.d("something", "w： " + w + " h: " + h + " oldw: " + oldw + " oldh: " + oldh);
        super.onSizeChanged(w, h, oldw, oldh);
        
        if (mState == STATE_PRE_OPEN && oldw == 0 && w > 0) {
            if (mContentView != null) {
                mOffset = -w;
                postOpenAction();
            }
        }
        
    }
    
    private OpenAction mOpenAction;
    
    private void postOpenAction() {
        if (DEBUG) {
            EvLog.d("something", "  open action offset: " + mOffset);
        }
        if (mState == STATE_OPENING) {
            return;
        }
        if (mOpenAction == null) {
            mOpenAction = new OpenAction();
//            requestFocus();
            post(mOpenAction);
        }
    }
    
    private void removeOpenAction() {
        if (mOpenAction != null) {
            removeCallbacks(mOpenAction);
            mOpenAction = null;
        }
    }
    
    /**
     * [打开动作]
     */
    private class OpenAction implements Runnable {
        @Override
        public void run() {
            if (mContentView == null) {
                return;
            }
            int dx = getWidth() / MOVE_COUNT;
            //限制最小位移为50dp
            if (dx < mMinOffset) {
                dx = mMinOffset;
            }
            if (mOffset != 0) {
                mContentView.setTranslationX(mOffset);
                if (mContentView.getVisibility() != View.VISIBLE) {
                    mContentView.setVisibility(View.VISIBLE);
                }
                mOffset += dx;
                if (mOffset > 0) {
                    mOffset = 0;
                }
                postDelayed(this, MOVE_DELAY_TIME);
                mState = STATE_OPENING;
            } else {
                mContentView.setTranslationX(0);
                removeOpenAction();
                mState = STATE_OPENED;
                if (mOnOpenListener != null) {
                    mOnOpenListener.onOpened();
                    DrawerStateSubject.getInstance().notifyDrawerOpened(mLevel);
                }
            }
        }
        
    }
    
    private CloseAction mCloseAction;
    
    private void postCloseAction() {
        if (DEBUG) {
            EvLog.d("something", "  close action offset: " + mOffset);
        }
        if (mState == STATE_CLOSING) {
            return;
        }
        
        removeOpenAction();
        if (mCloseAction == null) {
            mCloseAction = new CloseAction();
            post(mCloseAction);
        }
    }
    
    private void removeCloseAction() {
        if (mCloseAction != null) {
            removeCallbacks(mCloseAction);
            mCloseAction = null;
        }
    }
    
    /**
     * [关闭动作]
     */
    private class CloseAction implements Runnable {
        @Override
        public void run() {
            if (mContentView == null) {
                return;
            }
            int dx = getWidth() / MOVE_COUNT;
            //限制最小位移为50dp
            if (dx < mMinOffset) {
                dx = mMinOffset;
            }
            if (mOffset != -getWidth()) {
                mContentView.setTranslationX(mOffset);
                if (mContentView.getVisibility() != View.VISIBLE) {
                    mContentView.setVisibility(View.VISIBLE);
                }
                mOffset -= dx;
                if (mOffset < -getWidth()) {
                    mOffset = -getWidth();
                }
                postDelayed(this, MOVE_DELAY_TIME);
                mState = STATE_CLOSING;
            } else {
                mContentView.setTranslationX(-getWidth());
                removeCloseAction();
                mState = STATE_CLOSED;
                
//                if(mContentView.getVisibility() == View.VISIBLE) {
//                    mContentView.setVisibility(View.INVISIBLE);
//                }
                
                //XXX
                removeAllViews();
                mContentView = null;
                
                //  通知出去
                if (mOnCloseListener != null) {
                    mOnCloseListener.onClosed();
                    DrawerStateSubject.getInstance().notifyDrawerClosed(mLevel);
                }
                
            }
        }
    }
    
    /**
     * [关闭后打开]
     */
    public void openAfterClosed() {
        if (mNeedOpenAfterClosed) {
            mNeedOpenAfterClosed = false;
//            if(mNextView != null && !mNextView.equals(mContentView)) {
//                mContentView = mNextView;
//                removeAllViews();
//                addView(mContentView);
//            }
            openDrawer();
        }
    }
    
    private OnCloseListener mOnCloseListener;
    
    /**
     * [设置关闭监听器]
     * @param onCloseListener 关闭监听器
     */
    public void setOnCloseListener(OnCloseListener onCloseListener) {
        mOnCloseListener = onCloseListener;
    }
    
    /**
     * [关闭监听器]
     */
    public interface OnCloseListener {
        
        /**
         * [完全关闭]
         */
        public void onClosed();
        
    }
    
    private OnOpenListener mOnOpenListener;
    
    /**
     * [设置打开监听器]
     * @param onOpenListener 打开监听器
     */
    public void setOnOpenListener(OnOpenListener onOpenListener) {
        mOnOpenListener = onOpenListener;
    }
    
    /**
     * [打开监听器]
     */
    public interface OnOpenListener {
        
        /**
         * [完全打开]
         */
        public void onOpened();
    }
    
}
