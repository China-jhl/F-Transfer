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

	// ���������������Ƿ��Ѿ������䵽ָ���� ImageView:
	private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		/**
		 * ΪImageViewʹ��WeakReference ȷ���� AsyncTask �����õ���Դ���Ա�GC(garbage
		 * collected)����Ϊ���������ʱ���� ȷ�� ImageView ��Ȼ���ڣ����������� onPostExecute()
		 * ����ȥ������á����ImageView Ҳ���Ѿ��������ˣ����磬
		 * ���������ʱ�û��Ѿ������Ǹ�Activity�������豸�Ѿ��������øı�(��ת��Ļ��)��
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
	 * ������Ĵ���ʾ���У� cancelPotentialWork �������ȷ��������һ����ImageView�����е��������ȡ�����������������
	 * ͨ��ִ�� cancel() ������ȡ��֮ǰ��һ������. ��С���������, New�����������п����Ѿ����ڣ������Ͳ���Ҫִ�������
	 * ���ˡ�������ʾ�����ʵ��һ�� cancelPotentialWork ��
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