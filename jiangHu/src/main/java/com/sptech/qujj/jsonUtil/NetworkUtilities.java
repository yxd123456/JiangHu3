package com.sptech.qujj.jsonUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

final public class NetworkUtilities {
	public static final int HTTP_REQUEST_TIMEOUT_MS = 60 * 1000;

	public static boolean isWifiEnabled(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled() && wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			return true;
		}
		return false;
	}

	public static boolean isNetWorkEnabled(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info == null || !info.isAvailable()) {
			return false;
		} else {
			return true;
		}

	}

	public static URLConnection getURLConnection(String url) {
		URLConnection connection = null;
		try {
			connection = new URL(url).openConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	public static String doPost(String fileString, Activity activity) throws Exception {
		HttpURLConnection connection = null;
		DataOutputStream dos = null;
		FileInputStream fin = null;
		InputStream inputStream = null;
		String boundary = "---------------------------265001916915724";

		// String urlServer = JsonConfig.PICTURE;
		String lineEnd = "\r\n";
		int bytesAvailable, bufferSize, bytesRead;
		int maxBufferSize = 1 * 1024 * 512;
		byte[] buffer = null;
		try {
			// connection = (HttpURLConnection) getURLConnection(urlServer);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			// 设置请求头内容
			connection.setRequestProperty("Connection", "Keep-Alive");
			// connection.setRequestProperty("Charset", "GB2312");
			connection.setRequestProperty("Content-Type", "text/plain");
			// connection.setRequestProperty("content-type", "text/html");
			connection.setConnectTimeout(30 * 1000);// 因为传照片，多给10秒吧
			connection.setReadTimeout(30 * 1000);
			connection.setUseCaches(true); //

			// SSLContext context = SSLContext.getInstance("TLS");
			// context.init(new KeyManager[0], xtmArray, new SecureRandom());
			// SSLSocketFactory socketFactory = context.getSocketFactory();
			// ((HttpsURLConnection)
			// connection).setSSLSocketFactory(socketFactory);
			// ((HttpsURLConnection)
			// connection).setHostnameVerifier(HOSTNAME_VERIFIER);

			// 伪造请求头
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			connection.connect();
			// 开始伪造POST Data里面的数据
			dos = new DataOutputStream(connection.getOutputStream());
			fin = new FileInputStream(fileString);
			// StringBuilder sb = new StringBuilder();
			// sb.append("--" + boundary + lineEnd);
			// sb.append("Content-Disposition: form-data; name=\"" + "fileName"
			// + "\"" + lineEnd);
			// sb.append(lineEnd);
			// sb.append(jsjj + lineEnd);
			// 公共参数 workNo
			// sb.append("--" + boundary + lineEnd);
			// sb.append("Content-Disposition: form-data; name=\"" + "workNo" +
			// "\"" + lineEnd);
			// sb.append(lineEnd);
			// sb.append(SPHelper.getWorkNo() + lineEnd);
			// deviceId
			// sb.append("--" + boundary + lineEnd);
			// sb.append("Content-Disposition: form-data; name=\"" + "deviceId"
			// + "\"" + lineEnd);
			// sb.append(lineEnd);
			// sb.append(Helper.getImei(activity) + lineEnd);

			// sb.append("--" + boundary + lineEnd);
			// sb.append("Content-Disposition: form-data; name=\"" +
			// "accessToken" + "\"" + lineEnd);
			// sb.append(lineEnd);
			// sb.append(SPHelper.getAccessToken() + lineEnd);

			// sb.append("--" + boundary + lineEnd);
			// sb.append("Content-Disposition: form-data; name=\"" +
			// "refreshToken" + "\"" + lineEnd);
			// sb.append(lineEnd);
			// sb.append(SPHelper.getRefreshToken() + lineEnd);

			// sb.append("--" + boundary + lineEnd);
			// sb.append("Content-Disposition: form-data; name=\"" + "platform"
			// + "\"" + lineEnd);
			// sb.append(lineEnd);
			// sb.append("android" + lineEnd);

			// sb.append("--" + boundary + lineEnd);
			// sb.append("Content-Disposition: form-data; name=\"" + "versionNo"
			// + "\"" + lineEnd);
			// sb.append(lineEnd);
			// sb.append(Constants.VERSIONNO + lineEnd);

			// sb.append("--" + boundary + lineEnd);
			// sb.append("Content-Disposition: form-data; name=\"" +
			// "deviceInfo" + "\"" + lineEnd);
			// sb.append(lineEnd);
			// sb.append((Helper.getBrand() + " "+ Helper.getOS()) + lineEnd);

			// sb.append("--" + boundary + lineEnd);
			// sb.append("Content-Disposition: form-data; name=\"" + "pushToken"
			// + "\"" + lineEnd);
			// sb.append(lineEnd);
			// sb.append(SPHelper.getCilentId() + lineEnd);

			// sb.append("--" + boundary + lineEnd);
			// sb.append("Content-Disposition: form-data; name=\"" + "loginTime"
			// + "\"" + lineEnd);
			// sb.append(lineEnd);
			// sb.append(SPHelper.getLoginTime() + lineEnd);

			// sb.append("--" + boundary + lineEnd);
			// sb.append("Content-Disposition: form-data; name=\"" + "passTime"
			// + "\"" + lineEnd);
			// sb.append(lineEnd);
			// sb.append(passtime + lineEnd);

			// sb.append("--" + boundary + lineEnd);
			// sb.append("Content-Disposition: form-data; name=\"" + "sign" +
			// "\"" + lineEnd);
			// sb.append(lineEnd);
			// sb.append(Tools.createPwd("rocky"+"&"+Helper.getImei(activity) +
			// "&"+SPHelper.getAccessToken()+ "&" + SPHelper.getRefreshToken()+
			// "&" + passtime) + lineEnd);
			// dos.writeBytes(sb.toString());// 发送表单字段数据

			// name=upload_file 对应的就是上传图片的file的参数名称
			String fileMeta = "--" + boundary + lineEnd + "Content-Disposition: form-data; name=upload_file ; fileName=file" + lineEnd
			        + "Content-Type: content/unknown\r\nContent-Transfer-Encoding: binary" + lineEnd + lineEnd;

			// 向流中写入fileMeta
			dos.write(fileMeta.getBytes());
			// 取得本地图片的字节流，向url流中写入图片字节流
			bytesAvailable = fin.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			bytesRead = fin.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fin.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fin.read(buffer, 0, bufferSize);
			}
			dos.writeBytes(lineEnd + lineEnd);
			// --------------------伪造images.jpg信息结束-----------------------------------
			// Log.d(TAG, "结束上传");
			System.out.println("空空111====" + fileString);
			// POST Data结束
			dos.writeBytes("--" + boundary + "--");
			// Server端返回的信息
			inputStream = connection.getInputStream();
			System.out.println("空空222====" + inputStream);
			if (dos != null) {
				dos.flush();
				dos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertStreamToString(inputStream);
	}

	//
	//
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "/n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
		return sb.toString();
	}

	// 版本更新downloadManager
	// public static void updateApk(Activity activity, String url, String
	// content,
	// DownloadManager downloadManager) {
	//
	// if (!Tools.isNetworkAvailable(activity)) { // 没有网络就不进行下去了..
	// ToastManage.showToast(ErrorMsg.TIMEOUT);
	// return;
	// }
	// boolean hasExternalStorage = Environment.getExternalStorageState()
	// .equals(Environment.MEDIA_MOUNTED);
	// if (!hasExternalStorage) {
	// Intent viewIntent = new Intent("android.intent.action.VIEW",
	// Uri.parse(url));
	// activity.startActivity(viewIntent);
	// return;
	// }
	// File folder = new File(APPConstants.APKFILE);
	// if (!folder.exists() || !folder.isDirectory()) {
	// folder.mkdirs();
	// }
	//
	// DownloadManager.Request request = new DownloadManager.Request(
	// Uri.parse(url));
	// request.setDestinationInExternalPublicDir(APPConstants.APKFILE,
	// APPConstants.APKNAME);
	// request.setTitle("发现新版本");
	// request.setDescription(content);
	// request.setVisibleInDownloadsUi(false);
	// request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
	// | DownloadManager.Request.NETWORK_WIFI);
	// request.setShowRunningNotification(true);
	// // request.allowScanningByMediaScanner();
	// // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
	// //
	// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
	// request.setMimeType("application/con.novem.taobao.download.file");
	// long downloadId = downloadManager.enqueue(request);
	// SPHelper.setDownApkId(downloadId);
	// }
}
