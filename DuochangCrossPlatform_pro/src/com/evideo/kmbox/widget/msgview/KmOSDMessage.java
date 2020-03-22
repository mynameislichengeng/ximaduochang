package com.evideo.kmbox.widget.msgview;

import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.widget.msgview.KmOSDMessageView.CutSongDisplayListener;




/**
 * @brief      : [文件功能说明]
 */
public class KmOSDMessage {
    
    public final static int KM_OSDTV_TYPE_MIN          = 0;
    
    // play    
    public final static int KM_OSDTV_TYPE_PLAY         = 1;
    
    // pause
    public final static int KM_OSDTV_TYPE_PAUSE        = 2;
    
    // origin track.原唱音轨
    public final static int KM_OSDTV_TYPE_TRACK_ORIGIN = 3;
    
    // instrument track.伴唱音轨
    public final static int KM_OSDTV_TYPE_TRACK_INSTRU = 4;
    
    // mute
    public final static int KM_OSDTV_TYPE_MUTE         = 5;
    
    // unmute
    public final static int KM_OSDTV_TYPE_UNMUTE       = 6;
    
    // audio volume adjust
    public final static int KM_OSDTV_TYPE_AUDIO        = 7;
    
    // 音效音量
    public final static int KM_OSDTV_TYPE_SOUND_EFFECT = 8;
    
    // speaker volume adjust.功放音量调节
    public final static int KM_OSDTV_TYPE_SPEAKER      = 9;
    
    // microphone volume adjust.
    public final static int KM_OSDTV_TYPE_MIC          = 10;
    
    // tone adjust.音调调节
    public final static int KM_OSDTV_TYPE_TONE         = 11;
    
    // 整蛊
    public final static int KM_OSDTV_TYPE_ZHENGGU      = 12;
    
    // 唱将
    public final static int KM_OSDTV_TYPE_CHANGJIANG   = 13;
    
    // 和声
    public final static int KM_OSDTV_TYPE_HESHENG      = 14;
    
    // 搞怪
    public final static int KM_OSDTV_TYPE_GAOGUAI      = 15;
    
    // when cut song, show the next song's infomation
    public final static int KM_OSDTV_TYPE_NEXTSONG     = 16;
    
    // 重唱
    public final static int KM_OSDTV_TYPE_REPLAY       = 17;
    
    // 喝彩
    public final static int KM_OSDTV_TYPE_HECAI        = 18;
    
    // 倒彩
    public final static int KM_OSDTV_TYPE_DAOCAI       = 19;
    
    // 鼓
    public final static int KM_OSDTV_TYPE_GU           = 20;
    
    // 沙锤
    public final static int KM_OSDTV_TYPE_SHACHUI      = 21;
    
    // 录音
    public final static int KM_OSDTV_TYPE_RECORDING    = 22;
    
    // 回放    
    public final static int KM_OSDTV_TYPE_PLAYBACK     = 23;
    
    // 显示tftp上传的图片
    public final static int KM_OSDTV_TYPE_TFTPPIC      = 24;
    
    // 更新tftp上传会员头像
    public final static int KM_OSDTV_TYPE_MEMBERPIC    = 25;
    
    // 发送祝福语
    public final static int KM_OSDTV_TYPE_BLESS        = 26;
    
    
    public final static int KM_OSD_TYPE_PLAYCTRL_SHOW = 40;//播控框
    public final static int KM_OSD_TYPE_PLAYCTRL_CHANGE_TRACK = 41;//切换原唱伴唱
    public final static int KM_OSD_TYPE_PLAYCTRL_OPEN_GRADE = 42;//开启评分
    public final static int KM_OSD_TYPE_PLAYCTRL_CLOSE_GRADE = 43;//关闭评分
    
    public final static int KM_OSD_TYPE_VOLUMECTRL_MUSIC_FOCUS = 51;//切换原唱伴唱
    public final static int KM_OSD_TYPE_VOLUMECTRL_MIC_FOCUS = 52;//评分开关
    
    /** [隐藏pause] */
    public final static int KM_OSDTV_TYPE_HIDE_PAUSE          = 60;
    
    public final static int KM_OSDTV_TYPE_MAX          = 100;
    
    private int id = 0;
    private OsdParams params = null;

    public KmOSDMessage(int id) {
        super();
        this.id = id;
    }    
    
    public int getTypeID(){
        return id;
    }

    public void setTypeId(int id) {
        this.id = id;
    }
    
    public OsdParams getOsdParams(){
        return this.params;
    }
    
    public void setOsdParams(OsdParams params){
        this.params = params;
    }
    
    public  ICutSongParams newICutSongParams(Song song, boolean isScore, String songName, String singName,
             String downInfo,boolean isTimerEnable){
        return new ICutSongParams(song, isScore,  songName,  singName,downInfo, isTimerEnable);
    }
    
    public IBlessParams newIBlessParams(String strUserId, String strUserName,
            String blessText){
        return this.new IBlessParams(strUserId, strUserName, blessText);
    }
    
    public  ITftpParams newITftpParams(String strUserId, String strUserName, String tftpPath){
        return this.new ITftpParams(strUserId, strUserName, tftpPath);
    }
    
    public static abstract class OsdParams{
        
    }
    
    public static class ICutSongParams extends OsdParams{
        public Song song;
        public boolean isScore = false;
        public String  songName = "";
        public String  singName = "";
        public String  downInfo = "";
        public boolean isTimerEnable = false;
        public CutSongDisplayListener listener = null;
        
        public ICutSongParams(Song song, boolean isScore, String songName, String singName,
                String downInfo, boolean isTimerEnable, CutSongDisplayListener listener) {
//            super();
            this.song = song;
            this.isScore = isScore;
            this.songName = songName;
            this.singName = singName;
            this.isTimerEnable = isTimerEnable;
            this.downInfo = downInfo;
            this.listener = listener;
        } 
        public ICutSongParams(Song song, boolean isScore, String songName, String singName,
                String downInfo, boolean isTimerEnable) {
            this(song, isScore, songName, singName,
                    downInfo, isTimerEnable, null);
        }        
    }
    
    public class IBlessParams extends OsdParams{
        public  String strUserId   = "";
        public  String strUserName = "";
        public  String blessText = "";
        public IBlessParams(String strUserId, String strUserName,
                String blessText) {
            super();
            this.strUserId = strUserId;
            this.strUserName = strUserName;
            this.blessText = blessText;
        }
        
    }
    
    public class ITftpParams extends OsdParams{
        public  String strUserId   = "";
        public  String strUserName = "";
        public String tftpPath = "";
        public ITftpParams(String strUserId, String strUserName, String tftpPath) {
            super();
            this.strUserId = strUserId;
            this.strUserName = strUserName;
            this.tftpPath = tftpPath;
        }
        
    }    
    
}
