
package com.evideo.kmbox.widget.mainview.globalsearch;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.model.dao.data.SingerManager;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;
import com.evideo.kmbox.util.DimensionsUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.LanguageUtils;
import com.evideo.kmbox.util.NetUtils;
import com.evideo.kmbox.widget.common.AutoEnlargeTextView;
import com.evideo.kmbox.widget.common.AutoHideTextView;
import com.evideo.kmbox.widget.common.AutoShowTextView;
import com.evideo.kmbox.widget.common.BaseSongListAdapter;
import com.evideo.kmbox.widget.common.CircleImageView;
import com.evideo.kmbox.widget.common.ListMarqueeTextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * [搜索历史记录的适配器]
 */
public class SearchHistoryAdapter extends BaseSongListAdapter<SearchHistoryItem> {
    
    private String mSearchSpell = "";
    private String mPictureHead = "";
    private DisplayImageOptions mOptions;

    private int mSingerColor;
    private int mOrderedSongColor;
    private int mOrderedSingerColor;
    private int mOriginalSpecWidth;
    private int mUnitWidth;
    private int mOffset;
    
    private int mSongNameHighlightColor;

    public SearchHistoryAdapter(Context context, ViewGroup parentView,
            ArrayList<SearchHistoryItem> datas) {
        super(context, parentView, datas);
        mOriginalSpecWidth = context.getResources().getDimensionPixelSize(R.dimen.order_song_list_item_spec_width);
        mSongNameHighlightColor = context.getResources().getColor(R.color.text_yellow);
        mSingerColor = context.getResources().getColor(R.color.text_light_gray);
        mOrderedSongColor = context.getResources().getColor(R.color.text_order_song);
        mOrderedSingerColor = context.getResources().getColor(R.color.text_alpha_order_song);
        setImageOptions();
        EvLog.i("wrq","create searchhistory adapter");
    } 
    
    public void setSingerIconUrlHead(String urlHead) {
    	mPictureHead = urlHead;
    }
    
    @Override
    public int getViewTypeCount() {
        return 3;
    }
    
    @Override
    public int getItemViewType(int position) {
        SearchHistoryItem item = getItem(position);
        if (item == null) {
            return 1;
        }
        return item.mItemType;
    }

    /**
     * [歌曲部分viewholder]
     */
    private class ViewHolderSong {
        
        AutoEnlargeTextView mSn;
        ListMarqueeTextView mSongName;
        AutoHideTextView mSingerName;
        AutoShowTextView mSingerNameBelow;
    }
    
    /**
     * [歌星部分viewholder]
     *
     */
    private class ViewHolderSinger {
        AutoEnlargeTextView mSn;
        AutoEnlargeTextView mSingerName;
        CircleImageView mSingerCover;
        AutoEnlargeTextView mSingerSongNum;
    }

