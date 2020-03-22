package com.evideo.kmbox.model.datacenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.ErrorInfo;
import com.evideo.kmbox.R;
import com.evideo.kmbox.SystemConfigManager;
import com.evideo.kmbox.exception.DCDenyOfServiceException;
import com.evideo.kmbox.exception.DCNoResultException;
import com.evideo.kmbox.exception.DCProtocolParseException;
import com.evideo.kmbox.exception.DataCenterCommuException;
import com.evideo.kmbox.exception.NoSerialNumberException;
import com.evideo.kmbox.model.dao.data.Media;
import com.evideo.kmbox.model.dao.data.Song;
import com.evideo.kmbox.model.dao.data.SongManager;
import com.evideo.kmbox.model.datacenter.proxy.data.DataCenterMessage;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.down.DownError;
import com.evideo.kmbox.model.playerctrl.list.PendingFavoriteListData;
import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.model.songmenu.SongMenu;
import com.evideo.kmbox.model.songmenu.SongMenuDetail;
import com.evideo.kmbox.model.songmenu.SongMenuDetailManager;
import com.evideo.kmbox.model.songmenu.SongMenuManager;
import com.evideo.kmbox.model.songtop.SongTop;
import com.evideo.kmbox.model.songtop.SongTopDetail;
import com.evideo.kmbox.model.songtop.SongTopDetailManager;
import com.evideo.kmbox.model.songtop.SongTopManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.model.update.huodong.HuodongItemInfo;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.HttpFile;
import com.evideo.kmbox.widget.mainview.singer.SingerCoverInfo;

/**
 * DataCenter通信的业务逻辑层封装
 */
public class DCDomain {

    private String dbUri = "";
    private String dbVersion = "";

    public static final int Media_Type_AAC2_Ori = 2;
    public static final int Media_Type_AAC2_Aac = 3;
    public static final int Media_Type_MKV = 4;
    public static final int Media_Type_MKV2 = 5;
    public static final int Media_Type_TS = 6;
    public static final int Media_Type_MPG = 7;
    public static final int Media_Type_AVI = 8;

    private DCDomain() {

    }

    public static final DCDomain getInstance() {

        return DCDomainHolder.INSTANCE;
    }

    private static class DCDomainHolder {
        private static final DCDomain INSTANCE = new DCDomain();
    }

    /**
     * Request Database version.
     *
     * @return data base version.
     * @throws DCDenyOfServiceException Data center deny of service.
     * @throws DCProtocolParseException
     * @throws NoSerialNumberException  No Serial Number Exception
     */
    public String requestDatabaseVersion() throws Exception {
        if (TextUtils.isEmpty(dbVersion)) {
            requestDatabaseInfo();
        }

        return dbVersion;
    }

    /**
     * Request Database URI.
     *
     * @return data base URI.
     * @throws DCDenyOfServiceException Data center deny of service.
     * @throws DCProtocolParseException
     * @throws NoSerialNumberException  No Serial Number Exception
     */
    public String requestDatabaseURI() throws Exception {
        if (TextUtils.isEmpty(dbUri)) {
            requestDatabaseInfo();
        }

        return dbUri;
    }

    /**
     * [功能说明]清除数据库信息
     */
    public void clearDatabaseInfo() {
        dbUri = "";
        dbVersion = "";
    }

    public BootPictureInfo requestPictureInfo(String key) throws Exception {
        DataCenterMessage request = new DataCenterMessage();

        request.put("function", "cdn_resource");
        request.put("parameterName", key);

        DataCenterMessage response = sendMessage(request);
//        checkErrorCode(response.get("errorcode"));
        String errorCode = response.get("errorcode");
        if (!errorCode.equals("0")) {
            throw new DataCenterCommuException("cdn_resource", errorCode);
        }

        String url = response.get("url");
        String version = response.get("version");
        JSONObject extended = response.getJSONObject("extended");
        long duration = extended.getLong("duration");

        return new BootPictureInfo(key, version, url, duration);
    }

