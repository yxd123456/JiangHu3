package com.sptech.qujj.core;

import android.os.Environment;

public class Constants {

	public static String VERSIONNO = "010000";// appversion=010000

	public static String DBName;
	public static String FILEIN;
	public static String APKFILE;
	public static String APKNAME = "jianghujiuji.apk";
	public static String SDCARD;
	public static String SDCARDRECORD;
	public static int SDFREESIZE = 50;

	public static final int EXITDIALOG = 1;

	public static String name = "";
	public static String barcode = "";
	public static String category = "";
	public static String subcategory = "";
	public static double price = 0;

	public static final String SDCARD_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath();

	public static final String USER_AGENT = "";// APP,Android_id,MI NOTE
												// LTE,Android4.4.4,1.0,16；

	// 客户端类型,设备号,手机型号,系统名称+版本号,APP版本号,SDK版本号；

	// public static final String LOCAL_DIR = SDCARD_DIR +
	// "/DmpJhjjLocal/Reord/";

	// public static final String AUDIO_DIR = SDCARD_DIR +
	// "/DmpJhjjLocal/Audio/";

	static {
		DBName = "blog.db";
		FILEIN = "DmpJhjjLocal";
		APKFILE = "JHJJApk";
		SDCARD = Environment.getExternalStorageDirectory() + "/DmpJhjjLocal"; // SD卡位置
		SDCARDRECORD = Environment.getExternalStorageDirectory()
				+ "/DmpJhjjLocal/Record/";

		// USER_AGENT = "APP"+android.provider.Settings.System.getString(
		// getContentResolver(), "android_id")+
		// android.os.Build.MODEL+android.os.Build.VERSION.SDK+android.os.Build.VERSION.RELEASE+
	}

}
