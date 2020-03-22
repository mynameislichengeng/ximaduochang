package com.evideo.kmbox.widget.common;

import com.evideo.kmbox.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

public class KmToggleButtonBig extends ToggleButton {

    public KmToggleButtonBig(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KmToggleButtonBig(Context context) {
        super(context);
    }
    
    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        syncTextAndBackGroundState(checked);
    }
    
    private void syncTextAndBackGroundState(boolean checked) {
        if(checked){
            setBackgroundResource(R.drawable.toggle_btn_open_big);
//            setText("");
        }else{
            setBackgroundResource(R.drawable.toggle_btn_close_big);
//            setText("");
        }
    }

}
