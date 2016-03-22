package com.sptech.qujj.view;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

import android.graphics.Bitmap;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class BitmapSoftRefCache implements ImageCache {

	private static final String TAG = "BitmapSoftRefCache";

	private LinkedHashMap<String, SoftReference<Bitmap>> map;

	public BitmapSoftRefCache() {
		map = new LinkedHashMap<String, SoftReference<Bitmap>>();
	}

	/**
	 * 从软引用集合中得到Bitmap对象
	 */
	@Override
	public Bitmap getBitmap(String url) {
		Bitmap bitmap = null;
		SoftReference<Bitmap> softRef = map.get(url);
		if (softRef != null) {
			bitmap = softRef.get();
			if (bitmap == null) {
				map.remove(url); // 从map中移除
			//	Logger.w(TAG, url + "对象已经被GC回收");
			} else {
			//	Logger.i(TAG, "命中" + url);
			}
		}
		return bitmap;
	}

	/**
	 * 从软引用集合中添加bitmap对象
	 */
	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		SoftReference<Bitmap> softRef = new SoftReference<Bitmap>(bitmap);
		map.put(url, softRef);
	}

}