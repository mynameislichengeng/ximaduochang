
package com.evideo.kmbox.widget.mainview.globalsearch;

import java.util.ArrayList;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evideo.kmbox.R;
import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.util.DimensionsUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.BaseSongListAdapter;
import com.evideo.kmbox.widget.common.CircleImageView;
import com.evideo.kmbox.widget.common.CustomSelectorGridView;
import com.evideo.kmbox.widget.common.MaskFocusLinearLayout;
import com.evideo.kmbox.widget.mainview.MainViewId;
import com.evideo.kmbox.widget.mainview.singer.SingerView.ISingerClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * [自定义歌星适配器]
 */
public class CustomSingerAdapter extends BaseSongListAdapter<Singer> implements
View.OnKeyListener,View.OnFocusChangeListener,View.OnClickListener{
    
    private String mPictureUrlHead = "";
    private DisplayImageOptions mOptions;
    private CustomSelectorGridView mParent;
    private boolean mAllowStatistic = false;

    private int mPaddingLeft = 0;
    private int mPaddingRight = 0;
    private int mPaddingTop = 0;
    private int mPaddingBottom = 0;
    public CustomSingerAdapter(Context context, ViewGroup parentView,
            ArrayList<Singer> datas) {
        super(context, parentView, datas);
        setImageOptions();
        mParent = (CustomSelectorGridView) parentView;
        mPaddingLeft = (int) DimensionsUtil.getDimension(mContext, R.dimen.global_search_singer_padding_left);//px4
        mPaddingRight = (int) DimensionsUtil.getDimension(mContext, R.dimen.global_search_singer_padding_right);
        mPaddingTop = (int) DimensionsUtil.getDimension(mContext, R.dimen.global_search_singer_padding_top);//px28
        mPaddingBottom = (int) DimensionsUtil.getDimension(mContext, R.dimen.global_search_singer_padding_bottom);//x19
        EvLog.i("padding:" + mPaddingLeft + "," + mPaddingTop+ "," + mPaddingBottom);
    }
    
    /**
     * [viewholder]
     */
    private class ViewHolder {
        CircleImageView mCoverIv;
        TextView mNameTv;
    }
    
    /**[打开点击统计]
     * @param value
     */
    public void setAllowStatistic(boolean value) {
        mAllowStatistic = value;
    }
    
    @Override
    protected View newConvertView(int position, ViewGroup parent) {
        MaskFocusLinearLayout view = (MaskFocusLinearLayout) View.inflate(
                mContext, R.layout.global_search_singer_item_lay, null);
        view.setFocusFrame(R.drawable.singer_icon_frame);
      
        view.setFocusPadding(mPaddingLeft, mPaddingTop, mPaddingRight, -mPaddingBottom);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mCoverIv = (CircleImageView) view.findViewById(R.id.ItemImage);
        viewHolder.mNameTv = (TextView) view.findViewById(R.id.ItemText); 
        view.setTag(viewHolder);
        view.setId(position);
        return view;
    }

    public interface IEdgeListener {
        public boolean onDownEdge();
        public boolean onLeftEdge();
        public boolean onRightEdge();
        public boolean onUpEdge();
    }
    
    private IEdgeListener mEdgeListener = null;
    public void setEdgeListener(IEdgeListener listener) {
        mEdgeListener = listener;
    }
    
    @Override
    protected void fillViewData(int position, View convertView) {
        if (convertView == null) {
            return;
        }
        //获取当前歌星
        final Singer info = getItem(position);
        final int index = position;

        MaskFocusLinearLayout lay = (MaskFocusLinearLayout) convertView;
        if (lay == null || !(lay instanceof MaskFocusLinearLayout)) {
            return;
        }
        lay.setOnClickListener(this);
        lay.setOnFocusChangeListener(this);
        lay.setOnKeyListener(this);

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
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

    private ISingerClickListener mListener;
    
    /**
     * [功能说明]
     * @param listener 设置点击监听
     */
    public void setSingerClickListner(ISingerClickListener listener) {
        mListener = listener;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
//        EvLog.i("v.getid=" + v.getId());
        int index = v.getId();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (event.getRepeatCount() >= 5) {
                    EvLog.i("reject long click getRepeatCount=" + event.getRepeatCount());
                    return true;
                }
                if (index == 0) {
                    if (mEdgeListener != null) {
                        return mEdgeListener.onLeftEdge();
                    }
                }
            } else if (keyCode ==  KeyEvent.KEYCODE_DPAD_UP) {
                if (mEdgeListener != null) {
                    return mEdgeListener.onUpEdge();
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (event.getRepeatCount() >= 5) {
                    EvLog.i("reject long click getRepeatCount=" + event.getRepeatCount());
                    return true;
                }
                if (index == (getCount() -1)) {
                    if (mEdgeListener != null) {
                        return mEdgeListener.onRightEdge();
                    }
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (mEdgeListener != null) {
                    return mEdgeListener.onDownEdge();
                }
            }
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EvLog.i("v.getid=" + v.getId());
        int index = v.getId();
        if (hasFocus && mParent != null && mParent instanceof CustomSelectorGridView) {
            EvLog.d("wrq", "here comes to the adapter selection listener");
            if (mParent.getOnItemSelectedListener() == null) {
                return;
            }
            mParent.getOnItemSelectedListener().onItemSelected(mParent, null, index, -1);
        }
    }

    @Override
    public void onClick(View v) {
        EvLog.i("onClick " + v.getId());
        final Singer info = getItem(v.getId());
        
        if (mListener != null) {
            mListener.onSingerItemClick(info, MainViewId.ID_SEARCH);
        }
        if (mAllowStatistic && info != null) {
            SearchHistoryManager.getInstance().save(
                    new SearchHistoryItem(info.getId(), SearchHistoryItem.SEARCH_ITEM_TYPE_SINGER));
            EvLog.i("wrq", "save datas to search history dao: singer id:" + info.getId()
                    + "singerName:" + info.getName());
        }
    }
}
