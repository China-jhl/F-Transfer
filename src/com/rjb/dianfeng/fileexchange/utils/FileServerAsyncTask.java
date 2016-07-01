package com.rjb.dianfeng.fileexchange.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.rjb.dianfeng.fileexchange.Constant;
import com.rjb.dianfeng.fileexchange.ReceiveFileActivity;
import com.rjb.dianfeng.fileexchange.database.FileRecordDB_dao;
import com.rjb.dianfeng.fileexchange.fragment.FileExchangeFragment;

public class FileServerAsyncTask extends AsyncTask<Void, Integer, String[]> {

	private Context context;
	private FileExchangeFragment fragment;

	private static final int BUFFER_SIZE = 1024;
	private int file_num;// 要传输的文件数量
	private int current_file;// 当前传输的文件xiabiao
	ProgressDialog progressDialog;
	private boolean isProgressDialogExist = false;
	private boolean isProgressDialogInstantiated = false;// 实例化对象了没有

	/**
	 * @param context
	 * @param statusText
	 */
	public FileServerAsyncTask(FileExchangeFragment fragment) {
		this.context = fragment.getActivity();
		this.fragment = fragment;
	}

	public void instantiateProgressDialog() {
		ReceiveFileActivity fileActivity = (ReceiveFileActivity) Constant.activities
				.get("receive_activity");
		if (fileActivity != null) {
			progressDialog = new ProgressDialog(fileActivity);
		}
	}

