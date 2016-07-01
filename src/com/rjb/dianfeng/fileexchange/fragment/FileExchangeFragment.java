package com.rjb.dianfeng.fileexchange.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.rjb.dianfeng.fileexchange.Constant;
import com.rjb.dianfeng.fileexchange.DeviceListActivity;
import com.rjb.dianfeng.fileexchange.File_ReceivedActivity;
import com.rjb.dianfeng.fileexchange.File_SentActivity;
import com.rjb.dianfeng.fileexchange.MainActivity;
import com.rjb.dianfeng.fileexchange.R;
import com.rjb.dianfeng.fileexchange.ReceiveFileActivity;
import com.rjb.dianfeng.fileexchange.receiver.WiFiDirectBroadcastReceiver;
import com.rjb.dianfeng.fileexchange.utils.FileServerAsyncTask;
import com.rjb.dianfeng.fileexchange.view.CleanView;

public class FileExchangeFragment extends BaseFragment implements
		OnClickListener, ConnectionInfoListener {
	public static List<File> send_files = new ArrayList<File>();// ���͵��ļ�
	private MainActivity mActivity;

	BroadcastReceiver receiver;
	private IntentFilter intentFilter;
	// private WifiP2pManager mManager;
	private boolean isWifiP2pEnabled = false;
	// private Channel channel;

	View view;
	private Button file_receive;
	private Button file_send;
	private CleanView cleanView;

	private int receiveFileNum;
	private boolean isCreate = true;// �����Ƿ���

	private WifiP2pInfo info;

	private SharedPreferences preferences;
	private Editor editor;

	private Button received_history;
	private Button sent_history;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (MainActivity) getActivity();
		preferences = mActivity.getSharedPreferences("host_file",
				Activity.MODE_PRIVATE);
		editor = preferences.edit();
		initWifiP2p();

		view = inflater.inflate(R.layout.fragment_exchange, null);
		initData();
		initView();
		return view;
	}

	private void initData() {
	}

	// �� wifi p2p �Ŀ�ܵ�һЩ��Ҫ������ʼ��
	private void initWifiP2p() {
		// mManager = MainActivity.mManager;
		// channel = MainActivity.mChannel;

		receiver = new WiFiDirectBroadcastReceiver(MainActivity.mManager,
				MainActivity.mChannel, mActivity);

		// ����receiver��intentFillter
		intentFilter = new IntentFilter();
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

	}

	private void initView() {
		file_receive = (Button) view.findViewById(R.id.bt_file_receive);
		file_receive.setOnClickListener(this);
		file_send = (Button) view.findViewById(R.id.bt_file_send);
		file_send.setOnClickListener(this);
		cleanImage();// cleanView �Ĵ���
		received_history = (Button) view.findViewById(R.id.test1);
		sent_history = (Button) view.findViewById(R.id.test2);
		received_history.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity,
						File_ReceivedActivity.class);
				startActivity(intent);
				mActivity.overridePendingTransition(R.anim.tran_in,
						R.anim.tran_out);
			}
		});

		sent_history.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, File_SentActivity.class);
				startActivity(intent);
				mActivity.overridePendingTransition(R.anim.tran_in,
						R.anim.tran_out);
			}
		});

	}

	public void cleanImage() {
		if (!mActivity.isCleanViewExist() && mActivity.isFileExchange()) {// �ǳ��б�Ҫ����isFileExchange,��Ϊ����fragment
																			// ������ζ��
																			// ��fragment�Ѿ���ʾ
			cleanView = new CleanView(mActivity);
			cleanView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (send_files.size() > 0) {
						send_files.clear();
						setAnimation();// ���ö���
						Toast.makeText(mActivity, "�����Ҫ������ļ�",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(mActivity, "û�д�����ļ�", Toast.LENGTH_SHORT)
								.show();
					}
				}

				private void setAnimation() {
					// float pivotX = cleanView.getX() + cleanView.getWidth()
					// / 2;
					//
					// float pivotY = cleanView.getY() + cleanView.getHeight()
					// / 2;
					// RotateAnimation animation = new RotateAnimation(0, 359,
					// pivotX, pivotY);
					// animation.setDuration(500);
					// animation.setRepeatCount(3);
					// cleanView.startAnimation(animation);
					// Animation animation_in =
					// AnimationUtils.loadAnimation(mActivity, R.anim.tran_in);
					// Animation animation_out =
					// AnimationUtils.loadAnimation(mActivity, R.anim.tran_out);
					// file_receive.startAnimation(animation_out);
					// file_receive.startAnimation(animation_in);
					// received_history.startAnimation(animation_out);
					// received_history.startAnimation(animation_in);
					//
					// file_send.startAnimation(animation_in);
					// file_send.startAnimation(animation_out);
					// sent_history.startAnimation(animation_in);
					// sent_history.startAnimation(animation_out);

					// Animation animation = AnimationUtils.loadAnimation(
					// mActivity, R.anim.clean_animation);
					// file_receive.startAnimation(animation);
					// received_history.startAnimation(animation);
					// file_send.startAnimation(animation);
					// sent_history.startAnimation(animation);

				}
			});
			mActivity.setCleanViewExist(true);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_file_receive:
			createGroup();// ˭�����ļ�˭��GO
			break;
		case R.id.bt_file_send:
			sendFile();// �����ļ�
			break;
		}
	}

	private void sendFile() {
		if (send_files.size() > 0) {
			for (int i = 0; i < send_files.size(); i++) {
				Log.i(Constant.TAG, send_files.get(i).getName());
			}
			startDeviceListActivity();
		} else {
			Toast.makeText(mActivity, "û���ļ����Դ���", Toast.LENGTH_SHORT).show();
		}
	}

	private void startDeviceListActivity() {
		Intent intent = new Intent(mActivity, DeviceListActivity.class);
		startActivity(intent);
		mActivity.overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	private void createGroup() {
		if (isWifiP2pEnabled) {
			if (MainActivity.mManager != null && MainActivity.mChannel != null) {
				// mManager.removeGroup(channel, null);// ȷ��һ��
				MainActivity.mManager.createGroup(MainActivity.mChannel, null);

				Intent intent = new Intent(mActivity, ReceiveFileActivity.class);
				intent.putExtra("receive_num", receiveFileNum);
				startActivity(intent);
				mActivity.overridePendingTransition(R.anim.tran_in,
						R.anim.tran_out);
			}
		} else {
			Toast.makeText(mActivity, "���wifi���ػ��� wifi-direct����",
					Toast.LENGTH_SHORT).show();
		}
	}

	public boolean isWifiP2pEnabled() {
		return isWifiP2pEnabled;
	}

	public void setWifiP2pEnabled(boolean isWifiP2pEnabled) {
		this.isWifiP2pEnabled = isWifiP2pEnabled;
	}

	@Override
	public void onResume() {
		super.onResume();
		// ע��broadcast receiver
		mActivity.registerReceiver(receiver, intentFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mActivity.unregisterReceiver(receiver);
		MainActivity.mManager.removeGroup(MainActivity.mChannel, null);
		if (cleanView != null) {
			cleanView.removeView();
		}
	}

	public void removeCleanView() {
		if (cleanView != null) {
			cleanView.removeView();
		}
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		if (info.groupFormed) {
			if (info.isGroupOwner) {
				// ���ӽ����� ���ұ����Ƿ����
				if (isCreate) {
					startFileTask();
					isCreate = false;
				}
				Log.i(Constant.TAG, "�����˽����ļ�task");
				// info
				this.info = info;
				Toast.makeText(mActivity, "I'm GO", Toast.LENGTH_SHORT).show();
			} else {

				// ûʲô�� ̫�ೡ����������������
				// String host = preferences.getString("host", "");
				// if (host.isEmpty()) {
				// editor.putString("host",
				// info.groupOwnerAddress.getHostAddress());
				// editor.commit();
				// Log.i(Constant.TAG,
				// "�Ѵ�����host:"
				// + info.groupOwnerAddress.getHostAddress());
				// } else {// ������� ���
				// editor.putString("host", "");
				// editor.commit();
				// Log.i(Constant.TAG,
				// "�Ѿ����host:"
				// + preferences.getString("host", "clean"));
				// }

				editor.putString("host",
						info.groupOwnerAddress.getHostAddress());
				editor.commit();
				Log.i(Constant.TAG,
						"�Ѵ�����host:" + info.groupOwnerAddress.getHostAddress());
				Toast.makeText(mActivity, "I'm client", Toast.LENGTH_SHORT)
						.show();

			}
		}
	}

	public void startFileTask() {
		new FileServerAsyncTask(this).execute();
	}

	public void setCreate(boolean isCreate) {
		this.isCreate = isCreate;
	}

	public void cancleConnect() {
		MainActivity.mManager.cancelConnect(MainActivity.mChannel, null);
	}

	public WifiP2pInfo getInfo() {
		return info;
	}

}
