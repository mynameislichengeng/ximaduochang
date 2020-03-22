package com.evideo.kmbox.widget.common;

import android.view.View;
import android.widget.AdapterView;

public interface OnItemClickCallback {
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id, int itemState);
}
