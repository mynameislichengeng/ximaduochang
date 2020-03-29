/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-9-15     zhouxinghua     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.widget.mainview.homepage;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;
import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.model.dao.data.SingerManager;
import com.evideo.kmbox.model.datacenter.HomePictureManager;
import com.evideo.kmbox.model.datacenter.HomePictureManager.PictureType;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.loganalyze.LogAnalyzeManager;
import com.evideo.kmbox.model.songmenu.SongMenu;
import com.evideo.kmbox.model.songmenu.SongMenuManager;
import com.evideo.kmbox.model.umeng.EventConst;
import com.evideo.kmbox.model.update.huodong.HuoDongDataManager;
import com.evideo.kmbox.model.update.huodong.HuoDongDataManager.HuodongLocalInfo;
import com.evideo.kmbox.model.update.huodong.HuodongType;
import com.evideo.kmbox.util.BitmapUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.RoundedCornersTransform;
import com.evideo.kmbox.widget.StatusBarWidget;
import com.evideo.kmbox.widget.common.MaskFocusAnimImageView;
import com.evideo.kmbox.widget.common.SmoothHorizontalScrollView;
import com.evideo.kmbox.widget.common.ToastUtil;
import com.evideo.kmbox.widget.mainview.AbsBaseView;
import com.evideo.kmbox.widget.mainview.IMainViewCallback;
import com.evideo.kmbox.widget.mainview.MainViewId;
import com.evideo.kmbox.widget.mainview.MainViewManager;
import com.evideo.kmbox.widget.mainview.huodong.BaseTransformer;
import com.evideo.kmbox.widget.mainview.huodong.FadeInTransformer;
import com.evideo.kmbox.widget.mainview.huodong.FixedSpeedScroller;
import com.evideo.kmbox.widget.mainview.huodong.HuodongDialog;
import com.evideo.kmbox.widget.mainview.huodong.HuodongImageAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * [功能说明]首页
 */
