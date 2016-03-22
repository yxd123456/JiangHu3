/**  
 * Copyright © 2015 蓝色互动. All rights reserved.
 *
 * @Title DownloaderProvider.java
 * @Prject XimanColorUser
 * @Package com.bm.ximancoloruser.utils.downloader
 * @Description TODO
 * @author eric  
 * @date 2015年1月10日 上午12:38:51
 * @version V1.0  
 */
package com.sptech.qujj.utils.downloader;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

import com.sptech.qujj.utils.downloader.DownloaderDatabase.Downloader;

/**
 * Copyright © 2015 蓝色互动. All rights reserved.
 * 
 * @Title DownloaderProvider.java
 * @Prject XimanColorUser
 * @Package com.bm.ximancoloruser.utils.downloader
 * @Description 下载器数据库中表的增删改查功能的封装类
 * @author 张成龙
 * @date 2015年1月10日 上午12:38:51
 * @version V1.0
 */
public class DownloaderProvider extends ContentProvider {

	private static UriMatcher uriMatcher;
	private DownloaderDatabaseHelper mDatabaseHelper;

	private static final int DOWNLOADER_INFO_ALL = 1;
	private static final int DOWNLOADER_INFO_SINGLE = 2;

	// 管理表的_id自增长问题
	private static final int SQLITE_SEQUENCE = 3;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(DownloaderDatabase.AUTHORITY, "downloader_info", DOWNLOADER_INFO_ALL);
		uriMatcher.addURI(DownloaderDatabase.AUTHORITY, "downloader_info/#", DOWNLOADER_INFO_SINGLE);
		uriMatcher.addURI(DownloaderDatabase.AUTHORITY, "sqlite_sequence", SQLITE_SEQUENCE);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (uriMatcher.match(uri)) {
		case DOWNLOADER_INFO_ALL:
			qb.setTables(Downloader.TABLE_NAME);
			break;
		default:
			break;
		}
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		int i = db.delete(qb.getTables(), selection, selectionArgs);
		return i;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		if (uriMatcher.match(uri) == DOWNLOADER_INFO_ALL) {
			long rowId = db.insert(Downloader.TABLE_NAME, null, values);
			return Uri.parse(uri + "/" + rowId);
		}
		throw new IllegalArgumentException("UnknownUri" + uri);
	}

	@Override
	public boolean onCreate() {
		mDatabaseHelper = new DownloaderDatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		int match = uriMatcher.match(uri);
		switch (match) {
		case DOWNLOADER_INFO_ALL:
			qb.setTables(Downloader.TABLE_NAME);
			break;
		default:
			throw new IllegalArgumentException("UnknownUri" + uri);
		}
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		Cursor ret = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		if (ret != null) {
			ret.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return ret;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		if (uriMatcher.match(uri) == DOWNLOADER_INFO_ALL) {
			return db.update(Downloader.TABLE_NAME, values, selection, selectionArgs);
		} else if (uriMatcher.match(uri) == DOWNLOADER_INFO_SINGLE) {
			return db.update(Downloader.TABLE_NAME, values, BaseColumns._ID + " = " + uri.getPathSegments().get(1), selectionArgs);
		} else if (uriMatcher.match(uri) == SQLITE_SEQUENCE) {
			return db.update("sqlite_sequence", values, selection, selectionArgs);
		}
		return 0;
	}

}
