/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年2月9日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.thirdapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.Log;

import com.evideo.kmbox.model.songmenu.SongMenu;
import com.evideo.kmbox.model.songmenu.SongMenuManager;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.mainview.MainViewManager;
import com.evideo.kmbox.widget.mainview.huodong.BmpDialog;

/**
 * [功能说明]
 */
public class ApkJumpHandler {

    private static boolean sDialogShow = false; 
    
    public static boolean isDialogShow() {
        return sDialogShow;
    }
    
    public static interface IHuodongJumpPictureDismissListener {
        public void onHuodongJumpPictureDismissListener();
    }
    public static boolean handleJump(int type,String param,Context context,final IHuodongJumpPictureDismissListener listener) {
        if (type == ApkJumpParamParser.JUMP_TYPE_SONG_MENU) {
            //FIXME
            int menuId = -1;
            try {
                menuId = Integer.valueOf(param);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            
            EvLog.i("id=" + menuId);
            SongMenu songMenu = SongMenuManager.getInstance().getSongMenuById(menuId);
            if (songMenu == null) {
                songMenu = new SongMenu(menuId, "", "","");
                Log.i("gsp", "handleJump:打开歌单里面的精选热门歌曲的详情页是什么 "+songMenu);
            }
            MainViewManager.getInstance().openSongMenuDetailsView(songMenu);
            return true;
        } else if (type == ApkJumpParamParser.JUMP_TYPE_RANK) {
            int rankId = -1;
            try {
                rankId = Integer.valueOf(param);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            
            EvLog.i("rank id=" + rankId);
            MainViewManager.getInstance().openRankView(rankId);
            return true;
        } else if (type == ApkJumpParamParser.JUMP_TYPE_HUODONG_BMP) {
            sDialogShow = true;
            BmpDialog bmpDialog = new BmpDialog(context);
            bmpDialog.setBmpUrl(param);
            bmpDialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface arg0) {
                    sDialogShow = false;
                    if (listener != null) {
                        listener.onHuodongJumpPictureDismissListener();
                    }
                }
            });
            bmpDialog.show();
        } else if (type == ApkJumpParamParser.JUMP_TYPE_HUODONG_HTML) {
            sDialogShow = true;
        }
        return false;
    }
}
