package com.evideo.kmbox.widget.common;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * [点歌动画控制者]
 */
public final class OrderSongAnimController {
    
    private OrderSongAnimController() { }
    
    private static OrderSongAnimController sInstance;
    
    public static OrderSongAnimController getInstance() {
        if (sInstance == null) {
            sInstance = new OrderSongAnimController();
        }
        return sInstance;
    }
    
    private OrderSongAnimView mOrderSongAnimView;
    
    public OrderSongAnimView init(Context context) {
        mOrderSongAnimView = new OrderSongAnimView(context);
        return mOrderSongAnimView;
    }
    
    public void startOrderSongAnim(Bitmap bitmap, float x, float y, int fromView) {
        if (mOrderSongAnimView != null) {
            mOrderSongAnimView.startOrderSongAnim(bitmap, x, y, fromView);
        }
    }

}
