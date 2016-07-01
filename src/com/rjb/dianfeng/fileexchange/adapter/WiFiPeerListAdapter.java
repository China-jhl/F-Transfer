package com.rjb.dianfeng.fileexchange.adapter;

import java.util.List;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rjb.dianfeng.fileexchange.R;

/**
 * Array adapter for ListFragment that maintains WifiP2pDevice list.
 */
public class WiFiPeerListAdapter extends BaseAdapter {

	private List<WifiP2pDevice> items;
	private Context mContext;

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public WiFiPeerListAdapter(Context context, List<WifiP2pDevice> objects) {
		items = objects;
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		if (convertView != null) {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		} else {
			view = View.inflate(mContext, R.layout.item_devices, null);
			holder = new ViewHolder();
			holder.device_icon = (ImageView) view
					.findViewById(R.id.iv_device_icon);
			holder.device_name = (TextView) view
					.findViewById(R.id.tv_device_name);
			holder.device_address = (TextView) view
					.findViewById(R.id.tv_device_address);
			view.setTag(holder);
		}
		holder.device_name.setText(items.get(position).deviceName);
		holder.device_address.setText("ŒÔ¿Ìµÿ÷∑:"
				+ items.get(position).deviceAddress);

		return view;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	class ViewHolder {
		public ImageView device_icon;
		public TextView device_name;
		public TextView device_address;
	}
}