public class HomePageView extends AbsBaseView implements OnClickListener,
        View.OnKeyListener {

    private final String TAG = HomePageView.class.getSimpleName();

    private int mFocusedId = -1;

    private OrderByPhoneDialog mOrderByPhoneDialog;
    private IMainViewCallback mMainViewCallback;
    private int mDefaultFocusViewId = 0;
    private boolean mHuodongUpdate = false;

    public static final int HOME_PAGE_COLUMN_MAX_ITEM = 2;
    public static final int HOME_PAGE_APP_COLUME_START_POS = 0;
    private final int HOME_PAGE_APP_CAROUSEL = 11;
    private int connt = 0;
    private int[] images = {R.drawable.carouse_meunl_animal, R.drawable.carouse_meun_graduation, R.drawable.carouse_meun_five};

    private Handler myHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HOME_PAGE_APP_CAROUSEL:
//                    initHuodongAdapterOne();
//                    log("--myHandle---handleMessage()--");
                    operateChangeHuadong();

                    break;
            }
        }
    };

    /**
     * @param activity
     */
    public HomePageView(Activity activity, int backViewId, IMainViewCallback mainViewCallback) {
        super(activity, backViewId);
        mMainViewCallback = mainViewCallback;
        initView();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getLayResId() {
        return R.layout.main_home_page_lay_third_platform;
    }

    @Override
    protected int getViewId() {
        return MainViewId.ID_HOME_PAGE;
    }

    public void firstItemRequestFocus() {
        findViewById(mDefaultFocusViewId).requestFocus();
    }

    /**
     * [功能说明] 首页皮肤更新
     */
    public void updateHomeView() {
        //更新首页大背景
        updateWindowBg(HomePictureManager.PictureType.WINDOW);
        //更新歌单
        getUpdatePictureByID(R.id.song_menu, HomePictureManager.PictureType.SONG_MENU, R.drawable.home_page_menu);
        //更新歌名
        getUpdatePictureByID(R.id.song_name, HomePictureManager.PictureType.SONG, R.drawable.home_page_song_name);
        //更新歌星
        getUpdatePictureByID(R.id.singer, HomePictureManager.PictureType.SINGER, R.drawable.home_page_singer);
        //最新歌曲
        getUpdatePictureByID(R.id.newestSong, HomePictureManager.PictureType.NEWEST_SONG, R.drawable.home_page_newestsong);
        //免费试唱
        getUpdatePictureByID(R.id.freesong, HomePictureManager.PictureType.RED_SONG, R.drawable.home_page_free_song);
        //儿童歌谣
        getUpdatePictureByID(R.id.child, HomePictureManager.PictureType.CHILDREN_SONG, R.drawable.home_page_children);
        //梨园戏曲
        getUpdatePictureByID(R.id.drama, HomePictureManager.PictureType.OPERA_SONG, R.drawable.home_page_opera);
        //更新排行及倒影
        getUpdatePictureByID(R.id.top, HomePictureManager.PictureType.TOP, R.drawable.home_page_top);
        //手机点歌
        getUpdatePictureByID(R.id.order_by_phone, HomePictureManager.PictureType.ORDER_BY_PHONE, R.drawable.home_page_order_by_phone_third_platform);
        //我的空间
        updateUserCenter();
        //关于我们
        getUpdatePictureByID(R.id.about, HomePictureManager.PictureType.ABOUT_US, R.drawable.home_page_about);
    }

    private void updateUserCenter() {
        HomePageMySpaceLayout lay = (HomePageMySpaceLayout) findViewById(R.id.user_center);
        if (lay == null) {
            return;
        }
        if (HomePictureManager.getInstance().hasPicture(HomePictureManager.PictureType.MY_SPACE)) {
            String typePath = HomePictureManager.getInstance().getPicturePath(HomePictureManager.PictureType.MY_SPACE);
            if (!TextUtils.isEmpty(typePath)) {
                Bitmap bmp = BitmapUtil.getBmpByPath(typePath);
                if (bmp != null) {
                    lay.updateBg(new BitmapDrawable(bmp));
                    return;
                }
            }
        }
        //restore default drawable
        lay.updateBgByResId(R.drawable.home_page_user_center);
        return;
    }

    /*
       private void updateMenu(int defaultDrawableId) {
           MaskFocusAnimImageView  songMenu = (MaskFocusAnimImageView)findViewById(R.id.song_menu);
           if (HomePictureManager.getInstance().hasPicture(HomePictureManager.PictureType.SONG_MENU)) {
               EvLog.i("updateMenu hasPicture");
               String songMenuPath = HomePictureManager.getInstance().getPicturePath(HomePictureManager.PictureType.SONG_MENU);
               if (!TextUtils.isEmpty(songMenuPath)) {
                   Bitmap srcBmp = BitmapFactory.decodeFile(songMenuPath);
                   if (srcBmp != null) {
                       songMenu.setImageBitmap(srcBmp);
                   }
               }
           } else {
               songMenu.setImageResource(defaultDrawableId);
           }
           return;
        }
       */
    private void updateWindowBg(PictureType type) {
        if (HomePictureManager.getInstance().hasPicture(HomePictureManager.PictureType.WINDOW)) {
            String windowPath = HomePictureManager.getInstance().getPicturePath(HomePictureManager.PictureType.WINDOW);
            if (!TextUtils.isEmpty(windowPath)) {
                Bitmap bmp = BitmapFactory.decodeFile(windowPath);
                if (bmp != null) {
                    mActivity.getWindow().setBackgroundDrawable(new BitmapDrawable(bmp));
                    return;
                }
            }
        }

        mActivity.getWindow().setBackgroundDrawableResource(R.drawable.app_bg);
        return;
    }


    private void getUpdatePictureByID(int resID, PictureType type, int defaultDrawableId) {
        EvLog.d("resID is:" + resID);
        EvLog.d("type is:" + type.toString());
        EvLog.d("getUpdatePictureByID");
        MaskFocusAnimImageView view = (MaskFocusAnimImageView) findViewById(resID);
        if (view == null) {
            return;
        }
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (HomePictureManager.getInstance().hasPicture(type)) {
            EvLog.d("HomePictureManager.getInstance().hasPicture");
            String typePath = HomePictureManager.getInstance().getPicturePath(type);
            EvLog.d("typePath is:" + typePath);
            if (!TextUtils.isEmpty(typePath)) {
                Bitmap bmp = BitmapUtil.getBmpByPath(typePath);
                EvLog.d("typePath != null");
                if (bmp != null) {
                    view.setImageBitmap(bmp);
                    EvLog.d("bmp != null");
                    return;
                }
            }
        }

        //restore default drawable
        view.setImageResource(defaultDrawableId);
        return;
    }


    private SmoothHorizontalScrollView mScrollView = null;

    public void ScrollToX(int x) {
        mScrollView.smoothScrollTo(x, mScrollView.getTop());
//        mScrollView.smoothScrollBy(x, mScrollView.getTop());
    }

    private void setClickListener(View view) {
        if (view != null) {
            view.setOnClickListener(this);
        }
    }

    private void initView() {
        mScrollView = (SmoothHorizontalScrollView) findViewById(R.id.home_scrollview);

        mDefaultFocusViewId = R.id.song_huodong_container;
        setClickListener(findViewById(R.id.singer));
        setClickListener(findViewById(R.id.song_name));
        setClickListener(findViewById(R.id.song_menu));
        setClickListener(findViewById(R.id.order_by_phone));
        setClickListener(findViewById(R.id.top));
        setClickListener(findViewById(R.id.child));
        setClickListener(findViewById(R.id.drama));
        setClickListener(findViewById(R.id.user_center));
        setClickListener(findViewById(R.id.about));
        setClickListener(findViewById(R.id.freesong));
        setClickListener(findViewById(R.id.newestSong));
        setClickListener(findViewById(R.id.song_huodong_container));

        initRightEdgeKeyListener();
//        initHuodongAdapter();
        initHuodongAdapterOne();
        Log.i("gsp", "initView:执行了几7777次 ");
    }

    private ImageView mHuodongViewPager = null;
    //    private RepeatTimerTask mRepeatTimerTask = null;
    private ArrayList<ImageView> imageViewContainer = null;
    private int mHuodongIndex = -1;
    private int mMyDuration = 800;          //持续时间
    private FixedSpeedScroller mScroller;
    //    private MaskFocusAnimLinearLayout mHuodongLayout = null;
    private List<HuodongLocalInfo> mDatas = null;
    private HuodongDialog mHuodongDialog = null;

    private HuodongImageAdapter mAdapter = null;

    private BaseTransformer switchTransformer(int position) {
        return new FadeInTransformer();
    }

    public void huodongUpdate() {
        mHuodongUpdate = true;
    }


    public void huodongReady() {
        List<HuodongLocalInfo> tmpList = HuoDongDataManager.getInstance().getList();
        if (tmpList.size() <= 0) {
            return;
        }
        mDatas.clear();
        mDatas.addAll(tmpList);
        if (imageViewContainer != null) {
            EvLog.i("empty imageViewContainer.size" + imageViewContainer.size());
            ImageView view = null;
            for (int i = 0; i < imageViewContainer.size(); i++) {
                view = imageViewContainer.get(i);
                view.setImageResource(0);
                view = null;
            }
        }
        imageViewContainer.clear();
        for (HuodongLocalInfo item : tmpList) {
            ImageView imageView = new ImageView(mActivity);
            imageView.setImageBitmap(BitmapUtil.getBmpByPath(item.smallBmpPath));
            imageViewContainer.add(imageView);
        }
        mHuodongIndex = -1;
//        mHuodongViewPager.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


    private void initHuodongAdapterOne() {
        mHuodongViewPager = (ImageView) findViewById(R.id.song_huodong);
        mHuodongViewPager.setImageResource(R.drawable.carouse_meunl_animal);
        mHuodongViewPager.setFocusable(false);
//        sendHandlerHuadong();
//        myHandle.sendEmptyMessageDelayed(HOME_PAGE_APP_CAROUSEL, 20000);
    }

    public void sendHandlerHuadong() {
        removeHandlerHuadong();
//        log("--sendHandlerHuadong()--");
        myHandle.sendEmptyMessageDelayed(HOME_PAGE_APP_CAROUSEL, 4000);
    }

    public void removeHandlerHuadong() {
//        log("--removeHandlerHuadong()--");
        myHandle.removeCallbacksAndMessages(null);
    }

    private SongMenu mHuaDongSongMenu = null;

    private void operateChangeHuadong() {

        List<SongMenu> mlist = SongMenuManager.getInstance().getSongMenuList();
        if (mlist != null && mlist.size() > 0) {
            if (connt >= mlist.size()) {
                connt = 0;
            } else {
//                log("--滑动的第几个界面--connt:" + connt);
            }
            mHuaDongSongMenu = mlist.get(connt);
            String url = mHuaDongSongMenu.imageUrl;
            int width = (int) mActivity.getResources().getDimension(R.dimen.dp10);
            RoundedCornersTransform transform = new RoundedCornersTransform(mActivity, width);
            transform.setNeedCorner(true, true, true, true);
            Glide.with(mActivity).
                    load(url).
                    asBitmap().
//                    override(width, width).
        centerCrop().
                    skipMemoryCache(false).
                    diskCacheStrategy(DiskCacheStrategy.ALL).
                    transform(transform).
                    into(mHuodongViewPager);

        } else {
            SongMenuManager.getInstance().startGetSongMenuListTask();

        }
        connt++;
        sendHandlerHuadong();

    }


    @Override
    public void onClick(View v) {
        if (v == null || mMainViewCallback == null) {
            return;
        }
        mFocusedId = v.getId();
        switch (v.getId()) {
            case R.id.song_huodong_container:
//                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_HUO_DONG);
//                clickHuodongBanner();
//                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_SONGMENU);
                if (mHuaDongSongMenu != null) {
//                    SongMenuManager.getInstance().notifySongMenuClicked(songMenu);
                    openHuadongDetailsView();
                    LogAnalyzeManager.onEventInSongMenuSubPage(mActivity, mHuaDongSongMenu.songMenuId);
//                    mMainViewCallback.openView(MainViewId.ID_SONG_MENU, MainViewId.ID_HOME_PAGE);
                } else {
                    ToastUtil.showLongToast(mActivity, "暂时还没有歌单数据,请求检查歌单是否存在");
                }

                break;
            case R.id.singer://歌星
                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_SINGER_SEARCH);
                mMainViewCallback.openView(MainViewId.ID_SINGER, MainViewId.ID_HOME_PAGE);
                break;
            case R.id.song_name://歌名
                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_SONG_SEARCH);
                mMainViewCallback.openView(MainViewId.ID_SONG_NAME, MainViewId.ID_HOME_PAGE);
                break;
            case R.id.song_menu://歌单
                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_SONGMENU);
                mMainViewCallback.openView(MainViewId.ID_SONG_MENU, MainViewId.ID_HOME_PAGE);
                break;
            case R.id.top://排行
                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_TOP);
                mMainViewCallback.openView(MainViewId.ID_TOP, MainViewId.ID_HOME_PAGE);
                break;
            case R.id.newestSong://最新歌曲
                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_NEWSONG);
                openNewSongDetailsView();
                break;
            case R.id.freesong://免费试唱
                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_FREESONG);
                mMainViewCallback.openView(MainViewId.ID_FREE_SONG, MainViewId.ID_HOME_PAGE);
                break;
            case R.id.child://麦霸歌曲
