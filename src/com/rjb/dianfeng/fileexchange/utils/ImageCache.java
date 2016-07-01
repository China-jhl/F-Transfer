package com.rjb.dianfeng.fileexchange.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;

public class ImageCache {
	private static int maxMemory = (int) (Runtime.getRuntime().maxMemory());
	private static final int DEFAULT_MEM_CACHE_SIZE = maxMemory / 8; // MB

	protected static final String TAG = "clear";
	private LruCache<String, Bitmap> mMemoryCache;// 一种巧妙地数据结构

	private ImageCache() {
		Log.i(TAG, "" + DEFAULT_MEM_CACHE_SIZE);
		mMemoryCache = new LruCache<String, Bitmap>(DEFAULT_MEM_CACHE_SIZE) {
			@Override
			protected int sizeOf(String key, Bitmap value) {

				return getBitmapSize(value);
			}

			@Override
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				if (evicted) {
					// 被清除出集合的对象,可以将该对象添加到软引用中或者缓存起来
					Log.i(TAG, key.toString());

				}
				super.entryRemoved(evicted, key, oldValue, newValue);
			}

		};
	}

	private static ImageCache cache = new ImageCache();

	public static ImageCache getInstance() {
		return cache;
	}

	@SuppressLint("NewApi")
	public static int getBitmapSize(Bitmap bitmap) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			return bitmap.getByteCount();
		}
		// Pre HC-MR1
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	public void clear() {
		mMemoryCache.evictAll();
	}

	public Bitmap get(String key) {
		Log.i(TAG, "GET" + key);
		return mMemoryCache.get(key);
	}

	public void put(String key, Bitmap bitmap) {
		if (key != null && bitmap != null) {
			mMemoryCache.put(key, bitmap);
		}
		Log.i(TAG, "PUT" + key);
	}

	// private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 8; // 5MB
	// private static final int DIS_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
	// protected static final String TAG = "clear";
	// private LruCache<Object, Bitmap> mMemoryCache;
	//
	// private DiskLruCache diskLruCache;
	//
	// private ImageCache() {
	// mMemoryCache = new LruCache<Object, Bitmap>(DEFAULT_MEM_CACHE_SIZE) {
	// @Override
	// protected int sizeOf(Object key, Bitmap value) {
	//
	// return getBitmapSize(value);
	// }
	//
	// @Override
	// protected void entryRemoved(boolean evicted, Object key,
	// Bitmap oldValue, Bitmap newValue) {
	// if (evicted) {
	// // 被清除出集合的对象,可以将该对象添加到软引用中或者缓存起来
	// Log.i(TAG, key.toString());
	//
	// }
	// super.entryRemoved(evicted, key, oldValue, newValue);
	// }
	//
	// };
	// if (Environment.getExternalStorageState().equals(
	// Environment.MEDIA_MOUNTED)) {
	// File externalStorageDirectory = Environment
	// .getExternalStorageDirectory();
	// String path = externalStorageDirectory.getAbsolutePath()
	// + "/img";
	// diskLruCache = DiskLruCache.openCache(VariableValue.MAIN,
	// new File(path), DIS_CACHE_SIZE);
	// }
	// }
	//
	// private static ImageCache cache = new ImageCache();
	//
	// public static ImageCache getInstance() {
	// return cache;
	// }
	//
	// public Bitmap get(Object key) {
	// Bitmap bitmap = mMemoryCache.get(key);
	// if (bitmap == null) {
	// if (diskLruCache != null)
	// bitmap = diskLruCache.get(key.toString());
	// if (bitmap != null) {
	// mMemoryCache.put(key, bitmap);
	// }
	// }
	//
	// return bitmap;
	// }
	//
	// public void put(Object key, Bitmap bitmap) {
	// mMemoryCache.put(key, bitmap);
	// Log.i(TAG,"PUT"+key);
	// if (diskLruCache != null)
	// diskLruCache.put(key.toString(), bitmap);
	// }
	//
	// @SuppressLint("NewApi") public static int getBitmapSize(Bitmap bitmap) {
	// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
	// return bitmap.getByteCount();
	// }
	// // Pre HC-MR1
	// return bitmap.getRowBytes() * bitmap.getHeight();
	// }
	//
	// public void clear() {
	// mMemoryCache.evictAll();
	// }

}
