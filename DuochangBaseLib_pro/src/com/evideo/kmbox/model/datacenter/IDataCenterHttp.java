package com.evideo.kmbox.model.datacenter;
/*
* Copyright (C) 2014-2016 福建星网视易信息系统有限公司
* All rights reserved by 福建星网视易信息系统有限公司
*
* Modification History:
* DateAuthorVersionDescription
* -----------------------------------------------
* 2017/4/24王凯1.0[修订说明]
*
*/

public interface IDataCenterHttp {

    String post(String url) throws Exception;

    void addHeader(String key, String value);

    void addContent(String key, String value);

    String getHeader();

    String getContent();

}
