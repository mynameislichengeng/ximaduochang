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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Point;
import android.util.Xml;

/**
 * [点歌搜索的键盘]
 */
public class SearchKeyboard {
    
    private static final String TAG_KEYBOARD = "Keyboard";
    private static final String TAG_KEY = "Key";
    private static final int ALPHABET_KEYBOARD_LENGTH = 26;
    private static final int NUMBER_KEYBOARD_LENGTH = 10;
    
    /** List of keys in this keyboard */
    private Key[] mAlphabetKeys = new Key[ALPHABET_KEYBOARD_LENGTH];
    private Key[] mNumberKeys = new Key[NUMBER_KEYBOARD_LENGTH];
    
    private int mRowNum;
    private int mColumnNum;
    
    public SearchKeyboard(Context context, int xmlLayoutResId) {
        loadKeyBoard(context, context.getResources().getXml(xmlLayoutResId));
    }
    
    public Key[] getAlphabetKeys() {
        return mAlphabetKeys;
    }
    
    public Key[] getNumberKeys() {
        return mNumberKeys;
    }
    
    public Key[] getAllKeys() {
        List<Key> tmpList = new ArrayList<Key>(mAlphabetKeys.length + mNumberKeys.length);
        tmpList.addAll(Arrays.asList(mAlphabetKeys));
        tmpList.addAll(Arrays.asList(mNumberKeys));
        Key[] C = new Key[tmpList.size()];
        for(int i = 0; i < tmpList.size();i++) {
            C[i] = tmpList.get(i);
        }
        return C;
    }
    
    public int getRowNum() {
        return mRowNum;
    }
    
    public int getColumnNum() {
        return mColumnNum;
    }
    
    private void loadKeyBoard(Context context, XmlResourceParser parser) {
        Key key = null;
        Resources res = context.getResources();
        try {
            int event;
            while ((event = parser.next()) != XmlResourceParser.END_DOCUMENT) {
                if (event == XmlResourceParser.START_TAG) {
                    String tag = parser.getName();
                    if (TAG_KEY.equals(tag)) {
                        key = createKeyFromXml(res, parser);
                        if (key.position < ALPHABET_KEYBOARD_LENGTH) {
                            mAlphabetKeys[key.position] = key;
                        } else {
                            mNumberKeys[key.position - ALPHABET_KEYBOARD_LENGTH] = key;
                        }
                    } else if (TAG_KEYBOARD.equals(tag)) {
                        TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser), R.styleable.Keyboard);
                        mRowNum = a.getInteger(R.styleable.Keyboard_rowNum, 0);
                        mColumnNum = a.getInteger(R.styleable.Keyboard_columnNum, 0);
                        a.recycle();
                    }
                }
            }
        } catch (Exception e) {
            EvLog.e("search_keyboard parse error: " + e);
        }
    }
    
    public void setAllKeysEnabled(boolean enabled) {
        if (mAlphabetKeys == null || mNumberKeys == null) {
            return;
        }
        for (Key k : mAlphabetKeys) {
            k.enable = enabled;
        }
        for (Key k : mNumberKeys) {
            k.enable = enabled;
        }
    }
    /**
     * @param c
     * @return 字符在键盘表中的位置。
     * @see search_keyboard.xml中的 keyPosition
     */
    private int countPosByChar(char c) {
        if (c >= 'A' && c <= 'Z') {
            return c - 'A';
        } else if (c >= '0' && c <= '9') {
            return c - '0' + ALPHABET_KEYBOARD_LENGTH;
        }
        return -1;
    }
    
    /**
     * @brief : [更新key的状态]
     * @param chars
     * @return true 有key是enable的， false 所有key都是disable的
     */
    public boolean updateKeyState(char[] chars) {
        if (mAlphabetKeys == null || mNumberKeys == null) {
            return false;
        }
        for (Key k : mAlphabetKeys) {
            k.enable = false;
        }
        for (Key k : mNumberKeys) {
            k.enable = false;
        }
        if (chars == null || chars.length <= 0) {
            return false;
        }
        boolean result = false;
        for (char c : chars) {
            switch (c) {
                case 'L':
                    mAlphabetKeys[countPosByChar('L')].enable = true;
                    mNumberKeys[countPosByChar('0') - ALPHABET_KEYBOARD_LENGTH].enable = true;
                    mNumberKeys[countPosByChar('6') - ALPHABET_KEYBOARD_LENGTH].enable = true;
                    result = true;
                    continue;
                case 'Y':
                    mAlphabetKeys[countPosByChar('Y')].enable = true;
                    mNumberKeys[countPosByChar('1') - ALPHABET_KEYBOARD_LENGTH].enable = true;
                    result = true;
                    continue;
                case 'E':
                    mAlphabetKeys[countPosByChar('E')].enable = true;
                    mNumberKeys[countPosByChar('2') - ALPHABET_KEYBOARD_LENGTH].enable = true;
                    result = true;
                    continue;
                case 'S':
                    mAlphabetKeys[countPosByChar('S')].enable = true;
                    mNumberKeys[countPosByChar('3') - ALPHABET_KEYBOARD_LENGTH].enable = true;
                    mNumberKeys[countPosByChar('4') - ALPHABET_KEYBOARD_LENGTH].enable = true;
                    result = true;
                    continue;
                case 'W':
                    mAlphabetKeys[countPosByChar('W')].enable = true;
                    mNumberKeys[countPosByChar('5') - ALPHABET_KEYBOARD_LENGTH].enable = true;
                    result = true;
                    continue;
                case 'Q':
                    mAlphabetKeys[countPosByChar('Q')].enable = true;
                    mNumberKeys[countPosByChar('7') - ALPHABET_KEYBOARD_LENGTH].enable = true;
                    result = true;
                    continue;
                case 'B':
                    mAlphabetKeys[countPosByChar('B')].enable = true;
                    mNumberKeys[countPosByChar('8') - ALPHABET_KEYBOARD_LENGTH].enable = true;
                    result = true;
                    continue;
                case 'J':
                    mAlphabetKeys[countPosByChar('J')].enable = true;
                    mNumberKeys[countPosByChar('9') - ALPHABET_KEYBOARD_LENGTH].enable = true;
                    result = true;
                    continue;
                default:
                    break;
            }
            for (Key k : mAlphabetKeys) {
                if (c == k.code) {
                    k.enable = true;
                    result = true;
                    break;
                }
            }
            for (Key k : mNumberKeys) {
                if (c == k.code) {
                    k.enable = true;
                    result = true;
                    break;
                }
            }
        }
//        EvLog.d("updateKeyState result " + result);
        return result;
    }
    
    protected Key createKeyFromXml(Resources res, XmlResourceParser parser) {
        return new Key(res, parser);
    }
    
    public static class Key {
        
        public int code;
        
        public CharSequence label;
        
        public Point point = new Point();
        
        public boolean enable;
        
        public int position;
        
        public Key(Resources res, XmlResourceParser parser) {
            TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser), R.styleable.Keyboard_Key);
            code = a.getInteger(R.styleable.Keyboard_Key_code, 65);
            label = a.getText(R.styleable.Keyboard_Key_keyLabel);
            point.y = a.getInteger(R.styleable.Keyboard_Key_keyRow, 0);
            point.x = a.getInteger(R.styleable.Keyboard_Key_keyColumn, 0);
            enable = a.getBoolean(R.styleable.Keyboard_Key_keyEnable, true);
            position = a.getInteger(R.styleable.Keyboard_Key_keyPosition, 0);
            a.recycle();
        }
    }

}
