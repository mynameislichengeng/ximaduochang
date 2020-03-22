package com.evideo.kmbox.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.evideo.kmbox.util.ShellUtils.CommandResult;

public class DeviceMountUtil {
	public static final String TAG = DeviceMountUtil.class.getSimpleName();
//	private static final boolean DEBUG = true;
	
	public static final String MOUNTPROCFILE = "/proc/mounts";
	public static final String K20_DISK_SYSFS_MOUNT_POINT = "/sys/devices/platform/usb20_host/usb2/2-1/2-1.2";
	public static final String USB_STORAGE_K20_POINT_PREFIX = "/mnt/usb_storage";
	
	public String mountlable;
	public String mountpoint;
	
	public static boolean checkMounted(String devicename){
		File file = new File(devicename);
		return file.exists();
	}
	
    /**
     * [功能说明] 挂载路径
     * @param string sysfs
     * @param mountPoint mountpoint
     * @return
     */
    public static String getMountPoint(String string, String mountPoint) {
        File file = new File(string);
        if (file.exists()) {
            List<DeviceMountUtil> list = getMountsPoint();
            for (DeviceMountUtil info : list) {
                if (info.mountpoint.startsWith(mountPoint)) {
                    String name = "/sys/dev/block/"
                            + new File(info.mountlable).getName();

                    //Log.d(TAG, "mount label is: " + name);

                    String CMD = "busybox realpath " + name;
                    String retmsg = CMD + " error: ";
                    CommandResult ret = ShellUtils.execCommand(CMD, false);
                    if (ret != null) {
                        if (ret.result == 0 && !TextUtils.isEmpty(ret.successMsg)) {
                            if (ret.successMsg.startsWith(string)) {
                                return info.mountpoint;
                            }
                        }
                        retmsg += ret.errorMsg; 
                    }
                    EvLog.e(retmsg);
                }
            }
        }
        return "";
    }
    
    /**
     * [功能说明] 设备挂载路径和节点信息
     * @param string sysfs
     * @param mountPoint mountpoint
     * @return
     */
    public static DeviceMountUtil getDeviceInfo(String string, String mountPoint) {
        File file = new File(string);
        if (file.exists()) {
            List<DeviceMountUtil> list = getMountsPoint();
            for (DeviceMountUtil info : list) {
                if (info.mountpoint.startsWith(mountPoint)) {
                    String name = "/sys/dev/block/"
                            + new File(info.mountlable).getName();
                    //Log.d(TAG, "mount label is: " + name);
                    String CMD = "busybox realpath " + name;
                    String retmsg = CMD + " error: ";
                    CommandResult ret = ShellUtils.execCommand(CMD, false);
                    if (ret != null) {
                        if (ret.result == 0 && !TextUtils.isEmpty(ret.successMsg)) {
                            if (ret.successMsg.startsWith(string)) {
                                return info;
                            }
                        }
                        retmsg += ret.errorMsg; 
                    }
                    EvLog.e(retmsg);
                }
            }
        }
        return null;
    }
    
    /**
     * [功能说明] 获取proc/mount信息
     * @return
     */
    public static List<DeviceMountUtil> getMountsPoint() {
        List<DeviceMountUtil> mounts = new ArrayList<DeviceMountUtil>();
        String CMD = "busybox grep '' /proc/mounts | busybox awk '{print $1,$2\"|\"}'";
        String retmsg = CMD + " error: ";
        CommandResult ret = ShellUtils.execCommand(CMD, false);
        if (ret != null) {
            if (ret.result == 0) {
                if (!TextUtils.isEmpty(ret.successMsg)) {
                    String[] lines = ret.successMsg.split("\\|");
                    for (int i = 0; i < lines.length; i++) {
                        String[] parts = lines[i].split(" ");
                        /*if (parts.length < 6) {
                            continue;
                        }*/
                        DeviceMountUtil mount = new DeviceMountUtil();
                        mount.mountlable = parts[0];
                        mount.mountpoint = parts[1];

                        /*EvLog.i("DeviceUtil",
                                "Parser factory xml file --- mountPoint is: "
                                        + parts[1]);*/

                        mounts.add(mount);
                    }
                    return mounts;
                }
            }
            retmsg += ret.errorMsg; 
        }
        EvLog.e(retmsg);
        return mounts;
    }
    
    public static String getK20StataMountPoint() {
        return DeviceMountUtil.getMountPoint(DeviceMountUtil.K20_DISK_SYSFS_MOUNT_POINT,
                DeviceMountUtil.USB_STORAGE_K20_POINT_PREFIX);
    }
    
    public static DeviceMountUtil getK20StataDeviceInfo() {
        return DeviceMountUtil.getDeviceInfo(DeviceMountUtil.K20_DISK_SYSFS_MOUNT_POINT,
                DeviceMountUtil.USB_STORAGE_K20_POINT_PREFIX);
    }
    
    public static boolean isMountedSdcard() {
        String cmd = "busybox grep  '/mnt/sdcard' /proc/mounts | busybox awk '{print $2\"|\"}'";
        String retmsg = "error read k20 sdcard mount: ";
        CommandResult ret = ShellUtils.execCommand(cmd, false);
        if (ret != null) {
            if (ret.result == 0 && !TextUtils.isEmpty(ret.successMsg)) {
                String[] lines = ret.successMsg.split("\\|");
                if (lines == null) {
                    return false;
                }
                for (int i = 0; i < lines.length; i++) {
                    if (lines[i].equals("/mnt/sdcard")) {
                        return true;
                    }
                }
                return false;
            }
            retmsg += ret.errorMsg; 
        }
        EvLog.e(retmsg);
        return false;
    }
	
	private static final String usbserial = "/sys/bus/usb-serial/devices";
	public static String isSerialExsist(String device){
		File file = new File(usbserial);
		if(file.isDirectory()){
			for(File f:file.listFiles()){
				BufferedReader reader = null;
				String CMD = "busybox realpath "+f.getAbsolutePath();
				try {
					Process process = Runtime.getRuntime().exec(CMD);
					if(process.waitFor() == 0){
						reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
						String path1 = "";
						String line;
						while((line = reader.readLine()) != null){
							path1 += line;
						}
						if(path1.startsWith(device)){
							return "/dev/"+f.getName();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally{
				    if (reader != null) {
				        try {
	                        reader.close();
	                    } catch (IOException e) {
	                    }
				    }
				}
			}
		}
		return "";
	}
	
}
