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
	private static final int WHAT_UPDATE = 1;// 跟新进度
	private static final int RECORD_FILE_SENT = 2;// 记录发送

	private int file_num;// 要传输的文件数量
	private int current_file;// 当前传输的文件xiabiao
	ProgressDialog progressDialog;
	private boolean isProgressDialogExist = false;
	private boolean isProgressDialogInstantiated = false;// 实例化对象了没有

	private int test_size;

	// private SharedPreferences preferences;
	// private Editor editor;

	public FileTransferService(String name) {
		super(name);
	}

	public FileTransferService() {
		super("FileTransferService");
		Log.i(Constant.TAG, "发送服务的构造器    当前线程"
				+ Thread.currentThread().getName());
		// createProgressDialog
		instantiateProgressDialog();
		// 经过验证 调用此构造器 在主线程
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
						if (current == current_file) {// 传输的文件的下标== current_file
							if (progressDialog != null
									&& progressDialog.isShowing()) {
								// setProgress(current_size, total_size,
								// current);
								ProgressDialogUtil.setProgress(progressDialog,
										current_size, speed, false, current,
										file_num);// 设置进度
								if (current_size == total_size) {
									// resetMax();
									ProgressDialogUtil
											.resetProgress(progressDialog);
								}
							} else {// progressDialog 没有show
								ProgressDialogUtil.showProgressDialog(
										progressDialog, total_size, current,
										file_num, false);
							}
						} else {
							current_file = current;
							// 重置 max 和 message
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
					instantiateProgressDialog();// 实例化
				}
			}
		};
	}

	protected void recordFile(File file, int arg1, int arg2) {
		FileRecordDB_dao dao = new FileRecordDB_dao(getApplicationContext());
		dao.insertToFSR(file.getAbsolutePath(), String.valueOf(file.length()));
		if ((arg1 + 1) == arg2) {// 传输完了
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
		Log.i(Constant.TAG, "开始传输");

		if (intent.getAction().equals(ACTION_SEND_FILE)) {

			// 获取 host 和 port
			String host = intent.getExtras().getString(
					EXTRAS_GROUP_OWNER_ADDRESS);
			int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
			Socket socket = new Socket();
			try {
				socket.bind(null);
				socket.connect((new InetSocketAddress(host, port)),
						SOCKET_TIMEOUT);
				Log.i(Constant.TAG,
						"socket 发送buffer" + socket.getSendBufferSize());
				Log.i(Constant.TAG,
						"socket 接受buffer" + socket.getReceiveBufferSize());

				// 获取文件路径 数据
				String[] path = intent.getExtras().getStringArray(
						EXTRAS_FILE_PATH);
				// 传送的文件数量
				int send_num = path.length;
				this.file_num = send_num;
				// 写入文件数量
				OutputStream stream = socket.getOutputStream();
				DataOutputStream dataOutputStream = new DataOutputStream(stream);
				dataOutputStream.writeInt(send_num);
				dataOutputStream.flush();
				// 循环读入文件名 按照以前那种在一个循环中做三件事，边界不清
				for (int i = 0; i < path.length; i++) {
					String fileName = getNameFromPath(path[i]);
					dataOutputStream.writeUTF(fileName);
					dataOutputStream.flush();
				}
				// 循环读入文件大小
				for (int i = 0; i < path.length; i++) {
					File file = new File(path[i]);
					dataOutputStream.writeInt((int) file.length());
					dataOutputStream.flush();
				}
				// 循环读入文件
				FileInputStream fileInputStream = null;
				// InputStream inputStream = null;
				for (int i = 0; i < path.length; i++) {
					int size = 0;// 记录传输的大小
					File file = new File(path[i]);
					// ContentResolver cr = context.getContentResolver();

					// inputStream =
					// cr.openInputStream(Uri.parse(file.toURI().toString()));//试试这种
					fileInputStream = new FileInputStream(file);
					byte[] buffer = new byte[1024];
					int len;
					Message msg;
					TransmissionSpeed.init();// 开始计算速率
					while ((len = fileInputStream.read(buffer)) != -1) {

						test_size += len;
						Log.i(Constant.TAG, "test_size=" + test_size / 1024.0f
								+ " KB");
						dataOutputStream.write(buffer, 0, len);
						dataOutputStream.flush();
						size = size + len;
						double speed = TransmissionSpeed.getSpeed(size);
						// 发送message
						msg = new Message();
						Bundle bundle = new Bundle();
						bundle.putDouble("speed", speed);
						int[] values = new int[] { i, size, (int) file.length() };
						msg.obj = values;
						msg.what = WHAT_UPDATE;
						msg.setData(bundle);
						mHandler.sendMessage(msg);
					}
					Log.i(Constant.TAG, "文件传输是否成功" + (size == file.length()));
					dataOutputStream.flush();
					if (size == file.length()) {// 记录发送 到数据库
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

				// // 循环处理
				// for (int i = 0; i < path.length; i++) {
				// File file = new File(path[i]);
				// String fileName = getNameFromPath(path[i]);
				// // 写入名字
				// dataOutputStream.writeUTF(fileName);
				// dataOutputStream.flush();
				// // 写入大小
				// dataOutputStream.writeLong(file.length());
				// dataOutputStream.flush();
				// // 写入文件
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
				// Log.i(Constant.TAG, "已经清除host:"+preferences.getString("host",
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
