package de.robv.android.xposed.installer.util;

import android.text.TextUtils;

/**
 * Created by lvyonggang on 2017/5/15.
 *  白名单的应用: 白名单的包名,不管先安装xposed还是app都要选中
 */

public class AppWhiteUtil {

    public static final String CYT_REALEASE = "com.cyt";   //cyt正式版
    public static final String CYT_DEBUG = "com.cyt.debug" ; //cyt测试版

    /**
     * xposed白名单的app包名：默认会自动勾选上
     * */
    public static String[] appWhites = {
         CYT_DEBUG ,
         CYT_REALEASE
    };

    public static boolean isWhiteApp(String packageName){
        if (TextUtils.isEmpty(packageName) || appWhites==null){
            return false ;
        }
        for (String temp : appWhites){
           if (packageName.equals(temp)){
               return true ;
           }
        }
        return false ;
    }

}
