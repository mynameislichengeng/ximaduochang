package com.evideo.kmbox.util;

import java.lang.Character.UnicodeBlock;
import java.util.Locale;

import android.provider.ContactsContract.FullNameStyle;
import android.text.TextUtils;

/**
 * @brief : [语言工具集，包含判断字符是否为中文、日文、数字等方法]
 */
public class LanguageUtils {
    
    private static final String JAPANESE_LANGUAGE = Locale.JAPANESE.getLanguage().toLowerCase();
    private static final String KOREAN_LANGUAGE = Locale.KOREAN.getLanguage().toLowerCase();
    // This includes simplified and traditional Chinese
    private static final String CHINESE_LANGUAGE = Locale.CHINESE.getLanguage().toLowerCase();

    /**
     * @brief : [字符c是否为中文]
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        int fullNameStyle = getFullNameStyle(c);
        return (fullNameStyle == FullNameStyle.CHINESE) ? true : false;
    }
    
    /**
     * @brief : [字符c是否为日文]
     * @param c
     * @return
     */
    public static boolean isJapanese(char c) {
        int fullNameStyle = getFullNameStyle(c);
        return (fullNameStyle == FullNameStyle.JAPANESE) ? true : false;
    }
    
    /**
     * @brief : [字符c是否为韩文]
     * @param c
     * @return
     */
    public static boolean isKorean(char c) {
        int fullNameStyle = getFullNameStyle(c);
        return (fullNameStyle == FullNameStyle.KOREAN) ? true : false;
    }
    
    /**
     * @brief : [字符c是否为数字]
     * @param c
     * @return
     */
    public static boolean isNum(char c) {
        return '0' <= c && c <= '9';
    }
    
    /**
     * @brief : [字符c是否为字母]
     * @param c
     * @return
     */
    public static boolean isLetter(char c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
    }
    
    public static int getFullNameStyle(char c) {
        int nameStyle = FullNameStyle.UNDEFINED;
        if(Character.isLetter(c)) {
            UnicodeBlock unicodeBlock = UnicodeBlock.of(c);
            if(!isLatinUnicodeBlock(unicodeBlock)) {
                if(isCJKUnicodeBlock(unicodeBlock)) {
                    return FullNameStyle.CHINESE;
                } else if(isJapanesePhoneticUnicodeBlock(unicodeBlock)) {
                    return FullNameStyle.JAPANESE;
                } else if (isKoreanUnicodeBlock(unicodeBlock)) {
                    return FullNameStyle.KOREAN;
                }
            }
            nameStyle = FullNameStyle.WESTERN;
        }
        return nameStyle;
    }
    
    private static boolean isLatinUnicodeBlock(UnicodeBlock unicodeBlock) {
        return unicodeBlock == UnicodeBlock.BASIC_LATIN ||
                unicodeBlock == UnicodeBlock.LATIN_1_SUPPLEMENT ||
                unicodeBlock == UnicodeBlock.LATIN_EXTENDED_A ||
                unicodeBlock == UnicodeBlock.LATIN_EXTENDED_B ||
                unicodeBlock == UnicodeBlock.LATIN_EXTENDED_ADDITIONAL;
    }

    private static boolean isCJKUnicodeBlock(UnicodeBlock unicodeBlock) {
        return unicodeBlock == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || unicodeBlock == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || unicodeBlock == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || unicodeBlock == UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || unicodeBlock == UnicodeBlock.CJK_RADICALS_SUPPLEMENT
                || unicodeBlock == UnicodeBlock.CJK_COMPATIBILITY
                || unicodeBlock == UnicodeBlock.CJK_COMPATIBILITY_FORMS
                || unicodeBlock == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || unicodeBlock == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT;
    }

    private static boolean isKoreanUnicodeBlock(UnicodeBlock unicodeBlock) {
        return unicodeBlock == UnicodeBlock.HANGUL_SYLLABLES ||
                unicodeBlock == UnicodeBlock.HANGUL_JAMO ||
                unicodeBlock == UnicodeBlock.HANGUL_COMPATIBILITY_JAMO;
    }

    private static boolean isJapanesePhoneticUnicodeBlock(UnicodeBlock unicodeBlock) {
        return unicodeBlock == UnicodeBlock.KATAKANA ||
                unicodeBlock == UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS ||
                unicodeBlock == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS ||
                unicodeBlock == UnicodeBlock.HIRAGANA;
    }
    
//    public static boolean isPre6CharAllChinese(String text) {
//        if(TextUtils.isEmpty(text)) {
//            return false;
//        }
//        boolean result = true;
//        int len = text.length();
//        int i = 0;
//        while(i < len && i < 6) {
//            if(!isChinese(text.charAt(i))) {
//                result = false;
//                break;
//            }
//            i++;
//        }
//        return result;
//    }
    
    /**
     * @brief : [判断字符串中前n个字符是否都为中文]
     * @param text
     * @param n
     * @return
     */
    public static boolean isPreCharAllChinese(String text, int n) {
        if(TextUtils.isEmpty(text) || n <= 0) {
            return false;
        }
        boolean result = true;
        int len = text.length();
        int i = 0;
        while(i < len && i < n) {
            if(!isChinese(text.charAt(i))) {
                result = false;
                break;
            }
            i++;
        }
        return result;
    }
    
    /**
     * @brief : [判断字符串中前n个字符是否都为中文或数字]
     * @param text
     * @param n
     * @return
     */
    public static boolean isPreCharAllNumOrChinese(String text, int n) {
        if(TextUtils.isEmpty(text) || n <= 0) {
            return false;
        }
        boolean result = true;
        int len = text.length();
        int i = 0;
        while(i < len && i < n) {
            if(!isChinese(text.charAt(i)) && !isNum(text.charAt(i))) {
                result = false;
                break;
            }
            i++;
        }
        return result;
    }
    
    /**
     * If the supplied name style is undefined, returns a default based on the
     * language, otherwise returns the supplied name style itself.
     * 
     * @param nameStyle See {@link FullNameStyle}.
     */
    public static int getAdjustedFullNameStyle(int nameStyle) {
        String mLanguage = Locale.getDefault().getLanguage().toLowerCase();
        if (nameStyle == FullNameStyle.UNDEFINED) {
            if (JAPANESE_LANGUAGE.equals(mLanguage)) {
                return FullNameStyle.JAPANESE;
            } else if (KOREAN_LANGUAGE.equals(mLanguage)) {
                return FullNameStyle.KOREAN;
            } else if (CHINESE_LANGUAGE.equals(mLanguage)) {
                return FullNameStyle.CHINESE;
            } else {
                return FullNameStyle.WESTERN;
            }
        } else if (nameStyle == FullNameStyle.CJK) {
            if (JAPANESE_LANGUAGE.equals(mLanguage)) {
                return FullNameStyle.JAPANESE;
            } else if (KOREAN_LANGUAGE.equals(mLanguage)) {
                return FullNameStyle.KOREAN;
            } else {
                return FullNameStyle.CHINESE;
            }
        }
        return nameStyle;
    }

}
