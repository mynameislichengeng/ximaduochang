/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年3月13日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.freesong;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.Message;
import com.evideo.kmbox.exception.DataCenterCommuException;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.datacenter.DCDomain;
import com.evideo.kmbox.model.datacenter.proxy.data.DataCenterMessage;
import com.evideo.kmbox.presenter.CommuPresenter.CommuCallback;
import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明]
 */
public class FreeSongCommu implements CommuCallback {
    private List<Song> mList = null;

    public interface IFreeSongCommuListener {
        public void onFreeSongGetSuccess(List<Song> list);
        public void onFreeSongGetFailed();
    }
    
    private IFreeSongCommuListener mListener = null;
    public void setListener(IFreeSongCommuListener listener) {
        mListener = listener;
    }
    
    private List<Integer> updateOnlineFreeSongList() throws Exception {

        DataCenterMessage request = new DataCenterMessage();
        DataCenterMessage response = new DataCenterMessage();

        request.put("function", "freesong");
        response = DCDomain.sendMessage(request);
        String errorCode = response.get("errorcode");
        String errorMessage = response.get("errormessage");
        EvLog.i("requestFreeSongList " + response.getContentString());
        if (!errorCode.equals("0")) {
            throw new DataCenterCommuException("freesong", errorCode,
                    errorMessage);
        }
        JSONObject rsJsonObject = new JSONObject(response.getContentString());

        JSONArray rJsonArray = rsJsonObject.getJSONArray("songidList");
        if (rJsonArray == null) {
            return Collections.emptyList();
        }

        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < rJsonArray.length(); i++) {
            list.add(rJsonArray.getInt(i));
//            EvLog.d("add songid:" + rJsonArray.getInt(i));
        }
        return list;
    }
    
    @Override
    public Boolean doCommu(Object... params) throws Exception {
        EvLog.d("begin to UpdateFreeSongListCommu");
        if (mList == null) {
            mList = new ArrayList<Song>();
        }
        mList.clear();
        List<Integer> tmpList = new ArrayList<Integer>();

        for (int i = 0; i < 2; i++) {
            try {
                tmpList = updateOnlineFreeSongList();
            } catch (Exception e) {
                if (tmpList != null) {
                    tmpList.clear();
                }
                e.printStackTrace();
                continue;
            }

            if (tmpList != null) {
                break;
            }
        }
        if (tmpList != null) {
            Song song = null;
            for (int i = 0; i < tmpList.size(); i++) {
                if (SongManager.getInstance().isExist(tmpList.get(i))) {
                    song = SongManager.getInstance()
                            .getSongById(tmpList.get(i));
                } else {
                    try {
                        EvLog.e("getSongFromDataCenter get songid from net:"
                                + tmpList.get(i));
                        song = SongManager.getInstance().getSongFromDataCenter(
                                tmpList.get(i));
                        if (song == null
                                || !SongManager.getInstance().add(song)) {
                            continue;
                        } else {
                            EvLog.e(tmpList.get(i) + " add to db success");
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        EvLog.e("getSongFromDataCenter failed:"
                                + tmpList.get(i));
                        continue;
                    }
                }

                if (song != null) {
                    mList.add(song);
                } else {
                    EvLog.e(tmpList.get(i) + " get song failed");
                }
            }
        }
        return (mList != null) ? (true) : (false);
    }

    @Override
    public void commuSuccess() {
        EvLog.d("UpdateFreeSongListCommu success,size=" + mList.size());
        if (mListener != null) {
            mListener.onFreeSongGetSuccess(mList);
        }
    }

    @Override
    public void commuFailed(Exception exception) {
        if (mList != null) {
            mList.clear();
        }
        if (mListener != null) {
            mListener.onFreeSongGetFailed();
        }
    }
}
