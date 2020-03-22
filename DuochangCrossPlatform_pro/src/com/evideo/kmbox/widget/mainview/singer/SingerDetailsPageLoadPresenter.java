
package com.evideo.kmbox.widget.mainview.singer;

import java.util.List;

import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.presenter.PageDataInfo;
import com.evideo.kmbox.presenter.PageLoadPresenter;

/**
 * [功能说明] 请求歌星全部歌曲
 */
public class SingerDetailsPageLoadPresenter extends PageLoadPresenter<Song> {
    private String mSingerName;
    private boolean mLocal = false;
    private int mTotalNum = 0;

    public SingerDetailsPageLoadPresenter(
            int pageSize, IPageLoadCallback<Song> pageLoadCallback, String singerName) {
        super(pageSize, pageLoadCallback);
        mSingerName = singerName;
    }
    
    public int getTotalNum() {
        return mTotalNum;
    }
    
    public void setOfflineSearch(boolean value) {
        mLocal = value;
    }

    @Override
    public PageDataInfo<Song> getData(int loadPage, int pageSize)
            throws Exception {
        PageInfo pageInfo = new PageInfo(loadPage - 1, pageSize);
        List<Song> datas;
        if (loadPage == 1) {
            mTotalNum  = SongManager.getInstance().getCountBySingerName(mSingerName/*, mLocal*/);
        }
//        int totalNum;

        datas = SongManager.getInstance().getSongBySingerName(mSingerName, pageInfo , mLocal);
//        totalNum = SongManager.getInstance().getCountBySingerName(mSingerName/*, mLocal*/);
        return new PageDataInfo<Song>(datas, mTotalNum);
    }
}
