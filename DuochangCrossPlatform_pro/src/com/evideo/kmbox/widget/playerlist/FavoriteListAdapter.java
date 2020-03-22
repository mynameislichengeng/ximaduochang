/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date                 Author            Version  Description
 *  -----------------------------------------------
 *  2015年3月16日              "wurongquan"       1.0      [修订说明]
 *
 */

package com.evideo.kmbox.widget.playerlist;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.widget.common.BaseSongListAdapter;
import com.evideo.kmbox.widget.common.ListMarqueeTextView;
import com.evideo.kmbox.R;

/**
 * [收藏列表适配器]
 */
public class FavoriteListAdapter extends BaseSongListAdapter<Song> {
    
    private LinearLayout.LayoutParams mSingerLayoutParams;
    private int mSpecWidthNormal;
    private int mSingerMarginWithDrawable;
    private int mSingerMarginNoDrawable;
    /**
     * @param context
     * @param parentView
     * @param datas
     */
    public FavoriteListAdapter(Context context, ViewGroup parentView,
            ArrayList<Song> datas) {
        super(context, parentView, datas);
        mSpecWidthNormal = context.getResources().
                getDimensionPixelSize(R.dimen.px1343);
        mSingerMarginWithDrawable = context.getResources()
                .getDimensionPixelSize(R.dimen.px_12);
        mSingerMarginNoDrawable = context.getResources().
                getDimensionPixelSize(R.dimen.px_6);
        

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected View newConvertView(int position, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.favorite_list_item, null);
        ViewHolder viewHolder = new ViewHolder();
//        viewHolder.mSn = (TextView) view.findViewById(R.id.favorite_list_item_sn);
//        viewHolder.mSnLay = view.findViewById(R.id.favorite_list_item_sn_lay);
//        viewHolder.mSongName = (ListMarqueeTextView) view.findViewById(R.id.favorite_list_item_song_name);
//        viewHolder.mSingerName = (TextView) view.findViewById(R.id.favorite_list_item_singer);
//        viewHolder.mUnderSn = (TextView) view.findViewById(R.id.favorite_list_item_sn_under);
        view.setTag(viewHolder);
        return view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillViewData(int position, View convertView) {
        if (convertView == null) {
            return;
        }

        Song item = getItem(position);
        
        if (item == null) {
            return;
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.mSn.setText(position + 1 + "");
        viewHolder.mSnLay.setVisibility(View.VISIBLE);       
        viewHolder.mSongName.setText(item.getName());    
        viewHolder.mSingerName.setText(item.getSingerDescription());
        viewHolder.mSongName.setSpecWidth(mSpecWidthNormal);
   
        mSingerLayoutParams = (LayoutParams) viewHolder.mSingerName.getLayoutParams();
        mSingerLayoutParams.topMargin = mSingerMarginNoDrawable;
        viewHolder.mSingerName.setLayoutParams(mSingerLayoutParams);
        viewHolder.mSingerName.setCompoundDrawablePadding(0);
        viewHolder.mSingerName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        viewHolder.mUnderSn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        viewHolder.mSongName.reset();
    }

    /**
     * [适配器内容携带者]
     */
    private class ViewHolder {
        TextView mSn;
        TextView mUnderSn;
        ListMarqueeTextView mSongName;
        TextView mSingerName;
        View mSnLay;
    }
}
