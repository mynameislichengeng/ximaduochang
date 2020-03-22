/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年12月3日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evideo.kmbox.R;
import com.evideo.kmbox.widget.common.MaskFocusButton;
import com.evideo.kmbox.widget.common.SearchKeyboard.Key;
import com.evideo.kmbox.widget.common.SearchKeyboardView;
import com.evideo.kmbox.widget.mainview.BreadCrumbsWidget;

/**
 * [功能说明]
 */
public class SearchWidget extends LinearLayout implements View.OnClickListener,View.OnKeyListener{
    private TextView mSearchContentTv;
    private MaskFocusButton mKeyboardSwitcherBtn;
    private MaskFocusButton mCleanBtn;
    public MaskFocusButton mDeleteBtn;
    private SearchKeyboardView mKeyboardView;
    private BreadCrumbsWidget mTitle;
    
    public static final int SEARCH_BTN_123 = 1;
    public static final int SEARCH_BTN_CLEAN = 2;
    public static final int SEARCH_BTN_DEL = 3;
    
    private ISearchItemClickListener mItemClickListener = null;
    private ISearchBtnClickListener mBtnClickListener = null;
    private IRightEdgeListener mRightEdgeListener = null;
    
  
    public interface ISearchBtnClickListener {
        public void onClickBtn(int index);
    }
    public interface ISearchItemClickListener {
        public void onClickItem(Key key);
    }
    
    public interface IRightEdgeListener {
        public void onRightEdge();
        
    }
    
    public interface IUpEdgeListener {
        public void onUpEdge();
    }

    private IUpEdgeListener mUpEdgeListener = null;
    public void setUpEdgeListener(IUpEdgeListener listener) {
        mUpEdgeListener = listener;
    }
    
    public void setRightEdgeListener(IRightEdgeListener listener) {
        mRightEdgeListener = listener;
    }
    
    public void setBtnClickListener(ISearchBtnClickListener listener) {
        mBtnClickListener = listener;
    }
    public void setItemClickListener(ISearchItemClickListener listener) {
        mItemClickListener = listener;
    }
    
    /**
     * @param context
     */
    public SearchWidget(Context context) {
        super(context);
        init(context);
    }

    public SearchWidget(Context context, AttributeSet attrs) {
        super(context,attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_search_layout, this);
        
        mTitle = (BreadCrumbsWidget) findViewById(R.id.main_singer_title_crumb);
        mSearchContentTv = (TextView) findViewById(R.id.order_song_search_tv);
        mKeyboardSwitcherBtn = (MaskFocusButton) findViewById(R.id.order_song_keyboard_switcher_btn);
        mKeyboardSwitcherBtn.setOnClickListener(this);
        mCleanBtn = (MaskFocusButton) findViewById(R.id.order_song_clean_btn);
        mCleanBtn.setOnClickListener(this);
        mDeleteBtn = (MaskFocusButton) findViewById(R.id.order_song_delete_btn);
        mDeleteBtn.setOnClickListener(this);
        
        mDeleteBtn.setOnKeyListener(this);
        mCleanBtn.setOnKeyListener(this);
        mKeyboardSwitcherBtn.setOnKeyListener(this);
        
        mKeyboardView = (SearchKeyboardView) findViewById(R.id.order_song_keyboard_gv);
        mKeyboardView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if (mItemClickListener != null) {
                    Key k = (Key) parent.getAdapter().getItem(position);
                    mItemClickListener.onClickItem(k);
                }
            }
        });

        mKeyboardView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                        && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                    if (mKeyboardView.isSelectedItemInRightEdge()) {
                        if (mRightEdgeListener !=null) {
                            mRightEdgeListener.onRightEdge();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }
 
    

    public String getSearchText() {
        return mSearchContentTv.getText().toString();
    }
    
    public void setSwitchBtnText(String text) {
        mKeyboardSwitcherBtn.setText(text);
    }
    
    public void setSearchText(String text) {
        mSearchContentTv.setText(text);;
    }
    
    public void setSearchTextSize(float size) {
        mSearchContentTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
    }
    
    public void setFirstTitle(String title) {
        mTitle.setFirstTitle(title);
    }
    
    public SearchKeyboardView getKeyboardView() {
        return mKeyboardView;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View arg0) {
        switch(arg0.getId()) {
        case R.id.order_song_keyboard_switcher_btn:
            if (mBtnClickListener != null) {
                mBtnClickListener.onClickBtn(SEARCH_BTN_123);
            }
            break;
        case R.id.order_song_clean_btn:
            if (mBtnClickListener != null) {
                mBtnClickListener.onClickBtn(SEARCH_BTN_CLEAN);
            }
            break;
        case R.id.order_song_delete_btn:
            if (mBtnClickListener != null) {
                mBtnClickListener.onClickBtn(SEARCH_BTN_DEL);
            }
            break;
        default:
            break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (v.getId() == R.id.order_song_delete_btn) {
                    if (mRightEdgeListener !=null) {
                        mRightEdgeListener.onRightEdge();
                        return true;
                    }
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (mUpEdgeListener != null) {
                    mUpEdgeListener.onUpEdge();
                    return true;
                }
            }
        }
        return false;
    }
}
