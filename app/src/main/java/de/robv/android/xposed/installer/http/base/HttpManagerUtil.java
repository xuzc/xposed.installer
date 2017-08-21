package de.robv.android.xposed.installer.http.base;

import java.util.HashMap;

import de.robv.android.xposed.installer.BuildConfig;
import de.robv.android.xposed.installer.XposedApp;
import de.robv.android.xposed.installer.http.responce.UpdateResponce;
import de.robv.android.xposed.installer.util.AppUtil;

/**
 * Created by lvyonggang on 2017/3/28.
 *  管理所有的http请求
 */

public class HttpManagerUtil {

    public static boolean isDebug = BuildConfig.DEBUG;

    // upgrade 系统接口
    public static final String URL_BASE =
            (isDebug) ? "http://upgrade-test.91cyt.com" : "http://upgrade.91cyt.com";

    public static final String URL_PRODUCT_CHECK=URL_BASE+"/api/product/check";
    //当前产品类型
    public static final String PRODUCT_CODE_XPOSED="XPINSTALL";


    /***
     * @fucntion: 用在第一次从服务器上获取xposed版本
     */
    public static void excuteXposedUpdate(IJsonCall call){

        BaseOkHttp okHttp = new BaseOkHttp();
        String url = URL_PRODUCT_CHECK ;
        Class josnCls = UpdateResponce.class ;
        String productCode=PRODUCT_CODE_XPOSED;
        int code = AppUtil.getAppCode(XposedApp.getInstance().getApplicationContext());
        HashMap<String,String> bodyHashMap = new HashMap<>();
        bodyHashMap.put("versionCode", code+"");
        bodyHashMap.put("productCode", productCode);
        okHttp.excutePost(url , bodyHashMap , josnCls , call);
    }

}
