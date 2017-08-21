package de.robv.android.xposed.installer.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *  公共数据存放接口。
 *    a)xposed安装器写入数据
 *    b)cyt读取数据
 */
public class SharedPreferencesHelper {

    private SharedPreferences sp;

    /**xposed共享数据的信息，不要轻易修改，不然第三方应用会出问题**/
    public static final String XPOSDE_PACKAGENAME = "de.robv.android.xposed.installer";
    public static final String XPOSDE_FILENAME = "xposedShare";
    public static final String ITEM_XPOSED_CODE="xposeVersioncode";
    public static final String ITEM_CYT_CODE="cytVersioncode";

    public SharedPreferencesHelper(Context context) {
        // 必须是MODE_WORLD_READABLE模式，否则会影响xposed模块读取不到配置数据，导致xposed功能不可用
        sp = context.getSharedPreferences(XPOSDE_FILENAME, Context.MODE_WORLD_READABLE);
    }

    private void put(String key, boolean value) {
        sp.edit().putBoolean(key, value).commit();
    }

    private void put(String key, float value) {
        sp.edit().putFloat(key, value).commit();
    }

    private void put(String key, long value) {
        sp.edit().putLong(key, value).commit();
    }

    private void putInt(String key, int value) {
        sp.edit().putInt(key, value).commit();
    }

    private String get(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    private boolean get(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    private float get(String key, float defValue) {
        return sp.getFloat(key, defValue);
    }

    private int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    private long get(String key, long defValue) {
        return sp.getLong(key, defValue);
    }


    public void setXposedAppCode(int appVersion){
        putInt(ITEM_XPOSED_CODE , appVersion);
    }

    public int getXposedAppCode(){
        return getInt(ITEM_XPOSED_CODE, 0);
    }

    public void setCytAppCode(int appVersion){
        putInt(ITEM_CYT_CODE , appVersion);
    }

    public int getCytAppCode(){
        return getInt(ITEM_CYT_CODE, 0);
    }

}