    @Override
    protected View newConvertView(int position, ViewGroup parent) {
//        EvLog.i("wrq", "search history item pos:" + position);
        switch(getItemViewType(position)) {
            case SearchHistoryItem.SEARCH_ITEM_TYPE_SONG:
                View view = View.inflate(mContext, R.layout.song_list_item_new, null);
                ViewHolderSong viewHolder = new ViewHolderSong();
                viewHolder.mSn = (AutoEnlargeTextView) view.findViewById(R.id.song_list_item_sn);
                viewHolder.mSongName = (ListMarqueeTextView) view.findViewById(R.id.song_list_item_song_name);
                viewHolder.mSingerName = (AutoHideTextView) view.findViewById(R.id.song_list_item_singer);
                viewHolder.mSingerNameBelow = (AutoShowTextView) view.findViewById(R.id.song_list_item_singer_below);
                
                viewHolder.mSn.setParentFocusedView(mParentView);
                viewHolder.mSingerName.setParentFocusedView(mParentView);
                viewHolder.mSingerNameBelow.setParentFocusedView(mParentView);
                viewHolder.mSongName.setParentFocusedView(mParentView);
                
                view.setTag(viewHolder);
                return view;
            case SearchHistoryItem.SEARCH_ITEM_TYPE_SINGER:
                view = View.inflate(mContext, R.layout.search_history_singer_lay, null);
                ViewHolderSinger viewHolderSinger = new ViewHolderSinger();
                viewHolderSinger.mSn = (AutoEnlargeTextView) view.findViewById(R.id.search_history_item_sn);
                viewHolderSinger.mSingerCover = (CircleImageView) view.findViewById(R.id.search_history_singer_cover);
                viewHolderSinger.mSingerCover.setEnableAutoShow(true);
                viewHolderSinger.mSingerName = (AutoEnlargeTextView) view.findViewById(R.id.search_history_singer_name);
                viewHolderSinger.mSingerName.setTextSizePairs(-1, 
                        DimensionsUtil.getDimensionPixelSize(mContext, R.dimen.px49));
                viewHolderSinger.mSingerSongNum = (AutoEnlargeTextView) view.findViewById(
                        R.id.search_history_singer_song_num);
                viewHolderSinger.mSingerSongNum.setTextSizePairs(
                        DimensionsUtil.getDimensionPixelSize(mContext, R.dimen.px29),
                        DimensionsUtil.getDimensionPixelSize(mContext, R.dimen.px39));

                viewHolderSinger.mSn.setParentFocusedView(mParentView);
                viewHolderSinger.mSingerSongNum.setParentFocusedView(mParentView);
                viewHolderSinger.mSingerCover.setParentFocusedView(mParentView);
                
                view.setTag(viewHolderSinger);
                return view;
            default:
                break;
        }
        return null;
    }

