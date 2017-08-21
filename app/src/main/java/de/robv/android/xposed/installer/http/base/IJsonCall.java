package de.robv.android.xposed.installer.http.base;

/**
 * Created by lvyonggang on 2017/3/23.
 */

public interface IJsonCall {
    /**
     *  @obj : 解析玩的json数据
     */
    void sucess(Object object);
    void onFailure(int code, String msg);

}
