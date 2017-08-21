package de.robv.android.xposed.installer.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * App 辅助类
 */

public class AppUtil {

    private static String TAG="AppUtil";

    public static String getAppVersion(Context context){
        String version ="1";
        PackageManager pm = context.getPackageManager();
        try {
           PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            version = pi.versionName ;
        } catch (Exception e){
            e.printStackTrace();
        }
        return  version;
    }

    public static int getAppCode(Context context){
        int code = 0;
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            code = pi.versionCode ;
        } catch (Exception e){
        }
        return  code;
    }

    public static int getCytAppCode(Context context){
        String relaesePkgName = AppWhiteUtil.CYT_REALEASE ;
        String debugPkgName = AppWhiteUtil.CYT_DEBUG ;
        int code = getAppCodeByPkg(context ,relaesePkgName);
        if (code<=0){
            code = getAppCodeByPkg(context , debugPkgName);
        }
        return code;
    }

    private static int getAppCodeByPkg(Context context , String packageName){
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo!=null){
                return packageInfo.versionCode ;
            }
        }catch (Exception e){

        }
        return  0;
    }
}
