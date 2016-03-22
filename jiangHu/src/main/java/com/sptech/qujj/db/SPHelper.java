package com.sptech.qujj.db;

import android.content.Context;
import android.content.SharedPreferences;

public class SPHelper {
	private static SPHelper helper;
	private SharedPreferences settings = null;
	private Context ctx;
	private SharedPreferences.Editor editor;
	private String sPrefsName;

	private static final String GUIDEID = "guideid"; // 引导界面标识
	private static final String DOWNAPKID = "downapkid";
	private static final String UPDATE = "update"; // 更新
	private static final String REFRESH = "refresh"; // 刷新首页

	public static void init(Context context) {
		helper = new SPHelper(context);
	}

	public static SPHelper getInstance() {
		if (helper == null) {
			throw new NullPointerException("NOT INIT sphelper,please call init in app first");
		}
		return helper;
	}

	private SPHelper(Context context) {
		this.ctx = context;
		sPrefsName = ctx.getPackageName();
		this.settings = ctx.getSharedPreferences(sPrefsName, Context.MODE_PRIVATE);
		this.editor = settings.edit();
	}

	public static boolean getGuideid() {
		return getInstance().settings.getBoolean(GUIDEID, true);
	}

	public static void setGuideid(boolean state) {
		getInstance().editor.putBoolean(GUIDEID, state);
		getInstance().editor.commit();
	}

	public static boolean getUpdate() {
		return getInstance().settings.getBoolean(UPDATE, false);
	}

	public static void setUpdate(boolean state) {
		getInstance().editor.putBoolean(UPDATE, state);
		getInstance().editor.commit();
	}

	public static boolean getRefresh() {
		return getInstance().settings.getBoolean(REFRESH, false);
	}

	public static void setRefresh(boolean state) {
		getInstance().editor.putBoolean(REFRESH, state);
		getInstance().editor.commit();
	}

	public static long getDownApkId() {
		return getInstance().settings.getLong(DOWNAPKID, 1);
	}

	public static void setDownApkId(long state) {
		getInstance().editor.putLong(DOWNAPKID, state);
		getInstance().editor.commit();
	}

}
