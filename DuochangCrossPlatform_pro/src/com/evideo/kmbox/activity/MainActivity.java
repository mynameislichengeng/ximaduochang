package com.evideo.kmbox.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.awirtz.util.RingBuffer;
import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.ErrorInfo;
import com.evideo.kmbox.KmApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.SystemConfigManager;
import com.evideo.kmbox.dao.WholeDAOHelper;
import com.evideo.kmbox.model.bmp.BmpCacheManager;
import com.evideo.kmbox.model.charge.IBaseCharge.IChargeInitListener;
import com.evideo.kmbox.model.chargeproxy.ChargeProxy;
import com.evideo.kmbox.model.chargeproxy.DeviceCharge;
import com.evideo.kmbox.model.chargeproxy.DeviceCommu;
import com.evideo.kmbox.model.dao.data.DatabaseManager;
import com.evideo.kmbox.model.dao.data.DatabaseSynchronizer;
import com.evideo.kmbox.model.dao.data.DatabaseSynchronizer.IDatabaseSyncListener;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.dao.data.SongSubject;
import com.evideo.kmbox.model.dao.data.StorageManager;
import com.evideo.kmbox.model.datacenter.DataCenterCommu;
import com.evideo.kmbox.model.datacenter.DataCenterCommu.IDCLoginResultListener;
import com.evideo.kmbox.model.datacenter.IHomePictureUpdateListener;
import com.evideo.kmbox.model.datacenter.UrlList;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.device.IDeviceConfig;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.down.DownError;
import com.evideo.kmbox.model.down.KmSongDownManager;
import com.evideo.kmbox.model.httpd.HttpdServer;
import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
import com.evideo.kmbox.model.observer.activity.ActivitySubject;
import com.evideo.kmbox.model.observer.keyevent.IKeyEventObserver;
import com.evideo.kmbox.model.observer.keyevent.KeyEventSubject;
import com.evideo.kmbox.model.observer.net.INetworkInfoObserver;
import com.evideo.kmbox.model.observer.net.IWifiInfoObserver;
import com.evideo.kmbox.model.observer.net.NetworkInfoSubject;
import com.evideo.kmbox.model.observer.net.WifiInfoSubject;
import com.evideo.kmbox.model.observer.screen.IScreenInfoObserver;
import com.evideo.kmbox.model.observer.screen.ScreenInfoSubject;
import com.evideo.kmbox.model.pay.PayActivity;
import com.evideo.kmbox.model.player.KmAudioTrackMode;
import com.evideo.kmbox.model.player.KmPlayerError;
import com.evideo.kmbox.model.player.KmVideoPlayerType;
import com.evideo.kmbox.model.playerctrl.IPlayStateObserver;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrl;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrlType.PlayMode;
import com.evideo.kmbox.model.playerctrl.KmPlayerCtrlType.PlayerCtrlState;
import com.evideo.kmbox.model.playerctrl.PlayCtrlHandler;
import com.evideo.kmbox.model.playerctrl.PlayCtrlHandler.IPlayCtrlEventListener;
import com.evideo.kmbox.model.playerctrl.PlayError;
import com.evideo.kmbox.model.playerctrl.PlayListDAOManager;
import com.evideo.kmbox.model.playerctrl.PlayListDAOManager.ListHandler;
import com.evideo.kmbox.model.playerctrl.list.BroadcastListManager;
import com.evideo.kmbox.model.playerctrl.list.BroadcastListManager.BroadcastPrepareListener;
import com.evideo.kmbox.model.playerctrl.list.FavoriteListManager;
import com.evideo.kmbox.model.playerctrl.list.FreeSongListManager;
import com.evideo.kmbox.model.playerctrl.list.FreeSongListManager.IGetFreeSongEventListener;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager;
import com.evideo.kmbox.model.playerctrl.list.PlayListManager.IPlayListListener;
import com.evideo.kmbox.model.playerctrl.list.SongOperationManager;
import com.evideo.kmbox.model.playerctrl.sunglist.SungListManager;
import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.model.song.MembersInfoList;
import com.evideo.kmbox.model.songinfo.KmPlayListItem;
import com.evideo.kmbox.model.songinfo.SongCategory;
import com.evideo.kmbox.model.songinfo.SongDataState;
import com.evideo.kmbox.model.storage.CacheManagerUtil;
import com.evideo.kmbox.model.tftpserver.TftpService;
import com.evideo.kmbox.model.thirdapp.ApkJumpHandler;
import com.evideo.kmbox.model.thirdapp.ApkJumpHandler.IHuodongJumpPictureDismissListener;
import com.evideo.kmbox.model.thirdapp.ApkJumpParamParser;
import com.evideo.kmbox.model.thirdapp.HomeWatchReceiver.IHomeKeyPressListener;
import com.evideo.kmbox.model.thirdapp.ThirdAppKeyEventManager;
import com.evideo.kmbox.model.umeng.EventConst;
import com.evideo.kmbox.model.umeng.PageName;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.model.update.BackgroundUpdateManager;
import com.evideo.kmbox.model.update.resource.UpdateDCCommu;
import com.evideo.kmbox.model.update.resource.UpdateResourceManager;
import com.evideo.kmbox.model.update.resource.UpdateResourceManager.IUpdateHomeRightBottomIconListener;
import com.evideo.kmbox.presenter.BasePresenter;
import com.evideo.kmbox.presenter.CommuPresenter;
import com.evideo.kmbox.service.log.LogSpaceManager;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;
import com.evideo.kmbox.util.HttpUtils;
import com.evideo.kmbox.util.NetUtils;
import com.evideo.kmbox.util.NetUtils.NetSpeedInfo;
import com.evideo.kmbox.util.PayInfoSyncReqBean;
import com.evideo.kmbox.util.TimeUtil;
import com.evideo.kmbox.util.XMLUtlis;
import com.evideo.kmbox.util.XmlUtil;
import com.evideo.kmbox.widget.MainBottomWidget;
import com.evideo.kmbox.widget.StatusBarMsg;
import com.evideo.kmbox.widget.StatusBarWidget.IStatusBarKeyListener;
import com.evideo.kmbox.widget.charge.ChargeViewManager;
import com.evideo.kmbox.widget.charge.ChargeViewManager.IAuthListener;
import com.evideo.kmbox.widget.charge.ChargeViewManager.IChargeFinishListener;
import com.evideo.kmbox.widget.common.CommonDialog;
import com.evideo.kmbox.widget.common.OrderSongAnimController;
import com.evideo.kmbox.widget.common.SelectDecodeDialog;
import com.evideo.kmbox.widget.common.ToastUtil;
import com.evideo.kmbox.widget.intonation.KmMainView;
import com.evideo.kmbox.widget.intonation.KmMainView.IBottomMVClickListener;
import com.evideo.kmbox.widget.mainview.MainViewManager;
import com.evideo.kmbox.widget.msgview.KmOSDMessage;
import com.evideo.kmbox.widget.msgview.KmOSDMessage.ICutSongParams;
import com.evideo.kmbox.widget.msgview.KmOSDMessageManager;
import com.evideo.kmbox.widget.msgview.KmOSDMessageView;
import com.evideo.kmbox.widget.mv.selected.MvSelectedView;
import com.evideo.kmbox.widget.playctrl.MVCtrlWidgetManager;
import com.evideo.kmbox.widget.playctrl.MVCtrlWidgetManager.IMVKeyListener;
import com.evideo.kmbox.widget.playctrl.PlayCtrlWidget;
import com.evideo.kmbox.widget.playctrl.PlayCtrlWidget.IPlayCtrlWidgetListener;
import com.migu.sdk.api.CommonInfo;
import com.migu.sdk.api.CommonPayInfo;
import com.migu.sdk.api.MiguSdk;
import com.migu.sdk.api.PayCallBack;
import com.shcmcc.tools.GetSysInfo;
import com.shcmcc.tools.UserAccountInfo;

