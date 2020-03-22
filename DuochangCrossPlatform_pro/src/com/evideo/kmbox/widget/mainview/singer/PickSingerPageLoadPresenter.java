
package com.evideo.kmbox.widget.mainview.singer;

import java.util.List;

import android.text.TextUtils;

import com.evideo.kmbox.model.dao.data.PageInfo;
import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.model.dao.data.SingerManager;
import com.evideo.kmbox.presenter.PageDataInfo;
import com.evideo.kmbox.presenter.PageLoadPresenter;

/**
 * [请求歌星信息]
 */
public class PickSingerPageLoadPresenter extends PageLoadPresenter<Singer> {
    
    private String mSpell;
    private int mTypeIndex = 0;
    private int mTotalNum = 0;

    public PickSingerPageLoadPresenter(int pageSize, 
            PageLoadPresenter.IPageLoadCallback<Singer> pageLoadCallback, String spell, int typeIndex) {
        super(pageSize, pageLoadCallback);
        mSpell = spell;
        mTypeIndex = typeIndex;
        if (TextUtils.isEmpty(mSpell)) {
            mSpell = "";
        }
    }

    /**
     * [功能说明]
     * @return 歌星总数
     */
    public int getTotalSingerNum() {
        return mTotalNum;
    }

    @Override
    public PageDataInfo<Singer> getData(int loadPage, int pageSize) throws Exception {
        PageInfo pageInfo = new PageInfo(loadPage - 1, pageSize);
        List<Singer> datas = SingerManager.getInstance().getSingerBySpell(mSpell, pageInfo, mTypeIndex);
        if (loadPage == 1) {
            mTotalNum = SingerManager.getInstance().getSingerCountBySpell(mSpell, mTypeIndex);
        }
        return new PageDataInfo<Singer>(datas, mTotalNum);
    }
}
