/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年10月7日     "dinghaiqiang"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.selected;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.widget.common.AutoEnlargeTextView;
import com.evideo.kmbox.widget.common.AutoHideTextView;
import com.evideo.kmbox.widget.common.AutoShowTextView;
import com.evideo.kmbox.widget.common.BaseSongListAdapter;
import com.evideo.kmbox.widget.common.ListMarqueeTextView;
import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;

/**
 * [已点列表适配器]
 */
public class SelectedListAdapter extends BaseSongListAdapter<KmPlayListItem> {
    
    private int mSpecWidthNormal;
    private int mSpecWidthLonger;
    private int mOrderedSongColor;
    private int mOrderedSingerColor;
    private String mPlayingFlag = "";
    private int mNormalSongColor;
    private int mNormalSingerColor;
    public SelectedListAdapter(Context context, ViewGroup parentView,
            ArrayList<KmPlayListItem> datas) {
        super(context, parentView, datas);
        mSpecWidthNormal = context.getResources().
                getDimensionPixelSize(R.dimen.px1200);
        mSpecWidthLonger = context.getResources().
                getDimensionPixelSize(R.dimen.px1300);
        mOrderedSongColor = context.getResources().getColor(R.color.text_order_song);
        mOrderedSingerColor = context.getResources().getColor(R.color.text_alpha_order_song);
        mNormalSongColor = context.getResources().getColor(R.color.text_white);
        mNormalSingerColor = context.getResources().getColor(R.color.text_light_gray);
        mPlayingFlag = context.getResources().getString(R.string.playing_flag);
    }
    
    public void setSongNameWidth(int normalWidth,int highlightWidth) {
        mSpecWidthNormal = normalWidth;
        mSpecWidthLonger = highlightWidth;
    }
    
    @Override
    protected View newConvertView(int position, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.selected_list_item_new, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mSn = (AutoEnlargeTextView) view.findViewById(R.id.song_list_item_sn);
        viewHolder.mSongName = (ListMarqueeTextView) view.findViewById(R.id.song_list_item_song_name);
        viewHolder.mSingerName = (AutoHideTextView) view.findViewById(R.id.song_list_item_singer);
        viewHolder.mSingerNameBelow = (AutoShowTextView) view.findViewById(R.id.song_list_item_singer_below);
//        viewHolder.mPlayingIcon = (ImageView) view.findViewById(R.id.song_list_item_tip_icon);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    protected void fillViewData(int position, View convertView) {
        if (convertView == null) {
            return;
        }

        KmPlayListItem item = getItem(position);
        
        if (item == null) {
            return;
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        if (position == 0) {
            viewHolder.mSn.setVisibility(View.VISIBLE);
            viewHolder.mSn.setText(mPlayingFlag);
          /*  viewHolder.mSn.setVisibility(View.GONE);
            viewHolder.mPlayingIcon.setImageResource(R.drawable.selected_song_playing);
            viewHolder.mPlayingIcon.setVisibility(View.VISIBLE);*/
        } else {
//            viewHolder.mPlayingIcon.setVisibility(View.GONE);
            viewHolder.mSn.setVisibility(View.VISIBLE);
            if (position > 9) {
                viewHolder.mSn.setText("" + position);
            } else {
                viewHolder.mSn.setText("0" + position);
            }
        }    
        
        viewHolder.mSongName.setText(item.getSong().getName());    
        viewHolder.mSingerName.setText(item.getSong().getSingerDescription());
        viewHolder.mSingerNameBelow.setText(item.getSong().getSingerDescription());
        
        viewHolder.mSn.setParentFocusedView(mParentView);
        viewHolder.mSingerName.setParentFocusedView(mParentView);
        viewHolder.mSingerNameBelow.setParentFocusedView(mParentView);
        viewHolder.mSongName.setParentFocusedView(mParentView);
        
        if (position == 0 || position == 1) {
            viewHolder.mSongName.setSpecWidth(mSpecWidthLonger);
            viewHolder.mSongName.setWidth(mSpecWidthLonger);
        } else {
            viewHolder.mSongName.setSpecWidth(mSpecWidthNormal);
            viewHolder.mSongName.setSpecWidth(mSpecWidthNormal);
        } 
        
        int posInPlayList = PlayListManager.getInstance().getPosBySongId(item.getSong().getId());
        if (posInPlayList == 0) {
            viewHolder.mSn.setTextColor(mOrderedSongColor);
            viewHolder.mSongName.setTextColor(mOrderedSongColor);
            viewHolder.mSingerName.setTextColor(mOrderedSingerColor);
            viewHolder.mSingerNameBelow.setTextColor(mOrderedSingerColor);
            String nameHint = item.getSong().getName();
            nameHint += BaseApplication.getInstance().getResources().getString(R.string.song_name_in_orderlist_playing);
            viewHolder.mSongName.setText(nameHint);
        } else {
            viewHolder.mSn.setTextColor(mNormalSongColor);
            viewHolder.mSongName.setTextColor(mNormalSongColor);
            viewHolder.mSingerName.setTextColor(mNormalSingerColor);
            viewHolder.mSingerNameBelow.setTextColor(mNormalSingerColor);
            viewHolder.mSongName.setText(item.getSong().getName());   
            viewHolder.mSingerName.setText(item.getSong().getSingerDescription());
        }
        viewHolder.mSingerName.setCompoundDrawablePadding(0);
        viewHolder.mSingerName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        viewHolder.mSingerNameBelow.setCompoundDrawablePadding(0);
        viewHolder.mSingerNameBelow.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        viewHolder.mSongName.reset();
    }
    
    /**
     * [功能说明]刷新item项的选中状态
     * @param selected true 选中 false 非选中
     * @param selectedPos 选中的位置
     */
    public void refreshSelectedState(boolean selected, int selectedPos) {
        if (mParentView == null) {
            return;
        }
        if (selectedPos >= getCount() || selectedPos < 0) {
            return;
        }
        int childCount = mParentView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            int pos = mParentView.getChildAt(i).getId();
            if (pos == selectedPos) {
                ViewHolder viewHolder = (ViewHolder) mParentView.getChildAt(i).getTag();
                viewHolder.mSn.setParentViewFocused(selected);
                viewHolder.mSingerName.setParentViewFocused(selected);
                viewHolder.mSingerNameBelow.setParentViewFocused(selected);
                viewHolder.mSongName.setParentViewFocused(selected);
                break;
            }
        }
    }
    
    /**
     * [适配器内容携带者]
     */
    private class ViewHolder {
//        ImageView mPlayingIcon;
        AutoEnlargeTextView mSn;
        ListMarqueeTextView mSongName;
        AutoHideTextView mSingerName;
        AutoShowTextView mSingerNameBelow;
    }
}