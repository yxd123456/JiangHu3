package com.sptech.qujj.constant;

public class Constant {
	
	public static boolean flag_iv = false;//单选按钮
	
	public static boolean FLAG_SHIMINGRENZHENGZHONG = false;//实名认证完善中
	
	public static boolean FLAG_SHIMINGRENZHENG = false;//是否已经完成实名认证
	public static boolean FLAG_KAIQITONGXUNLU = false;//是否已经开启通讯录
	
	// 首页 弹出广告 上次弹出日期
	public final static String DIALOG_ADVER = "dialog_adver";
	// 用户信息
	public final static String USER_INFO = "user_info";
	public final static String IS_LOGIN = "is_login"; // 是否已登录
	public final static String USER_NAME = "user_name";// 用户名
	public final static String USER_PHONE = "user_phone"; // 用户手机号
	public final static String USER_CARD = "user_card"; // 用户身份证号
	public final static String USER_FACE = "user_face"; // 用户头像
	public final static String USER_EMAILNUM = "user_emailnum"; // 用户邮箱
	public final static String USER_BANKCARD_NUM = "user_bankcard_num"; // 银行卡数量
	public final static String SUPPORTBANK = "app_support_bank"; // 支持的银行卡

	public final static String SUPPORTBLUEBANK = "app_support_blue_bank"; // 支持的信用卡
	// 下载文件的路径
	public final static String DOWNLOAD_FILE_PATH = "/JiangHu/download/";
	// public final static HashMap<String, String> SUPPORTBANKMAP = new
	// HashMap<String, String>();// 支持的银行卡Map(
	// // (名称，图片流)
	public final static String USER_AUTH = "user_auth";// 用户认证情况；0未认证 1已认证 2认证失败

	public static final int POINT_STATE_NORMAL = 0; // 正常状态

	public static final int POINT_STATE_SELECTED = 1; // 按下状态

	public static final int POINT_STATE_WRONG = 2; // 错误状态
	/**
	 * 是否开启手势密码
	 */
	public final static String IS_USE_HANDPWD = "is_use_Handpwd";
	public final static String IS_SHOWSETHANDPWD = "is_showsethandpwd";
	public final static String HANDPASSWORD = "handpassword";
	/**
	 * 是否开启个推
	 */
	public final static String IS_OPENGETUI = "is_open_getui";
	/**
	 * 手势密码
	 */

	public final static String IS_OPENZHANGDAN = "is_openzhangdan";
	/**
	 * 手势密码
	 */

	/**
	 * 是否2G/3G/4G网络提醒
	 */
	public final static String IS_NETWORK = "is_open_network";

	public final static String UID = "userid";
	public final static String KEY = "userkey";
	// 保存着Volley框架请求的路径
	public final static String VOLLEY_CACHE_PATH = "/Jianghu/Cache/";
	// 缓存json格式数据的路径
	public final static String JSON_DATA_CACHE_PATH = "/Jianghu/jsonCache/";
	// 保存着 UserBean 缓存的路径
	public final static String USER_CACHE_PATH = "/Jianghu/userCache/";
	// 保存着上传图片的路径
	public final static String UPLOAD_PICTURE_PATH = "/Jianghu/upload/";
	//保存支付密码
	public final static String PAYPWD = "pay_pwd";
}