import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends BaseActivity implements INetworkInfoObserver, IWifiInfoObserver,
        IScreenInfoObserver, IKeyEventObserver, AudioManager.OnAudioFocusChangeListener,
        IPlayListListener, IPlayStateObserver, BroadcastPrepareListener,
        IPlayCtrlWidgetListener, IMVKeyListener,
        IUpdateHomeRightBottomIconListener, IHomePictureUpdateListener, IHomeKeyPressListener,
        IBottomMVClickListener, IHuodongJumpPictureDismissListener {
    private String TAG = "MainActivity";
    private String sign = "";
    private Context mContext = null;

    private KmMainView mMainView = null;
    private long mSwitchSongTime = 0;
    private boolean mIsActivityOnStopExecute = false;
    public int mPlayerStatusBeforeScreenOff = PlayerCtrlState.STATE_IDLE;
    public static final int DELAY_SEND_CUT_SONG_WHEN_PLAY_ERROR = 5000;//ms
    private boolean mIsShowBootPicture = false;
    private CommonDialog mExitAppDialog;
    /**
     * [显示报幕]
     */
    private ShowAnnounceRunnable mShowAnnounceRunnable = null;
    /**
     * [隐藏报幕]
     */
    private HideAnnounceRunnable mHideAnnounceRunnable = null;

    private static final int DELAY_SHOW_ANNOUNCE = 0;
    private static final int DELAY_HIDE_ANNOUNCE = 3000;


    /**
     * [报幕开始显示时间]
     */
    private long mAnnounceShowStartTime = 0;
    /*activity pause时记录歌曲播放的时间进度*/
//    private long mSavePauseTime = 0l;
    private KmPlayListItem mPauseSongItem = null;
    private MVCtrlWidgetManager mMVCtrlWidget = new MVCtrlWidgetManager();

    private MvSelectedView mMvSelectedView = null;
    private TftpService mTftp = null;
    private int mContinuousCutSongCount = 0;

    private boolean mIsAuthedOnCreate = false;
    private String resultDesc;
    private String userId;
    private String userId1;
    private String token;
    private String resultDesc1;
    private String messagePassword;
    private CommonInfo commonInfo;
    private CommonPayInfo commonPayInfos;
    public static MainActivity mainActivity = null;

    //  增值计费
    private String ctype, orderId, payNum, chargePolicy, stbID, bizExpiryDate, operCode,
            index, isMonthly, customPeriod, billTimes, billInterval, campaignId, fee, spCode, servCode,
            channelCode, contentCode, platForm_code, cpparam, reserveParam, bizType, productCode, cooperaterCode;
    //  增值计费
    private String loginPassword, loginAcount, stdid = "哈哈哈哈哈 测试参数传递";

    private String epgToken, epgProvince, epgCityCode, epgCopyrightId, epgUserGroup;
    private String productCode1;
    private String resultDesc2;
    private String terminalType;
    private String rder;

    /**
     * [功能说明] 显示报幕
     *
     * @param canScore     是否可评分
     * @param autoDisapper 是否自动消失
     *                     showPercent 是否显示百分比
     */
    private void showAnnounce(boolean canScore, boolean autoDisapper,/*boolean showPercent*/String text) {

        KmOSDMessageManager.getInstance().removeKmMessageDrawView(KmOSDMessage.KM_OSDTV_TYPE_NEXTSONG);

        final KmPlayListItem currentSong = KmPlayerCtrl.getInstance().getPlayingSong();
        if (currentSong == null) {
            EvLog.e("showAnnounce getPlayingSong null");
            return;
        }

        KmOSDMessage type = new KmOSDMessage(KmOSDMessage.KM_OSDTV_TYPE_NEXTSONG);

        ICutSongParams params = null;
        if (currentSong.isCanScoreInDB() //可评分歌曲
                && !currentSong.isSongCanScore() //评分文件不存在
                && currentSong.getSongCategory() != SongCategory.CATEGORY_BROADCAST //非公播歌曲
                && autoDisapper) { //字幕自动消失
            params = new ICutSongParams(currentSong.getSong(), canScore, currentSong.getSongName(),
                    currentSong.getSingerName(), text, autoDisapper, new KmOSDMessageView.CutSongDisplayListener() {

                @Override
                public void onDisplayStart() {
                }

                @Override
                public void onDisplayRepeat() {
                }

                @Override
                public void onDisplayEnd() {
                    EvLog.d("recv onAnimationEnd");
                    if (!MainViewManager.getInstance().isMainViewVisible() &&
                            currentSong.getSongCategory() == SongCategory.CATEGORY_PLAYLIST) {
                        ToastUtil.showLongToast(this_, getResources().getString(R.string.toast_erc_downfailed));
                    }
                }
            });
        } else {
            params = type.newICutSongParams(currentSong.getSong(), canScore, currentSong.getSongName(),
                    currentSong.getSingerName(), text, autoDisapper);
        }

        type.setOsdParams(params);
        KmOSDMessageManager.getInstance().sendKMMessageDrawView(type);
    }

    //[切歌]
    public void playNextSong() {
        EvLog.d("play next song");
        selectDecoder();

        if (!NetUtils.isNetworkConnected(this_)) {
            ToastUtil.showLongToast(this_, this_.getResources().getString(R.string.toast_network_error));
            MainViewManager.getInstance().updateMainViewCurrentSong(this_.getResources().getString(R.string.toast_network_error));
            return;
        }

        resetMainView();
        mMVCtrlWidget.hide();
        delayHideAnnounce(0);
//        adjustPlayList();
        KmPlayListItem item = null;

        //先播放点播歌曲
        if (PlayListManager.getInstance().getCount() > 0) {
            item = PlayListManager.getInstance().getItemByPos(0);
        } else {
            //FIXME notify prepare broadcast song
            KmPlayListItem broadcastSongItem = BroadcastListManager.getInstance().getLocalCompleteBSong();
            if (broadcastSongItem == null) {
                BroadcastListManager.getInstance().getRandomSong();
                return;
            } else {
                item = broadcastSongItem;
            }
        }

        KmPlayerCtrl.getInstance().playNextSong(item);

        //判断报幕是否显示评分标志
        boolean showGradeFlag = false;
        delayShowAnnnounce(showGradeFlag, DELAY_SHOW_ANNOUNCE);
        return;
    }

    private void delayShowAnnnounce(boolean canScore, long delayTime) {
        if (mShowAnnounceRunnable != null) {
            BaseApplication.getHandler().removeCallbacks(mShowAnnounceRunnable);
            mShowAnnounceRunnable = null;
        }
        mShowAnnounceRunnable = new ShowAnnounceRunnable(canScore);
        BaseApplication.getHandler().postDelayed(mShowAnnounceRunnable, delayTime);
    }

    // 登录接口
    public String getXML() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                InputStream in = null;   //读
                int responseCode = 0;    //远程主机响应的HTTP状态码
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                stringBuffer.append("<message module=\"SCSP\" version=\"1.1\">");
                stringBuffer.append(" <header action=\"REQUEST\" command=\"LOGINAUTH\"/>");
                stringBuffer.append("<body>");
                stringBuffer.append("  <loginAuth loginType=\"5\" account=\"0109722567653822847\" password=\"822847\" stbId=\"005303FF0002390018502CCCE6343CEF\"/>");
                stringBuffer.append("</body>");
                stringBuffer.append("</message>");

                try {
                    String s = stringBuffer.toString();
                    byte[] xmlbyte = s.getBytes("UTF-8");

                    URL url = new URL("http://183.192.162.103:80/scspProxy");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(30000);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestProperty("Content-Type", "text/xml");
                    conn.setRequestProperty("Content-Length",
                            String.valueOf(xmlbyte.length));

                    conn.getOutputStream().write(xmlbyte);
                    conn.getOutputStream().flush();
                    conn.getOutputStream().close();
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("请求url失败");
                    }
                    InputStream is = conn.getInputStream();// 获取返回数据
                    // 使用输出流来输出字符(可选)
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = is.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                    sign = out.toString("UTF-8");
                    out.close();
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sign.getBytes());

                    ArrayList<String> signs = XMLUtlis.signXml(byteArrayInputStream);
                    resultDesc = signs.get(1);

                    userId1 = signs.get(2);
                    messagePassword = signs.get(4);

                    token = signs.get(6);
                    if (resultDesc.equals("成功")) {
                        getConnect();
//                       getUnsubscribe();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return sign;
    }

    /**
     * [点歌延时时间]
     */
    public static final int ORDER_SONG_DELAY_DURATION = 300;

    private void realTopSong(final String customerId, final int songId, final SongOperationManager.ITopSongResultListener listener) {
        BaseApplication.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean ret = PlayListManager.getInstance().addSong(customerId, songId, true);
                if (listener != null) {
                    if (ret) {
                        listener.onTopSongSuccess(songId);
                    } else {
                        listener.onTopSongFailed(songId);
                    }
                }
            }
        }, ORDER_SONG_DELAY_DURATION);
    }

    // 内容下单
    public String getConnect() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                InputStream in = null;   //读
                int responseCode = 0;    //远程主机响应的HTTP状态码
                StringBuffer xml = new StringBuffer();
                xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                xml.append("<message module=\"SCSP\" version=\"1.1\">");
                xml.append("<header action=\"REQUEST\" command=\"PRO_TO_CONTNET\"/>");
                xml.append("<body>");
                xml.append("<proToContent operation=\"2\">");
                xml.append("<contentId>1032001422</contentId>");
                xml.append("<strategyCode><![CDATA[9901000297]]></strategyCode>");
                xml.append("<platform>699213</platform>");
                xml.append("</proToContent>");
                xml.append("</body>");
                xml.append("</message>");
                try {
                    String s = xml.toString();
                    byte[] xmlbyte = s.getBytes("UTF-8");

                    URL url = new URL("http://183.192.162.165:30060/oms-api/OmsProductManageServlet");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(30000);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestProperty("Content-Type", "text/xml");
                    conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                    conn.setRequestProperty("Content-Length",
                            String.valueOf(xmlbyte.length));

                    conn.getOutputStream().write(xmlbyte);
                    conn.getOutputStream().flush();
                    conn.getOutputStream().close();
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("请求url失败");
                    }
                    InputStream is = conn.getInputStream();// 获取返回数据
                    // 使用输出流来输出字符(可选)
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = is.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }

                    sign = out.toString("UTF-8");
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sign.getBytes());
                    ArrayList<String> conectList = XMLUtlis.connectXml(byteArrayInputStream);
                    resultDesc1 = conectList.get(1);
                    if (resultDesc1.equals("OK")) {

                        getPayOrder();
                    }
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Log.i("gsp", "getConnect: ");
        return sign;
    }

    //鉴权接口
    public void getPayOrder() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                InputStream in = null;   //读
                int responseCode = 0;    //远程主机响应的HTTP状态码
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                stringBuffer.append("<message module=\"SCSP\" version=\"1.1\">");
                stringBuffer.append("<header action=\"REQUEST\" command=\"AUTHORIZE\"/>");
                stringBuffer.append("<body>");
                Log.i("gsp", "run: 参数 token" + token);
                stringBuffer.append("<authorize userId=\"0109722567653822847\" terminalId=\"005303FF0002390018502CCCE6343CEF\" copyRightId=\"699213\" systemId=\"0\" contentId=\"1032001422\" consumeLocal=\"29\" consumeScene=\"01\" consumeBehaviour=\"02\" preview=\"0\" token=\"" + token + "\"/>");
                stringBuffer.append("</body>");
                stringBuffer.append("</message>");
                try {
                    String s = stringBuffer.toString();
                    Log.i("gsp", "run: 鉴权的报文" + s);
                    byte[] xmlbyte = s.getBytes("UTF-8");

                    URL url = new URL("http://183.192.162.103:80/scspProxy");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(30000);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestProperty("Content-Type", "text/xml");
                    conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                    conn.setRequestProperty("Content-Length",
                            String.valueOf(xmlbyte.length));

                    conn.getOutputStream().write(xmlbyte);
                    conn.getOutputStream().flush();
                    conn.getOutputStream().close();
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("请求url失败");
                    }
                    InputStream is = conn.getInputStream();// 获取返回数据
                    // 使用输出流来输出字符(可选)
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = is.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }

                    sign = out.toString("UTF-8");
                    out.close();
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sign.getBytes());
                    ArrayList<String> conectList = XMLUtlis.authorizeXml(byteArrayInputStream);
                    String result = conectList.get(0);
                    productCode1 = conectList.get(7);
                    if (!result.equals("0")) {
                        getAdvPay();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    // 调起咪咕SDK
    public void getAdvPay() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                InputStream in = null;   //读
                int responseCode = 0;    //远程主机响应的HTTP状态码
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                stringBuffer.append("<message module=\"SCSP\" version=\"1.1\">");
                stringBuffer.append("<header action=\"REQUEST\" command=\"ADVPAY\"/>");
                stringBuffer.append("<body>");
                stringBuffer.append("<advPay seqId=\"18776077474\" userId=\"" + userId1 + "\" terminalId=\"005303FF0002390018502CCCE6343CEF\" copyRightId=\"699213\" productCode=\"8802000330\" amount=\"100\" contentId=\"1032001422\" consumeLocal=\"21\" consumeScene=\"01\" consumeBehaviour=\"02\" channelId=\"00001\" payType=\"16\" accountIdentify=\"18776077474\" token=\"" + token + "\"/>");
                stringBuffer.append("</body>");
                stringBuffer.append("</message>");
                try {
                    String s = stringBuffer.toString();
                    byte[] xmlbyte = s.getBytes("UTF-8");

                    URL url = new URL("http://183.192.162.103:80/scspProxy");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(30000);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestProperty("Content-Type", "text/xml");
                    conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                    conn.setRequestProperty("Content-Length",
                            String.valueOf(xmlbyte.length));

                    conn.getOutputStream().write(xmlbyte);
                    conn.getOutputStream().flush();
                    conn.getOutputStream().close();
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("请求url失败");
                    }
                    InputStream is = conn.getInputStream();// 获取返回数据
                    // 使用输出流来输出字符(可选)
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = is.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }

                    sign = out.toString("UTF-8");
                    out.close();
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sign.getBytes());
                    ArrayList<String> conectList = XMLUtlis.advPayXml(byteArrayInputStream);

                    resultDesc2 = conectList.get(1);
                    String payParam = conectList.get(6);
                    Log.i("gsp", "run: 下单参数" + payParam + "resultDesc2" + resultDesc2);
                    if (resultDesc2.equals("成功")) {
                        String payParamName = new String(Base64.decode(payParam.getBytes(), Base64.DEFAULT));
                        try {
                            InputStream isNa = new ByteArrayInputStream(payParamName.getBytes());
                            Log.i(TAG, "showPay:  获取的流是" + isNa);
                            PayInfoSyncReqBean payInfoSyncReq = XMLUtlis.pull2xmls(isNa);
                            ctype = payInfoSyncReq.getCtype();
                            orderId = payInfoSyncReq.getOrderId();
                            payNum = payInfoSyncReq.getPayNum();
                            bizType = payInfoSyncReq.getBizType();
                            chargePolicy = payInfoSyncReq.getChargePolicy();
                            stbID = payInfoSyncReq.getStbID();
                            bizExpiryDate = payInfoSyncReq.getCustomBizExpiryDate();
                            operCode = payInfoSyncReq.getOperCode();

                            index = payInfoSyncReq.getPayInfoList().get(0).getIndex();
                            isMonthly = payInfoSyncReq.getPayInfoList().get(0).getIsMonthly();
                            customPeriod = payInfoSyncReq.getPayInfoList().get(0).getCustomPeriod();
                            billTimes = payInfoSyncReq.getPayInfoList().get(0).getBillTimes();
                            billInterval = payInfoSyncReq.getPayInfoList().get(0).getBillInterval();
                            campaignId = payInfoSyncReq.getPayInfoList().get(0).getCampaignId();
                            fee = payInfoSyncReq.getPayInfoList().get(0).getFee();
                            spCode = payInfoSyncReq.getPayInfoList().get(0).getSpCode();
                            servCode = payInfoSyncReq.getPayInfoList().get(0).getServCode();
                            channelCode = payInfoSyncReq.getPayInfoList().get(0).getChannelCode();
                            productCode = payInfoSyncReq.getPayInfoList().get(0).getProductCode();
                            contentCode = payInfoSyncReq.getPayInfoList().get(0).getContentCode();
                            platForm_code = payInfoSyncReq.getPayInfoList().get(0).getPlatForm_Code();
                            cpparam = payInfoSyncReq.getPayInfoList().get(0).getCpparam();
                            reserveParam = payInfoSyncReq.getPayInfoList().get(0).getReserveParam();
                            cooperaterCode = payInfoSyncReq.getPayInfoList().get(0).getCooperateCode();
                            Log.i(TAG, "showPay: 解析出来的数值是 " + ctype + "stbID " + stbID + "inder " + index + "orderId " + orderId
                                    + "payNum " + payNum + " bizType " + bizType + "bizExpiryDate " + bizExpiryDate + " chargePolicy" + chargePolicy +
                                    "operCode " + operCode + "isMonthly " + isMonthly + " customPeriod" + customPeriod + "billTimes " + billTimes + "billInterval " + billInterval
                                    + " campaignId" + campaignId + " fee " + fee + " " + "spCode " + spCode + "servCode " + servCode + " channelCode " + channelCode + "productCode "
                                    + productCode + "contentCode " + contentCode + "platForm_code " + platForm_code + "cpparam " + cpparam + "reserveParam " + reserveParam);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //TODO   调起咪咕支付
                        initSdk();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private void initSdk() {

        // TODO 咪咕计费
        Log.i(TAG, "initSdk: 调起SDK支付");
        commonInfo = new CommonInfo();
        commonInfo.setOrderId(orderId);
        commonInfo.setcType(ctype);
        commonInfo.setOperCode(operCode);
        commonInfo.setSyn(true);
        commonInfo.setStbId(stbID);
        commonInfo.setPayNum(payNum);
        commonInfo.setDesc("");
        commonInfo.setCustomBizExpiryDate("");
        commonInfo.setConfirmPageId("");

        commonPayInfos = new CommonPayInfo();
        commonPayInfos.setOrderId(orderId);
        commonPayInfos.setIsMonthly(isMonthly);
        commonPayInfos.setChannelId(channelCode);
        commonPayInfos.setCpId(cooperaterCode);
        commonPayInfos.setContentId(contentCode);
        commonPayInfos.setPrice(fee);
        commonPayInfos.setDescription("");
        commonPayInfos.setScdChannel("");
        commonPayInfos.setProductId(productCode);
        commonPayInfos.setSpCode(spCode);
        commonPayInfos.setServCode(servCode);
        commonPayInfos.setCustomPeriod(customPeriod);
        commonPayInfos.setBillTimes("");
        commonPayInfos.setCampaignId("");
        commonPayInfos.setBillInterval("");
        CommonPayInfo[] commonPayInfo = new CommonPayInfo[1];
        commonPayInfo[0] = commonPayInfos;


        PayCallBack.IPayCallback iPayCallback = new PayCallBack.IPayCallback() {
            @Override
            public void onResult(int i, String s, String s1) {

                if (i == 1) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DeviceCommu.sendPayResult(rder);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                } else {

                    Log.i("gsp", "onResult:1 " + " 返回结果" + i + "结果返回码" + s + "返回结果描述" + s1);
                }


            }

        };

        MiguSdk.pay(MainActivity.this, commonInfo, commonPayInfo, "计费信息",
                "暂留字段", iPayCallback);

    }


    public void getUnsubscribe() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                InputStream in = null;   //读
                int responseCode = 0;    //远程主机响应的HTTP状态码
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                stringBuffer.append("<message module=\"SCSP\" version=\"1.1\">");
                stringBuffer.append(" <header action=\"REQUEST\" command=\"QUITORDER\"/>");
                stringBuffer.append("<body>");
                stringBuffer.append("<quitOrder userId=\"" + userId1 + "\" accountIdentify=\"18776077474\" productCode=\"8802000330\" srcTerminalId=\"\" orderSeq=\"\"  token=\"" + token + "\" />\n");
                stringBuffer.append("</body>");
                stringBuffer.append("</message>");
                try {
                    String s = stringBuffer.toString();
                    Log.i("gsp", "run:退订接口 " + s);
                    byte[] xmlbyte = s.getBytes("UTF-8");

                    URL url = new URL("http://183.192.162.103:80/scspProxy");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(30000);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestProperty("Content-Type", "text/xml");
                    conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                    conn.setRequestProperty("Content-Length",
                            String.valueOf(xmlbyte.length));

                    conn.getOutputStream().write(xmlbyte);
                    conn.getOutputStream().flush();
                    conn.getOutputStream().close();

                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("请求url失败");
                    }
                    InputStream is = conn.getInputStream();// 获取返回数据
                    // 使用输出流来输出字符(可选)
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = is.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }

                    sign = out.toString("UTF-8");
                    Log.i("gsp", "run: 退订返回" + sign);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private class ShowAnnounceRunnable implements Runnable {
        private boolean mCanScore = false;

        public ShowAnnounceRunnable(boolean canScore) {
            mCanScore = canScore;
        }

        @Override
        public void run() {
            EvLog.i(">>>> show annouce");
            updateTopSongInfo();
            mMainView.getStatusBar().showUserHeadImage();
            KmPlayListItem currentPlay = KmPlayerCtrl.getInstance().getPlayingSong();
            if (currentPlay != null && currentPlay.isMediaAvailable()) {
                showAnnounce(mCanScore, false, "");
            } else {
                showAnnounce(mCanScore, false, getResources().getString(R.string.toast_down_stage_prepare_getmedia));
            }
            mAnnounceShowStartTime = System.currentTimeMillis();
            mShowAnnounceRunnable = null;
        }
    }

    ;

    private class HideAnnounceRunnable implements Runnable {
        @Override
        public void run() {
            EvLog.i(">>>> hide annouce");
            KmOSDMessageManager.getInstance().hideKmMessageDrawView(
                    new KmOSDMessage(KmOSDMessage.KM_OSDTV_TYPE_NEXTSONG));
            mHideAnnounceRunnable = null;
        }
    }

    ;

    private void delayHideAnnounce(long time) {
        EvLog.i(">>>>>>>>>>>>> delayHideAnnounce " + time);
        if (mHideAnnounceRunnable != null) {
            BaseApplication.getHandler().removeCallbacks(mHideAnnounceRunnable);
            mHideAnnounceRunnable = null;
        }
        mHideAnnounceRunnable = new HideAnnounceRunnable();
        BaseApplication.getHandler().postDelayed(mHideAnnounceRunnable, time);
    }

    public void switchTrack() {
        mMVCtrlWidget.hide();

        int mode = KmPlayerCtrl.getInstance().getAudioSingMode();
        EvLog.w(" PLAY_CTRL_MSG_SWITCH_TRACK  current mode = " + mode);

        if (mode == KmAudioTrackMode.MODE_ACC) {
            KmPlayerCtrl.getInstance().setAudioSingMode(KmAudioTrackMode.MODE_ORI);
            KmSharedPreferences.getInstance().putBoolean(KeyName.KEY_SETTING_GLOBAL_ORIGINAL_SING, true);
            KmOSDMessageManager.getInstance().sendKMMessageDrawView(new KmOSDMessage(KmOSDMessage.KM_OSDTV_TYPE_TRACK_ORIGIN));
        } else {
            KmPlayerCtrl.getInstance().setAudioSingMode(KmAudioTrackMode.MODE_ACC);
            KmSharedPreferences.getInstance().putBoolean(KeyName.KEY_SETTING_GLOBAL_ORIGINAL_SING, false);
            KmOSDMessageManager.getInstance().sendKMMessageDrawView(new KmOSDMessage(KmOSDMessage.KM_OSDTV_TYPE_TRACK_INSTRU));
        }
    }

    private void updateTopSongInfo() {
        String songInfo = "";
//        final String tagPlayBack = getString(R.string.topsonghint_playback);
        final String tagNextPlay = getString(R.string.topsonghint_nextsong);
        final String tagPlaying = getString(R.string.topsonghint_playing);

//        int playBackCount = PlayBackListManager.getInstance().getCount();
        int playListCount = PlayListManager.getInstance().getCount();
        
        /*if (playBackCount > 0) {
            songInfo = tagPlayBack + PlayBackListManager.getInstance().getItemByPos(0).getSongName();
            if (playBackCount > 1) {
                songInfo += tagNextPlay + PlayBackListManager.getInstance().getItemByPos(1).getSongName();
            } else {
                if (playListCount > 0 ) {
                    songInfo += tagNextPlay + PlayListManager.getInstance().getItemByPos(0).getSongName();
                }
            }
        } else */
        if (playListCount > 0) {
            songInfo = tagPlaying + PlayListManager.getInstance().getItemByPos(0).getSongName();
            if (playListCount > 1) {
                songInfo += tagNextPlay + PlayListManager.getInstance().getItemByPos(1).getSongName();
            }
        } else {
            KmPlayListItem currentPlay = KmPlayerCtrl.getInstance().getPlayingSong();
            //公播歌曲顶部显示
            if (currentPlay != null) {
                if (currentPlay.getSongCategory() == SongCategory.CATEGORY_BROADCAST) {
                    final String broadcastSuffix = getString(R.string.topsonghint_broadcast);
                    songInfo = broadcastSuffix + currentPlay.getSongName();
                }
            } else {
                songInfo = getString(R.string.topsonghint_no_song);
            }
        }
        EvLog.i("topSongHint: " + songInfo);
        MainViewManager.getInstance().updateMainViewCurrentSong(songInfo);
    }

    private boolean mFirstAuthFinish = false;

    /**
     * [功能说明] 计费鉴权
     */
    private void initChargeAuth() {
        EvLog.d("---------------- initChargeAuth -----------");
        if (!ChargeProxy.getInstance().isInitSuccess()) {
            EvLog.d("charge init failed------------");
            return;
        }
        ChargeViewManager.getInstance().auth(new IAuthListener() {

            @Override
            public void onAuthSuccess() {
                mFirstAuthFinish = true;
            }

            @Override
            public void onAuthFailed() {
                mFirstAuthFinish = true;
            }
        });
    }

    private void setChargeOrderBtnClickListener() {
        ChargeViewManager.getInstance().addListener(new IChargeFinishListener() {
            @Override
            public void onChargeSuccess() {
                EvLog.i("onChargeSuccess");
                KmPlayListItem item = KmPlayerCtrl.getInstance().getPlayingSong();
                int msg = PlayCtrlHandler.PLAY_CTRL_MSG_REPLAY_SONG;
                if (item == null) {
                    msg = PlayCtrlHandler.PLAY_CTRL_MSG_NEXT_SONG;
                }
                PlayCtrlHandler.getInstance().sendEmptyMessage(msg);
                DeviceConfigManager.getInstance().setRemainVipTime(ChargeProxy.getInstance().getValidTime());
            }

            @Override
            public void onChargeFailed() {
                if (mShowPayPageRunnable != null) {
                    int playedTime = KmPlayerCtrl.getInstance().getPlayedTime() / 1000;
                    int remainTime = (int) (mShowPayPageRunnable.getMaxPlayTime() - playedTime);
                    if (remainTime > 0) {
                        return;
                    }
                    PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_CUT_SONG);
                }
            }

            @Override
            public void onChargeCancel() {
                if (mShowPayPageRunnable != null) {
                    int playedTime = KmPlayerCtrl.getInstance().getPlayedTime() / 1000;
                    int remainTime = (int) (mShowPayPageRunnable.getMaxPlayTime() - playedTime);
                    if (remainTime > 0) {
                        return;
                    }
                    PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_CUT_SONG);
                }
            }
        });
        mMainView.getStatusBar().setChargeOrderButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DataCenterCommu.getInstance().isLoginSuccess()) {
                    ToastUtil.showLongToast(this_, this_.getResources().getString(R.string.dc_is_login));
                    return;
                }

                if (!ChargeProxy.getInstance().isInitSuccess()) {
                    ToastUtil.showLongToast(this_, this_.getResources().getString(R.string.unicom_pay_error_init_failed));
                    return;
                }
