package com.evideo.kmbox.model.dao.data;

public class PageInfo {
    private int mPageIndex = 0;
    private int mPageSize = 10;

    public PageInfo(int pageIndex, int pageSize)
    {
        mPageIndex = pageIndex;
        mPageSize = pageSize;
    }
    
    public PageInfo(int pageSize)
    {
        mPageIndex = 0;
        mPageSize = pageSize;
    }
    
    public int getPageIndex()
    {
        return mPageIndex;
    }
    
    public void setPageIndex(int pageIndex)
    {
        mPageIndex = pageIndex;
    }
    
    public int getPageSize()
    {
        return mPageSize;
    }
    
    public void setPageSize(int pageSize)
    {
        mPageSize = pageSize;
    }
}
