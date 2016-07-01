package com.rjb.dianfeng.fileexchange.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

import com.rjb.dianfeng.fileexchange.Constant;
import com.rjb.dianfeng.fileexchange.DeviceListActivity;
import com.rjb.dianfeng.fileexchange.MainActivity;
import com.rjb.dianfeng.fileexchange.fragment.FileExchangeFragment;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

	private WifiP2pManager mManager;
	private Channel channel;
	private MainActivity activity;// allow the broadcastReceiver to send updates
									// to the activity

	public WiFiDirectBroadcastReceiver(WifiP2pManager mManager,
			Channel channel, MainActivity activity) {
		this.mManager = mManager;
		this.channel = channel;
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
			// 当设备的Wi—Fi connection 改变的时候
			connectionChanged(intent);

		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
			// 在这儿处理 requestPeers() 得到peers 列表
			peerChanged();
		} else if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
			// wifi p2p 状态改变
			wifiP2pStatetChanged(intent);
		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
				.equals(action)) {
			// 设备的细节变化时

		}
	}

	private void connectionChanged(Intent intent) {
		if (mManager == null) {
			return;
		}

		NetworkInfo networkInfo = (NetworkInfo) intent
				.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

		if (networkInfo.isConnected()) {

			// we are connected with the other device, request connection
			FileExchangeFragment fileExchangeFragment = (FileExchangeFragment) activity
					.getExchangePagerAdapter().getMapFragment()
					.get("fileExchange");
			if (fileExchangeFragment != null) {
				mManager.requestConnectionInfo(channel, fileExchangeFragment);
			}
		} else {
			// It's a disconnect
			// activity.resetData();
		}
	}

	private void wifiP2pStatetChanged(Intent intent) {
		FileExchangeFragment exchangeFragment = (FileExchangeFragment) activity
				.getExchangePagerAdapter().getMapFragment().get("fileExchange");
		int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
		if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
			exchangeFragment.setWifiP2pEnabled(true);
		} else {
			exchangeFragment.setWifiP2pEnabled(false);
			// exchangeFragment.resetData();
		}
		Log.d("wifi action", "P2P state changed - " + state);
	}

	private void peerChanged() {
		if (mManager != null) {
			DeviceListActivity deviceListActivity = (DeviceListActivity) Constant.activities
					.get("DeviceListActivity");
			if (deviceListActivity != null) {
				mManager.requestPeers(channel, deviceListActivity);
			}
		}
	}

}
