package com.sptech.qujj.utils.downloader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.sptech.qujj.utils.downloader.DownloaderDatabase.Downloader;

public class DownloaderDatabaseHelper extends SQLiteOpenHelper {

	private SQLiteDatabase db;

	public DownloaderDatabaseHelper(Context context) {
		super(context, DownloaderDatabase.DATABASE_NAME, null, DownloaderDatabase.DATABASE_VERSION);
	}

	public SQLiteDatabase getSQLiteDatabase() {
		db = getWritableDatabase();
		return db;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createDownloaderTable(db);
	}

	private void createDownloaderTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Downloader.TABLE_NAME + "(" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + Downloader.PATH + " TEXT," + Downloader.THREAD_ID + " INTEGER,"
				+ Downloader.DOWN_LENGTH + " INTEGER);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXIST downloader_info");
		onCreate(db);
	}
}
