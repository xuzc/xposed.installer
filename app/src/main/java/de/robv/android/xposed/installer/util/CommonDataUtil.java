package de.robv.android.xposed.installer.util;

import android.content.Context;

/**
 * Created by lvyonggang on 2017/5/24.
 *  多个应用共有数据
 */

public class CommonDataUtil {

    private static String TAG="CommonDataUtil";

    public static void writeData(Context context){
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
        sharedPreferencesHelper.setXposedAppCode(AppUtil.getAppCode(context));
        sharedPreferencesHelper.setCytAppCode(AppUtil.getCytAppCode(context));
    }

    public static void readData(Context context){
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
        int cytCode = sharedPreferencesHelper.getCytAppCode();
        int xposedCode = sharedPreferencesHelper.getXposedAppCode();
        LogUtil.e(TAG ,"cytCode:"+cytCode);
        LogUtil.e(TAG ,"xposedCode:"+xposedCode);
    }
}
