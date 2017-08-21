package de.robv.android.xposed.installer.http.responce;

/**
 * Created by lvyonggang on 2017/3/23.
 */

public class UpdateResponce {

    private String description ; //升级描述
    private int versionCode ; //版本号
    private String versionName ; //版本名称
    private String downloadUrl ; //下载地址



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
