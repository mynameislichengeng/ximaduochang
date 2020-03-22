/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-4-15     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.presenter;

import java.util.List;

/**
 * [功能说明]分页数据信息
 */
public class PageDataInfo<T> {

    /** [分页数据] */
    public List<T> datas;
    /** [所有数据的总数] */
    public int totalNum;
    
    public PageDataInfo(List<T> datas, int totalNum) {
        super();
        this.datas = datas;
        this.totalNum = totalNum;
    }
    
}
