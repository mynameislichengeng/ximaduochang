package com.evideo.kmbox.model.dao.data;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.evideo.kmbox.KmApplication;
import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.StorageVolumeDAO;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.presenter.LifoAsyncPresenter;
import com.evideo.kmbox.update.db.UpdateDbManager;
import com.evideo.kmbox.util.CommonUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.util.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StorageManager /*implements IExStorageObserver*/ {

    public static final long INTERNAL_STORAGE_RESERVED_CAPACITY = 20 * 1024 * 1024;

    /**
     * [第三方平台resource空间限制]
     */
    public static final long INTERNAL_STORAGE_LIMIT_ON_THIRD_PLATFORM = 512 * 1024 * 1024L;

//    private static final String USB_STORAGE_PREFIX = "/mnt/usb_storage";

    private static StorageManager instance = null;
    private List<StorageVolume> mStorageVolumeList = new ArrayList<StorageVolume>();
    private StorageVolumeUpdateSizePresenter mVolumeUpdateSizePresenter = null;

    public static StorageManager getInstance() {
        if (instance == null) {
            synchronized (StorageManager.class) {
                if (instance == null) {
                    instance = new StorageManager();
                }
            }
        }

        return instance;
    }

    private StorageManager() {
        // initStorageVolume();
    }

    private void initStorageVolumeAsThirdApp() {
        EvLog.i("begin initStorageVolumeAsThirdApp");
        String path = ResourceSaverPathManager.getInstance().getRootPath();

        StorageVolume volume = new StorageVolume(StorageManager.getUUID(path),
                StorageManager.getLabel(path), path);
        EvLog.i("StorageManager,start to add volume");
        volume.setScanned(true);
        if (!addVolume(volume)) {
            EvLog.i("StorageManager,add volume to db fail: already exists in db list");
            return;
        }
    }

    public void initStorageVolume(Context context) {
        EvLog.i("begin initStorageVolume");
        initStorageVolumeAsThirdApp();
        return;
    }

    public boolean hasEnoughSpace(long space) {
        long availableSize = 0;
        StorageVolume volume = null;
        List<StorageVolume> volumeList = getScannedVolumeList();

        for (int i = 0; i < volumeList.size(); i++) {
            volume = volumeList.get(i);
            long result = FileUtil.getAvailableSize(volume.getPath());

            if (result != -1) {
                availableSize += result;
            }
        }

        return availableSize >= space;
    }

    public StorageVolume getSuitableVolume(long expectedSize) {
        List<StorageVolume> list = new ArrayList<StorageVolume>(getScannedVolumeList());
        if (DeviceConfigManager.getInstance().isThirdApp()) {
            EvLog.d("thirdApp: getSuitableVolume, expectedSize:" + expectedSize + ",list.size=" + list.size());
            if (list.size() > 0) {
                return list.get(0);
            }
        }

        EvLog.d("start getSuitableDirectory, expectedSize:" + expectedSize + ",size=" + list.size());

        StorageVolume defaultVolume = null;

        for (int i = 0; i < list.size(); i++) {
            EvLog.e("getSuitableVolume: " + list.get(i));
            if (list.get(i).isInternalSDCard()) {
                defaultVolume = list.get(i);
            } else if (FileUtil.getAvailableSize(list.get(i).getPath()) > expectedSize) {
                EvLog.d("end getSuitableDirectory, external path:" + list.get(i).getResVideoPath());
                String directory = list.get(i).getResVideoPath();
                if (!FileUtil.isFileExist(directory)) {
                    FileUtil.mkdir(directory);
                    FileUtil.grantWriteReadAccess(directory);
                }
                return list.get(i);
            }
        }

        if (defaultVolume == null) {
            EvLog.e("internal sd card volume should not be null!!!");
            return null;
//            String path = StorageConstant.INTERNAL_SDCARD_ROOT;
//            defaultVolume = new StorageVolume(getUUID(path), StorageConstant.INTERNAL_SDCARD_LABEL, path);
        }

        // 保持/mnt/sdcard/kmbox/resouce 文件大小不超过1G
        // 对resource占用空间进行限制
        long resSize = FileUtil.countLength(defaultVolume.getResVideoPath());
        ContentResolver resolver = KmApplication.getInstance().getContentResolver();
        if (resolver != null) {
            boolean smartDbSwitch = true;
            long limitSize = StorageManager.getInstance().getStorageLimitSize();
            if (smartDbSwitch) {
                limitSize = StorageManager.getInstance().getStorageLimitSize();
            } else {
                limitSize = FileUtil.getTotalSize(defaultVolume.getPath()) -
                        StorageManager.getInstance().getStorageLimitSize();
            }
            if (resSize > limitSize) {
                EvLog.d("end getSuitableDirectory on third platform, resource is more than limit size");
                return null;
            }
        }

        // volume为/mnt/sdcard
        long availableSize = FileUtil.getAvailableSize(defaultVolume.getPath()) - INTERNAL_STORAGE_RESERVED_CAPACITY;

        if (availableSize >= expectedSize) {
            String path = defaultVolume.getResVideoPath();
            EvLog.d("end getSuitableDirectory, path = " + path);
            return defaultVolume;
        } else {
            EvLog.d("end getSuitableDirectory, availableSize < expectedSize,path null");
        }

        return null;
    }
    
    public StorageVolume getVolumeOfFile(String file) {
        if (file == null) {
            return null;
        }
        synchronized (mStorageVolumeList) {

            for (int i = 0; i < mStorageVolumeList.size(); ++i) {
                if (file.startsWith(mStorageVolumeList.get(i).getPath())) {
                    return mStorageVolumeList.get(i);
                }
            }

        }

        return null;
    }

    public StorageVolume getVolume(String uuid) {

        synchronized (mStorageVolumeList) {

            for (int i = 0; i < mStorageVolumeList.size(); ++i) {
                if (mStorageVolumeList.get(i).getUUID().equals(uuid)) {
                    return mStorageVolumeList.get(i);
                }
            }

        }

        return null;
    }

    public boolean addScannedVolume(StorageVolume volume) {
        // update to db
        StorageVolumeDAO dao = DAOFactory.getInstance().getStorageVolumeDAO();
        if (dao.isExist(volume)) {
            String msg = "a duplicate volume:" + volume.getLabel();
            EvLog.e(msg);
            StorageManager.getInstance().updateStorageVolumeSize(volume);
            UmengAgentUtil.reportError(msg);
            return false;
        } else {
            dao.add(volume);
        }
        StorageManager.getInstance().updateStorageVolumeSize(volume);
        volume.setScanned(true);

        return true;
    }

    public boolean isExistInDbByPath(String path) {
        String uuid = getUUID(path);
        if (uuid == null || uuid.length() == 0) {
            return false;
        }

        StorageVolumeDAO dao = DAOFactory.getInstance().getStorageVolumeDAO();
        return dao.isExist(uuid, path);
    }

    public boolean isExistInDbByUUID(String uuid) {
        if (uuid == null || uuid.length() == 0) {
            return false;
        }

        StorageVolumeDAO dao = DAOFactory.getInstance().getStorageVolumeDAO();
        return dao.isExist(uuid);
    }

    public void removeScannedVolume(StorageVolume volume) {
        synchronized (mStorageVolumeList) {
            Iterator<StorageVolume> iter = mStorageVolumeList.iterator();

            while (iter.hasNext()) {
                StorageVolume v = iter.next();
                if (v.getUUID().equals(volume.getUUID())) {
                    iter.remove();
                    break;
                }
            }
        }

        MediaManager.getInstance().removeMediaByStorageVolume(volume);
        DAOFactory.getInstance().getStorageVolumeDAO().remove(volume.getUUID());
    }

    public boolean isExistVolume(String uuid) {
        return getVolume(uuid) != null;
    }

    public List<StorageVolume> getScannedVolumeList() {

        List<StorageVolume> volumeList = new ArrayList<StorageVolume>();

        synchronized (mStorageVolumeList) {
            int size = mStorageVolumeList.size();

            for (int i = 0; i < size; i++) {
                if (mStorageVolumeList.get(i).isScanned()) {
                    volumeList.add(mStorageVolumeList.get(i));
                }
            }
        }

        return volumeList;
    }

    public List<StorageVolume> getUnscannedVolumeList() {

        List<StorageVolume> volumeList = new ArrayList<StorageVolume>();

        synchronized (mStorageVolumeList) {
            int size = mStorageVolumeList.size();

            for (int i = 0; i < size; i++) {
                if (!mStorageVolumeList.get(i).isScanned()) {
                    volumeList.add(mStorageVolumeList.get(i));
                }
            }
        }

        return volumeList;
    }

    private static synchronized String getUUID(String path) {
        String uuid = "";
        String uuidFileDir = FileUtil.concatPath(path, StorageConstant.STORAGE_VOLUME_KMBOX_ROOT);
        String uuidFilePath = FileUtil.concatPath(uuidFileDir, StorageConstant.STORAGE_VOLUME_UUID_FILE_NAME);

        File uuidFile = new File(uuidFilePath);

        if (uuidFile.exists()) {
            FileReader reader = null;
            BufferedReader br = null;
            try {
                reader = new FileReader(uuidFile);
                br = new BufferedReader(reader);
                uuid = br.readLine();

                if (uuid == null || uuid.isEmpty()) {
                    uuid = java.util.UUID.randomUUID().toString();
                }
            } catch (FileNotFoundException e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
            } catch (Exception e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
            } finally {
                CommonUtil.safeClose(br);
                CommonUtil.safeClose(reader);
            }
        } else {
            uuid = java.util.UUID.randomUUID().toString();
            OutputStream output = null;
            try {
                output = new FileOutputStream(uuidFilePath, false);
                output.write(uuid.getBytes());
            } catch (IOException e) {
                String msg = "StorageManager add scanned volume(" + uuidFilePath + ") failed:" + e.getMessage();
                EvLog.e(msg);
                UmengAgentUtil.reportError(msg);
            } finally {
                CommonUtil.safeClose(output);
            }
        }

        uuidFile = null;
        EvLog.i("StorageManager:path=" + path + ",uuid=" + uuid);
        return uuid;
    }

    private static String getLabel(String path) {
        String label = "unknown";
        int index = path.lastIndexOf("/");
        if (index != -1) {
            label = path.substring(index + 1);
        }

        return label;
    }

    public static String getMediaPath(String path) {
        return FileUtil.concatPath(path, StorageConstant.CONFIG_FILE_MEDIA_PATH);
    }

    public static String getSongPath(String path) {
        return FileUtil.concatPath(path, StorageConstant.CONFIG_FILE_SONG_PATH);
    }

    public static String getSingerPath(String path) {
        return FileUtil.concatPath(path, StorageConstant.CONFIG_FILE_SINGER_PATH);
    }

    public long getAvailableSize() {
        long freeSize = 0;
        List<StorageVolume> volumeList = getScannedVolumeList();
        if (volumeList != null && volumeList.size() > 0) {
            for (StorageVolume volume : volumeList) {
                long volumeFreeSize = FileUtil.getAvailableSize(volume.getPath());
                if (volumeFreeSize < 0) {
                    continue;
                }
                freeSize += volumeFreeSize;
            }
        }
        return freeSize;
    }

    public long getTotalSize() {
        long totalSize = 0;
        List<StorageVolume> volumeList = getScannedVolumeList();
        if (volumeList != null && volumeList.size() > 0) {
            for (StorageVolume volume : volumeList) {
                long volumeTotalSize = FileUtil.getTotalSize(volume.getPath());
                if (volumeTotalSize < 0) {
                    continue;
                }
                totalSize += volumeTotalSize;
            }
        }
        return totalSize;
    }

    private boolean mRegisted = false;
    private List<KmStorageEventListener> mListeners = new ArrayList<KmStorageEventListener>();

    public void registerListener(KmStorageEventListener listener) {
        if (listener == null) {
            return;
        }

        synchronized (this.mListeners) {
            this.mListeners.add(listener);
        }
    }

    public void unregisterListener(KmStorageEventListener listener) {
        if (listener == null) {
            return;
        }

        synchronized (this.mListeners) {
            int size = this.mListeners.size();
            for (int i = 0; i < size; i++) {
                KmStorageEventListener l = mListeners.get(i);
                if (l == listener) {
                    mListeners.remove(i);
                    break;
                }
            }

            if ((mListeners.size() == 0) && mRegisted) {
                // FIXME disable storage event
                /*if(isStorageEventOpen()) {
                    ExStorageSubject.getInstance().unregistExStorageObserver(this);
                }*/
                mRegisted = false;
            }
        }
    }

    public boolean isStorageEventOpen() {
        return KmSharedPreferences.getInstance().getBoolean(KeyName.KEY_STORAGE_EVENT_SWITCH, false);
    }

    public boolean setIsStorageEventOpen(boolean isOpen) {
        return KmSharedPreferences.getInstance().putBoolean(KeyName.KEY_STORAGE_EVENT_SWITCH, isOpen);
    }

    public void notifyListeners(StorageVolume volume, int event) {
        int size = mListeners.size();

        for (int i = 0; i < size; i++) {
            mListeners.get(i).onKmStorageEvent(volume, event);
        }
    }
    
    private boolean addVolume(StorageVolume volume) {
        synchronized (mStorageVolumeList) {
            Iterator<StorageVolume> iter = mStorageVolumeList.iterator();
            StorageVolume v = null;
            while (iter.hasNext()) {
                v = iter.next();
                EvLog.i("get already exists Volume UUID:" + v.getUUID());
                if (v.getUUID().equals(volume.getUUID())) {
                    EvLog.i("exists volume UUID in list: " + v.getUUID() + "if equals target volume??" + volume.getUUID());
                    return false;
                }
            }

            mStorageVolumeList.add(volume);
            EvLog.i("taget UUID " + volume.getUUID() + "add to list success");
        }

        EvLog.i("add storage:" + volume.getPath() + ", " + volume.getUUID());

        return true;
    }

    /**
     * [检测是否是支持的歌曲格式文件]
     *
     * @param fileName 文件名
     * @return true是 false不是
     */
    private static boolean checkFormat(String fileName) {

        if (fileName == null || fileName.isEmpty()) {
            return false;
        }

        int lastIndex = fileName.lastIndexOf(".");
        //不能在最前面，或最后一位。
        if (lastIndex <= 0 || lastIndex >= fileName.length() - 1) {
            return false;
        }

        fileName = fileName.substring(fileName.lastIndexOf(".") + 1);

        boolean result = isSupport(UpdateDbManager.STB_SUPPORT_SONG_FORMAT, fileName);

        return result;
    }

    private static String getName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        char standard = '.';
        int index = 0;
        for (int i = 0; i < fileName.length(); i++) {
            if (fileName.charAt(i) == standard) {
                // 确保是最后一个“.”
                index = i;
            }
        }
        // 获取文件名
        fileName = fileName.substring(0, index);

        return fileName;
    }

    // String数组是否还有 String数据 s
    private static boolean isSupport(String[] strs, String s) {
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].indexOf(s) != -1) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSongNameDigit(String name) {
        if (name.length() != WholeSongManager.EVIDEO_NUMBER_SONG_LENGTH
                && name.length() != WholeSongManager.EVIDEO_NUMBER_SONG_LENGTH + 1) {
            EvLog.i("evideo number song require 8/9 place");
            return false;
        }

        for (int i = name.length(); --i >= 0; ) {
            if (!Character.isDigit(name.charAt(i))) {
                EvLog.i("Non pure number");
                return false;
            }
        }

        return true;
    }

    /*@Override
    public void onExStorageRemoved(String storagePath) {
        EvLog.i("storage removed:" + storagePath);
    	Message msg = Message.obtain();
    	msg.obj = storagePath;
    	msg.what = 1;
    	mHandler.sendMessageDelayed(msg, 10 * 1000);
    }*/

    public static class KmStorageReceiver extends BroadcastReceiver {

        private static final String ACTION = "com.evideo.kmbox.kmstorage";
        private static final String KEY_ROOT_PATH = "root_path";
        private static final String KEY_NAME = "name";

        public KmStorageReceiver() {
            super();
        }

        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (!ACTION.equals(intent.getAction())) {
                return;
            }

            String rootPath = intent.getStringExtra(KEY_ROOT_PATH);
            String name = intent.getStringExtra(KEY_NAME);

            File file = new File(rootPath);
            File[] files = file.listFiles();

            List<StorageVolume> volumeList = new ArrayList<StorageVolume>();
            for (int i = 0; i < files.length; i++) {
                String path = files[i].getPath();
                if (StorageManager.getInstance().isExistInDbByPath(path)) {
                    continue;
                }
                StorageVolume volume = new StorageVolume(StorageManager.getUUID(path),
                        StorageManager.getLabel(path), path);

                StorageManager.getInstance().addVolume(volume);
                StorageManager.getInstance().addScannedVolume(volume);
                // new StorageVolumeScannerPresenter(volume).start();
                StorageManager.getInstance().notifyListeners(volume, KmStorageEventListener.StorageEvent_Mounted);
                EvLog.i("StorageManager:add volume:" + path);
                volumeList.add(volume);
            }
        }
    }

    public void updateStorageVolumeSize(StorageVolume volume) {
        if (volume == null) {
            return;
        }
        /*if (mVolumeUpdateSizePresenter != null
                && mVolumeUpdateSizePresenter.isRunning()) {
            return;
        } else */
        if (mVolumeUpdateSizePresenter != null) {
            mVolumeUpdateSizePresenter.cancel();
            mVolumeUpdateSizePresenter = null;
        }

        mVolumeUpdateSizePresenter = new StorageVolumeUpdateSizePresenter();
        mVolumeUpdateSizePresenter.setRunning(true);
        mVolumeUpdateSizePresenter.start(volume);
    }

    private static class StorageVolumeUpdateSizePresenter extends LifoAsyncPresenter<Long> {

        private static boolean isRunning = false;

        @Override
        protected Long doInBackground(Object... params)
                throws Exception {

            StorageVolume volume = (StorageVolume) params[0];
            if (volume == null || volume != null && TextUtils.isEmpty(volume.getUUID())) {
                setRunning(false);
                return null;
            }
            long size = execSizeShell(volume);
            // update to db
            StorageVolumeDAO dao = DAOFactory.getInstance().getStorageVolumeDAO();
            if (dao != null) {
                dao.updateStorageVolumeResSize(volume.getUUID(), size);
            }
            return size;
        }

        public void setRunning(boolean isrunnig) {
            isRunning = isrunnig;
        }

        public boolean isRunning() {
            return isRunning;
        }

        public synchronized static long execSizeShell(StorageVolume volume) {
            long size = 0;
            if (volume == null || volume != null && TextUtils.isEmpty(volume.getUUID())) {
                return size;
            }
            File file = new File(FileUtil.concatPath(volume.getPath(),
                    StorageConstant.STORAGE_VOLUME_KMBOX_VIDEO_ROOT));

            if (!file.exists()) {
                return size;
            }
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles != null && childFiles.length <= 0) {
                return size;
            }
            for (int i = 0; i < childFiles.length; i++) {
                if (UpdateDbManager.isMediaFileSupport(childFiles[i])) {
                    size += FileUtil.countLength(childFiles[i].getAbsolutePath());
                }
            }
            return size;
        }

        @Override
        protected void onCompleted(Long result, Object... params) {
            setRunning(false);
        }

        @Override
        protected void onFailed(Exception exception, Object... params) {
            setRunning(false);
            EvLog.e(exception.getMessage());
            UmengAgentUtil.reportError(exception);
        }

    }

    public long getStorageLimitSize() {
        return StorageManager.INTERNAL_STORAGE_LIMIT_ON_THIRD_PLATFORM;
    }

}
