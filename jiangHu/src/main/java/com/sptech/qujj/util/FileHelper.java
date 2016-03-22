package com.sptech.qujj.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.sptech.qujj.APPConstants;

public class FileHelper {
	private Context context;
	private String SDPATH;
	private String FILESPATH;

	public static String TAG = "FileHelper";

	public static boolean createFile(String destFileName) {
		File file = new File(destFileName);
		if (file.exists()) {
			Log.d(TAG, "创建单个文件" + destFileName + "失败，目标文件已存在！");
			return false;
		}
		if (destFileName.endsWith(File.separator)) {
			Log.d(TAG, "创建单个文件" + destFileName + "失败，目标文件不能为目录！");
			return false;
		}
		// 判断目标文件所在的目录是否存在
		if (!file.getParentFile().exists()) {
			// 如果目标文件所在的目录不存在，则创建父目录
			Log.d(TAG, "目标文件所在目录不存在，准备创建它！");
			if (!file.getParentFile().mkdirs()) {
				Log.d(TAG, "创建目标文件所在目录失败！");
				return false;
			}
		}
		// 创建目标文件
		try {
			if (file.createNewFile()) {
				Log.d(TAG, "创建单个文件" + destFileName + "成功！");
				return true;
			} else {
				Log.d(TAG, "创建单个文件" + destFileName + "失败！");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "创建单个文件" + destFileName + "失败！" + e.getMessage());
			return false;
		}
	}

	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {
			Log.d(TAG, "创建目录" + destDirName + "失败，目标目录已经存在");
			return false;
		}
		if (!destDirName.endsWith(File.separator)) {
			destDirName = destDirName + File.separator;
		}
		// 创建目录
		if (dir.mkdirs()) {
			Log.d(TAG, "创建目录" + destDirName + "成功！");
			return true;
		} else {
			Log.d(TAG, "创建目录" + destDirName + "失败！");
			return false;
		}
	}

	public FileHelper(Context context) {
		this.context = context;
		SDPATH = Environment.getExternalStorageDirectory().getPath() + "//";
		if (context != null) {
			FILESPATH = this.context.getFilesDir().getPath() + "//";
		}
	}

	/**
	 * 在SD�?�上创建文件
	 * 
	 * @throws IOException
	 */
	public File creatSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 删除SD上的文件
	 * 
	 * @param fileName
	 */
	public boolean delSDFile(String fileName) {
		File file = new File(SDPATH + fileName);
		if (file == null || !file.exists() || file.isDirectory())
			return false;
		file.delete();
		return true;
	}

	public static Boolean isFileExist(String filename) {
		if (filename == null || ("").equals(filename))
			return false;

		File file = new File(filename);
		return file.exists();
	}

	/**
	 * 在SD上创建目录
	 * 
	 * @param dirName
	 */
	public File creatSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		if (!dir.exists())
			dir.mkdir();
		return dir;
	}

	public File createSDSubDir(String dirname) {
		File dir = new File(SDPATH + dirname);
		dir.mkdirs();
		return dir;
	}

	/**
	 * 删除SD上的目录
	 * 
	 * @param dirName
	 */
	public boolean delSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		return delDir(dir);
	}

	/**
	 * 修改SD上的文件或目录
	 * 
	 * @param fileName
	 */
	public boolean renameSDFile(String oldfileName, String newFileName) {
		File oleFile = new File(SDPATH + oldfileName);
		File newFile = new File(SDPATH + newFileName);
		return oleFile.renameTo(newFile);
	}

	/**
	 * 拷�?SD�?�上的�?�个文件
	 * 
	 * @param path
	 * @throws IOException
	 */
	public static boolean copySDFileTo(String srcFileName, String destFileName) {
		if (srcFileName.compareToIgnoreCase(destFileName) == 0)
			return false;

		File srcFile = new File(srcFileName);
		File destFile = new File(destFileName);
		try {
			return copyFileTo(srcFile, destFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 拷�?SD�?�上指定目录的所有文件
	 * 
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public boolean copySDFilesTo(String srcDirName, String destDirName)
			throws IOException {
		File srcDir = new File(SDPATH + srcDirName);
		File destDir = new File(SDPATH + destDirName);
		return copyFilesTo(srcDir, destDir);
	}

	/**
	 * 移动SD�?�上的�?�个文件
	 * 
	 * @param srcFileName
	 * @param destFileName
	 * @return
	 * @throws IOException
	 */
	public boolean moveSDFileTo(String srcFileName, String destFileName)
			throws IOException {
		File srcFile = new File(SDPATH + srcFileName);
		File destFile = new File(SDPATH + destFileName);
		return moveFileTo(srcFile, destFile);
	}

	/**
	 * 移动SD�?�上的指定目录的所有文件
	 * 
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public boolean moveSDFilesTo(String srcDirName, String destDirName)
			throws IOException {
		File srcDir = new File(SDPATH + srcDirName);
		File destDir = new File(SDPATH + destDirName);
		return moveFilesTo(srcDir, destDir);
	}

	/*
	 * 将文件写入sd�?�。如:writeSDFile("test.txt");
	 * 
	 * public Output writeSDFile(String fileName) throws IOException { File file
	 * = new File(SDPATH + fileName); FileOutputStream fos = new
	 * FileOutputStream(file); return new Output(fos); }
	 * 
	 * 
	 * 在原有文件上继续写文件。如:appendSDFile("test.txt");
	 * 
	 * public Output appendSDFile(String fileName) throws IOException { File
	 * file = new File(SDPATH + fileName); FileOutputStream fos = new
	 * FileOutputStream(file, true); return new Output(fos); }
	 * 
	 * 
	 * 从SD�?�读�?�文件。如:readSDFile("test.txt");
	 * 
	 * public Input readSDFile(String fileName) throws IOException { File file =
	 * new File(SDPATH + fileName); FileInputStream fis = new
	 * FileInputStream(file); return new Input(fis); }
	 */

	/**
	 * 建立�?有文件
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File creatDataFile(String fileName) throws IOException {
		File file = new File(FILESPATH + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 建立�?有目录
	 * 
	 * @param dirName
	 * @return
	 */
	public File creatDataDir(String dirName) {
		File dir = new File(FILESPATH + dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * 删除�?有文件
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean delDataFile(String fileName) {
		File file = new File(FILESPATH + fileName);
		return delFile(file);
	}

	/**
	 * 删除�?有目录
	 * 
	 * @param dirName
	 * @return
	 */
	public boolean delDataDir(String dirName) {
		File file = new File(FILESPATH + dirName);
		return delDir(file);
	}

	/**
	 * 更改�?有文件�??
	 * 
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public boolean renameDataFile(String oldName, String newName) {
		File oldFile = new File(FILESPATH + oldName);
		File newFile = new File(FILESPATH + newName);
		return oldFile.renameTo(newFile);
	}

	/**
	 * 在�?有目录下进行文件�?制
	 * 
	 * @param srcFileName
	 *            ： 包�?�路径�?�文件�??
	 * @param destFileName
	 * @return
	 * @throws IOException
	 */
	public boolean copyDataFileTo(String srcFileName, String destFileName)
			throws IOException {
		File srcFile = new File(FILESPATH + srcFileName);
		File destFile = new File(FILESPATH + destFileName);
		return copyFileTo(srcFile, destFile);
	}

	/**
	 * �?制�?有目录里指定目录的所有文件
	 * 
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public boolean copyDataFilesTo(String srcDirName, String destDirName)
			throws IOException {
		File srcDir = new File(FILESPATH + srcDirName);
		File destDir = new File(FILESPATH + destDirName);
		return copyFilesTo(srcDir, destDir);
	}

	/**
	 * 移动�?有目录下的�?�个文件
	 * 
	 * @param srcFileName
	 * @param destFileName
	 * @return
	 * @throws IOException
	 */
	public boolean moveDataFileTo(String srcFileName, String destFileName)
			throws IOException {
		File srcFile = new File(FILESPATH + srcFileName);
		File destFile = new File(FILESPATH + destFileName);
		return moveFileTo(srcFile, destFile);
	}

	/**
	 * 移动�?有目录下的指定目录下的所有文件
	 * 
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public boolean moveDataFilesTo(String srcDirName, String destDirName)
			throws IOException {
		File srcDir = new File(FILESPATH + srcDirName);
		File destDir = new File(FILESPATH + destDirName);
		return moveFilesTo(srcDir, destDir);
	}

	/*
	 * 将文件写入应用�?有的files目录。如:writeFile("test.txt");
	 * 
	 * public Output wirteFile(String fileName) throws IOException {
	 * OutputStream os = context.openFileOutput(fileName,
	 * Context.MODE_WORLD_WRITEABLE); return new Output(os); }
	 * 
	 * 
	 * 在原有文件上继续写文件。如:appendFile("test.txt");
	 * 
	 * public Output appendFile(String fileName) throws IOException {
	 * OutputStream os = context.openFileOutput(fileName, Context.MODE_APPEND);
	 * return new Output(os); }
	 * 
	 * 
	 * 从应用的�?有目录files读�?�文件。如:readFile("test.txt");
	 * 
	 * public Input readFile(String fileName) throws IOException { InputStream
	 * is = context.openFileInput(fileName); return new Input(is); }
	 */

	/**********************************************************************************************************/
	/*********************************************************************************************************/
	/**
	 * 删除一个文件
	 * 
	 * @param file
	 * @return
	 */
	public static boolean delFile(File file) {
		if (file.isDirectory())
			return false;
		return file.delete();
	}

	public static boolean delFile(String filepath) {
		if (filepath == null || ("").equals(filepath))
			return false;
		File file = new File(filepath);
		if (file.isDirectory())
			return false;
		return file.delete();
	}

	/**
	 * 删除一个目录（�?�以是�?�空目录）
	 * 
	 * @param dir
	 */
	public boolean delDir(File dir) {
		if (dir == null || !dir.exists() || dir.isFile()) {
			return false;
		}
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				delDir(file);// 递归
			}
		}
		// dir.delete();
		return true;
	}

	/**
	 * 拷�?一个文件,srcFile�?文件，destFile目标文件
	 * 
	 * @param path
	 * @throws IOException
	 */
	public static boolean copyFileTo(File srcFile, File destFile)
			throws IOException {
		if (srcFile.isDirectory() || destFile.isDirectory())
			return false;// 判断是�?�是文件
		FileInputStream fis = new FileInputStream(srcFile);
		FileOutputStream fos = new FileOutputStream(destFile);
		int readLen = 0;
		byte[] buf = new byte[1024];
		while ((readLen = fis.read(buf)) != -1) {
			fos.write(buf, 0, readLen);
		}
		fos.flush();
		fos.close();
		fis.close();
		return true;
	}

	/**
	 * 拷�?目录下的所有文件到指定目录
	 * 
	 * @param srcDir
	 * @param destDir
	 * @return
	 * @throws IOException
	 */
	public boolean copyFilesTo(File srcDir, File destDir) throws IOException {
		if (!srcDir.isDirectory() || !destDir.isDirectory())
			return false;// 判断是�?�是目录
		if (!destDir.exists())
			return false;// 判断目标目录是�?�存在
		File[] srcFiles = srcDir.listFiles();
		for (int i = 0; i < srcFiles.length; i++) {
			if (srcFiles[i].isFile()) {
				// 获得目标文件
				File destFile = new File(destDir.getPath() + "//"
						+ srcFiles[i].getName());
				copyFileTo(srcFiles[i], destFile);
			} else if (srcFiles[i].isDirectory()) {
				File theDestDir = new File(destDir.getPath() + "//"
						+ srcFiles[i].getName());
				copyFilesTo(srcFiles[i], theDestDir);
			}
		}
		return true;
	}

	/**
	 * 移动一个文件
	 * 
	 * @param srcFile
	 * @param destFile
	 * @return
	 * @throws IOException
	 */
	public boolean moveFileTo(File srcFile, File destFile) throws IOException {
		boolean iscopy = copyFileTo(srcFile, destFile);
		if (!iscopy)
			return false;
		delFile(srcFile);
		return true;
	}

	/**
	 * 移动目录下的所有文件到指定目录
	 * 
	 * @param srcDir
	 * @param destDir
	 * @return
	 * @throws IOException
	 */
	public boolean moveFilesTo(File srcDir, File destDir) throws IOException {
		if (!srcDir.isDirectory() || !destDir.isDirectory()) {
			return false;
		}
		File[] srcDirFiles = srcDir.listFiles();
		for (int i = 0; i < srcDirFiles.length; i++) {
			if (srcDirFiles[i].isFile()) {
				File oneDestFile = new File(destDir.getPath() + "//"
						+ srcDirFiles[i].getName());
				moveFileTo(srcDirFiles[i], oneDestFile);
				delFile(srcDirFiles[i]);
			} else if (srcDirFiles[i].isDirectory()) {
				File oneDestFile = new File(destDir.getPath() + "//"
						+ srcDirFiles[i].getName());
				moveFilesTo(srcDirFiles[i], oneDestFile);
				delDir(srcDirFiles[i]);
			}

		}
		return true;
	}

	public String readFileSize(String file) {
		File dir = new File(SDPATH + file);
		FileInputStream fis;
		try {
			fis = new FileInputStream(dir);
			int fileLen = fis.available();
			return String.valueOf(fileLen);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

		}
		return "";
	}

	public static String getAutoFileOrFilesSize(String filePath) {
		File file = new File(APPConstants.SDCARD);
		long blockSize = 0;
		try {
			if (file.isDirectory()) {
				blockSize = getFileSizes(file);
			} else {
				blockSize = getFileSize(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("获取文件大小", "获取失败!");
		}
		return FormetFileSize(blockSize);
	}

	public static long getFileSize(File file) throws Exception {
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
		} else {
			file.createNewFile();
			Log.e("获取文件大小", "文件不存在!");
		}
		return size;
	}

	private static long getFileSizes(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSizes(flist[i]);
			} else {
				size = size + getFileSize(flist[i]);
			}
		}
		return size;
	}

	private static String FormetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		DecimalFormat df1 = new DecimalFormat("#");
		String fileSizeString = "";
		String wrongSize = "0KB"; // 0KB
		if (fileS == 0) {
			return wrongSize;
		}
		if (fileS < 1048576) {
			fileSizeString = df1.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;
	}

}