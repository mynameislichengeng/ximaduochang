/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年9月5日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.songtop;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;
import com.evideo.kmbox.model.songtop.SongTopDetail;
import com.evideo.kmbox.widget.common.AutoEnlargeTextView;
import com.evideo.kmbox.widget.common.AutoHideTextView;
import com.evideo.kmbox.widget.common.AutoShowTextView;
import com.evideo.kmbox.widget.common.BaseSongListAdapter;
import com.evideo.kmbox.widget.common.ListMarqueeTextView;

import java.util.ArrayList;

/**
 * [功能说明]
 */
public class SongTopDetailAdapter extends BaseSongListAdapter<SongTopDetail> {

//    private String mSearchSpell = "";

    /**
     * [设置搜索首拼]
     *
     * @param searchSpell 搜索字母首拼
     */
   /* public void setSearchSpell(String searchSpell) {
        mSearchSpell = searchSpell;
    }*/

    private int mSingerColor;
    private int mOrderedSongColor;
    private int mOrderedSingerColor;
    private int mOriginalSpecWidth;
    private int mUnitWidth;
    private int mOffset;

    private int mSongNameHighlightColor;

    public SongTopDetailAdapter(Context context, ViewGroup parentView, ArrayList<SongTopDetail> datas) {
        super(context, parentView, datas);
        mOriginalSpecWidth = context.getResources().getDimensionPixelSize(R.dimen.order_song_list_item_spec_width);
        mSongNameHighlightColor = context.getResources().getColor(R.color.text_yellow);
        mSingerColor = context.getResources().getColor(R.color.text_light_gray);
        mOrderedSongColor = context.getResources().getColor(R.color.text_order_song);
        mOrderedSingerColor = context.getResources().getColor(R.color.text_alpha_order_song);
    }

    @Override
    protected View newConvertView(int position, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.song_top_detail_item, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mSn = (AutoEnlargeTextView) view.findViewById(R.id.song_list_item_sn);
        viewHolder.mSongName = (ListMarqueeTextView) view.findViewById(R.id.song_list_item_song_name);
        viewHolder.mOrderTime = (TextView) view.findViewById(R.id.song_order_time_tv);
        viewHolder.mScoreFlag = (TextView) view.findViewById(R.id.song_score_tv);
        viewHolder.mLocalExistFlag = (TextView) view.findViewById(R.id.song_local_exist_tv);
        viewHolder.mSingerName = (AutoHideTextView) view.findViewById(R.id.song_list_item_singer);
        viewHolder.mSingerNameBelow = (AutoShowTextView) view.findViewById(R.id.song_list_item_singer_below);

        viewHolder.mSn.setParentFocusedView(mParentView);
        viewHolder.mSingerName.setParentFocusedView(mParentView);
        viewHolder.mSingerNameBelow.setParentFocusedView(mParentView);
        viewHolder.mSongName.setParentFocusedView(mParentView);

        view.setTag(viewHolder);
        return view;
    }

