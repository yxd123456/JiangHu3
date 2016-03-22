package com.sptech.qujj.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.sptech.qujj.APPConstants;
import com.sptech.qujj.basic.ActivityCollect;

public class Tools {

	/**
	 * 检测程序是否进入后台
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isAppOnForeground(Context context) {
		// Returns a list of application processes that are running on the
		// device
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = context.getPackageName();
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				System.out.println("");
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否连网
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		if (context != null) {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity == null) {

			} else {// 获取所有网络连接信息
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {// 逐一查找状态为已连接的网络
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 将数据库导入到sd卡中
	 * 
	 * @param context
	 * @throws IOException
	 */
	public static void copyDataBaseToSdCard(Context context) throws IOException {
		String databaseFilenames = context.getFilesDir().getPath() + "/"
				+ APPConstants.DBName;
		File dir = new File("/data/data/com.novem.youting/databases/");
		if (!dir.exists())// 判断文件夹是否存在，不存在就新建一个
			dir.mkdir();
		FileOutputStream os = null;

		String desc = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/youting4";

		try {
			os = new FileOutputStream(desc);// 得到数据库文件的写入流
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		InputStream is = new FileInputStream(databaseFilenames);
		byte[] buffer = new byte[8192];
		int count = 0;
		try {

			while ((count = is.read(buffer)) > 0) {
				os.write(buffer, 0, count);
				os.flush();
			}
		} catch (IOException e) {

		}
		try {
			is.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 判断 一个字段的值否为空
	 * 
	 * @author Michael.Zhang 2013-9-7 下午4:39:00
	 * @param s
	 * @return
	 */
	public static boolean isNull(String s) {
		if (null == s || s.equals("") || s.equalsIgnoreCase("null")) {
			return true;
		}

		return false;
	}
	// 保留2位小数
	public static String saveTwoNumber(double f) {
		DecimalFormat df = new DecimalFormat("#");
		return df.format(f);
	}

	// 将单个的code组装piccode的String,上传给服务器 A_B_C
	public static String saveCode(List<String> list) {
		String code = "";
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				if (i == list.size() - 1) {
					code = code + list.get(i);
				} else {
					code = code + list.get(i) + "-";
				}
			}
			return code;
		} else {
			return "";
		}
	}

	public static String getFirstCode(String code) {
		String[] strarray = code.split("-");
		if (strarray.length > 0) {
			return strarray[0];
		} else {
			return "";
		}
	}

	public static List<String> getCodeList(String code) {
		List<String> _list = new ArrayList<String>();
		String[] strarray = code.split("-");
		for (int i = 0; i < strarray.length; i++) {
			_list.add(strarray[i]);
		}
		return _list;
	}

	public static String[] getCompleteItems(int complete) {
		int index = 1;
		if (complete == 90) {
			index = 1;
		} else {
			index = (90 - complete) / 10;
		}
		String[] itemBack = new String[index];
		for (int i = 0; i < itemBack.length; i++) {
			itemBack[i] = (90 - 10 * i) + "%";
		}
		return itemBack;
	}

	public static String[] getRebackCompleteItems() {
		String[] itemBack = new String[9];
		for (int i = 0; i < itemBack.length; i++) {
			itemBack[i] = (90 - 10 * i) + "%";
		}
		return itemBack;
	}

	public static Bitmap readBitmap(Context context, Uri selectedImage) {
		Bitmap bm = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 5;
		AssetFileDescriptor fileDescriptor = null;
		try {
			fileDescriptor = context.getContentResolver()
					.openAssetFileDescriptor(selectedImage, "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				bm = BitmapFactory.decodeFileDescriptor(
						fileDescriptor.getFileDescriptor(), null, options);
				fileDescriptor.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bm;
	}

	public static String insetDBStr(String str) {
		if (str != null && !TextUtils.isEmpty(str) && !"null".equals(str)) {
			return str.replaceAll("'", "").trim();
		} else {
			return "";
		}

	}

	public static boolean hasSDCard(Context context) {
		boolean hasExternalStorage = Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED);

		return hasExternalStorage;
	}

	public static String createPwd(String pwd) {
		String newPwd = pwd;
		char[] charPwd = newPwd.toCharArray();
		for (int i = 0; i < charPwd.length; i++) {
			charPwd[i] = (char) (charPwd[i] - 20);
		}
		String convetorStr = new String(charPwd);
		return convetorStr;
	}

	public static String parseByte2HexStr(byte[] buf) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1)
				hex = '0' + hex;
			sb.append(hex);
		}
		return sb.toString();
	}

	// 将服务器返回的-1 改为""
	public static String spHelperTv(String str) {
		if ("-1".equals(str)) {
			return "";
		}
		return str;
	}

	public static void clearLogin() {
		System.out.println("清除数据了=+clearLogin");
		// SPHelper.setUid("");
		// SPHelper.setMobileNum("");
		//
		// SPHelper.setIDCardNum("");
		// SPHelper.setRealName("");
		// SPHelper.setNickName("");
		// SPHelper.setEmail("");
		// SPHelper.setAvatar("");
		// SPHelper.setResetQuestion("");
		// SPHelper.setResetQuestionID("");
		// SPHelper.setAnswer("");
		// SPHelper.setGender("");
		// SPHelper.setCity("");
		// SPHelper.setQQid("");
		// SPHelper.setSinaid("");
	}

	public static void addActivityList(Activity activity) {
		ActivityCollect.add(activity);
	}

	public static void removeActivityList(Activity activity) {
		ActivityCollect.remove(activity);
	}

	public static void clearActivityList() {
		ActivityCollect.list.clear();
	}

}
