package com.rjb.dianfeng.fileexchange.utils;

import java.lang.ref.WeakReference;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

/**
 * 将BitmapDrawable 与 BitmapWorkerTask 结合起来
 * 
 * @author 龙
 * 
 */
class AsyncDrawable extends BitmapDrawable {
	@SuppressWarnings("rawtypes")
	private final WeakReference bitmapWorkerTaskReference;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AsyncDrawable(Resources res, Bitmap bitmap,
			BitmapWorkerTask bitmapWorkerTask) {
		super(res, bitmap);
		bitmapWorkerTaskReference = new WeakReference(bitmapWorkerTask);
	}


	public BitmapWorkerTask getBitmapWorkerTask() {
		return (BitmapWorkerTask) bitmapWorkerTaskReference.get();
	}
}
