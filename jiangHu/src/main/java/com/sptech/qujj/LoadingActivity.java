package com.sptech.qujj;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.igexin.sdk.PushManager;
import com.sptech.qujj.activity.GestureVerifyActivity;
import com.sptech.qujj.activity.LoginActivity;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.db.SPHelper;
import com.sptech.qujj.dialog.CustomDialog;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.FileHelper;
import com.sptech.qujj.util.HttpUtil;

public class LoadingActivity extends BaseActivity {

	public static int screenHeight, screenWidth;
	private FileHelper helper;
	private SharedPreferences spf;
	private final int INITDATA = 1;
	private DialogHelper dialogHelper;
	/**
	 * 下载的弹窗
	 */
	private CustomDialog downloadDialog;
	/**
	 * 是否正在下载
	 */
	private boolean isDownloading = false;
	private ProgressBar progressBar;
	private TextView tvProgressView;
	/**
	 * 版本更新Url地址
	 */
	private String verionUpdateUrl;
	private static final int PROCESSING = 1;
	/*
	 * 下载的apk绝对路径
	 */
	private String filePath;
	boolean islogin;
	private int is_update;
	boolean isusehandpwd;
	private static final int FAILUER = -1;

	// private Handler handlerdown = new UIHandler();
	//
	// private final class UIHandler extends Handler {
	// @Override
	// public void handleMessage(Message msg) {
	// switch (msg.what) {
	// case PROCESSING:
	// int size = msg.getData().getInt("size");
	// progressBar.setProgress(size);
	// float num = (float) progressBar.getProgress() / (float)
	// progressBar.getMax();
	// int result = (int) (num * 100);
	// tvProgressView.setText("更新中..." + result + "%");
	// if (progressBar.getProgress() == progressBar.getMax()) {
	// exit();
	// FileService fileService = new FileService(LoadingActivity.this);
	// fileService.delete(verionUpdateUrl);
	// Toast.makeText(LoadingActivity.this, "下载更新文件成功", 500).show();
	// openApkFile(new File(filePath));
	// downloadDialog.dismiss();
	// isDownloading = false;
	// }
	// break;
	// case FAILUER:
	// Toast.makeText(LoadingActivity.this, "下载更新文件失败", 500).show();
	// downloadDialog.dismiss();
	// isDownloading = false;
	// // Noupdate(0);
	// break;
	// default:
	// break;
	// }
	// }
	// }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_yt_loading);
		initView();
	}

	private void initView() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;

		float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		dialogHelper = new DialogHelper(this);
		islogin = spf.getBoolean(Constant.IS_LOGIN, false);
		isusehandpwd = spf.getBoolean(Constant.IS_USE_HANDPWD + spf.getString(Constant.USER_PHONE, ""), false);
		// System.out.println("loading====" + screenWidth + "   =======" +
		// screenHeight + "density 屏幕密度(像素比例)=" + density +
		// "densityDPI 屏幕密度（每寸像素)=" + densityDPI);
		// dp与px换算公式：
		// pixs =dips * (densityDpi/160).
		// dips=(pixs*160)/densityDpi
		// try {
		// DbManager.updateDataBase(this); // getApplicationContext()
		// // Tools.copyDataBaseToSdCard(this);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// ActivityJumpManager.jumpDelay(this, GuideActivity.class, 1, 3, 1000);

		// System.out.println("进？？ getGuideid == " + SPHelper.getGuideid());

		handler.sendEmptyMessage(INITDATA);
		if (spf.getBoolean(Constant.IS_OPENGETUI, false)) {
			PushManager.getInstance().initialize(getApplicationContext());
		} else {
			PushManager.getInstance().stopService(getApplicationContext());
		}
		// checkUpdate();
		if (SPHelper.getGuideid()) {
			System.out.println("进来？？" + SPHelper.getGuideid());
			ActivityJumpManager.jumpDelay(this, GuideActivity.class, 1, 3, 2000);
			PushManager.getInstance().initialize(getApplicationContext());
			spf.edit().putBoolean(Constant.IS_OPENGETUI, true).commit();

		} else {
			if (islogin) {
				if (isusehandpwd) {
					System.out.println("islogin == " + islogin);
					ActivityJumpManager.jumpDelay(this, GestureVerifyActivity.class, 1, 3, 1000);
				} else {
					System.out.println("islogin == " + islogin);
					ActivityJumpManager.jumpDelay(this, MainActivity.class, 1, 3, 1000);
				}
				// ActivityJumpManager.jumpDelay(this,
				// GestureVerifyActivity.class, 1, 3, 2000);
				// Intent intent = new Intent(LoadingActivity.this,
				// GestureVerifyActivity.class);
				// startActivity(intent);
				// overridePendingTransition(R.anim.push_left_in,
				// R.anim.push_left_out);
			} else {
				ActivityJumpManager.jumpDelay(this, LoginActivity.class, 1, 3, 2000);
			}
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {

			case INITDATA:
				if (spf.getString(Constant.SUPPORTBANK, "").equals("")) {
					getData();
				}
				if (spf.getString(Constant.SUPPORTBLUEBANK, "").equals("")) {
					getXinData();
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onBackPressed() {
	}

	// 获取 储蓄卡的图标信息
	private void getData() {
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("card_type", 1);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(LoadingActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.SUPPORTBANKLIST, params, BaseData.class, null, setdealSuccessListener, errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> setdealSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			if (response.code.equals("0")) {

				byte[] b = Base64.decode(response.data);
				if (b != null && !b.equals("")) {
					// JSONArray s = JSON.parseArray(new String(b));
					System.out.println("支持的储蓄卡数据 == " + new String(b));
					// 保存到SharePreference
					spf.edit().putString(Constant.SUPPORTBANK, new String(b)).commit();
				}
			} else {
				// Toast.makeText(LoadingActivity.this, response.desc,
				// Toast.LENGTH_SHORT).show();
				ToastManage.showToast(response.desc);
			}
		}

	};

	// 获取 信用卡的图标信息
	private void getXinData() {
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("card_type", 2);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(LoadingActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.SUPPORTBANKLIST, params, BaseData.class, null, setXinSuccessListener, errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> setXinSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			if (response.code.equals("0")) {
				byte[] b = Base64.decode(response.data);
				if (b != null && !b.equals("")) {
					// JSONArray s = JSON.parseArray(new String(b));
					System.out.println("支持的信用卡数据 == " + new String(b));
					// 保存到SharePreference
					spf.edit().putString(Constant.SUPPORTBLUEBANK, new String(b)).commit();
				}
			} else {
				// Toast.makeText(LoadingActivity.this, response.desc,
				// Toast.LENGTH_SHORT).show();
				ToastManage.showToast(response.desc);
			}

		}

	};

	// 调用web服务失败返回数据
	@SuppressLint("ShowToast")
	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				dialogHelper.stopProgressDialog();
				// Noupdate(0);
			}
		};
	}

	// private void checkUpdate() {
	// // dialogHelper.startProgressDialog();
	// // dialogHelper.setDialogMessage("正在检查版本，请稍候...");
	// // the below codes is just for test
	// HashMap<String, Object> data = new HashMap<String, Object>();
	// data.put("versioncode", AppUtil.getVersionCode(this));
	// HashMap<String, String> params = new HashMap<String, String>();
	// params.put("data", HttpUtil.getData(data));
	// params.put("sign", HttpUtil.getSign(data));
	// HttpVolleyRequest<BaseData> request = new
	// HttpVolleyRequest<BaseData>(this, false);
	// request.HttpVolleyRequestPost(JsonConfig.AUTOUPDATE, params,
	// BaseData.class, null, checkSuccessListener(), errorListener());
	// }
	//
	// private Listener<BaseData> checkSuccessListener() {
	// return new Listener<BaseData>() {
	//
	// @Override
	// public void onResponse(BaseData response) {
	// // dialogHelper.stopProgressDialog();
	// if (response.code.equals("0")) {
	// byte[] b = Base64.decode(response.data);
	// JSONObject s = JSON.parseObject(new String(b));
	// is_update = s.getIntValue("is_update");// 0,无需更新 1.普通更新
	// // 2，强制更新
	// int versioncode = s.getIntValue("versioncode");
	// String versionname = s.getString("versionname");
	// verionUpdateUrl = s.getString("downloadurl");
	// if (is_update == 0) {
	// // Noupdate(1000);
	// } else if (is_update == 1) {
	// createVersionInfoDialog();
	// } else {
	// createVersionInfoDialog();
	// }
	// } else {
	// ToastManage.showToast(response.desc);
	// // Noupdate(0);
	// }
	// }
	//
	// };
	// }

	// private void Noupdate(int second) {
	// if (SPHelper.getGuideid()) {
	// System.out.println("进来？？" + SPHelper.getGuideid());
	// ActivityJumpManager.jumpDelay(this, GuideActivity.class, 1, 3, 1000);
	// PushManager.getInstance().initialize(getApplicationContext());
	// spf.edit().putBoolean(Constant.IS_OPENGETUI, true).commit();
	//
	// } else {
	// if (islogin) {
	// if (isusehandpwd) {
	// System.out.println("islogin == " + islogin);
	// ActivityJumpManager.jumpDelay(this, GestureVerifyActivity.class, 1, 3,
	// second);
	// } else {
	// System.out.println("islogin == " + islogin);
	// ActivityJumpManager.jumpDelay(this, MainActivity.class, 1, 3, second);
	// }
	// } else {
	// ActivityJumpManager.jumpDelay(this, LoginActivity.class, 1, 3, second);
	// }
	// }
	//
	// }

	// /**
	// *
	// * @author 谷松磊
	// * @Description 版本信息对话框
	// * @return void
	// * @date 2015年1月15日 上午12:02:15
	// */
	// private void createVersionInfoDialog() {
	// VersionUpdateDialog dr = new VersionUpdateDialog(LoadingActivity.this,
	// new EventHandleListener() {
	// @Override
	// public void eventRifhtHandlerListener() {
	// createDownloadInfoDialog();
	// }
	//
	// @Override
	// public void eventLeftHandlerListener() {
	// if (is_update == 1) {
	// // Noupdate(0);
	// } else {
	// System.exit(0);
	// }
	// }
	// });
	// dr.createMyDialog();
	// }
	//
	// /**
	// *
	// * @author 谷松磊
	// * @Description 版本更新进度对话框
	// * @return void
	// * @date 2015年1月15日 上午12:03:35
	// */
	// private void createDownloadInfoDialog() {
	// if (downloadDialog != null || isDownloading) {
	// downloadDialog.show();
	// return;
	// }
	// downloadDialog = new CustomDialog(LoadingActivity.this,
	// R.style.custom_dialog_style, R.layout.dialog_version_update_select_view);
	// downloadDialog.show();
	//
	// /*
	// * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
	// * 对象,这样这可以以同样的方式改变这个Activity的属性.
	// */
	// Window dialogWindow = downloadDialog.getWindow();
	// WindowManager.LayoutParams lp = dialogWindow.getAttributes();
	// lp.width = getResources().getDisplayMetrics().widthPixels;
	// // 一定要在show()方法之后
	// dialogWindow.setAttributes(lp);
	// progressBar = (ProgressBar)
	// downloadDialog.findViewById(R.id.pb_dialog_version_update);
	// tvProgressView = (TextView)
	// downloadDialog.findViewById(R.id.tv_dialog_top_title);
	//
	// startDownload();
	// }
	//
	// public void startDownload() {
	// if (verionUpdateUrl == null || verionUpdateUrl.equals("")) {
	// Toast.makeText(getApplicationContext(), "无效的Url地址！", 500).show();
	// return;
	// }
	//
	// if
	// (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
	// {
	// File saveDir = new
	// File(Environment.getExternalStorageDirectory().getPath() +
	// Constant.DOWNLOAD_FILE_PATH);
	// if (!saveDir.exists()) {
	// saveDir.mkdirs();
	// }
	// download(verionUpdateUrl, saveDir);
	// isDownloading = true;
	// } else {
	// Toast.makeText(getApplicationContext(), "内存不足或者内存卡不存在！", 500).show();
	// }
	// }
	//
	// private void download(String path, File saveDir) {
	// task = new DownloadTask(path, saveDir);
	// new Thread(task).start();
	// }
	//
	// private DownloadTask task;
	//
	// private final class DownloadTask implements Runnable {
	// private String path;
	// private File saveDir;
	// private FileDownloader fileDownloader;
	//
	// public DownloadTask(String path, File saveDir) {
	// this.path = path;
	// this.saveDir = saveDir;
	// }
	//
	// public void exit() {
	// if (fileDownloader != null) {
	// fileDownloader.exit();
	// }
	// }
	//
	// private DownloadProgressListener downloadProgressListener = new
	// DownloadProgressListener() {
	// @Override
	// public void OnDownloadSize(int size) {
	// Message msg = new Message();
	// msg.what = PROCESSING;
	// msg.getData().putInt("size", size);
	// handlerdown.sendMessage(msg);
	// }
	// };
	//
	// @Override
	// public void run() {
	// try {
	// fileDownloader = new FileDownloader(getApplicationContext(), path,
	// saveDir, 1);
	// filePath = fileDownloader.getFileAbsolutePath();
	// progressBar.setMax(fileDownloader.getFileSize());
	// fileDownloader.download(downloadProgressListener);
	// } catch (Exception e) {
	// e.printStackTrace();
	// handlerdown.sendMessage(handler.obtainMessage(FAILUER));
	// }
	// }
	//
	// }
	//
	// private void exit() {
	// if (task != null) {
	// task.exit();
	// }
	// }

	// /**
	// * @author 谷松磊
	// * @Description 安装APK文件
	// * @return void
	// * @date 2015年1月15日 上午2:35:07
	// */
	// private void openApkFile(File filePath) {
	// Intent intent = new Intent();
	// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// intent.setAction(android.content.Intent.ACTION_VIEW);
	// intent.setDataAndType(Uri.fromFile(filePath),
	// "application/vnd.android.package-archive");
	// startActivity(intent);
	// }
	//
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
	// if (is_update == 1) {
	// // Noupdate(0);
	// } else {
	// System.exit(0);
	// }
	//
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	// @Override
	// protected void onResume() {
	// // TODO Auto-generated method stub
	// super.onResume();
	// Noupdate();
	// }

}
