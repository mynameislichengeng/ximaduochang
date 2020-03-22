/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-9-17     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.singer;

import java.util.ArrayList;
import java.util.List;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.exception.DCNoResultException;
import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
import com.evideo.kmbox.model.umeng.EventConst;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.presenter.PageLoadPresenter.IPageLoadCallback;
import com.evideo.kmbox.util.DimensionsUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.SearchWidget;
import com.evideo.kmbox.widget.SearchWidget.IRightEdgeListener;
import com.evideo.kmbox.widget.SearchWidget.ISearchBtnClickListener;
import com.evideo.kmbox.widget.SearchWidget.ISearchItemClickListener;
import com.evideo.kmbox.widget.common.AnimLoadingView;
import com.evideo.kmbox.widget.common.CustomSelectorGridView;
import com.evideo.kmbox.widget.common.MaskFocusTextView;
import com.evideo.kmbox.widget.common.CustomSelectorGridView.IEdgeListener;
import com.evideo.kmbox.widget.common.SearchKeyboard.Key;
import com.evideo.kmbox.widget.common.SearchKeyboardView;
import com.evideo.kmbox.widget.mainview.AbsBaseView;
import com.evideo.kmbox.widget.mainview.MainViewId;
import com.evideo.kmbox.widget.mainview.MainViewManager;

/**
 * [功能说明]
 */
public class SingerView extends AbsBaseView implements
        IPageLoadCallback<Singer> {

    private final String TAG = SingerView.class.getSimpleName();

    private static final int PAGE_SIZE = 50;
    private static final int PAGE_LOAD_EDGE_COUNT = 20;
    private static final int PAGE_SIZE_SINGER = 20;
    private static final int SINGER_TYPE_COUNT = 8;

    private SearchWidget mSearchWidget;

    private MaskFocusTextView[] mSingerTypes;
    private CustomSelectorGridView mSingerGv;
    private SingerGridViewAdapter mAdapter;

    private ArrayList<Singer> mDatas = null;
    private PickSingerPageLoadPresenter mPageLoadPresenter;
    private String mCurSpell = "";
    private int mTabViewPadding = 0;
    private int mNormalTabTvSize = 0;
    private int mHighlightTabTvSize = 0;
    private int mTabViewHeight = 0;
    private int mFadingEdgeLength;
    private float mTextHintSize;
    private float mTextCommonSize;
    private int mCurTabIndex = 0;
    private int mPreTabIndex = 0;
    private int mSingerItemPadding = 0;
    private int mSearchBtnPadding = 0;

    private AnimLoadingView mLoadingView;

    /**
     * @param activity
     * @param backViewId
     */
    public SingerView(Activity activity, int backViewId) {
        super(activity, backViewId);
        mDatas = new ArrayList<Singer>();
        initDimens();
        initSearchKeyboardView();
        initRightView();
    }

    private void initDimens() {
        mFadingEdgeLength = (int) DimensionsUtil.getDimension(mActivity,
                R.dimen.singer_gridview_fading_edge_length);
        mTextHintSize = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px39);
        mTextCommonSize = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px39);
        mTabViewPadding = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px18);
        mNormalTabTvSize = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px27);
        mHighlightTabTvSize = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px33);
        mTabViewHeight = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px98);
        mSingerItemPadding = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px25);
        mSearchBtnPadding = (int) DimensionsUtil.getDimension(mActivity, R.dimen.px19);
    }

