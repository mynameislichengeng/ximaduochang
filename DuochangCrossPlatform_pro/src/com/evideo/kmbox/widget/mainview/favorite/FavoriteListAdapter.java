/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-10-5     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.favorite;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;
import com.evideo.kmbox.widget.common.AutoEnlargeTextView;
import com.evideo.kmbox.widget.common.AutoHideTextView;
import com.evideo.kmbox.widget.common.AutoShowTextView;
import com.evideo.kmbox.widget.common.BaseSongListAdapter;
import com.evideo.kmbox.widget.common.ListMarqueeTextView;
import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;

/**
 * [功能说明]
 */
public class FavoriteListAdapter extends BaseSongListAdapter<Integer> {
    
    private int mSingerColor;
    private int mOrderedSongColor;
    private int mOrderedSingerColor;
    private int mOriginalSpecWidth;

    /**
     * @param context
     * @param parentView
     * @param datas
     */
    public FavoriteListAdapter(Context context, ViewGroup parentView,
            ArrayList<Integer> datas) {
        super(context, parentView, datas);
        mOriginalSpecWidth = context.getResources().getDimensionPixelSize(R.dimen.px1343);
        mSingerColor = context.getResources().getColor(R.color.text_light_gray);
        mOrderedSongColor = context.getResources().getColor(R.color.text_order_song);
        mOrderedSingerColor = context.getResources().getColor(R.color.text_alpha_order_song);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected View newConvertView(int position, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.favorite_list_item, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mSn = (AutoEnlargeTextView) view.findViewById(R.id.song_list_item_sn);
        viewHolder.mSongName = (ListMarqueeTextView) view.findViewById(R.id.song_list_item_song_name);
        viewHolder.mScoreFlag = (TextView) view.findViewById(R.id.song_score_tv);
        viewHolder.mLocalExistFlag = (TextView) view.findViewById(R.id.song_local_exist_tv);
        viewHolder.mSingerName = (AutoHideTextView) view.findViewById(R.id.song_list_item_singer);
        viewHolder.mSingerNameBelow = (AutoShowTextView) view.findViewById(R.id.song_list_item_singer_below);
        
        viewHolder.mSn.setParentFocusedView(mParentView);
        viewHolder.mSongName.setParentFocusedView(mParentView);
        viewHolder.mSingerName.setParentFocusedView(mParentView);
        viewHolder.mSingerNameBelow.setParentFocusedView(mParentView);
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
        Integer item = getItem(position);
        Song song = SongManager.getInstance().getSongById(item);
        if (song == null) {
            return;
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
//        viewHolder.mSn.setText((position + 1) + " ");
        String songName = song.getName();
        viewHolder.mSongName.setText(songName);
        viewHolder.mSingerName.setText(song.getSingerDescription());
        viewHolder.mSingerNameBelow.setText(song.getSingerDescription());
        
//        final TextPaint paint = viewHolder.mSongName.getPaint();
        
        // 随着序号大于9（即position > 8）时，序号长度会增长，因此需要算出序号长度增长的偏移量
        if (position > 8) {
            viewHolder.mSn.setText("" + (position + 1));
        } else {
            viewHolder.mSn.setText("0" + (position + 1));
        }
        viewHolder.mSongName.setMaxWidth(mOriginalSpecWidth);
        viewHolder.mSongName.setSpecWidth(mOriginalSpecWidth);
        
       /* if (song.hasCachedMedia()) {
            viewHolder.mLocalExistFlag.setCompoundDrawablePadding(3);
            viewHolder.mLocalExistFlag.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.song_flag_save_local, 0, 0, 0);
        } else*/ {
            viewHolder.mLocalExistFlag.setCompoundDrawablePadding(0);
            viewHolder.mLocalExistFlag.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, 0, 0);
        }

        viewHolder.mSongName.setCompoundDrawablePadding(0);
        viewHolder.mSongName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        setSongOrderedState(viewHolder, song);
    }
    
    private void setSongOrderedState(ViewHolder viewHolder, Song item) {
        if (viewHolder == null || item == null) {
            return;
        }
//        if (PlayListManager.getInstance().isExistBySongId(item.getId())) {
        int posInPlayList = PlayListManager.getInstance().getPosBySongId(item.getId());
        if (posInPlayList >= 0) {
//            viewHolder.mSn.setTextColor(mOrderedSongColor);
            viewHolder.mSongName.setTextColor(mOrderedSongColor);
            viewHolder.mSingerName.setTextColor(mOrderedSingerColor);
            viewHolder.mSingerNameBelow.setTextColor(mOrderedSingerColor);
            String nameHint = item.getName();
            if (posInPlayList == 0) {
                nameHint += BaseApplication.getInstance().getResources().getString(R.string.song_name_in_orderlist_playing);
            } else {
                nameHint += BaseApplication.getInstance().getResources().getString(R.string.song_name_in_orderlist, posInPlayList);;
            }
            viewHolder.mSongName.setText(nameHint);
        } else {
//            viewHolder.mSn.setTextColor(Color.WHITE);
            viewHolder.mSongName.setTextColor(Color.WHITE);
            viewHolder.mSingerName.setTextColor(mSingerColor);
            viewHolder.mSingerNameBelow.setTextColor(mSingerColor);
        }
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
     * [刷新已点状态]
     */
    public void refreshOrderedState() {
        if (mParentView == null) {
            return;
        }
        int childCount = mParentView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            int pos = mParentView.getChildAt(i).getId();
            if (pos >= this.getCount() || pos < 0) {
                continue;
            }
            Integer item = this.getItem(pos);
            ViewHolder viewHolder = (ViewHolder) mParentView.getChildAt(i).getTag();
            Song song = SongManager.getInstance().getSongById(item);
            if (song == null) {
                return;
            }
            setSongOrderedState(viewHolder, song);
        }
    }
    
    /**
     * [适配器内容携带者]
     */
    private class ViewHolder {
        AutoEnlargeTextView mSn;
        ListMarqueeTextView mSongName;
        TextView mScoreFlag;
        TextView mLocalExistFlag;
        AutoHideTextView mSingerName;
        AutoShowTextView mSingerNameBelow;
    }

}
