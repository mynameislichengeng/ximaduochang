package com.evideo.kmbox.model.datacenter.proxy.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DataCenterMessage {

    private JSONObject objJson = new JSONObject();
    /**
     * result of command
     **/
    public int result = -1;
    /**
     * error message of command result
     **/
    public String errorMsg = "unknown";

    public void put(String key, String value) throws JSONException {
        objJson.put(key, value);
    }

    public void put(String key, Object value) throws JSONException {
        objJson.put(key, value);
    }

    public String get(String key) throws JSONException {
        String value = "";
        if (objJson.has(key)) {
            value = objJson.getString(key);
        }
        return value;
    }

    public void setContentString(String content) throws JSONException {
        objJson = new JSONObject(content);
        Log.i("gsp", "setContentString:content " + getContentString());
    }

    public String getContentString() {

        return objJson.toString();
    }

    public JSONArray getJSONArray(String key) throws JSONException {
        return objJson.getJSONArray(key);
    }

    public JSONObject getJSONObject(String key) throws JSONException {
        return objJson.getJSONObject(key);
    }

    public DataCenterMessage() {
    }
}
