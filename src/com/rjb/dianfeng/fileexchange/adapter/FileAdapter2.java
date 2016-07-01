package com.rjb.dianfeng.fileexchange.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rjb.dianfeng.fileexchange.R;
import com.rjb.dianfeng.fileexchange.utils.BitmapWorkerTask;
import com.rjb.dianfeng.fileexchange.utils.ImageCache;
import com.rjb.dianfeng.fileexchange.utils.Utils;

/**
 * 按存储位置对文件进行分类时的fileAdapter
 * 
 * @author 龙
 * 
 */
public class FileAdapter2 extends BaseAdapter {
	private Context mContext;
	private File[] files;

	private List<File> selectedPosition = new ArrayList<File>();// 存放选中的item对应的文件
	private boolean isOpretated = false;// 是不是对文件进行操作

	public FileAdapter2(Context mContext, File[] files) {
		this.mContext = mContext;
		this.files = files;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return files.length;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		if (convertView != null) {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		} else {
			view = View.inflate(mContext, R.layout.item_files_listview, null);
			holder = new ViewHolder();
			holder.file_icon = (ImageView) view.findViewById(R.id.iv_file_icon);
			holder.file_name = (TextView) view.findViewById(R.id.tv_file_name);
			holder.file_des = (TextView) view.findViewById(R.id.tv_file_size);
			holder.arrow = (ImageView) view.findViewById(R.id.iv_arrow);
			holder.chose = (ImageView) view.findViewById(R.id.iv_chose);
			view.setTag(holder);
		}
		File file = files[position];
		holder.file_name.setText(file.getName());
		if (file.isDirectory()) {
			if (file.list() != null) {
				holder.file_des.setText("文件：" + file.list().length);
				holder.file_icon.setImageResource(R.drawable.folder_icon);
				holder.arrow.setVisibility(View.VISIBLE);
				holder.chose.setVisibility(View.GONE);
			}
		} else {
			int size = (int) file.length();
			String formatSize = Utils.getSizeKB(size);
			holder.file_des.setText(formatSize);
			// holder.file_icon.setImageResource(R.drawable.apk_icon);// 下午的活儿
			setFileIcon(file, holder.file_icon);
			holder.arrow.setVisibility(View.GONE);

			if (isOpretated) {
				holder.chose.setVisibility(View.VISIBLE);
				if (!selectedPosition.contains(files[position])) {
					holder.chose.setImageResource(R.drawable.unckecked);
				} else {
					holder.chose.setImageResource(R.drawable.checked);
				}
			} else {
				holder.chose.setVisibility(View.GONE);
			}
		}
		return view;
	}

	private void setFileIcon(File file, ImageView file_icon) {
		String extension = Utils.getExtensionFromPath(file.getAbsolutePath());
		if (extension.equals("doc")) {
			file_icon.setImageResource(R.drawable.word_icon);
		} else if (extension.equals("xls")) {
			file_icon.setImageResource(R.drawable.excel_icon);
		} else if (extension.equals("ppt")) {
			file_icon.setImageResource(R.drawable.ppt_icon);
		} else if (extension.equals("txt")) {
			file_icon.setImageResource(R.drawable.txt_icon);
		} else if (extension.equals("mp3") || extension.equals("amr")) {
			file_icon.setImageResource(R.drawable.music_icon);
		} else if (extension.equals("apk")) {
			file_icon.setImageResource(R.drawable.apk_icon);
		} else if (extension.equals("jpg") || extension.equals("jpeg")
				|| extension.equals("png") || extension.equals("gif")) {
			Bitmap bm = ImageCache.getInstance().get(file.getAbsolutePath());
			if (bm != null) {
				file_icon.setImageBitmap(bm);
			} else {
				new BitmapWorkerTask(file_icon, mContext).loadBitmap(file
						.getAbsolutePath());
			}
		} else if (extension.equals("zip") || extension.equals("rar")) {
			file_icon.setImageResource(R.drawable.zip_icon);
		} else {
			file_icon.setImageResource(R.drawable.no_type);
		}
	}

	public class ViewHolder {
		public ImageView file_icon;
		public TextView file_name;
		public TextView file_des;
		public ImageView arrow;
		public ImageView chose;
	}

	public void add(File file) {
		selectedPosition.add(file);
	}

	public void remove(File file) {
		selectedPosition.remove(file);
	}

	public void clear() {
		selectedPosition.clear();
	}

	public void setOpretated(boolean isOpretated) {
		this.isOpretated = isOpretated;
	}

	public boolean isOpretated() {
		return isOpretated;
	}

}
