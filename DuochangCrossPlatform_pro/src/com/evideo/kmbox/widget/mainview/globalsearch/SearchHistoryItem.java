
package com.evideo.kmbox.widget.mainview.globalsearch;

/**
 * [搜索历史的项]
 */
public class SearchHistoryItem {
    
    /** [ITEM类型为歌曲] */
    public static final int SEARCH_ITEM_TYPE_SONG = 1;
    /** [ITEM类型为歌星] */
    public static final int SEARCH_ITEM_TYPE_SINGER = 2;
    
    /** [item类型] */
    public int mItemType;
    /** [item的id] */
    public int mId;
    
    public SearchHistoryItem(int mType, int mId) {
        this.mItemType = mType;
        this.mId = mId;
    }

    public int getmId() {
        return mId;
    }
}
