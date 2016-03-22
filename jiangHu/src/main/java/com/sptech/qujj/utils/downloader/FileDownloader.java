package com.sptech.qujj.utils.downloader;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;

public class FileDownloader {
	private static final String TAG = "FileDownloader";
	private Context context;
	private FileService fileService;
	private boolean exited;

	/* 已下载文件长度 */
	private int downloadSize = 0;

	/* 原始文件长度 */
	private int fileSize = 0;

	/* 线程数 */
	private DownloadThread[] threads;

	/* 本地保存文件 */
	private File saveFile;

	/* 缓存各线程下载的长度 */
	private Map<Integer, Integer> data = new ConcurrentHashMap<Integer, Integer>();

	/* 每条线程下载的长度 */
	private int block;

	/* 下载路径 */
	private String downloadUrl;

	/**
	 * 获取线程数
	 */
	public int getThreadSize() {
		return threads.length;
	}

	public void exit() {
		exited = true;
	}

	public boolean getExited() {
		return exited;
	}

	/**
	 * 
	 * 获取文件大小
	 * 
	 * @return
	 */
	public int getFileSize() {
		return fileSize;
	}

	/**
	 * 
	 * 累计已下载大小
	 * 
	 * @param size
	 */
	protected synchronized void append(int size) {
		downloadSize += size;
	}

	/**
	 * 更新指定线程最后下载的位置
	 * 
	 * @param threadId
	 *            线程id
	 * @param pos
	 *            最后下载的位置
	 */
	protected synchronized void update(int threadId, int pos) {
		data.put(threadId, pos);
		fileService.update(downloadUrl, threadId, pos);
	}

	/**
	 * 
	 * 构建文件下载器
	 * 
	 * @param downloadUrl
	 *            下载路径
	 * @param fileSaveDir
	 *            文件保存目录
	 * @param threadNum
	 *            下载线程数
	 */
	public FileDownloader(Context context, String downloadUrl, File fileSaveDir, int threadNum) {
		try {
			this.context = context;
			this.downloadUrl = downloadUrl;
			fileService = new FileService(this.context);
			URL url = new URL(this.downloadUrl);
			if (!fileSaveDir.exists()) {
				fileSaveDir.mkdirs();
			}
			threads = new DownloadThread[threadNum];

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty(
					"Accept",
					"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Referer", this.downloadUrl);
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.connect();
			printResponseHeader(conn);

			if (conn.getResponseCode() == 200) {
				fileSize = conn.getContentLength();// 根据响应获取文件大小
				if (fileSize <= 0)
					throw new RuntimeException("Unkown file size ");

				String filename = getFileName(conn);// 获取文件名称
				saveFile = new File(fileSaveDir, filename);// 构建保存文件
				Map<Integer, Integer> logdata = fileService.getData(this.downloadUrl);// 获取下载记录

				if (logdata.size() > 0) {// 如果存在下载记录
					for (Map.Entry<Integer, Integer> entry : logdata.entrySet())
						data.put(entry.getKey(), entry.getValue());// 把各条线程已经下载的数据长度放入data中
				}

				if (this.data.size() == this.threads.length) {// 下面计算所有线程已经下载的数据长度
					for (int i = 0; i < threads.length; i++) {
						downloadSize += data.get(i + 1);
					}
					System.out.println("已经下载的长度" + downloadSize + "个字节");
					print("已经下载的长度" + this.downloadSize);
				}

				// 计算每条线程下载的数据长度
				block = (fileSize % threads.length) == 0 ? fileSize / threads.length : fileSize / threads.length + 1;

				Log.e("TAG", "fileSize=" + fileSize);
				Log.e("TAG", "block=" + block);

			} else {
				System.out.println("服务器响应错误：" + conn.getResponseCode() + conn.getResponseMessage());
				throw new RuntimeException("server no response ");
			}
		} catch (Exception e) {
			print(e.toString());
			throw new RuntimeException("Can't connect this url!");
		}
	}

	/**
	 * ��ȡ�ļ���
	 * 
	 * @param conn
	 * @return
	 */
	private String getFileName(HttpURLConnection conn) {
		String filename = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);

		if (filename == null || "".equals(filename.trim())) {// 如果获取不到文件名称
			for (int i = 0;; i++) {
				String mine = conn.getHeaderField(i);
				if (mine == null) {
					break;
				}
				if ("content-disposition".equals(conn.getHeaderFieldKey(i).toLowerCase())) {
					Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());
					if (m.find()) {
						return m.group(1);
					}
				}
			}
			filename = UUID.randomUUID() + ".tmp";// 默认取一个文件名
		}
		return filename;
	}

	/**
	 * 开始下载文件
	 * 
	 * @param listener
	 *            监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
	 * @return 已下载文件大小
	 * @throws Exception
	 */

	public int download(DownloadProgressListener listener) throws Exception {
		try {
			RandomAccessFile randOut = new RandomAccessFile(saveFile, "rw");
			if (fileSize > 0) {
				randOut.setLength(fileSize);
			}
			randOut.close();
			URL url = new URL(downloadUrl);

			if (data.size() != threads.length) {
				data.clear();
				for (int i = 0; i < threads.length; i++) {
					data.put(i + 1, 0);
					;// 初始化每条线程已经下载的数据长度为0
				}
			}

			for (int i = 0; i < threads.length; i++) {// 开启线程进行下载
				int downLength = data.get(i + 1);
				if (downLength < block && downloadSize < fileSize) {// 判断线程是否已经完成下载,否则继续下载
					threads[i] = new DownloadThread(this, url, saveFile, block, data.get(i + 1), i + 1);
					threads[i].setPriority(7);
					threads[i].start();
				} else {
					threads[i] = null;
				}
			}
			// 删除记录重新添加
			fileService.delete(downloadUrl);
			fileService.save(downloadUrl, data);
			boolean notFinish = true;// 下载未完成

			while (notFinish) {// 假定全部线程下载完成
				Thread.sleep(900);
				notFinish = false;// 假定全部线程下载完成

				for (int i = 0; i < threads.length; i++) {
					if (threads[i] != null && !threads[i].isFinish()) {// 如果发现线程未完成下载
						notFinish = true;// 设置标志为下载没有完成

						if (threads[i].getDownLength() == -1) {// 如果下载失败,再重新下载
							threads[i] = new DownloadThread(this, url, saveFile, block, data.get(i + 1), i + 1);
							threads[i].setPriority(7);
							threads[i].start();
						}
					}
				}
				if (listener != null) {
					listener.OnDownloadSize(downloadSize);// 通知目前已经下载完成的数据长度
				}
			}
			if (downloadSize == fileSize) {
				fileService.delete(downloadUrl);
			}
		} catch (Exception e) {
			print(e.toString());
			throw new Exception("file download fail");
		}
		return downloadSize;
	}

	/**
	 * 
	 * 获取Http响应头字段
	 * 
	 * @param http
	 * @return
	 */
	public static Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
		Map<String, String> header = new LinkedHashMap<String, String>();
		for (int i = 0;; i++) {
			String mine = http.getHeaderField(i);
			if (mine == null) {
				break;
			}
			header.put(http.getHeaderFieldKey(i), mine);
		}

		return header;
	}

	/**
	 * 
	 * 打印Http头字段
	 * 
	 * @param http
	 */

	public static void printResponseHeader(HttpURLConnection http) {
		Map<String, String> header = getHttpResponseHeader(http);

		for (Map.Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey() != null ? entry.getKey() + ":" : "";
			print(key + entry.getValue());
		}
	}

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public String getFileAbsolutePath() {
		return saveFile.getAbsolutePath();
	}

}
