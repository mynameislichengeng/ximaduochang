/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年8月30日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.usercenter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evideo.kmbox.R;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.BaseSongListAdapter;
import com.evideo.kmbox.widget.mainview.usercenter.UserCenterSimpleView.UserCenterTabItem;

/**
 * [歌单列表adapter]
 */
public class UserCenterTabAdapter extends BaseSongListAdapter<UserCenterTabItem> {

    private int mNormalSize;
    private int mEnlargeSize;
    private int mNormalColor;
    private int mEnlargeColor;

    private TextView mLastCheckedView = null;

    /**
     * @param context
     * @param parentView
     * @param datas
     */
    public UserCenterTabAdapter(Context context, ViewGroup parentView,
            ArrayList<UserCenterTabItem> datas) {
        super(context, parentView, datas);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        super.init();
        mNormalSize = mContext.getResources().getDimensionPixelSize(
                R.dimen.px39);
        mEnlargeSize = mContext.getResources().getDimensionPixelSize(
                R.dimen.px49);
        
        mEnlargeColor = mContext.getResources().getColor(
                R.color.text_white);
        mNormalColor =  mContext.getResources().getColor(
                R.color.myspace_tab_item_normal_color);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected View newConvertView(int position, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.main_view_my_space_tab_item,
                null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mNameTv = (TextView) view
                .findViewById(R.id.main_view_song_top_item_name_tv);
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
        final UserCenterTabItem item = getItem(position);
        if (item == null) {
            return;
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.mNameTv.setText(item.tabName);
        viewHolder.mNameTv.setTextColor(mNormalColor);
    }

    /**
     * [viewholder]
     */
    private class ViewHolder {
        TextView mNameTv;
    }

    /**
     * [功能说明] 设置view选中状态
     * 
     * @param checkedView
     *            选中view
     */
    public void setCheckedView(View checkedView) {
        if (checkedView == null) {
            return;
        }
        if (mLastCheckedView != null) {
            setViewSelected(mLastCheckedView, false);
        } 
        ViewHolder holder = (ViewHolder) checkedView.getTag();
        TextView textView = holder.mNameTv;
        setViewSelected(textView, true);
    }
    
    /**
     * [功能说明]焦点离开但选中状态
     */
    public void setViewSelectedButNotFocus() {
        TextView textView = mLastCheckedView;
        if (textView != null) {
            ((LinearLayout) textView.getParent()).setBackground(
                    mContext.getResources().getDrawable(R.color.transparent));
            textView.setTextColor(mEnlargeColor);
//            textView.setTextSize(mNormalSize);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalSize);
        }
    }

    public void setViewLostFocus() {
        TextView textView = mLastCheckedView;
        if (textView != null) {
            ((LinearLayout) textView.getParent()).setBackground(
                    mContext.getResources().getDrawable(R.color.transparent));
            textView.setTextColor(mNormalColor);
//            textView.setTextSize(mNormalSize);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalSize);
        }
    }
    
    public int getCurrentViewTabId() {
        String text = (String) mLastCheckedView.getText();
        if (TextUtils.isEmpty(text)) {
            EvLog.d("getCurrentViewTabId text");
            return -1;
        }
        for (UserCenterTabItem data : super.mDatas) {
            if (data.tabName.equals(text)) {
                return data.tabId;
            }
        }
        return -1;
    }
    
    public void emptyCheckedView() {
        mLastCheckedView = null;
    }
    private void setViewSelected(TextView view, boolean selected) {
        if (view != null) {
            if (selected) {
//                EvLog.d("setViewSelected ----");
                ((LinearLayout) view.getParent()).setBackground(mContext.getResources().getDrawable(
                        R.drawable.song_top_item_bg));
                view.setTextColor(mEnlargeColor);
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, mEnlargeSize);
                mLastCheckedView = view;
            } else {
                ((LinearLayout) view.getParent()).setBackground(mContext.getResources().getDrawable(
                        R.color.transparent));
                view.setTextColor(mNormalColor);
//                view.setTextSize(mNormalSize);
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalSize);
            }
        }
    }
}
