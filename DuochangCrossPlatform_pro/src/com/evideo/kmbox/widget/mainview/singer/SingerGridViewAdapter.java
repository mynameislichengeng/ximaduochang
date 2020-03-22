/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-10-12     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.singer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.evideo.kmbox.R;
import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.BaseSongListAdapter;
import com.evideo.kmbox.widget.common.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * [功能说明]
 */
public class SingerGridViewAdapter extends BaseSongListAdapter<Singer> {
    
    private DisplayImageOptions mOptions;

    /**
     * @param context
     * @param parentView
     * @param datas
     */
    public SingerGridViewAdapter(Context context, ViewGroup parentView,
            ArrayList<Singer> datas) {
        super(context, parentView, datas);
        setImageOptions();
    }
    
    /**
     * [viewholder]
     */
    private class ViewHolder {
        CircleImageView mCoverIv;
        TextView mNameTv;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected View newConvertView(int position, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.gridview_item, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mCoverIv = (CircleImageView) view.findViewById(R.id.ItemImage);
        viewHolder.mNameTv = (TextView) view.findViewById(R.id.ItemText);   
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
        //获取当前歌星
        final Singer info = getItem(position);
        if (info == null) {
            return;
        }
        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.mNameTv.setText(info.getName());
        //设置默认图片
        viewHolder.mCoverIv.setBackgroundResource(R.drawable.singer_default);
        String resId = info.getPictureResourceId();
        int idValue = -1;
        try {
            idValue = Integer.valueOf(resId);
        } catch (Exception e) {
            EvLog.e("Error PicTure ResId");
        }
        if (idValue <= 0) {
            viewHolder.mCoverIv.setImageResource(R.drawable.singer_default);
            return;
        }
        ImageLoader.getInstance().displayImage(
                DeviceConfigManager.getInstance().getPicurhead() + "?fileid=" + info.getPictureResourceId(), viewHolder.mCoverIv, mOptions);
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
}
