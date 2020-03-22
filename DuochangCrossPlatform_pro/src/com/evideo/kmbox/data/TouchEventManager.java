package com.evideo.kmbox.data;

import android.view.MotionEvent;

import com.evideo.kmbox.model.touch.TouchPostionParam;

public class TouchEventManager {

    public static boolean isTouchCommon(TouchPostionParam touchPostionParam, MotionEvent event) {
        if (touchPostionParam == null) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();

        float left = touchPostionParam.getLeft();
        float right = touchPostionParam.getRight();
        float up = touchPostionParam.getUp();
        float down = touchPostionParam.getDown();


        if (left < x && x < right) {
            if (up < y && y < down) {
                return true;
            }
        }
        return false;
    }
}