    public static DataCenterMessage sendMessage(DataCenterMessage request) throws Exception {
        DataCenterMessage response = null;
        try {
            long timeStart = System.currentTimeMillis();
            response = DataCenterCommu.getInstance().sendMessage(request);
            if ((System.currentTimeMillis() - timeStart) > 300) {
                EvLog.i("datacenter sendmessage eclipse:" + (System.currentTimeMillis() - timeStart) + " ms");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return response;
    }

    public ResourceInfo requestResourceInfo(String key) throws Exception {
        DataCenterMessage request = new DataCenterMessage();

        request.put("function", "cdn_resource");
        request.put("parameterName", key);
//        EvLog.d("requestResourceInfo,request=" + request.getContentString());
        DataCenterMessage response = sendMessage(request);

        EvLog.d("requestResourceInfo,key:" + key + ",response=" + response.getContentString());
//        checkErrorCode(response.get("errorcode"));
        String errorCode = response.get("errorcode");
        if (!errorCode.equals("0")) {
//            throw new DataCenterCommuException("bs_cdn_resource", errorCode);
            return new ResourceInfo("", "", Integer.valueOf(errorCode));
        }

        String url = response.get("url");
        String version = response.get("version");

        return new ResourceInfo(version, url, 0);
    }

    //返回歌曲信息
    public Song requestSongInfo(int songId) throws Exception {

        DataCenterMessage request = new DataCenterMessage();

        request.put("function", "song");
        request.put("songid", String.format("%08d", songId));

        DataCenterMessage response = sendMessage(request);
        Log.i("gsp", "requestSongInfo: response:" + response.toString());
        String errorCode = response.get("errorcode");

        if (!errorCode.equals("0")) {
            throw new DataCenterCommuException("song", errorCode);
        }

        int id = Integer.valueOf(response.get("id"));
        if (id == -1) {
            return null;
        }
        // TODO 设置的数据
        String name = response.get("name");
        String spell = response.get("py");
        String singerDescription = response.get("singername");
        int singerId0 = Integer.valueOf(response.get("singerid1"));
        int singerId1 = Integer.valueOf(response.get("singerid2"));
        int singerId2 = Integer.valueOf(response.get("singerid3"));
        int singerId3 = Integer.valueOf(response.get("singerid4"));
        int language = Integer.valueOf(response.get("language"));
        int type = Integer.valueOf(response.get("type"));
        boolean canScore = Integer.valueOf(response.get("isgrand")) == 0;
        int playRate = Integer.valueOf(response.get("playnum"));
        String albumResourceId = response.get("album");
        if (TextUtils.isEmpty(singerDescription)) {
            singerDescription = BaseApplication.getInstance().getString(R.string.default_singer_name);
        }

        Song song = new Song(id, name, spell, singerDescription, new int[]{singerId0, singerId1, singerId2, singerId3},
                language, type, playRate, albumResourceId, canScore);
        return song;
    }

    //返回歌曲的下载链接、评分文件下载链接等信息
    public int requestSongMediaList(int songId, String mac, String token, List<Media> list, ErrorInfo errorInfo) throws Exception {
        if (list == null) {
            errorInfo.errorCode = DownError.ERROR_CODE_INPUT_PARAM_INVALID;
            errorInfo.errorMessage = "input param invalid in requestSongMediaList";
            return errorInfo.errorCode;
        }

        //JSONException
        DataCenterMessage request = new DataCenterMessage();
        request.put("function", "media");
        request.put("songid", String.format("%08d", songId));
        request.put("mac", mac);
        request.put("token", token);
        request.put("code", DeviceConfigManager.getInstance().getCode());
        EvLog.d("requestSongMediaList:" + request.getContentString());
        DataCenterMessage response = null;
        String errorCode = "";
        try {
            response = sendMessage(request);
            errorCode = response.get("errorcode");
            if (TextUtils.isEmpty(errorCode)) {
                errorInfo.errorMessage = "requestSongMediaList error,content:" + response.getContentString()
                        + ",sn=" + DeviceConfigManager.getInstance().getChipId();
                UmengAgentUtil.reportError(errorInfo.errorMessage);
                errorInfo.errorCode = DownError.ERROR_CODE_GET_MEDIALIST_NULL;
                return errorInfo.errorCode;
            }
        } catch (Exception e) {
//                throw e;
            EvLog.i("sendMessage catch exception," + e.getMessage());
            errorInfo.errorCode = DownError.ERROR_CODE_SEND_GET_MEDIALIST_FAILED;
            errorInfo.errorMessage = "send requestSongMediaList message failed";
            return errorInfo.errorCode;
        }

        EvLog.d(songId + ",response:" + response.getContentString());
        if (!errorCode.equals("0")) {
            EvLog.d("errormessage: " + response.get("errormessage"));
//            throw new DataCenterCommuException("sn_song_media_list", errorCode);
            errorInfo.errorCode = DownError.ERROR_CODE_SERVER_RESPONSE_ERROR;
            errorInfo.errorCodeSupplement = Integer.valueOf(errorCode);
            errorInfo.errorMessage = response.get("errormessage");
            return errorInfo.errorCode;
        }

        // get medialist
        JSONArray array = response.getJSONArray("medialist");
        if (array == null) {
            errorInfo.errorCode = DownError.ERROR_CODE_GET_MEDIALIST_NULL;
            errorInfo.errorMessage = "get no medialist array,response content:" + response.getContentString();
            return errorInfo.errorCode;
        }

        if (array.length() == 0) {
            errorInfo.errorCode = DownError.ERROR_CODE_GET_MEDIALIST_NULL;
            errorInfo.errorMessage = "medialist array is empty,response content:" + response.getContentString();
            return errorInfo.errorCode;
        }

        if (TextUtils.isEmpty(response.get("origininfo"))) {
            errorInfo.errorCode = DownError.ERROR_CODE_SERVER_RESPONSE_CONTENT_INVALID;
            errorInfo.errorMessage = "origininfo is empty,response content:" + response.getContentString();
            return errorInfo.errorCode;
        }
        int origininfo = Integer.valueOf(response.get("origininfo"));

        if (TextUtils.isEmpty(response.get("accompanyinfo"))) {
            errorInfo.errorCode = DownError.ERROR_CODE_SERVER_RESPONSE_CONTENT_INVALID;
            errorInfo.errorMessage = "accompanyinfo is empty,response content:" + response.getContentString();
            return errorInfo.errorCode;
        }
        int accompanyinfo = Integer.valueOf(response.get("accompanyinfo"));

        if (TextUtils.isEmpty(response.get("vol"))) {
            errorInfo.errorCode = DownError.ERROR_CODE_SERVER_RESPONSE_CONTENT_INVALID;
            errorInfo.errorMessage = "vol is empty,response content:" + response.getContentString();
            return errorInfo.errorCode;
        }

        if (response.get("logout") != null) {
            boolean ret = Boolean.valueOf(response.get("logout"));
            if (ret) {
            }
        }
        int vol = Integer.valueOf(response.get("vol"));

        String subtitleurl = response.get("subtitleurl");

        String url = "";
        int fileSize = 0;
//        String extension = "";
//        String fileName = "";
        int duration = 0;
        int type = 0;
        int mediaType = 0;

        for (int i = 0; i < array.length(); i++) {
            mediaType = array.getJSONObject(i).getInt("type");
            if (mediaType == Media_Type_MKV || mediaType == Media_Type_MKV2) {
                type = Media.Type_MKV;
            } else if (mediaType == Media_Type_TS) {
                type = Media.Type_TS;
            } else if (mediaType == Media_Type_MPG) {
                type = Media.Type_MPG;
            } else if (mediaType == Media_Type_AVI) {
                type = Media.Type_AVI;
            } else if (mediaType == Media_Type_AAC2_Ori || mediaType == Media_Type_AAC2_Aac) {
                type = Media.Type_AACX2;
            } else {
                type = Media.Type_Unknown;
            }
            url = array.getJSONObject(i).getString("url");
            String contentid2 = url.substring(0, url.lastIndexOf(";"));
            String cpid2 = url.substring(url.indexOf(";") + 1);
            url = "http://gslbserv.itv.cmvideo.cn/" + cpid2 + "?channel-id=shengytcsp&Contentid=" + contentid2 + "&authCode=3a&stbId=005903FF000100606001C0132B02ED15&usergroup=g28093100000&userToken=bc646872b5f7b79a5574a1e19b6c0e6a28vv";
            EvLog.i("requestSongMediaList url=" + url);
            fileSize = array.getJSONObject(i).getInt("filesize");
            //如果数据中心返回的文件大小不正常，直接http获取
            if (fileSize < 5 * 1024 * 1024) {
                UmengAgentUtil.reportError("[DC-Error] fileSize is wrong,songid=" + songId);
                HttpFile file = new HttpFile();
                int ret = file.open(url);
                if (ret == 0) {
                    fileSize = (int) file.getContentLength();
                    EvLog.e("http get fileSize:" + fileSize);
                }
                file.close();
                file = null;
            }

            if (array.getJSONObject(i).has("duration")) {
                duration = array.getJSONObject(i).getInt("duration");
            }

            if (duration <= 0) {
                UmengAgentUtil.reportError("[DC-Error] duration <= 0,songid=" + songId);
                duration = SystemConfigManager.VIDEO_DEFAULT_DURATION;
            }
//            EvLog.i("songid=" + songId + ",ByteRate=" + (fileSize)/(duration*1024) + "KByte/S");

            Media media = new Media(Media.Invalid_Id,
                    type, songId, "songname",
                    url,
                    origininfo,
                    accompanyinfo,
                    vol, "",
                    subtitleurl);
            if (duration > 0) {
                media.setDuration(duration);
//                EvLog.d(songId + " set duration:" + duration);
            }
            media.setLocalSubtitleName("");
            media.setResourceSize(fileSize);
            list.add(media);
        }

        if (list.size() == 0) {
            errorInfo.errorCode = DownError.ERROR_CODE_MEDIA_LIST_INVALID;
            //补充http  responseCode
//            if (httpResponseCode != 0) {
//                errorInfo.errorCodeSupplement = httpResponseCode;
//            }
            errorInfo.errorMessage = "all medai url in json is invalid";
        } else {
            errorInfo.errorCode = 0;
        }

        return errorInfo.errorCode;
    }

    //产品商用曲库的下载
    private void requestDatabaseInfo() throws Exception {
        DataCenterMessage request = new DataCenterMessage();
        DataCenterMessage response = new DataCenterMessage();

        request.put("function", "db_info");
        response = sendMessage(request);

        String errorCode = response.get("errorcode");
        if (!errorCode.equals("0")) {
            throw new DataCenterCommuException("db_info", errorCode);
        }

        dbUri = response.get("url");
        dbVersion = response.get("version");
        EvLog.d("dbUri=" + dbUri + ",dbVersion=" + dbVersion);
    }

    /**
     * [普通歌曲]
     */
    public static final int SONG_TYPE_NORMAL = 0;
    /**
     * [评分歌曲]
     */
    public static final int SONG_TYPE_KM_TRAIN = 8;

    /**
     * [请求歌单列表（精选集）]
     *
     * @return 歌单列表
     * @throws DCProtocolParseException 数据中心协议解析异常
     * @throws DCNoResultException      数据中心无返回值
     */
    public List<SongMenu> requestSongMenuList() throws Exception {
        EvLog.d("begin to requestSongMenuList");
        DataCenterMessage request = new DataCenterMessage();
        DataCenterMessage response = new DataCenterMessage();

        request.put("function", "song_menu_list");
        request.put("companycode", "00002");

        response = sendMessage(request);
        String errorCode = response.get("errorcode");
        String errorMessage = response.get("errormessage");

        if (!errorCode.equals("0")) {
            throw new DataCenterCommuException("song_menu_list", errorCode, errorMessage);
        }
        String picUrlHead = response.get("picurlhead");

        JSONObject rsJsonObject = new JSONObject(response.get("rs"));

        int returnNum = rsJsonObject.getInt("return");

        if (returnNum == 0) {
            throw new DCNoResultException("requestSongMenuList");
        }

        JSONArray rJsonArray = rsJsonObject.getJSONArray("r");

        if (rJsonArray == null) {
            return Collections.emptyList();
        }

        int size = rJsonArray.length();
        // 解析歌单列表
        List<SongMenu> songMenuList = new ArrayList<SongMenu>();
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = rJsonArray.getJSONObject(i);
            if (jsonObject == null) {
                continue;
            }

            int songMenuId = jsonObject.getInt("tid");
            String name = jsonObject.getString("s");
            String description = jsonObject.getString("des");
            int fid = jsonObject.getInt("fid");
            Log.i("gsp", "requestSongMenuList:+++ " + songMenuId + "  " + fid + "  " + " ");
            String imageUrl = picUrlHead + "?fileid=" + fid;
            SongMenu songMenu = new SongMenu(songMenuId, imageUrl, name,
                    description);
            Log.i("gsp", "requestSongMenuList: ++" + songMenu.toString());
            songMenuList.add(songMenu);
        }

        if (!songMenuList.isEmpty()) {
            // 把歌单列表保存到数据库中
            SongMenuManager.getInstance().saveSongMenuDataToDB(songMenuList);
            // 更新时间戳
            KmSharedPreferences.getInstance().putLong(
                    KeyName.KEY_SONG_MENU_DATA_TIMESTAMP, System.currentTimeMillis());
        }

        return songMenuList;
    }

    /**
     * [请求歌单歌曲列表]
     *
     * @param songMenuId 歌单id（专辑id）
     * @param startPos   申请起始记录数，1表示第1条
     * @param requesNum  申请的记录数，-1表示所有
     * @return 歌曲列表
     * @throws DCProtocolParseException 数据中心协议解析异常
     * @throws DCNoResultException      数据中心无返回值
     */
    public List<Song> requestSongMenuDetailsInfo(
            int songMenuId, int startPos, int requesNum) throws Exception {

        DataCenterMessage request = new DataCenterMessage();
        DataCenterMessage response = null;

        request.put("function", "song_menu_song_list");
        request.put("companycode", "00002");
        request.put("tid", songMenuId + "");
        request.put("startpos", startPos + "");
        request.put("requestnum", requesNum + "");

        response = sendMessage(request);
//        Log.i("gsp", "requestSongMenuDetailsInfo:请求的数据 " + response);
//        if (response != null) {
//            Log.i("gsp", "requestSongMenuDetailsInfo:返回结果: " + response.getContentString());
//            Log.i("gsp", "requestSongMenuDetailsInfo:返回结果: " + response.errorMsg);
//        }

        String errorCode = response.get("errorcode");
        String errorMessage = response.get("errormessage");
        if (!errorCode.equals("0")) {
            throw new DataCenterCommuException("song_menu_song_list", errorCode, errorMessage);
        }

        String picUrlHead = response.get("picurlhead");
        String picId = response.get("picid");

        JSONObject rsJsonObject = new JSONObject(response.get("rs"));

        int totalNum = rsJsonObject.getInt("total");
        int returnNum = rsJsonObject.getInt("return");
        Log.i("gsp", "requestSongMenuDetailsInfo: returnNum:" + returnNum);
        Log.i("gsp", "requestSongMenuDetailsInfo: totalNum:" + totalNum);
        if (returnNum == 0) {
            throw new DCNoResultException("requestSongMenuDetailsInfo");
        }

        JSONArray rJsonArray = rsJsonObject.getJSONArray("r");
        Log.i("gsp", "requestSongMenuDetailsInfo: " + rJsonArray);
        if (rJsonArray == null) {
            return Collections.emptyList();
        }

        int size = rJsonArray.length();
        // 解析歌单歌曲列表
        List<Song> songList = new ArrayList<Song>();
        JSONObject jsonObject = null;
        int id = 0;
        Song song = null;
        for (int i = 0; i < size; i++) {
            jsonObject = rJsonArray.getJSONObject(i);
            Log.i("gsp", "requestSongMenuDetailsInfo:jsonObject+" + jsonObject.toString());
            if (jsonObject == null) {
                continue;
            }
            String sss = jsonObject.getString("id");

//            id =Integer.valueOf(sss);
            id = jsonObject.getInt("id");
            Log.i("gsp", "requestSongMenuDetailsInfo:      " + " id" + id + "    " + sss);
            // song对象先从数据库中获取,没有再从网络获取
            song = SongManager.getInstance().getSongById(id);
            Log.i("gsp", "request1111SongMenuDetailsInfo: " + song);
            if (song == null) {
                song = requestSongInfo(id);
                Log.i("gsp", "request1qqqSongMenuDetailsInfo: " + song);
                if (song != null) {
                    SongManager.getInstance().add(song);
                }
            }
            if (song != null) {
                songList.add(song);
            }
        }

        SongMenu songMenu = SongMenuManager.getInstance().getSongMenuById(songMenuId);
        if (songMenu != null) {
            // 保存大图url
            songMenu.imageUrlBig = picUrlHead + "?fileid=" + picId;
            SongMenuManager.getInstance().save(songMenu);
        } else {
            if (songMenuId == SongMenu.SONG_MENU_ID_NEW_SONG) {
                EvLog.i("add SONG_MENU_ID_NEW_SONG to tblSongmenuDb");
                String imageUrl = picUrlHead + "?fileid=" + picId;
                String name = BaseApplication.getInstance().getResources().getString(R.string.song_menu_new_song);
                String desp = BaseApplication.getInstance().getResources().getString(R.string.song_menu_new_song_description);
                songMenu = new SongMenu(songMenuId, imageUrl, name, desp);
                songMenu.imageUrlBig = picUrlHead + "?fileid=" + picId;
                SongMenuManager.getInstance().save(songMenu);
            } else {
                EvLog.i(songMenuId + " add  to tblSongmenuDb");
//                String imageUrl = picUrlHead + "?fileid=" + picId;
                songMenu = new SongMenu(songMenuId, "", "", "");
                songMenu.imageUrlBig = picUrlHead + "?fileid=" + picId;
                if (response.get("s") != null) {
                    songMenu.name = response.get("s");
                }
                if (response.get("des") != null) {
                    songMenu.description = response.get("des");
                }
                EvLog.e("songMenuId=" + songMenuId + ",name=" + songMenu.name + ",description=" + songMenu.description);
                SongMenuManager.getInstance().save(songMenu);
            }
        }

        // 保存歌单详情到数据库中并更新时间戳
        if (!songList.isEmpty() && songMenu != null) {
            List<SongMenuDetail> details = new ArrayList<SongMenuDetail>();
            for (Song songItem : songList) {
                SongMenuDetail detail = new SongMenuDetail(songMenuId, songItem.getId());
                details.add(detail);
            }
            SongMenuDetailManager.getInstace().saveSongMenuDetailsToDB(songMenuId, details);

            // 在获取第一页数据时保存时间戳和歌曲总数
            if (startPos == 1) {
                songMenu.timestamp = System.currentTimeMillis();
                songMenu.totalNum = totalNum;
                SongMenuManager.getInstance().save(songMenu);
            }
        }
        return songList;
    }

    /**
     * [功能说明]
     *
     * @param singerId 歌星ID
     * @return 歌星头像信息
     */
    public SingerCoverInfo requestSingerCover(int singerId) throws Exception {
        EvLog.i("here comes the request process");
        DataCenterMessage request = new DataCenterMessage();
        DataCenterMessage response = new DataCenterMessage();

        request.put("function", "singer_picture");
        request.put("SongsterID", singerId + "");

        response = sendMessage(request);

        String errorCode = response.get("errorcode");
        String errorMessage = response.get("errormessage");

        if (!errorCode.equals("0")) {
            throw new DataCenterCommuException("singer_picture", errorCode, errorMessage);
        }

        SingerCoverInfo info = new SingerCoverInfo();
        String picHeadUrl = response.get("picurhead");
        info.mPicHeadUrl = picHeadUrl;

        if (response.get("pictures").isEmpty()) {
            throw new DCNoResultException("requestSingerHeaderPic");
        }
        JSONObject picJsonObject = new JSONObject(response.get("pictures"));
        String url = new String();
        url = picJsonObject.getString("pic_fileid_h");
        info.mCoverPicH = url;
        url = picJsonObject.getString("pic_fileid_l");
        info.mCoverPicL = url;
        url = picJsonObject.getString("pic_fileid_m");
        info.mCoverPicM = url;
        url = picJsonObject.getString("pic_fileid_s");
        info.mCoverPicS = url;
        return info;
    }

    /**
     * [功能说明]
     * 返回活动页面的图片及对应的活动链接地址
     */
    public List<HuodongItemInfo> requestHuoDongList() throws Exception {
        DataCenterMessage request = new DataCenterMessage();
        DataCenterMessage response = new DataCenterMessage();

        request.put("function", "activity_source");

        response = sendMessage(request);
        String errorCode = response.get("errorcode");
        String errorMessage = response.get("errormessage");

        if (!errorCode.equals("0")) {
            throw new DataCenterCommuException("activity_source", errorCode, errorMessage);
        }

        JSONArray array = response.getJSONArray("activity");
        List<HuodongItemInfo> listInfo = new ArrayList<HuodongItemInfo>();

        for (int i = 0; i < array.length(); i++) {
            HuodongItemInfo info = new HuodongItemInfo();
            info.activity_id = array.getJSONObject(i).getInt("activity_id");
            info.activity_type = array.getJSONObject(i).getInt("activity_type");
            info.activity_title = array.getJSONObject(i).getString("activity_title");
//            info.activityUrl = array.getJSONObject(i).getString("activity_url");
            JSONArray imgUrlarray = array.getJSONObject(i).getJSONArray("img_url");
            if (imgUrlarray == null) {
                continue;
            }
            info.imgUrl = (String) imgUrlarray.get(0);
            info.activity_arg0 = (String) array.getJSONObject(i).getString("args0");
            listInfo.add(info);
        }
        EvLog.d("get listInfo.size=" + listInfo.size());
        return listInfo;
    }

    /**
     * [功能说明]请求大家都在唱
     *
     * @return 关键词列表
     */
    public List<String> requestHotSearchWords() throws Exception {
        EvLog.i("here comes request Hot Search Words");
        DataCenterMessage request = new DataCenterMessage();
        DataCenterMessage response = new DataCenterMessage();
        request.put("function", "everyone_singing");

        response = sendMessage(request);

        String errorCode = response.get("errorcode");
        String errorMessage = response.get("errormessage");

        if (!errorCode.equals("0")) {
            throw new DataCenterCommuException("everyone_singing", errorCode, errorMessage);
        }
        JSONArray array = response.getJSONArray("resource");

        List<String> listInfo = new ArrayList<String>();

        for (int i = 0; i < array.length(); i++) {
            String info = array.getString(i);
            if (info == null) {
                continue;
            }
            listInfo.add(info);
        }
        return listInfo;
    }

    /**
     * [功能说明]获取待删歌曲的id列表
     *
     * @param version 版本
     * @return 待删歌曲的id列表
     */
    public PendingDeleteSongData requestSongsToBeDeleted(int version) throws Exception {
        DataCenterMessage request = new DataCenterMessage();

        request.put("function", "del_songs");
        request.put("version", String.valueOf(version));

        DataCenterMessage response = sendMessage(request);

        String errorCode = response.get("errorcode");
        EvLog.d("requestSongsToBeDeleted:" + response.getContentString());
        if (!errorCode.equals("0")) {
            throw new DataCenterCommuException("del_songs", errorCode);
        }

        String newVersion = response.get("max_version");
        JSONArray array = response.getJSONArray("songs_id_list");
        List<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < array.length(); i++) {
            list.add(array.getInt(i));
        }

        return new PendingDeleteSongData(Integer.valueOf(newVersion).intValue(), list);
    }

