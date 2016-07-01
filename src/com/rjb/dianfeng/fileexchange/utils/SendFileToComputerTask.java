package com.rjb.dianfeng.fileexchange.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.rjb.dianfeng.fileexchange.Constant;
import com.rjb.dianfeng.fileexchange.DeviceListActivity;
import com.rjb.dianfeng.fileexchange.database.FileRecordDB_dao;

public class SendFileToComputerTask extends AsyncTask<Void, Integer, Boolean> {

	private String path;
	private Context mContext;
	private String name;
	DeviceListActivity deviceListActivity;
	private int File_index;// 传输文件的下标

	public SendFileToComputerTask(Context mContext, String path, int index) {
		this.path = path;
		this.mContext = mContext;
		this.name = Utils.getNameFromPath(path);
		this.deviceListActivity = (DeviceListActivity) mContext;
		this.File_index = index;
		Log.i(Constant.TAG, "当前index" + File_index);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Log.i(Constant.TAG, "开始传输到电脑");
		if (path != "") {
			String end = "\r\n";
			String twoHyphens = "--";
			String boundary = "*****";
			try {
				URL url = new URL(Constant.url_upload);
				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				/*
				 * Output to the connection. Default is false, set to true
				 * because post method must write something to the connection
				 */
				con.setDoOutput(true);
				/* Read from the connection. Default is true. */
				con.setDoInput(true);
				/* Post cannot use caches */
				con.setUseCaches(false);
				/* Set the post method. Default is GET */
				con.setRequestMethod("POST");
				/* 设置请求属性 */
				con.setRequestProperty("Connection", "Keep-Alive");
				con.setRequestProperty("Charset", "UTF-8");
				con.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				/* 设置StrictMode 否则HTTPURLConnection连接失败，因为这是在主进程中进行网络连接 */
				StrictMode
						.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
								.detectDiskReads().detectDiskWrites()
								.detectNetwork().penaltyLog().build());
				/* 设置DataOutputStream，getOutputStream中默认调用connect() */
				DataOutputStream ds = new DataOutputStream(
						con.getOutputStream()); // output to the connection
				ds.writeBytes(twoHyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; "
						+ "name=\"file\";filename=\"" + name + "\"" + end);
				ds.writeBytes(end);
				/* 取得文件的FileInputStream */
				// ContentResolver cr = mContext.getContentResolver();
				File file = new File(path);
				FileInputStream fileInputStream = new FileInputStream(file);
				// InputStream fStream = cr.openInputStream(Uri.parse(path));
				/* 设置每次写入8192bytes */
				int bufferSize = 8192;
				byte[] buffer = new byte[bufferSize]; // 8k
				int length = -1;
				/* 从文件读取数据至缓冲区 */

				int size = (int) (file.length());
				int len = 0;
				TransmissionSpeed.init();
				while ((length = fileInputStream.read(buffer)) != -1) {
					/* 将资料写入DataOutputStream中 */
					ds.write(buffer, 0, length);
					len += length;
					double speed = TransmissionSpeed.getSpeed(len);
					publishProgress(File_index, len, size, (int) speed);
					Log.i(Constant.TAG, "已经传输" + len / 1024.0f + "kb");
				}
				ds.writeBytes(end);
				ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
				/* 关闭流，写入的东西自动生成Http正文 */
				fileInputStream.close();
				/* 关闭DataOutputStream */
				ds.close();
				/* 从返回的输入流读取响应信息 */
				InputStream is = con.getInputStream(); // input from the
														// connection 正式建立HTTP连接
				int ch;
				StringBuffer b = new StringBuffer();
				while ((ch = is.read()) != -1) {
					b.append((char) ch);

				}

			} catch (Exception e) {
				deviceListActivity.cancleProgressDialog();// 取消progressDialog
				Log.i(Constant.TAG, "错误信息" + e.getMessage());
				return false;
			}

		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if ((File_index + 1) == deviceListActivity.getFile_num()) {
			deviceListActivity.cancleProgressDialog();// 取消progressDialog
		}
		if (result) {
			File file = new File(path);
			recordFile(path, String.valueOf(file.length()));
			Toast.makeText(mContext, file.getName() + "传输成功",
					Toast.LENGTH_SHORT).show();

		} else {
			Toast.makeText(mContext, "传输失败", Toast.LENGTH_SHORT).show();
		}
		super.onPostExecute(result);
	}

	private void recordFile(String path, String size) {
		FileRecordDB_dao db_dao = new FileRecordDB_dao(mContext);
		db_dao.insertToFSR(path, size);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		deviceListActivity.onProgressUpdate(values);
	}
}