    @Override
    protected void fillViewData(int position, View convertView) {
        if (convertView == null) {
            return;
        }
        SongTopDetail item = getItem(position);
        if (item == null) {
            return;
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
//        viewHolder.mSn.setText((position + 1) + " ");
        String songName = item.songName/*getName()*/;
        viewHolder.mSongName.setText(songName);

        if (item.orderRate > 10000) {
            double realOrder = (double) item.orderRate / 10000;
            viewHolder.mOrderTime.setText(String.format("%.1f", realOrder) + "万点播");
        } else {
            viewHolder.mOrderTime.setText(String.valueOf(item.orderRate) + "点播");
        }
        viewHolder.mSingerName.setText(item.singerName/*getSingerDescription()*/);
        viewHolder.mSingerNameBelow.setText(item.singerName/*getSingerDescription()*/);

        viewHolder.mSn.setParentFocusedView(mParentView);
        viewHolder.mSingerName.setParentFocusedView(mParentView);
        viewHolder.mSingerNameBelow.setParentFocusedView(mParentView);
        viewHolder.mSongName.setParentFocusedView(mParentView);

        final TextPaint paint = viewHolder.mSongName.getPaint();

        // 获取序号最高位所占宽度
        if (mUnitWidth <= 0) {
            mUnitWidth = (int) paint.measureText(getTopDigit(position + 1));
        }
        
        /*// 随着序号大于9（即position > 8）时，序号长度会增长，因此需要算出序号长度增长的偏移量
        if (position > 8) {
            viewHolder.mSn.setText("" + (position + 1));
            mOffset = (int) paint.measureText((position + 1) + "") - mUnitWidth;
            if (mOffset > 0) {
//                viewHolder.mSongName.setSpecWidth(mOriginalSpecWidth - mOffset);
                viewHolder.mSongName.setSpecWidth(mOriginalSpecWidth);
            }
        } else {
            viewHolder.mSn.setText("0" + (position + 1));
            viewHolder.mSongName.setSpecWidth(mOriginalSpecWidth);
        }
        viewHolder.mSongName.setWidth(mOriginalSpecWidth);*/
        // 随着序号大于9（即position > 8）时，序号长度会增长，因此需要算出序号长度增长的偏移量
        if (position > 8) {
            viewHolder.mSn.setText("" + (position + 1));
        } else {
            viewHolder.mSn.setText("0" + (position + 1));
        }

        viewHolder.mSongName.setSpecWidth(mOriginalSpecWidth);
        viewHolder.mSongName.setMaxWidth(mOriginalSpecWidth);

        viewHolder.mScoreFlag.setCompoundDrawablePadding(0);
        viewHolder.mScoreFlag.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        setSongOrderedState(viewHolder, item);
    }

    public void setSongNameSpecWidth(int specWidth) {
        mOriginalSpecWidth = specWidth;
    }

    private String getTopDigit(int num) {
        if (num > 0) {
            String numStr = String.valueOf(num);
            return numStr.charAt(0) + "";
        }
        return "0";
    }
    
    /*private void setSongHighlightState(ViewHolder viewHolder, SongTopDetail item) {
        if (TextUtils.isEmpty(mSearchSpell) || viewHolder == null || item == null) {
            return;
        }
        String name = item.songNamegetName();
        String spell = item.getSpell();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(spell)) {
            return;
        }
        int index = spell.indexOf(mSearchSpell);
        if (index < 0 || index + mSearchSpell.length() > name.length()) {
            return;
        }
        if (LanguageUtils.isPreCharAllNumOrChinese(name, index + mSearchSpell.length())) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(name);
            ssb.setSpan(new ForegroundColorSpan(mSongNameHighlightColor), 
                    index, index + mSearchSpell.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            viewHolder.mSongName.setText(ssb);
        }
    }*/

    /**
     * [设置歌曲为已点状态]
     *
     * @param song 歌曲对象
     */
    public void setSongOrdered(Song song) {
        if (song == null || mParentView == null) {
            return;
        }
        int childCount = mParentView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            int pos = mParentView.getChildAt(i).getId();
            if (pos >= this.getCount() || pos < 0) {
                continue;
            }
            SongTopDetail item = this.getItem(pos);
            if (item != null && item.songId/*getId()*/ == song.getId()) {
                setSongOrdered(mParentView.getChildAt(i));
                break;
            }
        }
    }

    /**
     * [设置歌曲为已点状态]
     *
     * @param convertView 歌曲所在view
     */
    private void setSongOrdered(View convertView) {
        if (convertView == null) {
            return;
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
//        viewHolder.mSn.setTextColor(mOrderedSongColor);
        viewHolder.mSongName.setTextColor(mOrderedSongColor);
        viewHolder.mSingerName.setTextColor(mOrderedSingerColor);
        viewHolder.mSingerNameBelow.setTextColor(mOrderedSingerColor);
    }

    /**
     * [功能说明]刷新item项的选中状态
     *
     * @param selected    true 选中 false 非选中
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
            SongTopDetail item = this.getItem(pos);
            ViewHolder viewHolder = (ViewHolder) mParentView.getChildAt(i).getTag();
            setSongOrderedState(viewHolder, item);
        }
    }

    private void setSongOrderedState(ViewHolder viewHolder, SongTopDetail item) {
        if (viewHolder == null || item == null) {
            return;
        }
//        if (PlayListManager.getInstance().isExistBySongId(item.getId())) {
        int posInPlayList = PlayListManager.getInstance().getPosBySongId(item.songId/*getId()*/);
        if (posInPlayList >= 0) {
//            viewHolder.mSn.setTextColor(mOrderedSongColor);
            viewHolder.mSongName.setTextColor(mOrderedSongColor);
            viewHolder.mSingerName.setTextColor(mOrderedSingerColor);
            viewHolder.mSingerNameBelow.setTextColor(mOrderedSingerColor);
            String nameHint = item.songName/*getName()*/;
            if (posInPlayList == 0) {
                nameHint += BaseApplication.getInstance().getResources().getString(R.string.song_name_in_orderlist_playing);
            } else {
                nameHint += BaseApplication.getInstance().getResources().getString(R.string.song_name_in_orderlist, posInPlayList);
                ;
            }
            viewHolder.mSongName.setText(nameHint);
        } else {
//            viewHolder.mSn.setTextColor(Color.WHITE);
            viewHolder.mSongName.setTextColor(Color.WHITE);
            viewHolder.mSingerName.setTextColor(mSingerColor);
            viewHolder.mSingerNameBelow.setTextColor(mSingerColor);
//            setSongHighlightState(viewHolder, item);
        }
    }

    /**
     * [适配器内容携带者]
     */
    private class ViewHolder {
        AutoEnlargeTextView mSn;
        ListMarqueeTextView mSongName;
        TextView mOrderTime;
        TextView mScoreFlag;
        TextView mLocalExistFlag;
        AutoHideTextView mSingerName;
        AutoShowTextView mSingerNameBelow;
    }
}
