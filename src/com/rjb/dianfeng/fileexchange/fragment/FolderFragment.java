package com.rjb.dianfeng.fileexchange.fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.rjb.dianfeng.fileexchange.Constant;
import com.rjb.dianfeng.fileexchange.R;
import com.rjb.dianfeng.fileexchange.adapter.FileAdapter2;
import com.rjb.dianfeng.fileexchange.entity.StorageInfo;
import com.rjb.dianfeng.fileexchange.utils.DialogUtil;
import com.rjb.dianfeng.fileexchange.utils.DialogUtil.IDialogMethod;
import com.rjb.dianfeng.fileexchange.utils.FileOperation;
import com.rjb.dianfeng.fileexchange.utils.Utils;

public class FolderFragment extends BaseFragment implements
		OnItemClickListener, OnClickListener {
	/**
	 * 
	 */

	private static final int ROOTFILE = 0;// 根目录
	private static final int FILE = 1;//
	private Activity mActivity;
	private Handler handler;
	private FileAdapter2 adapter2;
	private ListView files;

	File file; // 对应点击item的file
	File[] fileList;
	File[] preFileListFiles;// 为了应对一下奇怪的文件。。。 既然已经是目录了，可是listFiles()既然会为空！！

	private static String path = "";// 记录当前路径信息

	private List<File> media_chose;// 被选中的
	private List<File> copy_files;// 从ClassificationFileActivity 传过来的复制文件

	private ImageView footer1;
	private ImageView footer2;
	private ImageView footer3;
	private ImageView footer4;
	private ImageView footer5;

	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		media_chose = new ArrayList<File>();
		copy_files = new ArrayList<File>();

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case ROOTFILE:
					fileList = (File[]) msg.obj;
					adapter2 = new FileAdapter2(mActivity, fileList);
					files.setAdapter(adapter2);
					break;
				case FILE:
					file = (File) msg.obj;
					if (file.isDirectory()) {
						fileList = file.listFiles();
						if (fileList != null) {
							preFileListFiles = fileList;// 不等于空的list先保存起来
							adapter2 = new FileAdapter2(mActivity, fileList);
							files.setAdapter(adapter2);
						} else {
							adapter2 = new FileAdapter2(mActivity,
									preFileListFiles);
							files.setAdapter(adapter2);
							fileList = preFileListFiles;// 在赋值回去
						}
					}
					break;
				}

				clearMedia_chose();// setAdapter时，清除“选中列表”

			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = getActivity();// 获得activity

		View view = inflater.inflate(R.layout.fragment_folder, null);
		files = (ListView) view.findViewById(R.id.lv_list_files);
		files.setOnItemClickListener(this);

		footer1 = (ImageView) view.findViewById(R.id.iv_footer_1);
		footer1.setOnClickListener(this);
		footer2 = (ImageView) view.findViewById(R.id.iv_footer_2);
		footer2.setOnClickListener(this);
		footer3 = (ImageView) view.findViewById(R.id.iv_footer_3);
		footer3.setOnClickListener(this);
		footer4 = (ImageView) view.findViewById(R.id.iv_footer_4);
		footer4.setOnClickListener(this);
		footer5 = (ImageView) view.findViewById(R.id.iv_footer_5);
		footer5.setOnClickListener(this);
		getRootFile();
		return view;
	}

	private void getRootFile() {
		List<StorageInfo> storages = Utils.listAvaliableStorage(mActivity);
		File[] fileList = new File[storages.size()];
		for (int i = 0; i < storages.size(); i++) {
			File file = new File(storages.get(i).path);
			fileList[i] = file;
		}
		Message message = new Message();
		message.obj = fileList;
		message.what = ROOTFILE;
		handler.sendMessage(message);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (fileList[position].isDirectory()) {
			path = fileList[position].getPath();// 赋值path

			// if (isCopy) {
			// if (copyFile != null) {
			// copy.setVisibility(View.VISIBLE);
			// }
			// }

			Message message = new Message();
			message.obj = fileList[position];
			message.what = FILE;
			handler.sendMessage(message);
		} else {
			if (adapter2.isOpretated()) {
				FileAdapter2.ViewHolder holder = (FileAdapter2.ViewHolder) view
						.getTag();
				if (media_chose.contains(fileList[position])) {
					media_chose.remove(fileList[position]);
					holder.chose.setImageResource(R.drawable.unckecked);
					adapter2.remove(fileList[position]);
				} else {
					media_chose.add(fileList[position]);
					holder.chose.setImageResource(R.drawable.checked);
					adapter2.add(fileList[position]);
				}
			} else {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(Intent.ACTION_VIEW);
				String extension = Utils
						.getExtensionFromPath(fileList[position]
								.getAbsolutePath());
				String mimeType = MimeTypeMap.getSingleton()
						.getMimeTypeFromExtension(extension);
				intent.setDataAndType(
						Uri.parse(fileList[position].toURI().toString()),
						mimeType);
				try {
					startActivity(intent);
				} catch (Exception e) {
					Toast.makeText(mActivity, "没有打开此应用的软件", Toast.LENGTH_SHORT)
							.show();
				}

			}
		}
	}

	@Override
	public void onStop() {
		handler.removeMessages(FILE);
		super.onStop();
	}

	public boolean onPressBack() {
		if (!path.isEmpty()) {
			String prePath = Utils.getPrePath(path);
			path = prePath;
			// Toast.makeText(mActivity, path, Toast.LENGTH_SHORT).show();
			// tv_path.setText(path);
			if (!prePath.isEmpty()) {
				if (Utils.separatorNum(prePath) == 1) {
					getRootFile();
					return true;
				}
				File preFile = new File(prePath);
				// Toast.makeText(mActivity, prePath,
				// Toast.LENGTH_SHORT).show();
				Message msg = new Message();
				msg.obj = preFile;
				msg.what = FILE;
				handler.sendMessage(msg);
				return true;
			}
			return false;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_footer_1:
			showCheckIcon();
			oprateMedia(Constant.FILE_RENAME);
			break;
		case R.id.iv_footer_2:
			showCheckIcon();
			oprateMedia(Constant.FILE_COPY);
			break;
		case R.id.iv_footer_3:
			showCheckIcon();
			oprateMedia(Constant.FILE_DELETE);
			break;
		case R.id.iv_footer_4:
			showCheckIcon();
			oprateMedia(Constant.FILE_INFO);
			break;
		case R.id.iv_footer_5:
			showCheckIcon();
			oprateMedia(Constant.FILE_SEND);
			break;

		}
	}

	private void showCheckIcon() {
		if (adapter2 != null) {
			adapter2.setOpretated(true);
			adapter2.notifyDataSetChanged();
		}
	}

	// 操作被选文件
	private void oprateMedia(int operation) {
		boolean succeed = false;
		switch (operation) {
		case Constant.FILE_RENAME:
			succeed = renameFile();
			break;
		case Constant.FILE_DELETE:
			succeed = deleteFile();
			break;
		case Constant.FILE_COPY:
			succeed = copyFile();
			break;
		case Constant.FILE_INFO:
			succeed = infoFile();
			break;
		case Constant.FILE_SEND:
			sendFile();
			break;
		}
		if (succeed) {// 如果操作成功
			afterSucceedFileOperation();
		}
	}

	private void sendFile() {
		if (media_chose.size() > 0) {
			if (FileExchangeFragment.send_files.size() > 0) {
				FileExchangeFragment.send_files.clear();
			}
			for (int i = 0; i < media_chose.size(); i++) {
				File file = new File(media_chose.get(i).getAbsolutePath());
				FileExchangeFragment.send_files.add(file);
			}
			afterSucceedFileOperation();
			Toast.makeText(mActivity, "快去发送吧", Toast.LENGTH_SHORT).show();
		}
	}

	private boolean infoFile() {
		if (media_chose.size() > 1) {
			Toast.makeText(mActivity, "只能操作一个文件", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (media_chose.size() == 1) {
			File file = media_chose.get(0);
			String name = file.getName();
			String path = file.getAbsolutePath();
			String mime_type = Utils.getExtensionFromPath(path);
			String size = Utils.getSizeKB((int) file.length());
			String info = "<p> 文件名称：" + name + "</p>" + "<p> 文件类型：" + mime_type
					+ "</p>" + "<p> 文件大小：" + size + "</p>" + "<p> 文件路径:" + path
					+ "</p>";
			try {
				DialogUtil.showFileInfo(mActivity, Html.fromHtml(info)
						.toString());
			} catch (Exception e) {
				Toast.makeText(mActivity, "文件有误", Toast.LENGTH_SHORT).show();
			}
		}
		return false;
	}

	private boolean copyFile() {
		boolean succeed = false;
		if (media_chose.size() > 0) {
			fillCopy_files();
		}
		if (copy_files.size() > 0) {

			for (int i = 0; i < copy_files.size(); i++) {
				File file = copy_files.get(i);
				succeed = FileOperation.copy(file, path);
			}
			if (succeed) {
				Toast.makeText(mActivity, "复制成功", Toast.LENGTH_SHORT).show();
				if (copy_files.size() > 0) {
					copy_files.clear();
				}
			} else {
				Toast.makeText(mActivity, "复制失败", Toast.LENGTH_SHORT).show();
				// fillCopy_files();
			}
		}
		return succeed;
	}

	// 不能简单的“ copy_files = media_chose"
	// 因为media_chose 一清零 copy_files也就清零了
	// 因为什么？
	private void fillCopy_files() {
		for (int i = 0; i < media_chose.size(); i++) {
			copy_files.add(new File(media_chose.get(i).getAbsolutePath()));
		}
	}

	public void afterSucceedFileOperation() {
		clearMedia_chose();
		adapter2.setOpretated(false);
		adapter2.clear();// 清空选中记录
		adapter2.notifyDataSetChanged();

		updateList();
	}

	private boolean renameFile() {
		if (media_chose.size() > 1) {
			Toast.makeText(mActivity, "一次只能修改一个文件", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (media_chose.size() == 1) {
			IDialogMethod methods = new IDialogMethod() {

				@Override
				public void confirm(String newName) {
					File file = media_chose.get(0);
					boolean succeed = FileOperation.reName(mActivity, file,
							newName);
					if (succeed) {
						Toast.makeText(mActivity, "重命名成功", Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(mActivity, "重命名失败", Toast.LENGTH_SHORT)
								.show();
					}
				}

				@Override
				public void cancel() {
					// TODO Auto-generated method stub

				}
			};
			String name = Utils.getNameFromPath(media_chose.get(0).getPath());
			DialogUtil.showDialog(getActivity(), methods, name,
					Constant.FOLDER_FRAGMENT);
		}

		return false;
	}

	private boolean deleteFile() {
		boolean succeed = false;
		if (media_chose.size() > 0) {
			for (int i = 0; i < media_chose.size(); i++) {
				succeed = FileOperation.delete(media_chose.get(i));

			}
			if (succeed) {
				Toast.makeText(mActivity, "删除成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mActivity, "删除失败", Toast.LENGTH_SHORT).show();
			}
		}
		return succeed;
	}

	private void clearMedia_chose() {
		if (media_chose != null) {
			media_chose.clear();// 清除原有数据
		}
	}

	// 用当前的path 重新填充listview
	private void updateList() {
		File file = new File(path);
		Message msg = new Message();
		msg.obj = file;
		msg.what = FILE;
		handler.sendMessage(msg);
	}

	public void setMedia_chose(List<File> files) {
		this.copy_files = files;
	}
}
