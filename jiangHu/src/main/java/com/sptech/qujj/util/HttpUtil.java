package com.sptech.qujj.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;

import com.alibaba.fastjson.JSON;
import com.sptech.qujj.APPConstants;
import com.sptech.qujj.db.SPHelper;
import com.sptech.qujj.exception.HttpException;
import com.sptech.qujj.jsonUtil.ErrorMsg;
import com.sptech.qujj.jsonUtil.HttpHelper;
import com.sptech.qujj.toast.ToastManage;

public class HttpUtil {
	/**
	 * map转data
	 * 
	 * @param mapdata
	 * @return
	 */
	public static String getData(HashMap<String, Object> mapdata) {
		String json = JSON.toJSONString(mapdata);
		String base = Base64.encode(json.getBytes());
		String data = "";
		try {
			data = URLEncoder.encode(base, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;

	}

	/**
	 * 只有data
	 * 
	 * @param
	 * @return
	 */
	public static String getSign(HashMap<String, Object> mapdata) {
		String json = JSON.toJSONString(mapdata);
		String base = Base64.encode(json.getBytes());
		String sign = Md5.md5s(base);
		return sign;

	}

	/**
	 * uid key
	 * 
	 * @param mapdata
	 * @return
	 */
	public static String getSign(String uid, String key) {
		String base = uid + key.toUpperCase();
		String sign = Md5.md5s(base);
		return sign;

	}

	/**
	 * data uid key
	 * 
	 * @param mapdata
	 * @return
	 */
	public static String getSign(HashMap<String, Object> mapdata, String uid, String key) {
		String json = JSON.toJSONString(mapdata);
		String content = uid + key.toUpperCase();
		String base = Base64.encode(json.getBytes()) + content;
		String sign = Md5.md5s(base);
		return sign;

	}

	public static byte[] encodeBase64File(String path) throws Exception {
		File file = new File(path);
		FileInputStream inputFile = new FileInputStream(file);
		byte[] buffer = new byte[(int) file.length()];
		inputFile.read(buffer);
		inputFile.close();
		return buffer;
	}

	//

	// 版本更新downloadManager
	public static void updateApk(Activity activity, String url, String content, DownloadManager downloadManager) {
		if (!Tools.isNetworkAvailable(activity)) { // 没有网络就不进行下去了..
			ToastManage.showToast(ErrorMsg.TIMEOUT);
			return;
		}
		boolean hasExternalStorage = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (!hasExternalStorage) {
			Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
			activity.startActivity(viewIntent);
			return;
		}
		File folder = new File(APPConstants.APKFILE);
		if (!folder.exists() || !folder.isDirectory()) {
			folder.mkdirs();
		}

		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
		request.setDestinationInExternalPublicDir(APPConstants.APKFILE, APPConstants.APKNAME);
		request.setTitle("发现新版本");
		request.setDescription("趣救急新版本下载");
		request.setVisibleInDownloadsUi(false);
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
		request.setShowRunningNotification(true);

		request.allowScanningByMediaScanner();
		request.setVisibleInDownloadsUi(true);

		// request.allowScanningByMediaScanner();
		// request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
		// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

		request.setMimeType("application/vnd.android.package-archive");
		// request.setMimeType("application/com.sptech.qujj.download.file");
		long downloadId = downloadManager.enqueue(request);
		SPHelper.setDownApkId(downloadId);

	}

	// 获取网络iP
	public static JSONObject GeoLocation() {
		String ak = "6c7ce39773971b8bf9c7b3846f1065a4";
		String ip = "";
		String url = "http://api.map.baidu.com/location/ip?ak=" + ak + "&ip=" + ip + "&coor=bd09ll";

		String result = null;
		try {
			result = HttpHelper.doGet(url);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			return new JSONObject(result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// 获取当前网络状态
	public static String getNetWorkType(Context context) {
		String type = "";
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null) {
			type = "null";
		} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			type = "wifi";
		} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
			int subType = info.getSubtype();
			if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
				type = "2G";
			} else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA || subType == TelephonyManager.NETWORK_TYPE_EVDO_A
			        || subType == TelephonyManager.NETWORK_TYPE_EVDO_0 || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
				type = "3G";
			} else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准
				type = "4G";
			}
		}
		return type;
	}

}
