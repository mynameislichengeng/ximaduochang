/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年10月5日     "dinghaiqiang"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.common;

import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.umeng.UmengAgent;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.SearchKeyboard.Key;
import com.evideo.kmbox.R;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * [功能说明]
 */
public class SearchKeyboardView extends CustomSelectorGridView {
    
    private Context mContext;
    private SearchKeyboard mSearchKeyboard;
    private AlphabetKeyAdapter mAlphabetKeyAdapter;
    private NumberKeyAdapter mNumberKeyAdapter;
    private static final int ALPHABET_KEYBOARD = 1;
    private static final int NUMBER_KEYBOARD = 2;
    private int mCurrentKeyboard = 0;
    private String mPageName;

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SearchKeyboardView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    
    public SearchKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public SearchKeyboardView(Context context) {
        super(context);
        init(context);
    }
    
    public void setPageName(String pageName) {
        mPageName = pageName;
    }

    private void init(Context context) {
        mSearchKeyboard = new SearchKeyboard(context, R.xml.search_keyboard);
        mContext = context;
        mAlphabetKeyAdapter = new AlphabetKeyAdapter();
        mNumberKeyAdapter = new NumberKeyAdapter();
        setDrawSelectorOnTop(true);
        setCacheColorHint(mContext.getResources().getColor(R.color.transparent_song_menu_item_background));
        setNumColumns(mSearchKeyboard.getColumnNum());
        setAdapter(mAlphabetKeyAdapter);
        mCurrentKeyboard = ALPHABET_KEYBOARD;

        int selectorPaddingLeft = getResources().getDimensionPixelSize(
                R.dimen.px0);
        int selectorPaddingTop = getResources().getDimensionPixelSize(
                R.dimen.px3);
        int selectorPaddingRight = getResources().getDimensionPixelSize(
                R.dimen.px3);
        int selectorPaddingBottom = getResources().getDimensionPixelSize(
                R.dimen.px3);
        setSelectorPadding(selectorPaddingLeft, selectorPaddingTop, 
                selectorPaddingRight, selectorPaddingBottom); 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
            Rect previouslyFocusedRect) {
        if (gainFocus) {
            onPageStart();
        }
        if (gainFocus && direction == View.FOCUS_RIGHT) {
            setSelection(0);
            return;
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }
    
    private void onPageStart() {
        if (!TextUtils.isEmpty(mPageName)) {
            UmengAgent.getInstance().onPageStart(mPageName);
        }
    }
   
    public boolean isSelectedItemInRightEdge() {
        int pos = getSelectedItemPosition();
        int columnNum = mSearchKeyboard.getColumnNum();
        if (columnNum == 0) {
            return false;
        }
        if ((pos + 1) % columnNum == 0) {
            return true;
        }
        if (mCurrentKeyboard == ALPHABET_KEYBOARD && pos == 25) {
            return true;
        }
        if (mCurrentKeyboard == NUMBER_KEYBOARD && pos == 9) {
            return true;
        }
        return false; 
    }
    
    public boolean updateKeyboardState(String spell) {
        if (TextUtils.isEmpty(spell)) {
            mSearchKeyboard.setAllKeysEnabled(true);
            ((BaseAdapter) getAdapter()).notifyDataSetChanged();
            return true;
        }
        return updateKeyboardState(SongManager.getInstance().getCharacterList(spell)); 
    }
    
    private boolean updateKeyboardState(char[] chars) {
        boolean result = mSearchKeyboard.updateKeyState(chars);
        ((BaseAdapter) getAdapter()).notifyDataSetChanged();
        return result; 
    }
   
    public void setLastLineFocus() {
        if (mCurrentKeyboard == ALPHABET_KEYBOARD) {
            setSelection(24);
        } else if (mCurrentKeyboard == NUMBER_KEYBOARD) {
            setSelection(6);
        }
    }
    
    /**
     * [切换键盘]
     */
    public void switchKeyboard() {
        if (mCurrentKeyboard == ALPHABET_KEYBOARD) {
            setAdapter(mNumberKeyAdapter);
            mCurrentKeyboard = NUMBER_KEYBOARD;
            mNumberKeyAdapter.notifyDataSetChanged();
        } else if (mCurrentKeyboard == NUMBER_KEYBOARD) {
            setAdapter(mAlphabetKeyAdapter);
            mCurrentKeyboard = ALPHABET_KEYBOARD;
            mAlphabetKeyAdapter.notifyDataSetChanged();
        }
    }
    
    public boolean isAlphabetKeyboard() {
        if (mCurrentKeyboard == ALPHABET_KEYBOARD) {
            return true;
        } else {
            return false;
        }
    }
    
    private class AlphabetKeyAdapter extends BaseAdapter {
        private Key[] mKeys = mSearchKeyboard.getAlphabetKeys();
        private TextView keyTv;

        @Override
        public int getCount() {
            if (mKeys != null) {
                return mKeys.length;
            }
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Key getItem(int position) {
            if (mKeys != null) {
                return mKeys[position];
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.search_key_lay, null);
                keyTv = (TextView) convertView.findViewById(R.id.search_key_tv);
                convertView.setTag(keyTv);
            } else {
                keyTv = (TextView) convertView.getTag();
            }
            Key key = getItem(position);
            if (key != null) {
                keyTv.setText(key.label);
                keyTv.setEnabled(key.enable);
            }
            return convertView;
        }
    }
    
    private class NumberKeyAdapter extends BaseAdapter {
        private Key[] mKeys = mSearchKeyboard.getNumberKeys();
        private TextView keyTv;

        /**
         * {@inheritDoc}
         */
        @Override
        public int getCount() {
            if (mKeys != null) {
                return mKeys.length;
            }
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Key getItem(int position) {
            if (mKeys != null) {
                return mKeys[position];
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.search_key_lay, null);
                keyTv = (TextView) convertView.findViewById(R.id.search_key_tv);
                convertView.setTag(keyTv);
            } else {
                keyTv = (TextView) convertView.getTag();
            }
            Key key = getItem(position);
            if (key != null) {
                keyTv.setText(key.label);
                keyTv.setEnabled(key.enable);
            }
            return convertView;
        }
    }
    
}