	@Override
	protected String[] doInBackground(Void... params) {
		try {
			ServerSocket serverSocket = new ServerSocket(Constant.PORT);
			Log.d("SOCKET", "Server: Socket opened");
			Socket client = serverSocket.accept();
			Log.d("SOCKET", "Server: connection done");
			// serverSocket.setReceiveBufferSize(524288);
			client.setReceiveBufferSize(524288);
			Log.i(Constant.TAG,
					"serversocket 接受buffer"
							+ serverSocket.getReceiveBufferSize());
			Log.d("SOCKET", "Server: connection done");
			Log.i(Constant.TAG, "serversocket.accept 得到的socket 接受buffer"
					+ client.getReceiveBufferSize());

			// 获取输入流
			InputStream inputstream = client.getInputStream();
			DataInputStream dataInputStream = new DataInputStream(inputstream);
			// 获取接收文件数量
			int receive_num = dataInputStream.readInt();
			this.file_num = receive_num;
			Log.i(Constant.TAG, "文件数量" + receive_num + "");
			// 路径数组 文件名数组 文件大小数组
			String[] path = new String[receive_num];
			String[] names = new String[receive_num];
			int[] size = new int[receive_num];
			// 循环读出文件名
			for (int i = 0; i < size.length; i++) {
				names[i] = dataInputStream.readUTF();
			}
			// 循环读出文件大小
			for (int i = 0; i < size.length; i++) {
				size[i] = dataInputStream.readInt();
			}
			// 循环读出文件
			for (int i = 0; i < size.length; i++) {
				TransmissionSpeed.init();// 开始计时
				File f = new File(Environment.getExternalStorageDirectory()
						+ "/" + context.getPackageName() + "/" + names[i]);
				path[i] = f.getAbsolutePath();
				File dirs = new File(f.getParent());
				if (!dirs.exists())
					dirs.mkdirs();
				f.createNewFile();
				FileOutputStream fileOutputStream = new FileOutputStream(f);
				byte[] buffer = new byte[BUFFER_SIZE];
				int size2 = 0;
				while (size2 < size[i]) {
					// 处理边界
					int len = 0;
					if ((size[i] - size2) < BUFFER_SIZE) {// 剩下不足1K的内容
						byte[] buffer_left = new byte[(size[i] - size2)];
						len = dataInputStream.read(buffer_left);
					} else {
						len = dataInputStream.read(buffer);
					}

					if (len != -1) {
						fileOutputStream.write(buffer, 0, len);
						fileOutputStream.flush();
						size2 = size2 + len;
						Log.i(Constant.TAG, "文件大小:" + size2 / 1024.0f);
						double speed = TransmissionSpeed.getSpeed(size2);
						publishProgress(i, size2, size[i], (int) speed);
					}
				}
				TransmissionSpeed.destroy();
				fileOutputStream.close();

				// byte[] buffer = new byte[size[i]];
				// dataInputStream.read(buffer);
				// fileOutputStream.write(buffer);
				// fileOutputStream.close();

			}

			//
			// // 循环处理
			// for (int i = 0; i < receive_num; i++) {
			// // 读出名字
			// String filename = dataInputStream.readUTF();
			// // 读出大小
			// long size = dataInputStream.readLong();
			// // 读出文件
			//
			// final File f = new File(
			// Environment.getExternalStorageDirectory() + "/"
			// + context.getPackageName() + "/" + filename);
			// path[i] = f.getAbsolutePath();
			// File dirs = new File(f.getParent());
			// if (!dirs.exists())
			// dirs.mkdirs();
			// f.createNewFile();
			// FileOutputStream fileOutputStream = new FileOutputStream(f);
			// byte[] buffer = new byte[(int) size];
			// dataInputStream.read(buffer);
			// fileOutputStream.write(buffer);
			// fileOutputStream.close();
			// // int len;
			// // while ((len = dataInputStream.read(buffer)) != -1) {
			// //
			// // // }
			// // byte[] buffer = new byte[1024];
			// // long size2 = 0l;
			// // while (size2 < size) {
			// // int len = dataInputStream.read(buffer);
			// // fileOutputStream.write(buffer, 0, len);
			// // fileOutputStream.flush();
			// // size2 = size2 + 1024;
			// // }
			// // fileOutputStream.close();
			// }
			dataInputStream.close();
			serverSocket.close();
			return path;
		} catch (IOException e) {
			Log.e("SOCKET", e.getMessage());
			ProgressDialogUtil.cancleProgressDialog(progressDialog);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String[] result) {
		// fragment.cancleConnect();// 取消连接
		fragment.setCreate(true);// 可以创建Task重新接收
		fragment.startFileTask();// 在开启一个task接受
		if (result != null) {
			// Intent intent = new Intent();
			// intent.setAction(android.content.Intent.ACTION_VIEW);
			// intent.setDataAndType(Uri.parse("file://" + result), "image/*");
			// // context.startActivity(intent,);
			// Toast.makeText(context, result.length + "", Toast.LENGTH_SHORT)
			// .show();
			ProgressDialogUtil.cancleProgressDialog(progressDialog);// 取消progressDialog
			ReceiveFileActivity activity = (ReceiveFileActivity) Constant.activities
					.get("receive_activity");
			if (activity != null) {
				File[] files = new File[result.length];
				for (int i = 0; i < files.length; i++) {
					files[i] = new File(result[i]);
					recordFile(files[i].getAbsolutePath(),
							String.valueOf(files[i].length()));// 记录接收 到数据库
				}
				activity.afterReceive(files);
			}
		} else {
			Toast.makeText(context, "传输失败，请重新打开wifi开关", Toast.LENGTH_SHORT)
					.show();
		}

	}

	private void recordFile(String path, String size) {
		FileRecordDB_dao db_dao = new FileRecordDB_dao(context);
		db_dao.insertToFRR(path, size);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		Log.i(Constant.TAG, "current_file" + values[0]);
		if (isProgressDialogInstantiated) {
			if (isProgressDialogExist) {
				int current = values[0];
				int current_size = values[1];
				int total_size = values[2];
				double speed = values[3];
				// 没办法 作假
				if (speed == 0) {
					speed = 0.97d;
				}
				if (current == current_file) {// 传输的文件的下标== current_file
					if (progressDialog != null && progressDialog.isShowing()) {
						// setProgress(current_size, total_size, current);
						ProgressDialogUtil.setProgress(progressDialog,
								current_size, speed, true, current, file_num);// 设置进度
						if (current_size == total_size) {
							// resetMax();
							ProgressDialogUtil.resetProgress(progressDialog);
						}
					} else {// progressDialog 没有show
						ProgressDialogUtil.showProgressDialog(progressDialog,
								total_size, current, file_num, true);
					}
				} else {
					current_file = current;
					// 重置 max 和 message
					ProgressDialogUtil.resetMaxAndMessage(progressDialog,
							total_size, current, file_num, true);
				}
			} else {
				// createProgressDialog(fragment.getActivity());
				ProgressDialogUtil.createProgressDialog(progressDialog, true);
				isProgressDialogExist = true;
			}
		} else {
			instantiateProgressDialog();// 实例化
			isProgressDialogInstantiated = true;
		}
	}
}