package com.sptech.qujj.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.sptech.qujj.APPConstants;
import com.sptech.qujj.model.Banner;

/**
 * Class Name: DbManager.java Function:
 * 
 * Modifications:
 * 
 * @author Arthur DateTime 2012-7-23 下午1:56:17
 * @version 1.0
 */
public class DbManager {

	private static String TAG = "DbManager";

	private static String path;

	private final static byte[] _writeLock = new byte[0];

	/**
	 * 初始化数据库
	 * 
	 * @param context
	 */
	public static void initDB(Context context) {
		path = context.getFilesDir().getPath() + "/" + APPConstants.DBName;
		File[] files = null;
		if (!new File(path).exists()) {
			files = context.getFilesDir().listFiles();
			int i = 0;
			while (i < files.length) {
				files[i].delete();
				i++;
			}
		} else {
			return;
		}
		InputStream is = null;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = context.openFileOutput(APPConstants.DBName, 0);
			is = context.getAssets().open(APPConstants.DBName);
			byte[] bytes = new byte[is.available()];
			is.read(bytes, 0, bytes.length);
			fileOutputStream.write(bytes);
		} catch (IOException e) {
			return;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 用事物插入数据库 yt
	public static void addBannerListTransaction(List<Banner> list) {
		DbConnection conn = null;
		try {
			conn = DbConnection.getConnection(path);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}

		try {
			conn.getDatabase().beginTransaction();
			for (Banner banner : list) {
				int id = Integer.valueOf(banner.getId());
				String title = banner.getTitle();
				String picurl = banner.getPicurl();
				String url = banner.getUrl();
				String createtime = banner.getCreatetime();
				String desc = banner.getDesc();
				int status = banner.getStatus();
				int appversion = banner.getAppversion();
				int priority = 0;
				int isdelete = 0;

				StringBuilder sBuilder = new StringBuilder();
				sBuilder.append("REPLACE into banner(id ,title ,picurl ,url ,createtime,desc,status,appversion,isdelete,priority  ) values( ");
				sBuilder.append("").append(id).append("");
				sBuilder.append(",'").append(title).append("'");
				sBuilder.append(",'").append(picurl).append("'");
				sBuilder.append(",'").append(url).append("'");
				sBuilder.append(",'").append(createtime).append("'");
				sBuilder.append(",'").append(desc).append("'");
				sBuilder.append(",'").append(status).append("'");
				sBuilder.append(",'").append(appversion).append("'");
				sBuilder.append(",'").append(isdelete).append("'");
				sBuilder.append(",'").append(priority).append("'");
				sBuilder.append(")");
				conn.execSQL(sBuilder.toString());
			}
			conn.getDatabase().setTransactionSuccessful();
			conn.getDatabase().endTransaction();
		} catch (SQLException e) {
			throw e;
		} finally {
			conn.close();
		}
	}

	public static List<Banner> queryAllBanner() {
		String sql = "select * from banner where isdelete = 0";
		List<Banner> list = new ArrayList<Banner>();

		DbConnection conn = null;
		try {
			conn = DbConnection.getConnection(path);
		} catch (Exception e1) {
			e1.printStackTrace();
			return list;
		}
		Cursor cursor = conn.query(sql);
		try {
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						// list.add(new Banner(cursor));
					} while (cursor.moveToNext());
				}
			}
		} catch (Exception e) {

		} finally {
			cursor.close();
			conn.close();
		}
		return list;
	}

	// update升级
	/**
	 * 判断数据库是否存�?
	 * 
	 * @return false or true
	 */
	public static boolean checkDataBase(Context context) {
		SQLiteDatabase checkDB = null;
		try {
			checkDB = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READONLY
							| SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		} catch (SQLiteException e) {
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	/**
	 * 判断数据库表中字段是否存在
	 * 
	 * @throws IOException
	 */
	private static Boolean isExistColumn(String column, String table) {
		String sql = "select * from sqlite_master where type='table' and tbl_name = '"
				+ table + "'";
		synchronized (_writeLock) {
			SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(path,
					null);
			try {
				Cursor cursor = database.rawQuery(sql, null);
				int index = cursor.getColumnIndex("sql");
				if (cursor.moveToFirst()) {
					sql = cursor.getString(index);
					if (sql.indexOf(column) != -1)
						return true;
					return false;
				}
				cursor.close();
				return false;
			} catch (Exception e) {

			} finally {
				database.close();
			}
			return false;
		}
	}

	/**
	 * 
	 * @throws IOException
	 */

	public static void updateDataBase(Context context) throws IOException {
		// 数据库第一步：判断数据库是否存在...

	}

	public static void ExcuteSql(String sql) {
		synchronized (_writeLock) {
			SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(path,
					null);
			database.beginTransaction();
			try {
				database.execSQL(sql);
				database.setTransactionSuccessful();
			} catch (SQLException e) {
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				database.endTransaction();
			}
		}
	}

}
