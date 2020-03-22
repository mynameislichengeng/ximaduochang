package com.evideo.kmbox.widget.msgview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.text.TextUtils;
import android.widget.ImageView;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.model.dao.data.Singer;
import com.evideo.kmbox.model.dao.data.SingerManager;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.StorageConstant;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.presenter.AsyncPresenter;
import com.evideo.kmbox.thread.AsyncTaskManage;
import com.evideo.kmbox.thread.AsyncTaskManage.IAsyncTask;
import com.evideo.kmbox.util.CommonUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;
import com.evideo.kmbox.util.NetUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @brief : [获取报幕封面图片]
 */
public class GetAnnouncerCoverPresenter extends AsyncPresenter<String> {
    
    private AnnouncerCoverCallback mAnnouncerCoverCallback;
    private DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
                                                .cacheInMemory(false)
                                                .cacheOnDisk(false)
                                                .build();
    
    public GetAnnouncerCoverPresenter(AnnouncerCoverCallback announcerCoverCallback) {
        mAnnouncerCoverCallback = announcerCoverCallback;
    }

    @Override
    protected String doInBackground(Object... params) throws Exception {
        if(params == null || params.length <= 0) {
            return null;
        }
        if(!NetUtils.isNetworkConnected(BaseApplication.getInstance())) {
            return null;
        }
        String url = (String)params[0];
        EvLog.d("GetAnnouncerCoverPresenter url: " + url);
        if(TextUtils.isEmpty(url)) {
            return null;
        }
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = getHttpClient();
        String[] urls = url.split("/");
        String filename = urls[urls.length - 1];
      /*  String localPath = FileUtil.concatPath(StorageConstant.INTERNAL_SDCARD_RES, 
                StorageConstant.INTERNAL_TFTP_ROOT+"/");*/
        String localPath = ResourceSaverPathManager.getInstance().getTftpSavePath();
        if (!FileUtil.isFileExist(localPath)) {
            FileUtil.mkdir(localPath);
        }
        localPath = FileUtil.concatPath(localPath, filename);
        File file = new File(localPath);
        if (file != null && file.exists()) {
            file.delete();
        }
        InputStream is = null;
        FileOutputStream fileOutputStream = null;
        try {
            final HttpClient tempHttpClient = httpClient;
            int result = AsyncTaskManage.getInstance().registerHttpTask(new IAsyncTask() {
                @Override
                public void onCancel() {
                    EvLog.d("网络请求过程监听", "AsyncTaskManage onCancel");
                    tempHttpClient.getConnectionManager().shutdown();
                }
            });
            if(result == AsyncTaskManage.RESULT_STOP) {
                return null;
            }
            
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
                if (is != null) {
                    fileOutputStream = new FileOutputStream(localPath, false);
                    byte[] buf = new byte[1024 * 8];
                    int ch = -1;
                    while ((ch = is.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, ch);
                    }
                }
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                }
                return localPath;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if(httpClient != null) {    // 关闭httpClient
                httpClient.getConnectionManager().shutdown();
                httpClient = null;
            }
            CommonUtil.safeClose(fileOutputStream);
            CommonUtil.safeClose(is);
            file = null;
        }
        return null;
    }

    @Override
    protected void onCompleted(String result, Object... params) {
        if(result == null || params == null || params.length <= 0) {
            if(mAnnouncerCoverCallback != null) {
                mAnnouncerCoverCallback.imageLoaded(null);
            }
            return;
        }
        if(mAnnouncerCoverCallback != null) {
            mAnnouncerCoverCallback.imageLoaded(result);
        }
    }

    @Override
    protected void onFailed(Exception exception, Object... params) {
        if(mAnnouncerCoverCallback != null) {
            mAnnouncerCoverCallback.imageLoaded(null);
        }
    }
    
    /**
     * @brief : [获取报幕封面url]
     * @param song
     * @return
     */
    public static String getCoverUrl(Song song) {
        String url = null;
        if(song == null) {
            return null;
        }
        
        url = song.getAlbumURI();
        
        if(TextUtils.isEmpty(url)) {
            Singer singer = SingerManager.getInstance().getSinger(song.getSingerId(0));
            if(singer != null) {
                url = singer.getPictureURI();
            }
        }
        
        return url;
    }
    
    /**
     * 从内存卡中异步加载本地图片
     * 
     * @param uri
     * @param imageView
     */
    public static void displayFromDisk(String uri, ImageView imageView) {
        // String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
        ImageLoader.getInstance().displayImage("file://" + uri, imageView);
    }
    
    /**
     * 从assets文件夹中异步加载图片
     * 
     * @param imageName
     *            图片名称，带后缀的，例如：1.png
     * @param imageView
     */
    public static void dispalyFromAssets(String imageName, ImageView imageView) {
        // String imageUri = "assets://image.png"; // from assets
        ImageLoader.getInstance().displayImage("assets://" + imageName,
                imageView);
    }
    
    /**
     * 从内容提提供者中抓取图片
     */
    public static void displayFromContent(String uri, ImageView imageView) {
        // String imageUri = "content://media/external/audio/albumart/13"; //
        // from content provider
        ImageLoader.getInstance().displayImage("content://" + uri, imageView);
    }

    

    /** [连接超时] */
    private static final int TIME_OUT_CON = 40 * 1000;
    /** [读取超时] */
    private static final int TIME_OUT_SO = 35 * 1000;
    
    /**
     * @brief : [获取设置好超时参数的httpClient]
     * @return
     */
    public static HttpClient getHttpClient() {
        HttpParams params = new BasicHttpParams();
        // 设置连接超时和 Socket超时
        HttpConnectionParams.setConnectionTimeout(params, TIME_OUT_CON);
        HttpConnectionParams.setSoTimeout(params, TIME_OUT_SO);
        HttpClient httpclient = new DefaultHttpClient(params);
        return httpclient;
    }
    
    public interface AnnouncerCoverCallback {
        public void imageLoaded(String localPath);
    }
 
}
