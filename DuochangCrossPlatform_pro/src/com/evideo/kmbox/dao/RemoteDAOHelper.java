package com.evideo.kmbox.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public final class RemoteDAOHelper extends SQLiteOpenHelper implements
        DatabaseConstants {
    private static RemoteDAOHelper instance = null;
    private static Context mContext = null;
    public static RemoteDAOHelper getInstance() {
        if(instance == null)
        {
            synchronized (RemoteDAOHelper.class) {
                if (instance == null) {
                    instance = new RemoteDAOHelper(mContext, "kmbox.db", null, 1);
                }
            }
        }
        
        return instance;
    }

    public RemoteDAOHelper(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
        // TODO
        
    }
    
    public static synchronized void init(Context context) {
        mContext = new DatabaseContext(context);;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }

}
