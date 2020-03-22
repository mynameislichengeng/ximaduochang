package com.evideo.kmbox.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.zip.ZipInputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import android.content.Context;
import android.os.StatFs;
import android.text.TextUtils;

import com.evideo.kmbox.KmApplication;
import com.evideo.kmbox.model.device.DeviceConfigManager;
import com.evideo.kmbox.model.device.ResourceSaverPathManager;
import com.evideo.kmbox.model.umeng.UmengAgentUtil;
import com.evideo.kmbox.util.ShellUtils.CommandResult;

/**
 * @brief : 文件类工具
 */
public class FileUtil {

    public static final int FIXED_MD5_LENGTH = 3 * 1024 * 1024;

     /**  
     * 复制整个文件夹内容  
     * @param oldPath  String 原文件路径 如：c:/fqf  
     * @param newPath  String 复制后路径 如：f:/fqf/ff  
     * @return boolean  
     */ 
    public static void copyFolder(String oldPath, String newPath) {  
        FileInputStream input = null;
        FileOutputStream output = null;
        try {  
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹  
            grantWriteReadAccess(newPath);
            File a = new File(oldPath);  
            String[] file = a.list();  
            File temp = null;  
            for (int i = 0; i < file.length; i++) {  
                if (oldPath.endsWith(File.separator)) {  
                    temp = new File(oldPath + file[i]);  
                } else {  
                    temp = new File(oldPath + File.separator + file[i]);  
                }  
 
                if (temp.isFile()) {  
                    input = new FileInputStream(temp);  
                    output = new FileOutputStream(newPath  
                            + "/" + (temp.getName()).toString());  
                    final byte[] b = new byte[1024 * 4];  
                    int len;  
                    while ((len = input.read(b)) != -1) {  
                        output.write(b, 0, len);  
                    }  
                    output.flush();  
                    output.close();  
                    input.close();  
                }  
                if (temp.isDirectory()) {// 如果是子文件夹  
                    copyFolder(oldPath + "/ " + file[i], newPath + "/ " 
                            + file[i]);  
                }  
            }  
        } catch (Exception e) {  
            System.out.println("复制整个文件夹内容操作出错 ");  
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        }  finally {
            CommonUtil.safeClose(input);
            CommonUtil.safeClose(output);
        }
    }  

    /**管道拷贝
     * @param sourcePath 全路径文件名
     * @param destPath 全路径文件名
     * @throws IOException
     */
    public static void copyFileUsingFileStream(String sourcePath,
            String destPath) throws IOException {
        File source = new File(sourcePath);
        File dest = new File(destPath);
        if (source == null || dest == null) {
            EvLog.e("fileutil", "args is error!");
            return;
        }

        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        FileInputStream sourceIn = null;
        FileOutputStream destOut = null;
        try {
            sourceIn = new FileInputStream(source);
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(sourceIn);
            destOut = new FileOutputStream(dest);
            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(destOut);
            // 缓冲数组
            byte[] b = new byte[1024];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
           CommonUtil.safeClose(sourceIn);
           CommonUtil.safeClose(destOut);
           CommonUtil.safeClose(inBuff);
           CommonUtil.safeClose(outBuff);
        }
    }

    /**
     * [功能说明] 移动文件到指定目录下
     * @param toDir 全路径目录
     * @param file 全路径文件名
     */
    public static void mvFileToDir(String toDir, String file) {
        File todirFile = new File(toDir);
        File pathFile = new File(file);
        if (todirFile == null || pathFile == null) {
            EvLog.e("fileutil", "args is error!");
            return;
        }
        if (!todirFile.exists()) {
            todirFile.mkdirs();
            grantWriteReadAccess(todirFile.getAbsolutePath());
        }
        if (todirFile.isFile()) {
            EvLog.e("fileutil", "file exist same file name");
            return;
        }
        if (!pathFile.exists() || pathFile.isDirectory()) {
            EvLog.e("fileutil","file is not exist");
            return;
        }
        if (pathFile.renameTo(new File(toDir + "/" +pathFile.getName()))) {
            EvLog.i("fileutil", "move "+ file + " success");
        } else {
            EvLog.e("fileutil", "move "+ file + " fail");
        }
    }

