package com.rjb.dianfeng.fileexchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rjb.dianfeng.fileexchange.adapter.WiFiPeerListAdapter;
import com.rjb.dianfeng.fileexchange.fragment.FileExchangeFragment;
import com.rjb.dianfeng.fileexchange.service.FileTransferService;
import com.rjb.dianfeng.fileexchange.utils.DialogUtil;
import com.rjb.dianfeng.fileexchange.utils.DialogUtil.IDialogMethod;
import com.rjb.dianfeng.fileexchange.utils.ProgressDialogUtil;
import com.rjb.dianfeng.fileexchange.utils.SendFileToComputerTask;

public class DeviceListActivity extends Activity implements PeerListListener,
		OnClickListener {
	private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

	private ProgressDialog progressDialog;
	private WiFiPeerListAdapter peerListAdapter;

	private Button find_peer;
	private Button find_computer;
	private Button send_online;
	private ListView devices;
	private TextView title;
	private ImageView back;

	private WifiP2pDevice device;// Ҫ���ӵ��豸

	private SharedPreferences preferences;
	private Editor editor;
	private String host;
	private Handler mHandler;
	private TimerTask timerTask;
	private Timer mTimer;

	public static final int WHAT_NO_SAVE = 0;// host��û����
	public static final int TIMER = 1;// ��ʱ��
	private int spendTime;// ��ȡhost֮ǰ��ȡ��ʱ��
	private static final int TOTAL_TIME = 10;// ��ȡhost �����ѵ����ʱ��

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_device_list);
		initData();
		initView();
		setListener();
	}

	private void setListener() {
		cancleConnect();// ȷ��֮ǰ�������Ѿ�ȡ����
		devices.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				cleanHost();// ��������host��Ϊ�� �����
				connect(position);// �����豸
			}

			private void cleanHost() {
				String host = preferences.getString("host", "");
				if (!host.isEmpty()) {
					editor.putString("host", "");
					editor.commit();
					Log.i(Constant.TAG,
							"host�Ѿ����:host="
									+ preferences.getString("host", ""));
				}
			}

		});
	}

	private void connect(int position) {
		device = peers.get(position);
		// config
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = device.deviceAddress;
		config.wps.setup = WpsInfo.PBC;
		config.groupOwnerIntent = 15;
		// �ȴ�ui
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		progressDialog = ProgressDialog.show(this, "Press back to cancel",
				"Connecting to :" + device.deviceAddress, true, true, null

		);
		// ������
		connectToDevice(config);
	}

	private void connectToDevice(WifiP2pConfig config) {
		MainActivity.mManager.connect(MainActivity.mChannel, config,
				new ActionListener() {

					@Override
					public void onSuccess() {
						if (progressDialog != null
								&& progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
						showDialog();
						Log.i(Constant.TAG, "���ӳɹ�");
					}

					@Override
					public void onFailure(int reason) {
						Toast.makeText(DeviceListActivity.this, "����ʧ��",
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	// ���� ���ȴ��Է�ȷ������
	private void showDialog() {
		DialogUtil.messageDialog(this, new IDialogMethod() {

			@Override
			public void confirm(String newName) {
				initTimerAndTask();
				mTimer.schedule(timerTask, 1000, 1000);// ��ʼ��ʱ
				host = preferences.getString("host", "");// ���� �ڵ��item֮��
															// Ҳ�������host֮��
				isHostSave();
			}

			@Override
			public void cancel() {
				// TODO Auto-generated method stub

			}
		}, "�ļ���ϸ��Ϣ", "ȷ�Ϸ��� ��");
	}

	// ���������ļ����� FileTransferService
	private void transferFile(String[] path, String address, int port) {

		Intent serviceIntent = new Intent(this, FileTransferService.class);
		serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
		serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
				address);
		serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT,
				port);
		Log.i(Constant.TAG, "����host");
		serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, path);
		startService(serviceIntent);
	}

	@SuppressLint("HandlerLeak")
	private void initData() {
		// ��ӵ�map ��
		Constant.activities.put("DeviceListActivity", this);
		// preferences
		preferences = getSharedPreferences("host_file", Activity.MODE_PRIVATE);
		editor = preferences.edit();
		// ��ʼ��handler
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case WHAT_NO_SAVE:
					host = preferences.getString("host", "");// û�д������������
					isHostSave();
				case TIMER:
					if (spendTime >= TOTAL_TIME) {
						Toast.makeText(getApplicationContext(),
								"���ӳ�ʱ����Է����µ�������ļ�", Toast.LENGTH_SHORT).show();
						cancleTimer();
						mHandler.removeMessages(WHAT_NO_SAVE);// ȡ����Ϣ
					}
					break;
				}
			}

		};

		initTimerAndTask();
	}

	private void initTimerAndTask() {
		// һ��timer �� һ��timerTask���ʹ��
		timerTask = new TimerTask() {

			@Override
			public void run() {
				mHandler.sendEmptyMessage(TIMER);
				spendTime++;// ʱ�� +1 ��
			}
		};

		mTimer = new Timer(true);// ��ʼ��timer
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (Constant.activities.containsValue(this)) {
			Constant.activities.remove("DeviceListActivity");
		}
	}

	private void initView() {
		find_peer = (Button) findViewById(R.id.bt_find_peers);
		find_peer.setOnClickListener(this);
		find_computer = (Button) findViewById(R.id.bt_find_computer);
		find_computer.setOnClickListener(this);
		send_online = (Button) findViewById(R.id.bt_send_online);
		send_online.setOnClickListener(this);
		devices = (ListView) findViewById(R.id.lv_peers);
		title = (TextView) findViewById(R.id.header_text);
		title.setText(R.string.sendFile);
		title.setOnClickListener(this);
		back = (ImageView) findViewById(R.id.iv_left);
		back.setVisibility(View.VISIBLE);
		back.setOnClickListener(this);
	}

	private void cancleTimer() {
		if (mTimer != null) {
			spendTime = 0;// ���¸�ֵ
			mTimer.cancel();
			Log.i(Constant.TAG, "��ʱ��ֹͣ");
		}
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peerList) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		peers.clear();
		peers.addAll(peerList.getDeviceList());

		// ����adapter
		if (peerListAdapter == null) {
			peerListAdapter = new WiFiPeerListAdapter(getApplicationContext(),
					peers);
			if (devices != null && peerListAdapter != null) {
				devices.setAdapter(peerListAdapter);
			}
		} else {
			peerListAdapter.notifyDataSetChanged();
		}

		if (peers.size() == 0) {
			Log.d("WIFI P2P", "No devices found");
			return;
		}
	}

	// ����discoverPeers����ʱ�����õĺ���
	public void onInitiateDiscovery() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		progressDialog = ProgressDialog.show(this, "Press back to cancel",
				"finding peers", true, true,
				new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {

					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_find_peers:
			MainActivity.mManager.discoverPeers(MainActivity.mChannel, null);
			onInitiateDiscovery();
			break;
		case R.id.iv_left:
			super.onBackPressed();
			break;
		case R.id.bt_find_computer:
			sendFile2Comp();// �����ļ���conputer
			break;
		case R.id.header_text:
			// ȡ��group
			MainActivity.mManager.removeGroup(MainActivity.mChannel, null);
			Log.i(Constant.TAG, "remove group");
			break;
		case R.id.bt_send_online:
			sendOnline();//���߷���
			break;
		}
	}

	private void sendOnline() {
		String[] path = getPathes();// �õ�Ҫ������ļ���path
		this.file_num = path.length;
		for (int i = 0; i < path.length; i++) {
			new SendFileToComputerTask(this, path[i], i).execute();
		}
	}

	private void sendFile2Comp() {
		IDialogMethod methods = new IDialogMethod() {

			@Override
			public void confirm(String newName) {
				String[] path = getPathes();// �õ�Ҫ������ļ���path
				DeviceListActivity.this.file_num = path.length;
				// �����ļ�
				transferFile(path, Constant.COMPUTER_ADDRESS,
						Constant.COMPUTER_PORT);
			}

			@Override
			public void cancel() {
				// TODO Auto-generated method stub

			}
		};
		DialogUtil.messageDialog(DeviceListActivity.this, methods, "�����ļ�������",
				"��ȷ�����ӵ�ָ������������");
	}

	private void cancleConnect() {
		MainActivity.mManager.cancelConnect(MainActivity.mChannel, null);
	}

	private void isHostSave() {
		if (host.isEmpty()) {
			mHandler.sendEmptyMessageDelayed(WHAT_NO_SAVE, 500);
			Log.i(Constant.TAG, "û���룬������host");
		} else {
			// �����ļ�
			String[] path = getPathes();// �õ�Ҫ������ļ���path
			transferFile(path, host, Constant.PORT);
			cancleTimer();
		}
	}

	public String[] getPathes() {
		String[] path = new String[FileExchangeFragment.send_files.size()];
		for (int i = 0; i < path.length; i++) {
			path[i] = FileExchangeFragment.send_files.get(i).getAbsolutePath();
		}
		return path;
	}

	private int file_num;// Ҫ������ļ�����
	private int current_file;// ��ǰ������ļ�xiabiao
	ProgressDialog progressDialog2;
	private boolean isProgressDialogExist = false;
	private boolean isProgressDialogInstantiated = false;// ʵ����������û��

	public void onProgressUpdate(Integer... values) {
		Log.i(Constant.TAG, "current_file" + values[0]);
		if (isProgressDialogInstantiated) {
			if (isProgressDialogExist) {
				int current = values[0];
				int current_size = values[1];
				int total_size = values[2];
				double speed = values[3];
				if (current == current_file) {// ������ļ����±�== current_file
					if (progressDialog2 != null && progressDialog2.isShowing()) {
						// setProgress(current_size, total_size, current);
						ProgressDialogUtil.setProgress(progressDialog2,
								current_size,speed,false,current,file_num);// ���ý���
						if (current_size == total_size) {
							// resetMax();
							ProgressDialogUtil.resetProgress(progressDialog2);
						}
					} else {// progressDialog û��show
						ProgressDialogUtil.showProgressDialog(progressDialog2,
								total_size, current, file_num, false);
					}
				} else {
					current_file = current;
					// ���� max �� message
					ProgressDialogUtil.resetMaxAndMessage(progressDialog2,
							total_size, current, file_num, false);
				}
			} else {
				// createProgressDialog(fragment.getActivity());
				ProgressDialogUtil.createProgressDialog(progressDialog2, false);
				isProgressDialogExist = true;
			}
		} else {
			instantiateProgressDialog();// ʵ����
			isProgressDialogInstantiated = true;
		}
	}

	private void instantiateProgressDialog() {
		progressDialog2 = new ProgressDialog(this);
		Log.i(Constant.TAG, "ʵ������progressdialog");
	}

	public void cancleProgressDialog() {
		ProgressDialogUtil.cancleProgressDialog(progressDialog2);// ȡ��progressDialog
		isProgressDialogExist = false;
		isProgressDialogInstantiated = false;
	}

	public int getFile_num() {
		return file_num;
	}

}
