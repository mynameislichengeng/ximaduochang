/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015年9月18日     "dinghaiqiang"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.songmenu;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evideo.kmbox.model.songmenu.SongMenu;
import com.evideo.kmbox.widget.common.BaseSongListAdapter;
import com.evideo.kmbox.widget.common.MaskFocusLinearLayout;
import com.evideo.kmbox.widget.common.RoundRectImageView;
import com.evideo.kmbox.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * [歌单列表adapter]
 */
public class SongMenuAdapter extends BaseSongListAdapter<SongMenu> {
    
    private DisplayImageOptions mOptions;
    private float mCoverRoundRectRadius;
    
    
    /**
     * @param context
     * @param parentView
     * @param datas
     */
    public SongMenuAdapter(Context context, ViewGroup parentView,
            ArrayList<SongMenu> datas) {
        super(context, parentView, datas);
        mCoverRoundRectRadius = (float) context.getResources().getDimension( R.dimen.px6);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        super.init();
        mOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .showImageForEmptyUri(R.drawable.song_menu_cover_default_small)
            .showImageOnFail(R.drawable.song_menu_cover_default_small)
            .showImageOnLoading(R.drawable.song_menu_cover_default_small)
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected View newConvertView(int position, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.song_menu_item, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mItemLay = (MaskFocusLinearLayout) view.findViewById(R.id.song_menu_item_lay);
        viewHolder.mCoverIv = (RoundRectImageView) view.findViewById(R.id.song_menu_item_cover);
        viewHolder.mCoverIv.setRadius(mCoverRoundRectRadius);
        viewHolder.mNameTv = (TextView) view.findViewById(R.id.song_menu_item_name_tv);
        viewHolder.mDescriptionTv = (TextView) view.findViewById(R.id.song_menu_item_description_tv);
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
        final SongMenu item = getItem(position);
        if (item == null) {
            return;
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.mNameTv.setText(item.name);
        if (TextUtils.isEmpty(item.description)) {
            viewHolder.mDescriptionTv.setVisibility(View.GONE);
            viewHolder.mDescriptionTv.setText("");
        } else {
            viewHolder.mDescriptionTv.setVisibility(View.VISIBLE);
            viewHolder.mDescriptionTv.setText(item.description);
        }
        
        if (SongMenu.SONG_MENU_ID_CHILD == item.songMenuId) {
            ImageLoader.getInstance().cancelDisplayTask(viewHolder.mCoverIv);
            viewHolder.mCoverIv.setImageResource(R.drawable.ic_song_menu_child);
        } else if (SongMenu.SONG_MENU_ID_DRAMA == item.songMenuId) {
            ImageLoader.getInstance().cancelDisplayTask(viewHolder.mCoverIv);
            viewHolder.mCoverIv.setImageResource(R.drawable.ic_song_menu_drama);
        } else {
            ImageLoader.getInstance().displayImage(item.imageUrl, viewHolder.mCoverIv, mOptions);
        }
        
        viewHolder.mItemLay.setFocusFrame(R.drawable.song_menu_item_selected);
        viewHolder.mItemLay.setForceFocusFlag(false);
        
    }
    
    /**
     * [viewholder]
     */
    private class ViewHolder {
        RoundRectImageView mCoverIv;
        TextView mNameTv;
        TextView mDescriptionTv;
        MaskFocusLinearLayout mItemLay;
    }

}