    /**
     * [功能说明]盒子向数据中心发送本地的收藏列表
     *
     * @return
     */
    public boolean requestFavoriteAdd(String chipid, List<Integer> songids) throws Exception {
        boolean ret = false;
        if (songids == null && TextUtils.isEmpty(chipid)
                || songids != null && songids.isEmpty()) {
            return ret;
        }
        JSONArray sendArray = new JSONArray();

        for (int i = 0; i < songids.size(); i++) {
            JSONObject obj = new JSONObject();
            obj.put("songid", String.format("%08d", songids.get(i)));
            sendArray.put(obj);
        }
        DataCenterMessage request = new DataCenterMessage();
        DataCenterMessage response = new DataCenterMessage();


        request.put("function", "collect_add");
        request.put("userid", chipid);
        request.put("list", sendArray);

        response = sendMessage(request);

        String errorCode = response.get("errorcode");
        if (!errorCode.equals("0")) {
            ret = false;
        }
        ret = true;
        return ret;
    }

    /**
     * [功能说明]盒子向数据中心请求删除云端的收藏列表
     *
     * @return
     */
    public boolean requestFavoriteDel(String chipid, List<Integer> songids) throws Exception {
        boolean ret = false;
        if (songids == null && TextUtils.isEmpty(chipid)
                || songids != null && songids.isEmpty()) {
            return ret;
        }
        DataCenterMessage request = new DataCenterMessage();
        DataCenterMessage response = new DataCenterMessage();

        JSONArray sendArray = new JSONArray();

        for (int i = 0; i < songids.size(); i++) {
            JSONObject obj = new JSONObject();
            obj.put("songid", String.format("%08d", songids.get(i)));
            sendArray.put(obj);
        }

        request.put("function", "collect_del");
        request.put("userid", chipid);
        request.put("list", sendArray);

        response = sendMessage(request);

        String errorCode = response.get("errorcode");
        if (!errorCode.equals("0")) {
            ret = false;
            throw new DataCenterCommuException("ru_favorite_del", errorCode);
        }
        ret = true;
        return ret;
    }

