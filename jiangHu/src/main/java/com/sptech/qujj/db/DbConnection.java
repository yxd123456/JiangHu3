package com.sptech.qujj.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
*  Class Name: DbConnection.java
*  Function:
*  
*     Modifications:   
*  
*  @author James  DateTime 2012-8-17 ����6:17:24    
*  @version 1.0
*/
public class DbConnection {

	private static DbConnection dbConnection ;
	
	private SQLiteDatabase database;
	
	public static void init(String path) {
		dbConnection = new DbConnection(path);
	}

	
	public static DbConnection getInstance() {
		if (dbConnection == null) {
			throw new NullPointerException("NOT INIT DbConnection,please call init in app first");
		}
		return dbConnection;
	}

	private DbConnection(String path) {
		 database = SQLiteDatabase.openDatabase(path,
				null,SQLiteDatabase.OPEN_READONLY);
	}

	DbConnection(SQLiteDatabase database) {
		this.database = database;
	}

	public static synchronized DbConnection getConnection(String path)
			throws Exception {
		return new DbConnection(SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE));
	}
	
	public Cursor  query(String sql) {
		return database.rawQuery(sql, null);
	}
	
	public synchronized void execSQL(String sql) {
		database.execSQL(sql);
	}
	
	public void close() {
		database.close();
	}

	public SQLiteDatabase getDatabase() {
		return database;
	}
	
	

}
