package de.robv.android.xposed.installer.http.base;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import de.robv.android.xposed.installer.util.SignatureUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

/**
 *  @funuction: ui主线程里使用的网络请求
 *  基于响应是json的网络响应工具类
 */
public class BaseOkHttp {

	private String TAG= "BaseOkHttp";

	/**网络响应超时的时间,单位秒*/
	public static final int NET_TIMEOUT=30;
	/**消息成功或者失败*/
	public static final int MSG_RESPONE=1;
	public static final int MSG_FAILURE=2;

	public static final int RESPONCE_EXCEPTION = -1 ;

	private Class  jsonCls ;
	private IJsonCall iCall = null;

	public static final String AUTH_KEY="upgrade";
	public static final String AUTH_VERSION="5.1.2";

	//auth_signature

	public static final String METHOD_GET="GET";
	public static final String METHOD_POST="POST";

	/**
	 * 主要是异步的请求结果包装到主线程里
	 */
	private Handler handler = new Handler(){
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case MSG_RESPONE:
				if (iCall!=null){
					Object object = msg.obj ;
					iCall.sucess(object);
				}
				break;
			case MSG_FAILURE:
				int code = msg.arg1 ;
				String errorMsg = (String) msg.obj;
				if (iCall!=null) {
					iCall.onFailure(code ,errorMsg);
				}
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 每个请求必须带的认证信息
	 */
	HashMap<String,String> getAuthMap(){
		HashMap<String ,String> authHashMap = new HashMap<String, String>();
		authHashMap.put("auth_key",AUTH_KEY);
		//时间单位是秒
		authHashMap.put("auth_timestamp" , System.currentTimeMillis()/1000+"");
		authHashMap.put("auth_version",AUTH_VERSION);
		return authHashMap;
	}




	/**
	 * @functions: 执行一个get请求,可以设置一些超时 代理
	 * @param url: url地址
	 * @param iCall :响应的回调
	 */
	public void excuteGet(String url , IJsonCall iCall){
		this.iCall = iCall ;
		Request.Builder builder = new Request.Builder();
		builder.url(url);
		builder.get();
		//构造请求
		Request request = builder.build();
		runRequest(request);
	}

	OkHttpClient getOkHttpClient(){
		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		//设置连接超时时间
		clientBuilder.connectTimeout(BaseOkHttp.NET_TIMEOUT , TimeUnit.SECONDS);
		//设置读取数据的超时时间
		clientBuilder.readTimeout(NET_TIMEOUT, TimeUnit.SECONDS);
		//设置写入数据的超时时间
		clientBuilder.writeTimeout(NET_TIMEOUT, TimeUnit.SECONDS);
		OkHttpClient client = clientBuilder.build();
		return client;
	}

	/**
	 * function:为了兼容所有的post请求
	 * @param url: 请求的网址
	 * @param headMap: 头部的键值对请求
	 * @param bodyHashMap:实体的键值对请求
	 * @param iCall: 响应完回调
	 */
	public void excutePost(String url , HashMap<String, String>headMap , HashMap<String, String>bodyHashMap ,Class jsonCls, IJsonCall iCall){
		this.iCall = iCall ;
		this.jsonCls = jsonCls ;
		Builder mBuilder = new Builder();
		//设置url
		mBuilder.url(url);
		//设置头部
		if (headMap!=null) {
			for (Entry<String, String> entry : headMap.entrySet()) {
				mBuilder.header(entry.getKey() , entry.getValue());
			}
		}
		//设置请求数据
		FormBody.Builder bodyBuilder = new FormBody.Builder();
		if (bodyHashMap!=null) {
			//追加认证消息
			bodyHashMap.putAll(getAuthMap());

			String signature =  SignatureUtil.getSignature(METHOD_POST ,url , bodyHashMap);

			for (Entry<String, String> entry : bodyHashMap.entrySet()) {
				bodyBuilder.add(entry.getKey() , entry.getValue()+"");
			}

			//追加签名信息
			bodyBuilder.add("auth_signature" , signature+"");
		}
		FormBody body = bodyBuilder.build();
		mBuilder.post(body);
		//执行请求
		Request request = mBuilder.build();
		runRequest(request);
	}
	
	/**
	 * function:为了兼容所有的post请求,头部确定的不需要的
	 * @param url: 请求的网址
	 * @param bodyHashMap:实体的键值对请求
	 * @param iCall: 响应完回调
	 */
	public void excutePost(String url , HashMap<String, String>bodyHashMap , Class jsonCls , IJsonCall iCall){
		excutePost(url, null , bodyHashMap ,jsonCls, iCall);
	}

	private void sendSucessMsg(Object object){
		Message message = new Message();
		message.what = MSG_RESPONE;
		message.obj = object;
		handler.sendMessage(message);
	}

	private void sendFairMsg(int code , String msg){
		Message message = new Message();
		message.what = MSG_FAILURE;
		message.arg1 = code ;
		message.obj = msg;
		handler.sendMessage(message);
	}

	private void runRequest(final Request request){
		OkHttpClient okHttpClient = getOkHttpClient();
		try {
			//执行异步请求
			okHttpClient.newCall(request).enqueue(new Callback() {

				@Override
				public void onResponse(Call arg0, Response response){
					String text="" ;
					try {
						if (response!=null && response.body()!=null){
							text= response.body().string();
						}
						if (response.code()==200){
							if (jsonCls!=null){
								Object object = new Gson().fromJson(text , jsonCls);
								sendSucessMsg(object);
							}
						}else{
							sendFairMsg(response.code() , text+"");
						}
					}catch (Exception e){
						sendFairMsg(response.code() , text+"");
					}
				}

				@Override
				public void onFailure(Call arg0, IOException arg1) {
					String message = arg1!=null ? arg1.getMessage() :"";
					sendFairMsg(RESPONCE_EXCEPTION , message);
				}
			});
		}catch (Exception e){
			//onResponse里会想外抛出异常
			e.printStackTrace();
			sendFairMsg(RESPONCE_EXCEPTION , "异常");
		}
	}
}
