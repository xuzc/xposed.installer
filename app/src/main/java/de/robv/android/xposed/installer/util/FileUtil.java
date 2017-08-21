package de.robv.android.xposed.installer.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;

import de.robv.android.xposed.installer.XposedApp;

/**
 * Created by xuzc<admin@xuzc.me> on 2017/2/22.
 *  文件工具类。 最好把所有涉及文件的操作路径都写在这里。便于管理和清除数据
 *  注意：1）重要的小的问文件，存放在app的file目录下[好像大小有限制]
 *        2）大的资源[视频、apk包]放在缓存目录下。
 */
public class FileUtil {

    private static final String TAG = "FileUtil";
    //文件根目录
    private static final String CYT_ROOT_PATH = "/cyt";

    //缓存的路径
    public static final String CACHE_PATH =CYT_ROOT_PATH +"/cache";
    //下载apk的路径
    public static final String APK_PATH = CACHE_PATH +"/download/";
    //日志的路径
    public static final String TASK_LOG_DIR = CACHE_PATH+"/log/task/process/";

    //放序列化设备对象的路径
    public static final String DATA_PATH = CYT_ROOT_PATH +"/data/";
	
	public static final String LOG_PATH = CYT_ROOT_PATH +"/log/";

	//下载服务器资源的路径
    public static final String SERVICE_SOURCE_NAME= "cytPic";
    public static final String SERVICE_SOURCE_PATH = CACHE_PATH +"/"+SERVICE_SOURCE_NAME+"/";

	/**
     * 创建根缓存目录
     * @return
     */
    public static String initCacheRootPath() {
        String path = "";
        //使用app的file目录，不用cache目录，防止被系统回收
        path = XposedApp.getInstance().getApplicationContext().getFilesDir().getPath()+CYT_ROOT_PATH ;
        return path;
    }

    /**
     * 创建序列化对象的根目录
     * @return
     */
    public static String initDataRootPath() {
        String path = "";
        //使用app的file目录，不用cache目录，防止被系统回收
        path = XposedApp.getInstance().getApplicationContext().getFilesDir().getPath()+DATA_PATH ;
        return path;
    }

    /**
     * APK类型的大文件最好放到sdcrad缓存里,直接放应用目录下有问题
     */
    public static String initApkRootPath(){
        String path = "";
        if (StorageUtil.isSdCardAvailable()){
            path = XposedApp.getInstance().getApplicationContext().getExternalCacheDir().getPath()+APK_PATH ;
        }else{
            path = XposedApp.getInstance().getApplicationContext().getCacheDir().getPath()+APK_PATH ;
        }
        return  path ;
    }

	/**

     * APK类型的大文件最好放到sdcrad缓存里,直接放应用目录下有问题
     */
    public static String initLogRootPath(){
        String path = "";
        if (StorageUtil.isSdCardAvailable()){
            path = XposedApp.getInstance().getApplicationContext().getExternalCacheDir().getPath()+LOG_PATH ;
        }else{
            path =XposedApp.getInstance().getApplicationContext().getCacheDir().getPath()+LOG_PATH ;
        }
        return  path ;
    }

	/**

     * 测试，先放到sdcard下，方便看效果，后面移到应用的文件里
     * APK类型的大文件最好放到sdcrad缓存里,直接放应用目录下有问题。
     */
    public static String initServiceSourcePath(){
        String path = "";
        if (StorageUtil.isSdCardAvailable()){
            path = XposedApp.getInstance().getApplicationContext().getExternalCacheDir().getPath()+SERVICE_SOURCE_PATH ;
        }else{
            path = XposedApp.getInstance().getApplicationContext().getCacheDir().getPath()+SERVICE_SOURCE_PATH ;
        }
        return  path ;
    }

