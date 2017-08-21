package de.robv.android.xposed.installer.update;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.robv.android.xposed.installer.R;
import de.robv.android.xposed.installer.util.ApkUtil;
import de.robv.android.xposed.installer.util.FileUtil;
import de.robv.android.xposed.installer.util.LogUtil;

/**
 * Created by lvyonggang on 2017/4/17.
 *  处理下载的类. 链接异常、超时、三次容错处理
 */

public class DownTaskManager {

    private String TAG = "DownTaskManager";

    //消息
    public static final int MSG_INIT = 1 ; //初始化得到数据的消息
    public static final int MSG_DOWING = 2 ; //下载中
    public static final int MSG_ERROR = 3 ; //下载异常的消息
    //大小单位
    public static final int SIZE_K = 1024 ;
    public static final int SIZE_M = 1024*1024 ;

    public static final int TRY_LIMIT = 3 ; //最多重使下次次数

    private String downUrl;  //下载地址
    private Context context; //下载的上下文
    private DownCall downCall ; //回调

    private boolean cancel ;
    private int tryCount = 0 ;
    //缓存apk的目录
    private String cacheApkDir = FileUtil.initApkRootPath();
    private String apkPath ; //下载的文件路径

    private int totalSize; //apk的大小
    private int downingSize; //正在下载的大小

    private String errorInfo ;

    public static final int TIME_OUT_CONNECT= 30*1000 ; //链接的超时30秒
    public static final int TIME_OUT_READ= 300*1000 ; //读取内容的超时300秒


    private Handler handler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what){
                case  MSG_INIT :
                    if (downCall!=null){
                        downCall.downing(0 , totalSize);
                    }
                case MSG_DOWING :
                    if (downCall!=null){
                        downCall.downing(downingSize , totalSize);
                        //注意整形的比较才相等，浮点型的话不一定
                        if (downingSize==totalSize & totalSize>0){
                            downCall.downed(true , context.getString(R.string.down_sucess));
                            downingSize = 0;
                            totalSize=0;
                        }
                    }
                    break;
                case MSG_ERROR :
                    if (downCall!=null){
                        downCall.downed(false , errorInfo);
                    }
            }
        }
    };

    public DownTaskManager(Context context, String downUrl, DownCall downCall) {
        this.context = context;
        this.downUrl = downUrl;
        this.downCall = downCall;
    }

    public DownTaskManager(Context context, String downUrl) {
        this.context = context;
        this.downUrl = downUrl;
    }

    public void setDownCall(DownCall downCall) {
        this.downCall = downCall;
    }

    /**
     * @fucntion ：开始下载
     */
    public void startDown(){
        if (TextUtils.isEmpty(downUrl)){
            return;
        }
        //截取下载文件的名称
        String sourceName = downUrl.substring(downUrl.lastIndexOf("/")+1, downUrl.length());
        apkPath = cacheApkDir+sourceName ;
        startDownTask();
    }

    /**
     * @fucntion ：启动下载任务
     */
    private void startDownTask(){
        if (TextUtils.isEmpty(downUrl)){
            return;
        }
        tryCount++;
        if (tryCount<=TRY_LIMIT){
            new DownloadTask().start();
        }
    }

    public void cancel(boolean cancle){
        this.cancel = cancle ;
    }


    /**
     * 获取微信版本的下载路径,如果存在缓存就直接删除掉
     */
    private File getApkFile() throws  Exception {
        File dirFile = new File(cacheApkDir);
        if (!dirFile.exists()){
            dirFile.mkdirs();
        }
        File apkFile =new File(apkPath);
        apkFile.createNewFile();
        return apkFile ;
    }



    /**
     * 下载前直接删除掉上次缓存的文件
     */
    private void deleteOldFile(){
        File apkFile =new File(apkPath);
        //如果文件存在，删除旧的缓存
        if (apkFile.exists()){
            apkFile.delete();
        }
    }

    /**
     * oss服务器有点神奇。开始获取不到资源的大小，要过一段时间才能获取到大小
     */
    private class DownloadTask extends Thread{

        private int waitTime = 10 ; //获取大小最多等10秒
        private int curTime = 0 ; //当前时间

        @Override
        public void run() {
            super.run();
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                deleteOldFile();
                URL url = new URL(downUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(false);
                connection.setConnectTimeout(TIME_OUT_CONNECT);
                connection.setReadTimeout(TIME_OUT_READ);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Charset", "UTF-8");
                connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                connection.setRequestProperty("Accept-Encoding", "identity");
                connection.connect();
                int code = connection.getResponseCode() ;
                if (code == HttpURLConnection.HTTP_OK){
                    LogUtil.i(TAG ,"下载响应的code:"+code);
                    //获取这个需要段时间
                    int fileLength = connection.getContentLength();
                    totalSize = fileLength ;
                    if (fileLength>0){
                        //资源获取成功
                        totalSize = fileLength ;
                        handler.sendEmptyMessage(MSG_INIT);
                    }else{
                        //链接失败,继续等待
                        errorInfo = context.getString(R.string.down_error_service);
                        handler.sendEmptyMessage(MSG_ERROR);
                        return;
                    }
                    input = connection.getInputStream();
                    output = new FileOutputStream(getApkFile());
                    byte data[] = new byte[SIZE_K];
                    int count;
                    while (((count = input.read(data))!= -1) && !cancel) {
                        if (cancel) {
                            input.close();
                            //取消下载了
                            break;
                        }
                        downingSize += count;
                        output.write(data, 0, count);
                        output.flush();
                        handler.sendEmptyMessage(MSG_DOWING);
                    }
                }else{
                    LogUtil.i(TAG ,"下载响应的code:"+code);
                    errorInfo = context.getString(R.string.down_error_service);
                    handler.sendEmptyMessage(MSG_ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //链接失败,继续等待
                errorInfo = context.getString(R.string.down_error_exception);
                handler.sendEmptyMessage(MSG_ERROR);
                return;
            } finally {
                try {
                    if (output != null){
                        output.close();
                    }
                    if (input != null){
                        input.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }

    public String getApkPath() {
        return apkPath;
    }

    /***
     * @function：前台界面的安装
     */
    public void installInUi(){
        ApkUtil.installInUi(context,apkPath);
    }

    /***
     * @function：后台静默的安装
     */
    public void installInSilence(){
        ApkUtil.installInSilence(apkPath);
    }

    public interface DownCall{

        /**
         *  @function: 下载过程中的进度
         *  downingSize: 已经下载的大小
         *  totalSize: 总的大小
         **/
        void downing(float downingSize, float totalSize);

        /**
         *  @function:  下载结束的情况。 成功，失败，取消等
         *  sucess: 是否成功
         *  info：不成功的提示信息
         **/
        void downed(boolean sucess, String info);
    }

}
