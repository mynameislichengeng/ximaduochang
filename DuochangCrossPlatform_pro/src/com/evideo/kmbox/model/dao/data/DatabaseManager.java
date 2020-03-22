package com.evideo.kmbox.model.dao.data;

import com.evideo.kmbox.dao.DAOFactory;

import android.content.Context;

public class DatabaseManager {
    static public void init(Context context) {
        DAOFactory.getInstance().setGlobalContext(context);
        MediaManager.getInstance().SyncWithStorageVolume();
    }
}
