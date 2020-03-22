package com.evideo.kmbox.model.dao.data;

public interface KmStorageEventListener {
    public static final int StorageEvent_Mounted = 1;

    public void onKmStorageEvent(StorageVolume volume, int event);
}
