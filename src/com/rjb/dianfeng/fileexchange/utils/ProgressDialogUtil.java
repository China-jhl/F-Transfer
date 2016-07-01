package com.rjb.dianfeng.fileexchange.utils;

import android.app.ProgressDialog;
import android.util.Log;

import com.rjb.dianfeng.fileexchange.Constant;
import com.rjb.dianfeng.fileexchange.R;

public class ProgressDialogUtil {
	private static String mSpeed;

	public static void resetProgress(ProgressDialog progressDialog) {
		if (progressDialog != null) {
			progressDialog.setProgress(0);
			Log.i(Constant.TAG, "resetProgress");
		}
	}

	public static void setProgress(ProgressDialog progressDialog,
			int current_size, double speed, boolean isReceive, int current,
			int file_num) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.setProgress(current_size);
			mSpeed = String.valueOf(speed);
			if (mSpeed.length() > 3) {
				mSpeed = mSpeed.substring(0, 4) + " Mb/s";
			}else if(mSpeed.equals("0.0")){
				mSpeed = "0.00 Mb/s";
			}
			Log.i(Constant.TAG, "setProgress");

			if (isReceive) {
				progressDialog.setMessage("已接收：" + "(" + current + "/"
						+ file_num + ")" + "      " + mSpeed);
			} else {
				progressDialog.setMessage("已发送：" + "(" + current + "/"
						+ file_num + ")" + "   " + mSpeed);
			}
		}
	}

	public static void createProgressDialog(ProgressDialog progressDialog,
			boolean isReceive) {
		if (progressDialog != null) {
			progressDialog.setIcon(R.drawable.ic_launcher);
			if (isReceive) {
				progressDialog.setTitle("文件接收中...");
			} else {
				progressDialog.setTitle("文件发送中...");
			}
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setCancelable(false);
			Log.i(Constant.TAG, "createProgressDialog");
		}
	}

	public static void showProgressDialog(ProgressDialog progressDialog,
			int total_size, int current, int file_num, boolean isReceive) {
		if (progressDialog != null) {
			progressDialog.setMax(total_size);
			if (isReceive) {
				progressDialog.setMessage("已接收：" + "(" + current + "/"
						+ file_num + ")");
			} else {
				progressDialog.setMessage("已发送：" + "(" + current + "/"
						+ file_num + ")");
			}
			progressDialog.show();
			Log.i(Constant.TAG, "showProgressDialog");
		}
	}

	public static void cancleProgressDialog(ProgressDialog progressDialog) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.cancel();
			Log.i(Constant.TAG, "cancleProgressDialog");
		}
	}

	public static void resetMaxAndMessage(ProgressDialog progressDialog,
			int total_size, int current, int file_num, boolean isRceive) {
		if (progressDialog != null) {
			progressDialog.setMax(total_size);
			if (isRceive) {
				progressDialog.setMessage("已接收：" + "(" + current + "/"
						+ file_num + "）");
			} else {
				progressDialog.setMessage("已发送：" + "(" + current + "/"
						+ file_num + "）");
			}
			Log.i(Constant.TAG, "resetMaxAndMessage");
		}
	}
}
