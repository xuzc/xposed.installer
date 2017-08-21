package de.robv.android.xposed.installer.util;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by lvyonggang on 2017/5/8.
 */

public class CommandUtil {

    /**
     *  @function: 重启手机
     */
    public static void reboot(){
        try {
            //测试是ok的,这两个命令可以替换使用,那个生效用哪个
            Runtime.getRuntime().exec(new String[]{"/system/xbin/su","-c","reboot now"});
            //Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","reboot now"});
        }catch (Exception e){

        }
    }

    /***
     * 静默安装apk
     * */
    public static void installApkSilence(String apkPath){
        rootCommand("pm install -r "+apkPath);
    }

    /**
     * 应用程序运行su命令，设备必须已破解(获得ROOT权限)
     * @param command 命令：String apkRoot="chmod 777 "+getPackageCodePath(); RootCommand(apkRoot);
     * @return 执行结果（true | false）
     */
    private static boolean rootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (IOException e) {
                Log.d("RootCommand", e.getMessage());
            } catch (NullPointerException e) {
                Log.d("RootCommand", e.getMessage());
            }
        }
        return true;
    }
}
