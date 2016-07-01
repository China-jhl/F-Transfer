package com.rjb.dianfeng.fileexchange;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rjb.dianfeng.fileexchange.adapter.FileRecordAdapter;
import com.rjb.dianfeng.fileexchange.database.FileRecordDB_dao;
import com.rjb.dianfeng.fileexchange.entity.MyFile;
import com.rjb.dianfeng.fileexchange.utils.DialogUtil;
import com.rjb.dianfeng.fileexchange.utils.DialogUtil.IDialogMethod;

/**
 * 接收到的文件 历史记录
 * 
 * @author 龙
 * 
 */
public class File_ReceivedActivity extends Activity implements OnClickListener {
	private TextView title;
	private ImageView back;
	private ImageView deleteAll;
	private ListView file_received_record;

	private FileRecordDB_dao db_dao;
	private FileRecordAdapter recordAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_file_received);
		initData();
		initView();
		setListener();
	}

	private void setListener() {
		back.setOnClickListener(this);
	}

	private void initData() {
		db_dao = new FileRecordDB_dao(getApplicationContext());
		MyFile[] files = db_dao.queryAllReceivedFilel();
		if (files != null) {
			recordAdapter = new FileRecordAdapter(getApplicationContext(),
					files);
		}
	}

	private void initView() {
		title = (TextView) findViewById(R.id.header_text);
		title.setText("接收文件历史记录");
		file_received_record = (ListView) findViewById(R.id.lv_received_files);
		if (recordAdapter != null) {
			file_received_record.setAdapter(recordAdapter);
		}
		back = (ImageView) findViewById(R.id.iv_left);
		back.setVisibility(View.VISIBLE);
		deleteAll = (ImageView) findViewById(R.id.iv_delete_all);
		deleteAll.setVisibility(View.VISIBLE);
		deleteAll.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_left:
			super.onBackPressed();
			break;
		case R.id.iv_delete_all:
			showDialog();
			break;
		}
	}

	private void showDialog() {
		IDialogMethod methods = new IDialogMethod() {

			@Override
			public void confirm(String newName) {
				FileRecordDB_dao db_dao = new FileRecordDB_dao(
						getApplicationContext());
				db_dao.clearFRRAll();
				if (recordAdapter != null) {
					recordAdapter.setFiles(null);
					recordAdapter.notifyDataSetChanged();// 重新getCount getView
					Toast.makeText(getApplicationContext(), "已经全部清除",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "没记录",
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void cancel() {
				// TODO Auto-generated method stub

			}
		};
		DialogUtil.messageDialog(File_ReceivedActivity.this, methods, "删除记录",
				"确认删除？");
	}
}
