/**  
 * Copyright © 2015 蓝色互动. All rights reserved.
 *
 * @Title DownloaderDatabase.java
 * @Prject XimanColorUser
 * @Package com.bm.ximancoloruser.utils.downloader
 * @Description TODO
 * @author eric  
 * @date 2015年1月10日 上午12:24:34
 * @version V1.0  
 */
package com.sptech.qujj.utils.downloader;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Copyright © 2015 蓝色互动. All rights reserved.
 * 
 * @Title DownloaderDatabase.java
 * @Prject XimanColorUser
 * @Package com.bm.ximancoloruser.utils.downloader
 * @Description 有关下载器数据库的常量集合类
 * @author 谷松磊
 * @date 2015年1月10日 上午12:24:34
 * @version V1.0
 */
public class DownloaderDatabase {

	public final static String DATABASE_NAME = "downloader.db";

	public final static int DATABASE_VERSION = 1;

	public static final String AUTHORITY = "com.sptech.qujj.utils.database.downloader.provider";
	public static final Uri SQLITE_SEQUENCE = Uri.parse("content://" + AUTHORITY + "/sqlite_sequence");

	// 相关数据的表
	public static final class Downloader implements BaseColumns {
		public static final String TABLE_NAME = "downloader_info";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/downloader_info");

		// 表的字段
		public static final String PATH = "download_path"; // 下载地址Url
		public static final String THREAD_ID = "thread_id"; // 线程Id
		public static final String DOWN_LENGTH = "down_length"; // 任务大小
	}

}
