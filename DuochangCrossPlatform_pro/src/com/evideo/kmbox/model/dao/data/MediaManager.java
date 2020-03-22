package com.evideo.kmbox.model.dao.data;

import java.util.ArrayList;
import java.util.List;

import com.evideo.kmbox.dao.DAOFactory;
import com.evideo.kmbox.dao.MediaDAO;
import com.evideo.kmbox.dao.StorageVolumeDAO;
import com.evideo.kmbox.dao.StorageVolumeMediaDAO;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.EvLog;

public class MediaManager {
    
    private static MediaManager instance;
    
    private MediaManager() {
    }
    
    public static MediaManager getInstance() {
        if (instance == null) {
            synchronized (MediaManager.class) {
                if(instance == null) {
                    instance = new MediaManager();
                }
            }
        }
        return instance;
    }

    public Media getMedia(int id) {
        MediaDAO dao = DAOFactory.getInstance().getMediaDAO();
        return dao.getMediaById(id);
    }

    public void deleteMedia(int id) {
    	Media media = getMedia(id);
    	
    	if (media == null) {
    		return;
    	}

    	DAOFactory.getInstance().getMediaDAO().delete(id);
    	
    	StorageVolume volume = StorageManager.getInstance().getVolume(media.getVolumeUUID());
    	
    	if (volume != null && !volume.isInternalSDCard()) {
    		StorageVolumeMediaDAO dao = DAOFactory.getInstance().getStorageVolumeMediaDAO(volume.getPath());
        	if (dao == null) {
    			String msg = "Update Storage Volume Media failed:" + volume.getPath();
    			EvLog.e(msg);
    			UmengAgentUtil.reportError(msg);
    		}
        	
        	dao.delete(StorageVolumeMedia.fromMedia(media));
    	}
    }
    
    public void deleteMediasBySongId(int songId) {
        MediaDAO dao = DAOFactory.getInstance().getMediaDAO();
        Media media = null;
        List<Media> mediaList = dao.getMedia(songId);
        for (int i = 0; i < mediaList.size(); i++) {
        	media = mediaList.get(i);
        	
        	if (media == null) {
        		continue;
        	}

        	deleteMedia(media.getId());
        }
    }

    public List<Media> getMediaListBySong(int songId) {
        MediaDAO dao = DAOFactory.getInstance().getMediaDAO();
        return dao.getMedia(songId);
    }
    
    public void SyncWithStorageVolume() {
        StorageVolumeDAO dao = DAOFactory.getInstance().getStorageVolumeDAO();
        List<StorageVolume> list = dao.getList();
        if (list.size() == 0) {
            return;
        }
        
        List<String> uuids = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            uuids.add(list.get(i).getUUID());
        }

        MediaDAO mediaDAO = DAOFactory.getInstance().getMediaDAO();
        mediaDAO.syncWithUUID(uuids);
    }
    
    public boolean update(Media media) {
        if (media == null) {
            return false;
        }

        MediaDAO mediaDAO = DAOFactory.getInstance().getMediaDAO();
        boolean result = mediaDAO.update(media);
        
        if (!result) {
        	String msg = "update media failed:" + media.toString();
        	EvLog.e(msg);
        	UmengAgentUtil.reportError(msg);
        }

        return result;
    }
    
    public boolean save(List<Media> list) {
        
        if (list.size() == 0) {
            return true;
        }
        
        MediaDAO dao = DAOFactory.getInstance().getMediaDAO();
        dao.save(list);

        return true;
    }
    
    public boolean updateOnlineMedia(int songId, List<Media> list) {
        if (list.size() == 0) {
            return true;
        }
        
        MediaDAO dao = DAOFactory.getInstance().getMediaDAO();
        dao.updateOnlineMedia(songId, list);
        
        return true;
    }
    
    public boolean updateMediaBaseInfo(int songId, List<Media> list) {
        if (list.size() == 0) {
            return true;
        }

        MediaDAO dao = DAOFactory.getInstance().getMediaDAO();
        dao.updateMediaBaseInfo(songId, list);

        return true;
    }

    public void removeMediaByStorageVolume(StorageVolume volume) {
    	DAOFactory.getInstance().removeStorageVolumeMediaDAO(volume.getPath());
        MediaDAO dao = DAOFactory.getInstance().getMediaDAO();
        dao.removeMediaByStorageVolumeUUID(volume.getUUID());
    }
    
    public void clearList() {
        MediaDAO dao = DAOFactory.getInstance().getMediaDAO();
        dao.cleaList();
    }
    
    public int getCount() {
        MediaDAO dao = DAOFactory.getInstance().getMediaDAO();
        return dao.getCount();
    }
    
}
