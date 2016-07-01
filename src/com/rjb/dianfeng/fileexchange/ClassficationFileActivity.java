package com.rjb.dianfeng.fileexchange;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.rjb.dianfeng.fileexchange.adapter.ClassificationAdapter;
import com.rjb.dianfeng.fileexchange.adapter.FileAdapter;
import com.rjb.dianfeng.fileexchange.adapter.FileAdapter.ViewHolder;
import com.rjb.dianfeng.fileexchange.entity.AppProvider;
import com.rjb.dianfeng.fileexchange.entity.AudioProvider;
import com.rjb.dianfeng.fileexchange.entity.BaseMedia;
import com.rjb.dianfeng.fileexchange.entity.Doc_etc_Provider;
import com.rjb.dianfeng.fileexchange.entity.PictureProvider;
import com.rjb.dianfeng.fileexchange.entity.VideoProvider;
import com.rjb.dianfeng.fileexchange.entity.ZipProvider;
import com.rjb.dianfeng.fileexchange.fragment.FileExchangeFragment;
import com.rjb.dianfeng.fileexchange.interfaces.IGetMediaList;
import com.rjb.dianfeng.fileexchange.utils.DialogUtil;
import com.rjb.dianfeng.fileexchange.utils.DialogUtil.IDialogMethod;
import com.rjb.dianfeng.fileexchange.utils.FileOperation;
import com.rjb.dianfeng.fileexchange.utils.MediaListAsyncTask;
import com.rjb.dianfeng.fileexchange.utils.Utils;