    /**
     * [功能说明]盒子向数据中心请求收藏列表
     *
     * @return
     */
    public PendingFavoriteListData requestFavoriteDownload(String chipid,
                                                           int startPos, int requesNum) throws Exception {
        PendingFavoriteListData data = new PendingFavoriteListData();
        if (TextUtils.isEmpty(chipid) || requesNum == 0) {
            return data;
        }
        DataCenterMessage request = new DataCenterMessage();
        DataCenterMessage response = new DataCenterMessage();

        request.put("function", "collect_download");
        request.put("userid", chipid);
        request.put("start", startPos);
        request.put("num", requesNum);
        response = sendMessage(request);

        String errorCode = response.get("errorcode");
        if (!errorCode.equals("0")) {
            throw new DataCenterCommuException("ru_favorite_download", errorCode);
        }
        JSONArray array = response.getJSONArray("list");
        int totalNum = Integer.parseInt(response.get("totalnum"));
//        int returnNum = Integer.parseInt(response.get("returnnum"));
        List<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < array.length(); i++) {
            list.add(array.getJSONObject(i).getInt("songid"));
        }
        data = new PendingFavoriteListData(totalNum, requesNum, list);
        return data;
    }

    //获取日志上传地址
    public DataCenterMessage requestLogUploadInfo() throws Exception {
        DataCenterMessage request = new DataCenterMessage();
        DataCenterMessage response = new DataCenterMessage();

        request.put("function", "log_url");
        request.put("suffix", "zip");
        response = sendMessage(request);

        String errorCode = response.get("errorcode");
        if (!errorCode.equals("0")) {
            throw new DataCenterCommuException("log_url", errorCode);
        }
//        checkErrorCode(errorCode);

        return response;
    }

    /**
     * [请求排行列表（精选集）]
     *
     * @return 歌单列表
     * @throws DCProtocolParseException 数据中心协议解析异常
     * @throws DCNoResultException      数据中心无返回值
     */
    public List<SongTop> requestSongTopList() throws Exception {
        DataCenterMessage request = new DataCenterMessage();
        DataCenterMessage response = new DataCenterMessage();

        request.put("function", "song_rank_list");
        request.put("companycode", "00002");

        response = sendMessage(request);

        if (SystemConfigManager.getInstance().isDebugVersion()) {
            EvLog.d("requestSongTopList : " + response.getContentString());
        }

        String errorCode = response.get("errorcode");
        String errorMessage = response.get("errormessage");

        if (!errorCode.equals("0")) {
            throw new DataCenterCommuException("song_rank_list", errorCode, errorMessage);
        }
        String picUrlHead = response.get("picurlhead");

        JSONObject rsJsonObject = new JSONObject(response.get("rs"));

        int returnNum = rsJsonObject.getInt("return");

        if (returnNum == 0) {
            throw new DCNoResultException("requestSongTopList");
        }

        JSONArray rJsonArray = rsJsonObject.getJSONArray("r");

        if (rJsonArray == null) {
            return Collections.emptyList();
        }

        int size = rJsonArray.length();
        // 解析歌单列表
        List<SongTop> songTopList = new ArrayList<SongTop>();
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = rJsonArray.getJSONObject(i);
            if (jsonObject == null) {
                continue;
            }

            int songTopId = jsonObject.getInt("id");
            String name = jsonObject.getString("s");
            int songTopTypeCode = 22;
            int fid = jsonObject.getInt("fid");
            String subTitle = jsonObject.getString("t");
            String songs = jsonObject.getString("songs");
            String singers = jsonObject.getString("singers");
            String imageUrl = picUrlHead + "?fileid=" + fid;
            SongTop songTop = new SongTop(songTopId, imageUrl, name, 0, 0, songTopTypeCode, subTitle, songs, singers);
            songTopList.add(songTop);
        }

        if (!songTopList.isEmpty()) {
            // 把歌单列表保存到数据库中
            SongTopManager.getInstance().saveSongTopDataToDB(songTopList);
            if (SystemConfigManager.getInstance().isDebugVersion()) {
                EvLog.d("zxs", "save SongTop =====>>>>" + songTopList);
            }
            // 更新时间戳
            KmSharedPreferences.getInstance().putLong(
                    KeyName.KEY_SONG_TOP_DATA_TIMESTAMP, System.currentTimeMillis());
        }
        //zxs
        //EvLog.d("zxs", songTopList.toString());
        return songTopList;
    }

    /**
     * [请求排行歌曲列表]
     *
     * @param startPos  申请起始记录数，0表示第1条
     * @param requesNum 申请的记录数，-1表示所有
     * @return 歌曲列表
     * @throws DCProtocolParseException 数据中心协议解析异常
     * @throws DCNoResultException      数据中心无返回值
     */
    public List<SongTopDetail> requestSongTopDetailsInfo(
            int songTopId, int startPos, int requesNum) throws Exception {

        DataCenterMessage request = new DataCenterMessage();
        DataCenterMessage response = new DataCenterMessage();

        request.put("function", "song_rank_detail");
        request.put("companycode", "00002");
        request.put("typeid", songTopId + "");
        request.put("startpos", startPos + "");
        request.put("requestnum", requesNum + "");

        if (SystemConfigManager.getInstance().isDebugVersion()) {
            EvLog.d("requestSongTopDetailsInfo getContentString:" + request.getContentString());
        }

        response = sendMessage(request);

        String errorCode = response.get("errorcode");
        String errorMessage = response.get("errormessage");

        if (!errorCode.equals("0")) {
            throw new DataCenterCommuException("song_rank_detail", errorCode, errorMessage);
        }

        JSONObject rsJsonObject = new JSONObject(response.get("rs"));

        int totalNum = Integer.valueOf(response.get("total"));
        EvLog.d("requestSongMenuDetailsInfo songMenuId: " + songTopId + " totalNum: " + totalNum);
        int returnNum = rsJsonObject.getInt("return");

        if (returnNum == 0) {
            throw new DCNoResultException("requestSongMenuDetailsInfo");
        }

        JSONArray rJsonArray = rsJsonObject.getJSONArray("r");

        if (rJsonArray == null) {
            return Collections.emptyList();
        }

        int size = rJsonArray.length();
        // 解析歌单歌曲列表
//        List<Song> songList = new ArrayList<Song>();
        boolean saveSongTopDetailsToDB = false;

        List<SongTopDetail> details = new ArrayList<SongTopDetail>();
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = rJsonArray.getJSONObject(i);
            if (jsonObject == null) {
                continue;
            }
            int id = jsonObject.getInt("id");
            String songName = jsonObject.getString("s");
            String singerName = jsonObject.getString("g");
            boolean score = jsonObject.getInt("p") == 1;

            int orderRate = 0;
            if (jsonObject.has("ordertimes")) {
                orderRate = jsonObject.getInt("ordertimes");
            }
            // song对象先从数据库中获取,没有再从网络获取
            Song song = SongManager.getInstance().getSongById(id);
            if (song == null) {
                song = requestSongInfo(id);
                if (song != null) {
                    SongManager.getInstance().add(song);
                }
            }
            if (song != null) {
//                songList.add(song);
                saveSongTopDetailsToDB = true;
            }

            if (song != null) {
                SongTopDetail detail = new SongTopDetail(songTopId, song.getId(), songName, singerName, score, orderRate);
                details.add(detail);
            } else {
                EvLog.e(id + " is not exist in db");
            }
        }

        SongTop songTop = SongTopManager.getInstance().getSongTopById(songTopId);
        if (songTop != null) {
            SongTopManager.getInstance().save(songTop);
        }

        // 保存歌单详情到数据库中并更新时间戳
        if (/*!songList.isEmpty()*/saveSongTopDetailsToDB && songTop != null) {
            SongTopDetailManager.getInstace().saveSongTopDetailsToDB(songTopId, details);

            // 在获取第一页数据时保存时间戳和歌曲总数,注意startPos从零开始。
            if (startPos == 0) {
                songTop.timestamp = System.currentTimeMillis();
                songTop.totalNum = totalNum;
                SongTopManager.getInstance().save(songTop);
            }
        }

        return details;
    }

    /**
     * [功能说明] 请求CDN测试点播链
     *
     * @return 点播链
     */
    public String requestCDNTestUrl() throws Exception {
        DataCenterMessage request = new DataCenterMessage();
        request.put("function", "http_esing");

        DataCenterMessage response = null;
        try {
            response = DataCenterCommu.getInstance().sendMessage(request);
        } catch (Exception e) {
            throw e;
        }
        String errorCode = response.get("errorcode");
        if (!errorCode.equals("0")) {
            throw new DataCenterCommuException("服务器返回ErrorCode:" + response.get("errormessage"));
        }

        String url = null;
        try {
            url = response.get("url");
        } catch (Exception e) {
            EvLog.e(e.getMessage());
            throw new DataCenterCommuException("获取测试url异常");
        }
        EvLog.i("TestURL", url);
        return url;
    }
}
