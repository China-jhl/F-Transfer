package com.rjb.dianfeng.fileexchange;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rjb.dianfeng.fileexchange.adapter.FileAdapter2;
import com.rjb.dianfeng.fileexchange.utils.Utils;

/**
 * ①：显示文件传输进度 ②：传输完成后，显示传输的文件
 * 
 * @author 龙
 * 
 */

@SuppressLint("CommitPrefEdits")
public class ReceiveFileActivity extends Activity implements OnClickListener,
		OnItemClickListener {
	private TextView title;
	private ImageView back;
	private ImageView discover;

	private FileAdapter2 adapter2;
	private ListView received_files;
	private File[] files;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_receive_file);
		initData();
		initView();
	}

	private void initData() {
		Constant.activities.put("receive_activity", this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (Constant.activities.containsValue(this)) {
			Constant.activities.remove("receive_activity");
		}
		MainActivity.mManager.removeGroup(MainActivity.mChannel, null);//移除
	}

	private void initView() {
		title = (TextView) findViewById(R.id.header_text);
		title.setText(R.string.receiveFile);
		title.setOnClickListener(this);
		back = (ImageView) findViewById(R.id.iv_left);
		back.setVisibility(View.VISIBLE);
		back.setOnClickListener(this);
		received_files = (ListView) findViewById(R.id.lv_receive_files);
		received_files.setOnItemClickListener(this);
		discover = (ImageView) findViewById(R.id.iv_delete_all);
		discover.setVisibility(View.VISIBLE);
		discover.setImageResource(R.drawable.ic_action_discover);
		discover.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_left:
			super.onBackPressed();
			break;
		case R.id.header_text:
			// 清除group
			MainActivity.mManager.removeGroup(MainActivity.mChannel, null);
			Log.i(Constant.TAG, "remove group");
			break;
		case R.id.iv_delete_all:
			// 发现peers
			discoverPeers();
			break;
		}
	}

	private void discoverPeers() {
		// 两个设备同时discover 更容易发现peers
		MainActivity.mManager.discoverPeers(MainActivity.mChannel, null);
	}

	/**
	 * 接收完文件后
	 */
	public void afterReceive(File[] files) {
		this.files = files;
		adapter2 = new FileAdapter2(this, files);
		received_files.setAdapter(adapter2);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		String extension = Utils.getExtensionFromPath(files[position]
				.getAbsolutePath());
		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				extension);
		intent.setDataAndType(Uri.parse(files[position].toURI().toString()),
				mimeType);
		try {
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(this, "没有打开此应用的软件", Toast.LENGTH_SHORT).show();
		}
	}
}
