package com.sptech.qujj;

import android.os.Environment;

public class APPConstants {
	// app升级配置(String.xml ,这里，manifest)
	public static String VERSIONNO = "010000";
	public static String PLATFORM = "android";
	public static String DBName;

	public static String SORTFLAGPRICEHIGH = "0";
	public static String SORTFLAGPRICELOWER = "1";
	public static String SORTFLAGDISTANCE = "3";
	public static String SORTFLAGGOOD = "2";

	public static String CACHEALL; // 内层file文件名字(真正存放图片的地方
	public static String FILECACHE; // 拍照用的缓存文件
	public static String APKFILE; // 更新下载的apk所在地址
	public static String APKNAME = "qujiuji.apk";
	public static String SDCARD;
	public static String SDCARDCACHE;
	public static int SDFREESIZE = 50; // sd卡剩余不足50M的时候提示用户存储卡已满

	// 排序方式
	public static int ORDERTIME = 1; // 时间逆序
	public static int ORDERTIME_DOWN = 2; // 时间顺序
	public static int ORDERCOMPLETE = 3; // 进度逆序
	public static int ORDERCOMPLETE_DOWM = 4; // 进度顺序

	// public static String APPID = "mXt2W6zJY66pXA31hgmLH6";
	// public static String AppKey = "1ixk7jcYErAwXCZ5VHOtn8";
	// public static String AppSecret = "eBZNfaPFuD6LlGXTIWRiB5";

	public static final int EXITDIALOG = 1;

	static {
		DBName = "tb.qujiuji.1.0.2.db";
		CACHEALL = "QuJiuJI";
		FILECACHE = "cachepic";
		APKFILE = "QuJiuJIApk";
		SDCARDCACHE = Environment.getExternalStorageDirectory() + "/" + CACHEALL;
		SDCARD = Environment.getExternalStorageDirectory() + "/" + CACHEALL;
	}

}