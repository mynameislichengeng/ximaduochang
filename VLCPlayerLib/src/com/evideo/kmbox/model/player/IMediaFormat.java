package com.evideo.kmbox.model.player;

/**
 * Created by chenyong on 16/1/14.
 */
public interface IMediaFormat {
    String KEY_MIME = "mime";
    String KEY_WIDTH = "width";
    String KEY_HEIGHT = "height";

    String getString(String var1);

    int getInteger(String var1);
}
