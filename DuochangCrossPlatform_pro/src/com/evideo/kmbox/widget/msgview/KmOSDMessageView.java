package com.evideo.kmbox.widget.msgview;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.song.MembersInfoList;
import com.evideo.kmbox.util.BitmapUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.RepeatTimerTask;
import com.evideo.kmbox.util.RepeatTimerTask.IActionCallback;
import com.evideo.kmbox.widget.common.ToastUtil;
import com.evideo.kmbox.widget.msgview.KmOSDMessage.IBlessParams;
import com.evideo.kmbox.widget.msgview.KmOSDMessage.ICutSongParams;
import com.evideo.kmbox.widget.msgview.KmOSDMessage.ITftpParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @brief      : [文件功能说明]
 */
public  class KmOSDMessageView extends FrameLayout{
  
    public KmOSDMessageView(LinearLayout view, Context c) {
        super(c);
//        this.context = c;
        initViewRes();
    }    
    
    // Set constructor private and do nothing
    // Can not new a instance outside class
    private Handler mactionhandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            if(msg == null || msg.obj == null){
                super.handleMessage(msg);
            }else{
                if(msg.obj instanceof KmOSDMessage){
                    KmOSDMessage type = (KmOSDMessage)(msg.obj);
                    switch (type.getTypeID()) {
                        case KmOSDMessage.KM_OSDTV_TYPE_PLAY:
                        case KmOSDMessage.KM_OSDTV_TYPE_TRACK_ORIGIN:
                        case KmOSDMessage.KM_OSDTV_TYPE_TRACK_INSTRU:
                        case KmOSDMessage.KM_OSDTV_TYPE_MUTE:
                        case KmOSDMessage.KM_OSDTV_TYPE_UNMUTE:
                        case KmOSDMessage.KM_OSDTV_TYPE_REPLAY:
                        case KmOSDMessage.KM_OSDTV_TYPE_ZHENGGU:
                        case KmOSDMessage.KM_OSDTV_TYPE_CHANGJIANG:
                        case KmOSDMessage.KM_OSDTV_TYPE_GAOGUAI:
                        case KmOSDMessage.KM_OSDTV_TYPE_HESHENG:
                        case KmOSDMessage.KM_OSDTV_TYPE_HECAI:
                        case KmOSDMessage.KM_OSDTV_TYPE_DAOCAI:
                        case KmOSDMessage.KM_OSDTV_TYPE_GU:
                        case KmOSDMessage.KM_OSDTV_TYPE_SHACHUI:  
                        case KmOSDMessage.KM_OSD_TYPE_PLAYCTRL_OPEN_GRADE:
                        case KmOSDMessage.KM_OSD_TYPE_PLAYCTRL_CLOSE_GRADE:
                            showPlayCtrl(type);
                            break;
                        case KmOSDMessage.KM_OSDTV_TYPE_HIDE_PAUSE:
                            repauseshow.stop();
                            if (playctrlpause.getVisibility() == VISIBLE) {
                                playctrlpause.setVisibility(INVISIBLE);
                            }
                            break;
                        case KmOSDMessage.KM_OSDTV_TYPE_PAUSE:
                            if(type.getTypeID() == KmOSDMessage.KM_OSDTV_TYPE_PAUSE){
                                clearLevel1();
                                repauseshow.scheduleAtFixedRate(0, KmOSDMessageView.playctrltimeout/6);
                            }
                            break;
                        case KmOSDMessage.KM_OSDTV_TYPE_BLESS:
                            showBlessInfo(type);
                            break;
                        case KmOSDMessage.KM_OSDTV_TYPE_TFTPPIC:
                            showTftpPic(type);
                            break;
                        case KmOSDMessage.KM_OSDTV_TYPE_NEXTSONG:
                            repauseshow.stop();
                            showCutSong(type);
                            break;
                        default:
                            super.handleMessage(msg);
                            break;
                    }
                }
            }        
        }            
    };
    
    /*
     * @brief : 获取xml资源
     * @return
     */
    private  int getLayResId() {
        return R.layout.km_msg_osdtv;
    }
      
    public void sendKMMessageDrawView(KmOSDMessage type){
        mactionhandler.sendMessage(mactionhandler.obtainMessage(type.getTypeID(), type));
    }
    
    public void removeKmMessageDrawView(int id){
        mactionhandler.removeMessages(id);
    }
    
    
    //更新报幕信息
    public void updateDownloadProcess(String info) {
        if (cutsongctrl != null && info != null ) {
            cutsongctrl.updateLoadProcess(info);
        }
    }
    
    //====================================
    
    /**
     * 
     * @brief : 初始化view资源
     */
    private  void initViewRes() {
        Context context = getContext();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(getLayResId(), this, true);
        playctrlimageview = (ImageView) findViewById(R.id.osdimageplayctrl);
        playctrlpause= (ImageView) findViewById(R.id.osdimageplayctrl_pause);
        Bitmap bmp = BitmapUtil.getBitmapByResId(BaseApplication.getInstance(), R.drawable.ic_tv_pause);
        playctrlpause.setImageBitmap(bmp);
//        bless = new BlessView(this, context);
//        tftploadSharedView = new TftpSharedView(this, context);
        cutsongctrl = new CutSongView(this, context);
        clear();
//        atmoplayer = new AsyncPlayer("atmoplayer");
        repauseshow = new RepeatTimerTask(new ShowPauseTime());
    }
    
    public boolean isAnnouceShow() {
        return (cutsongctrl!=null) ? (cutsongctrl.getVisibility()) : (false);
    }
    
    private int showPlayCtrl(KmOSDMessage msg){
        int id = msg.getTypeID();
        EvLog.d("showplayctrl " + id);
        if(id <= KmOSDMessage.KM_OSDTV_TYPE_MIN 
                    || id >= KmOSDMessage.KM_OSDTV_TYPE_MAX){
            return -1;
        } 
        if(id == KmOSDMessage.KM_OSDTV_TYPE_PLAY 
                || id == KmOSDMessage.KM_OSDTV_TYPE_REPLAY){
            repauseshow.stop();
        }
        clearLevel1();
        playctrlimageview.setVisibility(VISIBLE);
        loadPlayCtrlPic(playctrlimageview, msg);
        removeShowPicCallback(mshowRunnable_Level1);
        mshowRunnable_Level1 = new ShowOsdRunnable(msg);        
        if(id == KmOSDMessage.KM_OSDTV_TYPE_ZHENGGU
                || id == KmOSDMessage.KM_OSDTV_TYPE_CHANGJIANG
                || id == KmOSDMessage.KM_OSDTV_TYPE_HESHENG
                || id == KmOSDMessage.KM_OSDTV_TYPE_GAOGUAI
                || id == KmOSDMessage.KM_OSDTV_TYPE_HECAI
                || id == KmOSDMessage.KM_OSDTV_TYPE_DAOCAI
                || id == KmOSDMessage.KM_OSDTV_TYPE_GU
                || id == KmOSDMessage.KM_OSDTV_TYPE_SHACHUI){
            //atmoplayer.stop();
          /*  if (atmoplayer == null) {
                atmoplayer = new AsyncPlayer("atmoplayer");
            }
            atmoplayer.play(getContext(), Uri.parse(getAtmoAudioPath(msg)),
                                        false, AudioManager.STREAM_MUSIC);*/
            ToastUtil.showLongToast(BaseApplication.getInstance(), "不支持音效！");
        }

        if(id == KmOSDMessage.KM_OSDTV_TYPE_BLESS
                || id == KmOSDMessage.KM_OSDTV_TYPE_TFTPPIC
                || id == KmOSDMessage.KM_OSDTV_TYPE_NEXTSONG){
            animationdrawable.setOneShot(true);
            if(animationdrawable.isRunning()){
                animationdrawable.stop();
            }
            animationdrawable.start();
            drawpostDelayed(mshowRunnable_Level1, animationdrawable
                    .getDuration(0)*animationdrawable.getNumberOfFrames());
        }else{
            drawpostDelayed(mshowRunnable_Level1, KmOSDMessageView.playctrltimeout);
        }
        return 0;        
    }
    
    private int showCutSong(KmOSDMessage msg){
        if (msg == null) {
            EvLog.e("showCutSong msg is null");
            return -1;
        }
        int id = msg.getTypeID();
        if(id <= KmOSDMessage.KM_OSDTV_TYPE_MIN 
                    || id >= KmOSDMessage.KM_OSDTV_TYPE_MAX){
            return -1;
        } 
        ICutSongParams params = (ICutSongParams) msg.getOsdParams();
        if (params == null) {
            EvLog.e("showCutSong msg params is null");
            return -1;
        }
        
        clearLevel2();
        removeShowPicCallback(mshowRunnable_Level2);
        
        cutsongctrl.setSinger(params.singName);
        cutsongctrl.setSong(params.songName);
//            cutsongctrl.updateLoadProcess(params.downInfo);
        cutsongctrl.startGetAnnouncerCoverTask(params.song);
        cutsongctrl.show(params.isScore);
        cutsongctrl.updateLoadProcess(params.downInfo);
        
        if (params.listener == null) {
            mCutSongDisplayListener = null;
        } else {
            mCutSongDisplayListener = params.listener;
        }
        return 0;
    }

    
    private int showBlessInfo(KmOSDMessage type){
        if(!(type.getOsdParams() instanceof KmOSDMessage.IBlessParams) 
                || type.getTypeID() <= KmOSDMessage.KM_OSDTV_TYPE_MIN 
                || type.getTypeID() >= KmOSDMessage.KM_OSDTV_TYPE_MAX){
            return -1;
        } 
        if ( cutsongctrl.getVisibility() == true ) {
            return 0;
        }
        if (bless == null) {
            bless = new BlessView(this, BaseApplication.getInstance());
            
        }
       /* if (emojihelper == null) {
            emojihelper = new EmojiDomParserHelper();
            emojihelper.initConfig(BaseApplication.getInstance());
        }*/
        IBlessParams params = (IBlessParams) type.getOsdParams();
        bless.setBlessText(/*emojihelper.getcharStrHtml(*/params.blessText/*)*/); 
        bless.setCustomerID(params.strUserId, params.strUserName);
        clearLevel2();
        bless.show();
        removeShowPicCallback(mshowRunnable_Level2);
        mshowRunnable_Level2 = new ShowOsdRunnable(type);
        drawpostDelayed(mshowRunnable_Level2, KmOSDMessageView.blessshowtimeout);
        return 0;
    }
    
    
    private int showTftpPic(KmOSDMessage type){
        if(!(type.getOsdParams() instanceof KmOSDMessage.ITftpParams)
                ||type.getTypeID() <= KmOSDMessage.KM_OSDTV_TYPE_MIN 
                || type.getTypeID() >= KmOSDMessage.KM_OSDTV_TYPE_MAX){
            return -1;
        } 
        if ( cutsongctrl.getVisibility() == true ) {
            return 0;
        }
        ITftpParams params = (ITftpParams) type.getOsdParams();
        if (TextUtils.isEmpty(params.tftpPath)) {
            return -1;
        }
        Bitmap bm = BitmapUtil.getBmpByPath(params.tftpPath);
        if(bm != null){
            if (tftploadSharedView == null) {
                tftploadSharedView = new TftpSharedView(this, BaseApplication.getInstance());
            }
            tftploadSharedView.setTftpPic(bm);
            tftploadSharedView.setCustomerID(params.strUserId, params.strUserName);
            clearLevel2();
            tftploadSharedView.show();
            removeShowPicCallback(mshowRunnable_Level2);
            mshowRunnable_Level2 = new ShowOsdRunnable(type);
            drawpostDelayed(mshowRunnable_Level2, KmOSDMessageView.blessshowtimeout);
        }
        return 0;
    }
    
       
    private void drawpostDelayed(Runnable run, long delay) {
        if (run != null ) {
            postDelayed(run, delay);
        }        
    }
    
    
    private void removeShowPicCallback(Runnable runnable){
        if(runnable != null){
            removeCallbacks(runnable);
        }
    }
    
    
    private void loadPlayCtrlPic(ImageView imageview,KmOSDMessage type){
       
        if(type != null && imageview != null){
            switch (type.getTypeID()) {
                case KmOSDMessage.KM_OSDTV_TYPE_PLAY:
                    imageview.setImageResource(R.drawable.ic_tv_play);
                    break;
                case KmOSDMessage.KM_OSDTV_TYPE_PAUSE:
                    imageview.setImageResource(R.drawable.ic_tv_pause);
                    break;
                case KmOSDMessage.KM_OSDTV_TYPE_TRACK_ORIGIN:
                    imageview.setImageResource(R.drawable.ic_tv_original);
                    break;
                case KmOSDMessage.KM_OSDTV_TYPE_TRACK_INSTRU:
                    imageview.setImageResource(R.drawable.ic_tv_accompany);
                    break;
                case KmOSDMessage.KM_OSDTV_TYPE_MUTE:
                    imageview.setImageResource(R.drawable.mute);
                    break;
                case KmOSDMessage.KM_OSDTV_TYPE_UNMUTE:
                    imageview.setImageResource(R.drawable.unmute);
                    break;
                /*case KmOSDMessage.KM_OSDTV_TYPE_ZHENGGU:
                    imageview.setImageResource(R.drawable.zhenggu);
                    break;
                case KmOSDMessage.KM_OSDTV_TYPE_CHANGJIANG:
                    imageview.setImageResource(R.drawable.changjiang);
                    break;
                case KmOSDMessage.KM_OSDTV_TYPE_HESHENG:
                    imageview.setImageResource(R.drawable.hesheng);
                    break;
                case KmOSDMessage.KM_OSDTV_TYPE_GAOGUAI:
                    imageview.setImageResource(R.drawable.gaoguai);
                    break;*/
                case KmOSDMessage.KM_OSDTV_TYPE_NEXTSONG:
                    break;
                case KmOSDMessage.KM_OSDTV_TYPE_REPLAY:
                    imageview.setImageResource(R.drawable.ic_tv_replay);
                    break;
                /*case KmOSDMessage.KM_OSDTV_TYPE_HECAI:
                    imageview.setImageResource(R.anim.atmo_hecai);
                    animationdrawable = (AnimationDrawable) imageview.getDrawable();
                    break;
                case KmOSDMessage.KM_OSDTV_TYPE_DAOCAI:
                    imageview.setImageResource(R.anim.atmo_daocai);
                    animationdrawable = (AnimationDrawable) imageview.getDrawable();
                    break;
                case KmOSDMessage.KM_OSDTV_TYPE_GU:
                    imageview.setImageResource(R.anim.atmo_gu);
                    animationdrawable = (AnimationDrawable) imageview.getDrawable();
                    break;
                case KmOSDMessage.KM_OSDTV_TYPE_SHACHUI:
                    imageview.setImageResource(R.anim.atmo_shachui);
                    animationdrawable = (AnimationDrawable) imageview.getDrawable();
                    break;*/
                /*case KmOSDMessage.KM_OSD_TYPE_PLAYCTRL_OPEN_GRADE:
                    imageview.setImageResource(R.drawable.ic_grade_open);
                    break;
                case KmOSDMessage.KM_OSD_TYPE_PLAYCTRL_CLOSE_GRADE:
                    imageview.setImageResource(R.drawable.ic_grade_close);
                    break;*/
                default:
                    break;
            }
        }
    }
    
    /*private String getAtmoAudioPath(KmOSDMessage type) {
        String path = null;
        Context context = getContext();
        if(type != null){
            switch (type.getTypeID()) {
                case KmOSDMessage.KM_OSDTV_TYPE_HECAI:
                case KmOSDMessage.KM_OSDTV_TYPE_HESHENG:
                    path = "android.resource://"+context.getPackageName()+"/"+R.raw.hecai2;
                    break;
                case KmOSDMessage.KM_OSDTV_TYPE_GU:
                    path = "android.resource://"+context.getPackageName()+"/"+R.raw.gu;
                    break;
                case KmOSDMessage.KM_OSDTV_TYPE_CHANGJIANG:        
                    path = "android.resource://"+context.getPackageName()+"/"+R.raw.xianhua2;
                    break;
                case KmOSDMessage.KM_OSDTV_TYPE_DAOCAI:
                    path = "android.resource://"+context.getPackageName()+"/"+R.raw.daocai2;
                    break;
                case KmOSDMessage.KM_OSDTV_TYPE_ZHENGGU:
                case KmOSDMessage.KM_OSDTV_TYPE_GAOGUAI:
                    path = "android.resource://"+context.getPackageName()+"/"+R.raw.whistle1;
                    break;
                case KmOSDMessage.KM_OSDTV_TYPE_SHACHUI:
                    num = context.getResources().getStringArray(R.array.shachui).length;
                    path = context.getResources().getStringArray(R.array.shachui)[(int)(Math.random()*num)];
                    path = "android.resource://"+context.getPackageName()+"/"+R.raw.shachui;
                    break;
                default:
                    path = "";
                    break;
            }
        }
        return path;
    }*/
        
    public void hideView(KmOSDMessage type){
        switch (type.getTypeID()) {
            case KmOSDMessage.KM_OSDTV_TYPE_NEXTSONG:
                cutsongctrl.hide();
                break;
            case KmOSDMessage.KM_OSDTV_TYPE_PAUSE:
                repauseshow.stop();
                break;
            default:
                break;
        }
    }
    
    public void clear() {
        playctrlpause.setVisibility(INVISIBLE);
        playctrlimageview.setVisibility(INVISIBLE);
        if (bless != null) {
            bless.hide();
        }
        if (tftploadSharedView != null) {
            tftploadSharedView.hide();
        }
        cutsongctrl.hide();
    }
    
    private void clearLevel1() {
        playctrlimageview.setVisibility(INVISIBLE);
    }
    
    private void clearLevel2() {
        if (bless != null) {
            bless.hide();
        }
        if (tftploadSharedView != null) {
            tftploadSharedView.hide();
        }
        cutsongctrl.hide();
    }
    
    //---------------------------------------
    
