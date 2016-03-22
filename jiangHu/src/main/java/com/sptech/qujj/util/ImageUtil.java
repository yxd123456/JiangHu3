package com.sptech.qujj.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.sptech.qujj.LoadingActivity;

public class ImageUtil {
	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static int computeSampleSize(BitmapFactory.Options options,

	int minSideLength, int maxNumOfPixels) {

		int initialSize = computeInitialSampleSize(options, minSideLength,

		maxNumOfPixels);

		int roundedSize;

		if (initialSize <= 8) {

			roundedSize = 1;

			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}

		} else {

			roundedSize = (initialSize + 7) / 8 * 8;

		}

		return roundedSize;

	}

	// Ҫ
	private static int computeInitialSampleSize(BitmapFactory.Options options,

	int minSideLength, int maxNumOfPixels) {

		double w = options.outWidth;

		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 :

		(int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));

		int upperBound = (minSideLength == -1) ? 128 :

		(int) Math.min(Math.floor(w / minSideLength),

		Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {

			// return the larger one when there is no overlapping zone.

			return lowerBound;

		}

		if ((maxNumOfPixels == -1) &&

		(minSideLength == -1)) {

			return 1;

		} else if (minSideLength == -1) {

			return lowerBound;

		} else {

			return upperBound;

		}

	}

	public static double getScaling(double targetWidth, double targetHeight,
			double standardWidth, double standardHeight) {

		double widthScaling = 0d;

		double heightScaling = 0d;

		if (targetWidth > standardWidth) {

			widthScaling = standardWidth / (targetWidth * 1.00d);

		} else {

			widthScaling = 1d;

		}

		if (targetHeight > standardHeight) {

			heightScaling = standardHeight / (targetHeight * 1.00d);

		} else {

			heightScaling = 1d;

		}

		return Math.max(widthScaling, heightScaling);

	}

	public static float GetRotatedegrees(String filepath) {
		try {
			ExifInterface exif = new ExifInterface(filepath);
			int dir = Integer.parseInt(exif
					.getAttribute(ExifInterface.TAG_ORIENTATION));

			// selectedImageUri.
			float degrees = 0;
			if (ExifInterface.ORIENTATION_ROTATE_90 == dir)
				degrees = 90;
			else if (ExifInterface.ORIENTATION_ROTATE_180 == dir)
				degrees = 180;
			else if (ExifInterface.ORIENTATION_ROTATE_270 == dir)
				degrees = 270;

			return degrees;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	public static String GetPicDate(String filepath) {
		try {
			ExifInterface exif = new ExifInterface(filepath);
			String date = exif.getAttribute(ExifInterface.TAG_DATETIME);

			return date;
		} catch (Exception e) {
			// TODO: handle exception
			String msg = e.getMessage();
			Log.d("exception", msg);
			return null;
		}
	}

	public static String GetExifAttrString(String filepath, String attr) {
		try {
			ExifInterface exif = new ExifInterface(filepath);
			String value = exif.getAttribute(attr);

			return value;
		} catch (Exception e) {
			// TODO: handle exception
			String msg = e.getMessage();
			Log.d("exception", msg);
			return null;
		}
	}

	public static float[] GetExifAttrString(String filepath) {
		try {
			ExifInterface exif = new ExifInterface(filepath);

			float[] ret = new float[2];

			Boolean value = exif.getLatLong(ret);

			if (value == true) {
				return ret;
			}
			return null;
		} catch (Exception e) {
			// TODO: handle exception
			String msg = e.getMessage();
			Log.d("exception", msg);
			return null;
		}
	}

	public static void SetExifAttr(String filepath, int dir) {
		try {
			int deg = 0;
			switch (dir) {
			case 90:
				deg = ExifInterface.ORIENTATION_ROTATE_90;
				break;
			case 180:
				deg = ExifInterface.ORIENTATION_ROTATE_180;
				break;
			case 270:
				deg = ExifInterface.ORIENTATION_ROTATE_270;
				break;
			}
			ExifInterface exif = new ExifInterface(filepath);

			exif.setAttribute(ExifInterface.TAG_ORIENTATION,
					Integer.toString(deg));

			exif.saveAttributes();
			// exif.getAttribute(ExifInterface.);
		} catch (Exception e) {
			// TODO: handle exception

		}
	}

	public static Bitmap zoomBitmap(Bitmap bitmap, float degrees) {
		if (bitmap == null)
			return null;
		// return zoomBitmap(bitmap,
		// LoadingActivity.screenHeight,LoadingActivity.screenWidth,degrees);
		return zoomBitmap(bitmap, LoadingActivity.screenHeight,
				LoadingActivity.screenWidth, degrees);
	}

	public static Bitmap zoomBitmapForSrc(Bitmap bitmap, float degrees) {
		if (bitmap == null)
			return null;

		return zoomBitmap(bitmap, 2600, 2600, degrees);

	}

	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h, float degrees) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scale = (float) getScaling(width, height, w, h);

		matrix.postRotate(degrees);
		matrix.postScale(scale, scale);

		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	public static float GetScale(int w, int h) {
		float scale = (float) getScaling(LoadingActivity.screenHeight,
				LoadingActivity.screenWidth, w, h);
		return scale;
	}

	// �Ŵ���СͼƬ
	public static Bitmap zoomBitmap(Bitmap bitmap, float scale, int x, int y,
			int w, int h) {

		Matrix matrix = new Matrix();

		matrix.postScale(scale, scale);

		int srcw = bitmap.getWidth();
		int srch = bitmap.getHeight();

		if (x + w <= srcw && y + h <= srch) {
			Bitmap newbmp = Bitmap.createBitmap(bitmap, x, y, w, h, matrix,
					true);

			return newbmp;
		} else
			return bitmap;
	}

	public static Bitmap cutImage(Bitmap bitmap, int w, int h, int size) {
		if (bitmap == null)
			return null;

		int targetWidth = bitmap.getWidth();
		int targetHeight = bitmap.getHeight();

		int standardWidth = w * size;
		int standardHeight = h * size;

		double widthScaling = 0d;

		double heightScaling = 0d;
		if (targetWidth > standardWidth) {
			widthScaling = standardWidth / (targetWidth * 1.00d);
		} else {
			widthScaling = 1d;
		}

		if (targetHeight > standardHeight) {
			heightScaling = standardHeight / (targetHeight * 1.00d);
		} else {
			heightScaling = 1d;
		}

		int x = 0, y = 0;
		float scale = 0;
		if (widthScaling < heightScaling) {
			x = (int) ((targetWidth - standardWidth / heightScaling) / 2.0);
			y = 0;
			scale = (float) heightScaling;
			standardWidth = (int) (standardWidth / scale);
			standardHeight = (int) (standardHeight / scale);
		} else {
			x = 0;
			y = (int) ((targetHeight - standardHeight / widthScaling) / 3.0);
			scale = (float) widthScaling;
			standardWidth = (int) (standardWidth / scale);
			standardHeight = (int) (standardHeight / scale);
		}

		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;

		return zoomBitmap(bitmap, scale, x, y, standardWidth, standardHeight);
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {

		int width = drawable.getIntrinsicWidth();

		int height = drawable.getIntrinsicHeight();

		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);

		Canvas canvas = new Canvas(bitmap);

		// canvas.dr

		drawable.setBounds(0, 0, width, height);

		drawable.draw(canvas);

		return bitmap;

	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);

		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);

		paint.setColor(color);

		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

		canvas.drawBitmap(bitmap, rect, rect, paint);

		// ColorMatrix cm = new ColorMatrix();

		return output;

	}

	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {

		final int reflectionGap = 4;

		int width = bitmap.getWidth();

		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();

		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);

		canvas.drawBitmap(bitmap, 0, 0, null);

		Paint deafalutPaint = new Paint();

		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);

		paint.setShader(shader);

		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;

	}

	public static boolean SaveImage(boolean isHasSDCard, Bitmap bitmap,
			String filename, ImageType type, int quality) {
		if (bitmap == null)
			return false;
		if (isHasSDCard) {
			File file = new File(filename);
			try {
				BufferedOutputStream out = new BufferedOutputStream(
						new FileOutputStream(file));
				Log.d("ImageUtil", "write to " + file.getAbsolutePath());
				if (type == ImageType.JPEG)
					bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
				else if (type == ImageType.PNG)
					bitmap.compress(Bitmap.CompressFormat.PNG, quality, out);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public static void ProcessImageFile(String srcfileString) {
		String renametoString = srcfileString + ".jpg";

		if (FileHelper.isFileExist(renametoString)) {
			if (!FileHelper.isFileExist(srcfileString)) {
				File file = new File(renametoString);
				File newFile = new File(srcfileString);
				Boolean retsave = file.renameTo(newFile);
				if (retsave) {
					FileHelper.delFile(renametoString);
				}
			} else {
				FileHelper.delFile(renametoString);
			}
		}
	}

	public static Bitmap Bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	public static Bitmap ReadImage(String filepath) {
		if (filepath == null)
			return null;

		File file = new File(filepath);

		Bitmap ret = null;
		if (file.exists()) {
			InputStream in;
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				options.inSampleSize = 2;
				ret = BitmapFactory.decodeFile(filepath, options);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return ret;
	}

	// չʾͼƬ
	public static Bitmap ReadMidImage(String filepath) {
		if (filepath == null)
			return null;

		File file = new File(filepath);

		Bitmap ret = null;
		if (file.exists()) {
			InputStream in;
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				ret = BitmapFactory.decodeFile(filepath, options);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return ret;
	}

	public static Bitmap ReadImage(String filepath, double width, double height) {
		if (filepath == null)
			return null;

		File file = new File(filepath);

		Bitmap ret = null;
		if (file.exists()) {
			InputStream in;
			try {
				/*
				 * if(width == 0 || height == 0){ BitmapFactory.Options options
				 * = new BitmapFactory.Options(); options.inJustDecodeBounds =
				 * false; options.inSampleSize = 2; //BitmapFactory.deco ret =
				 * BitmapFactory.decodeFile(filepath, options); }else{
				 * 
				 * }
				 */
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				options.inSampleSize = 2;
				// BitmapFactory.deco
				ret = BitmapFactory.decodeFile(filepath, options);
				// ret = Bitmap.createScaledBitmap(ret, 50, 50, false);
				// in = new FileInputStream(file);
				// ret = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return ret;
	}

	public static byte[] GetImageStr(String filePath) {
		InputStream in = null;
		byte[] data = null;
		try {
			in = new FileInputStream(filePath);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	// 淘宝图片处理
	// 拍照处理
	public static boolean savePhotoAction(Context context, File file,
			String srcfileString, String midfiString, String thumbString) {
		Bitmap bitmap = null;
		Uri selectedImageUri = Uri.fromFile(file);
		if (selectedImageUri != null) {
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				bitmap = BitmapFactory.decodeStream(
						context.getContentResolver().openInputStream(
								selectedImageUri), null, options);

				int size = Math.max(options.outWidth, options.outHeight);
				int scale = ImageUtil.computeSampleSize(options, -1,
						LoadingActivity.screenWidth
								* LoadingActivity.screenHeight);
				scale = size / 2000 + 1;
				options.inJustDecodeBounds = false;
				options.inSampleSize = scale;
				bitmap = BitmapFactory.decodeStream(
						context.getContentResolver().openInputStream(
								selectedImageUri), null, options);
			} catch (Exception e) {
				e.printStackTrace();
			}

			float degrees = ImageUtil.GetRotatedegrees(srcfileString);
			ImageUtil.SetExifAttr(srcfileString, (int) (degrees));

			Bitmap middleBitmap = ImageUtil.zoomBitmap(bitmap, degrees);
			Bitmap cutImage = ImageUtil.cutImage(middleBitmap, 75, 75, 2);

			boolean ret;

			// ret = SaveImage(hasSDCard(context),middleBitmap, midfiString,
			// ImageType.JPEG,100);
			// if (!ret)
			// return false;

			ret = SaveImage(hasSDCard(context), cutImage, thumbString,
					ImageType.JPEG, 100);
			if (!ret)
				return false;

			if (middleBitmap != null && !middleBitmap.isRecycled())
				middleBitmap.recycle();
			if (cutImage != null && !cutImage.isRecycled())
				cutImage.recycle();

			if (bitmap != null) {
				if (bitmap != null && !bitmap.isRecycled()) {
					bitmap.recycle();
				}
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static boolean saveSDAction(Context context, Uri selectedImageUri,
			String fileStr, String uploadfileStr) {
		Bitmap bitmap = null;
		if (selectedImageUri != null) {
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				bitmap = BitmapFactory.decodeStream(
						context.getContentResolver().openInputStream(
								selectedImageUri), null, options);

				int size = Math.max(options.outWidth, options.outHeight);
				int scale = ImageUtil.computeSampleSize(options, -1,
						LoadingActivity.screenWidth
								* LoadingActivity.screenHeight);
				scale = size / 2000 + 1;
				options.inJustDecodeBounds = false;
				options.inSampleSize = scale;
				bitmap = BitmapFactory.decodeStream(
						context.getContentResolver().openInputStream(
								selectedImageUri), null, options);
			} catch (Exception e) {
				e.printStackTrace();
			}

			float degrees = ImageUtil.GetRotatedegrees(uploadfileStr);
			ImageUtil.SetExifAttr(uploadfileStr, (int) (degrees));

			Bitmap middleBitmap = ImageUtil.zoomBitmap(bitmap, degrees);
			Bitmap cutImage = ImageUtil.cutImage(middleBitmap, 75, 75, 2);

			boolean ret;

			ret = SaveImage(hasSDCard(context), middleBitmap, uploadfileStr,
					ImageType.JPEG, 100);
			if (!ret)
				return false;

			ret = SaveImage(hasSDCard(context), cutImage, fileStr,
					ImageType.JPEG, 100);
			if (!ret)
				return false;

			if (middleBitmap != null && !middleBitmap.isRecycled())
				middleBitmap.recycle();
			if (cutImage != null && !cutImage.isRecycled())
				cutImage.recycle();
			if (bitmap != null && !bitmap.isRecycled())
				bitmap.recycle();

			if (bitmap != null) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static boolean hasSDCard(Context context) {
		boolean hasExternalStorage = Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED);

		return hasExternalStorage;
	}

	public static boolean createFile(Context context) {
		boolean hasExternalStorage = Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED);

		return hasExternalStorage;
	}
}
