package com.evideo.kmbox.model.player;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 默认的RenderView
 * 该类的目的是修改RenderView的实现而不影响应用层,只需应用层重新编译即可
 */
public class DefaultVideoRenderView extends FrameLayout {


    private void config() {
        setBackgroundColor(0xFF000000);
    }

    public DefaultVideoRenderView(Context context) {
        super(context);
        config();
    }

    public DefaultVideoRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        config();
    }

    public DefaultVideoRenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        config();
    }


    /* public DefaultVideoRenderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        config();
    }*/
}