//    private Context   context   = null;
    private ImageView playctrlimageview = null;
    private ImageView playctrlpause = null;
    private BlessView  bless  = null;
    private TftpSharedView tftploadSharedView  = null;
//    private EmojiDomParserHelper emojihelper = null;
    private static int playctrltimeout = 6000;
    private static int blessshowtimeout = 10000;
    private AsyncPlayer atmoplayer = null;
    private AnimationDrawable animationdrawable = null;
    private CutSongView cutsongctrl = null;
    private RepeatTimerTask repauseshow = null;
    
    
    private Runnable  mshowRunnable_Level1 = null;
    private Runnable  mshowRunnable_Level2 = null;
    
    private class ShowOsdRunnable implements Runnable{
        
        private KmOSDMessage type = null;
        public ShowOsdRunnable(KmOSDMessage type) {
            super();
            this.type = type;
        }

        @Override
        public void run() {
            if(type == null){
                return;
            }
            int id = type.getTypeID();
            if( id== KmOSDMessage.KM_OSDTV_TYPE_PLAY
                    || id == KmOSDMessage.KM_OSDTV_TYPE_TRACK_ORIGIN
                    || id == KmOSDMessage.KM_OSDTV_TYPE_TRACK_INSTRU
                    || id == KmOSDMessage.KM_OSDTV_TYPE_MUTE
                    || id == KmOSDMessage.KM_OSDTV_TYPE_UNMUTE
                    || id == KmOSDMessage.KM_OSDTV_TYPE_REPLAY
                    || id == KmOSDMessage.KM_OSDTV_TYPE_ZHENGGU
                    || id == KmOSDMessage.KM_OSDTV_TYPE_CHANGJIANG
                    || id == KmOSDMessage.KM_OSDTV_TYPE_GAOGUAI
                    || id == KmOSDMessage.KM_OSDTV_TYPE_HESHENG
                    || id == KmOSDMessage.KM_OSDTV_TYPE_HECAI
                    || id == KmOSDMessage.KM_OSDTV_TYPE_DAOCAI
                    || id == KmOSDMessage.KM_OSDTV_TYPE_GU
                    || id == KmOSDMessage.KM_OSDTV_TYPE_SHACHUI
                    || id == KmOSDMessage.KM_OSD_TYPE_PLAYCTRL_OPEN_GRADE
                    || id == KmOSDMessage.KM_OSD_TYPE_PLAYCTRL_CLOSE_GRADE) {
                clearLevel1();
                if (atmoplayer != null) {
                    atmoplayer.stop();
                }
            } else if (id == KmOSDMessage.KM_OSDTV_TYPE_NEXTSONG
                    || id == KmOSDMessage.KM_OSDTV_TYPE_TFTPPIC
                    || id == KmOSDMessage.KM_OSDTV_TYPE_BLESS) {
                clearLevel2();
                if(id == KmOSDMessage.KM_OSDTV_TYPE_NEXTSONG) {
                    if (mCutSongDisplayListener != null) {
                        mCutSongDisplayListener.onDisplayEnd();
                    }
                }
            }
         }
    }
    
    private class ShowPauseTime implements IActionCallback{
        
        private int ncount = 0;
        
        @Override
        public void repeat() {
    
            if(playctrlimageview.getVisibility() == VISIBLE){
                playctrlpause.setVisibility(INVISIBLE);
                ncount = 7;
            }
            if(ncount > 6 ){
                // 隐藏
                if (playctrlpause.getVisibility() == VISIBLE) {
                    playctrlpause.setVisibility(INVISIBLE);
                }
            }else{
                // 显示
//                clearLevel1();
                if (  playctrlpause.getVisibility() == INVISIBLE) {
                    clearLevel1();
                    playctrlpause.setVisibility(VISIBLE);    
                }
            }
            ncount = (++ncount)%9;
        }

        @Override
        public void start() {
            ncount = 0;
        }

        @Override
        public void stop() {
            ncount = 0;
            playctrlpause.setVisibility(INVISIBLE);
        }
        
    }
    
    public static interface CutSongDisplayListener{
        void onDisplayStart();
        void onDisplayEnd();
        void onDisplayRepeat();
    }
    
    private CutSongDisplayListener mCutSongDisplayListener = null;
    
    public void setCutSongDisplayListener(CutSongDisplayListener listener) {
        mCutSongDisplayListener = listener;
    }
    
    private class CutSongView  {
        RelativeLayout cutsonglay = null;
        ViewGroup parentview = null;
//        Context   context = null;
        ImageView songCover = null;
        ImageView scoreIcon = null;
        TextView textsong = null;
        TextView textsinger = null;
        TextView loadprocess = null;
        boolean bstatus = false;
        AnimationSet animationSet1 = null;
        TranslateAnimation  ta = null;
//        private GetAnnouncerCoverPresenter mGetAnnouncerCoverPresenter;
        public CutSongView(ViewGroup view, Context context) {
//            this.context =  context;
            parentview   =  view;
            cutsonglay =  (RelativeLayout) parentview.findViewById(R.id.cutsong_ordinary);
            cutsonglay.setBackgroundResource(R.drawable.next1);
            songCover = (ImageView) parentview.findViewById(R.id.songCover);
            
            scoreIcon = (ImageView) parentview.findViewById(R.id.scoreicon);
            textsong = (TextView) parentview.findViewById(R.id.songtext);
            textsinger = (TextView) parentview.findViewById(R.id.singertext);
            loadprocess = (TextView) parentview.findViewById(R.id.loadprocess);
            animationSet1 = new AnimationSet(true);
            ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF,1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f);// 设置动画的偏移效果
            ta.setDuration(500);// 动画持续时间15000
            animationSet1.addAnimation(ta);
            animationSet1.setFillAfter(true);// 保持动画效果，在动画结束时
            scoreIcon.setVisibility(INVISIBLE);
            bstatus = false;
        }
        
        public void show(boolean bscore){
            bstatus = true;
            if (bscore) {
                scoreIcon.setVisibility(VISIBLE);
            } else {
                scoreIcon.setVisibility(INVISIBLE);
            }
            cutsonglay.setVisibility(VISIBLE);
            cutsonglay.startAnimation(animationSet1);
        }
        
        public void hide(){
            cutsonglay.setVisibility(INVISIBLE);
            scoreIcon.setVisibility(INVISIBLE);
            cutsonglay.clearAnimation();
            bstatus = false;
        }
        
        public void updateLoadProcess(String info){
//            EvLog.d("updateLoadProcess info=" + info);
            loadprocess.setText(info);
        }
            
        public void setSong(String name){
            if (TextUtils.isEmpty(name)) {
                textsong.setText(R.string.unknownsong);
            } else {
                textsong.setText(name);
            }
            //报幕显示歌名多行和单行显示
            int maxsize = parentview.getResources().getDimensionPixelSize(R.dimen.px60);
            int minsize = parentview.getResources().getDimensionPixelSize(R.dimen.px40);
            float textsize = 0;            
            textsong.setTextSize(TypedValue.COMPLEX_UNIT_PX,maxsize);
            textsong.setSingleLine(true);
            TextPaint textpaint = textsong.getPaint();
            float maxpaintsize = textpaint.measureText((String) textsong.getText());
            textsong.setTextSize(TypedValue.COMPLEX_UNIT_PX,minsize);
            textpaint = textsong.getPaint();
            float minpaintsize = textpaint.measureText((String) textsong.getText());
            if (textsong.getWidth() > 0) {
                float defualtsize = textsong.getWidth();
                float b1 = maxpaintsize/defualtsize;
                float b2 = minpaintsize/defualtsize;
                if ( b2 > 1 ) {
                    textsize = minsize;
                } else if ( b1 > 1 && b2 <= 1) {
                    textsize = maxsize / b1;
                } else if ( b1 <= 1 ) {
                    textsize = maxsize;
                }     
            } else {
                textsize = maxsize;
            }
//            EvLog.d("textsize:" + textsize);
            textsong.setTextSize(TypedValue.COMPLEX_UNIT_PX,textsize);
        }
        
        public void setSinger(String name){
            if (TextUtils.isEmpty(name)) {
                textsinger.setText(R.string.nosinger);
            } else {
                textsinger.setText(name);
            }            
        }
        
        public boolean getVisibility(){
            return bstatus;
        }
        
        private DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .build();
        
        /**
         * @brief : [启动获取报幕封面图片的任务]
         * @param song
         */
        public void startGetAnnouncerCoverTask(Song song) {
            if (song == null) {
                return;
            }
            songCover.setImageBitmap(null);
            String url = GetAnnouncerCoverPresenter.getCoverUrl(song);
            if (TextUtils.isEmpty(url)) {
                return;
            }
            ImageLoader.getInstance().displayImage(url, songCover, mOptions);
        }
    }
    
    private class BlessView  {
        
        RelativeLayout blesslay = null;
        ViewGroup parentview = null;
//        Context   context = null;
        TextView singer = null;
        TextView blesstext = null;
        ImageView blesspic = null;
        boolean bstatus = false;
        public BlessView(ViewGroup view, Context context) {
//            this.context =  context;
            parentview   =  view;
            blesslay =  (RelativeLayout) parentview.findViewById(R.id.blesslay);
            Bitmap bmp = BitmapUtil.getBitmapByResId(context, R.drawable.blessbg);
            blesslay.setBackground(new BitmapDrawable(bmp));
            singer = (TextView) parentview.findViewById(R.id.blessername);
            blesstext = (TextView) parentview.findViewById(R.id.blesstext);
            blesspic = (ImageView) parentview.findViewById(R.id.blesserpic);
            bstatus = false;
        }
        
        public void show(){
            blesslay.setVisibility(VISIBLE);
            bstatus = true;
        }
        
        public void hide(){
            blesslay.setVisibility(INVISIBLE);
            bstatus = false;
        }
            
        public void setCustomerID(String customerid, String username){
            String path = null;
            String name = null;
            if (customerid != null) {
                path = MembersInfoList.getInstance().getPicPath(customerid);
                name = MembersInfoList.getInstance().getCustomerName(customerid);
            }
            if(!TextUtils.isEmpty(name)){
                singer.setText(name);
            }else if(!TextUtils.isEmpty(username)){
                singer.setText(username);
            }else{
                singer.setText(R.string.nouser);
            }
            if(!TextUtils.isEmpty(path)){
                blesspic.setVisibility(VISIBLE);
                blesspic.setImageBitmap(BitmapUtil.getBmpByPath(path));
            }else{
                blesspic.setVisibility(INVISIBLE);
            }            
        }
        
        public void setBlessText(CharSequence charSequence){
            blesstext.setText(charSequence);
        }        
        
        public boolean getVisibility(){
            return bstatus;
        }
    }
    
    private class TftpSharedView  {
        
        RelativeLayout tftplay = null;
        ViewGroup parentview = null;
//        Context   context = null;
        TextView nametext = null;
        ImageView tftppic = null;
        ImageView memberpic = null;
        boolean bstatus = false;
        
        public TftpSharedView(ViewGroup view, Context context) {
//            this.context =  context;
            parentview   =  view;
            tftplay =  (RelativeLayout) parentview.findViewById(R.id.tftplay);
            tftplay.setBackgroundResource(R.drawable.osd_local_picbg);
           /* Bitmap bmp = BitmapUtil.getBitmapByResId(BaseApplication.getInstance(), R.drawable.osd_local_picbg);
            tftplay.setBackgroundDrawable(new BitmapDrawable(bmp));*/
            nametext = (TextView) parentview.findViewById(R.id.tftpname);
            tftppic = (ImageView) parentview.findViewById(R.id.tftppic);
            memberpic = (ImageView) parentview.findViewById(R.id.memberpic);
            bstatus = false;
        }
        
        public void show(){
            tftplay.setVisibility(VISIBLE);
            bstatus = true;
        }
        
        public void hide(){
            tftplay.setVisibility(INVISIBLE);
            bstatus = false;
        }
            
        public void setCustomerID(String customerid, String username){
            String path = null;
            String name = null;
            if (customerid != null) {
                path = MembersInfoList.getInstance().getPicPath(customerid);
                name = MembersInfoList.getInstance().getCustomerName(customerid);
            }
            if(!TextUtils.isEmpty(name)){
                nametext.setText(name);
            }else if(!TextUtils.isEmpty(username)){
                nametext.setText(username);
            }else{
                nametext.setText(R.string.nouser);
            }
            if(!TextUtils.isEmpty(path)){
                memberpic.setImageBitmap(BitmapUtil.getBmpByPath(path));
            }else{
                memberpic.setImageResource(R.drawable.osd_local_defaultfig);
            }            
        }
        
        public void setTftpPic(Bitmap bm){
            if(bm != null){
                tftppic.setImageBitmap(bm);    
            }            
        }        
        
        public boolean getVisibility(){
            return bstatus;
        }
    }
}
