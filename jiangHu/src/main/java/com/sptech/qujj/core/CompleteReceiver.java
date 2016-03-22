package com.sptech.qujj.core;

import java.io.File;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.sptech.qujj.APPConstants;
import com.sptech.qujj.db.SPHelper;

public class CompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (APPConstants.APKFILE != null && intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			if (completeDownloadId == SPHelper.getDownApkId()) {
				String fileName = "";
				DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);// 从下载服务获取下载管理器
				DownloadManager.Query query = new DownloadManager.Query();
				query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);// 设置过滤状态：成功
				Cursor c = downloadManager.query(query);// 查询以前下载过的‘成功文件’
				if (c.moveToFirst()) {// 移动到最新下载的文件
					fileName = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
				}
				fileName = fileName.replace("file://", "");
				install(context, fileName);

			}
		}

	}

	public static boolean install(Context context, String filePath) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		File file = new File(filePath);
		if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
			i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
			return true;
		}
		return false;
	}
}