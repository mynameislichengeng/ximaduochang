package com.evideo.kmbox.model.player;


/**
 * Created by chenyong on 16/1/14.
 */
public interface ITrackInfo {
    int MEDIA_TRACK_TYPE_AUDIO = 2;
    int MEDIA_TRACK_TYPE_METADATA = 5;
    int MEDIA_TRACK_TYPE_SUBTITLE = 4;
    int MEDIA_TRACK_TYPE_TIMEDTEXT = 3;
    int MEDIA_TRACK_TYPE_UNKNOWN = 0;
    int MEDIA_TRACK_TYPE_VIDEO = 1;

    IMediaFormat getFormat();

    int getTrackType();

    String getInfoInline();
}
