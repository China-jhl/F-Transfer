package com.rjb.dianfeng.fileexchange.utils;

import java.lang.ref.WeakReference;

import com.rjb.dianfeng.fileexchange.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

@SuppressWarnings("rawtypes")
public class BitmapWorkerTask extends AsyncTask {
	private final WeakReference imageViewReference;
	public String data = "";
	private Bitmap mPlaceHolderBitmap;
	private Context mContext;

	@SuppressWarnings("unchecked")
	public BitmapWorkerTask(ImageView imageView, Context context) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		imageViewReference = new WeakReference(imageView);
		this.mContext = context;
		mPlaceHolderBitmap = BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.before_load);
	}

	@Override
	protected Object doInBackground(Object... params) {
		data = (String) params[0];
		return SampledBitmap.decodeSampledBitmapFromResource(data, 60, 60);
	}

	@Override
	protected void onPostExecute(Object result) {
		if (isCancelled()) {
			result = null;
		}
		if (imageViewReference != null && result != null) {
			final ImageView imageView = (ImageView) imageViewReference.get();
			final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
			if (this == bitmapWorkerTask && imageView != null) {
				imageView.setImageBitmap((Bitmap) result);
				ImageCache.getInstance().put(data, (Bitmap) result);
			}
		}
	}

	// 被用作检索任务是否已经被分配到指定的 ImageView:
	private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		/**
		 * 为ImageView使用WeakReference 确保了 AsyncTask 所引用的资源可以被GC(garbage
		 * collected)。因为当任务结束时不能 确保 ImageView 仍然存在，因此你必须在 onPostExecute()
		 * 里面去检查引用。这个ImageView 也许已经不存在了，例如，
		 * 在任务结束时用户已经不在那个Activity或者是设备已经发生配置改变(旋转屏幕等)。
		 */
		return null;
	}

	@SuppressWarnings("unchecked")
	public void loadBitmap(String path) {
		ImageView imageView = (ImageView) imageViewReference.get();
		if (cancelPotentialWork(path, imageView)) {
			final BitmapWorkerTask task = new BitmapWorkerTask(imageView,
					mContext);
			final AsyncDrawable asyncDrawable = new AsyncDrawable(
					mContext.getResources(), mPlaceHolderBitmap, task);
			imageView.setImageDrawable(asyncDrawable);
			task.execute(path);
		}
	}

	/**
	 * 在上面的代码示例中， cancelPotentialWork 方法检查确保了另外一个在ImageView中运行的任务得以取消。如果是这样，它
	 * 通过执行 cancel() 方法来取消之前的一个任务. 在小部分情况下, New出来的任务有可能已经存在，这样就不需要执行这个任
	 * 务了。下面演示了如何实现一个 cancelPotentialWork 。
	 * 
	 * @param data
	 * @param imageView
	 * @return
	 */
	public boolean cancelPotentialWork(String data, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
		if (bitmapWorkerTask != null) {
			final String bitmapData = bitmapWorkerTask.data;
			if (bitmapData == "" || bitmapData != data) {
				// Cancel previous task
				bitmapWorkerTask.cancel(true);
			} else {
				// The same work is already in progress
				return false;
			}
		}
		// No task associated with the ImageView 
		return true;
	}

}