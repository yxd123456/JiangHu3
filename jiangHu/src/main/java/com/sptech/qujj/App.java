package com.sptech.qujj;

import java.io.File;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.tsz.afinal.FinalDb;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.igexin.sdk.PushManager;
import com.sptech.qujj.cache.DataCache;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.db.SPHelper;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.AppUtil;
import com.sptech.qujj.util.ImageUtil;

public class App extends Application {

	public static App app;
	private static KeyStore keyStore;
	public static final String CLIENT_KET_PASSWORD = "QuJiuJi";
	public DataCache dataCache;
	public DataCache userCache;
	int width;
	int height;

	public static App getInstance() {
		return app;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
		SDKInitializer.initialize(this);
		// DbManager.initDB(this);
		// PushManager.getInstance().initialize(getApplication());
		SPHelper.init(this);
		ImageUtil.createFile(this);
		ToastManage.init(this);
		File jsonCacheFile = new File(Environment.getExternalStorageDirectory() + Constant.JSON_DATA_CACHE_PATH);
		if (!jsonCacheFile.exists()) {
			jsonCacheFile.mkdirs();
		}
		dataCache = DataCache.get(jsonCacheFile);

		// 获取个人登录本地存储的对象
		File userCacheFile = new File(Environment.getExternalStorageDirectory() + Constant.USER_CACHE_PATH);
		if (!userCacheFile.exists()) {
			userCacheFile.mkdirs();
		}
		userCache = DataCache.get(userCacheFile);

		// 创建图片上传的文件夹
		File PHOTO_DIR = new File(Environment.getExternalStorageDirectory().getPath() + Constant.UPLOAD_PICTURE_PATH);
		if (!PHOTO_DIR.exists()) {
			PHOTO_DIR.mkdirs();
		}
		// initEngineManager(this);
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		//Log.e("shuangpeng", " 宽*高   :  "+width+"*"+height);
		//Toast.makeText(this, " 宽*高   :  "+width+"*"+height, 1).show();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	synchronized public static App getApplication() {
		return app;
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
	}

	public KeyStore getKeyStore() throws Exception {
		if (keyStore == null) {
			keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null, null);
			/*
			 * keyStore.load(app.getAssets().open("cacerts.bks"),
			 * CLIENT_KET_PASSWORD.toCharArray());
			 */
		}
		return keyStore;
	}

	public DataCache getCache() {
		return dataCache;
	}

	public DataCache getUserCache() {
		return userCache;
	}

	// ---aFinal数据库
	public static FinalDb getDataDb() {
		if (app != null) {
			return FinalDb.create(app, "data.db", true);
		}
		return null;
	}

	public static FinalDb getCacheDb() {
		if (app != null) {
			return FinalDb.create(app, "cache.db", true);
		}
		return null;
	}

	/**
	 * 获取imei
	 * 
	 * @return
	 */
	// 客户端类型,设备号,手机型号,系统名称+版本号,APP版本号,SDK版本号,CID；
	// 举例：
	// APP,Android_id,MI NOTE LTE,Android4.4.4,1.0,16,个推ClientID(32位)；
	// App,863216020912596,OPPO,R829T,android4.2.2,1.0.0,17,620fde094f8390bf7200fa22320ef89b
	// textView.setText("手机型号: " + android.os.Build.MODEL + ",\nSDK版本:"
	// + + + ",\n系统版本:"
	// + +);
	// String cid = PushManager.getInstance().getClientid(this);
	public String Getuseragent() {
		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		String mtype = android.os.Build.MODEL; // 手机型号
		String mtyb = android.os.Build.BRAND;// 手机品牌
		String cid = PushManager.getInstance().getClientid(this);
		if (TextUtils.isEmpty(imei) || "000000000000000".equals(imei)) {
			imei = getSerUnicode();
		}
		if (null == cid) {
			cid = "";
		}
		String useragent = "App," + imei + "," + mtyb + "," + mtype + ",android" + android.os.Build.VERSION.RELEASE + "," + getVersion() + "," + android.os.Build.VERSION.SDK + "," + cid + ","
		        + getNetWorkType(getInstance()) + "," + AppUtil.getVersionCode(getApplicationContext()) + "," + "guanwang,,"+width*height;

		System.out.println("useragent" + useragent);
		return useragent;
	}

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

	public String getSerUnicode() {

		String m_szLongID = getBuildId() + getWLANMAC();
		// compute md5
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
		// get md5 bytes
		byte p_md5Data[] = m.digest();
		// create a hex string
		String m_szUniqueID = new String();
		for (int i = 0; i < p_md5Data.length; i++) {
			int b = (0xFF & p_md5Data[i]);
			// if it is a single digit, make sure it have 0 in front (proper
			// padding)
			if (b <= 0xF)
				m_szUniqueID += "0";
			// add number to string
			m_szUniqueID += Integer.toHexString(b);
		} // hex string to uppercase

		return m_szUniqueID.toUpperCase();

	}

	public String getVersion() {
		String version = null;
		PackageManager manager = this.getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return version;

	}

	public String getBuildId() {
		String m_szDevIDShort = "35"
		        + // we make this look like a valid IMEI
		        Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
		        + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10
		        + Build.USER.length() % 10; // 13 digits
		return m_szDevIDShort;
	}

	public String getWLANMAC() {
		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		return wm.getConnectionInfo().getMacAddress();
	}

}