    @Override
    protected void fillViewData(int position, View convertView) {
        if (convertView == null) {
            return;
        }
        
        SearchHistoryItem item = getItem(position);
        
        if (item == null) {
            return;
        }
        
        switch(getItemViewType(position)) {
            case SearchHistoryItem.SEARCH_ITEM_TYPE_SONG:
                ViewHolderSong viewHolder = (ViewHolderSong) convertView.getTag();
                Song song = SongManager.getInstance().getSongById(item.mId);
                if (song == null) {
                    return;
                }
                String songName = song.getName();
                viewHolder.mSongName.setText(songName);
                viewHolder.mSingerName.setText(song.getSingerDescription());
                viewHolder.mSingerNameBelow.setText(song.getSingerDescription());

                final TextPaint paint = viewHolder.mSongName.getPaint();

                // 获取序号最高位所占宽度
                if (mUnitWidth <= 0) {
                    mUnitWidth = (int) paint.measureText(getTopDigit(position + 1));
                }

                // 随着序号大于9（即position > 8）时，序号长度会增长，因此需要算出序号长度增长的偏移量
                if (position > 8) {
                    viewHolder.mSn.setText("" + (position + 1));
                    mOffset = (int) paint.measureText((position + 1) + "")
                            - mUnitWidth;
                    if (mOffset > 0) {
                        viewHolder.mSongName.setSpecWidth(mOriginalSpecWidth);
                    }
                } else {
                    viewHolder.mSn.setText("0" + (position + 1));
                    viewHolder.mSongName.setSpecWidth(mOriginalSpecWidth);
                }
                viewHolder.mSongName.setWidth(mOriginalSpecWidth);

                viewHolder.mSingerName.setCompoundDrawablePadding(3);
                viewHolder.mSingerName.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.km_score_placeholder, 0, 0, 0);
                viewHolder.mSingerNameBelow.setCompoundDrawablePadding(0);
                viewHolder.mSingerNameBelow
                        .setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                setSongOrderedState(viewHolder, song);
                break;
            case SearchHistoryItem.SEARCH_ITEM_TYPE_SINGER:
                final Singer info = SingerManager.getInstance().getSinger(item.mId);
                if (info == null) {
                    return;
                }
                final ViewHolderSinger viewHolderSinger = (ViewHolderSinger) convertView.getTag();
                if (position > 8) {
                    viewHolderSinger.mSn.setText("" + (position + 1));
                } else {
                    viewHolderSinger.mSn.setText("0" + (position + 1));
                }
                
                String singerName = info.getName();
                if (viewHolderSinger.mSingerName != null) {
                    viewHolderSinger.mSingerName.setText(singerName);
                } else {
                    EvLog.e("wrq", "NonePointer: singerName TextView");
                    return;
                }

                //设置默认图片
                viewHolderSinger.mSingerCover.setBackgroundResource(R.drawable.singer_default);
                int songNumbySingerName = SongManager.getInstance()
                		.getCountBySingerName(info.getName()/*, !NetUtils.isNetworkConnected(mContext)*/);
                viewHolderSinger.mSingerSongNum.setText(
                        mContext.getString(R.string.global_search_singer_song_num, songNumbySingerName));
                String resId = info.getPictureResourceId();
                int idValue = -1;
                try {
                    idValue = Integer.valueOf(resId);
                } catch (Exception e) {
                    EvLog.e("Error PicTure ResId");
                }
                if (idValue <= 0) {
                    viewHolderSinger.mSingerCover.setImageResource(R.drawable.singer_default);
                    return;
                }
                String url = mPictureHead + resId;
                ImageLoader.getInstance().displayImage(url, viewHolderSinger.mSingerCover, mOptions);
                break;
            default:
                break;
        }
    }
    
    private void setImageOptions() {
        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.singer_default)
                .showImageOnFail(R.drawable.singer_default)
                .showImageOnLoading(R.drawable.singer_default)
                .build();
    }
    
    /**
     * [功能说明]
     * @param specWidth 走马灯宽度
     */
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
    
    private void setSongHighlightState(ViewHolderSong viewHolder, Song item) {
        if (TextUtils.isEmpty(mSearchSpell) || viewHolder == null || item == null) {
            return;
        }
        String name = item.getName();
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
    }
    
    /**
     * [设置歌曲为已点状态]
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
            SearchHistoryItem searchItem = getItem(pos);
            if (searchItem.mItemType == SearchHistoryItem.SEARCH_ITEM_TYPE_SONG) {
                Song item = SongManager.getInstance().getSongById(searchItem.mId);
                if (item != null && item.getId() == song.getId()) {
                    setSongOrdered(mParentView.getChildAt(i));
                    break;
                }
            }
        }
    }
    
    /**
     * [设置歌曲为已点状态]
     * @param convertView 歌曲所在view
     */
    private void setSongOrdered(View convertView) {
        if (convertView == null) {
            return;
        }
        ViewHolderSong viewHolder = (ViewHolderSong) convertView.getTag();
        viewHolder.mSongName.setTextColor(mOrderedSongColor);
        viewHolder.mSingerName.setTextColor(mOrderedSingerColor);
        viewHolder.mSingerNameBelow.setTextColor(mOrderedSingerColor);
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
                Object viewHolder = mParentView.getChildAt(i).getTag();
                if (viewHolder instanceof ViewHolderSong) {
                    ((ViewHolderSong)viewHolder).mSn.setParentViewFocused(selected);
                    ((ViewHolderSong)viewHolder).mSingerName.setParentViewFocused(selected);
                    ((ViewHolderSong)viewHolder).mSingerNameBelow.setParentViewFocused(selected);
                    ((ViewHolderSong)viewHolder).mSongName.setParentViewFocused(selected);
                } else if (viewHolder instanceof ViewHolderSinger) {
                    ((ViewHolderSinger) viewHolder).mSn.setParentViewFocused(selected);
                    ((ViewHolderSinger) viewHolder).mSingerCover.setParentViewFocused(selected);
                    ((ViewHolderSinger) viewHolder).mSingerSongNum.setParentViewFocused(selected);
                }
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
            SearchHistoryItem searchItem = this.getItem(pos);
            Song item = SongManager.getInstance().getSongById(searchItem.mId);
            ViewHolderSong viewHolder = (ViewHolderSong) mParentView.getChildAt(i).getTag();
            setSongOrderedState(viewHolder, item);
        }
    }
    
    private void setSongOrderedState(ViewHolderSong viewHolder, Song item) {
        if (viewHolder == null || item == null) {
            return;
        }
        int posInPlayList = PlayListManager.getInstance().getPosBySongId(item.getId());
        if (posInPlayList >= 0) {
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
            viewHolder.mSongName.setTextColor(Color.WHITE);
            viewHolder.mSingerName.setTextColor(mSingerColor);
            viewHolder.mSingerNameBelow.setTextColor(mSingerColor);
            setSongHighlightState(viewHolder, item);
        }
    }
}
