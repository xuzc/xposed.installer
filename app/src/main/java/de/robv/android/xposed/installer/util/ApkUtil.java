package de.robv.android.xposed.installer.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by lvyonggang on 2017/4/17.
 */

public class ApkUtil {

    private static String TAG="ApkUtil";

    //大小单位
    public static final int SIZE_K = 1024 ;
    public static final int SIZE_M = 1024*1024 ;

    /**
     * @function：有ui界面的apk安装
     */
    public static void installInUi(Context context , String apkPath){
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        File apkFile = new File(apkPath);
        install.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        context.startActivity(install);
    }

    /**
     * @function： 安装完打开apk
     */
    public static void openApk(Context context , String apkPath){
        PackageManager manager = context.getPackageManager();
        PackageInfo packageInfo = manager.getPackageArchiveInfo(apkPath , PackageManager.GET_ACTIVITIES);
        Intent intent = manager.getLaunchIntentForPackage(packageInfo.applicationInfo.packageName);
        context.startActivity(intent);
    }

    /**
     * 返回带一位小数点的M大小
     **/
    public static String getSizeOfM(float size){
        float temp = size/SIZE_M ;
        String result = dealValue(temp);
        return  result+"M";
    }

    /**
     * 合理显示浮点型，最多只保留一位小数点
     */
    public static String dealValue(float value){
        String temp = value+"";
        if (temp.contains(".")){
            //有小数点的话
            int index = -1 ;
            for (int i=0 ; i<temp.length();i++){
                if (temp.codePointAt(i) =='.'){
                    index = i;
                    break;
                }
            }
            if (index>=0){
                int end = index+2;
                temp = temp.substring(0,end);
            }
        }
        return temp ;
    }

    /***
     * @function: 后台静默的安装
     */
    public static void installInSilence(String apkPath){
        CommandUtil.installApkSilence(apkPath);
    }


    /***
     * @function: 兼容apk升级后xposed选择不生效。 因为第一选择的时候xposed路径是/data/app/com.cyt-1/base.apk，
     *   后续升级可能就会变成/data/app/com.cyt-n/base.apk. 策略就是找到最后一次升级的，把它名字改成合适的。
     */
    public static class RenameCytAppThread extends Thread {

        private Context context ;
        private String rootPath="/data/app/";
        private String targetPath="";
        private String pacakageName="";

        public RenameCytAppThread(Context context){
            this.context = context ;
            pacakageName = context.getPackageName();
            targetPath = rootPath+pacakageName+"-1";
        }

        @Override
        public void run() {
            super.run();
            //命令是否正常执行
            boolean cmdExcuse = false ;
            try {
                //提升权限,要在root手机里才起作用，会抛出异常
                Runtime runtime = Runtime.getRuntime();
                Process proc = runtime.exec("su");
                cmdExcuse = true ;
            }catch (Exception e){

            }

            if (!cmdExcuse){
                return;
            }

            File file = new File(rootPath);
            if (file.exists()){
                //过滤含指定目录的文件夹
                File[] listFiles  = file.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        if (pathname.getName().contains(pacakageName)){
                            return true ;
                        }
                        return false;
                    }
                });

                //没有合适的
                if (listFiles==null){
                    return;
                }
                //不管升不升级，系统里app目录下只存在1份apk数据
                File tempFile = listFiles[0];
                String currentPath = tempFile.getAbsolutePath();
                if (!currentPath.equals(targetPath)){
                    //文件夹重命名，注意重命名的文件和要命名成的文件必须处于同一级目录
                    tempFile.renameTo(new File(targetPath));
                }

            }
        }
    }
}
