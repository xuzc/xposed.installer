package de.robv.android.xposed.installer.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import de.robv.android.xposed.installer.R;

public class NetUtil {

	/**没网*/
	public static final int NET_NONE = 1;
	/**wifi网*/
	public static final int NET_WIFI = 2;
	/**移动网*/
	public static final int NET_MOBILE = 3;
	/**第三方网*/
	public static final int NET_OTHER = 4;
	
	public static int getNetType(ConnectivityManager mConnectivityManager) {
		int type = 0;
		NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isAvailable()) {
			// 网络连接
			String name = netInfo.getTypeName();
			// netInfo.getType() == ConnectivityManager.TYPE_MOBILE
			if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				// wifi网络
				type = NET_WIFI;
			} else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				// 移动网络
				type = NET_MOBILE;
			} else {
				// 其它网络
				type = NET_OTHER;
			}
		} else {
			// 没有网
			type = NET_NONE;
		}
		return type;
	}

	/**
	 * 检测当前网络是否可用
	 * @param context
	 * @return true 可用，false 不可用
	 */
	public static boolean isNetworkAvailable(Context context) {
		boolean hasNet = false ;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
					hasNet =  true;
				}
			}
		}
		return hasNet;
	}

	/**
	 * 网络是否可用，不可用给出确定框
	 * @param context
	 * @return true 可用，false 不可用
	 */
	public static boolean isNetworkAvailableUi(Context context) {
		boolean hasNet = isNetworkAvailable(context);
		if (!hasNet){
			Toast.makeText(context, R.string.global_no_net ,Toast.LENGTH_SHORT).show();
		}
		return hasNet;
	}
}
