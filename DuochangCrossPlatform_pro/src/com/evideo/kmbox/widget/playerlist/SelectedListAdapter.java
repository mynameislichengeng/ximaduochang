package com.evideo.kmbox.widget.playerlist;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.evideo.kmbox.R;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.widget.common.BaseSongListAdapter;
import com.evideo.kmbox.widget.common.ListMarqueeTextView;

/**
 * [已点列表适配器]
 */
public class SelectedListAdapter extends BaseSongListAdapter<KmPlayListItem> {
    
    private LinearLayout.LayoutParams mSingerLayoutParams;
    private int mSpecWidthNormal;
    private int mSpecWidthLonger;
    private int mSingerMarginWithDrawable;
    private int mSingerMarginNoDrawable;
    
    public SelectedListAdapter(Context context, ViewGroup parentView,
            ArrayList<KmPlayListItem> datas) {
        super(context, parentView, datas);
        mSpecWidthNormal = context.getResources().
                getDimensionPixelSize(R.dimen.selected_list_item_spec_width);
        mSpecWidthLonger = context.getResources().
                getDimensionPixelSize(R.dimen.selected_list_item_spec_width_longer);
        mSingerMarginWithDrawable = context.getResources()
                .getDimensionPixelSize(R.dimen.selected_sung_list_singer_margin_top_with_drawable);
        mSingerMarginNoDrawable = context.getResources().
                getDimensionPixelSize(R.dimen.selected_sung_list_singer_margin_top_no_drawable);
    }
    
    @Override
    protected View newConvertView(int position, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.selected_list_item, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mSn = (TextView) view.findViewById(R.id.song_list_item_sn);
        viewHolder.mSnLay = view.findViewById(R.id.song_list_item_sn_lay);
        viewHolder.mSongName = (ListMarqueeTextView) view.findViewById(R.id.song_list_item_song_name);
        viewHolder.mSingerName = (TextView) view.findViewById(R.id.song_list_item_singer);
        viewHolder.mPlayingIcon = (ImageView) view.findViewById(R.id.song_list_item_tip_icon);
        viewHolder.mUnderSn = (TextView) view.findViewById(R.id.song_list_item_sn_under);
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
            viewHolder.mSnLay.setVisibility(View.GONE);
            viewHolder.mPlayingIcon.setImageResource(R.drawable.selected_song_playing);
            viewHolder.mPlayingIcon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mPlayingIcon.setVisibility(View.GONE);
            viewHolder.mSn.setText(position + "");
            viewHolder.mSnLay.setVisibility(View.VISIBLE);
        }    
        
        viewHolder.mSongName.setText(item.getSongName());    
        viewHolder.mSingerName.setText(item.getSingerName());
        
        if (position == 0 || position == 1) {
            viewHolder.mSongName.setSpecWidth(mSpecWidthLonger);
        } else {
            viewHolder.mSongName.setSpecWidth(mSpecWidthNormal);
        }
        
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
        ImageView mPlayingIcon;
        View mSnLay;
    }
}
