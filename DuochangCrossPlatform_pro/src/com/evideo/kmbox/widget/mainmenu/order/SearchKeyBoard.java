package com.evideo.kmbox.widget.mainmenu.order;

import com.evideo.kmbox.R;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Point;
import android.util.Xml;

public class SearchKeyBoard {
    
    private static final String TAG_KEYBOARD = "Keyboard";
    private static final String TAG_KEY = "Key";
    
    /** List of keys in this keyboard */
    private Key[] mKeys = new Key[36];
    
    private int mRowNum;
    
    private int mColumnNum;
    
    public SearchKeyBoard(Context context, int xmlLayoutResId) {
        loadKeyBoard(context, context.getResources().getXml(xmlLayoutResId));
    }
    
    public Key[] getKeys() {
        return mKeys;
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
                    if(TAG_KEY.equals(tag)) {
                        key = createKeyFromXml(res, parser);
                        mKeys[key.position] = key;
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
            UmengAgentUtil.reportError(e);
        }
    }
    
    public void setAllKeysEnabled(boolean enabled) {
        if(mKeys == null) {
            return;
        }
        for(Key k : mKeys) {
            k.enable = enabled;
        }
    }
    
    private int countPosByChar(char c) {
        if(c >= 'A' && c <= 'Z') {
            return c - 'A';
        } else if (c >= '1' && c <= '9') {
            return c - '1' + 26;
        } else if (c == '0') {
            return 35;
        }
        return -1;
    }
    
    /**
     * @brief : [更新key的状态]
     * @param chars
     * @return true 有key是enable的， false 所有key都是disable的
     */
    public boolean updateKeyState(char[] chars) {
        if (mKeys == null) {
            return false;
        }
        for (Key k : mKeys) {
            k.enable = false;
        }
        if (chars == null || chars.length <= 0) {
            return false;
        }
        boolean result = false;
        for (char c : chars) {
            switch (c) {
                case 'L':
                    mKeys[countPosByChar('L')].enable = true;
                    mKeys[countPosByChar('0')].enable = true;
                    mKeys[countPosByChar('6')].enable = true;
                    result = true;
                    continue;
                case 'Y':
                    mKeys[countPosByChar('Y')].enable = true;
                    mKeys[countPosByChar('1')].enable = true;
                    result = true;
                    continue;
                case 'E':
                    mKeys[countPosByChar('E')].enable = true;
                    mKeys[countPosByChar('2')].enable = true;
                    result = true;
                    continue;
                case 'S':
                    mKeys[countPosByChar('S')].enable = true;
                    mKeys[countPosByChar('3')].enable = true;
                    mKeys[countPosByChar('4')].enable = true;
                    result = true;
                    continue;
                case 'W':
                    mKeys[countPosByChar('W')].enable = true;
                    mKeys[countPosByChar('5')].enable = true;
                    result = true;
                    continue;
                case 'Q':
                    mKeys[countPosByChar('Q')].enable = true;
                    mKeys[countPosByChar('7')].enable = true;
                    result = true;
                    continue;
                case 'B':
                    mKeys[countPosByChar('B')].enable = true;
                    mKeys[countPosByChar('8')].enable = true;
                    result = true;
                    continue;
                case 'J':
                    mKeys[countPosByChar('J')].enable = true;
                    mKeys[countPosByChar('9')].enable = true;
                    result = true;
                    continue;
                default:
                    break;
            }
            for (Key k : mKeys) {
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
