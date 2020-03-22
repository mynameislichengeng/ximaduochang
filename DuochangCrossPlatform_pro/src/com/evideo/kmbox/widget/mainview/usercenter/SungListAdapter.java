/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-11-5     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.usercenter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;
import com.evideo.kmbox.model.playerctrl.sunglist.SungListItem;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.TimeUtil;
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
public class SungListAdapter extends BaseSongListAdapter<SungListItem> {
    
    private int mSingerColor;
    private int mOrderedSongColor;
    private int mOrderedSingerColor;
    private int mOriginalSpecWidth;

    /**
     * @param context
     * @param parentView
     * @param datas
     */
    public SungListAdapter(Context context, ViewGroup parentView,
            ArrayList<SungListItem> datas) {
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
        View view = View.inflate(mContext, R.layout.record_list_item, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mSn = (AutoEnlargeTextView) view.findViewById(R.id.song_list_item_sn);
        viewHolder.mSongName = (ListMarqueeTextView) view.findViewById(R.id.song_list_item_song_name);
        viewHolder.mScoreFlag = (TextView)view.findViewById(R.id.song_score_tv);
        viewHolder.mProgress = (ProgressBar) view.findViewById(R.id.record_uploading_pb);
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
        SungListItem item = getItem(position);
        if (item == null) {
            return;
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
//        viewHolder.mSn.setText((position + 1) + " ");
        String songName = item.getSongName();
        viewHolder.mSongName.setText(songName);
        viewHolder.mSingerName.setText(item.getSingerDescription());
        viewHolder.mSingerNameBelow.setText(item.getSingerDescription());
        
        // 随着序号大于9（即position > 8）时，序号长度会增长，因此需要算出序号长度增长的偏移量
        if (position > 8) {
            viewHolder.mSn.setText("" + (position + 1));
        } else {
            viewHolder.mSn.setText("0" + (position + 1));
        }
        viewHolder.mSongName.setSpecWidth(mOriginalSpecWidth);
        
        viewHolder.mScoreFlag.setCompoundDrawablePadding(0);
        viewHolder.mScoreFlag.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        setSongOrderedState(viewHolder, item);
    }
    
    private void setSongOrderedState(ViewHolder viewHolder, SungListItem item) {
        if (viewHolder == null || item == null) {
            return;
        }
        /*if (PlayListManager.getInstance().isExistBySongId(item.getSongId())) {*/
        int posInPlayList = PlayListManager.getInstance().getPosBySongId(item.getSongId());
        if (posInPlayList >= 0) {
            viewHolder.mSongName.setTextColor(mOrderedSongColor);
            viewHolder.mSingerName.setTextColor(mOrderedSingerColor);
            viewHolder.mSingerNameBelow.setTextColor(mOrderedSingerColor);
            String nameHint = item.getSongName();
            if (posInPlayList == 0) {
                nameHint += BaseApplication.getInstance().getResources().getString(R.string.song_name_in_orderlist_playing);
            } else {
                nameHint += BaseApplication.getInstance().getResources().getString(R.string.song_name_in_orderlist, posInPlayList);;
            }
            viewHolder.mSongName.setText(nameHint);
        } else {
            viewHolder.mSongName.setTextColor(Color.WHITE);
            viewHolder.mSingerName.setTextColor(mSingerColor);
            viewHolder.mSingerNameBelow.setTextColor(mSingerColor);
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
            SungListItem item = this.getItem(pos);
            ViewHolder viewHolder = (ViewHolder) mParentView.getChildAt(i).getTag();
            setSongOrderedState(viewHolder, item);
        }
    }
    
    
    /**
     * [适配器内容携带者]
     */
    private class ViewHolder {
        AutoEnlargeTextView mSn;
        ListMarqueeTextView mSongName;
        TextView mScoreFlag;
        ProgressBar mProgress;
        AutoHideTextView mSingerName;
        AutoShowTextView mSingerNameBelow;
    }
}