//                // TODO  订购参数
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            rder = DeviceCommu.queryTradeNo("14");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }).start();
//
//                String xml = getXML();


                //DOTO 我要订购
                ChargeViewManager.getInstance().clickChargeView(this_);
            }
        });
    }

    private void initView() {

        initMainView();
        KmOSDMessageManager.getInstance().initViewRes(mMainView.getOSDContainer(), this_);

        //是否付费
        if (DeviceConfigManager.getInstance().isSupportCharge()) {
            //  没有付费开始鉴权
            initChargeAuth();
            setChargeOrderBtnClickListener();
        } else {
            if (mMainView != null) {
                mMainView.getStatusBar().hideChargePayBtn();
            }
        }
    }

    private void initCharge() {
        if (!DeviceConfigManager.getInstance().isSupportCharge()) {
            return;
        }
        ChargeProxy.getInstance().setChargeDevice(new DeviceCharge());
        ChargeProxy.getInstance().setInitListener(new IChargeInitListener() {

            @Override
            public void onChargeInitSuccess() {
            }

            @Override
            public void onChargeInitFailed(final int errorCode) {
                EvLog.e("init failed");
                BasePresenter.runInUI(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showLongToast(MainActivity.this, "计费初始化失败:" + errorCode);
                    }
                });
            }
        });
        ChargeProxy.getInstance().init(MainActivity.this);
    }

    private float startX, startY, offsetX, offsetY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EvLog.i("MainActivity__onCreate");
        super.onCreate(savedInstanceState);

        mContext = this.getBaseContext();

        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                event.getAction();
//                MotionEvent.ACTION_DOWN
//                MotionEvent.ACTION_MOVE
//                event.getX();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("gsp", "按下");

                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e("gsp", "抬起");

                        offsetX = event.getX() - startX;
                        offsetY = event.getY() - startY;
                        if (Math.abs(offsetX) > Math.abs(offsetY)) {
                            Log.e("gsp", "");
                            if (offsetX < -5) { // left

                            } else if (offsetX > 5) { // right

                                Log.i("gsp", "onTouch:888888888888888 已开始右滑动了   ");

                                if (MainViewManager.getInstance().isMainViewVisible()) {
                                    operateExitView();
                                } else {
                                    //mv中的退出操作
                                    operateExitFromMv();
                                }
                            }
                        } else {
                            if (offsetY < -5) { // up

                            } else if (offsetY > 5) { // down

                            }
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:

                        int x = (int) event.getRawX();
                        int y = (int) event.getRawY();
                        Log.e("gsp", "移动" + x);
                        break;
                }
                return true;

            }
        });


        BaseApplication.getInstance().addActivity(this);
        // TODO 初始化移动框架
