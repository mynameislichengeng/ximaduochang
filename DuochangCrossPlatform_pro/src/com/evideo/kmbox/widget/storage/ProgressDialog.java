package com.evideo.kmbox.widget.storage;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evideo.kmbox.R;
import com.evideo.kmbox.widget.common.CommonDialog;

/**
 * [进度对话框]
 */
public class ProgressDialog extends CommonDialog {
    
    private ProgressBar mProgressBar;
    private TextView mProgressTv;
    private TextView mContentTv;
    private TextView mSongNameTv;
    private TextView mRemainTimeTv;

    public ProgressDialog(Context context) {
        super(context);
        setTitle(-1);
        View view = View.inflate(context, R.layout.dialog_add_song, null);
        setContentLayout(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        init(context);
    }
    
    private void init(Context context) {
        mContentTv = (TextView) findViewById(R.id.content_tv);
        
        mProgressBar = (ProgressBar) findViewById(R.id.pb);
        mProgressTv = (TextView) findViewById(R.id.progress_tv);
        mSongNameTv = (TextView) findViewById(R.id.song_name_tv);
        mRemainTimeTv = (TextView) findViewById(R.id.predict_remain_time_tv);
        
        // TODO 隐藏预计剩余时间
        mRemainTimeTv.setVisibility(View.INVISIBLE);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setContent(int resId) {
        mContentTv.setText(resId);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setContent(String content) {
        mContentTv.setText(content);
    }
    
    /**
     * [设置进度是否可见]
     * @param visible true:可见,false：不可见
     */
    public void setProgressTextVisible(boolean visible) {
        mProgressTv.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
    
    /**
     * [更新进度]
     * @param progress 进度百分比
     */
    public void updateProgress(int progress) {
        mProgressBar.setProgress(progress);
        mProgressTv.setText(progress + "%");
    }
    
    /**
     * [设置歌曲信息视图是否可见]
     * @param visible true:可见;false：不可见
     */
    public void setSongsInfoVisible(boolean visible) {
        findViewById(R.id.add_songs_info_lay).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
    
    /**
     * [设置歌名]
     * @param name 歌名
     */
    public void setSongName(String name) {
        mSongNameTv.setText(name);
    }
    
    /**
     * [设置剩余时间]
     * @param remainTime 剩余时间
     */
    public void setRemainTime(String remainTime) {
        mRemainTimeTv.setText(getContext().getString(R.string.songbook_predict_remain_time, remainTime));
    }

}
