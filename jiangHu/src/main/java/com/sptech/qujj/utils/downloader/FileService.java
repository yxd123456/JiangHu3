package com.sptech.qujj.utils.downloader;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sptech.qujj.utils.downloader.DownloaderDatabase.Downloader;

public class FileService {
	private Context mContext;
	private String[] projection = new String[] { Downloader.THREAD_ID, Downloader.PATH, Downloader.DOWN_LENGTH };

	public FileService(Context context) {
		mContext = context;
	}

	public Map<Integer, Integer> getData(String path) {
		Cursor cursor = mContext.getContentResolver().query(Downloader.CONTENT_URI, projection, Downloader.PATH + " = " + "'" + path + "'", null, null);
		Map<Integer, Integer> data = new HashMap<Integer, Integer>();
		while (cursor.moveToNext()) {
			data.put(cursor.getInt(cursor.getColumnIndexOrThrow(Downloader.THREAD_ID)), cursor.getInt(cursor.getColumnIndexOrThrow(Downloader.DOWN_LENGTH)));
		}
		cursor.close();
		return data;
	}

	public void save(String path, Map<Integer, Integer> map) {
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			ContentValues values = new ContentValues();
			values.put(Downloader.PATH, path);
			values.put(Downloader.THREAD_ID, entry.getKey());
			values.put(Downloader.DOWN_LENGTH, entry.getValue());
			mContext.getContentResolver().insert(Downloader.CONTENT_URI, values);
		}
	}

	public void update(String path, int threadId, int pos) {
		ContentValues values = new ContentValues();
		values.put(Downloader.DOWN_LENGTH, pos);
		mContext.getContentResolver().update(Downloader.CONTENT_URI, values, Downloader.PATH + " = " + "'" + path + "'" + " and " + Downloader.THREAD_ID + " = " + threadId, null); // 此处
																																													// and
																																													// 不能大写，切记！
	}

	public void delete(String path) {
		mContext.getContentResolver().delete(Downloader.CONTENT_URI, Downloader.PATH + " = " + "'" + path + "'", null);
	}

}
