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
	private int File_index;// �����ļ����±�

	public SendFileToComputerTask(Context mContext, String path, int index) {
		this.path = path;
		this.mContext = mContext;
		this.name = Utils.getNameFromPath(path);
		this.deviceListActivity = (DeviceListActivity) mContext;
		this.File_index = index;
		Log.i(Constant.TAG, "��ǰindex" + File_index);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Log.i(Constant.TAG, "��ʼ���䵽����");
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
				/* ������������ */
				con.setRequestProperty("Connection", "Keep-Alive");
				con.setRequestProperty("Charset", "UTF-8");
				con.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				/* ����StrictMode ����HTTPURLConnection����ʧ�ܣ���Ϊ�������������н����������� */
				StrictMode
						.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
								.detectDiskReads().detectDiskWrites()
								.detectNetwork().penaltyLog().build());
				/* ����DataOutputStream��getOutputStream��Ĭ�ϵ���connect() */
				DataOutputStream ds = new DataOutputStream(
						con.getOutputStream()); // output to the connection
				ds.writeBytes(twoHyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; "
						+ "name=\"file\";filename=\"" + name + "\"" + end);
				ds.writeBytes(end);
				/* ȡ���ļ���FileInputStream */
				// ContentResolver cr = mContext.getContentResolver();
				File file = new File(path);
				FileInputStream fileInputStream = new FileInputStream(file);
				// InputStream fStream = cr.openInputStream(Uri.parse(path));
				/* ����ÿ��д��8192bytes */
				int bufferSize = 8192;
				byte[] buffer = new byte[bufferSize]; // 8k
				int length = -1;
				/* ���ļ���ȡ������������ */

				int size = (int) (file.length());
				int len = 0;
				TransmissionSpeed.init();
				while ((length = fileInputStream.read(buffer)) != -1) {
					/* ������д��DataOutputStream�� */
					ds.write(buffer, 0, length);
					len += length;
					double speed = TransmissionSpeed.getSpeed(len);
					publishProgress(File_index, len, size, (int) speed);
					Log.i(Constant.TAG, "�Ѿ�����" + len / 1024.0f + "kb");
				}
				ds.writeBytes(end);
				ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
				/* �ر�����д��Ķ����Զ�����Http���� */
				fileInputStream.close();
				/* �ر�DataOutputStream */
				ds.close();
				/* �ӷ��ص���������ȡ��Ӧ��Ϣ */
				InputStream is = con.getInputStream(); // input from the
														// connection ��ʽ����HTTP����
				int ch;
				StringBuffer b = new StringBuffer();
				while ((ch = is.read()) != -1) {
					b.append((char) ch);

				}

			} catch (Exception e) {
				deviceListActivity.cancleProgressDialog();// ȡ��progressDialog
				Log.i(Constant.TAG, "������Ϣ" + e.getMessage());
				return false;
			}

		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if ((File_index + 1) == deviceListActivity.getFile_num()) {
			deviceListActivity.cancleProgressDialog();// ȡ��progressDialog
		}
		if (result) {
			File file = new File(path);
			recordFile(path, String.valueOf(file.length()));
			Toast.makeText(mContext, file.getName() + "����ɹ�",
					Toast.LENGTH_SHORT).show();

		} else {
			Toast.makeText(mContext, "����ʧ��", Toast.LENGTH_SHORT).show();
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