//                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_NEWSONG);
//                openNewSongDetailsView();
                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_CHILD);
                openMBSongDetailsView();
                break;
            case R.id.drama://王菲戏曲
//                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_NEWSONG);
//                openNewSongDetailsView();
                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_DRAMA);
                openWFSongDetailsView();
                break;
            case R.id.order_by_phone://热门精选
                // TODO 手机替换成最新歌曲
                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_NEWSONG);
                openNewSongDetailsView();
//                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_PHONE_ORDER_SONG);
//                showOrderByPhoneDialog();
                break;
            case R.id.about://客服
                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_CUSTOMER_SERVICE);
                mMainViewCallback.openView(MainViewId.ID_ABOUT, MainViewId.ID_HOME_PAGE);
                break;
            case R.id.user_center:
                LogAnalyzeManager.onEvent(mActivity, EventConst.ID_CLICK_USER_CENTER);

                mMainViewCallback.openView(MainViewId.ID_USER_SPACE, MainViewId.ID_HOME_PAGE);
                break;
            default:
                break;
        }
    }


    private void openHuadongDetailsView() {
        if (mMainViewCallback != null) {

            mMainViewCallback.openSongMenuDetailsView(mHuaDongSongMenu);
        }
    }

    private void openChildDetailsView() {
        if (mMainViewCallback != null) {
            SongMenu songMenu = SongMenu.getSongMenuChild();
            mMainViewCallback.openSongMenuDetailsView(songMenu);
        }
    }

    private void openDramaDetailsView() {
        if (mMainViewCallback != null) {
            SongMenu songMenu = SongMenu.getSongMenuDrama();
            mMainViewCallback.openSongMenuDetailsView(songMenu);
        }
    }

    private void openNewSongDetailsView() {
        if (mMainViewCallback != null) {
            SongMenu songMenu = SongMenu.getSongMenuNewSong();
            mMainViewCallback.openSongMenuDetailsView(songMenu);
        }
    }

    private void openWFSongDetailsView() {
        if (mMainViewCallback != null) {
            SongMenu songMenu = SongMenu.getSongMenuWFSong();
            mMainViewCallback.openSongMenuDetailsView(songMenu);
        }
    }

    private void openMBSongDetailsView() {
        if (mMainViewCallback != null) {
            SongMenu songMenu = SongMenu.getSongMenuMBSong();
            mMainViewCallback.openSongMenuDetailsView(songMenu);
        }
    }

    private boolean isFocusedIdValid() {
        if (mFocusedId == R.id.singer || mFocusedId == R.id.song_name
                || mFocusedId == R.id.song_menu
                || mFocusedId == R.id.order_by_phone
                || mFocusedId == R.id.top || mFocusedId == R.id.child
                || mFocusedId == R.id.drama /*|| mFocusedId == R.id.activity*/
                || mFocusedId == R.id.about /*|| mFocusedId == R.id.localsong*/
                /*|| mFocusedId == R.id.setting || mFocusedId == R.id.market*/
                || mFocusedId == R.id.freesong || mFocusedId == R.id.newestSong
                || mFocusedId == R.id.user_center || mFocusedId == R.id.song_huodong_container) {
            return true;
        }
        return false;
    }

    public boolean isIdAtRightEdge(int id) {
        if (id == R.id.order_by_phone || id == R.id.about || id == R.id.user_center) {
            return true;
        }
        return false;
    }

    private void showOrderByPhoneDialog() {
        if (mOrderByPhoneDialog == null) {
            mOrderByPhoneDialog = new OrderByPhoneDialog(mActivity);
        }
        mOrderByPhoneDialog.show();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (SongMenuManager.getInstance().getSongMenuList().size() == 0) {
            SongMenuManager.getInstance().startGetSongMenuListTask();
        }
        SongMenuManager.getInstance().registSongMenuListListener(songMenuListListener);

        sendHandlerHuadong();

        findViewById(R.id.about).setNextFocusDownId(R.id.small_mv_frame);
        updateUserCenter();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!DeviceConfigManager.getInstance().isThirdApp()) {
        }
        SongMenuManager.getInstance().unregistSongMenuListListener(songMenuListListener);

        removeHandlerHuadong();
    }


    private SongMenuManager.ISongMenuListListener songMenuListListener = new SongMenuManager.ISongMenuListListener() {
        @Override
        public void onSongMenuSelected(SongMenu songMenu) {

        }

        @Override
        public void onSongMenuDataChanged() {

            operateChangeHuadong();
        }

        @Override
        public void onSongMenuClicked(SongMenu songMenu) {

        }
    };

    @Override
    protected void resetFocus() {

        if (isFocusedIdValid()) {
            findViewById(mFocusedId).requestFocus();
        } else {
            findViewById(mDefaultFocusViewId).requestFocus();
        }
    }

    /**
     * [功能说明] 初始化设置、客服与消息中对于向右键的处理
     */
    private void initRightEdgeKeyListener() {
        if (DeviceConfigManager.getInstance().isThirdApp()) {
            // 非K米盒子，不作此处理
            return;
        }
        findViewById(R.id.about).setOnKeyListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (v.getId()) {
            case R.id.about:
            case R.id.order_by_phone:
            case R.id.user_center:
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    StatusBarWidget widget = MainViewManager.getInstance().getStatusBar();
                    if (widget != null) {
                        widget.setSelectedNumFocus();
                        Log.i("gsp", "onKey: 这个onKey到搜索是为了？？？？？？");
                        return true;
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void clickExitKey() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSmallMVUpKey() {
        return false;
    }

    @Override
    public boolean onSmallMVRightKey() {
        return false;
    }

    @Override
    public boolean onStatusBarDownKey() {
        return false;
    }

    private void log(String tag) {
        Log.d("gsp", TAG + ">>>" + tag);
    }
}