//    public boolean resumeFocus() {
//        if (mSearchWidget != null && mSearchWidget.getKeyboardView() != null) {
//            mSearchWidget.getKeyboardView().requestFocus();
//            return true;
//        }
//        return false;
//    }

    private void initSearchKeyboardView() {
        mPageLoadPresenter = new PickSingerPageLoadPresenter(PAGE_SIZE_SINGER, this, "", mCurTabIndex);

        mSearchWidget = (SearchWidget) findViewById(R.id.singer_search);
        mSearchWidget.setFirstTitle(getString(R.string.main_singer_title));

        mSearchWidget.getKeyboardView().setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    EvLog.i("getKeyboardView focus");
                    mSingerGv.setFocusable(false);
                } else {
                    EvLog.i("getKeyboardView not focus");
                    mSingerGv.setFocusable(true);
                }
            }
        });

        mSearchWidget.setBtnClickListener(new ISearchBtnClickListener() {

            @Override
            public void onClickBtn(int index) {
                if (index == SearchWidget.SEARCH_BTN_123) {
                    SearchKeyboardView boardView = mSearchWidget.getKeyboardView();
                    if (boardView == null) {
                        return;
                    }
                    boardView.switchKeyboard();
                    if (boardView.isAlphabetKeyboard()) {
                        mSearchWidget.setSwitchBtnText("123");
                    } else {
                        mSearchWidget.setSwitchBtnText("ABC");
                    }
                    LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_KEYBOARD_SWITCH);
                } else if (index == SearchWidget.SEARCH_BTN_CLEAN) {
                    mSearchWidget.setSearchText("");
                    mSearchWidget.setSearchTextSize(mTextHintSize);
                    onSearchContentChanged("");
                    LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_KEYBOARD_CLEAR);
                } else if (index == SearchWidget.SEARCH_BTN_DEL) {
                    String content = mSearchWidget.getSearchText();
                    if (!TextUtils.isEmpty(content)) {
                        String spell = content.substring(0, content.length() - 1);
                        mSearchWidget.setSearchTextSize(TextUtils.isEmpty(spell) ? mTextHintSize
                                : mTextCommonSize);
                        mSearchWidget.setSearchText(spell);
                        onSearchContentChanged(spell);

                        LogAnalyzeManager.onEvent(mActivity,
                                EventConst.ID_CLICK_ORDER_SONG_VIEW_BACKSPACE);
                    }
                }
            }
        });

        mSearchWidget.setItemClickListener(new ISearchItemClickListener() {

            @Override
            public void onClickItem(Key key) {
                if (key != null && key.enable) {
                    StringBuilder sb = new StringBuilder(mSearchWidget.getSearchText());
                    mSearchWidget.setSearchTextSize(mTextCommonSize);
                    mSearchWidget.setSearchText(sb.append(key.label).toString());
                    onSearchContentChanged(sb.toString());
//                    EvLog.i("SearchContent " + sb.toString());
                }
            }
        });
        mSearchWidget.setRightEdgeListener(new IRightEdgeListener() {

            @Override
            public void onRightEdge() {
                moveFocusToRightRect();
            }
        });
    }

    /**
     * [功能说明] 焦点从搜索框移动到右侧歌星列表
     */
    private void moveFocusToRightRect() {
        if (mSingerGv != null) {
            mSingerGv.setFocusable(true);
            mSingerGv.requestFocus();
        }
    }

    private void initRightView() {
        mSingerTypes = new MaskFocusTextView[SINGER_TYPE_COUNT];
        mSingerTypes[0] = (MaskFocusTextView) findViewById(R.id.singer_type_all_singers);
        mSingerTypes[1] = (MaskFocusTextView) findViewById(R.id.singer_type_mainland_male);
        mSingerTypes[2] = (MaskFocusTextView) findViewById(R.id.singer_type_mainland_female);
        mSingerTypes[3] = (MaskFocusTextView) findViewById(R.id.singer_type_hongkong_taiwan_male);
        mSingerTypes[4] = (MaskFocusTextView) findViewById(R.id.singer_type_hongkong_taiwan_female);
        mSingerTypes[5] = (MaskFocusTextView) findViewById(R.id.singer_type_chinese_bands);
        mSingerTypes[6] = (MaskFocusTextView) findViewById(R.id.singer_type_foreign_singers);
        mSingerTypes[7] = (MaskFocusTextView) findViewById(R.id.singer_type_foreign_bands);

        int id = MainViewManager.getInstance().getStatusBar().getSelectedNumId();
        mSingerTypes[7].setNextFocusRightId(id);

        mLoadingView = (AnimLoadingView) findViewById(R.id.singer_loading_widget);

        OnFocusChangeListener listener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                log("-----onFocusChange()----");
                if (!(arg0 instanceof MaskFocusTextView)
                        || !(arg0.getTag() instanceof Integer)) {
                    return;
                }

                int index = (Integer) arg0.getTag();
                log("index:" + index);
                if (!arg1) {
                    mPreTabIndex = index;
                    return;
                }
                mCurTabIndex = index;
                setTabChecked(index);
                if (mCurTabIndex != mPreTabIndex) {
                    onTabSelected(index);
                    LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_ALL_SINGER_TYPE_LIST);
                }
            }
        };

        View.OnKeyListener keyListener = new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                log("onKey(View v, int keyCode, KeyEvent event)----event.getAction:" + event.getAction() + ">>keyCode: " + keyCode);
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (event.getRepeatCount() >= 1) {
                        EvLog.i("reject long click event");
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
//                        mFocusViewBeforeMoveToSelectedNum = v;
                        MainViewManager.getInstance().getStatusBar().requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (mCurTabIndex == (mSingerTypes.length - 1)) {
//                            mFocusViewBeforeMoveToSelectedNum = v;
                            MainViewManager.getInstance().getStatusBar().setSelectedNumFocus();
                            return true;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        EvLog.i("recv KEYCODE_DPAD_DOWN ");
                        if (mSingerGv != null && mSingerGv.getVisibility() == View.VISIBLE) {
                            mSingerGv.requestFocus();
                            return true;
                        } else {
                            EvLog.e("mSingerGv.getVisibility=" + mSingerGv.getVisibility());
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (mCurTabIndex == 0) {
                            mSearchWidget.getKeyboardView().setSelection(5);
                            mSearchWidget.getKeyboardView().requestFocus();
                            return true;
                        }
                    }
                }
                return false;
            }
        };

