package com.evideo.kmbox.widget.playerlist;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.playerctrl.sunglist.SungListItem;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.BaseSongListAdapter;
import com.evideo.kmbox.widget.common.ListMarqueeTextView;

/**
 * [已唱列表适配器]
 */
public class SungListAdapter extends BaseSongListAdapter<SungListItem> {
    
    private RelativeLayout.LayoutParams mSingerLayoutParams;
    private int mSingerMarginWithDrawable;
    private int mSingerMarginNoDrawable;

    public SungListAdapter(Context context, ViewGroup parentView,
            ArrayList<SungListItem> datas) {
        super(context, parentView, datas);
        mSingerMarginWithDrawable = context.getResources()
                .getDimensionPixelSize(R.dimen.selected_sung_list_singer_margin_top_with_drawable);
        mSingerMarginNoDrawable = context.getResources().
                getDimensionPixelSize(R.dimen.selected_sung_list_singer_margin_top_no_drawable);
    }

    @Override
    protected View newConvertView(int position, ViewGroup parent) {

        View view = View.inflate(mContext, R.layout.sung_list_item, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mSn = (TextView) view.findViewById(R.id.sung_list_item_sn);
        viewHolder.mUnderSn = (TextView) view.findViewById(R.id.song_list_item_sn_under);
        viewHolder.mSongName = (ListMarqueeTextView) view.findViewById(R.id.sung_list_item_song_name);
        viewHolder.mSingerAndScore = (TextView) view.findViewById(R.id.sung_list_item_singer_and_score);
//        viewHolder.mScore = (TextView) view.findViewById(R.id.sung_list_item_score);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    protected void fillViewData(int position, View convertView) {
        if (convertView == null) {
            return;
        }
        
        Resources res = BaseApplication.getInstance().getResources();
        SungListItem item = null;
        try {
            item = getItem(position);
        } catch (Exception e) {
            EvLog.e("item not exists");
        }
            
        if (item == null) {
            return;
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.mSn.setText((position + 1) +  "");
        viewHolder.mSongName.setText(item.getSongName()); 
        
        String singer = item.getSingerDescription();
        viewHolder.mSingerAndScore.setText(item.getSingerDescription());
        mSingerLayoutParams = (LayoutParams) viewHolder.mSingerAndScore.getLayoutParams();
        
        mSingerLayoutParams.topMargin = mSingerMarginNoDrawable;
        viewHolder.mSingerAndScore.setLayoutParams(mSingerLayoutParams);
        viewHolder.mSingerAndScore.setCompoundDrawablePadding(0);
        viewHolder.mSingerAndScore.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        viewHolder.mUnderSn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

    }
    
    
    /**
     * [listview内容携带者]
     */
    private class ViewHolder {
        TextView mSn;
        TextView mUnderSn;
        ListMarqueeTextView mSongName;
        TextView mSingerAndScore;
        TextView mScore;
    }
}
