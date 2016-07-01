// Copyright 2011 Google Inc. All Rights Reserved.

package com.rjb.dianfeng.fileexchange.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rjb.dianfeng.fileexchange.Constant;
import com.rjb.dianfeng.fileexchange.DeviceListActivity;
import com.rjb.dianfeng.fileexchange.database.FileRecordDB_dao;
import com.rjb.dianfeng.fileexchange.utils.ProgressDialogUtil;
import com.rjb.dianfeng.fileexchange.utils.TransmissionSpeed;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService {

	private static final int SOCKET_TIMEOUT = 15000;
	public static final String ACTION_SEND_FILE = "com.rjb.dianfeng.fileexchange.SEND_FILE";
	public static final String EXTRAS_FILE_PATH = "file_path";
	public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
	public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";

	private Handler mHandler;
	private static final int WHAT_UPDATE = 1;// ���½���
	private static final int RECORD_FILE_SENT = 2;// ��¼����

	private int file_num;// Ҫ������ļ�����
	private int current_file;// ��ǰ������ļ�xiabiao
	ProgressDialog progressDialog;
	private boolean isProgressDialogExist = false;
	private boolean isProgressDialogInstantiated = false;// ʵ����������û��

	private int test_size;

	// private SharedPreferences preferences;
	// private Editor editor;

	public FileTransferService(String name) {
		super(name);
	}

	public FileTransferService() {
		super("FileTransferService");
		Log.i(Constant.TAG, "���ͷ���Ĺ�����    ��ǰ�߳�"
				+ Thread.currentThread().getName());
		// createProgressDialog
		instantiateProgressDialog();
		// ������֤ ���ô˹����� �����߳�
		createHandler();
	}

	public void instantiateProgressDialog() {
		DeviceListActivity listActivity = (DeviceListActivity) Constant.activities
				.get("DeviceListActivity");
		if (listActivity != null) {
			progressDialog = new ProgressDialog(listActivity);
			isProgressDialogInstantiated = true;
		}
	}

	@SuppressLint("HandlerLeak")
	private void createHandler() {
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case WHAT_UPDATE:
					int values[] = (int[]) msg.obj;
					Bundle bundle = msg.getData();
					if (bundle != null) {
						handleResult(values, bundle.getDouble("speed"));
						Log.i("SPEED", "SPEED NOW:"+bundle.getDouble("speed"));
					}

					break;
				case RECORD_FILE_SENT:
					File file = (File) msg.obj;
					recordFile(file, msg.arg1, msg.arg2);
					break;
				}
				super.handleMessage(msg);
			}

			private void handleResult(int[] values, double speed) {
				if (isProgressDialogInstantiated) {
					if (isProgressDialogExist) {
						int current = values[0];
						int current_size = values[1];
						int total_size = values[2];
						if (current == current_file) {// ������ļ����±�== current_file
							if (progressDialog != null
									&& progressDialog.isShowing()) {
								// setProgress(current_size, total_size,
								// current);
								ProgressDialogUtil.setProgress(progressDialog,
										current_size, speed, false, current,
										file_num);// ���ý���
								if (current_size == total_size) {
									// resetMax();
									ProgressDialogUtil
											.resetProgress(progressDialog);
								}
							} else {// progressDialog û��show
								ProgressDialogUtil.showProgressDialog(
										progressDialog, total_size, current,
										file_num, false);
							}
						} else {
							current_file = current;
							// ���� max �� message
							ProgressDialogUtil.resetMaxAndMessage(
									progressDialog, total_size, current,
									file_num, false);
						}
					} else {
						// createProgressDialog(fragment.getActivity());
						ProgressDialogUtil.createProgressDialog(progressDialog,
								false);
						isProgressDialogExist = true;
					}
				} else {
					instantiateProgressDialog();// ʵ����
				}
			}
		};
	}

	protected void recordFile(File file, int arg1, int arg2) {
		FileRecordDB_dao dao = new FileRecordDB_dao(getApplicationContext());
		dao.insertToFSR(file.getAbsolutePath(), String.valueOf(file.length()));
		if ((arg1 + 1) == arg2) {// ��������
			cancleProgressDialog();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		// Context context = getApplicationContext();
		// preferences = getSharedPreferences("host_file",
		// Context.MODE_PRIVATE);
		// editor = preferences.edit();
		Log.i(Constant.TAG, "��ʼ����");

		if (intent.getAction().equals(ACTION_SEND_FILE)) {

			// ��ȡ host �� port
			String host = intent.getExtras().getString(
					EXTRAS_GROUP_OWNER_ADDRESS);
			int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
			Socket socket = new Socket();
			try {
				socket.bind(null);
				socket.connect((new InetSocketAddress(host, port)),
						SOCKET_TIMEOUT);
				Log.i(Constant.TAG,
						"socket ����buffer" + socket.getSendBufferSize());
				Log.i(Constant.TAG,
						"socket ����buffer" + socket.getReceiveBufferSize());

				// ��ȡ�ļ�·�� ����
				String[] path = intent.getExtras().getStringArray(
						EXTRAS_FILE_PATH);
				// ���͵��ļ�����
				int send_num = path.length;
				this.file_num = send_num;
				// д���ļ�����
				OutputStream stream = socket.getOutputStream();
				DataOutputStream dataOutputStream = new DataOutputStream(stream);
				dataOutputStream.writeInt(send_num);
				dataOutputStream.flush();
				// ѭ�������ļ��� ������ǰ������һ��ѭ�����������£��߽粻��
				for (int i = 0; i < path.length; i++) {
					String fileName = getNameFromPath(path[i]);
					dataOutputStream.writeUTF(fileName);
					dataOutputStream.flush();
				}
				// ѭ�������ļ���С
				for (int i = 0; i < path.length; i++) {
					File file = new File(path[i]);
					dataOutputStream.writeInt((int) file.length());
					dataOutputStream.flush();
				}
				// ѭ�������ļ�
				FileInputStream fileInputStream = null;
				// InputStream inputStream = null;
				for (int i = 0; i < path.length; i++) {
					int size = 0;// ��¼����Ĵ�С
					File file = new File(path[i]);
					// ContentResolver cr = context.getContentResolver();

					// inputStream =
					// cr.openInputStream(Uri.parse(file.toURI().toString()));//��������
					fileInputStream = new FileInputStream(file);
					byte[] buffer = new byte[1024];
					int len;
					Message msg;
					TransmissionSpeed.init();// ��ʼ��������
					while ((len = fileInputStream.read(buffer)) != -1) {

						test_size += len;
						Log.i(Constant.TAG, "test_size=" + test_size / 1024.0f
								+ " KB");
						dataOutputStream.write(buffer, 0, len);
						dataOutputStream.flush();
						size = size + len;
						double speed = TransmissionSpeed.getSpeed(size);
						// ����message
						msg = new Message();
						Bundle bundle = new Bundle();
						bundle.putDouble("speed", speed);
						int[] values = new int[] { i, size, (int) file.length() };
						msg.obj = values;
						msg.what = WHAT_UPDATE;
						msg.setData(bundle);
						mHandler.sendMessage(msg);
					}
					Log.i(Constant.TAG, "�ļ������Ƿ�ɹ�" + (size == file.length()));
					dataOutputStream.flush();
					if (size == file.length()) {// ��¼���� �����ݿ�
						Message message = new Message();
						message.obj = file;
						message.arg1 = i;
						message.arg2 = path.length;
						message.what = RECORD_FILE_SENT;
						mHandler.sendMessage(message);
					}
				}
				test_size = 0;
				TransmissionSpeed.destroy();
				fileInputStream.close();
				dataOutputStream.close();

				// // ѭ������
				// for (int i = 0; i < path.length; i++) {
				// File file = new File(path[i]);
				// String fileName = getNameFromPath(path[i]);
				// // д������
				// dataOutputStream.writeUTF(fileName);
				// dataOutputStream.flush();
				// // д���С
				// dataOutputStream.writeLong(file.length());
				// dataOutputStream.flush();
				// // д���ļ�
				// FileInputStream fileInputStream = new FileInputStream(
				// file);
				// byte[] buffer = new byte[1024];
				// int len;
				// while ((len = fileInputStream.read(buffer)) != -1) {
				// dataOutputStream.write(buffer, 0, len);
				// dataOutputStream.flush();
				// }
				//
				// fileInputStream.close();
				// }
				// dataOutputStream.close();

			} catch (IOException e) {
				Log.e(Constant.TAG, e.getMessage() + "   name="
						+ e.getClass().getName());
				test_size = 0;
				cancleProgressDialog();
			} finally {
				if (socket != null) {
					if (socket.isConnected()) {
						try {
							socket.close();
						} catch (IOException e) {
							// Give up
							e.printStackTrace();
						}
					}
				}
				// editor.putString("host", "");
				// editor.commit();
				// Log.i(Constant.TAG, "�Ѿ����host:"+preferences.getString("host",
				// "clean"));
			}

		}
	}

	public void cancleProgressDialog() {
		mHandler.removeMessages(WHAT_UPDATE);
		mHandler.removeMessages(RECORD_FILE_SENT);
		ProgressDialogUtil.cancleProgressDialog(progressDialog);
	}

	public static String getNameFromPath(String s) {
		int index = s.lastIndexOf("/");
		String name = s.substring(index + 1);
		return name;
	}

}
