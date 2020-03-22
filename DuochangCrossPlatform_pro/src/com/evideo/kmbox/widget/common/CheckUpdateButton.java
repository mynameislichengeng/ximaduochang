package com.evideo.kmbox.widget.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.evideo.kmbox.R;

/*
 *    The MIT License (MIT)
 *
 *   Copyright (c) 2014 Danylyk Dmytro
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

public class CheckUpdateButton extends ProcessButton {
    
    private boolean focusFlag = false;
    private int mFocusPadding;

    public CheckUpdateButton(Context context) {
        super(context);
        init(context);
    }

    public CheckUpdateButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CheckUpdateButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mFocusPadding = context.getResources().getDimensionPixelSize(R.dimen.common_focus_padding);
    }

    @Override
    public void drawProgress(Canvas canvas) {
        float scale = (float) getProgress() / (float) getMaxProgress();
        float indicatorWidth = (float) (getMeasuredWidth()) * scale;

        Drawable drawable = getResources().getDrawable(R.drawable.button_green_about_check_update).mutate();
        drawable.setBounds(0, 0, 
        		(int) indicatorWidth, getMeasuredHeight());
        drawable.draw(canvas);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
            Rect previouslyFocusedRect) {
        focusFlag = focused;
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawFocus(canvas);
    }
    
    private void drawFocus(Canvas canvas) {
        if (focusFlag) {
            Drawable drawable = getResources().getDrawable(R.drawable.focus_frame_new);
            drawable.setBounds(-mFocusPadding, -mFocusPadding, 
            		getWidth() + mFocusPadding, getHeight() + mFocusPadding);
            drawable.draw(canvas);
        }
    }
}
