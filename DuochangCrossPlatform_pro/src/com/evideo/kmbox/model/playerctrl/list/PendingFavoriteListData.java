/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date			Author		Version		Description
 *  -----------------------------------------------
 *  2016-5-27		"zhaoyunlong"		1.0		[修订说明]
 *
 */

package com.evideo.kmbox.model.playerctrl.list;

import java.util.Collections;
import java.util.List;

/**
 * [功能说明]
 */
public class PendingFavoriteListData {
    
    private int mTotalNum = 0;
    private int mReturnNum =  0;
    private List<Integer> mList = Collections.emptyList();
    
    
    
    /**
     * 
     */
    public PendingFavoriteListData() {
        super();
    }
    /**
     * @param totalNum
     * @param returnNum
     * @param list
     */
    public PendingFavoriteListData(int totalNum, int returnNum,
            List<Integer> list) {
        super();
        this.mTotalNum = totalNum;
        this.mReturnNum = returnNum;
        this.mList = list;
    }
    public int getTotalNum() {
        return mTotalNum;
    }
    public void setTotalNum(int totalNum) {
        this.mTotalNum = totalNum;
    }
    public int getReturnNum() {
        return mReturnNum;
    }
    public void setReturnNum(int returnNum) {
        this.mReturnNum = returnNum;
    }
    public List<Integer> getList() {
        return mList;
    }
    public void setList(List<Integer> list) {
        this.mList = list;
    }
}