//        initSysInfo();
        //发起登录请求
        DataCenterCommu.getInstance().setLoginResultListener(new IDCLoginResultListener() {

            @Override
            public void onDCLoginSuccess() {
                BasePresenter.runInUI(new Runnable() {

                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onDCLoginFailed() {
                BasePresenter.runInUI(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showLongToast(mContext, "服务器登录失败");
                    }
                });
            }
        }, loginAcount, epgProvince, stdid, terminalType, epgToken);

        if (DeviceConfigManager.getInstance().isSupportCharge()) {
            initCharge(); // 初始化计费
            mIsAuthedOnCreate = !TextUtils.isEmpty(ChargeProxy.getInstance().getTradeNo());
        }

        startSaveLog();
        // init database first
        DatabaseManager.init(KmApplication.getInstance().getContext());

        StorageManager.getInstance().initStorageVolume(this);

        checkIsDebugVersion();

        initView();

        int jumpType = ApkJumpParamParser.getInstance().getJumpType();
        if (jumpType != ApkJumpParamParser.JUMP_TYPE_NONE) {
            if (jumpType == ApkJumpParamParser.JUMP_TYPE_RANK || jumpType == ApkJumpParamParser.JUMP_TYPE_SONG_MENU) {
                showMainUi();
            }
            ApkJumpHandler.handleJump(jumpType, ApkJumpParamParser.getInstance().getJumpParam(), this, this);
        } else {
            showMainUi();
        }
        // TODO 咪咕计费初始化
        MiguSdk.initializeApp(this);
    }

    private void initSysInfo() {
        GetSysInfo instance = GetSysInfo.getInstance("10086", "", this);

        // 获取终端标识
        String firmwareVersion = instance.getFirmwareVersion();
        // 获取盒子32位序列号
        stdid = instance.getSnNum();
        //获取盒子型号
        terminalType = instance.getTerminalType();
        // 盒子固件版本号
        String hardwareVersion = instance.getHardwareVersion();
        // 是否登录
        boolean epgLogined = instance.isEpgLogined();
        // 获取盒子登录后 token
        epgToken = instance.getEpgToken();
        String Epguserid = instance.getEpgUserId();
        //获取配对设备（手机或者 pad）的用户 id
        String epgMobileUserId = instance.getEpgMobileUserId();
        // 获取牌照方 EPG 首页地址
        String epgIndexUrl = instance.getEpgIndexUrl();
        //获取配对设备（手机或者 pad）的登录后 token
        String epgMobileToken = instance.getEpgMobileToken();
        //获取首门户地址
        String epgCmccHomeUrl = instance.getEpgCmccHomeUrl();
        //获取 Eccode
        String epgEccode = instance.getEpgEccode();
        //获取 CopyrightId
        epgCopyrightId = instance.getEpgCopyrightId();
        //获取 EccoporationCode
        String epgEccoporationCode = instance.getEpgEccoporationCode();
        //获取机顶盒省份编码
        epgProvince = instance.getEpgProvince();
        //获取机顶盒地市编码
        epgCityCode = instance.getEpgCityCode();
        //获取牌照方对应 cp 标识
        String epgCpCode = instance.getEpgCpCode();
        //获取用户组标识
        epgUserGroup = instance.getEpgUserGroup();
        //获取 EPG 附加参数
        String epgBusinessParam = instance.getEpgBusinessParam();
        //获取广电分配的 tvid
        String tvid = instance.getTVID();
        //获取 15 位序列号
        String deviceId = instance.getDeviceId();
        //获取服务框架版本
        String firmwareVersion1 = instance.getFirmwareVersion();
        //获取升级策略方式
        String upgradeMode = instance.getUpgradeMode();
        //获取统一播放器版本号信息
        String playerVersion = instance.getPlayerVersion();
        instance.getEpgMobileUserId();
        String epgMobileDeviceId = instance.getEpgMobileDeviceId();


        Log.i(TAG, "initSysInfo: +" + "是否登录" + epgLogined + "获取登录用户名" + epgToken + "获取配对设备（手机或者 pad）的用户" +
                "epgMobileDeviceId" + epgMobileDeviceId + "Epguserid" + Epguserid + epgMobileToken + "epgCopyrightId= " + epgCopyrightId + "epgEccoporationCode= " + epgEccoporationCode + "epgProvince " + epgProvince + "upgradeMode = " + upgradeMode + "firmwareVersion1 = " + firmwareVersion1 +
                "deviceId =" + deviceId + "epgBusinessParam=  " + epgBusinessParam + "epgUserGroup" + epgUserGroup + "epgCityCode" + epgCityCode + "epgEccode" + epgEccode + epgMobileUserId + "EPG 首页地址"
                + epgIndexUrl + "获取广电分配的 tvid = " + tvid + "获取首门户地址 = " + epgCmccHomeUrl
                + "firmwareVersion = " + firmwareVersion + "terminalType = " + terminalType + "hardwareVersion = " + hardwareVersion + "获取牌照方对应 cp 标识 = " + epgCpCode + "获取统一播放器版本号信息 = " + playerVersion);

        // 需要设置相应的系统权限还有 签名要拿到
//        SetSysInfo setInstance = SetSysInfo.getInstance(this, "10086", "");
//        Log.i(TAG, "initSysInfo: 是否为空"+setInstance);
//        setInstance.setEpgUserId("哈哈哈哈哈哈哈 我可以自己设置");

        //服务框架的认证信息
        UserAccountInfo userAccountInfo = UserAccountInfo.getInstance(this);
        // 获取认证地址
        String loginUrl = userAccountInfo.getLoginUrl();
        //获取认证用户名
        loginAcount = userAccountInfo.getLoginAcount();
        //获取认证口令
        loginPassword = userAccountInfo.getLoginPassword();
        Log.i(TAG, "initSysInfo:获取认证地址 " + loginUrl + "获取认证用户名" + loginAcount + "获取认证口令" + loginPassword);
    }

    private void checkIsDebugVersion() {
        ActivityInfo info;
        try {
            info = this.getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            if (info == null) {
                return;
            }
            boolean ret = info.metaData.getBoolean("RELEASE");
            if (ret) {
                EvLog.d(" is release version ");
            } else {
                EvLog.d(" is debug version ");
                IDeviceConfig config = DeviceConfigManager.getInstance().getDevice();
                config.mSupportCharge = false;
                EvLog.i("isSupportCharge:" + DeviceConfigManager.getInstance().isSupportCharge());
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showMainUi() {
        setContentView(mMainView.view());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏

        //初始化MV界面播放控件
        mMVCtrlWidget.init(this_);
        mMVCtrlWidget.setMVKeyListener(MainActivity.this);
//        mMVCtrlWidget.setPlayBackCtrlListener(MainActivity.this);
        mMVCtrlWidget.setPlayCtrlWidgetListener(MainActivity.this);

        UpdateDCCommu.init();
        UpdateResourceManager.setHomePictureUpdateListener(MainActivity.this);
//        UpdateResourceManager.setUpdateHomeRightBottomIcon(MainActivity.this);
        UpdateResourceManager.init();

        BackgroundUpdateManager.getInstance().start(true);
        handleStart();
    }

    private void selectDecoder() {
        boolean firstUse = KmSharedPreferences.getInstance().getBoolean(KeyName.KEY_FIRST_USE_APP, true);
        if (firstUse) {
            int assginPlayId = DeviceConfigManager.getInstance().getAssignPlayerId();
            String text = "";
            String title = getResources().getString(R.string.select_decode_title);
            if (assginPlayId != KmVideoPlayerType.MEDIAPLAYER) {
                text += getResources().getString(R.string.select_decode_use_ijk);
            } else {
                text += getResources().getString(R.string.select_decode_use_org);
            }
            text += getResources().getString(R.string.select_decode_hint);
            text += getResources().getString(R.string.select_decode_happy);

            SelectDecodeDialog selectDecoderDialog = new SelectDecodeDialog(this_);
            selectDecoderDialog.setTitle(title);
            selectDecoderDialog.setContent(text);
            selectDecoderDialog.setOneOkButton(R.string.confirm, null);
            selectDecoderDialog.show();
            KmSharedPreferences.getInstance().putInt(KeyName.KEY_USE_VLC_DECODE, assginPlayId);
            KmSharedPreferences.getInstance().putBoolean(KeyName.KEY_FIRST_USE_APP, false);
        }
    }

    private void initFreeSongList() {
        FreeSongListManager.getInstance().addGetFreeSongEventListener(new IGetFreeSongEventListener() {

            @Override
            public void onStartGetFreeSong() {
                if (mMainView != null) {
                    mMainView.updateMainViewCurrentSong(getString(R.string.get_online_free_song_list));
                }
            }

            @Override
            public void onFinishGetFreeSong() {
                if (DeviceConfigManager.getInstance().getBroadcastSongType() == IDeviceConfig.BROADCAST_SONG_TYPE_ONLINE) {
                    if (KmPlayerCtrl.getInstance().getPlayingSong() == null) {
                        int state = KmPlayerCtrl.getInstance().getPlayerState();
                        if (state == PlayerCtrlState.STATE_IDLE && PlayListManager.getInstance().getCount() == 0) {
                            EvLog.i("onFinishGetFreeSong----------");
                            PlayCtrlHandler.getInstance().sendEmptyMessageDelayed(PlayCtrlHandler.PLAY_CTRL_MSG_NEXT_SONG, 0);
                        }
                    }
                }
            }

            @Override
            public void onErrorGetFreeSong() {
                ToastUtil.showLongToast(MainActivity.this, getResources().getString(R.string.get_online_free_song_failed));
            }

            @Override
            public void onUpdateFreeSong() {
                BroadcastListManager.getInstance().setUpdateSong();
            }
        });

        FreeSongListManager.getInstance().init();
    }

    private void initPlayCtrlMessage() {
        PlayCtrlHandler.getInstance().setListener(new IPlayCtrlEventListener() {

            @Override
            public void onEvent(int eventId, Message msg) {
                switch (eventId) {
                    case PlayCtrlHandler.EVENT_PALY_PAUSE:
                        HandlePlayAndPause();
                        break;
                    case PlayCtrlHandler.EVENT_SWITCH_TRACK:
                        switchTrack();
                        break;
                    case PlayCtrlHandler.EVENT_PLAY_NEXT_SONG:
                        playNextSong();
                        break;
                    case PlayCtrlHandler.EVENT_CUT_SONG:
                        onClickCutSong();
                        break;
                    case PlayCtrlHandler.EVENT_REPLAY:
                        replaySong();
                        break;
//                    case PlayCtrlHandler.EVENT_GRADE_CHANGE:
//                        handleGradeModeChange();
//                        break;
                    case PlayCtrlHandler.EVENT_UPDATE_PLAYBACK_TIME:
                        updatePlayBackPlayTime();
                        break;
                    case PlayCtrlHandler.EVENT_VOL_UP:
                        mMVCtrlWidget.showVolWidgetByEvent(this_, 0, true);
                        break;
                    case PlayCtrlHandler.EVENT_VOL_DOWN:
                        mMVCtrlWidget.showVolWidgetByEvent(this_, 0, false);
                        break;
                    case PlayCtrlHandler.EVENT_VOL_MUTE:
                        handleMuteEvent();
                        break;
                    case PlayCtrlHandler.EVENT_MIC_UP:
                        /*if (DeviceConfigManager.getInstance().isThirdApp())*/
                    {
                        ToastUtil.showLongToast(this_, getResources().getString(R.string.not_support_adjust_mic_vol));
                    } /*else {
                        mMVCtrlWidget.showVolWidgetByEvent(1,true);
                    }*/
                    break;
                    case PlayCtrlHandler.EVENT_MIC_DOWN:
                        /*if (DeviceConfigManager.getInstance().isThirdApp())*/
                    {
                        ToastUtil.showLongToast(this_, getResources().getString(R.string.not_support_adjust_mic_vol));
                    } /*else {
                        mMVCtrlWidget.showVolWidgetByEvent(1,false);
                    }*/
                    break;
                    case PlayCtrlHandler.EVENT_WAKE_RESUME:
                        KmSongDownManager.getInstance().resumeDown();
                        if (mPlayerStatusBeforeScreenOff == PlayerCtrlState.STATE_PLAY) {
                            EvLog.d("  player is in pause state, so resume player");
                            KmPlayerCtrl.getInstance().play();
                        }
                        break;
                }
            }
        });
    }

    public void showLoadingView(String content) {
        if (mMainView != null) {
            mMainView.showLoadingView(content);
        }
    }

    public void showLoadingView(int resid) {
        if (mMainView != null) {
            mMainView.showLoadingView(resid);
        }
    }

    public void dismissLoadingView() {
        if (mMainView != null) {
            mMainView.dismissLoadingView();
        }
    }

    private void initLocalHttpServer() {
        HttpdServer.getInstance().start();
    }

    /**
     * @brief : [注册观察者]
     */
    private void registObserver() {
        // BootInfoSubject.getInstance().registBootInfoObserver(this);
        NetworkInfoSubject.getInstance().registNetworkInfoObserver(this);
        WifiInfoSubject.getInstance().registWifiInfoObserver(this);
        ScreenInfoSubject.getInstance().registerScreenInfoObserver(this);
        ScreenInfoSubject.getInstance().start(this_);
        //ExStorageSubject.getInstance().registExStorageObserver(this);
        PlayListManager.getInstance().registerSongObserver();
        SungListManager.getInstance().registerSongObserver();
        FavoriteListManager.getInstance().registerSongObserver();
    }

    /**
     * @brief : [注销观察者]
     */
    private void unregistObserver() {
        // BootInfoSubject.getInstance().unregistBootInfoObserver(this);
        NetworkInfoSubject.getInstance().unregistNetworkInfoObserver(this);
        WifiInfoSubject.getInstance().unregistWifiInfoObserver(this);
        ScreenInfoSubject.getInstance().stop();
        ScreenInfoSubject.getInstance().unregisterScreenInfoObserver(this);
        //ExStorageSubject.getInstance().unregistExStorageObserver(this);
        PlayListManager.getInstance().unregisterSongObserver();
        SungListManager.getInstance().unregisterSongObserver();
        FavoriteListManager.getInstance().unregisterSongObserver();
    }

    private void initMainView() {
//        long start = System.currentTimeMillis();
        if (mMainView == null) {
            mMainView = new KmMainView(mContext);
        }
//        EvLog.e("initMainView eclipse:" + (System.currentTimeMillis()-start));
//        mMainView.setVideoViewEventListener(this);
        mMainView.setBottomMVClickListener(this);
        mMainView.setStatusBarKeyListener(new IStatusBarKeyListener() {

            @Override
            public boolean onKey(int btnType, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                return MainViewManager.getInstance().handleStatusBarKeyEvent(btnType, keyCode, event);
            }
        });

        mMainView.setSmallMVKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                return MainViewManager.getInstance().handleSmallMVKeyEvent(arg1, arg2);
            }
        });

        if (DeviceConfigManager.getInstance().isThirdApp()) {
            mMainView.showVideoView();
        }

        initMvSelectedView();
        initMainUi();

        initOrderSongAnimView();
    }

    private void initMvSelectedView() {
        if (mMainView != null) {
            mMvSelectedView = new MvSelectedView(this_);
            if (mMvSelectedView != null) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
                lp.gravity = Gravity.RIGHT;
                mMainView.view().addView(mMvSelectedView, lp);
            }
        }