    /**
	 * 递归创建文件夹
     *
     * @param dirPath
     * @return 创建失败返回""
     */
    public static String createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.getParentFile().exists()) {
                file.mkdir();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
    }

    /**
     * 递归创建文件夹
     * @param file
     * @return 创建失败返回""
     */
    public static String createFile(File file) {
        try {
            if (file.getParentFile().exists()) {
                file.createNewFile();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取图片缓存目录
     *
     * @return 创建失败, 返回""
     */
    public static String getImageCachePath() {
        String path = createDir(initCacheRootPath() + File.separator + "img" + File.separator);
        return path;
    }

    /**
     * 获取图片裁剪缓存目录
     *
     * @return 创建失败, 返回""
     */
    public static String getImageCropCachePath() {
        String path = createDir(initCacheRootPath() + File.separator + "imgCrop" + File.separator);
        return path;
    }

    /**
     * 将内容写入文件
     *
     * @param file eg:/mnt/sdcard/demo.txt
     * @param content  内容
     */
    public static void writeFileSdcard(String file, String content, boolean isAppend) {
        try {
            FileOutputStream fout = new FileOutputStream(file, isAppend);
            byte[] bytes = content.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开Asset下的文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static InputStream openAssetFile(Context context, String fileName) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        try {
            is = am.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }

    /**
     * 获取Raw下的文件内容
     *
     * @param context
     * @param resId
     * @return 文件内容
     */
    public static String getFileFromRaw(Context context, int resId) {
        if (context == null) {
            return null;
        }

        StringBuilder s = new StringBuilder();
        try {
            InputStreamReader in = new InputStreamReader(context.getResources().openRawResource(resId));
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                s.append(line);
            }
            return s.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 文件拷贝
     *
     * @param src  源文件
     * @param desc 目的文件
     */
    public static void fileChannelCopy(File src, File desc) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        try {
            fi = new FileInputStream(src);
            fo = new FileOutputStream(desc);
            FileChannel in = fi.getChannel();//得到对应的文件通道
            FileChannel out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fo != null) fo.close();
                if (fi != null) fi.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 转换文件大小
     *
     * @param fileLen 单位B
     * @return
     */
    public static String formatFileSizeToString(long fileLen) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileLen < 1024) {
            fileSizeString = df.format((double) fileLen) + "B";
        } else if (fileLen < 1048576) {
            fileSizeString = df.format((double) fileLen / 1024) + "K";
        } else if (fileLen < 1073741824) {
            fileSizeString = df.format((double) fileLen / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileLen / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 删除指定文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean deleteFile(File file) throws IOException {
        return deleteFileOrDirectory(file);
    }

    /**
     * 删除指定文件，如果是文件夹，则递归删除
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean deleteFileOrDirectory(File file) throws IOException {
        try {
            if (file==null){
                return false ;
            }
            LogUtil.i(TAG , file.getAbsolutePath());
            if (file.isDirectory()){
                Log.i(TAG , "进入目录:"+file.getAbsolutePath());
                File[] childFiles = file.listFiles();
                // 递归删除文件夹下的子文件
                for (int i = 0; i < childFiles.length; i++) {
                    deleteFileOrDirectory(childFiles[i]);
                    LogUtil.i(TAG , "删除文件:"+childFiles[i].getAbsolutePath());
                }
                //删除当前目录
                return file.delete();
            }else if (file.isFile()){
                LogUtil.i(TAG , "进入文件并删除文件:"+file.getAbsolutePath());
                return file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /***
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * 获取文件内容
     *
     * @param bufferedReader
     * @return
     */
    private static String getFileOutputString(BufferedReader bufferedReader) throws IOException {
        if (bufferedReader == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append("\n").append(line);
        }
        bufferedReader.close();
        return sb.toString();
    }

    /**
     * 获取文件内容
     *
     * @param path
     * @return
     */
    public static String getFileOutputString(File path) {
        try {
            return getFileOutputString(new BufferedReader(new InputStreamReader(
                    new BufferedInputStream(new FileInputStream(path)), "utf-8")));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取文件内容
     *
     * @param path
     * @return
     */
    public static String getFileOutputString(File path, boolean isConvertCodeAndGetText) {
        try {
            if (isConvertCodeAndGetText) {
                return getFileOutputString(convertCodeAndGetText(path));
            } else  {
                return getFileOutputString(path);
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取文件内容
     *
     * @param path
     * @return
     */
    public static String getFileOutputString(String path) {
        return getFileOutputString(new File(path));
    }

    /**
     * 获取文件内容
     *
     * @param path
     * @return
     */
    public static String getFileOutputString(String path, boolean isConvertCodeAndGetText) {
        return getFileOutputString(new File(path), isConvertCodeAndGetText);
    }

    /**
     * 自动转码，解决中文乱码问题
     * @param file
     * @return
     */
    private static BufferedReader convertCodeAndGetText(File file) {
        BufferedReader reader;
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream in = new BufferedInputStream(fis);
            in.mark(4);
            byte[] first3bytes = new byte[3];
            in.read(first3bytes);
            in.reset();
            if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB
                    && first3bytes[2] == (byte) 0xBF) {// utf-8
                reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            } else if (first3bytes[0] == (byte) 0xFF
                    && first3bytes[1] == (byte) 0xFE) {
                reader = new BufferedReader(
                        new InputStreamReader(in, "unicode"));
            } else if (first3bytes[0] == (byte) 0xFE
                    && first3bytes[1] == (byte) 0xFF) {
                reader = new BufferedReader(new InputStreamReader(in,
                        "utf-16be"));
            } else if (first3bytes[0] == (byte) 0xFF
                    && first3bytes[1] == (byte) 0xFF) {
                reader = new BufferedReader(new InputStreamReader(in,
                        "utf-16le"));
            } else {
                reader = new BufferedReader(new InputStreamReader(in, "GBK"));
            }
        } catch (FileNotFoundException e) {
            reader= null;
        } catch (IOException e) {
            reader = null;
        }
        return reader;
    }

    /**
     * @function： 清除缓存目录下及子目录里文件：注意文件路径一定要到删除文件的上一级文件夹。
     * 1)apk下载文件
     * 2）日志文件
     */
    public static void cleanCache(){
        try {
            //删除日志缓存
            String rootPath = initCacheRootPath();
            String logPath = rootPath + TASK_LOG_DIR ;
            File cacheLogFile = new File(logPath);
            deleteFileOrDirectory(cacheLogFile);

            //删除apk缓存。app本身及微信的版本
            String apkPath = initApkRootPath();
            File cacheApkFile = new File(apkPath);
            deleteFileOrDirectory(cacheApkFile);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
