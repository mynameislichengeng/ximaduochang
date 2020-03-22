package com.evideo.kmbox.widget.common;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * [歌曲列表adapter的基类]
 * @param <T>
 */
public abstract class BaseSongListAdapter<T> extends BaseAdapter {
    
    protected Context mContext;
    protected ArrayList<T> mDatas;
    protected ViewGroup mParentView;
    
    public void updateData(ArrayList<T> list) {
        mDatas.clear();
        mDatas = list;
    }
    
    public BaseSongListAdapter(Context context, ViewGroup parentView, ArrayList<T>  datas) {
        mContext = context;
        mParentView = parentView;
        mDatas = datas;
        if (mDatas == null) {
            mDatas = new ArrayList<T>();
        }
        init();
    }
    
    public void init() {
    }

    @Override
    public int getCount() {
        if (mDatas != null) {
            Log.i("gso", "getCount返回数据条: "+mDatas.size());
            return mDatas.size();
        }
        return 0;
    }

    @Override
    public T getItem(int position) {
        if (mDatas == null) {
            return null;
        }
        if (position < 0 || position >= mDatas.size()) {
            return null;
        }
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = newConvertView(position, parent);
        }
        convertView.setId(position);
        fillViewData(position, convertView);
        return convertView;
    }
    
    /**
     * [构造界面]
     * @param position position
     * @param parent 父view
     * @return convertView
     */
    protected abstract View newConvertView(int position, ViewGroup parent);
    
    /**
     * [将数据填充进界面]
     * @param position  position
     * @param convertView convertView
     */
    protected abstract void fillViewData(int position, View convertView);
    
}