//        EvLog.e("initMvSelectedView eclipse:" + (System.currentTimeMillis()-start));
    }

    private void initMainUi() {
        long start = System.currentTimeMillis();
        if (mMainView != null) {
            MainViewManager.getInstance().init(this_, mMainView);
        }
        EvLog.e("initMainUi time:" + (System.currentTimeMillis() - start));
    }

    private void initOrderSongAnimView() {
        if (mMainView != null) {
            mMainView.view().addView(OrderSongAnimController.getInstance().init(this_));
        }
    }

    private void handleMuteEvent() {
        EvLog.i("receive mute message!");
        if (KmPlayerCtrl.getInstance().isMute()) {
            KmPlayerCtrl.getInstance().unmute();
            KmOSDMessageManager.getInstance().sendKMMessageDrawView(new KmOSDMessage(KmOSDMessage.KM_OSDTV_TYPE_UNMUTE));
        } else {
            KmPlayerCtrl.getInstance().mute();
            KmOSDMessageManager.getInstance().sendKMMessageDrawView(new KmOSDMessage(KmOSDMessage.KM_OSDTV_TYPE_MUTE));
        }
    }

    public void showPlayCtrlView() {

        if (mMVCtrlWidget.isPlayCtrlWidgetShowing()) {
            log("showPlayCtrlView()---false");
            return;
        } else {
            log("showPlayCtrlView()---true");
        }

        boolean gradeOpen = false;
        boolean showGradeBtn = false;
        KmPlayListItem currentSong = KmPlayerCtrl.getInstance().getPlayingSong();
        if (currentSong == null) {
            EvLog.d("current song is null, not show PlayCtrlView");
            ToastUtil.showLongToast(this_, "当前没有歌曲播放哦");
            return;
        }

        if (showGradeBtn) {
            mMVCtrlWidget.showPlayWidgetWithGradeBtn(gradeOpen);
        } else {
            mMVCtrlWidget.showPlayWidgetWithoutGradeBtn();
        }
    }

    /**
     * 在mv中退出操作
     */
    public void operateExitFromMv() {
        if (mMvSelectedView != null && mMvSelectedView.getVisibility() == View.VISIBLE/*MvSelectedManager.getInstance().isVisible()*/) {
            /*MvSelectedManager.getInstance().hideMvSelectedView();*/
            mMvSelectedView.hide();
        } else {
            MainViewManager.getInstance().switchMainView();
        }
    }

    /*
    正常的退出view
     */
    public void operateExitView() {
        EvLog.e("isMainViewVisible notifyBackPressed");
        ActivitySubject.getInstance().notifyBackPressed();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        /*if(event.getAction() == KeyEvent.ACTION_DOWN) {
            ComboKeyWatcher.getInstance().checkKeyCode(event.getKeyCode());
        }*/
        return super.dispatchKeyEvent(event);
    }

    public void onClickCutSong() {
       /* int mode = KmPlayerCtrl.getInstance().getMode();
        if (mode == PlayMode.MODE_PLAYBACK) {
            dismissLoadingView();
            PlayCtrlHandler.getInstance().removeMessages(PlayCtrlHandler.MSG_UPDATE_PLAY_TIME);
            updatePlayBackPlayTime();
            mMainView.hidePlayBackHint();
        }*/
        int state = KmPlayerCtrl.getInstance().getPlayerState();
        if (state == PlayerCtrlState.STATE_PREPARING) {
            ToastUtil.showLongToast(this, getResources().getString(R.string.cutsong_too_quick));
            return;
        }

        KmPlayListItem item = KmPlayerCtrl.getInstance().getPlayingSong();
        adjustPlayList();
        if (item != null && item.getSongCategory() == SongCategory.CATEGORY_PLAYLIST) {
            item.setSerialNum(-1);
        }

        if (KmPlayerCtrl.getInstance().isPlayerDecoding()) {
            EvLog.d("cutsong: is decoding, need to stop" + KmPlayerCtrl.getInstance().getPlayerState());
            KmPlayerCtrl.getInstance().stop();
        } else {
            EvLog.d("cutsong: is not decoding, direct send message");
            handlePlayStopEvent(false);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mIsShowBootPicture) {
            return true;
        }
        EvLog.d("keyCode = " + keyCode);
//        ActivityWebView view = MainViewManager.getInstance().getActivityWebView();
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                if (DeviceConfigManager.getInstance().isThirdApp()) {
                    exitApp();
                    return true;
                }

                if (MainViewManager.getInstance().isMainViewVisible()) {
                    MainViewManager.getInstance().backToHome();
                } else {
                    onMVHomeKeyEvent();
                }
                return true;
            case IDeviceConfig.KEYEVENT_SWITCH_MV:
                if (MainViewManager.getInstance().isMainViewVisible()) {
                    MainViewManager.getInstance().switchMainView();
                } else {
                    onMVMenuKeyEvent();
                }
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_PLAY_PAUSE);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                return false;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return false;
            case KeyEvent.KEYCODE_F1:
                onMVPlayPauseKeyEvent();
                return true;
           /* case 164:
                ThirdPartyApps.getInstance().startDefaultSettings();
                return true;*/
            case KeyEvent.KEYCODE_BACK:
                if (MainViewManager.getInstance().isMainViewVisible()) {
                    EvLog.e("isMainViewVisible notifyBackPressed");
                    ActivitySubject.getInstance().notifyBackPressed();
                } else {
                   /* if (mMainView.isRemoteControlShow()) {
                        mMainView.hideRemoteControlGuide();
                        KmSharedPreferences.getInstance().putBoolean(KeyName.KEY_SHOW_REMOTE_CONTROL_GUIDE, false);
                        return true;
                    }*/

                    if (mMvSelectedView != null && mMvSelectedView.getVisibility() == View.VISIBLE/*MvSelectedManager.getInstance().isVisible()*/) {
                        /*MvSelectedManager.getInstance().hideMvSelectedView();*/
                        mMvSelectedView.hide();
                    } else {
                        MainViewManager.getInstance().switchMainView();
                    }
                }
                return true;
            default:
                break;
        }

        if (MainViewManager.getInstance().isMainViewVisible()) {
            return super.onKeyDown(keyCode, event);
        } else {
            //mv界面中已点列表可见的情况下
            if (/*MvSelectedManager.getInstance().isVisible()*/mMvSelectedView != null && mMvSelectedView.getVisibility() == View.VISIBLE) {
                /*if (DeviceInfoUtils.isBesTvLittleRed() && keyCode == KeyEvent.KEYCODE_DEL) {
                    MvSelectedManager.getInstance().hideMvSelectedView();
                    return true;
                }*/
                return super.onKeyDown(keyCode, event);
            } else {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    if (mMvSelectedView != null) {
                        mMvSelectedView.show();
                    }
//                    MvSelectedManager.getInstance().showMvSelectedView();
                    return true;
                }
            }
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
//                EvLog.e("recv enter keycode-----isRemoteControlShow:" + mMainView.isRemoteControlShow());
                /*if (mMainView.isRemoteControlShow()) {
                    mMainView.hideRemoteControlGuide();
                    KmSharedPreferences.getInstance().putBoolean(KeyName.KEY_SHOW_REMOTE_CONTROL_GUIDE, false);
                    return true;
                }*/

                /*if (KmPlayerCtrl.getInstance().getMode() == PlayMode.MODE_PLAYBACK) {
                    mMVCtrlWidget.showPlayBackWidget();
                } else*/
            {
                showPlayCtrlView();
            }
            return true;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return super.onKeyDown(keyCode, event);
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_F1:
//                EvLog.i("event.getRepeatCount():" + event.getRepeatCount());
                //避免长按事件
                if (event.getRepeatCount() == 0) {
                    if ((System.currentTimeMillis() - this.mSwitchSongTime) > SystemConfigManager.SWITCH_SONG_EFFECT_TIME) {
                        ToastUtil.showToast(this, R.string.toast_switch_song_text);
                        mSwitchSongTime = System.currentTimeMillis();
                    } else {
                        ToastUtil.dismissToast();
                        if (NetUtils.isNetworkConnected(mContext)) {
                            onClickCutSong();
                            UmengAgentUtil.onEventCutSong(this_, keyCode);
                        } else {
                            ToastUtil.showToast(this, R.string.toast_network_error);
                        }
                        this.mSwitchSongTime = 0;
                    }
                }
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private IDatabaseSyncListener mDBDownListener = new IDatabaseSyncListener() {
        private int mMessageId = -1;
        private String mainViewDbUpdateTx = "";

        @Override
        public void onStart() {
            EvLog.i("dao updater", "start updater dao ------");

            BasePresenter.runInUI(new Runnable() {
                @Override
                public void run() {
                    StatusBarMsg msg = new StatusBarMsg();
                    msg.content = getString(R.string.songbook_update_db_from_datacenter, "0");
                    msg.iconResId = R.drawable.ic_status_bar_msg;
                    mMessageId = mMainView.getStatusBar().addDatabaseMsg(msg);
                    mMainView.getStatusBar().showDbUpdateTv();
                }
            });
        }

        @Override
        public void onFinish(boolean result) {
            EvLog.i("dao updater", ">>>>>>>>>>>>>>>>>>>> finish " + result);

            BasePresenter.runInUI(new Runnable() {
                @Override
                public void run() {
                    if (mMessageId >= 0) {
                        mMainView.getStatusBar().removeStatusBarMsg(mMessageId);
                    }
                    mMainView.getStatusBar().updateDbUpdateTv("");
                    mMainView.getStatusBar().hideDbUpdateTv();
                }
            });
            /*if (DeviceConfigManager.getInstance().getBroadcastSongType() == IDeviceConfig.BROADCAST_SONG_TYPE_ONLINE) {
                if (KmPlayerCtrl.getInstance().getPlayingSong() == null) {
                    int state = KmPlayerCtrl.getInstance().getPlayerState();
                    if (state == PlayerCtrlState.STATE_IDLE && PlayListManager.getInstance().getCount() == 0) {
                        PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_NEXT_SONG);
                    }
                }
            }*/
        }

        @Override
        public void onProgress(float progress) {
            final int showProgress = (int) (progress * 100);
            BasePresenter.runInUI(new Runnable() {
                @Override
                public void run() {
                    if (mMessageId >= 0) {
                        StatusBarMsg msg = new StatusBarMsg();
                        msg.iconResId = R.drawable.ic_status_bar_msg;

                        if (showProgress >= 100) {
                            msg.content = getString(R.string.songbook_update_db_from_datacenter_successed);
                            mainViewDbUpdateTx = "";
                        } else {
                            msg.content = getString(R.string.songbook_update_db_from_datacenter, String.valueOf(showProgress));
                            mainViewDbUpdateTx = getString(R.string.songbook_update_db_from_datacenter_mainview, String.valueOf(showProgress));
                        }

                        mMainView.getStatusBar().updateStatusBarMsg(mMessageId, msg);
                        mMainView.getStatusBar().updateDbUpdateTv(mainViewDbUpdateTx);
                    }
                }
            });
        }

        @Override
        public void onError(String message) {
            EvLog.w("dao updater", "error:" + message);
            final String errMessage = message;
            BasePresenter.runInUI(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showLongToast(this_, getString(R.string.songbook_update_db_from_datacenter_failed, errMessage));
                }
            });
        }
    };

    private void initDatabaseSynchronizer() {
        DatabaseSynchronizer.getInstance().setListener(mDBDownListener);
        DatabaseSynchronizer.getInstance().asyncUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void handleActivityResumeEvent() {
        EvLog.d(" videoview create");
        if (!KmPlayerCtrl.getInstance().isPlayerValid()) {
            EvLog.e("Player is inValid, createPlayer");
            KmPlayerCtrl.getInstance().createPlayer();
        }
        if (mIsActivityOnStopExecute) {
            if (mPauseSongItem != null) {
                resetMainView();
                mMVCtrlWidget.hide();
                delayHideAnnounce(0);

                EvLog.i("activity resume, resume play song:" + mPauseSongItem.getSongName());
                KmPlayerCtrl.getInstance().playNextSong(
                        new KmPlayListItem(mPauseSongItem));
                // 判断报幕是否显示评分标志
                boolean showGradeFlag = false;
                delayShowAnnnounce(showGradeFlag, DELAY_SHOW_ANNOUNCE);
                mPauseSongItem = null;
            } else {
                EvLog.i("activity resume, send play next song");
                PlayCtrlHandler.getInstance().sendEmptyMessageDelayed(PlayCtrlHandler.PLAY_CTRL_MSG_REPLAY_SONG, 2000);
            }
            mIsActivityOnStopExecute = false;
        }
    }

    @Override
    protected void onResume() {
        EvLog.d("MainActivity onResume -------------");
        //判断是否需要记录第三方应用的停留时间
        if (mIsActivityOnStopExecute) {
            EvLog.d("mIsActivityOnStopExecute is :" + mIsActivityOnStopExecute);
//            EvLog.i("isTopActivity:" + BaseApplication.getInstance().isTopActivity(SystemConfigManager.KMBOX_ACTIVITY));
            EvLog.d("isTopActivity", BaseApplication.getInstance().getTopClassName());
            if (BaseApplication.getInstance().isTopActivity(SystemConfigManager.KMBOX_ACTIVITY) ||
                    BaseApplication.getInstance().isTopActivity(SystemConfigManager.PAY_ACTIVITY) ||
                    BaseApplication.getInstance().isTopActivity(SystemConfigManager.ALI_PAY_SDK_ACTIVITY_STRING)) {
                KmSongDownManager.getInstance().resumeDown();
                handleActivityResumeEvent();
                if (ChargeProxy.getInstance().getCharge() != null) {
                    ChargeProxy.getInstance().getCharge().cancelCheckPayResult();
                }
            }
        }

        super.onResume();

        // 停止第三方音乐播放
        pauseMusicSericeCommand();

        LogAnalyzeManager.getInstance().onPageStart(PageName.MAIN_ACTIVITY);
        LogAnalyzeManager.onResume(this);

        KeyEventSubject.getInstance().registerKeyEventObserver(this);
        ThirdAppKeyEventManager.getInstance().registerKeyReceiver(this);
        ThirdAppKeyEventManager.getInstance().setHomeKeyListener(this);
        mainActivity = this;
    }


    @Override
    protected void onPause() {
        EvLog.i("MainActivity", " onPause -------------");

        if (!BaseApplication.getInstance().isTopActivity(SystemConfigManager.KMBOX_ACTIVITY)) {
            EvLog.e("begin to enter :" + BaseApplication.getInstance().getTopPackageName());
        }
        super.onPause();
        LogAnalyzeManager.getInstance().onPageEnd(PageName.MAIN_ACTIVITY);
        LogAnalyzeManager.onPause(this);
        KeyEventSubject.getInstance().unregisterKeyEventObserver(this);
    }

    @Override
    protected void onStop() {
        EvLog.i("MainActivity onStop ------------------------");

        mIsActivityOnStopExecute = true;
        mPlayerStatusBeforeScreenOff = KmPlayerCtrl.getInstance().getPlayerState();

        DatabaseSynchronizer.getInstance().stop();
        SongSubject.getInstance().stop();
        handleActivityStopEvent();

        super.onStop();
    }

    private void handleActivityStopEvent() {
        KmPlayListItem currentSong = KmPlayerCtrl.getInstance().getPlayingSong();
        if (currentSong != null) {
            if (mPauseSongItem == null) {
                mPauseSongItem = new KmPlayListItem(currentSong);
            } else {
                mPauseSongItem.copy(currentSong);
            }
        }
        KmPlayerCtrl.getInstance().releasePlayer();
        KmSongDownManager.getInstance().pauseDown();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        EvLog.e("onConfigurationChanged() Called");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();

        EvLog.i("onStart ----------------------");
    }

    private void initList() {
        PlayListManager.getInstance().registerListener(MainActivity.this);
        PlayListManager.getInstance().init();

        FavoriteListManager.getInstance().init();

//        PlayBackListManager.getInstance().registerListener(MainActivity.this);
        initPlayCtrlMessage();

        SungListManager.getInstance().init();
        initFreeSongList();
        BroadcastListManager.getInstance().setBroadcastScannerListener(this);
    }

    private void handleStart() {
        MembersInfoList.getInstance().InitRes(getApplicationContext());
        registObserver();

        WholeDAOHelper.init(this_);

        initLocalHttpServer();
        initPlayerCtrl();

        initList();

        initDatabaseSynchronizer();

        // 停止第三方音乐播放
        pauseMusicSericeCommand();
        initGradeAndSingFromSetting();

        if (DeviceConfigManager.getInstance().getBroadcastSongType() == IDeviceConfig.BROADCAST_SONG_TYPE_LOCAL) {
            if (KmPlayerCtrl.getInstance().getPlayingSong() == null) {
                int state = KmPlayerCtrl.getInstance().getPlayerState();
                if (state == PlayerCtrlState.STATE_IDLE && PlayListManager.getInstance().getCount() == 0) {
                    PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_NEXT_SONG);
                }
            }
        }

        DatabaseSynchronizer.getInstance().start();
        SongSubject.getInstance().start();
    }

    private void pauseMusicSericeCommand() {
    	/*Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        sendBroadcast(i);*/
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            EvLog.e("pauseMusicSericeCommand:result=" + result);
        }
    }

    private void initGradeAndSingFromSetting() {
        boolean singVal = KmSharedPreferences.getInstance().getBoolean(KeyName.KEY_SETTING_GLOBAL_ORIGINAL_SING, true);
        int mode = (singVal) ? (KmAudioTrackMode.MODE_ORI) : (KmAudioTrackMode.MODE_ACC);
        EvLog.d(" sing mode read from setting " + mode);
        KmPlayerCtrl.getInstance().setAudioSingMode(mode);
    }

    private final static int MAX_CONTINUES_CUT_SONG_MAX_NUM = 4;

    private boolean handleContinusCutSong() {
        mContinuousCutSongCount++;
        if (mContinuousCutSongCount >= MAX_CONTINUES_CUT_SONG_MAX_NUM) {
            String text = getResources().getString(R.string.continus_cutsong);
            final SelectDecodeDialog selectDecoderDialog = new SelectDecodeDialog(this_);
            selectDecoderDialog.setTitle("");
            selectDecoderDialog.setContent(text);
            selectDecoderDialog.setOneOkButton(R.string.confirm, null);
            selectDecoderDialog.show();
            return true;
        }
        return false;
    }

    private void handlePlayAutoStopEvent() {
        CacheManagerUtil.getInstance().unlockCurrentPlayingSong();
        KmPlayListItem currentSong = KmPlayerCtrl.getInstance().getPlayingSong();
        if (currentSong == null) {
            PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_NEXT_SONG);
            return;
        }

        if (KmPlayerCtrl.getInstance().getWorkMode() == PlayMode.MODE_PLAYBACK) {
            dismissLoadingView();
            updatePlayBackPlayTime();
            PlayCtrlHandler.getInstance().removeMessages(PlayCtrlHandler.MSG_UPDATE_PLAY_TIME);

            if (!MainViewManager.getInstance().isMainViewVisible()) {
                mMVCtrlWidget.showPlayBackWidget();
            } else {
                PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_NEXT_SONG);
            }
            return;
        }

        handleMediaFileWhenPlayStop(currentSong);

        String shareCode = ""/*RecordShareUtil.getUUID()*/;
        int playTime = KmPlayerCtrl.getInstance().getPlayedTime() / 1000;//ms to s
        EvLog.d("handlePlayAutoStopEvent, playTime=" + playTime);
        if ((playTime + 30) < currentSong.getDuration()) {
            EvLog.e("prev cut song");
            String errorMsg = "";
            if (!TextUtils.isEmpty(Build.DEVICE)) {
                errorMsg = Build.DEVICE;
            }
            errorMsg += ": prev cut song,playTime:" + playTime + ",duration:" + currentSong.getDuration();
            UmengAgentUtil.reportError(errorMsg);
            if (handleContinusCutSong()) {
                return;
            }
        } else {
            mContinuousCutSongCount = 0;
        }

        if (currentSong.getSongCategory() == SongCategory.CATEGORY_PLAYLIST) {
            addToSungList(currentSong, shareCode, playTime);
        }
        adjustPlayList();
        PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_NEXT_SONG);
    }

    private void handlePlayStopEvent(boolean reallyDecoded) {
        KmPlayListItem currentSong = KmPlayerCtrl.getInstance().getPlayingSong();
        if (currentSong == null) {
            PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_NEXT_SONG);
            return;
        }

        String shareCode = "";
        if (reallyDecoded) {
            CacheManagerUtil.getInstance().unlockCurrentPlayingSong();
            KmOSDMessageManager.getInstance().hideKmMessageDrawView(new KmOSDMessage(KmOSDMessage.KM_OSDTV_TYPE_PAUSE));
            int songCategory = currentSong.getSongCategory();
            if (songCategory == SongCategory.CATEGORY_PLAYLIST) {
                int playTime = KmPlayerCtrl.getInstance().getPlayedTime() / 1000;//ms to s
                EvLog.d("handlePlayStopEvent:getPlayedTime=" + playTime);
                shareCode = getUUID();
                addToSungList(currentSong, shareCode, playTime);
            }
        }

        handleMediaFileWhenPlayStop(currentSong);

        if (reallyDecoded) {
            int mode = KmPlayerCtrl.getInstance().getWorkMode();

            if (mode == PlayMode.MODE_REPLAY) {
                EvLog.d("replay mode,not auto play next song");
                return;
            }
        }

        PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_NEXT_SONG);
    }

    private static final String KM_RECORD_UUID_HEADER = "a1";

    private String getUUID() {
        UUID uuid = UUID.randomUUID();
        String strUUID = uuid.toString();
        return KM_RECORD_UUID_HEADER + strUUID.substring(2, 8) + strUUID.substring(9, 13) + strUUID.substring(14, 18) + strUUID.substring(19, 23) + strUUID.substring(24, strUUID.length());
    }

    private void handleMediaFileWhenPlayStop(KmPlayListItem currentSong) {
        if (currentSong == null) {
            return;
        }

        if (DeviceConfigManager.getInstance().isNeedDelFile()) {
            int id = BroadcastListManager.getInstance().getCurrentBSongId();
            if (id == currentSong.getSongId()) {
                EvLog.d("LocalBroadcastSong ,not del video file");
            } else {
//                EvLog.d("not del video file:" + currentSong.getVideoPath());
                EvLog.d("del video file:" + currentSong.getVideoPath());
                FileUtil.deleteFile(currentSong.getVideoPath());
                currentSong.emptyMedia();
            }
        } else {
            currentSong.checkSuffix();
        }
    }

    private void adjustPlayList() {
        KmPlayListItem curSong = KmPlayerCtrl.getInstance().getPlayingSong();
        if (curSong == null) {
            EvLog.e("adjustPlayList cursong is null");
            return;
        }
        /*if (curSong.getSongCategory() == SongCategory.CATEGORY_PLAYBACK) {
            EvLog.d("playback stop, adjust playbacklist");
            if (!PlayBackListManager.getInstance().removeItemBySerialNum(curSong.getSerialNum())) {
                EvLog.e("remove list failed,playbacklist size=" + PlayBackListManager.getInstance().getCount());
                PlayBackListManager.getInstance().clear();
            }
            return;
        } else*/
        {
            /*if (PlayBackListManager.getInstance().getCount() > 0) {
                EvLog.e("going to playback mode,not adjuse playlist");
                return;
            }*/
            int pos = PlayListManager.getInstance().getPos(curSong.getSerialNum());
            if (pos == 0) {
                PlayListManager.getInstance().delSong(curSong.getSerialNum());
            }
        }
    }

    /**
     * [调整已点列表与已唱列表]
     */
    private boolean addToSungList(KmPlayListItem curSong, String shareCode, int playTime) {
       /* if (PlayListManager.getInstance().getListCount() <= 0) {
            return false;
        }

        KmPlayListItem curSong = KmPlayerCtrl.getInstance().getPlayingSong();
        if (curSong == null) {
            EvLog.e("addToSungList failed, cursong is null");
            return false;
        }*/

        /*int pos = PlayListManager.getInstance().getPosBySongId(curSong.getSongId());
        //当前歌曲是已点歌曲
        if (pos == 0) {
            PlayListManager.getInstance().getItemByPos(0).setScoreValue(curSong.getScoreValue());
            EvLog.d("add:" + curSong.getSongName() + ",to singedlist,shareCode:" + shareCode);
            Handler listDaoHandler = PlayListDAOManager.getInstance().getHandler();
            Message msg = listDaoHandler.obtainMessage(ListHandler.SUNGLIST_ADD_ITEM);
            msg.obj = curSong;
            msg.getData().putString("shareCode", shareCode);
            msg.getData().putInt("playTime", playTime);
            listDaoHandler.sendMessage(msg);
//            return SungListManager.getInstance().addItem(curSong,shareCode);
        } */
        if (curSong != null) {
            EvLog.d("add:" + curSong.getSongName() + ",to singedlist,shareCode:" + shareCode);
            Handler listDaoHandler = PlayListDAOManager.getInstance().getHandler();
            Message msg = listDaoHandler.obtainMessage(ListHandler.SUNGLIST_ADD_ITEM);
            msg.obj = curSong;
            msg.getData().putString("shareCode", shareCode);
            msg.getData().putInt("playTime", playTime);
            listDaoHandler.sendMessage(msg);
        }
        return false;
    }

    private void handlePlayErrorEvent(ErrorInfo errInfo) {
        if (!BaseApplication.getInstance().isTopActivity(SystemConfigManager.KMBOX_ACTIVITY)) {
            EvLog.e(SystemConfigManager.KMBOX_ACTIVITY + " is not on top,not handle player error msg");
            return;
        }

        CacheManagerUtil.getInstance().unlockCurrentPlayingSong();
        if (!NetUtils.isNetworkConnected(mContext)) {
            delayHideAnnounce(0);
            ToastUtil.showLongToast(mContext, getString(R.string.toast_network_error));
            MainViewManager.getInstance().updateMainViewCurrentSong(getString(R.string.toast_network_error));
            return;
        }

        if (errInfo.errorType == PlayError.ERROR_TYPE_PLAY_FAILED_BY_DECODER) {
            if (errInfo.errorCode == KmPlayerError.ERR_INIT_TRACK_INFO ||
                    errInfo.errorCode == KmPlayerError.ERR_SELECT_TRACK) {
                KmSharedPreferences.getInstance().putInt(KeyName.KEY_USE_VLC_DECODE, KmVideoPlayerType.VLC);
                SelectDecodeDialog selectDecoderDialog = new SelectDecodeDialog(this_);
                selectDecoderDialog.setTitle(getResources().getString(R.string.dialog_title_mediaplayer_error));
                selectDecoderDialog.setContent(getResources().getString(R.string.dialog_content_mediaplayer_error));
                selectDecoderDialog.setOneOkButton(R.string.confirm, null);
                selectDecoderDialog.show();
                return;
            }
        }

        String tvShowInfo = "";
        if (errInfo.errorType == DownError.ERROR_TYPE_DOWN_MEDIA_FAILED ||
                errInfo.errorType == DownError.ERROR_TYPE_GET_MEDIALIST_FAILED) {
            tvShowInfo = DownError.getTVShowMessage(this_, errInfo);
        } else if (errInfo.errorType == PlayError.ERROR_TYPE_PLAY_FAILED ||
                errInfo.errorType == PlayError.ERROR_TYPE_PLAY_FAILED_BY_DECODER) {
            tvShowInfo = PlayError.getTVShowMessage(this_, errInfo);
        }

        //FIXME
//        LogAnalyzeManager.reportError(this_, errInfo.errorMessage);

        if (errInfo.errorType == PlayError.ERROR_TYPE_PLAY_FAILED) {
            if (errInfo.errorCode == PlayError.ERROR_CODE_BROADCASTSONG_NOTEXIST_LOCAL) {
                PlayCtrlHandler.getInstance().removeMessages(PlayCtrlHandler.PLAY_CTRL_MSG_NEXT_SONG);
                PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_NEXT_SONG);
                return;
            }
        } else if (errInfo.errorType == PlayError.ERROR_TYPE_PLAY_FAILED_BY_DECODER) {
            if (errInfo.errorCode == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                EvLog.e("recv mediaserver died error,recreate player");
                /*mMainView.getVideoView().setVisibility(View.GONE);
                KmPlayerCtrl.getInstance().reCreatePlayer();
                mMainView.getVideoView().setVisibility(View.VISIBLE);*/
            }
        }

        ToastUtil.showLongToast(this_, tvShowInfo);

        //解码出错，文件后缀检查重命名
        KmPlayListItem currentSong = KmPlayerCtrl.getInstance().getPlayingSong();
        handleMediaFileWhenPlayStop(currentSong);

        adjustPlayList();

        if (KmPlayerCtrl.getInstance().getMode() == PlayMode.MODE_PLAYBACK) {
            EvLog.e("playback mode ----------");
            stopPlayBack();
        } else {
            EvLog.e("normal mode ----------");
            if (handleContinusCutSong()) {
                return;
            }
            PlayCtrlHandler.getInstance().removeMessages(PlayCtrlHandler.PLAY_CTRL_MSG_NEXT_SONG);
            PlayCtrlHandler.getInstance().sendEmptyMessageDelayed(PlayCtrlHandler.PLAY_CTRL_MSG_NEXT_SONG,
                    DELAY_SEND_CUT_SONG_WHEN_PLAY_ERROR);
        }
    }

    private boolean isNeedShowGradeResult() {
        int state = KmPlayerCtrl.getInstance().getPlayerState();
        if (state == PlayerCtrlState.STATE_AUTOSTOP) {
            EvLog.d(" grade open, let gradehandler handle cutsong msg");
            return true;
        } else {
            //可评分歌曲播放时间超过60s需要先显示评分结果
            if (KmPlayerCtrl.getInstance().getPlayedTime() >= 60 * 1000) {
                EvLog.d("wait grade result listener play next song ");
                return true;
            }
        }
        return false;
    }


    private void initPlayerCtrl() {
        KmPlayerCtrl.getInstance().registPlayStateObserver(this);
    }


    public void replaySong() {
        mMVCtrlWidget.hide();
        resetMainView();
        int state = KmPlayerCtrl.getInstance().getPlayerState();
        EvLog.d("exec replaySong in state " + state);

        if (state == PlayerCtrlState.STATE_PAUSE) {
            KmOSDMessageManager.getInstance().sendKMMessageDrawView(new KmOSDMessage(KmOSDMessage.KM_OSDTV_TYPE_HIDE_PAUSE));
        }

        //解码器处于工作状态下的重唱处理
        if (KmPlayerCtrl.getInstance().isPlayerDecoding()) {
            dismissLoadingView();
//            KmPlayerCtrl.getInstance().replay();
//            return;
        }

        updateTopSongInfo();
        KmPlayerCtrl.getInstance().replay();
        //FIXME
        delayShowAnnnounce(false, DELAY_SHOW_ANNOUNCE);
        return;
    }

    public void HandlePlayAndPause() {
        int state = KmPlayerCtrl.getInstance().getPlayerState();
        switch (state) {
            case PlayerCtrlState.STATE_PAUSE:
                EvLog.d(" pause to play ");
                KmPlayerCtrl.getInstance().play();
                break;
            case PlayerCtrlState.STATE_PLAY:
                EvLog.d(" play to pause ");
                KmPlayerCtrl.getInstance().pause();
                break;
            case PlayerCtrlState.STATE_BUFFERING:
                int beforeBuffering = KmPlayerCtrl.getInstance().getStateBeforeBuffering();
                if (beforeBuffering == PlayerCtrlState.STATE_PAUSE) {
                    KmPlayerCtrl.getInstance().setStateBeforeBuffering(PlayerCtrlState.STATE_PLAY);
                } else if (beforeBuffering == PlayerCtrlState.STATE_PLAY) {
                    KmPlayerCtrl.getInstance().setStateBeforeBuffering(PlayerCtrlState.STATE_PAUSE);
                } else {
                    EvLog.d(" bufferingstate get invalid  beforeBuffering=" + beforeBuffering);
                }
                break;
            case PlayerCtrlState.STATE_IDLE:
                KmPlayListItem curSong = KmPlayerCtrl.getInstance().getPlayingSong();
                if (curSong == null) {
                    return;
                }

                if (KmPlayerCtrl.getInstance().isLoadingData()) {
                    if (KmPlayerCtrl.getInstance().isSongCanPlayAfterPreLoad()) {
                        EvLog.d(" dataloading state , set play to pause ");
                        KmPlayerCtrl.getInstance().pause();
                    } else {
                        EvLog.d(" dataloading state , set pause to play ");
                        KmOSDMessageManager.getInstance().hideKmMessageDrawView(new KmOSDMessage(KmOSDMessage.KM_OSDTV_TYPE_PAUSE));
                        KmPlayerCtrl.getInstance().play();
                    }
                } else {//FIXME
                    EvLog.d(" dataready state , pause to play ");
                    // 显示播放图片
                    KmOSDMessageManager.getInstance().sendKMMessageDrawView(
                            new KmOSDMessage(KmOSDMessage.KM_OSDTV_TYPE_PLAY));

                    KmPlayerCtrl.getInstance().play();
                }
                break;
            default:
                EvLog.d("HandlePlayAndPause　invalid state　" + state);
                break;
        }
        return;
    }

    private void resetMainView() {
        mMainView.hidePlayBackHint();
        dismissLoadingView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        EvLog.i("mainactivity ondestroy");
        release();
        super.onDestroy();
        /*RefWatcher refWatcher = KmApplication.getRefWatcher(this);
        refWatcher.watch(this);*/
    }

    private void startSaveLog() {
        if (DeviceConfigManager.getInstance().isSupportSaveLog()) {
//            EvLog.d("Application__startSaveLog");
            LogSpaceManager.getInstance().start();
        }
    }

    /**
     * [功能说明]释放资源
     */
    private void release() {
        EvLog.i("release start =============>");
        RingBuffer.getInstance().destory();
        if (mTftp != null) {
            mTftp.stop();
        }
        if (DeviceConfigManager.getInstance().isSupportSaveLog()) {
            LogSpaceManager.getInstance().destory();
        }
        KmSongDownManager.getInstance().uninit();
        unregistObserver();
        mMVCtrlWidget.dismiss();

        HttpdServer.getInstance().stop();

        if (DeviceConfigManager.getInstance().isSupportCharge()) {
            ChargeProxy.getInstance().uninit();
        }
        FreeSongListManager.getInstance().uninit();

        EvLog.i("release RabbitMQManager disConnect ");
//        MessageCenterManager.getInstance().uninit();
        KmPlayerCtrl.getInstance().stop();
        KmPlayerCtrl.getInstance().uninit();
        PlayListDAOManager.getInstance().uninit();
        BackgroundUpdateManager.getInstance().stop();
        PlayListManager.getInstance().uninit();
        SungListManager.getInstance().uninit();
        FavoriteListManager.getInstance().uninit();
        ResourceSaverPathManager.getInstance().uninit();
        KmApplication.getInstance().uninit();
        BmpCacheManager.getInstance().clear();
        EvLog.i("release over-----");
    }

    private void stopPlayBack() {
        EvLog.e("stopPlayBack ----------------");
        onClickCutSong();
    }

    @Override
    public void onNetworkChanged(boolean isConnected) {
        EvLog.e("onNetworkChanged >>>>>>>>>>>>>>" + isConnected);
        boolean isWifi = !NetUtils.isCurrNetworkEthernet(this_);
        isConnected = NetUtils.isNetworkConnected(this_);
//        updateNetinfoIcon(isWifi,isConnected);

        if (isConnected && !isWifi) {
            if (FreeSongListManager.getInstance().getList().size() == 0) {
                FreeSongListManager.getInstance().init();
                EvLog.i("FreeSongListManager init");
                return;
            }
            KmPlayListItem item = KmPlayerCtrl.getInstance().getPlayingSong();
            if (item == null) {
                EvLog.i("PLAY_CTRL_MSG_NEXT_SONG");
                PlayCtrlHandler.getInstance().sendEmptyMessageDelayed(PlayCtrlHandler.PLAY_CTRL_MSG_NEXT_SONG, 2000);
                return;
            }
            PlayCtrlHandler.getInstance().sendEmptyMessageDelayed(PlayCtrlHandler.PLAY_CTRL_MSG_REPLAY_SONG, 2000);
        }
    }

    @Override
    public void onWifiStateChange(boolean isConnected) {
//        boolean isWifi = !NetUtils.isCurrNetworkEthernet(this_);
//        isConnected = NetUtils.isNetworkConnected(this_);
//        updateNetinfoIcon(isWifi,isConnected);
    }

    @Override
    public void onWifiRssiChange() {
//        boolean isWifi = !NetUtils.isCurrNetworkEthernet(this_);
//        boolean isConnected = NetUtils.isNetworkConnected(this_);
//        updateNetinfoIcon(isWifi,isConnected);
    }

    public interface OnDialogKeyListener {
        void onDialogKeyListener(int arg1, KeyEvent arg2);
    }

    @Override
    public void onScreenStateChange(boolean isScreenOn) {
        if (isScreenOn) {
            EvLog.d("MainActivity screen on ");
        } else {
            EvLog.d("MainActivity screen off ");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onHomeKeyPressed() {
        EvLog.d("onHomeKeyPressed");
        mMVCtrlWidget.hide();
    }

    public void updatePlayBackPlayTime() {
        if (KmPlayerCtrl.getInstance().getWorkMode() != PlayMode.MODE_PLAYBACK) {
            PlayCtrlHandler.getInstance().removeMessages(PlayCtrlHandler.MSG_UPDATE_PLAY_TIME);
            return;
        }

        final String playingHeader = getResources().getString(R.string.playback_tv_hint);
        final String playoverHeader = getResources().getString(R.string.playback_tv_over_hint);
        int state = KmPlayerCtrl.getInstance().getPlayerState();
        int remainTime = 0;
        String timeHint = "";
        if (state == PlayerCtrlState.STATE_PAUSE || state == PlayerCtrlState.STATE_PLAY) {
            remainTime = KmPlayerCtrl.getInstance().getDuration() - KmPlayerCtrl.getInstance().getPlayedTime();
            if (remainTime < 0) {
                remainTime = 0;
            }
            timeHint = playingHeader + TimeUtil.getShowTime(remainTime);
        } else {
            timeHint = playoverHeader;
        }

        mMainView.updatePlayBackInfo(timeHint);
        PlayCtrlHandler.getInstance().sendEmptyMessageDelayed(PlayCtrlHandler.MSG_UPDATE_PLAY_TIME, 1000);
        return;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                EvLog.d("qiangv", "============================AUDIOFOCUS_GAIN");
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player  
                EvLog.d("qiangv", "============================AUDIOFOCUS_LOSS");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop  
                // playback. We don't release the media player because playback  
                // is likely to resume  
                EvLog.d("qiangv", "============================AUDIOFOCUS_LOSS_TRANSIENT");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing  
                // at an attenuated level  
                EvLog.d("qiangv", "============================AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                break;
        }
    }

    public void showExitDialog() {
        // K米盒子不显示应用退出对话框
        if (!DeviceConfigManager.getInstance().isThirdApp()) {
            return;
        }
        if (mExitAppDialog == null) {
            mExitAppDialog = new CommonDialog(this_);
            mExitAppDialog.setTitle(-1);

            final String text = mContext.getResources().getString(R.string.exit_app_tip) + mContext.getResources().getString(R.string.app_name);
            mExitAppDialog.setContent(text);
            mExitAppDialog.setButton(R.string.exit, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean monkey = KmSharedPreferences.getInstance().getBoolean(KeyName.KEY_MONKEY_TEST, false);
                    if (!monkey) {
                        exitApp();
                    }
                }
            }, R.string.stay, null);
            mExitAppDialog.setOkBtnBg(R.drawable.btn_red_bg);
        }
        mExitAppDialog.show();
        return;
    }

    private void exitApp() {
        BaseApplication.getInstance().exit();
    }

    @Override
    public boolean onBackKeyPressed() {
        return false;
    }

    @Override
    public void onPlayListChange() {
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                EvLog.i("MainActivity onPlayListChange---------");
                updateTopSongInfo();
                int listCount = PlayListManager.getInstance().getCount();

                KmPlayListItem item = KmPlayerCtrl.getInstance().getPlayingSong();
                //开机启动时，有点播歌曲 或者 本地曲库为空时，点播歌曲
                if (item == null) {
                    EvLog.d("have ordersong when launch or local empty to order");
                    PlayCtrlHandler.getInstance().sendEmptyMessageDelayed(PlayCtrlHandler.PLAY_CTRL_MSG_CUT_SONG, 3000);
                } else if (item.getSongCategory() == SongCategory.CATEGORY_BROADCAST) {
                    EvLog.d("from broadcast to order");
                    PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_CUT_SONG);
                } else if (item.getSongCategory() == SongCategory.CATEGORY_PLAYLIST) {
                    KmPlayerCtrl.getInstance().playListChange();
                    if (listCount == 0) {

                    }
                }
            }
        });
    }

    public class ShowPayPageRunnable implements Runnable {
        private int mMaxPlayTime = 0;

        public ShowPayPageRunnable(int maxPlayTime) {
            mMaxPlayTime = maxPlayTime;
        }

        public int getMaxPlayTime() {
            return mMaxPlayTime;
        }

        public void run() {
            if (KmPlayerCtrl.getInstance().isPlayerDecoding()) {
                int playedTime = KmPlayerCtrl.getInstance().getPlayedTime() / 1000;
                int remainTime = (int) (mMaxPlayTime - playedTime);
                if (remainTime > 0) {
                    EvLog.d("ShowPayPageRunnable, remain time " + remainTime);
                    BaseApplication.getHandler().removeCallbacks(mShowPayPageRunnable);
                    BaseApplication.getHandler().postDelayed(mShowPayPageRunnable, remainTime * 1000);
                    return;
                }
                EvLog.d("ShowPayPageRunnable, need to pause" + KmPlayerCtrl.getInstance().getPlayerState() + ",playTime=" + KmPlayerCtrl.getInstance().getPlayedTime());

                if (!ChargeProxy.getInstance().isInitSuccess()) {
                    ToastUtil.showLongToast(this_, this_.getResources().getString(R.string.unicom_pay_error_init_failed));
                    PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_NEXT_SONG);
                    return;
                }
                KmPlayerCtrl.getInstance().pause();
                // TODO  订购参数
                Log.i(TAG, "run:我的订购2 ");
//                getXML();

//                getUnsubscribe();
//                ChargeViewManager.getInstance().clickChargeView(this_);
            }
        }
    }

    ;

    private ShowPayPageRunnable mShowPayPageRunnable = null;

    private void checkRemoveShowPageRunnable() {
        if (mShowPayPageRunnable != null) {
            BaseApplication.getHandler().removeCallbacks(mShowPayPageRunnable);
        }
    }

    private void checkWhetherShowPayPage(int songid, int duration) {
        if (!ChargeProxy.getInstance().isAuthed()) {
            if (!FreeSongListManager.getInstance().isFreeSong(songid)) {
                int maxPlayTime = SystemConfigManager.FREE_SING_MAX_TIME;
                if (duration < maxPlayTime) {
                    maxPlayTime = duration / 2;
                }
                if (mShowPayPageRunnable == null) {
                    mShowPayPageRunnable = new ShowPayPageRunnable(maxPlayTime);
                }
                int playedTime = KmPlayerCtrl.getInstance().getPlayedTime() / 1000;
                int delayShowPayTime = (int) (maxPlayTime - playedTime);
                EvLog.i("maxPlayTime:" + maxPlayTime + ",playedTime:" + playedTime + ",duration:" + duration);
                if (delayShowPayTime < 0) {
                    delayShowPayTime = 0;
                }
                BaseApplication.getHandler().removeCallbacks(mShowPayPageRunnable);
                EvLog.e(songid + " delay " + delayShowPayTime + " pause");
                BaseApplication.getHandler().postDelayed(mShowPayPageRunnable, delayShowPayTime * 1000);
            }
        }
    }

    @Override
    public void onPlayStart() {
        EvLog.d(" onPlayStart ---------------");
        //系统处于屏保状态
        if (!ScreenInfoSubject.getInstance().isScreenOn()) {
            EvLog.e("screen off:recv onPlayStart message,pause player");
            KmPlayerCtrl.getInstance().pause();
            mPlayerStatusBeforeScreenOff = PlayerCtrlState.STATE_PLAY;
            return;
        }

        KmPlayListItem currentInfo = KmPlayerCtrl.getInstance().getPlayingSong();
        EvLog.d(currentInfo.getSongName() + " recv onPlayStart,duration: " + currentInfo.getDuration());

        if (currentInfo != null && currentInfo.getSongCategory() == SongCategory.CATEGORY_PLAYLIST) {
            if (DeviceConfigManager.getInstance().isSupportCharge() && SystemConfigManager.SUPPORT_FREE_SING) {
                checkWhetherShowPayPage(currentInfo.getSongId(), currentInfo.getDuration());
            }
        }

        if (mMainView != null) {
            mMainView.showVideoView();
        }

        long announceShowElipse = System.currentTimeMillis() - mAnnounceShowStartTime;
        long delayHideAnnounceTime = DELAY_HIDE_ANNOUNCE - announceShowElipse;
        if (delayHideAnnounceTime < 0) {
            delayHideAnnounceTime = 0;
        }

        //联通盒子解码器从发出onFirstFrame到真正解码起来，时间有差接近2s
        if (DeviceConfigManager.getInstance().isThirdApp()) {
            delayHideAnnounce(delayHideAnnounceTime + 1600);
        }

        //回放模式
        if (currentInfo.getSongCategory() == SongCategory.CATEGORY_PLAYBACK) {
            PlayCtrlHandler.getInstance().sendEmptyMessageDelayed(PlayCtrlHandler.MSG_UPDATE_PLAY_TIME, 1000);
            return;
        }

        //点播歌曲记录点击率
        if (currentInfo.getSongCategory() == SongCategory.CATEGORY_PLAYLIST) {
            Song song = SongManager.getInstance().getSongById(currentInfo.getSongId());
            if (song == null) {
                UmengAgentUtil.reportError(currentInfo.getSongId() + " can not find in song");
            } else {
                int usageRate = song.getUsageRate() + 1;
                song.setUsageRate(usageRate);
            }
        }
        return;
    }


    @Override
    public void onPlay() {
        if (KmPlayerCtrl.getInstance().getWorkMode() == PlayMode.MODE_PLAYBACK) {
            KmOSDMessageManager.getInstance().sendKMMessageDrawView(new KmOSDMessage(KmOSDMessage.KM_OSDTV_TYPE_PLAY));
            return;
        }

        KmOSDMessageManager.getInstance().sendKMMessageDrawView(new KmOSDMessage(KmOSDMessage.KM_OSDTV_TYPE_PLAY));

    }

    @Override
    public void onPlayPause() {
        KmOSDMessageManager.getInstance().sendKMMessageDrawView(new KmOSDMessage(KmOSDMessage.KM_OSDTV_TYPE_PAUSE));
    }

    @Override
    public void onPlayStop() {
        EvLog.d("recv STATE_eStop msg,workmode=" + KmPlayerCtrl.getInstance().getWorkMode());
        checkRemoveShowPageRunnable();
        handlePlayStopEvent(true);
    }

    @Override
    public void onPlayAutoStop() {
        EvLog.i("onPlayAutoStop-------------------");
        checkRemoveShowPageRunnable();
        handlePlayAutoStopEvent();
    }

    @Override
    public void onPlayError(ErrorInfo info) {
        checkRemoveShowPageRunnable();
        handlePlayErrorEvent(info);
    }

    @Override
    public void onBufferingChange(int percent) {
        showLoadingView(getString(R.string.playerhint_buffering) + String.valueOf(percent) + "%");
        KmPlayListItem currentSong = KmPlayerCtrl.getInstance().getPlayingSong();
        if (currentSong != null) {
            String errorMsg = "[songid:" + currentSong.getSongId() + "][buffering percent:" + percent + "][playedtime=" + KmPlayerCtrl.getInstance().getPlayedTime() + "]";
            EvLog.e(errorMsg);
            UmengAgentUtil.reportError(errorMsg);
        }

        if (percent == 0) {
        } else if (percent == 100) {
            EvLog.i(" buffering end ");
            dismissLoadingView();
        }
    }

    @Override
    public void onBroadcastPrepared(KmPlayListItem item) {
        if (item == null) {
            EvLog.w("no broadcastsong");
            //本地没有歌曲播放时，清空播控内容保存的歌曲信息
            KmPlayerCtrl.getInstance().setPlayingSong(null);
            //回放切歌后，如果没有点播会走到这里，需要重新设置一下播放模式为正常模式
            /*if (KmPlayerCtrl.getInstance().getWorkMode() == PlayMode.MODE_PLAYBACK) {
                KmPlayerCtrl.getInstance().setWorkMode(PlayMode.MODE_NORMAL);
            }*/
            updateTopSongInfo();
//            mMainView.getBottomWidget().showNoSong();
            if (!DeviceConfigManager.getInstance().isThirdApp()) {
                ToastUtil.showLongToast(BaseApplication.getInstance(), getString(R.string.toast_nobroadcastsong_text));
            }
            return;
        }

        if (KmPlayerCtrl.getInstance().isPlayerDecoding()) {
            EvLog.e("onBroadcastPrepared  isPlayerDecoding");
            return;
        }

        if (PlayListManager.getInstance().getCount() > 0) {
            EvLog.e("Playlist is not empty,just return");
            return;
        }
        int broadcastSongType = DeviceConfigManager.getInstance().getBroadcastSongType();

        if (broadcastSongType == IDeviceConfig.BROADCAST_SONG_TYPE_LOCAL && !item.isMediaAvailable()) {
            EvLog.e(item.getSongName() + " broadcastsong,but local not exist");
            UmengAgentUtil.reportError(item.getSongName() + " broadcastsong,but local not exist");
            PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_NEXT_SONG);
            return;
        }
        EvLog.i("beign to playNext Broadcast Song:" + item.getSongName());
        KmPlayerCtrl.getInstance().playNextSong(item);
        boolean score = false;
        delayShowAnnnounce(score, DELAY_SHOW_ANNOUNCE);
    }

    @Override
    public void updateDownState() {
        KmPlayListItem currentSong = KmPlayerCtrl.getInstance().getPlayingSong();

        if (currentSong == null) {
            return;
        }

        String hint = "";
        int dataState = currentSong.getDataState();
        switch (dataState) {
            case SongDataState.STATE_NONE:
                hint = getResources().getString(R.string.toast_down_stage_prepare_getmedia);
                break;
            case SongDataState.STATE_GET_MEDIA:
                hint = getResources().getString(R.string.toast_down_stage_getmedia);
                break;
            case SongDataState.STATE_DOWNING:
                hint = getResources().getString(R.string.toast_down_prepare_downmedia);
                break;
            case SongDataState.STATE_COMPLETE:
                hint = getResources().getString(R.string.toast_down_media_down_finish);
                break;
            case SongDataState.STATE_ERROR:
                hint = getResources().getString(R.string.toast_down_media_down_error);
                break;
        }
        EvLog.i("updateDownState:" + hint);
        KmOSDMessageManager.getInstance().getKmOSDMessageView().updateDownloadProcess(hint);

    }

    @Override
    public void updateDownPercent(int percent, float speed) {
        NetSpeedInfo info = NetUtils.getNetSpeedWithUnit(speed);
        String hint = this_.getString(R.string.playerhint_loading, "" + percent, info.speed, info.unit);
//        EvLog.i("updateDownPercent hint:" + hint);
        KmOSDMessageManager.getInstance().getKmOSDMessageView().updateDownloadProcess(hint);
    }

    @Override
    public void onClickPlayCtrlBtn(int index, View view) {
        if (index == PlayCtrlWidget.PLAYCTRL_INDEX_SWITCH_GRADE_MODE) {
            PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_CHANGE_GRADE_MODE);
        } else if (index == PlayCtrlWidget.PLAYCTRL_INDEX_SWITCH_TRACK) {
            PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_SWITCH_TRACK);
        } else if (index == PlayCtrlWidget.PLAYCTRL_INDEX_NEXT_SONG) {
            onClickCutSong();
        } else if (index == PlayCtrlWidget.PLAYCTRL_INDEX_PLAY_PAUSE) {
            PlayCtrlHandler.getInstance().sendEmptyMessage(PlayCtrlHandler.PLAY_CTRL_MSG_PLAY_PAUSE);
        } else if (index == PlayCtrlWidget.PLAYCTRL_INDEX_REPLAY) {
            Message msg = PlayCtrlHandler.getInstance().obtainMessage(PlayCtrlHandler.PLAY_CTRL_MSG_REPLAY_SONG);
            msg.arg1 = PlayCtrlWidget.PLAYCTRL_INDEX_REPLAY;
            PlayCtrlHandler.getInstance().sendMessage(msg);
        } else if (index == PlayCtrlWidget.PLAYCTRL_INDEX_PLAY_LIST) {
            if (mMvSelectedView != null) {
                if (mMvSelectedView.getVisibility() != View.VISIBLE) {
                    mMvSelectedView.show();
                } else {
                    mMvSelectedView.hide();
                }
            }

            mMVCtrlWidget.hide();
        }
    }

    @Override
    public void onMVVolumeUpKeyEvent() {
        mMVCtrlWidget.showVolWidgetByEvent(this_, 0, true);
        LogAnalyzeManager.onEvent(this_, EventConst.ID_CLICK_VOLUME_MUSIC);
    }

    @Override
    public void onMVVolumeDownKeyEvent() {
        mMVCtrlWidget.showVolWidgetByEvent(this_, 0, false);
        LogAnalyzeManager.onEvent(this_, EventConst.ID_CLICK_VOLUME_MUSIC);
    }

    @Override
    public void onMVMenuKeyEvent() {
        if (!MainViewManager.getInstance().isMainViewVisible()) {
            if (/*MvSelectedManager.getInstance().isVisible()*/mMvSelectedView != null && mMvSelectedView.getVisibility() == View.VISIBLE) {
                EvLog.d("in mtv ,hide mv view");
                /*MvSelectedManager.getInstance().hideMvSelectedView();*/
                mMvSelectedView.hide();
            }
            EvLog.d("from mtv to main view");
            MainViewManager.getInstance().switchMainView();
        }
    }

    @Override
    public void onMVHomeKeyEvent() {
        if (!MainViewManager.getInstance().isMainViewVisible()) {
            if (/*MvSelectedManager.getInstance().isVisible()*/mMvSelectedView != null && mMvSelectedView.getVisibility() == View.VISIBLE) {
                EvLog.d("in mtv ,hide mv view");
                /*MvSelectedManager.getInstance().hideMvSelectedView();*/
                mMvSelectedView.hide();
            }
            EvLog.d("from mtv to home view");
        }
        MainViewManager.getInstance().backToHome();
    }

    @Override
    public void onMVPlayPauseKeyEvent() {
        UmengAgentUtil.onEventPlayPause(this_, EventConst.ID_CLICK_REMOTE_PLAY_PAUSE);
        HandlePlayAndPause();
    }

    @Override
    public void onUpdateHomeRightBottomIcon(final String path) {
        EvLog.d("updateHomeRightBottomIcon from:" + path);
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                MainBottomWidget bottomWidget = mMainView.getBottomWidget();
                if (bottomWidget == null) {
                    return;
                }
                bottomWidget.updateBottomRightIcon(path);
            }
        });
    }

    @Override
    public void onHomePictureUpdate() {
        BasePresenter.runInUI(new Runnable() {
            @Override
            public void run() {
                MainViewManager.getInstance().updateHomeView();
            }
        });
    }


    @Override
    public void onHomeKeyPress() {
        exitApp();
    }

    @Override
    public void onBottomMVClick() {
        MainViewManager.getInstance().switchMainView();
    }

    @Override
    public void onHuodongJumpPictureDismissListener() {
        showMainUi();
    }

    @Override
    public void exitActivity() {
        release();
    }

    private void log(String msg) {
        Log.d("gsp", TAG + ">>>" + msg);
    }

}