//        OnTouchListener onTouchListener = new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                log("--onTouch()---");
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//
//                        log("--onTouch()---MotionEvent.ACTION_DOWN----tag:" + v.getTag());
//
//                        v.requestFocus();
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        log("--onTouch()---MotionEvent.ACTION_MOVE----");
//
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        log("--onTouch()---MotionEvent.ACTION_UP----");
//
//                        break;
//
//                }
//                return false;
//            }
//        };

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (!(arg0 instanceof MaskFocusTextView)
                        || !(arg0.getTag() instanceof Integer)) {
                    return;
                }

                int index = (Integer) arg0.getTag();

//                if (!arg1) {
//                    mPreTabIndex = index;
//                    return;
//                }
                mCurTabIndex = index;
                setTabChecked(index);
//                if (mCurTabIndex != mPreTabIndex) {
                onTabSelected(index);
                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_ALL_SINGER_TYPE_LIST);
//                }
            }
        };


        for (int i = 0; i < mSingerTypes.length; i++) {
            mSingerTypes[i].setFocusFrame(R.drawable.focus_frame_new);
            mSingerTypes[i].setTextColor(BaseApplication.getInstance().getBaseContext().getResources().getColor(R.color.white));
//            mSingerTypes[i].setTextSize(mNormalTabTvSize);
            mSingerTypes[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalTabTvSize);
//            EvLog.e("mSingerTypes setTextSize " + mNormalTabTvSize);
            mSingerTypes[i].setHeight(mTabViewHeight);
            mSingerTypes[i].setTag(i);
            mSingerTypes[i].setFocusPadding(mTabViewPadding + 2, mTabViewPadding,
                    mTabViewPadding + 2, mTabViewPadding);
            mSingerTypes[i].setOnFocusChangeListener(listener);
            mSingerTypes[i].setOnKeyListener(keyListener);
            mSingerTypes[i].setOnClickListener(onClickListener);
//            mSingerTypes[i].setOnTouchListener(onTouchListener);
//            mSingerTypes[i].setNextFocusDownId(R.id.singer_gv);
        }

        mSingerGv = (CustomSelectorGridView) findViewById(R.id.singer_gv);
        mAdapter = new SingerGridViewAdapter(BaseApplication.getInstance().getBaseContext(), mSingerGv, mDatas);
        mSingerGv.setAdapter(mAdapter);
        mSingerGv.setEnableSquareSelector(true);
        mSingerGv.setCustomSelectorDrawable(BaseApplication.getInstance().getBaseContext().getResources().getDrawable(R.drawable.singer_icon_frame));
        mSingerGv.setSelectorPadding( // 左、上、右、下
                getResources().getDimensionPixelSize(R.dimen.singer_order_padding_left)
                , getResources().getDimensionPixelSize(R.dimen.singer_order_padding_top)
                , getResources().getDimensionPixelSize(R.dimen.singer_order_padding_right)
                , getResources().getDimensionPixelSize(R.dimen.singer_order_padding_bottom));
        mSingerGv.setVerticalFadingEdgeEnabled(true);
        mSingerGv.setFadingEdgeLength(mFadingEdgeLength);
        mSingerGv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                log("----setOnItemClickListener--onItemClick---");
                SingerGridViewAdapter adapter = (SingerGridViewAdapter) arg0
                        .getAdapter();
                if (adapter == null) {
                    EvLog.e(" no singerGridViewAdapter Found");
                    return;
                }
                Singer singer = adapter.getItem(arg2);
                if (mListener != null && singer != null) {
                    mListener.onSingerItemClick(singer, MainViewId.ID_SINGER);
                } else {
                    EvLog.e(" no SingerItem clicked!!");
                }
                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_ALL_SINGER_LIST);
            }
        });
        mSingerGv.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                log("----setOnItemSelectedListener--onItemSelected---");
                if (mAdapter == null || mPageLoadPresenter == null) {
                    return;
                }

                Singer item = (Singer) arg0.getAdapter().getItem(arg2);

                if (item == null) {
                    return;
                }

                if (arg2 <= (mAdapter.getCount() - 1)
                        && arg2 >= (mAdapter.getCount() - PAGE_LOAD_EDGE_COUNT)) {
                    mPageLoadPresenter.loadNextPage();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        /*mSingerGv.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP
                        && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                    if (mSingerGv.getSelectedItemPosition() < 5) {
                        mSingerTypes[mCurTabIndex].requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });*/
        mSingerGv.setEdgeListener(new IEdgeListener() {

            @Override
            public boolean onRightEdge() {
                MainViewManager.getInstance().getStatusBar().setSelectedNumFocus();
                return true;
            }

            @Override
            public boolean onLeftEdge() {
                mSearchWidget.getKeyboardView().setSelection(5);
                mSearchWidget.getKeyboardView().requestFocus();
                return true;
            }

            @Override
            public boolean onDownEdge() {
                MainViewManager.getInstance().setSmallMvFocus();
                return true;
            }

            @Override
            public boolean onUpEdge() {
                mSingerTypes[mCurTabIndex].requestFocus();
                return true;
            }
        });
        setTabChecked(0);
        onTabSelected(0);
        int selectNumId = MainViewManager.getInstance().getStatusBar().getSelectedNumId();
        mSingerGv.setNextFocusRightId(selectNumId);
        /*mKeyboardView.setNextFocusRightId(R.id.singer_gv);
        mDeleteBtn.setNextFocusRightId(R.id.singer_gv);*/
        mSearchWidget.setNextFocusRightId(R.id.singer_gv);
    }

    private void setTabChecked(int index) {
        mCurTabIndex = index;
        highlightFonts(mSingerTypes[mCurTabIndex]);
        for (int i = 0; i < mSingerTypes.length; i++) {
            if (i == index) {
                continue;
            }
            restoreFonts(mSingerTypes[i]);
        }
    }

    private void onTabSelected(int index) {
        loadData(index);
//        EvLog.i("Singer Type Selected: " + index);
    }

    private void restoreFonts(MaskFocusTextView view) {
        if (view == null) {
            return;
        }
        view.setTextColor(BaseApplication.getInstance().getBaseContext().getResources().getColor(R.color.white));
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalTabTvSize);
    }

    private void highlightFonts(MaskFocusTextView view) {
        if (view == null) {
            return;
        }
        view.setTextColor(BaseApplication.getInstance().getBaseContext().getResources().getColor(R.color.text_yellow));
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, mHighlightTabTvSize);
    }

    private void onSearchContentChanged(String content) {
        String spell = changeNum2Letter(content);
        if (!mCurSpell.equals(spell)) {
            mCurSpell = spell;
            setTabChecked(0);
            onTabSelected(0);
        }
    }

    private void loadData() {
        this.loadData(0);
    }

    private void loadData(int index) {
        if (mPageLoadPresenter != null) {
            mPageLoadPresenter.stopTask();
        }
        mPageLoadPresenter = new PickSingerPageLoadPresenter(PAGE_SIZE, this,
                mCurSpell, index);
        mPageLoadPresenter.loadData();
    }

    private String changeNum2Letter(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        // EvLog.d("changeNum2Letter content " + content);
        char[] chars = content.toCharArray();
        int size = chars.length;
        for (int i = 0; i < size; i++) {
            switch (chars[i] - '0') {
                case 0:
                case 6:
                    chars[i] = 'L';
                    break;
                case 1:
                    chars[i] = 'Y';
                    break;
                case 2:
                    chars[i] = 'E';
                    break;
                case 3:
                case 4:
                    chars[i] = 'S';
                    break;
                case 5:
                    chars[i] = 'W';
                    break;
                case 7:
                    chars[i] = 'Q';
                    break;
                case 8:
                    chars[i] = 'B';
                    break;
                case 9:
                    chars[i] = 'J';
                    break;
                default:
                    break;
            }
        }
        // EvLog.d("changeNum2Letter after " + new String(chars));
        return new String(chars);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onPageStart();
        //避免从歌星详情退出焦点又跑到所有歌星上面
       /* mCurTabIndex = 0;
        if (mSingerTypes[mCurTabIndex] != null) {
            mSingerTypes[0].requestFocus();
        }*/
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getLayResId() {
        return R.layout.main_singer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getViewId() {
        return MainViewId.ID_SINGER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetFocus() {
        if (mSingerGv != null && mSingerGv.getAdapter() != null && mSingerGv.getAdapter().getCount() > 0) {
            mSingerGv.requestFocus();
        } else if (mSearchWidget != null) {
            mSearchWidget.getKeyboardView().requestFocus();
        }
    }

    private void showLoadingView() {
        if (mLoadingView.getVisibility() != View.VISIBLE) {
            mLoadingView.setVisibility(View.VISIBLE);
        }
        mLoadingView.startAnim();
        mSingerGv.setVisibility(View.GONE);
    }

    private void showLoadingErrorView(int resid) {
        if (mLoadingView.getVisibility() != View.VISIBLE) {
            mLoadingView.setVisibility(View.VISIBLE);
        }
        mLoadingView.stopAnim();
        mLoadingView.showLoadFail(resid);
        mSingerGv.setVisibility(View.GONE);
    }

    private void showSingerGridView() {
        if (mLoadingView.getVisibility() != View.GONE) {
            mLoadingView.stopAnim();
            mLoadingView.setVisibility(View.GONE);
        }
        mSingerGv.setVisibility(View.VISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPreLoadData(boolean isReset, boolean isNext) {
        if (mDatas.size() == 0 || isReset) {
            showLoadingView();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPostLoadData(Exception e, boolean isReset, boolean isNext,
                               List<Singer> datas) {
        EvLog.d("SingerView updateGridView isReset: " + isReset + " isNext: "
                + isNext);
        if (e != null) {
            handleException(e, isReset);
        }
        if (e == null && mAdapter != null && datas != null) {
            if (isReset) {
                mDatas.clear();
            }
            showSingerGridView();
            mDatas.addAll(datas);
            mAdapter.notifyDataSetChanged();
            if (isReset) {
                mSingerGv.setSelection(0);
            }
            mSingerGv.refreshDrawableState();
        }
    }
    
    /*private void changeSingerGridViewFocusState() {
        if (mSingerGv == null) {
            return;
        }
        mSingerGv.setFocusable(false);
        mSingerGv.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSingerGv.setFocusable(true);
            }
        }, 1000);
    }*/

    private void handleException(Exception e, boolean isReset) {
        EvLog.e(e.getMessage());
        if (e instanceof NetworkErrorException) {
            if (isReset) {
                showLoadingErrorView(R.string.error_loading_song_network);
            } else {
//                mListView
//                        .showFootView(R.string.error_list_foot_loading_song_network);
            }
        } else if (e instanceof DCNoResultException) {
            if (isReset) {
                showLoadingErrorView(R.string.error_loading_song_no_result);
            } else {
//                mListView.showFootView(R.string.error_list_foot_loading_song);
            }
        } else {
            if (isReset) {
                showLoadingErrorView(R.string.error_loading_song);
            } else {
//                mListView.showFootView(R.string.error_list_foot_loading_song);
            }
        }
    }

    /**
     * [歌星点击回调]
     */
    public interface ISingerClickListener {
        /**
         * [歌星点击动作]
         *
         * @param singer     歌星项
         * @param backViewId 后退界面
         */
        public void onSingerItemClick(Singer singer, int backViewId);
    }

    private ISingerClickListener mListener;

    /**
     * [设置singerItem监听]
     *
     * @param listener 监听器
     */
    public void setSingerClickListener(ISingerClickListener listener) {
        mListener = listener;
    }

    @Override
    protected void clickExitKey() {
        if (mDatas != null) {
            mDatas.clear();
        }
    }


    @Override
    public boolean onSmallMVUpKey() {
        mSearchWidget.getKeyboardView().setLastLineFocus();
        mSearchWidget.getKeyboardView().requestFocus();
        return true;
    }

    @Override
    public boolean onSmallMVRightKey() {
        if (mSingerGv.getVisibility() == View.VISIBLE) {
            mSingerGv.requestFocus();
            return true;
        }
        return false;
    }

    @Override
    public boolean onStatusBarDownKey() {
        log("onStatusBarDownKey()");
        if (mCurTabIndex < 0 || mCurTabIndex >= mSingerTypes.length) {
            mCurTabIndex = 0;
        }
        mSingerTypes[mCurTabIndex].requestFocus();
        return true;
    }

    private void log(String tag) {
        Log.d("gsp", TAG + ">>>" + tag);
    }
}