    public static long getFileSize(String fileName) {
        long fileSize = -1;
        File file = new File(fileName);
        if (file.exists()) {
            fileSize = file.length();
        } 
        file = null;
        return fileSize;
    } 
    public static boolean isFileComplete(String filename, long fileExpectSize) {
        boolean result = false;
        File file = new File(filename);
        if (file.exists()) {
            if ( file.length() == fileExpectSize ) {
                result = true;
            }
        }
        file = null;
        return result;
    }
    
    public static boolean isFileExist(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static File reFileName(File file, String rename) {
        if (file != null) {
            if (file.exists()) {
                String c = file.getParent();
                File refile = new File(c + File.separator + rename);
                if (file.renameTo(refile)) {
                    EvLog.i("FileUtil", "修改成功!");
                    file.deleteOnExit();
                    return refile;
                }
            }
        }
        EvLog.i("FileUtil", "修改失败");
        return null;
    }
    
    public static boolean renameFileWithPath(String sourcePath,String destPath) {
        if (TextUtils.isEmpty(sourcePath) || TextUtils.isEmpty(destPath)) {
            return false;
        }
        boolean ret =  false;
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            return ret;
        }
        
//        String c = sourceFile.getParent();
        File refile = new File(destPath);
        if (sourceFile.renameTo(refile)) {
            EvLog.i("FileUtil", "rename success");
            sourceFile.deleteOnExit();
            ret = true;
        } else {
            EvLog.e("FileUtil", "rename failed!");
        }
        sourceFile = null;
        refile = null;
        return true;
    }
    
    public static boolean renameFile(String sourcePath,String destName) {
        if (sourcePath == null || destName == null) {
            return false;
        }
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            return false;
        }
        
