package de.robv.android.xposed.installer.update;

import android.app.Activity;
import android.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import de.robv.android.xposed.installer.R;
import de.robv.android.xposed.installer.http.base.HttpManagerUtil;
import de.robv.android.xposed.installer.http.base.IJsonCall;
import de.robv.android.xposed.installer.http.responce.UpdateResponce;

/**
 * Created by lvyonggang on 2017/3/23.
 *  apk升级管理器
 */

public class ApkUpdateManager {

    private String TAG="ApkUpdateManager";
    private Activity activity ;
    private UpdateResponce updateResponce ;
    private LoadingDialog loadingDialog ;
    private ApkInfoDialog apkInfoDialog;

    public static final int TYPE_SHOW_UPDATE = 1 ; //有新版本
    public static final int TYPE_SHOW_NULL = 2 ; //无新版

    //静默升级的话不弹框，主动升级的话弹框
    public boolean showLoading;

    private long checkApkStep = 24*60*60; //2天

    public ApkUpdateManager(Activity activity , boolean showLoading){
        this.activity = activity ;
        this.showLoading = showLoading;
        if (showLoading){
            loadingDialog = new LoadingDialog(activity);
            loadingDialog.setMessage(R.string.down_check_title);
        }
    }


    private void showLoadingDialog(){
        if (showLoading && loadingDialog!=null && !loadingDialog.isShowing()){
            loadingDialog.show();
        }
    }

    private void dimissLoading(){
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
    }

    private void showUpdateDialog(){
        if (updateResponce ==null){
            return;
        }
        apkInfoDialog = new ApkInfoDialog(activity);
        apkInfoDialog.setTitle(R.string.update_title);
        apkInfoDialog.setVersion(updateResponce.getVersionName());
        apkInfoDialog.setInfo(updateResponce.getDescription());
        apkInfoDialog.setSureClick(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i(TAG , "url: "+updateResponce.getDownloadUrl());
                dismissUpdateDialog();
                showDownDialog();
            }
        });
        if (apkInfoDialog !=null && !apkInfoDialog.isShowing()){
            apkInfoDialog.show();
        }
    }

    public void dismissUpdateDialog(){
        if (apkInfoDialog !=null && apkInfoDialog.isShowing()){
            apkInfoDialog.dismiss();
        }
    }

    public void showDownDialog(){
        if (updateResponce==null && updateResponce.getDownloadUrl()==null){
            return;
        }
        String url = updateResponce.getDownloadUrl();
        DownLoadDialog downLoadDialog = new DownLoadDialog(activity, url);
        downLoadDialog.setTitleInfor(R.string.down_title);
        downLoadDialog.show();
    }

    /***
     * 升级策略先屏蔽
     */
    public void updateInMainCheck(){
//        //有间隔更新
//        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil();
//        long preTime = sharedPreferencesUtil.get(SharedPreferencesUtil.APK_CHECK_MAIN , 0 );
//        long curTime =  System.currentTimeMillis()/1000 ;
//        boolean isNeedUpdate = (curTime - preTime > checkApkStep) ;
//        if (isNeedUpdate){
//            updateCheck();
//            sharedPreferencesUtil.put(SharedPreferencesUtil.APK_CHECK_MAIN , System.currentTimeMillis()/1000);
//        }
    }


    public void updateCheck(){
        //实时点击更新
        showLoadingDialog();
        IJsonCall call = new IJsonCall() {
            @Override
            public void sucess(Object object) {
                updateResponce =(UpdateResponce)object;
                dimissLoading();
                showUpdateDialog();
            }

            @Override
            public void onFailure(int code, String msg) {
                dimissLoading();
                toastLaster();
            }
        };
        HttpManagerUtil.excuteXposedUpdate(call);
    }

    private void toastLaster(){
        if (showLoading){
            Toast.makeText(activity , R.string.down_laster_title , Toast.LENGTH_SHORT).show();
        }
    }
}
