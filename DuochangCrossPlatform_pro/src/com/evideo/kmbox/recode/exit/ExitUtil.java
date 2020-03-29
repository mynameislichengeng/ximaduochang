package com.evideo.kmbox.recode.exit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.evideo.kmbox.R;

public class ExitUtil {


    /**
     * x的范围
     *
     * @param context
     * @return
     */
    public static int getExitXMax(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.px50);
    }

    public static int getExitMoveX() {
        return 10;
    }


}
