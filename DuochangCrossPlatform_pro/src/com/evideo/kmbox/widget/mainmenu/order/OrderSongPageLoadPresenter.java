package com.evideo.kmbox.widget.mainmenu.order;

import java.util.List;

import android.text.TextUtils;

import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.presenter.PageDataInfo;
import com.evideo.kmbox.presenter.PageLoadPresenter;

/**
 * [点歌分页加载异步任务类]
 */
public class OrderSongPageLoadPresenter extends PageLoadPresenter<Song> {
    
    private String mSpell;
    private boolean mLocal = false;
    private int mTotalNum = 0;

    public OrderSongPageLoadPresenter(int pageSize, 
            PageLoadPresenter.IPageLoadCallback<Song> pageLoadCallback, String spell) {
        super(pageSize, pageLoadCallback);
        mSpell = spell;
        if (TextUtils.isEmpty(mSpell)) {
            mSpell = "";
        }
    }

    /**
     * [歌曲总数]
     * @return 歌曲总数
     */
    public int getTotalSongNum() {
        return mTotalNum;
    }
    
    /**
     * [功能说明]离线搜索
     * @param local true or false
     */
    public void setOffLineSearch(boolean local) {
        mLocal = local;
    }

    @Override
    public PageDataInfo<Song> getData(int loadPage, int pageSize) throws Exception {
        PageInfo pageInfo = new PageInfo(loadPage - 1, pageSize);
        List<Song> datas = SongManager.getInstance().getSongListByFuzzySpell(mSpell, pageInfo, mLocal);
        if (loadPage == 1) {
            mTotalNum = SongManager.getInstance().getCountByFuzzySpell(mSpell, mLocal);
        }
        return new PageDataInfo<Song>(datas, mTotalNum);
    }
}
