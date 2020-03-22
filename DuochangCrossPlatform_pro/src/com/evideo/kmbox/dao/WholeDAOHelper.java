/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-7-27     "liuyantao"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * [全库数据库]
 */
public class WholeDAOHelper extends SQLiteOpenHelper implements
        DatabaseConstants {
    private static WholeDAOHelper sInstance = null;
    private static Context mContext = null;

    /**
     * [单例]
     * @return 单例
     */
    public static WholeDAOHelper getInstance() {
        if (sInstance == null) {
            synchronized (WholeDAOHelper.class) {
                if (sInstance == null) {
                    sInstance = new WholeDAOHelper(mContext, "whole_kmbox.db", null, 4);
                }
            }
        }

        return sInstance;
    }

    public WholeDAOHelper(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
        // TODO
    }

    /**
     * [init]
     * @param context
     */
    public static synchronized void init(Context context) {
        mContext = new DatabaseContext(context);
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
