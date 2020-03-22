package com.evideo.kmbox.widget.msgview;

import android.content.Context;
import android.widget.LinearLayout;
import com.evideo.kmbox.BaseApplication;

/**
 * @brief : [文件功能说明]
 */
public class KmOSDMessageManager {

    public static boolean instanceFlag = false; // true if 1 instance

    private KmOSDMessageView mOsdMessageView = null;
    private LinearLayout mParentView = null;

    private KmOSDMessageManager() {
    }

    private static KmOSDMessageManager instance = null;


    public static KmOSDMessageManager getInstance() {
        if (instance == null) {
            synchronized (KmOSDMessageManager.class) {
                KmOSDMessageManager temp = instance;
                if (temp == null) {
                    temp = new KmOSDMessageManager();
                    instance = temp;
                }
            }
        }
        return instance;
    }

    @Override
    public void finalize() {
        instanceFlag = false;
        instance = null;
    }

    public void initViewRes(LinearLayout view, Context c) {
        mParentView = view;
        mOsdMessageView = new KmOSDMessageView(view, c);
    }

    public void sendKMMessageDrawView(KmOSDMessage type) {
        BaseApplication.getHandler().post(new Runnable() {

            @Override
            public void run() {
                mParentView.removeAllViews();
                mParentView.addView(mOsdMessageView);
            }
        });
        if (mOsdMessageView != null) {
            mOsdMessageView.sendKMMessageDrawView(type);
        }
    }

    public KmOSDMessageView getKmOSDMessageView() {
        if (mOsdMessageView != null) {
            return mOsdMessageView;
        }
        return null;
    }

    public void hideKmMessageDrawView(KmOSDMessage type) {
        if (mOsdMessageView != null) {
            mOsdMessageView.hideView(type);
        }
    }

    public void removeKmMessageDrawView(int id) {
        if (mOsdMessageView != null) {
            mOsdMessageView.removeKmMessageDrawView(id);
        }
    }
}