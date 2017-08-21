package de.robv.android.xposed.installer.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

import de.robv.android.xposed.installer.BuildConfig;

/**
 * Created by lvyonggang on 2017/3/27.
 */
public class LogUtil {

    public static boolean isDebug = BuildConfig.DEBUG;

    private static String TAG ="LogUtil";

    public static  void i(String tag , String msg){
        if (isDebug){
            Log.i(tag , msg);
        }
    }

    public static  void d(String tag , String msg){
        if (isDebug){
            Log.d(tag , msg);
        }
    }

    public static  void w(String tag , String msg){
        if (isDebug){
            Log.w(tag , msg);
        }
    }

    public static  void e(String tag , String msg){
        if (isDebug){
            Log.e(tag , msg);
        }
    }

    public static  void v(String tag , String msg){
        if (isDebug){
            Log.v(tag , msg);
        }
    }

    public static void showToast(Context context , String str){
        if (isDebug){
            Toast.makeText(context,str, Toast.LENGTH_LONG).show();
        }
    }

    public static  void e(String msg){
        LogUtil.e(TAG,msg);
        writeFile(msg);
    }


    public static void writeFile(String content){
        String rootPath = SdCardUtil.getRootPath();
        SdCardUtil.createDir(rootPath);
        String filePath = rootPath+ File.separator+"xposedLog.log";
        String text =DateUtil.dateToString(new Date())+" "+ content+"\n";
        SdCardUtil.writeFileSdcard(filePath, text, true);
    }
}
