package com.evideo.kmbox.widget.common;

/**
 * [功能说明]歌曲列表keyDown事件
 */
public interface OnSongListKeyDownEventListener {
    
    /**
     * [功能说明]焦点到最右边时的向右keydown事件
     */
    public void onRightEdgeKeyDown();
    
    /**
     * [功能说明]焦点到最左边时的向左keydown事件
     */
    public void onLeftEdgeKeyDown();

    public void onDownEdgeKeyDown();
    
    public void onUpEdgeKeyDown();
}
