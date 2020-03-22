package com.evideo.kmbox.widget.common;

import com.evideo.kmbox.R;
import com.evideo.kmbox.util.EvLog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class MaskFocusImageButton extends ImageButton {
    
    private boolean focusFlag = false;
    
    private boolean forceFocus = false;
    
    public boolean isForceFocus() {
        return forceFocus;
    }

    public void setForceFocus(boolean forceFocus) {
        this.forceFocus = forceFocus;
        invalidate();
    }

    public MaskFocusImageButton(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    public MaskFocusImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaskFocusImageButton(Context context) {
        super(context);
    }
    
    @Override
    protected void onFocusChanged(boolean focused, int direction,
            Rect previouslyFocusedRect) {
        EvLog.d("something", "MaskFocusButton: gainFocus: " + focused + " direction: " + direction);
        focusFlag = focused;
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawFocus(canvas);
    }
    
    private void drawFocus(Canvas canvas) {
        if((focusFlag && !isPressed()) || forceFocus) {
            Drawable drawable = getResources().getDrawable(R.drawable.focus_frame_new);
            drawable.setBounds(0, 0, getWidth(), getHeight());
            drawable.draw(canvas);
        }
    }

}
