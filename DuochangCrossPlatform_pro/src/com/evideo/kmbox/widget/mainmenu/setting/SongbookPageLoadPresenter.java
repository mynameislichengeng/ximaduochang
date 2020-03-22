package com.evideo.kmbox.widget.mainmenu.setting;

import java.util.List;

import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.presenter.PageDataInfo;
import com.evideo.kmbox.presenter.PageLoadPresenter;
import com.evideo.kmbox.util.EvLog;

/**
 * [曲库分页加载异步任务类]
 */
public class SongbookPageLoadPresenter extends PageLoadPresenter<Song> {

    public SongbookPageLoadPresenter(
            int pageSize,
            com.evideo.kmbox.presenter.PageLoadPresenter.IPageLoadCallback<Song> pageLoadCallback) {
        super(pageSize, pageLoadCallback);
    }

    @Override
    public PageDataInfo<Song> getData(int loadPage, int pageSize) throws Exception {
        EvLog.d("SongbookPageLoadPresenter  getData loadPage: " + loadPage + " pageSize: " + pageSize);
        PageInfo pageInfo = new PageInfo(loadPage - 1, pageSize);
        List<Song> datas = SongManager.getInstance().getCachedSongList(pageInfo);
        EvLog.d("SongbookPageLoadPresenter load size=" + datas.size());
        int totalNum = SongManager.getInstance().getCachedSongCount();
        return new PageDataInfo<Song>(datas, totalNum);
    }

}
