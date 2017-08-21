package de.robv.android.xposed.installer.receivers;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import de.robv.android.xposed.installer.util.LogUtil;
import de.robv.android.xposed.installer.util.ModuleUtil;
import de.robv.android.xposed.installer.util.ModuleUtil.InstalledModule;
import de.robv.android.xposed.installer.util.NotificationUtil;

public class PackageChangeReceiver extends BroadcastReceiver {

    private String TAG="PackageChangeReceiver";
    private final static ModuleUtil mModuleUtil = ModuleUtil.getInstance();

    private static String getPackageName(Intent intent) {
        Uri uri = intent.getData();
        return (uri != null) ? uri.getSchemeSpecificPart() : null;
    }
    @Override
    public void onReceive(final Context context, final Intent intent) {
        //1.如果是升级[移除包并且替换]，返回
        LogUtil.e("1111111111");
        LogUtil.e("action:"+intent.getAction()+"; extraReplace:"+intent.getBooleanExtra(Intent.EXTRA_REPLACING, false));
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED) && intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)){
            // Ignore existing packages being removed in order to be updated
            LogUtil.e("进入升级逻辑11111");
            return;
        }
        String packageName = getPackageName(intent);
        LogUtil.e("packageName："+packageName);
        if (packageName == null){
            return;
        }
        LogUtil.e("2222222");

        //2.改变的时候,列出改变的组件的数组，非当前包的话返回
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) {
            // make sure that the change is for the complete package, not only a
            // component
            String[] components = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_COMPONENT_NAME_LIST);
            if (components != null) {
                boolean isForPackage = false;
                for (String component : components) {
                    LogUtil.e("component:"+component);
                    if (packageName.equals(component)) {
                        isForPackage = true;
                        break;
                    }
                }
                if (!isForPackage){
                    LogUtil.e("222 退出方法");
                    return;
                }
            }
        }
        //3.加载当前的组件
        LogUtil.e("3333333333");
        InstalledModule module = ModuleUtil.getInstance().reloadSingleModule(packageName);
        LogUtil.e("module:"+module);
        if (module == null
                || intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            // Package being removed, disable it if it was a previously active
            // Xposed mod
            LogUtil.e("3333数据为空或者是移除的广播");
            if (mModuleUtil.isModuleEnabled(packageName)) {
                LogUtil.e("3333清空旧的应用存在的数据");
                mModuleUtil.setModuleEnabled(packageName, false);
                mModuleUtil.updateModulesList(false);
            }
            return;
        }
        //4.根据类型设置值
        LogUtil.e("4444444");
        if (mModuleUtil.isModuleEnabled(packageName)) {
            LogUtil.e("444包名选中,更新数据");
            mModuleUtil.updateModulesList(false);
            NotificationUtil.showModulesUpdatedNotification();
        } else {
            LogUtil.e("444包名没有选中,弹出通知框");
            NotificationUtil.showNotActivatedNotification(packageName, module.getAppName());
        }
    }

}