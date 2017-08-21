package de.robv.android.xposed.installer.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import de.robv.android.xposed.installer.floatview.FloatManager;
import de.robv.android.xposed.installer.util.AppWhiteUtil;
import de.robv.android.xposed.installer.util.LogUtil;
import de.robv.android.xposed.installer.util.ModuleUtil;
import de.robv.android.xposed.installer.util.NotificationUtil;

/**
 * Created by lvyonggang on 2017/5/15.
 *  兼容xposed不用选择的逻辑
 */

public class CytPackageChangeReceiver extends BroadcastReceiver{

    public static final int TYPE_NULL = 0;
    public static final int TYPE_ADD = 1;
    public static final int TYPE_DELETE = 2;
    public static final int TYPE_INSTASLL = 3; //改变类型[升级]

    /**cyt app更新时候发送的广播,切记不要轻易修改,要和cyt app保持一致*/
    public static final String CYT_UPDATE_ACTION="com.cyt.update";
    public static final String INTENT_PACKAGENAME="packageName";

    private String TAG="CytPackageChangeReceiver";
    private final static ModuleUtil mModuleUtil = ModuleUtil.getInstance();


    private static String getPackageName(Intent intent) {
        Uri uri = intent.getData();
        return (uri != null) ? uri.getSchemeSpecificPart() : null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equals(CYT_UPDATE_ACTION)){
            //cyt app升级
            String updatPackageName = intent.getStringExtra(INTENT_PACKAGENAME);
            dealUpdate(context , updatPackageName);
        }else if (action.equals(Intent.ACTION_PACKAGE_ADDED)){
            String packageName = getPackageName(intent);
            dealAdd(context , packageName);
        }else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)){
            String packageName = getPackageName(intent);
            dealRemove(packageName);
        }
    }

    /***
     * 类型判断：
     *   1)升级的完整消息： ACTION_PACKAGE_REMOVED&&replace=true ->ACTION_PACKAGE_ADDED && replace=true
     */
    private int getIntentType(Intent intent){
        String action = intent.getAction();
        LogUtil.e("action:"+action +"; extralReplace:"+ intent.getBooleanExtra(Intent.EXTRA_REPLACING, false));
        int type = TYPE_NULL ;
        if (action.equals(Intent.ACTION_PACKAGE_ADDED)){
            return TYPE_ADD ;
        }else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)){
            type = TYPE_DELETE ;
        }else if (action.equals(Intent.ACTION_PACKAGE_CHANGED)){
            //type = TYPE_CHANGE ;
        }else if (action.equals(Intent.ACTION_INSTALL_PACKAGE) ||action.equals(Intent.ACTION_PACKAGE_INSTALL) ){
            type = TYPE_INSTASLL;
        }
        return type ;
    }

//    private void showFloat(Context context){
//        FloatManager floatManager = new FloatManager(context);
//        floatManager.showFloatView();
//    }

    public void dealAdd(Context context ,  String packageName){
        //检测当前包是否配置xposed标识
        ModuleUtil.InstalledModule module = ModuleUtil.getInstance().reloadSingleModule(packageName);
        boolean isWhite = AppWhiteUtil.isWhiteApp(packageName);
        if (module!=null){
            if (isWhite){
                //追加的逻辑.加包的时候默认选中
                mModuleUtil.setModuleEnabled(packageName, true);
                mModuleUtil.updateModulesList(false);
                //showFloat(context);
            }else{
                NotificationUtil.showNotActivatedNotification(packageName, module.getAppName());
            }
        }else{
            mModuleUtil.setModuleEnabled(packageName, false);
            mModuleUtil.updateModulesList(false);
        }
    }

    public void dealRemove(String packageName){
        //删除时候如果当前的包没有配置xposed，但是旧的配置了并选择了要清空数据
        ModuleUtil.InstalledModule module = ModuleUtil.getInstance().reloadSingleModule(packageName);
        if (module==null){
            if (mModuleUtil.isModuleEnabled(packageName)) {
                mModuleUtil.setModuleEnabled(packageName, false);
                mModuleUtil.updateModulesList(false);
            }
        }
    }

    public void dealUpdate(Context context ,String packageName){
        LogUtil.e("dealUpdate 1111");
        ModuleUtil.InstalledModule module = ModuleUtil.getInstance().reloadSingleModule(packageName);
        LogUtil.e("dealUpdate 222 . module:"+module);
        boolean isWhite = AppWhiteUtil.isWhiteApp(packageName);
        LogUtil.e("dealUpdate 222 . isWhite:"+isWhite);
        if (isWhite){
            mModuleUtil.setModuleEnabled(packageName, true);
            //刷新数据
            mModuleUtil.updateModulesList(false);
            //showFloat(context);
        }
    }
}