public class ClassficationFileActivity extends Activity implements
		OnClickListener {
	private ListView files;
	private FileAdapter fileAdapter;
	List<? extends BaseMedia> mediaList;
	private Handler handler;

	private ImageView footer1;
	private ImageView footer2;
	private ImageView footer3;
	private ImageView footer4;
	private ImageView footer5;
	private ImageView leftArrow;

	private List<BaseMedia> media_chose;// 被选中的

	private final static int RUNONUI = 1;// 在ui线程中执行的函数
	private int cellNum;// classification fragment 点击的cell号
	private String part_uri;// uri的一部分
	private IGetMediaList getMediaList;// 接口

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_classification_file);
		initData();
		initView();
		setListener();
	}

	@SuppressLint("HandlerLeak")
	private void initData() {
		// 定义handler
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case RUNONUI:
					if (mediaList.size() > 0) {
						fileAdapter = new FileAdapter(mediaList,
								getApplicationContext(), cellNum);
						files.setAdapter(fileAdapter);
					} else {
						Toast.makeText(getBaseContext(), "Nothing",
								Toast.LENGTH_SHORT).show();
					}
					break;

				}
			}
		};

		// 获取传过来的数据
		Intent intent = getIntent();
		cellNum = intent.getIntExtra("cellNum", 0);
		handleData(cellNum);

		media_chose = new ArrayList<BaseMedia>();
	}

	// 处理classification fragment传过来的数据
	private void handleData(int cellNum) {
		// 初始化接口
		getMediaList = new IGetMediaList() {

			@Override
			public void setMediaList(List<? extends BaseMedia> mediaList) {
				ClassficationFileActivity.this.mediaList = mediaList;
				handler.sendEmptyMessage(RUNONUI);
			}
		};
		new MediaListAsyncTask(getApplicationContext(), getMediaList)// 将接口传入异步任务类
				.execute(cellNum);
		// 获取part_uri
		getPartUri(cellNum);

	}

	private void getPartUri(int cellNum) {
		switch (cellNum) {
		case ClassificationAdapter.FILE_APP:
			part_uri = AppProvider.URL_FILE;

			break;
		case ClassificationAdapter.FILE_MUSIC:
			part_uri = AudioProvider.URL_FILE;

			break;
		case ClassificationAdapter.FILE_PIC:
			part_uri = PictureProvider.URL_FILE;

			break;
		case ClassificationAdapter.FILE_TXT:
			part_uri = Doc_etc_Provider.URL_FILE;

			break;
		case ClassificationAdapter.FILE_VIDEO:
			part_uri = VideoProvider.URL_FILE;

			break;
		case ClassificationAdapter.FILE_ZIPS:
			part_uri = ZipProvider.URL_FILE;

			break;
		}
	}

	private void initView() {
		files = (ListView) findViewById(R.id.lv_same_files);
		footer1 = (ImageView) findViewById(R.id.iv_footer_1);
		footer1.setOnClickListener(this);
		footer2 = (ImageView) findViewById(R.id.iv_footer_2);
		footer2.setOnClickListener(this);
		footer3 = (ImageView) findViewById(R.id.iv_footer_3);
		footer3.setOnClickListener(this);
		footer4 = (ImageView) findViewById(R.id.iv_footer_4);
		footer4.setOnClickListener(this);
		footer5 = (ImageView) findViewById(R.id.iv_footer_5);
		footer5.setOnClickListener(this);
		leftArrow = (ImageView) findViewById(R.id.iv_left);
		leftArrow.setVisibility(View.VISIBLE);
		leftArrow.setOnClickListener(this);
	}

	private void setListener() {
		files.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (fileAdapter.isOpretated()) {// 如果要对文件进行操作
					FileAdapter.ViewHolder holder = (ViewHolder) view.getTag();
					if (media_chose.contains(mediaList.get(position))) {// 如果已经有了这个media
						media_chose.remove(mediaList.get(position));// 移除
						holder.chose.setImageResource(R.drawable.unckecked);
						fileAdapter.remove(mediaList.get(position));// 之所以这样写是因为集合中添加删除的是对象的引用，当你添加
																	// 一个
																	// 整型对象（1）引用后.你没办法用API判断“1”是否存在
																	// 没办法用API
																	// 移除“1”.因为你用这些API传入的参数不一定先前添加对象的引用，可能是个新对象的引用
					} else {
						media_chose.add(mediaList.get(position));
						holder.chose.setImageResource(R.drawable.checked);
						fileAdapter.add(mediaList.get(position));// 添加被选中的位置
																	// //而这个不同。我一直操作的是mediaList中的对象的引用，这个是确定的。
					}
				} else {// 不进行文件的选中操作
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction(Intent.ACTION_VIEW);
					setDataAndType(position, intent);
					try {
						startActivity(intent);
					} catch (Exception e) {
						Toast.makeText(getApplicationContext(), "没有打开此应用的软件",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	@Override
	protected void onStop() {
		handler.removeMessages(RUNONUI);
		super.onStop();
	}

	private void setDataAndType(int position, Intent intent) {
		Uri uri = Uri.parse(part_uri + mediaList.get(position).getId());
		intent.setDataAndType(uri, mediaList.get(position).getMime_type());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_footer_1:
			showCheckIcon();
			operateMedia(Constant.FILE_RENAME);
			break;
		case R.id.iv_footer_2:
			showCheckIcon();
			operateMedia(Constant.FILE_COPY);
			break;
		case R.id.iv_footer_3:
			showCheckIcon();
			operateMedia(Constant.FILE_DELETE);
			break;
		case R.id.iv_footer_4:
			showCheckIcon();
			operateMedia(Constant.FILE_INFO);
			break;
		case R.id.iv_left:
			onBackPressed();
			break;
		case R.id.iv_footer_5:
			showCheckIcon();
			operateMedia(Constant.FILE_SEND);
			break;
		}
	}

	private void showCheckIcon() {
		if (fileAdapter != null) {
			fileAdapter.setOpretated(true);
			fileAdapter.notifyDataSetChanged();
		}
	}

	// 操作被选文件
	private void operateMedia(int operation) {
		int succeed = 0;
		switch (operation) {
		case Constant.FILE_RENAME:
			succeed = renameFile();
			break;
		case Constant.FILE_DELETE:
			succeed = deleteFile();
			break;
		case Constant.FILE_COPY:
			copyFile();
			break;
		case Constant.FILE_INFO:
			succeed = infoFile();
			break;
		case Constant.FILE_SEND:
			sendFile();
			break;
		}
		if (succeed != 0) {// 如果操作成功
			afterSucceedFileOperation();
		}

	}

	private void sendFile() {
		if (media_chose.size() > 0) {
			if (FileExchangeFragment.send_files.size() > 0) {
				FileExchangeFragment.send_files.clear();
			}
			for (int i = 0; i < media_chose.size(); i++) {
				File file = new File(media_chose.get(i).getPath());
				FileExchangeFragment.send_files.add(file);
			}
			afterSucceedFileOperation();
			Toast.makeText(getApplicationContext(), "快去发送吧", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private int infoFile() {
		if (media_chose.size() > 1) {
			Toast.makeText(getApplicationContext(), "只能操作一个文件",
					Toast.LENGTH_SHORT).show();
			return 0;
		}
		if (media_chose.size() == 1) {
			BaseMedia media = media_chose.get(0);
			String mime_type = media.getMime_type();
			String path = media.getPath();
			String name = Utils.getNameFromPath(path);
			String size = Utils.getSizeKB(media.getSize());
			String info = "<p> 文件名称：" + name + "</p>" + "<p> 文件类型：" + mime_type
					+ "</p>" + "<p> 文件大小：" + size + "</p>" + "<p> 文件路径:" + path
					+ "</p>";
			try {
				DialogUtil.showFileInfo(ClassficationFileActivity.this, Html
						.fromHtml(info).toString());
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "文件有误",
						Toast.LENGTH_SHORT).show();
			}

			// return 1;
		}
		return 0;
	}

	private void copyFile() {
		if (media_chose.size() > 0) {
			Intent intent = new Intent(this, MainActivity.class);
			String value[] = new String[media_chose.size()];
			for (int i = 0; i < media_chose.size(); i++) {
				value[i] = media_chose.get(i).getPath();
			}
			intent.putExtra("file_path", value);
			intent.putExtra("flag_activity",
					Constant.START_MAINACTIVITY_FROM_CLASSIFICATIONFILEACTIVITY);
			startActivity(intent);
		}
	}

	public void afterSucceedFileOperation() {
		if (media_chose != null) {
			media_chose.clear();// 清除原有数据
		}
		fileAdapter.setOpretated(false);
		fileAdapter.clear();// 清空选中记录
		fileAdapter.notifyDataSetChanged();
		new MediaListAsyncTask(getApplicationContext(), getMediaList)// 将接口传入异步任务类
				.execute(cellNum);
	}

	private int renameFile() {
		if (media_chose.size() > 1) {
			Toast.makeText(getApplicationContext(), "一次只能修改一个文件",
					Toast.LENGTH_SHORT).show();
			return 0;
		}
		if (media_chose.size() == 1) {
			IDialogMethod methods = new IDialogMethod() {

				@Override
				public void confirm(String newName) {
					BaseMedia media = media_chose.get(0);
					File file = new File(media.getPath());
					String id = media.getId() + "";
					int succeed = FileOperation.reName(getApplicationContext(),
							file, newName, Uri.parse(part_uri + id), id);
					if (succeed != 0) {
						Toast.makeText(getApplicationContext(), "重命名成功",
								Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void cancel() {
					// TODO Auto-generated method stub

				}
			};
			String name = Utils.getNameFromPath(media_chose.get(0).getPath());
			DialogUtil.showDialog(ClassficationFileActivity.this, methods,
					name, Constant.ClASSIFICATION_FILE_ACTIVITY);
		}

		return 0;
	}

	private int deleteFile() {
		int succeed = 0;
		for (int i = 0; i < media_chose.size(); i++) {
			BaseMedia media = media_chose.get(i);
			File file = new File(media.getPath());
			String id = media.getId() + "";
			succeed = FileOperation.delete(getApplicationContext(), file,
					Uri.parse(part_uri + id), id);
			if (succeed != 0) {
				Toast.makeText(getApplicationContext(), "删除成功",
						Toast.LENGTH_SHORT).show();
			}
		}
		return succeed;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
