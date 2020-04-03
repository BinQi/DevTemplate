package wbq.frame.util.device;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 网络工具类
 */
public class NetUtil {
	/**
	 * 检测网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkAvailable(Context context) {
		boolean result = false;
		if (context != null) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivityManager != null) {
				NetworkInfo networkInfo = null;
				/**
				 * 防止这个crash
				 * Caused by: android.os.DeadSystemException
				 ... 11 more
				 android.os.DeadSystemException
				 at android.net.ConnectivityManager.getActiveNetworkInfo(ConnectivityManager.java:876)

				 */
				try {
					networkInfo = connectivityManager.getActiveNetworkInfo();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (networkInfo != null && networkInfo.isAvailable()) {
					result = true;
				}
			}
		}
		return result;
	}
	
	/**
	 * 检测WIFI是否可用
	 * @param context
	 * @return
	 */
	public static boolean isWifiEnable(Context context) {
		boolean result = false;
		if (context != null) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivityManager != null) {
				NetworkInfo networkInfo = null;
				try {
					networkInfo = connectivityManager.getActiveNetworkInfo();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (networkInfo != null && networkInfo.isConnected()
						&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					result = true;
				}
			}
		}
		return result;
	}
	
	/**
	 * 网络类型定义
	 *
	 * @author matt
	 * @date: 2015年1月30日
	 *
	 */
	public static enum NetworkType {
		unknown,
		wifi,
		mobile2g,
		mobile3g4g,
	}
	/**
	 * 获取当前网络状态，wifi，GPRS，3G，4G
	 * 
	 * @param context
	 * @return
	 */
	public static NetworkType getNetworkType(Context context) {
		NetworkType ret = NetworkType.unknown;
		try {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkinfo = manager.getActiveNetworkInfo();
			if (networkinfo.getType() == ConnectivityManager.TYPE_WIFI) {
				ret = NetworkType.wifi;
			} else if (networkinfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				int subtype = networkinfo.getSubtype();
				switch (subtype) {
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_CDMA:
				case TelephonyManager.NETWORK_TYPE_EDGE:
				case TelephonyManager.NETWORK_TYPE_GPRS:
				case TelephonyManager.NETWORK_TYPE_IDEN:
					// 2G
					ret = NetworkType.mobile2g;
					break;
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_UMTS:
					// 3G,4G
					ret = NetworkType.mobile3g4g;
					break;
				case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				default:
					// unknown
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 获取本地IP函数
	 * @return
	 */
	public static String getLocalIPAddress() {
		try {
			for (Enumeration<NetworkInterface> mEnumeration = NetworkInterface
					.getNetworkInterfaces(); mEnumeration.hasMoreElements();) {
				NetworkInterface intf = mEnumeration.nextElement();
				for (Enumeration<InetAddress> enumIPAddr = intf.getInetAddresses(); enumIPAddr
						.hasMoreElements();) {
					InetAddress inetAddress = enumIPAddr.nextElement();
					//如果不是回环地址
					if (!inetAddress.isLoopbackAddress()) {
						//直接返回本地IP地址
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {
		}
		return null;
	}
}
