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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;


public class DataCenterHttps implements IDataCenterHttp{

    Map<String,String> headers = new HashMap<String,String>();
    Map<String,String> content = new HashMap<String,String>();

    @Override
    public String post(String url) throws Exception {
        Request.Builder builder = new Request.Builder();
        if(headers!=null && headers.size()!=0){
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            Iterator<Map.Entry<String, String>> iterator = entries.iterator();
            while (iterator.hasNext()){
                Map.Entry<String, String> next = iterator.next();
                String key = next.getKey();
                String value = next.getValue();
                builder.addHeader(key,value);
            }
        }
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        if(content!=null && content.size()!=0){
            Set<Map.Entry<String, String>> entries = content.entrySet();
            Iterator<Map.Entry<String, String>> iterator = entries.iterator();
            while (iterator.hasNext()){
                Map.Entry<String, String> next = iterator.next();
                String key = next.getKey();
                String value = next.getValue();
                bodyBuilder.addEncoded(key,value);
            }
        }
        builder.post(bodyBuilder.build());
        Request request = builder.url(url).build();
        Response response = OkHttpClientFactory.getInstance().getClient().newCall(request).execute();
        if (!response.isSuccessful()){
            response.close();
            throw new IOException("Unexpected code " + response);
        }
        return response.body().string();
    }

    @Override
    public void addHeader(String key, String value) {
        if(!headers.containsKey(key)){
            headers.put(key, value);
        }
    }

    @Override
    public void addContent(String key, String value) {
        if(!content.containsKey(key)){
            content.put(key, value);
        }
    }

    @Override
    public String getHeader() {
        return headers.toString();
    }

    @Override
    public String getContent() {
        return content.toString();
    }

}