        String c = sourceFile.getParent();
        File refile = new File(c + File.separator + destName);
        if (sourceFile.renameTo(refile)) {
            EvLog.i("FileUtil", "rename success");
            sourceFile.deleteOnExit();
        } else {
            EvLog.e("FileUtil", "rename failed!");
        }
        return true;
    }
    
    public static boolean renameFile(String sourcePath,String destName,boolean delOldFile) {
        if (sourcePath == null || destName == null) {
            return false;
        }
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            return false;
        }
        
        String c = sourceFile.getParent();
        File refile = new File(c + File.separator + destName);
        if (sourceFile.renameTo(refile)) {
            EvLog.i("FileUtil", "rename success");
            if (delOldFile) {
                sourceFile.deleteOnExit();
            }
        } else {
            EvLog.e("FileUtil", "rename failed!");
        }
        return true;
    }
    

    public static void deleteFile(String fileName) {
        if ( fileName == null )
            return;
        
        File file = new File(fileName);
        if (file.exists()) {
            EvLog.d("delete file " + fileName);
            file.delete();
        } else {
            EvLog.d(fileName + " is not exist");
        }
        return;
    }

    public static void emptyDir(String dir) {
        EvLog.e(dir + " to del ");
        File file = new File(dir);

        if ( file.exists() ) {
            if (file.isDirectory()) { // 如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    files[i].delete(); // 把每个文件 用这个方法进行迭代
                }
            }
        }
    }
    
    public static boolean isDirEmpty(String dir) {
        File file = new File(dir);

        if ( file.exists() ) {
            if (file.isDirectory()) { // 如果它是一个目录
                return !(file.listFiles().length > 0); // 声明目录下所有的文件 files[];
            }
        }
        return true;
    }
    
    public static void mkdir(String dir) {
        File file =new File(dir);    
        if (!file.exists() && !file.isDirectory()) {       
            file.mkdir();
//            grantWriteReadAccess(file.getAbsolutePath());
        }  
    }
    
    public static void grantWriteReadAccess(String abspath) {
        if (TextUtils.isEmpty(abspath)) {
            EvLog.e("path is null");
            return;
        }
        String cmd = "busybox chmod a+rw -R " + abspath;
        String retmsg = "error chmod : ";
        EvLog.i(cmd);
        CommandResult ret = ShellUtils.execCommand(cmd, false);
        if (ret != null) {
            if (ret.result == 0) {
                return;
            }
            retmsg += ret.errorMsg; 
        }
        EvLog.e(retmsg);
    }
    
    public static void deleteDir(String dir) {
        File file = new File(dir);

        if ( file.exists() ) {
            if (file.isDirectory()) { // 如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    files[i].delete(); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        }
    }

  /*  public static String getTtftpPath() {
        String tftppath = FileUtil.concatPath(StorageConstant.INTERNAL_SDCARD_RES, 
                StorageConstant.INTERNAL_TFTP_ROOT+"/");
        try {
            if (!(new File(tftppath).isDirectory())) {
                new File(tftppath).mkdirs();
            }
        } catch (SecurityException e) {
            EvLog.e(e.getMessage());
            UmengAgentUtil.reportError(e);
        }
        return tftppath;
    }*/
    
    /**
     * @brief : [删除指定文件夹下的所有文件]
     * @param path
     */
    public static void deleteAllFiles(String path) {
        if(path == null) {
            return;
        }
        EvLog.i("deleteAllFiles in " + path);
        File file = new File(path);
        if(!file.exists() || !file.isDirectory()) {    //文件不存在或不是目录
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            
            if(path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if(temp.isFile()) {
                EvLog.d("del file: " + temp.getName());
                temp.delete();
            } else if(temp.isDirectory()) {
                deleteAllFiles(temp.getAbsolutePath());
                EvLog.d("del dir:" + file.getName());
                temp.delete();
            }
            
        }
    }
    
    /**
     * @brief : [计算该路径对应文件或目录的长度，文件或目录不存在返回-1]
     * 使用单线程递归方式计算
     * @param path
     * @return
     */
    public static long countLength(String path) {
        if(path == null) {
            return -1;
        }
        
        File file = new File(path);
        if(file.exists()) {
            if(file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0;
                for(File f : children) {
                    size += countLength(f.getAbsolutePath());
                }
                return size;
            } else if (file.isFile()) {
                return file.length();
            }
        }
        
        return -1;
    }
    
    /**
     * @brief : [获取可用空间大小]
     * @param path
     * @return
     */
    public static long getAvailableSize(String path) {
        File file = new File(path);
        if(!file.exists() || !file.isDirectory()) {
            return -1;
        }
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        return blockSize * availableBlocks;
    }
    /**
     * @brief : [获取格式化后的可用空间大小]
     * @param context
     * @param path
     * @return
     */
    public static String getAvailableFormatSize(Context context, String path) {
        long size = getAvailableSize(path);
        if(size < 0) {
            return null;
        }
        return FileSizeFormatter.formatFileSize(context, size);
    }
    
    /**
     * @brief : [获取总大小]
     * @param path
     * @return
     */
    public static long getTotalSize(String path) {
        File file = new File(path);
        if(!file.exists() || !file.isDirectory()) {
            return -1;
        }
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSize();
        long blockCount = statFs.getBlockCount();
        return blockSize * blockCount;
    }
    /**
     * @brief : [获取格式化后的总大小]
     * @param context
     * @param path
     * @return
     */
    public static String getTotalFormatSize(Context context, String path) {
        long size = getTotalSize(path);
        if(size < 0) {
            return null;
        }
        return FileSizeFormatter.formatFileSize(context, size);
    }
    
    /**
     * @brief : [组合路径]
     * @param path
     * @param append
     * @return
     */
    public static String concatPath(String path, String append) {
        if(path == null) {
            return null;
        }
        StringBuffer temp = new StringBuffer(path);
        if(path.endsWith(File.separator)) {
            temp.append(append);
        } else {
            temp.append(File.separator + append);
        }
        return temp.toString();
    }

    /**
     * @brief : [压缩文件夹内的文件 ]
     * @param zipFilePath 需要压缩的文件夹名
     * @param newzipFileName
     * @throws IOException 
     */
    public static void doZip(String zipFilePath, String newzipFileName) throws IOException{
        if (!FileUtil.isFileExist(zipFilePath)) {
            return;
        }
        
        File zipFile = new File(zipFilePath); 
        String zipFileName = zipFile.getParent() + "/"+ newzipFileName;//压缩后生成的zip文件名 
        FileOutputStream os = null;
        ZipOutputStream zipOut = null;    //压缩Zip 
        try {
            os = new FileOutputStream(zipFileName);
            zipOut = new ZipOutputStream(new BufferedOutputStream(os));
            handleDir(zipFile, zipOut);
        } finally {
            CommonUtil.safeClose(os);
            CommonUtil.safeClose(zipOut);
        }
    } 
    
    //由doZip调用,递归完成目录文件读取 
    private static void handleDir(File dir , ZipOutputStream zipOut) throws IOException{ 
       
        File[] files = null; 
        byte[] buf = new byte[512]; 
        int    readedBytes; 

        if (dir.isDirectory()) {
            files = dir.listFiles(); 
        } else {
            files = new File[1];
            files[0] = dir; 
        }
    
        FileInputStream fileIn = null; 
        try {
            if(files.length == 0){//如果目录为空,则单独创建之. 
                //ZipEntry的isDirectory()方法中,目录以"/"结尾. 
                zipOut.putNextEntry(new ZipEntry(dir.toString() + "/")); 
                zipOut.closeEntry(); 
            } 
            else{//如果目录不为空,则分别处理目录和文件. 
                for(File fileName : files){ 
                    if(fileName.isDirectory()){ 
                        handleDir(fileName , zipOut); 
                    } 
                    else{ 
                        fileIn = new FileInputStream(fileName); 
                        zipOut.putNextEntry(new ZipEntry(fileName.getName())); 

                        while((readedBytes = fileIn.read(buf))>0){ 
                            zipOut.write(buf , 0 , readedBytes); 
                        } 
                        zipOut.closeEntry(); 
                        CommonUtil.safeClose(fileIn);
                    } 
                } 
            } 
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CommonUtil.safeClose(fileIn);
        }
    }
    
    /**
     * 解压缩文件到指定的目录.
     * 
     * @param unZipfileName 需要解压缩的文件
     * @param mDestPath 解压缩后存放的路径
     */
    public static void unZip(String unZipfileName, String mDestPath) {
        if (!mDestPath.endsWith("/")) {
            mDestPath = mDestPath + "/";
        }

        FileOutputStream fileOut = null;
        ZipInputStream zipIn = null;
        java.util.zip.ZipEntry zipEntry = null;
        File file = null;
        int readedBytes = 0;
        byte buf[] = new byte[4096];
        FileInputStream fileIn = null;
        try {
            fileIn = new FileInputStream(unZipfileName);
            zipIn = new ZipInputStream(new BufferedInputStream(fileIn));
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                file = new File(mDestPath + zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    // 如果指定文件的目录不存在,则创建之.
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    fileOut = new FileOutputStream(file);
                    while ((readedBytes = zipIn.read(buf)) > 0) {
                        fileOut.write(buf, 0, readedBytes);
                    }
                    CommonUtil.safeClose(fileOut);
                }
                zipIn.closeEntry();
            }
        } catch (IOException ioe) {
            EvLog.e(ioe.getMessage());
            UmengAgentUtil.reportError(ioe);
        } finally {
            CommonUtil.safeClose(fileIn);
            CommonUtil.safeClose(fileOut);
        }
    }
    
    /**
     * [获取文件的MD5值] ,有缺陷，暂时少用
     * @param file 文件路径
     * @return MD5值
     * @throws FileNotFoundException 文件未找到异常
     */
    public static String getMd5ByFile(File file) throws Exception {
        String value = null;
        FileInputStream in = new FileInputStream(file);
        FileChannel ch = null;
        MappedByteBuffer byteBuffer = null;
        
        try {
            long size = Math.min(file.length(), FIXED_MD5_LENGTH);
//          MappedByteBuffer byteBuffer = in.getChannel().map(
//                    FileChannel.MapMode.READ_ONLY, 0, file.length());
            ch = in.getChannel();
            byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, size);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                    in = null;
                    file = null;
                } catch (IOException e) {
                    EvLog.e(e.getMessage());
                    UmengAgentUtil.reportError(e);
                }
            }
            if (ch != null) {
                try {
                    ch.close();
                    ch = null;
                } catch (IOException e) {
                    EvLog.e(e.getMessage());
                    UmengAgentUtil.reportError(e);
                }
            }
            if (byteBuffer != null) {
                byteBuffer.clear();
                byteBuffer = null;
            }
        }
        return value;
    }
   /* public static void clean(final Object buffer) throws Exception {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                try {
                    Method getCleanerMethod = buffer.getClass().getMethod(
                            "cleaner", new Class[0]);
                    getCleanerMethod.setAccessible(true);
                    Cleaner cleaner = (Cleaner) getCleanerMethod.invoke(buffer,
                            new Object[0]);
                    cleaner.clean();
                } catch (Exception e) {
                    EvLog.e(e.getMessage());
                    UmengAgentUtil.reportError(e);
                }
                return null;
            }
        });

    }*/
    
    static MessageDigest MD5 = null;

    static {
        try {
        MD5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ne) {
            EvLog.e(ne.getMessage());
            UmengAgentUtil.reportError(ne);
        }
    }


    /**
     * 对一个文件前3M获取md5值
     * @return md5串
     */
    public static String getMD5(File file) throws Exception{
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8*1024];
            int length;
            int size = 0;
            while (((length = fileInputStream.read(buffer)) != -1)
                    && size < FIXED_MD5_LENGTH) {
                MD5.update(buffer, 0, length);
                size += length;
            }
            return new String(Hex.encodeHex(MD5.digest()));
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
                    fileInputStream = null;
            } catch (IOException e) {
                EvLog.e(e.getMessage());
                UmengAgentUtil.reportError(e);
            }
        }
    }
    
    public static String getFileExt(String filePath) {
        if (filePath == null) {
            return null;
        }

        int pos = filePath.indexOf(".");
        if (pos < 0 || pos >= filePath.length()) {
            return null;          
        }
        
        return filePath.substring(filePath.indexOf(".")+1);
    }
    /**
     * [功能说明] 获取resource目录下文件
     * @return
     */
    public static File[] scanKmResourceFile(String path) {
        if (TextUtils.isEmpty(path)) {
            path = ResourceSaverPathManager.getInstance().getResourceSavePath()/*KmConfig.RESOURCE_SAVE_PATH*/; 
        }
        File file = new File(path);
        if (file == null || file != null && !file.exists()) {
            return null;
        }
        return file.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.isFile()) {
                    if (pathname.getName().trim().toLowerCase().endsWith(".ts")
                            || pathname.getName().trim().toLowerCase().endsWith(".erc")
                            || pathname.getName().trim().toLowerCase().endsWith(".mp3")
                            || pathname.getName().trim().toLowerCase().endsWith(".aacb")
                            || pathname.getName().trim().toLowerCase().endsWith(".aacy")
                            || pathname.getName().trim().toLowerCase().endsWith(".ts.tmp")) {
                        return true;
                    }
                }
                return false;
            }
        });
    }
    public static String getFileNameByPath(String path) {
        String name = "";
        if (TextUtils.isEmpty(path)) {
            return name;
        }
        File file = new File(path);
        if (file != null && file.exists()) {
            name = file.getName();
        }
        return name;
    }

    /**
     * 拷贝assert文件到特定目录下
     * 
     * @param strOutFileName
     * @param assetsFileName
     * @throws IOException
     */
    public static void copyDataToSD(String strOutFileName, String assetsFileName)
            /*throws IOException*/ {
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            myInput = KmApplication.getInstance().getAssets().open(assetsFileName);
            createFile(strOutFileName);
            if (myInput != null) {
                myOutput = new FileOutputStream(strOutFileName);
                byte[] buffer = new byte[1024];
                int length = myInput.read(buffer);
                while (length > 0) {
                    myOutput.write(buffer, 0, length);
                    length = myInput.read(buffer);
                }
                myOutput.flush();
                myOutput.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
           CommonUtil.safeClose(myInput); 
           CommonUtil.safeClose(myOutput); 
        }
    }

    public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if (file.exists()) {
            EvLog.i("创建单个文件" + destFileName + "失败，目标文件已存在！");
            return false;
        }
        if (destFileName.endsWith(File.separator)) {
            EvLog.e("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
            return false;
        }
        // 判断目标文件所在的目录是否存在
        if (!file.getParentFile().exists()) {
            // 如果目标文件所在的目录不存在，则创建父目录
            EvLog.i("目标文件所在目录不存在，准备创建它！");
            if (!file.getParentFile().mkdirs()) {
                EvLog.e("创建目标文件所在目录失败！");
                return false;
            }
        }
        // 创建目标文件
        try {
            if (file.createNewFile()) {
                return true;
            } else {
                EvLog.e("创建单个文件" + destFileName + "失败！");
                return false;
            }
        } catch (IOException e) {
            EvLog.e("创建单个文件" + destFileName + "失败！" + e.getMessage());
            UmengAgentUtil.reportError(e);
            return false;  
        }  
    }  
    
    public static boolean createDir(String destDirName) {
        File file = new File(destDirName);
        if (file.exists()) {
            EvLog.i(destDirName + " is already exist!");
            return false;
        }
        // 判断目标文件所在的目录是否存在
        if (!file.getParentFile().exists()) {
            // 如果目标文件所在的目录不存在，则创建父目录
            EvLog.i("目标文件所在目录不存在，准备创建它！");
            if (!file.getParentFile().mkdirs()) {
                EvLog.e("创建目标文件所在目录失败！");
                return false;
            }
            grantWriteReadAccess(file.getParentFile().getAbsolutePath());
        }
        // 创建目标文件
        try {
            if (file.mkdir()) {
                grantWriteReadAccess(file.getAbsolutePath());
                EvLog.i("创建单个文件" + destDirName + "成功！");
                return true;
            } else {
                EvLog.e("创建单个文件" + destDirName + "失败！");
                return false;
            }
        } catch (Exception e) {
            EvLog.e("创建单个文件" + destDirName + "失败！" + e.getMessage());
            UmengAgentUtil.reportError(e);
            return false;  
        }  
    }  
    
    /**
     * [功能说明] 读取文件内容
     * @param filePath 文件路径
     * @param bufferSize 每次读取缓冲区大小
     * @return
     */
    public static byte[] getFileByteArray(String filePath,int bufferSize) throws Exception{
        if (TextUtils.isEmpty(filePath) || bufferSize <= 0) {
            EvLog.e("getFileByteArray input param is invalid");
            return null;
        }
        byte[] ret = null;
        FileInputStream is = null;
        try {
            is = new FileInputStream(new File(filePath));
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[bufferSize];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            is.close();
            ret = buffer.toByteArray();
        } catch (FileNotFoundException e2) {
            EvLog.e(e2.getMessage());
        } catch (IOException e1) {
            EvLog.e(e1.getMessage());
        } finally {
            CommonUtil.safeClose(is);
        }
        return ret;
    }
    
    public static void writeFileData(String fileName,String message){ 
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(fileName);
            byte[] bytes = message.getBytes();
            fout.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CommonUtil.safeClose(fout);
        }
    }
}
