/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年8月23日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.update.huodong;

/**
 * [功能说明]
 */
public class HuodongItemInfo {

    
    public int activity_type;
    public int activity_id;
    public String activity_title;
    public String imgUrl;//入口图
    public String activity_arg0;
        
    public HuodongItemInfo() {
        activity_type = -1;
        activity_id = -1;
        activity_title = "";
        imgUrl = "";
        imgUrl = "";
    }
    
    public HuodongItemInfo(int id,int type,String title,String imgUrl,String arg0) {
        this.activity_id = id;
        this.activity_type = type;
        this.activity_title = title;
        this.imgUrl = imgUrl;
        this.activity_arg0 = arg0;
    }
}
