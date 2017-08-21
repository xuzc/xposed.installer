package de.robv.android.xposed.installer.util;


import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 最好把和sdcard相关的目录放到这里，方便清除缓存的时候用到
 * */
public class SdCardUtil {

    //文件根目录
    private static final String ROOT_PATH = "/xposed";

    public static String getRootPath(){
        return getSdCardPath()+ ROOT_PATH;
    }

	/**
     * @return SDCard是否存在
     */
    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * @return SD卡根目录路径
     */
    public static String getSdCardPath() {
        boolean exist = isSdCardExist();
        String sdpath = "";
        if (exist) {
            sdpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        } else {
            sdpath = null;
        }
        return sdpath;
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

}
